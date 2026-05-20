import { Link } from "react-router";

import styles from "./Page.module.css";

export function UnauthorizedPage() {
    return (
        <main className={styles.page}>
            <section className={styles.content}>
                <p className={styles.eyebrow}>Unauthorized</p>
                <h1>You do not have access to this page</h1>

                <p className={styles.description}>
                    Log in with the correct account type to continue.
                </p>

                <div className={styles.actions}>
                    <Link className={styles.primaryLink} to="/login">
                        Log in
                    </Link>

                    <Link className={styles.secondaryLink} to="/">
                        Go home
                    </Link>
                </div>
            </section>
        </main>
    );
}
