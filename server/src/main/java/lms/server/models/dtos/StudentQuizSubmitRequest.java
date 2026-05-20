package lms.server.models.dtos;

import java.util.List;

public class StudentQuizSubmitRequest {

    private List<StudentQuizAnswerRequest> answers;

    public List<StudentQuizAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<StudentQuizAnswerRequest> answers) {
        this.answers = answers;
    }
}