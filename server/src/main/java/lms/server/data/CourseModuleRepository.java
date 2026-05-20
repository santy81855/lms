package lms.server.data;

import lms.server.models.CourseModule;
import lms.server.models.VisibilityStatus;

import java.util.List;
import java.util.Optional;

public interface CourseModuleRepository {
    Optional<CourseModule> findById(Long id);

    Optional<CourseModule> findByIdAndCourseId(Long moduleId, Long courseId);

    List<CourseModule> findByCourseId(Long courseId);

    CourseModule add(CourseModule module);

    boolean update(CourseModule module);

    boolean updateStatus(Long moduleId, VisibilityStatus status);

    boolean updateOrder(Long moduleId, Integer moduleOrder);

    boolean deleteById(Long id);

    boolean deleteByIdAndCourseId(Long moduleId, Long courseId);
}