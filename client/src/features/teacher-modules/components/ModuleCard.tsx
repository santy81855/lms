import { Link } from "react-router";

import type { CourseModule } from "../types/moduleTypes";
import { ModuleStatusBadge } from "./ModuleStatusBadge";

import styles from "./ModuleCard.module.css";

type ModuleCardProps = {
    module: CourseModule;
};

export function ModuleCard({ module }: ModuleCardProps) {
    return (
        <article className={styles.card}>
            <div className={styles.header}>
                <div className={styles.titleGroup}>
                    <p className={styles.orderLabel}>
                        Module {module.moduleOrder}
                    </p>
                    <h3 className={styles.title}>{module.title}</h3>
                </div>

                <ModuleStatusBadge status={module.status} />
            </div>

            {module.description && (
                <p className={styles.description}>{module.description}</p>
            )}

            <Link
                className={styles.detailsLink}
                to={`/teacher/courses/${module.courseId}/modules/${module.id}`}
            >
                View module
            </Link>
        </article>
    );
}
