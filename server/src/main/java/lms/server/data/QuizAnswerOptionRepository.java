package lms.server.data;

import lms.server.models.QuizAnswerOption;

import java.util.List;
import java.util.Optional;

public interface QuizAnswerOptionRepository {
    Optional<QuizAnswerOption> findById(Long id);

    Optional<QuizAnswerOption> findByIdAndQuestionId(Long optionId, Long questionId);

    List<QuizAnswerOption> findByQuestionId(Long questionId);

    QuizAnswerOption add(QuizAnswerOption option);

    boolean update(QuizAnswerOption option);

    boolean updateOrder(Long optionId, Integer optionOrder);

    boolean deleteById(Long id);

    boolean deleteByIdAndQuestionId(Long optionId, Long questionId);
}