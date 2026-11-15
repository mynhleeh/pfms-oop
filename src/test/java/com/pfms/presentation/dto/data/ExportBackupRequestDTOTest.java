package com.pfms.presentation.dto.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử ExportBackupRequestDTO")
class ExportBackupRequestDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đường dẫn và phạm vi FULL")
        void shouldCreateWithFullScope() {
            ExportBackupRequestDTO dto = new ExportBackupRequestDTO(
                    "C:/backups/full", "FULL");

            assertEquals("C:/backups/full", dto.getDestinationPath());
            assertEquals("FULL", dto.getScope());
            assertTrue(dto.isFullScope());
            assertFalse(dto.isTransactionsScope());
        }

        @Test
        @DisplayName("Khởi tạo thành công với đường dẫn và phạm vi TRANSACTIONS")
        void shouldCreateWithTransactionsScope() {
            ExportBackupRequestDTO dto = new ExportBackupRequestDTO(
                    "C:/backups/txns", "TRANSACTIONS");

            assertEquals("C:/backups/txns", dto.getDestinationPath());
            assertEquals("TRANSACTIONS", dto.getScope());
            assertFalse(dto.isFullScope());
            assertTrue(dto.isTransactionsScope());
        }

        @Test
        @DisplayName("Constructor tiện ích tự động gán phạm vi mặc định là FULL")
        void shouldDefaultToFullScopeInConvenienceConstructor() {
            ExportBackupRequestDTO dto = new ExportBackupRequestDTO("C:/backups");

            assertEquals("C:/backups", dto.getDestinationPath());
            assertEquals(ExportBackupRequestDTO.DEFAULT_SCOPE, dto.getScope());
            assertTrue(dto.isFullScope());
            assertFalse(dto.isTransactionsScope());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Tự động gán phạm vi FULL khi scope nhận giá trị null hoặc khoảng trắng")
        void shouldNormalizeNullOrBlankScopeToFull(String blankScope) {
            ExportBackupRequestDTO dto = new ExportBackupRequestDTO("C:/backups", blankScope);

            assertEquals("FULL", dto.getScope());
            assertTrue(dto.isFullScope());
        }

        @Test
        @DisplayName("Tự động cắt khoảng trắng và viết hoa phạm vi đầu vào")
        void shouldTrimAndUppercaseScope() {
            ExportBackupRequestDTO dto = new ExportBackupRequestDTO(
                    "  C:/backups/txns  ", "  transactions  ");

            assertEquals("C:/backups/txns", dto.getDestinationPath());
            assertEquals("TRANSACTIONS", dto.getScope());
            assertTrue(dto.isTransactionsScope());
        }

        @Test
        @DisplayName("Giữ nguyên null khi destinationPath là null")
        void shouldPreserveNullDestinationPath() {
            ExportBackupRequestDTO dto = new ExportBackupRequestDTO(null, "FULL");

            assertNull(dto.getDestinationPath());
            assertEquals("FULL", dto.getScope());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Hợp đồng Nhận diện (Identity Contract)")
    class IdentityTests {

        @Test
        @DisplayName("equals và hashCode hoạt động nhất quán")
        void shouldRespectEqualsAndHashCode() {
            ExportBackupRequestDTO dto1 = new ExportBackupRequestDTO("C:/b1", "FULL");
            ExportBackupRequestDTO dto2 = new ExportBackupRequestDTO("C:/b1", "FULL");
            ExportBackupRequestDTO dto3 = new ExportBackupRequestDTO("C:/b1", "TRANSACTIONS");

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dto3);
            assertNotEquals(null, dto1);
            assertNotEquals("String", dto1);
        }

        @Test
        @DisplayName("toString thể hiện đường dẫn đích và phạm vi xuất")
        void shouldContainFieldsInToString() {
            ExportBackupRequestDTO dto = new ExportBackupRequestDTO("C:/b1", "FULL");
            String str = dto.toString();

            assertTrue(str.contains("destinationPath='C:/b1'"));
            assertTrue(str.contains("scope='FULL'"));
        }

        @Test
        @DisplayName("Chuẩn hóa scope không phụ thuộc Locale mặc định (tr-TR)")
        void shouldNormalizeScopeWithLocaleRoot() {
            Locale previous = Locale.getDefault();
            try {
                Locale.setDefault(Locale.forLanguageTag("tr-TR"));
                ExportBackupRequestDTO dto = new ExportBackupRequestDTO("C:/backups/txns", "transactions");

                assertEquals("TRANSACTIONS", dto.getScope());
            } finally {
                Locale.setDefault(previous);
            }
        }
    }
}
