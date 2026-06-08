package lms.server.models.dtos;

import lms.server.models.Quiz;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentQuizAttemptStatusResponseTest {

    @Test
    void fromShouldBuildResponseWhenStudentCanTakeQuiz() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);
        quiz.setAttemptsAllowed(3);

        StudentQuizAttemptStatusResponse response =
                StudentQuizAttemptStatusResponse.from(quiz, 1);

        assertEquals(10L, response.getQuizId());
        assertEquals(3, response.getAttemptsAllowed());
        assertEquals(1, response.getAttemptsUsed());
        assertEquals(2, response.getAttemptsRemaining());
        assertTrue(response.isCanTake());
    }

    @Test
    void fromShouldBuildResponseWhenStudentCannotTakeQuiz() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);
        quiz.setAttemptsAllowed(3);

        StudentQuizAttemptStatusResponse response =
                StudentQuizAttemptStatusResponse.from(quiz, 3);

        assertEquals(10L, response.getQuizId());
        assertEquals(3, response.getAttemptsAllowed());
        assertEquals(3, response.getAttemptsUsed());
        assertEquals(0, response.getAttemptsRemaining());
        assertFalse(response.isCanTake());
    }

    @Test
    void fromShouldUseDefaultAttemptsWhenQuizAttemptsAllowedIsNull() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);

        StudentQuizAttemptStatusResponse response =
                StudentQuizAttemptStatusResponse.from(quiz, 0);

        assertEquals(1, response.getAttemptsAllowed());
        assertEquals(0, response.getAttemptsUsed());
        assertEquals(1, response.getAttemptsRemaining());
        assertTrue(response.isCanTake());
    }

    @Test
    void getAttemptsRemainingShouldNotReturnNegativeNumber() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);
        quiz.setAttemptsAllowed(1);

        StudentQuizAttemptStatusResponse response =
                StudentQuizAttemptStatusResponse.from(quiz, 5);

        assertEquals(0, response.getAttemptsRemaining());
        assertFalse(response.isCanTake());
    }
}