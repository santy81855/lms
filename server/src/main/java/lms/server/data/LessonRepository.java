package lms.server.data;

import lms.server.models.Lesson;
import lms.server.models.VisibilityStatus;

import java.util.List;
import java.util.Optional;

public interface LessonRepository {
    Optional<Lesson> findById(Long id);

    Optional<Lesson> findByIdAndModuleId(Long lessonId, Long moduleId);

    List<Lesson> findByModuleId(Long moduleId);

    Lesson add(Lesson lesson);

    boolean update(Lesson lesson);

    boolean updateOrder(Long lessonId, Integer lessonOrder);

    boolean updateStatus(Long lessonId, VisibilityStatus status);

    boolean deleteById(Long id);

    boolean deleteByIdAndModuleId(Long lessonId, Long moduleId);

    List<Lesson> findPublishedByCourseId(Long courseId);
}