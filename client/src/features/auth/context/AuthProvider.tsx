import {
    useCallback,
    useEffect,
    useMemo,
    useState,
    type ReactNode,
} from "react";

import { isApiError } from "@/api";

import {
    getCurrentUser,
    login as loginRequest,
    logout as logoutRequest,
    registerStudent as registerStudentRequest,
    registerTeacher as registerTeacherRequest,
} from "../api/authApi";

import { AuthContext } from "./authContext";

import type {
    AuthUser,
    LoginRequest,
    RegisterRequest,
    UserRole,
} from "../types/authTypes";

type AuthProviderProps = {
    children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
    const [user, setUser] = useState<AuthUser | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    const roles = useMemo(() => user?.roles ?? [], [user]);
    const isAuthenticated = user !== null;

    const refreshCurrentUser = useCallback(async () => {
        try {
            const currentUser = await getCurrentUser();
            setUser(currentUser);
        } catch (error) {
            if (
                isApiError(error) &&
                (error.status === 401 || error.status === 403)
            ) {
                setUser(null);
                return;
            }

            throw error;
        }
    }, []);

    useEffect(() => {
        async function restoreSession() {
            try {
                await refreshCurrentUser();
            } finally {
                setIsLoading(false);
            }
        }

        restoreSession();
    }, [refreshCurrentUser]);

    const login = useCallback(async (data: LoginRequest) => {
        const authenticatedUser = await loginRequest(data);
        setUser(authenticatedUser);
        return authenticatedUser;
    }, []);

    const registerStudent = useCallback(async (data: RegisterRequest) => {
        return registerStudentRequest(data);
    }, []);

    const registerTeacher = useCallback(async (data: RegisterRequest) => {
        return registerTeacherRequest(data);
    }, []);

    const logout = useCallback(async () => {
        try {
            await logoutRequest();
        } finally {
            setUser(null);
        }
    }, []);

    const hasRole = useCallback(
        (role: UserRole) => {
            return roles.includes(role);
        },
        [roles]
    );

    const value = useMemo(
        () => ({
            user,
            roles,
            isLoading,
            isAuthenticated,
            login,
            registerStudent,
            registerTeacher,
            logout,
            refreshCurrentUser,
            hasRole,
        }),
        [
            user,
            roles,
            isLoading,
            isAuthenticated,
            login,
            registerStudent,
            registerTeacher,
            logout,
            refreshCurrentUser,
            hasRole,
        ]
    );

    return (
        <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
    );
}
