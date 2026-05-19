package lms.server.data;

import lms.server.models.QuizQuestion;

import java.util.List;
import java.util.Optional;

public interface QuizQuestionRepository {
    Optional<QuizQuestion> findById(Long id);

    Optional<QuizQuestion> findByIdAndQuizId(Long questionId, Long quizId);

    List<QuizQuestion> findByQuizId(Long quizId);

    QuizQuestion add(QuizQuestion question);

    boolean update(QuizQuestion question);

    boolean updateOrder(Long questionId, Integer questionOrder);

    boolean deleteById(Long id);

    boolean deleteByIdAndQuizId(Long questionId, Long quizId);
}