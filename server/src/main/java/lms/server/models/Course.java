package lms.server.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

public class Course {
    private Long id;

    @NotNull(message = "Teacher id is required.")
    private Long teacherId;

    @NotBlank(message = "Title is required.")
    @Size(max = 255, message = "Title must be 255 characters or fewer.")
    private String title;

    @Size(max = 100, message = "Subject must be 100 characters or fewer.")
    private String subject;

    @NotNull(message = "Grade level is required.")
    private GradeLevel gradeLevel;

    private String description;

    @NotNull(message = "Course status is required.")
    private CourseStatus status;

    @NotBlank(message = "Join code is required.")
    @Size(max = 20, message = "Join code must be 20 characters or fewer.")
    private String joinCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
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
        Course course = (Course) o;
        return Objects.equals(id, course.id) && Objects.equals(teacherId, course.teacherId) && Objects.equals(title, course.title) && Objects.equals(subject, course.subject) && gradeLevel == course.gradeLevel && Objects.equals(description, course.description) && status == course.status && Objects.equals(joinCode, course.joinCode) && Objects.equals(createdAt, course.createdAt) && Objects.equals(updatedAt, course.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teacherId, title, subject, gradeLevel, description, status, joinCode, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", teacherId=" + teacherId +
                ", title='" + title + '\'' +
                ", subject='" + subject + '\'' +
                ", gradeLevel=" + gradeLevel +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", joinCode='" + joinCode + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}