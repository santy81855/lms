import { useEffect, useState } from "react";
import { getQuizFeedback } from "../api/quizTakingApi";
import type { QuizFeedback, StudentQuizResult } from "../types/quizTakingTypes";
import styles from "./QuizFeedbackAccordion.module.css";
import { isApiError } from "@/api";
import type { Quiz } from "@/features/teacher-content";
import { AiQuizFeedback } from "./AiQuizFeedback";
import { LessonReferenceFeedback } from "./LessonReferenceFeedback";

type QuizFeedbackAccordionProps = {
    results : StudentQuizResult,
    quizMetadata : Quiz
}

export function QuizFeedbackAccordion(props : QuizFeedbackAccordionProps) {

    const [expanded, setExpanded] = useState(false);
    const [hasLoaded, setHasLoaded] = useState(false);
    const [errors, setErrors] = useState("");
    const[feedback, setFeedback] = useState<QuizFeedback | null>(null);

    // only fetch the course feedback when the student wants to open the feedback
    useEffect(() => {
        // don't need to fetch the same data twice
        if(hasLoaded){
            return;
        }

        getQuizFeedback(props.results.quizId)
            .then( (response) => {
                setFeedback(response);
            })
            .catch((error: unknown) => {
                if (isApiError(error)) {
                    setErrors(error.message);
                } else{
                    setErrors("Something went wrong while fetching submission feedback");
                }
            })
            .finally(() => {
                setHasLoaded(true);
            })

    }, [expanded])

    return (
        <div className="accordion-group">
            <details>
                <summary className={styles.summary}>View Feedback</summary>                
                {
                    hasLoaded && 
                    errors.length && 
                    feedback != null &&
                    <div className={styles.content}>
                        {feedback.content.map((res) => {
                            return <div key={res.questionNumber}>
                                {feedback.type === "aiOverview" && 
                                <AiQuizFeedback  content={res}/>}
                                
                                {feedback.type === "lessonReference" && 
                                <LessonReferenceFeedback content={res}/>}
                            </div>
                        })}
                    </div>
                }
            </details>
        </div>
    );
}

