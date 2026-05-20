package lms.server.data;

import lms.server.models.ContentItemType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class ModuleContentOrderJdbcClientRepository implements ModuleContentOrderRepository {

    private final JdbcClient jdbcClient;

    public ModuleContentOrderJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean orderExists(Long moduleId, Integer itemOrder) {
        final String sql = """
                SELECT COUNT(*)
                FROM (
                    SELECT id
                    FROM lessons
                    WHERE module_id = ?
                      AND lesson_order = ?

                    UNION ALL

                    SELECT id
                    FROM assignments
                    WHERE module_id = ?
                      AND assignment_order = ?

                    UNION ALL

                    SELECT id
                    FROM quizzes
                    WHERE module_id = ?
                      AND quiz_order = ?
                ) AS module_items;
                """;

        Integer count = jdbcClient.sql(sql)
                .param(moduleId)
                .param(itemOrder)
                .param(moduleId)
                .param(itemOrder)
                .param(moduleId)
                .param(itemOrder)
                .query(Integer.class)
                .single();

        return count != null && count > 0;
    }

    @Override
    public int getNextOrderForModule(Long moduleId) {
        final String sql = """
                SELECT COALESCE(MAX(item_order), 0) + 1
                FROM (
                    SELECT lesson_order AS item_order
                    FROM lessons
                    WHERE module_id = ?

                    UNION ALL

                    SELECT assignment_order AS item_order
                    FROM assignments
                    WHERE module_id = ?

                    UNION ALL

                    SELECT quiz_order AS item_order
                    FROM quizzes
                    WHERE module_id = ?
                ) AS module_items;
                """;

        Integer nextOrder = jdbcClient.sql(sql)
                .param(moduleId)
                .param(moduleId)
                .param(moduleId)
                .query(Integer.class)
                .single();

        return nextOrder == null ? 1 : nextOrder;
    }

    @Override
    public int countItemsByModuleId(Long moduleId) {
        final String sql = """
                SELECT COUNT(*)
                FROM (
                    SELECT id
                    FROM lessons
                    WHERE module_id = ?

                    UNION ALL

                    SELECT id
                    FROM assignments
                    WHERE module_id = ?

                    UNION ALL

                    SELECT id
                    FROM quizzes
                    WHERE module_id = ?
                ) AS module_items;
                """;

        Integer count = jdbcClient.sql(sql)
                .param(moduleId)
                .param(moduleId)
                .param(moduleId)
                .query(Integer.class)
                .single();

        return count == null ? 0 : count;
    }

    @Override
    public Optional<Integer> findItemOrder(ContentItemType itemType, Long itemId) {
        String sql = switch (itemType) {
            case LESSON -> """
                    SELECT lesson_order
                    FROM lessons
                    WHERE id = ?;
                    """;

            case ASSIGNMENT -> """
                    SELECT assignment_order
                    FROM assignments
                    WHERE id = ?;
                    """;

            case QUIZ -> """
                    SELECT quiz_order
                    FROM quizzes
                    WHERE id = ?;
                    """;
        };

        return jdbcClient.sql(sql)
                .param(itemId)
                .query(Integer.class)
                .optional();
    }

    @Override
    public boolean updateItemOrder(ContentItemType itemType, Long itemId, Integer itemOrder) {
        String sql = switch (itemType) {
            case LESSON -> """
                    UPDATE lessons
                    SET lesson_order = ?
                    WHERE id = ?;
                    """;

            case ASSIGNMENT -> """
                    UPDATE assignments
                    SET assignment_order = ?
                    WHERE id = ?;
                    """;

            case QUIZ -> """
                    UPDATE quizzes
                    SET quiz_order = ?
                    WHERE id = ?;
                    """;
        };

        return jdbcClient.sql(sql)
                .param(itemOrder)
                .param(itemId)
                .update() > 0;
    }

    @Override
    @Transactional
    public int shiftOrdersForward(Long moduleId, Integer startingOrder) {
        int rowsAffected = 0;

        final String lessonSql = """
                UPDATE lessons
                SET lesson_order = lesson_order + 1
                WHERE module_id = ?
                  AND lesson_order >= ?
                ORDER BY lesson_order DESC;
                """;

        final String assignmentSql = """
                UPDATE assignments
                SET assignment_order = assignment_order + 1
                WHERE module_id = ?
                  AND assignment_order >= ?
                ORDER BY assignment_order DESC;
                """;

        final String quizSql = """
                UPDATE quizzes
                SET quiz_order = quiz_order + 1
                WHERE module_id = ?
                  AND quiz_order >= ?
                ORDER BY quiz_order DESC;
                """;

        rowsAffected += jdbcClient.sql(lessonSql)
                .param(moduleId)
                .param(startingOrder)
                .update();

        rowsAffected += jdbcClient.sql(assignmentSql)
                .param(moduleId)
                .param(startingOrder)
                .update();

        rowsAffected += jdbcClient.sql(quizSql)
                .param(moduleId)
                .param(startingOrder)
                .update();

        return rowsAffected;
    }

    @Override
    @Transactional
    public int shiftOrdersBackward(Long moduleId, Integer startingOrder) {
        int rowsAffected = 0;

        final String lessonSql = """
                UPDATE lessons
                SET lesson_order = lesson_order - 1
                WHERE module_id = ?
                  AND lesson_order > ?
                ORDER BY lesson_order ASC;
                """;

        final String assignmentSql = """
                UPDATE assignments
                SET assignment_order = assignment_order - 1
                WHERE module_id = ?
                  AND assignment_order > ?
                ORDER BY assignment_order ASC;
                """;

        final String quizSql = """
                UPDATE quizzes
                SET quiz_order = quiz_order - 1
                WHERE module_id = ?
                  AND quiz_order > ?
                ORDER BY quiz_order ASC;
                """;

        rowsAffected += jdbcClient.sql(lessonSql)
                .param(moduleId)
                .param(startingOrder)
                .update();

        rowsAffected += jdbcClient.sql(assignmentSql)
                .param(moduleId)
                .param(startingOrder)
                .update();

        rowsAffected += jdbcClient.sql(quizSql)
                .param(moduleId)
                .param(startingOrder)
                .update();

        return rowsAffected;
    }
}