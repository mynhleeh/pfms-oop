package com.pfms.presentation.view.category.dialog;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.JFrame;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử CreateNeedGroupDialog - Hộp thoại tạo nhóm nhu cầu mới")
class CreateNeedGroupDialogTest {

    private CategoryGUIController categoryController;
    private CreateNeedGroupDialog dialog;

    @BeforeEach
    void setUp() {
        categoryController = Mockito.mock(CategoryGUIController.class);
        dialog = new CreateNeedGroupDialog((JFrame) null, categoryController);
        dialog.setErrorConsumer(msg -> {});
    }

    @Test
    @DisplayName("Khởi tạo hộp thoại có các trường rỗng và cờ thành công ban đầu là false")
    void testInitialDialogState() {
        assertFalse(dialog.isSuccess());
        assertNull(dialog.getCreatedNeedGroup());
        assertEquals("", dialog.getNameInputValue());
        assertEquals("EXPENSE", dialog.getTypeInputValue());
    }

    @Test
    @DisplayName("Lưu nhóm nhu cầu thành công khi nhập dữ liệu hợp lệ")
    void testSaveNeedGroupSuccess() {
        NeedGroupResponseDTO response = new NeedGroupResponseDTO(
                UUID.randomUUID(), "Chi tiêu thiết yếu", "EXPENSE", "ACTIVE", "cart", "#FF5722", LocalDateTime.now());

        when(categoryController.handleCreateNeedGroup("EXPENSE", "Chi tiêu thiết yếu", "cart", "#FF5722"))
                .thenReturn(response);

        dialog.setTypeInputValue("EXPENSE");
        dialog.setNameInputValue("Chi tiêu thiết yếu");
        dialog.setIconInputValue("cart");
        dialog.setColorInputValue("#FF5722");
        dialog.triggerSave();

        assertTrue(dialog.isSuccess());
        assertSame(response, dialog.getCreatedNeedGroup());
        verify(categoryController).handleCreateNeedGroup("EXPENSE", "Chi tiêu thiết yếu", "cart", "#FF5722");
    }

    @Test
    @DisplayName("Bắt giữ ngoại lệ nghiệp vụ khi tên nhóm nhu cầu bị trùng lặp")
    void testSaveNeedGroupDuplicateExceptionHandled() {
        when(categoryController.handleCreateNeedGroup("EXPENSE", "Đã có", "", ""))
                .thenThrow(new BusinessException(ErrorCode.ERR_NAM_002, "Nhóm nhu cầu 'Đã có' đã tồn tại"));

        AtomicReference<String> errorCapture = new AtomicReference<>();
        dialog.setErrorConsumer(errorCapture::set);

        dialog.setTypeInputValue("EXPENSE");
        dialog.setNameInputValue("Đã có");
        dialog.triggerSave();

        assertFalse(dialog.isSuccess());
        assertNull(dialog.getCreatedNeedGroup());
        assertNotNull(errorCapture.get());
        assertTrue(errorCapture.get().contains("đã tồn tại"));
    }

    @Test
    @DisplayName("Ném NullPointerException khi CategoryGUIController bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new CreateNeedGroupDialog((JFrame) null, null));
    }
}
