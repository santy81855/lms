import { useEffect, useState } from "react";
import { Link } from "react-router";
import { isApiError } from "@/api";

import { CourseCard } from "../components/CourseCard";
import { getTeacherCourses } from "../api/teacherCourseApi";
import type { Course } from "../types/courseTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./TeacherDashboardPage.module.css";

export function TeacherDashboardPage() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingCourses, setIsLoadingCourses] = useState(true);

    useEffect(() => {
        async function loadCourses() {
            try {
                const teacherCourses = await getTeacherCourses();
                setCourses(teacherCourses);
            } catch (error) {
                if (isApiError(error)) {
                    setErrorMessage(error.message);
                } else {
                    setErrorMessage(
                        "Something went wrong while loading courses."
                    );
                }
            } finally {
                setIsLoadingCourses(false);
            }
        }

        loadCourses();
    }, []);

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <div className={styles.header}>
                    <div>
                        <p className={pageStyles.eyebrow}>Teacher dashboard</p>
                        <h1>Manage your courses</h1>
                    </div>

                    <div className={styles.headerActions}>
                        <Link
                            className={styles.secondaryButton}
                            to="/teacher/courses/ai-generate"
                        >
                            Generate with AI
                        </Link>

                        <Link
                            className={styles.createButton}
                            to="/teacher/courses/new"
                        >
                            Create course
                        </Link>
                    </div>
                </div>

                <p className={pageStyles.description}>
                    View your courses and their current publishing status.
                </p>

                {isLoadingCourses && <p>Loading courses...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {!isLoadingCourses && !errorMessage && courses.length === 0 && (
                    <div className={styles.emptyState}>
                        <h2>No courses yet</h2>
                        <p>
                            Once we build the create-course form, your courses
                            will appear here.
                        </p>
                    </div>
                )}

                {!isLoadingCourses && courses.length > 0 && (
                    <div className={styles.courseList}>
                        {courses.map((course) => (
                            <CourseCard key={course.id} course={course} />
                        ))}
                    </div>
                )}
            </section>
        </main>
    );
}
