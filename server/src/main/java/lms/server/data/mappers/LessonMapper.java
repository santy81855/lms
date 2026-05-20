package lms.server.data.mappers;

import lms.server.models.Lesson;
import lms.server.models.VisibilityStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LessonMapper implements RowMapper<Lesson> {

    @Override
    public Lesson mapRow(ResultSet resultSet, int i) throws SQLException {
        Lesson lesson = new Lesson();

        lesson.setId(resultSet.getLong("id"));
        lesson.setModuleId(resultSet.getLong("module_id"));
        lesson.setTitle(resultSet.getString("title"));
        lesson.setContent(resultSet.getString("content"));
        lesson.setLessonOrder(resultSet.getInt("lesson_order"));

        int estimatedMinutes = resultSet.getInt("estimated_minutes");
        if (!resultSet.wasNull()) {
            lesson.setEstimatedMinutes(estimatedMinutes);
        }

        lesson.setStatus(
                VisibilityStatus.valueOf(resultSet.getString("status"))
        );

        if (resultSet.getTimestamp("created_at") != null) {
            lesson.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            lesson.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("published_at") != null) {
            lesson.setPublishedAt(resultSet.getTimestamp("published_at").toLocalDateTime());
        }

        return lesson;
    }
}