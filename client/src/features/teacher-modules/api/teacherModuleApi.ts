import { apiClient } from "@/api";

import type { CourseModule, ModuleFormData } from "../types/moduleTypes";

export function getCourseModules(courseId: number) {
    return apiClient<CourseModule[]>(`/api/courses/${courseId}/modules`);
}

export function createCourseModule(courseId: number, data: ModuleFormData) {
    return apiClient<CourseModule>(`/api/courses/${courseId}/modules`, {
        method: "POST",
        body: data,
    });
}

export function getCourseModule(moduleId: number) {
    return apiClient<CourseModule>(`/api/modules/${moduleId}`);
}

export function updateCourseModule(moduleId: number, data: ModuleFormData) {
    return apiClient<CourseModule>(`/api/modules/${moduleId}`, {
        method: "PUT",
        body: data,
    });
}

export async function publishCourseModule(moduleId: number) {
    await apiClient<null>(`/api/modules/${moduleId}/publish`, {
        method: "PUT",
    });
}

export async function archiveCourseModule(moduleId: number) {
    await apiClient<null>(`/api/modules/${moduleId}/archive`, {
        method: "PUT",
    });
}

export async function returnCourseModuleToDraft(moduleId: number) {
    await apiClient<null>(`/api/modules/${moduleId}/draft`, {
        method: "PUT",
    });
}

export async function moveCourseModule(moduleId: number, moduleOrder: number) {
    await apiClient<null>(
        `/api/modules/${moduleId}/move?moduleOrder=${encodeURIComponent(
            moduleOrder
        )}`,
        {
            method: "PUT",
        }
    );
}

export async function deleteCourseModule(moduleId: number) {
    await apiClient<null>(`/api/modules/${moduleId}`, {
        method: "DELETE",
    });
}
