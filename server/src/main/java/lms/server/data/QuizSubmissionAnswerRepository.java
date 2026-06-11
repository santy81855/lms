package lms.server.data;

import lms.server.models.QuizSubmissionAnswer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface QuizSubmissionAnswerRepository {

    List<QuizSubmissionAnswer> findBySubmissionId(Long submissionId);

    QuizSubmissionAnswer add(QuizSubmissionAnswer answer);

    boolean updateGrade(Long answerId,
                        Double points,
                        Boolean isCorrect,
                        Long gradedBy);

    List<QuizSubmissionAnswer> findUngradedShortAnswers(Long quizId);

    Optional<QuizSubmissionAnswer> findById(Long id);
}