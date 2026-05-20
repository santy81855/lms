import { Link } from "react-router";

import styles from "./Page.module.css";

export function UnauthorizedPage() {
    return (
        <main className={styles.page}>
            <section className={styles.content}>
                <h1>Unauthorized</h1>

                <p className={styles.description}>
                    You do not have permission to view this page.
                </p>

                <Link className={styles.primaryLink} to="/">
                    Go home
                </Link>
            </section>
        </main>
    );
}
