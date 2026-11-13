package com.pfms.presentation.dto.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử RestoreCategoryRequestDTO")
class RestoreCategoryRequestDTOTest {

    private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000030");

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với categoryId hợp lệ và hỗ trợ phương thức getId() alias")
        void shouldCreateWithValidCategoryId() {
            RestoreCategoryRequestDTO dto = new RestoreCategoryRequestDTO(sampleCategoryId);

            assertEquals(sampleCategoryId, dto.getCategoryId());
            assertEquals(sampleCategoryId, dto.getId());
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
                    () -> new RestoreCategoryRequestDTO(null)
            );

            assertTrue(exception.getMessage().contains("Mã danh mục"));
        }
    }

    @Nested
    @DisplayName("Kiểm tra tính đồng nhất (equals, hashCode, toString)")
    class IdentityTests {

        @Test
        @DisplayName("Hai đối tượng cùng categoryId phải bằng nhau và cùng hashCode")
        void shouldBeEqualWhenCategoryIdMatches() {
            RestoreCategoryRequestDTO dto1 = new RestoreCategoryRequestDTO(sampleCategoryId);
            RestoreCategoryRequestDTO dto2 = new RestoreCategoryRequestDTO(sampleCategoryId);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác categoryId không được bằng nhau")
        void shouldNotBeEqualWhenCategoryIdDiffers() {
            UUID otherId = UUID.fromString("00000000-0000-0000-0000-000000000031");
            RestoreCategoryRequestDTO dto1 = new RestoreCategoryRequestDTO(sampleCategoryId);
            RestoreCategoryRequestDTO dto2 = new RestoreCategoryRequestDTO(otherId);

            assertNotEquals(dto1, dto2);
            assertNotEquals(dto1, null);
            assertNotEquals(dto1, "DifferentType");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và mã categoryId")
        void shouldReturnInformativeToString() {
            RestoreCategoryRequestDTO dto = new RestoreCategoryRequestDTO(sampleCategoryId);
            String str = dto.toString();

            assertTrue(str.contains("RestoreCategoryRequestDTO"));
            assertTrue(str.contains(sampleCategoryId.toString()));
        }
    }
}

