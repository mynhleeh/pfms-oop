package com.pfms.presentation.dto.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử RenameNeedGroupRequestDTO")
class RenameNeedGroupRequestDTOTest {

    private final UUID sampleGroupId = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private final UUID otherGroupId = UUID.fromString("00000000-0000-0000-0000-000000000021");

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 4 tham số needGroupId, name, icon, color")
        void shouldCreateWithAllFields() {
            RenameNeedGroupRequestDTO dto = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "Y tế", "medical-bag", "#009688");

            assertEquals(sampleGroupId, dto.getNeedGroupId());
            assertEquals("Y tế", dto.getName());
            assertEquals("medical-bag", dto.getIcon());
            assertEquals("#009688", dto.getColor());
            assertTrue(dto.hasAppearanceUpdate());
            assertThrows(NoSuchMethodException.class, () -> RenameNeedGroupRequestDTO.class.getMethod("getId"));
        }

        @Test
        @DisplayName("Khởi tạo 2 tham số giữ icon và color là null để không ghi đè hiển thị hiện có")
        void shouldCreateWithOnlyIdAndNameWithoutAppearanceUpdate() {
            RenameNeedGroupRequestDTO dto = new RenameNeedGroupRequestDTO(sampleGroupId, "Sức khỏe");

            assertEquals(sampleGroupId, dto.getNeedGroupId());
            assertEquals("Sức khỏe", dto.getName());
            assertNull(dto.getIcon());
            assertNull(dto.getColor());
            assertFalse(dto.hasAppearanceUpdate());
        }

        @Test
        @DisplayName("Tự động cắt khoảng trắng hai đầu cho name, icon và color")
        void shouldTrimNameIconAndColorWhitespace() {
            RenameNeedGroupRequestDTO dto = new RenameNeedGroupRequestDTO(
                    sampleGroupId,
                    "   Y tế và Thuốc   ",
                    "   heart-pulse   ",
                    "   #E91E63   "
            );

            assertEquals("Y tế và Thuốc", dto.getName());
            assertEquals("heart-pulse", dto.getIcon());
            assertEquals("#E91E63", dto.getColor());
            assertTrue(dto.hasAppearanceUpdate());
        }

        @Test
        @DisplayName("Phân biệt rõ giữ nguyên (null), xóa rỗng (\"\") và cập nhật một phần icon/color")
        void shouldRecognizePartialAndClearingAppearanceUpdates() {
            RenameNeedGroupRequestDTO onlyIcon = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "Y tế", "star", null);
            assertEquals("star", onlyIcon.getIcon());
            assertNull(onlyIcon.getColor());
            assertTrue(onlyIcon.hasAppearanceUpdate());

            RenameNeedGroupRequestDTO onlyColor = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "Y tế", null, "#FF0000");
            assertNull(onlyColor.getIcon());
            assertEquals("#FF0000", onlyColor.getColor());
            assertTrue(onlyColor.hasAppearanceUpdate());

            RenameNeedGroupRequestDTO clearBoth = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "Y tế", "   ", "   ");
            assertEquals("", clearBoth.getIcon());
            assertEquals("", clearBoth.getColor());
            assertTrue(clearBoth.hasAppearanceUpdate());

            RenameNeedGroupRequestDTO keepBoth = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "Y tế", null, null);
            assertNull(keepBoth.getIcon());
            assertNull(keepBoth.getColor());
            assertFalse(keepBoth.hasAppearanceUpdate());
        }
    }

    @Nested
    @DisplayName("Tiền điều kiện bắt buộc và Dữ liệu đi qua tới Service/Domain")
    class PreconditionAndPassthroughTests {

        @Test
        @DisplayName("Ném NullPointerException khi needGroupId là null ở cả 2 constructor")
        void shouldThrowExceptionWhenNeedGroupIdIsNull() {
            NullPointerException ex2Args = assertThrows(
                    NullPointerException.class,
                    () -> new RenameNeedGroupRequestDTO(null, "Tên mới")
            );
            NullPointerException ex4Args = assertThrows(
                    NullPointerException.class,
                    () -> new RenameNeedGroupRequestDTO(null, "Tên mới", "icon", "#111111")
            );

            assertTrue(ex2Args.getMessage().contains("Mã nhóm nhu cầu"));
            assertTrue(ex4Args.getMessage().contains("Mã nhóm nhu cầu"));
        }

        @Test
        @DisplayName("Giữ nguyên name là null để Service/Domain xử lý ERR_NAM_001")
        void shouldPreserveNullNameForServiceValidation() {
            RenameNeedGroupRequestDTO dto = new RenameNeedGroupRequestDTO(sampleGroupId, null);

            assertNull(dto.getName());
            assertFalse(dto.hasAppearanceUpdate());
        }

        @Test
        @DisplayName("Cho phép name, icon, color vượt ngưỡng độ dài đi qua DTO để Domain xử lý")
        void shouldPassThroughOversizedStringsForDomainValidation() {
            String longName = "N".repeat(51);
            String longIcon = "I".repeat(51);
            String longColor = "#1234567890";

            RenameNeedGroupRequestDTO dto = assertDoesNotThrow(
                    () -> new RenameNeedGroupRequestDTO(sampleGroupId, longName, longIcon, longColor));

            assertEquals(longName, dto.getName());
            assertEquals(longIcon, dto.getIcon());
            assertEquals(longColor, dto.getColor());
            assertTrue(dto.hasAppearanceUpdate());
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (equals, hashCode, toString)")
    class IdentityTests {

        @Test
        @DisplayName("Hai DTO cùng giá trị sau chuẩn hóa phải bằng nhau và cùng hashCode")
        void shouldBeEqualAndShareHashCodeWhenNormalizedAttributesMatch() {
            RenameNeedGroupRequestDTO dto1 = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "Y tế", "heart", "#E91E63");
            RenameNeedGroupRequestDTO dto2 = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "  Y tế  ", "  heart ", "  #E91E63 ");

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());

            RenameNeedGroupRequestDTO twoArg = new RenameNeedGroupRequestDTO(sampleGroupId, "Y tế");
            RenameNeedGroupRequestDTO fourArgNulls = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "  Y tế  ", null, null);
            assertEquals(twoArg, fourArgNulls);
            assertEquals(twoArg.hashCode(), fourArgNulls.hashCode());
        }

        @Test
        @DisplayName("Không bằng nhau khi khác bất kỳ trường dữ liệu nào")
        void shouldNotEqualWhenAnyFieldDiffers() {
            RenameNeedGroupRequestDTO base = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "Y tế", "heart", "#E91E63");

            assertNotEquals(base, new RenameNeedGroupRequestDTO(otherGroupId, "Y tế", "heart", "#E91E63"));
            assertNotEquals(base, new RenameNeedGroupRequestDTO(sampleGroupId, "Sức khỏe", "heart", "#E91E63"));
            assertNotEquals(base, new RenameNeedGroupRequestDTO(sampleGroupId, "Y tế", "star", "#E91E63"));
            assertNotEquals(base, new RenameNeedGroupRequestDTO(sampleGroupId, "Y tế", "heart", "#000000"));
            assertNotEquals(base, null);
            assertNotEquals(base, "NotADTO");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và thông tin các thuộc tính")
        void shouldProduceInformativeToString() {
            RenameNeedGroupRequestDTO dto = new RenameNeedGroupRequestDTO(
                    sampleGroupId, "Y tế", "heart", "#E91E63");

            String str = dto.toString();
            assertTrue(str.contains("RenameNeedGroupRequestDTO"));
            assertTrue(str.contains(sampleGroupId.toString()));
            assertTrue(str.contains("Y tế"));
            assertTrue(str.contains("heart"));
            assertTrue(str.contains("#E91E63"));
        }
    }
}

