package lms.server.data;

import lms.server.models.CourseEnrollment;
import lms.server.models.EnrollmentStatus;

import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository {

    Optional<CourseEnrollment> findById(Long id);

    Optional<CourseEnrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);

    List<CourseEnrollment> findByStudentId(Long studentId);

    List<CourseEnrollment> findByCourseId(Long courseId);

    CourseEnrollment add(CourseEnrollment enrollment);

    boolean updateStatus(Long courseId, Long studentId, EnrollmentStatus status);

    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);

    boolean deleteByCourseIdAndStudentId(Long courseId, Long studentId);
}