import { useState, type FormEvent } from "react";

import type { GradeLevel } from "@/features/teacher-courses";

import type { AiCourseGenerationFormData } from "../types/aiCourseGenerationTypes";

import styles from "./AiCourseGenerationForm.module.css";

const gradeLevelOptions: { value: GradeLevel; label: string }[] = [
    { value: "ELEMENTARY", label: "Elementary" },
    { value: "MIDDLE_SCHOOL", label: "Middle school" },
    { value: "HIGH_SCHOOL", label: "High school" },
    { value: "UNIVERSITY", label: "University" },
    { value: "OTHER", label: "Other" },
];

type AiCourseGenerationFormProps = {
    errorMessage?: string;
    isSubmitting?: boolean;
    onSubmit: (data: AiCourseGenerationFormData) => void | Promise<void>;
};

export function AiCourseGenerationForm({
    errorMessage,
    isSubmitting = false,
    onSubmit,
}: AiCourseGenerationFormProps) {
    const [title, setTitle] = useState("");
    const [subject, setSubject] = useState("");
    const [gradeLevel, setGradeLevel] = useState<GradeLevel>("HIGH_SCHOOL");
    const [description, setDescription] = useState("");
    const [syllabusText, setSyllabusText] = useState("");
    const [moduleCount, setModuleCount] = useState("5");
    const [includeQuizzes, setIncludeQuizzes] = useState(true);

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        await onSubmit({
            title,
            subject,
            gradeLevel,
            description,
            syllabusText,
            moduleCount: Number(moduleCount),
            includeAssignments: false,
            includeQuizzes,
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

            <div className={styles.inlineFields}>
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
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="description">
                    Course description
                </label>

                <textarea
                    className={styles.textarea}
                    id="description"
                    name="description"
                    value={description}
                    disabled={isSubmitting}
                    onChange={(event) => setDescription(event.target.value)}
                    rows={4}
                    placeholder="Optional overview for the course."
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="syllabusText">
                    Syllabus text
                </label>

                <textarea
                    className={styles.largeTextarea}
                    id="syllabusText"
                    name="syllabusText"
                    value={syllabusText}
                    disabled={isSubmitting}
                    onChange={(event) => setSyllabusText(event.target.value)}
                    rows={14}
                    placeholder="Paste the syllabus, course outline, standards, topics, weekly plan, or learning objectives here."
                    required
                />
            </div>

            <div className={styles.inlineFields}>
                <div className={styles.fieldGroup}>
                    <label className={styles.label} htmlFor="moduleCount">
                        Number of modules
                    </label>

                    <input
                        className={styles.input}
                        id="moduleCount"
                        name="moduleCount"
                        type="number"
                        min={1}
                        max={20}
                        step={1}
                        value={moduleCount}
                        disabled={isSubmitting}
                        onChange={(event) => setModuleCount(event.target.value)}
                        required
                    />
                </div>

                <label className={styles.checkboxCard}>
                    <input
                        type="checkbox"
                        checked={includeQuizzes}
                        disabled={isSubmitting}
                        onChange={(event) =>
                            setIncludeQuizzes(event.target.checked)
                        }
                    />

                    <span>Generate quizzes too</span>
                </label>
            </div>

            <p className={styles.helpText}>
                Assignments are skipped for this MVP, so AI generation will
                create courses, modules, lessons, and optionally quizzes.
            </p>

            {errorMessage && (
                <p className={styles.errorMessage}>{errorMessage}</p>
            )}

            <button
                className={styles.submitButton}
                type="submit"
                disabled={isSubmitting}
            >
                {isSubmitting ? "Generating course..." : "Generate course"}
            </button>
        </form>
    );
}
