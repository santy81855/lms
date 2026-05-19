package lms.server.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lms.server.data.CourseRepository;
import lms.server.models.Course;
import lms.server.models.CourseStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {

    private static final String JOIN_CODE_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int JOIN_CODE_LENGTH = 8;
    private static final int JOIN_CODE_ATTEMPTS = 25;

    private final CourseRepository courseRepository;
    private final Validator validator;
    private final SecureRandom secureRandom = new SecureRandom();

    public CourseService(CourseRepository courseRepository, Validator validator) {
        this.courseRepository = courseRepository;
        this.validator = validator;
    }

    public List<Course> findCoursesByTeacherId(Long teacherId) {
        if (teacherId == null) {
            return List.of();
        }

        return courseRepository.findByTeacherId(teacherId);
    }

    public Result<Course> findCourseByIdForTeacher(Long courseId, Long teacherId) {
        Result<Course> result = new Result<>();

        if (!requireId(courseId, "Course id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        Optional<Course> course = courseRepository.findByIdAndTeacherId(courseId, teacherId);

        if (course.isEmpty()) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(course.get());
        return result;
    }

    public boolean teacherOwnsCourse(Long courseId, Long teacherId) {
        if (courseId == null || teacherId == null) {
            return false;
        }

        return courseRepository.findByIdAndTeacherId(courseId, teacherId).isPresent();
    }

    @Transactional
    public Result<Course> createCourse(Course course, Long teacherId) {
        Result<Course> result = new Result<>();

        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (course == null) {
            result.addMessage("Course is required.", ResultType.INVALID);
            return result;
        }

        Course newCourse = new Course();
        newCourse.setTeacherId(teacherId);
        newCourse.setTitle(trim(course.getTitle()));
        newCourse.setSubject(trimToNull(course.getSubject()));
        newCourse.setGradeLevel(course.getGradeLevel());
        newCourse.setDescription(trimToNull(course.getDescription()));
        newCourse.setStatus(CourseStatus.DRAFT);
        newCourse.setJoinCode(generateUniqueJoinCode());

        validate(newCourse, result);

        if (!result.isSuccess()) {
            return result;
        }

        result.setPayload(courseRepository.add(newCourse));
        return result;
    }

    @Transactional
    public Result<Course> updateCourse(Course course, Long teacherId) {
        Result<Course> result = new Result<>();

        if (!requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (course == null) {
            result.addMessage("Course is required.", ResultType.INVALID);
            return result;
        }

        if (!requireId(course.getId(), "Course id is required.", result)) {
            return result;
        }

        Optional<Course> existing = courseRepository.findByIdAndTeacherId(course.getId(), teacherId);

        if (existing.isEmpty()) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        Course updatedCourse = new Course();
        updatedCourse.setId(existing.get().getId());
        updatedCourse.setTeacherId(teacherId);
        updatedCourse.setTitle(trim(course.getTitle()));
        updatedCourse.setSubject(trimToNull(course.getSubject()));
        updatedCourse.setGradeLevel(course.getGradeLevel());
        updatedCourse.setDescription(trimToNull(course.getDescription()));

        // Backend-owned fields should not be overwritten by the request body.
        updatedCourse.setStatus(existing.get().getStatus());
        updatedCourse.setJoinCode(existing.get().getJoinCode());

        validate(updatedCourse, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (!courseRepository.update(updatedCourse)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        Course savedCourse = courseRepository.findByIdAndTeacherId(course.getId(), teacherId)
                .orElse(updatedCourse);

        result.setPayload(savedCourse);
        return result;
    }

    @Transactional
    public Result<Void> publishCourse(Long courseId, Long teacherId) {
        return updateCourseStatus(courseId, teacherId, CourseStatus.ACTIVE);
    }

    @Transactional
    public Result<Void> archiveCourse(Long courseId, Long teacherId) {
        return updateCourseStatus(courseId, teacherId, CourseStatus.ARCHIVED);
    }

    @Transactional
    public Result<Void> returnCourseToDraft(Long courseId, Long teacherId) {
        return updateCourseStatus(courseId, teacherId, CourseStatus.DRAFT);
    }

    @Transactional
    public Result<Void> updateCourseStatus(Long courseId, Long teacherId, CourseStatus status) {
        Result<Void> result = new Result<>();

        if (!requireId(courseId, "Course id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (status == null) {
            result.addMessage("Course status is required.", ResultType.INVALID);
            return result;
        }

        if (!teacherOwnsCourse(courseId, teacherId)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        if (!courseRepository.updateStatus(courseId, teacherId, status)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
        }

        return result;
    }

    @Transactional
    public Result<Void> deleteCourse(Long courseId, Long teacherId) {
        Result<Void> result = new Result<>();

        if (!requireId(courseId, "Course id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        if (!teacherOwnsCourse(courseId, teacherId)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        if (!courseRepository.deleteByIdAndTeacherId(courseId, teacherId)) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
        }

        return result;
    }

    private void validate(Course course, Result<Course> result) {
        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        for (ConstraintViolation<Course> violation : violations) {
            result.addMessage(violation.getMessage(), ResultType.INVALID);
        }
    }

    private String generateUniqueJoinCode() {
        for (int attempt = 0; attempt < JOIN_CODE_ATTEMPTS; attempt++) {
            String joinCode = generateJoinCode();

            if (!courseRepository.existsByJoinCode(joinCode)) {
                return joinCode;
            }
        }

        throw new IllegalStateException("Could not generate a unique course join code.");
    }

    private String generateJoinCode() {
        StringBuilder result = new StringBuilder(JOIN_CODE_LENGTH);

        for (int i = 0; i < JOIN_CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(JOIN_CODE_CHARACTERS.length());
            result.append(JOIN_CODE_CHARACTERS.charAt(index));
        }

        return result.toString();
    }

    private boolean requireId(Long id, String message, Result<?> result) {
        if (id == null) {
            result.addMessage(message, ResultType.INVALID);
            return false;
        }

        return true;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}