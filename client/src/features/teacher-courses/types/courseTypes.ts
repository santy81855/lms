export type CourseStatus = "DRAFT" | "ACTIVE" | "ARCHIVED";

export type GradeLevel =
    | "ELEMENTARY"
    | "MIDDLE_SCHOOL"
    | "HIGH_SCHOOL"
    | "UNIVERSITY"
    | "OTHER";

export type Course = {
    id: number;
    teacherId: number;
    title: string;
    subject: string | null;
    gradeLevel: GradeLevel;
    description: string | null;
    status: CourseStatus;
    joinCode: string;
    createdAt: string | null;
    updatedAt: string | null;
};

export type CourseFormData = {
    title: string;
    subject: string;
    gradeLevel: GradeLevel;
    description: string;
};
