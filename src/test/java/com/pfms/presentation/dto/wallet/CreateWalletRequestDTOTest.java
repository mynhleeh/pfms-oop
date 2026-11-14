package com.pfms.presentation.dto.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
        @DisplayName("Tự động cắt tỉa khoảng trắng ghi chú nếu có nội dung và chuẩn hóa ghi chú toàn khoảng trắng thành rỗng")
        void shouldTrimNoteWhenProvided() {
            CreateWalletRequestDTO dto = new CreateWalletRequestDTO("Ví Tiết Kiệm", "   Quỹ khẩn cấp 6 tháng   ");
            CreateWalletRequestDTO dtoWhitespaceNote = new CreateWalletRequestDTO("Ví Tiết Kiệm", "   \t  ");

            assertEquals("Ví Tiết Kiệm", dto.getName());
            assertEquals("Quỹ khẩn cấp 6 tháng", dto.getNote());
            assertEquals("", dtoWhitespaceNote.getNote());
        }
    }

    @Nested
    @DisplayName("Chuyển tiếp dữ liệu cho Domain kiểm soát nghiệp vụ (Thin DTO)")
    class DomainValidationPassthroughTests {

        @Test
        @DisplayName("Giữ nguyên giá trị null của tên ví để Domain ném ERR_NAM_001 thay vì ném tại DTO")
        void shouldPreserveNullNameForDomainValidation() {
            CreateWalletRequestDTO dto2Args = assertDoesNotThrow(
                    () -> new CreateWalletRequestDTO(null, "Ghi chú hợp lệ"));
            CreateWalletRequestDTO dto1Arg = assertDoesNotThrow(
                    () -> new CreateWalletRequestDTO(null));

            assertNull(dto2Args.getName());
            assertEquals("Ghi chú hợp lệ", dto2Args.getNote());
            assertNull(dto1Arg.getName());
            assertEquals("", dto1Arg.getNote());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Cắt tỉa chuỗi rỗng/khoảng trắng thành chuỗi rỗng và không chặn nghiệp vụ tại DTO")
        void shouldTrimBlankNameToEmptyWithoutThrowingAtDto(String blankName) {
            CreateWalletRequestDTO dto2Args = assertDoesNotThrow(
                    () -> new CreateWalletRequestDTO(blankName, "Ghi chú hợp lệ"));
            CreateWalletRequestDTO dto1Arg = assertDoesNotThrow(
                    () -> new CreateWalletRequestDTO(blankName));

            assertEquals("", dto2Args.getName());
            assertEquals("Ghi chú hợp lệ", dto2Args.getNote());
            assertEquals("", dto1Arg.getName());
            assertEquals("", dto1Arg.getNote());
        }

        @Test
        @DisplayName("Truyền thẳng tên ví vượt quá 50 ký tự để Domain xử lý giới hạn độ dài")
        void shouldPreserveOversizedNameForDomainValidation() {
            String oversizedName = "V".repeat(51);
            CreateWalletRequestDTO dto = assertDoesNotThrow(
                    () -> new CreateWalletRequestDTO(oversizedName, "Ghi chú hợp lệ"));

            assertEquals(oversizedName, dto.getName());
            assertEquals(51, dto.getName().length());
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

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dtoDifferentName);
            assertNotEquals(dto1, dtoDifferentNote);
            assertNotEquals(dto1, null);
            assertNotEquals(dto1, "NotADTO");
        }

        @Test
        @DisplayName("Hợp đồng equals, hashCode và toString vẫn an toàn khi tên ví là null")
        void shouldHandleNullNameSafelyInEqualsHashCodeAndToString() {
            CreateWalletRequestDTO dtoNull1 = new CreateWalletRequestDTO(null, "Chi tiêu");
            CreateWalletRequestDTO dtoNull2 = new CreateWalletRequestDTO(null, "Chi tiêu");
            CreateWalletRequestDTO dtoNamed = new CreateWalletRequestDTO("Ví MB Bank", "Chi tiêu");

            assertEquals(dtoNull1, dtoNull2);
            assertEquals(dtoNull1.hashCode(), dtoNull2.hashCode());
            assertNotEquals(dtoNull1, dtoNamed);

            String text = dtoNull1.toString();
            assertTrue(text.contains("CreateWalletRequestDTO"));
            assertTrue(text.contains("null"));
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
