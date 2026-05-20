package lms.server.data;

import lms.server.data.mappers.CourseEnrollmentMapper;
import lms.server.models.CourseEnrollment;
import lms.server.models.EnrollmentStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CourseEnrollmentJdbcClientRepository implements CourseEnrollmentRepository {

    private final JdbcClient jdbcClient;

    public CourseEnrollmentJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<CourseEnrollment> findById(Long id) {
        final String sql = """
                SELECT id, course_id, student_id, enrollment_status, enrolled_at, completed_at
                FROM course_enrollments
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new CourseEnrollmentMapper())
                .optional();
    }

    @Override
    public Optional<CourseEnrollment> findByCourseIdAndStudentId(Long courseId, Long studentId) {
        final String sql = """
                SELECT id, course_id, student_id, enrollment_status, enrolled_at, completed_at
                FROM course_enrollments
                WHERE course_id = ?
                  AND student_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(courseId)
                .param(studentId)
                .query(new CourseEnrollmentMapper())
                .optional();
    }

    @Override
    public List<CourseEnrollment> findByStudentId(Long studentId) {
        final String sql = """
                SELECT id, course_id, student_id, enrollment_status, enrolled_at, completed_at
                FROM course_enrollments
                WHERE student_id = ?
                ORDER BY enrolled_at DESC;
                """;

        return jdbcClient.sql(sql)
                .param(studentId)
                .query(new CourseEnrollmentMapper())
                .list();
    }

    @Override
    public List<CourseEnrollment> findByCourseId(Long courseId) {
        final String sql = """
                SELECT id, course_id, student_id, enrollment_status, enrolled_at, completed_at
                FROM course_enrollments
                WHERE course_id = ?
                ORDER BY enrolled_at DESC;
                """;

        return jdbcClient.sql(sql)
                .param(courseId)
                .query(new CourseEnrollmentMapper())
                .list();
    }

    @Override
    public CourseEnrollment add(CourseEnrollment enrollment) {
        final String sql = """
                INSERT INTO course_enrollments (
                    course_id,
                    student_id,
                    enrollment_status
                )
                VALUES (?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(enrollment.getCourseId())
                .param(enrollment.getStudentId())
                .param(enrollment.getEnrollmentStatus().name())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Course enrollment insert failed.") {};
        }

        enrollment.setId(keyHolder.getKey().longValue());

        return enrollment;
    }

    @Override
    public boolean updateStatus(Long courseId, Long studentId, EnrollmentStatus status) {
        final String sql = """
                UPDATE course_enrollments
                SET enrollment_status = ?,
                    completed_at = CASE
                        WHEN ? = 'COMPLETED' THEN CURRENT_TIMESTAMP
                        ELSE completed_at
                    END
                WHERE course_id = ?
                  AND student_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(status.name())
                .param(status.name())
                .param(courseId)
                .param(studentId)
                .update() > 0;
    }

    @Override
    public boolean existsByCourseIdAndStudentId(Long courseId, Long studentId) {
        final String sql = """
                SELECT COUNT(*)
                FROM course_enrollments
                WHERE course_id = ?
                  AND student_id = ?;
                """;

        Integer count = jdbcClient.sql(sql)
                .param(courseId)
                .param(studentId)
                .query(Integer.class)
                .single();

        return count != null && count > 0;
    }

    @Override
    public boolean deleteByCourseIdAndStudentId(Long courseId, Long studentId) {
        final String sql = """
                DELETE FROM course_enrollments
                WHERE course_id = ?
                  AND student_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(courseId)
                .param(studentId)
                .update() > 0;
    }
}