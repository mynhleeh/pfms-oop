package com.pfms.domain.model.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for WalletStatus Enum")
class WalletStatusTest {

    @Test
    @DisplayName("Should contain exactly 2 constants: ACTIVE and ARCHIVED")
    void testEnumConstantsCount() {
        WalletStatus[] values = WalletStatus.values();
        assertEquals(2, values.length, "WalletStatus phải có chính xác 2 hằng số theo thiết kế OOD");
        assertSame(WalletStatus.ACTIVE, WalletStatus.valueOf("ACTIVE"));
        assertSame(WalletStatus.ARCHIVED, WalletStatus.valueOf("ARCHIVED"));
    }

    @Test
    @DisplayName("Should return valid codes and Vietnamese display labels")
    void testStateAndGetters() {
        assertEquals("ACTIVE", WalletStatus.ACTIVE.getCode());
        assertEquals("Đang hoạt động", WalletStatus.ACTIVE.getDisplayLabel());

        assertEquals("ARCHIVED", WalletStatus.ARCHIVED.getCode());
        assertEquals("Đã lưu trữ", WalletStatus.ARCHIVED.getDisplayLabel());
    }

    @ParameterizedTest(name = "WalletStatus.{0} should have non-blank code and displayLabel matching contract")
    @EnumSource(WalletStatus.class)
    @DisplayName("Should verify state integrity for all WalletStatus constants")
    void testStateIntegrity(WalletStatus status) {
        assertNotNull(status.getCode());
        assertFalse(status.getCode().isBlank(), "Code không được để rỗng");
        assertEquals(status.name(), status.getCode(), "Code phải trùng khớp với name()");

        assertNotNull(status.getDisplayLabel());
        assertFalse(status.getDisplayLabel().isBlank(), "DisplayLabel không được để rỗng");
    }

    @Test
    @DisplayName("Should correctly identify active and archived status via query methods")
    void testQueriesIsActiveAndIsArchived() {
        assertTrue(WalletStatus.ACTIVE.isActive());
        assertFalse(WalletStatus.ACTIVE.isArchived());

        assertTrue(WalletStatus.ARCHIVED.isArchived());
        assertFalse(WalletStatus.ARCHIVED.isActive());
    }

    @ParameterizedTest(name = "fromString(\"{0}\") should resolve to ACTIVE")
    @ValueSource(strings = { "ACTIVE", "active", "Active", "  ACTIVE  ", "  active \t" })
    @DisplayName("Should parse valid strings to ACTIVE case-insensitively with trimming")
    void testFromStringValidActive(String input) {
        assertSame(WalletStatus.ACTIVE, WalletStatus.fromString(input));
    }

    @ParameterizedTest(name = "fromString(\"{0}\") should resolve to ARCHIVED")
    @ValueSource(strings = { "ARCHIVED", "archived", "Archived", "  ARCHIVED  ", "  archived \n" })
    @DisplayName("Should parse valid strings to ARCHIVED case-insensitively with trimming")
    void testFromStringValidArchived(String input) {
        assertSame(WalletStatus.ARCHIVED, WalletStatus.fromString(input));
    }

    @ParameterizedTest(name = "fromString(\"{0}\") should throw IllegalArgumentException")
    @ValueSource(strings = { "", "   ", "\t\n", "DELETED", "INACTIVE", "CLOSED", "123" })
    @DisplayName("Should throw IllegalArgumentException for blank or invalid strings")
    void testFromStringInvalid(String input) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> WalletStatus.fromString(input));
        assertNotNull(ex.getMessage());
        if (!input.isBlank()) {
            assertTrue(ex.getMessage().contains(input), "Thông báo lỗi phải chứa giá trị không hợp lệ đầu vào");
        }
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when fromString argument is null")
    void testFromStringNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> WalletStatus.fromString(null));
        assertTrue(ex.getMessage().contains("không được để trống"));
    }
}

