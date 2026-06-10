import { apiClient } from "@/api";
import type { Result } from "@/types/api";

import type {
    QuizFeedback,
    QuizQuestionFeedback,
    StudentQuiz,
    StudentQuizResult,
    StudentQuizSubmitRequest,
} from "../types/quizTakingTypes";

export function getStudentQuizForTaking(quizId: number) {
    return apiClient<StudentQuiz>(`/api/student/quizzes/${quizId}/take`);
}

export function submitStudentQuiz(
    quizId: number,
    data: StudentQuizSubmitRequest
) {
    return apiClient<StudentQuizResult>(
        `/api/student/quizzes/${quizId}/submit`,
        {
            method: "POST",
            body: data,
        },
    );
}

export function getLatestStudentQuizResult(quizId: number) {
    return apiClient<StudentQuizResult>(
        `/api/student/quizzes/${quizId}/latest-result`,
    );
}

export function getAllStudentQuizResults(quizId: number) {
    return apiClient<Result<StudentQuizResult[]>>(
        `/api/student/quizzes/${quizId}/all-results`,
    );
}

export function getQuizFeedback(resultId: number){
    return apiClient<QuizFeedback>(`/quizzes/${resultId}/submissions/{submissionId}/feedback`);
}