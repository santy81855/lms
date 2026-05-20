package lms.server.controllers;

import lms.server.domain.AiCourseGenerationService;
import lms.server.domain.Result;
import lms.server.domain.ResultType;
import lms.server.domain.UserService;
import lms.server.models.Course;
import lms.server.models.RoleName;
import lms.server.models.User;
import lms.server.models.dtos.CreateCourseFromSyllabusRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class AiCourseGenerationController {

    private final AiCourseGenerationService aiCourseGenerationService;
    private final UserService userService;

    public AiCourseGenerationController(AiCourseGenerationService aiCourseGenerationService,
                                        UserService userService) {
        this.aiCourseGenerationService = aiCourseGenerationService;
        this.userService = userService;
    }

    @PostMapping("/ai-generate")
    public ResponseEntity<?> createCourseFromSyllabus(@RequestBody CreateCourseFromSyllabusRequest request,
                                                      Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Course> result = aiCourseGenerationService.createCourseFromSyllabus(
                request,
                teacher.get().getId()
        );

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
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
}