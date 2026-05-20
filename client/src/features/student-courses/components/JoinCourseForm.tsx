import { useState, type FormEvent } from "react";

import type { JoinCourseFormData } from "../types/studentCourseTypes";

import styles from "./JoinCourseForm.module.css";

type JoinCourseFormProps = {
    errorMessage?: string;
    successMessage?: string;
    isSubmitting?: boolean;
    onSubmit: (data: JoinCourseFormData) => void | Promise<void>;
};

export function JoinCourseForm({
    errorMessage,
    successMessage,
    isSubmitting = false,
    onSubmit,
}: JoinCourseFormProps) {
    const [joinCode, setJoinCode] = useState("");

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        await onSubmit({
            joinCode: joinCode.trim(),
        });

        setJoinCode("");
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="joinCode">
                    Course join code
                </label>

                <input
                    className={styles.input}
                    id="joinCode"
                    name="joinCode"
                    type="text"
                    value={joinCode}
                    disabled={isSubmitting}
                    onChange={(event) => setJoinCode(event.target.value)}
                    placeholder="Enter join code"
                    required
                />
            </div>

            {errorMessage && (
                <p className={styles.errorMessage}>{errorMessage}</p>
            )}
            {successMessage && (
                <p className={styles.successMessage}>{successMessage}</p>
            )}

            <button
                className={styles.submitButton}
                type="submit"
                disabled={isSubmitting}
            >
                {isSubmitting ? "Joining..." : "Join course"}
            </button>
        </form>
    );
}
