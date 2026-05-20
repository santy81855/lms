package lms.server.data;

import lms.server.data.mappers.AssignmentMapper;
import lms.server.models.Assignment;
import lms.server.models.VisibilityStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AssignmentJdbcClientRepository implements AssignmentRepository {

    private final JdbcClient jdbcClient;

    public AssignmentJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Assignment> findById(Long id) {
        final String sql = """
                SELECT id, module_id, title, instructions, assignment_order,
                       due_at, max_points, submission_type, status,
                       created_at, updated_at, published_at
                FROM assignments
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new AssignmentMapper())
                .optional();
    }

    @Override
    public Optional<Assignment> findByIdAndModuleId(Long assignmentId, Long moduleId) {
        final String sql = """
                SELECT id, module_id, title, instructions, assignment_order,
                       due_at, max_points, submission_type, status,
                       created_at, updated_at, published_at
                FROM assignments
                WHERE id = ?
                  AND module_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(assignmentId)
                .param(moduleId)
                .query(new AssignmentMapper())
                .optional();
    }

    @Override
    public List<Assignment> findByModuleId(Long moduleId) {
        final String sql = """
                SELECT id, module_id, title, instructions, assignment_order,
                       due_at, max_points, submission_type, status,
                       created_at, updated_at, published_at
                FROM assignments
                WHERE module_id = ?
                ORDER BY assignment_order;
                """;

        return jdbcClient.sql(sql)
                .param(moduleId)
                .query(new AssignmentMapper())
                .list();
    }

    @Override
    public Assignment add(Assignment assignment) {
        final String sql = """
                INSERT INTO assignments (
                    module_id,
                    title,
                    instructions,
                    assignment_order,
                    due_at,
                    max_points,
                    submission_type,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(assignment.getModuleId())
                .param(assignment.getTitle())
                .param(assignment.getInstructions())
                .param(assignment.getAssignmentOrder())
                .param(assignment.getDueAt())
                .param(assignment.getMaxPoints())
                .param(assignment.getSubmissionType().name())
                .param(assignment.getStatus().name())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Assignment insert failed.") {};
        }

        assignment.setId(keyHolder.getKey().longValue());

        return assignment;
    }

    @Override
    public boolean update(Assignment assignment) {
        final String sql = """
                UPDATE assignments
                SET title = ?,
                    instructions = ?,
                    due_at = ?,
                    max_points = ?,
                    submission_type = ?
                WHERE id = ?
                  AND module_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(assignment.getTitle())
                .param(assignment.getInstructions())
                .param(assignment.getDueAt())
                .param(assignment.getMaxPoints())
                .param(assignment.getSubmissionType().name())
                .param(assignment.getId())
                .param(assignment.getModuleId())
                .update() > 0;
    }

    @Override
    public boolean updateOrder(Long assignmentId, Integer assignmentOrder) {
        final String sql = """
                UPDATE assignments
                SET assignment_order = ?
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(assignmentOrder)
                .param(assignmentId)
                .update() > 0;
    }

    @Override
    public boolean updateStatus(Long assignmentId, VisibilityStatus status) {
        final String sql = """
                UPDATE assignments
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
                .param(assignmentId)
                .update() > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = """
                DELETE FROM assignments
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .update() > 0;
    }

    @Override
    public boolean deleteByIdAndModuleId(Long assignmentId, Long moduleId) {
        final String sql = """
                DELETE FROM assignments
                WHERE id = ?
                  AND module_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(assignmentId)
                .param(moduleId)
                .update() > 0;
    }
}