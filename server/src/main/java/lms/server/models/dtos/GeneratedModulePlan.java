package lms.server.models.dtos;

import java.util.List;

public class GeneratedModulePlan {

    private String title;
    private String description;
    private List<GeneratedLessonPlan> lessons;
    private List<GeneratedAssignmentPlan> assignments;
    private List<GeneratedQuizPlan> quizzes;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<GeneratedLessonPlan> getLessons() {
        return lessons;
    }

    public List<GeneratedAssignmentPlan> getAssignments() {
        return assignments;
    }

    public List<GeneratedQuizPlan> getQuizzes() {
        return quizzes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLessons(List<GeneratedLessonPlan> lessons) {
        this.lessons = lessons;
    }

    public void setAssignments(List<GeneratedAssignmentPlan> assignments) {
        this.assignments = assignments;
    }

    public void setQuizzes(List<GeneratedQuizPlan> quizzes) {
        this.quizzes = quizzes;
    }
}