import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { createQuizAnswerOption } from "../api/quizAuthoringApi";
import { AnswerOptionForm } from "../components/AnswerOptionForm";
import type { QuizAnswerOptionFormData } from "../types/quizAuthoringTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./CreateOptionPage.module.css";

export function CreateOptionPage() {
    const navigate = useNavigate();
    const { courseId, moduleId, quizId, questionId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedModuleId = Number(moduleId);
    const parsedQuizId = Number(quizId);
    const parsedQuestionId = Number(questionId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidModuleId =
        Number.isInteger(parsedModuleId) && parsedModuleId > 0;
    const isValidQuizId = Number.isInteger(parsedQuizId) && parsedQuizId > 0;
    const isValidQuestionId =
        Number.isInteger(parsedQuestionId) && parsedQuestionId > 0;

    const isValidRoute =
        isValidCourseId &&
        isValidModuleId &&
        isValidQuizId &&
        isValidQuestionId;

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleCreateOption(data: QuizAnswerOptionFormData) {
        if (!isValidRoute) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            await createQuizAnswerOption(parsedQuestionId, data);

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}/questions/${parsedQuestionId}`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while creating the option."
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
                            ? `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}/questions/${parsedQuestionId}`
                            : "/teacher"
                    }
                >
                    Back to question
                </Link>

                {!isValidRoute && (
                    <p className={styles.errorMessage}>
                        Invalid course, module, quiz, or question id.
                    </p>
                )}

                {isValidRoute && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>Quiz authoring</p>
                            <h1>Create answer option</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Add an answer choice and mark whether it is correct.
                        </p>

                        <AnswerOptionForm
                            submitLabel="Create option"
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleCreateOption}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
