import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { getTeacherCourse, updateCourse } from "../api/teacherCourseApi";
import { CourseForm } from "../components/CourseForm";
import type { Course, CourseFormData } from "../types/courseTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./EditCoursePage.module.css";

function getCourseFormData(course: Course): CourseFormData {
    return {
        title: course.title,
        subject: course.subject ?? "",
        gradeLevel: course.gradeLevel,
        description: course.description ?? "",
    };
}

export function EditCoursePage() {
    const navigate = useNavigate();
    const { courseId } = useParams();

    const parsedCourseId = Number(courseId);
    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;

    const [course, setCourse] = useState<Course | null>(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingCourse, setIsLoadingCourse] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!isValidCourseId) {
            return;
        }

        let shouldIgnore = false;

        getTeacherCourse(parsedCourseId)
            .then((courseDetails) => {
                if (!shouldIgnore) {
                    setCourse(courseDetails);
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

    async function handleUpdateCourse(data: CourseFormData) {
        if (!isValidCourseId) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const updatedCourse = await updateCourse(parsedCourseId, data);
            navigate(`/teacher/courses/${updatedCourse.id}`, { replace: true });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the course."
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

                {isValidCourseId && isLoadingCourse && <p>Loading course...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {isValidCourseId &&
                    !isLoadingCourse &&
                    !errorMessage &&
                    !course && <p>Course not found.</p>}

                {isValidCourseId && !isLoadingCourse && course && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>
                                Teacher courses
                            </p>
                            <h1>Edit course</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Update the basic course details. Publishing status
                            and join code are managed separately.
                        </p>

                        <CourseForm
                            submitLabel="Save changes"
                            initialValues={getCourseFormData(course)}
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleUpdateCourse}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
