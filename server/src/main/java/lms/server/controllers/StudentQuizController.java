package lms.server.controllers;

import lms.server.domain.Result;
import lms.server.domain.ResultType;
import lms.server.domain.StudentQuizService;
import lms.server.domain.UserService;
import lms.server.models.RoleName;
import lms.server.models.User;
import lms.server.models.dtos.StudentQuizSubmitRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
public class StudentQuizController {

    private final StudentQuizService studentQuizService;
    private final UserService userService;

    public StudentQuizController(StudentQuizService studentQuizService,
                                 UserService userService) {
        this.studentQuizService = studentQuizService;
        this.userService = userService;
    }

    @GetMapping("/quizzes/{quizId}/take")
    public ResponseEntity<?> findQuizForTaking(@PathVariable Long quizId,
                                               Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentQuizService.findQuizForTaking(
                quizId,
                student.get().getId()
        );

        return resultToResponse(result);
    }

    @PostMapping("/quizzes/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Long quizId,
                                        @RequestBody StudentQuizSubmitRequest request,
                                        Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentQuizService.submitQuiz(
                quizId,
                student.get().getId(),
                request
        );

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
    }

    @GetMapping("/quizzes/{quizId}/all-results")
    public ResponseEntity<?> findAllResults(@PathVariable Long quizId,
                                              Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentQuizService.findAllResults(
                quizId,
                student.get().getId()
        );

        return resultToResponse(result);
    }

    @GetMapping("/quizzes/{quizId}/latest-result")
    public ResponseEntity<?> findLatestResult(@PathVariable Long quizId,
                                              Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentQuizService.findLatestResult(
                quizId,
                student.get().getId()
        );

        return resultToResponse(result);
    }

    private Optional<User> getCurrentStudent(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Optional<User> user = userService.findByEmailWithRoles(authentication.getName());

        if (user.isEmpty() || !hasStudentRole(user.get())) {
            return Optional.empty();
        }

        return user;
    }

    private boolean hasStudentRole(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.STUDENT);
    }

    private ResponseEntity<?> unauthorizedOrForbidden(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(List.of("You must be logged in."));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(List.of("Student access is required."));
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
}