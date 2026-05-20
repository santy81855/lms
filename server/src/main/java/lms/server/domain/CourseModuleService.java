package lms.server.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lms.server.data.CourseModuleRepository;
import lms.server.models.CourseModule;
import lms.server.models.VisibilityStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseModuleService {

    private final CourseModuleRepository courseModuleRepository;
    private final CourseService courseService;
    private final Validator validator;

    public CourseModuleService(CourseModuleRepository courseModuleRepository,
                               CourseService courseService,
                               Validator validator) {
        this.courseModuleRepository = courseModuleRepository;
        this.courseService = courseService;
        this.validator = validator;
    }

    public Result<List<CourseModule>> findModulesByCourseId(Long courseId, Long teacherId) {
        Result<List<CourseModule>> result = new Result<>();

        if (!requireId(courseId, "Course id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (!courseService.teacherOwnsCourse(courseId, teacherId)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(courseModuleRepository.findByCourseId(courseId));
        return result;
    }

    public Result<CourseModule> findModuleByIdForTeacher(Long moduleId, Long teacherId) {
        Result<CourseModule> result = new Result<>();

        if (!requireId(moduleId, "Module id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        Optional<CourseModule> module = findModuleForTeacher(moduleId, teacherId);

        if (module.isEmpty()) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(module.get());
        return result;
    }

    public boolean teacherOwnsModule(Long moduleId, Long teacherId) {
        return findModuleForTeacher(moduleId, teacherId).isPresent();
    }

    public Optional<CourseModule> findModuleForTeacher(Long moduleId, Long teacherId) {
        if (moduleId == null || teacherId == null) {
            return Optional.empty();
        }

        return courseModuleRepository.findById(moduleId)
                .filter(module -> courseService.teacherOwnsCourse(module.getCourseId(), teacherId));
    }

    @Transactional
    public Result<CourseModule> createModule(CourseModule module, Long courseId, Long teacherId) {
        Result<CourseModule> result = new Result<>();

        if (!requireId(courseId, "Course id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (module == null) {
            result.addMessage("Module is required.", ResultType.INVALID);
            return result;
        }

        if (!courseService.teacherOwnsCourse(courseId, teacherId)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        int nextModuleOrder = getNextModuleOrder(courseId);
        Integer moduleOrder = module.getModuleOrder();

        if (moduleOrder == null) {
            moduleOrder = nextModuleOrder;
        } else if (moduleOrder <= 0) {
            result.addMessage("Module order must be greater than zero.", ResultType.INVALID);
            return result;
        } else if (moduleOrder > nextModuleOrder) {
            result.addMessage("Module order cannot be greater than the next available order.", ResultType.INVALID);
            return result;
        }

        CourseModule newModule = new CourseModule();
        newModule.setCourseId(courseId);
        newModule.setTitle(trim(module.getTitle()));
        newModule.setDescription(trimToNull(module.getDescription()));
        newModule.setModuleOrder(moduleOrder);
        newModule.setStatus(VisibilityStatus.DRAFT);

        validate(newModule, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (moduleOrder < nextModuleOrder) {
            shiftModuleOrdersForward(courseId, moduleOrder);
        }

        result.setPayload(courseModuleRepository.add(newModule));
        return result;
    }

    @Transactional
    public Result<CourseModule> updateModule(CourseModule module, Long teacherId) {
        Result<CourseModule> result = new Result<>();

        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (module == null) {
            result.addMessage("Module is required.", ResultType.INVALID);
            return result;
        }

        if (!requireId(module.getId(), "Module id is required.", result)) {
            return result;
        }

        Optional<CourseModule> existing = findModuleForTeacher(module.getId(), teacherId);

        if (existing.isEmpty()) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }

        CourseModule existingModule = existing.get();

        Integer moduleOrder = module.getModuleOrder() == null
                ? existingModule.getModuleOrder()
                : module.getModuleOrder();

        CourseModule updatedModule = new CourseModule();
        updatedModule.setId(existingModule.getId());
        updatedModule.setCourseId(existingModule.getCourseId());
        updatedModule.setTitle(trim(module.getTitle()));
        updatedModule.setDescription(trimToNull(module.getDescription()));
        updatedModule.setModuleOrder(moduleOrder);
        updatedModule.setStatus(existingModule.getStatus());

        validate(updatedModule, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (!moduleOrder.equals(existingModule.getModuleOrder())) {
            Result<Void> moveResult = moveModuleInternal(existingModule, moduleOrder);

            if (!moveResult.isSuccess()) {
                copyMessages(moveResult, result);
                return result;
            }
        }

        if (!courseModuleRepository.update(updatedModule)) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }

        CourseModule savedModule = courseModuleRepository.findById(updatedModule.getId())
                .orElse(updatedModule);

        result.setPayload(savedModule);
        return result;
    }

    @Transactional
    public Result<Void> publishModule(Long moduleId, Long teacherId) {
        return updateModuleStatus(moduleId, teacherId, VisibilityStatus.PUBLISHED);
    }

    @Transactional
    public Result<Void> archiveModule(Long moduleId, Long teacherId) {
        return updateModuleStatus(moduleId, teacherId, VisibilityStatus.ARCHIVED);
    }

    @Transactional
    public Result<Void> returnModuleToDraft(Long moduleId, Long teacherId) {
        return updateModuleStatus(moduleId, teacherId, VisibilityStatus.DRAFT);
    }

    @Transactional
    public Result<Void> updateModuleStatus(Long moduleId, Long teacherId, VisibilityStatus status) {
        Result<Void> result = new Result<>();

        if (!requireId(moduleId, "Module id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (status == null) {
            result.addMessage("Module status is required.", ResultType.INVALID);
            return result;
        }

        if (!teacherOwnsModule(moduleId, teacherId)) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }

        if (!courseModuleRepository.updateStatus(moduleId, status)) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
        }

        return result;
    }

    @Transactional
    public Result<Void> moveModule(Long moduleId, Long teacherId, Integer moduleOrder) {
        Result<Void> result = new Result<>();

        if (!requireId(moduleId, "Module id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        Optional<CourseModule> existing = findModuleForTeacher(moduleId, teacherId);

        if (existing.isEmpty()) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }

        return moveModuleInternal(existing.get(), moduleOrder);
    }

    @Transactional
    public Result<Void> deleteModule(Long moduleId, Long teacherId) {
        Result<Void> result = new Result<>();

        if (!requireId(moduleId, "Module id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        Optional<CourseModule> existing = findModuleForTeacher(moduleId, teacherId);

        if (existing.isEmpty()) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }

        CourseModule module = existing.get();

        if (!courseModuleRepository.deleteByIdAndCourseId(moduleId, module.getCourseId())) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }

        shiftModuleOrdersBackward(module.getCourseId(), module.getModuleOrder());
        return result;
    }

    private Result<Void> moveModuleInternal(CourseModule module, Integer newOrder) {
        Result<Void> result = new Result<>();

        if (newOrder == null) {
            result.addMessage("Module order is required.", ResultType.INVALID);
            return result;
        }

        if (newOrder <= 0) {
            result.addMessage("Module order must be greater than zero.", ResultType.INVALID);
            return result;
        }

        int moduleCount = courseModuleRepository.findByCourseId(module.getCourseId()).size();

        if (newOrder > moduleCount) {
            result.addMessage("Module order cannot be greater than the number of modules in the course.", ResultType.INVALID);
            return result;
        }

        if (newOrder.equals(module.getModuleOrder())) {
            return result;
        }

        if (!courseModuleRepository.updateOrder(module.getId(), 0)) {
            throw new IllegalStateException("Could not temporarily move module for reordering.");
        }

        shiftModuleOrdersBackward(module.getCourseId(), module.getModuleOrder());
        shiftModuleOrdersForward(module.getCourseId(), newOrder);

        if (!courseModuleRepository.updateOrder(module.getId(), newOrder)) {
            throw new IllegalStateException("Could not finish module reordering.");
        }

        return result;
    }

    private int getNextModuleOrder(Long courseId) {
        return courseModuleRepository.findByCourseId(courseId).stream()
                .map(CourseModule::getModuleOrder)
                .filter(order -> order != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private void shiftModuleOrdersForward(Long courseId, Integer startingOrder) {
        courseModuleRepository.findByCourseId(courseId).stream()
                .filter(module -> module.getModuleOrder() != null)
                .filter(module -> module.getModuleOrder() >= startingOrder)
                .sorted(Comparator.comparing(CourseModule::getModuleOrder).reversed())
                .forEach(module -> {
                    boolean success = courseModuleRepository.updateOrder(
                            module.getId(),
                            module.getModuleOrder() + 1
                    );

                    if (!success) {
                        throw new IllegalStateException("Could not shift module orders forward.");
                    }
                });
    }

    private void shiftModuleOrdersBackward(Long courseId, Integer startingOrder) {
        courseModuleRepository.findByCourseId(courseId).stream()
                .filter(module -> module.getModuleOrder() != null)
                .filter(module -> module.getModuleOrder() > startingOrder)
                .sorted(Comparator.comparing(CourseModule::getModuleOrder))
                .forEach(module -> {
                    boolean success = courseModuleRepository.updateOrder(
                            module.getId(),
                            module.getModuleOrder() - 1
                    );

                    if (!success) {
                        throw new IllegalStateException("Could not shift module orders backward.");
                    }
                });
    }

    private void validate(CourseModule module, Result<CourseModule> result) {
        Set<ConstraintViolation<CourseModule>> violations = validator.validate(module);

        for (ConstraintViolation<CourseModule> violation : violations) {
            result.addMessage(violation.getMessage(), ResultType.INVALID);
        }
    }

    private boolean requireId(Long id, String message, Result<?> result) {
        if (id == null) {
            result.addMessage(message, ResultType.INVALID);
            return false;
        }

        return true;
    }

    private void copyMessages(Result<?> source, Result<?> target) {
        for (String message : source.getMessages()) {
            target.addMessage(message, source.getType());
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}