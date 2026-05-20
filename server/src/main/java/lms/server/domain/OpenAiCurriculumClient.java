package lms.server.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lms.server.models.dtos.CreateCourseFromSyllabusRequest;
import lms.server.models.dtos.GeneratedCoursePlan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Component
public class OpenAiCurriculumClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public OpenAiCurriculumClient(@Value("${openai.api-key:}") String apiKey,
                                  @Value("${openai.model:gpt-4.1}") String model) {
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.model = model;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .build();
    }

    public Result<GeneratedCoursePlan> generateCoursePlan(CreateCourseFromSyllabusRequest request) {
        Result<GeneratedCoursePlan> result = new Result<>();

        if (apiKey == null || apiKey.isBlank()) {
            result.addMessage("OpenAI API key is not configured.", ResultType.INVALID);
            return result;
        }

        if (request == null) {
            result.addMessage("Syllabus request is required.", ResultType.INVALID);
            return result;
        }

        if (request.getSyllabusText() == null || request.getSyllabusText().isBlank()) {
            result.addMessage("Syllabus text is required.", ResultType.INVALID);
            return result;
        }

        try {
            Map<String, Object> requestBody = buildRequestBody(request);

            String responseBody = restClient.post()
                    .uri("/responses")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            String generatedJson = extractOutputText(responseBody);

            if (generatedJson == null || generatedJson.isBlank()) {
                result.addMessage("OpenAI response did not include generated course JSON.", ResultType.INVALID);
                return result;
            }

            GeneratedCoursePlan plan = objectMapper.readValue(generatedJson, GeneratedCoursePlan.class);
            result.setPayload(plan);
            return result;

        } catch (RestClientException ex) {
            result.addMessage("OpenAI request failed: " + ex.getMessage(), ResultType.INVALID);
            return result;
        } catch (Exception ex) {
            result.addMessage("Could not parse generated course plan: " + ex.getMessage(), ResultType.INVALID);
            return result;
        }
    }

    private Map<String, Object> buildRequestBody(CreateCourseFromSyllabusRequest request) {
        String input = """
                Create an LMS course plan from this syllabus.

                Teacher-provided course fields:
                title: %s
                subject: %s
                gradeLevel: %s
                description: %s
                requestedModuleCount: %s
                includeAssignments: %s
                includeQuizzes: %s

                Syllabus:
                %s
                """.formatted(
                valueOrBlank(request.getTitle()),
                valueOrBlank(request.getSubject()),
                request.getGradeLevel() == null ? "OTHER" : request.getGradeLevel().name(),
                valueOrBlank(request.getDescription()),
                request.getModuleCount() == null ? "auto" : request.getModuleCount(),
                Boolean.TRUE.equals(request.getIncludeAssignments()),
                Boolean.TRUE.equals(request.getIncludeQuizzes()),
                request.getSyllabusText()
        );

        return Map.of(
                "model", model,
                "instructions", buildInstructions(),
                "input", input,
                "temperature", 0.4,
                "text", Map.of(
                        "format", Map.of(
                                "type", "json_schema",
                                "name", "generated_course_plan",
                                "strict", true,
                                "schema", buildJsonSchema()
                        )
                )
        );
    }

    private String buildInstructions() {
        return """
                You generate structured LMS course plans for teachers.

                Rules:
                - Return only JSON matching the schema.
                - Do not include markdown.
                - Use practical, classroom-ready language.
                - Keep lesson content concise but useful.
                - All modules, lessons, assignments, quizzes, questions, and options should be draft-ready.
                - Use gradeLevel values only from: ELEMENTARY, MIDDLE_SCHOOL, HIGH_SCHOOL, UNIVERSITY, OTHER.
                - Use questionType values only from: MULTIPLE_CHOICE or TRUE_FALSE.
                - Do not create SHORT_ANSWER questions for this MVP.
                - Every quiz question must have at least 2 options.
                - Every quiz question must have exactly one correct option.
                - If includeAssignments is false, return an empty assignments array for each module.
                - If includeQuizzes is false, return an empty quizzes array for each module.
                """;
    }

    private Map<String, Object> buildJsonSchema() {
        Map<String, Object> optionSchema = Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "optionText", Map.of("type", "string"),
                        "correct", Map.of("type", "boolean")
                ),
                "required", List.of("optionText", "correct")
        );

        Map<String, Object> questionSchema = Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "questionText", Map.of("type", "string"),
                        "questionType", Map.of(
                                "type", "string",
                                "enum", List.of("MULTIPLE_CHOICE", "TRUE_FALSE")
                        ),
                        "points", Map.of("type", "number"),
                        "explanation", Map.of("type", "string"),
                        "options", Map.of(
                                "type", "array",
                                "items", optionSchema
                        )
                ),
                "required", List.of(
                        "questionText",
                        "questionType",
                        "points",
                        "explanation",
                        "options"
                )
        );

        Map<String, Object> quizSchema = Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "title", Map.of("type", "string"),
                        "description", Map.of("type", "string"),
                        "maxPoints", Map.of("type", "number"),
                        "timeLimitMinutes", Map.of("type", "integer"),
                        "attemptsAllowed", Map.of("type", "integer"),
                        "questions", Map.of(
                                "type", "array",
                                "items", questionSchema
                        )
                ),
                "required", List.of(
                        "title",
                        "description",
                        "maxPoints",
                        "timeLimitMinutes",
                        "attemptsAllowed",
                        "questions"
                )
        );

        Map<String, Object> assignmentSchema = Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "title", Map.of("type", "string"),
                        "instructions", Map.of("type", "string"),
                        "maxPoints", Map.of("type", "number")
                ),
                "required", List.of("title", "instructions", "maxPoints")
        );

        Map<String, Object> lessonSchema = Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "title", Map.of("type", "string"),
                        "content", Map.of("type", "string"),
                        "estimatedMinutes", Map.of("type", "integer")
                ),
                "required", List.of("title", "content", "estimatedMinutes")
        );

        Map<String, Object> moduleSchema = Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "title", Map.of("type", "string"),
                        "description", Map.of("type", "string"),
                        "lessons", Map.of(
                                "type", "array",
                                "items", lessonSchema
                        ),
                        "assignments", Map.of(
                                "type", "array",
                                "items", assignmentSchema
                        ),
                        "quizzes", Map.of(
                                "type", "array",
                                "items", quizSchema
                        )
                ),
                "required", List.of(
                        "title",
                        "description",
                        "lessons",
                        "assignments",
                        "quizzes"
                )
        );

        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "title", Map.of("type", "string"),
                        "subject", Map.of("type", "string"),
                        "gradeLevel", Map.of(
                                "type", "string",
                                "enum", List.of(
                                        "ELEMENTARY",
                                        "MIDDLE_SCHOOL",
                                        "HIGH_SCHOOL",
                                        "UNIVERSITY",
                                        "OTHER"
                                )
                        ),
                        "description", Map.of("type", "string"),
                        "modules", Map.of(
                                "type", "array",
                                "items", moduleSchema
                        )
                ),
                "required", List.of(
                        "title",
                        "subject",
                        "gradeLevel",
                        "description",
                        "modules"
                )
        );
    }

    private String extractOutputText(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode output = root.path("output");

        if (!output.isArray()) {
            return null;
        }

        for (JsonNode outputItem : output) {
            JsonNode content = outputItem.path("content");

            if (!content.isArray()) {
                continue;
            }

            for (JsonNode contentItem : content) {
                if ("output_text".equals(contentItem.path("type").asText())) {
                    return contentItem.path("text").asText();
                }
            }
        }

        return null;
    }

    private String valueOrBlank(String value) {
        return value == null ? "" : value;
    }
}