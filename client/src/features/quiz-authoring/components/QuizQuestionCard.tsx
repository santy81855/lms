import { Link } from "react-router";

import type { QuizQuestion } from "../types/quizAuthoringTypes";

import styles from "./QuizQuestionCard.module.css";

type QuizQuestionCardProps = {
    courseId: number;
    moduleId: number;
    quizId: number;
    question: QuizQuestion;
};

function formatQuestionType(questionType: QuizQuestion["questionType"]) {
    return questionType
        .split("_")
        .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
        .join(" ");
}

export function QuizQuestionCard({
    courseId,
    moduleId,
    quizId,
    question,
}: QuizQuestionCardProps) {
    return (
        <article className={styles.card}>
            <div className={styles.header}>
                <div className={styles.titleGroup}>
                    <p className={styles.meta}>
                        Question {question.questionOrder} ·{" "}
                        {formatQuestionType(question.questionType)}
                    </p>

                    <h3 className={styles.title}>{question.questionText}</h3>
                </div>

                <span className={styles.points}>{question.points} pts</span>
            </div>

            {question.explanation && (
                <p className={styles.explanation}>{question.explanation}</p>
            )}

            <Link
                className={styles.detailsLink}
                to={`/teacher/courses/${courseId}/modules/${moduleId}/quizzes/${quizId}/questions/${question.id}`}
            >
                View question
            </Link>
        </article>
    );
}
