package lms.server.data.mappers;

import lms.server.models.CourseEnrollment;
import lms.server.models.EnrollmentStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseEnrollmentMapper implements RowMapper<CourseEnrollment> {

    @Override
    public CourseEnrollment mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        CourseEnrollment enrollment = new CourseEnrollment();

        enrollment.setId(resultSet.getLong("id"));
        enrollment.setCourseId(resultSet.getLong("course_id"));
        enrollment.setStudentId(resultSet.getLong("student_id"));

        enrollment.setEnrollmentStatus(
                EnrollmentStatus.valueOf(resultSet.getString("enrollment_status"))
        );

        if (resultSet.getTimestamp("enrolled_at") != null) {
            enrollment.setEnrolledAt(resultSet.getTimestamp("enrolled_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("completed_at") != null) {
            enrollment.setCompletedAt(resultSet.getTimestamp("completed_at").toLocalDateTime());
        }

        return enrollment;
    }
}