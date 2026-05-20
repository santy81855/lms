package lms.server.data.mappers;

import lms.server.models.QuizSubmission;
import lms.server.models.QuizSubmissionStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizSubmissionMapper implements RowMapper<QuizSubmission> {

    @Override
    public QuizSubmission mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        QuizSubmission submission = new QuizSubmission();

        submission.setId(resultSet.getLong("id"));
        submission.setQuizId(resultSet.getLong("quiz_id"));
        submission.setStudentId(resultSet.getLong("student_id"));
        submission.setAttemptNumber(resultSet.getInt("attempt_number"));

        submission.setStatus(
                QuizSubmissionStatus.valueOf(resultSet.getString("status"))
        );

        submission.setScore(resultSet.getBigDecimal("score"));
        submission.setMaxScore(resultSet.getBigDecimal("max_score"));

        if (resultSet.getTimestamp("started_at") != null) {
            submission.setStartedAt(resultSet.getTimestamp("started_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("submitted_at") != null) {
            submission.setSubmittedAt(resultSet.getTimestamp("submitted_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("graded_at") != null) {
            submission.setGradedAt(resultSet.getTimestamp("graded_at").toLocalDateTime());
        }

        return submission;
    }
}