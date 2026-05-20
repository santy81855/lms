package lms.server.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

public class Lesson {
    private Long id;

    @NotNull(message = "Module id is required.")
    private Long moduleId;

    @NotBlank(message = "Title is required.")
    @Size(max = 255, message = "Title must be 255 characters or fewer.")
    private String title;

    private String content;

    @NotNull(message = "Lesson order is required.")
    @Positive(message = "Lesson order must be greater than zero.")
    private Integer lessonOrder;

    @PositiveOrZero(message = "Estimated minutes cannot be negative.")
    private Integer estimatedMinutes;

    @NotNull(message = "Lesson status is required.")
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLessonOrder() {
        return lessonOrder;
    }

    public void setLessonOrder(Integer lessonOrder) {
        this.lessonOrder = lessonOrder;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id) && Objects.equals(moduleId, lesson.moduleId) && Objects.equals(title, lesson.title) && Objects.equals(content, lesson.content) && Objects.equals(lessonOrder, lesson.lessonOrder) && Objects.equals(estimatedMinutes, lesson.estimatedMinutes) && status == lesson.status && Objects.equals(createdAt, lesson.createdAt) && Objects.equals(updatedAt, lesson.updatedAt) && Objects.equals(publishedAt, lesson.publishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, moduleId, title, content, lessonOrder, estimatedMinutes, status, createdAt, updatedAt, publishedAt);
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", moduleId=" + moduleId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", lessonOrder=" + lessonOrder +
                ", estimatedMinutes=" + estimatedMinutes +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", publishedAt=" + publishedAt +
                '}';
    }
}