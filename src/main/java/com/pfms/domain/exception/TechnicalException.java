package com.pfms.domain.exception;

import java.util.Objects;

/**
 * Base abstract exception representing system, storage, and infrastructure failures.
 * Strictly mandates encapsulation of a technical Throwable cause to preserve stack traces.
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

