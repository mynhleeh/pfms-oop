package com.pfms.presentation.dto.category;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói mã định danh danh mục cần xóa gửi từ tầng trình diễn xuống tầng dịch vụ (UC-CAT-03).
 * Hệ thống sẽ tự động kiểm tra lịch sử giao dịch để phân nhánh xóa vĩnh viễn hoặc xóa mềm.
 */
public final class DeleteCategoryRequestDTO {

    // region State
    private final UUID categoryId;
    // endregion

    // region Constructors
    public DeleteCategoryRequestDTO(UUID categoryId) {
        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
    }
    // endregion

    // region Getters
    public UUID getCategoryId() {
        return this.categoryId;
    }

    public UUID getId() {
        return this.categoryId;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeleteCategoryRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.categoryId);
    }

    @Override
    public String toString() {
        return "DeleteCategoryRequestDTO{" +
                "categoryId=" + this.categoryId +
                '}';
    }
    // endregion
}
