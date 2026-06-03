import { Link } from "react-router";

import type { ModuleContentItem } from "@/features/teacher-content";

import styles from "./StudentContentItemCard.module.css";
import type { QuizAttemptStatus } from "../types/studentContentTypes";
import { defaultQuizAttemptRemaining } from "../types/studentContentTypes";
import { useEffect, useState } from "react";
import { getAttemptsRemaining } from "@/features/quiz-taking";
import { isApiError } from "@/api";

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
    const [quizAttemptsRemaining, setQuizAttemptsRemaining] = useState<QuizAttemptStatus>(defaultQuizAttemptRemaining);
    const [isLoadingDetails, setIsLoadingDetails] = useState(true);
    const [errors, setErrors] = useState("");

    useEffect(() => {
        if(item.itemType !== "QUIZ"){
            return;
        }

        getAttemptsRemaining(item.id)
            .then(attempts => {
                setQuizAttemptsRemaining(attempts);
            }
        ).catch((error: unknown) => {
            if(isApiError(error)){
                setErrors(error.message);
            } else{
                setErrors("Something went wrong while fetching data for this quiz");
            }
        }).finally(() => {            
            setIsLoadingDetails(false);
        })
    }, []);

    return (
        <article className={styles.card}>
            { errors && 
                <div>
                    <p className={styles.errorText}>{errors}</p>
                </div>}

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
            {item.itemType === "QUIZ" 
                && !errors
                && !isLoadingDetails 
                && <p className={styles.mutedText}>
                    attempts remaining: {quizAttemptsRemaining.attemptsRemaining} / {quizAttemptsRemaining.attemptsAllowed}
                </p>}
        </article>
    );
}
