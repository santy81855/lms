package lms.server.models.dtos;

import lms.server.models.Quiz;

public class StudentQuizAttemptStatusResponse {

    private Long quizId;
    private Integer attemptsAllowed;
    private Integer attemptsUsed;
    private boolean canTake;

    public StudentQuizAttemptStatusResponse(Long quizId,
                                            Integer attemptsAllowed,
                                            Integer attemptsUsed,
                                            boolean canTake) {
        this.quizId = quizId;
        this.attemptsAllowed = attemptsAllowed;
        this.attemptsUsed = attemptsUsed;
        this.canTake = canTake;
    }

    public static StudentQuizAttemptStatusResponse from(Quiz quiz, int attemptsUsed) {
        return new StudentQuizAttemptStatusResponse(
                quiz.getId(),
                quiz.getAttemptsAllowedOrDefault(),
                attemptsUsed,
                quiz.canTakeWithAttemptsUsed(attemptsUsed)
        );
    }


    public Long getQuizId() {
        return quizId;
    }

    public Integer getAttemptsAllowed() {
        return attemptsAllowed;
    }

    public Integer getAttemptsUsed() {
        return attemptsUsed;
    }

    public Integer getAttemptsRemaining() {
        return calculateAttemptsRemaining();
    }

    public boolean isCanTake() {
        return canTake;
    }

    private Integer calculateAttemptsRemaining() {
        return Math.max(attemptsAllowed - attemptsUsed, 0);
    }
}