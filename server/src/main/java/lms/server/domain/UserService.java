package lms.server.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lms.server.data.RoleRepository;
import lms.server.data.UserRepository;
import lms.server.models.AccountStatus;
import lms.server.models.Role;
import lms.server.models.RoleName;
import lms.server.models.User;
import lms.server.models.dtos.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Validator validator;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, Validator validator, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Result<User> registerStudent(RegisterRequest request) {
        return registerWithRole(request, RoleName.STUDENT);
    }

    @Transactional
    public Result<User> registerTeacher(RegisterRequest request) {
        return registerWithRole(request, RoleName.TEACHER);
    }

    @Transactional
    private Result<User> registerWithRole(RegisterRequest request, RoleName roleName) {
        Result<User> result = new Result<>();
        if (request == null) {
            result.addMessage("Register request is required.", ResultType.INVALID);
            return result;
        }
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<RegisterRequest> violation : violations) {
                result.addMessage(violation.getMessage(), ResultType.INVALID);
            }
            return result;
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            result.addMessage("Email is already in use.",  ResultType.INVALID);
            return result;
        }

        // this is a backend error, rather than a user error, so we throw an exception
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException(roleName + " role is missing."));

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAccountStatus(AccountStatus.ACTIVE);

        User savedUser = userRepository.add(user);

        roleRepository.addRoleToUser(savedUser.getId(), role.getId());

        savedUser.getRoles().add(role);
        result.setPayload(savedUser);

        return result;
    }

    public Optional<User> findByEmailWithRoles(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        return userRepository.findByEmailWithRoles(email.toLowerCase().trim());
    }

}
