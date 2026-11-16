package com.pfms.presentation.dashboard;

import com.pfms.presentation.dashboard.viewmodel.RecentTransactionViewModel;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.report.ReportGUIController;
import com.pfms.presentation.transaction.TransactionGUIController;
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
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử DashboardPane - Bảng điều khiển tổng quan tài chính JavaFX")
class DashboardPaneTest {

    private WalletGUIController walletController;
    private TransactionGUIController transactionController;
    private ReportGUIController reportController;

    private DashboardPane dashboardPane;

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        walletController = Mockito.mock(WalletGUIController.class);
        transactionController = Mockito.mock(TransactionGUIController.class);
        reportController = Mockito.mock(ReportGUIController.class);

        WalletsSummaryResponseDTO emptySummary = new WalletsSummaryResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, "VNĐ", Collections.emptyMap());
        when(walletController.getWalletsSummary()).thenReturn(emptySummary);
        when(transactionController.filterTransactions(any())).thenReturn(new TransactionListResponseDTO());

        JavaFxTestHelper.runOnFxThread(() -> {
            dashboardPane = new DashboardPane(walletController, transactionController, reportController);
            dashboardPane.setMessageConsumer(msg -> {});
            dashboardPane.setErrorConsumer(msg -> {});
            dashboardPane.setWarningConsumer(msg -> {});
            dashboardPane.setConfirmConsumer(msg -> true);
        });
    }

    @Test
    @DisplayName("Ném NullPointerException khi các controller bị null trong constructor")
    void testConstructorNullValidation() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertThrows(NullPointerException.class, () -> new DashboardPane(null, transactionController, reportController));
            assertThrows(NullPointerException.class, () -> new DashboardPane(walletController, null, reportController));
            assertThrows(NullPointerException.class, () -> new DashboardPane(walletController, transactionController, null));
        });
    }

    @Test
    @DisplayName("Khởi tạo bảng điều khiển với tiêu đề và chu kỳ tài chính tháng hiện tại")
    void testInitialStylingAndHeader() {
        assertNotNull(dashboardPane.getHeaderTitleLabel());
        assertEquals("Bảng Điều Khiển Tổng Quan", dashboardPane.getHeaderTitleLabel().getText());

        String currentCycle = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        assertNotNull(dashboardPane.getCycleSubtitleLabel());
        assertTrue(dashboardPane.getCycleSubtitleLabel().getText().contains(currentCycle));
    }

    @Test
    @DisplayName("4 Thẻ KPI Card có các nhãn giá trị và phụ đề đầy đủ")
    void testKpiCardsInitialValues() {
        assertNotNull(dashboardPane.getTotalBalanceValueLabel());
        assertNotNull(dashboardPane.getIncomeValueLabel());
        assertNotNull(dashboardPane.getExpenseValueLabel());
        assertNotNull(dashboardPane.getSavingsRateValueLabel());

        assertNotNull(dashboardPane.getTotalBalanceSubtextLabel());
        assertNotNull(dashboardPane.getIncomeSubtextLabel());
        assertNotNull(dashboardPane.getExpenseSubtextLabel());
        assertNotNull(dashboardPane.getSavingsRateSubtextLabel());
    }

    @Test
    @DisplayName("Bảng giao dịch gần nhất cấu hình đúng 5 cột hiển thị")
    void testRecentTableStructure() {
        var table = dashboardPane.getRecentTable();
        assertNotNull(table);
        assertEquals(5, table.getColumns().size());
        assertEquals("THỜI GIAN", table.getColumns().get(0).getText());
        assertEquals("VÍ TÀI CHÍNH", table.getColumns().get(1).getText());
        assertEquals("DANH MỤC", table.getColumns().get(2).getText());
        assertEquals("GHI CHÚ", table.getColumns().get(3).getText());
        assertEquals("SỐ TIỀN (VNĐ)", table.getColumns().get(4).getText());
    }

    @Test
    @DisplayName("reloadDashboard cập nhật chính xác số liệu tổng hợp 4 thẻ KPI và bảng giao dịch")
    void testReloadDashboardWithData() {
        UUID wallet1Id = UUID.randomUUID();
        UUID wallet2Id = UUID.randomUUID();
        WalletResponseDTO w1 = new WalletResponseDTO(
                wallet1Id, "Ví Chính", new BigDecimal("5000000"), "VNĐ", true, "ACTIVE", "", LocalDateTime.now());
        WalletResponseDTO w2 = new WalletResponseDTO(
                wallet2Id, "Ví Phụ", new BigDecimal("2000000"), "VNĐ", false, "ARCHIVED", "", LocalDateTime.now());
        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                List.of(w1, w2), new BigDecimal("7000000"), "VNĐ", Map.of(wallet1Id, BigDecimal.valueOf(71.4), wallet2Id, BigDecimal.valueOf(28.6)));
        when(walletController.getWalletsSummary()).thenReturn(summary);

        UUID cat1 = UUID.randomUUID();
        UUID cat2 = UUID.randomUUID();
        TransactionResponseDTO incomeTxn = new TransactionResponseDTO(
                UUID.randomUUID(), wallet1Id, "Ví Chính", cat1, "Lương",
                new BigDecimal("20000000"), "INCOME", LocalDate.now(), "Lương tháng",
                new BigDecimal("25000000"), LocalDateTime.now());
        TransactionResponseDTO expenseTxn = new TransactionResponseDTO(
                UUID.randomUUID(), wallet1Id, "Ví Chính", cat2, "Tiền nhà",
                new BigDecimal("8000000"), "EXPENSE", LocalDate.now(), "Thuê nhà",
                new BigDecimal("17000000"), LocalDateTime.now());

        TransactionListResponseDTO txnResponse = new TransactionListResponseDTO(
                List.of(incomeTxn, expenseTxn),
                new BigDecimal("20000000"),
                new BigDecimal("8000000"),
                new BigDecimal("12000000"),
                1, 50, 2, 1);
        when(transactionController.filterTransactions(any())).thenReturn(txnResponse);

        JavaFxTestHelper.runOnFxThread(() -> {
            dashboardPane.reloadDashboard();
        });

        // Kiểm tra Thẻ 1: Tổng số dư khả dụng
        assertTrue(dashboardPane.getTotalBalanceValueLabel().getText().contains("7,000,000"));
        assertTrue(dashboardPane.getTotalBalanceSubtextLabel().getText().contains("1 ví"));

        // Kiểm tra Thẻ 2: Thu nhập trong tháng
        assertTrue(dashboardPane.getIncomeValueLabel().getText().contains("20,000,000"));
        assertTrue(dashboardPane.getIncomeSubtextLabel().getText().contains("1 giao dịch thu"));

        // Kiểm tra Thẻ 3: Chi tiêu trong tháng
        assertTrue(dashboardPane.getExpenseValueLabel().getText().contains("8,000,000"));
        assertTrue(dashboardPane.getExpenseSubtextLabel().getText().contains("40.0%"));

        // Kiểm tra Thẻ 4: Tỷ lệ tiết kiệm ròng: (12M / 20M) * 100 = 60.0%
        assertTrue(dashboardPane.getSavingsRateValueLabel().getText().contains("60.0%"));
        assertTrue(dashboardPane.getSavingsRateSubtextLabel().getText().contains("> 30%"));

        // Kiểm tra bảng giao dịch gần nhất
        assertEquals(2, dashboardPane.getRecentTxnsList().size());
        RecentTransactionViewModel row0 = dashboardPane.getRecentTxnsList().get(0);
        assertEquals("+20,000,000", row0.amountFormatted());
        assertTrue(row0.isIncome());

        RecentTransactionViewModel row1 = dashboardPane.getRecentTxnsList().get(1);
        assertEquals("-8,000,000", row1.amountFormatted());
        assertFalse(row1.isIncome());
    }

    @Test
    @DisplayName("Xử lý an toàn khi thu nhập bằng 0 tránh chia cho 0")
    void testZeroIncomeDivisionSafeguard() {
        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, "VNĐ", Collections.emptyMap());
        when(walletController.getWalletsSummary()).thenReturn(summary);

        TransactionListResponseDTO emptyTxns = new TransactionListResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 50, 0, 0);
        when(transactionController.filterTransactions(any())).thenReturn(emptyTxns);

        assertDoesNotThrow(() -> {
            JavaFxTestHelper.runOnFxThread(() -> {
                dashboardPane.reloadDashboard();
            });
        });

        assertTrue(dashboardPane.getTotalBalanceValueLabel().getText().contains("0"));
        assertTrue(dashboardPane.getIncomeValueLabel().getText().contains("0"));
        assertTrue(dashboardPane.getExpenseValueLabel().getText().contains("0"));
        assertTrue(dashboardPane.getSavingsRateValueLabel().getText().contains("0.0%"));
    }
}
