package lms.server.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lms.server.models.QuizSubmission;
import lms.server.models.QuizSubmissionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuizSubmissionResponse {

    private Long id;

    @JsonProperty("quiz_id")
    private Long quizId;

    @JsonProperty("student_id")
    private Long studentId;

    @JsonProperty("attempt_number")
    private Integer attemptNumber;

    private QuizSubmissionStatus status;

    private BigDecimal score;

    @JsonProperty("max_score")
    private BigDecimal maxScore;

    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt;

    @JsonProperty("graded_at")
    private LocalDateTime gradedAt;

    public QuizSubmissionResponse(QuizSubmission submission) {
        this.id = submission.getId();
        this.quizId = submission.getQuizId();
        this.studentId = submission.getStudentId();
        this.attemptNumber = submission.getAttemptNumber();
        this.status = submission.getStatus();
        this.score = submission.getScore();
        this.maxScore = submission.getMaxScore();
        this.startedAt = submission.getStartedAt();
        this.submittedAt = submission.getSubmittedAt();
        this.gradedAt = submission.getGradedAt();
    }

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
}
