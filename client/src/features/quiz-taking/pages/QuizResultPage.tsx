import { useEffect, useState } from "react";
import { Link, useParams } from "react-router";

import { isApiError } from "@/api";

import { getAllStudentQuizResults } from "../api/quizTakingApi";
import { QuizResultCard } from "../components/QuizResultCard";
import type { StudentQuizResult } from "../types/quizTakingTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./QuizResultPage.module.css";

export function QuizResultPage() {
    const { courseId, quizId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedQuizId = Number(quizId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidQuizId = Number.isInteger(parsedQuizId) && parsedQuizId > 0;
    const isValidRoute = isValidCourseId && isValidQuizId;
    const [results, setResults] = useState<StudentQuizResult[] | null>(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingResult, setIsLoadingResult] = useState(true);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        getAllStudentQuizResults(parsedQuizId)
            .then((response) => {
                if (!shouldIgnore) {
                    setResults(response.payload);
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
                        "Something went wrong while loading the quiz results.",
                    );
                }
            })
            .finally(() => {
                if (!shouldIgnore) {
                    setIsLoadingResult(false);
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
                        isValidCourseId && isValidQuizId
                            ? `/student/courses/${parsedCourseId}/quizzes/${parsedQuizId}`
                            : "/student"
                    }
                >
                    Back to quiz overview
                </Link>

                <div>
                    <p className={pageStyles.eyebrow}>Quiz result</p>
                    <h1>Latest result</h1>
                </div>

                {!isValidRoute && (
                    <p className={styles.errorMessage}>
                        Invalid course or quiz id.
                    </p>
                )}

                {isValidRoute && isLoadingResult && <p>Loading result...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {isValidRoute &&
                    !isLoadingResult &&
                    !errorMessage &&
                    !results && <p>No quiz results found.</p>}

                {isValidRoute &&
                    !isLoadingResult &&
                    results &&
                    results.map((res) => <QuizResultCard result={res} />)}
            </section>
        </main>
    );
}
