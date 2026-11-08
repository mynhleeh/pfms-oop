package com.pfms.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for StorageException")
class StorageExceptionTest {

    private static final String CUSTOM_MESSAGE = "Không thể ghi dữ liệu ví vào tệp wallets.json";
    private static final IOException ROOT_CAUSE = new IOException("Disk full or permission denied");

    @Test
    @DisplayName("Should create exception with cause only and verify default ErrorCode and properties")
    void testConstructorWithCauseOnly() {
        StorageException ex = new StorageException(ROOT_CAUSE);

        assertEquals(ErrorCode.ERR_SYS_001, ex.getErrorCode());
        assertEquals(ErrorCode.ERR_SYS_001.getDefaultMessage(), ex.getMessage());
        assertSame(ROOT_CAUSE, ex.getCause());
        assertNotNull(ex.getTimestamp());
        assertTrue(ex.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should create exception with custom message and cause with default ErrorCode")
    void testConstructorWithCustomMessageAndCause() {
        StorageException ex = new StorageException(CUSTOM_MESSAGE, ROOT_CAUSE);

        assertEquals(ErrorCode.ERR_SYS_001, ex.getErrorCode());
        assertEquals(CUSTOM_MESSAGE, ex.getMessage());
        assertSame(ROOT_CAUSE, ex.getCause());
    }

    @Test
    @DisplayName("Should create exception with custom ErrorCode and cause")
    void testConstructorWithErrorCodeAndCause() {
        StorageException ex = new StorageException(ErrorCode.ERR_SYS_001, ROOT_CAUSE);

        assertEquals(ErrorCode.ERR_SYS_001, ex.getErrorCode());
        assertEquals(ErrorCode.ERR_SYS_001.getDefaultMessage(), ex.getMessage());
        assertSame(ROOT_CAUSE, ex.getCause());
    }

    @Test
    @DisplayName("Should create exception with canonical 3-arg constructor preserving all properties")
    void testCanonicalConstructor() {
        StorageException ex = new StorageException(ErrorCode.ERR_SYS_001, CUSTOM_MESSAGE, ROOT_CAUSE);

        assertEquals(ErrorCode.ERR_SYS_001, ex.getErrorCode());
        assertEquals(CUSTOM_MESSAGE, ex.getMessage());
        assertSame(ROOT_CAUSE, ex.getCause());
    }

    @Test
    @DisplayName("Should fallback to default message when custom message is null")
    void testNullCustomMessageFallsBackToDefaultMessage() {
        StorageException ex = new StorageException((String) null, ROOT_CAUSE);

        assertEquals(ErrorCode.ERR_SYS_001, ex.getErrorCode());
        assertEquals(ErrorCode.ERR_SYS_001.getDefaultMessage(), ex.getMessage());
        assertSame(ROOT_CAUSE, ex.getCause());
    }

    @Test
    @DisplayName("Should throw NullPointerException when cause or errorCode is null")
    void testDefensiveNullChecks() {
        // Cause is null
        assertThrows(NullPointerException.class, () -> new StorageException((Throwable) null));
        assertThrows(NullPointerException.class, () -> new StorageException(CUSTOM_MESSAGE, null));
        assertThrows(NullPointerException.class, () -> new StorageException(ErrorCode.ERR_SYS_001, null));
        assertThrows(NullPointerException.class,
                () -> new StorageException(ErrorCode.ERR_SYS_001, CUSTOM_MESSAGE, null));

        // ErrorCode is null
        assertThrows(NullPointerException.class, () -> new StorageException((ErrorCode) null, ROOT_CAUSE));
        assertThrows(NullPointerException.class,
                () -> new StorageException(null, CUSTOM_MESSAGE, ROOT_CAUSE));
    }

    @Test
    @DisplayName("Should satisfy inheritance hierarchy: StorageException -> TechnicalException -> PfmsException")
    void testInheritanceHierarchy() {
        StorageException ex = new StorageException(ROOT_CAUSE);

        assertTrue(ex instanceof TechnicalException);
        assertTrue(ex instanceof PfmsException);
        assertTrue(ex instanceof RuntimeException);
        assertFalse((Object) ex instanceof BusinessException,
                "StorageException không được thuộc nhánh BusinessException");
    }
}
