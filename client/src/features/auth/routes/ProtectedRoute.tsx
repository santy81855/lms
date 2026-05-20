import type { ReactNode } from "react";
import { Navigate, Outlet, useLocation } from "react-router";

import { useAuth } from "../hooks/useAuth";

type ProtectedRouteProps = {
    children?: ReactNode;
};

export function ProtectedRoute({ children }: ProtectedRouteProps) {
    const location = useLocation();
    const { isLoading, isAuthenticated } = useAuth();

    if (isLoading) {
        return <p>Loading...</p>;
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace state={{ from: location }} />;
    }

    return children ?? <Outlet />;
}
