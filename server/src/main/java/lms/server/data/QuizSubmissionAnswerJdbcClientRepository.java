package lms.server.data;

import lms.server.data.mappers.QuizSubmissionAnswerMapper;
import lms.server.models.QuizSubmissionAnswer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuizSubmissionAnswerJdbcClientRepository implements QuizSubmissionAnswerRepository {

    private final JdbcClient jdbcClient;

    public QuizSubmissionAnswerJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<QuizSubmissionAnswer> findBySubmissionId(Long submissionId) {
        final String sql = """
                SELECT id, quiz_submission_id, question_id, selected_option_id,
                       short_answer_text, is_correct, points_earned, created_at
                FROM quiz_submission_answers
                WHERE quiz_submission_id = ?
                ORDER BY id;
                """;

        return jdbcClient.sql(sql)
                .param(submissionId)
                .query(new QuizSubmissionAnswerMapper())
                .list();
    }

    @Override
    public QuizSubmissionAnswer add(QuizSubmissionAnswer answer) {
        final String sql = """
                INSERT INTO quiz_submission_answers (
                    quiz_submission_id,
                    question_id,
                    selected_option_id,
                    short_answer_text,
                    is_correct,
                    points_earned
                )
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(answer.getQuizSubmissionId())
                .param(answer.getQuestionId())
                .param(answer.getSelectedOptionId())
                .param(answer.getShortAnswerText())
                .param(answer.getCorrect())
                .param(answer.getPointsEarned())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Quiz submission answer insert failed.") {};
        }

        answer.setId(keyHolder.getKey().longValue());

        return answer;
    }
}