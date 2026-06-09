import { isAfter, subHours } from "date-fns";

import styles from "./RecentlyUpdatedBadge.module.css";

type RecentlyUpdatedBadgeProps = {
    updatedAt: string | null;
};

export function RecentlyUpdatedBadge({
    updatedAt,
}: RecentlyUpdatedBadgeProps) {
    if (
        !updatedAt ||
        !isAfter(
            new Date(updatedAt),
            subHours(new Date(), 24)
        )
    ) {
        return null;
    }

    return (
        <span className={styles.recentlyUpdated}>
            Recently Updated
        </span>
    );
}