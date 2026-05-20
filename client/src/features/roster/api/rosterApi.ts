import { apiClient } from "@/api";

import type { CourseRosterStudent } from "../types/rosterTypes";

export function getCourseRoster(courseId: number) {
    return apiClient<CourseRosterStudent[]>(
        `/api/courses/${courseId}/students`
    );
}
