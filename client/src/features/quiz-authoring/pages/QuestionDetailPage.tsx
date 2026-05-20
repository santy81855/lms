import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import {
    deleteQuizAnswerOption,
    deleteQuizQuestion,
    getQuestionAnswerOptions,
    getQuizQuestions,
} from "../api/quizAuthoringApi";
import { AnswerOptionCard } from "../components/AnswerOptionCard";
import type {
    QuizAnswerOption,
    QuizQuestion,
} from "../types/quizAuthoringTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./QuestionDetailPage.module.css";

function formatQuestionType(questionType: QuizQuestion["questionType"]) {
    return questionType
        .split("_")
        .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
        .join(" ");
}

export function QuestionDetailPage() {
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
    const [options, setOptions] = useState<QuizAnswerOption[]>([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [actionMessage, setActionMessage] = useState("");
    const [isLoadingQuestion, setIsLoadingQuestion] = useState(true);
    const [isRunningAction, setIsRunningAction] = useState(false);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        Promise.all([
            getQuizQuestions(parsedQuizId),
            getQuestionAnswerOptions(parsedQuestionId),
        ])
            .then(([questions, answerOptions]) => {
                if (!shouldIgnore) {
                    const matchingQuestion =
                        questions.find(
                            (questionItem) =>
                                questionItem.id === parsedQuestionId
                        ) ?? null;

                    setQuestion(matchingQuestion);
                    setOptions(answerOptions);
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

    async function reloadOptions() {
        const answerOptions = await getQuestionAnswerOptions(parsedQuestionId);
        setOptions(answerOptions);
    }

    async function handleDeleteOption(optionId: number) {
        const confirmed = window.confirm(
            "Are you sure you want to delete this answer option? This cannot be undone."
        );

        if (!confirmed || !isValidRoute) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await deleteQuizAnswerOption(optionId);
            await reloadOptions();
            setActionMessage("Answer option deleted.");
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while deleting the option."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    async function handleDeleteQuestion() {
        const confirmed = window.confirm(
            "Are you sure you want to delete this question? This cannot be undone."
        );

        if (!confirmed || !isValidRoute) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await deleteQuizQuestion(parsedQuestionId);

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while deleting the question."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <Link
                    className={pageStyles.secondaryLink}
                    to={
                        isValidCourseId && isValidModuleId && isValidQuizId
                            ? `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}`
                            : "/teacher"
                    }
                >
                    Back to quiz
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

                {actionMessage && (
                    <p className={styles.successMessage}>{actionMessage}</p>
                )}

                {isValidRoute &&
                    !isLoadingQuestion &&
                    !errorMessage &&
                    !question && <p>Question not found.</p>}

                {isValidRoute && !isLoadingQuestion && question && (
                    <>
                        <div className={styles.header}>
                            <div className={styles.titleGroup}>
                                <p className={pageStyles.eyebrow}>
                                    Question {question.questionOrder}
                                </p>

                                <h1>{question.questionText}</h1>
                            </div>

                            <span className={styles.points}>
                                {question.points} pts
                            </span>
                        </div>

                        <div className={styles.detailsCard}>
                            <dl className={styles.detailList}>
                                <div>
                                    <dt>Question ID</dt>
                                    <dd>{question.id}</dd>
                                </div>

                                <div>
                                    <dt>Quiz ID</dt>
                                    <dd>{question.quizId}</dd>
                                </div>

                                <div>
                                    <dt>Type</dt>
                                    <dd>
                                        {formatQuestionType(
                                            question.questionType
                                        )}
                                    </dd>
                                </div>

                                <div>
                                    <dt>Order</dt>
                                    <dd>{question.questionOrder}</dd>
                                </div>
                            </dl>

                            {question.explanation && (
                                <div className={styles.descriptionBlock}>
                                    <h2>Explanation</h2>
                                    <p>{question.explanation}</p>
                                </div>
                            )}
                        </div>

                        <div className={styles.optionsCard}>
                            <div className={styles.sectionHeader}>
                                <div>
                                    <h2>Answer options</h2>
                                    <p>
                                        Add answer options for multiple-choice
                                        and true/false questions.
                                    </p>
                                </div>

                                {question.questionType !== "SHORT_ANSWER" && (
                                    <Link
                                        className={styles.secondaryButton}
                                        to={`/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}/questions/${question.id}/options/new`}
                                    >
                                        Add option
                                    </Link>
                                )}
                            </div>

                            {options.length === 0 && (
                                <div className={styles.emptyState}>
                                    <h3>No answer options yet</h3>

                                    {question.questionType ===
                                    "SHORT_ANSWER" ? (
                                        <p>
                                            Short answer questions do not need
                                            answer options for the MVP.
                                        </p>
                                    ) : (
                                        <p>
                                            Add answer options and mark the
                                            correct response before publishing
                                            this quiz.
                                        </p>
                                    )}
                                </div>
                            )}

                            {options.length > 0 && (
                                <div className={styles.optionList}>
                                    {options.map((option) => (
                                        <AnswerOptionCard
                                            key={option.id}
                                            courseId={parsedCourseId}
                                            moduleId={parsedModuleId}
                                            quizId={parsedQuizId}
                                            questionId={question.id}
                                            option={option}
                                            isRunningAction={isRunningAction}
                                            onDelete={handleDeleteOption}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>

                        <div className={styles.actionCard}>
                            <h2>Question actions</h2>

                            <div className={styles.actions}>
                                <Link
                                    className={styles.secondaryButton}
                                    to={`/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${parsedQuizId}/questions/${question.id}/edit`}
                                >
                                    Edit details
                                </Link>

                                <button
                                    className={styles.dangerButton}
                                    type="button"
                                    disabled={isRunningAction}
                                    onClick={handleDeleteQuestion}
                                >
                                    Delete question
                                </button>
                            </div>
                        </div>
                    </>
                )}
            </section>
        </main>
    );
}
