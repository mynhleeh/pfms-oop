package com.pfms.domain.exception;

import java.util.Objects;

/**
 * Ngoại lệ ném ra khi không tìm thấy thực thể theo mã định danh hoặc tên trong hệ thống.
 */
public class EntityNotFoundException extends BusinessException {

    // region State
    private final String entityType;
    private final String identifier;
    // endregion

    // region Constructors
    public EntityNotFoundException(String entityType, String identifier) {
        this(ErrorCode.ERR_NOT_FOUND, entityType, identifier);
    }

    public EntityNotFoundException(ErrorCode errorCode, String entityType, String identifier) {
        super(errorCode, String.format("Không tìm thấy %s với định danh [%s].", entityType, identifier));
        this.entityType = Objects.requireNonNull(entityType, "Loại thực thể không được để null");
        this.identifier = Objects.requireNonNull(identifier, "Định danh không được để null");
    }
    // endregion

    // region Getters
    public String getEntityType() {
        return this.entityType;
    }

    public String getIdentifier() {
        return this.identifier;
    }
    // endregion
}
