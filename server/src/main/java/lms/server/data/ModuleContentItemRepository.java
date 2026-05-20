package lms.server.data;

import lms.server.models.dtos.ModuleContentItem;

import java.util.List;

public interface ModuleContentItemRepository {
    List<ModuleContentItem> findByModuleId(Long moduleId);
}