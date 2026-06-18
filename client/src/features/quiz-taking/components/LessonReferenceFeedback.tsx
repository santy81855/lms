import { Link } from "react-router";
import type { QuizQuestionFeedback } from "../types/quizTakingTypes";
import styles from "./QuizFeedbackCard.module.css"
import { LessonReferenceWithMissingRef } from "./LessonReferenceWithMissingRef";
import { LessonReferenceFeedbackWithReference } from "./LessonReferenceFeedbackWithReference";

type LessonReferenceFeedbackProps = {
    content: QuizQuestionFeedback;
}

export function LessonReferenceFeedback( { content } : LessonReferenceFeedbackProps){

    return (
    content.feedback !== null && content.feedback.length !== 0 ? 
        <LessonReferenceFeedbackWithReference content={content} />
        :
        <LessonReferenceWithMissingRef content={content}/>
    );
}