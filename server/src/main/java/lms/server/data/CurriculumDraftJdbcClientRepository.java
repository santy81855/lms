package lms.server.data;

import lms.server.data.mappers.CurriculumDraftMapper;
import lms.server.models.CurriculumDraft;
import lms.server.models.GenerationStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CurriculumDraftJdbcClientRepository implements CurriculumDraftRepository {

    private final JdbcClient jdbcClient;

    public CurriculumDraftJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<CurriculumDraft> findById(Long id) {
        final String sql = """
                SELECT id, course_id, syllabus_upload_id, teacher_id, title,
                       generation_status, ai_model, generated_content_json,
                       teacher_notes, error_message, created_at, completed_at, accepted_at
                FROM curriculum_drafts
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new CurriculumDraftMapper())
                .optional();
    }

    @Override
    public Optional<CurriculumDraft> findByIdAndTeacherId(Long draftId, Long teacherId) {
        final String sql = """
                SELECT id, course_id, syllabus_upload_id, teacher_id, title,
                       generation_status, ai_model, generated_content_json,
                       teacher_notes, error_message, created_at, completed_at, accepted_at
                FROM curriculum_drafts
                WHERE id = ?
                  AND teacher_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(draftId)
                .param(teacherId)
                .query(new CurriculumDraftMapper())
                .optional();
    }

    @Override
    public List<CurriculumDraft> findByCourseId(Long courseId) {
        final String sql = """
                SELECT id, course_id, syllabus_upload_id, teacher_id, title,
                       generation_status, ai_model, generated_content_json,
                       teacher_notes, error_message, created_at, completed_at, accepted_at
                FROM curriculum_drafts
                WHERE course_id = ?
                ORDER BY created_at DESC;
                """;

        return jdbcClient.sql(sql)
                .param(courseId)
                .query(new CurriculumDraftMapper())
                .list();
    }

    @Override
    public List<CurriculumDraft> findByTeacherId(Long teacherId) {
        final String sql = """
                SELECT id, course_id, syllabus_upload_id, teacher_id, title,
                       generation_status, ai_model, generated_content_json,
                       teacher_notes, error_message, created_at, completed_at, accepted_at
                FROM curriculum_drafts
                WHERE teacher_id = ?
                ORDER BY created_at DESC;
                """;

        return jdbcClient.sql(sql)
                .param(teacherId)
                .query(new CurriculumDraftMapper())
                .list();
    }

    @Override
    public CurriculumDraft add(CurriculumDraft draft) {
        final String sql = """
                INSERT INTO curriculum_drafts (
                    course_id,
                    syllabus_upload_id,
                    teacher_id,
                    title,
                    generation_status,
                    ai_model,
                    generated_content_json,
                    teacher_notes,
                    error_message
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(draft.getCourseId())
                .param(draft.getSyllabusUploadId())
                .param(draft.getTeacherId())
                .param(draft.getTitle())
                .param(draft.getGenerationStatus().name())
                .param(draft.getAiModel())
                .param(draft.getGeneratedContentJson())
                .param(draft.getTeacherNotes())
                .param(draft.getErrorMessage())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Curriculum draft insert failed.") {};
        }

        draft.setId(keyHolder.getKey().longValue());

        return draft;
    }

    @Override
    public boolean updateGeneratedContent(Long draftId, String generatedContentJson) {
        final String sql = """
                UPDATE curriculum_drafts
                SET generated_content_json = ?
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(generatedContentJson)
                .param(draftId)
                .update() > 0;
    }

    @Override
    public boolean updateGenerationStatus(Long draftId, GenerationStatus status, String errorMessage) {
        final String sql = """
                UPDATE curriculum_drafts
                SET generation_status = ?,
                    error_message = ?,
                    completed_at = CASE
                        WHEN ? IN ('COMPLETED', 'FAILED') THEN CURRENT_TIMESTAMP
                        ELSE completed_at
                    END
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(status.name())
                .param(errorMessage)
                .param(status.name())
                .param(draftId)
                .update() > 0;
    }

    @Override
    public boolean updateTeacherNotes(Long draftId, Long teacherId, String teacherNotes) {
        final String sql = """
                UPDATE curriculum_drafts
                SET teacher_notes = ?
                WHERE id = ?
                  AND teacher_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(teacherNotes)
                .param(draftId)
                .param(teacherId)
                .update() > 0;
    }

    @Override
    public boolean markAccepted(Long draftId, Long teacherId) {
        final String sql = """
                UPDATE curriculum_drafts
                SET accepted_at = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND teacher_id = ?
                  AND accepted_at IS NULL;
                """;

        return jdbcClient.sql(sql)
                .param(draftId)
                .param(teacherId)
                .update() > 0;
    }

    @Override
    public boolean deleteByIdAndTeacherId(Long draftId, Long teacherId) {
        final String sql = """
                DELETE FROM curriculum_drafts
                WHERE id = ?
                  AND teacher_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(draftId)
                .param(teacherId)
                .update() > 0;
    }
}