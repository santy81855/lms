package lms.server.domain;

import lms.server.data.AssignmentRepository;
import lms.server.data.CourseModuleRepository;
import lms.server.data.CourseRepository;
import lms.server.data.LessonRepository;
import lms.server.data.ModuleContentItemRepository;
import lms.server.data.QuizRepository;
import lms.server.models.Assignment;
import lms.server.models.Course;
import lms.server.models.CourseModule;
import lms.server.models.CourseStatus;
import lms.server.models.Lesson;
import lms.server.models.Quiz;
import lms.server.models.VisibilityStatus;
import lms.server.models.dtos.ModuleContentItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentContentService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final ModuleContentItemRepository moduleContentItemRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final QuizRepository quizRepository;
    private final StudentCourseService studentCourseService;

    public StudentContentService(CourseRepository courseRepository,
                                 CourseModuleRepository courseModuleRepository,
                                 ModuleContentItemRepository moduleContentItemRepository,
                                 LessonRepository lessonRepository,
                                 AssignmentRepository assignmentRepository,
                                 QuizRepository quizRepository,
                                 StudentCourseService studentCourseService) {
        this.courseRepository = courseRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.moduleContentItemRepository = moduleContentItemRepository;
        this.lessonRepository = lessonRepository;
        this.assignmentRepository = assignmentRepository;
        this.quizRepository = quizRepository;
        this.studentCourseService = studentCourseService;
    }

    public Result<Course> findCourseByIdForStudent(Long courseId, Long studentId) {
        return findAccessibleCourse(courseId, studentId);
    }

    public Result<List<CourseModule>> findModulesByCourseId(Long courseId, Long studentId) {
        Result<List<CourseModule>> result = new Result<>();

        Result<Course> courseResult = findAccessibleCourse(courseId, studentId);

        if (!courseResult.isSuccess()) {
            copyErrors(courseResult, result);
            return result;
        }

        List<CourseModule> modules = courseModuleRepository.findByCourseId(courseId).stream()
                .filter(module -> module.getStatus() == VisibilityStatus.PUBLISHED)
                .toList();

        result.setPayload(modules);
        return result;
    }

    public Result<List<ModuleContentItem>> findModuleContentItems(Long moduleId, Long studentId) {
        Result<List<ModuleContentItem>> result = new Result<>();

        Result<CourseModule> moduleResult = findAccessibleModule(moduleId, studentId);

        if (!moduleResult.isSuccess()) {
            copyErrors(moduleResult, result);
            return result;
        }

        List<ModuleContentItem> contentItems = moduleContentItemRepository.findByModuleId(moduleId).stream()
                .filter(item -> item.getStatus() == VisibilityStatus.PUBLISHED)
                .toList();

        result.setPayload(contentItems);
        return result;
    }

    public Result<Lesson> findLessonById(Long lessonId, Long studentId) {
        Result<Lesson> result = new Result<>();

        if (!requireId(lessonId, "Lesson id is required.", result)
                || !requireId(studentId, "Student id is required.", result)) {
            return result;
        }

        Optional<Lesson> lesson = lessonRepository.findById(lessonId);

        if (lesson.isEmpty() || lesson.get().getStatus() != VisibilityStatus.PUBLISHED) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
            return result;
        }

        Result<CourseModule> moduleResult = findAccessibleModule(
                lesson.get().getModuleId(),
                studentId
        );

        if (!moduleResult.isSuccess()) {
            result.addMessage("Lesson not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(lesson.get());
        return result;
    }

    public Result<Assignment> findAssignmentById(Long assignmentId, Long studentId) {
        Result<Assignment> result = new Result<>();

        if (!requireId(assignmentId, "Assignment id is required.", result)
                || !requireId(studentId, "Student id is required.", result)) {
            return result;
        }

        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);

        if (assignment.isEmpty() || assignment.get().getStatus() != VisibilityStatus.PUBLISHED) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
            return result;
        }

        Result<CourseModule> moduleResult = findAccessibleModule(
                assignment.get().getModuleId(),
                studentId
        );

        if (!moduleResult.isSuccess()) {
            result.addMessage("Assignment not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(assignment.get());
        return result;
    }

    public Result<Quiz> findQuizById(Long quizId, Long studentId) {
        Result<Quiz> result = new Result<>();

        if (!requireId(quizId, "Quiz id is required.", result)
                || !requireId(studentId, "Student id is required.", result)) {
            return result;
        }

        Optional<Quiz> quiz = quizRepository.findById(quizId);

        if (quiz.isEmpty() || quiz.get().getStatus() != VisibilityStatus.PUBLISHED) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }

        Result<CourseModule> moduleResult = findAccessibleModule(
                quiz.get().getModuleId(),
                studentId
        );

        if (!moduleResult.isSuccess()) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(quiz.get());
        return result;
    }

    public Result<CourseModule> findModuleById(Long moduleId, Long studentId) {
        return findAccessibleModule(moduleId, studentId);
    }

    private Result<Course> findAccessibleCourse(Long courseId, Long studentId) {
        Result<Course> result = new Result<>();

        if (!requireId(courseId, "Course id is required.", result)
                || !requireId(studentId, "Student id is required.", result)) {
            return result;
        }

        Optional<Course> course = courseRepository.findById(courseId);

        if (course.isEmpty() || course.get().getStatus() != CourseStatus.ACTIVE) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        if (!studentCourseService.studentIsEnrolledInCourse(courseId, studentId)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(course.get());
        return result;
    }

    private Result<CourseModule> findAccessibleModule(Long moduleId, Long studentId) {
        Result<CourseModule> result = new Result<>();

        if (!requireId(moduleId, "Module id is required.", result)
                || !requireId(studentId, "Student id is required.", result)) {
            return result;
        }

        Optional<CourseModule> module = courseModuleRepository.findById(moduleId);

        if (module.isEmpty() || module.get().getStatus() != VisibilityStatus.PUBLISHED) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }

        Result<Course> courseResult = findAccessibleCourse(
                module.get().getCourseId(),
                studentId
        );

        if (!courseResult.isSuccess()) {
            result.addMessage("Module not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(module.get());
        return result;
    }

    private boolean requireId(Long id, String message, Result<?> result) {
        if (id == null) {
            result.addMessage(message, ResultType.INVALID);
            return false;
        }

        return true;
    }

    private void copyErrors(Result<?> source, Result<?> target) {
        for (String message : source.getMessages()) {
            target.addMessage(message, source.getType());
        }
    }
}