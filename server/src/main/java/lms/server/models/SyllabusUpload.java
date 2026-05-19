package lms.server.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

public class SyllabusUpload {
    private Long id;

    @NotNull(message = "Course id is required.")
    private Long courseId;

    @NotNull(message = "Teacher id is required.")
    private Long teacherId;

    @Size(max = 255, message = "Title must be 255 characters or fewer.")
    private String title;

    @NotNull(message = "Input type is required.")
    private SyllabusInputType inputType;

    private String syllabusText;

    @Size(max = 255, message = "Original file name must be 255 characters or fewer.")
    private String originalFileName;

    @Size(max = 500, message = "File URL must be 500 characters or fewer.")
    private String fileUrl;

    @NotNull(message = "Processing status is required.")
    private ProcessingStatus processingStatus;

    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SyllabusInputType getInputType() {
        return inputType;
    }

    public void setInputType(SyllabusInputType inputType) {
        this.inputType = inputType;
    }

    public String getSyllabusText() {
        return syllabusText;
    }

    public void setSyllabusText(String syllabusText) {
        this.syllabusText = syllabusText;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SyllabusUpload that = (SyllabusUpload) o;
        return Objects.equals(id, that.id) && Objects.equals(courseId, that.courseId) && Objects.equals(teacherId, that.teacherId) && Objects.equals(title, that.title) && inputType == that.inputType && Objects.equals(syllabusText, that.syllabusText) && Objects.equals(originalFileName, that.originalFileName) && Objects.equals(fileUrl, that.fileUrl) && processingStatus == that.processingStatus && Objects.equals(errorMessage, that.errorMessage) && Objects.equals(createdAt, that.createdAt) && Objects.equals(processedAt, that.processedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, teacherId, title, inputType, syllabusText, originalFileName, fileUrl, processingStatus, errorMessage, createdAt, processedAt);
    }

    @Override
    public String toString() {
        return "SyllabusUpload{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", title='" + title + '\'' +
                ", inputType=" + inputType +
                ", syllabusText='" + syllabusText + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", processingStatus=" + processingStatus +
                ", errorMessage='" + errorMessage + '\'' +
                ", createdAt=" + createdAt +
                ", processedAt=" + processedAt +
                '}';
    }
}