package com.pfms.presentation.transaction.component;

import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
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

@DisplayName("Kiểm thử TransactionFilterBar - Thanh công cụ lọc giao dịch JavaFX 2 tầng")
class TransactionFilterBarTest {

    private TransactionFilterBar filterBar;
    private AtomicBoolean filterTriggered;
    private AtomicBoolean resetTriggered;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        filterTriggered = new AtomicBoolean(false);
        resetTriggered = new AtomicBoolean(false);
        JavaFxTestHelper.runOnFxThread(() -> {
            filterBar = new TransactionFilterBar(
                    () -> filterTriggered.set(true),
                    () -> resetTriggered.set(true)
            );
        });
    }

    @Test
    @DisplayName("Khởi tạo mặc định đầy đủ các trường lọc và nhãn mặc định")
    void testInitialState() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertEquals("Tất cả", filterBar.getTypeFilterBox().getValue());
            assertEquals("Tất cả ví", filterBar.getWalletFilterBox().getValue());
            assertEquals("Tất cả nhóm", filterBar.getGroupFilterBox().getValue());
            assertEquals("Tất cả danh mục", filterBar.getCategoryFilterBox().getValue());
            assertNull(filterBar.getStartDatePicker().getValue());
            assertNull(filterBar.getEndDatePicker().getValue());
            assertEquals("", filterBar.getKeywordField().getText());
            assertEquals("ALL", filterBar.getCurrentQuickTag());
        });
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

        JavaFxTestHelper.runOnFxThread(() -> {
            filterBar.populateWallets(List.of(wallet));
            filterBar.populateCategories(List.of(category1, category2));

            assertEquals(2, filterBar.getWalletFilterBox().getItems().size());
            assertEquals(3, filterBar.getGroupFilterBox().getItems().size()); // "Tất cả nhóm", "Chi tiêu thiết yếu", "Thu nhập cố định"
            assertEquals(3, filterBar.getCategoryFilterBox().getItems().size()); // "Tất cả danh mục", "Ăn uống", "Lương"
            assertEquals(wallet, filterBar.getWalletFilterBox().getItems().get(1));
        });
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

        JavaFxTestHelper.runOnFxThread(() -> {
            filterBar.populateWallets(List.of(wallet));
            filterBar.populateCategories(List.of(category));

            filterBar.getTypeFilterBox().setValue("EXPENSE");
            filterBar.getWalletFilterBox().setValue(wallet);
            filterBar.getCategoryFilterBox().setValue(category);
            filterBar.getStartDatePicker().setValue(LocalDate.of(2026, 1, 1));
            filterBar.getEndDatePicker().setValue(LocalDate.of(2026, 1, 31));
            filterBar.getKeywordField().setText("Xăng xe");

            FilterTransactionsRequestDTO request = filterBar.buildFilterRequest(1, 20);

            assertEquals("EXPENSE", request.getTransactionType());
            assertNotNull(request.getWalletIds());
            assertTrue(request.getWalletIds().contains(walletId));
            assertNotNull(request.getCategoryIds());
            assertTrue(request.getCategoryIds().contains(categoryId));
            assertEquals(LocalDate.of(2026, 1, 1), request.getStartDate());
            assertEquals(LocalDate.of(2026, 1, 31), request.getEndDate());
            assertEquals("Xăng xe", request.getKeyword());
        });
    }

    @Test
    @DisplayName("Đặt lại bộ lọc (Reset) đưa toàn bộ trạng thái về ban đầu")
    void testResetFilters() {
        JavaFxTestHelper.runOnFxThread(() -> {
            filterBar.getTypeFilterBox().setValue("INCOME");
            filterBar.getKeywordField().setText("Thưởng");
            filterBar.getStartDatePicker().setValue(LocalDate.of(2026, 1, 1));

            filterBar.getResetButton().fire();

            assertEquals("Tất cả", filterBar.getTypeFilterBox().getValue());
            assertEquals("", filterBar.getKeywordField().getText());
            assertNull(filterBar.getStartDatePicker().getValue());
            assertTrue(resetTriggered.get());
        });
    }

    @Test
    @DisplayName("Chọn thẻ ngày nhanh cập nhật chính xác ngày bắt đầu và kết thúc")
    void testQuickDateTags() {
        JavaFxTestHelper.runOnFxThread(() -> {
            filterBar.setQuickDate("MONTH");
            LocalDate today = LocalDate.now();
            assertEquals(today.withDayOfMonth(1), filterBar.getStartDatePicker().getValue());
            assertEquals(today, filterBar.getEndDatePicker().getValue());

            filterBar.setQuickDate("ALL");
            assertNull(filterBar.getStartDatePicker().getValue());
            assertNull(filterBar.getEndDatePicker().getValue());
        });
    }
}
