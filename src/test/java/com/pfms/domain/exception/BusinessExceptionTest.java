package com.pfms.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for BusinessException")
class BusinessExceptionTest {

    @Test
    @DisplayName("Should create BusinessException with ErrorCode and verify defaults")
    void testConstructorWithErrorCodeOnly() {
        BusinessException ex = new BusinessException(ErrorCode.ERR_FIN_001);

        assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        assertEquals(ErrorCode.ERR_FIN_001.getDefaultMessage(), ex.getMessage());
        assertNotNull(ex.getTimestamp());
        assertTrue(ex.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should create BusinessException with ErrorCode and custom message")
    void testConstructorWithErrorCodeAndCustomMessage() {
        String customMsg = "Ví nguồn không hợp lệ để chuyển khoản";
        BusinessException ex = new BusinessException(ErrorCode.ERR_TRF_001, customMsg);

        assertEquals(ErrorCode.ERR_TRF_001, ex.getErrorCode());
        assertEquals(customMsg, ex.getMessage());
        assertNotNull(ex.getTimestamp());
    }

    @Test
    @DisplayName("Should fallback to ErrorCode defaultMessage when customMessage is null")
    void testNullCustomMessageFallsBackToDefaultMessage() {
        BusinessException ex = new BusinessException(ErrorCode.ERR_FIN_001, null);

        assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        assertEquals(ErrorCode.ERR_FIN_001.getDefaultMessage(), ex.getMessage());
        assertNotNull(ex.getTimestamp());
    }

    @Test
    @DisplayName("Should throw NullPointerException when ErrorCode is null")
    void testConstructorWithNullErrorCode() {
        assertThrows(NullPointerException.class, () -> new BusinessException(null));
        assertThrows(NullPointerException.class, () -> new BusinessException(null, "Tùy biến"));
    }

    @Test
    @DisplayName("Should have null cause as BusinessException does not wrap technical failures")
    void testCauseIsNull() {
        BusinessException ex = new BusinessException(ErrorCode.ERR_WAL_001);

        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Should verify type inheritance hierarchy")
    void testTypeHierarchy() {
        BusinessException ex = new BusinessException(ErrorCode.ERR_CAT_001);

        assertInstanceOf(PfmsException.class, ex);
        assertInstanceOf(RuntimeException.class, ex);
    }
}
