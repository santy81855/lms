package lms.server.data;

import lms.server.models.User;

import java.util.Optional;

public interface UserRepository {
    public Optional<User> findById(Long id);
    public Optional<User> findByIdWithRole(Long id);
    public Optional<User> findByEmail(String email);
    Optional<User> findByEmailWithRoles(String email);
    public User add(User user);
}
