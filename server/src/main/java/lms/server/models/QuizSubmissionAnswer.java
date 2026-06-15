package lms.server.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class QuizSubmissionAnswer {

    private Long id;

    @NotNull(message = "Quiz submission id is required.")
    private Long quizSubmissionId;

    @NotNull(message = "Question id is required.")
    private Long questionId;

    private Long selectedOptionId;
    private String shortAnswerText;
    private Boolean correct;

    @NotNull(message = "Points earned is required.")
    @DecimalMin(value = "0.00", message = "Points earned cannot be negative.")
    private BigDecimal pointsEarned;

    private LocalDateTime createdAt;

    private String studentName;
    private String questionText;
    private BigDecimal maxPoints;

    public String getStudentName() {
        return studentName;
    }

    public String getQuestionText() {
        return questionText;
    }

    public BigDecimal getMaxPoints() {
        return maxPoints;
    }

    public Long getId() {
        return id;
    }

    public Long getQuizSubmissionId() {
        return quizSubmissionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public String getShortAnswerText() {
        return shortAnswerText;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public BigDecimal getPointsEarned() {
        return pointsEarned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuizSubmissionId(Long quizSubmissionId) {
        this.quizSubmissionId = quizSubmissionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public void setShortAnswerText(String shortAnswerText) {
        this.shortAnswerText = shortAnswerText;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public void setPointsEarned(BigDecimal pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizSubmissionAnswer that = (QuizSubmissionAnswer) o;
        return Objects.equals(id, that.id) && Objects.equals(quizSubmissionId, that.quizSubmissionId) && Objects.equals(questionId, that.questionId) && Objects.equals(selectedOptionId, that.selectedOptionId) && Objects.equals(shortAnswerText, that.shortAnswerText) && Objects.equals(correct, that.correct) && Objects.equals(pointsEarned, that.pointsEarned) && Objects.equals(createdAt, that.createdAt) && Objects.equals(studentName, that.studentName) && Objects.equals(questionText, that.questionText) && Objects.equals(maxPoints, that.maxPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quizSubmissionId, questionId, selectedOptionId, shortAnswerText, correct, pointsEarned, createdAt, studentName, questionText, maxPoints);
    }

    @Override
    public String toString() {
        return "QuizSubmissionAnswer{" +
                "id=" + id +
                ", quizSubmissionId=" + quizSubmissionId +
                ", questionId=" + questionId +
                ", selectedOptionId=" + selectedOptionId +
                ", shortAnswerText='" + shortAnswerText + '\'' +
                ", correct=" + correct +
                ", pointsEarned=" + pointsEarned +
                ", createdAt=" + createdAt +
                ", studentName='" + studentName + '\'' +
                ", questionText='" + questionText + '\'' +
                ", maxPoints=" + maxPoints +
                '}';
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setMaxPoints(BigDecimal maxPoints) {
        this.maxPoints = maxPoints;
    }
}