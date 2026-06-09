package lms.server.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum QuizFeedbackType {

    NO_FEEDBACK("no-feedback"),
    SCORE("score"),
    LESSON_REFERENCE("lesson-reference"),
    AI_OVERVIEW("ai-overview");

    private final String jsonValue;

    QuizFeedbackType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

}
