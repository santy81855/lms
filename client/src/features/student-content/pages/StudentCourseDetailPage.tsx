import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";
import type { Course } from "@/features/teacher-courses";
import {
    completeStudentCourse,
    dropStudentCourse,
} from "@/features/student-courses";

import {
    getStudentCourse,
    getStudentCourseModules,
    getStudentModuleContentItems,
} from "../api/studentContentApi";
import { StudentModuleSection } from "../components/StudentModuleSection";
import type { StudentModuleWithContent } from "../types/studentContentTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./StudentCourseDetailPage.module.css";

function formatGradeLevel(gradeLevel: Course["gradeLevel"]) {
    return gradeLevel
        .split("_")
        .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
        .join(" ");
}

export function StudentCourseDetailPage() {
    const navigate = useNavigate();
    const { courseId } = useParams();

    const parsedCourseId = Number(courseId);
    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;

    const [course, setCourse] = useState<Course | null>(null);
    const [modulesWithContent, setModulesWithContent] = useState<
        StudentModuleWithContent[]
    >([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [actionMessage, setActionMessage] = useState("");
    const [isLoadingCourse, setIsLoadingCourse] = useState(true);
    const [isRunningAction, setIsRunningAction] = useState(false);

    useEffect(() => {
        if (!isValidCourseId) {
            return;
        }

        let shouldIgnore = false;

        async function loadCourseContent() {
            const [studentCourse, studentModules] = await Promise.all([
                getStudentCourse(parsedCourseId),
                getStudentCourseModules(parsedCourseId),
            ]);

            const moduleContent = await Promise.all(
                studentModules.map(async (module) => ({
                    module,
                    contentItems: await getStudentModuleContentItems(module.id),
                }))
            );

            if (!shouldIgnore) {
                setCourse(studentCourse);
                setModulesWithContent(moduleContent);
            }
        }

        loadCourseContent()
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

    async function handleDropCourse() {
        const confirmed = window.confirm(
            "Are you sure you want to drop this course? It will be removed from your active courses."
        );

        if (!confirmed || !isValidCourseId) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await dropStudentCourse(parsedCourseId);
            navigate("/student", { replace: true });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while dropping the course."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    async function handleCompleteCourse() {
        const confirmed = window.confirm(
            "Mark this course as complete? It will be removed from your active courses."
        );

        if (!confirmed || !isValidCourseId) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await completeStudentCourse(parsedCourseId);
            setActionMessage("Course marked complete.");
            navigate("/student", { replace: true });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while completing the course."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <Link className={pageStyles.secondaryLink} to="/student">
                    Back to my courses
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
                            <div>
                                <p className={pageStyles.eyebrow}>
                                    Student course
                                </p>
                                <h1>{course.title}</h1>
                            </div>

                            <span className={styles.statusBadge}>
                                {course.status}
                            </span>
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
                            </dl>

                            {course.description && (
                                <p className={styles.description}>
                                    {course.description}
                                </p>
                            )}
                        </div>

                        <div className={styles.actionCard}>
                            <h2>Course actions</h2>

                            <div className={styles.actions}>
                                <button
                                    className={styles.primaryButton}
                                    type="button"
                                    disabled={isRunningAction}
                                    onClick={handleCompleteCourse}
                                >
                                    Mark complete
                                </button>

                                <button
                                    className={styles.dangerButton}
                                    type="button"
                                    disabled={isRunningAction}
                                    onClick={handleDropCourse}
                                >
                                    Drop course
                                </button>
                            </div>
                        </div>

                        <div className={styles.moduleSection}>
                            <div className={styles.sectionHeader}>
                                <h2>Modules</h2>
                                <p>
                                    {modulesWithContent.length} published module
                                    {modulesWithContent.length === 1 ? "" : "s"}
                                </p>
                            </div>

                            {modulesWithContent.length === 0 && (
                                <div className={styles.emptyState}>
                                    <h3>No published modules yet</h3>
                                    <p>
                                        Your teacher has not published any
                                        modules for this course.
                                    </p>
                                </div>
                            )}

                            {modulesWithContent.length > 0 && (
                                <div className={styles.moduleList}>
                                    {modulesWithContent.map(
                                        (moduleWithContent) => (
                                            <StudentModuleSection
                                                key={
                                                    moduleWithContent.module.id
                                                }
                                                courseId={course.id}
                                                moduleWithContent={
                                                    moduleWithContent
                                                }
                                            />
                                        )
                                    )}
                                </div>
                            )}
                        </div>
                    </>
                )}
            </section>
        </main>
    );
}
