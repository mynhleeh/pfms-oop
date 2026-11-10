package com.pfms.domain.model.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.transaction.TransactionType;

import java.util.Objects;
import java.util.UUID;

/**
 * Thực thể miền Danh mục chi tiết (Category) trong hệ thống PFMS.
 * Đại diện cho đơn vị phân loại cấp 3 gán nhãn mục đích cho các giao dịch thu chi.
 */
public class Category {

    // region State
    private final UUID categoryId;
    private String name;
    private UUID needGroupId;
    private final TransactionType transactionType;
    private CategoryStatus status;
    private String icon;
    private String color;
    // endregion

    // region Constructors
    public Category(UUID categoryId, String name, UUID needGroupId,
                    TransactionType transactionType, String icon, String color) {

        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.name = validateAndNormalizeName(name);
        this.needGroupId = Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        this.transactionType = Objects.requireNonNull(transactionType, "Loại thu chi không được để null");
        this.status = CategoryStatus.ACTIVE;
        this.icon = normalizeIcon(icon);
        this.color = normalizeColor(color);
    }
    // endregion

    // region Commands
    public void rename(String newName) {
        this.name = validateAndNormalizeName(newName);
    }

    public void changeNeedGroup(UUID newNeedGroupId, TransactionType targetGroupType) {
        Objects.requireNonNull(newNeedGroupId, "Mã nhóm nhu cầu mới không được để null");
        Objects.requireNonNull(targetGroupType, "Loại thu chi nhóm đích không được để null");

        if (targetGroupType != this.transactionType) {
            throw new BusinessException(ErrorCode.ERR_CAT_001,
                    "Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm khác loại.");
        }
        this.needGroupId = newNeedGroupId;
    }

    public void updateAppearance(String newIcon, String newColor) {
        this.icon = normalizeIcon(newIcon);
        this.color = normalizeColor(newColor);
    }

    public void markDeleted() {
        this.status = CategoryStatus.DELETED;
    }

    public void restore(boolean isParentGroupActive) {
        if (!isParentGroupActive) {
            throw new BusinessException(ErrorCode.ERR_CAT_004,
                    "Nhóm chứa danh mục này đang ở trạng thái đã xóa. Hãy khôi phục nhóm trước.");
        }
        this.status = CategoryStatus.ACTIVE;
    }
    // endregion

    // region Queries
    public UUID getCategoryId() {
        return this.categoryId;
    }

    public String getName() {
        return this.name;
    }

    public UUID getNeedGroupId() {
        return this.needGroupId;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public CategoryStatus getStatus() {
        return this.status;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getColor() {
        return this.color;
    }

    public boolean isActive() {
        return this.status == CategoryStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return this.status == CategoryStatus.DELETED;
    }
    // endregion

    // region Helpers
    private static String validateAndNormalizeName(String rawName) {
        if (rawName == null) {
            throw new BusinessException(ErrorCode.ERR_NAM_001, "Tên danh mục không được để null.");
        }
        String trimmed = rawName.trim();
        if (trimmed.isEmpty() || trimmed.length() > 50) {
            throw new BusinessException(ErrorCode.ERR_NAM_001,
                    "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu.");
        }
        return trimmed;
    }

    private static String normalizeIcon(String rawIcon) {
        if (rawIcon == null) {
            return "";
        }
        String trimmed = rawIcon.trim();
        if (trimmed.length() > 50) {
            throw new IllegalArgumentException("Biểu tượng danh mục không được vượt quá 50 ký tự.");
        }
        return trimmed;
    }

    private static String normalizeColor(String rawColor) {
        if (rawColor == null) {
            return "";
        }
        String trimmed = rawColor.trim();
        if (trimmed.length() > 10) {
            throw new IllegalArgumentException("Mã màu danh mục không được vượt quá 10 ký tự.");
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
        Category category = (Category) o;
        return Objects.equals(this.categoryId, category.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.categoryId);
    }

    @Override
    public String toString() {
        return String.format("Category[id=%s, name='%s', needGroupId=%s, type=%s, status=%s]",
                this.categoryId, this.name, this.needGroupId, this.transactionType, this.status);
    }
    // endregion
}

