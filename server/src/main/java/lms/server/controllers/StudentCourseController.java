package lms.server.controllers;

import lms.server.domain.Result;
import lms.server.domain.ResultType;
import lms.server.domain.StudentCourseService;
import lms.server.domain.UserService;
import lms.server.models.Course;
import lms.server.models.CourseEnrollment;
import lms.server.models.RoleName;
import lms.server.models.User;
import lms.server.models.dtos.JoinCourseRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
public class StudentCourseController {

    private final StudentCourseService studentCourseService;
    private final UserService userService;

    public StudentCourseController(StudentCourseService studentCourseService,
                                   UserService userService) {
        this.studentCourseService = studentCourseService;
        this.userService = userService;
    }

    @GetMapping("/enrollments")
    public ResponseEntity<?> findMyEnrollments(Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        List<CourseEnrollment> enrollments = studentCourseService.findEnrollmentsByStudentId(
                student.get().getId()
        );

        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/courses")
    public ResponseEntity<?> findMyCourses(Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        List<Course> courses = studentCourseService.findCoursesByStudentId(
                student.get().getId()
        );

        return ResponseEntity.ok(courses);
    }

    @PostMapping("/courses/join")
    public ResponseEntity<?> joinCourse(@RequestBody JoinCourseRequest request,
                                        Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        String joinCode = request == null ? null : request.getJoinCode();

        Result<CourseEnrollment> result = studentCourseService.joinCourseByJoinCode(
                joinCode,
                student.get().getId()
        );

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
    }

    @PutMapping("/courses/{courseId}/drop")
    public ResponseEntity<?> dropCourse(@PathVariable Long courseId,
                                        Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = studentCourseService.dropCourse(
                courseId,
                student.get().getId()
        );

        return emptyResultToResponse(result);
    }

    @PutMapping("/courses/{courseId}/complete")
    public ResponseEntity<?> completeCourse(@PathVariable Long courseId,
                                            Authentication authentication) {
        Optional<User> student = getCurrentStudent(authentication);

        if (student.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = studentCourseService.completeCourse(
                courseId,
                student.get().getId()
        );

        return emptyResultToResponse(result);
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