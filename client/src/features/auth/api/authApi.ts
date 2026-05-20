import { apiClient } from "@/api";
import type {
    AuthUser,
    LoginRequest,
    RegisterRequest,
} from "../types/authTypes";

export function getCurrentUser() {
    return apiClient<AuthUser>("/api/auth/me");
}

export function login(data: LoginRequest) {
    return apiClient<AuthUser>("/api/auth/login", {
        method: "POST",
        body: data,
    });
}

export function registerStudent(data: RegisterRequest) {
    return apiClient<AuthUser>("/api/auth/register/student", {
        method: "POST",
        body: data,
    });
}

export function registerTeacher(data: RegisterRequest) {
    return apiClient<AuthUser>("/api/auth/register/teacher", {
        method: "POST",
        body: data,
    });
}

export async function logout() {
    await apiClient<null>("/api/auth/logout", {
        method: "POST",
    });
}
