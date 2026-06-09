package lms.server.data;

import lms.server.data.mappers.QuizSubmissionMapper;
import lms.server.models.QuizSubmission;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuizSubmissionJdbcClientRepository implements QuizSubmissionRepository {

    private static final String SELECT = """
            SELECT id, quiz_id, student_id, attempt_number, status,
                   score, max_score, started_at, submitted_at, graded_at
            FROM quiz_submissions
            """;

    private final JdbcClient jdbcClient;

    public QuizSubmissionJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<QuizSubmission> findById(Long id) {
        final String sql = SELECT + """
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new QuizSubmissionMapper())
                .optional();
    }

    @Override
    public List<QuizSubmission> findByQuizIdAndStudentId(Long quizId, Long studentId) {
        final String sql = SELECT + """
                WHERE quiz_id = ?
                  AND student_id = ?
                ORDER BY attempt_number;
                """;

        return jdbcClient.sql(sql)
                .param(quizId)
                .param(studentId)
                .query(new QuizSubmissionMapper())
                .list();
    }

    @Override
    public Optional<QuizSubmission> findLatestByQuizIdAndStudentId(Long quizId, Long studentId) {
        final String sql = SELECT + """
                WHERE quiz_id = ?
                  AND student_id = ?
                ORDER BY attempt_number DESC
                LIMIT 1;
                """;

        return jdbcClient.sql(sql)
                .param(quizId)
                .param(studentId)
                .query(new QuizSubmissionMapper())
                .optional();
    }

    @Override
    public int countByQuizIdAndStudentId(Long quizId, Long studentId) {
        final String sql = """
                SELECT COUNT(*)
                FROM quiz_submissions
                WHERE quiz_id = ?
                  AND student_id = ?;
                """;

        Integer count = jdbcClient.sql(sql)
                .param(quizId)
                .param(studentId)
                .query(Integer.class)
                .single();

        return count == null ? 0 : count;
    }

    @Override
    public QuizSubmission add(QuizSubmission submission) {
        final String sql = """
                INSERT INTO quiz_submissions (
                    quiz_id,
                    student_id,
                    attempt_number,
                    status,
                    score,
                    max_score,
                    started_at,
                    graded_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(submission.getQuizId())
                .param(submission.getStudentId())
                .param(submission.getAttemptNumber())
                .param(submission.getStatus().name())
                .param(submission.getScore())
                .param(submission.getMaxScore())
                .param(submission.getStartedAt())
                .param(submission.getGradedAt())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Quiz submission insert failed.") {};
        }

        submission.setId(keyHolder.getKey().longValue());

        return submission;
    }

    @Override
    public List<QuizSubmission> findByQuizId(Long quizId) {
        final String sql = SELECT + """
            WHERE quiz_id = ?
            ORDER BY student_id, attempt_number;
            """;

        return jdbcClient.sql(sql)
                .param(quizId)
                .query(new QuizSubmissionMapper())
                .list();
    }
}