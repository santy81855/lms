package lms.server.data;

import lms.server.data.mappers.CourseModuleMapper;
import lms.server.models.CourseModule;
import lms.server.models.VisibilityStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CourseModuleJdbcClientRepository implements CourseModuleRepository {

    private final JdbcClient jdbcClient;

    public CourseModuleJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<CourseModule> findById(Long id) {
        final String sql = """
                SELECT id, course_id, title, description, module_order,
                       status, created_at, updated_at, published_at
                FROM course_modules
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new CourseModuleMapper())
                .optional();
    }

    @Override
    public Optional<CourseModule> findByIdAndCourseId(Long moduleId, Long courseId) {
        final String sql = """
                SELECT id, course_id, title, description, module_order,
                       status, created_at, updated_at, published_at
                FROM course_modules
                WHERE id = ?
                  AND course_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(moduleId)
                .param(courseId)
                .query(new CourseModuleMapper())
                .optional();
    }

    @Override
    public List<CourseModule> findByCourseId(Long courseId) {
        final String sql = """
                SELECT id, course_id, title, description, module_order,
                       status, created_at, updated_at, published_at
                FROM course_modules
                WHERE course_id = ?
                ORDER BY module_order;
                """;

        return jdbcClient.sql(sql)
                .param(courseId)
                .query(new CourseModuleMapper())
                .list();
    }

    @Override
    public CourseModule add(CourseModule module) {
        final String sql = """
                INSERT INTO course_modules (
                    course_id,
                    title,
                    description,
                    module_order,
                    status
                )
                VALUES (?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(module.getCourseId())
                .param(module.getTitle())
                .param(module.getDescription())
                .param(module.getModuleOrder())
                .param(module.getStatus().name())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Course module insert failed.") {};
        }

        module.setId(keyHolder.getKey().longValue());

        return module;
    }

    @Override
    public boolean update(CourseModule module) {
        final String sql = """
                UPDATE course_modules
                SET title = ?,
                    description = ?,
                    module_order = ?
                WHERE id = ?
                  AND course_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(module.getTitle())
                .param(module.getDescription())
                .param(module.getModuleOrder())
                .param(module.getId())
                .param(module.getCourseId())
                .update() > 0;
    }

    @Override
    public boolean updateStatus(Long moduleId, VisibilityStatus status) {
        final String sql = """
                UPDATE course_modules
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
                .param(moduleId)
                .update() > 0;
    }

    @Override
    public boolean updateOrder(Long moduleId, Integer moduleOrder) {
        final String sql = """
                UPDATE course_modules
                SET module_order = ?
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(moduleOrder)
                .param(moduleId)
                .update() > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = """
                DELETE FROM course_modules
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .update() > 0;
    }

    @Override
    public boolean deleteByIdAndCourseId(Long moduleId, Long courseId) {
        final String sql = """
                DELETE FROM course_modules
                WHERE id = ?
                  AND course_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(moduleId)
                .param(courseId)
                .update() > 0;
    }
}