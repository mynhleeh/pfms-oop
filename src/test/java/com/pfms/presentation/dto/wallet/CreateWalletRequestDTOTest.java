package com.pfms.presentation.dto.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CreateWalletRequestDTO")
class CreateWalletRequestDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với tên ví và ghi chú hợp lệ")
        void shouldCreateWithValidNameAndNote() {
            CreateWalletRequestDTO dto = new CreateWalletRequestDTO("Ví MB Bank", "Chi tiêu gia đình");

            assertEquals("Ví MB Bank", dto.getName());
            assertEquals("Chi tiêu gia đình", dto.getNote());
        }

        @Test
        @DisplayName("Khởi tạo thành công chỉ với tên ví, ghi chú mặc định là chuỗi rỗng")
        void shouldCreateWithOnlyNameAndDefaultEmptyNote() {
            CreateWalletRequestDTO dto = new CreateWalletRequestDTO("Ví Tiền Mặt");

            assertEquals("Ví Tiền Mặt", dto.getName());
            assertEquals("", dto.getNote());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng tên ví và chuẩn hóa ghi chú null thành rỗng")
        void shouldTrimNameAndNormalizeNullNoteToEmpty() {
            CreateWalletRequestDTO dto = new CreateWalletRequestDTO("   Ví Đầu Tư   ", null);

            assertEquals("Ví Đầu Tư", dto.getName());
            assertEquals("", dto.getNote());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng ghi chú nếu có nội dung")
        void shouldTrimNoteWhenProvided() {
            CreateWalletRequestDTO dto = new CreateWalletRequestDTO("Ví Tiết Kiệm", "   Quỹ khẩn cấp 6 tháng   ");

            assertEquals("Ví Tiết Kiệm", dto.getName());
            assertEquals("Quỹ khẩn cấp 6 tháng", dto.getNote());
        }
    }

    @Nested
    @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
    class InvalidInputTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Ném IllegalArgumentException khi tên ví null, rỗng hoặc chỉ toàn khoảng trắng")
        void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new CreateWalletRequestDTO(invalidName, "Ghi chú hợp lệ")
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
            CreateWalletRequestDTO dto1 = new CreateWalletRequestDTO("Ví MB Bank", "Chi tiêu");
            CreateWalletRequestDTO dto2 = new CreateWalletRequestDTO("Ví MB Bank", "Chi tiêu");
            CreateWalletRequestDTO dtoDifferentName = new CreateWalletRequestDTO("Ví VPBank", "Chi tiêu");
            CreateWalletRequestDTO dtoDifferentNote = new CreateWalletRequestDTO("Ví MB Bank", "Khác");

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dtoDifferentName);
            assertNotEquals(dto1, dtoDifferentNote);
            assertNotEquals(null, dto1);
            assertNotEquals(dto1, "NotADTO");
        }

        @Test
        @DisplayName("Phương thức toString cung cấp thông tin định danh và thuộc tính")
        void shouldProvideMeaningfulToString() {
            CreateWalletRequestDTO dto = new CreateWalletRequestDTO("Ví MB Bank", "Chi tiêu");
            String text = dto.toString();

            assertTrue(text.contains("CreateWalletRequestDTO"));
            assertTrue(text.contains("Ví MB Bank"));
            assertTrue(text.contains("Chi tiêu"));
        }
    }
}
