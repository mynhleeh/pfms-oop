package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.service.impl.common.EntityLookupSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Bộ đối soát nội bộ kiểm tra tính toàn vẹn dữ liệu hệ thống.
 * Kiểm định số dư ví, phân cấp danh mục và điều phối kiểm tra giao dịch chuyển tiền, phụ phí.
 */
final class SystemDataReconciler {

    // region Constructors
    private SystemDataReconciler() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static ReconciliationReportResponseDTO reconcile(
            List<Wallet> wallets,
            List<Transaction> transactions,
            List<Category> categories,
            List<NeedGroup> needGroups,
            List<BalanceAdjustmentRecord> adjustments) {

        Map<UUID, Wallet> walletMap = EntityLookupSupport.indexById(wallets, Wallet::getId);
        Map<UUID, Category> categoryMap = EntityLookupSupport.indexById(categories, Category::getCategoryId);
        Map<UUID, NeedGroup> groupMap = EntityLookupSupport.indexById(needGroups, NeedGroup::getNeedGroupId);

        List<ReconciliationIssueResponseDTO> issues = new ArrayList<>();

        boolean walletBalanceConsistent = checkWalletBalances(wallets, transactions, adjustments, issues);
        boolean transferConsistent = TransactionIntegrityChecker.checkTransfers(
                walletMap, transactions, categoryMap, issues);
        boolean categoryHierarchyConsistent = checkCategoryHierarchy(
                categories, groupMap, transactions, categoryMap, issues);
        boolean feeConsistent = TransactionIntegrityChecker.checkFees(
                transactions, categoryMap, issues);

        if (walletBalanceConsistent && transferConsistent && categoryHierarchyConsistent && feeConsistent) {
            return ReconciliationReportResponseDTO.perfect(
                    wallets.size(), transactions.size(), categories.size(), adjustments.size());
        }

        return new ReconciliationReportResponseDTO(
                walletBalanceConsistent, transferConsistent, categoryHierarchyConsistent, feeConsistent,
                wallets.size(), transactions.size(), categories.size(), adjustments.size(), issues);
    }
    // endregion

    // region Helpers
    private static boolean checkWalletBalances(
            List<Wallet> wallets,
            List<Transaction> transactions,
            List<BalanceAdjustmentRecord> adjustments,
            List<ReconciliationIssueResponseDTO> issues) {

        Map<UUID, Money> calculatedBalances = computeNetBalances(transactions, adjustments);
        boolean consistent = true;
        for (Wallet wallet : wallets) {
            Money calculated = calculatedBalances.getOrDefault(wallet.getId(), Money.ZERO);
            if (calculated.compareTo(wallet.getCurrentBalance()) != 0) {
                consistent = recordIssue(issues, "BR-FIN-02", "BALANCE_MISMATCH",
                        String.format("Ví [%s] có số dư ghi nhận (%s) không khớp với dòng tiền tính toán (%s), chênh lệch: %s",
                                wallet.getName(), wallet.getCurrentBalance().formatDisplay(),
                                calculated.formatDisplay(),
                                wallet.getCurrentBalance().subtract(calculated).formatDisplay()),
                        wallet.getId().toString(),
                        "Thực hiện kiểm kê đối soát số dư ví hoặc phục hồi dữ liệu từ bản sao lưu gần nhất");
            }
        }
        return consistent;
    }

    private static Map<UUID, Money> computeNetBalances(
            List<Transaction> transactions,
            List<BalanceAdjustmentRecord> adjustments) {

        Map<UUID, Money> map = new HashMap<>();
        for (Transaction txn : transactions) {
            if (txn instanceof IncomeExpenseTransaction ie) {
                Money delta = (ie.getTransactionType() == TransactionType.INCOME)
                        ? ie.getAmount()
                        : Money.ZERO.subtract(ie.getAmount());
                if (ie.getFee().isPresent()) {
                    delta = delta.subtract(ie.getFee().get().getAmount());
                }
                map.merge(ie.getWallet().getId(), delta, Money::add);
            } else if (txn instanceof TransferTransaction trf) {
                map.merge(trf.getSourceWallet().getId(), Money.ZERO.subtract(trf.getAmount()), Money::add);
                if (trf.getFee().isPresent()) {
                    map.merge(trf.getSourceWallet().getId(),
                            Money.ZERO.subtract(trf.getFee().get().getAmount()), Money::add);
                }
                map.merge(trf.getDestinationWallet().getId(), trf.getAmount(), Money::add);
            }
        }
        for (BalanceAdjustmentRecord adj : adjustments) {
            map.merge(adj.getWalletId(), adj.getDifference(), Money::add);
        }
        return map;
    }

    private static boolean checkCategoryHierarchy(
            List<Category> categories,
            Map<UUID, NeedGroup> groupMap,
            List<Transaction> transactions,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = true;
        for (Category cat : categories) {
            String catIdStr = cat.getCategoryId().toString();
            NeedGroup group = groupMap.get(cat.getNeedGroupId());
            if (group == null) {
                consistent = recordIssue(issues, "BR-FIN-04", "CATEGORY_INCONSISTENCY",
                        String.format("Danh mục [%s] không trực thuộc nhóm nhu cầu hợp lệ nào", cat.getName()),
                        catIdStr, "Gán lại nhóm nhu cầu cha hợp lệ cho danh mục");
            } else if (cat.getTransactionType() != group.getTransactionType()) {
                consistent = recordIssue(issues, "BR-FIN-04", "CATEGORY_INCONSISTENCY",
                        String.format("Danh mục [%s] có loại thu chi (%s) không khớp với nhóm cha (%s)",
                                cat.getName(), cat.getTransactionType(), group.getTransactionType()),
                        catIdStr, "Chuyển danh mục sang nhóm cha có cùng loại thu chi");
            }
        }

        for (Transaction txn : transactions) {
            if (!(txn instanceof IncomeExpenseTransaction ie)) {
                continue;
            }
            String txnIdStr = ie.getTransactionId().toString();
            Category cat = categoryMap.get(ie.getCategory().getCategoryId());
            if (cat == null) {
                consistent = recordIssue(issues, "BR-FIN-04", "CATEGORY_INCONSISTENCY",
                        String.format("Giao dịch [%s] trỏ tới danh mục [%s] không tồn tại",
                                ie.getTransactionId(), ie.getCategory().getCategoryId()),
                        txnIdStr, "Gán lại danh mục phân loại hợp lệ cho giao dịch");
            } else if (cat.getTransactionType() != ie.getTransactionType()) {
                consistent = recordIssue(issues, "BR-FIN-04", "CATEGORY_INCONSISTENCY",
                        String.format("Giao dịch [%s] có loại (%s) không khớp với loại của danh mục [%s] (%s)",
                                ie.getTransactionId(), ie.getTransactionType(), cat.getName(), cat.getTransactionType()),
                        txnIdStr, "Gán danh mục có cùng loại thu chi với giao dịch");
            }
        }
        return consistent;
    }

    private static boolean recordIssue(
            List<ReconciliationIssueResponseDTO> issues,
            String ruleCode,
            String issueType,
            String message,
            String entityId,
            String suggestion) {

        issues.add(new ReconciliationIssueResponseDTO("ERROR", ruleCode, issueType, message, entityId, suggestion));
        return false;
    }
    // endregion
}
