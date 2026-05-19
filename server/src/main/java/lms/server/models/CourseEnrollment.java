package lms.server.models;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

public class CourseEnrollment {

    private Long id;

    @NotNull(message = "Course id is required.")
    private Long courseId;

    @NotNull(message = "Student id is required.")
    private Long studentId;

    @NotNull(message = "Enrollment status is required.")
    private EnrollmentStatus enrollmentStatus;

    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;

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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public EnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CourseEnrollment that = (CourseEnrollment) o;
        return Objects.equals(id, that.id)
                && Objects.equals(courseId, that.courseId)
                && Objects.equals(studentId, that.studentId)
                && enrollmentStatus == that.enrollmentStatus
                && Objects.equals(enrolledAt, that.enrolledAt)
                && Objects.equals(completedAt, that.completedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, studentId, enrollmentStatus, enrolledAt, completedAt);
    }

    @Override
    public String toString() {
        return "CourseEnrollment{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", studentId=" + studentId +
                ", enrollmentStatus=" + enrollmentStatus +
                ", enrolledAt=" + enrolledAt +
                ", completedAt=" + completedAt +
                '}';
    }
}