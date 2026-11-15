package com.pfms.presentation.controller;

import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.category.CreateCategoryRequestDTO;
import com.pfms.presentation.dto.category.CreateNeedGroupRequestDTO;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import com.pfms.presentation.dto.category.RenameCategoryRequestDTO;
import com.pfms.presentation.dto.category.RenameNeedGroupRequestDTO;
import com.pfms.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử CategoryGUIController - Bộ điều phối giao diện phân hệ Quản lý Danh mục")
class CategoryGUIControllerTest {

    private CategoryService categoryService;
    private CategoryGUIController controller;

    @BeforeEach
    void setUp() {
        categoryService = Mockito.mock(CategoryService.class);
        controller = new CategoryGUIController(categoryService);
    }

    @Nested
    @DisplayName("Kiểm thử Khởi tạo (Constructor)")
    class ConstructorTests {

        @Test
        @DisplayName("Ném NullPointerException khi CategoryService là null")
        void constructor_NullService_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () -> new CategoryGUIController(null));
        }

        @Test
        @DisplayName("Khởi tạo thành công khi truyền CategoryService hợp lệ")
        void constructor_ValidService_InstantiatesSuccessfully() {
            assertNotNull(controller);
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleCreateNeedGroup (UC-CAT-01, FR-CAT-02)")
    class CreateNeedGroupTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi loại giao dịch bị null hoặc rỗng")
        void handleCreateNeedGroup_BlankType_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleCreateNeedGroup("  ", "Ăn uống", "icon.png", "#FF0000"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi loại giao dịch không phải INCOME hoặc EXPENSE")
        void handleCreateNeedGroup_InvalidType_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleCreateNeedGroup("TRANSFER", "Ăn uống", "icon.png", "#FF0000"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên nhóm nhu cầu để trống")
        void handleCreateNeedGroup_BlankName_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleCreateNeedGroup("EXPENSE", "   ", "icon.png", "#FF0000"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên nhóm nhu cầu vượt quá 50 ký tự")
        void handleCreateNeedGroup_NameExceeds50_ThrowsIllegalArgumentException() {
            String longName = "A".repeat(51);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleCreateNeedGroup("EXPENSE", longName, "icon.png", "#FF0000"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi biểu tượng vượt quá 50 ký tự")
        void handleCreateNeedGroup_IconExceeds50_ThrowsIllegalArgumentException() {
            String longIcon = "I".repeat(51);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleCreateNeedGroup("EXPENSE", "Ăn uống", longIcon, "#FF0000"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã màu vượt quá 20 ký tự")
        void handleCreateNeedGroup_ColorExceeds20_ThrowsIllegalArgumentException() {
            String longColor = "C".repeat(21);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleCreateNeedGroup("EXPENSE", "Ăn uống", "icon.png", longColor));
        }

        @Test
        @DisplayName("Kiểm thực thành công và ủy quyền cho categoryService.createNeedGroup")
        void handleCreateNeedGroup_ValidInputs_DelegatesToService() {
            UUID id = UUID.randomUUID();
            NeedGroupResponseDTO expected = new NeedGroupResponseDTO(
                    id, "Ăn uống", "EXPENSE", "ACTIVE", "icon.png", "#FF0000", LocalDateTime.now());
            when(categoryService.createNeedGroup(any())).thenReturn(expected);

            NeedGroupResponseDTO result = controller.handleCreateNeedGroup(
                    "expense", "  Ăn uống  ", " icon.png ", " #FF0000 ");

            assertNotNull(result);
            assertEquals("Ăn uống", result.getName());

            ArgumentCaptor<CreateNeedGroupRequestDTO> captor = ArgumentCaptor.forClass(CreateNeedGroupRequestDTO.class);
            verify(categoryService).createNeedGroup(captor.capture());
            CreateNeedGroupRequestDTO request = captor.getValue();
            assertEquals("EXPENSE", request.getTransactionType());
            assertEquals("Ăn uống", request.getName());
            assertEquals("icon.png", request.getIcon());
            assertEquals("#FF0000", request.getColor());
        }

        @Test
        @DisplayName("Hỗ trợ phương thức nạp chồng chỉ có loại và tên nhóm nhu cầu")
        void handleCreateNeedGroup_Overload_DelegatesWithDefaultIconAndColor() {
            UUID id = UUID.randomUUID();
            NeedGroupResponseDTO expected = new NeedGroupResponseDTO(
                    id, "Lương", "INCOME", "ACTIVE", "", "", LocalDateTime.now());
            when(categoryService.createNeedGroup(any())).thenReturn(expected);

            NeedGroupResponseDTO result = controller.handleCreateNeedGroup("INCOME", "Lương");
            assertNotNull(result);

            ArgumentCaptor<CreateNeedGroupRequestDTO> captor = ArgumentCaptor.forClass(CreateNeedGroupRequestDTO.class);
            verify(categoryService).createNeedGroup(captor.capture());
            CreateNeedGroupRequestDTO request = captor.getValue();
            assertEquals("INCOME", request.getTransactionType());
            assertEquals("Lương", request.getName());
            assertEquals("", request.getIcon());
            assertEquals("", request.getColor());
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleRenameNeedGroup (UC-CAT-02, FR-CAT-03)")
    class RenameNeedGroupTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã nhóm nhu cầu là null")
        void handleRenameNeedGroup_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRenameNeedGroup(null, "Tên mới", "icon.png", "#00FF00"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên mới để trống")
        void handleRenameNeedGroup_BlankName_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRenameNeedGroup(UUID.randomUUID(), "   ", "icon.png", "#00FF00"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên mới quá 50 ký tự")
        void handleRenameNeedGroup_NameExceeds50_ThrowsIllegalArgumentException() {
            String longName = "B".repeat(51);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRenameNeedGroup(UUID.randomUUID(), longName, "icon.png", "#00FF00"));
        }

        @Test
        @DisplayName("Kiểm thực thành công và ủy quyền cho categoryService.renameNeedGroup")
        void handleRenameNeedGroup_ValidInputs_DelegatesToService() {
            UUID id = UUID.randomUUID();
            NeedGroupResponseDTO expected = new NeedGroupResponseDTO(
                    id, "Ăn uống ngoài", "EXPENSE", "ACTIVE", "new-icon.png", "#00FF00", LocalDateTime.now());
            when(categoryService.renameNeedGroup(any())).thenReturn(expected);

            NeedGroupResponseDTO result = controller.handleRenameNeedGroup(
                    id, "  Ăn uống ngoài  ", " new-icon.png ", " #00FF00 ");

            assertNotNull(result);
            assertEquals("Ăn uống ngoài", result.getName());

            ArgumentCaptor<RenameNeedGroupRequestDTO> captor = ArgumentCaptor.forClass(RenameNeedGroupRequestDTO.class);
            verify(categoryService).renameNeedGroup(captor.capture());
            RenameNeedGroupRequestDTO request = captor.getValue();
            assertEquals(id, request.getNeedGroupId());
            assertEquals("Ăn uống ngoài", request.getName());
            assertEquals("new-icon.png", request.getIcon());
            assertEquals("#00FF00", request.getColor());
        }

        @Test
        @DisplayName("Hỗ trợ phương thức nạp chồng chỉ có ID và tên mới")
        void handleRenameNeedGroup_Overload_DelegatesWithDefaultIconAndColor() {
            UUID id = UUID.randomUUID();
            NeedGroupResponseDTO expected = new NeedGroupResponseDTO(
                    id, "Thưởng", "INCOME", "ACTIVE", "", "", LocalDateTime.now());
            when(categoryService.renameNeedGroup(any())).thenReturn(expected);

            NeedGroupResponseDTO result = controller.handleRenameNeedGroup(id, "Thưởng");
            assertNotNull(result);

            ArgumentCaptor<RenameNeedGroupRequestDTO> captor = ArgumentCaptor.forClass(RenameNeedGroupRequestDTO.class);
            verify(categoryService).renameNeedGroup(captor.capture());
            RenameNeedGroupRequestDTO request = captor.getValue();
            assertEquals(id, request.getNeedGroupId());
            assertEquals("Thưởng", request.getName());
            assertEquals("", request.getIcon());
            assertEquals("", request.getColor());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Nhóm nhu cầu Vòng đời (Restore & Delete)")
    class NeedGroupLifecycleTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã nhóm nhu cầu khôi phục là null")
        void handleRestoreNeedGroup_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleRestoreNeedGroup(null));
        }

        @Test
        @DisplayName("Ủy quyền khôi phục cho categoryService.restoreNeedGroup")
        void handleRestoreNeedGroup_ValidId_DelegatesToService() {
            UUID id = UUID.randomUUID();
            NeedGroupResponseDTO expected = new NeedGroupResponseDTO(
                    id, "Ăn uống", "EXPENSE", "ACTIVE", "", "", LocalDateTime.now());
            when(categoryService.restoreNeedGroup(id)).thenReturn(expected);

            NeedGroupResponseDTO result = controller.handleRestoreNeedGroup(id);
            assertSame(expected, result);
            verify(categoryService).restoreNeedGroup(id);
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã nhóm nhu cầu xóa là null")
        void handleDeleteNeedGroup_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleDeleteNeedGroup(null));
        }

        @Test
        @DisplayName("Ủy quyền xóa nhóm nhu cầu cho categoryService.deleteNeedGroup")
        void handleDeleteNeedGroup_ValidId_DelegatesToService() {
            UUID id = UUID.randomUUID();
            controller.handleDeleteNeedGroup(id);
            verify(categoryService).deleteNeedGroup(id);
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleCreateCategory (UC-CAT-01, FR-CAT-02)")
    class CreateCategoryTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã nhóm nhu cầu cha là null")
        void handleCreateCategory_NullParentId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleCreateCategory(null, "Cà phê"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên danh mục con để trống")
        void handleCreateCategory_BlankName_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleCreateCategory(UUID.randomUUID(), "   "));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên danh mục con vượt quá 50 ký tự")
        void handleCreateCategory_NameExceeds50_ThrowsIllegalArgumentException() {
            String longName = "C".repeat(51);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleCreateCategory(UUID.randomUUID(), longName));
        }

        @Test
        @DisplayName("Kiểm thực thành công và ủy quyền cho categoryService.createCategory")
        void handleCreateCategory_ValidInputs_DelegatesToService() {
            UUID parentId = UUID.randomUUID();
            UUID catId = UUID.randomUUID();
            CategoryResponseDTO expected = new CategoryResponseDTO(
                    catId, "Cà phê", parentId, "Ăn uống", "EXPENSE", "ACTIVE", LocalDateTime.now());
            when(categoryService.createCategory(any())).thenReturn(expected);

            CategoryResponseDTO result = controller.handleCreateCategory(parentId, "  Cà phê  ");

            assertNotNull(result);
            assertEquals("Cà phê", result.getName());

            ArgumentCaptor<CreateCategoryRequestDTO> captor = ArgumentCaptor.forClass(CreateCategoryRequestDTO.class);
            verify(categoryService).createCategory(captor.capture());
            CreateCategoryRequestDTO request = captor.getValue();
            assertEquals(parentId, request.getParentGroupId());
            assertEquals("Cà phê", request.getName());
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleRenameCategory (UC-CAT-02, FR-CAT-03)")
    class RenameCategoryTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã danh mục là null")
        void handleRenameCategory_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRenameCategory(null, "Trà sữa", UUID.randomUUID()));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên mới để trống")
        void handleRenameCategory_BlankName_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRenameCategory(UUID.randomUUID(), "   ", UUID.randomUUID()));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên mới vượt quá 50 ký tự")
        void handleRenameCategory_NameExceeds50_ThrowsIllegalArgumentException() {
            String longName = "D".repeat(51);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRenameCategory(UUID.randomUUID(), longName, UUID.randomUUID()));
        }

        @Test
        @DisplayName("Kiểm thực thành công đổi tên và chuyển nhóm cha, ủy quyền cho categoryService.renameCategory")
        void handleRenameCategory_WithNewParentGroup_DelegatesToService() {
            UUID catId = UUID.randomUUID();
            UUID newParentId = UUID.randomUUID();
            CategoryResponseDTO expected = new CategoryResponseDTO(
                    catId, "Trà sữa", newParentId, "Giải trí", "EXPENSE", "ACTIVE", LocalDateTime.now());
            when(categoryService.renameCategory(any())).thenReturn(expected);

            CategoryResponseDTO result = controller.handleRenameCategory(catId, "  Trà sữa  ", newParentId);

            assertNotNull(result);
            assertEquals("Trà sữa", result.getName());

            ArgumentCaptor<RenameCategoryRequestDTO> captor = ArgumentCaptor.forClass(RenameCategoryRequestDTO.class);
            verify(categoryService).renameCategory(captor.capture());
            RenameCategoryRequestDTO request = captor.getValue();
            assertEquals(catId, request.getCategoryId());
            assertEquals("Trà sữa", request.getName());
            assertEquals(newParentId, request.getNewParentGroupId());
        }

        @Test
        @DisplayName("Hỗ trợ phương thức nạp chồng chỉ đổi tên mà không chuyển nhóm cha")
        void handleRenameCategory_WithoutNewParentGroup_DelegatesWithNullParent() {
            UUID catId = UUID.randomUUID();
            CategoryResponseDTO expected = new CategoryResponseDTO(
                    catId, "Cà phê muối", UUID.randomUUID(), "Ăn uống", "EXPENSE", "ACTIVE", LocalDateTime.now());
            when(categoryService.renameCategory(any())).thenReturn(expected);

            CategoryResponseDTO result = controller.handleRenameCategory(catId, "Cà phê muối");

            assertNotNull(result);
            ArgumentCaptor<RenameCategoryRequestDTO> captor = ArgumentCaptor.forClass(RenameCategoryRequestDTO.class);
            verify(categoryService).renameCategory(captor.capture());
            RenameCategoryRequestDTO request = captor.getValue();
            assertEquals(catId, request.getCategoryId());
            assertEquals("Cà phê muối", request.getName());
            assertNull(request.getNewParentGroupId());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Danh mục Vòng đời (Restore & Delete)")
    class CategoryLifecycleTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã danh mục khôi phục là null")
        void handleRestoreCategory_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleRestoreCategory(null));
        }

        @Test
        @DisplayName("Ủy quyền khôi phục danh mục cho categoryService.restoreCategory")
        void handleRestoreCategory_ValidId_DelegatesToService() {
            UUID id = UUID.randomUUID();
            CategoryResponseDTO expected = new CategoryResponseDTO(
                    id, "Cà phê", UUID.randomUUID(), "Ăn uống", "EXPENSE", "ACTIVE", LocalDateTime.now());
            when(categoryService.restoreCategory(id)).thenReturn(expected);

            CategoryResponseDTO result = controller.handleRestoreCategory(id);
            assertSame(expected, result);
            verify(categoryService).restoreCategory(id);
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã danh mục xóa là null")
        void handleDeleteCategory_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleDeleteCategory(null));
        }

        @Test
        @DisplayName("Ủy quyền xóa danh mục cho categoryService.deleteCategory")
        void handleDeleteCategory_ValidId_DelegatesToService() {
            UUID id = UUID.randomUUID();
            controller.handleDeleteCategory(id);
            verify(categoryService).deleteCategory(id);
        }
    }

    @Nested
    @DisplayName("Kiểm thử loadCategoryHierarchy (UC-CAT-04, FR-CAT-01)")
    class LoadCategoryHierarchyTests {

        @Test
        @DisplayName("Ủy quyền truy xuất cấu trúc cây phân cấp danh mục cho categoryService.getCategoryHierarchy")
        void loadCategoryHierarchy_DelegatesToService() {
            UUID groupId = UUID.randomUUID();
            List<CategoryHierarchyResponseDTO> expected = Collections.singletonList(
                    new CategoryHierarchyResponseDTO("EXPENSE", groupId, "Ăn uống", "ACTIVE", Collections.emptyList()));
            when(categoryService.getCategoryHierarchy()).thenReturn(expected);

            List<CategoryHierarchyResponseDTO> result = controller.loadCategoryHierarchy();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Ăn uống", result.get(0).getNeedGroupName());
            verify(categoryService).getCategoryHierarchy();
        }
    }
}
