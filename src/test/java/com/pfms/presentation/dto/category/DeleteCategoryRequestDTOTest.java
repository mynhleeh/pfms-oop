package com.pfms.presentation.dto.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử DeleteCategoryRequestDTO")
class DeleteCategoryRequestDTOTest {

    private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000020");

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với categoryId hợp lệ và hỗ trợ phương thức getId() alias")
        void shouldCreateWithValidCategoryId() {
            DeleteCategoryRequestDTO dto = new DeleteCategoryRequestDTO(sampleCategoryId);

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
                    () -> new DeleteCategoryRequestDTO(null)
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
            DeleteCategoryRequestDTO dto1 = new DeleteCategoryRequestDTO(sampleCategoryId);
            DeleteCategoryRequestDTO dto2 = new DeleteCategoryRequestDTO(sampleCategoryId);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác categoryId không được bằng nhau")
        void shouldNotBeEqualWhenCategoryIdDiffers() {
            UUID otherId = UUID.fromString("00000000-0000-0000-0000-000000000021");
            DeleteCategoryRequestDTO dto1 = new DeleteCategoryRequestDTO(sampleCategoryId);
            DeleteCategoryRequestDTO dto2 = new DeleteCategoryRequestDTO(otherId);

            assertNotEquals(dto1, dto2);
            assertNotEquals(dto1, null);
            assertNotEquals(dto1, "DifferentType");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và mã categoryId")
        void shouldReturnInformativeToString() {
            DeleteCategoryRequestDTO dto = new DeleteCategoryRequestDTO(sampleCategoryId);
            String str = dto.toString();

            assertTrue(str.contains("DeleteCategoryRequestDTO"));
            assertTrue(str.contains(sampleCategoryId.toString()));
        }
    }
}
