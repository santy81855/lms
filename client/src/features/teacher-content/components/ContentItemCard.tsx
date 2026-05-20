import { Link } from "react-router";

import { ModuleStatusBadge } from "@/features/teacher-modules";

import type { ModuleContentItem } from "../types/contentTypes";

import styles from "./ContentItemCard.module.css";

type ContentItemCardProps = {
    courseId: number;
    item: ModuleContentItem;
};

function formatItemType(itemType: ModuleContentItem["itemType"]) {
    return itemType.charAt(0) + itemType.slice(1).toLowerCase();
}

function getItemDetailPath(courseId: number, item: ModuleContentItem) {
    if (item.itemType === "LESSON") {
        return `/teacher/courses/${courseId}/modules/${item.moduleId}/lessons/${item.id}`;
    }

    if (item.itemType === "QUIZ") {
        return `/teacher/courses/${courseId}/modules/${item.moduleId}/quizzes/${item.id}`;
    }

    return null;
}

export function ContentItemCard({ courseId, item }: ContentItemCardProps) {
    const detailPath = getItemDetailPath(courseId, item);

    return (
        <article className={styles.card}>
            <div className={styles.header}>
                <div className={styles.titleGroup}>
                    <p className={styles.meta}>
                        {formatItemType(item.itemType)} {item.itemOrder}
                    </p>

                    <h3 className={styles.title}>{item.title}</h3>
                </div>

                <ModuleStatusBadge status={item.status} />
            </div>

            {detailPath ? (
                <Link className={styles.detailsLink} to={detailPath}>
                    View {formatItemType(item.itemType).toLowerCase()}
                </Link>
            ) : (
                <p className={styles.mutedText}>
                    {formatItemType(item.itemType)} details skipped for MVP.
                </p>
            )}
        </article>
    );
}
