import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { isApiError } from "@/api";

import { CourseCard } from "../components/CourseCard";
import { getTeacherCourses } from "../api/teacherCourseApi";
import type { Course } from "../types/courseTypes";
import SearchBar from "../components/SearchBar";

import pageStyles from "@/pages/Page.module.css";
import styles from "./TeacherDashboardPage.module.css";
import { CourseTypeSelect } from "../components/CourseTypeSelect";
import { CourseSortSelect } from "@/components/common/CourseSortSelect";

export function TeacherDashboardPage() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingCourses, setIsLoadingCourses] = useState(true);
    const [searchContent, setSearchContent] = useState("");
    const [sort, setSort] = useState("A-Z");
    const [courseType, setCourseType] = useState("all");

    const ALL = "all";

    const filteredCourses = useMemo(() => {
        return [...courses]
            .filter((course) => {
                return course.matchesSearch(searchContent);
            })
            .sort((a, b) => {
                if (sort === "A-Z") {
                    return a.title.localeCompare(b.title);
                }

                if (sort === "Newest") {
                    return (
                        new Date(b.updatedAt ?? 0).getTime() -
                        new Date(a.updatedAt ?? 0).getTime()
                    );
                }

                if (sort === "Oldest") {
                    return (
                        new Date(a.updatedAt ?? 0).getTime() -
                        new Date(b.updatedAt ?? 0).getTime()
                    );
                }

                return 0;
            })
            .filter((course) => {
                const status = course.status ?? "";

                return (
                    status.toLowerCase() === courseType.toLowerCase() ||
                    courseType.toLowerCase() === ALL
                );
            });
    }, [courses, searchContent, sort, courseType]);

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
                        "Something went wrong while loading courses.",
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

                <SearchBar
                    searchContent={searchContent}
                    setSearchContent={setSearchContent}
                />

                <CourseSortSelect setSort={setSort} />

                <CourseTypeSelect setCourseType={setCourseType} />

                <p className={pageStyles.description}>
                    View your courses and their current publishing status.
                </p>

                <div className={styles.summaryRow}>
                    <p className={styles.summaryRowItem}>
                        Total Courses: {courses.length}
                    </p>

                    <p className={styles.summaryRowItem}>
                        Drafts:{" "}
                        {
                            courses.filter(
                                (course) => course.status === "DRAFT",
                            ).length
                        }
                    </p>

                    <p className={styles.summaryRowItem}>
                        Active:{" "}
                        {
                            courses.filter(
                                (course) => course.status === "ACTIVE",
                            ).length
                        }
                    </p>

                    <p className={styles.summaryRowItem}>
                        Archived:{" "}
                        {
                            courses.filter(
                                (course) => course.status === "ARCHIVED",
                            ).length
                        }
                    </p>
                </div>

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

                {!isLoadingCourses &&
                    !errorMessage &&
                    courses.length > 0 &&
                    filteredCourses.length === 0 && (
                        <div className={styles.emptyState}>
                            <h2>No courses found</h2>
                            <p>Try changing your search or filter.</p>
                        </div>
                    )}

                {!isLoadingCourses &&
                    !errorMessage &&
                    filteredCourses.length > 0 && (
                        <div className={styles.courseList}>
                            {filteredCourses.map((course) => (
                                <CourseCard key={course.id} course={course} />
                            ))}
                        </div>
                    )}
            </section>
        </main>
    );
}
