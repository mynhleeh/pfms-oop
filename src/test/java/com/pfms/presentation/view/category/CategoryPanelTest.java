package com.pfms.presentation.view.category;

import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử CategoryPanel - Bảng điều khiển phân hệ quản lý danh mục thu chi")
class CategoryPanelTest {

    private CategoryGUIController categoryController;
    private CategoryPanel panel;

    private UUID group1Id;
    private UUID group2Id;

    @BeforeEach
    void setUp() {
        categoryController = Mockito.mock(CategoryGUIController.class);

        group1Id = UUID.randomUUID();
        CategoryResponseDTO cat1 = new CategoryResponseDTO(
                UUID.randomUUID(), "Ăn uống", group1Id, "Thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO cat2 = new CategoryResponseDTO(
                UUID.randomUUID(), "Đi lại", group1Id, "Thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());

        CategoryHierarchyResponseDTO group1 = new CategoryHierarchyResponseDTO(
                "EXPENSE", group1Id, "Thiết yếu", "ACTIVE", List.of(cat1, cat2));

        group2Id = UUID.randomUUID();
        CategoryResponseDTO cat3 = new CategoryResponseDTO(
                UUID.randomUUID(), "Tiền lương", group2Id, "Thu nhập chính", "INCOME", "ACTIVE", LocalDateTime.now());

        CategoryHierarchyResponseDTO group2 = new CategoryHierarchyResponseDTO(
                "INCOME", group2Id, "Thu nhập chính", "ACTIVE", List.of(cat3));

        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(group1, group2));

        panel = new CategoryPanel(categoryController);
        panel.setMessageConsumer(msg -> {});
        panel.setErrorConsumer(msg -> {});
        panel.setWarningConsumer(msg -> {});
    }

    @Test
    @DisplayName("Khởi tạo bảng điều khiển nạp sẵn cây danh mục và nhãn tóm tắt chính xác")
    void testInitialDataLoaded() {
        assertNotNull(panel.getTree());
        assertNotNull(panel.getTable());
        assertTrue(panel.getSummaryText().contains("Tổng số nhóm: 2"));
        assertTrue(panel.getSummaryText().contains("Tổng số danh mục: 3"));
    }

    @Test
    @DisplayName("Làm mới danh mục gọi lại CategoryGUIController")
    void testReloadCategoriesCallsController() {
        panel.reloadCategories();
        verify(categoryController, atLeastOnce()).loadCategoryHierarchy();
    }

    @Test
    @DisplayName("Kích hoạt callback onCategoryChanged khi làm mới danh mục")
    void testCategoryChangedCallback() {
        AtomicBoolean triggered = new AtomicBoolean(false);
        panel.setOnCategoryChangedListener(() -> triggered.set(true));

        panel.reloadCategories();
        assertTrue(triggered.get());
    }

    @Test
    @DisplayName("Ném NullPointerException khi CategoryGUIController bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new CategoryPanel(null));
    }
}
