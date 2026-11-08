package com.pfms.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

    @ParameterizedTest(name = "Should instantiate BusinessException with {0}")
    @EnumSource(value = ErrorCode.class, names = {
            "ERR_FIN_001", "ERR_FIN_002",
            "ERR_NAM_001",
            "ERR_TRF_001", "ERR_TRF_002",
            "ERR_FEE_001", "ERR_FEE_002", "ERR_FEE_003",
            "ERR_CAT_001", "ERR_CAT_002", "ERR_CAT_003", "ERR_CAT_004", "ERR_CAT_005"
    })
    @DisplayName("Should correctly instantiate BusinessException for all 13 standard business ErrorCodes")
    void testAllStandardBusinessErrorCodes(ErrorCode errorCode) {
        BusinessException ex = new BusinessException(errorCode);

        assertEquals(errorCode, ex.getErrorCode());
        assertEquals(errorCode.getDefaultMessage(), ex.getMessage());
        assertNotNull(ex.getTimestamp());
        assertNull(ex.getCause());
        assertFalse(errorCode.getCode().isBlank(), "Mã lỗi không được rỗng");
        assertFalse(errorCode.getDefaultMessage().isBlank(), "Thông điệp mặc định không được rỗng");
    }
}
