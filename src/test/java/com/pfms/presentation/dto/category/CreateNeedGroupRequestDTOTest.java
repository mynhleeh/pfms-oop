package com.pfms.presentation.dto.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CreateNeedGroupRequestDTO")
class CreateNeedGroupRequestDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 4 tham số transactionType, name, icon, color")
        void shouldCreateWithAllFields() {
            CreateNeedGroupRequestDTO dto = new CreateNeedGroupRequestDTO(
                    "EXPENSE", "Sức khỏe", "heart", "#E91E63");

            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals("Sức khỏe", dto.getName());
            assertEquals("heart", dto.getIcon());
            assertEquals("#E91E63", dto.getColor());
        }

        @Test
        @DisplayName("Khởi tạo 2 tham số tự động gán icon và color mặc định là chuỗi rỗng")
        void shouldCreateWith2ParamsDefaultingOptionalFields() {
            CreateNeedGroupRequestDTO dto = new CreateNeedGroupRequestDTO("INCOME", "Thu nhập phụ");

            assertEquals("INCOME", dto.getTransactionType());
            assertEquals("Thu nhập phụ", dto.getName());
            assertEquals("", dto.getIcon());
            assertEquals("", dto.getColor());
        }

        @Test
        @DisplayName("Chuẩn hóa viết hoa transactionType và cắt khoảng trắng hai đầu cho name, icon, color")
        void shouldTrimAllFieldsAndUppercaseTransactionType() {
            CreateNeedGroupRequestDTO dto = new CreateNeedGroupRequestDTO(
                    "  income  ",
                    "   Đầu tư dài hạn   ",
                    "   trending-up   ",
                    "   #4CAF50   "
            );

            assertEquals("INCOME", dto.getTransactionType());
            assertEquals("Đầu tư dài hạn", dto.getName());
            assertEquals("trending-up", dto.getIcon());
            assertEquals("#4CAF50", dto.getColor());
        }

        @Test
        @DisplayName("Chuyển icon và color bị null thành chuỗi rỗng")
        void shouldNormalizeNullAppearanceToEmptyStrings() {
            CreateNeedGroupRequestDTO dto = new CreateNeedGroupRequestDTO(
                    "EXPENSE", "Giải trí", null, null);

            assertEquals("", dto.getIcon());
            assertEquals("", dto.getColor());
        }
    }

    @Nested
    @DisplayName("Chuẩn hóa và Cho phép Dữ liệu Nghiệp vụ Đi qua Tới Service/Domain")
    class ValidationPassthroughTests {

        @Test
        @DisplayName("Giữ nguyên name là null để Service/Domain xử lý ERR_NAM_001")
        void shouldPreserveNullNameForServiceValidation() {
            CreateNeedGroupRequestDTO dto = new CreateNeedGroupRequestDTO("EXPENSE", null);

            assertNull(dto.getName());
        }

        @ParameterizedTest(name = "Loại thu chi trống/null \"{0}\" được chuẩn hóa thành null")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Chuẩn hóa transactionType null hoặc khoảng trắng về null để Service xử lý ERR_CAT_001")
        void shouldNormalizeBlankOrNullTransactionTypeToNull(String rawType) {
            CreateNeedGroupRequestDTO dto = new CreateNeedGroupRequestDTO(rawType, "Thiết yếu");

            assertNull(dto.getTransactionType());
        }

        @ParameterizedTest(name = "Loại thu chi không chuẩn \"{0}\" được giữ ở dạng viết hoa để Service kiểm tra")
        @ValueSource(strings = {"TRANSFER", "  other  "})
        @DisplayName("Cho phép chuỗi transactionType lạ đi qua DTO để Service kiểm tra ERR_CAT_001")
        void shouldPassThroughArbitraryTransactionTypeUppercase(String rawType) {
            CreateNeedGroupRequestDTO dto = new CreateNeedGroupRequestDTO(rawType, "Nhóm thử nghiệm");

            assertEquals(rawType.trim().toUpperCase(), dto.getTransactionType());
        }

        @Test
        @DisplayName("Cho phép name, icon, color vượt ngưỡng độ dài đi qua DTO để Domain xử lý")
        void shouldPassThroughOversizedStringsForDomainValidation() {
            String longName = "N".repeat(51);
            String longIcon = "I".repeat(51);
            String longColor = "#1234567890";

            CreateNeedGroupRequestDTO dto = assertDoesNotThrow(
                    () -> new CreateNeedGroupRequestDTO("EXPENSE", longName, longIcon, longColor));

            assertEquals(longName, dto.getName());
            assertEquals(longIcon, dto.getIcon());
            assertEquals(longColor, dto.getColor());
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (equals, hashCode, toString)")
    class IdentityTests {

        @Test
        @DisplayName("Hai DTO có cùng giá trị sau chuẩn hóa phải bằng nhau và cùng hashCode")
        void shouldBeEqualAndShareHashCodeWhenNormalizedAttributesMatch() {
            CreateNeedGroupRequestDTO dto1 = new CreateNeedGroupRequestDTO(
                    "EXPENSE", "Ăn uống", "utensils", "#FF5722");
            CreateNeedGroupRequestDTO dto2 = new CreateNeedGroupRequestDTO(
                    "  expense ", " Ăn uống ", " utensils ", " #FF5722 ");

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Không bằng nhau khi khác bất kỳ trường dữ liệu nào")
        void shouldNotEqualWhenAnyFieldDiffers() {
            CreateNeedGroupRequestDTO base = new CreateNeedGroupRequestDTO(
                    "EXPENSE", "Thiết yếu", "home", "#2196F3");

            assertNotEquals(base, new CreateNeedGroupRequestDTO("INCOME", "Thiết yếu", "home", "#2196F3"));
            assertNotEquals(base, new CreateNeedGroupRequestDTO("EXPENSE", "Hưởng thụ", "home", "#2196F3"));
            assertNotEquals(base, new CreateNeedGroupRequestDTO("EXPENSE", "Thiết yếu", "star", "#2196F3"));
            assertNotEquals(base, new CreateNeedGroupRequestDTO("EXPENSE", "Thiết yếu", "home", "#000000"));
            assertNotEquals(base, null);
            assertNotEquals(base, "NotADTO");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và thông tin các thuộc tính")
        void shouldProduceInformativeToString() {
            CreateNeedGroupRequestDTO dto = new CreateNeedGroupRequestDTO(
                    "EXPENSE", "Học tập", "book", "#3F51B5");

            String str = dto.toString();
            assertTrue(str.contains("CreateNeedGroupRequestDTO"));
            assertTrue(str.contains("EXPENSE"));
            assertTrue(str.contains("Học tập"));
            assertTrue(str.contains("book"));
            assertTrue(str.contains("#3F51B5"));
        }
    }
}

