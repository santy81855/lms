package lms.server.models.dtos;

import lms.server.models.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuizSubmissionFeedbackResponse {

    private String type;
    private QuizFeedbackQuizResponse quiz;
    private BigDecimal score;
    private BigDecimal maxScore;
    private List<QuizSubmissionFeedbackItem> content;

    public QuizSubmissionFeedbackResponse(String type,
                                          QuizFeedbackQuizResponse quiz,
                                          BigDecimal score,
                                          BigDecimal maxScore,
                                          List<QuizSubmissionFeedbackItem> content) {
        this.type = type;
        this.quiz = quiz;
        this.score = score;
        this.maxScore = maxScore;
        this.content = content;
    }

    public static QuizSubmissionFeedbackResponse noFeedback(Quiz quiz) {
        return new QuizSubmissionFeedbackResponse(
                FeedbackTypeCodes.NO_FEEDBACK,
                QuizFeedbackQuizResponse.from(quiz),
                null,
                null,
                List.of()
        );
    }

    public static QuizSubmissionFeedbackResponse scoreOnly(Quiz quiz, QuizSubmission submission) {
        return new QuizSubmissionFeedbackResponse(
                FeedbackTypeCodes.SCORE,
                QuizFeedbackQuizResponse.from(quiz),
                submission.getScore(),
                submission.getMaxScore(),
                List.of()
        );
    }

    public static QuizSubmissionFeedbackResponse lessonReference(Quiz quiz, QuizSubmission submission, List<QuizSubmissionAnswer> submissionAnswers, List<QuizQuestion> questions) {
        Map<Long, QuizQuestion> questionsById = questions.stream()
                .collect(Collectors.toMap(QuizQuestion::getId, Function.identity()));

        List<QuizSubmissionFeedbackItem> content = submissionAnswers.stream()
                .filter(answer -> !Boolean.TRUE.equals(answer.getCorrect()))
                .map(answer -> questionsById.get(answer.getQuestionId()))
                .filter(question -> question != null)
                .sorted(Comparator.comparing(QuizQuestion::getQuestionOrder))
                .map(QuizSubmissionFeedbackItem::fromQuizQuestion)
                .toList();

        return new QuizSubmissionFeedbackResponse(
                FeedbackTypeCodes.LESSON_REFERENCE,
                QuizFeedbackQuizResponse.from(quiz),
                submission.getScore(),
                submission.getMaxScore(),
                content
        );
    }

    public static QuizSubmissionFeedbackResponse aiOverviewPlaceholder(Quiz quiz, QuizSubmission submission) {
        return new QuizSubmissionFeedbackResponse(
                FeedbackTypeCodes.AI_OVERVIEW,
                QuizFeedbackQuizResponse.from(quiz),
                submission.getScore(),
                submission.getMaxScore(),
                List.of()
        );
    }

    /**
     * Builds the quiz feedback response for the quiz's configured feedback type.
     *
     * Required arguments depend on the quiz feedback type:
     *
     * <ul>
     *     <li>{@code NO_FEEDBACK}: requires {@code quiz}. {@code submission},
     *     {@code submissionAnswers}, and {@code questions} are ignored.</li>
     *
     *     <li>{@code SCORE}: requires {@code quiz} and {@code submission}.
     *     {@code submissionAnswers} and {@code questions} are ignored.</li>
     *
     *     <li>{@code LESSON_REFERENCE}: requires {@code quiz}, {@code submission},
     *     {@code submissionAnswers}, and {@code questions}. Wrong submission answers
     *     are matched to their quiz questions so the response can include each
     *     question's associated lesson id.</li>
     *
     *     <li>{@code AI_OVERVIEW}: requires {@code quiz} and {@code submission}.
     *     {@code submissionAnswers} and {@code questions}. Currently non-functional
     *     until AI feedback logic is implemented.</li>
     * </ul>
     *
     * @param quiz the quiz whose feedback type determines the response shape
     * @param submission the student's quiz submission; required for score-based feedback
     * @param submissionAnswers the submitted answers; required only for lesson-reference feedback
     * @param questions the quiz questions; required only for lesson-reference feedback
     * @return a feedback response matching the quiz's configured feedback type
     */

    public static QuizSubmissionFeedbackResponse from(Quiz quiz, QuizSubmission submission, List<QuizSubmissionAnswer> submissionAnswers, List<QuizQuestion> questions) {
        return switch (quiz.getFeedbackTypeCodeOrDefault()) {
            case "NO_FEEDBACK" -> noFeedback(quiz);
            case "SCORE" -> scoreOnly(quiz, submission);
            case "LESSON_REFERENCE" -> lessonReference(quiz, submission, submissionAnswers, questions);
            case "AI_OVERVIEW" -> aiOverviewPlaceholder(quiz, submission);
            default -> scoreOnly(quiz, submission);
        };
    }

    public String getType() {
        return type;
    }

    public QuizFeedbackQuizResponse getQuiz() {
        return quiz;
    }

    public BigDecimal getScore() {
        return score;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public List<QuizSubmissionFeedbackItem> getContent() {
        return content;
    }
}