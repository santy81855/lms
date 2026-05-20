import type { VisibilityStatus } from "@/features/teacher-modules";

export type ContentItemType = "LESSON" | "ASSIGNMENT" | "QUIZ";

export type SubmissionType = "TEXT" | "FILE";

export type ModuleContentItem = {
    id: number;
    moduleId: number;
    title: string;
    itemType: ContentItemType;
    itemOrder: number;
    status: VisibilityStatus;
};

export type Lesson = {
    id: number;
    moduleId: number;
    title: string;
    content: string | null;
    lessonOrder: number;
    estimatedMinutes: number | null;
    status: VisibilityStatus;
    createdAt: string | null;
    updatedAt: string | null;
    publishedAt: string | null;
};

export type Assignment = {
    id: number;
    moduleId: number;
    title: string;
    instructions: string | null;
    assignmentOrder: number;
    dueAt: string | null;
    maxPoints: number;
    submissionType: SubmissionType;
    status: VisibilityStatus;
    createdAt: string | null;
    updatedAt: string | null;
    publishedAt: string | null;
};

export type Quiz = {
    id: number;
    moduleId: number;
    title: string;
    description: string | null;
    quizOrder: number;
    maxPoints: number;
    timeLimitMinutes: number | null;
    attemptsAllowed: number;
    status: VisibilityStatus;
    createdAt: string | null;
    updatedAt: string | null;
    publishedAt: string | null;
};

export type LessonFormData = {
    title: string;
    content: string;
    lessonOrder?: number | null;
    estimatedMinutes?: number | null;
};

export type AssignmentFormData = {
    title: string;
    instructions: string;
    assignmentOrder?: number | null;
    dueAt?: string | null;
    maxPoints: number;
    submissionType: SubmissionType;
};

export type QuizFormData = {
    title: string;
    description: string;
    quizOrder?: number | null;
    maxPoints: number;
    timeLimitMinutes?: number | null;
    attemptsAllowed: number;
};
