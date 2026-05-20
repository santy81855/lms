import type { CourseStatus } from "../types/courseTypes";

import styles from "./CourseStatusBadge.module.css";

type CourseStatusBadgeProps = {
    status: CourseStatus;
};

const statusClassNames: Record<CourseStatus, string> = {
    DRAFT: styles.draft,
    ACTIVE: styles.active,
    ARCHIVED: styles.archived,
};

function formatStatus(status: CourseStatus) {
    return status.charAt(0) + status.slice(1).toLowerCase();
}

export function CourseStatusBadge({ status }: CourseStatusBadgeProps) {
    return (
        <span className={`${styles.badge} ${statusClassNames[status]}`}>
            {formatStatus(status)}
        </span>
    );
}
