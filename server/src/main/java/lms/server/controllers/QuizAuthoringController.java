package lms.server.controllers;

import lms.server.domain.QuizAuthoringService;
import lms.server.domain.Result;
import lms.server.domain.ResultType;
import lms.server.domain.UserService;
import lms.server.models.QuizAnswerOption;
import lms.server.models.QuizQuestion;
import lms.server.models.RoleName;
import lms.server.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class QuizAuthoringController {

    private final QuizAuthoringService quizAuthoringService;
    private final UserService userService;

    public QuizAuthoringController(QuizAuthoringService quizAuthoringService,
                                   UserService userService) {
        this.quizAuthoringService = quizAuthoringService;
        this.userService = userService;
    }

    @GetMapping("/api/quizzes/{quizId}/questions")
    public ResponseEntity<?> findQuestionsByQuizId(@PathVariable Long quizId,
                                                   Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<List<QuizQuestion>> result = quizAuthoringService.findQuestionsByQuizId(
                quizId,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PostMapping("/api/quizzes/{quizId}/questions")
    public ResponseEntity<?> createQuizQuestion(@PathVariable Long quizId,
                                                @RequestBody QuizQuestion question,
                                                Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<QuizQuestion> result = quizAuthoringService.createQuizQuestion(
                question,
                quizId,
                teacher.get().getId()
        );

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
    }

    @PutMapping("/api/questions/{questionId}")
    public ResponseEntity<?> updateQuizQuestion(@PathVariable Long questionId,
                                                @RequestBody QuizQuestion question,
                                                Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        if (question != null) {
            question.setId(questionId);
        }

        Result<QuizQuestion> result = quizAuthoringService.updateQuizQuestion(
                question,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PutMapping("/api/questions/{questionId}/move")
    public ResponseEntity<?> moveQuizQuestion(@PathVariable Long questionId,
                                              @RequestParam Integer questionOrder,
                                              Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = quizAuthoringService.moveQuizQuestion(
                questionId,
                teacher.get().getId(),
                questionOrder
        );

        return emptyResultToResponse(result);
    }

    @DeleteMapping("/api/questions/{questionId}")
    public ResponseEntity<?> deleteQuizQuestion(@PathVariable Long questionId,
                                                Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = quizAuthoringService.deleteQuizQuestion(
                questionId,
                teacher.get().getId()
        );

        return emptyResultToResponse(result);
    }

    @GetMapping("/api/questions/{questionId}/options")
    public ResponseEntity<?> findAnswerOptionsByQuestionId(@PathVariable Long questionId,
                                                           Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<List<QuizAnswerOption>> result = quizAuthoringService.findAnswerOptionsByQuestionId(
                questionId,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PostMapping("/api/questions/{questionId}/options")
    public ResponseEntity<?> createQuizAnswerOption(@PathVariable Long questionId,
                                                    @RequestBody QuizAnswerOption option,
                                                    Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<QuizAnswerOption> result = quizAuthoringService.createQuizAnswerOption(
                option,
                questionId,
                teacher.get().getId()
        );

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
    }

    @PutMapping("/api/options/{optionId}")
    public ResponseEntity<?> updateQuizAnswerOption(@PathVariable Long optionId,
                                                    @RequestBody QuizAnswerOption option,
                                                    Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        if (option != null) {
            option.setId(optionId);
        }

        Result<QuizAnswerOption> result = quizAuthoringService.updateQuizAnswerOption(
                option,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PutMapping("/api/options/{optionId}/move")
    public ResponseEntity<?> moveQuizAnswerOption(@PathVariable Long optionId,
                                                  @RequestParam Integer optionOrder,
                                                  Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = quizAuthoringService.moveQuizAnswerOption(
                optionId,
                teacher.get().getId(),
                optionOrder
        );

        return emptyResultToResponse(result);
    }

    @DeleteMapping("/api/options/{optionId}")
    public ResponseEntity<?> deleteQuizAnswerOption(@PathVariable Long optionId,
                                                    Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = quizAuthoringService.deleteQuizAnswerOption(
                optionId,
                teacher.get().getId()
        );

        return emptyResultToResponse(result);
    }

    private Optional<User> getCurrentTeacher(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Optional<User> user = userService.findByEmailWithRoles(authentication.getName());

        if (user.isEmpty() || !hasTeacherRole(user.get())) {
            return Optional.empty();
        }

        return user;
    }

    private boolean hasTeacherRole(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.TEACHER);
    }

    private ResponseEntity<?> unauthorizedOrForbidden(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(List.of("You must be logged in."));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(List.of("Teacher access is required."));
    }

    private ResponseEntity<?> resultToResponse(Result<?> result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getPayload());
        }

        if (result.getType() == ResultType.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessages());
        }

        return ResponseEntity.badRequest().body(result.getMessages());
    }

    private ResponseEntity<?> emptyResultToResponse(Result<Void> result) {
        if (result.isSuccess()) {
            return ResponseEntity.noContent().build();
        }

        if (result.getType() == ResultType.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessages());
        }

        return ResponseEntity.badRequest().body(result.getMessages());
    }
}