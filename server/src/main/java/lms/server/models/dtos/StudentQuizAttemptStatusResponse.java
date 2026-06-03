package lms.server.models.dtos;

public class StudentQuizAttemptStatusResponse {

    private Long quizId;
    private Integer attemptsAllowed;
    private Integer attemptsUsed;
    private Integer attemptsRemaining;
    private boolean canTake;

    public StudentQuizAttemptStatusResponse(Long quizId,
                                            Integer attemptsAllowed,
                                            Integer attemptsUsed) {
        this.quizId = quizId;
        this.attemptsAllowed = attemptsAllowed;
        this.attemptsUsed = attemptsUsed;
        this.attemptsRemaining = Math.max(attemptsAllowed - attemptsUsed, 0);
        this.canTake = attemptsUsed < attemptsAllowed;
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
        return attemptsRemaining;
    }

    public boolean isCanTake() {
        return canTake;
    }
}