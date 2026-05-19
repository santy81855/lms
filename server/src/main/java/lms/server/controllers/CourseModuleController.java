package lms.server.controllers;

import lms.server.domain.CourseModuleService;
import lms.server.domain.Result;
import lms.server.domain.ResultType;
import lms.server.domain.UserService;
import lms.server.models.CourseModule;
import lms.server.models.RoleName;
import lms.server.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CourseModuleController {

    private final CourseModuleService courseModuleService;
    private final UserService userService;

    public CourseModuleController(CourseModuleService courseModuleService,
                                  UserService userService) {
        this.courseModuleService = courseModuleService;
        this.userService = userService;
    }

    @GetMapping("/api/courses/{courseId}/modules")
    public ResponseEntity<?> findModulesByCourseId(@PathVariable Long courseId,
                                                   Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<List<CourseModule>> result = courseModuleService.findModulesByCourseId(
                courseId,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PostMapping("/api/courses/{courseId}/modules")
    public ResponseEntity<?> createModule(@PathVariable Long courseId,
                                          @RequestBody CourseModule module,
                                          Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<CourseModule> result = courseModuleService.createModule(
                module,
                courseId,
                teacher.get().getId()
        );

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
    }

    @GetMapping("/api/modules/{moduleId}")
    public ResponseEntity<?> findModuleById(@PathVariable Long moduleId,
                                            Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<CourseModule> result = courseModuleService.findModuleByIdForTeacher(
                moduleId,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PutMapping("/api/modules/{moduleId}")
    public ResponseEntity<?> updateModule(@PathVariable Long moduleId,
                                          @RequestBody CourseModule module,
                                          Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        if (module != null) {
            module.setId(moduleId);
        }

        Result<CourseModule> result = courseModuleService.updateModule(
                module,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PutMapping("/api/modules/{moduleId}/publish")
    public ResponseEntity<?> publishModule(@PathVariable Long moduleId,
                                           Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = courseModuleService.publishModule(moduleId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/modules/{moduleId}/archive")
    public ResponseEntity<?> archiveModule(@PathVariable Long moduleId,
                                           Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = courseModuleService.archiveModule(moduleId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/modules/{moduleId}/draft")
    public ResponseEntity<?> returnModuleToDraft(@PathVariable Long moduleId,
                                                 Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = courseModuleService.returnModuleToDraft(moduleId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/modules/{moduleId}/move")
    public ResponseEntity<?> moveModule(@PathVariable Long moduleId,
                                        @RequestParam Integer moduleOrder,
                                        Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = courseModuleService.moveModule(
                moduleId,
                teacher.get().getId(),
                moduleOrder
        );

        return emptyResultToResponse(result);
    }

    @DeleteMapping("/api/modules/{moduleId}")
    public ResponseEntity<?> deleteModule(@PathVariable Long moduleId,
                                          Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = courseModuleService.deleteModule(moduleId, teacher.get().getId());
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