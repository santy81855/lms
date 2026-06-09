package lms.server.data;

import lms.server.models.QuizSubmission;

import java.util.List;
import java.util.Optional;

public interface QuizSubmissionRepository {

    Optional<QuizSubmission> findById(Long id);

    List<QuizSubmission> findByQuizIdAndStudentId(Long quizId, Long studentId);

    Optional<QuizSubmission> findLatestByQuizIdAndStudentId(Long quizId, Long studentId);

    int countByQuizIdAndStudentId(Long quizId, Long studentId);

    QuizSubmission add(QuizSubmission submission);

    List<QuizSubmission> findByQuizId(Long quizId);
}