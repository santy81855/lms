import { Link } from "react-router";

import type { ModuleContentItem } from "@/features/teacher-content";

import styles from "./StudentContentItemCard.module.css";

type StudentContentItemCardProps = {
    courseId: number;
    item: ModuleContentItem;
};

function formatItemType(itemType: ModuleContentItem["itemType"]) {
    return itemType.charAt(0) + itemType.slice(1).toLowerCase();
}

function getStudentItemPath(courseId: number, item: ModuleContentItem) {
    if (item.itemType === "LESSON") {
        return `/student/courses/${courseId}/lessons/${item.id}`;
    }

    if (item.itemType === "QUIZ") {
        return `/student/courses/${courseId}/quizzes/${item.id}`;
    }

    return null;
}

export function StudentContentItemCard({
    courseId,
    item,
}: StudentContentItemCardProps) {
    const itemTypeLabel = formatItemType(item.itemType);
    const itemPath = getStudentItemPath(courseId, item);

    return (
        <article className={styles.card}>
            <div>
                <p className={styles.meta}>
                    {itemTypeLabel} {item.itemOrder}
                </p>

                <h4 className={styles.title}>{item.title}</h4>
            </div>

            {itemPath ? (
                <Link className={styles.detailsLink} to={itemPath}>
                    Open {itemTypeLabel.toLowerCase()}
                </Link>
            ) : (
                <p className={styles.mutedText}>
                    {itemTypeLabel} is skipped for the MVP.
                </p>
            )}
        </article>
    );
}
