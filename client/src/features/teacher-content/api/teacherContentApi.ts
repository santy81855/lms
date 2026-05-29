import { apiClient } from "@/api";

import type {
    Assignment,
    AssignmentFormData,
    Lesson,
    LessonFormData,
    ModuleContentItem,
    Quiz,
    QuizFormData,
    QuizSubmission,
    QuizSubmissionContainer,
} from "../types/contentTypes";

function encodeOrder(order: number) {
    return encodeURIComponent(order);
}

/**
 * Combined module content
 */

export function getModuleContentItems(moduleId: number) {
    return apiClient<ModuleContentItem[]>(`/api/modules/${moduleId}/content`);
}

/**
 * Lessons
 */

export function getModuleLessons(moduleId: number) {
    return apiClient<Lesson[]>(`/api/modules/${moduleId}/lessons`);
}

export function createLesson(moduleId: number, data: LessonFormData) {
    return apiClient<Lesson>(`/api/modules/${moduleId}/lessons`, {
        method: "POST",
        body: data,
    });
}

export function updateLesson(lessonId: number, data: LessonFormData) {
    return apiClient<Lesson>(`/api/lessons/${lessonId}`, {
        method: "PUT",
        body: data,
    });
}

export async function publishLesson(lessonId: number) {
    await apiClient<null>(`/api/lessons/${lessonId}/publish`, {
        method: "PUT",
    });
}

export async function archiveLesson(lessonId: number) {
    await apiClient<null>(`/api/lessons/${lessonId}/archive`, {
        method: "PUT",
    });
}

export async function returnLessonToDraft(lessonId: number) {
    await apiClient<null>(`/api/lessons/${lessonId}/draft`, {
        method: "PUT",
    });
}

export async function moveLesson(lessonId: number, lessonOrder: number) {
    await apiClient<null>(
        `/api/lessons/${lessonId}/move?lessonOrder=${encodeOrder(lessonOrder)}`,
        {
            method: "PUT",
        }
    );
}

export async function deleteLesson(lessonId: number) {
    await apiClient<null>(`/api/lessons/${lessonId}`, {
        method: "DELETE",
    });
}

/**
 * Assignments
 */

export function getModuleAssignments(moduleId: number) {
    return apiClient<Assignment[]>(`/api/modules/${moduleId}/assignments`);
}

export function createAssignment(moduleId: number, data: AssignmentFormData) {
    return apiClient<Assignment>(`/api/modules/${moduleId}/assignments`, {
        method: "POST",
        body: data,
    });
}

export function updateAssignment(
    assignmentId: number,
    data: AssignmentFormData
) {
    return apiClient<Assignment>(`/api/assignments/${assignmentId}`, {
        method: "PUT",
        body: data,
    });
}

export async function publishAssignment(assignmentId: number) {
    await apiClient<null>(`/api/assignments/${assignmentId}/publish`, {
        method: "PUT",
    });
}

export async function archiveAssignment(assignmentId: number) {
    await apiClient<null>(`/api/assignments/${assignmentId}/archive`, {
        method: "PUT",
    });
}

export async function returnAssignmentToDraft(assignmentId: number) {
    await apiClient<null>(`/api/assignments/${assignmentId}/draft`, {
        method: "PUT",
    });
}

export async function moveAssignment(
    assignmentId: number,
    assignmentOrder: number
) {
    await apiClient<null>(
        `/api/assignments/${assignmentId}/move?assignmentOrder=${encodeOrder(
            assignmentOrder
        )}`,
        {
            method: "PUT",
        }
    );
}

export async function deleteAssignment(assignmentId: number) {
    await apiClient<null>(`/api/assignments/${assignmentId}`, {
        method: "DELETE",
    });
}

/**
 * Quizzes
 */

export function getModuleQuizzes(moduleId: number) {
    return apiClient<Quiz[]>(`/api/modules/${moduleId}/quizzes`);
}

export function createQuiz(moduleId: number, data: QuizFormData) {
    return apiClient<Quiz>(`/api/modules/${moduleId}/quizzes`, {
        method: "POST",
        body: data,
    });
}

export function updateQuiz(quizId: number, data: QuizFormData) {
    return apiClient<Quiz>(`/api/quizzes/${quizId}`, {
        method: "PUT",
        body: data,
    });
}

export async function publishQuiz(quizId: number) {
    await apiClient<null>(`/api/quizzes/${quizId}/publish`, {
        method: "PUT",
    });
}

export async function archiveQuiz(quizId: number) {
    await apiClient<null>(`/api/quizzes/${quizId}/archive`, {
        method: "PUT",
    });
}

export async function returnQuizToDraft(quizId: number) {
    await apiClient<null>(`/api/quizzes/${quizId}/draft`, {
        method: "PUT",
    });
}

export async function moveQuiz(quizId: number, quizOrder: number) {
    await apiClient<null>(
        `/api/quizzes/${quizId}/move?quizOrder=${encodeOrder(quizOrder)}`,
        {
            method: "PUT",
        }
    );
}

export async function deleteQuiz(quizId: number) {
    await apiClient<null>(`/api/quizzes/${quizId}`, {
        method: "DELETE",
    });
}

export async function getQuizSubmissions(quizId: number) : Promise<QuizSubmissionContainer>{
    return await apiClient<QuizSubmissionContainer>(`/api/quizzes/${quizId}/submissions`);
}
