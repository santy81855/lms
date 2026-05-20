package lms.server.data;

import lms.server.data.mappers.QuizQuestionMapper;
import lms.server.models.QuizQuestion;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuizQuestionJdbcClientRepository implements QuizQuestionRepository {

    private final JdbcClient jdbcClient;

    public QuizQuestionJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<QuizQuestion> findById(Long id) {
        final String sql = """
                SELECT id, quiz_id, question_text, question_type,
                       question_order, points, explanation,
                       created_at, updated_at
                FROM quiz_questions
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new QuizQuestionMapper())
                .optional();
    }

    @Override
    public Optional<QuizQuestion> findByIdAndQuizId(Long questionId, Long quizId) {
        final String sql = """
                SELECT id, quiz_id, question_text, question_type,
                       question_order, points, explanation,
                       created_at, updated_at
                FROM quiz_questions
                WHERE id = ?
                  AND quiz_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(questionId)
                .param(quizId)
                .query(new QuizQuestionMapper())
                .optional();
    }

    @Override
    public List<QuizQuestion> findByQuizId(Long quizId) {
        final String sql = """
                SELECT id, quiz_id, question_text, question_type,
                       question_order, points, explanation,
                       created_at, updated_at
                FROM quiz_questions
                WHERE quiz_id = ?
                ORDER BY question_order;
                """;

        return jdbcClient.sql(sql)
                .param(quizId)
                .query(new QuizQuestionMapper())
                .list();
    }

    @Override
    public QuizQuestion add(QuizQuestion question) {
        final String sql = """
                INSERT INTO quiz_questions (
                    quiz_id,
                    question_text,
                    question_type,
                    question_order,
                    points,
                    explanation
                )
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(question.getQuizId())
                .param(question.getQuestionText())
                .param(question.getQuestionType().name())
                .param(question.getQuestionOrder())
                .param(question.getPoints())
                .param(question.getExplanation())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Quiz question insert failed.") {};
        }

        question.setId(keyHolder.getKey().longValue());

        return question;
    }

    @Override
    public boolean update(QuizQuestion question) {
        final String sql = """
                UPDATE quiz_questions
                SET question_text = ?,
                    question_type = ?,
                    points = ?,
                    explanation = ?
                WHERE id = ?
                  AND quiz_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(question.getQuestionText())
                .param(question.getQuestionType().name())
                .param(question.getPoints())
                .param(question.getExplanation())
                .param(question.getId())
                .param(question.getQuizId())
                .update() > 0;
    }

    @Override
    public boolean updateOrder(Long questionId, Integer questionOrder) {
        final String sql = """
                UPDATE quiz_questions
                SET question_order = ?
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(questionOrder)
                .param(questionId)
                .update() > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = """
                DELETE FROM quiz_questions
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .update() > 0;
    }

    @Override
    public boolean deleteByIdAndQuizId(Long questionId, Long quizId) {
        final String sql = """
                DELETE FROM quiz_questions
                WHERE id = ?
                  AND quiz_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(questionId)
                .param(quizId)
                .update() > 0;
    }
}