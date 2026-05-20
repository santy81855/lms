import { Link, useNavigate } from "react-router";

import { useAuth } from "@/features/auth";

import styles from "./LandingPage.module.css";

export function LandingPage() {
    const navigate = useNavigate();
    const { user, isAuthenticated, hasRole, logout } = useAuth();

    const dashboardPath = hasRole("TEACHER") ? "/teacher" : "/student";

    async function handleLogout() {
        await logout();
        navigate("/", { replace: true });
    }

    return (
        <main className={styles.page}>
            <nav className={styles.nav}>
                <Link className={styles.brand} to="/">
                    Course Manager
                </Link>

                <div className={styles.navActions}>
                    {isAuthenticated ? (
                        <>
                            <span className={styles.userLabel}>
                                {user?.firstName
                                    ? `Hi, ${user.firstName}`
                                    : "Signed in"}
                            </span>

                            <Link
                                className={styles.secondaryLink}
                                to={dashboardPath}
                            >
                                Dashboard
                            </Link>

                            <button
                                className={styles.textButton}
                                type="button"
                                onClick={handleLogout}
                            >
                                Log out
                            </button>
                        </>
                    ) : (
                        <>
                            <Link className={styles.secondaryLink} to="/login">
                                Log in
                            </Link>

                            <Link className={styles.primaryLink} to="/register">
                                Create account
                            </Link>
                        </>
                    )}
                </div>
            </nav>

            <section className={styles.hero}>
                <p className={styles.eyebrow}>
                    Learning management made simple
                </p>

                <h1>Build, teach, and take courses in one place.</h1>

                <p className={styles.description}>
                    Teachers can create courses, publish lessons and quizzes,
                    generate course drafts with AI, and view rosters. Students
                    can join courses, read lessons, take quizzes, and track
                    their progress.
                </p>

                <div className={styles.heroActions}>
                    {isAuthenticated ? (
                        <Link className={styles.primaryLink} to={dashboardPath}>
                            Go to dashboard
                        </Link>
                    ) : (
                        <>
                            <Link className={styles.primaryLink} to="/register">
                                Get started
                            </Link>

                            <Link className={styles.secondaryLink} to="/login">
                                I already have an account
                            </Link>
                        </>
                    )}
                </div>
            </section>
        </main>
    );
}
