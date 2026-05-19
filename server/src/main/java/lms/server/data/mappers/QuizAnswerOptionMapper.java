package lms.server.data.mappers;

import lms.server.models.QuizAnswerOption;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizAnswerOptionMapper implements RowMapper<QuizAnswerOption> {

    @Override
    public QuizAnswerOption mapRow(ResultSet resultSet, int i) throws SQLException {
        QuizAnswerOption option = new QuizAnswerOption();

        option.setId(resultSet.getLong("id"));
        option.setQuestionId(resultSet.getLong("question_id"));
        option.setOptionText(resultSet.getString("option_text"));
        option.setOptionOrder(resultSet.getInt("option_order"));
        option.setCorrect(resultSet.getBoolean("is_correct"));

        if (resultSet.getTimestamp("created_at") != null) {
            option.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            option.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        }

        return option;
    }
}