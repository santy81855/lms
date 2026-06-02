package lms.server.data;

import lms.server.models.FeedbackType;

import java.util.Optional;

public interface FeedbackTypeRepository {
    Optional<FeedbackType> findByCode(String code);
}