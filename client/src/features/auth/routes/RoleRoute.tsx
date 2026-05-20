import type { ReactNode } from "react";
import { Navigate, Outlet } from "react-router";

import { useAuth } from "../hooks/useAuth";

import type { UserRole } from "../types/authTypes";

type RoleRouteProps = {
    allowedRoles: UserRole[];
    children?: ReactNode;
};

export function RoleRoute({ allowedRoles, children }: RoleRouteProps) {
    const { roles } = useAuth();

    const hasAllowedRole = allowedRoles.some((role) => roles.includes(role));

    if (!hasAllowedRole) {
        return <Navigate to="/unauthorized" replace />;
    }

    return children ?? <Outlet />;
}
