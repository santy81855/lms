import { useEffect, useState } from "react";
import { Link, useParams } from "react-router";

import { isApiError } from "@/api";
import type { Lesson } from "@/features/teacher-content";

import { getStudentLesson } from "../api/studentContentApi";

import pageStyles from "@/pages/Page.module.css";
import styles from "./StudentLessonDetailPage.module.css";

export function StudentLessonDetailPage() {
    const { courseId, lessonId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedLessonId = Number(lessonId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidLessonId =
        Number.isInteger(parsedLessonId) && parsedLessonId > 0;
    const isValidRoute = isValidCourseId && isValidLessonId;

    const [lesson, setLesson] = useState<Lesson | null>(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingLesson, setIsLoadingLesson] = useState(true);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        getStudentLesson(parsedLessonId)
            .then((studentLesson) => {
                if (!shouldIgnore) {
                    setLesson(studentLesson);
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
                        "Something went wrong while loading the lesson."
                    );
                }
            })
            .finally(() => {
                if (!shouldIgnore) {
                    setIsLoadingLesson(false);
                }
            });

        return () => {
            shouldIgnore = true;
        };
    }, [isValidRoute, parsedLessonId]);

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
                        Invalid course or lesson id.
                    </p>
                )}

                {isValidRoute && isLoadingLesson && <p>Loading lesson...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {isValidRoute &&
                    !isLoadingLesson &&
                    !errorMessage &&
                    !lesson && <p>Lesson not found.</p>}

                {isValidRoute && !isLoadingLesson && lesson && (
                    <>
                        <div className={styles.header}>
                            <div>
                                <p className={pageStyles.eyebrow}>
                                    Lesson {lesson.lessonOrder}
                                </p>

                                <h1>{lesson.title}</h1>
                            </div>

                            {lesson.estimatedMinutes !== null && (
                                <span className={styles.timeBadge}>
                                    {lesson.estimatedMinutes} min
                                </span>
                            )}
                        </div>

                        <article className={styles.lessonCard}>
                            {lesson.content ? (
                                <p className={styles.lessonContent}>
                                    {lesson.content}
                                </p>
                            ) : (
                                <p className={styles.emptyText}>
                                    This lesson does not have content yet.
                                </p>
                            )}
                        </article>
                    </>
                )}
            </section>
        </main>
    );
}
