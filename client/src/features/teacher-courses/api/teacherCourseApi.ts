import { apiClient } from "@/api";
import { Course } from "../types/courseTypes";
import type { CourseFormData } from "../types/courseTypes";

export async function getTeacherCourses() {
    const courses = await apiClient<Course[]>("/api/courses/teacher");

    return courses.map(course => new Course(course));
}

export async function getTeacherCourse(courseId: number) {
    const course = await apiClient<Course>(`/api/courses/${courseId}`);

    return new Course(course);
}

export async function createCourse(data: CourseFormData) {
    const course = await apiClient<Course>("/api/courses", {
        method: "POST",
        body: data,
    });

    return new Course(course);
}

export async function updateCourse(courseId: number, data: CourseFormData) {
    const course = await apiClient<Course>(`/api/courses/${courseId}`, {
        method: "PUT",
        body: data,
    });

    return new Course(course);
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
