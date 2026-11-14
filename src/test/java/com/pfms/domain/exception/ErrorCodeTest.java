package com.pfms.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for ErrorCode Enum")
class ErrorCodeTest {

    private static final Pattern CODE_PATTERN = Pattern.compile("^ERR-[A-Z]{3}-\\d{3}$");
    private static final int EXPECTED_TOTAL_CODES = 20;

    @Test
    @DisplayName("Should contain exactly 20 standardized system error codes")
    void testTotalErrorCodeCount() {
        assertEquals(EXPECTED_TOTAL_CODES, ErrorCode.values().length,
                "Hệ thống phải có chính xác 20 mã lỗi theo đặc tả OOD Mục 8.3.2");
    }

    @Test
    @DisplayName("Should define ERR_REP_001 with expected code and message")
    void testReportErrorCodeMetadata() {
        assertEquals("ERR-REP-001", ErrorCode.ERR_REP_001.getCode());
        assertEquals("Tham số thời gian hoặc loại báo cáo không hợp lệ.", ErrorCode.ERR_REP_001.getDefaultMessage());
    }

    @ParameterizedTest(name = "ErrorCode {0} should match naming pattern and have valid messages")
    @EnumSource(ErrorCode.class)
    @DisplayName("Should verify regex pattern and non-blank messages for every ErrorCode")
    void testErrorCodeStructureAndMessage(ErrorCode errorCode) {
        assertNotNull(errorCode.getCode());
        assertNotNull(errorCode.getDefaultMessage());

        assertFalse(errorCode.getCode().isBlank(), "Mã code không được để rỗng");
        assertFalse(errorCode.getDefaultMessage().isBlank(), "Thông điệp mặc định không được để rỗng");

        assertTrue(CODE_PATTERN.matcher(errorCode.getCode()).matches(),
                () -> String.format("Mã lỗi [%s] phải tuân thủ định dạng chuẩn ERR-[PHÂN_HỆ]-[SỐ_THỨ_TỰ]", errorCode.getCode()));
    }

    @Test
    @DisplayName("Should ensure all ErrorCode string representations and default messages are strictly unique")
    void testErrorCodeUniqueness() {
        Set<String> uniqueCodes = new HashSet<>();
        Set<String> uniqueMessages = new HashSet<>();
        for (ErrorCode ec : ErrorCode.values()) {
            boolean codeAdded = uniqueCodes.add(ec.getCode());
            assertTrue(codeAdded, () -> "Phát hiện mã lỗi bị trùng lặp: " + ec.getCode());

            boolean messageAdded = uniqueMessages.add(ec.getDefaultMessage());
            assertTrue(messageAdded, () -> "Phát hiện thông điệp mặc định bị trùng lặp: " + ec.getDefaultMessage());
        }
        assertEquals(EXPECTED_TOTAL_CODES, uniqueCodes.size());
        assertEquals(EXPECTED_TOTAL_CODES, uniqueMessages.size());
    }
}

