import type { VisibilityStatus } from "../types/moduleTypes";

import styles from "./ModuleStatusBadge.module.css";

type ModuleStatusBadgeProps = {
    status: VisibilityStatus;
};

const statusClassNames: Record<VisibilityStatus, string> = {
    DRAFT: styles.draft,
    PUBLISHED: styles.published,
    ARCHIVED: styles.archived,
};

function formatStatus(status: VisibilityStatus) {
    return status.charAt(0) + status.slice(1).toLowerCase();
}

export function ModuleStatusBadge({ status }: ModuleStatusBadgeProps) {
    return (
        <span className={`${styles.badge} ${statusClassNames[status]}`}>
            {formatStatus(status)}
        </span>
    );
}
