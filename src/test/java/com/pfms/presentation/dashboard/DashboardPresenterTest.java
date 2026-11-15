package com.pfms.presentation.dashboard;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.transaction.TransactionGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.dashboard.viewmodel.DashboardKpiViewModel;
import com.pfms.presentation.dashboard.viewmodel.RecentTransactionViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử DashboardPresenter - Bộ điều phối giao diện tổng quan tài chính")
class DashboardPresenterTest {

    private DashboardView view;
    private WalletGUIController walletController;
    private TransactionGUIController transactionController;
    private DashboardPresenter presenter;

    @BeforeEach
    void setUp() {
        view = Mockito.mock(DashboardView.class);
        walletController = Mockito.mock(WalletGUIController.class);
        transactionController = Mockito.mock(TransactionGUIController.class);

        presenter = new DashboardPresenter(view, walletController, transactionController);
    }

    @Test
    @DisplayName("Ném NullPointerException khi tham số constructor bị null")
    void testConstructorNullValidation() {
        assertThrows(NullPointerException.class, () -> new DashboardPresenter(null, walletController, transactionController));
        assertThrows(NullPointerException.class, () -> new DashboardPresenter(view, null, transactionController));
        assertThrows(NullPointerException.class, () -> new DashboardPresenter(view, walletController, null));
    }

    @Test
    @DisplayName("loadDashboard() tính toán đầy đủ 4 thẻ KPI và định dạng giao dịch gần nhất")
    void testLoadDashboardSuccess() {
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

        presenter.loadDashboard();

        String currentCycle = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        verify(view).setCycleSubtitle(contains(currentCycle));

        ArgumentCaptor<DashboardKpiViewModel> kpiCaptor = ArgumentCaptor.forClass(DashboardKpiViewModel.class);
        verify(view).renderKpis(kpiCaptor.capture());

        DashboardKpiViewModel kpi = kpiCaptor.getValue();
        assertEquals("7,000,000 VNĐ", kpi.totalBalanceFormatted());
        assertEquals("1 ví đang hoạt động", kpi.totalBalanceSubtext());
        assertEquals("20,000,000 VNĐ", kpi.incomeFormatted());
        assertEquals("1 giao dịch thu", kpi.incomeSubtext());
        assertEquals("8,000,000 VNĐ", kpi.expenseFormatted());
        assertEquals("40.0% so với thu nhập", kpi.expenseSubtext());
        assertEquals("60.0%", kpi.savingsRateFormatted());
        assertEquals("Mục tiêu tích lũy > 30%", kpi.savingsRateSubtext());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RecentTransactionViewModel>> txnCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).renderRecentTransactions(txnCaptor.capture());

        List<RecentTransactionViewModel> txns = txnCaptor.getValue();
        assertEquals(2, txns.size());

        RecentTransactionViewModel t1 = txns.get(0);
        assertEquals("+20,000,000", t1.amountFormatted());
        assertTrue(t1.isIncome());
        assertEquals("Ví Chính", t1.walletName());
        assertEquals("Lương", t1.categoryName());

