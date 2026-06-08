package lms.server.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Quiz {
    private Long id;

    @NotNull(message = "Module id is required.")
    private Long moduleId;

    @NotBlank(message = "Title is required.")
    @Size(max = 255, message = "Title must be 255 characters or fewer.")
    private String title;

    private String description;

    @NotNull(message = "Quiz order is required.")
    @Positive(message = "Quiz order must be greater than zero.")
    private Integer quizOrder;

    @NotNull(message = "Max points is required.")
    @DecimalMin(value = "0.00", message = "Max points cannot be negative.")
    private BigDecimal maxPoints;

    @PositiveOrZero(message = "Time limit minutes cannot be negative.")
    private Integer timeLimitMinutes;

    @NotNull(message = "Attempts allowed is required.")
    @Positive(message = "Attempts allowed must be greater than zero.")
    private Integer attemptsAllowed;

    @NotNull(message = "Quiz status is required.")
    private VisibilityStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuizOrder() {
        return quizOrder;
    }

    public void setQuizOrder(Integer quizOrder) {
        this.quizOrder = quizOrder;
    }

    public BigDecimal getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(BigDecimal maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public Integer getAttemptsAllowed() {
        return attemptsAllowed;
    }

    public void setAttemptsAllowed(Integer attemptsAllowed) {
        this.attemptsAllowed = attemptsAllowed;
    }

    public VisibilityStatus getStatus() {
        return status;
    }

    public void setStatus(VisibilityStatus status) {
        this.status = status;
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

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public int getAttemptsAllowedOrDefault() {
        return attemptsAllowed == null ? 1 : attemptsAllowed;
    }

    public boolean canTakeWithAttemptsUsed(int attemptsUsed) {
        return attemptsUsed < getAttemptsAllowedOrDefault();
    }

    public int getAttemptsRemaining(int attemptsUsed) {
        return Math.max(getAttemptsAllowedOrDefault() - attemptsUsed, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return Objects.equals(id, quiz.id) && Objects.equals(moduleId, quiz.moduleId) && Objects.equals(title, quiz.title) && Objects.equals(description, quiz.description) && Objects.equals(quizOrder, quiz.quizOrder) && Objects.equals(maxPoints, quiz.maxPoints) && Objects.equals(timeLimitMinutes, quiz.timeLimitMinutes) && Objects.equals(attemptsAllowed, quiz.attemptsAllowed) && status == quiz.status && Objects.equals(createdAt, quiz.createdAt) && Objects.equals(updatedAt, quiz.updatedAt) && Objects.equals(publishedAt, quiz.publishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, moduleId, title, description, quizOrder, maxPoints, timeLimitMinutes, attemptsAllowed, status, createdAt, updatedAt, publishedAt);
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", moduleId=" + moduleId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", quizOrder=" + quizOrder +
                ", maxPoints=" + maxPoints +
                ", timeLimitMinutes=" + timeLimitMinutes +
                ", attemptsAllowed=" + attemptsAllowed +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", publishedAt=" + publishedAt +
                '}';
    }
}