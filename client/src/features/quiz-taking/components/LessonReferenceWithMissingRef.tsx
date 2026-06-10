import type { QuizQuestionFeedback } from "../types/quizTakingTypes";
import styles from "./QuizFeedbackCard.module.css"

type LessonReferenceFeedbackProps = {
    content: QuizQuestionFeedback;
}

export function LessonReferenceWithMissingRef({ content } : LessonReferenceFeedbackProps){
    return (
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