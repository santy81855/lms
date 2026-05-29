import { Link } from "react-router";

import type { Course } from "../types/courseTypes";
import { CourseStatusBadge } from "./CourseStatusBadge";
import { useEffect, useState } from "react";

import styles from "./CourseCard.module.css";

type CourseCardProps = {
    course: Course;
};

function formatGradeLevel(gradeLevel: Course["gradeLevel"]) {
    return gradeLevel
        .split("_")
        .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
        .join(" ");
}


export function CourseCard({ course }: CourseCardProps) {
    // controlls when the popup should appear
    const [visible, setVisible] = useState(false);
    // controlls the use effect that triggers when
    // the component should appear.  This prevents
    // recursively re-rendering the component in the useEffect()
    const [clicked, setClicked] = useState(1);
    // this state is used to unmount the html from the DOM
    // after it has finished fading out.  This state is necessary
    // because unmounting from the DOM using the visible state
    // will immediately make the component disappear, skipping
    // the animation
    const [mounted, setMounted] = useState(false);

    async function copyCode(){
        try{
            await navigator.clipboard.writeText(course.joinCode);
            setVisible(true);
            setClicked(clicked + 1);
            setMounted(true);
            console.log("fading in")
        } catch (error){
            console.error("Failed to copy join code:", error);
        }
    };

    useEffect(() => {
        const fadeOut = async () => {
            // let the component remain fully rendered for 1 second before fading out
            await new Promise(f => setTimeout(f, 1000));
            setVisible(false);
            // let the fade-out animation play for 1 second before removing the component from the DOM
            await new Promise(f => setTimeout(f, 1000));
            setMounted(false);

        }
        fadeOut();
    }, [clicked])
    
    return (
        <article className={styles.card}>
            {mounted ? 
                <div 
                    className={ visible ? styles.visible : styles.fadeOut}
                    aria-hidden={visible}
                    >
                    <p>copied course code</p>
                </div>
            : <></>}

            <div className={styles.header}>
                <div className={styles.titleGroup}>
                    <h2 className={styles.title}>{course.title}</h2>

                    <p className={styles.meta}>
                        {course.subject || "No subject"} ·{" "}
                        {formatGradeLevel(course.gradeLevel)}
                    </p>
                </div>

                <CourseStatusBadge status={course.status} />
            </div>

            {course.description && (
                <p className={styles.description}>{course.description}</p>
            )}

            <div className={styles.footer}>
                
                <span className={styles.joinCode}>
                    <button onClick={() => copyCode()}>
                        Join code: {course.joinCode}
                    </button>
                </span>



                <Link
                    className={styles.detailsLink}
                    to={`/teacher/courses/${course.id}`}
                >
                    View details
                </Link>
            </div>
        </article>
    );
}
