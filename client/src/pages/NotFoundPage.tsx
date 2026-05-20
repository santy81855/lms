import { Link } from "react-router";

import styles from "./Page.module.css";

export function NotFoundPage() {
    return (
        <main className={styles.page}>
            <section className={styles.content}>
                <h1>Page not found</h1>

                <p className={styles.description}>
                    The page you are looking for does not exist.
                </p>

                <Link className={styles.primaryLink} to="/">
                    Go home
                </Link>
            </section>
        </main>
    );
}
