package lms.server.data.mappers;

import lms.server.models.Role;
import lms.server.models.RoleName;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet resultSet, int i) throws SQLException {
        Role role = new Role();

        role.setId(resultSet.getLong("id"));

        role.setName(
                RoleName.valueOf(resultSet.getString("name"))
        );

        role.setDescription(resultSet.getString("description"));

        if (resultSet.getTimestamp("created_at") != null) {
            role.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        return role;
    }
}