export type VisibilityStatus = "DRAFT" | "PUBLISHED" | "ARCHIVED";

export type CourseModule = {
    id: number;
    courseId: number;
    title: string;
    description: string | null;
    moduleOrder: number;
    status: VisibilityStatus;
    createdAt: string | null;
    updatedAt: string | null;
    publishedAt: string | null;
};

export type ModuleFormData = {
    title: string;
    description: string;
    moduleOrder?: number | null;
};
