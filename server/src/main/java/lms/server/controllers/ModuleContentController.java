package lms.server.controllers;

import lms.server.domain.ModuleContentService;
import lms.server.domain.Result;
import lms.server.domain.ResultType;
import lms.server.domain.UserService;
import lms.server.models.Assignment;
import lms.server.models.Lesson;
import lms.server.models.Quiz;
import lms.server.models.RoleName;
import lms.server.models.User;
import lms.server.models.dtos.ModuleContentItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ModuleContentController {

    private final ModuleContentService moduleContentService;
    private final UserService userService;

    public ModuleContentController(ModuleContentService moduleContentService,
                                   UserService userService) {
        this.moduleContentService = moduleContentService;
        this.userService = userService;
    }

    @GetMapping("/api/modules/{moduleId}/content")
    public ResponseEntity<?> findModuleContentItems(@PathVariable Long moduleId,
                                                    Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<List<ModuleContentItem>> result = moduleContentService.findModuleContentItems(
                moduleId,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @GetMapping("/api/modules/{moduleId}/lessons")
    public ResponseEntity<?> findLessonsByModuleId(@PathVariable Long moduleId,
                                                   Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<List<Lesson>> result = moduleContentService.findLessonsByModuleId(
                moduleId,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PostMapping("/api/modules/{moduleId}/lessons")
    public ResponseEntity<?> createLesson(@PathVariable Long moduleId,
                                          @RequestBody Lesson lesson,
                                          Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Lesson> result = moduleContentService.createLesson(
                lesson,
                moduleId,
                teacher.get().getId()
        );

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
    }

    @PutMapping("/api/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable Long lessonId,
                                          @RequestBody Lesson lesson,
                                          Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        if (lesson != null) {
            lesson.setId(lessonId);
        }

        Result<Lesson> result = moduleContentService.updateLesson(
                lesson,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PutMapping("/api/lessons/{lessonId}/publish")
    public ResponseEntity<?> publishLesson(@PathVariable Long lessonId,
                                           Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.publishLesson(lessonId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/lessons/{lessonId}/archive")
    public ResponseEntity<?> archiveLesson(@PathVariable Long lessonId,
                                           Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.archiveLesson(lessonId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/lessons/{lessonId}/draft")
    public ResponseEntity<?> returnLessonToDraft(@PathVariable Long lessonId,
                                                 Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.returnLessonToDraft(lessonId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/lessons/{lessonId}/move")
    public ResponseEntity<?> moveLesson(@PathVariable Long lessonId,
                                        @RequestParam Integer lessonOrder,
                                        Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.moveLesson(
                lessonId,
                teacher.get().getId(),
                lessonOrder
        );

        return emptyResultToResponse(result);
    }

    @DeleteMapping("/api/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long lessonId,
                                          Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.deleteLesson(lessonId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @GetMapping("/api/modules/{moduleId}/assignments")
    public ResponseEntity<?> findAssignmentsByModuleId(@PathVariable Long moduleId,
                                                       Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<List<Assignment>> result = moduleContentService.findAssignmentsByModuleId(
                moduleId,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PostMapping("/api/modules/{moduleId}/assignments")
    public ResponseEntity<?> createAssignment(@PathVariable Long moduleId,
                                              @RequestBody Assignment assignment,
                                              Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Assignment> result = moduleContentService.createAssignment(
                assignment,
                moduleId,
                teacher.get().getId()
        );

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
    }

    @PutMapping("/api/assignments/{assignmentId}")
    public ResponseEntity<?> updateAssignment(@PathVariable Long assignmentId,
                                              @RequestBody Assignment assignment,
                                              Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        if (assignment != null) {
            assignment.setId(assignmentId);
        }

        Result<Assignment> result = moduleContentService.updateAssignment(
                assignment,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PutMapping("/api/assignments/{assignmentId}/publish")
    public ResponseEntity<?> publishAssignment(@PathVariable Long assignmentId,
                                               Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.publishAssignment(assignmentId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/assignments/{assignmentId}/archive")
    public ResponseEntity<?> archiveAssignment(@PathVariable Long assignmentId,
                                               Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.archiveAssignment(assignmentId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/assignments/{assignmentId}/draft")
    public ResponseEntity<?> returnAssignmentToDraft(@PathVariable Long assignmentId,
                                                     Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.returnAssignmentToDraft(assignmentId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/assignments/{assignmentId}/move")
    public ResponseEntity<?> moveAssignment(@PathVariable Long assignmentId,
                                            @RequestParam Integer assignmentOrder,
                                            Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.moveAssignment(
                assignmentId,
                teacher.get().getId(),
                assignmentOrder
        );

        return emptyResultToResponse(result);
    }

    @DeleteMapping("/api/assignments/{assignmentId}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long assignmentId,
                                              Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.deleteAssignment(assignmentId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @GetMapping("/api/modules/{moduleId}/quizzes")
    public ResponseEntity<?> findQuizzesByModuleId(@PathVariable Long moduleId,
                                                   Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<List<Quiz>> result = moduleContentService.findQuizzesByModuleId(
                moduleId,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PostMapping("/api/modules/{moduleId}/quizzes")
    public ResponseEntity<?> createQuiz(@PathVariable Long moduleId,
                                        @RequestBody Quiz quiz,
                                        Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Quiz> result = moduleContentService.createQuiz(
                quiz,
                moduleId,
                teacher.get().getId()
        );

        if (!result.isSuccess()) {
            return resultToResponse(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getPayload());
    }

    @PutMapping("/api/quizzes/{quizId}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long quizId,
                                        @RequestBody Quiz quiz,
                                        Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        if (quiz != null) {
            quiz.setId(quizId);
        }

        Result<Quiz> result = moduleContentService.updateQuiz(
                quiz,
                teacher.get().getId()
        );

        return resultToResponse(result);
    }

    @PutMapping("/api/quizzes/{quizId}/publish")
    public ResponseEntity<?> publishQuiz(@PathVariable Long quizId,
                                         Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.publishQuiz(quizId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/quizzes/{quizId}/archive")
    public ResponseEntity<?> archiveQuiz(@PathVariable Long quizId,
                                         Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.archiveQuiz(quizId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/quizzes/{quizId}/draft")
    public ResponseEntity<?> returnQuizToDraft(@PathVariable Long quizId,
                                               Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.returnQuizToDraft(quizId, teacher.get().getId());
        return emptyResultToResponse(result);
    }

    @PutMapping("/api/quizzes/{quizId}/move")
    public ResponseEntity<?> moveQuiz(@PathVariable Long quizId,
                                      @RequestParam Integer quizOrder,
                                      Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.moveQuiz(
                quizId,
                teacher.get().getId(),
                quizOrder
        );

        return emptyResultToResponse(result);
    }

    @DeleteMapping("/api/quizzes/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId,
                                        Authentication authentication) {
        Optional<User> teacher = getCurrentTeacher(authentication);

        if (teacher.isEmpty()) {
            return unauthorizedOrForbidden(authentication);
        }

        Result<Void> result = moduleContentService.deleteQuiz(quizId, teacher.get().getId());
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