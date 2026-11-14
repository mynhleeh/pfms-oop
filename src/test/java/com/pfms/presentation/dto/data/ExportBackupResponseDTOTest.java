package com.pfms.presentation.dto.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử ExportBackupResponseDTO")
class ExportBackupResponseDTOTest {

    private final LocalDateTime sampleTime = LocalDateTime.of(2026, 11, 14, 15, 30, 0);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ các trường thông tin bản sao lưu")
        void shouldCreateWithAllFields() {
            ExportBackupResponseDTO dto = new ExportBackupResponseDTO(
                    "C:/backups/pfms_2026.json",
                    1048576L,
                    sampleTime,
                    "FULL",
                    4, 28, 150, 3);

            assertEquals("C:/backups/pfms_2026.json", dto.getFilePath());
            assertEquals(1048576L, dto.getFileSizeBytes());
            assertEquals(sampleTime, dto.getExportedAt());
            assertEquals("FULL", dto.getScope());
            assertEquals(4, dto.getWalletCount());
            assertEquals(28, dto.getCategoryCount());
            assertEquals(150, dto.getTransactionCount());
            assertEquals(3, dto.getAdjustmentCount());
            assertEquals(185, dto.getTotalRecordCount());
            assertTrue(dto.isFullScope());
            assertFalse(dto.isTransactionsScope());
        }

        @Test
        @DisplayName("Khởi tạo thành công với phạm vi TRANSACTIONS")
        void shouldCreateWithTransactionsScope() {
            ExportBackupResponseDTO dto = new ExportBackupResponseDTO(
                    "C:/backups/pfms_txns.json",
                    51200L,
                    sampleTime,
                    "TRANSACTIONS",
                    0, 0, 75, 0);

            assertEquals("TRANSACTIONS", dto.getScope());
            assertFalse(dto.isFullScope());
            assertTrue(dto.isTransactionsScope());
            assertEquals(75, dto.getTotalRecordCount());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa số âm thành 0 và null filePath thành chuỗi rỗng")
        void shouldNormalizeNegativeCountsAndNullFilePath() {
            ExportBackupResponseDTO dto = new ExportBackupResponseDTO(
                    null,
                    -100L,
                    null,
                    null,
                    -1, -2, -3, -4);

            assertEquals("", dto.getFilePath());
            assertEquals(0L, dto.getFileSizeBytes());
            assertNotNull(dto.getExportedAt());
            assertEquals("FULL", dto.getScope());
            assertEquals(0, dto.getWalletCount());
            assertEquals(0, dto.getCategoryCount());
            assertEquals(0, dto.getTransactionCount());
            assertEquals(0, dto.getAdjustmentCount());
            assertEquals(0, dto.getTotalRecordCount());
            assertTrue(dto.isFullScope());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Tự động gán phạm vi FULL khi scope nhận giá trị null hoặc rỗng")
        void shouldDefaultScopeToFull(String blankScope) {
            ExportBackupResponseDTO dto = new ExportBackupResponseDTO(
                    "path.json", 100L, sampleTime, blankScope, 1, 1, 1, 1);

            assertEquals("FULL", dto.getScope());
            assertTrue(dto.isFullScope());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Hợp đồng Nhận diện (Identity Contract)")
    class IdentityTests {

        @Test
        @DisplayName("equals và hashCode hoạt động nhất quán")
        void shouldRespectEqualsAndHashCode() {
            ExportBackupResponseDTO dto1 = new ExportBackupResponseDTO(
                    "path.json", 100L, sampleTime, "FULL", 1, 1, 1, 1);
            ExportBackupResponseDTO dto2 = new ExportBackupResponseDTO(
                    "path.json", 100L, sampleTime, "FULL", 1, 1, 1, 1);
            ExportBackupResponseDTO dto3 = new ExportBackupResponseDTO(
                    "other.json", 100L, sampleTime, "FULL", 1, 1, 1, 1);

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dto3);
            assertNotEquals(null, dto1);
            assertNotEquals("String", dto1);
        }

        @Test
        @DisplayName("toString thể hiện đường dẫn, dung lượng và tổng số bản ghi")
        void shouldContainFieldsInToString() {
            ExportBackupResponseDTO dto = new ExportBackupResponseDTO(
                    "path.json", 100L, sampleTime, "FULL", 2, 3, 5, 1);
            String str = dto.toString();

            assertTrue(str.contains("ExportBackupResponseDTO"));
            assertTrue(str.contains("filePath='path.json'"));
            assertTrue(str.contains("fileSizeBytes=100"));
            assertTrue(str.contains("totalRecordCount=11"));
            assertTrue(str.contains("scope='FULL'"));
        }
    }
}

