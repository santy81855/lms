import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";
import {
    getCourseModules,
    ModuleCard,
    type CourseModule,
} from "@/features/teacher-modules";

import {
    archiveCourse,
    deleteCourse,
    getTeacherCourse,
    publishCourse,
    returnCourseToDraft,
} from "../api/teacherCourseApi";
import { CourseStatusBadge } from "../components/CourseStatusBadge";
import type { Course } from "../types/courseTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./CourseDetailPage.module.css";

function formatGradeLevel(gradeLevel: Course["gradeLevel"]) {
    return gradeLevel
        .split("_")
        .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
        .join(" ");
}

export function CourseDetailPage() {
    const navigate = useNavigate();
    const { courseId } = useParams();

    const parsedCourseId = Number(courseId);
    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;

    const [course, setCourse] = useState<Course | null>(null);
    const [modules, setModules] = useState<CourseModule[]>([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [actionMessage, setActionMessage] = useState("");
    const [isLoadingCourse, setIsLoadingCourse] = useState(true);
    const [isRunningAction, setIsRunningAction] = useState(false);

    useEffect(() => {
        if (!isValidCourseId) {
            return;
        }

        let shouldIgnore = false;

        Promise.all([
            getTeacherCourse(parsedCourseId),
            getCourseModules(parsedCourseId),
        ])
            .then(([courseDetails, courseModules]) => {
                if (!shouldIgnore) {
                    setCourse(courseDetails);
                    setModules(courseModules);
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
                        "Something went wrong while loading the course."
                    );
                }
            })
            .finally(() => {
                if (!shouldIgnore) {
                    setIsLoadingCourse(false);
                }
            });

        return () => {
            shouldIgnore = true;
        };
    }, [isValidCourseId, parsedCourseId]);

    async function runCourseAction(
        action: () => Promise<void>,
        successMessage: string
    ) {
        if (!isValidCourseId) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await action();

            const updatedCourse = await getTeacherCourse(parsedCourseId);
            setCourse(updatedCourse);

            setActionMessage(successMessage);
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the course."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    async function handleDeleteCourse() {
        const confirmed = window.confirm(
            "Are you sure you want to delete this course? This cannot be undone."
        );

        if (!confirmed || !isValidCourseId) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await deleteCourse(parsedCourseId);
            navigate("/teacher", { replace: true });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while deleting the course."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <Link className={pageStyles.secondaryLink} to="/teacher">
                    Back to courses
                </Link>

                {!isValidCourseId && (
                    <p className={styles.errorMessage}>Invalid course id.</p>
                )}

                {isValidCourseId && isLoadingCourse && <p>Loading course...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {actionMessage && (
                    <p className={styles.successMessage}>{actionMessage}</p>
                )}

                {isValidCourseId &&
                    !isLoadingCourse &&
                    !errorMessage &&
                    !course && <p>Course not found.</p>}

                {isValidCourseId && !isLoadingCourse && course && (
                    <>
                        <div className={styles.header}>
                            <div className={styles.titleGroup}>
                                <p className={pageStyles.eyebrow}>
                                    Course details
                                </p>
                                <h1>{course.title}</h1>
                            </div>

                            <CourseStatusBadge status={course.status} />
                        </div>

                        <div className={styles.detailsCard}>
                            <dl className={styles.detailList}>
                                <div>
                                    <dt>Subject</dt>
                                    <dd>{course.subject || "No subject"}</dd>
                                </div>

                                <div>
                                    <dt>Grade level</dt>
                                    <dd>
                                        {formatGradeLevel(course.gradeLevel)}
                                    </dd>
                                </div>

                                <div>
                                    <dt>Join code</dt>
                                    <dd>{course.joinCode}</dd>
                                </div>

                                <div>
                                    <dt>Course ID</dt>
                                    <dd>{course.id}</dd>
                                </div>
                            </dl>

                            {course.description && (
                                <div className={styles.descriptionBlock}>
                                    <h2>Description</h2>
                                    <p>{course.description}</p>
                                </div>
                            )}
                        </div>

                        <div className={styles.modulesCard}>
                            <div className={styles.sectionHeader}>
                                <div>
                                    <h2>Modules</h2>
                                    <p>
                                        Organize this course into ordered
                                        modules for students to follow.
                                    </p>
                                </div>

                                <Link
                                    className={styles.secondaryButton}
                                    to={`/teacher/courses/${course.id}/modules/new`}
                                >
                                    Create module
                                </Link>
                            </div>

                            {modules.length === 0 && (
                                <div className={styles.emptyState}>
                                    <h3>No modules yet</h3>
                                    <p>
                                        Once we add the module form, created
                                        modules will appear here.
                                    </p>
                                </div>
                            )}

                            {modules.length > 0 && (
                                <div className={styles.moduleList}>
                                    {modules.map((module) => (
                                        <ModuleCard
                                            key={module.id}
                                            module={module}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>

                        <div className={styles.actionCard}>
                            <h2>Course actions</h2>

                            <div className={styles.actions}>
                                <Link
                                    className={styles.secondaryButton}
                                    to={`/teacher/courses/${course.id}/edit`}
                                >
                                    Edit details
                                </Link>

                                <button
                                    className={styles.primaryButton}
                                    type="button"
                                    disabled={
                                        isRunningAction ||
                                        course.status === "ACTIVE"
                                    }
                                    onClick={() =>
                                        runCourseAction(
                                            () => publishCourse(course.id),
                                            "Course published."
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
                                        course.status === "DRAFT"
                                    }
                                    onClick={() =>
                                        runCourseAction(
                                            () =>
                                                returnCourseToDraft(course.id),
                                            "Course returned to draft."
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
                                        course.status === "ARCHIVED"
                                    }
                                    onClick={() =>
                                        runCourseAction(
                                            () => archiveCourse(course.id),
                                            "Course archived."
                                        )
                                    }
                                >
                                    Archive
                                </button>

                                <button
                                    className={styles.dangerButton}
                                    type="button"
                                    disabled={isRunningAction}
                                    onClick={handleDeleteCourse}
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
