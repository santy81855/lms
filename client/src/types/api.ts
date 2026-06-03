export type Result<T> = {
    success: boolean;
    payload: T;
    messages: string[];
    type?: string;
};