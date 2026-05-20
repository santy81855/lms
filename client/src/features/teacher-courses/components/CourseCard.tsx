import { Link } from "react-router";

import type { Course } from "../types/courseTypes";
import { CourseStatusBadge } from "./CourseStatusBadge";

import styles from "./CourseCard.module.css";

type CourseCardProps = {
    course: Course;
};

function formatGradeLevel(gradeLevel: Course["gradeLevel"]) {
    return gradeLevel
        .split("_")
        .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
        .join(" ");
}

export function CourseCard({ course }: CourseCardProps) {
    return (
        <article className={styles.card}>
            <div className={styles.header}>
                <div className={styles.titleGroup}>
                    <h2 className={styles.title}>{course.title}</h2>

                    <p className={styles.meta}>
                        {course.subject || "No subject"} ·{" "}
                        {formatGradeLevel(course.gradeLevel)}
                    </p>
                </div>

                <CourseStatusBadge status={course.status} />
            </div>

            {course.description && (
                <p className={styles.description}>{course.description}</p>
            )}

            <div className={styles.footer}>
                <span className={styles.joinCode}>
                    Join code: {course.joinCode}
                </span>

                <Link
                    className={styles.detailsLink}
                    to={`/teacher/courses/${course.id}`}
                >
                    View details
                </Link>
            </div>
        </article>
    );
}
