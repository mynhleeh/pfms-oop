package com.pfms.presentation.dto.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
        @DisplayName("Tự động cắt tỉa khoảng trắng ghi chú nếu có nội dung và chuẩn hóa ghi chú toàn khoảng trắng thành rỗng")
        void shouldTrimNoteWhenProvided() {
            UpdateWalletRequestDTO dto = new UpdateWalletRequestDTO(sampleId, "Ví Tiết Kiệm",
                    "   Quỹ đầu tư dài hạn   ");
            UpdateWalletRequestDTO dtoWhitespaceNote = new UpdateWalletRequestDTO(sampleId, "Ví Tiết Kiệm",
                    "   \t  ");

            assertEquals("Ví Tiết Kiệm", dto.getName());
            assertEquals("Quỹ đầu tư dài hạn", dto.getNote());
            assertEquals("", dtoWhitespaceNote.getNote());
        }
    }

    @Nested
    @DisplayName("Tiền điều kiện khóa kỹ thuật & Chuyển tiếp dữ liệu sang Domain (Thin DTO)")
    class PreconditionAndPassthroughTests {

        @Test
        @DisplayName("Ném NullPointerException khi mã ví walletId bị null")
        void shouldThrowExceptionWhenWalletIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateWalletRequestDTO(null, "Tên hợp lệ", "Ghi chú"));
            assertTrue(ex.getMessage().contains("Mã ví"));
        }

        @Test
        @DisplayName("Giữ nguyên giá trị null của tên ví để Domain ném ERR_NAM_001 thay vì ném tại DTO")
        void shouldPreserveNullNameForDomainValidation() {
            UpdateWalletRequestDTO dto3Args = assertDoesNotThrow(
                    () -> new UpdateWalletRequestDTO(sampleId, null, "Ghi chú hợp lệ"));
            UpdateWalletRequestDTO dto2Args = assertDoesNotThrow(
                    () -> new UpdateWalletRequestDTO(sampleId, null));

            assertEquals(sampleId, dto3Args.getWalletId());
            assertNull(dto3Args.getName());
            assertEquals("Ghi chú hợp lệ", dto3Args.getNote());

            assertEquals(sampleId, dto2Args.getWalletId());
            assertNull(dto2Args.getName());
            assertEquals("", dto2Args.getNote());
        }

        @ParameterizedTest
        @ValueSource(strings = { "", "   ", "\t", "\n" })
        @DisplayName("Cắt tỉa chuỗi rỗng/khoảng trắng thành chuỗi rỗng và không chặn nghiệp vụ tại DTO")
        void shouldTrimBlankNameToEmptyWithoutThrowingAtDto(String blankName) {
            UpdateWalletRequestDTO dto3Args = assertDoesNotThrow(
                    () -> new UpdateWalletRequestDTO(sampleId, blankName, "Ghi chú hợp lệ"));
            UpdateWalletRequestDTO dto2Args = assertDoesNotThrow(
                    () -> new UpdateWalletRequestDTO(sampleId, blankName));

            assertEquals("", dto3Args.getName());
            assertEquals("Ghi chú hợp lệ", dto3Args.getNote());
            assertEquals("", dto2Args.getName());
            assertEquals("", dto2Args.getNote());
        }

        @Test
        @DisplayName("Truyền thẳng tên ví vượt quá 50 ký tự để Domain xử lý giới hạn độ dài")
        void shouldPreserveOversizedNameForDomainValidation() {
            String oversizedName = "V".repeat(51);
            UpdateWalletRequestDTO dto = assertDoesNotThrow(
                    () -> new UpdateWalletRequestDTO(sampleId, oversizedName, "Ghi chú hợp lệ"));

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
            UUID otherId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            UpdateWalletRequestDTO dto1 = new UpdateWalletRequestDTO(sampleId, "Ví MB", "Ghi chú");
            UpdateWalletRequestDTO dto2 = new UpdateWalletRequestDTO(sampleId, "Ví MB", "Ghi chú");
            UpdateWalletRequestDTO dtoDiffId = new UpdateWalletRequestDTO(otherId, "Ví MB", "Ghi chú");
            UpdateWalletRequestDTO dtoDiffName = new UpdateWalletRequestDTO(sampleId, "Ví VP", "Ghi chú");
            UpdateWalletRequestDTO dtoDiffNote = new UpdateWalletRequestDTO(sampleId, "Ví MB", "Khác");

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dtoDiffId);
            assertNotEquals(dto1, dtoDiffName);
            assertNotEquals(dto1, dtoDiffNote);
            assertNotEquals(dto1, null);
            assertNotEquals(dto1, "DifferentType");
        }

        @Test
        @DisplayName("Hợp đồng equals, hashCode và toString vẫn an toàn khi tên ví là null")
        void shouldHandleNullNameSafelyInEqualsHashCodeAndToString() {
            UpdateWalletRequestDTO dtoNull1 = new UpdateWalletRequestDTO(sampleId, null, "Ghi chú");
            UpdateWalletRequestDTO dtoNull2 = new UpdateWalletRequestDTO(sampleId, null, "Ghi chú");
            UpdateWalletRequestDTO dtoNamed = new UpdateWalletRequestDTO(sampleId, "Ví MB", "Ghi chú");

            assertEquals(dtoNull1, dtoNull2);
            assertEquals(dtoNull1.hashCode(), dtoNull2.hashCode());
            assertNotEquals(dtoNull1, dtoNamed);

            String text = dtoNull1.toString();
            assertTrue(text.contains("UpdateWalletRequestDTO"));
            assertTrue(text.contains("null"));
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
