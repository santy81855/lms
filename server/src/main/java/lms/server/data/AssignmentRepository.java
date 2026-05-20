package lms.server.data;

import lms.server.models.Assignment;
import lms.server.models.VisibilityStatus;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository {
    Optional<Assignment> findById(Long id);

    Optional<Assignment> findByIdAndModuleId(Long assignmentId, Long moduleId);

    List<Assignment> findByModuleId(Long moduleId);

    Assignment add(Assignment assignment);

    boolean update(Assignment assignment);

    boolean updateOrder(Long assignmentId, Integer assignmentOrder);

    boolean updateStatus(Long assignmentId, VisibilityStatus status);

    boolean deleteById(Long id);

    boolean deleteByIdAndModuleId(Long assignmentId, Long moduleId);
}