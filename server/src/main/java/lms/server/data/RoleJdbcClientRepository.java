package lms.server.data;

import lms.server.data.mappers.RoleMapper;
import lms.server.models.Role;
import lms.server.models.RoleName;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleJdbcClientRepository implements RoleRepository{

    private final JdbcClient jdbcClient;

    public RoleJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Role> findByName(RoleName roleName) {
        final String sql = """
                SELECT id, name, description, created_at
                FROM roles
                WHERE name = ?;
                """;

        return jdbcClient.sql(sql)
                .param(roleName.name())
                .query(new RoleMapper())
                .optional();
    }

    @Override
    public List<Role> findByUserId(Long userId) {
        final String sql = """
                SELECT r.id, r.name, r.description, r.created_at
                FROM roles r
                INNER JOIN user_roles ur ON r.id = ur.role_id
                WHERE ur.user_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(userId)
                .query(new RoleMapper())
                .list();
    }

    @Override
    public void addRoleToUser(Long userId, Long roleId) {
        final String sql = """
                INSERT INTO user_roles (user_id, role_id)
                VALUES (?, ?);
                """;

        jdbcClient.sql(sql)
                .param(userId)
                .param(roleId)
                .update();
    }
}