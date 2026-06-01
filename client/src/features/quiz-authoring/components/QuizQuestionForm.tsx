import { useEffect, useState, type FormEvent } from "react";

import type {
    QuestionType,
    QuizQuestionFormData,
} from "../types/quizAuthoringTypes";

import styles from "./QuizQuestionForm.module.css";

const questionTypeOptions: { value: QuestionType; label: string }[] = [
    { value: "MULTIPLE_CHOICE", label: "Multiple choice" },
    { value: "TRUE_FALSE", label: "True / false" },
    { value: "SHORT_ANSWER", label: "Short answer" },
];

const defaultValues: QuizQuestionFormData = {
    questionText: "",
    questionType: "MULTIPLE_CHOICE",
    questionOrder: null,
    points: 1,
    explanation: "",
};

type QuizQuestionFormProps = {
    submitLabel: string;
    initialValues?: QuizQuestionFormData;
    errorMessage?: string;
    isSubmitting?: boolean;
    onSubmit: (data: QuizQuestionFormData) => void | Promise<void>;
};

export function QuizQuestionForm({
    submitLabel,
    initialValues = defaultValues,
    errorMessage,
    isSubmitting = false,
    onSubmit,
}: QuizQuestionFormProps) {
    const [questionText, setQuestionText] = useState(
        initialValues.questionText
    );
    const [questionType, setQuestionType] = useState<QuestionType>(
        initialValues.questionType
    );
    const [questionOrder, setQuestionOrder] = useState(
        initialValues.questionOrder?.toString() ?? ""
    );
    const [points, setPoints] = useState(initialValues.points.toString());
    const [lessons, setLessons] = useState([]);

    // load the lessons that a teacher can link to this quiz question
    useEffect(() => {

    }, [])

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        await onSubmit({
            questionText,
            questionType,
            questionOrder:
                questionOrder.trim() === "" ? null : Number(questionOrder),
            points: Number(points),
            // teacher-generated explanations are not yet implemented, but the
            // project  already stubs out the data types, HTML, and
            // helper methods to use them.  While the current backlog 
            // does not include teacher-generated explanations, it is reasonable 
            // to assume it may in the near future.  As such, I will leave in
            // the explanation in this form submission, as removing it would
            // also require refactoring typescript data types and the backend to omit this
            // data -- which would subsequently need to be refactored back in
            // when we decide to implement it.
            explanation: "",
        });
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="questionText">
                    Question text
                </label>

                <textarea
                    className={styles.textarea}
                    id="questionText"
                    name="questionText"
                    value={questionText}
                    disabled={isSubmitting}
                    onChange={(event) => setQuestionText(event.target.value)}
                    rows={5}
                    placeholder="Write the question students will answer."
                    required
                />
            </div>

            <div className={styles.inlineFields}>
                <div className={styles.fieldGroup}>
                    <label className={styles.label} htmlFor="questionType">
                        Question type
                    </label>

                    <select
                        className={styles.input}
                        id="questionType"
                        name="questionType"
                        value={questionType}
                        disabled={isSubmitting}
                        onChange={(event) =>
                            setQuestionType(event.target.value as QuestionType)
                        }
                        required
                    >
                        {questionTypeOptions.map((option) => (
                            <option key={option.value} value={option.value}>
                                {option.label}
                            </option>
                        ))}
                    </select>
                </div>

                <div className={styles.fieldGroup}>
                    <label className={styles.label} htmlFor="points">
                        Points
                    </label>

                    <input
                        className={styles.input}
                        id="points"
                        name="points"
                        type="number"
                        min={0}
                        step={0.01}
                        value={points}
                        disabled={isSubmitting}
                        onChange={(event) => setPoints(event.target.value)}
                        required
                    />
                </div>
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="questionOrder">
                    Question order
                </label>

                <input
                    className={styles.input}
                    id="questionOrder"
                    name="questionOrder"
                    type="number"
                    min={1}
                    step={1}
                    value={questionOrder}
                    disabled={isSubmitting}
                    onChange={(event) => setQuestionOrder(event.target.value)}
                    placeholder="Leave blank to add to the end"
                />

                <p className={styles.helpText}>
                    Optional. Leave blank to place this question after the
                    current last question.
                </p>
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="explanation">
                    Link to a lesson
                </label>

                <select className={styles.fieldGroup}>
                    <option className={styles.label}>option1</option>

                </select>
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
