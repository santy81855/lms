package lms.server.data.mappers;

import lms.server.models.CurriculumDraft;
import lms.server.models.GenerationStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurriculumDraftMapper implements RowMapper<CurriculumDraft> {

    @Override
    public CurriculumDraft mapRow(ResultSet resultSet, int i) throws SQLException {
        CurriculumDraft draft = new CurriculumDraft();

        draft.setId(resultSet.getLong("id"));
        draft.setCourseId(resultSet.getLong("course_id"));
        draft.setSyllabusUploadId(resultSet.getLong("syllabus_upload_id"));
        draft.setTeacherId(resultSet.getLong("teacher_id"));
        draft.setTitle(resultSet.getString("title"));

        draft.setGenerationStatus(
                GenerationStatus.valueOf(resultSet.getString("generation_status"))
        );

        draft.setAiModel(resultSet.getString("ai_model"));
        draft.setGeneratedContentJson(resultSet.getString("generated_content_json"));
        draft.setTeacherNotes(resultSet.getString("teacher_notes"));
        draft.setErrorMessage(resultSet.getString("error_message"));

        if (resultSet.getTimestamp("created_at") != null) {
            draft.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("completed_at") != null) {
            draft.setCompletedAt(resultSet.getTimestamp("completed_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("accepted_at") != null) {
            draft.setAcceptedAt(resultSet.getTimestamp("accepted_at").toLocalDateTime());
        }

        return draft;
    }
}