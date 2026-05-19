package lms.server.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

public class Role {
    private Long id;
    @NotNull(message="Name is required.")
    private RoleName name;
    @NotBlank(message="Description is required.")
    private String description;
    @NotNull(message="Created at date time is required.")
    LocalDateTime createdAt;
    @NotNull(message="Updated at date time is required.")
    LocalDateTime modifiedDate;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdDate=" + createdAt +
                ", modifiedDate=" + modifiedDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name) && Objects.equals(description, role.description) && Objects.equals(createdAt, role.createdAt) && Objects.equals(modifiedDate, role.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, createdAt, modifiedDate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
