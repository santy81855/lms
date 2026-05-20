import { apiClient } from "@/api";
import type { Course, CourseFormData } from "../types/courseTypes";

export function getTeacherCourses() {
    return apiClient<Course[]>("/api/courses/teacher");
}

export function getTeacherCourse(courseId: number) {
    return apiClient<Course>(`/api/courses/${courseId}`);
}

export function createCourse(data: CourseFormData) {
    return apiClient<Course>("/api/courses", {
        method: "POST",
        body: data,
    });
}

export function updateCourse(courseId: number, data: CourseFormData) {
    return apiClient<Course>(`/api/courses/${courseId}`, {
        method: "PUT",
        body: data,
    });
}

export async function publishCourse(courseId: number) {
    await apiClient<null>(`/api/courses/${courseId}/publish`, {
        method: "PUT",
    });
}

export async function archiveCourse(courseId: number) {
    await apiClient<null>(`/api/courses/${courseId}/archive`, {
        method: "PUT",
    });
}

export async function returnCourseToDraft(courseId: number) {
    await apiClient<null>(`/api/courses/${courseId}/draft`, {
        method: "PUT",
    });
}

export async function deleteCourse(courseId: number) {
    await apiClient<null>(`/api/courses/${courseId}`, {
        method: "DELETE",
    });
}
