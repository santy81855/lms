package lms.server.controllers;

import lms.server.domain.Result;
import lms.server.domain.ResultType;
import lms.server.domain.StudentContentService;
import lms.server.domain.UserService;
import lms.server.models.RoleName;
import lms.server.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
public class StudentContentController {

    private final StudentContentService studentContentService;
    private final UserService userService;

    public StudentContentController(StudentContentService studentContentService,
                                    UserService userService) {
        this.studentContentService = studentContentService;
        this.userService = userService;
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> findCourseById(@PathVariable Long courseId,
                                            Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentContentService.findCourseByIdForStudent(
                courseId,
                student.get().getId()
        );

        return resultToResponse(result);
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<?> findModulesByCourseId(@PathVariable Long courseId,
                                                   Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentContentService.findModulesByCourseId(
                courseId,
                student.get().getId()
        );

        return resultToResponse(result);
    }

    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<?> findModuleById(@PathVariable Long moduleId,
                                            Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentContentService.findModuleById(
                moduleId,
                student.get().getId()
        );

        return resultToResponse(result);
    }

    @GetMapping("/modules/{moduleId}/content")
    public ResponseEntity<?> findModuleContentItems(@PathVariable Long moduleId,
                                                    Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentContentService.findModuleContentItems(
                moduleId,
                student.get().getId()
        );

        return resultToResponse(result);
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<?> findLessonById(@PathVariable Long lessonId,
                                            Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentContentService.findLessonById(
                lessonId,
                student.get().getId()
        );

        return resultToResponse(result);
    }

    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<?> findAssignmentById(@PathVariable Long assignmentId,
                                                Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentContentService.findAssignmentById(
                assignmentId,
                student.get().getId()
        );

        return resultToResponse(result);
    }

    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<?> findQuizById(@PathVariable Long quizId,
                                          Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<?> result = studentContentService.findQuizById(
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