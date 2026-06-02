package lms.server.data;

import lms.server.models.FeedbackType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FeedbackTypeJdbcClientRepository implements FeedbackTypeRepository {

    private final JdbcClient jdbcClient;

    public FeedbackTypeJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<FeedbackType> findByCode(String code) {
        final String sql = """
                SELECT id, code, description
                FROM feedback_type
                WHERE code = ?;
                """;

        return jdbcClient.sql(sql)
                .param(code)
                .query((rs, rowNum) -> {
                    FeedbackType feedbackType = new FeedbackType();
                    feedbackType.setId(rs.getLong("id"));
                    feedbackType.setCode(rs.getString("code"));
                    feedbackType.setDescription(rs.getString("description"));
                    return feedbackType;
                })
                .optional();
    }
}