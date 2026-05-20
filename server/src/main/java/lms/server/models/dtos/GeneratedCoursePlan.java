package lms.server.models.dtos;

import lms.server.models.GradeLevel;

import java.util.List;

public class GeneratedCoursePlan {

    private String title;
    private String subject;
    private GradeLevel gradeLevel;
    private String description;
    private List<GeneratedModulePlan> modules;

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public String getDescription() {
        return description;
    }

    public List<GeneratedModulePlan> getModules() {
        return modules;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setGradeLevel(GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setModules(List<GeneratedModulePlan> modules) {
        this.modules = modules;
    }
}