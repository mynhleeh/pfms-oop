package com.pfms.domain.exception;

/**
 * Base exception representing business rule violations and domain constraint
 * failures.
 * Originates from user actions and domain logic; does not wrap a technical
 * Throwable cause.
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
