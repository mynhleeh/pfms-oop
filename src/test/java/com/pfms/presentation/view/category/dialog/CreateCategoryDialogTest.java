package com.pfms.presentation.view.category.dialog;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.JFrame;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử CreateCategoryDialog - Hộp thoại tạo danh mục con mới")
class CreateCategoryDialogTest {

    private CategoryGUIController categoryController;
    private UUID parentGroupId;
    private CreateCategoryDialog dialog;

    @BeforeEach
    void setUp() {
        categoryController = Mockito.mock(CategoryGUIController.class);
        parentGroupId = UUID.randomUUID();

        CategoryHierarchyResponseDTO group1 = new CategoryHierarchyResponseDTO(
                "EXPENSE", parentGroupId, "Chi tiêu thiết yếu", "ACTIVE", Collections.emptyList());

        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(group1));

        dialog = new CreateCategoryDialog((JFrame) null, categoryController);
        dialog.setErrorConsumer(msg -> {});
    }

    @Test
    @DisplayName("Khởi tạo hộp thoại nạp sẵn danh sách nhóm nhu cầu cha và trạng thái ban đầu false")
    void testInitialDialogState() {
        assertFalse(dialog.isSuccess());
        assertNull(dialog.getCreatedCategory());
        assertEquals("", dialog.getNameInputValue());
        assertEquals(parentGroupId, dialog.getSelectedParentGroupId());
    }

    @Test
    @DisplayName("Lưu danh mục con thành công khi nhập dữ liệu hợp lệ")
    void testSaveCategorySuccess() {
        CategoryResponseDTO response = new CategoryResponseDTO(
                UUID.randomUUID(), "Ăn uống", parentGroupId, "Chi tiêu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());

        when(categoryController.handleCreateCategory(parentGroupId, "Ăn uống")).thenReturn(response);

        dialog.setSelectedParentGroupId(parentGroupId);
        dialog.setNameInputValue("Ăn uống");
        dialog.triggerSave();

        assertTrue(dialog.isSuccess());
        assertSame(response, dialog.getCreatedCategory());
        verify(categoryController).handleCreateCategory(parentGroupId, "Ăn uống");
    }

    @Test
    @DisplayName("Bắt giữ ngoại lệ nghiệp vụ khi tên danh mục bị trùng lặp trong nhóm cha")
    void testSaveCategoryDuplicateExceptionHandled() {
        when(categoryController.handleCreateCategory(parentGroupId, "Đã tồn tại"))
                .thenThrow(new BusinessException(ErrorCode.ERR_NAM_002, "Danh mục 'Đã tồn tại' đã có trong nhóm"));

        AtomicReference<String> errorCapture = new AtomicReference<>();
        dialog.setErrorConsumer(errorCapture::set);

        dialog.setSelectedParentGroupId(parentGroupId);
        dialog.setNameInputValue("Đã tồn tại");
        dialog.triggerSave();

        assertFalse(dialog.isSuccess());
        assertNull(dialog.getCreatedCategory());
        assertNotNull(errorCapture.get());
        assertTrue(errorCapture.get().contains("đã có trong nhóm"));
    }

    @Test
    @DisplayName("Ném NullPointerException khi CategoryGUIController bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new CreateCategoryDialog((JFrame) null, null));
    }
}
