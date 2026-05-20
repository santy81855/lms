import { apiClient } from "@/api";
import type { Course } from "@/features/teacher-courses";

import type {
    CourseEnrollment,
    JoinCourseFormData,
} from "../types/studentCourseTypes";

export function getStudentCourses() {
    return apiClient<Course[]>("/api/student/courses");
}

export function getStudentEnrollments() {
    return apiClient<CourseEnrollment[]>("/api/student/enrollments");
}

export function joinCourse(data: JoinCourseFormData) {
    return apiClient<CourseEnrollment>("/api/student/courses/join", {
        method: "POST",
        body: data,
    });
}

export async function dropStudentCourse(courseId: number) {
    await apiClient<null>(`/api/student/courses/${courseId}/drop`, {
        method: "PUT",
    });
}

export async function completeStudentCourse(courseId: number) {
    await apiClient<null>(`/api/student/courses/${courseId}/complete`, {
        method: "PUT",
    });
}
