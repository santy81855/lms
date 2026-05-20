package lms.server.models.dtos;

import lms.server.models.GradeLevel;

public class CreateCourseFromSyllabusRequest {

    private String title;
    private String subject;
    private GradeLevel gradeLevel;
    private String description;
    private String syllabusText;

    private Integer moduleCount;
    private Boolean includeAssignments;
    private Boolean includeQuizzes;

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

    public String getSyllabusText() {
        return syllabusText;
    }

    public Integer getModuleCount() {
        return moduleCount;
    }

    public Boolean getIncludeAssignments() {
        return includeAssignments;
    }

    public Boolean getIncludeQuizzes() {
        return includeQuizzes;
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

    public void setSyllabusText(String syllabusText) {
        this.syllabusText = syllabusText;
    }

    public void setModuleCount(Integer moduleCount) {
        this.moduleCount = moduleCount;
    }

    public void setIncludeAssignments(Boolean includeAssignments) {
        this.includeAssignments = includeAssignments;
    }

    public void setIncludeQuizzes(Boolean includeQuizzes) {
        this.includeQuizzes = includeQuizzes;
    }
}