        RecentTransactionViewModel t2 = txns.get(1);
        assertEquals("-8,000,000", t2.amountFormatted());
        assertFalse(t2.isIncome());
        assertEquals("Tiền nhà", t2.categoryName());
    }

    @Test
    @DisplayName("loadDashboard() xử lý an toàn khi không có dữ liệu ví và giao dịch rỗng")
    void testLoadDashboardEmpty() {
        when(walletController.getWalletsSummary()).thenReturn(new WalletsSummaryResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, "VNĐ", Collections.emptyMap()));
        when(transactionController.filterTransactions(any())).thenReturn(new TransactionListResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 50, 0, 0));

        presenter.loadDashboard();

        ArgumentCaptor<DashboardKpiViewModel> kpiCaptor = ArgumentCaptor.forClass(DashboardKpiViewModel.class);
        verify(view).renderKpis(kpiCaptor.capture());

        DashboardKpiViewModel kpi = kpiCaptor.getValue();
        assertEquals("0 VNĐ", kpi.totalBalanceFormatted());
        assertEquals("0 ví đang hoạt động", kpi.totalBalanceSubtext());
        assertEquals("0 VNĐ", kpi.incomeFormatted());
        assertEquals("0 giao dịch thu", kpi.incomeSubtext());
        assertEquals("0 VNĐ", kpi.expenseFormatted());
        assertEquals("0.0% so với thu nhập", kpi.expenseSubtext());
        assertEquals("0.0%", kpi.savingsRateFormatted());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RecentTransactionViewModel>> txnCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).renderRecentTransactions(txnCaptor.capture());
        assertTrue(txnCaptor.getValue().isEmpty());
    }

    @Test
    @DisplayName("loadDashboard() tính toán tỷ lệ tiết kiệm âm khi chi tiêu vượt thu nhập")
    void testNegativeSavingsRate() {
        when(walletController.getWalletsSummary()).thenReturn(new WalletsSummaryResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, "VNĐ", Collections.emptyMap()));

        TransactionListResponseDTO txnResponse = new TransactionListResponseDTO(
                Collections.emptyList(),
                new BigDecimal("10000000"),
                new BigDecimal("15000000"),
                new BigDecimal("-5000000"),
                1, 50, 2, 1);
        when(transactionController.filterTransactions(any())).thenReturn(txnResponse);

        presenter.loadDashboard();

        ArgumentCaptor<DashboardKpiViewModel> kpiCaptor = ArgumentCaptor.forClass(DashboardKpiViewModel.class);
        verify(view).renderKpis(kpiCaptor.capture());

        DashboardKpiViewModel kpi = kpiCaptor.getValue();
        assertEquals("-50.0%", kpi.savingsRateFormatted());
        assertEquals("150.0% so với thu nhập", kpi.expenseSubtext());
    }

    @Test
    @DisplayName("loadDashboard() giới hạn tối đa 10 giao dịch gần nhất")
    void testLimitRecentTransactionsTo10() {
        when(walletController.getWalletsSummary()).thenReturn(new WalletsSummaryResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, "VNĐ", Collections.emptyMap()));

        List<TransactionResponseDTO> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            list.add(new TransactionResponseDTO(
                    UUID.randomUUID(), UUID.randomUUID(), "Ví", UUID.randomUUID(), "Danh mục",
                    BigDecimal.valueOf(100000), "EXPENSE", LocalDate.now(), "Giao dịch " + i,
                    BigDecimal.valueOf(500000), LocalDateTime.now()));
        }
        TransactionListResponseDTO response = new TransactionListResponseDTO(
                list, BigDecimal.ZERO, BigDecimal.valueOf(1500000), BigDecimal.valueOf(-1500000), 1, 50, 15, 1);
        when(transactionController.filterTransactions(any())).thenReturn(response);

        presenter.loadDashboard();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RecentTransactionViewModel>> txnCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).renderRecentTransactions(txnCaptor.capture());
        assertEquals(10, txnCaptor.getValue().size());
    }

    @Test
    @DisplayName("loadDashboard() bắt ngoại lệ từ controller, render KPI mặc định và báo lỗi qua view")
    void testExceptionHandling() {
        when(walletController.getWalletsSummary()).thenThrow(new BusinessException(ErrorCode.ERR_WAL_001, "Lỗi kết nối cơ sở dữ liệu ví"));

        presenter.loadDashboard();

        verify(view).renderKpis(DashboardKpiViewModel.empty());
        verify(view).renderRecentTransactions(Collections.emptyList());
        verify(view).showError("Lỗi kết nối cơ sở dữ liệu ví");
    }

    @Test
    @DisplayName("Getters trả về đúng các đối tượng thành phần")
    void testGetters() {
        assertSame(view, presenter.getView());
        assertSame(walletController, presenter.getWalletController());
        assertSame(transactionController, presenter.getTransactionController());
    }
}
