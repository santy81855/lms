package lms.server.data.mappers;

import lms.server.models.Assignment;
import lms.server.models.SubmissionType;
import lms.server.models.VisibilityStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AssignmentMapper implements RowMapper<Assignment> {

    @Override
    public Assignment mapRow(ResultSet resultSet, int i) throws SQLException {
        Assignment assignment = new Assignment();

        assignment.setId(resultSet.getLong("id"));
        assignment.setModuleId(resultSet.getLong("module_id"));
        assignment.setTitle(resultSet.getString("title"));
        assignment.setInstructions(resultSet.getString("instructions"));
        assignment.setAssignmentOrder(resultSet.getInt("assignment_order"));

        if (resultSet.getTimestamp("due_at") != null) {
            assignment.setDueAt(resultSet.getTimestamp("due_at").toLocalDateTime());
        }

        assignment.setMaxPoints(resultSet.getBigDecimal("max_points"));

        assignment.setSubmissionType(
                SubmissionType.valueOf(resultSet.getString("submission_type"))
        );

        assignment.setStatus(
                VisibilityStatus.valueOf(resultSet.getString("status"))
        );

        if (resultSet.getTimestamp("created_at") != null) {
            assignment.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            assignment.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("published_at") != null) {
            assignment.setPublishedAt(resultSet.getTimestamp("published_at").toLocalDateTime());
        }

        return assignment;
    }
}