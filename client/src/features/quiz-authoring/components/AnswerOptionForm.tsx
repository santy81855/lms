import { useState, type FormEvent } from "react";

import type { QuizAnswerOptionFormData } from "../types/quizAuthoringTypes";

import styles from "./AnswerOptionForm.module.css";

const defaultValues: QuizAnswerOptionFormData = {
    optionText: "",
    optionOrder: null,
    correct: false,
};

type AnswerOptionFormProps = {
    submitLabel: string;
    initialValues?: QuizAnswerOptionFormData;
    errorMessage?: string;
    isSubmitting?: boolean;
    onSubmit: (data: QuizAnswerOptionFormData) => void | Promise<void>;
};

export function AnswerOptionForm({
    submitLabel,
    initialValues = defaultValues,
    errorMessage,
    isSubmitting = false,
    onSubmit,
}: AnswerOptionFormProps) {
    const [optionText, setOptionText] = useState(initialValues.optionText);
    const [optionOrder, setOptionOrder] = useState(
        initialValues.optionOrder?.toString() ?? ""
    );
    const [correct, setCorrect] = useState(initialValues.correct);

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        await onSubmit({
            optionText,
            optionOrder: optionOrder.trim() === "" ? null : Number(optionOrder),
            correct,
        });
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="optionText">
                    Option text
                </label>

                <textarea
                    className={styles.textarea}
                    id="optionText"
                    name="optionText"
                    value={optionText}
                    disabled={isSubmitting}
                    onChange={(event) => setOptionText(event.target.value)}
                    rows={4}
                    placeholder="Write the answer option students can choose."
                    required
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="optionOrder">
                    Option order
                </label>

                <input
                    className={styles.input}
                    id="optionOrder"
                    name="optionOrder"
                    type="number"
                    min={1}
                    step={1}
                    value={optionOrder}
                    disabled={isSubmitting}
                    onChange={(event) => setOptionOrder(event.target.value)}
                    placeholder="Leave blank to add to the end"
                />

                <p className={styles.helpText}>
                    Optional. Leave blank to place this option after the current
                    last option.
                </p>
            </div>

            <label className={styles.checkboxRow}>
                <input
                    type="checkbox"
                    checked={correct}
                    disabled={isSubmitting}
                    onChange={(event) => setCorrect(event.target.checked)}
                />

                <span>Mark this option as correct</span>
            </label>

            {errorMessage && (
                <p className={styles.errorMessage}>{errorMessage}</p>
            )}

            <button
                className={styles.submitButton}
                type="submit"
                disabled={isSubmitting}
            >
                {isSubmitting ? "Saving..." : submitLabel}
            </button>
        </form>
    );
}
