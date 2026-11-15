package com.pfms.domain.model.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

@DisplayName("EntityStatus - Kiểm thử trạng thái thực thể dùng chung")
class EntityStatusTest {

    @Nested
    @DisplayName("1. Kiểm tra hằng số và nhãn hiển thị")
    class ConstantsAndLabelsTests {

        @Test
        @DisplayName("Cung cấp đủ 3 trạng thái vòng đời: ACTIVE, ARCHIVED, DELETED")
        void shouldContainAllExpectedStatuses() {
            assertEquals(3, EntityStatus.values().length);
            assertNotNull(EntityStatus.valueOf("ACTIVE"));
            assertNotNull(EntityStatus.valueOf("ARCHIVED"));
            assertNotNull(EntityStatus.valueOf("DELETED"));
        }

        @Test
        @DisplayName("Mã code và nhãn hiển thị tiếng Việt khớp với đặc tả chuẩn")
        void shouldExposeCorrectCodesAndDisplayLabels() {
            assertEquals("ACTIVE", EntityStatus.ACTIVE.getCode());
            assertEquals("Đang hoạt động", EntityStatus.ACTIVE.getDisplayLabel());

            assertEquals("ARCHIVED", EntityStatus.ARCHIVED.getCode());
            assertEquals("Đã lưu trữ", EntityStatus.ARCHIVED.getDisplayLabel());

            assertEquals("DELETED", EntityStatus.DELETED.getCode());
            assertEquals("Đã xóa", EntityStatus.DELETED.getDisplayLabel());
        }
    }

    @Nested
    @DisplayName("2. Kiểm tra các phương thức truy vấn trạng thái (Queries)")
    class StateQueryTests {

        @Test
        @DisplayName("ACTIVE: isActive() = true, các hàm còn lại = false")
        void activeStateQueries() {
            assertTrue(EntityStatus.ACTIVE.isActive());
            assertFalse(EntityStatus.ACTIVE.isArchived());
            assertFalse(EntityStatus.ACTIVE.isDeleted());
        }

        @Test
        @DisplayName("ARCHIVED: isArchived() = true, các hàm còn lại = false")
        void archivedStateQueries() {
            assertFalse(EntityStatus.ARCHIVED.isActive());
            assertTrue(EntityStatus.ARCHIVED.isArchived());
            assertFalse(EntityStatus.ARCHIVED.isDeleted());
        }

        @Test
        @DisplayName("DELETED: isDeleted() = true, các hàm còn lại = false")
        void deletedStateQueries() {
            assertFalse(EntityStatus.DELETED.isActive());
            assertFalse(EntityStatus.DELETED.isArchived());
            assertTrue(EntityStatus.DELETED.isDeleted());
        }
    }

    @Nested
    @DisplayName("3. Kiểm tra chuyển đổi từ chuỗi (fromString)")
    class FromStringTests {

        @ParameterizedTest
        @ValueSource(strings = {"ACTIVE", "active", " Active ", "ACTIVE "})
        @DisplayName("Chuyển đổi thành công chuỗi hợp lệ sang ACTIVE bất kể hoa thường và khoảng trắng")
        void shouldParseActiveVariants(String raw) {
            assertEquals(EntityStatus.ACTIVE, EntityStatus.fromString(raw));
        }

        @ParameterizedTest
        @ValueSource(strings = {"ARCHIVED", "archived", " Archived ", "ARCHIVED "})
        @DisplayName("Chuyển đổi thành công chuỗi hợp lệ sang ARCHIVED")
        void shouldParseArchivedVariants(String raw) {
            assertEquals(EntityStatus.ARCHIVED, EntityStatus.fromString(raw));
        }

        @ParameterizedTest
        @ValueSource(strings = {"DELETED", "deleted", " Deleted ", "DELETED "})
        @DisplayName("Chuyển đổi thành công chuỗi hợp lệ sang DELETED")
        void shouldParseDeletedVariants(String raw) {
            assertEquals(EntityStatus.DELETED, EntityStatus.fromString(raw));
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Ném ngoại lệ IllegalArgumentException khi chuỗi trạng thái null hoặc rỗng")
        void shouldThrowWhenInputIsEmptyOrNull(String raw) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> EntityStatus.fromString(raw));
            assertEquals("Trạng thái không được để trống.", ex.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"UNKNOWN", "PENDING", "INACTIVE", "123"})
        @DisplayName("Ném ngoại lệ IllegalArgumentException khi chuỗi trạng thái không nằm trong danh mục")
        void shouldThrowWhenInputIsInvalid(String raw) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> EntityStatus.fromString(raw));
            assertEquals("Trạng thái không hợp lệ: " + raw, ex.getMessage());
        }

        @Test
        @DisplayName("Không phụ thuộc JVM default Locale (ví dụ tr-TR) khi parse chuỗi thường")
        void shouldParseLowercaseIndependentlyOfDefaultLocale() {
            Locale previousLocale = Locale.getDefault();
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            try {
                assertEquals(EntityStatus.ACTIVE, EntityStatus.fromString("active"));
                assertEquals(EntityStatus.ARCHIVED, EntityStatus.fromString("archived"));
                assertEquals(EntityStatus.DELETED, EntityStatus.fromString("deleted"));
                assertThrows(IllegalArgumentException.class,
                        () -> EntityStatus.fromString("ACT\u0130VE"));
            } finally {
                Locale.setDefault(previousLocale);
            }
        }
    }
}

