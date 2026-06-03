package lms.server.models.dtos;

import java.util.List;

public class CourseLessonsResponse {

    private List<CourseLessonSummaryResponse> lessons;

    public CourseLessonsResponse(List<CourseLessonSummaryResponse> lessons) {
        this.lessons = lessons;
    }

    public List<CourseLessonSummaryResponse> getLessons() {
        return lessons;
    }
}