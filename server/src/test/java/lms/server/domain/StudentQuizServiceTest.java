package lms.server.domain;

import lms.server.data.QuizAnswerOptionRepository;
import lms.server.data.QuizQuestionRepository;
import lms.server.data.QuizSubmissionAnswerRepository;
import lms.server.data.QuizSubmissionRepository;
import lms.server.models.Quiz;
import lms.server.models.dtos.StudentQuizAttemptStatusResponse;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentQuizServiceTest {

    @Test
    void findAttemptStatusShouldReturnAttemptStatusWhenQuizIsAccessible() {
        StudentContentService studentContentService = mock(StudentContentService.class);
        QuizQuestionRepository quizQuestionRepository = mock(QuizQuestionRepository.class);
        QuizAnswerOptionRepository quizAnswerOptionRepository = mock(QuizAnswerOptionRepository.class);
        QuizSubmissionRepository quizSubmissionRepository = mock(QuizSubmissionRepository.class);
        QuizSubmissionAnswerRepository quizSubmissionAnswerRepository = mock(QuizSubmissionAnswerRepository.class);

        StudentQuizService service = new StudentQuizService(
                studentContentService,
                quizQuestionRepository,
                quizAnswerOptionRepository,
                quizSubmissionRepository,
                quizSubmissionAnswerRepository
        );

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
        StudentContentService studentContentService = mock(StudentContentService.class);
        QuizQuestionRepository quizQuestionRepository = mock(QuizQuestionRepository.class);
        QuizAnswerOptionRepository quizAnswerOptionRepository = mock(QuizAnswerOptionRepository.class);
        QuizSubmissionRepository quizSubmissionRepository = mock(QuizSubmissionRepository.class);
        QuizSubmissionAnswerRepository quizSubmissionAnswerRepository = mock(QuizSubmissionAnswerRepository.class);

        StudentQuizService service = new StudentQuizService(
                studentContentService,
                quizQuestionRepository,
                quizAnswerOptionRepository,
                quizSubmissionRepository,
                quizSubmissionAnswerRepository
        );

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
}