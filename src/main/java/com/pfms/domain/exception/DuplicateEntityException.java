package com.pfms.domain.exception;

import java.util.Objects;

/**
 * Ngoại lệ nghiệp vụ ném ra khi tạo mới hoặc cập nhật Ví, Danh mục có tên bị
 * trùng lặp với một bản ghi đang hoạt động khác trong hệ thống.
 */
public class DuplicateEntityException extends BusinessException {

    // region State
    private final String entityType;
    private final String name;
    // endregion

    // region Constructors
    public DuplicateEntityException(String entityType, String name) {
        this(ErrorCode.ERR_NAM_002, entityType, name);
    }

    public DuplicateEntityException(ErrorCode errorCode, String entityType, String name) {
        super(errorCode, String.format("%s với tên [%s] đã tồn tại trong hệ thống.", entityType, name));
        this.entityType = Objects.requireNonNull(entityType, "Loại thực thể không được để null");
        this.name = Objects.requireNonNull(name, "Tên thực thể không được để null");
    }
    // endregion

    // region Getters
    public String getEntityType() {
        return this.entityType;
    }

    public String getName() {
        return this.name;
    }
    // endregion
}
