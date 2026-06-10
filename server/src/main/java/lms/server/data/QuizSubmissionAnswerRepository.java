package lms.server.data;

import lms.server.models.QuizSubmissionAnswer;

import java.util.List;
import java.util.Optional;

public interface QuizSubmissionAnswerRepository {

    List<QuizSubmissionAnswer> findBySubmissionId(Long submissionId);

    QuizSubmissionAnswer add(QuizSubmissionAnswer answer);

    boolean updateGrade(Long answerId,
                        Double pointsEarned,
                        Boolean isCorrect,
                        Long gradedBy);

    List<QuizSubmissionAnswer> findUngradedShortAnswers(Long quizId);

    Optional<QuizSubmissionAnswer> findById(Long id);
}