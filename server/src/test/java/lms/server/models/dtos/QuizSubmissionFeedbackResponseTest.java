package lms.server.models.dtos;

import lms.server.models.FeedbackTypeCodes;
import lms.server.models.Quiz;
import lms.server.models.QuizQuestion;
import lms.server.models.QuizSubmission;
import lms.server.models.QuizSubmissionAnswer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizSubmissionFeedbackResponseTest {

    @Test
    void lessonReferenceShouldIncludeOnlyIncorrectAnswers() {
        Quiz quiz = makeQuiz();

        QuizSubmission submission = new QuizSubmission();
        submission.setScore(new BigDecimal("5.00"));
        submission.setMaxScore(new BigDecimal("10.00"));

        QuizQuestion questionOne = makeQuestion(
                1L,
                1,
                "Question 1",
                101L
        );

        QuizQuestion questionTwo = makeQuestion(
                2L,
                2,
                "Question 2",
                102L
        );

        QuizSubmissionAnswer correctAnswer = makeAnswer(1L, true);
        QuizSubmissionAnswer wrongAnswer = makeAnswer(2L, false);

        QuizSubmissionFeedbackResponse response =
                QuizSubmissionFeedbackResponse.lessonReference(
                        quiz,
                        submission,
                        List.of(correctAnswer, wrongAnswer),
                        List.of(questionOne, questionTwo)
                );

        assertEquals(FeedbackTypeCodes.LESSON_REFERENCE, response.getType());
        assertEquals(new BigDecimal("5.00"), response.getScore());
        assertEquals(new BigDecimal("10.00"), response.getMaxScore());

        assertEquals(1, response.getContent().size());

        QuizSubmissionFeedbackItem item = response.getContent().get(0);

        assertEquals(2, item.getQuestionNumber());
        assertEquals("Question 2", item.getQuestionContents());
        assertEquals(102L, item.getAssociatedLessonId());
    }

    @Test
    void lessonReferenceShouldSortIncorrectQuestionsByQuestionOrder() {
        Quiz quiz = makeQuiz();

        QuizSubmission submission = new QuizSubmission();
        submission.setScore(new BigDecimal("0.00"));
        submission.setMaxScore(new BigDecimal("10.00"));

        QuizQuestion laterQuestion = makeQuestion(
                1L,
                3,
                "Later question",
                201L
        );

        QuizQuestion earlierQuestion = makeQuestion(
                2L,
                1,
                "Earlier question",
                202L
        );

        QuizSubmissionAnswer laterWrongAnswer = makeAnswer(1L, false);
        QuizSubmissionAnswer earlierWrongAnswer = makeAnswer(2L, false);

        QuizSubmissionFeedbackResponse response =
                QuizSubmissionFeedbackResponse.lessonReference(
                        quiz,
                        submission,
                        List.of(laterWrongAnswer, earlierWrongAnswer),
                        List.of(laterQuestion, earlierQuestion)
                );

        assertEquals(2, response.getContent().size());

        assertEquals(1, response.getContent().get(0).getQuestionNumber());
        assertEquals("Earlier question", response.getContent().get(0).getQuestionContents());
        assertEquals(202L, response.getContent().get(0).getAssociatedLessonId());

        assertEquals(3, response.getContent().get(1).getQuestionNumber());
        assertEquals("Later question", response.getContent().get(1).getQuestionContents());
        assertEquals(201L, response.getContent().get(1).getAssociatedLessonId());
    }

    @Test
    void lessonReferenceShouldIgnoreAnswerWhenQuestionIsMissing() {
        Quiz quiz = makeQuiz();

        QuizSubmission submission = new QuizSubmission();
        submission.setScore(new BigDecimal("0.00"));
        submission.setMaxScore(new BigDecimal("10.00"));

        QuizSubmissionAnswer wrongAnswerForMissingQuestion = makeAnswer(99L, false);

        QuizSubmissionFeedbackResponse response =
                QuizSubmissionFeedbackResponse.lessonReference(
                        quiz,
                        submission,
                        List.of(wrongAnswerForMissingQuestion),
                        List.of()
                );

        assertEquals(FeedbackTypeCodes.LESSON_REFERENCE, response.getType());
        assertEquals(0, response.getContent().size());
    }

    @Test
    void lessonReferenceShouldIncludeWrongQuestionEvenWhenAssociatedLessonIdIsNull() {
        Quiz quiz = makeQuiz();

        QuizSubmission submission = new QuizSubmission();
        submission.setScore(new BigDecimal("0.00"));
        submission.setMaxScore(new BigDecimal("10.00"));

        QuizQuestion question = makeQuestion(
                1L,
                1,
                "Question with no associated lesson",
                null
        );

        QuizSubmissionAnswer wrongAnswer = makeAnswer(1L, false);

        QuizSubmissionFeedbackResponse response =
                QuizSubmissionFeedbackResponse.lessonReference(
                        quiz,
                        submission,
                        List.of(wrongAnswer),
                        List.of(question)
                );

        assertEquals(1, response.getContent().size());
        assertEquals(1, response.getContent().get(0).getQuestionNumber());
        assertEquals("Question with no associated lesson", response.getContent().get(0).getQuestionContents());
        assertEquals(null, response.getContent().get(0).getAssociatedLessonId());
    }

    private Quiz makeQuiz() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);
        quiz.setTitle("Quiz 1");
        quiz.setDescription("Quiz description");
        quiz.setQuizOrder(1);
        quiz.setMaxPoints(new BigDecimal("10.00"));
        quiz.setTimeLimitMinutes(30);
        quiz.setAttemptsAllowed(2);
        quiz.setFeedbackTypeCode(FeedbackTypeCodes.LESSON_REFERENCE);
        return quiz;
    }

    private QuizQuestion makeQuestion(Long id,
                                      Integer questionOrder,
                                      String questionText,
                                      Long associatedLessonId) {
        QuizQuestion question = new QuizQuestion();
        question.setId(id);
        question.setQuestionOrder(questionOrder);
        question.setQuestionText(questionText);
        question.setAssociatedLessonId(associatedLessonId);
        return question;
    }

    private QuizSubmissionAnswer makeAnswer(Long questionId, Boolean correct) {
        QuizSubmissionAnswer answer = new QuizSubmissionAnswer();
        answer.setQuestionId(questionId);
        answer.setCorrect(correct);
        return answer;
    }
}