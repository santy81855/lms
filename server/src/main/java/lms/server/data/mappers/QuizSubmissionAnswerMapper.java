package lms.server.data.mappers;

import lms.server.models.QuizSubmissionAnswer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizSubmissionAnswerMapper implements RowMapper<QuizSubmissionAnswer> {

    @Override
    public QuizSubmissionAnswer mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        QuizSubmissionAnswer answer = new QuizSubmissionAnswer();

        answer.setId(resultSet.getLong("id"));
        answer.setQuizSubmissionId(resultSet.getLong("quiz_submission_id"));
        answer.setQuestionId(resultSet.getLong("question_id"));

        answer.setSelectedOptionId(
                resultSet.getObject("selected_option_id", Long.class)
        );

        answer.setShortAnswerText(resultSet.getString("short_answer_text"));

        answer.setCorrect(
                resultSet.getObject("is_correct", Boolean.class)
        );

        answer.setPointsEarned(resultSet.getBigDecimal("points_earned"));

        if (resultSet.getTimestamp("created_at") != null) {
            answer.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        return answer;
    }
}