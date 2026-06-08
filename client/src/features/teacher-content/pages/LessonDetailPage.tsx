import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";
import { ModuleStatusBadge } from "@/features/teacher-modules";

import {
    archiveLesson,
    deleteLesson,
    getModuleLessons,
    publishLesson,
    returnLessonToDraft,
} from "../api/teacherContentApi";
import type { Lesson } from "../types/contentTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./LessonDetailPage.module.css";

import { isAfter, subHours } from "date-fns";

export function LessonDetailPage() {
    const navigate = useNavigate();
    const { courseId, moduleId, lessonId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedModuleId = Number(moduleId);
    const parsedLessonId = Number(lessonId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidModuleId =
        Number.isInteger(parsedModuleId) && parsedModuleId > 0;
    const isValidLessonId =
        Number.isInteger(parsedLessonId) && parsedLessonId > 0;
    const isValidRoute = isValidCourseId && isValidModuleId && isValidLessonId;

    const [lesson, setLesson] = useState<Lesson | null>(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [actionMessage, setActionMessage] = useState("");
    const [isLoadingLesson, setIsLoadingLesson] = useState(true);
    const [isRunningAction, setIsRunningAction] = useState(false);
    const [now] = useState(() => new Date());

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        getModuleLessons(parsedModuleId)
            .then((lessons) => {
                if (!shouldIgnore) {
                    const matchingLesson =
                        lessons.find(
                            (lessonItem) => lessonItem.id === parsedLessonId
                        ) ?? null;

                    setLesson(matchingLesson);
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
    }, [isValidRoute, parsedLessonId, parsedModuleId]);

    async function reloadLesson() {
        const lessons = await getModuleLessons(parsedModuleId);
        const matchingLesson =
            lessons.find((lessonItem) => lessonItem.id === parsedLessonId) ??
            null;

        setLesson(matchingLesson);
    }

    async function runLessonAction(
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
            await reloadLesson();
            setActionMessage(successMessage);
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the lesson."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    async function handleDeleteLesson() {
        const confirmed = window.confirm(
            "Are you sure you want to delete this lesson? This cannot be undone."
        );

        if (!confirmed || !isValidRoute) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await deleteLesson(parsedLessonId);

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
                    "Something went wrong while deleting the lesson."
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
                        Invalid course, module, or lesson id.
                    </p>
                )}

                {isValidRoute && isLoadingLesson && <p>Loading lesson...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {actionMessage && (
                    <p className={styles.successMessage}>{actionMessage}</p>
                )}

                {isValidRoute &&
                    !isLoadingLesson &&
                    !errorMessage &&
                    !lesson && <p>Lesson not found.</p>}

                {isValidRoute && !isLoadingLesson && lesson && (
                    <>
                        <div className={styles.header}>
                            <div className={styles.titleGroup}>
                                <p className={pageStyles.eyebrow}>
                                    Lesson {lesson.lessonOrder}
                                </p>

                                <h1>{lesson.title}</h1>
                            </div>

                            <ModuleStatusBadge status={lesson.status} />
                        </div>

                        <div className={styles.detailsCard}>
                            <dl className={styles.detailList}>
                                <div>
                                    <dt>Lesson ID</dt>
                                    <dd>{lesson.id}</dd>
                                </div>

                                <div>
                                    <dt>Module ID</dt>
                                    <dd>{lesson.moduleId}</dd>
                                </div>

                                <div>
                                    <dt>Order</dt>
                                    <dd>{lesson.lessonOrder}</dd>
                                </div>

                                <div>
                                    <dt>Estimated minutes</dt>
                                    <dd>
                                        {lesson.estimatedMinutes ?? "Not set"}
                                    </dd>
                                </div>

                                <div>
                                    <dt>Published at</dt>
                                    <dd>
                                        {lesson.publishedAt ?? "Not published"}
                                    </dd>
                                </div>

                                <div>
                                    <dd>
                                        {lesson.updatedAt &&
                                            isAfter(
                                                new Date(lesson.updatedAt),
                                                subHours(now, 24)
                                            ) && (
                                                <span className={styles.recentlyUpdated}>
                                                    Recently Updated
                                                </span>
                                            )}
                                    </dd>
                                </div>
                            </dl>
                        </div>

                        <div className={styles.contentCard}>
                            <h2>Lesson content</h2>

                            {lesson.content ? (
                                <p className={styles.lessonContent}>
                                    {lesson.content}
                                </p>
                            ) : (
                                <p className={styles.emptyText}>
                                    No lesson content yet.
                                </p>
                            )}
                        </div>

                        <div className={styles.actionCard}>
                            <h2>Lesson actions</h2>

                            <div className={styles.actions}>
                                <Link
                                    className={styles.secondaryButton}
                                    to={`/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/lessons/${lesson.id}/edit`}
                                >
                                    Edit details
                                </Link>

                                <button
                                    className={styles.primaryButton}
                                    type="button"
                                    disabled={
                                        isRunningAction ||
                                        lesson.status === "PUBLISHED"
                                    }
                                    onClick={() =>
                                        runLessonAction(
                                            () => publishLesson(lesson.id),
                                            "Lesson published."
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
                                        lesson.status === "DRAFT"
                                    }
                                    onClick={() =>
                                        runLessonAction(
                                            () =>
                                                returnLessonToDraft(lesson.id),
                                            "Lesson returned to draft."
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
                                        lesson.status === "ARCHIVED"
                                    }
                                    onClick={() =>
                                        runLessonAction(
                                            () => archiveLesson(lesson.id),
                                            "Lesson archived."
                                        )
                                    }
                                >
                                    Archive
                                </button>

                                <button
                                    className={styles.dangerButton}
                                    type="button"
                                    disabled={isRunningAction}
                                    onClick={handleDeleteLesson}
                                >
                                    Delete
                                </button>
                            </div>
                        </div>
                    </>
                )}
            </section>
        </main>
    );
}
