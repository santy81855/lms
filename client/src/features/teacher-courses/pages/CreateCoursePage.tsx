import { useState } from "react";
import { Link, useNavigate } from "react-router";

import { isApiError } from "@/api";

import { createCourse } from "../api/teacherCourseApi";
import { CourseForm } from "../components/CourseForm";
import type { CourseFormData } from "../types/courseTypes";

import pageStyles from "@/pages/Page.module.css";

export function CreateCoursePage() {
    const navigate = useNavigate();

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleCreateCourse(data: CourseFormData) {
        setErrorMessage("");
        setIsSubmitting(true);

        try {
            await createCourse(data);
            navigate("/teacher");
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while creating the course."
                );
            }
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <Link className={pageStyles.secondaryLink} to="/teacher">
                    Back to courses
                </Link>

                <div>
                    <p className={pageStyles.eyebrow}>Teacher courses</p>
                    <h1>Create course</h1>
                </div>

                <p className={pageStyles.description}>
                    Start with the basic course details. The course will be
                    saved as a draft.
                </p>

                <CourseForm
                    submitLabel="Create course"
                    errorMessage={errorMessage}
                    isSubmitting={isSubmitting}
                    onSubmit={handleCreateCourse}
                />
            </section>
        </main>
    );
}
