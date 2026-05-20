import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { createCourseModule } from "../api/teacherModuleApi";
import { ModuleForm } from "../components/ModuleForm";
import type { ModuleFormData } from "../types/moduleTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./CreateModulePage.module.css";

export function CreateModulePage() {
    const navigate = useNavigate();
    const { courseId } = useParams();

    const parsedCourseId = Number(courseId);
    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleCreateModule(data: ModuleFormData) {
        if (!isValidCourseId) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            await createCourseModule(parsedCourseId, data);
            navigate(`/teacher/courses/${parsedCourseId}`, { replace: true });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while creating the module."
                );
            }
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <Link
                    className={pageStyles.secondaryLink}
                    to={
                        isValidCourseId
                            ? `/teacher/courses/${parsedCourseId}`
                            : "/teacher"
                    }
                >
                    Back to course
                </Link>

                {!isValidCourseId && (
                    <p className={styles.errorMessage}>Invalid course id.</p>
                )}

                {isValidCourseId && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>
                                Teacher modules
                            </p>
                            <h1>Create module</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Add a new module to organize lessons, assignments,
                            and quizzes within this course.
                        </p>

                        <ModuleForm
                            submitLabel="Create module"
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleCreateModule}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
