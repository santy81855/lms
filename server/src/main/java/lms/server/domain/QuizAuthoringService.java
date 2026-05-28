package lms.server.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lms.server.data.QuizAnswerOptionRepository;
import lms.server.data.QuizQuestionRepository;
import lms.server.data.QuizSubmissionRepository;
import lms.server.models.dtos.QuizSubmissionResponse;
import lms.server.models.dtos.QuizSubmissionsResponse;
import lms.server.models.Quiz;
import lms.server.models.QuizAnswerOption;
import lms.server.models.QuizQuestion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class QuizAuthoringService {

    private final ModuleContentService moduleContentService;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerOptionRepository quizAnswerOptionRepository;
    private final Validator validator;
    private final QuizSubmissionRepository quizSubmissionRepository;

    public QuizAuthoringService(ModuleContentService moduleContentService,
                                QuizQuestionRepository quizQuestionRepository,
                                QuizAnswerOptionRepository quizAnswerOptionRepository,
                                QuizSubmissionRepository quizSubmissionRepository,
                                Validator validator) {
        this.moduleContentService = moduleContentService;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerOptionRepository = quizAnswerOptionRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.validator = validator;
    }

    public Result<QuizSubmissionsResponse> findSubmissionsByQuizId(Long quizId, Long teacherId) {
        Result<QuizSubmissionsResponse> result = new Result<>();

        if (!requireId(quizId, "Quiz id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (!moduleContentService.teacherOwnsQuiz(quizId, teacherId)) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }

        List<QuizSubmissionResponse> submissions = quizSubmissionRepository.findByQuizId(quizId)
                .stream()
                .map(QuizSubmissionResponse::new)
                .toList();

        result.setPayload(new QuizSubmissionsResponse(submissions));
        return result;
    }

    public Result<List<QuizQuestion>> findQuestionsByQuizId(Long quizId, Long teacherId) {
        Result<List<QuizQuestion>> result = new Result<>();

        if (!requireId(quizId, "Quiz id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (!moduleContentService.teacherOwnsQuiz(quizId, teacherId)) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(quizQuestionRepository.findByQuizId(quizId));
        return result;
    }

    @Transactional
    public Result<QuizQuestion> createQuizQuestion(QuizQuestion question, Long quizId, Long teacherId) {
        Result<QuizQuestion> result = new Result<>();

        if (!requireId(quizId, "Quiz id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (question == null) {
            result.addMessage("Quiz question is required.", ResultType.INVALID);
            return result;
        }

        if (!moduleContentService.teacherOwnsQuiz(quizId, teacherId)) {
            result.addMessage("Quiz not found.", ResultType.NOT_FOUND);
            return result;
        }

        int nextQuestionOrder = getNextQuestionOrder(quizId);
        Integer questionOrder = question.getQuestionOrder();

        if (questionOrder == null) {
            questionOrder = nextQuestionOrder;
        } else if (questionOrder <= 0) {
            result.addMessage("Question order must be greater than zero.", ResultType.INVALID);
            return result;
        } else if (questionOrder > nextQuestionOrder) {
            result.addMessage("Question order cannot be greater than the next available order.", ResultType.INVALID);
            return result;
        }

        QuizQuestion newQuestion = new QuizQuestion();
        newQuestion.setQuizId(quizId);
        newQuestion.setQuestionText(trim(question.getQuestionText()));
        newQuestion.setQuestionType(question.getQuestionType());
        newQuestion.setQuestionOrder(questionOrder);
        newQuestion.setPoints(question.getPoints());
        newQuestion.setExplanation(trimToNull(question.getExplanation()));

        validate(newQuestion, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (questionOrder < nextQuestionOrder) {
            shiftQuestionOrdersForward(quizId, questionOrder);
        }

        result.setPayload(quizQuestionRepository.add(newQuestion));
        return result;
    }

    @Transactional
    public Result<QuizQuestion> updateQuizQuestion(QuizQuestion question, Long teacherId) {
        Result<QuizQuestion> result = new Result<>();

        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (question == null) {
            result.addMessage("Quiz question is required.", ResultType.INVALID);
            return result;
        }

        if (!requireId(question.getId(), "Question id is required.", result)) {
            return result;
        }

        Optional<QuizQuestion> existing = findQuestionForTeacher(question.getId(), teacherId);

        if (existing.isEmpty()) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
            return result;
        }

        QuizQuestion existingQuestion = existing.get();

        Integer questionOrder = question.getQuestionOrder() == null
                ? existingQuestion.getQuestionOrder()
                : question.getQuestionOrder();

        QuizQuestion updatedQuestion = new QuizQuestion();
        updatedQuestion.setId(existingQuestion.getId());
        updatedQuestion.setQuizId(existingQuestion.getQuizId());
        updatedQuestion.setQuestionText(trim(question.getQuestionText()));
        updatedQuestion.setQuestionType(question.getQuestionType());
        updatedQuestion.setQuestionOrder(questionOrder);
        updatedQuestion.setPoints(question.getPoints());
        updatedQuestion.setExplanation(trimToNull(question.getExplanation()));

        validate(updatedQuestion, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (!questionOrder.equals(existingQuestion.getQuestionOrder())) {
            Result<Void> moveResult = moveQuestionInternal(existingQuestion, questionOrder);

            if (!moveResult.isSuccess()) {
                copyMessages(moveResult, result);
                return result;
            }
        }

        if (!quizQuestionRepository.update(updatedQuestion)) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(updatedQuestion);
        return result;
    }

    @Transactional
    public Result<Void> moveQuizQuestion(Long questionId, Long teacherId, Integer questionOrder) {
        Result<Void> result = new Result<>();

        Optional<QuizQuestion> existing = findQuestionForTeacherWithResult(questionId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        return moveQuestionInternal(existing.get(), questionOrder);
    }

    @Transactional
    public Result<Void> deleteQuizQuestion(Long questionId, Long teacherId) {
        Result<Void> result = new Result<>();

        Optional<QuizQuestion> existing = findQuestionForTeacherWithResult(questionId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        QuizQuestion question = existing.get();

        if (!quizQuestionRepository.deleteByIdAndQuizId(questionId, question.getQuizId())) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
            return result;
        }

        shiftQuestionOrdersBackward(question.getQuizId(), question.getQuestionOrder());
        return result;
    }

    public Result<List<QuizAnswerOption>> findAnswerOptionsByQuestionId(Long questionId, Long teacherId) {
        Result<List<QuizAnswerOption>> result = new Result<>();

        if (!requireId(questionId, "Question id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (findQuestionForTeacher(questionId, teacherId).isEmpty()) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(quizAnswerOptionRepository.findByQuestionId(questionId));
        return result;
    }

    @Transactional
    public Result<QuizAnswerOption> createQuizAnswerOption(QuizAnswerOption option,
                                                           Long questionId,
                                                           Long teacherId) {
        Result<QuizAnswerOption> result = new Result<>();

        if (!requireId(questionId, "Question id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (option == null) {
            result.addMessage("Quiz answer option is required.", ResultType.INVALID);
            return result;
        }

        if (findQuestionForTeacher(questionId, teacherId).isEmpty()) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
            return result;
        }

        int nextOptionOrder = getNextOptionOrder(questionId);
        Integer optionOrder = option.getOptionOrder();

        if (optionOrder == null) {
            optionOrder = nextOptionOrder;
        } else if (optionOrder <= 0) {
            result.addMessage("Option order must be greater than zero.", ResultType.INVALID);
            return result;
        } else if (optionOrder > nextOptionOrder) {
            result.addMessage("Option order cannot be greater than the next available order.", ResultType.INVALID);
            return result;
        }

        QuizAnswerOption newOption = new QuizAnswerOption();
        newOption.setQuestionId(questionId);
        newOption.setOptionText(trim(option.getOptionText()));
        newOption.setOptionOrder(optionOrder);
        newOption.setCorrect(option.getCorrect());

        validate(newOption, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (optionOrder < nextOptionOrder) {
            shiftOptionOrdersForward(questionId, optionOrder);
        }

        result.setPayload(quizAnswerOptionRepository.add(newOption));
        return result;
    }

    @Transactional
    public Result<QuizAnswerOption> updateQuizAnswerOption(QuizAnswerOption option, Long teacherId) {
        Result<QuizAnswerOption> result = new Result<>();

        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (option == null) {
            result.addMessage("Quiz answer option is required.", ResultType.INVALID);
            return result;
        }

        if (!requireId(option.getId(), "Option id is required.", result)) {
            return result;
        }

        Optional<QuizAnswerOption> existing = findAnswerOptionForTeacher(option.getId(), teacherId);

        if (existing.isEmpty()) {
            result.addMessage("Quiz answer option not found.", ResultType.NOT_FOUND);
            return result;
        }

        QuizAnswerOption existingOption = existing.get();

        Integer optionOrder = option.getOptionOrder() == null
                ? existingOption.getOptionOrder()
                : option.getOptionOrder();

        QuizAnswerOption updatedOption = new QuizAnswerOption();
        updatedOption.setId(existingOption.getId());
        updatedOption.setQuestionId(existingOption.getQuestionId());
        updatedOption.setOptionText(trim(option.getOptionText()));
        updatedOption.setOptionOrder(optionOrder);
        updatedOption.setCorrect(option.getCorrect());

        validate(updatedOption, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (!optionOrder.equals(existingOption.getOptionOrder())) {
            Result<Void> moveResult = moveOptionInternal(existingOption, optionOrder);

            if (!moveResult.isSuccess()) {
                copyMessages(moveResult, result);
                return result;
            }
        }

        if (!quizAnswerOptionRepository.update(updatedOption)) {
            result.addMessage("Quiz answer option not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(updatedOption);
        return result;
    }

    @Transactional
    public Result<Void> moveQuizAnswerOption(Long optionId, Long teacherId, Integer optionOrder) {
        Result<Void> result = new Result<>();

        Optional<QuizAnswerOption> existing = findAnswerOptionForTeacherWithResult(optionId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        return moveOptionInternal(existing.get(), optionOrder);
    }

    @Transactional
    public Result<Void> deleteQuizAnswerOption(Long optionId, Long teacherId) {
        Result<Void> result = new Result<>();

        Optional<QuizAnswerOption> existing = findAnswerOptionForTeacherWithResult(optionId, teacherId, result);

        if (!result.isSuccess() || existing.isEmpty()) {
            return result;
        }

        QuizAnswerOption option = existing.get();

        if (!quizAnswerOptionRepository.deleteByIdAndQuestionId(optionId, option.getQuestionId())) {
            result.addMessage("Quiz answer option not found.", ResultType.NOT_FOUND);
            return result;
        }

        shiftOptionOrdersBackward(option.getQuestionId(), option.getOptionOrder());
        return result;
    }

    public Optional<QuizQuestion> findQuestionForTeacher(Long questionId, Long teacherId) {
        if (questionId == null || teacherId == null) {
            return Optional.empty();
        }

        Optional<QuizQuestion> question = quizQuestionRepository.findById(questionId);

        if (question.isEmpty()) {
            return Optional.empty();
        }

        Optional<Quiz> quiz = moduleContentService.findQuizForTeacher(
                question.get().getQuizId(),
                teacherId
        );

        if (quiz.isEmpty()) {
            return Optional.empty();
        }

        return question;
    }

    private Optional<QuizQuestion> findQuestionForTeacherWithResult(Long questionId,
                                                                    Long teacherId,
                                                                    Result<?> result) {
        if (!requireId(questionId, "Question id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }

        Optional<QuizQuestion> question = findQuestionForTeacher(questionId, teacherId);

        if (question.isEmpty()) {
            result.addMessage("Quiz question not found.", ResultType.NOT_FOUND);
        }

        return question;
    }

    private Optional<QuizAnswerOption> findAnswerOptionForTeacher(Long optionId, Long teacherId) {
        if (optionId == null || teacherId == null) {
            return Optional.empty();
        }

        Optional<QuizAnswerOption> option = quizAnswerOptionRepository.findById(optionId);

        if (option.isEmpty()) {
            return Optional.empty();
        }

        Optional<QuizQuestion> question = findQuestionForTeacher(
                option.get().getQuestionId(),
                teacherId
        );

        if (question.isEmpty()) {
            return Optional.empty();
        }

        return option;
    }

    private Optional<QuizAnswerOption> findAnswerOptionForTeacherWithResult(Long optionId,
                                                                            Long teacherId,
                                                                            Result<?> result) {
        if (!requireId(optionId, "Option id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return Optional.empty();
        }

        Optional<QuizAnswerOption> option = findAnswerOptionForTeacher(optionId, teacherId);

        if (option.isEmpty()) {
            result.addMessage("Quiz answer option not found.", ResultType.NOT_FOUND);
        }

        return option;
    }

    private Result<Void> moveQuestionInternal(QuizQuestion question, Integer newOrder) {
        Result<Void> result = new Result<>();

        if (newOrder == null) {
            result.addMessage("Question order is required.", ResultType.INVALID);
            return result;
        }

        if (newOrder <= 0) {
            result.addMessage("Question order must be greater than zero.", ResultType.INVALID);
            return result;
        }

        int questionCount = quizQuestionRepository.findByQuizId(question.getQuizId()).size();

        if (newOrder > questionCount) {
            result.addMessage("Question order cannot be greater than the number of questions in the quiz.", ResultType.INVALID);
            return result;
        }

        if (newOrder.equals(question.getQuestionOrder())) {
            return result;
        }

        if (!quizQuestionRepository.updateOrder(question.getId(), 0)) {
            throw new IllegalStateException("Could not temporarily move quiz question for reordering.");
        }

        shiftQuestionOrdersBackward(question.getQuizId(), question.getQuestionOrder());
        shiftQuestionOrdersForward(question.getQuizId(), newOrder);

        if (!quizQuestionRepository.updateOrder(question.getId(), newOrder)) {
            throw new IllegalStateException("Could not finish quiz question reordering.");
        }

        return result;
    }

    private int getNextQuestionOrder(Long quizId) {
        return quizQuestionRepository.findByQuizId(quizId).stream()
                .map(QuizQuestion::getQuestionOrder)
                .filter(order -> order != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private void shiftQuestionOrdersForward(Long quizId, Integer startingOrder) {
        quizQuestionRepository.findByQuizId(quizId).stream()
                .filter(question -> question.getQuestionOrder() != null)
                .filter(question -> question.getQuestionOrder() >= startingOrder)
                .sorted(Comparator.comparing(QuizQuestion::getQuestionOrder).reversed())
                .forEach(question -> {
                    boolean success = quizQuestionRepository.updateOrder(
                            question.getId(),
                            question.getQuestionOrder() + 1
                    );

                    if (!success) {
                        throw new IllegalStateException("Could not shift quiz question orders forward.");
                    }
                });
    }

    private void shiftQuestionOrdersBackward(Long quizId, Integer startingOrder) {
        quizQuestionRepository.findByQuizId(quizId).stream()
                .filter(question -> question.getQuestionOrder() != null)
                .filter(question -> question.getQuestionOrder() > startingOrder)
                .sorted(Comparator.comparing(QuizQuestion::getQuestionOrder))
                .forEach(question -> {
                    boolean success = quizQuestionRepository.updateOrder(
                            question.getId(),
                            question.getQuestionOrder() - 1
                    );

                    if (!success) {
                        throw new IllegalStateException("Could not shift quiz question orders backward.");
                    }
                });
    }

    private Result<Void> moveOptionInternal(QuizAnswerOption option, Integer newOrder) {
        Result<Void> result = new Result<>();

        if (newOrder == null) {
            result.addMessage("Option order is required.", ResultType.INVALID);
            return result;
        }

        if (newOrder <= 0) {
            result.addMessage("Option order must be greater than zero.", ResultType.INVALID);
            return result;
        }

        int optionCount = quizAnswerOptionRepository.findByQuestionId(option.getQuestionId()).size();

        if (newOrder > optionCount) {
            result.addMessage("Option order cannot be greater than the number of options for the question.", ResultType.INVALID);
            return result;
        }

        if (newOrder.equals(option.getOptionOrder())) {
            return result;
        }

        if (!quizAnswerOptionRepository.updateOrder(option.getId(), 0)) {
            throw new IllegalStateException("Could not temporarily move quiz answer option for reordering.");
        }

        shiftOptionOrdersBackward(option.getQuestionId(), option.getOptionOrder());
        shiftOptionOrdersForward(option.getQuestionId(), newOrder);

        if (!quizAnswerOptionRepository.updateOrder(option.getId(), newOrder)) {
            throw new IllegalStateException("Could not finish quiz answer option reordering.");
        }

        return result;
    }

    private int getNextOptionOrder(Long questionId) {
        return quizAnswerOptionRepository.findByQuestionId(questionId).stream()
                .map(QuizAnswerOption::getOptionOrder)
                .filter(order -> order != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private void shiftOptionOrdersForward(Long questionId, Integer startingOrder) {
        quizAnswerOptionRepository.findByQuestionId(questionId).stream()
                .filter(option -> option.getOptionOrder() != null)
                .filter(option -> option.getOptionOrder() >= startingOrder)
                .sorted(Comparator.comparing(QuizAnswerOption::getOptionOrder).reversed())
                .forEach(option -> {
                    boolean success = quizAnswerOptionRepository.updateOrder(
                            option.getId(),
                            option.getOptionOrder() + 1
                    );

                    if (!success) {
                        throw new IllegalStateException("Could not shift quiz answer option orders forward.");
                    }
                });
    }

    private void shiftOptionOrdersBackward(Long questionId, Integer startingOrder) {
        quizAnswerOptionRepository.findByQuestionId(questionId).stream()
                .filter(option -> option.getOptionOrder() != null)
                .filter(option -> option.getOptionOrder() > startingOrder)
                .sorted(Comparator.comparing(QuizAnswerOption::getOptionOrder))
                .forEach(option -> {
                    boolean success = quizAnswerOptionRepository.updateOrder(
                            option.getId(),
                            option.getOptionOrder() - 1
                    );

                    if (!success) {
                        throw new IllegalStateException("Could not shift quiz answer option orders backward.");
                    }
                });
    }

    private <T> void validate(T model, Result<?> result) {
        Set<ConstraintViolation<T>> violations = validator.validate(model);

        for (ConstraintViolation<T> violation : violations) {
            result.addMessage(violation.getMessage(), ResultType.INVALID);
        }
    }

    private boolean requireId(Long id, String message, Result<?> result) {
        if (id == null) {
            result.addMessage(message, ResultType.INVALID);
            return false;
        }

        return true;
    }

    private void copyMessages(Result<?> source, Result<?> target) {
        for (String message : source.getMessages()) {
            target.addMessage(message, source.getType());
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}