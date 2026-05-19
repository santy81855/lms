package lms.server.data.mappers;

import lms.server.models.ProcessingStatus;
import lms.server.models.SyllabusInputType;
import lms.server.models.SyllabusUpload;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SyllabusUploadMapper implements RowMapper<SyllabusUpload> {

    @Override
    public SyllabusUpload mapRow(ResultSet resultSet, int i) throws SQLException {
        SyllabusUpload upload = new SyllabusUpload();

        upload.setId(resultSet.getLong("id"));
        upload.setCourseId(resultSet.getLong("course_id"));
        upload.setTeacherId(resultSet.getLong("teacher_id"));
        upload.setTitle(resultSet.getString("title"));

        upload.setInputType(
                SyllabusInputType.valueOf(resultSet.getString("input_type"))
        );

        upload.setSyllabusText(resultSet.getString("syllabus_text"));
        upload.setOriginalFileName(resultSet.getString("original_file_name"));
        upload.setFileUrl(resultSet.getString("file_url"));

        upload.setProcessingStatus(
                ProcessingStatus.valueOf(resultSet.getString("processing_status"))
        );

        upload.setErrorMessage(resultSet.getString("error_message"));

        if (resultSet.getTimestamp("created_at") != null) {
            upload.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("processed_at") != null) {
            upload.setProcessedAt(resultSet.getTimestamp("processed_at").toLocalDateTime());
        }

        return upload;
    }
}