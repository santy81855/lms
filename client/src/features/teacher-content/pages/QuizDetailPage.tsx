import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";
import { ModuleStatusBadge } from "@/features/teacher-modules";
import {
    getQuizQuestions,
    QuizQuestionCard,
    type QuizQuestion,
} from "@/features/quiz-authoring";

import {
    archiveQuiz,
    deleteQuiz,
    getModuleQuizzes,
    publishQuiz,
    returnQuizToDraft,
    getQuizSubmissions,
} from "../api/teacherContentApi";
import type { Quiz, QuizSubmission } from "../types/contentTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./QuizDetailPage.module.css";

import { RecentlyUpdatedBadge } from "@/components/common/RecentlyUpdatedBadge";

export function QuizDetailPage() {
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
    const [questions, setQuestions] = useState<QuizQuestion[]>([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [actionMessage, setActionMessage] = useState("");
    const [isLoadingQuiz, setIsLoadingQuiz] = useState(true);
    const [isRunningAction, setIsRunningAction] = useState(false);
    const [viewSubmissions, setViewSubmissions] = useState(false);
    const [quizSubmissions, setQuizSubmissions] = useState<QuizSubmission[]>([]);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        Promise.all([
            getModuleQuizzes(parsedModuleId),
            getQuizQuestions(parsedQuizId),
        ])
            .then(([moduleQuizzes, quizQuestions]) => {
                if (!shouldIgnore) {
                    const matchingQuiz =
                        moduleQuizzes.find(
                            (quizItem) => quizItem.id === parsedQuizId
                        ) ?? null;

                    setQuiz(matchingQuiz);
                    setQuestions(quizQuestions);
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

    useEffect(() => {
        if (!isValidRoute || !quiz?.id) {
            return;
        }
        // RETURN HERE TO COMPLETE
        Promise.all([getQuizSubmissions(quiz.id)])
            .then(([result]) => {
                setQuizSubmissions(result.submissions);
                console.log(result.submissions);

            }).catch((error: unknown) => {
                console.error(error);
            });


    },
        [quiz?.publishedAt]);

    async function reloadQuiz() {
        const [moduleQuizzes, quizQuestions] = await Promise.all([
            getModuleQuizzes(parsedModuleId),
            getQuizQuestions(parsedQuizId),
        ]);

        const matchingQuiz =
            moduleQuizzes.find((quizItem) => quizItem.id === parsedQuizId) ??
            null;

        setQuiz(matchingQuiz);
        setQuestions(quizQuestions);
    }

    async function runQuizAction(
        action: () => Promise<void>,
        successMessage: string
    ) {
        if (!isValidRoute) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await action();
            await reloadQuiz();
            setActionMessage(successMessage);
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the quiz."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    async function handleDeleteQuiz() {
        const confirmed = window.confirm(
            "Are you sure you want to delete this quiz? This cannot be undone."
        );

        if (!confirmed || !isValidRoute) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await deleteQuiz(parsedQuizId);

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
                    "Something went wrong while deleting the quiz."
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
                        isValidModuleId && isValidCourseId
                            ? `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}`
                            : "/teacher"
                    }
                >
                    Back to module
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

                {actionMessage && (
                    <p className={styles.successMessage}>{actionMessage}</p>
                )}

                {isValidRoute && !isLoadingQuiz && !errorMessage && !quiz && (
                    <p>Quiz not found.</p>
                )}

                {isValidRoute && !isLoadingQuiz && quiz && (
                    <>
                        <div className={styles.header}>
                            <div className={styles.titleGroup}>
                                <p className={pageStyles.eyebrow}>
                                    Quiz {quiz.quizOrder}
                                </p>
                                <h1>{quiz.title}</h1>
                            </div>

                            <ModuleStatusBadge status={quiz.status} />
                        </div>

                        <div className={styles.detailsCard}>
                            <dl className={styles.detailList}>
                                <div>
                                    <dt>Quiz ID</dt>
                                    <dd>{quiz.id}</dd>
                                </div>

                                <div>
                                    <dt>Module ID</dt>
                                    <dd>{quiz.moduleId}</dd>
                                </div>

                                <div>
                                    <dt>Order</dt>
                                    <dd>{quiz.quizOrder}</dd>
                                </div>

                                <div>
                                    <dt>Max points</dt>
                                    <dd>{quiz.maxPoints}</dd>
                                </div>

                                <div>
                                    <dt>Time limit</dt>
                                    <dd>
                                        {quiz.timeLimitMinutes === null
                                            ? "No limit"
                                            : `${quiz.timeLimitMinutes} minutes`}
                                    </dd>
                                </div>

                                <div>
                                    <dt>Attempts allowed</dt>
                                    <dd>{quiz.attemptsAllowed}</dd>
                                </div>

                                <div>
                                    <dt>Published at</dt>
                                    <dd>
                                        {quiz.publishedAt ?? "Not published"}
                                    </dd>
                                </div>

                                <div>
                                    <dd>
                                        <RecentlyUpdatedBadge updatedAt={quiz.updatedAt} />
                                    </dd>
                                </div>
                            </dl>

                            {quiz.description && (
                                <div className={styles.descriptionBlock}>
                                    <h2>Description</h2>
                                    <p>{quiz.description}</p>
                                </div>
                            )}
                        </div>

                        <div className={styles.questionsCard}>
                            <div className={styles.sectionHeader}>
                                <div>
                                    <h2>Questions</h2>
                                    <p>
                                        Add questions and answer options before
                                        publishing this quiz to students.
                                    </p>
                                </div>

                                <Link
                                    className={styles.secondaryButton}
                                    to={`/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${quiz.id}/questions/new`}
                                >
                                    Add question
                                </Link>
                            </div>

                            {questions.length === 0 && (
                                <div className={styles.emptyState}>
                                    <h3>No questions yet</h3>
                                    <p>
                                        Add at least one question before
                                        publishing this quiz to students.
                                    </p>
                                </div>
                            )}

                            {questions.length > 0 && (
                                <div className={styles.questionList}>
                                    {questions.map((question) => (
                                        <QuizQuestionCard
                                            key={question.id}
                                            courseId={parsedCourseId}
                                            moduleId={parsedModuleId}
                                            quizId={parsedQuizId}
                                            question={question}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>

                        {questions.length === 0 && (
                            <p className={styles.warningMessage}>
                                Add at least one question before publishing this
                                quiz.
                            </p>
                        )}

                        <div className={styles.actionCard}>
                            <h2>Quiz actions</h2>

                            <div className={styles.actions}>

                                {isRunningAction || quiz.status !== "PUBLISHED" ? <></> :
                                    <button
                                        aria-disabled={isRunningAction || quiz.status !== "PUBLISHED" || questions.length === 0}
                                        onClick={() => setViewSubmissions(!viewSubmissions)}
                                        className={styles.secondaryButton}
                                    >
                                        View Submissions
                                    </button>
                                }


                                <Link
                                    className={styles.secondaryButton}
                                    to={`/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/quizzes/${quiz.id}/edit`}
                                >
                                    Edit details
                                </Link>
                                <button
                                    className={styles.primaryButton}
                                    type="button"
                                    disabled={
                                        isRunningAction ||
                                        quiz.status === "PUBLISHED" ||
                                        questions.length === 0
                                    }
                                    onClick={() =>
                                        runQuizAction(
                                            () => publishQuiz(quiz.id),
                                            "Quiz published."
                                        )
                                    }
                                >
                                    Publish
                                </button>

                                <button
                                    className={styles.secondaryButton}
                                    type="button"
                                    disabled={
                                        isRunningAction ||
                                        quiz.status === "DRAFT"
                                    }
                                    onClick={() =>
                                        runQuizAction(
                                            () => returnQuizToDraft(quiz.id),
                                            "Quiz returned to draft."
                                        )
                                    }
                                >
                                    Return to draft
                                </button>

                                <button
                                    className={styles.secondaryButton}
                                    type="button"
                                    disabled={
                                        isRunningAction ||
                                        quiz.status === "ARCHIVED"
                                    }
                                    onClick={() =>
                                        runQuizAction(
                                            () => archiveQuiz(quiz.id),
                                            "Quiz archived."
                                        )
                                    }
                                >
                                    Archive
                                </button>

                                <button
                                    className={styles.dangerButton}
                                    type="button"
                                    disabled={isRunningAction}
                                    onClick={handleDeleteQuiz}
                                >
                                    Delete
                                </button>
                            </div>
                        </div>

                        {viewSubmissions ?
                            <div className={styles.actionCard}>
                                <h2>Quiz submissions</h2>
                                <table className={styles.submissionTable}>
                                    <thead >
                                        <tr >
                                            <th className={styles.submissionsTable}>Student</th>
                                            <th className={styles.submissionsTable}>Score</th>
                                            <th className={styles.submissionsTable}>Date</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {quizSubmissions.map(submission => {
                                            return (
                                                <tr key={submission.id}>
                                                    <td>{submission.student_name}</td>
                                                    <td>{submission.score}</td>
                                                    <td>{submission.submitted_at.split("T")[0]}</td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                            : <></>}
                    </>
                )}
            </section>
        </main>
    );
}
