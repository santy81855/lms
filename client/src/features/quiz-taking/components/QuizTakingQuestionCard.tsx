import type { StudentQuizQuestion } from "../types/quizTakingTypes";
import { QuizOptionChoice } from "./QuizOptionChoice";

import styles from "./QuizTakingQuestionCard.module.css";

type QuizTakingQuestionCardProps = {
    question: StudentQuizQuestion;
    selectedOptionId?: number;
    shortAnswerText?: string;
    disabled?: boolean;
    onSelectOption: (questionId: number, optionId: number) => void;
    onChangeShortAnswer: (questionId: number, value: string) => void;
};

function formatQuestionType(questionType: StudentQuizQuestion["questionType"]) {
    return questionType
        .split("_")
        .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
        .join(" ");
}

export function QuizTakingQuestionCard({
    question,
    selectedOptionId,
    shortAnswerText = "",
    disabled = false,
    onSelectOption,
    onChangeShortAnswer,
}: QuizTakingQuestionCardProps) {
    const isShortAnswer = question.questionType === "SHORT_ANSWER";

    return (
        <article className={styles.card}>
            <div className={styles.header}>
                <div>
                    <p className={styles.meta}>
                        Question {question.questionOrder} ·{" "}
                        {formatQuestionType(question.questionType)}
                    </p>

                    <h3 className={styles.title}>{question.questionText}</h3>
                </div>

                <span className={styles.points}>{question.points} pts</span>
            </div>

            {isShortAnswer ? (
                <div className={styles.fieldGroup}>
                    <label
                        className={styles.label}
                        htmlFor={`question-${question.id}`}
                    >
                        Your answer
                    </label>

                    <textarea
                        className={styles.textarea}
                        id={`question-${question.id}`}
                        value={shortAnswerText}
                        disabled={disabled}
                        onChange={(event) =>
                            onChangeShortAnswer(question.id, event.target.value)
                        }
                        rows={4}
                        required
                    />
                </div>
            ) : (
                <div className={styles.optionList}>
                    {question.options.map((option) => (
                        <QuizOptionChoice
                            key={option.id}
                            option={option}
                            name={`question-${question.id}`}
                            checked={selectedOptionId === option.id}
                            disabled={disabled}
                            onChange={(optionId) =>
                                onSelectOption(question.id, optionId)
                            }
                        />
                    ))}
                </div>
            )}
        </article>
    );
}
