package com.pfms.presentation.dto.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CategoryResponseDTO")
class CategoryResponseDTOTest {

    private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000040");
    private final UUID sampleParentGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final LocalDateTime sampleTime = LocalDateTime.of(2026, 11, 13, 10, 0, 0);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ tham số bao gồm createdAt")
        void shouldCreateWithAllParameters() {
            CategoryResponseDTO dto = new CategoryResponseDTO(
                    sampleCategoryId,
                    "Cà phê & Trà sữa",
                    sampleParentGroupId,
                    "Ăn uống thiết yếu",
                    "EXPENSE",
                    "ACTIVE",
                    sampleTime
            );

            assertEquals(sampleCategoryId, dto.getCategoryId());
            assertEquals("Cà phê & Trà sữa", dto.getName());
            assertEquals(sampleParentGroupId, dto.getParentGroupId());
            assertEquals("Ăn uống thiết yếu", dto.getParentGroupName());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals("ACTIVE", dto.getStatus());
            assertEquals(sampleTime, dto.getCreatedAt());
            assertTrue(dto.isActive());
            assertFalse(dto.isDeleted());
            assertThrows(NoSuchMethodException.class, () -> CategoryResponseDTO.class.getMethod("getId"));
            assertThrows(NoSuchMethodException.class, () -> CategoryResponseDTO.class.getMethod("getNeedGroupId"));
        }

        @Test
        @DisplayName("Khởi tạo thành công với constructor rút gọn, tự động gán createdAt không null")
        void shouldCreateWithConvenienceConstructorAndDefaultCreatedAt() {
            CategoryResponseDTO dto = new CategoryResponseDTO(
                    sampleCategoryId,
                    "Lương hàng tháng",
                    sampleParentGroupId,
                    "Thu nhập chính",
                    "INCOME",
                    "ACTIVE"
            );

            assertEquals(sampleCategoryId, dto.getCategoryId());
            assertEquals("Lương hàng tháng", dto.getName());
            assertNotNull(dto.getCreatedAt());
            assertTrue(dto.isActive());
            assertFalse(dto.isDeleted());
        }

        @Test
        @DisplayName("Chuẩn hóa các trường chuỗi và trạng thái DELETED")
        void shouldNormalizeWhitespaceAndIdentifyDeletedStatus() {
            CategoryResponseDTO dto = new CategoryResponseDTO(
                    sampleCategoryId,
                    "   Đi lại Grab   ",
                    sampleParentGroupId,
                    "   Di chuyển   ",
                    "expense",
                    "DELETED",
                    null
            );

            assertEquals("Đi lại Grab", dto.getName());
            assertEquals("Di chuyển", dto.getParentGroupName());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals("DELETED", dto.getStatus());
            assertNotNull(dto.getCreatedAt());
            assertFalse(dto.isActive());
            assertTrue(dto.isDeleted());
        }

        @Test
        @DisplayName("Mặc định status ACTIVE và transactionType EXPENSE nếu nhận null hoặc rỗng")
        void shouldDefaultStatusAndTransactionTypeWhenNullOrBlank() {
            CategoryResponseDTO dto = new CategoryResponseDTO(
                    sampleCategoryId,
                    "Chi tiêu khác",
                    sampleParentGroupId,
                    null,
                    null,
                    null,
                    null
            );

            assertEquals("Chi tiêu khác", dto.getName());
            assertEquals("", dto.getParentGroupName());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals("ACTIVE", dto.getStatus());
            assertTrue(dto.isActive());
        }
    }

    @Nested
    @DisplayName("Kiểm tra tiền điều kiện bắt buộc")
    class PreconditionTests {

        @Test
        @DisplayName("Ném NullPointerException khi categoryId là null")
        void shouldThrowExceptionWhenCategoryIdIsNull() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> new CategoryResponseDTO(null, "Tên", sampleParentGroupId, "Nhóm", "EXPENSE", "ACTIVE", sampleTime)
            );

            assertTrue(exception.getMessage().contains("Mã danh mục"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi parentGroupId là null")
        void shouldThrowExceptionWhenParentGroupIdIsNull() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> new CategoryResponseDTO(sampleCategoryId, "Tên", null, "Nhóm", "EXPENSE", "ACTIVE", sampleTime)
            );

            assertTrue(exception.getMessage().contains("nhóm nhu cầu cha"));
        }
    }

    @Nested
    @DisplayName("Kiểm tra tính đồng nhất (equals, hashCode, toString)")
    class IdentityTests {

        @Test
        @DisplayName("Hai đối tượng cùng giá trị thuộc tính phải bằng nhau và cùng hashCode")
        void shouldBeEqualWhenAttributesMatch() {
            CategoryResponseDTO dto1 = new CategoryResponseDTO(
                    sampleCategoryId, "Sách", sampleParentGroupId, "Giáo dục", "EXPENSE", "ACTIVE", sampleTime);
            CategoryResponseDTO dto2 = new CategoryResponseDTO(
                    sampleCategoryId, "Sách", sampleParentGroupId, "Giáo dục", "EXPENSE", "ACTIVE", sampleTime);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác categoryId không được bằng nhau")
        void shouldNotBeEqualWhenCategoryIdDiffers() {
            UUID otherId = UUID.fromString("00000000-0000-0000-0000-000000000099");
            CategoryResponseDTO dto1 = new CategoryResponseDTO(
                    sampleCategoryId, "Sách", sampleParentGroupId, "Giáo dục", "EXPENSE", "ACTIVE", sampleTime);
            CategoryResponseDTO dto2 = new CategoryResponseDTO(
                    otherId, "Sách", sampleParentGroupId, "Giáo dục", "EXPENSE", "ACTIVE", sampleTime);

            assertNotEquals(dto1, dto2);
            assertNotEquals(dto1, null);
            assertNotEquals(dto1, "DifferentType");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và các trường thông tin chính")
        void shouldReturnInformativeToString() {
            CategoryResponseDTO dto = new CategoryResponseDTO(
                    sampleCategoryId, "Sách", sampleParentGroupId, "Giáo dục", "EXPENSE", "ACTIVE", sampleTime);
            String str = dto.toString();

            assertTrue(str.contains("CategoryResponseDTO"));
            assertTrue(str.contains(sampleCategoryId.toString()));
            assertTrue(str.contains("Sách"));
            assertTrue(str.contains(sampleParentGroupId.toString()));
            assertTrue(str.contains("Giáo dục"));
        }

        @Test
        @DisplayName("Chuẩn hóa transactionType và status không phụ thuộc Locale mặc định (tr-TR)")
        void shouldNormalizeFieldsWithLocaleRoot() {
            Locale previous = Locale.getDefault();
            try {
                Locale.setDefault(Locale.forLanguageTag("tr-TR"));
                CategoryResponseDTO dto = new CategoryResponseDTO(
                        UUID.randomUUID(),
                        "Lương",
                        UUID.randomUUID(),
                        "Thu nhập",
                        "income",
                        "active"
                );

                assertEquals("INCOME", dto.getTransactionType());
                assertEquals("ACTIVE", dto.getStatus());
            } finally {
                Locale.setDefault(previous);
            }
        }
    }
}

