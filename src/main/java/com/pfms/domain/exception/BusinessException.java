package com.pfms.domain.exception;

/**
 * Ngoại lệ đại diện cho các vi phạm quy tắc nghiệp vụ phát sinh từ thao tác của người dùng.
 */
public class BusinessException extends PfmsException {

    // region Constructors
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
    // endregion
}
