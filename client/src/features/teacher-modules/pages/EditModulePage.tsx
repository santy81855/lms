import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";

import { getCourseModule, updateCourseModule } from "../api/teacherModuleApi";
import { ModuleForm } from "../components/ModuleForm";
import type { CourseModule, ModuleFormData } from "../types/moduleTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./EditModulePage.module.css";

function getModuleFormData(module: CourseModule): ModuleFormData {
    return {
        title: module.title,
        description: module.description ?? "",
        moduleOrder: module.moduleOrder,
    };
}

export function EditModulePage() {
    const navigate = useNavigate();
    const { courseId, moduleId } = useParams();

    const parsedCourseId = Number(courseId);
    const parsedModuleId = Number(moduleId);

    const isValidCourseId =
        Number.isInteger(parsedCourseId) && parsedCourseId > 0;
    const isValidModuleId =
        Number.isInteger(parsedModuleId) && parsedModuleId > 0;
    const isValidRoute = isValidCourseId && isValidModuleId;

    const [module, setModule] = useState<CourseModule | null>(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoadingModule, setIsLoadingModule] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        getCourseModule(parsedModuleId)
            .then((moduleDetails) => {
                if (!shouldIgnore) {
                    setModule(moduleDetails);
                }
            })
            .catch((error: unknown) => {
                if (shouldIgnore) {
                    return;
                }

                if (isApiError(error)) {
                    setErrorMessage(error.message);
                } else {
                    setErrorMessage(
                        "Something went wrong while loading the module."
                    );
                }
            })
            .finally(() => {
                if (!shouldIgnore) {
                    setIsLoadingModule(false);
                }
            });

        return () => {
            shouldIgnore = true;
        };
    }, [isValidRoute, parsedModuleId]);

    async function handleUpdateModule(data: ModuleFormData) {
        if (!isValidRoute) {
            return;
        }

        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const updatedModule = await updateCourseModule(
                parsedModuleId,
                data
            );

            navigate(
                `/teacher/courses/${parsedCourseId}/modules/${updatedModule.id}`,
                { replace: true }
            );
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the module."
                );
            }
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <Link
                    className={pageStyles.secondaryLink}
                    to={
                        isValidRoute
                            ? `/teacher/courses/${parsedCourseId}/modules/${parsedModuleId}`
                            : "/teacher"
                    }
                >
                    Back to module
                </Link>

                {!isValidRoute && (
                    <p className={styles.errorMessage}>
                        Invalid course or module id.
                    </p>
                )}

                {isValidRoute && isLoadingModule && <p>Loading module...</p>}

                {errorMessage && (
                    <p className={styles.errorMessage}>{errorMessage}</p>
                )}

                {isValidRoute &&
                    !isLoadingModule &&
                    !errorMessage &&
                    !module && <p>Module not found.</p>}

                {isValidRoute && !isLoadingModule && module && (
                    <>
                        <div>
                            <p className={pageStyles.eyebrow}>
                                Teacher modules
                            </p>
                            <h1>Edit module</h1>
                        </div>

                        <p className={pageStyles.description}>
                            Update the module title, description, or order.
                            Publishing status is managed separately.
                        </p>

                        <ModuleForm
                            submitLabel="Save changes"
                            initialValues={getModuleFormData(module)}
                            errorMessage={errorMessage}
                            isSubmitting={isSubmitting}
                            onSubmit={handleUpdateModule}
                        />
                    </>
                )}
            </section>
        </main>
    );
}
