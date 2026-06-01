package lms.server.data;

import lms.server.data.mappers.QuizMapper;
import lms.server.models.Quiz;
import lms.server.models.VisibilityStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuizJdbcClientRepository implements QuizRepository {

    private final JdbcClient jdbcClient;

    private static final String SELECT = """
            SELECT id, module_id, title, description, quiz_order,
                   max_points, time_limit_minutes, attempts_allowed,
                   status, feedback_type, created_at, updated_at, published_at
            FROM quizzes       
            """;

    public QuizJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Quiz> findById(Long id) {
        final String sql = SELECT + """
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new QuizMapper())
                .optional();
    }

    @Override
    public Optional<Quiz> findByIdAndModuleId(Long quizId, Long moduleId) {
        final String sql = SELECT + """
                WHERE id = ?
                  AND module_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(quizId)
                .param(moduleId)
                .query(new QuizMapper())
                .optional();
    }

    @Override
    public List<Quiz> findByModuleId(Long moduleId) {
        final String sql = SELECT + """
                WHERE module_id = ?
                ORDER BY quiz_order;
                """;

        return jdbcClient.sql(sql)
                .param(moduleId)
                .query(new QuizMapper())
                .list();
    }

    @Override
    public Quiz add(Quiz quiz) {
        final String sql = """
                INSERT INTO quizzes (
                    module_id,
                    title,
                    description,
                    quiz_order,
                    max_points,
                    time_limit_minutes,
                    attempts_allowed,
                    status,
                    feedback_type
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(quiz.getModuleId())
                .param(quiz.getTitle())
                .param(quiz.getDescription())
                .param(quiz.getQuizOrder())
                .param(quiz.getMaxPoints())
                .param(quiz.getTimeLimitMinutes())
                .param(quiz.getAttemptsAllowed())
                .param(quiz.getStatus().name())
                .param(quiz.getFeedbackType().name())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Quiz insert failed.") {};
        }

        quiz.setId(keyHolder.getKey().longValue());

        return quiz;
    }

    @Override
    public boolean update(Quiz quiz) {
        final String sql = """
                UPDATE quizzes
                SET title = ?,
                    description = ?,
                    max_points = ?,
                    time_limit_minutes = ?,
                    attempts_allowed = ?
                WHERE id = ?
                  AND module_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(quiz.getTitle())
                .param(quiz.getDescription())
                .param(quiz.getMaxPoints())
                .param(quiz.getTimeLimitMinutes())
                .param(quiz.getAttemptsAllowed())
                .param(quiz.getId())
                .param(quiz.getModuleId())
                .update() > 0;
    }

    @Override
    public boolean updateOrder(Long quizId, Integer quizOrder) {
        final String sql = """
                UPDATE quizzes
                SET quiz_order = ?
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(quizOrder)
                .param(quizId)
                .update() > 0;
    }

    @Override
    public boolean updateStatus(Long quizId, VisibilityStatus status) {
        final String sql = """
                UPDATE quizzes
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
                .param(quizId)
                .update() > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = """
                DELETE FROM quizzes
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .update() > 0;
    }

    @Override
    public boolean deleteByIdAndModuleId(Long quizId, Long moduleId) {
        final String sql = """
                DELETE FROM quizzes
                WHERE id = ?
                  AND module_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(quizId)
                .param(moduleId)
                .update() > 0;
    }
}