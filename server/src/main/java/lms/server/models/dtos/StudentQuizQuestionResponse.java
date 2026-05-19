package lms.server.models.dtos;

import lms.server.models.QuestionType;

import java.math.BigDecimal;
import java.util.List;

public class StudentQuizQuestionResponse {

    private Long id;
    private Long quizId;
    private String questionText;
    private QuestionType questionType;
    private Integer questionOrder;
    private BigDecimal points;
    private List<StudentQuizOptionResponse> options;

    public Long getId() {
        return id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public BigDecimal getPoints() {
        return points;
    }

    public List<StudentQuizOptionResponse> getOptions() {
        return options;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public void setPoints(BigDecimal points) {
        this.points = points;
    }

    public void setOptions(List<StudentQuizOptionResponse> options) {
        this.options = options;
    }
}