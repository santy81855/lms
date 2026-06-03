import { useEffect, useState } from "react";
import { Link, useParams } from "react-router";

import { isApiError } from "@/api";
import type { Quiz } from "@/features/teacher-content";

import { getStudentQuiz } from "../api/studentContentApi";

import pageStyles from "@/pages/Page.module.css";
import styles from "./StudentQuizDetailPage.module.css";
import { getAttemptsRemaining } from "@/features/quiz-taking";
import type { QuizAttemptStatus } from "../types/studentContentTypes";

export function StudentQuizDetailPage() {
    const defaultQuizAttemptStatus : QuizAttemptStatus = {
        quizId: 0,
        attemptsAllowed: 0,
        attemptsUsed: 0,
        attemptsRemaining: 0,
        canTake: false
    }

    const { courseId, quizId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedQuizId = Number(quizId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidQuizId = Number.isInteger(parsedQuizId) && parsedQuizId > 0;
    const isValidRoute = isValidCourseId && isValidQuizId;

    const [quiz, setQuiz] = useState<Quiz | null>(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingQuiz, setIsLoadingQuiz] = useState(true);

    // TODO: students can fully retake a quiz before the backend tells them they're
    // out of attempts.  For a better user experience, the user should not be
    // given the option to retake a quiz they don't have attempts on, and they
    // should be navigated away if they change the URL to a quiz they don't have
    // quiz attempts remaining for
    const [attemptsRemainingStatus, setAttemptsRemaining] = useState<QuizAttemptStatus>(defaultQuizAttemptStatus);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;
        Promise.all([
            getStudentQuiz(parsedQuizId),
            getAttemptsRemaining(parsedQuizId)
        ])
            .then(([studentQuiz, attemptsRemainingResponse]) => {
                if (!shouldIgnore) {
                    setQuiz(studentQuiz);
                    setAttemptsRemaining(attemptsRemainingResponse);
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
    }, [isValidRoute, parsedQuizId]);

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <Link
                    className={pageStyles.secondaryLink}
                    to={
                        isValidCourseId
                            ? `/student/courses/${parsedCourseId}`
                            : "/student"
                    }
                >
                    Back to course
                </Link>

                {!isValidRoute && (
                    <p className={styles.errorMessage}>
                        Invalid course or quiz id.
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
                        <div className={styles.header}>
                            <div>
                                <p className={pageStyles.eyebrow}>
                                    Quiz {quiz.quizOrder}
                                </p>
                                <h1>{quiz.title}</h1>
                            </div>

                            <span className={styles.statusBadge}>
                                Published
                            </span>
                        </div>

                        <div className={styles.detailsCard}>
                            <dl className={styles.detailList}>
                                <div>
                                    <dt>Max points</dt>
                                    <dd>{quiz.maxPoints}</dd>
                                </div>

                                <div>
                                    <dt>Attempts allowed</dt>
                                    <dd>{quiz.attemptsAllowed}</dd>
                                </div>

                                <div>
                                    <dt>Time limit</dt>
                                    <dd>
                                        {quiz.timeLimitMinutes === null
                                            ? "No limit"
                                            : `${quiz.timeLimitMinutes} minutes`}
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
                        
                        <div className={styles.noticeCard}>
                            <h2>{attemptsRemainingStatus.attemptsRemaining > 0 ? "Ready to start?" : "No More Attempts Remaining"}</h2>
                            {attemptsRemainingStatus.attemptsRemaining > 0 &&
                                <p>
                                    Answer each question, then submit the quiz to
                                    see your latest score.
                                </p>
                            }

                            <div className={styles.actions}>
                                {attemptsRemainingStatus.attemptsRemaining > 0 && 
                                <Link
                                    className={styles.primaryButton}
                                    to={`/student/courses/${parsedCourseId}/quizzes/${quiz.id}/take`}
                                >
                                    Take quiz
                                </Link> 
                                }

                                <Link
                                    className={styles.secondaryButton}
                                    to={`/student/courses/${parsedCourseId}/quizzes/${quiz.id}/result`}
                                >
                                    View latest result
                                </Link>
                            </div>
                        </div>
                    </>
                )}
            </section>
        </main>
    );
}
