package lms.server.models.dtos;

import lms.server.models.Quiz;
import lms.server.models.QuizFeedbackType;
import lms.server.models.QuizSubmission;

import java.math.BigDecimal;
import java.util.List;

public class QuizSubmissionFeedbackResponse {

    private QuizFeedbackType type;
    private QuizFeedbackQuizResponse quiz;
    private BigDecimal score;
    private BigDecimal maxScore;
    private List<QuizSubmissionFeedbackItem> content;

    public QuizSubmissionFeedbackResponse(QuizFeedbackType type,
                                          Quiz quiz,
                                          BigDecimal score,
                                          BigDecimal maxScore,
                                          List<QuizSubmissionFeedbackItem> content) {
        this.type = type;
        this.quiz = new QuizFeedbackQuizResponse(quiz);
        this.score = score;
        this.maxScore = maxScore;
        this.content = content;
    }

    public static QuizSubmissionFeedbackResponse noFeedback(Quiz quiz) {
        return new QuizSubmissionFeedbackResponse(
                QuizFeedbackType.NO_FEEDBACK,
                quiz,
                null,
                null,
                List.of()
        );
    }

    public static QuizSubmissionFeedbackResponse scoreOnly(Quiz quiz, QuizSubmission submission) {
        return new QuizSubmissionFeedbackResponse(
                QuizFeedbackType.SCORE,
                quiz,
                submission.getScore(),
                submission.getMaxScore(),
                List.of()
        );
    }

    public static QuizSubmissionFeedbackResponse lessonReferencePlaceholder(Quiz quiz, QuizSubmission submission) {
        return new QuizSubmissionFeedbackResponse(
                QuizFeedbackType.LESSON_REFERENCE,
                quiz,
                submission.getScore(),
                submission.getMaxScore(),
                List.of()
        );
    }

    public static QuizSubmissionFeedbackResponse aiOverviewPlaceholder(Quiz quiz, QuizSubmission submission) {
        return new QuizSubmissionFeedbackResponse(
                QuizFeedbackType.AI_OVERVIEW,
                quiz,
                submission.getScore(),
                submission.getMaxScore(),
                List.of()
        );
    }

    public static QuizSubmissionFeedbackResponse from(Quiz quiz, QuizSubmission submission) {
        QuizFeedbackType feedbackType = quiz.getFeedbackTypeOrDefault();

        return switch (feedbackType) {
            case NO_FEEDBACK -> noFeedback(quiz);
            case SCORE -> scoreOnly(quiz, submission);
            case LESSON_REFERENCE -> lessonReferencePlaceholder(quiz, submission);
            case AI_OVERVIEW -> aiOverviewPlaceholder(quiz, submission);
        };
    }

    public QuizFeedbackType getType() {
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