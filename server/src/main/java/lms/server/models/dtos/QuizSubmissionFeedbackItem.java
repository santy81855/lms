package lms.server.models.dtos;

import lms.server.models.QuizQuestion;

public class QuizSubmissionFeedbackItem {

    private Integer questionNumber;
    private String questionContents;
    private Long associatedLessonId;

    public QuizSubmissionFeedbackItem(Integer questionNumber, String questionContents, Long associatedLessonId) {
        this.questionNumber = questionNumber;
        this.questionContents = questionContents;
        this.associatedLessonId = associatedLessonId;
    }

    public static QuizSubmissionFeedbackItem fromQuizQuestion(QuizQuestion question) {
        return new QuizSubmissionFeedbackItem(
                question.getQuestionOrder(),
                question.getQuestionText(),
                question.getAssociatedLessonId()
        );
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionContents() {
        return questionContents;
    }

    public Long getAssociatedLessonId() {
        return associatedLessonId;
    }
}