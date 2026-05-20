import type { Course } from "@/features/teacher-courses";
import { Link } from "react-router";
import styles from "./StudentCourseCard.module.css";

type StudentCourseCardProps = {
    course: Course;
};

function formatGradeLevel(gradeLevel: Course["gradeLevel"]) {
    return gradeLevel
        .split("_")
        .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
        .join(" ");
}

export function StudentCourseCard({ course }: StudentCourseCardProps) {
    return (
        <article className={styles.card}>
            <div className={styles.header}>
                <div className={styles.titleGroup}>
                    <p className={styles.meta}>
                        {course.subject || "No subject"} ·{" "}
                        {formatGradeLevel(course.gradeLevel)}
                    </p>

                    <h3 className={styles.title}>{course.title}</h3>
                </div>

                <span className={styles.statusBadge}>{course.status}</span>
            </div>

            {course.description && (
                <p className={styles.description}>{course.description}</p>
            )}

            <Link
                className={styles.detailsLink}
                to={`/student/courses/${course.id}`}
            >
                Open course
            </Link>
        </article>
    );
}
