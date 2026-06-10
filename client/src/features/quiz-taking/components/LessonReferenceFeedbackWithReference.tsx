import styles from "./QuizFeedbackCard.module.css"
import { Link } from "react-router";
import type { QuizQuestionFeedback } from "../types/quizTakingTypes";

type LessonReferenceFeedbackProps = {
    content: QuizQuestionFeedback;
}

export function LessonReferenceFeedbackWithReference({ content } : LessonReferenceFeedbackProps){
    return (
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
    );
}