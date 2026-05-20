import { useState, type FormEvent } from "react";

import type { ModuleFormData } from "../types/moduleTypes";

import styles from "./ModuleForm.module.css";

const defaultValues: ModuleFormData = {
    title: "",
    description: "",
    moduleOrder: null,
};

type ModuleFormProps = {
    submitLabel: string;
    initialValues?: ModuleFormData;
    errorMessage?: string;
    isSubmitting?: boolean;
    onSubmit: (data: ModuleFormData) => void | Promise<void>;
};

export function ModuleForm({
    submitLabel,
    initialValues = defaultValues,
    errorMessage,
    isSubmitting = false,
    onSubmit,
}: ModuleFormProps) {
    const [title, setTitle] = useState(initialValues.title);
    const [description, setDescription] = useState(initialValues.description);
    const [moduleOrder, setModuleOrder] = useState(
        initialValues.moduleOrder?.toString() ?? ""
    );

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        await onSubmit({
            title,
            description,
            moduleOrder: moduleOrder.trim() === "" ? null : Number(moduleOrder),
        });
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="title">
                    Module title
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
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="moduleOrder">
                    Module order
                </label>

                <input
                    className={styles.input}
                    id="moduleOrder"
                    name="moduleOrder"
                    type="number"
                    min={1}
                    step={1}
                    value={moduleOrder}
                    disabled={isSubmitting}
                    onChange={(event) => setModuleOrder(event.target.value)}
                    placeholder="Leave blank to add to the end"
                />

                <p className={styles.helpText}>
                    Optional. Leave blank to place this module after the current
                    last module.
                </p>
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
