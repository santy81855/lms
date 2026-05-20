package lms.server.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Objects;

public class QuizAnswerOption {
    private Long id;

    @NotNull(message = "Question id is required.")
    private Long questionId;

    @NotBlank(message = "Option text is required.")
    private String optionText;

    @NotNull(message = "Option order is required.")
    @Positive(message = "Option order must be greater than zero.")
    private Integer optionOrder;

    @NotNull(message = "Correct answer flag is required.")
    private Boolean correct;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Integer getOptionOrder() {
        return optionOrder;
    }

    public void setOptionOrder(Integer optionOrder) {
        this.optionOrder = optionOrder;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
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
        QuizAnswerOption that = (QuizAnswerOption) o;
        return Objects.equals(id, that.id) && Objects.equals(questionId, that.questionId) && Objects.equals(optionText, that.optionText) && Objects.equals(optionOrder, that.optionOrder) && Objects.equals(correct, that.correct) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, questionId, optionText, optionOrder, correct, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "QuizAnswerOption{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", optionText='" + optionText + '\'' +
                ", optionOrder=" + optionOrder +
                ", correct=" + correct +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}