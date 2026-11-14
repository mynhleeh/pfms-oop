package com.pfms.presentation.dto.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử RestoreResultDTO")
class RestoreResultDTOTest {

    private final LocalDateTime sampleTime = LocalDateTime.of(2026, 11, 14, 16, 0, 0);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ các trường thông tin kết quả phục hồi")
        void shouldCreateWithAllFields() {
            RestoreResultDTO dto = new RestoreResultDTO(
                    true,
                    "Phục hồi dữ liệu hệ thống thành công",
                    sampleTime,
                    3, 20, 100, 2);

            assertTrue(dto.isSuccess());
            assertEquals("Phục hồi dữ liệu hệ thống thành công", dto.getMessage());
            assertEquals(sampleTime, dto.getRestoredAt());
            assertEquals(3, dto.getRestoredWallets());
            assertEquals(20, dto.getRestoredCategories());
            assertEquals(100, dto.getRestoredTransactions());
            assertEquals(2, dto.getRestoredAdjustments());
            assertEquals(125, dto.getTotalRestoredRecords());
        }

        @Test
        @DisplayName("Phương thức xuất xưởng success tạo kết quả phục hồi thành công")
        void shouldCreateSuccessResultViaFactory() {
            RestoreResultDTO dto = RestoreResultDTO.success(
                    "Hoàn tất phục hồi", 4, 15, 80, 1);

            assertTrue(dto.isSuccess());
            assertEquals("Hoàn tất phục hồi", dto.getMessage());
            assertNotNull(dto.getRestoredAt());
            assertEquals(4, dto.getRestoredWallets());
            assertEquals(15, dto.getRestoredCategories());
            assertEquals(80, dto.getRestoredTransactions());
            assertEquals(1, dto.getRestoredAdjustments());
            assertEquals(100, dto.getTotalRestoredRecords());
        }

        @Test
        @DisplayName("Phương thức xuất xưởng failure tạo kết quả phục hồi thất bại với số lượng 0")
        void shouldCreateFailureResultViaFactory() {
            RestoreResultDTO dto = RestoreResultDTO.failure("Tệp sao lưu bị hỏng cấu trúc JSON");

            assertFalse(dto.isSuccess());
            assertEquals("Tệp sao lưu bị hỏng cấu trúc JSON", dto.getMessage());
            assertNotNull(dto.getRestoredAt());
            assertEquals(0, dto.getRestoredWallets());
            assertEquals(0, dto.getRestoredCategories());
            assertEquals(0, dto.getRestoredTransactions());
            assertEquals(0, dto.getRestoredAdjustments());
            assertEquals(0, dto.getTotalRestoredRecords());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa số âm thành 0 và null message thành chuỗi rỗng")
        void shouldNormalizeNegativeCountsAndNullMessage() {
            RestoreResultDTO dto = new RestoreResultDTO(
                    false, null, null, -1, -5, -10, -2);

            assertFalse(dto.isSuccess());
            assertEquals("", dto.getMessage());
            assertNotNull(dto.getRestoredAt());
            assertEquals(0, dto.getRestoredWallets());
            assertEquals(0, dto.getRestoredCategories());
            assertEquals(0, dto.getRestoredTransactions());
            assertEquals(0, dto.getRestoredAdjustments());
            assertEquals(0, dto.getTotalRestoredRecords());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Hợp đồng Nhận diện (Identity Contract)")
    class IdentityTests {

        @Test
        @DisplayName("equals và hashCode hoạt động nhất quán")
        void shouldRespectEqualsAndHashCode() {
            RestoreResultDTO dto1 = new RestoreResultDTO(true, "Thành công", sampleTime, 1, 2, 3, 4);
            RestoreResultDTO dto2 = new RestoreResultDTO(true, "Thành công", sampleTime, 1, 2, 3, 4);
            RestoreResultDTO dto3 = new RestoreResultDTO(false, "Thất bại", sampleTime, 0, 0, 0, 0);

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dto3);
            assertNotEquals(null, dto1);
            assertNotEquals("String", dto1);
        }

        @Test
        @DisplayName("toString thể hiện trạng thái, thông điệp và tổng số bản ghi phục hồi")
        void shouldContainFieldsInToString() {
            RestoreResultDTO dto = new RestoreResultDTO(true, "Thành công", sampleTime, 1, 2, 3, 4);
            String str = dto.toString();

            assertTrue(str.contains("success=true"));
            assertTrue(str.contains("message='Thành công'"));
            assertTrue(str.contains("totalRestoredRecords=10"));
        }
    }
}
