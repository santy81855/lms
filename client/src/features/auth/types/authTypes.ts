export type UserRole = "STUDENT" | "TEACHER" | "ADMIN";

export type AuthUser = {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    accountStatus: string;
    roles: UserRole[];
};

export type LoginRequest = {
    email: string;
    password: string;
};

export type RegisterRequest = {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
};
