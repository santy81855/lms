/*
** TEMPORARY FILE, I refactored into smaller service files, but will keep for now just in case.
 */

package lms.server.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lms.server.data.AssignmentRepository;
import lms.server.data.CourseModuleRepository;
import lms.server.data.CourseRepository;
import lms.server.data.LessonRepository;
import lms.server.data.ModuleContentItemRepository;
import lms.server.data.ModuleContentOrderRepository;
import lms.server.data.QuizAnswerOptionRepository;
import lms.server.data.QuizQuestionRepository;
import lms.server.data.QuizRepository;
import lms.server.models.Assignment;
import lms.server.models.ContentItemType;
import lms.server.models.Course;
import lms.server.models.CourseModule;
import lms.server.models.CourseStatus;
import lms.server.models.Lesson;
import lms.server.models.Quiz;
import lms.server.models.QuizAnswerOption;
import lms.server.models.QuizQuestion;
import lms.server.models.VisibilityStatus;
import lms.server.models.dtos.ModuleContentItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseManagementService {
    private static final String JOIN_CODE_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int JOIN_CODE_LENGTH = 8;
    private static final int JOIN_CODE_ATTEMPTS = 25;
    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerOptionRepository quizAnswerOptionRepository;
    private final ModuleContentItemRepository moduleContentItemRepository;
    private final ModuleContentOrderRepository moduleContentOrderRepository;
    private final Validator validator;
    private final SecureRandom secureRandom = new SecureRandom();

    public CourseManagementService(CourseRepository courseRepository, CourseModuleRepository courseModuleRepository, LessonRepository lessonRepository, AssignmentRepository assignmentRepository, QuizRepository quizRepository, QuizQuestionRepository quizQuestionRepository, QuizAnswerOptionRepository quizAnswerOptionRepository, ModuleContentItemRepository moduleContentItemRepository, ModuleContentOrderRepository moduleContentOrderRepository, Validator validator) {
        this.courseRepository = courseRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.lessonRepository = lessonRepository;
        this.assignmentRepository = assignmentRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerOptionRepository = quizAnswerOptionRepository;
        this.moduleContentItemRepository = moduleContentItemRepository;
        this.moduleContentOrderRepository = moduleContentOrderRepository;
        this.validator = validator;
    }

    public List<Course> findCoursesByTeacherId(Long teacherId) {
        if (teacherId == null) {
            return List.of();
        }
        return courseRepository.findByTeacherId(teacherId);
    }

    public Result<Course> findCourseByIdForTeacher(Long courseId, Long teacherId) {
        Result<Course> result = new Result<>();
        if (!requireId(courseId, "Course id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        Optional<Course> course = courseRepository.findByIdAndTeacherId(courseId, teacherId);
        if (course.isEmpty()) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(course.get());
        return result;
    }

    @Transactional
    public Result<Course> createCourse(Course course, Long teacherId) {
        Result<Course> result = new Result<>();
        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (course == null) {
            result.addMessage("Course is required.", ResultType.INVALID);
            return result;
        }
        Course newCourse = new Course();
        newCourse.setTeacherId(teacherId);
        newCourse.setTitle(trim(course.getTitle()));
        newCourse.setSubject(trimToNull(course.getSubject()));
        newCourse.setGradeLevel(course.getGradeLevel());
        newCourse.setDescription(trimToNull(course.getDescription()));
        newCourse.setStatus(CourseStatus.DRAFT);
        newCourse.setJoinCode(generateUniqueJoinCode());
        validate(newCourse, result);
        if (!result.isSuccess()) {
            return result;
        }
        result.setPayload(courseRepository.add(newCourse));
        return result;
    }

    @Transactional
    public Result<Course> updateCourse(Course course, Long teacherId) {
        Result<Course> result = new Result<>();
        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (course == null) {
            result.addMessage("Course is required.", ResultType.INVALID);
            return result;
        }
        if (!requireId(course.getId(), "Course id is required.", result)) {
            return result;
        }
        Optional<Course> existing = courseRepository.findByIdAndTeacherId(course.getId(), teacherId);
        if (existing.isEmpty()) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }
        Course updatedCourse = new Course();
        updatedCourse.setId(existing.get().getId());
        updatedCourse.setTeacherId(teacherId);
        updatedCourse.setTitle(trim(course.getTitle()));
        updatedCourse.setSubject(trimToNull(course.getSubject()));
        updatedCourse.setGradeLevel(course.getGradeLevel());
        updatedCourse.setDescription(trimToNull(course.getDescription()));
        updatedCourse.setStatus(existing.get().getStatus());
        updatedCourse.setJoinCode(existing.get().getJoinCode());
        validate(updatedCourse, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (!courseRepository.update(updatedCourse)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(updatedCourse);
        return result;
    }

    @Transactional
    public Result<Void> publishCourse(Long courseId, Long teacherId) {
        return updateCourseStatus(courseId, teacherId, CourseStatus.ACTIVE);
    }

    @Transactional
    public Result<Void> archiveCourse(Long courseId, Long teacherId) {
        return updateCourseStatus(courseId, teacherId, CourseStatus.ARCHIVED);
    }

    @Transactional
    public Result<Void> returnCourseToDraft(Long courseId, Long teacherId) {
        return updateCourseStatus(courseId, teacherId, CourseStatus.DRAFT);
    }

    @Transactional
    public Result<Void> updateCourseStatus(Long courseId, Long teacherId, CourseStatus status) {
        Result<Void> result = new Result<>();
        if (!requireId(courseId, "Course id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (status == null) {
            result.addMessage("Course status is required.", ResultType.INVALID);
            return result;
        }
        if (courseRepository.findByIdAndTeacherId(courseId, teacherId).isEmpty()) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }
        if (!courseRepository.updateStatus(courseId, teacherId, status)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
        }
        return result;
    }

    @Transactional
    public Result<Void> deleteCourse(Long courseId, Long teacherId) {
        Result<Void> result = new Result<>();
        if (!requireId(courseId, "Course id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (courseRepository.findByIdAndTeacherId(courseId, teacherId).isEmpty()) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }
        if (!courseRepository.deleteByIdAndTeacherId(courseId, teacherId)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
        }
        return result;
    }

    public Result<List<CourseModule>> findModulesByCourseId(Long courseId, Long teacherId) {
        Result<List<CourseModule>> result = new Result<>();
        if (!requireId(courseId, "Course id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (courseRepository.findByIdAndTeacherId(courseId, teacherId).isEmpty()) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(courseModuleRepository.findByCourseId(courseId));
        return result;
    }

    @Transactional
    public Result<CourseModule> createModule(CourseModule module, Long courseId, Long teacherId) {
        Result<CourseModule> result = new Result<>();
        if (!requireId(courseId, "Course id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (module == null) {
            result.addMessage("Module is required.", ResultType.INVALID);
            return result;
        }
        if (courseRepository.findByIdAndTeacherId(courseId, teacherId).isEmpty()) {
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
        Integer moduleOrder = module.getModuleOrder() == null ? existingModule.getModuleOrder() : module.getModuleOrder();
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
        result.setPayload(updatedModule);
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
        if (!requireId(moduleId, "Module id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (status == null) {
            result.addMessage("Module status is required.", ResultType.INVALID);
            return result;
        }
        if (findModuleForTeacher(moduleId, teacherId).isEmpty()) {
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
        if (!requireId(moduleId, "Module id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
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
        if (!requireId(moduleId, "Module id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
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

    public Result<List<ModuleContentItem>> findModuleContentItems(Long moduleId, Long teacherId) {
        Result<List<ModuleContentItem>> result = new Result<>();
        if (!requireId(moduleId, "Module id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (findModuleForTeacher(moduleId, teacherId).isEmpty()) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(moduleContentItemRepository.findByModuleId(moduleId));
        return result;
    }

    public Result<List<Lesson>> findLessonsByModuleId(Long moduleId, Long teacherId) {
        Result<List<Lesson>> result = new Result<>();
        if (!validateModuleAccess(moduleId, teacherId, result)) {
            return result;
        }
        result.setPayload(lessonRepository.findByModuleId(moduleId));
        return result;
    }

    @Transactional
    public Result<Lesson> createLesson(Lesson lesson, Long moduleId, Long teacherId) {
        Result<Lesson> result = new Result<>();
        if (!validateModuleWriteRequest(lesson, moduleId, teacherId, "Lesson is required.", result)) {
            return result;
        }
        Integer itemOrder = resolveNewContentItemOrder(moduleId, lesson.getLessonOrder(), result);
        if (!result.isSuccess()) {
            return result;
        }
        Lesson newLesson = new Lesson();
        newLesson.setModuleId(moduleId);
        newLesson.setTitle(trim(lesson.getTitle()));
        newLesson.setContent(trimToNull(lesson.getContent()));
        newLesson.setLessonOrder(itemOrder);
        newLesson.setEstimatedMinutes(lesson.getEstimatedMinutes());
        newLesson.setStatus(VisibilityStatus.DRAFT);
        validate(newLesson, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (itemOrder < moduleContentOrderRepository.getNextOrderForModule(moduleId)) {
            moduleContentOrderRepository.shiftOrdersForward(moduleId, itemOrder);
        }
        result.setPayload(lessonRepository.add(newLesson));
        return result;
    }

    @Transactional
    public Result<Lesson> updateLesson(Lesson lesson, Long teacherId) {
        Result<Lesson> result = new Result<>();
        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (lesson == null) {
            result.addMessage("Lesson is required.", ResultType.INVALID);
            return result;
        }
        if (!requireId(lesson.getId(), "Lesson id is required.", result)) {
            return result;
        }
        Optional<Lesson> existing = lessonRepository.findById(lesson.getId());
        if (existing.isEmpty()) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
            return result;
        }
        Lesson existingLesson = existing.get();
        if (findModuleForTeacher(existingLesson.getModuleId(), teacherId).isEmpty()) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
            return result;
        }
        Integer itemOrder = lesson.getLessonOrder() == null ? existingLesson.getLessonOrder() : lesson.getLessonOrder();
        Lesson updatedLesson = new Lesson();
        updatedLesson.setId(existingLesson.getId());
        updatedLesson.setModuleId(existingLesson.getModuleId());
        updatedLesson.setTitle(trim(lesson.getTitle()));
        updatedLesson.setContent(trimToNull(lesson.getContent()));
        updatedLesson.setLessonOrder(itemOrder);
        updatedLesson.setEstimatedMinutes(lesson.getEstimatedMinutes());
        updatedLesson.setStatus(existingLesson.getStatus());
        validate(updatedLesson, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (!itemOrder.equals(existingLesson.getLessonOrder())) {
            Result<Void> moveResult = moveContentItemInternal(ContentItemType.LESSON, existingLesson.getId(), existingLesson.getModuleId(), existingLesson.getLessonOrder(), itemOrder);
            if (!moveResult.isSuccess()) {
                copyMessages(moveResult, result);
                return result;
            }
        }
        if (!lessonRepository.update(updatedLesson)) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(updatedLesson);
        return result;
    }

    @Transactional
    public Result<Void> publishLesson(Long lessonId, Long teacherId) {
        return updateLessonStatus(lessonId, teacherId, VisibilityStatus.PUBLISHED);
    }

    @Transactional
    public Result<Void> archiveLesson(Long lessonId, Long teacherId) {
        return updateLessonStatus(lessonId, teacherId, VisibilityStatus.ARCHIVED);
    }

    @Transactional
    public Result<Void> returnLessonToDraft(Long lessonId, Long teacherId) {
        return updateLessonStatus(lessonId, teacherId, VisibilityStatus.DRAFT);
    }

    @Transactional
    public Result<Void> updateLessonStatus(Long lessonId, Long teacherId, VisibilityStatus status) {
        Result<Void> result = new Result<>();
        Optional<Lesson> existing = findLessonForTeacher(lessonId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        if (status == null) {
            result.addMessage("Lesson status is required.", ResultType.INVALID);
            return result;
        }
        if (!lessonRepository.updateStatus(lessonId, status)) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
        }
        return result;
    }

    @Transactional
    public Result<Void> moveLesson(Long lessonId, Long teacherId, Integer itemOrder) {
        Result<Void> result = new Result<>();
        Optional<Lesson> existing = findLessonForTeacher(lessonId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        Lesson lesson = existing.get();
        return moveContentItemInternal(ContentItemType.LESSON, lesson.getId(), lesson.getModuleId(), lesson.getLessonOrder(), itemOrder);
    }

    @Transactional
    public Result<Void> deleteLesson(Long lessonId, Long teacherId) {
        Result<Void> result = new Result<>();
        Optional<Lesson> existing = findLessonForTeacher(lessonId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        Lesson lesson = existing.get();
        if (!lessonRepository.deleteByIdAndModuleId(lessonId, lesson.getModuleId())) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
            return result;
        }
        moduleContentOrderRepository.shiftOrdersBackward(lesson.getModuleId(), lesson.getLessonOrder());
        return result;
    }

    public Result<List<Assignment>> findAssignmentsByModuleId(Long moduleId, Long teacherId) {
        Result<List<Assignment>> result = new Result<>();
        if (!validateModuleAccess(moduleId, teacherId, result)) {
            return result;
        }
        result.setPayload(assignmentRepository.findByModuleId(moduleId));
        return result;
    }

    @Transactional
    public Result<Assignment> createAssignment(Assignment assignment, Long moduleId, Long teacherId) {
        Result<Assignment> result = new Result<>();
        if (!validateModuleWriteRequest(assignment, moduleId, teacherId, "Assignment is required.", result)) {
            return result;
        }
        Integer itemOrder = resolveNewContentItemOrder(moduleId, assignment.getAssignmentOrder(), result);
        if (!result.isSuccess()) {
            return result;
        }
        Assignment newAssignment = new Assignment();
        newAssignment.setModuleId(moduleId);
        newAssignment.setTitle(trim(assignment.getTitle()));
        newAssignment.setInstructions(trimToNull(assignment.getInstructions()));
        newAssignment.setAssignmentOrder(itemOrder);
        newAssignment.setDueAt(assignment.getDueAt());
        newAssignment.setMaxPoints(assignment.getMaxPoints());
        newAssignment.setSubmissionType(assignment.getSubmissionType());
        newAssignment.setStatus(VisibilityStatus.DRAFT);
        validate(newAssignment, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (itemOrder < moduleContentOrderRepository.getNextOrderForModule(moduleId)) {
            moduleContentOrderRepository.shiftOrdersForward(moduleId, itemOrder);
        }
        result.setPayload(assignmentRepository.add(newAssignment));
        return result;
    }

    @Transactional
    public Result<Assignment> updateAssignment(Assignment assignment, Long teacherId) {
        Result<Assignment> result = new Result<>();
        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (assignment == null) {
            result.addMessage("Assignment is required.", ResultType.INVALID);
            return result;
        }
        if (!requireId(assignment.getId(), "Assignment id is required.", result)) {
            return result;
        }
        Optional<Assignment> existing = assignmentRepository.findById(assignment.getId());
        if (existing.isEmpty()) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
            return result;
        }
        Assignment existingAssignment = existing.get();
        if (findModuleForTeacher(existingAssignment.getModuleId(), teacherId).isEmpty()) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
            return result;
        }
        Integer itemOrder = assignment.getAssignmentOrder() == null ? existingAssignment.getAssignmentOrder() : assignment.getAssignmentOrder();
        Assignment updatedAssignment = new Assignment();
        updatedAssignment.setId(existingAssignment.getId());
        updatedAssignment.setModuleId(existingAssignment.getModuleId());
        updatedAssignment.setTitle(trim(assignment.getTitle()));
        updatedAssignment.setInstructions(trimToNull(assignment.getInstructions()));
        updatedAssignment.setAssignmentOrder(itemOrder);
        updatedAssignment.setDueAt(assignment.getDueAt());
        updatedAssignment.setMaxPoints(assignment.getMaxPoints());
        updatedAssignment.setSubmissionType(assignment.getSubmissionType());
        updatedAssignment.setStatus(existingAssignment.getStatus());
        validate(updatedAssignment, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (!itemOrder.equals(existingAssignment.getAssignmentOrder())) {
            Result<Void> moveResult = moveContentItemInternal(ContentItemType.ASSIGNMENT, existingAssignment.getId(), existingAssignment.getModuleId(), existingAssignment.getAssignmentOrder(), itemOrder);
            if (!moveResult.isSuccess()) {
                copyMessages(moveResult, result);
                return result;
            }
        }
        if (!assignmentRepository.update(updatedAssignment)) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(updatedAssignment);
        return result;
    }

    @Transactional
    public Result<Void> publishAssignment(Long assignmentId, Long teacherId) {
        return updateAssignmentStatus(assignmentId, teacherId, VisibilityStatus.PUBLISHED);
    }

    @Transactional
    public Result<Void> archiveAssignment(Long assignmentId, Long teacherId) {
        return updateAssignmentStatus(assignmentId, teacherId, VisibilityStatus.ARCHIVED);
    }

    @Transactional
    public Result<Void> returnAssignmentToDraft(Long assignmentId, Long teacherId) {
        return updateAssignmentStatus(assignmentId, teacherId, VisibilityStatus.DRAFT);
    }

    @Transactional
    public Result<Void> updateAssignmentStatus(Long assignmentId, Long teacherId, VisibilityStatus status) {
        Result<Void> result = new Result<>();
        Optional<Assignment> existing = findAssignmentForTeacher(assignmentId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        if (status == null) {
            result.addMessage("Assignment status is required.", ResultType.INVALID);
            return result;
        }
        if (!assignmentRepository.updateStatus(assignmentId, status)) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
        }
        return result;
    }

    @Transactional
    public Result<Void> moveAssignment(Long assignmentId, Long teacherId, Integer itemOrder) {
        Result<Void> result = new Result<>();
        Optional<Assignment> existing = findAssignmentForTeacher(assignmentId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        Assignment assignment = existing.get();
        return moveContentItemInternal(ContentItemType.ASSIGNMENT, assignment.getId(), assignment.getModuleId(), assignment.getAssignmentOrder(), itemOrder);
    }

    @Transactional
    public Result<Void> deleteAssignment(Long assignmentId, Long teacherId) {
        Result<Void> result = new Result<>();
        Optional<Assignment> existing = findAssignmentForTeacher(assignmentId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        Assignment assignment = existing.get();
        if (!assignmentRepository.deleteByIdAndModuleId(assignmentId, assignment.getModuleId())) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
            return result;
        }
        moduleContentOrderRepository.shiftOrdersBackward(assignment.getModuleId(), assignment.getAssignmentOrder());
        return result;
    }

    public Result<List<Quiz>> findQuizzesByModuleId(Long moduleId, Long teacherId) {
        Result<List<Quiz>> result = new Result<>();
        if (!validateModuleAccess(moduleId, teacherId, result)) {
            return result;
        }
        result.setPayload(quizRepository.findByModuleId(moduleId));
        return result;
    }

    @Transactional
    public Result<Quiz> createQuiz(Quiz quiz, Long moduleId, Long teacherId) {
        Result<Quiz> result = new Result<>();
        if (!validateModuleWriteRequest(quiz, moduleId, teacherId, "Quiz is required.", result)) {
            return result;
        }
        Integer itemOrder = resolveNewContentItemOrder(moduleId, quiz.getQuizOrder(), result);
        if (!result.isSuccess()) {
            return result;
        }
        Quiz newQuiz = new Quiz();
        newQuiz.setModuleId(moduleId);
        newQuiz.setTitle(trim(quiz.getTitle()));
        newQuiz.setDescription(trimToNull(quiz.getDescription()));
        newQuiz.setQuizOrder(itemOrder);
        newQuiz.setMaxPoints(quiz.getMaxPoints());
        newQuiz.setTimeLimitMinutes(quiz.getTimeLimitMinutes());
        newQuiz.setAttemptsAllowed(quiz.getAttemptsAllowed());
        newQuiz.setStatus(VisibilityStatus.DRAFT);
        validate(newQuiz, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (itemOrder < moduleContentOrderRepository.getNextOrderForModule(moduleId)) {
            moduleContentOrderRepository.shiftOrdersForward(moduleId, itemOrder);
        }
        result.setPayload(quizRepository.add(newQuiz));
        return result;
    }

    @Transactional
    public Result<Quiz> updateQuiz(Quiz quiz, Long teacherId) {
        Result<Quiz> result = new Result<>();
        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (quiz == null) {
            result.addMessage("Quiz is required.", ResultType.INVALID);
            return result;
        }
        if (!requireId(quiz.getId(), "Quiz id is required.", result)) {
            return result;
        }
        Optional<Quiz> existing = quizRepository.findById(quiz.getId());
        if (existing.isEmpty()) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }
        Quiz existingQuiz = existing.get();
        if (findModuleForTeacher(existingQuiz.getModuleId(), teacherId).isEmpty()) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }
        Integer itemOrder = quiz.getQuizOrder() == null ? existingQuiz.getQuizOrder() : quiz.getQuizOrder();
        Quiz updatedQuiz = new Quiz();
        updatedQuiz.setId(existingQuiz.getId());
        updatedQuiz.setModuleId(existingQuiz.getModuleId());
        updatedQuiz.setTitle(trim(quiz.getTitle()));
        updatedQuiz.setDescription(trimToNull(quiz.getDescription()));
        updatedQuiz.setQuizOrder(itemOrder);
        updatedQuiz.setMaxPoints(quiz.getMaxPoints());
        updatedQuiz.setTimeLimitMinutes(quiz.getTimeLimitMinutes());
        updatedQuiz.setAttemptsAllowed(quiz.getAttemptsAllowed());
        updatedQuiz.setStatus(existingQuiz.getStatus());
        validate(updatedQuiz, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (!itemOrder.equals(existingQuiz.getQuizOrder())) {
            Result<Void> moveResult = moveContentItemInternal(ContentItemType.QUIZ, existingQuiz.getId(), existingQuiz.getModuleId(), existingQuiz.getQuizOrder(), itemOrder);
            if (!moveResult.isSuccess()) {
                copyMessages(moveResult, result);
                return result;
            }
        }
        if (!quizRepository.update(updatedQuiz)) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(updatedQuiz);
        return result;
    }

    @Transactional
    public Result<Void> publishQuiz(Long quizId, Long teacherId) {
        return updateQuizStatus(quizId, teacherId, VisibilityStatus.PUBLISHED);
    }

    @Transactional
    public Result<Void> archiveQuiz(Long quizId, Long teacherId) {
        return updateQuizStatus(quizId, teacherId, VisibilityStatus.ARCHIVED);
    }

    @Transactional
    public Result<Void> returnQuizToDraft(Long quizId, Long teacherId) {
        return updateQuizStatus(quizId, teacherId, VisibilityStatus.DRAFT);
    }

    @Transactional
    public Result<Void> updateQuizStatus(Long quizId, Long teacherId, VisibilityStatus status) {
        Result<Void> result = new Result<>();
        Optional<Quiz> existing = findQuizForTeacher(quizId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        if (status == null) {
            result.addMessage("Quiz status is required.", ResultType.INVALID);
            return result;
        }
        if (!quizRepository.updateStatus(quizId, status)) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
        }
        return result;
    }

    @Transactional
    public Result<Void> moveQuiz(Long quizId, Long teacherId, Integer itemOrder) {
        Result<Void> result = new Result<>();
        Optional<Quiz> existing = findQuizForTeacher(quizId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        Quiz quiz = existing.get();
        return moveContentItemInternal(ContentItemType.QUIZ, quiz.getId(), quiz.getModuleId(), quiz.getQuizOrder(), itemOrder);
    }

    @Transactional
    public Result<Void> deleteQuiz(Long quizId, Long teacherId) {
        Result<Void> result = new Result<>();
        Optional<Quiz> existing = findQuizForTeacher(quizId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        Quiz quiz = existing.get();
        if (!quizRepository.deleteByIdAndModuleId(quizId, quiz.getModuleId())) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }
        moduleContentOrderRepository.shiftOrdersBackward(quiz.getModuleId(), quiz.getQuizOrder());
        return result;
    }

    public Result<List<QuizQuestion>> findQuestionsByQuizId(Long quizId, Long teacherId) {
        Result<List<QuizQuestion>> result = new Result<>();
        if (!requireId(quizId, "Quiz id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        Optional<Quiz> quiz = findQuizForTeacher(quizId, teacherId, result);
        if (!result.isSuccess() || quiz.isEmpty()) {
            return result;
        }
        result.setPayload(quizQuestionRepository.findByQuizId(quizId));
        return result;
    }

    @Transactional
    public Result<QuizQuestion> createQuizQuestion(QuizQuestion question, Long quizId, Long teacherId) {
        Result<QuizQuestion> result = new Result<>();
        if (!requireId(quizId, "Quiz id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (question == null) {
            result.addMessage("Quiz question is required.", ResultType.INVALID);
            return result;
        }
        Optional<Quiz> quiz = findQuizForTeacher(quizId, teacherId, result);
        if (!result.isSuccess() || quiz.isEmpty()) {
            return result;
        }
        int nextQuestionOrder = getNextQuestionOrder(quizId);
        Integer questionOrder = question.getQuestionOrder();
        if (questionOrder == null) {
            questionOrder = nextQuestionOrder;
        } else if (questionOrder <= 0) {
            result.addMessage("Question order must be greater than zero.", ResultType.INVALID);
            return result;
        } else if (questionOrder > nextQuestionOrder) {
            result.addMessage("Question order cannot be greater than the next available order.", ResultType.INVALID);
            return result;
        }
        QuizQuestion newQuestion = new QuizQuestion();
        newQuestion.setQuizId(quizId);
        newQuestion.setQuestionText(trim(question.getQuestionText()));
        newQuestion.setQuestionType(question.getQuestionType());
        newQuestion.setQuestionOrder(questionOrder);
        newQuestion.setPoints(question.getPoints());
        newQuestion.setExplanation(trimToNull(question.getExplanation()));
        validate(newQuestion, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (questionOrder < nextQuestionOrder) {
            shiftQuestionOrdersForward(quizId, questionOrder);
        }
        result.setPayload(quizQuestionRepository.add(newQuestion));
        return result;
    }

    @Transactional
    public Result<QuizQuestion> updateQuizQuestion(QuizQuestion question, Long teacherId) {
        Result<QuizQuestion> result = new Result<>();
        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (question == null) {
            result.addMessage("Quiz question is required.", ResultType.INVALID);
            return result;
        }
        if (!requireId(question.getId(), "Question id is required.", result)) {
            return result;
        }
        Optional<QuizQuestion> existing = findQuestionForTeacher(question.getId(), teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        QuizQuestion existingQuestion = existing.get();
        Integer questionOrder = question.getQuestionOrder() == null ? existingQuestion.getQuestionOrder() : question.getQuestionOrder();
        QuizQuestion updatedQuestion = new QuizQuestion();
        updatedQuestion.setId(existingQuestion.getId());
        updatedQuestion.setQuizId(existingQuestion.getQuizId());
        updatedQuestion.setQuestionText(trim(question.getQuestionText()));
        updatedQuestion.setQuestionType(question.getQuestionType());
        updatedQuestion.setQuestionOrder(questionOrder);
        updatedQuestion.setPoints(question.getPoints());
        updatedQuestion.setExplanation(trimToNull(question.getExplanation()));
        validate(updatedQuestion, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (!questionOrder.equals(existingQuestion.getQuestionOrder())) {
            Result<Void> moveResult = moveQuestionInternal(existingQuestion, questionOrder);
            if (!moveResult.isSuccess()) {
                copyMessages(moveResult, result);
                return result;
            }
        }
        if (!quizQuestionRepository.update(updatedQuestion)) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(updatedQuestion);
        return result;
    }

    @Transactional
    public Result<Void> moveQuizQuestion(Long questionId, Long teacherId, Integer questionOrder) {
        Result<Void> result = new Result<>();
        Optional<QuizQuestion> existing = findQuestionForTeacher(questionId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        return moveQuestionInternal(existing.get(), questionOrder);
    }

    @Transactional
    public Result<Void> deleteQuizQuestion(Long questionId, Long teacherId) {
        Result<Void> result = new Result<>();
        Optional<QuizQuestion> existing = findQuestionForTeacher(questionId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        QuizQuestion question = existing.get();
        if (!quizQuestionRepository.deleteByIdAndQuizId(questionId, question.getQuizId())) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
            return result;
        }
        shiftQuestionOrdersBackward(question.getQuizId(), question.getQuestionOrder());
        return result;
    }

    public Result<List<QuizAnswerOption>> findAnswerOptionsByQuestionId(Long questionId, Long teacherId) {
        Result<List<QuizAnswerOption>> result = new Result<>();
        if (!requireId(questionId, "Question id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        Optional<QuizQuestion> question = findQuestionForTeacher(questionId, teacherId, result);
        if (!result.isSuccess() || question.isEmpty()) {
            return result;
        }
        result.setPayload(quizAnswerOptionRepository.findByQuestionId(questionId));
        return result;
    }

    @Transactional
    public Result<QuizAnswerOption> createQuizAnswerOption(QuizAnswerOption option, Long questionId, Long teacherId) {
        Result<QuizAnswerOption> result = new Result<>();
        if (!requireId(questionId, "Question id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (option == null) {
            result.addMessage("Quiz answer option is required.", ResultType.INVALID);
            return result;
        }
        Optional<QuizQuestion> question = findQuestionForTeacher(questionId, teacherId, result);
        if (!result.isSuccess() || question.isEmpty()) {
            return result;
        }
        int nextOptionOrder = getNextOptionOrder(questionId);
        Integer optionOrder = option.getOptionOrder();
        if (optionOrder == null) {
            optionOrder = nextOptionOrder;
        } else if (optionOrder <= 0) {
            result.addMessage("Option order must be greater than zero.", ResultType.INVALID);
            return result;
        } else if (optionOrder > nextOptionOrder) {
            result.addMessage("Option order cannot be greater than the next available order.", ResultType.INVALID);
            return result;
        }
        QuizAnswerOption newOption = new QuizAnswerOption();
        newOption.setQuestionId(questionId);
        newOption.setOptionText(trim(option.getOptionText()));
        newOption.setOptionOrder(optionOrder);
        newOption.setCorrect(option.getCorrect());
        validate(newOption, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (optionOrder < nextOptionOrder) {
            shiftOptionOrdersForward(questionId, optionOrder);
        }
        result.setPayload(quizAnswerOptionRepository.add(newOption));
        return result;
    }

    @Transactional
    public Result<QuizAnswerOption> updateQuizAnswerOption(QuizAnswerOption option, Long teacherId) {
        Result<QuizAnswerOption> result = new Result<>();
        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (option == null) {
            result.addMessage("Quiz answer option is required.", ResultType.INVALID);
            return result;
        }
        if (!requireId(option.getId(), "Option id is required.", result)) {
            return result;
        }
        Optional<QuizAnswerOption> existing = findAnswerOptionForTeacher(option.getId(), teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        QuizAnswerOption existingOption = existing.get();
        Integer optionOrder = option.getOptionOrder() == null ? existingOption.getOptionOrder() : option.getOptionOrder();
        QuizAnswerOption updatedOption = new QuizAnswerOption();
        updatedOption.setId(existingOption.getId());
        updatedOption.setQuestionId(existingOption.getQuestionId());
        updatedOption.setOptionText(trim(option.getOptionText()));
        updatedOption.setOptionOrder(optionOrder);
        updatedOption.setCorrect(option.getCorrect());
        validate(updatedOption, result);
        if (!result.isSuccess()) {
            return result;
        }
        if (!optionOrder.equals(existingOption.getOptionOrder())) {
            Result<Void> moveResult = moveOptionInternal(existingOption, optionOrder);
            if (!moveResult.isSuccess()) {
                copyMessages(moveResult, result);
                return result;
            }
        }
        if (!quizAnswerOptionRepository.update(updatedOption)) {
            result.addMessage("Quiz answer option not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(updatedOption);
        return result;
    }

    @Transactional
    public Result<Void> moveQuizAnswerOption(Long optionId, Long teacherId, Integer optionOrder) {
        Result<Void> result = new Result<>();
        Optional<QuizAnswerOption> existing = findAnswerOptionForTeacher(optionId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        return moveOptionInternal(existing.get(), optionOrder);
    }

    @Transactional
    public Result<Void> deleteQuizAnswerOption(Long optionId, Long teacherId) {
        Result<Void> result = new Result<>();
        Optional<QuizAnswerOption> existing = findAnswerOptionForTeacher(optionId, teacherId, result);
        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }
        QuizAnswerOption option = existing.get();
        if (!quizAnswerOptionRepository.deleteByIdAndQuestionId(optionId, option.getQuestionId())) {
            result.addMessage("Quiz answer option not found.", ResultType.NOT_FOUND);
            return result;
        }
        shiftOptionOrdersBackward(option.getQuestionId(), option.getOptionOrder());
        return result;
    }

    @Transactional
    public Result<Void> moveContentItem(ContentItemType itemType, Long itemId, Long teacherId, Integer itemOrder) {
        Result<Void> result = new Result<>();
        if (!requireId(itemId, "Content item id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }
        if (itemType == null) {
            result.addMessage("Content item type is required.", ResultType.INVALID);
            return result;
        }
        Optional<ContentLocation> location = findContentLocation(itemType, itemId);
        if (location.isEmpty() || findModuleForTeacher(location.get().moduleId(), teacherId).isEmpty()) {
            result.addMessage("Content item not found.", ResultType.NOT_FOUND);
            return result;
        }
        return moveContentItemInternal(itemType, itemId, location.get().moduleId(), location.get().itemOrder(), itemOrder);
    }

    private boolean validateModuleAccess(Long moduleId, Long teacherId, Result<?> result) {
        if (!requireId(moduleId, "Module id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return false;
        }
        if (findModuleForTeacher(moduleId, teacherId).isEmpty()) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return false;
        }
        return true;
    }

    private <T> boolean validateModuleWriteRequest(T model, Long moduleId, Long teacherId, String missingModelMessage, Result<?> result) {
        if (!validateModuleAccess(moduleId, teacherId, result)) {
            return false;
        }
        if (model == null) {
            result.addMessage(missingModelMessage, ResultType.INVALID);
            return false;
        }
        return true;
    }

    private Optional<CourseModule> findModuleForTeacher(Long moduleId, Long teacherId) {
        if (moduleId == null || teacherId == null) {
            return Optional.empty();
        }
        return courseModuleRepository.findById(moduleId).filter(module -> courseRepository.findByIdAndTeacherId(module.getCourseId(), teacherId).isPresent());
    }

    private Optional<Lesson> findLessonForTeacher(Long lessonId, Long teacherId, Result<?> result) {
        if (!requireId(lessonId, "Lesson id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }
        Optional<Lesson> existing = lessonRepository.findById(lessonId);
        if (existing.isEmpty() || findModuleForTeacher(existing.get().getModuleId(), teacherId).isEmpty()) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
            return Optional.empty();
        }
        return existing;
    }

    private Optional<Assignment> findAssignmentForTeacher(Long assignmentId, Long teacherId, Result<?> result) {
        if (!requireId(assignmentId, "Assignment id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }
        Optional<Assignment> existing = assignmentRepository.findById(assignmentId);
        if (existing.isEmpty() || findModuleForTeacher(existing.get().getModuleId(), teacherId).isEmpty()) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
            return Optional.empty();
        }
        return existing;
    }

    private Optional<Quiz> findQuizForTeacher(Long quizId, Long teacherId, Result<?> result) {
        if (!requireId(quizId, "Quiz id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }
        Optional<Quiz> existing = quizRepository.findById(quizId);
        if (existing.isEmpty() || findModuleForTeacher(existing.get().getModuleId(), teacherId).isEmpty()) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return Optional.empty();
        }
        return existing;
    }

    private Optional<QuizQuestion> findQuestionForTeacher(Long questionId, Long teacherId, Result<?> result) {
        if (!requireId(questionId, "Question id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }
        Optional<QuizQuestion> existing = quizQuestionRepository.findById(questionId);
        if (existing.isEmpty()) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
            return Optional.empty();
        }
        Result<Quiz> quizResult = new Result<>();
        Optional<Quiz> quiz = findQuizForTeacher(existing.get().getQuizId(), teacherId, quizResult);
        if (quiz.isEmpty()) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
            return Optional.empty();
        }
        return existing;
    }

    private Optional<QuizAnswerOption> findAnswerOptionForTeacher(Long optionId, Long teacherId, Result<?> result) {
        if (!requireId(optionId, "Option id is required.", result) || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }
        Optional<QuizAnswerOption> existing = quizAnswerOptionRepository.findById(optionId);
        if (existing.isEmpty()) {
            result.addMessage("Quiz answer option not found.", ResultType.NOT_FOUND);
            return Optional.empty();
        }
        Result<QuizQuestion> questionResult = new Result<>();
        Optional<QuizQuestion> question = findQuestionForTeacher(existing.get().getQuestionId(), teacherId, questionResult);
        if (question.isEmpty()) {
            result.addMessage("Quiz answer option not found.", ResultType.NOT_FOUND);
            return Optional.empty();
        }
        return existing;
    }

    private Integer resolveNewContentItemOrder(Long moduleId, Integer requestedOrder, Result<?> result) {
        int nextOrder = moduleContentOrderRepository.getNextOrderForModule(moduleId);
        if (requestedOrder == null) {
            return nextOrder;
        }
        if (requestedOrder <= 0) {
            result.addMessage("Content item order must be greater than zero.", ResultType.INVALID);
            return null;
        }
        if (requestedOrder > nextOrder) {
            result.addMessage("Content item order cannot be greater than the next available order.", ResultType.INVALID);
            return null;
        }
        return requestedOrder;
    }

    private Result<Void> moveContentItemInternal(ContentItemType itemType, Long itemId, Long moduleId, Integer currentOrder, Integer newOrder) {
        Result<Void> result = new Result<>();
        if (newOrder == null) {
            result.addMessage("Content item order is required.", ResultType.INVALID);
            return result;
        }
        if (newOrder <= 0) {
            result.addMessage("Content item order must be greater than zero.", ResultType.INVALID);
            return result;
        }
        int itemCount = moduleContentOrderRepository.countItemsByModuleId(moduleId);
        if (newOrder > itemCount) {
            result.addMessage("Content item order cannot be greater than the number of items in the module.", ResultType.INVALID);
            return result;
        }
        if (newOrder.equals(currentOrder)) {
            return result;
        }
        if (!moduleContentOrderRepository.updateItemOrder(itemType, itemId, 0)) {
            throw new IllegalStateException("Could not temporarily move content item for reordering.");
        }
        moduleContentOrderRepository.shiftOrdersBackward(moduleId, currentOrder);
        moduleContentOrderRepository.shiftOrdersForward(moduleId, newOrder);
        if (!moduleContentOrderRepository.updateItemOrder(itemType, itemId, newOrder)) {
            throw new IllegalStateException("Could not finish content item reordering.");
        }
        return result;
    }

    private Optional<ContentLocation> findContentLocation(ContentItemType itemType, Long itemId) {
        return switch (itemType) {
            case LESSON ->
                    lessonRepository.findById(itemId).map(lesson -> new ContentLocation(lesson.getModuleId(), lesson.getLessonOrder()));
            case ASSIGNMENT ->
                    assignmentRepository.findById(itemId).map(assignment -> new ContentLocation(assignment.getModuleId(), assignment.getAssignmentOrder()));
            case QUIZ ->
                    quizRepository.findById(itemId).map(quiz -> new ContentLocation(quiz.getModuleId(), quiz.getQuizOrder()));
        };
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
        return courseModuleRepository.findByCourseId(courseId).stream().map(CourseModule::getModuleOrder).filter(order -> order != null).max(Integer::compareTo).orElse(0) + 1;
    }

    private void shiftModuleOrdersForward(Long courseId, Integer startingOrder) {
        courseModuleRepository.findByCourseId(courseId).stream().filter(module -> module.getModuleOrder() != null).filter(module -> module.getModuleOrder() >= startingOrder).sorted(Comparator.comparing(CourseModule::getModuleOrder).reversed()).forEach(module -> {
            boolean success = courseModuleRepository.updateOrder(module.getId(), module.getModuleOrder() + 1);
            if (!success) {
                throw new IllegalStateException("Could not shift module orders forward.");
            }
        });
    }

    private void shiftModuleOrdersBackward(Long courseId, Integer startingOrder) {
        courseModuleRepository.findByCourseId(courseId).stream().filter(module -> module.getModuleOrder() != null).filter(module -> module.getModuleOrder() > startingOrder).sorted(Comparator.comparing(CourseModule::getModuleOrder)).forEach(module -> {
            boolean success = courseModuleRepository.updateOrder(module.getId(), module.getModuleOrder() - 1);
            if (!success) {
                throw new IllegalStateException("Could not shift module orders backward.");
            }
        });
    }

    private Result<Void> moveQuestionInternal(QuizQuestion question, Integer newOrder) {
        Result<Void> result = new Result<>();
        if (newOrder == null) {
            result.addMessage("Question order is required.", ResultType.INVALID);
            return result;
        }
        if (newOrder <= 0) {
            result.addMessage("Question order must be greater than zero.", ResultType.INVALID);
            return result;
        }
        int questionCount = quizQuestionRepository.findByQuizId(question.getQuizId()).size();
        if (newOrder > questionCount) {
            result.addMessage("Question order cannot be greater than the number of questions in the quiz.", ResultType.INVALID);
            return result;
        }
        if (newOrder.equals(question.getQuestionOrder())) {
            return result;
        }
        if (!quizQuestionRepository.updateOrder(question.getId(), 0)) {
            throw new IllegalStateException("Could not temporarily move quiz question for reordering.");
        }
        shiftQuestionOrdersBackward(question.getQuizId(), question.getQuestionOrder());
        shiftQuestionOrdersForward(question.getQuizId(), newOrder);
        if (!quizQuestionRepository.updateOrder(question.getId(), newOrder)) {
            throw new IllegalStateException("Could not finish quiz question reordering.");
        }
        return result;
    }

    private int getNextQuestionOrder(Long quizId) {
        return quizQuestionRepository.findByQuizId(quizId).stream().map(QuizQuestion::getQuestionOrder).filter(order -> order != null).max(Integer::compareTo).orElse(0) + 1;
    }

    private void shiftQuestionOrdersForward(Long quizId, Integer startingOrder) {
        quizQuestionRepository.findByQuizId(quizId).stream().filter(question -> question.getQuestionOrder() != null).filter(question -> question.getQuestionOrder() >= startingOrder).sorted(Comparator.comparing(QuizQuestion::getQuestionOrder).reversed()).forEach(question -> {
            boolean success = quizQuestionRepository.updateOrder(question.getId(), question.getQuestionOrder() + 1);
            if (!success) {
                throw new IllegalStateException("Could not shift quiz question orders forward.");
            }
        });
    }

    private void shiftQuestionOrdersBackward(Long quizId, Integer startingOrder) {
        quizQuestionRepository.findByQuizId(quizId).stream().filter(question -> question.getQuestionOrder() != null).filter(question -> question.getQuestionOrder() > startingOrder).sorted(Comparator.comparing(QuizQuestion::getQuestionOrder)).forEach(question -> {
            boolean success = quizQuestionRepository.updateOrder(question.getId(), question.getQuestionOrder() - 1);
            if (!success) {
                throw new IllegalStateException("Could not shift quiz question orders backward.");
            }
        });
    }

    private Result<Void> moveOptionInternal(QuizAnswerOption option, Integer newOrder) {
        Result<Void> result = new Result<>();
        if (newOrder == null) {
            result.addMessage("Option order is required.", ResultType.INVALID);
            return result;
        }
        if (newOrder <= 0) {
            result.addMessage("Option order must be greater than zero.", ResultType.INVALID);
            return result;
        }
        int optionCount = quizAnswerOptionRepository.findByQuestionId(option.getQuestionId()).size();
        if (newOrder > optionCount) {
            result.addMessage("Option order cannot be greater than the number of options for the question.", ResultType.INVALID);
            return result;
        }
        if (newOrder.equals(option.getOptionOrder())) {
            return result;
        }
        if (!quizAnswerOptionRepository.updateOrder(option.getId(), 0)) {
            throw new IllegalStateException("Could not temporarily move quiz answer option for reordering.");
        }
        shiftOptionOrdersBackward(option.getQuestionId(), option.getOptionOrder());
        shiftOptionOrdersForward(option.getQuestionId(), newOrder);
        if (!quizAnswerOptionRepository.updateOrder(option.getId(), newOrder)) {
            throw new IllegalStateException("Could not finish quiz answer option reordering.");
        }
        return result;
    }

    private int getNextOptionOrder(Long questionId) {
        return quizAnswerOptionRepository.findByQuestionId(questionId).stream().map(QuizAnswerOption::getOptionOrder).filter(order -> order != null).max(Integer::compareTo).orElse(0) + 1;
    }

    private void shiftOptionOrdersForward(Long questionId, Integer startingOrder) {
        quizAnswerOptionRepository.findByQuestionId(questionId).stream().filter(option -> option.getOptionOrder() != null).filter(option -> option.getOptionOrder() >= startingOrder).sorted(Comparator.comparing(QuizAnswerOption::getOptionOrder).reversed()).forEach(option -> {
            boolean success = quizAnswerOptionRepository.updateOrder(option.getId(), option.getOptionOrder() + 1);
            if (!success) {
                throw new IllegalStateException("Could not shift quiz answer option orders forward.");
            }
        });
    }

    private void shiftOptionOrdersBackward(Long questionId, Integer startingOrder) {
        quizAnswerOptionRepository.findByQuestionId(questionId).stream().filter(option -> option.getOptionOrder() != null).filter(option -> option.getOptionOrder() > startingOrder).sorted(Comparator.comparing(QuizAnswerOption::getOptionOrder)).forEach(option -> {
            boolean success = quizAnswerOptionRepository.updateOrder(option.getId(), option.getOptionOrder() - 1);
            if (!success) {
                throw new IllegalStateException("Could not shift quiz answer option orders backward.");
            }
        });
    }

    private String generateUniqueJoinCode() {
        for (int attempt = 0; attempt < JOIN_CODE_ATTEMPTS; attempt++) {
            String joinCode = generateJoinCode();
            if (!courseRepository.existsByJoinCode(joinCode)) {
                return joinCode;
            }
        }
        throw new IllegalStateException("Could not generate a unique course join code.");
    }

    private String generateJoinCode() {
        StringBuilder result = new StringBuilder(JOIN_CODE_LENGTH);
        for (int i = 0; i < JOIN_CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(JOIN_CODE_CHARACTERS.length());
            result.append(JOIN_CODE_CHARACTERS.charAt(index));
        }
        return result.toString();
    }

    private <T> void validate(T model, Result<?> result) {
        Set<ConstraintViolation<T>> violations = validator.validate(model);
        for (ConstraintViolation<T> violation : violations) {
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

    private record ContentLocation(Long moduleId, Integer itemOrder) {
    }
}