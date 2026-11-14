package com.pfms.presentation.dto.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử RestoreBackupRequestDTO")
class RestoreBackupRequestDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đường dẫn tệp sao lưu hợp lệ")
        void shouldCreateWithValidPath() {
            RestoreBackupRequestDTO dto = new RestoreBackupRequestDTO("C:/backups/pfms_2026.json");

            assertEquals("C:/backups/pfms_2026.json", dto.getBackupFilePath());
        }

        @Test
        @DisplayName("Tự động cắt khoảng trắng hai đầu của đường dẫn tệp sao lưu")
        void shouldTrimBackupFilePath() {
            RestoreBackupRequestDTO dto = new RestoreBackupRequestDTO("   C:/backups/pfms.json   ");

            assertEquals("C:/backups/pfms.json", dto.getBackupFilePath());
        }

        @Test
        @DisplayName("Giữ nguyên null khi đường dẫn truyền vào là null")
        void shouldPreserveNullPath() {
            RestoreBackupRequestDTO dto = new RestoreBackupRequestDTO(null);

            assertNull(dto.getBackupFilePath());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Hợp đồng Nhận diện (Identity Contract)")
    class IdentityTests {

        @Test
        @DisplayName("equals và hashCode hoạt động nhất quán")
        void shouldRespectEqualsAndHashCode() {
            RestoreBackupRequestDTO dto1 = new RestoreBackupRequestDTO("C:/backup.json");
            RestoreBackupRequestDTO dto2 = new RestoreBackupRequestDTO("C:/backup.json");
            RestoreBackupRequestDTO dto3 = new RestoreBackupRequestDTO("C:/other.json");

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dto3);
            assertNotEquals(null, dto1);
            assertNotEquals("Other", dto1);
        }

        @Test
        @DisplayName("toString thể hiện đường dẫn tệp sao lưu")
        void shouldContainFieldsInToString() {
            RestoreBackupRequestDTO dto = new RestoreBackupRequestDTO("C:/backup.json");
            String str = dto.toString();

            assertTrue(str.contains("backupFilePath='C:/backup.json'"));
        }
    }
}
