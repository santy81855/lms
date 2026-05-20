import { Link } from "react-router";

import { RegisterForm } from "../components/RegisterForm";

import styles from "@/pages/Page.module.css";

export function RegisterPage() {
    return (
        <main className={styles.page}>
            <section className={styles.content}>
                <Link className={styles.secondaryLink} to="/">
                    Back home
                </Link>

                <div>
                    <p className={styles.eyebrow}>Get started</p>
                    <h1>Create your account</h1>
                </div>

                <p className={styles.description}>
                    Register as a student or teacher, then continue into your
                    dashboard.
                </p>

                <RegisterForm />
            </section>
        </main>
    );
}
