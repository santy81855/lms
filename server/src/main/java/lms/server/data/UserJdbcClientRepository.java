package lms.server.data;

import lms.server.data.mappers.UserMapper;
import lms.server.models.Role;
import lms.server.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJdbcClientRepository implements UserRepository {

    private final JdbcClient jdbcClient;
    private final RoleJdbcClientRepository roleJdbcClientRepository;

    public UserJdbcClientRepository(JdbcClient jdbcClient,  RoleJdbcClientRepository roleJdbcClientRepository) {
        this.jdbcClient = jdbcClient;
        this.roleJdbcClientRepository = roleJdbcClientRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        final String sql = """
            SELECT id, first_name, last_name, email, password_hash,
                   account_status, created_at, updated_at
            FROM users
            WHERE id = ?;
            """;

        return jdbcClient.sql(sql).param(id)
                .query(new UserMapper())
                .optional();

    }

    @Override
    public Optional<User> findByIdWithRole(Long id) {
        Optional<User> result = findById(id);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        User user = result.get();

        List<Role> roles = roleJdbcClientRepository.findByUserId(id);
        user.setRoles(new HashSet<>(roles));

        return Optional.of(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final String sql = """
            SELECT id, first_name, last_name, email, password_hash,
                   account_status, created_at, updated_at
            FROM users
            WHERE email = ?;
            """;

        return jdbcClient.sql(sql)
                .param(email)
                .query(new UserMapper())
                .optional();
    }

    @Override
    public Optional<User> findByEmailWithRoles(String email) {
        return findByEmail(email).map(user -> {
            List<Role> roles = roleJdbcClientRepository.findByUserId(user.getId());
            user.setRoles(new HashSet<>(roles));
            return user;
        });
    }

    @Override
    public User add(User user) {
        final String sql = """
            INSERT INTO users (
                first_name,
                last_name,
                email,
                password_hash,
                account_status
            )
            VALUES (?, ?, ?, ?, ?);
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(user.getFirstName())
                .param(user.getLastName())
                .param(user.getEmail())
                .param(user.getPasswordHash())
                .param(user.getAccountStatus().name())
                .update(keyHolder, "id");

        if (rowsAffected <= 0) {
            throw new DataAccessException("User insert failed.") {};
        }

        user.setId(keyHolder.getKey().longValue());

        return user;
    }

    @Override
    public List<User> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        final String sql = """
            SELECT id, first_name, last_name, email, password_hash,
                   account_status, created_at, updated_at
            FROM users
            WHERE id IN (:ids);
            """;

        return jdbcClient.sql(sql)
                .param("ids", ids)
                .query(new UserMapper())
                .list();
    }
}
