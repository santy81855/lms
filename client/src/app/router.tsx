import { createBrowserRouter } from "react-router";

import {
    LoginPage,
    ProtectedRoute,
    RegisterPage,
    RoleRoute,
} from "@/features/auth";
import { StudentDashboardPage } from "@/features/student-courses";
import {
    CourseDetailPage,
    CreateCoursePage,
    EditCoursePage,
    TeacherDashboardPage,
} from "@/features/teacher-courses";
import {
    CreateModulePage,
    EditModulePage,
    ModuleDetailPage,
} from "@/features/teacher-modules";
import {
    CreateLessonPage,
    CreateQuizPage,
    EditLessonPage,
    EditQuizPage,
    LessonDetailPage,
    QuizDetailPage,
} from "@/features/teacher-content";
import {
    CreateOptionPage,
    CreateQuestionPage,
    EditOptionPage,
    EditQuestionPage,
    QuestionDetailPage,
} from "@/features/quiz-authoring";
import { AiCourseGenerationPage } from "@/features/ai-course-generation";
import {
    StudentCourseDetailPage,
    StudentLessonDetailPage,
    StudentQuizDetailPage,
} from "@/features/student-content";
import { AppLayout } from "@/layouts";
import { LandingPage } from "@/pages/LandingPage";
import { NotFoundPage } from "@/pages/NotFoundPage";
import { UnauthorizedPage } from "@/pages/UnauthorizedPage";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <LandingPage />,
    },
    {
        path: "/login",
        element: <LoginPage />,
    },
    {
        path: "/register",
        element: <RegisterPage />,
    },
    {
        element: <ProtectedRoute />,
        children: [
            {
                element: <AppLayout />,
                children: [
                    {
                        element: <RoleRoute allowedRoles={["TEACHER"]} />,
                        children: [
                            {
                                path: "/teacher",
                                element: <TeacherDashboardPage />,
                            },
                            {
                                path: "/teacher/courses/new",
                                element: <CreateCoursePage />,
                            },
                            {
                                path: "/teacher/courses/:courseId",
                                element: <CourseDetailPage />,
                            },
                            {
                                path: "/teacher/courses/ai-generate",
                                element: <AiCourseGenerationPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/edit",
                                element: <EditCoursePage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/new",
                                element: <CreateModulePage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId",
                                element: <ModuleDetailPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/edit",
                                element: <EditModulePage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/lessons/new",
                                element: <CreateLessonPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/lessons/:lessonId",
                                element: <LessonDetailPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/lessons/:lessonId/edit",
                                element: <EditLessonPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/quizzes/new",
                                element: <CreateQuizPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/quizzes/:quizId",
                                element: <QuizDetailPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/quizzes/:quizId/questions/new",
                                element: <CreateQuestionPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/quizzes/:quizId/questions/:questionId",
                                element: <QuestionDetailPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/quizzes/:quizId/questions/:questionId/edit",
                                element: <EditQuestionPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/quizzes/:quizId/questions/:questionId/options/new",
                                element: <CreateOptionPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/quizzes/:quizId/questions/:questionId/options/:optionId/edit",
                                element: <EditOptionPage />,
                            },
                            {
                                path: "/teacher/courses/:courseId/modules/:moduleId/quizzes/:quizId/edit",
                                element: <EditQuizPage />,
                            },
                        ],
                    },
                    {
                        element: <RoleRoute allowedRoles={["STUDENT"]} />,
                        children: [
                            {
                                path: "/student",
                                element: <StudentDashboardPage />,
                            },
                            {
                                path: "/student/courses/:courseId",
                                element: <StudentCourseDetailPage />,
                            },
                            {
                                path: "/student/courses/:courseId/lessons/:lessonId",
                                element: <StudentLessonDetailPage />,
                            },
                            {
                                path: "/student/courses/:courseId/quizzes/:quizId",
                                element: <StudentQuizDetailPage />,
                            },
                        ],
                    },
                ],
            },
        ],
    },
    {
        path: "/unauthorized",
        element: <UnauthorizedPage />,
    },
    {
        path: "*",
        element: <NotFoundPage />,
    },
]);
