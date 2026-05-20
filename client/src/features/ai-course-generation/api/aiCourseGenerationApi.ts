import { apiClient } from "@/api";
import type { Course } from "@/features/teacher-courses";

import type { AiCourseGenerationFormData } from "../types/aiCourseGenerationTypes.ts";

export function generateCourseFromSyllabus(data: AiCourseGenerationFormData) {
    return apiClient<Course>("/api/courses/ai-generate", {
        method: "POST",
        body: data,
    });
}
