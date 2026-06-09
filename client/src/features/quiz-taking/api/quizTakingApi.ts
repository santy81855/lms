import { apiClient } from "@/api";
import type { Result } from "@/types/api";

import type {
    QuizFeedback,
    QuizQuestionFeedback,
    StudentQuiz,
    StudentQuizResult,
    StudentQuizSubmitRequest,
} from "../types/quizTakingTypes";

export function getStudentQuizForTaking(quizId: number) {
    return apiClient<StudentQuiz>(`/api/student/quizzes/${quizId}/take`);
}

export function submitStudentQuiz(
    quizId: number,
    data: StudentQuizSubmitRequest
) {
    return apiClient<StudentQuizResult>(
        `/api/student/quizzes/${quizId}/submit`,
        {
            method: "POST",
            body: data,
        },
    );
}

export function getLatestStudentQuizResult(quizId: number) {
    return apiClient<StudentQuizResult>(
        `/api/student/quizzes/${quizId}/latest-result`,
    );
}

export function getAllStudentQuizResults(quizId: number) {
    return apiClient<Result<StudentQuizResult[]>>(
        `/api/student/quizzes/${quizId}/all-results`,
    );
}

export function getQuizFeedback(resultId: number){
    let quizFeedbackMock : QuizFeedback = {
        type: "",
        quiz: {
            id: 0,
            moduleId: 0,
            title: "",
            description: null,
            quizOrder: 0,
            maxPoints: 0,
            timeLimitMinutes: null,
            attemptsAllowed: 0,
            status: "PUBLISHED",
            createdAt: null,
            updatedAt: null,
            publishedAt: null,
            feedbackTypeCode: ""
        },
        score: 0,
        maxScore: 0,
        content: []
    };

    // mocking backend responses for lessonReference quizes
    const questionFeedback : QuizQuestionFeedback = {
        questionNumber: 1,
        questionText: "This is the question",
        feedback: "This is the feedback for an AI Overview"
    };
    const secondQuestionFeedback : QuizQuestionFeedback = {  
        questionNumber: 2,
        questionText: "This is a question on a quiz with lesson references.",
        feedback: "http://localhost:5173/student/courses/16/quizzes/22/result"
    };
    quizFeedbackMock.type = "aiOverview";
    quizFeedbackMock.quiz.title = "Quiz 1";
    quizFeedbackMock.score = 7;
    quizFeedbackMock.maxScore = 10;
    quizFeedbackMock.content = [questionFeedback, secondQuestionFeedback];

    return new Promise<QuizFeedback>((resolve, reject) => {
        resolve(quizFeedbackMock);
    });

    return apiClient<QuizFeedback>(`/quizzes/${resultId}/submissions/{submissionId}/feedback`);
}