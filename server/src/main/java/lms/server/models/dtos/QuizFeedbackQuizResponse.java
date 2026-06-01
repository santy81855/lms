package lms.server.models.dtos;

import lms.server.models.Quiz;

import java.math.BigDecimal;

public class QuizFeedbackQuizResponse {

    private Long id;
    private String title;
    private String description;
    private Integer quizOrder;
    private BigDecimal maxPoints;
    private Integer timeLimitMinutes;
    private Integer attemptsAllowed;

    public QuizFeedbackQuizResponse(Quiz quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.description = quiz.getDescription();
        this.quizOrder = quiz.getQuizOrder();
        this.maxPoints = quiz.getMaxPoints();
        this.timeLimitMinutes = quiz.getTimeLimitMinutes();
        this.attemptsAllowed = quiz.getAttemptsAllowed();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuizOrder() {
        return quizOrder;
    }

    public BigDecimal getMaxPoints() {
        return maxPoints;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public Integer getAttemptsAllowed() {
        return attemptsAllowed;
    }
}