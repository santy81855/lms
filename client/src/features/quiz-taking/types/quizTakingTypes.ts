export type StudentQuestionType =
    | "MULTIPLE_CHOICE"
    | "TRUE_FALSE"
    | "SHORT_ANSWER";

export type StudentQuizOption = {
    id: number;
    questionId: number;
    optionText: string;
    optionOrder: number;
};

export type StudentQuizQuestion = {
    id: number;
    quizId: number;
    questionText: string;
    questionType: StudentQuestionType;
    questionOrder: number;
    points: number;
    options: StudentQuizOption[];
};

export type StudentQuiz = {
    id: number;
    moduleId: number;
    title: string;
    description: string | null;
    maxPoints: number;
    timeLimitMinutes: number | null;
    attemptsAllowed: number;
    questions: StudentQuizQuestion[];
};

export type StudentQuizAnswerRequest = {
    questionId: number;
    selectedOptionId?: number | null;
    shortAnswerText?: string | null;
};

export type StudentQuizSubmitRequest = {
    answers: StudentQuizAnswerRequest[];
};

export type StudentQuizResult = {
    submissionId: number;
    quizId: number;
    studentId: number;
    attemptNumber: number;
    score: number;
    maxScore: number;
};
