package lms.server.domain;

import lms.server.data.QuizAnswerOptionRepository;
import lms.server.data.QuizQuestionRepository;
import lms.server.data.QuizSubmissionAnswerRepository;
import lms.server.data.QuizSubmissionRepository;
import lms.server.models.*;
import lms.server.models.dtos.QuizSubmissionFeedbackResponse;
import lms.server.models.dtos.StudentQuizAttemptStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentQuizServiceTest {

    @MockitoBean
    StudentContentService studentContentService;

    @MockitoBean
    QuizQuestionRepository quizQuestionRepository;

    @MockitoBean
    QuizAnswerOptionRepository quizAnswerOptionRepository;

    @MockitoBean
    QuizSubmissionRepository quizSubmissionRepository;

    @MockitoBean
    QuizSubmissionAnswerRepository quizSubmissionAnswerRepository;

    StudentQuizService service;

    @BeforeEach
    void setup() {
        service = new StudentQuizService(
                studentContentService,
                quizQuestionRepository,
                quizAnswerOptionRepository,
                quizSubmissionRepository,
                quizSubmissionAnswerRepository
        );
    }

    @Test
    void findAttemptStatusShouldReturnAttemptStatusWhenQuizIsAccessible() {
        Quiz quiz = new Quiz();
        quiz.setId(7L);
        quiz.setAttemptsAllowed(3);

        Result<Quiz> quizResult = new Result<>();
        quizResult.setPayload(quiz);

        when(studentContentService.findQuizById(7L, 22L)).thenReturn(quizResult);
        when(quizSubmissionRepository.countByQuizIdAndStudentId(7L, 22L)).thenReturn(2);

        Result<StudentQuizAttemptStatusResponse> result =
                service.findAttemptStatus(7L, 22L);

        assertTrue(result.isSuccess());

        StudentQuizAttemptStatusResponse response = result.getPayload();

        assertEquals(7L, response.getQuizId());
        assertEquals(3, response.getAttemptsAllowed());
        assertEquals(2, response.getAttemptsUsed());
        assertEquals(1, response.getAttemptsRemaining());
        assertTrue(response.isCanTake());

        verify(studentContentService).findQuizById(7L, 22L);
        verify(quizSubmissionRepository).countByQuizIdAndStudentId(7L, 22L);
    }

    @Test
    void findAttemptStatusShouldReturnNotFoundWhenQuizIsNotAccessible() {
        Result<Quiz> quizResult = new Result<>();
        quizResult.addMessage("Quiz not found.", ResultType.NOT_FOUND);

        when(studentContentService.findQuizById(7L, 22L)).thenReturn(quizResult);

        Result<StudentQuizAttemptStatusResponse> result =
                service.findAttemptStatus(7L, 22L);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertTrue(result.getMessages().contains("Quiz not found."));

        verify(studentContentService).findQuizById(7L, 22L);
        verifyNoInteractions(quizSubmissionRepository);
    }

    @Test
    void findSubmissionFeedbackShouldReturnLessonReferenceFeedback() {
        Long quizId = 10L;
        Long submissionId = 20L;
        Long studentId = 30L;

        Quiz quiz = makeLessonReferenceQuiz(quizId);

        Result<Quiz> quizResult = new Result<>();
        quizResult.setPayload(quiz);

        QuizSubmission submission = new QuizSubmission();
        submission.setId(submissionId);
        submission.setQuizId(quizId);
        submission.setStudentId(studentId);
        submission.setScore(new BigDecimal("5.00"));
        submission.setMaxScore(new BigDecimal("10.00"));

        QuizQuestion questionOne = makeQuestion(
                1L,
                quizId,
                1,
                "Correct question",
                101L
        );

        QuizQuestion questionTwo = makeQuestion(
                2L,
                quizId,
                2,
                "Wrong question",
                102L
        );

        QuizSubmissionAnswer correctAnswer = makeAnswer(1L, true);
        QuizSubmissionAnswer wrongAnswer = makeAnswer(2L, false);

        when(studentContentService.findQuizById(quizId, studentId))
                .thenReturn(quizResult);

        when(quizSubmissionRepository.findById(submissionId))
                .thenReturn(Optional.of(submission));

        when(quizSubmissionAnswerRepository.findBySubmissionId(submissionId))
                .thenReturn(List.of(correctAnswer, wrongAnswer));

        when(quizQuestionRepository.findByQuizId(quizId))
                .thenReturn(List.of(questionOne, questionTwo));

        Result<QuizSubmissionFeedbackResponse> result =
                service.findSubmissionFeedback(quizId, submissionId, studentId);

        assertTrue(result.isSuccess());

        QuizSubmissionFeedbackResponse response = result.getPayload();

        assertEquals(FeedbackTypeCodes.LESSON_REFERENCE, response.getType());
        assertEquals(new BigDecimal("5.00"), response.getScore());
        assertEquals(new BigDecimal("10.00"), response.getMaxScore());
        assertEquals(1, response.getContent().size());

        assertEquals(2, response.getContent().get(0).getQuestionNumber());
        assertEquals("Wrong question", response.getContent().get(0).getQuestionContents());
        assertEquals(102L, response.getContent().get(0).getAssociatedLessonId());

        verify(studentContentService).findQuizById(quizId, studentId);
        verify(quizSubmissionRepository).findById(submissionId);
        verify(quizSubmissionAnswerRepository).findBySubmissionId(submissionId);
        verify(quizQuestionRepository).findByQuizId(quizId);
    }

    @Test
    void findSubmissionFeedbackShouldReturnNotFoundWhenSubmissionDoesNotBelongToStudent() {
        Long quizId = 10L;
        Long submissionId = 20L;
        Long studentId = 30L;

        Quiz quiz = makeLessonReferenceQuiz(quizId);

        Result<Quiz> quizResult = new Result<>();
        quizResult.setPayload(quiz);

        QuizSubmission submission = new QuizSubmission();
        submission.setId(submissionId);
        submission.setQuizId(quizId);
        submission.setStudentId(999L);

        when(studentContentService.findQuizById(quizId, studentId))
                .thenReturn(quizResult);

        when(quizSubmissionRepository.findById(submissionId))
                .thenReturn(Optional.of(submission));

        Result<QuizSubmissionFeedbackResponse> result =
                service.findSubmissionFeedback(quizId, submissionId, studentId);

        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertTrue(result.getMessages().contains("Quiz submission not found."));
    }

    @Test
    void findSubmissionFeedbackShouldReturnNotFoundWhenSubmissionDoesNotBelongToQuiz() {
        Long quizId = 10L;
        Long submissionId = 20L;
        Long studentId = 30L;

        Quiz quiz = makeLessonReferenceQuiz(quizId);

        Result<Quiz> quizResult = new Result<>();
        quizResult.setPayload(quiz);

        QuizSubmission submission = new QuizSubmission();
        submission.setId(submissionId);
        submission.setQuizId(999L);
        submission.setStudentId(studentId);

        when(studentContentService.findQuizById(quizId, studentId))
                .thenReturn(quizResult);

        when(quizSubmissionRepository.findById(submissionId))
                .thenReturn(Optional.of(submission));

        Result<QuizSubmissionFeedbackResponse> result =
                service.findSubmissionFeedback(quizId, submissionId, studentId);

        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertTrue(result.getMessages().contains("Quiz submission not found."));
    }

    private Quiz makeLessonReferenceQuiz(Long quizId) {
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setTitle("Quiz 1");
        quiz.setDescription("Quiz description");
        quiz.setQuizOrder(1);
        quiz.setMaxPoints(new BigDecimal("10.00"));
        quiz.setTimeLimitMinutes(30);
        quiz.setAttemptsAllowed(2);
        quiz.setFeedbackTypeCode(FeedbackTypeCodes.LESSON_REFERENCE);
        return quiz;
    }

    private QuizQuestion makeQuestion(Long id,
                                      Long quizId,
                                      Integer questionOrder,
                                      String questionText,
                                      Long associatedLessonId) {
        QuizQuestion question = new QuizQuestion();
        question.setId(id);
        question.setQuizId(quizId);
        question.setQuestionOrder(questionOrder);
        question.setQuestionText(questionText);
        question.setAssociatedLessonId(associatedLessonId);
        return question;
    }

    private QuizSubmissionAnswer makeAnswer(Long questionId, Boolean correct) {
        QuizSubmissionAnswer answer = new QuizSubmissionAnswer();
        answer.setQuestionId(questionId);
        answer.setCorrect(correct);
        return answer;
    }
}