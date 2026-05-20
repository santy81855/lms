package lms.server.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Assignment {
    private Long id;

    @NotNull(message = "Module id is required.")
    private Long moduleId;

    @NotBlank(message = "Title is required.")
    @Size(max = 255, message = "Title must be 255 characters or fewer.")
    private String title;

    private String instructions;

    @NotNull(message = "Assignment order is required.")
    @Positive(message = "Assignment order must be greater than zero.")
    private Integer assignmentOrder;

    private LocalDateTime dueAt;

    @NotNull(message = "Max points is required.")
    @DecimalMin(value = "0.00", message = "Max points cannot be negative.")
    private BigDecimal maxPoints;

    @NotNull(message = "Submission type is required.")
    private SubmissionType submissionType;

    @NotNull(message = "Assignment status is required.")
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getAssignmentOrder() {
        return assignmentOrder;
    }

    public void setAssignmentOrder(Integer assignmentOrder) {
        this.assignmentOrder = assignmentOrder;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public BigDecimal getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(BigDecimal maxPoints) {
        this.maxPoints = maxPoints;
    }

    public SubmissionType getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(SubmissionType submissionType) {
        this.submissionType = submissionType;
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
        Assignment that = (Assignment) o;
        return Objects.equals(id, that.id) && Objects.equals(moduleId, that.moduleId) && Objects.equals(title, that.title) && Objects.equals(instructions, that.instructions) && Objects.equals(assignmentOrder, that.assignmentOrder) && Objects.equals(dueAt, that.dueAt) && Objects.equals(maxPoints, that.maxPoints) && submissionType == that.submissionType && status == that.status && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(publishedAt, that.publishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, moduleId, title, instructions, assignmentOrder, dueAt, maxPoints, submissionType, status, createdAt, updatedAt, publishedAt);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", moduleId=" + moduleId +
                ", title='" + title + '\'' +
                ", instructions='" + instructions + '\'' +
                ", assignmentOrder=" + assignmentOrder +
                ", dueAt=" + dueAt +
                ", maxPoints=" + maxPoints +
                ", submissionType=" + submissionType +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", publishedAt=" + publishedAt +
                '}';
    }
}