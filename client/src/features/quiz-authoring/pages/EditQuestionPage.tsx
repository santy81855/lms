import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { getQuizQuestions, updateQuizQuestion } from "../api/quizAuthoringApi";
import { QuizQuestionForm } from "../components/QuizQuestionForm";
import type {
    QuizQuestion,
    QuizQuestionFormData,
} from "../types/quizAuthoringTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./EditQuestionPage.module.css";

function getQuestionFormData(question: QuizQuestion): QuizQuestionFormData {
    return {
        questionText: question.questionText,
        questionType: question.questionType,
        questionOrder: question.questionOrder,
        points: question.points,
        explanation: question.explanation ?? "",
    };
}

export function EditQuestionPage() {
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

    const [question, setQuestion] = useState<QuizQuestion | null>(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingQuestion, setIsLoadingQuestion] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        getQuizQuestions(parsedQuizId)
            .then((questions) => {
                if (!shouldIgnore) {
                    const matchingQuestion =
                        questions.find(
                            (questionItem) =>
                                questionItem.id === parsedQuestionId
                        ) ?? null;

                    setQuestion(matchingQuestion);
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
                        "Something went wrong while loading the question."
                    );
                }
            })
            .finally(() => {
                if (!shouldIgnore) {
                    setIsLoadingQuestion(false);
                }
            });

        return () => {
            shouldIgnore = true;
        };
    }, [isValidRoute, parsedQuizId, parsedQuestionId]);

    async function handleUpdateQuestion(data: QuizQuestionFormData) {
        if (!isValidRoute) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const updatedQuestion = await updateQuizQuestion(
                parsedQuestionId,
                data
            );

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}/questions/${updatedQuestion.id}`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the question."
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

                {isValidRoute && isLoadingQuestion && (
                    <p>Loading question...</p>
                )}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {isValidRoute &&
                    !isLoadingQuestion &&
                    !errorMessage &&
                    !question && <p>Question not found.</p>}

                {isValidRoute && !isLoadingQuestion && question && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>Quiz authoring</p>
                            <h1>Edit question</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Update the question text, type, points, order, or
                            explanation.
                        </p>

                        <QuizQuestionForm
                            submitLabel="Save changes"
                            initialValues={getQuestionFormData(question)}
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleUpdateQuestion}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
