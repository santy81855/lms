import type { CourseRosterStudent } from "../types/rosterTypes";
import { RosterStatusBadge } from "./RosterStatusBadge";

import styles from "./RosterStudentCard.module.css";

type RosterStudentCardProps = {
    student: CourseRosterStudent;
};

function formatDate(value: string | null) {
    if (!value) {
        return "—";
    }

    return new Intl.DateTimeFormat(undefined, {
        dateStyle: "medium",
        timeStyle: "short",
    }).format(new Date(value));
}

export function RosterStudentCard({ student }: RosterStudentCardProps) {
    const fullName = `${student.firstName} ${student.lastName}`;

    return (
        <article className={styles.card}>
            <div className={styles.header}>
                <div>
                    <p className={styles.meta}>Student #{student.studentId}</p>
                    <h3 className={styles.title}>{fullName}</h3>
                </div>

                <RosterStatusBadge status={student.enrollmentStatus} />
            </div>

            <dl className={styles.detailList}>
                <div>
                    <dt>Email</dt>
                    <dd>{student.email}</dd>
                </div>

                <div>
                    <dt>Enrolled</dt>
                    <dd>{formatDate(student.enrolledAt)}</dd>
                </div>

                <div>
                    <dt>Completed</dt>
                    <dd>{formatDate(student.completedAt)}</dd>
                </div>
            </dl>
        </article>
    );
}
