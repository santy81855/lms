package lms.server.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lms.server.data.CourseEnrollmentRepository;
import lms.server.data.CourseRepository;
import lms.server.models.Course;
import lms.server.models.CourseEnrollment;
import lms.server.models.CourseStatus;
import lms.server.models.EnrollmentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentCourseService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final Validator validator;

    public StudentCourseService(CourseRepository courseRepository,
                                CourseEnrollmentRepository courseEnrollmentRepository,
                                Validator validator) {
        this.courseRepository = courseRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.validator = validator;
    }

    public List<CourseEnrollment> findEnrollmentsByStudentId(Long studentId) {
        if (studentId == null) {
            return List.of();
        }

        return courseEnrollmentRepository.findByStudentId(studentId);
    }

    public List<Course> findCoursesByStudentId(Long studentId) {
        if (studentId == null) {
            return List.of();
        }

        return courseEnrollmentRepository.findByStudentId(studentId).stream()
                .filter(enrollment -> enrollment.getEnrollmentStatus() == EnrollmentStatus.ACTIVE)
                .map(CourseEnrollment::getCourseId)
                .map(courseRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Transactional
    public Result<CourseEnrollment> joinCourseByJoinCode(String joinCode, Long studentId) {
        Result<CourseEnrollment> result = new Result<>();

        if (studentId == null) {
            result.addMessage("Student id is required.", ResultType.INVALID);
            return result;
        }

        if (joinCode == null || joinCode.isBlank()) {
            result.addMessage("Join code is required.", ResultType.INVALID);
            return result;
        }

        String normalizedJoinCode = joinCode.trim().toUpperCase();

        Optional<Course> course = courseRepository.findByJoinCode(normalizedJoinCode);

        if (course.isEmpty()) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        if (course.get().getStatus() != CourseStatus.ACTIVE) {
            result.addMessage("Course is not open for enrollment.", ResultType.INVALID);
            return result;
        }

        Optional<CourseEnrollment> existingEnrollment =
                courseEnrollmentRepository.findByCourseIdAndStudentId(course.get().getId(), studentId);

        if (existingEnrollment.isPresent()) {
            CourseEnrollment enrollment = existingEnrollment.get();

            if (enrollment.getEnrollmentStatus() == EnrollmentStatus.ACTIVE) {
                result.addMessage("Student is already enrolled in this course.", ResultType.INVALID);
                return result;
            }

            if (!courseEnrollmentRepository.updateStatus(
                    course.get().getId(),
                    studentId,
                    EnrollmentStatus.ACTIVE
            )) {
                result.addMessage("Could not reactivate enrollment.", ResultType.INVALID);
                return result;
            }

            CourseEnrollment reactivatedEnrollment = courseEnrollmentRepository
                    .findByCourseIdAndStudentId(course.get().getId(), studentId)
                    .orElse(enrollment);

            result.setPayload(reactivatedEnrollment);
            return result;
        }

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setCourseId(course.get().getId());
        enrollment.setStudentId(studentId);
        enrollment.setEnrollmentStatus(EnrollmentStatus.ACTIVE);

        validate(enrollment, result);

        if (!result.isSuccess()) {
            return result;
        }

        result.setPayload(courseEnrollmentRepository.add(enrollment));
        return result;
    }

    @Transactional
    public Result<Void> dropCourse(Long courseId, Long studentId) {
        Result<Void> result = new Result<>();

        if (!requireId(courseId, "Course id is required.", result)
                || !requireId(studentId, "Student id is required.", result)) {
            return result;
        }

        Optional<CourseEnrollment> enrollment =
                courseEnrollmentRepository.findByCourseIdAndStudentId(courseId, studentId);

        if (enrollment.isEmpty()) {
            result.addMessage("Enrollment not found.", ResultType.NOT_FOUND);
            return result;
        }

        if (!courseEnrollmentRepository.updateStatus(courseId, studentId, EnrollmentStatus.DROPPED)) {
            result.addMessage("Enrollment not found.", ResultType.NOT_FOUND);
        }

        return result;
    }

    @Transactional
    public Result<Void> completeCourse(Long courseId, Long studentId) {
        Result<Void> result = new Result<>();

        if (!requireId(courseId, "Course id is required.", result)
                || !requireId(studentId, "Student id is required.", result)) {
            return result;
        }

        Optional<CourseEnrollment> enrollment =
                courseEnrollmentRepository.findByCourseIdAndStudentId(courseId, studentId);

        if (enrollment.isEmpty()) {
            result.addMessage("Enrollment not found.", ResultType.NOT_FOUND);
            return result;
        }

        if (!courseEnrollmentRepository.updateStatus(courseId, studentId, EnrollmentStatus.COMPLETED)) {
            result.addMessage("Enrollment not found.", ResultType.NOT_FOUND);
        }

        return result;
    }

    public boolean studentIsEnrolledInCourse(Long courseId, Long studentId) {
        if (courseId == null || studentId == null) {
            return false;
        }

        Optional<CourseEnrollment> enrollment =
                courseEnrollmentRepository.findByCourseIdAndStudentId(courseId, studentId);

        return enrollment.isPresent()
                && enrollment.get().getEnrollmentStatus() == EnrollmentStatus.ACTIVE;
    }

    private void validate(CourseEnrollment enrollment, Result<CourseEnrollment> result) {
        Set<ConstraintViolation<CourseEnrollment>> violations = validator.validate(enrollment);

        for (ConstraintViolation<CourseEnrollment> violation : violations) {
            result.addMessage(violation.getMessage(), ResultType.INVALID);
        }
    }

    private boolean requireId(Long id, String message, Result<?> result) {
        if (id == null) {
            result.addMessage(message, ResultType.INVALID);
            return false;
        }

        return true;
    }
}