import type { ModuleContentItem } from "@/features/teacher-content";
import type { CourseModule } from "@/features/teacher-modules";

export type StudentModuleWithContent = {
    module: CourseModule;
    contentItems: ModuleContentItem[];
};

export type QuizAttemptStatus = {
    quizId: number;
    attemptsAllowed: number;
    attemptsUsed: number;
    attemptsRemaining: number;
    canTake: boolean;
}