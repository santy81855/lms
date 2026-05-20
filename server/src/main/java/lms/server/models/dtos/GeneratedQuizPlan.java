package lms.server.models.dtos;

import java.math.BigDecimal;
import java.util.List;

public class GeneratedQuizPlan {

    private String title;
    private String description;
    private BigDecimal maxPoints;
    private Integer timeLimitMinutes;
    private Integer attemptsAllowed;
    private List<GeneratedQuizQuestionPlan> questions;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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

    public List<GeneratedQuizQuestionPlan> getQuestions() {
        return questions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxPoints(BigDecimal maxPoints) {
        this.maxPoints = maxPoints;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public void setAttemptsAllowed(Integer attemptsAllowed) {
        this.attemptsAllowed = attemptsAllowed;
    }

    public void setQuestions(List<GeneratedQuizQuestionPlan> questions) {
        this.questions = questions;
    }
}