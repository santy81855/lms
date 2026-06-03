package lms.server.models.dtos;

import lms.server.models.Lesson;

public class CourseLessonSummaryResponse {

    private Long id;
    private Long moduleId;
    private String title;

    public CourseLessonSummaryResponse(Lesson lesson) {
        this.id = lesson.getId();
        this.moduleId = lesson.getModuleId();
        this.title = lesson.getTitle();
    }

    public Long getId() {
        return id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public String getTitle() {
        return title;
    }
}