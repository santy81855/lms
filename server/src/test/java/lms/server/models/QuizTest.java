package lms.server.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuizTest {

    @Test
    void getAttemptsAllowedOrDefaultShouldReturnAttemptsAllowedWhenPresent() {
        Quiz quiz = new Quiz();
        quiz.setAttemptsAllowed(3);

        assertEquals(3, quiz.getAttemptsAllowedOrDefault());
    }

    @Test
    void getAttemptsAllowedOrDefaultShouldReturnOneWhenAttemptsAllowedIsNull() {
        Quiz quiz = new Quiz();

        assertEquals(1, quiz.getAttemptsAllowedOrDefault());
    }

    @Test
    void canTakeWithAttemptsUsedShouldReturnTrueWhenAttemptsRemain() {
        Quiz quiz = new Quiz();
        quiz.setAttemptsAllowed(3);

        assertTrue(quiz.canTakeWithAttemptsUsed(2));
    }

    @Test
    void canTakeWithAttemptsUsedShouldReturnFalseWhenNoAttemptsRemain() {
        Quiz quiz = new Quiz();
        quiz.setAttemptsAllowed(3);

        assertFalse(quiz.canTakeWithAttemptsUsed(3));
    }

    @Test
    void getAttemptsRemainingShouldReturnRemainingAttempts() {
        Quiz quiz = new Quiz();
        quiz.setAttemptsAllowed(3);

        assertEquals(1, quiz.getAttemptsRemaining(2));
    }

    @Test
    void getAttemptsRemainingShouldNotReturnNegativeNumber() {
        Quiz quiz = new Quiz();
        quiz.setAttemptsAllowed(3);

        assertEquals(0, quiz.getAttemptsRemaining(5));
    }
}