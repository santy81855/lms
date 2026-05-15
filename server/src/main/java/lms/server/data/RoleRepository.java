package lms.server.data;

import lms.server.models.Role;
import lms.server.models.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    List<Role> findByUserId(Long userId);
    Optional<Role> findByName(RoleName roleName);
    void addRoleToUser(Long userId, Long roleId);
}
