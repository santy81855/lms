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

    private QuizFeedbackQuizResponse(Long id,
                                     String title,
                                     String description,
                                     Integer quizOrder,
                                     BigDecimal maxPoints,
                                     Integer timeLimitMinutes,
                                     Integer attemptsAllowed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.quizOrder = quizOrder;
        this.maxPoints = maxPoints;
        this.timeLimitMinutes = timeLimitMinutes;
        this.attemptsAllowed = attemptsAllowed;
    }

    public static QuizFeedbackQuizResponse from(Quiz quiz) {
        return new QuizFeedbackQuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getQuizOrder(),
                quiz.getMaxPoints(),
                quiz.getTimeLimitMinutes(),
                quiz.getAttemptsAllowed()
        );
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