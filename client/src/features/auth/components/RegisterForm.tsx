import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router";

import { isApiError } from "@/api";

import { useAuth } from "../hooks/useAuth";
import type { UserRole } from "../types/authTypes";

import styles from "./RegisterForm.module.css";

type RegisterRole = Extract<UserRole, "STUDENT" | "TEACHER">;

function getPostLoginPath(roles: string[]) {
    if (roles.includes("TEACHER")) {
        return "/teacher";
    }

    if (roles.includes("STUDENT")) {
        return "/student";
    }

    return "/";
}

export function RegisterForm() {
    const navigate = useNavigate();

    const { registerStudent, registerTeacher, login, isLoading } = useAuth();

    const [role, setRole] = useState<RegisterRole>("STUDENT");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const registerData = {
                firstName,
                lastName,
                email,
                password,
            };

            if (role === "TEACHER") {
                await registerTeacher(registerData);
            } else {
                await registerStudent(registerData);
            }

            const authenticatedUser = await login({
                email,
                password,
            });

            navigate(getPostLoginPath(authenticatedUser.roles), {
                replace: true,
            });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while creating your account."
                );
            }
        } finally {
            setIsSubmitting(false);
        }
    }

    const isDisabled = isLoading || isSubmitting;

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <fieldset className={styles.roleGroup} disabled={isDisabled}>
                <legend className={styles.legend}>Account type</legend>

                <label className={styles.radioCard}>
                    <input
                        type="radio"
                        name="role"
                        value="STUDENT"
                        checked={role === "STUDENT"}
                        onChange={() => setRole("STUDENT")}
                    />
                    <span>Student</span>
                </label>

                <label className={styles.radioCard}>
                    <input
                        type="radio"
                        name="role"
                        value="TEACHER"
                        checked={role === "TEACHER"}
                        onChange={() => setRole("TEACHER")}
                    />
                    <span>Teacher</span>
                </label>
            </fieldset>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="firstName">
                    First name
                </label>

                <input
                    className={styles.input}
                    id="firstName"
                    name="firstName"
                    type="text"
                    autoComplete="given-name"
                    value={firstName}
                    disabled={isDisabled}
                    onChange={(event) => setFirstName(event.target.value)}
                    required
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label} htmlFor="lastName">
                    Last name
                </label>

                <input
                    className={styles.input}
                    id="lastName"
                    name="lastName"
                    type="text"
                    autoComplete="family-name"
                    value={lastName}
                    disabled={isDisabled}
                    onChange={(event) => setLastName(event.target.value)}
                    required
                />
            </div>

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
                    autoComplete="new-password"
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
                {isSubmitting ? "Creating account..." : "Create account"}
            </button>

            <p className={styles.helperText}>
                Already have an account? <Link to="/login">Log in</Link>.
            </p>
        </form>
    );
}
