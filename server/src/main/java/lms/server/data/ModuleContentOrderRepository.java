package lms.server.data;

import lms.server.models.ContentItemType;

import java.util.Optional;

public interface ModuleContentOrderRepository {
    boolean orderExists(Long moduleId, Integer itemOrder);

    int getNextOrderForModule(Long moduleId);

    int countItemsByModuleId(Long moduleId);

    Optional<Integer> findItemOrder(ContentItemType itemType, Long itemId);

    boolean updateItemOrder(ContentItemType itemType, Long itemId, Integer itemOrder);

    int shiftOrdersForward(Long moduleId, Integer startingOrder);

    int shiftOrdersBackward(Long moduleId, Integer startingOrder);
}