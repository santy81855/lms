package lms.server.domain;

import lms.server.models.Assignment;
import lms.server.models.Course;
import lms.server.models.CourseModule;
import lms.server.models.GradeLevel;
import lms.server.models.Lesson;
import lms.server.models.QuestionType;
import lms.server.models.Quiz;
import lms.server.models.QuizAnswerOption;
import lms.server.models.QuizQuestion;
import lms.server.models.SubmissionType;
import lms.server.models.dtos.CreateCourseFromSyllabusRequest;
import lms.server.models.dtos.GeneratedAssignmentPlan;
import lms.server.models.dtos.GeneratedCoursePlan;
import lms.server.models.dtos.GeneratedLessonPlan;
import lms.server.models.dtos.GeneratedModulePlan;
import lms.server.models.dtos.GeneratedQuizAnswerOptionPlan;
import lms.server.models.dtos.GeneratedQuizPlan;
import lms.server.models.dtos.GeneratedQuizQuestionPlan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AiCourseGenerationService {

    private final OpenAiCurriculumClient openAiCurriculumClient;
    private final CourseService courseService;
    private final CourseModuleService courseModuleService;
    private final ModuleContentService moduleContentService;
    private final QuizAuthoringService quizAuthoringService;

    public AiCourseGenerationService(OpenAiCurriculumClient openAiCurriculumClient,
                                     CourseService courseService,
                                     CourseModuleService courseModuleService,
                                     ModuleContentService moduleContentService,
                                     QuizAuthoringService quizAuthoringService) {
        this.openAiCurriculumClient = openAiCurriculumClient;
        this.courseService = courseService;
        this.courseModuleService = courseModuleService;
        this.moduleContentService = moduleContentService;
        this.quizAuthoringService = quizAuthoringService;
    }

    @Transactional
    public Result<Course> createCourseFromSyllabus(CreateCourseFromSyllabusRequest request,
                                                   Long teacherId) {
        Result<Course> result = new Result<>();

        if (teacherId == null) {
            result.addMessage("Teacher id is required.", ResultType.INVALID);
            return result;
        }

        validateRequest(request, result);

        if (!result.isSuccess()) {
            return result;
        }

        Result<GeneratedCoursePlan> generatedPlanResult =
                openAiCurriculumClient.generateCoursePlan(request);

        if (!generatedPlanResult.isSuccess()) {
            copyErrors(generatedPlanResult, result);
            return result;
        }

        GeneratedCoursePlan plan = generatedPlanResult.getPayload();

        if (plan == null) {
            result.addMessage("Generated course plan is required.", ResultType.INVALID);
            return result;
        }

        Result<Course> courseResult = createCourse(request, plan, teacherId);

        if (!courseResult.isSuccess()) {
            copyErrors(courseResult, result);
            return result;
        }

        Course createdCourse = courseResult.getPayload();

        try {
            createModulesAndContent(plan, createdCourse.getId(), teacherId);
        } catch (GeneratedCourseCreationException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            result.addMessage(ex.getMessage(), ResultType.INVALID);
            return result;
        }

        result.setPayload(createdCourse);
        return result;
    }

    private void createModulesAndContent(GeneratedCoursePlan plan,
                                         Long courseId,
                                         Long teacherId) {
        List<GeneratedModulePlan> modules = safeList(plan.getModules());

        for (GeneratedModulePlan modulePlan : modules) {
            CourseModule module = new CourseModule();
            module.setTitle(valueOrDefault(modulePlan.getTitle(), "Generated Module"));
            module.setDescription(valueOrDefault(modulePlan.getDescription(), ""));

            Result<CourseModule> moduleResult = courseModuleService.createModule(
                    module,
                    courseId,
                    teacherId
            );

            if (!moduleResult.isSuccess()) {
                throwCreationException("Could not create generated module", moduleResult);
            }

            CourseModule createdModule = moduleResult.getPayload();

            createLessons(modulePlan, createdModule.getId(), teacherId);
            createAssignments(modulePlan, createdModule.getId(), teacherId);
            createQuizzes(modulePlan, createdModule.getId(), teacherId);
        }
    }

    private Result<Course> createCourse(CreateCourseFromSyllabusRequest request,
                                        GeneratedCoursePlan plan,
                                        Long teacherId) {
        Course course = new Course();

        course.setTitle(valueOrDefault(plan.getTitle(), request.getTitle()));
        course.setSubject(valueOrDefault(plan.getSubject(), request.getSubject()));
        course.setGradeLevel(resolveGradeLevel(plan, request));
        course.setDescription(valueOrDefault(plan.getDescription(), request.getDescription()));

        return courseService.createCourse(course, teacherId);
    }

    private void createLessons(GeneratedModulePlan modulePlan,
                               Long moduleId,
                               Long teacherId) {
        for (GeneratedLessonPlan lessonPlan : safeList(modulePlan.getLessons())) {
            Lesson lesson = new Lesson();

            lesson.setTitle(valueOrDefault(lessonPlan.getTitle(), "Generated Lesson"));
            lesson.setContent(valueOrDefault(lessonPlan.getContent(), ""));
            lesson.setEstimatedMinutes(
                    lessonPlan.getEstimatedMinutes() == null ? 20 : lessonPlan.getEstimatedMinutes()
            );

            Result<Lesson> lessonResult = moduleContentService.createLesson(
                    lesson,
                    moduleId,
                    teacherId
            );

            if (!lessonResult.isSuccess()) {
                throwCreationException("Could not create generated lesson", lessonResult);
            }
        }
    }

    private void createAssignments(GeneratedModulePlan modulePlan,
                                   Long moduleId,
                                   Long teacherId) {
        for (GeneratedAssignmentPlan assignmentPlan : safeList(modulePlan.getAssignments())) {
            Assignment assignment = new Assignment();

            assignment.setTitle(valueOrDefault(assignmentPlan.getTitle(), "Generated Assignment"));
            assignment.setInstructions(valueOrDefault(assignmentPlan.getInstructions(), ""));
            assignment.setMaxPoints(
                    assignmentPlan.getMaxPoints() == null
                            ? BigDecimal.valueOf(100)
                            : assignmentPlan.getMaxPoints()
            );
            assignment.setSubmissionType(SubmissionType.TEXT);

            Result<Assignment> assignmentResult = moduleContentService.createAssignment(
                    assignment,
                    moduleId,
                    teacherId
            );

            if (!assignmentResult.isSuccess()) {
                throwCreationException("Could not create generated assignment", assignmentResult);
            }
        }
    }

    private void createQuizzes(GeneratedModulePlan modulePlan,
                               Long moduleId,
                               Long teacherId) {
        for (GeneratedQuizPlan quizPlan : safeList(modulePlan.getQuizzes())) {
            Quiz quiz = new Quiz();

            quiz.setTitle(valueOrDefault(quizPlan.getTitle(), "Generated Quiz"));
            quiz.setDescription(valueOrDefault(quizPlan.getDescription(), ""));
            quiz.setMaxPoints(
                    quizPlan.getMaxPoints() == null
                            ? BigDecimal.valueOf(10)
                            : quizPlan.getMaxPoints()
            );
            quiz.setTimeLimitMinutes(
                    quizPlan.getTimeLimitMinutes() == null ? 20 : quizPlan.getTimeLimitMinutes()
            );
            quiz.setAttemptsAllowed(
                    quizPlan.getAttemptsAllowed() == null ? 1 : quizPlan.getAttemptsAllowed()
            );

            Result<Quiz> quizResult = moduleContentService.createQuiz(
                    quiz,
                    moduleId,
                    teacherId
            );

            if (!quizResult.isSuccess()) {
                throwCreationException("Could not create generated quiz", quizResult);
            }

            Quiz createdQuiz = quizResult.getPayload();

            createQuizQuestions(quizPlan, createdQuiz.getId(), teacherId);
        }
    }

    private void createQuizQuestions(GeneratedQuizPlan quizPlan,
                                     Long quizId,
                                     Long teacherId) {
        for (GeneratedQuizQuestionPlan questionPlan : safeList(quizPlan.getQuestions())) {
            QuizQuestion question = new QuizQuestion();

            question.setQuestionText(valueOrDefault(questionPlan.getQuestionText(), "Generated question"));
            question.setQuestionType(
                    questionPlan.getQuestionType() == null
                            ? QuestionType.MULTIPLE_CHOICE
                            : questionPlan.getQuestionType()
            );
            question.setPoints(
                    questionPlan.getPoints() == null
                            ? BigDecimal.ONE
                            : questionPlan.getPoints()
            );
            question.setExplanation(valueOrDefault(questionPlan.getExplanation(), ""));

            Result<QuizQuestion> questionResult = quizAuthoringService.createQuizQuestion(
                    question,
                    quizId,
                    teacherId
            );

            if (!questionResult.isSuccess()) {
                throwCreationException("Could not create generated quiz question", questionResult);
            }

            QuizQuestion createdQuestion = questionResult.getPayload();

            createQuizAnswerOptions(questionPlan, createdQuestion.getId(), teacherId);
        }
    }

    private void createQuizAnswerOptions(GeneratedQuizQuestionPlan questionPlan,
                                         Long questionId,
                                         Long teacherId) {
        for (GeneratedQuizAnswerOptionPlan optionPlan : safeList(questionPlan.getOptions())) {
            QuizAnswerOption option = new QuizAnswerOption();

            option.setOptionText(valueOrDefault(optionPlan.getOptionText(), "Generated option"));
            option.setCorrect(Boolean.TRUE.equals(optionPlan.getCorrect()));

            Result<QuizAnswerOption> optionResult = quizAuthoringService.createQuizAnswerOption(
                    option,
                    questionId,
                    teacherId
            );

            if (!optionResult.isSuccess()) {
                throwCreationException("Could not create generated quiz answer option", optionResult);
            }
        }
    }

    private void validateRequest(CreateCourseFromSyllabusRequest request,
                                 Result<?> result) {
        if (request == null) {
            result.addMessage("Course generation request is required.", ResultType.INVALID);
            return;
        }

        if (request.getSyllabusText() == null || request.getSyllabusText().isBlank()) {
            result.addMessage("Syllabus text is required.", ResultType.INVALID);
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            result.addMessage("Course title is required.", ResultType.INVALID);
        }

        if (request.getGradeLevel() == null) {
            result.addMessage("Grade level is required.", ResultType.INVALID);
        }
    }

    private GradeLevel resolveGradeLevel(GeneratedCoursePlan plan,
                                         CreateCourseFromSyllabusRequest request) {
        if (plan.getGradeLevel() != null) {
            return plan.getGradeLevel();
        }

        if (request.getGradeLevel() != null) {
            return request.getGradeLevel();
        }

        return GradeLevel.OTHER;
    }

    private String valueOrDefault(String value, String fallback) {
        if (value != null && !value.isBlank()) {
            return value;
        }

        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }

        return "";
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private void copyErrors(Result<?> source, Result<?> target) {
        for (String message : source.getMessages()) {
            target.addMessage(message, source.getType());
        }
    }

    private void throwCreationException(String prefix, Result<?> result) {
        String message = prefix + ": " + String.join(", ", result.getMessages());
        throw new GeneratedCourseCreationException(message);
    }

    private static class GeneratedCourseCreationException extends RuntimeException {
        public GeneratedCourseCreationException(String message) {
            super(message);
        }
    }
}