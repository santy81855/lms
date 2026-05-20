package lms.server.data;

import lms.server.models.Quiz;
import lms.server.models.VisibilityStatus;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {
    Optional<Quiz> findById(Long id);

    Optional<Quiz> findByIdAndModuleId(Long quizId, Long moduleId);

    List<Quiz> findByModuleId(Long moduleId);

    Quiz add(Quiz quiz);

    boolean update(Quiz quiz);

    boolean updateOrder(Long quizId, Integer quizOrder);

    boolean updateStatus(Long quizId, VisibilityStatus status);

    boolean deleteById(Long id);

    boolean deleteByIdAndModuleId(Long quizId, Long moduleId);
}