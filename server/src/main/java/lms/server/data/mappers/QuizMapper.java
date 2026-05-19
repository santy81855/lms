package lms.server.data.mappers;

import lms.server.models.Quiz;
import lms.server.models.VisibilityStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizMapper implements RowMapper<Quiz> {

    @Override
    public Quiz mapRow(ResultSet resultSet, int i) throws SQLException {
        Quiz quiz = new Quiz();

        quiz.setId(resultSet.getLong("id"));
        quiz.setModuleId(resultSet.getLong("module_id"));
        quiz.setTitle(resultSet.getString("title"));
        quiz.setDescription(resultSet.getString("description"));
        quiz.setQuizOrder(resultSet.getInt("quiz_order"));
        quiz.setMaxPoints(resultSet.getBigDecimal("max_points"));

        int timeLimitMinutes = resultSet.getInt("time_limit_minutes");
        if (!resultSet.wasNull()) {
            quiz.setTimeLimitMinutes(timeLimitMinutes);
        }

        quiz.setAttemptsAllowed(resultSet.getInt("attempts_allowed"));

        quiz.setStatus(
                VisibilityStatus.valueOf(resultSet.getString("status"))
        );

        if (resultSet.getTimestamp("created_at") != null) {
            quiz.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            quiz.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("published_at") != null) {
            quiz.setPublishedAt(resultSet.getTimestamp("published_at").toLocalDateTime());
        }

        return quiz;
    }
}