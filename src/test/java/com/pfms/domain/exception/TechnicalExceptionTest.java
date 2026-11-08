package com.pfms.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for TechnicalException")
class TechnicalExceptionTest {

    private static class TestTechnicalException extends TechnicalException {
        TestTechnicalException(ErrorCode errorCode, Throwable cause) {
            super(errorCode, cause);
        }

        TestTechnicalException(ErrorCode errorCode, String customMessage, Throwable cause) {
            super(errorCode, customMessage, cause);
        }
    }

    @Test
    @DisplayName("Should create TechnicalException with ErrorCode and cause")
    void testConstructorWithErrorCodeAndCause() {
        IOException cause = new IOException("Disk read error");
        TestTechnicalException ex = new TestTechnicalException(ErrorCode.ERR_SYS_001, cause);

        assertEquals(ErrorCode.ERR_SYS_001, ex.getErrorCode());
        assertEquals(ErrorCode.ERR_SYS_001.getDefaultMessage(), ex.getMessage());
        assertSame(cause, ex.getCause());
        assertNotNull(ex.getTimestamp());
        assertTrue(ex.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should create TechnicalException with customMessage and cause")
    void testConstructorWithCustomMessageAndCause() {
        IOException cause = new IOException("Access denied");
        String customMsg = "Không thể đọc tệp sao lưu hệ thống";
        TestTechnicalException ex = new TestTechnicalException(ErrorCode.ERR_SYS_001, customMsg, cause);

        assertEquals(ErrorCode.ERR_SYS_001, ex.getErrorCode());
        assertEquals(customMsg, ex.getMessage());
        assertSame(cause, ex.getCause());
        assertNotNull(ex.getTimestamp());
    }

    @Test
    @DisplayName("Should fallback to defaultMessage when customMessage is null")
    void testFallbackWhenCustomMessageIsNull() {
        IOException cause = new IOException("Corrupted payload");
        TestTechnicalException ex = new TestTechnicalException(ErrorCode.ERR_DAT_001, null, cause);

        assertEquals(ErrorCode.ERR_DAT_001, ex.getErrorCode());
        assertEquals(ErrorCode.ERR_DAT_001.getDefaultMessage(), ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    @DisplayName("Should throw NullPointerException when cause is null")
    void testMandatoryCauseConstraint() {
        NullPointerException ex1 = assertThrows(NullPointerException.class,
                () -> new TestTechnicalException(ErrorCode.ERR_SYS_001, null));
        assertEquals("Cause must not be null", ex1.getMessage());

        NullPointerException ex2 = assertThrows(NullPointerException.class,
                () -> new TestTechnicalException(ErrorCode.ERR_SYS_001, "Custom msg", null));
        assertEquals("Cause must not be null", ex2.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException when ErrorCode is null")
    void testNullErrorCodeThrows() {
        IOException cause = new IOException("Failure");
        assertThrows(NullPointerException.class, () -> new TestTechnicalException(null, cause));
        assertThrows(NullPointerException.class, () -> new TestTechnicalException(null, "Msg", cause));
    }

    @Test
    @DisplayName("Should verify inheritance hierarchy for TechnicalException")
    void testTypeHierarchy() {
        IOException cause = new IOException("IO failure");
        TestTechnicalException ex = new TestTechnicalException(ErrorCode.ERR_SYS_001, cause);

        assertInstanceOf(TechnicalException.class, ex);
        assertInstanceOf(PfmsException.class, ex);
        assertInstanceOf(RuntimeException.class, ex);
    }
}

