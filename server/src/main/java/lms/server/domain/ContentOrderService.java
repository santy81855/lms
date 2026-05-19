package lms.server.domain;

import lms.server.data.ModuleContentOrderRepository;
import lms.server.models.ContentItemType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentOrderService {

    private final ModuleContentOrderRepository moduleContentOrderRepository;

    public ContentOrderService(ModuleContentOrderRepository moduleContentOrderRepository) {
        this.moduleContentOrderRepository = moduleContentOrderRepository;
    }

    public int getNextOrderForModule(Long moduleId) {
        return moduleContentOrderRepository.getNextOrderForModule(moduleId);
    }

    public int countItemsByModuleId(Long moduleId) {
        return moduleContentOrderRepository.countItemsByModuleId(moduleId);
    }

    public Integer resolveNewContentItemOrder(Long moduleId, Integer requestedOrder, Result<?> result) {
        int nextOrder = getNextOrderForModule(moduleId);

        if (requestedOrder == null) {
            return nextOrder;
        }

        if (requestedOrder <= 0) {
            result.addMessage("Content item order must be greater than zero.", ResultType.INVALID);
            return null;
        }

        if (requestedOrder > nextOrder) {
            result.addMessage("Content item order cannot be greater than the next available order.", ResultType.INVALID);
            return null;
        }

        return requestedOrder;
    }

    public void shiftOrdersForwardForInsert(Long moduleId, Integer itemOrder) {
        int nextOrder = getNextOrderForModule(moduleId);

        if (itemOrder < nextOrder) {
            moduleContentOrderRepository.shiftOrdersForward(moduleId, itemOrder);
        }
    }

    public void shiftOrdersBackwardAfterDelete(Long moduleId, Integer deletedOrder) {
        moduleContentOrderRepository.shiftOrdersBackward(moduleId, deletedOrder);
    }

    @Transactional
    public Result<Void> moveContentItem(ContentItemType itemType,
                                        Long itemId,
                                        Long moduleId,
                                        Integer currentOrder,
                                        Integer newOrder) {
        Result<Void> result = new Result<>();

        if (itemType == null) {
            result.addMessage("Content item type is required.", ResultType.INVALID);
            return result;
        }

        if (itemId == null) {
            result.addMessage("Content item id is required.", ResultType.INVALID);
            return result;
        }

        if (moduleId == null) {
            result.addMessage("Module id is required.", ResultType.INVALID);
            return result;
        }

        if (currentOrder == null) {
            result.addMessage("Current content item order is required.", ResultType.INVALID);
            return result;
        }

        if (newOrder == null) {
            result.addMessage("Content item order is required.", ResultType.INVALID);
            return result;
        }

        if (newOrder <= 0) {
            result.addMessage("Content item order must be greater than zero.", ResultType.INVALID);
            return result;
        }

        int itemCount = countItemsByModuleId(moduleId);

        if (newOrder > itemCount) {
            result.addMessage("Content item order cannot be greater than the number of items in the module.", ResultType.INVALID);
            return result;
        }

        if (newOrder.equals(currentOrder)) {
            return result;
        }

        if (!moduleContentOrderRepository.updateItemOrder(itemType, itemId, 0)) {
            throw new IllegalStateException("Could not temporarily move content item for reordering.");
        }

        moduleContentOrderRepository.shiftOrdersBackward(moduleId, currentOrder);
        moduleContentOrderRepository.shiftOrdersForward(moduleId, newOrder);

        if (!moduleContentOrderRepository.updateItemOrder(itemType, itemId, newOrder)) {
            throw new IllegalStateException("Could not finish content item reordering.");
        }

        return result;
    }
}