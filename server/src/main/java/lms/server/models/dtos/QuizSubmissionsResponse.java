package lms.server.models.dtos;

import java.util.List;

public class QuizSubmissionsResponse {

    private List<QuizSubmissionResponse> submissions;

    public QuizSubmissionsResponse(List<QuizSubmissionResponse> submissions) {
        this.submissions = submissions;
    }

    public List<QuizSubmissionResponse> getSubmissions() {
        return submissions;
    }
}
