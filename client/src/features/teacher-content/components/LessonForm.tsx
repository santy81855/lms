import { useState, type FormEvent } from "react";

import type { LessonFormData } from "../types/contentTypes";

import styles from "./LessonForm.module.css";

const defaultValues: LessonFormData = {
    title: "",
    content: "",
    lessonOrder: null,
    estimatedMinutes: null,
};

type LessonFormProps = {
    submitLabel: string;
    initialValues?: LessonFormData;
    errorMessage?: string;
    isSubmitting?: boolean;
    onSubmit: (data: LessonFormData) => void | Promise<void>;
};

export function LessonForm({
    submitLabel,
    initialValues = defaultValues,
    errorMessage,
    isSubmitting = false,
    onSubmit,
}: LessonFormProps) {
    const [title, setTitle] = useState(initialValues.title);
    const [content, setContent] = useState(initialValues.content);
    const [lessonOrder, setLessonOrder] = useState(
        initialValues.lessonOrder?.toString() ?? ""
    );
    const [estimatedMinutes, setEstimatedMinutes] = useState(
        initialValues.estimatedMinutes?.toString() ?? ""
    );

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        await onSubmit({
            title,
            content,
            lessonOrder: lessonOrder.trim() === "" ? null : Number(lessonOrder),
            estimatedMinutes:
                estimatedMinutes.trim() === ""
                    ? null
                    : Number(estimatedMinutes),
        });
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="title">
                    Lesson title
                </label>

                <input
                    className={styles.input}
                    id="title"
                    name="title"
                    type="text"
                    value={title}
                    disabled={isSubmitting}
                    onChange={(event) => setTitle(event.target.value)}
                    required
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="content">
                    Lesson content
                </label>

                <textarea
                    className={styles.textarea}
                    id="content"
                    name="content"
                    value={content}
                    disabled={isSubmitting}
                    onChange={(event) => setContent(event.target.value)}
                    rows={10}
                    placeholder="Write the lesson body, notes, examples, instructions, or reading material."
                />
            </div>

            <div className={styles.inlineFields}>
                <div className={styles.fieldGroup}>
                    <label className={styles.label} htmlFor="lessonOrder">
                        Lesson order
                    </label>

                    <input
                        className={styles.input}
                        id="lessonOrder"
                        name="lessonOrder"
                        type="number"
                        min={1}
                        step={1}
                        value={lessonOrder}
                        disabled={isSubmitting}
                        onChange={(event) => setLessonOrder(event.target.value)}
                        placeholder="Add to end"
                    />

                    <p className={styles.helpText}>Optional.</p>
                </div>

                <div className={styles.fieldGroup}>
                    <label className={styles.label} htmlFor="estimatedMinutes">
                        Estimated minutes
                    </label>

                    <input
                        className={styles.input}
                        id="estimatedMinutes"
                        name="estimatedMinutes"
                        type="number"
                        min={0}
                        step={1}
                        value={estimatedMinutes}
                        disabled={isSubmitting}
                        onChange={(event) =>
                            setEstimatedMinutes(event.target.value)
                        }
                        placeholder="20"
                    />

                    <p className={styles.helpText}>Optional.</p>
                </div>
            </div>

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
