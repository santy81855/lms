package lms.server.data;

import lms.server.data.mappers.QuizSubmissionAnswerMapper;
import lms.server.models.QuizSubmissionAnswer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
                       short_answer_text, is_correct, points_earned,
                       is_graded, graded_at, graded_by, created_at
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

    @Override
    public boolean updateGrade(Long answerId,
                               Double points,
                               Boolean isCorrect,
                               Long gradedBy) {

        final String sql = """
                UPDATE quiz_submission_answers
                SET points_earned = ?,
                    is_correct = ?,
                    is_graded = TRUE,
                    graded_at = NOW(),
                    graded_by = ?
                WHERE id = ?;
                """;


        int safeCorrect = (isCorrect != null && isCorrect) ? 1 : 0;

        return jdbcClient.sql(sql)
                .param(points)
                .param(safeCorrect)
                .param(gradedBy)
                .param(answerId)
                .update() > 0;
    }

    @Override
    public List<QuizSubmissionAnswer> findUngradedShortAnswers(Long quizId) {

        final String sql = """
                    SELECT qsa.id,
                           qsa.quiz_submission_id,
                           qsa.question_id,
                           qsa.selected_option_id,
                           qsa.short_answer_text,
                           qsa.is_correct,
                           qsa.points_earned,
                           qsa.is_graded,
                           qsa.graded_at,
                           qsa.graded_by,
                           qsa.created_at,
                           CONCAT(u.first_name, ' ', u.last_name) AS student_name,
                           qq.question_text,
                           qq.points AS max_points
                    FROM quiz_submission_answers qsa
                    JOIN quiz_submissions qs ON qs.id = qsa.quiz_submission_id
                    JOIN users u ON u.id = qs.student_id
                    JOIN quiz_questions qq ON qq.id = qsa.question_id
                    WHERE qq.quiz_id = ?
                      AND qq.question_type = 'SHORT_ANSWER'
                      AND qsa.is_graded = FALSE
                    ORDER BY qsa.created_at;
                """;

        return jdbcClient.sql(sql)
                .param(quizId)
                .query(new QuizSubmissionAnswerMapper())
                .list();
    }

    @Override
    public Optional<QuizSubmissionAnswer> findById(Long id) {

        final String sql = """
                SELECT id,
                       quiz_submission_id,
                       question_id,
                       selected_option_id,
                       short_answer_text,
                       is_correct,
                       points_earned,
                       is_graded,
                       graded_at,
                       graded_by,
                       created_at
                FROM quiz_submission_answers
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new QuizSubmissionAnswerMapper())
                .optional();
    }
}