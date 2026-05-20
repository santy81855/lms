import { apiClient } from "@/api";

import type {
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
        }
    );
}

export function getLatestStudentQuizResult(quizId: number) {
    return apiClient<StudentQuizResult>(
        `/api/student/quizzes/${quizId}/latest-result`
    );
}
