import type { StudentQuizResult } from "../types/quizTakingTypes";
import { formatTimeAgo } from "../lib/formattingHelper";
import { QuizFeedbackAccordion } from "./QuizFeedbackAccordion";
import styles from "./QuizResultCard.module.css";
import type { Quiz } from "@/features/teacher-content";

type QuizResultCardProps = {
    result: StudentQuizResult;
    quizMetaData: Quiz;
};

export function QuizResultCard({ result, quizMetaData }: QuizResultCardProps) {
    const percentage =
        result.maxScore === 0
            ? 0
            : Math.round((result.score / result.maxScore) * 100);
    
            return (
        <article className={styles.card}>
            <div className={styles.titleContainer}>
                <p className={styles.eyebrow}>Attempt {result.attemptNumber}</p>
                <p className={styles.date}>
                    {formatTimeAgo(result.submittedAt?.toLocaleString())}
                </p>
            </div>

            {
                quizMetaData.feedbackTypeCode !== "NO_CONTENT" && 
                <>
                <h2>
                    Score: {result.score} / {result.maxScore}
                </h2>
                <p className={styles.percentage}>{percentage}%</p>
                </>
            }


            <dl className={styles.detailList}>
                <div>
                    <dt>Submission ID</dt>
                    <dd>{result.submissionId}</dd>
                </div>

                <div>
                    <dt>Quiz ID</dt>
                    <dd>{result.quizId}</dd>
                </div>
            </dl>
            {
                quizMetaData.feedbackTypeCode !== "NO_CONTENT" &&
                quizMetaData.feedbackTypeCode !== "SCORE" &&
                <QuizFeedbackAccordion result={result}/>
            }
        </article>
    );
}
