import { Link, isRouteErrorResponse, useRouteError } from "react-router";

import styles from "./Page.module.css";

function getErrorMessage(error: unknown) {
    if (isRouteErrorResponse(error)) {
        return `${error.status} ${error.statusText}`;
    }

    if (error instanceof Error) {
        return error.message;
    }

    return "Something went wrong.";
}

export function AppErrorPage() {
    const error = useRouteError();

    return (
        <main className={styles.page}>
            <section className={styles.content}>
                <p className={styles.eyebrow}>Application error</p>
                <h1>Something went wrong</h1>

                <p className={styles.description}>{getErrorMessage(error)}</p>

                <Link className={styles.primaryLink} to="/">
                    Go home
                </Link>
            </section>
        </main>
    );
}
