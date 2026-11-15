package com.pfms.domain.model.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.common.EntityName;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.transaction.TransactionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Nhóm nhu cầu cấp 2 trong hệ thống PFMS.
 * Gom cụm các danh mục chi tiết (cấp 3) cùng loại thu/chi để phân tích tài
 * chính.
 */
public class NeedGroup {

    // region State
    private final UUID needGroupId;
    private EntityName name;
    private final TransactionType transactionType;
    private EntityStatus status;
    private String icon;
    private String color;
    private final List<Category> categories;
    private final LocalDateTime createdAt;
    // endregion

    // region Constructors
    public NeedGroup(UUID needGroupId, String name, TransactionType transactionType, String icon, String color) {
        this(needGroupId, name, transactionType, EntityStatus.ACTIVE,
                icon, color, LocalDateTime.now(), false);
    }

    private NeedGroup(
            UUID needGroupId, String name, TransactionType transactionType,
            EntityStatus status, String icon, String color, LocalDateTime createdAt,
            boolean reconstituting) {

        this.needGroupId = Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        this.name = reconstituting ? EntityName.reconstitute(name) : EntityName.of(name, "Tên nhóm nhu cầu");
        this.transactionType = Objects.requireNonNull(transactionType, "Loại thu chi không được để null");
        Objects.requireNonNull(status, "Trạng thái nhóm nhu cầu không được để null");
        if (status != EntityStatus.ACTIVE && status != EntityStatus.DELETED) {
            throw new IllegalArgumentException("Trạng thái nhóm nhu cầu chỉ hỗ trợ ACTIVE hoặc DELETED");
        }
        this.status = status;
        this.icon = normalizeIcon(icon);
        this.color = normalizeColor(color);
        this.categories = new ArrayList<>();
        this.createdAt = Objects.requireNonNull(createdAt, "Thời điểm tạo nhóm nhu cầu không được để null");
    }

    public static NeedGroup reconstitute(
            UUID needGroupId, String name,
            TransactionType transactionType, EntityStatus status,
            String icon, String color, LocalDateTime createdAt) {
        return new NeedGroup(needGroupId, name, transactionType, status, icon, color, createdAt, true);
    }
    // endregion

    // region Commands
    public void rename(String newName) {
        this.name = EntityName.of(newName, "Tên nhóm nhu cầu");
    }

    public void updateAppearance(String newIcon, String newColor) {
        this.icon = normalizeIcon(newIcon);
        this.color = normalizeColor(newColor);
    }

    public void addCategory(Category category) {
        Objects.requireNonNull(category, "Danh mục không được để null");

        if (this.isDeleted()) {
            throw new BusinessException(ErrorCode.ERR_CAT_005,
                    "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng.");
        }
        if (category.getTransactionType() != this.transactionType) {
            throw new BusinessException(ErrorCode.ERR_CAT_001,
                    "Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm khác loại.");
        }
        this.categories.add(category);
    }

    public void removeCategory(Category category) {
        Objects.requireNonNull(category, "Danh mục không được để null");

        if (!this.categories.contains(category)) {
            throw new BusinessException(ErrorCode.ERR_NOT_FOUND,
                    "Không tìm thấy danh mục yêu cầu trong nhóm nhu cầu.");
        }
        this.categories.remove(category);
    }

    public void markDeleted(int activeGroupCountInType) {
        if (activeGroupCountInType <= 1) {
            throw new BusinessException(ErrorCode.ERR_CAT_002,
                    "Không thể xóa nhóm cuối cùng của loại thu chi này.");
        }
        this.status = EntityStatus.DELETED;
        for (Category category : this.categories) {
            category.markDeleted();
        }
    }

    public void restore() {
        this.status = EntityStatus.ACTIVE;
    }
    // endregion

    // region Queries
    public UUID getNeedGroupId() {
        return this.needGroupId;
    }

    public String getName() {
        return this.name.getValue();
    }

    public EntityName getEntityName() {
        return this.name;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public EntityStatus getStatus() {
        return this.status;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getColor() {
        return this.color;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(this.categories);
    }

    public boolean isActive() {
        return this.status == EntityStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return this.status == EntityStatus.DELETED;
    }
    // endregion

    // region Helpers
    private static String normalizeIcon(String rawIcon) {
        if (rawIcon == null) {
            return "";
        }
        String trimmed = rawIcon.trim();
        if (trimmed.length() > 50) {
            throw new IllegalArgumentException("Biểu tượng nhóm không được vượt quá 50 ký tự.");
        }
        return trimmed;
    }

    private static String normalizeColor(String rawColor) {
        if (rawColor == null) {
            return "";
        }
        String trimmed = rawColor.trim();
        if (trimmed.length() > 10) {
            throw new IllegalArgumentException("Mã màu nhóm không được vượt quá 10 ký tự.");
        }
        return trimmed;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NeedGroup other = (NeedGroup) o;
        return Objects.equals(this.needGroupId, other.needGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.needGroupId);
    }

    @Override
    public String toString() {
        return String.format("NeedGroup[id=%s, name='%s', type=%s, status=%s, categoriesCount=%d]",
                this.needGroupId, this.name, this.transactionType, this.status, this.categories.size());
    }
    // endregion
}
