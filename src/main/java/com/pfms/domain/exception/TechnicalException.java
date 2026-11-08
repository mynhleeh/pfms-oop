package com.pfms.domain.exception;

import java.util.Objects;

/**
 * Lớp ngoại lệ trừu tượng đại diện cho các sự cố kỹ thuật, tệp tin và hạ tầng hệ thống.
 * Bắt buộc lưu giữ nguyên nhân lỗi gốc để phục vụ truy vết.
 */
public abstract class TechnicalException extends PfmsException {

    // region Constructors
    protected TechnicalException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, Objects.requireNonNull(cause, "Cause must not be null"));
    }

    protected TechnicalException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, Objects.requireNonNull(cause, "Cause must not be null"));
    }
    // endregion
}
