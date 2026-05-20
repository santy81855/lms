import { useState } from "react";
import { Link, NavLink, Outlet, useNavigate } from "react-router";

import { isApiError } from "@/api";
import { useAuth } from "@/features/auth";

import styles from "./AppLayout.module.css";

function getDashboardPath(roles: string[]) {
    if (roles.includes("TEACHER")) {
        return "/teacher";
    }

    if (roles.includes("STUDENT")) {
        return "/student";
    }

    return "/";
}

export function AppLayout() {
    const navigate = useNavigate();
    const { user, roles, logout } = useAuth();

    const [errorMessage, setErrorMessage] = useState("");
    const [isLoggingOut, setIsLoggingOut] = useState(false);

    async function handleLogout() {
        setErrorMessage("");
        setIsLoggingOut(true);

        try {
            await logout();
            navigate("/login", { replace: true });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage("Something went wrong while logging out.");
            }
        } finally {
            setIsLoggingOut(false);
        }
    }

    return (
        <div className={styles.layout}>
            <header className={styles.navbar}>
                <Link className={styles.brand} to={getDashboardPath(roles)}>
                    LMS
                </Link>

                <nav className={styles.navLinks} aria-label="Main navigation">
                    {roles.includes("TEACHER") && (
                        <NavLink
                            className={({ isActive }) =>
                                isActive
                                    ? `${styles.navLink} ${styles.active}`
                                    : styles.navLink
                            }
                            to="/teacher"
                        >
                            Teacher
                        </NavLink>
                    )}

                    {roles.includes("STUDENT") && (
                        <NavLink
                            className={({ isActive }) =>
                                isActive
                                    ? `${styles.navLink} ${styles.active}`
                                    : styles.navLink
                            }
                            to="/student"
                        >
                            Student
                        </NavLink>
                    )}
                </nav>

                <div className={styles.userArea}>
                    <span className={styles.userName}>
                        {user ? `${user.firstName} ${user.lastName}` : "User"}
                    </span>

                    <button
                        className={styles.logoutButton}
                        type="button"
                        onClick={handleLogout}
                        disabled={isLoggingOut}
                    >
                        {isLoggingOut ? "Logging out..." : "Logout"}
                    </button>
                </div>
            </header>

            {errorMessage && (
                <p className={styles.errorMessage}>{errorMessage}</p>
            )}

            <div className={styles.main}>
                <Outlet />
            </div>
        </div>
    );
}
