import { Link } from "react-router";

import { LoginForm } from "../components/LoginForm";

import styles from "@/pages/Page.module.css";

export function LoginPage() {
    return (
        <main className={styles.page}>
            <section className={styles.content}>
                <Link className={styles.secondaryLink} to="/">
                    Back home
                </Link>

                <div>
                    <p className={styles.eyebrow}>Welcome back</p>
                    <h1>Log in to your account</h1>
                </div>

                <p className={styles.description}>
                    Use your teacher or student account to continue.
                </p>

                <LoginForm />
            </section>
        </main>
    );
}
