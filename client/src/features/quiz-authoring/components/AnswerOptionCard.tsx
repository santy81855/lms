import { Link } from "react-router";

import type { QuizAnswerOption } from "../types/quizAuthoringTypes";

import styles from "./AnswerOptionCard.module.css";

type AnswerOptionCardProps = {
    courseId: number;
    moduleId: number;
    quizId: number;
    questionId: number;
    option: QuizAnswerOption;
    isRunningAction?: boolean;
    onDelete: (optionId: number) => void | Promise<void>;
};

export function AnswerOptionCard({
    courseId,
    moduleId,
    quizId,
    questionId,
    option,
    isRunningAction = false,
    onDelete,
}: AnswerOptionCardProps) {
    return (
        <article className={styles.card}>
            <div className={styles.header}>
                <div>
                    <p className={styles.meta}>Option {option.optionOrder}</p>
                    <h3 className={styles.title}>{option.optionText}</h3>
                </div>

                <span
                    className={
                        option.correct ? styles.correctBadge : styles.badge
                    }
                >
                    {option.correct ? "Correct" : "Incorrect"}
                </span>
            </div>

            <div className={styles.actions}>
                <Link
                    className={styles.secondaryButton}
                    to={`/teacher/courses/${courseId}/modules/${moduleId}/quizzes/${quizId}/questions/${questionId}/options/${option.id}/edit`}
                >
                    Edit option
                </Link>

                <button
                    className={styles.dangerButton}
                    type="button"
                    disabled={isRunningAction}
                    onClick={() => onDelete(option.id)}
                >
                    Delete
                </button>
            </div>
        </article>
    );
}
