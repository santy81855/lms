import { useEffect, useState } from "react";
import { getQuizFeedback } from "../api/quizTakingApi";
import type { QuizFeedback, StudentQuizResult } from "../types/quizTakingTypes";
import styles from "./QuizFeedbackAccordion.module.css";
import { isApiError } from "@/api";


export function QuizFeedbackAccordion({ result } : {result : StudentQuizResult}) {

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

        getQuizFeedback(result.quizId)
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
                        <p>Feedback list goes here</p>
                    </div>
                }
            </details>
        </div>
    );
}

