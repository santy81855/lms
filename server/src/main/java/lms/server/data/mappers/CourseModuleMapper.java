package lms.server.data.mappers;

import lms.server.models.CourseModule;
import lms.server.models.VisibilityStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseModuleMapper implements RowMapper<CourseModule> {

    @Override
    public CourseModule mapRow(ResultSet resultSet, int i) throws SQLException {
        CourseModule module = new CourseModule();

        module.setId(resultSet.getLong("id"));
        module.setCourseId(resultSet.getLong("course_id"));
        module.setTitle(resultSet.getString("title"));
        module.setDescription(resultSet.getString("description"));
        module.setModuleOrder(resultSet.getInt("module_order"));

        module.setStatus(
                VisibilityStatus.valueOf(resultSet.getString("status"))
        );

        if (resultSet.getTimestamp("created_at") != null) {
            module.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            module.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("published_at") != null) {
            module.setPublishedAt(resultSet.getTimestamp("published_at").toLocalDateTime());
        }

        return module;
    }
}