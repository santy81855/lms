package lms.server.data;

import lms.server.data.mappers.ModuleContentItemMapper;
import lms.server.models.dtos.ModuleContentItem;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ModuleContentItemJdbcClientRepository implements ModuleContentItemRepository {

    private final JdbcClient jdbcClient;

    public ModuleContentItemJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<ModuleContentItem> findByModuleId(Long moduleId) {
        final String sql = """
                SELECT id,
                       module_id,
                       title,
                       'LESSON' AS item_type,
                       lesson_order AS item_order,
                       status
                FROM lessons
                WHERE module_id = ?

                UNION ALL

                SELECT id,
                       module_id,
                       title,
                       'ASSIGNMENT' AS item_type,
                       assignment_order AS item_order,
                       status
                FROM assignments
                WHERE module_id = ?

                UNION ALL

                SELECT id,
                       module_id,
                       title,
                       'QUIZ' AS item_type,
                       quiz_order AS item_order,
                       status
                FROM quizzes
                WHERE module_id = ?

                ORDER BY item_order, item_type, id;
                """;

        return jdbcClient.sql(sql)
                .param(moduleId)
                .param(moduleId)
                .param(moduleId)
                .query(new ModuleContentItemMapper())
                .list();
    }
}