package com.pfms.presentation.presenter;

import com.pfms.presentation.contract.DashboardView;
import com.pfms.presentation.controller.TransactionGUIController;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.view.common.ViewUtils;
import com.pfms.presentation.viewmodel.dashboard.DashboardKpiViewModel;
import com.pfms.presentation.viewmodel.dashboard.RecentTransactionViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Bộ điều phối giao diện phân hệ Dashboard theo mô hình MVP.
 * Đóng gói toàn bộ logic tổng hợp chỉ số tài chính, 100% không phụ thuộc Swing/AWT.
 */
public class DashboardPresenter {

    // region Fields
    private static final DateTimeFormatter CYCLE_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
    private final DashboardView view;
    private final WalletGUIController walletController;
    private final TransactionGUIController transactionController;
    // endregion

    // region Constructors
    public DashboardPresenter(
            DashboardView view,
            WalletGUIController walletController,
            TransactionGUIController transactionController) {
        this.view = Objects.requireNonNull(view, "DashboardView không được để null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        this.transactionController = Objects.requireNonNull(transactionController, "TransactionGUIController không được để null");
    }
    // endregion

    // region Actions
    public void onRefreshClicked() {
        loadDashboard();
    }
    // endregion

    // region Data
    public void loadDashboard() {
        LocalDate today = LocalDate.now();
        this.view.setCycleSubtitle("Chu kỳ tài chính: Tháng " + today.format(CYCLE_FORMATTER));
        try {
            WalletsSummaryResponseDTO walletSummary = this.walletController.getWalletsSummary();
            FilterTransactionsRequestDTO filter = new FilterTransactionsRequestDTO(
                    today.withDayOfMonth(1), today, null, null, null, "", 1, 100);
            TransactionListResponseDTO txnResponse = this.transactionController.filterTransactions(filter);

            DashboardKpiViewModel kpis = computeKpis(walletSummary, txnResponse);
            List<RecentTransactionViewModel> recentTxns = buildRecentTransactions(txnResponse);

            this.view.renderKpis(kpis);
            this.view.renderRecentTransactions(recentTxns);
        } catch (Exception ex) {
            this.view.renderKpis(DashboardKpiViewModel.empty());
            this.view.renderRecentTransactions(Collections.emptyList());
            this.view.showError(ex.getMessage());
        }
    }
    // endregion

    // region Getters
    public DashboardView getView() {
        return this.view;
    }

    public WalletGUIController getWalletController() {
        return this.walletController;
    }

    public TransactionGUIController getTransactionController() {
        return this.transactionController;
    }
    // endregion

    // region Helpers
    private DashboardKpiViewModel computeKpis(WalletsSummaryResponseDTO summary, TransactionListResponseDTO txns) {
        BigDecimal totalBal = (summary != null && summary.getTotalBalance() != null) ? summary.getTotalBalance() : BigDecimal.ZERO;
        long activeCount = (summary != null && summary.getWallets() != null)
                ? summary.getWallets().stream().filter(w -> "ACTIVE".equalsIgnoreCase(w.getStatus())).count() : 0;

        BigDecimal inc = (txns != null && txns.getTotalIncome() != null) ? txns.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal exp = (txns != null && txns.getTotalExpense() != null) ? txns.getTotalExpense() : BigDecimal.ZERO;
        BigDecimal net = (txns != null && txns.getNetCashFlow() != null) ? txns.getNetCashFlow() : inc.subtract(exp);

        long incCount = (txns != null && txns.getItems() != null)
                ? txns.getItems().stream().filter(t -> "INCOME".equalsIgnoreCase(t.getTransactionType())).count() : 0;

        String expSubtext = (inc.compareTo(BigDecimal.ZERO) > 0)
                ? exp.multiply(BigDecimal.valueOf(100)).divide(inc, 1, RoundingMode.HALF_UP).toPlainString() + "% so với thu nhập"
                : "0.0% so với thu nhập";

        String savRate = (inc.compareTo(BigDecimal.ZERO) > 0)
                ? net.multiply(BigDecimal.valueOf(100)).divide(inc, 1, RoundingMode.HALF_UP).toPlainString() + "%"
                : "0.0%";

        return new DashboardKpiViewModel(
                ViewUtils.formatCurrency(totalBal), activeCount + " ví đang hoạt động",
                ViewUtils.formatCurrency(inc), incCount + " giao dịch thu",
                ViewUtils.formatCurrency(exp), expSubtext, savRate, "Mục tiêu tích lũy > 30%");
    }

    private List<RecentTransactionViewModel> buildRecentTransactions(TransactionListResponseDTO txnResponse) {
        if (txnResponse == null || txnResponse.getItems() == null) {
            return Collections.emptyList();
        }
        List<TransactionResponseDTO> items = txnResponse.getItems().stream().limit(10).toList();
        List<RecentTransactionViewModel> result = new ArrayList<>(items.size());
        for (TransactionResponseDTO dto : items) {
            boolean isIncome = "INCOME".equalsIgnoreCase(dto.getTransactionType());
            boolean isExpense = "EXPENSE".equalsIgnoreCase(dto.getTransactionType());
            String sign = isIncome ? "+" : (isExpense ? "-" : "");
            String amountFormatted = sign + ViewUtils.formatAmount(dto.getAmount());
            result.add(new RecentTransactionViewModel(
                    dto.getTransactionId(),
                    dto.getTransactionDate() != null ? ViewUtils.formatDate(dto.getTransactionDate()) : "-",
                    dto.getTransactionType(),
                    dto.getCategoryName() != null ? dto.getCategoryName() : "-",
                    dto.getWalletName() != null ? dto.getWalletName() : "-",
                    amountFormatted, isIncome,
                    dto.getNote() != null ? dto.getNote() : ""));
        }
        return result;
    }
    // endregion
}
