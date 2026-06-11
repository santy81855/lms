package lms.server.models.dtos;

import lms.server.models.QuizQuestion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizSubmissionFeedbackItemTest {

    @Test
    void fromQuizQuestionShouldMapQuestionFields() {
        QuizQuestion question = new QuizQuestion();
        question.setQuestionOrder(2);
        question.setQuestionText("What is the square root of 16?");
        question.setAssociatedLessonId(7L);

        QuizSubmissionFeedbackItem item =
                QuizSubmissionFeedbackItem.fromQuizQuestion(question);

        assertEquals(2, item.getQuestionNumber());
        assertEquals("What is the square root of 16?", item.getQuestionContents());
        assertEquals(7L, item.getAssociatedLessonId());
    }

    @Test
    void fromQuizQuestionShouldAllowNullAssociatedLessonId() {
        QuizQuestion question = new QuizQuestion();
        question.setQuestionOrder(1);
        question.setQuestionText("What is 2 + 2?");
        question.setAssociatedLessonId(null);

        QuizSubmissionFeedbackItem item =
                QuizSubmissionFeedbackItem.fromQuizQuestion(question);

        assertEquals(1, item.getQuestionNumber());
        assertEquals("What is 2 + 2?", item.getQuestionContents());
        assertEquals(null, item.getAssociatedLessonId());
    }
}