import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { getModuleQuizzes, updateQuiz } from "../api/teacherContentApi";
import { QuizForm } from "../components/QuizForm";
import type { Quiz, QuizFormData } from "../types/contentTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./EditQuizPage.module.css";

function getQuizFormData(quiz: Quiz): QuizFormData {
    return {
        title: quiz.title,
        description: quiz.description ?? "",
        quizOrder: quiz.quizOrder,
        maxPoints: quiz.maxPoints,
        timeLimitMinutes: quiz.timeLimitMinutes,
        attemptsAllowed: quiz.attemptsAllowed,
    };
}

export function EditQuizPage() {
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

    const [quiz, setQuiz] = useState<Quiz | null>(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingQuiz, setIsLoadingQuiz] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        getModuleQuizzes(parsedModuleId)
            .then((quizzes) => {
                if (!shouldIgnore) {
                    const matchingQuiz =
                        quizzes.find(
                            (quizItem) => quizItem.id === parsedQuizId
                        ) ?? null;

                    setQuiz(matchingQuiz);
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
                        "Something went wrong while loading the quiz."
                    );
                }
            })
            .finally(() => {
                if (!shouldIgnore) {
                    setIsLoadingQuiz(false);
                }
            });

        return () => {
            shouldIgnore = true;
        };
    }, [isValidRoute, parsedModuleId, parsedQuizId]);

    async function handleUpdateQuiz(data: QuizFormData) {
        if (!isValidRoute) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const updatedQuiz = await updateQuiz(parsedQuizId, data);

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${updatedQuiz.id}`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the quiz."
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

                {isValidRoute && isLoadingQuiz && <p>Loading quiz...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {isValidRoute && !isLoadingQuiz && !errorMessage && !quiz && (
                    <p>Quiz not found.</p>
                )}

                {isValidRoute && !isLoadingQuiz && quiz && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>
                                Teacher quizzes
                            </p>
                            <h1>Edit quiz</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Update quiz settings. Publishing status and
                            questions are managed separately.
                        </p>

                        <QuizForm
                            submitLabel="Save changes"
                            initialValues={getQuizFormData(quiz)}
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleUpdateQuiz}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
