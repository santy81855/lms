package lms.server.controllers;

import lms.server.domain.QuizAuthoringService;
import lms.server.domain.Result;
import lms.server.domain.ResultType;
import lms.server.domain.UserService;
import lms.server.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
public class QuizGradingController {

    private final QuizAuthoringService quizAuthoringService;
    private final UserService userService;

    public QuizGradingController(QuizAuthoringService quizAuthoringService,
                                 UserService userService) {
        this.quizAuthoringService = quizAuthoringService;
        this.userService = userService;
    }

    @GetMapping("/{quizId}/pending-grading")
    public ResponseEntity<?> getPendingGrading(@PathVariable Long quizId,
                                               Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);
        if (teacher.isEmpty()) return unauthorizedOrForbidden(authentication);

        Result<List<QuizSubmissionAnswer>> result =
                quizAuthoringService.findPendingGrading(quizId, teacher.get().getId());

        if (!result.isSuccess()) {
            if (result.getType() == ResultType.INVALID || result.getType() == ResultType.NOT_FOUND)
                return ResponseEntity.badRequest().body(result.getMessages());
        }

        return ResponseEntity.ok(result.getPayload());
    }

    @PostMapping("/grade-answer/{answerId}")
    public ResponseEntity<?> gradeAnswer(@PathVariable Long answerId,
                                         @RequestParam Double points,
                                         @RequestParam Boolean isCorrect,
                                         Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);
        if (teacher.isEmpty()) return unauthorizedOrForbidden(authentication);

        Result<Void> result = quizAuthoringService.gradeQuizSubmissionAnswer(
                answerId,
                points,
                isCorrect,
                teacher.get().getId()
        );

        if (!result.isSuccess()) {
            if (result.getType() == ResultType.NOT_FOUND)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessages());
            return ResponseEntity.badRequest().body(result.getMessages());
        }

        return ResponseEntity.noContent().build();
    }

    private Optional<User> getCurrentTeacher(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return Optional.empty();
        Optional<User> user = userService.findByEmailWithRoles(authentication.getName());
        if (user.isEmpty() || !user.get().getRoles().stream()
                .anyMatch(r -> r.getName().equals(RoleName.TEACHER))) return Optional.empty();
        return user;
    }

    private ResponseEntity<?> unauthorizedOrForbidden(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(List.of("You must be logged in."));
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(List.of("Teacher access is required."));
    }
}