import { Link } from "react-router";

import styles from "./Page.module.css";

export function LandingPage() {
    return (
        <main className={styles.page}>
            <section className={styles.content}>
                <p className={styles.eyebrow}>LMS Course Manager</p>

                <h1>Build, manage, and take courses in one place.</h1>

                <p className={styles.description}>
                    A lightweight learning management system for teachers and
                    students.
                </p>

                <div className={styles.actions}>
                    <Link className={styles.primaryLink} to="/login">
                        Log in
                    </Link>

                    <Link className={styles.secondaryLink} to="/register">
                        Create account
                    </Link>
                </div>
            </section>
        </main>
    );
}
