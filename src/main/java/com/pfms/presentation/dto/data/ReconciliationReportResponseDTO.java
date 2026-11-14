package com.pfms.presentation.dto.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO đóng gói báo cáo kết quả đối soát toàn vẹn dữ liệu hệ thống (FR-DAT-03).
 * Tổng hợp 4 điều kiện toàn vẹn tài chính, số lượng bản ghi đã quét và danh
 * sách sai lệch phát hiện.
 */
public final class ReconciliationReportResponseDTO {

    // region State
    private final boolean walletBalanceConsistent;
    private final boolean transferConsistent;
    private final boolean categoryHierarchyConsistent;
    private final boolean feeConsistent;

    private final int scannedWallets;
    private final int scannedTransactions;
    private final int scannedCategories;
    private final int scannedAdjustments;

    private final List<ReconciliationIssueResponseDTO> issues;
    // endregion

    // region Constructors
    public ReconciliationReportResponseDTO(
            boolean walletBalanceConsistent,
            boolean transferConsistent,
            boolean categoryHierarchyConsistent,
            boolean feeConsistent,
            int scannedWallets,
            int scannedTransactions,
            int scannedCategories,
            int scannedAdjustments,
            List<ReconciliationIssueResponseDTO> issues) {

        this.walletBalanceConsistent = walletBalanceConsistent;
        this.transferConsistent = transferConsistent;
        this.categoryHierarchyConsistent = categoryHierarchyConsistent;
        this.feeConsistent = feeConsistent;

        this.scannedWallets = Math.max(0, scannedWallets);
        this.scannedTransactions = Math.max(0, scannedTransactions);
        this.scannedCategories = Math.max(0, scannedCategories);
        this.scannedAdjustments = Math.max(0, scannedAdjustments);

        this.issues = (issues == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(issues));
    }
    // endregion

    // region Factory
    public static ReconciliationReportResponseDTO perfect(
            int scannedWallets,
            int scannedTransactions,
            int scannedCategories,
            int scannedAdjustments) {

        return new ReconciliationReportResponseDTO(
                true,
                true,
                true,
                true,
                scannedWallets,
                scannedTransactions,
                scannedCategories,
                scannedAdjustments,
                Collections.emptyList());
    }
    // endregion

    // region Getters
    public boolean isWalletBalanceConsistent() {
        return this.walletBalanceConsistent;
    }

    public boolean isTransferConsistent() {
        return this.transferConsistent;
    }

    public boolean isCategoryHierarchyConsistent() {
        return this.categoryHierarchyConsistent;
    }

    public boolean isFeeConsistent() {
        return this.feeConsistent;
    }

    public int getScannedWallets() {
        return this.scannedWallets;
    }

    public int getScannedTransactions() {
        return this.scannedTransactions;
    }

    public int getScannedCategories() {
        return this.scannedCategories;
    }

    public int getScannedAdjustments() {
        return this.scannedAdjustments;
    }

    public int getTotalScannedRecords() {
        return this.scannedWallets
                + this.scannedTransactions
                + this.scannedCategories
                + this.scannedAdjustments;
    }

    public List<ReconciliationIssueResponseDTO> getIssues() {
        return this.issues;
    }
    // endregion

    // region Helpers
    public boolean isFullyConsistent() {
        return this.walletBalanceConsistent
                && this.transferConsistent
                && this.categoryHierarchyConsistent
                && this.feeConsistent
                && getErrorCount() == 0;
    }

    public long getErrorCount() {
        return this.issues.stream().filter(ReconciliationIssueResponseDTO::isError).count();
    }

    public long getWarningCount() {
        return this.issues.stream().filter(ReconciliationIssueResponseDTO::isWarning).count();
    }

    public long getInfoCount() {
        return this.issues.stream().filter(ReconciliationIssueResponseDTO::isInfo).count();
    }

    public int getTotalIssues() {
        return this.issues.size();
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReconciliationReportResponseDTO that)) {
            return false;
        }
        return this.walletBalanceConsistent == that.walletBalanceConsistent
                && this.transferConsistent == that.transferConsistent
                && this.categoryHierarchyConsistent == that.categoryHierarchyConsistent
                && this.feeConsistent == that.feeConsistent
                && this.scannedWallets == that.scannedWallets
                && this.scannedTransactions == that.scannedTransactions
                && this.scannedCategories == that.scannedCategories
                && this.scannedAdjustments == that.scannedAdjustments
                && Objects.equals(this.issues, that.issues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.walletBalanceConsistent,
                this.transferConsistent,
                this.categoryHierarchyConsistent,
                this.feeConsistent,
                this.scannedWallets,
                this.scannedTransactions,
                this.scannedCategories,
                this.scannedAdjustments,
                this.issues);
    }

    @Override
    public String toString() {
        return "ReconciliationReportResponseDTO{" +
                "walletBalanceConsistent=" + this.walletBalanceConsistent +
                ", transferConsistent=" + this.transferConsistent +
                ", categoryHierarchyConsistent=" + this.categoryHierarchyConsistent +
                ", feeConsistent=" + this.feeConsistent +
                ", scannedWallets=" + this.scannedWallets +
                ", scannedTransactions=" + this.scannedTransactions +
                ", scannedCategories=" + this.scannedCategories +
                ", scannedAdjustments=" + this.scannedAdjustments +
                ", totalIssues=" + this.issues.size() +
                ", fullyConsistent=" + isFullyConsistent() +
                '}';
    }
    // endregion
}

