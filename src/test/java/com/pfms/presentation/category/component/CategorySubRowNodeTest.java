package com.pfms.presentation.category.component;

import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử thành phần hiển thị dòng danh mục con (CategorySubRowNode)")
class CategorySubRowNodeTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Khởi tạo dòng danh mục con hiển thị chính xác tên, mô tả và trạng thái")
    void testSubCategoryRowInit() {
        JavaFxTestHelper.runOnFxThread(() -> {
            CategoryResponseDTO cat = new CategoryResponseDTO(
                    UUID.randomUUID(), "Ăn Uống Gia Đình", UUID.randomUUID(), "Thiết Yếu",
                    "EXPENSE", "ACTIVE"
            );

            CategorySubRowNode node = new CategorySubRowNode(cat, null, null);

            assertEquals("└", node.getIndentLabel().getText());
            assertEquals("Ăn Uống Gia Đình", node.getNameLabel().getText());
            assertEquals("Thiết Yếu", node.getNoteLabel().getText());
            assertEquals("ACTIVE", node.getStatusBadge().getText());
            assertEquals(48.0, node.getBtnEdit().getPrefWidth());
            assertEquals(48.0, node.getBtnDelete().getPrefWidth());
        });
    }

    @Test
    @DisplayName("Nút Sửa và Nút Xóa kích hoạt callback chính xác")
    void testActionButtonsTriggerCallbacks() {
        JavaFxTestHelper.runOnFxThread(() -> {
            AtomicBoolean editCalled = new AtomicBoolean(false);
            AtomicBoolean deleteCalled = new AtomicBoolean(false);

            CategoryResponseDTO cat = new CategoryResponseDTO(
                    UUID.randomUUID(), "Đi lại", UUID.randomUUID(), "",
                    "EXPENSE", "ACTIVE"
            );

            CategorySubRowNode node = new CategorySubRowNode(
                    cat,
                    c -> editCalled.set(true),
                    c -> deleteCalled.set(true)
            );

            assertEquals("—", node.getNoteLabel().getText());

            node.getBtnEdit().fire();
            assertTrue(editCalled.get());

            node.getBtnDelete().fire();
            assertTrue(deleteCalled.get());
        });
    }
}
