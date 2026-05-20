import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { createQuiz } from "../api/teacherContentApi";
import { QuizForm } from "../components/QuizForm";
import type { QuizFormData } from "../types/contentTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./CreateQuizPage.module.css";

export function CreateQuizPage() {
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

    async function handleCreateQuiz(data: QuizFormData) {
        if (!isValidRoute) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const createdQuiz = await createQuiz(parsedModuleId, data);

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${createdQuiz.id}`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while creating the quiz."
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
                                Teacher quizzes
                            </p>
                            <h1>Create quiz</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Create the quiz shell first. Next, you will add
                            questions and answer options.
                        </p>

                        <QuizForm
                            submitLabel="Create quiz"
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleCreateQuiz}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
