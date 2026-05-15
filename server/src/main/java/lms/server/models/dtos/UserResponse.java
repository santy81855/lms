package lms.server.models.dtos;

import lms.server.models.Role;
import lms.server.models.RoleName;
import lms.server.models.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String accountStatus;
    private Set<RoleName> roles;

    public UserResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.accountStatus = user.getAccountStatus().name();
        this.roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public Set<RoleName> getRoles() {
        return roles;
    }
}
