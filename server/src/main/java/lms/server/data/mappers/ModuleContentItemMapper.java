package lms.server.data.mappers;

import lms.server.models.ContentItemType;
import lms.server.models.VisibilityStatus;
import lms.server.models.dtos.ModuleContentItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ModuleContentItemMapper implements RowMapper<ModuleContentItem> {

    @Override
    public ModuleContentItem mapRow(ResultSet resultSet, int i) throws SQLException {
        ModuleContentItem item = new ModuleContentItem();

        item.setId(resultSet.getLong("id"));
        item.setModuleId(resultSet.getLong("module_id"));
        item.setTitle(resultSet.getString("title"));

        item.setItemType(
                ContentItemType.valueOf(resultSet.getString("item_type"))
        );

        item.setItemOrder(resultSet.getInt("item_order"));

        item.setStatus(
                VisibilityStatus.valueOf(resultSet.getString("status"))
        );

        return item;
    }
}