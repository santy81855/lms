import { useState, useRef } from "react";

import { Link, useNavigate } from "react-router";

import { isApiError } from "@/api";

import { generateCourseFromSyllabus } from "../api/aiCourseGenerationApi";
import { AiCourseGenerationForm } from "../components/AiCourseGenerationForm";
import type { AiCourseGenerationFormData } from "../types/aiCourseGenerationTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./AiCourseGenerationPage.module.css";

import BackToTop from "@/components/common/BackToTopButton";

export function AiCourseGenerationPage() {
    const navigate = useNavigate();

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    const scrollContainerRef = useRef<HTMLDivElement | null>(null);

    async function handleGenerateCourse(data: AiCourseGenerationFormData) {
        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const generatedCourse = await generateCourseFromSyllabus(data);

            navigate(`/teacher/courses/${generatedCourse.id}`, {
                replace: true,
            });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while generating the course."
                );
            }
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <main className={pageStyles.page} ref={scrollContainerRef}>
            <section className={pageStyles.content}>
                <Link className={pageStyles.secondaryLink} to="/teacher">
                    Back to courses
                </Link>

                <div>
                    <p className={pageStyles.eyebrow}>AI course generation</p>
                    <h1>Generate course from syllabus</h1>
                </div>

                <p className={pageStyles.description}>
                    Paste a syllabus or course outline, choose the structure,
                    and let the backend generate a draft course you can edit.
                </p>

                <div className={styles.noticeCard}>
                    <h2>What happens next?</h2>
                    <p>
                        The generated course will open on the normal course
                        detail page, where you can review modules, lessons,
                        quizzes, questions, and answer options using the same
                        teacher tools.
                    </p>
                </div>

                <AiCourseGenerationForm
                    errorMessage={errorMessage}
                    isSubmitting={isSubmitting}
                    onSubmit={handleGenerateCourse}
                />
            </section>
            <BackToTop
                divRef={scrollContainerRef}
            />
        </main>
    );
}
