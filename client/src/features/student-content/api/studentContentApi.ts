import { apiClient } from "@/api";
import type { Course } from "@/features/teacher-courses";
import type { CourseModule } from "@/features/teacher-modules";
import type {
    Lesson,
    ModuleContentItem,
    Quiz,
} from "@/features/teacher-content";

export function getStudentCourse(courseId: number) {
    return apiClient<Course>(`/api/student/courses/${courseId}`);
}

export function getStudentCourseModules(courseId: number) {
    return apiClient<CourseModule[]>(
        `/api/student/courses/${courseId}/modules`
    );
}

export function getStudentModuleContentItems(moduleId: number) {
    return apiClient<ModuleContentItem[]>(
        `/api/student/modules/${moduleId}/content`
    );
}

export function getStudentLesson(lessonId: number) {
    return apiClient<Lesson>(`/api/student/lessons/${lessonId}`);
}

export function getStudentQuiz(quizId: number) {
    // const mockQuiz : Quiz = {
    //     id: 22,
    //     moduleId: 0,
    //     title: "",
    //     description: null,
    //     quizOrder: 0,
    //     maxPoints: 10,
    //     timeLimitMinutes: null,
    //     attemptsAllowed: 2,
    //     status: "PUBLISHED",
    //     createdAt: null,
    //     updatedAt: null,
    //     publishedAt: null,
    //     feedbackTypeCode: "SCORE"
    // }
    // return new Promise<Quiz>((resolve, reject) => {
    //     resolve(mockQuiz);
    // });

    return apiClient<Quiz>(`/api/student/quizzes/${quizId}`);
}
