package lms.server.models.dtos;

import lms.server.models.ContentItemType;
import lms.server.models.VisibilityStatus;

import java.util.Objects;

public class ModuleContentItem {
    private Long id;
    private Long moduleId;
    private String title;
    private ContentItemType itemType;
    private Integer itemOrder;
    private VisibilityStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ContentItemType getItemType() {
        return itemType;
    }

    public void setItemType(ContentItemType itemType) {
        this.itemType = itemType;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public VisibilityStatus getStatus() {
        return status;
    }

    public void setStatus(VisibilityStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ModuleContentItem that = (ModuleContentItem) o;
        return Objects.equals(id, that.id) && Objects.equals(moduleId, that.moduleId) && Objects.equals(title, that.title) && itemType == that.itemType && Objects.equals(itemOrder, that.itemOrder) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, moduleId, title, itemType, itemOrder, status);
    }

    @Override
    public String toString() {
        return "ModuleContentItem{" +
                "id=" + id +
                ", moduleId=" + moduleId +
                ", title='" + title + '\'' +
                ", itemType=" + itemType +
                ", itemOrder=" + itemOrder +
                ", status=" + status +
                '}';
    }
}