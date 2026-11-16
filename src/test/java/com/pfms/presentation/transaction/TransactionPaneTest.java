package com.pfms.presentation.transaction;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.transaction.viewmodel.TransactionItemViewModel;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử TransactionPane - Bảng điều khiển phân hệ Giao dịch JavaFX")
class TransactionPaneTest {

    private TransactionGUIController transactionController;
    private WalletGUIController walletController;
    private CategoryGUIController categoryController;

    private TransactionResponseDTO txnExpense;
    private TransactionResponseDTO txnIncome;
    private TransactionListResponseDTO listResponse;
    private TransactionPane pane;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        transactionController = Mockito.mock(TransactionGUIController.class);
        walletController = Mockito.mock(WalletGUIController.class);
        categoryController = Mockito.mock(CategoryGUIController.class);

        UUID walletId = UUID.randomUUID();
        WalletResponseDTO w1 = new WalletResponseDTO(
                walletId, "Ví Tiền mặt", new BigDecimal("5000000"), "VND", true, "ACTIVE", "Tiền mặt", LocalDateTime.now());

        when(walletController.loadWalletsSummary()).thenReturn(
                new WalletsSummaryResponseDTO(List.of(w1), new BigDecimal("5000000"), "VND", Collections.emptyMap()));

        UUID catId1 = UUID.randomUUID();
        CategoryResponseDTO cat1 = new CategoryResponseDTO(
                catId1, "Ăn uống", UUID.randomUUID(), "Thiết yếu", "EXPENSE", "ACTIVE");
        CategoryHierarchyResponseDTO group = new CategoryHierarchyResponseDTO(
                "EXPENSE", UUID.randomUUID(), "Thiết yếu", "ACTIVE", List.of(cat1));
        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(group));

        txnExpense = new TransactionResponseDTO(
                UUID.randomUUID(), walletId, "Ví Tiền mặt",
                catId1, "Ăn uống", new BigDecimal("150000"),
                "EXPENSE", LocalDate.now(), "Ăn trưa", new BigDecimal("4850000"), LocalDateTime.now());

        txnIncome = new TransactionResponseDTO(
                UUID.randomUUID(), walletId, "Ví Tiền mặt",
                catId1, "Thưởng", new BigDecimal("2000000"),
                "INCOME", LocalDate.now(), "Thưởng dự án", new BigDecimal("6850000"), LocalDateTime.now());

        listResponse = new TransactionListResponseDTO(
                List.of(txnExpense, txnIncome), new BigDecimal("2000000"), new BigDecimal("150000"), new BigDecimal("1850000"), 1, 20, 2L, 1);

        when(transactionController.loadTransactions(any())).thenReturn(listResponse);

        JavaFxTestHelper.runOnFxThread(() -> {
            pane = new TransactionPane(transactionController, walletController, categoryController);
        });
    }

    @Test
    @DisplayName("Khởi tạo TransactionPane nạp chính xác dữ liệu giao dịch vào TableView và KPI cards")
    void testInitialDataLoaded() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(pane.getTableView());
            assertEquals(2, pane.getTableData().size());

            TransactionItemViewModel item1 = pane.getTableData().get(0);
            assertFalse(item1.isIncome());
            assertEquals("Ăn uống", item1.categoryName());
            assertEquals("Ví Tiền mặt", item1.walletName());

            TransactionItemViewModel item2 = pane.getTableData().get(1);
            assertTrue(item2.isIncome());

            assertTrue(pane.getSummaryText().contains("Tổng thu: 2,000,000 VNĐ"));
            assertTrue(pane.getSummaryText().contains("Tổng chi: 150,000 VNĐ"));
            assertTrue(pane.getSummaryText().contains("Dòng tiền thuần: 1,850,000 VNĐ"));
            assertTrue(pane.getPageInfoText().contains("Trang 1 / 1 (Tổng 2 giao dịch)"));

            assertTrue(pane.getStatTotalIncomeLabel().getText().contains("2,000,000 VNĐ"));
            assertTrue(pane.getStatTotalExpenseLabel().getText().contains("150,000 VNĐ"));
            assertTrue(pane.getStatNetCashFlowLabel().getText().contains("1,850,000 VNĐ"));

            assertNotNull(pane.getFilterBar());
            assertNotNull(pane.getPresenter());

            assertEquals(7, pane.getTableView().getColumns().size());
            assertEquals("Ngày Giao Dịch", pane.getTableView().getColumns().get(0).getText());
            assertEquals("Số Tiền", pane.getTableView().getColumns().get(1).getText());
            assertEquals("Ví Tài Khoản", pane.getTableView().getColumns().get(2).getText());
            assertEquals("Nhóm Nhu Cầu", pane.getTableView().getColumns().get(3).getText());
            assertEquals("Danh Mục", pane.getTableView().getColumns().get(4).getText());
            assertEquals("Ghi Chú", pane.getTableView().getColumns().get(5).getText());
            assertEquals("Thao Tác", pane.getTableView().getColumns().get(6).getText());

            assertNotNull(pane.getStreamHeader());
            assertNotNull(pane.getStreamItemsContainer());
            assertEquals(2, pane.getStreamItemsContainer().getChildren().size());
        });
    }

    @Test
    @DisplayName("renderTransactions cập nhật chính xác danh sách giao dịch mới và các nhãn tóm tắt")
    void testRenderTransactions() {
        TransactionItemViewModel customItem = new TransactionItemViewModel(
                UUID.randomUUID(), 1, "16/11/2026", "Tiền điện", "Ví Tiền mặt",
                "500,000 ₫", false, "Hóa đơn điện", "4,350,000 ₫");

        JavaFxTestHelper.runOnFxThread(() -> {
            pane.renderTransactions(
                    List.of(customItem),
                    "Tổng thu: 0 VNĐ | Tổng chi: 500,000 VNĐ | Dòng tiền thuần: -500,000 VNĐ",
                    "Trang 1 / 1 (Tổng 1 giao dịch)");

            assertEquals(1, pane.getTableData().size());
            assertEquals("Tiền điện", pane.getTableData().get(0).categoryName());
            assertFalse(pane.getTableData().get(0).isIncome());

            assertEquals(1, pane.getStreamItemsContainer().getChildren().size());
            assertTrue(pane.getStreamItemsContainer().getChildren().get(0) instanceof com.pfms.presentation.transaction.component.TransactionRowNode);

            assertTrue(pane.getStatTotalIncomeLabel().getText().contains("0 VNĐ"));
            assertTrue(pane.getStatTotalExpenseLabel().getText().contains("500,000 VNĐ"));
            assertTrue(pane.getStatNetCashFlowLabel().getText().contains("-500,000 VNĐ"));
        });
    }

    @Test
    @DisplayName("renderTransactions với danh sách rỗng hiển thị thẻ thông báo rỗng phù hợp")
    void testRenderEmptyTransactions() {
        JavaFxTestHelper.runOnFxThread(() -> {
            pane.renderTransactions(
                    List.of(),
                    "Tổng thu: 0 VNĐ | Tổng chi: 0 VNĐ | Dòng tiền thuần: 0 VNĐ",
                    "Trang 1 / 1 (Tổng 0 giao dịch)");
            assertEquals(0, pane.getTableData().size());
            assertEquals(1, pane.getStreamItemsContainer().getChildren().size());
        });
    }

    @Test
    @DisplayName("setSelectedTypeFilter chuyển tiếp chính xác đến thanh lọc filterBar")
    void testSetSelectedTypeFilter() {
        JavaFxTestHelper.runOnFxThread(() -> {
            pane.setSelectedTypeFilter("INCOME");
            assertEquals("INCOME", pane.getFilterBar().getTypeFilterBox().getValue());

            pane.setSelectedTypeFilter("EXPENSE");
            assertEquals("EXPENSE", pane.getFilterBar().getTypeFilterBox().getValue());
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

        pane.showMessage("Ghi nhận thành công");
        assertEquals("Ghi nhận thành công", msgRef.get());

        pane.showWarning("Khoản chi vượt hạn mức");
        assertEquals("Khoản chi vượt hạn mức", warnRef.get());

        pane.showError("Lỗi cập nhật");
        assertEquals("Lỗi cập nhật", errRef.get());
    }
}
