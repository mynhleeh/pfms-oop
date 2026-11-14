package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.presentation.dto.data.ReconciliationIssueDTO;
import com.pfms.presentation.dto.data.ReconciliationReportDTO;
import com.pfms.service.impl.common.EntityLookupSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Bộ đối soát nội bộ kiểm tra tính toàn vẹn dữ liệu hệ thống.
 * Quét độc lập 4 điều kiện: số dư ví, giao dịch chuyển tiền, phân cấp danh mục và tính hợp lệ của phụ phí.
 */
final class SystemDataReconciler {

    // region Constructors
    private SystemDataReconciler() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static ReconciliationReportDTO reconcile(
            List<Wallet> wallets,
            List<Transaction> transactions,
            List<Category> categories,
            List<NeedGroup> needGroups,
            List<BalanceAdjustmentRecord> adjustments) {

        Map<UUID, Wallet> walletMap = EntityLookupSupport.indexById(wallets, Wallet::getId);
        Map<UUID, Category> categoryMap = EntityLookupSupport.indexById(categories, Category::getCategoryId);
        Map<UUID, NeedGroup> groupMap = EntityLookupSupport.indexById(needGroups, NeedGroup::getNeedGroupId);

        List<ReconciliationIssueDTO> issues = new ArrayList<>();

        boolean walletBalanceConsistent = checkWalletBalances(wallets, transactions, adjustments, issues);
        boolean transferConsistent = checkTransfers(walletMap, transactions, categoryMap, issues);
        boolean categoryHierarchyConsistent = checkCategoryHierarchy(categories, groupMap, transactions, categoryMap, issues);
        boolean feeConsistent = checkFees(transactions, categoryMap, issues);

        int scannedWallets = wallets.size();
        int scannedTransactions = transactions.size();
        int scannedCategories = categories.size();
        int scannedAdjustments = adjustments.size();

        if (walletBalanceConsistent && transferConsistent && categoryHierarchyConsistent && feeConsistent) {
            return ReconciliationReportDTO.perfect(
                    scannedWallets, scannedTransactions, scannedCategories, scannedAdjustments);
        }

        return new ReconciliationReportDTO(
                walletBalanceConsistent,
                transferConsistent,
                categoryHierarchyConsistent,
                feeConsistent,
                scannedWallets,
                scannedTransactions,
                scannedCategories,
                scannedAdjustments,
                issues);
    }
    // endregion

    // region Helpers
    private static boolean checkWalletBalances(
            List<Wallet> wallets,
            List<Transaction> transactions,
            List<BalanceAdjustmentRecord> adjustments,
            List<ReconciliationIssueDTO> issues) {

        Map<UUID, Money> calculatedBalances = computeNetBalances(transactions, adjustments);
        boolean consistent = true;
        for (Wallet wallet : wallets) {
            Money calculated = calculatedBalances.getOrDefault(wallet.getId(), Money.ZERO);
            if (calculated.compareTo(wallet.getCurrentBalance()) != 0) {
                consistent = recordIssue(issues, "BR-FIN-02", "BALANCE_MISMATCH",
                        String.format("Ví [%s] có số dư ghi nhận (%s) không khớp với dòng tiền tính toán (%s), chênh lệch: %s",
                                wallet.getName(), wallet.getCurrentBalance().formatDisplay(),
                                calculated.formatDisplay(), wallet.getCurrentBalance().subtract(calculated).formatDisplay()),
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
                    map.merge(trf.getSourceWallet().getId(), Money.ZERO.subtract(trf.getFee().get().getAmount()), Money::add);
                }
                map.merge(trf.getDestinationWallet().getId(), trf.getAmount(), Money::add);
            }
        }
        for (BalanceAdjustmentRecord adj : adjustments) {
            map.merge(adj.getWalletId(), adj.getDifference(), Money::add);
        }
        return map;
    }

