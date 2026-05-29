import { useEffect, useState } from "react";
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
    const [filteredCourses, setFilteredCourses] = useState<Course[]>([]);
    const [searchContent, setSearchContent] = useState("");
    const [sort, setSort] = useState("A-Z");
    const [courseType, setCourseType] = useState("all");

    useEffect(() => {
        async function loadCourses() {
            try {
                const teacherCourses = await getTeacherCourses();
                setCourses(teacherCourses);
                setFilteredCourses(teacherCourses);
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
    }, [searchContent, sort]);

    useEffect(() => {
        const filtered = courses
            .filter((course) => {
                const title = course.title ?? "";
                const subject = course.subject ?? "";
                const description = course.description ?? "";

                const search = searchContent.toLowerCase();

                return (
                    title.toLowerCase().includes(search) ||
                    subject.toLowerCase().includes(search) ||
                    description.toLowerCase().includes(search)
                );
            })
            .sort((a, b) => {
                if (sort === "A-Z") {
                    return a.title.localeCompare(b.title);
                }

                if (sort === "Newest") {
                    return (
                        new Date(b.updatedAt ?? "").getTime() -
                        new Date(a.updatedAt ?? "").getTime()
                    );
                }

                if (sort === "Oldest") {
                    return (
                        new Date(a.updatedAt ?? "").getTime() -
                        new Date(b.updatedAt ?? "").getTime()
                    );
                }

                return 0;
            })
            // Filter by course status
            .filter((course) => {
                const status = course.status ?? "";

                return (
                    status.toLowerCase() === courseType.toLowerCase() ||
                    courseType.toLowerCase() === "all"
                );
            });
        setFilteredCourses([...filtered]);
    }, [courses, searchContent, sort, courseType]);

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
                        {filteredCourses.map((course) => (
                            <CourseCard key={course.id} course={course} />
                        ))}
                    </div>
                )}
            </section>
        </main>
    );
}
