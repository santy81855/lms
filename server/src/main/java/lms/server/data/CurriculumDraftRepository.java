package lms.server.data;

import lms.server.models.CurriculumDraft;
import lms.server.models.GenerationStatus;

import java.util.List;
import java.util.Optional;

public interface CurriculumDraftRepository {
    Optional<CurriculumDraft> findById(Long id);

    Optional<CurriculumDraft> findByIdAndTeacherId(Long draftId, Long teacherId);

    List<CurriculumDraft> findByCourseId(Long courseId);

    List<CurriculumDraft> findByTeacherId(Long teacherId);

    CurriculumDraft add(CurriculumDraft draft);

    boolean updateGeneratedContent(Long draftId, String generatedContentJson);

    boolean updateGenerationStatus(Long draftId, GenerationStatus status, String errorMessage);

    boolean updateTeacherNotes(Long draftId, Long teacherId, String teacherNotes);

    boolean markAccepted(Long draftId, Long teacherId);

    boolean deleteByIdAndTeacherId(Long draftId, Long teacherId);
}