import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router";

import { isApiError } from "@/api";
import {
    ContentItemCard,
    getModuleContentItems,
    type ModuleContentItem,
} from "@/features/teacher-content";

import {
    archiveCourseModule,
    deleteCourseModule,
    getCourseModule,
    moveCourseModule,
    publishCourseModule,
    returnCourseModuleToDraft,
} from "../api/teacherModuleApi";
import { ModuleStatusBadge } from "../components/ModuleStatusBadge";
import type { CourseModule } from "../types/moduleTypes";

import pageStyles from "@/pages/Page.module.css";
import styles from "./ModuleDetailPage.module.css";

export function ModuleDetailPage() {
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
    const [contentItems, setContentItems] = useState<ModuleContentItem[]>([]);
    const [moveOrder, setMoveOrder] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [actionMessage, setActionMessage] = useState("");
    const [isLoadingModule, setIsLoadingModule] = useState(true);
    const [isRunningAction, setIsRunningAction] = useState(false);

    useEffect(() => {
        if (!isValidRoute) {
            return;
        }

        let shouldIgnore = false;

        Promise.all([
            getCourseModule(parsedModuleId),
            getModuleContentItems(parsedModuleId),
        ])
            .then(([moduleDetails, moduleContentItems]) => {
                if (!shouldIgnore) {
                    setModule(moduleDetails);
                    setContentItems(moduleContentItems);
                    setMoveOrder(moduleDetails.moduleOrder.toString());
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

    async function reloadModule() {
        const [updatedModule, updatedContentItems] = await Promise.all([
            getCourseModule(parsedModuleId),
            getModuleContentItems(parsedModuleId),
        ]);

        setModule(updatedModule);
        setContentItems(updatedContentItems);
        setMoveOrder(updatedModule.moduleOrder.toString());
    }

    async function runModuleAction(
        action: () => Promise<void>,
        successMessage: string
    ) {
        if (!isValidRoute) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await action();
            await reloadModule();
            setActionMessage(successMessage);
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while updating the module."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    async function handleMoveModule(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        if (!isValidRoute) {
            return;
        }

        const nextOrder = Number(moveOrder);

        if (!Number.isInteger(nextOrder) || nextOrder <= 0) {
            setErrorMessage("Module order must be a positive whole number.");
            return;
        }

        await runModuleAction(
            () => moveCourseModule(parsedModuleId, nextOrder),
            "Module order updated."
        );
    }

    async function handleDeleteModule() {
        const confirmed = window.confirm(
            "Are you sure you want to delete this module? This cannot be undone."
        );

        if (!confirmed || !isValidRoute) {
            return;
        }

        setActionMessage("");
        setErrorMessage("");
        setIsRunningAction(true);

        try {
            await deleteCourseModule(parsedModuleId);
            navigate(`/teacher/courses/${parsedCourseId}`, { replace: true });
        } catch (error) {
            if (isApiError(error)) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage(
                    "Something went wrong while deleting the module."
                );
            }
        } finally {
            setIsRunningAction(false);
        }
    }

    return (
        <main className={pageStyles.page}>
            <section className={pageStyles.content}>
                <Link
                    className={pageStyles.secondaryLink}
                    to={
                        isValidCourseId
                            ? `/teacher/courses/${parsedCourseId}`
                            : "/teacher"
                    }
                >
                    Back to course
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

                {actionMessage && (
                    <p className={styles.successMessage}>{actionMessage}</p>
                )}

                {isValidRoute &&
                    !isLoadingModule &&
                    !errorMessage &&
                    !module && <p>Module not found.</p>}

                {isValidRoute && !isLoadingModule && module && (
                    <>
                        <div className={styles.header}>
                            <div className={styles.titleGroup}>
                                <p className={pageStyles.eyebrow}>
                                    Module {module.moduleOrder}
                                </p>

                                <h1>{module.title}</h1>
                            </div>

                            <ModuleStatusBadge status={module.status} />
                        </div>

                        <div className={styles.detailsCard}>
                            <dl className={styles.detailList}>
                                <div>
                                    <dt>Module ID</dt>
                                    <dd>{module.id}</dd>
                                </div>

                                <div>
                                    <dt>Course ID</dt>
                                    <dd>{module.courseId}</dd>
                                </div>

                                <div>
                                    <dt>Order</dt>
                                    <dd>{module.moduleOrder}</dd>
                                </div>

                                <div>
                                    <dt>Published at</dt>
                                    <dd>
                                        {module.publishedAt ?? "Not published"}
                                    </dd>
                                </div>
                            </dl>

                            {module.description && (
                                <div className={styles.descriptionBlock}>
                                    <h2>Description</h2>
                                    <p>{module.description}</p>
                                </div>
                            )}
                        </div>

                        <div className={styles.contentCard}>
                            <div className={styles.sectionHeader}>
                                <div>
                                    <h2>Content</h2>
                                    <p>
                                        Add lessons, assignments, and quizzes
                                        for this module. AI-generated content
                                        will appear here too.
                                    </p>
                                </div>

                                <div className={styles.contentActions}>
                                    <Link
                                        className={styles.secondaryButton}
                                        to={`/teacher/courses/${parsedCourseId}/modules/${module.id}/lessons/new`}
                                    >
                                        Add lesson
                                    </Link>

                                    <Link
                                        className={styles.secondaryButton}
                                        to={`/teacher/courses/${parsedCourseId}/modules/${module.id}/quizzes/new`}
                                    >
                                        Add quiz
                                    </Link>
                                    <button
                                        className={styles.secondaryButton}
                                        type="button"
                                        disabled
                                    >
                                        Add assignment
                                    </button>
                                </div>
                            </div>

                            {contentItems.length === 0 && (
                                <div className={styles.emptyState}>
                                    <h3>No content yet</h3>
                                    <p>
                                        Once we add content forms, lessons,
                                        assignments, and quizzes will appear
                                        here.
                                    </p>
                                </div>
                            )}

                            {contentItems.length > 0 && (
                                <div className={styles.contentList}>
                                    {contentItems.map((item) => (
                                        <ContentItemCard
                                            key={`${item.itemType}-${item.id}`}
                                            courseId={parsedCourseId}
                                            item={item}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>

                        <div className={styles.actionCard}>
                            <h2>Module actions</h2>

                            <div className={styles.actions}>
                                <Link
                                    className={styles.secondaryButton}
                                    to={`/teacher/courses/${parsedCourseId}/modules/${module.id}/edit`}
                                >
                                    Edit details
                                </Link>

                                <button
                                    className={styles.primaryButton}
                                    type="button"
                                    disabled={
                                        isRunningAction ||
                                        module.status === "PUBLISHED"
                                    }
                                    onClick={() =>
                                        runModuleAction(
                                            () =>
                                                publishCourseModule(module.id),
                                            "Module published."
                                        )
                                    }
                                >
                                    Publish
                                </button>

                                <button
                                    className={styles.secondaryButton}
                                    type="button"
                                    disabled={
                                        isRunningAction ||
                                        module.status === "DRAFT"
                                    }
                                    onClick={() =>
                                        runModuleAction(
                                            () =>
                                                returnCourseModuleToDraft(
                                                    module.id
                                                ),
                                            "Module returned to draft."
                                        )
                                    }
                                >
                                    Return to draft
                                </button>

                                <button
                                    className={styles.secondaryButton}
                                    type="button"
                                    disabled={
                                        isRunningAction ||
                                        module.status === "ARCHIVED"
                                    }
                                    onClick={() =>
                                        runModuleAction(
                                            () =>
                                                archiveCourseModule(module.id),
                                            "Module archived."
                                        )
                                    }
                                >
                                    Archive
                                </button>

                                <button
                                    className={styles.dangerButton}
                                    type="button"
                                    disabled={isRunningAction}
                                    onClick={handleDeleteModule}
                                >
                                    Delete
                                </button>
                            </div>
                        </div>

                        <div className={styles.actionCard}>
                            <h2>Move module</h2>

                            <form
                                className={styles.moveForm}
                                onSubmit={handleMoveModule}
                            >
                                <div className={styles.fieldGroup}>
                                    <label
                                        className={styles.label}
                                        htmlFor="moveOrder"
                                    >
                                        New module order
                                    </label>

                                    <input
                                        className={styles.input}
                                        id="moveOrder"
                                        name="moveOrder"
                                        type="number"
                                        min={1}
                                        step={1}
                                        value={moveOrder}
                                        disabled={isRunningAction}
                                        onChange={(event) =>
                                            setMoveOrder(event.target.value)
                                        }
                                        required
                                    />
                                </div>

                                <button
                                    className={styles.secondaryButton}
                                    type="submit"
                                    disabled={isRunningAction}
                                >
                                    Move module
                                </button>
                            </form>
                        </div>
                    </>
                )}
            </section>
        </main>
    );
}
