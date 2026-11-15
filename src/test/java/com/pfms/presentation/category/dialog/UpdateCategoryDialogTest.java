package com.pfms.presentation.category.dialog;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.category.CategoryGUIController;
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

@DisplayName("Kiểm thử UpdateCategoryDialog - Hộp thoại sửa và chuyển nhóm cha danh mục con (UC-CAT-02)")
class UpdateCategoryDialogTest {

    private CategoryGUIController categoryController;
    private UUID categoryId;
    private UUID group1Id;
    private UUID group2Id;
    private UUID incomeGroupId;
    private UpdateCategoryDialog dialog;

    @BeforeEach
    void setUp() {
        categoryController = Mockito.mock(CategoryGUIController.class);
        categoryId = UUID.randomUUID();
        group1Id = UUID.randomUUID();
        group2Id = UUID.randomUUID();
        incomeGroupId = UUID.randomUUID();

        CategoryHierarchyResponseDTO group1 = new CategoryHierarchyResponseDTO(
                "EXPENSE", group1Id, "Chi tiêu thiết yếu", "ACTIVE", Collections.emptyList());
        CategoryHierarchyResponseDTO group2 = new CategoryHierarchyResponseDTO(
                "EXPENSE", group2Id, "Chi tiêu linh hoạt", "ACTIVE", Collections.emptyList());
        CategoryHierarchyResponseDTO groupInc = new CategoryHierarchyResponseDTO(
                "INCOME", incomeGroupId, "Thu nhập chính", "ACTIVE", Collections.emptyList());

        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(group1, group2, groupInc));

        dialog = new UpdateCategoryDialog(
                (JFrame) null,
                categoryController,
                categoryId,
                "Ăn uống",
                group1Id,
                "EXPENSE"
        );
        dialog.setErrorConsumer(msg -> {});
    }

    @Test
    @DisplayName("Khởi tạo hộp thoại nạp sẵn tên cũ, chọn nhóm cha ban đầu và chỉ lọc nhóm cùng loại EXPENSE")
    void testInitialDialogState() {
        assertFalse(dialog.isSuccess());
        assertNull(dialog.getUpdatedCategory());
        assertEquals("Ăn uống", dialog.getNameInputValue());
        assertEquals(group1Id, dialog.getSelectedParentGroupId());
    }

    @Test
    @DisplayName("Cập nhật danh mục thành công khi đổi cả tên và chuyển nhóm cha mới")
    void testUpdateCategoryNameAndParentGroupSuccess() {
        CategoryResponseDTO response = new CategoryResponseDTO(
                categoryId, "Ăn ngoài", group2Id, "Chi tiêu linh hoạt", "EXPENSE", "ACTIVE", LocalDateTime.now());

        when(categoryController.handleRenameCategory(categoryId, "Ăn ngoài", group2Id)).thenReturn(response);

        dialog.setNameInputValue("Ăn ngoài");
        dialog.setSelectedParentGroupId(group2Id);
        dialog.triggerSave();

        assertTrue(dialog.isSuccess());
        assertSame(response, dialog.getUpdatedCategory());
        verify(categoryController).handleRenameCategory(categoryId, "Ăn ngoài", group2Id);
    }

    @Test
    @DisplayName("Bắt giữ ngoại lệ nghiệp vụ khi đổi tên danh mục bị trùng lặp")
    void testUpdateCategoryDuplicateExceptionHandled() {
        when(categoryController.handleRenameCategory(categoryId, "Đã tồn tại", group1Id))
                .thenThrow(new BusinessException(ErrorCode.ERR_NAM_002, "Tên danh mục đã tồn tại trong nhóm"));

        AtomicReference<String> errorCapture = new AtomicReference<>();
        dialog.setErrorConsumer(errorCapture::set);

        dialog.setNameInputValue("Đã tồn tại");
        dialog.triggerSave();

        assertFalse(dialog.isSuccess());
        assertNull(dialog.getUpdatedCategory());
        assertNotNull(errorCapture.get());
        assertTrue(errorCapture.get().contains("Tên danh mục đã tồn tại"));
    }

    @Test
    @DisplayName("Ném NullPointerException khi tham số bắt buộc bị null")
    void testNullArgsThrowsException() {
        assertThrows(NullPointerException.class, () -> new UpdateCategoryDialog(
                (JFrame) null, null, categoryId, "Ăn uống", group1Id, "EXPENSE"));
        assertThrows(NullPointerException.class, () -> new UpdateCategoryDialog(
                (JFrame) null, categoryController, null, "Ăn uống", group1Id, "EXPENSE"));
    }
}
