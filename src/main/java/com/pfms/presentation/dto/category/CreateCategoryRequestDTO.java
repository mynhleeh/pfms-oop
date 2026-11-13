package com.pfms.presentation.dto.category;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói dữ liệu yêu cầu tạo danh mục chi tiết cấp 3 thuộc nhóm nhu cầu cha
 * gửi từ tầng trình diễn xuống tầng dịch vụ (UC-CAT-01).
 */
public final class CreateCategoryRequestDTO {

    // region State
    private final UUID parentGroupId;
    private final String name;
    // endregion

    // region Constructors
    public CreateCategoryRequestDTO(UUID parentGroupId, String name) {
        this.parentGroupId = Objects.requireNonNull(parentGroupId, "Mã nhóm nhu cầu cha không được để null");
        this.name = (name == null) ? null : name.trim();
    }
    // endregion

    // region Getters
    public UUID getParentGroupId() {
        return this.parentGroupId;
    }

    public UUID getNeedGroupId() {
        return this.parentGroupId;
    }

    public String getName() {
        return this.name;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateCategoryRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.parentGroupId, that.parentGroupId)
                && Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.parentGroupId, this.name);
    }

    @Override
    public String toString() {
        return "CreateCategoryRequestDTO{" +
                "parentGroupId=" + this.parentGroupId +
                ", name='" + this.name + '\'' +
                '}';
    }
    // endregion
}
