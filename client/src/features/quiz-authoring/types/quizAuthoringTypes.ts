export type QuestionType = "MULTIPLE_CHOICE" | "TRUE_FALSE" | "SHORT_ANSWER";

export type QuizQuestion = {
    id: number;
    quizId: number;
    questionText: string;
    questionType: QuestionType;
    questionOrder: number;
    points: number;
    explanation: string | null;
    createdAt: string | null;
    updatedAt: string | null;
    associatedLessonId: number;
};

export type QuizAnswerOption = {
    id: number;
    questionId: number;
    optionText: string;
    optionOrder: number;
    correct: boolean;
    createdAt: string | null;
    updatedAt: string | null;
};

export type QuizQuestionFormData = {
    questionText: string;
    questionType: QuestionType;
    questionOrder?: number | null;
    points: number;
    explanation: string;
    associatedLessonId: number;
};

export type QuizAnswerOptionFormData = {
    optionText: string;
    optionOrder?: number | null;
    correct: boolean;
};
