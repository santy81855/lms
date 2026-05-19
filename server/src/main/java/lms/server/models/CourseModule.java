package lms.server.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

public class CourseModule {
    private Long id;

    @NotNull(message = "Course id is required.")
    private Long courseId;

    @NotBlank(message = "Title is required.")
    @Size(max = 255, message = "Title must be 255 characters or fewer.")
    private String title;

    private String description;

    @NotNull(message = "Module order is required.")
    @Positive(message = "Module order must be greater than zero.")
    private Integer moduleOrder;

    @NotNull(message = "Module status is required.")
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public Integer getModuleOrder() {
        return moduleOrder;
    }

    public void setModuleOrder(Integer moduleOrder) {
        this.moduleOrder = moduleOrder;
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
        CourseModule that = (CourseModule) o;
        return Objects.equals(id, that.id) && Objects.equals(courseId, that.courseId) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(moduleOrder, that.moduleOrder) && status == that.status && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(publishedAt, that.publishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, title, description, moduleOrder, status, createdAt, updatedAt, publishedAt);
    }

    @Override
    public String toString() {
        return "CourseModule{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", moduleOrder=" + moduleOrder +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", publishedAt=" + publishedAt +
                '}';
    }
}
