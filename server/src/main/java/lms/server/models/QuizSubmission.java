package lms.server.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class QuizSubmission {

    private Long id;

    @NotNull(message = "Quiz id is required.")
    private Long quizId;

    @NotNull(message = "Student id is required.")
    private Long studentId;

    @NotNull(message = "Attempt number is required.")
    @Positive(message = "Attempt number must be greater than zero.")
    private Integer attemptNumber;

    @NotNull(message = "Quiz submission status is required.")
    private QuizSubmissionStatus status;

    @DecimalMin(value = "0.00", message = "Score cannot be negative.")
    private BigDecimal score;

    @NotNull(message = "Max score is required.")
    @DecimalMin(value = "0.00", message = "Max score cannot be negative.")
    private BigDecimal maxScore;

    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;

    public Long getId() {
        return id;
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

    public QuizSubmissionStatus getStatus() {
        return status;
    }

    public BigDecimal getScore() {
        return score;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setStatus(QuizSubmissionStatus status) {
        this.status = status;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuizSubmission that = (QuizSubmission) o;

        return Objects.equals(id, that.id)
                && Objects.equals(quizId, that.quizId)
                && Objects.equals(studentId, that.studentId)
                && Objects.equals(attemptNumber, that.attemptNumber)
                && status == that.status
                && Objects.equals(score, that.score)
                && Objects.equals(maxScore, that.maxScore)
                && Objects.equals(startedAt, that.startedAt)
                && Objects.equals(submittedAt, that.submittedAt)
                && Objects.equals(gradedAt, that.gradedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                quizId,
                studentId,
                attemptNumber,
                status,
                score,
                maxScore,
                startedAt,
                submittedAt,
                gradedAt
        );
    }

    @Override
    public String toString() {
        return "QuizSubmission{" +
                "id=" + id +
                ", quizId=" + quizId +
                ", studentId=" + studentId +
                ", attemptNumber=" + attemptNumber +
                ", status=" + status +
                ", score=" + score +
                ", maxScore=" + maxScore +
                ", startedAt=" + startedAt +
                ", submittedAt=" + submittedAt +
                ", gradedAt=" + gradedAt +
                '}';
    }
}