package lms.server.models.dtos;

public class StudentQuizAnswerRequest {

    private Long questionId;
    private Long selectedOptionId;
    private String shortAnswerText;

    public Long getQuestionId() {
        return questionId;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public String getShortAnswerText() {
        return shortAnswerText;
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
}