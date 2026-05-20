package lms.server.data;

import lms.server.data.mappers.LessonMapper;
import lms.server.models.Lesson;
import lms.server.models.VisibilityStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LessonJdbcClientRepository implements LessonRepository {

    private final JdbcClient jdbcClient;

    public LessonJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Lesson> findById(Long id) {
        final String sql = """
                SELECT id, module_id, title, content, lesson_order,
                       estimated_minutes, status, created_at, updated_at, published_at
                FROM lessons
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new LessonMapper())
                .optional();
    }

    @Override
    public Optional<Lesson> findByIdAndModuleId(Long lessonId, Long moduleId) {
        final String sql = """
                SELECT id, module_id, title, content, lesson_order,
                       estimated_minutes, status, created_at, updated_at, published_at
                FROM lessons
                WHERE id = ?
                  AND module_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(lessonId)
                .param(moduleId)
                .query(new LessonMapper())
                .optional();
    }

    @Override
    public List<Lesson> findByModuleId(Long moduleId) {
        final String sql = """
                SELECT id, module_id, title, content, lesson_order,
                       estimated_minutes, status, created_at, updated_at, published_at
                FROM lessons
                WHERE module_id = ?
                ORDER BY lesson_order;
                """;

        return jdbcClient.sql(sql)
                .param(moduleId)
                .query(new LessonMapper())
                .list();
    }

    @Override
    public Lesson add(Lesson lesson) {
        final String sql = """
                INSERT INTO lessons (
                    module_id,
                    title,
                    content,
                    lesson_order,
                    estimated_minutes,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(lesson.getModuleId())
                .param(lesson.getTitle())
                .param(lesson.getContent())
                .param(lesson.getLessonOrder())
                .param(lesson.getEstimatedMinutes())
                .param(lesson.getStatus().name())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Lesson insert failed.") {};
        }

        lesson.setId(keyHolder.getKey().longValue());

        return lesson;
    }

    @Override
    public boolean update(Lesson lesson) {
        final String sql = """
                UPDATE lessons
                SET title = ?,
                    content = ?,
                    estimated_minutes = ?
                WHERE id = ?
                  AND module_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(lesson.getTitle())
                .param(lesson.getContent())
                .param(lesson.getEstimatedMinutes())
                .param(lesson.getId())
                .param(lesson.getModuleId())
                .update() > 0;
    }

    @Override
    public boolean updateOrder(Long lessonId, Integer lessonOrder) {
        final String sql = """
                UPDATE lessons
                SET lesson_order = ?
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(lessonOrder)
                .param(lessonId)
                .update() > 0;
    }

    @Override
    public boolean updateStatus(Long lessonId, VisibilityStatus status) {
        final String sql = """
                UPDATE lessons
                SET status = ?,
                    published_at = CASE
                        WHEN ? = 'PUBLISHED' THEN CURRENT_TIMESTAMP
                        ELSE published_at
                    END
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(status.name())
                .param(status.name())
                .param(lessonId)
                .update() > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = """
                DELETE FROM lessons
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .update() > 0;
    }

    @Override
    public boolean deleteByIdAndModuleId(Long lessonId, Long moduleId) {
        final String sql = """
                DELETE FROM lessons
                WHERE id = ?
                  AND module_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(lessonId)
                .param(moduleId)
                .update() > 0;
    }
}