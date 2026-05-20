import type { StudentModuleWithContent } from "../types/studentContentTypes";
import { StudentContentItemCard } from "./StudentContentItemCard";

import styles from "./StudentModuleSection.module.css";

type StudentModuleSectionProps = {
    courseId: number;
    moduleWithContent: StudentModuleWithContent;
};

export function StudentModuleSection({
    courseId,
    moduleWithContent,
}: StudentModuleSectionProps) {
    const { module, contentItems } = moduleWithContent;

    return (
        <article className={styles.card}>
            <div className={styles.header}>
                <div>
                    <p className={styles.meta}>Module {module.moduleOrder}</p>
                    <h3 className={styles.title}>{module.title}</h3>
                </div>
            </div>

            {module.description && (
                <p className={styles.description}>{module.description}</p>
            )}

            {contentItems.length === 0 && (
                <div className={styles.emptyState}>
                    <p>No published content in this module yet.</p>
                </div>
            )}

            {contentItems.length > 0 && (
                <div className={styles.contentList}>
                    {contentItems.map((item) => (
                        <StudentContentItemCard
                            key={`${item.itemType}-${item.id}`}
                            courseId={courseId}
                            item={item}
                        />
                    ))}
                </div>
            )}
        </article>
    );
}
