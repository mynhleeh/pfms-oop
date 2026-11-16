package com.pfms.presentation.transaction.component;

import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử TransactionFilterToolbar - Thanh công cụ lọc giao dịch 2 tầng")
class TransactionFilterToolbarTest {

    private TransactionFilterToolbar toolbar;
    private AtomicBoolean filterTriggered;
    private AtomicBoolean resetTriggered;

    @BeforeEach
    void setUp() {
        filterTriggered = new AtomicBoolean(false);
        resetTriggered = new AtomicBoolean(false);
        toolbar = new TransactionFilterToolbar(
                () -> filterTriggered.set(true),
                () -> resetTriggered.set(true)
        );
    }

    @Test
    @DisplayName("Khởi tạo mặc định đầy đủ các trường lọc và nhãn mặc định")
    void testInitialState() {
        assertEquals("Tất cả", toolbar.getTypeFilterBox().getSelectedItem());
        assertEquals("Tất cả ví", toolbar.getWalletFilterBox().getSelectedItem());
        assertEquals("Tất cả nhóm", toolbar.getGroupFilterBox().getSelectedItem());
        assertEquals("Tất cả danh mục", toolbar.getCategoryFilterBox().getSelectedItem());
        assertEquals("", toolbar.getStartDateField().getText());
        assertEquals("", toolbar.getEndDateField().getText());
        assertEquals("", toolbar.getKeywordField().getText());
    }

