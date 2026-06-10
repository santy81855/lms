package lms.server.domain;

import lms.server.data.QuizAnswerOptionRepository;
import lms.server.data.QuizQuestionRepository;
import lms.server.data.QuizSubmissionAnswerRepository;
import lms.server.data.QuizSubmissionRepository;
import lms.server.models.QuestionType;
import lms.server.models.Quiz;
import lms.server.models.QuizAnswerOption;
import lms.server.models.QuizQuestion;
import lms.server.models.QuizSubmission;
import lms.server.models.QuizSubmissionAnswer;
import lms.server.models.QuizSubmissionStatus;
import lms.server.models.dtos.StudentQuizAnswerRequest;
import lms.server.models.dtos.StudentQuizOptionResponse;
import lms.server.models.dtos.StudentQuizQuestionResponse;
import lms.server.models.dtos.StudentQuizResponse;
import lms.server.models.dtos.StudentQuizResultResponse;
import lms.server.models.dtos.StudentQuizSubmitRequest;
import lms.server.models.dtos.StudentQuizAttemptStatusResponse;
import lms.server.models.QuizFeedbackType;
import lms.server.models.dtos.QuizSubmissionFeedbackResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StudentQuizService {

    private final StudentContentService studentContentService;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerOptionRepository quizAnswerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizSubmissionAnswerRepository quizSubmissionAnswerRepository;

    public StudentQuizService(StudentContentService studentContentService,
                              QuizQuestionRepository quizQuestionRepository,
                              QuizAnswerOptionRepository quizAnswerOptionRepository,
                              QuizSubmissionRepository quizSubmissionRepository,
                              QuizSubmissionAnswerRepository quizSubmissionAnswerRepository) {
        this.studentContentService = studentContentService;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerOptionRepository = quizAnswerOptionRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.quizSubmissionAnswerRepository = quizSubmissionAnswerRepository;
    }

    public Result<StudentQuizAttemptStatusResponse> findAttemptStatus(Long quizId, Long studentId) {
        Result<StudentQuizAttemptStatusResponse> result = new Result<>();

        Result<Quiz> quizResult = studentContentService.findQuizById(quizId, studentId);

        if (!quizResult.isSuccess()) {
            copyErrors(quizResult, result);
            return result;
        }

        Quiz quiz = quizResult.getPayload();

        int attemptsUsed = quizSubmissionRepository.countByQuizIdAndStudentId(quizId, studentId);

        result.setPayload(StudentQuizAttemptStatusResponse.from(quiz, attemptsUsed));

        return result;
    }

    public Result<StudentQuizResponse> findQuizForTaking(Long quizId, Long studentId) {
        Result<StudentQuizResponse> result = new Result<>();

        Result<Quiz> quizResult = studentContentService.findQuizById(quizId, studentId);

        if (!quizResult.isSuccess()) {
            copyErrors(quizResult, result);
            return result;
        }

        Quiz quiz = quizResult.getPayload();

        StudentQuizResponse response = buildStudentQuizResponse(quiz);

        result.setPayload(response);
        return result;
    }

    @Transactional
    public Result<StudentQuizResultResponse> submitQuiz(Long quizId,
                                                        Long studentId,
                                                        StudentQuizSubmitRequest request) {
        Result<StudentQuizResultResponse> result = new Result<>();

        Result<Quiz> quizResult = studentContentService.findQuizById(quizId, studentId);

        if (!quizResult.isSuccess()) {
            copyErrors(quizResult, result);
            return result;
        }

        Quiz quiz = quizResult.getPayload();

        List<QuizQuestion> questions = quizQuestionRepository.findByQuizId(quizId).stream()
                .sorted(Comparator.comparing(QuizQuestion::getQuestionOrder))
                .toList();

        if (questions.isEmpty()) {
            result.addMessage("Quiz has no questions.", ResultType.INVALID);
            return result;
        }

        int attemptsUsed = quizSubmissionRepository.countByQuizIdAndStudentId(quizId, studentId);
        int attemptsAllowed = quiz.getAttemptsAllowed() == null ? 1 : quiz.getAttemptsAllowed();

        if (attemptsUsed >= attemptsAllowed) {
            result.addMessage("No quiz attempts remaining.", ResultType.INVALID);
            return result;
        }

        Map<Long, StudentQuizAnswerRequest> answerMap = mapAnswers(request, result);

        if (!result.isSuccess()) {
            return result;
        }

        HashSet<Long> validQuestionIds = new HashSet<>();

        for (QuizQuestion question : questions) {
            validQuestionIds.add(question.getId());
        }

        for (Long answeredQuestionId : answerMap.keySet()) {
            if (!validQuestionIds.contains(answeredQuestionId)) {
                result.addMessage("Submitted answer does not belong to this quiz.", ResultType.INVALID);
                return result;
            }
        }

        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal maxScore = BigDecimal.ZERO;
        List<QuizSubmissionAnswer> answersToSave = new ArrayList<>();

        for (QuizQuestion question : questions) {
            if (question.getQuestionType() == QuestionType.SHORT_ANSWER) {
                result.addMessage("Short answer questions are not supported for auto-graded quizzes yet.", ResultType.INVALID);
                return result;
            }

            StudentQuizAnswerRequest submittedAnswer = answerMap.get(question.getId());

            if (submittedAnswer == null) {
                result.addMessage("Every question must be answered.", ResultType.INVALID);
                return result;
            }

            if (submittedAnswer.getSelectedOptionId() == null) {
                result.addMessage("Selected option id is required for each answer.", ResultType.INVALID);
                return result;
            }

            QuizAnswerOption selectedOption = quizAnswerOptionRepository
                    .findByIdAndQuestionId(submittedAnswer.getSelectedOptionId(), question.getId())
                    .orElse(null);

            if (selectedOption == null) {
                result.addMessage("Selected option does not belong to the question.", ResultType.INVALID);
                return result;
            }

            BigDecimal questionPoints = question.getPoints() == null ? BigDecimal.ZERO : question.getPoints();
            maxScore = maxScore.add(questionPoints);

            boolean correct = Boolean.TRUE.equals(selectedOption.getCorrect());
            BigDecimal pointsEarned = correct ? questionPoints : BigDecimal.ZERO;

            totalScore = totalScore.add(pointsEarned);

            QuizSubmissionAnswer answer = new QuizSubmissionAnswer();
            answer.setQuestionId(question.getId());
            answer.setSelectedOptionId(selectedOption.getId());
            answer.setShortAnswerText(null);
            answer.setCorrect(correct);
            answer.setPointsEarned(pointsEarned);

            answersToSave.add(answer);
        }

        QuizSubmission submission = new QuizSubmission();
        submission.setQuizId(quizId);
        submission.setStudentId(studentId);
        submission.setAttemptNumber(attemptsUsed + 1);
        submission.setStatus(QuizSubmissionStatus.GRADED);
        submission.setScore(totalScore);
        submission.setMaxScore(maxScore);
        submission.setStartedAt(LocalDateTime.now());
        submission.setGradedAt(LocalDateTime.now());

        QuizSubmission savedSubmission = quizSubmissionRepository.add(submission);

        for (QuizSubmissionAnswer answer : answersToSave) {
            answer.setQuizSubmissionId(savedSubmission.getId());
            quizSubmissionAnswerRepository.add(answer);
        }

        result.setPayload(buildResultResponse(savedSubmission));
        return result;
    }

    public Result<List<StudentQuizResultResponse>> findAllResults(Long quizId, Long studentId) {
        Result<List<StudentQuizResultResponse>> result = new Result<>();

        Result<Quiz> quizResult = studentContentService.findQuizById(quizId, studentId);

        if (!quizResult.isSuccess()) {
            copyErrors(quizResult, result);
            return result;
        }

        // find all submissions
        List<QuizSubmission> submissionList = quizSubmissionRepository.findByQuizIdAndStudentId(quizId, studentId);

        if (submissionList.isEmpty()) {
            result.addMessage("No quiz results found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(buildResultListResponse(submissionList));
        return result;
    }

    private List<StudentQuizResultResponse> buildResultListResponse(List<QuizSubmission> list) {
        List<StudentQuizResultResponse> ret = new ArrayList<>();
        for (QuizSubmission sub : list) {
            StudentQuizResultResponse temp = buildResultResponse(sub);
            temp.setSubmittedAt(sub.getSubmittedAt()); // add the submitted at field only to the list of all submissions
            ret.add(temp);
        }
        return ret;
    }

    public Result<StudentQuizResultResponse> findLatestResult(Long quizId, Long studentId) {
        Result<StudentQuizResultResponse> result = new Result<>();

        Result<Quiz> quizResult = studentContentService.findQuizById(quizId, studentId);

        if (!quizResult.isSuccess()) {
            copyErrors(quizResult, result);
            return result;
        }

        QuizSubmission submission = quizSubmissionRepository
                .findLatestByQuizIdAndStudentId(quizId, studentId)
                .orElse(null);

        if (submission == null) {
            result.addMessage("Quiz result not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(buildResultResponse(submission));
        return result;
    }

    private StudentQuizResponse buildStudentQuizResponse(Quiz quiz) {
        StudentQuizResponse response = new StudentQuizResponse();

        response.setId(quiz.getId());
        response.setModuleId(quiz.getModuleId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setMaxPoints(quiz.getMaxPoints());
        response.setTimeLimitMinutes(quiz.getTimeLimitMinutes());
        response.setAttemptsAllowed(quiz.getAttemptsAllowed());

        List<StudentQuizQuestionResponse> questions = quizQuestionRepository.findByQuizId(quiz.getId()).stream()
                .sorted(Comparator.comparing(QuizQuestion::getQuestionOrder))
                .map(this::buildQuestionResponse)
                .toList();

        response.setQuestions(questions);

        return response;
    }

    private StudentQuizQuestionResponse buildQuestionResponse(QuizQuestion question) {
        StudentQuizQuestionResponse response = new StudentQuizQuestionResponse();

        response.setId(question.getId());
        response.setQuizId(question.getQuizId());
        response.setQuestionText(question.getQuestionText());
        response.setQuestionType(question.getQuestionType());
        response.setQuestionOrder(question.getQuestionOrder());
        response.setPoints(question.getPoints());

        List<StudentQuizOptionResponse> options = quizAnswerOptionRepository.findByQuestionId(question.getId()).stream()
                .sorted(Comparator.comparing(QuizAnswerOption::getOptionOrder))
                .map(this::buildOptionResponse)
                .toList();

        response.setOptions(options);

        return response;
    }

    private StudentQuizOptionResponse buildOptionResponse(QuizAnswerOption option) {
        StudentQuizOptionResponse response = new StudentQuizOptionResponse();

        response.setId(option.getId());
        response.setQuestionId(option.getQuestionId());
        response.setOptionText(option.getOptionText());
        response.setOptionOrder(option.getOptionOrder());

        return response;
    }

    private StudentQuizResultResponse buildResultResponse(QuizSubmission submission) {
        StudentQuizResultResponse response = new StudentQuizResultResponse();

        response.setSubmissionId(submission.getId());
        response.setQuizId(submission.getQuizId());
        response.setStudentId(submission.getStudentId());
        response.setAttemptNumber(submission.getAttemptNumber());
        response.setScore(submission.getScore());
        response.setMaxScore(submission.getMaxScore());

        return response;
    }

    private Map<Long, StudentQuizAnswerRequest> mapAnswers(StudentQuizSubmitRequest request,
                                                           Result<?> result) {
        Map<Long, StudentQuizAnswerRequest> answerMap = new HashMap<>();

        if (request == null || request.getAnswers() == null || request.getAnswers().isEmpty()) {
            result.addMessage("Quiz answers are required.", ResultType.INVALID);
            return answerMap;
        }

        for (StudentQuizAnswerRequest answer : request.getAnswers()) {
            if (answer == null || answer.getQuestionId() == null) {
                result.addMessage("Question id is required for each answer.", ResultType.INVALID);
                continue;
            }

            if (answerMap.containsKey(answer.getQuestionId())) {
                result.addMessage("Each question can only be answered once.", ResultType.INVALID);
                continue;
            }

            answerMap.put(answer.getQuestionId(), answer);
        }

        return answerMap;
    }

    private void copyErrors(Result<?> source, Result<?> target) {
        for (String message : source.getMessages()) {
            target.addMessage(message, source.getType());
        }
    }

    public Result<QuizSubmissionFeedbackResponse> findSubmissionFeedback(Long quizId, Long submissionId, Long studentId) {
        Result<QuizSubmissionFeedbackResponse> result = new Result<>();

        if (quizId == null) {
            result.addMessage("Quiz id is required.", ResultType.INVALID);
            return result;
        }

        if (submissionId == null) {
            result.addMessage("Submission id is required.", ResultType.INVALID);
            return result;
        }

        if (studentId == null) {
            result.addMessage("Student id is required.", ResultType.INVALID);
            return result;
        }

        Result<Quiz> quizResult = studentContentService.findQuizById(quizId, studentId);

        if (!quizResult.isSuccess()) {
            copyErrors(quizResult, result);
            return result;
        }

        Quiz quiz = quizResult.getPayload();

        Optional<QuizSubmission> submissionResult =
                quizSubmissionRepository.findById(submissionId);

        if (submissionResult.isEmpty()) {
            result.addMessage("Quiz submission not found.", ResultType.NOT_FOUND);
            return result;
        }

        QuizSubmission submission = submissionResult.get();

        if (!submission.getQuizId().equals(quizId)
                || !submission.getStudentId().equals(studentId)) {
            result.addMessage("Quiz submission not found.", ResultType.NOT_FOUND);
            return result;
        }

        if (quiz.usesNoFeedback()) {
            result.setPayload(QuizSubmissionFeedbackResponse.noFeedback(quiz));
            return result;
        }

        if (quiz.usesScoreFeedback()) {
            result.setPayload(QuizSubmissionFeedbackResponse.scoreOnly(quiz, submission));
            return result;
        }

        if (quiz.usesLessonReferenceFeedback()) {
            List<QuizSubmissionAnswer> submissionAnswers =
                    quizSubmissionAnswerRepository.findBySubmissionId(submissionId);

            List<QuizQuestion> questions =
                    quizQuestionRepository.findByQuizId(quizId);

            result.setPayload(QuizSubmissionFeedbackResponse.lessonReference(
                    quiz,
                    submission,
                    submissionAnswers,
                    questions
            ));
            return result;
        }

        if (quiz.usesAiOverviewFeedback()) {
            result.setPayload(QuizSubmissionFeedbackResponse.aiOverviewPlaceholder(quiz, submission));
            return result;
        }

        result.setPayload(QuizSubmissionFeedbackResponse.scoreOnly(quiz, submission));
        return result;
    }
}