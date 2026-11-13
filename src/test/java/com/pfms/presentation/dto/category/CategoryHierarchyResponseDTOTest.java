package com.pfms.presentation.dto.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CategoryHierarchyResponseDTO")
class CategoryHierarchyResponseDTOTest {

    private final UUID sampleNeedGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000040");

    private CategoryResponseDTO createSampleCategory(String name) {
        return new CategoryResponseDTO(
                sampleCategoryId,
                name,
                sampleNeedGroupId,
                "Ăn uống thiết yếu",
                "EXPENSE",
                "ACTIVE"
        );
    }

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ tham số và danh sách danh mục con")
        void shouldCreateWithAllParameters() {
            List<CategoryResponseDTO> categories = List.of(
                    createSampleCategory("Cơm trưa"),
                    createSampleCategory("Cà phê")
            );

            CategoryHierarchyResponseDTO dto = new CategoryHierarchyResponseDTO(
                    "EXPENSE",
                    sampleNeedGroupId,
                    "Ăn uống thiết yếu",
                    "ACTIVE",
                    categories
            );

            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals(sampleNeedGroupId, dto.getNeedGroupId());
            assertEquals("Ăn uống thiết yếu", dto.getNeedGroupName());
            assertEquals("ACTIVE", dto.getNeedGroupStatus());
            assertEquals(2, dto.getCategoryCount());
            assertEquals(2, dto.getCategories().size());
            assertTrue(dto.isActive());
        }

        @Test
        @DisplayName("Bảo vệ tính bất biến: danh sách trả về không thể chỉnh sửa và không bị ảnh hưởng bởi danh sách nguồn")
        void shouldEnforceImmutabilityAndDefensiveCopy() {
            List<CategoryResponseDTO> mutableList = new ArrayList<>();
            mutableList.add(createSampleCategory("Khoản 1"));

            CategoryHierarchyResponseDTO dto = new CategoryHierarchyResponseDTO(
                    "EXPENSE",
                    sampleNeedGroupId,
                    "Nhóm chi tiêu",
                    "ACTIVE",
                    mutableList
            );

            // Thay đổi danh sách nguồn không ảnh hưởng đến DTO
            mutableList.add(createSampleCategory("Khoản 2"));
            assertEquals(1, dto.getCategoryCount());

            // Thao tác chỉnh sửa danh sách trả về từ DTO phải ném ngoại lệ
            assertThrows(UnsupportedOperationException.class, () -> dto.getCategories().add(createSampleCategory("Khoản 3")));
        }

        @Test
        @DisplayName("Tự động gán danh sách rỗng khi categories là null")
        void shouldDefaultToEmptyListWhenCategoriesNull() {
            CategoryHierarchyResponseDTO dto = new CategoryHierarchyResponseDTO(
                    "INCOME",
                    sampleNeedGroupId,
                    "Thu nhập thụ động",
                    "ACTIVE",
                    null
            );

            assertNotNull(dto.getCategories());
            assertTrue(dto.getCategories().isEmpty());
            assertEquals(0, dto.getCategoryCount());
        }

        @Test
        @DisplayName("Chuẩn hóa khoảng trắng và gán giá trị mặc định cho loại thu chi và trạng thái")
        void shouldNormalizeWhitespaceAndDefaults() {
            CategoryHierarchyResponseDTO dto = new CategoryHierarchyResponseDTO(
                    null,
                    sampleNeedGroupId,
                    "   Nhóm mặc định   ",
                    null,
                    null
            );

            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals("Nhóm mặc định", dto.getNeedGroupName());
            assertEquals("ACTIVE", dto.getNeedGroupStatus());
            assertTrue(dto.isActive());
        }
    }

    @Nested
    @DisplayName("Kiểm tra tiền điều kiện bắt buộc")
    class PreconditionTests {

        @Test
        @DisplayName("Ném NullPointerException khi needGroupId là null")
        void shouldThrowExceptionWhenNeedGroupIdIsNull() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> new CategoryHierarchyResponseDTO("EXPENSE", null, "Nhóm", "ACTIVE", List.of())
            );

            assertTrue(exception.getMessage().contains("Mã nhóm nhu cầu"));
        }
    }

    @Nested
    @DisplayName("Kiểm tra tính đồng nhất (equals, hashCode, toString)")
    class IdentityTests {

        @Test
        @DisplayName("Hai đối tượng cùng giá trị thuộc tính phải bằng nhau và cùng hashCode")
        void shouldBeEqualWhenAttributesMatch() {
            CategoryHierarchyResponseDTO dto1 = new CategoryHierarchyResponseDTO(
                    "EXPENSE", sampleNeedGroupId, "Ăn uống", "ACTIVE", List.of());
            CategoryHierarchyResponseDTO dto2 = new CategoryHierarchyResponseDTO(
                    "EXPENSE", sampleNeedGroupId, "Ăn uống", "ACTIVE", List.of());

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác thuộc tính không được bằng nhau")
        void shouldNotBeEqualWhenAttributesDiffer() {
            UUID otherId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            CategoryHierarchyResponseDTO dto1 = new CategoryHierarchyResponseDTO(
                    "EXPENSE", sampleNeedGroupId, "Ăn uống", "ACTIVE", List.of());
            CategoryHierarchyResponseDTO dto2 = new CategoryHierarchyResponseDTO(
                    "EXPENSE", otherId, "Ăn uống", "ACTIVE", List.of());

            assertNotEquals(dto1, dto2);
            assertNotEquals(dto1, null);
            assertNotEquals(dto1, "DifferentType");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và thông tin phân cấp")
        void shouldReturnInformativeToString() {
            CategoryHierarchyResponseDTO dto = new CategoryHierarchyResponseDTO(
                    "EXPENSE", sampleNeedGroupId, "Ăn uống", "ACTIVE", List.of());
            String str = dto.toString();

            assertTrue(str.contains("CategoryHierarchyResponseDTO"));
            assertTrue(str.contains(sampleNeedGroupId.toString()));
            assertTrue(str.contains("Ăn uống"));
            assertTrue(str.contains("EXPENSE"));
        }
    }
}

