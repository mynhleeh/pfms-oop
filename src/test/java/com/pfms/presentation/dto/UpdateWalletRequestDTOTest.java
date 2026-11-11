package com.pfms.presentation.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử UpdateWalletRequestDTO")
class UpdateWalletRequestDTOTest {

    private final UUID sampleId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với mã ví, tên ví và ghi chú hợp lệ")
        void shouldCreateWithValidIdNameAndNote() {
            UpdateWalletRequestDTO dto = new UpdateWalletRequestDTO(sampleId, "Ví Tiền Mặt Mới", "Cập nhật ghi chú");

            assertEquals(sampleId, dto.getWalletId());
            assertEquals("Ví Tiền Mặt Mới", dto.getName());
            assertEquals("Cập nhật ghi chú", dto.getNote());
        }

        @Test
        @DisplayName("Khởi tạo thành công không truyền ghi chú, ghi chú mặc định là chuỗi rỗng")
        void shouldCreateWithOnlyIdAndName() {
            UpdateWalletRequestDTO dto = new UpdateWalletRequestDTO(sampleId, "Ví MB Bank");

            assertEquals(sampleId, dto.getWalletId());
            assertEquals("Ví MB Bank", dto.getName());
            assertEquals("", dto.getNote());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng tên ví và chuẩn hóa ghi chú null thành rỗng")
        void shouldTrimNameAndNormalizeNullNoteToEmpty() {
            UpdateWalletRequestDTO dto = new UpdateWalletRequestDTO(sampleId, "   Ví Chi Tiêu   ", null);

            assertEquals("Ví Chi Tiêu", dto.getName());
            assertEquals("", dto.getNote());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng ghi chú nếu có nội dung")
        void shouldTrimNoteWhenProvided() {
            UpdateWalletRequestDTO dto = new UpdateWalletRequestDTO(sampleId, "Ví Tiết Kiệm", "   Quỹ đầu tư dài hạn   ");

            assertEquals("Ví Tiết Kiệm", dto.getName());
            assertEquals("Quỹ đầu tư dài hạn", dto.getNote());
        }
    }

    @Nested
    @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
    class InvalidInputTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã ví walletId bị null")
        void shouldThrowExceptionWhenWalletIdIsNull() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new UpdateWalletRequestDTO(null, "Tên hợp lệ", "Ghi chú")
            );
            assertTrue(ex.getMessage().contains("Mã ví"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Ném IllegalArgumentException khi tên ví null, rỗng hoặc chỉ toàn khoảng trắng")
        void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new UpdateWalletRequestDTO(sampleId, invalidName, "Ghi chú")
            );
            assertTrue(ex.getMessage().contains("Tên ví"));
        }
    }

    @Nested
    @DisplayName("Hợp đồng Bình đẳng và Bất biến (Equality & Immutability)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai DTO cùng thuộc tính phải bình đẳng và cùng hashCode")
        void shouldRespectEqualsAndHashCodeContract() {
            UUID otherId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            UpdateWalletRequestDTO dto1 = new UpdateWalletRequestDTO(sampleId, "Ví MB", "Ghi chú");
            UpdateWalletRequestDTO dto2 = new UpdateWalletRequestDTO(sampleId, "Ví MB", "Ghi chú");
            UpdateWalletRequestDTO dtoDiffId = new UpdateWalletRequestDTO(otherId, "Ví MB", "Ghi chú");
            UpdateWalletRequestDTO dtoDiffName = new UpdateWalletRequestDTO(sampleId, "Ví VP", "Ghi chú");
            UpdateWalletRequestDTO dtoDiffNote = new UpdateWalletRequestDTO(sampleId, "Ví MB", "Khác");

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dtoDiffId);
            assertNotEquals(dto1, dtoDiffName);
            assertNotEquals(dto1, dtoDiffNote);
            assertNotEquals(null, dto1);
            assertNotEquals(dto1, "DifferentType");
        }

        @Test
        @DisplayName("Phương thức toString cung cấp thông tin định danh và thuộc tính")
        void shouldProvideMeaningfulToString() {
            UpdateWalletRequestDTO dto = new UpdateWalletRequestDTO(sampleId, "Ví MB", "Ghi chú");
            String text = dto.toString();

            assertTrue(text.contains("UpdateWalletRequestDTO"));
            assertTrue(text.contains(sampleId.toString()));
            assertTrue(text.contains("Ví MB"));
            assertTrue(text.contains("Ghi chú"));
        }
    }
}