    private static boolean checkTransfers(
            Map<UUID, Wallet> walletMap,
            List<Transaction> transactions,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueDTO> issues) {

        boolean consistent = true;
        for (Transaction txn : transactions) {
            if (!(txn instanceof TransferTransaction trf)) {
                continue;
            }
            UUID srcId = trf.getSourceWallet().getId();
            UUID dstId = trf.getDestinationWallet().getId();
            String txnIdStr = trf.getTransactionId().toString();

            if (srcId.equals(dstId)) {
                consistent = recordIssue(issues, "BR-FIN-03", "TRANSFER_INCONSISTENCY",
                        String.format("Giao dịch chuyển tiền [%s] có ví nguồn trùng với ví đích", trf.getTransactionId()),
                        txnIdStr, "Hủy bỏ giao dịch chuyển tiền trùng ví hoặc cập nhật lại ví đích");
            }
            if (!walletMap.containsKey(srcId)) {
                consistent = recordIssue(issues, "BR-FIN-03", "TRANSFER_INCONSISTENCY",
                        String.format("Giao dịch chuyển tiền [%s] trỏ tới ví nguồn [%s] không tồn tại", trf.getTransactionId(), srcId),
                        txnIdStr, "Kiểm tra lại tính hợp lệ của ví nguồn");
            }
            if (!walletMap.containsKey(dstId)) {
                consistent = recordIssue(issues, "BR-FIN-03", "TRANSFER_INCONSISTENCY",
                        String.format("Giao dịch chuyển tiền [%s] trỏ tới ví đích [%s] không tồn tại", trf.getTransactionId(), dstId),
                        txnIdStr, "Kiểm tra lại tính hợp lệ của ví đích");
            }
            if (trf.getFee().isPresent()) {
                TransactionFee fee = trf.getFee().get();
                if (!fee.getWallet().getId().equals(srcId)) {
                    consistent = recordIssue(issues, "BR-FIN-02", "TRANSFER_INCONSISTENCY",
                            String.format("Phí của giao dịch chuyển tiền [%s] không ghi nhận vào ví nguồn", trf.getTransactionId()),
                            txnIdStr, "Gán lại mã ví của phụ phí chuyển tiền khớp với ví nguồn");
                }
                Category feeCat = categoryMap.get(fee.getCategory().getCategoryId());
                if (feeCat == null || feeCat.getTransactionType() != TransactionType.EXPENSE) {
                    consistent = recordIssue(issues, "BR-FIN-04", "TRANSFER_INCONSISTENCY",
                            String.format("Phụ phí của chuyển tiền [%s] gắn với danh mục không hợp lệ hoặc không phải chi phí", trf.getTransactionId()),
                            txnIdStr, "Gán lại danh mục chi phí hợp lệ cho phụ phí chuyển tiền");
                }
            }
        }
        return consistent;
    }

    private static boolean checkCategoryHierarchy(
            List<Category> categories,
            Map<UUID, NeedGroup> groupMap,
            List<Transaction> transactions,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueDTO> issues) {

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
                        String.format("Giao dịch [%s] trỏ tới danh mục [%s] không tồn tại", ie.getTransactionId(), ie.getCategory().getCategoryId()),
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

    private static boolean checkFees(
            List<Transaction> transactions,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueDTO> issues) {

        boolean consistent = true;
        for (Transaction txn : transactions) {
            if (txn.getFee().isEmpty()) {
                continue;
            }
            TransactionFee fee = txn.getFee().get();
            String feeIdStr = fee.getFeeId().toString();
            if (!fee.getParentTransactionId().equals(txn.getTransactionId())) {
                consistent = recordIssue(issues, "BR-FIN-02", "FEE_INCONSISTENCY",
                        String.format("Phụ phí [%s] có mã giao dịch cha không khớp với giao dịch [%s]", fee.getFeeId(), txn.getTransactionId()),
                        feeIdStr, "Cập nhật lại mã giao dịch cha cho phụ phí");
            }
            Category feeCat = categoryMap.get(fee.getCategory().getCategoryId());
            if (feeCat == null) {
                consistent = recordIssue(issues, "BR-FIN-04", "FEE_INCONSISTENCY",
                        String.format("Phụ phí [%s] trỏ tới danh mục không tồn tại", fee.getFeeId()),
                        feeIdStr, "Gán danh mục chi phí hợp lệ cho phụ phí");
            } else if (feeCat.getTransactionType() != TransactionType.EXPENSE) {
                consistent = recordIssue(issues, "BR-FIN-04", "FEE_INCONSISTENCY",
                        String.format("Danh mục của phụ phí [%s] phải thuộc loại EXPENSE", fee.getFeeId()),
                        feeIdStr, "Chuyển danh mục phụ phí sang danh mục loại chi phí");
            }
        }
        return consistent;
    }

    private static boolean recordIssue(
            List<ReconciliationIssueDTO> issues,
            String ruleCode,
            String issueType,
            String message,
            String entityId,
            String suggestion) {

        issues.add(new ReconciliationIssueDTO("ERROR", ruleCode, issueType, message, entityId, suggestion));
        return false;
    }
    // endregion
}
