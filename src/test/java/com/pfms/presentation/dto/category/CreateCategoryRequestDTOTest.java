package com.pfms.presentation.dto.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CreateCategoryRequestDTO")
class CreateCategoryRequestDTOTest {

    private final UUID sampleParentGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với parentGroupId và name hợp lệ")
        void shouldCreateWithValidParentGroupIdAndName() {
            CreateCategoryRequestDTO dto = new CreateCategoryRequestDTO(sampleParentGroupId, "Ăn uống hàng ngày");

            assertEquals(sampleParentGroupId, dto.getParentGroupId());
            assertEquals(sampleParentGroupId, dto.getNeedGroupId());
            assertEquals("Ăn uống hàng ngày", dto.getName());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng của tên danh mục")
        void shouldTrimNameWhitespace() {
            CreateCategoryRequestDTO dto = new CreateCategoryRequestDTO(sampleParentGroupId, "   Mua sắm online   ");

            assertEquals("Mua sắm online", dto.getName());
        }

        @Test
        @DisplayName("Giữ nguyên giá trị null của name để tầng dịch vụ hoặc kiểm tra biểu mẫu xử lý")
        void shouldPreserveNullName() {
            CreateCategoryRequestDTO dto = new CreateCategoryRequestDTO(sampleParentGroupId, null);

            assertNull(dto.getName());
        }
    }

    @Nested
    @DisplayName("Kiểm tra tiền điều kiện bắt buộc")
    class PreconditionTests {

        @Test
        @DisplayName("Ném NullPointerException khi parentGroupId là null")
        void shouldThrowExceptionWhenParentGroupIdIsNull() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> new CreateCategoryRequestDTO(null, "Ăn uống")
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
            CreateCategoryRequestDTO dto1 = new CreateCategoryRequestDTO(sampleParentGroupId, "Cà phê");
            CreateCategoryRequestDTO dto2 = new CreateCategoryRequestDTO(sampleParentGroupId, "Cà phê");

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác thuộc tính không được bằng nhau")
        void shouldNotBeEqualWhenAttributesDiffer() {
            UUID anotherGroupId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            CreateCategoryRequestDTO dto1 = new CreateCategoryRequestDTO(sampleParentGroupId, "Cà phê");
            CreateCategoryRequestDTO dto2 = new CreateCategoryRequestDTO(anotherGroupId, "Cà phê");
            CreateCategoryRequestDTO dto3 = new CreateCategoryRequestDTO(sampleParentGroupId, "Trà sữa");

            assertNotEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertNotEquals(dto1, null);
            assertNotEquals(dto1, "NotADTO");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và các trường thông tin")
        void shouldReturnInformativeToString() {
            CreateCategoryRequestDTO dto = new CreateCategoryRequestDTO(sampleParentGroupId, "Tiền điện");
            String str = dto.toString();

            assertTrue(str.contains("CreateCategoryRequestDTO"));
            assertTrue(str.contains(sampleParentGroupId.toString()));
            assertTrue(str.contains("Tiền điện"));
        }
    }
}
