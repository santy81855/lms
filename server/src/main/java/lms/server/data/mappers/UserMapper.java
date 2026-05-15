package lms.server.data.mappers;

import lms.server.models.AccountStatus;
import lms.server.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();

        user.setId(resultSet.getLong("id"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));

        user.setAccountStatus(
                AccountStatus.valueOf(resultSet.getString("account_status"))
        );

        if (resultSet.getTimestamp("created_at") != null) {
            user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            user.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        }

        return user;
    }
}