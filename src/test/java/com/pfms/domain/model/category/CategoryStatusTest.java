package com.pfms.domain.model.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for CategoryStatus Enum")
class CategoryStatusTest {

    @Test
    @DisplayName("Should contain exactly 2 constants: ACTIVE and DELETED")
    void testEnumConstantsCount() {
        CategoryStatus[] values = CategoryStatus.values();
        assertEquals(2, values.length, "CategoryStatus phải có chính xác 2 hằng số theo thiết kế OOD");
        assertSame(CategoryStatus.ACTIVE, CategoryStatus.valueOf("ACTIVE"));
        assertSame(CategoryStatus.DELETED, CategoryStatus.valueOf("DELETED"));
    }

    @Test
    @DisplayName("Should return valid codes and Vietnamese display labels")
    void testStateAndGetters() {
        assertEquals("ACTIVE", CategoryStatus.ACTIVE.getCode());
        assertEquals("Đang hoạt động", CategoryStatus.ACTIVE.getDisplayLabel());

        assertEquals("DELETED", CategoryStatus.DELETED.getCode());
        assertEquals("Đã xóa", CategoryStatus.DELETED.getDisplayLabel());
    }

    @ParameterizedTest(name = "CategoryStatus.{0} should have non-blank code and displayLabel matching contract")
    @EnumSource(CategoryStatus.class)
    @DisplayName("Should verify state integrity for all CategoryStatus constants")
    void testStateIntegrity(CategoryStatus status) {
        assertNotNull(status.getCode());
        assertFalse(status.getCode().isBlank(), "Code không được để rỗng");
        assertEquals(status.name(), status.getCode(), "Code phải trùng khớp với name()");

        assertNotNull(status.getDisplayLabel());
        assertFalse(status.getDisplayLabel().isBlank(), "DisplayLabel không được để rỗng");
    }

    @Test
    @DisplayName("Should correctly identify active and deleted status via query methods")
    void testQueriesIsActiveAndIsDeleted() {
        assertTrue(CategoryStatus.ACTIVE.isActive());
        assertFalse(CategoryStatus.ACTIVE.isDeleted());

        assertTrue(CategoryStatus.DELETED.isDeleted());
        assertFalse(CategoryStatus.DELETED.isActive());
    }

    @ParameterizedTest(name = "fromString(\"{0}\") should resolve to ACTIVE")
    @ValueSource(strings = { "ACTIVE", "active", "Active", "  ACTIVE  ", "  active \t" })
    @DisplayName("Should parse valid strings to ACTIVE case-insensitively with trimming")
    void testFromStringValidActive(String input) {
        assertSame(CategoryStatus.ACTIVE, CategoryStatus.fromString(input));
    }

    @ParameterizedTest(name = "fromString(\"{0}\") should resolve to DELETED")
    @ValueSource(strings = { "DELETED", "deleted", "Deleted", "  DELETED  ", "  deleted \n" })
    @DisplayName("Should parse valid strings to DELETED case-insensitively with trimming")
    void testFromStringValidDeleted(String input) {
        assertSame(CategoryStatus.DELETED, CategoryStatus.fromString(input));
    }

    @ParameterizedTest(name = "fromString(\"{0}\") should throw IllegalArgumentException")
    @ValueSource(strings = { "", "   ", "\t\n", "ARCHIVED", "INACTIVE", "CLOSED", "UNKNOWN", "123" })
    @DisplayName("Should throw IllegalArgumentException for blank or invalid strings")
    void testFromStringInvalid(String input) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CategoryStatus.fromString(input));
        assertNotNull(ex.getMessage());
        if (!input.isBlank()) {
            assertTrue(ex.getMessage().contains(input), "Thông báo lỗi phải chứa giá trị không hợp lệ đầu vào");
        }
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when fromString argument is null")
    void testFromStringNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CategoryStatus.fromString(null));
        assertTrue(ex.getMessage().contains("không được để trống"));
    }
}

