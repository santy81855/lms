import { Link } from "react-router";
import type { QuizQuestionFeedback } from "../types/quizTakingTypes";
import styles from "./LessonReferenceFeedback.module.css"

type LessonReferenceFeedbackProps = {
    content: QuizQuestionFeedback;
}

export function LessonReferenceFeedback( { content } : LessonReferenceFeedbackProps){

    return (
        <div className={styles.feedback}>
        <Link  to={content.feedback}>
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
    );
}