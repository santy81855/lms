import { createContext } from "react";

import type {
    AuthUser,
    LoginRequest,
    RegisterRequest,
    UserRole,
} from "../types/authTypes";

export type AuthContextValue = {
    user: AuthUser | null;
    roles: UserRole[];
    isLoading: boolean;
    isAuthenticated: boolean;
    login: (data: LoginRequest) => Promise<AuthUser>;
    registerStudent: (data: RegisterRequest) => Promise<AuthUser>;
    registerTeacher: (data: RegisterRequest) => Promise<AuthUser>;
    logout: () => Promise<void>;
    refreshCurrentUser: () => Promise<void>;
    hasRole: (role: UserRole) => boolean;
};

export const AuthContext = createContext<AuthContextValue | null>(null);