    @Test
    @DisplayName("Nạp danh sách ví và danh mục thành công, tự động trích xuất các nhóm nhu cầu")
    void testPopulateWalletsAndCategories() {
        UUID walletId = UUID.randomUUID();
        WalletResponseDTO wallet = new WalletResponseDTO(
                walletId, "Ví Tiết Kiệm", new BigDecimal("10000000"), "VND", false, "ACTIVE", "Tiết kiệm", LocalDateTime.now());

        UUID categoryId1 = UUID.randomUUID();
        CategoryResponseDTO category1 = new CategoryResponseDTO(
                categoryId1, "Ăn uống", UUID.randomUUID(), "Chi tiêu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());

        UUID categoryId2 = UUID.randomUUID();
        CategoryResponseDTO category2 = new CategoryResponseDTO(
                categoryId2, "Lương", UUID.randomUUID(), "Thu nhập cố định", "INCOME", "ACTIVE", LocalDateTime.now());

        toolbar.populateWallets(List.of(wallet));
        toolbar.populateCategories(List.of(category1, category2));

        assertEquals(2, toolbar.getWalletFilterBox().getItemCount());
        assertEquals(3, toolbar.getGroupFilterBox().getItemCount()); // "Tất cả nhóm", "Chi tiêu thiết yếu", "Thu nhập cố định"
        assertEquals(3, toolbar.getCategoryFilterBox().getItemCount()); // "Tất cả danh mục", "Ăn uống", "Lương"
        assertEquals(wallet, toolbar.getWalletFilterBox().getItemAt(1));
    }

    @Test
    @DisplayName("Tạo FilterTransactionsRequestDTO chính xác khi chọn ví, danh mục cụ thể và ngày")
    void testBuildFilterRequest() {
        UUID walletId = UUID.randomUUID();
        WalletResponseDTO wallet = new WalletResponseDTO(
                walletId, "Ví MB Bank", new BigDecimal("5000000"), "VND", false, "ACTIVE", "Giao dịch", LocalDateTime.now());

        UUID categoryId = UUID.randomUUID();
        CategoryResponseDTO category = new CategoryResponseDTO(
                categoryId, "Đi lại", UUID.randomUUID(), "Chi tiêu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());

        toolbar.populateWallets(List.of(wallet));
        toolbar.populateCategories(List.of(category));

        toolbar.getTypeFilterBox().setSelectedItem("EXPENSE");
        toolbar.getWalletFilterBox().setSelectedItem(wallet);
        toolbar.getCategoryFilterBox().setSelectedItem(category);
        toolbar.getStartDateField().setText("01/01/2026");
        toolbar.getEndDateField().setText("31/01/2026");
        toolbar.getKeywordField().setText("Xăng xe");

        FilterTransactionsRequestDTO request = toolbar.buildFilterRequest(1, 20);

        assertEquals("EXPENSE", request.getTransactionType());
        assertNotNull(request.getWalletIds());
        assertTrue(request.getWalletIds().contains(walletId));
        assertNotNull(request.getCategoryIds());
        assertTrue(request.getCategoryIds().contains(categoryId));
        assertEquals(LocalDate.of(2026, 1, 1), request.getStartDate());
        assertEquals(LocalDate.of(2026, 1, 31), request.getEndDate());
        assertEquals("Xăng xe", request.getKeyword());
        assertEquals(1, request.getPageNumber());
        assertEquals(20, request.getPageSize());
    }

    @Test
    @DisplayName("Lọc theo nhóm tự động thu thập toàn bộ categoryId thuộc nhóm đó khi để Tất cả danh mục")
    void testFilterByGroupCollectsAllCategoryIdsInGroup() {
        UUID cat1 = UUID.randomUUID();
        CategoryResponseDTO c1 = new CategoryResponseDTO(
                cat1, "Ăn uống", UUID.randomUUID(), "Chi tiêu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        UUID cat2 = UUID.randomUUID();
        CategoryResponseDTO c2 = new CategoryResponseDTO(
                cat2, "Đi lại", UUID.randomUUID(), "Chi tiêu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        UUID cat3 = UUID.randomUUID();
        CategoryResponseDTO c3 = new CategoryResponseDTO(
                cat3, "Du lịch", UUID.randomUUID(), "Chi tiêu tùy ý", "EXPENSE", "ACTIVE", LocalDateTime.now());

        toolbar.populateCategories(List.of(c1, c2, c3));

        toolbar.getGroupFilterBox().setSelectedItem("Chi tiêu thiết yếu");
        FilterTransactionsRequestDTO request = toolbar.buildFilterRequest(1, 20);

        assertNotNull(request.getCategoryIds());
        assertEquals(2, request.getCategoryIds().size());
        assertTrue(request.getCategoryIds().contains(cat1));
        assertTrue(request.getCategoryIds().contains(cat2));
        assertFalse(request.getCategoryIds().contains(cat3));
    }

    @Test
    @DisplayName("Thao tác chọn thẻ ngày nhanh (setQuickDate) cập nhật chính xác ngày và kích hoạt lọc")
    void testQuickDateOptions() {
        filterTriggered.set(false);
        toolbar.setQuickDate("MONTH");

        assertTrue(filterTriggered.get());
        assertFalse(toolbar.getStartDateField().getText().isEmpty());
        assertFalse(toolbar.getEndDateField().getText().isEmpty());

        filterTriggered.set(false);
        toolbar.setQuickDate("ALL");
        assertTrue(filterTriggered.get());
        assertEquals("", toolbar.getStartDateField().getText());
        assertEquals("", toolbar.getEndDateField().getText());
    }

    @Test
    @DisplayName("Đặt lại bộ lọc đưa mọi trường về giá trị mặc định")
    void testResetFilters() {
        toolbar.getTypeFilterBox().setSelectedItem("INCOME");
        toolbar.getStartDateField().setText("01/01/2026");
        toolbar.getEndDateField().setText("31/01/2026");
        toolbar.getKeywordField().setText("Lương");

        toolbar.getResetButton().doClick();

        assertTrue(resetTriggered.get());
        assertEquals("Tất cả", toolbar.getTypeFilterBox().getSelectedItem());
        assertEquals("Tất cả ví", toolbar.getWalletFilterBox().getSelectedItem());
        assertEquals("Tất cả nhóm", toolbar.getGroupFilterBox().getSelectedItem());
        assertEquals("Tất cả danh mục", toolbar.getCategoryFilterBox().getSelectedItem());
        assertEquals("", toolbar.getStartDateField().getText());
        assertEquals("", toolbar.getEndDateField().getText());
        assertEquals("", toolbar.getKeywordField().getText());
    }

    @Test
    @DisplayName("Nhấn nút Lọc kích hoạt callback onFilterAction")
    void testFilterButtonClickTriggersAction() {
        filterTriggered.set(false);
        toolbar.getFilterButton().doClick();
        assertTrue(filterTriggered.get());
    }
}
