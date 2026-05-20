import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { createQuizQuestion } from "../api/quizAuthoringApi";
import { QuizQuestionForm } from "../components/QuizQuestionForm";
import type { QuizQuestionFormData } from "../types/quizAuthoringTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./CreateQuestionPage.module.css";

export function CreateQuestionPage() {
    const navigate = useNavigate();
    const { courseId, moduleId, quizId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedModuleId = Number(moduleId);
    const parsedQuizId = Number(quizId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidModuleId =
        Number.isInteger(parsedModuleId) && parsedModuleId > 0;
    const isValidQuizId = Number.isInteger(parsedQuizId) && parsedQuizId > 0;
    const isValidRoute = isValidCourseId && isValidModuleId && isValidQuizId;

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleCreateQuestion(data: QuizQuestionFormData) {
        if (!isValidRoute) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const createdQuestion = await createQuizQuestion(
                parsedQuizId,
                data
            );

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}/questions/${createdQuestion.id}`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while creating the question."
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
                            ? `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}`
                            : "/teacher"
                    }
                >
                    Back to quiz
                </Link>

                {!isValidRoute && (
                    <p className={styles.errorMessage}>
                        Invalid course, module, or quiz id.
                    </p>
                )}

                {isValidRoute && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>Quiz authoring</p>
                            <h1>Create question</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Add a question to this quiz. You can add answer
                            options after the question is created.
                        </p>

                        <QuizQuestionForm
                            submitLabel="Create question"
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleCreateQuestion}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
