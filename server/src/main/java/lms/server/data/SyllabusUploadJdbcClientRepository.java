package lms.server.data;

import lms.server.data.mappers.SyllabusUploadMapper;
import lms.server.models.ProcessingStatus;
import lms.server.models.SyllabusUpload;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SyllabusUploadJdbcClientRepository implements SyllabusUploadRepository {

    private final JdbcClient jdbcClient;

    public SyllabusUploadJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<SyllabusUpload> findById(Long id) {
        final String sql = """
                SELECT id, course_id, teacher_id, title, input_type,
                       syllabus_text, original_file_name, file_url,
                       processing_status, error_message, created_at, processed_at
                FROM syllabus_uploads
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(id)
                .query(new SyllabusUploadMapper())
                .optional();
    }

    @Override
    public Optional<SyllabusUpload> findByIdAndTeacherId(Long uploadId, Long teacherId) {
        final String sql = """
                SELECT id, course_id, teacher_id, title, input_type,
                       syllabus_text, original_file_name, file_url,
                       processing_status, error_message, created_at, processed_at
                FROM syllabus_uploads
                WHERE id = ?
                  AND teacher_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(uploadId)
                .param(teacherId)
                .query(new SyllabusUploadMapper())
                .optional();
    }

    @Override
    public List<SyllabusUpload> findByCourseId(Long courseId) {
        final String sql = """
                SELECT id, course_id, teacher_id, title, input_type,
                       syllabus_text, original_file_name, file_url,
                       processing_status, error_message, created_at, processed_at
                FROM syllabus_uploads
                WHERE course_id = ?
                ORDER BY created_at DESC;
                """;

        return jdbcClient.sql(sql)
                .param(courseId)
                .query(new SyllabusUploadMapper())
                .list();
    }

    @Override
    public List<SyllabusUpload> findByTeacherId(Long teacherId) {
        final String sql = """
                SELECT id, course_id, teacher_id, title, input_type,
                       syllabus_text, original_file_name, file_url,
                       processing_status, error_message, created_at, processed_at
                FROM syllabus_uploads
                WHERE teacher_id = ?
                ORDER BY created_at DESC;
                """;

        return jdbcClient.sql(sql)
                .param(teacherId)
                .query(new SyllabusUploadMapper())
                .list();
    }

    @Override
    public SyllabusUpload add(SyllabusUpload upload) {
        final String sql = """
                INSERT INTO syllabus_uploads (
                    course_id,
                    teacher_id,
                    title,
                    input_type,
                    syllabus_text,
                    original_file_name,
                    file_url,
                    processing_status,
                    error_message
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(upload.getCourseId())
                .param(upload.getTeacherId())
                .param(upload.getTitle())
                .param(upload.getInputType().name())
                .param(upload.getSyllabusText())
                .param(upload.getOriginalFileName())
                .param(upload.getFileUrl())
                .param(upload.getProcessingStatus().name())
                .param(upload.getErrorMessage())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("Syllabus upload insert failed.") {};
        }

        upload.setId(keyHolder.getKey().longValue());

        return upload;
    }

    @Override
    public boolean updateProcessingStatus(Long uploadId, ProcessingStatus status, String errorMessage) {
        final String sql = """
                UPDATE syllabus_uploads
                SET processing_status = ?,
                    error_message = ?
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(status.name())
                .param(errorMessage)
                .param(uploadId)
                .update() > 0;
    }

    @Override
    public boolean markProcessed(Long uploadId) {
        final String sql = """
                UPDATE syllabus_uploads
                SET processed_at = CURRENT_TIMESTAMP
                WHERE id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(uploadId)
                .update() > 0;
    }

    @Override
    public boolean deleteByIdAndTeacherId(Long uploadId, Long teacherId) {
        final String sql = """
                DELETE FROM syllabus_uploads
                WHERE id = ?
                  AND teacher_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(uploadId)
                .param(teacherId)
                .update() > 0;
    }
}