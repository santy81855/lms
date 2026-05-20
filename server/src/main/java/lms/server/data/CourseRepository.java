package lms.server.data;

import lms.server.models.Course;
import lms.server.models.CourseStatus;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findById(Long id);

    Optional<Course> findByIdAndTeacherId(Long courseId, Long teacherId);

    Optional<Course> findByJoinCode(String joinCode);

    List<Course> findByTeacherId(Long teacherId);

    Course add(Course course);

    boolean update(Course course);

    boolean updateStatus(Long courseId, Long teacherId, CourseStatus status);

    boolean deleteByIdAndTeacherId(Long courseId, Long teacherId);

    boolean existsByJoinCode(String joinCode);
}