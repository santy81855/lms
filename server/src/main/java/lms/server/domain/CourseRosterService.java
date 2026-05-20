package lms.server.domain;

import lms.server.data.CourseEnrollmentRepository;
import lms.server.data.CourseRepository;
import lms.server.data.UserRepository;
import lms.server.models.Course;
import lms.server.models.CourseEnrollment;
import lms.server.models.User;
import lms.server.models.dtos.CourseRosterStudentResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CourseRosterService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserRepository userRepository;

    public CourseRosterService(CourseRepository courseRepository,
                               CourseEnrollmentRepository courseEnrollmentRepository,
                               UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.userRepository = userRepository;
    }

    public Result<List<CourseRosterStudentResponse>> findStudentsByCourseId(Long courseId,
                                                                            Long teacherId) {
        Result<List<CourseRosterStudentResponse>> result = new Result<>();

        if (!requireId(courseId, "Course id is required.", result)
                || !requireId(teacherId, "Teacher id is required.", result)) {
            return result;
        }

        Optional<Course> course = courseRepository.findByIdAndTeacherId(courseId, teacherId);

        if (course.isEmpty()) {
            result.addMessage("Course not found.", ResultType.NOT_FOUND);
            return result;
        }

        List<CourseRosterStudentResponse> students = courseEnrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::buildRosterStudentResponse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator
                        .comparing(CourseRosterStudentResponse::getLastName)
                        .thenComparing(CourseRosterStudentResponse::getFirstName))
                .toList();

        result.setPayload(students);
        return result;
    }

    private Optional<CourseRosterStudentResponse> buildRosterStudentResponse(CourseEnrollment enrollment) {
        Optional<User> student = userRepository.findById(enrollment.getStudentId());

        if (student.isEmpty()) {
            return Optional.empty();
        }

        CourseRosterStudentResponse response = new CourseRosterStudentResponse();

        response.setStudentId(student.get().getId());
        response.setFirstName(student.get().getFirstName());
        response.setLastName(student.get().getLastName());
        response.setEmail(student.get().getEmail());
        response.setEnrollmentStatus(enrollment.getEnrollmentStatus());
        response.setEnrolledAt(enrollment.getEnrolledAt());
        response.setCompletedAt(enrollment.getCompletedAt());

        return Optional.of(response);
    }

    private boolean requireId(Long id, String message, Result<?> result) {
        if (id == null) {
            result.addMessage(message, ResultType.INVALID);
            return false;
        }

        return true;
    }
}