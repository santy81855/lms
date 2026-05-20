import type { EnrollmentStatus } from "@/features/student-courses";

export type CourseRosterStudent = {
    studentId: number;
    firstName: string;
    lastName: string;
    email: string;
    enrollmentStatus: EnrollmentStatus;
    enrolledAt: string | null;
    completedAt: string | null;
};
