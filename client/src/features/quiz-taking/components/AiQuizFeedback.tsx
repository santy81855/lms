import type { QuizQuestionFeedback } from "../types/quizTakingTypes";
import styles from "./QuizFeedbackCard.module.css"

type AiQuizFeedbackProps = {
    content: QuizQuestionFeedback;
}
export function AiQuizFeedback({ content }: AiQuizFeedbackProps ){
    return (
        <div className={styles.feedback}>
            <h3 className={styles.question} >
                {content.questionNumber}) {content.questionText}
            </h3>
            <div className={styles.response}>
                <p>
                {content.feedback}
                </p>
            </div>
        </div>

    );
}