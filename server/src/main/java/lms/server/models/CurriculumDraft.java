package lms.server.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

public class CurriculumDraft {
    private Long id;

    @NotNull(message = "Course id is required.")
    private Long courseId;

    @NotNull(message = "Syllabus upload id is required.")
    private Long syllabusUploadId;

    @NotNull(message = "Teacher id is required.")
    private Long teacherId;

    @Size(max = 255, message = "Title must be 255 characters or fewer.")
    private String title;

    @NotNull(message = "Generation status is required.")
    private GenerationStatus generationStatus;

    @Size(max = 100, message = "AI model must be 100 characters or fewer.")
    private String aiModel;

    private String teacherNotes;
    private String errorMessage;

    // Add these if you update curriculum_drafts with draft JSON support.
    private String generatedContentJson;
    private LocalDateTime acceptedAt;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSyllabusUploadId() {
        return syllabusUploadId;
    }

    public void setSyllabusUploadId(Long syllabusUploadId) {
        this.syllabusUploadId = syllabusUploadId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(GenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getTeacherNotes() {
        return teacherNotes;
    }

    public void setTeacherNotes(String teacherNotes) {
        this.teacherNotes = teacherNotes;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getGeneratedContentJson() {
        return generatedContentJson;
    }

    public void setGeneratedContentJson(String generatedContentJson) {
        this.generatedContentJson = generatedContentJson;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CurriculumDraft that = (CurriculumDraft) o;
        return Objects.equals(id, that.id) && Objects.equals(courseId, that.courseId) && Objects.equals(syllabusUploadId, that.syllabusUploadId) && Objects.equals(teacherId, that.teacherId) && Objects.equals(title, that.title) && generationStatus == that.generationStatus && Objects.equals(aiModel, that.aiModel) && Objects.equals(teacherNotes, that.teacherNotes) && Objects.equals(errorMessage, that.errorMessage) && Objects.equals(generatedContentJson, that.generatedContentJson) && Objects.equals(acceptedAt, that.acceptedAt) && Objects.equals(createdAt, that.createdAt) && Objects.equals(completedAt, that.completedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, syllabusUploadId, teacherId, title, generationStatus, aiModel, teacherNotes, errorMessage, generatedContentJson, acceptedAt, createdAt, completedAt);
    }

    @Override
    public String toString() {
        return "CurriculumDraft{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", syllabusUploadId=" + syllabusUploadId +
                ", teacherId=" + teacherId +
                ", title='" + title + '\'' +
                ", generationStatus=" + generationStatus +
                ", aiModel='" + aiModel + '\'' +
                ", teacherNotes='" + teacherNotes + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", generatedContentJson='" + generatedContentJson + '\'' +
                ", acceptedAt=" + acceptedAt +
                ", createdAt=" + createdAt +
                ", completedAt=" + completedAt +
                '}';
    }
}