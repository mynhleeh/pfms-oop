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

@DisplayName("Kiểm thử TransactionFilterToolbar - Thanh công cụ lọc giao dịch")
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
        assertEquals("Tất cả danh mục", toolbar.getCategoryFilterBox().getSelectedItem());
        assertEquals("", toolbar.getStartDateField().getText());
        assertEquals("", toolbar.getEndDateField().getText());
        assertEquals("", toolbar.getKeywordField().getText());
    }

    @Test
    @DisplayName("Nạp danh sách ví và danh mục thành công")
    void testPopulateWalletsAndCategories() {
        UUID walletId = UUID.randomUUID();
        WalletResponseDTO wallet = new WalletResponseDTO(
                walletId, "Ví Tiết Kiệm", new BigDecimal("10000000"), "VND", false, "ACTIVE", "Tiết kiệm", LocalDateTime.now());

        UUID categoryId = UUID.randomUUID();
        CategoryResponseDTO category = new CategoryResponseDTO(
                categoryId, "Ăn uống", UUID.randomUUID(), "Chi tiêu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());

        toolbar.populateWallets(List.of(wallet));
        toolbar.populateCategories(List.of(category));

        assertEquals(2, toolbar.getWalletFilterBox().getItemCount());
        assertEquals(2, toolbar.getCategoryFilterBox().getItemCount());
        assertEquals(wallet, toolbar.getWalletFilterBox().getItemAt(1));
        assertEquals(category, toolbar.getCategoryFilterBox().getItemAt(1));
    }

    @Test
    @DisplayName("Tạo FilterTransactionsRequestDTO chính xác khi chọn ví, danh mục và ngày")
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
