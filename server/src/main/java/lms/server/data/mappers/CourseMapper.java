package lms.server.data.mappers;

import lms.server.models.Course;
import lms.server.models.CourseStatus;
import lms.server.models.GradeLevel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseMapper implements RowMapper<Course> {

    @Override
    public Course mapRow(ResultSet resultSet, int i) throws SQLException {
        Course course = new Course();

        course.setId(resultSet.getLong("id"));
        course.setTeacherId(resultSet.getLong("teacher_id"));
        course.setTitle(resultSet.getString("title"));
        course.setSubject(resultSet.getString("subject"));

        course.setGradeLevel(
                GradeLevel.valueOf(resultSet.getString("grade_level"))
        );

        course.setDescription(resultSet.getString("description"));

        course.setStatus(
                CourseStatus.valueOf(resultSet.getString("status"))
        );

        course.setJoinCode(resultSet.getString("join_code"));

        if (resultSet.getTimestamp("created_at") != null) {
            course.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            course.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        }

        return course;
    }
}