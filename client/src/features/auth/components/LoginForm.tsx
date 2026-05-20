import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router";

import { isApiError } from "@/api";

import { useAuth } from "../hooks/useAuth";

import styles from "./LoginForm.module.css";

const TEST_LOGIN = {
    email: "",
    password: "",
};

function getPostLoginPath(roles: string[]) {
    if (roles.includes("TEACHER")) {
        return "/teacher";
    }

    if (roles.includes("STUDENT")) {
        return "/student";
    }

    return "/";
}

export function LoginForm() {
    const navigate = useNavigate();
    //const location = useLocation();
    const { login, isLoading } = useAuth();

    const [email, setEmail] = useState(TEST_LOGIN.email);
    const [password, setPassword] = useState(TEST_LOGIN.password);
    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const authenticatedUser = await login({
                email,
                password,
            });

            //const from = location.state?.from?.pathname;
            navigate(getPostLoginPath(authenticatedUser.roles), {
                replace: true,
            });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage("Something went wrong while logging in.");
            }
        } finally {
            setIsSubmitting(false);
        }
    }

    const isDisabled = isLoading || isSubmitting;

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="email">
                    Email
                </label>

                <input
                    className={styles.input}
                    id="email"
                    name="email"
                    type="email"
                    autoComplete="email"
                    value={email}
                    disabled={isDisabled}
                    onChange={(event) => setEmail(event.target.value)}
                    required
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="password">
                    Password
                </label>

                <input
                    className={styles.input}
                    id="password"
                    name="password"
                    type="password"
                    autoComplete="current-password"
                    value={password}
                    disabled={isDisabled}
                    onChange={(event) => setPassword(event.target.value)}
                    required
                />
            </div>

            {errorMessage && (
                <p className={styles.errorMessage}>{errorMessage}</p>
            )}

            <button
                className={styles.submitButton}
                type="submit"
                disabled={isDisabled}
            >
                {isSubmitting ? "Logging in..." : "Log in"}
            </button>

            <p className={styles.helperText}>
                Need an account? <Link to="/register">Create one</Link>.
            </p>
        </form>
    );
}
