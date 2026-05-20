package lms.server.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lms.server.data.AssignmentRepository;
import lms.server.data.LessonRepository;
import lms.server.data.ModuleContentItemRepository;
import lms.server.data.QuizRepository;
import lms.server.models.Assignment;
import lms.server.models.ContentItemType;
import lms.server.models.CourseModule;
import lms.server.models.Lesson;
import lms.server.models.Quiz;
import lms.server.models.VisibilityStatus;
import lms.server.models.dtos.ModuleContentItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ModuleContentService {

    private final CourseModuleService courseModuleService;
    private final ContentOrderService contentOrderService;
    private final ModuleContentItemRepository moduleContentItemRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final QuizRepository quizRepository;
    private final Validator validator;

    public ModuleContentService(CourseModuleService courseModuleService,
                                ContentOrderService contentOrderService,
                                ModuleContentItemRepository moduleContentItemRepository,
                                LessonRepository lessonRepository,
                                AssignmentRepository assignmentRepository,
                                QuizRepository quizRepository,
                                Validator validator) {
        this.courseModuleService = courseModuleService;
        this.contentOrderService = contentOrderService;
        this.moduleContentItemRepository = moduleContentItemRepository;
        this.lessonRepository = lessonRepository;
        this.assignmentRepository = assignmentRepository;
        this.quizRepository = quizRepository;
        this.validator = validator;
    }

    public Result<List<ModuleContentItem>> findModuleContentItems(Long moduleId, Long teacherId) {
        Result<List<ModuleContentItem>> result = new Result<>();

        if (!validateModuleAccess(moduleId, teacherId, result)) {
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

        Integer lessonOrder = contentOrderService.resolveNewContentItemOrder(
                moduleId,
                lesson.getLessonOrder(),
                result
        );

        if (!result.isSuccess()) {
            return result;
        }

        Lesson newLesson = new Lesson();
        newLesson.setModuleId(moduleId);
        newLesson.setTitle(trim(lesson.getTitle()));
        newLesson.setContent(trimToNull(lesson.getContent()));
        newLesson.setLessonOrder(lessonOrder);
        newLesson.setEstimatedMinutes(lesson.getEstimatedMinutes());
        newLesson.setStatus(VisibilityStatus.DRAFT);

        validate(newLesson, result);

        if (!result.isSuccess()) {
            return result;
        }

        contentOrderService.shiftOrdersForwardForInsert(moduleId, lessonOrder);

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

        Optional<Lesson> existing = findLessonForTeacher(lesson.getId(), teacherId);

        if (existing.isEmpty()) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
            return result;
        }

        Lesson existingLesson = existing.get();

        Integer lessonOrder = lesson.getLessonOrder() == null
                ? existingLesson.getLessonOrder()
                : lesson.getLessonOrder();

        Lesson updatedLesson = new Lesson();
        updatedLesson.setId(existingLesson.getId());
        updatedLesson.setModuleId(existingLesson.getModuleId());
        updatedLesson.setTitle(trim(lesson.getTitle()));
        updatedLesson.setContent(trimToNull(lesson.getContent()));
        updatedLesson.setLessonOrder(lessonOrder);
        updatedLesson.setEstimatedMinutes(lesson.getEstimatedMinutes());
        updatedLesson.setStatus(existingLesson.getStatus());

        validate(updatedLesson, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (!lessonOrder.equals(existingLesson.getLessonOrder())) {
            Result<Void> moveResult = contentOrderService.moveContentItem(
                    ContentItemType.LESSON,
                    existingLesson.getId(),
                    existingLesson.getModuleId(),
                    existingLesson.getLessonOrder(),
                    lessonOrder
            );

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

        if (status == null) {
            result.addMessage("Lesson status is required.", ResultType.INVALID);
            return result;
        }

        Optional<Lesson> existing = findLessonForTeacherWithResult(lessonId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        if (!lessonRepository.updateStatus(lessonId, status)) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
        }

        return result;
    }

    @Transactional
    public Result<Void> moveLesson(Long lessonId, Long teacherId, Integer lessonOrder) {
        Result<Void> result = new Result<>();
        Optional<Lesson> existing = findLessonForTeacherWithResult(lessonId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        Lesson lesson = existing.get();

        return contentOrderService.moveContentItem(
                ContentItemType.LESSON,
                lesson.getId(),
                lesson.getModuleId(),
                lesson.getLessonOrder(),
                lessonOrder
        );
    }

    @Transactional
    public Result<Void> deleteLesson(Long lessonId, Long teacherId) {
        Result<Void> result = new Result<>();
        Optional<Lesson> existing = findLessonForTeacherWithResult(lessonId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        Lesson lesson = existing.get();

        if (!lessonRepository.deleteByIdAndModuleId(lessonId, lesson.getModuleId())) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
            return result;
        }

        contentOrderService.shiftOrdersBackwardAfterDelete(lesson.getModuleId(), lesson.getLessonOrder());
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

        Integer assignmentOrder = contentOrderService.resolveNewContentItemOrder(
                moduleId,
                assignment.getAssignmentOrder(),
                result
        );

        if (!result.isSuccess()) {
            return result;
        }

        Assignment newAssignment = new Assignment();
        newAssignment.setModuleId(moduleId);
        newAssignment.setTitle(trim(assignment.getTitle()));
        newAssignment.setInstructions(trimToNull(assignment.getInstructions()));
        newAssignment.setAssignmentOrder(assignmentOrder);
        newAssignment.setDueAt(assignment.getDueAt());
        newAssignment.setMaxPoints(assignment.getMaxPoints());
        newAssignment.setSubmissionType(assignment.getSubmissionType());
        newAssignment.setStatus(VisibilityStatus.DRAFT);

        validate(newAssignment, result);

        if (!result.isSuccess()) {
            return result;
        }

        contentOrderService.shiftOrdersForwardForInsert(moduleId, assignmentOrder);

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

        Optional<Assignment> existing = findAssignmentForTeacher(assignment.getId(), teacherId);

        if (existing.isEmpty()) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
            return result;
        }

        Assignment existingAssignment = existing.get();

        Integer assignmentOrder = assignment.getAssignmentOrder() == null
                ? existingAssignment.getAssignmentOrder()
                : assignment.getAssignmentOrder();

        Assignment updatedAssignment = new Assignment();
        updatedAssignment.setId(existingAssignment.getId());
        updatedAssignment.setModuleId(existingAssignment.getModuleId());
        updatedAssignment.setTitle(trim(assignment.getTitle()));
        updatedAssignment.setInstructions(trimToNull(assignment.getInstructions()));
        updatedAssignment.setAssignmentOrder(assignmentOrder);
        updatedAssignment.setDueAt(assignment.getDueAt());
        updatedAssignment.setMaxPoints(assignment.getMaxPoints());
        updatedAssignment.setSubmissionType(assignment.getSubmissionType());
        updatedAssignment.setStatus(existingAssignment.getStatus());

        validate(updatedAssignment, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (!assignmentOrder.equals(existingAssignment.getAssignmentOrder())) {
            Result<Void> moveResult = contentOrderService.moveContentItem(
                    ContentItemType.ASSIGNMENT,
                    existingAssignment.getId(),
                    existingAssignment.getModuleId(),
                    existingAssignment.getAssignmentOrder(),
                    assignmentOrder
            );

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

        if (status == null) {
            result.addMessage("Assignment status is required.", ResultType.INVALID);
            return result;
        }

        Optional<Assignment> existing = findAssignmentForTeacherWithResult(assignmentId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        if (!assignmentRepository.updateStatus(assignmentId, status)) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
        }

        return result;
    }

    @Transactional
    public Result<Void> moveAssignment(Long assignmentId, Long teacherId, Integer assignmentOrder) {
        Result<Void> result = new Result<>();
        Optional<Assignment> existing = findAssignmentForTeacherWithResult(assignmentId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        Assignment assignment = existing.get();

        return contentOrderService.moveContentItem(
                ContentItemType.ASSIGNMENT,
                assignment.getId(),
                assignment.getModuleId(),
                assignment.getAssignmentOrder(),
                assignmentOrder
        );
    }

    @Transactional
    public Result<Void> deleteAssignment(Long assignmentId, Long teacherId) {
        Result<Void> result = new Result<>();
        Optional<Assignment> existing = findAssignmentForTeacherWithResult(assignmentId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        Assignment assignment = existing.get();

        if (!assignmentRepository.deleteByIdAndModuleId(assignmentId, assignment.getModuleId())) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
            return result;
        }

        contentOrderService.shiftOrdersBackwardAfterDelete(assignment.getModuleId(), assignment.getAssignmentOrder());
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

        Integer quizOrder = contentOrderService.resolveNewContentItemOrder(
                moduleId,
                quiz.getQuizOrder(),
                result
        );

        if (!result.isSuccess()) {
            return result;
        }

        Quiz newQuiz = new Quiz();
        newQuiz.setModuleId(moduleId);
        newQuiz.setTitle(trim(quiz.getTitle()));
        newQuiz.setDescription(trimToNull(quiz.getDescription()));
        newQuiz.setQuizOrder(quizOrder);
        newQuiz.setMaxPoints(quiz.getMaxPoints());
        newQuiz.setTimeLimitMinutes(quiz.getTimeLimitMinutes());
        newQuiz.setAttemptsAllowed(quiz.getAttemptsAllowed());
        newQuiz.setStatus(VisibilityStatus.DRAFT);

        validate(newQuiz, result);

        if (!result.isSuccess()) {
            return result;
        }

        contentOrderService.shiftOrdersForwardForInsert(moduleId, quizOrder);

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

        Optional<Quiz> existing = findQuizForTeacher(quiz.getId(), teacherId);

        if (existing.isEmpty()) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }

        Quiz existingQuiz = existing.get();

        Integer quizOrder = quiz.getQuizOrder() == null
                ? existingQuiz.getQuizOrder()
                : quiz.getQuizOrder();

        Quiz updatedQuiz = new Quiz();
        updatedQuiz.setId(existingQuiz.getId());
        updatedQuiz.setModuleId(existingQuiz.getModuleId());
        updatedQuiz.setTitle(trim(quiz.getTitle()));
        updatedQuiz.setDescription(trimToNull(quiz.getDescription()));
        updatedQuiz.setQuizOrder(quizOrder);
        updatedQuiz.setMaxPoints(quiz.getMaxPoints());
        updatedQuiz.setTimeLimitMinutes(quiz.getTimeLimitMinutes());
        updatedQuiz.setAttemptsAllowed(quiz.getAttemptsAllowed());
        updatedQuiz.setStatus(existingQuiz.getStatus());

        validate(updatedQuiz, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (!quizOrder.equals(existingQuiz.getQuizOrder())) {
            Result<Void> moveResult = contentOrderService.moveContentItem(
                    ContentItemType.QUIZ,
                    existingQuiz.getId(),
                    existingQuiz.getModuleId(),
                    existingQuiz.getQuizOrder(),
                    quizOrder
            );

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

        if (status == null) {
            result.addMessage("Quiz status is required.", ResultType.INVALID);
            return result;
        }

        Optional<Quiz> existing = findQuizForTeacherWithResult(quizId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        if (!quizRepository.updateStatus(quizId, status)) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
        }

        return result;
    }

    @Transactional
    public Result<Void> moveQuiz(Long quizId, Long teacherId, Integer quizOrder) {
        Result<Void> result = new Result<>();
        Optional<Quiz> existing = findQuizForTeacherWithResult(quizId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        Quiz quiz = existing.get();

        return contentOrderService.moveContentItem(
                ContentItemType.QUIZ,
                quiz.getId(),
                quiz.getModuleId(),
                quiz.getQuizOrder(),
                quizOrder
        );
    }

    @Transactional
    public Result<Void> deleteQuiz(Long quizId, Long teacherId) {
        Result<Void> result = new Result<>();
        Optional<Quiz> existing = findQuizForTeacherWithResult(quizId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        Quiz quiz = existing.get();

        if (!quizRepository.deleteByIdAndModuleId(quizId, quiz.getModuleId())) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }

        contentOrderService.shiftOrdersBackwardAfterDelete(quiz.getModuleId(), quiz.getQuizOrder());
        return result;
    }

    public Optional<Quiz> findQuizForTeacher(Long quizId, Long teacherId) {
        if (quizId == null || teacherId == null) {
            return Optional.empty();
        }

        return quizRepository.findById(quizId)
                .filter(quiz -> courseModuleService.teacherOwnsModule(quiz.getModuleId(), teacherId));
    }

    public boolean teacherOwnsQuiz(Long quizId, Long teacherId) {
        return findQuizForTeacher(quizId, teacherId).isPresent();
    }

    private Optional<Lesson> findLessonForTeacher(Long lessonId, Long teacherId) {
        if (lessonId == null || teacherId == null) {
            return Optional.empty();
        }

        return lessonRepository.findById(lessonId)
                .filter(lesson -> courseModuleService.teacherOwnsModule(lesson.getModuleId(), teacherId));
    }

    private Optional<Lesson> findLessonForTeacherWithResult(Long lessonId, Long teacherId, Result<?> result) {
        if (!requireId(lessonId, "Lesson id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }

        Optional<Lesson> lesson = findLessonForTeacher(lessonId, teacherId);

        if (lesson.isEmpty()) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
        }

        return lesson;
    }

    private Optional<Assignment> findAssignmentForTeacher(Long assignmentId, Long teacherId) {
        if (assignmentId == null || teacherId == null) {
            return Optional.empty();
        }

        return assignmentRepository.findById(assignmentId)
                .filter(assignment -> courseModuleService.teacherOwnsModule(assignment.getModuleId(), teacherId));
    }

    private Optional<Assignment> findAssignmentForTeacherWithResult(Long assignmentId, Long teacherId, Result<?> result) {
        if (!requireId(assignmentId, "Assignment id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }

        Optional<Assignment> assignment = findAssignmentForTeacher(assignmentId, teacherId);

        if (assignment.isEmpty()) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
        }

        return assignment;
    }

    private Optional<Quiz> findQuizForTeacherWithResult(Long quizId, Long teacherId, Result<?> result) {
        if (!requireId(quizId, "Quiz id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }

        Optional<Quiz> quiz = findQuizForTeacher(quizId, teacherId);

        if (quiz.isEmpty()) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
        }

        return quiz;
    }

    private boolean validateModuleAccess(Long moduleId, Long teacherId, Result<?> result) {
        if (!requireId(moduleId, "Module id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return false;
        }

        Optional<CourseModule> module = courseModuleService.findModuleForTeacher(moduleId, teacherId);

        if (module.isEmpty()) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return false;
        }

        return true;
    }

    private <T> boolean validateModuleWriteRequest(T model,
                                                   Long moduleId,
                                                   Long teacherId,
                                                   String missingModelMessage,
                                                   Result<?> result) {
        if (!validateModuleAccess(moduleId, teacherId, result)) {
            return false;
        }

        if (model == null) {
            result.addMessage(missingModelMessage, ResultType.INVALID);
            return false;
        }

        return true;
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
}