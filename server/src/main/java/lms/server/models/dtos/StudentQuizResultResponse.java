package lms.server.models.dtos;

import java.math.BigDecimal;

public class StudentQuizResultResponse {

    private Long submissionId;
    private Long quizId;
    private Long studentId;
    private Integer attemptNumber;
    private BigDecimal score;
    private BigDecimal maxScore;

    public Long getSubmissionId() {
        return submissionId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public BigDecimal getScore() {
        return score;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }
}