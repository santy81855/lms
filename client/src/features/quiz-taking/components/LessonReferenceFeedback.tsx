import { Link } from "react-router";
import type { QuizQuestionFeedback } from "../types/quizTakingTypes";
import styles from "./QuizFeedbackCard.module.css"
import { useLocation } from "react-router";

type LessonReferenceFeedbackProps = {
    content: QuizQuestionFeedback;
}

export function LessonReferenceFeedback( { content } : LessonReferenceFeedbackProps){
    const location = useLocation();

    return (
    content.feedback !== null && content.feedback.length !== 0 ? 
        // happy path card to show to the student -- the teacher has added a link to the lesson
        <div className={styles.feedback}>
        <Link  to={content.feedback }>
            <h3 className={styles.question} >
                {content.questionNumber}) {content.questionText}
            </h3>
            <div className={styles.response}>
                <p>
                Incorrect response.  Click on this card to review the lesson material. 
                </p>
            </div>
        </Link>
        </div>
        :
        // card that gets shown if teacher forgets to add feedback
        <div className={styles.feedback}>
            <h3 className={styles.question} >
                {content.questionNumber}) {content.questionText}
            </h3>
            <div className={styles.response}>
                <p>
                Incorrect response.  Your teacher has not linked this quiz question to any lessons for review. 
                </p>
            </div>
        </div>
    );
}