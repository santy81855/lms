package lms.server.data;

import lms.server.data.mappers.QuizMapper;
import lms.server.models.FeedbackType;
import lms.server.models.Quiz;
import lms.server.models.QuizFeedbackType;
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
    private final FeedbackTypeRepository feedbackTypeRepository;

    private static final String SELECT = """
            SELECT q.id, q.module_id, q.title, q.description, q.quiz_order,
                    q.max_points, q.time_limit_minutes, q.attempts_allowed,
                    q.status, q.feedback_type_id, ft.code AS feedback_type,
                    q.created_at, q.updated_at, q.published_at
            FROM quizzes q
            INNER JOIN feedback_type ft ON q.feedback_type_id = ft.id
            """;

    public QuizJdbcClientRepository(JdbcClient jdbcClient, FeedbackTypeRepository feedbackTypeRepository) {
        this.jdbcClient = jdbcClient;
        this.feedbackTypeRepository = feedbackTypeRepository;
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
                    feedback_type_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        Long feedbackTypeId = findFeedbackTypeId(quiz.getFeedbackTypeCodeOrDefault());

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
                .param(feedbackTypeId)
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Quiz insert failed.") {};
        }

        quiz.setId(keyHolder.getKey().longValue());
        quiz.setFeedbackTypeId(feedbackTypeId);

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
                    feedback_type_id = ?
                WHERE id = ?
                  AND module_id = ?;
                """;

        Long feedbackTypeId = findFeedbackTypeId(quiz.getFeedbackTypeCodeOrDefault());

        return jdbcClient.sql(sql)
                .param(quiz.getTitle())
                .param(quiz.getDescription())
                .param(quiz.getMaxPoints())
                .param(quiz.getTimeLimitMinutes())
                .param(quiz.getAttemptsAllowed())
                .param(feedbackTypeId)
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

    private Long findFeedbackTypeId(String feedbackTypeCode) {
        return feedbackTypeRepository.findByCode(feedbackTypeCode)
                .map(FeedbackType::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Feedback type is missing from the database: " + feedbackTypeCode
                ));
    }
}