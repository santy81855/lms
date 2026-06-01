import { apiClient } from "@/api";

import type {
    QuizAnswerOption,
    QuizAnswerOptionFormData,
    QuizQuestion,
    QuizQuestionFormData,
} from "../types/quizAuthoringTypes";

function encodeOrder(order: number) {
    return encodeURIComponent(order);
}

/**
 * Quiz questions
 */

export function getQuizQuestions(quizId: number) {
    return apiClient<QuizQuestion[]>(`/api/quizzes/${quizId}/questions`);
}

export function createQuizQuestion(quizId: number, data: QuizQuestionFormData) {
    return apiClient<QuizQuestion>(`/api/quizzes/${quizId}/questions`, {
        method: "POST",
        body: data,
    });
}

export function updateQuizQuestion(
    questionId: number,
    data: QuizQuestionFormData
) {
    return apiClient<QuizQuestion>(`/api/questions/${questionId}`, {
        method: "PUT",
        body: data,
    });
}

export async function moveQuizQuestion(
    questionId: number,
    questionOrder: number
) {
    await apiClient<null>(
        `/api/questions/${questionId}/move?questionOrder=${encodeOrder(
            questionOrder
        )}`,
        {
            method: "PUT",
        }
    );
}

export async function deleteQuizQuestion(questionId: number) {
    await apiClient<null>(`/api/questions/${questionId}`, {
        method: "DELETE",
    });
}

/**
 * Answer options
 */

export function getQuestionAnswerOptions(questionId: number) {
    return apiClient<QuizAnswerOption[]>(
        `/api/questions/${questionId}/options`
    );
}

export function createQuizAnswerOption(
    questionId: number,
    data: QuizAnswerOptionFormData
) {
    return apiClient<QuizAnswerOption>(`/api/questions/${questionId}/options`, {
        method: "POST",
        body: data,
    });
}

export function updateQuizAnswerOption(
    optionId: number,
    data: QuizAnswerOptionFormData
) {
    return apiClient<QuizAnswerOption>(`/api/options/${optionId}`, {
        method: "PUT",
        body: data,
    });
}

export async function moveQuizAnswerOption(
    optionId: number,
    optionOrder: number
) {
    await apiClient<null>(
        `/api/options/${optionId}/move?optionOrder=${encodeOrder(optionOrder)}`,
        {
            method: "PUT",
        }
    );
}

export async function deleteQuizAnswerOption(optionId: number) {
    await apiClient<null>(`/api/options/${optionId}`, {
        method: "DELETE",
    });
}

export async function fetchLessons(courseId: id){
    
}
