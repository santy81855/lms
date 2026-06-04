package lms.server.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class QuizQuestion {
    private Long id;

    @NotNull(message = "Quiz id is required.")
    private Long quizId;

    @NotBlank(message = "Question text is required.")
    private String questionText;

    @NotNull(message = "Question type is required.")
    private QuestionType questionType;

    @NotNull(message = "Question order is required.")
    @Positive(message = "Question order must be greater than zero.")
    private Integer questionOrder;

    @NotNull(message = "Points is required.")
    @DecimalMin(value = "0.00", message = "Points cannot be negative.")
    private BigDecimal points;

    private String explanation;

    private Long associatedLessonId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BigDecimal getPoints() {
        return points;
    }

    public void setPoints(BigDecimal points) {
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Long getAssociatedLessonId() {
        return associatedLessonId;
    }

    public void setAssociatedLessonId(Long associatedLessonId) {
        this.associatedLessonId = associatedLessonId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizQuestion that = (QuizQuestion) o;
        return Objects.equals(id, that.id) && Objects.equals(quizId, that.quizId) && Objects.equals(questionText, that.questionText) && questionType == that.questionType && Objects.equals(questionOrder, that.questionOrder) && Objects.equals(points, that.points) && Objects.equals(explanation, that.explanation) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quizId, questionText, questionType, questionOrder, points, explanation, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "id=" + id +
                ", quizId=" + quizId +
                ", questionText='" + questionText + '\'' +
                ", questionType=" + questionType +
                ", questionOrder=" + questionOrder +
                ", points=" + points +
                ", explanation='" + explanation + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}