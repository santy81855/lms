package lms.server.data.mappers;

import lms.server.models.QuestionType;
import lms.server.models.QuizQuestion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizQuestionMapper implements RowMapper<QuizQuestion> {

    @Override
    public QuizQuestion mapRow(ResultSet resultSet, int i) throws SQLException {
        QuizQuestion question = new QuizQuestion();

        question.setId(resultSet.getLong("id"));
        question.setQuizId(resultSet.getLong("quiz_id"));
        question.setQuestionText(resultSet.getString("question_text"));
        question.setAssociatedLessonId(resultSet.getObject("associated_lesson_id", Long.class));

        question.setQuestionType(
                QuestionType.valueOf(resultSet.getString("question_type"))
        );

        question.setQuestionOrder(resultSet.getInt("question_order"));
        question.setPoints(resultSet.getBigDecimal("points"));
        question.setExplanation(resultSet.getString("explanation"));

        if (resultSet.getTimestamp("created_at") != null) {
            question.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            question.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        }

        return question;
    }
}