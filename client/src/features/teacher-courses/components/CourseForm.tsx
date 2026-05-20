import { useState, type FormEvent } from "react";

import type { CourseFormData, GradeLevel } from "../types/courseTypes";

import styles from "./CourseForm.module.css";

const gradeLevelOptions: { value: GradeLevel; label: string }[] = [
    { value: "ELEMENTARY", label: "Elementary" },
    { value: "MIDDLE_SCHOOL", label: "Middle school" },
    { value: "HIGH_SCHOOL", label: "High school" },
    { value: "UNIVERSITY", label: "University" },
    { value: "OTHER", label: "Other" },
];

const defaultValues: CourseFormData = {
    title: "",
    subject: "",
    gradeLevel: "HIGH_SCHOOL",
    description: "",
};

type CourseFormProps = {
    submitLabel: string;
    initialValues?: CourseFormData;
    errorMessage?: string;
    isSubmitting?: boolean;
    onSubmit: (data: CourseFormData) => void | Promise<void>;
};

export function CourseForm({
    submitLabel,
    initialValues = defaultValues,
    errorMessage,
    isSubmitting = false,
    onSubmit,
}: CourseFormProps) {
    const [title, setTitle] = useState(initialValues.title);
    const [subject, setSubject] = useState(initialValues.subject);
    const [gradeLevel, setGradeLevel] = useState<GradeLevel>(
        initialValues.gradeLevel
    );
    const [description, setDescription] = useState(initialValues.description);

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        await onSubmit({
            title,
            subject,
            gradeLevel,
            description,
        });
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="title">
                    Course title
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
                <label className={styles.label} htmlFor="subject">
                    Subject
                </label>

                <input
                    className={styles.input}
                    id="subject"
                    name="subject"
                    type="text"
                    value={subject}
                    disabled={isSubmitting}
                    onChange={(event) => setSubject(event.target.value)}
                    placeholder="Math, Biology, History..."
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="gradeLevel">
                    Grade level
                </label>

                <select
                    className={styles.input}
                    id="gradeLevel"
                    name="gradeLevel"
                    value={gradeLevel}
                    disabled={isSubmitting}
                    onChange={(event) =>
                        setGradeLevel(event.target.value as GradeLevel)
                    }
                    required
                >
                    {gradeLevelOptions.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
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
                />
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
