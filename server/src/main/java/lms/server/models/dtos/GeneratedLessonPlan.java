package lms.server.models.dtos;

public class GeneratedLessonPlan {

    private String title;
    private String content;
    private Integer estimatedMinutes;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }
}