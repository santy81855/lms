import { apiClient } from "@/api";

import type {
    StudentQuiz,
    StudentQuizResult,
    StudentQuizSubmitRequest,
} from "../types/quizTakingTypes";
import { defaultQuizAttemptRemaining, type QuizAttemptStatus } from "@/features/student-content";

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

export function getAttemptsRemaining(quizId: number){
    // mocking backend response until the API endpoint is merged in
    // remove this when merged
    return new Promise<QuizAttemptStatus>((resolve, reject) => {
        const quizAttempts : QuizAttemptStatus = {
            quizId: 0,
            attemptsAllowed: 2,
            attemptsUsed: 1,
            attemptsRemaining: 1,
            canTake: true
        };
        
        resolve(quizAttempts);
    })
    // TODO: uncomment the following code when the API endpoint is merged
    // return apiClient<QuizAttemptStatus>(`/api/quizzes/${quizId}/attempt-status`);
}