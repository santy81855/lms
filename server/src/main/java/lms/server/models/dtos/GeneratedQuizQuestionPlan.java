package lms.server.models.dtos;

import lms.server.models.QuestionType;

import java.math.BigDecimal;
import java.util.List;

public class GeneratedQuizQuestionPlan {

    private String questionText;
    private QuestionType questionType;
    private BigDecimal points;
    private String explanation;
    private List<GeneratedQuizAnswerOptionPlan> options;

    public String getQuestionText() {
        return questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public BigDecimal getPoints() {
        return points;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<GeneratedQuizAnswerOptionPlan> getOptions() {
        return options;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public void setPoints(BigDecimal points) {
        this.points = points;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setOptions(List<GeneratedQuizAnswerOptionPlan> options) {
        this.options = options;
    }
}