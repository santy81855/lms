package lms.server.data;

import lms.server.data.mappers.QuizAnswerOptionMapper;
import lms.server.models.QuizAnswerOption;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuizAnswerOptionJdbcClientRepository implements QuizAnswerOptionRepository {

    private final JdbcClient jdbcClient;

    public QuizAnswerOptionJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<QuizAnswerOption> findById(Long id) {
        final String sql = """
                SELECT id, question_id, option_text, option_order,
                       is_correct, created_at, updated_at
                FROM quiz_answer_options
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new QuizAnswerOptionMapper())
                .optional();
    }

    @Override
    public Optional<QuizAnswerOption> findByIdAndQuestionId(Long optionId, Long questionId) {
        final String sql = """
                SELECT id, question_id, option_text, option_order,
                       is_correct, created_at, updated_at
                FROM quiz_answer_options
                WHERE id = ?
                  AND question_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(optionId)
                .param(questionId)
                .query(new QuizAnswerOptionMapper())
                .optional();
    }

    @Override
    public List<QuizAnswerOption> findByQuestionId(Long questionId) {
        final String sql = """
                SELECT id, question_id, option_text, option_order,
                       is_correct, created_at, updated_at
                FROM quiz_answer_options
                WHERE question_id = ?
                ORDER BY option_order;
                """;

        return jdbcClient.sql(sql)
                .param(questionId)
                .query(new QuizAnswerOptionMapper())
                .list();
    }

    @Override
    public QuizAnswerOption add(QuizAnswerOption option) {
        final String sql = """
                INSERT INTO quiz_answer_options (
                    question_id,
                    option_text,
                    option_order,
                    is_correct
                )
                VALUES (?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(option.getQuestionId())
                .param(option.getOptionText())
                .param(option.getOptionOrder())
                .param(option.getCorrect())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Quiz answer option insert failed.") {};
        }

        option.setId(keyHolder.getKey().longValue());

        return option;
    }

    @Override
    public boolean update(QuizAnswerOption option) {
        final String sql = """
                UPDATE quiz_answer_options
                SET option_text = ?,
                    is_correct = ?
                WHERE id = ?
                  AND question_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(option.getOptionText())
                .param(option.getCorrect())
                .param(option.getId())
                .param(option.getQuestionId())
                .update() > 0;
    }

    @Override
    public boolean updateOrder(Long optionId, Integer optionOrder) {
        final String sql = """
                UPDATE quiz_answer_options
                SET option_order = ?
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(optionOrder)
                .param(optionId)
                .update() > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = """
                DELETE FROM quiz_answer_options
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .update() > 0;
    }

    @Override
    public boolean deleteByIdAndQuestionId(Long optionId, Long questionId) {
        final String sql = """
                DELETE FROM quiz_answer_options
                WHERE id = ?
                  AND question_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(optionId)
                .param(questionId)
                .update() > 0;
    }
}