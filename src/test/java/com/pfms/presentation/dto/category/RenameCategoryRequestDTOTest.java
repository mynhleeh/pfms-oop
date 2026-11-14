package com.pfms.presentation.dto.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử RenameCategoryRequestDTO")
class RenameCategoryRequestDTOTest {

    private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private final UUID sampleNewParentGroupId = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với categoryId, name và newParentGroupId")
        void shouldCreateWithAllFields() {
            RenameCategoryRequestDTO dto = new RenameCategoryRequestDTO(
                    sampleCategoryId, "Ăn nhà hàng", sampleNewParentGroupId);

            assertEquals(sampleCategoryId, dto.getCategoryId());
            assertEquals(sampleCategoryId, dto.getId());
            assertEquals("Ăn nhà hàng", dto.getName());
            assertEquals(sampleNewParentGroupId, dto.getNewParentGroupId());
            assertTrue(dto.hasNewParentGroup());
        }

        @Test
        @DisplayName("Khởi tạo thành công với constructor rút gọn (không chuyển nhóm cha)")
        void shouldCreateWithOnlyCategoryIdAndName() {
            RenameCategoryRequestDTO dto = new RenameCategoryRequestDTO(sampleCategoryId, "Ăn sáng");

            assertEquals(sampleCategoryId, dto.getCategoryId());
            assertEquals("Ăn sáng", dto.getName());
            assertNull(dto.getNewParentGroupId());
            assertFalse(dto.hasNewParentGroup());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng tên danh mục")
        void shouldTrimNameWhitespace() {
            RenameCategoryRequestDTO dto = new RenameCategoryRequestDTO(
                    sampleCategoryId, "   Đi chợ cuối tuần   ", sampleNewParentGroupId);

            assertEquals("Đi chợ cuối tuần", dto.getName());
        }

        @Test
        @DisplayName("Giữ nguyên giá trị null của name để tầng dịch vụ hoặc kiểm tra biểu mẫu xử lý")
        void shouldPreserveNullName() {
            RenameCategoryRequestDTO dto = new RenameCategoryRequestDTO(
                    sampleCategoryId, null, null);

            assertNull(dto.getName());
            assertNull(dto.getNewParentGroupId());
            assertFalse(dto.hasNewParentGroup());
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
                    () -> new RenameCategoryRequestDTO(null, "Tên mới", sampleNewParentGroupId)
            );

            assertTrue(exception.getMessage().contains("Mã danh mục"));
        }
    }

    @Nested
    @DisplayName("Kiểm tra tính đồng nhất (equals, hashCode, toString)")
    class IdentityTests {

        @Test
        @DisplayName("Hai đối tượng cùng giá trị thuộc tính phải bằng nhau và cùng hashCode")
        void shouldBeEqualWhenAttributesMatch() {
            RenameCategoryRequestDTO dto1 = new RenameCategoryRequestDTO(
                    sampleCategoryId, "Tiền nước", sampleNewParentGroupId);
            RenameCategoryRequestDTO dto2 = new RenameCategoryRequestDTO(
                    sampleCategoryId, "Tiền nước", sampleNewParentGroupId);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác thuộc tính không được bằng nhau")
        void shouldNotBeEqualWhenAttributesDiffer() {
            UUID otherCatId = UUID.fromString("00000000-0000-0000-0000-000000000099");
            RenameCategoryRequestDTO dto1 = new RenameCategoryRequestDTO(
                    sampleCategoryId, "Tiền nước", sampleNewParentGroupId);
            RenameCategoryRequestDTO dto2 = new RenameCategoryRequestDTO(
                    otherCatId, "Tiền nước", sampleNewParentGroupId);
            RenameCategoryRequestDTO dto3 = new RenameCategoryRequestDTO(
                    sampleCategoryId, "Tiền điện", sampleNewParentGroupId);
            RenameCategoryRequestDTO dto4 = new RenameCategoryRequestDTO(
                    sampleCategoryId, "Tiền nước", null);

            assertNotEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertNotEquals(dto1, dto4);
            assertNotEquals(dto1, null);
            assertNotEquals(dto1, "DifferentType");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và các trường thông tin")
        void shouldReturnInformativeToString() {
            RenameCategoryRequestDTO dto = new RenameCategoryRequestDTO(
                    sampleCategoryId, "Học phí", sampleNewParentGroupId);
            String str = dto.toString();

            assertTrue(str.contains("RenameCategoryRequestDTO"));
            assertTrue(str.contains(sampleCategoryId.toString()));
            assertTrue(str.contains("Học phí"));
            assertTrue(str.contains(sampleNewParentGroupId.toString()));
        }
    }
}

