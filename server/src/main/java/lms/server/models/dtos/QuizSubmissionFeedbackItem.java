package lms.server.models.dtos;

public class QuizSubmissionFeedbackItem {

    private Integer questionNumber;
    private String questionContents;
    private String feedback;

    public QuizSubmissionFeedbackItem(Integer questionNumber, String questionContents, String feedback) {
        this.questionNumber = questionNumber;
        this.questionContents = questionContents;
        this.feedback = feedback;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionContents() {
        return questionContents;
    }

    public String getFeedback() {
        return feedback;
    }
}