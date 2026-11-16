package com.pfms.presentation.category;

import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.testutil.JavaFxTestHelper;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử CategoryPane - Cây danh mục và nhóm nhu cầu JavaFX")
class CategoryPaneTest {

    private CategoryGUIController categoryController;
    private CategoryHierarchyResponseDTO groupExpense;
    private CategoryHierarchyResponseDTO groupIncome;
    private CategoryPane pane;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        categoryController = Mockito.mock(CategoryGUIController.class);

        UUID groupId1 = UUID.randomUUID();
        UUID catId1 = UUID.randomUUID();
        UUID catId2 = UUID.randomUUID();
        CategoryResponseDTO cat1 = new CategoryResponseDTO(
                catId1, "Ăn uống", groupId1, "Thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO cat2 = new CategoryResponseDTO(
                catId2, "Đi lại", groupId1, "Thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());

        groupExpense = new CategoryHierarchyResponseDTO(
                "EXPENSE", groupId1, "Thiết yếu", "ACTIVE", List.of(cat1, cat2));

        UUID groupId2 = UUID.randomUUID();
        UUID catId3 = UUID.randomUUID();
        CategoryResponseDTO cat3 = new CategoryResponseDTO(
                catId3, "Tiền lương", groupId2, "Thu nhập chính", "INCOME", "ACTIVE", LocalDateTime.now());

        groupIncome = new CategoryHierarchyResponseDTO(
                "INCOME", groupId2, "Thu nhập chính", "ACTIVE", List.of(cat3));

        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(groupExpense, groupIncome));

        JavaFxTestHelper.runOnFxThread(() -> {
            pane = new CategoryPane(categoryController);
        });
    }

    @Test
    @DisplayName("Khởi tạo CategoryPane nạp dữ liệu phân cấp chính xác vào TreeTableView")
    void testInitialDataLoaded() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(pane.getTreeTableView());
            TreeItem<CategoryPane.CategoryHierarchyItem> root = pane.getTreeTableView().getRoot();
            assertNotNull(root);
            assertEquals(2, root.getChildren().size());

            TreeItem<CategoryPane.CategoryHierarchyItem> node1 = root.getChildren().get(0);
            assertEquals("Thiết yếu", node1.getValue().name());
            assertTrue(node1.getValue().isGroup());
            assertEquals(2, node1.getChildren().size());
            assertEquals("Ăn uống", node1.getChildren().get(0).getValue().name());
            assertFalse(node1.getChildren().get(0).getValue().isGroup());

            TreeItem<CategoryPane.CategoryHierarchyItem> node2 = root.getChildren().get(1);
            assertEquals("Thu nhập chính", node2.getValue().name());
            assertEquals(1, node2.getChildren().size());

            assertTrue(pane.getSummaryText().contains("Tổng số: 2 nhóm nhu cầu | 3 danh mục chi tiết"));
        });
    }

    @Test
    @DisplayName("renderCategoryHierarchy cập nhật chính xác cấu trúc phân cấp mới")
    void testRenderCategoryHierarchy() {
        UUID newGroupId = UUID.randomUUID();
        CategoryHierarchyResponseDTO customGroup = new CategoryHierarchyResponseDTO(
                "EXPENSE", newGroupId, "Đầu tư", "ACTIVE", List.of());

        JavaFxTestHelper.runOnFxThread(() -> {
            pane.renderCategoryHierarchy(List.of(customGroup), 1, 0);

            TreeItem<CategoryPane.CategoryHierarchyItem> root = pane.getTreeTableView().getRoot();
            assertEquals(1, root.getChildren().size());
            assertEquals("Đầu tư", root.getChildren().get(0).getValue().name());
            assertTrue(pane.getSummaryText().contains("1 nhóm nhu cầu | 0 danh mục"));
        });
    }

    @Test
    @DisplayName("Các hàm showMessage, showWarning, showError chuyển tiếp chính xác đến consumer")
    void testNotificationConsumers() {
        AtomicReference<String> msgRef = new AtomicReference<>();
        AtomicReference<String> warnRef = new AtomicReference<>();
        AtomicReference<String> errRef = new AtomicReference<>();

        pane.setMessageConsumer(msgRef::set);
        pane.setWarningConsumer(warnRef::set);
        pane.setErrorConsumer(errRef::set);

        pane.showMessage("Tạo thành công");
        assertEquals("Tạo thành công", msgRef.get());

        pane.showWarning("Cảnh báo tên trùng");
        assertEquals("Cảnh báo tên trùng", warnRef.get());

        pane.showError("Lỗi hệ thống");
        assertEquals("Lỗi hệ thống", errRef.get());
    }

    @Test
    @DisplayName("Presenter được gán hợp lệ và liên kết chính xác với view")
    void testPresenterAttached() {
        assertNotNull(pane.getPresenter());
    }

    @Test
    @DisplayName("Nút làm mới khởi tạo đúng text, style và kích hoạt presenter.loadData()")
    void testRefreshButtonTriggersLoadData() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(pane.getBtnRefresh());
            assertEquals("Làm mới", pane.getBtnRefresh().getText());
            assertTrue(pane.getBtnRefresh().getStyleClass().contains("btn-secondary"));

            pane.getBtnRefresh().fire();
            Mockito.verify(categoryController, Mockito.atLeast(2)).loadCategoryHierarchy();
        });
    }
}
