import type { GradeLevel } from "@/features/teacher-courses";

export type AiCourseGenerationFormData = {
    title: string;
    subject: string;
    gradeLevel: GradeLevel;
    description: string;
    syllabusText: string;
    moduleCount: number;
    includeAssignments: boolean;
    includeQuizzes: boolean;
};
