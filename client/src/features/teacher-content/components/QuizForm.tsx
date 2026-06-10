import { useState, type FormEvent } from "react";

import type { QuizFormData } from "../types/contentTypes";

import styles from "./QuizForm.module.css";

const defaultValues: QuizFormData = {
    title: "",
    description: "",
    quizOrder: null,
    maxPoints: 10,
    timeLimitMinutes: null,
    attemptsAllowed: 1,
    feedbackTypeCode: "score",
};

type feedbackTooltip = {key: string, tooltip: string, dropdownText: string};

type QuizFormProps = {
    submitLabel: string;
    initialValues?: QuizFormData;
    errorMessage?: string;
    isSubmitting?: boolean;
    onSubmit: (data: QuizFormData) => void | Promise<void>;
};

export function QuizForm({
    submitLabel,
    initialValues = defaultValues,
    errorMessage,
    isSubmitting = false,
    onSubmit,
}: QuizFormProps) {
    const [title, setTitle] = useState(initialValues.title);
    const [description, setDescription] = useState(initialValues.description);
    const [quizOrder, setQuizOrder] = useState(
        initialValues.quizOrder?.toString() ?? ""
    );
    const [maxPoints, setMaxPoints] = useState(
        initialValues.maxPoints.toString()
    );
    const [timeLimitMinutes, setTimeLimitMinutes] = useState(
        initialValues.timeLimitMinutes?.toString() ?? ""
    );
    const [attemptsAllowed, setAttemptsAllowed] = useState(
        initialValues.attemptsAllowed.toString()
    );
    const [feedbackType, setFeedbackType] = useState("score");

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        await onSubmit({
            title,
            description,
            quizOrder: quizOrder.trim() === "" ? null : Number(quizOrder),
            maxPoints: Number(maxPoints),
            timeLimitMinutes:
                timeLimitMinutes.trim() === ""
                    ? null
                    : Number(timeLimitMinutes),
            attemptsAllowed: Number(attemptsAllowed),
            feedbackTypeCode: feedbackType,
        });
    }

    const feedbackOptions: feedbackTooltip[] = [
            {
                key: "score",
                tooltip: "show the students score when they submit the quiz in the form: score / maximum score.",
                dropdownText: "score (default)"
            },
            {
                key: "noFeedback",
                tooltip: "don't show the students any feedback when they submit the quiz.",
                dropdownText: "No Feedback"
            },
            {
                key: "lessonReference",
                tooltip: "Show the students what questions they got right and wrong, and provide a link to the associated lesson module.",
                dropdownText: "Reference Course Content"
            }, 
            {
                key: "aiOverview",
                tooltip: "Show the students all submitted questions with the correct answers.  Provide feedback on incorrect responses with an AI overview.",
                dropdownText: "AI Overview"
            }
        ];

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="title">
                    Quiz title
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
                <label className={styles.label} htmlFor="description">
                    Description
                </label>

                <textarea
                    className={styles.textarea}
                    id="description"
                    name="description"
                    value={description}
                    disabled={isSubmitting}
                    onChange={(event) => setDescription(event.target.value)}
                    rows={5}
                    placeholder="Briefly describe what this quiz covers."
                />
            </div>

            <div className={styles.inlineFields}>
                <div className={styles.fieldGroup}>
                    <label className={styles.label} htmlFor="quizOrder">
                        Quiz order
                    </label>

                    <input
                        className={styles.input}
                        id="quizOrder"
                        name="quizOrder"
                        type="number"
                        min={1}
                        step={1}
                        value={quizOrder}
                        disabled={isSubmitting}
                        onChange={(event) => setQuizOrder(event.target.value)}
                        placeholder="Add to end"
                    />

                    <p className={styles.helpText}>Optional.</p>
                </div>

                <div className={styles.fieldGroup}>
                    <label className={styles.label} htmlFor="maxPoints">
                        Max points
                    </label>

                    <input
                        className={styles.input}
                        id="maxPoints"
                        name="maxPoints"
                        type="number"
                        min={0}
                        step={0.01}
                        value={maxPoints}
                        disabled={isSubmitting}
                        onChange={(event) => setMaxPoints(event.target.value)}
                        required
                    />
                </div>
            </div>

            <div className={styles.inlineFields}>
                <div className={styles.fieldGroup}>
                    <label className={styles.label} htmlFor="timeLimitMinutes">
                        Time limit minutes
                    </label>

                    <input
                        className={styles.input}
                        id="timeLimitMinutes"
                        name="timeLimitMinutes"
                        type="number"
                        min={0}
                        step={1}
                        value={timeLimitMinutes}
                        disabled={isSubmitting}
                        onChange={(event) =>
                            setTimeLimitMinutes(event.target.value)
                        }
                        placeholder="No limit"
                    />

                    <p className={styles.helpText}>Optional.</p>
                </div>

                <div className={styles.fieldGroup}>
                    <label className={styles.label} htmlFor="attemptsAllowed">
                        Attempts allowed
                    </label>

                    <input
                        className={styles.input}
                        id="attemptsAllowed"
                        name="attemptsAllowed"
                        type="number"
                        min={1}
                        step={1}
                        value={attemptsAllowed}
                        disabled={isSubmitting}
                        onChange={(event) =>
                            setAttemptsAllowed(event.target.value)
                        }
                        required
                    />
                </div>

                <div className="styles.fieldGroup">
                    <label className={styles.label} htmlFor="feedbackType">
                        Quiz Feedback
                    </label>

                    <select 
                        className={styles.input} 
                        onChange={(e) => setFeedbackType(e.target.value)}
                        >
                        {feedbackOptions.map(tooltip => {
                            return (
                            <option value={tooltip.key}>
                                {tooltip.dropdownText}
                            </option>)
                        })}

                    </select>

                    <p className={styles.helpText}>
                        {
                            feedbackOptions.find(t => t.key === feedbackType)?.tooltip
                        }
                    </p>
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
