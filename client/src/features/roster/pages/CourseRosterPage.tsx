import { useEffect, useState } from "react";
import { Link, useParams } from "react-router";

import { isApiError } from "@/api";
import { getTeacherCourse, type Course } from "@/features/teacher-courses";

import { getCourseRoster } from "../api/rosterApi";
import { RosterStudentCard } from "../components/RosterStudentCard";
import type { CourseRosterStudent } from "../types/rosterTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./CourseRosterPage.module.css";

export function CourseRosterPage() {
    const { courseId } = useParams();

    const parsedCourseId = Number(courseId);
    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;

    const [course, setCourse] = useState<Course | null>(null);
    const [students, setStudents] = useState<CourseRosterStudent[]>([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingRoster, setIsLoadingRoster] = useState(true);

    useEffect(() => {
        if (!isValidCourseId) {
            return;
        }

        let shouldIgnore = false;

        Promise.all([
            getTeacherCourse(parsedCourseId),
            getCourseRoster(parsedCourseId),
        ])
            .then(([teacherCourse, courseRoster]) => {
                if (!shouldIgnore) {
                    setCourse(teacherCourse);
                    setStudents(courseRoster);
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
                        "Something went wrong while loading the roster."
                    );
                }
            })
            .finally(() => {
                if (!shouldIgnore) {
                    setIsLoadingRoster(false);
                }
            });

        return () => {
            shouldIgnore = true;
        };
    }, [isValidCourseId, parsedCourseId]);

    const activeCount = students.filter(
        (student) => student.enrollmentStatus === "ACTIVE"
    ).length;

    const completedCount = students.filter(
        (student) => student.enrollmentStatus === "COMPLETED"
    ).length;

    const droppedCount = students.filter(
        (student) => student.enrollmentStatus === "DROPPED"
    ).length;

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <Link
                    className={pageStyles.secondaryLink}
                    to={
                        isValidCourseId
                            ? `/teacher/courses/${parsedCourseId}`
                            : "/teacher"
                    }
                >
                    Back to course
                </Link>

                {!isValidCourseId && (
                    <p className={styles.errorMessage}>Invalid course id.</p>
                )}

                {isValidCourseId && isLoadingRoster && <p>Loading roster...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {isValidCourseId && !isLoadingRoster && course && (
                    <>
                        <div className={styles.header}>
                            <div>
                                <p className={pageStyles.eyebrow}>
                                    Course roster
                                </p>
                                <h1>{course.title}</h1>
                            </div>

                            <span className={styles.countBadge}>
                                {students.length} student
                                {students.length === 1 ? "" : "s"}
                            </span>
                        </div>

                        <div className={styles.summaryGrid}>
                            <article className={styles.summaryCard}>
                                <p>Active</p>
                                <strong>{activeCount}</strong>
                            </article>

                            <article className={styles.summaryCard}>
                                <p>Completed</p>
                                <strong>{completedCount}</strong>
                            </article>

                            <article className={styles.summaryCard}>
                                <p>Dropped</p>
                                <strong>{droppedCount}</strong>
                            </article>
                        </div>

                        {students.length === 0 && (
                            <div className={styles.emptyState}>
                                <h2>No students yet</h2>
                                <p>
                                    Students will appear here after they join
                                    this course with the join code.
                                </p>
                            </div>
                        )}

                        {students.length > 0 && (
                            <div className={styles.rosterList}>
                                {students.map((student) => (
                                    <RosterStudentCard
                                        key={`${student.studentId}-${student.enrollmentStatus}`}
                                        student={student}
                                    />
                                ))}
                            </div>
                        )}
                    </>
                )}
            </section>
        </main>
    );
}
