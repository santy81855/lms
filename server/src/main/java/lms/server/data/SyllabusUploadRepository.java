package lms.server.data;

import lms.server.models.ProcessingStatus;
import lms.server.models.SyllabusUpload;

import java.util.List;
import java.util.Optional;

public interface SyllabusUploadRepository {
    Optional<SyllabusUpload> findById(Long id);

    Optional<SyllabusUpload> findByIdAndTeacherId(Long uploadId, Long teacherId);

    List<SyllabusUpload> findByCourseId(Long courseId);

    List<SyllabusUpload> findByTeacherId(Long teacherId);

    SyllabusUpload add(SyllabusUpload upload);

    boolean updateProcessingStatus(Long uploadId, ProcessingStatus status, String errorMessage);

    boolean markProcessed(Long uploadId);

    boolean deleteByIdAndTeacherId(Long uploadId, Long teacherId);
}