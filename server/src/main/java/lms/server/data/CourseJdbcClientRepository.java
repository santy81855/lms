package lms.server.data;

import lms.server.data.mappers.CourseMapper;
import lms.server.models.Course;
import lms.server.models.CourseStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CourseJdbcClientRepository implements CourseRepository {

    private final JdbcClient jdbcClient;

    public CourseJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Course> findById(Long id) {
        final String sql = """
                SELECT id, teacher_id, title, subject, grade_level, description,
                       status, join_code, created_at, updated_at
                FROM courses
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new CourseMapper())
                .optional();
    }

    @Override
    public Optional<Course> findByIdAndTeacherId(Long courseId, Long teacherId) {
        final String sql = """
                SELECT id, teacher_id, title, subject, grade_level, description,
                       status, join_code, created_at, updated_at
                FROM courses
                WHERE id = ?
                  AND teacher_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(courseId)
                .param(teacherId)
                .query(new CourseMapper())
                .optional();
    }

    @Override
    public Optional<Course> findByJoinCode(String joinCode) {
        final String sql = """
                SELECT id, teacher_id, title, subject, grade_level, description,
                       status, join_code, created_at, updated_at
                FROM courses
                WHERE join_code = ?;
                """;

        return jdbcClient.sql(sql)
                .param(joinCode)
                .query(new CourseMapper())
                .optional();
    }

    @Override
    public List<Course> findByTeacherId(Long teacherId) {
        final String sql = """
                SELECT id, teacher_id, title, subject, grade_level, description,
                       status, join_code, created_at, updated_at
                FROM courses
                WHERE teacher_id = ?
                ORDER BY created_at DESC;
                """;

        return jdbcClient.sql(sql)
                .param(teacherId)
                .query(new CourseMapper())
                .list();
    }

    @Override
    public Course add(Course course) {
        final String sql = """
                INSERT INTO courses (
                    teacher_id,
                    title,
                    subject,
                    grade_level,
                    description,
                    status,
                    join_code
                )
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(course.getTeacherId())
                .param(course.getTitle())
                .param(course.getSubject())
                .param(course.getGradeLevel().name())
                .param(course.getDescription())
                .param(course.getStatus().name())
                .param(course.getJoinCode())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Course insert failed.") {};
        }

        course.setId(keyHolder.getKey().longValue());

        return course;
    }

    @Override
    public boolean update(Course course) {
        final String sql = """
                UPDATE courses
                SET title = ?,
                    subject = ?,
                    grade_level = ?,
                    description = ?
                WHERE id = ?
                  AND teacher_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(course.getTitle())
                .param(course.getSubject())
                .param(course.getGradeLevel().name())
                .param(course.getDescription())
                .param(course.getId())
                .param(course.getTeacherId())
                .update() > 0;
    }

    @Override
    public boolean updateStatus(Long courseId, Long teacherId, CourseStatus status) {
        final String sql = """
                UPDATE courses
                SET status = ?
                WHERE id = ?
                  AND teacher_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(status.name())
                .param(courseId)
                .param(teacherId)
                .update() > 0;
    }

    @Override
    public boolean deleteByIdAndTeacherId(Long courseId, Long teacherId) {
        final String sql = """
                DELETE FROM courses
                WHERE id = ?
                  AND teacher_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(courseId)
                .param(teacherId)
                .update() > 0;
    }

    @Override
    public boolean existsByJoinCode(String joinCode) {
        final String sql = """
                SELECT COUNT(*)
                FROM courses
                WHERE join_code = ?;
                """;

        Integer count = jdbcClient.sql(sql)
                .param(joinCode)
                .query(Integer.class)
                .single();

        return count != null && count > 0;
    }
}