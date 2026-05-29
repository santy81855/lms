export type CourseStatus = "DRAFT" | "ACTIVE" | "ARCHIVED";

export type GradeLevel =
    | "ELEMENTARY"
    | "MIDDLE_SCHOOL"
    | "HIGH_SCHOOL"
    | "UNIVERSITY"
    | "OTHER";


export class Course {
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

    constructor(course?: Partial<Course>) {
        this.id = course?.id ?? 0;
        this.teacherId = course?.teacherId ?? 0;
        this.title = course?.title ?? "";
        this.subject = course?.subject ?? null;
        this.gradeLevel = course?.gradeLevel ?? "OTHER";
        this.description = course?.description ?? null;
        this.status = course?.status ?? "DRAFT";
        this.joinCode = course?.joinCode ?? "";
        this.createdAt = course?.createdAt ?? null;
        this.updatedAt = course?.updatedAt ?? null;
    }

    matchesSearch(searchContent: string): boolean {
        const search = searchContent.toLowerCase();

        return (
            this.title.toLowerCase().includes(search) ||
            (this.subject ?? "").toLowerCase().includes(search) ||
            (this.description ?? "").toLowerCase().includes(search)
        );
    }
}

export type CourseFormData = {
    title: string;
    subject: string;
    gradeLevel: GradeLevel;
    description: string;
};
