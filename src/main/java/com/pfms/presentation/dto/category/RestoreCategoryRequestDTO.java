package com.pfms.presentation.dto.category;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói mã định danh danh mục đã xóa mềm cần khôi phục trạng thái hoạt động
 * gửi từ tầng trình diễn xuống tầng dịch vụ (UC-CAT-03 / UC-CAT-02).
 */
public final class RestoreCategoryRequestDTO {

    // region State
    private final UUID categoryId;
    // endregion

    // region Constructors
    public RestoreCategoryRequestDTO(UUID categoryId) {
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
        if (!(o instanceof RestoreCategoryRequestDTO that)) {
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
        return "RestoreCategoryRequestDTO{" +
                "categoryId=" + this.categoryId +
                '}';
    }
    // endregion
}

