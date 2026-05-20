import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { createLesson } from "../api/teacherContentApi";
import { LessonForm } from "../components/LessonForm";
import type { LessonFormData } from "../types/contentTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./CreateLessonPage.module.css";

export function CreateLessonPage() {
    const navigate = useNavigate();
    const { courseId, moduleId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedModuleId = Number(moduleId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidModuleId =
        Number.isInteger(parsedModuleId) && parsedModuleId > 0;
    const isValidRoute = isValidCourseId && isValidModuleId;

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleCreateLesson(data: LessonFormData) {
        if (!isValidRoute) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            await createLesson(parsedModuleId, data);

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}`,
                {
                    replace: true,
                }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while creating the lesson."
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
                        isValidRoute
                            ? `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}`
                            : "/teacher"
                    }
                >
                    Back to module
                </Link>

                {!isValidRoute && (
                    <p className={styles.errorMessage}>
                        Invalid course or module id.
                    </p>
                )}

                {isValidRoute && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>
                                Teacher content
                            </p>
                            <h1>Create lesson</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Add instructional content to this module. The lesson
                            will be saved as a draft.
                        </p>

                        <LessonForm
                            submitLabel="Create lesson"
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleCreateLesson}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
