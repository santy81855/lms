import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { getModuleLessons, updateLesson } from "../api/teacherContentApi";
import { LessonForm } from "../components/LessonForm";
import type { Lesson, LessonFormData } from "../types/contentTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./EditLessonPage.module.css";

function getLessonFormData(lesson: Lesson): LessonFormData {
    return {
        title: lesson.title,
        content: lesson.content ?? "",
        lessonOrder: lesson.lessonOrder,
        estimatedMinutes: lesson.estimatedMinutes,
    };
}

export function EditLessonPage() {
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
    const [isLoadingLesson, setIsLoadingLesson] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

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

    async function handleUpdateLesson(data: LessonFormData) {
        if (!isValidRoute) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const updatedLesson = await updateLesson(parsedLessonId, data);

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/lessons/${updatedLesson.id}`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the lesson."
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
                            ? `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}/lessons/${parsedLessonId}`
                            : "/teacher"
                    }
                >
                    Back to lesson
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

                {isValidRoute &&
                    !isLoadingLesson &&
                    !errorMessage &&
                    !lesson && <p>Lesson not found.</p>}

                {isValidRoute && !isLoadingLesson && lesson && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>
                                Teacher content
                            </p>
                            <h1>Edit lesson</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Update the lesson title, body, order, or estimated
                            time. Publishing status is managed separately.
                        </p>

                        <LessonForm
                            submitLabel="Save changes"
                            initialValues={getLessonFormData(lesson)}
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleUpdateLesson}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
