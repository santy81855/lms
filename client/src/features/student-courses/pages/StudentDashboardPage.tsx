import { useEffect, useState } from "react";

import { isApiError } from "@/api";
import type { Course } from "@/features/teacher-courses";

import { getStudentCourses, joinCourse } from "../api/studentCourseApi";
import { JoinCourseForm } from "../components/JoinCourseForm";
import { StudentCourseCard } from "../components/StudentCourseCard";
import type { JoinCourseFormData } from "../types/studentCourseTypes";
import SearchBar from "../components/SearchBar";

import pageStyles from "@/pages/Page.module.css";
import styles from "./StudentDashboardPage.module.css";

export function StudentDashboardPage() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [loadErrorMessage, setLoadErrorMessage] = useState("");
    const [joinErrorMessage, setJoinErrorMessage] = useState("");
    const [joinSuccessMessage, setJoinSuccessMessage] = useState("");
    const [isLoadingCourses, setIsLoadingCourses] = useState(true);
    const [isJoiningCourse, setIsJoiningCourse] = useState(false);

    async function loadCourses() {
        const studentCourses = await getStudentCourses();
        setCourses(studentCourses);
    }

    useEffect(() => {
        let shouldIgnore = false;

        getStudentCourses()
            .then((studentCourses) => {
                if (!shouldIgnore) {
                    setCourses(studentCourses);
                }
            })
            .catch((error: unknown) => {
                if (shouldIgnore) {
                    return;
                }

                if (isApiError(error)) {
                    setLoadErrorMessage(error.message);
                } else {
                    setLoadErrorMessage(
                        "Something went wrong while loading your courses."
                    );
                }
            })
            .finally(() => {
                if (!shouldIgnore) {
                    setIsLoadingCourses(false);
                }
            });

        return () => {
            shouldIgnore = true;
        };
    }, []);

    async function handleJoinCourse(data: JoinCourseFormData) {
        setJoinErrorMessage("");
        setJoinSuccessMessage("");
        setIsJoiningCourse(true);

        try {
            await joinCourse(data);
            await loadCourses();

            setJoinSuccessMessage("Course joined.");
        } catch (error) {
            if (isApiError(error)) {
                setJoinErrorMessage(error.message);
            } else {
                setJoinErrorMessage(
                    "Something went wrong while joining the course."
                );
            }
        } finally {
            setIsJoiningCourse(false);
        }
    }

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <div>
                    <p className={pageStyles.eyebrow}>Student dashboard</p>
                    <h1>My courses</h1>
                </div>

                <SearchBar />

                <p className={pageStyles.description}>
                    Join a course with your teacher’s join code, then open your
                    enrolled courses from here.
                </p>

                <div className={styles.joinCard}>
                    <div>
                        <h2>Join a course</h2>
                        <p>Enter the join code your teacher shared with you.</p>
                    </div>

                    <JoinCourseForm
                        errorMessage={joinErrorMessage}
                        successMessage={joinSuccessMessage}
                        isSubmitting={isJoiningCourse}
                        onSubmit={handleJoinCourse}
                    />
                </div>

                <div className={styles.courseSection}>
                    <div className={styles.sectionHeader}>
                        <h2>Enrolled courses</h2>
                        <p>
                            {courses.length} course
                            {courses.length === 1 ? "" : "s"}
                        </p>
                    </div>

                    {isLoadingCourses && <p>Loading courses...</p>}

                    {loadErrorMessage && (
                        <p className={styles.errorMessage}>
                            {loadErrorMessage}
                        </p>
                    )}

                    {!isLoadingCourses &&
                        !loadErrorMessage &&
                        courses.length === 0 && (
                            <div className={styles.emptyState}>
                                <h3>No courses yet</h3>
                                <p>
                                    Use a join code to enroll in your first
                                    course.
                                </p>
                            </div>
                        )}

                    {!isLoadingCourses && courses.length > 0 && (
                        <div className={styles.courseList}>
                            {courses.map((course) => (
                                <StudentCourseCard
                                    key={course.id}
                                    course={course}
                                />
                            ))}
                        </div>
                    )}
                </div>
            </section>
        </main>
    );
}
