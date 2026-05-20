import { ApiError } from "./errors";

const API_BASE_URL = "http://localhost:8080";

type HttpMethod = "GET" | "POST" | "PUT" | "DELETE" | "PATCH";

type ApiClientOptions = {
    method?: HttpMethod;
    body?: unknown;
    headers?: HeadersInit;
};

async function parseResponse(response: Response): Promise<unknown> {
    if (response.status === 204) {
        return null;
    }

    const text = await response.text();

    if (!text) {
        return null;
    }

    const contentType = response.headers.get("content-type");

    if (contentType?.includes("application/json")) {
        return JSON.parse(text);
    }

    return text;
}

function getErrorMessage(data: unknown, fallback: string) {
    if (Array.isArray(data)) {
        return data.join(", ");
    }

    if (!data || typeof data !== "object") {
        return fallback;
    }

    if ("message" in data && typeof data.message === "string") {
        return data.message;
    }

    if ("messages" in data && Array.isArray(data.messages)) {
        return data.messages.join(", ");
    }

    if ("errors" in data && Array.isArray(data.errors)) {
        return data.errors.join(", ");
    }

    return fallback;
}

export async function apiClient<T>(
    path: string,
    options: ApiClientOptions = {}
): Promise<T> {
    const { method = "GET", body, headers } = options;

    const hasBody = body !== undefined;

    const response = await fetch(`${API_BASE_URL}${path}`, {
        method,
        credentials: "include",
        headers: {
            ...(hasBody ? { "Content-Type": "application/json" } : {}),
            ...headers,
        },
        body: hasBody ? JSON.stringify(body) : undefined,
    });

    const data = await parseResponse(response);

    if (!response.ok) {
        const message = getErrorMessage(
            data,
            `Request failed with status ${response.status}`
        );

        throw new ApiError(message, response.status, data);
    }

    return data as T;
}
