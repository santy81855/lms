import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import {
    getQuestionAnswerOptions,
    updateQuizAnswerOption,
} from "../api/quizAuthoringApi";
import { AnswerOptionForm } from "../components/AnswerOptionForm";
import type {
    QuizAnswerOption,
    QuizAnswerOptionFormData,
} from "../types/quizAuthoringTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./EditOptionPage.module.css";

function getOptionFormData(option: QuizAnswerOption): QuizAnswerOptionFormData {
    return {
        optionText: option.optionText,
        optionOrder: option.optionOrder,
        correct: option.correct,
    };
}

export function EditOptionPage() {
    const navigate = useNavigate();
    const { courseId, moduleId, quizId, questionId, optionId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedModuleId = Number(moduleId);
    const parsedQuizId = Number(quizId);
    const parsedQuestionId = Number(questionId);
    const parsedOptionId = Number(optionId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidModuleId =
        Number.isInteger(parsedModuleId) && parsedModuleId > 0;
    const isValidQuizId = Number.isInteger(parsedQuizId) && parsedQuizId > 0;
    const isValidQuestionId =
        Number.isInteger(parsedQuestionId) && parsedQuestionId > 0;
    const isValidOptionId =
        Number.isInteger(parsedOptionId) && parsedOptionId > 0;

    const isValidRoute =
        isValidCourseId &&
        isValidModuleId &&
        isValidQuizId &&
        isValidQuestionId &&
        isValidOptionId;

    const [option, setOption] = useState<QuizAnswerOption | null>(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingOption, setIsLoadingOption] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        getQuestionAnswerOptions(parsedQuestionId)
            .then((answerOptions) => {
                if (!shouldIgnore) {
                    const matchingOption =
                        answerOptions.find(
                            (answerOption) => answerOption.id === parsedOptionId
                        ) ?? null;

                    setOption(matchingOption);
                }
            })
            .catch((error: unknown) => {
                if (shouldIgnore) {
                    return;
                }

                if (isApiError(error)) {
                    setErrorMessage(error.message);
                } else {
                    setErrorMessage(
                        "Something went wrong while loading the option."
                    );
                }
            })
            .finally(() => {
                if (!shouldIgnore) {
                    setIsLoadingOption(false);
                }
            });

        return () => {
            shouldIgnore = true;
        };
    }, [isValidRoute, parsedQuestionId, parsedOptionId]);

    async function handleUpdateOption(data: QuizAnswerOptionFormData) {
        if (!isValidRoute) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            await updateQuizAnswerOption(parsedOptionId, data);

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}/questions/${parsedQuestionId}`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the option."
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
                        isValidCourseId &&
                        isValidModuleId &&
                        isValidQuizId &&
                        isValidQuestionId
                            ? `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}/questions/${parsedQuestionId}`
                            : "/teacher"
                    }
                >
                    Back to question
                </Link>

                {!isValidRoute && (
                    <p className={styles.errorMessage}>
                        Invalid course, module, quiz, question, or option id.
                    </p>
                )}

                {isValidRoute && isLoadingOption && <p>Loading option...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {isValidRoute &&
                    !isLoadingOption &&
                    !errorMessage &&
                    !option && <p>Answer option not found.</p>}

                {isValidRoute && !isLoadingOption && option && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>Quiz authoring</p>
                            <h1>Edit answer option</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Update the option text, order, or correctness.
                        </p>

                        <AnswerOptionForm
                            submitLabel="Save changes"
                            initialValues={getOptionFormData(option)}
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleUpdateOption}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
