package com.pfms.domain.exception;

import java.time.Instant;
import java.util.Objects;

/**
 * Root abstract unchecked exception for all PFMS domain and technical failures.
 */
public abstract class PfmsException extends RuntimeException {

    // region State
    private final ErrorCode errorCode;
    private final Instant timestamp;
    // endregion

    // region Constructors
    protected PfmsException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    protected PfmsException(ErrorCode errorCode, String customMessage) {
        this(errorCode, customMessage, null);
    }

    protected PfmsException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, null, cause);
    }

    protected PfmsException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage != null ? customMessage
                : Objects.requireNonNull(errorCode, "ErrorCode must not be null").getDefaultMessage(), cause);
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode must not be null");
        this.timestamp = Instant.now();
    }
    // endregion

    // region Getters
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }
    // endregion
}
