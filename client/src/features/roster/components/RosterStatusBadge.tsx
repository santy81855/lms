import type { EnrollmentStatus } from "@/features/student-courses";

import styles from "./RosterStatusBadge.module.css";

type RosterStatusBadgeProps = {
    status: EnrollmentStatus;
};

export function RosterStatusBadge({ status }: RosterStatusBadgeProps) {
    return <span className={styles.badge}>{status}</span>;
}
