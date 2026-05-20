import type { StudentQuizOption } from "../types/quizTakingTypes";

import styles from "./QuizOptionChoice.module.css";

type QuizOptionChoiceProps = {
    option: StudentQuizOption;
    name: string;
    checked: boolean;
    disabled?: boolean;
    onChange: (optionId: number) => void;
};

export function QuizOptionChoice({
    option,
    name,
    checked,
    disabled = false,
    onChange,
}: QuizOptionChoiceProps) {
    return (
        <label className={styles.choice}>
            <input
                type="radio"
                name={name}
                checked={checked}
                disabled={disabled}
                onChange={() => onChange(option.id)}
            />

            <span>{option.optionText}</span>
        </label>
    );
}
