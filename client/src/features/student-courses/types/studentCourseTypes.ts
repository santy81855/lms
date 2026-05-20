export type EnrollmentStatus = "ACTIVE" | "DROPPED" | "COMPLETED";

export type CourseEnrollment = {
    id: number;
    courseId: number;
    studentId: number;
    enrollmentStatus: EnrollmentStatus;
    enrolledAt: string | null;
    completedAt: string | null;
};

export type JoinCourseFormData = {
    joinCode: string;
};
