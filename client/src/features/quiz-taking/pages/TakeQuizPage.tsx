import { useEffect, useMemo, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import {
    getAttemptsRemaining,
    getStudentQuizForTaking,
    submitStudentQuiz,
} from "../api/quizTakingApi";
import { QuizTakingQuestionCard } from "../components/QuizTakingQuestionCard";
import type {
    StudentQuiz,
    StudentQuizAnswerRequest,
} from "../types/quizTakingTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./TakeQuizPage.module.css";
import { defaultQuizAttemptRemaining, type QuizAttemptStatus } from "@/features/student-content";

export function TakeQuizPage() {
    const navigate = useNavigate();
    const { courseId, quizId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedQuizId = Number(quizId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidQuizId = Number.isInteger(parsedQuizId) && parsedQuizId > 0;
    const isValidRoute = isValidCourseId && isValidQuizId;

    const [quiz, setQuiz] = useState<StudentQuiz | null>(null);
    const [selectedOptionIds, setSelectedOptionIds] = useState<
        Record<number, number>
    >({});
    const [shortAnswers, setShortAnswers] = useState<Record<number, string>>(
        {}
    );
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingQuiz, setIsLoadingQuiz] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [attemptsRemaining, setAttemptsRemaining] = useState<QuizAttemptStatus>(defaultQuizAttemptRemaining);

    useEffect(() => {
        if (!isValidRoute || quiz == null) {
            return;
        }

        let shouldIgnore = false;

        Promise.all([
            getStudentQuizForTaking(parsedQuizId),
            getAttemptsRemaining(quiz?.id)
        ])
            .then(([studentQuiz, attempts]) => {
                if(attempts.attemptsRemaining <= 0){
                    setErrorMessage("You may not retake this quiz");
                    return;
                }
                if (!shouldIgnore) {
                    setQuiz(studentQuiz);
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

    const answeredQuestionCount = useMemo(() => {
        if (!quiz) {
            return 0;
        }

        return quiz.questions.filter((question) => {
            if (question.questionType === "SHORT_ANSWER") {
                return (shortAnswers[question.id] ?? "").trim().length > 0;
            }

            return selectedOptionIds[question.id] !== undefined;
        }).length;
    }, [quiz, selectedOptionIds, shortAnswers]);

    const allQuestionsAnswered =
        quiz !== null &&
        quiz.questions.length > 0 &&
        answeredQuestionCount === quiz.questions.length;

    function handleSelectOption(questionId: number, optionId: number) {
        setSelectedOptionIds((current) => ({
            ...current,
            [questionId]: optionId,
        }));
    }

    function handleChangeShortAnswer(questionId: number, value: string) {
        setShortAnswers((current) => ({
            ...current,
            [questionId]: value,
        }));
    }

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        if (!quiz || !isValidRoute) {
            return;
        }

        if (!allQuestionsAnswered) {
            setErrorMessage("Please answer every question before submitting.");
            return;
        }

        const answers: StudentQuizAnswerRequest[] = quiz.questions.map(
            (question) => {
                if (question.questionType === "SHORT_ANSWER") {
                    return {
                        questionId: question.id,
                        selectedOptionId: null,
                        shortAnswerText: shortAnswers[question.id].trim(),
                    };
                }

                return {
                    questionId: question.id,
                    selectedOptionId: selectedOptionIds[question.id],
                    shortAnswerText: null,
                };
            }
        );

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            await submitStudentQuiz(parsedQuizId, { answers });

            navigate(
                `/student/courses/${parsedCourseId}/quizzes/${parsedQuizId}/result`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while submitting the quiz."
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
                        isValidCourseId && isValidQuizId
                            ? `/student/courses/${parsedCourseId}/quizzes/${parsedQuizId}`
                            : "/student"
                    }
                >
                    Back to quiz overview
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
                                <p className={pageStyles.eyebrow}>Take quiz</p>
                                <h1>{quiz.title}</h1>
                            </div>

                            <span className={styles.progressBadge}>
                                {answeredQuestionCount} /{" "}
                                {quiz.questions.length} answered
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

                            {quiz.description && <p>{quiz.description}</p>}
                        </div>

                        {quiz.questions.length === 0 && (
                            <div className={styles.emptyState}>
                                <h2>No questions available</h2>
                                <p>
                                    This quiz does not have published questions
                                    yet.
                                </p>
                            </div>
                        )}

                        {quiz.questions.length > 0 && (
                            <form
                                className={styles.quizForm}
                                onSubmit={handleSubmit}
                            >
                                {quiz.questions.map((question) => (
                                    <QuizTakingQuestionCard
                                        key={question.id}
                                        question={question}
                                        selectedOptionId={
                                            selectedOptionIds[question.id]
                                        }
                                        shortAnswerText={
                                            shortAnswers[question.id]
                                        }
                                        disabled={isSubmitting}
                                        onSelectOption={handleSelectOption}
                                        onChangeShortAnswer={
                                            handleChangeShortAnswer
                                        }
                                    />
                                ))}

                                <button
                                    className={styles.submitButton}
                                    type="submit"
                                    disabled={
                                        isSubmitting || !allQuestionsAnswered
                                    }
                                >
                                    {isSubmitting
                                        ? "Submitting..."
                                        : "Submit quiz"}
                                </button>
                            </form>
                        )}
                    </>
                )}
            </section>
        </main>
    );
}
