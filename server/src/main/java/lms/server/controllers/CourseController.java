package lms.server.controllers;

import lms.server.domain.CourseService;
import lms.server.domain.Result;
import lms.server.domain.ResultType;
import lms.server.domain.UserService;
import lms.server.models.Course;
import lms.server.models.RoleName;
import lms.server.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/teacher")
    public ResponseEntity<?> findCoursesForTeacher(Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        return ResponseEntity.ok(courseService.findCoursesByTeacherId(teacher.get().getId()));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> findCourseById(@PathVariable Long courseId,
                                            Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Course> result = courseService.findCourseByIdForTeacher(courseId, teacher.get().getId());
        return resultToResponse(result);
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course,
                                          Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Course> result = courseService.createCourse(course, teacher.get().getId());

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId,
                                          @RequestBody Course course,
                                          Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        if (course != null) {
            course.setId(courseId);
        }

        Result<Course> result = courseService.updateCourse(course, teacher.get().getId());
        return resultToResponse(result);
    }

    @PutMapping("/{courseId}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable Long courseId,
                                           Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = courseService.publishCourse(courseId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/{courseId}/archive")
    public ResponseEntity<?> archiveCourse(@PathVariable Long courseId,
                                           Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = courseService.archiveCourse(courseId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/{courseId}/draft")
    public ResponseEntity<?> returnCourseToDraft(@PathVariable Long courseId,
                                                 Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = courseService.returnCourseToDraft(courseId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId,
                                          Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = courseService.deleteCourse(courseId, teacher.get().getId());
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