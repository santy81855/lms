package lms.server.data;

import lms.server.models.QuizSubmissionAnswer;

import java.util.List;

public interface QuizSubmissionAnswerRepository {

    List<QuizSubmissionAnswer> findBySubmissionId(Long submissionId);

    QuizSubmissionAnswer add(QuizSubmissionAnswer answer);
}