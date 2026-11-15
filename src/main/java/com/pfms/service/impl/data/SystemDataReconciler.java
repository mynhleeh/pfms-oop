package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.service.impl.common.EntityLookupSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * Bộ đối soát nội bộ kiểm tra tính toàn vẹn dữ liệu hệ thống.
 * Kiểm định số dư ví, phân cấp danh mục, mã định danh duy nhất và giao dịch chuyển tiền, phụ phí.
 */
final class SystemDataReconciler {

    // region Constructors
    private SystemDataReconciler() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static ReconciliationReportResponseDTO reconcile(BackupPayload payload) {
        Objects.requireNonNull(payload, "BackupPayload không được để null");
        return reconcile(
                payload.getWallets(),
                payload.getTransactions(),
                payload.getCategories(),
                payload.getNeedGroups(),
                payload.getAdjustments());
    }

    static ReconciliationReportResponseDTO reconcile(
            List<Wallet> wallets,
            List<Transaction> transactions,
            List<Category> categories,
            List<NeedGroup> needGroups,
            List<BalanceAdjustmentRecord> adjustments) {

        List<ReconciliationIssueResponseDTO> issues = new ArrayList<>();

        boolean uniqueIds = checkUniqueIdentifiers(wallets, needGroups, categories, transactions, adjustments, issues);
        Map<UUID, Wallet> walletMap = EntityLookupSupport.indexById(wallets, Wallet::getId);
        Map<UUID, Category> categoryMap = EntityLookupSupport.indexById(categories, Category::getCategoryId);
        Map<UUID, NeedGroup> groupMap = EntityLookupSupport.indexById(needGroups, NeedGroup::getNeedGroupId);

        boolean walletBalanceConsistent = checkWalletBalances(wallets, walletMap, transactions, adjustments, issues);
        boolean transferConsistent = checkTransfers(walletMap, transactions, categoryMap, issues);
        boolean categoryHierarchyConsistent = checkCategoryHierarchy(categories, groupMap, transactions, categoryMap, issues);
        boolean feeConsistent = checkFees(transactions, categoryMap, issues);

        if (uniqueIds && walletBalanceConsistent && transferConsistent && categoryHierarchyConsistent && feeConsistent) {
            return ReconciliationReportResponseDTO.perfect(
                    wallets.size(), transactions.size(), categories.size(), adjustments.size());
        }

        return new ReconciliationReportResponseDTO(
                walletBalanceConsistent && uniqueIds, transferConsistent, categoryHierarchyConsistent, feeConsistent,
                wallets.size(), transactions.size(), categories.size(), adjustments.size(), issues);
    }
    // endregion

    // region Helpers
    private static boolean checkUniqueIdentifiers(
            List<Wallet> wallets,
            List<NeedGroup> needGroups,
            List<Category> categories,
            List<Transaction> transactions,
            List<BalanceAdjustmentRecord> adjustments,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = checkDuplicateIds(wallets, Wallet::getId, "ví", issues);
        consistent &= checkDuplicateIds(needGroups, NeedGroup::getNeedGroupId, "nhóm nhu cầu", issues);
        consistent &= checkDuplicateIds(categories, Category::getCategoryId, "danh mục", issues);
        consistent &= checkDuplicateIds(transactions, Transaction::getTransactionId, "giao dịch", issues);
        consistent &= checkDuplicateIds(adjustments, BalanceAdjustmentRecord::getRecordId, "bản ghi điều chỉnh số dư", issues);
        return consistent;
    }

    private static <T> boolean checkDuplicateIds(
            List<T> items,
            Function<T, UUID> idExtractor,
            String entityTypeName,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = true;
        Set<UUID> seenIds = new HashSet<>();
        for (T item : items) {
            UUID id = idExtractor.apply(item);
            if (!seenIds.add(id)) {
                consistent &= recordIssue(issues, "BR-DAT-02", "DUPLICATE_ID",
                        String.format("Trùng mã %s trong bản sao lưu: %s", entityTypeName, id),
                        id.toString(), "Loại bỏ bản ghi " + entityTypeName + " trùng lặp");
            }
        }
        return consistent;
    }

    private static boolean checkWalletBalances(
            List<Wallet> wallets,
            Map<UUID, Wallet> walletMap,
            List<Transaction> transactions,
            List<BalanceAdjustmentRecord> adjustments,
            List<ReconciliationIssueResponseDTO> issues) {

        Map<UUID, Money> calculatedBalances = computeNetBalances(transactions, adjustments);
        boolean consistent = true;
        for (Wallet wallet : wallets) {
            if (wallet.getCurrentBalance().isNegative()) {
                consistent &= recordIssue(issues, "BR-FIN-02", "BALANCE_MISMATCH",
                        String.format("Ví [%s] có số dư âm không hợp lệ (%s)",
                                wallet.getName(), wallet.getCurrentBalance().formatDisplay()),
                        wallet.getId().toString(),
                        "Thực hiện kiểm kê điều chỉnh số dư ví về mức không âm hoặc phục hồi dữ liệu từ bản sao lưu");
            }
            Money calculated = calculatedBalances.getOrDefault(wallet.getId(), Money.ZERO);
            if (calculated.compareTo(wallet.getCurrentBalance()) != 0) {
                consistent &= recordIssue(issues, "BR-FIN-02", "BALANCE_MISMATCH",
                        String.format("Ví [%s] có số dư ghi nhận (%s) không khớp với dòng tiền tính toán (%s), chênh lệch: %s",
                                wallet.getName(), wallet.getCurrentBalance().formatDisplay(),
                                calculated.formatDisplay(),
                                wallet.getCurrentBalance().subtract(calculated).formatDisplay()),
                        wallet.getId().toString(),
                        "Thực hiện kiểm kê đối soát số dư ví hoặc phục hồi dữ liệu từ bản sao lưu gần nhất");
            }
        }
        for (Transaction txn : transactions) {
            if (txn instanceof IncomeExpenseTransaction ie && !walletMap.containsKey(ie.getWallet().getId())) {
                consistent &= recordIssue(issues, "BR-FIN-02", "BALANCE_MISMATCH",
                        String.format("Giao dịch thu chi [%s] trỏ tới ví [%s] không tồn tại",
                                ie.getTransactionId(), ie.getWallet().getId()),
                        ie.getTransactionId().toString(),
                        "Kiểm tra lại mã ví của giao dịch thu chi hoặc phục hồi từ bản sao lưu hợp lệ");
            }
        }
        for (BalanceAdjustmentRecord adj : adjustments) {
            if (!walletMap.containsKey(adj.getWalletId())) {
                consistent &= recordIssue(issues, "BR-FIN-02", "BALANCE_MISMATCH",
                        String.format("Bản ghi kiểm kê [%s] trỏ tới ví [%s] không tồn tại",
                                adj.getRecordId(), adj.getWalletId()),
                        adj.getRecordId().toString(),
                        "Kiểm tra lại mã ví của bản ghi kiểm kê hoặc phục hồi từ bản sao lưu hợp lệ");
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
                        ? ie.getAmount() : Money.ZERO.subtract(ie.getAmount());
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

    private static boolean checkCategoryHierarchy(
            List<Category> categories,
            Map<UUID, NeedGroup> groupMap,
            List<Transaction> transactions,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = true;
        for (Category cat : categories) {
            NeedGroup group = groupMap.get(cat.getNeedGroupId());
            if (group == null) {
                consistent &= recordIssue(issues, "BR-FIN-04", "CATEGORY_INCONSISTENCY",
                        String.format("Danh mục [%s] không trực thuộc nhóm nhu cầu hợp lệ nào", cat.getName()),
                        cat.getCategoryId(), "Gán lại nhóm nhu cầu cha hợp lệ cho danh mục");
            } else if (cat.getTransactionType() != group.getTransactionType()) {
                consistent &= recordIssue(issues, "BR-FIN-04", "CATEGORY_INCONSISTENCY",
                        String.format("Danh mục [%s] có loại thu chi (%s) không khớp với nhóm cha (%s)",
                                cat.getName(), cat.getTransactionType(), group.getTransactionType()),
                        cat.getCategoryId(), "Chuyển danh mục sang nhóm cha có cùng loại thu chi");
            }
        }

        for (Transaction txn : transactions) {
            if (txn instanceof IncomeExpenseTransaction ie) {
                Category cat = categoryMap.get(ie.getCategory().getCategoryId());
                if (cat == null) {
                    consistent &= recordIssue(issues, "BR-FIN-04", "CATEGORY_INCONSISTENCY",
                            String.format("Giao dịch [%s] trỏ tới danh mục [%s] không tồn tại",
                                    ie.getTransactionId(), ie.getCategory().getCategoryId()),
                            ie.getTransactionId(), "Gán lại danh mục phân loại hợp lệ cho giao dịch");
                } else if (cat.getTransactionType() != ie.getTransactionType()) {
                    consistent &= recordIssue(issues, "BR-FIN-04", "CATEGORY_INCONSISTENCY",
                            String.format("Giao dịch [%s] có loại (%s) không khớp với loại của danh mục [%s] (%s)",
                                    ie.getTransactionId(), ie.getTransactionType(), cat.getName(), cat.getTransactionType()),
                            ie.getTransactionId(), "Gán danh mục có cùng loại thu chi với giao dịch");
                }
            }
        }
        return consistent;
    }

    private static boolean checkTransfers(
            Map<UUID, Wallet> walletMap,
            List<Transaction> transactions,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = true;
        for (Transaction txn : transactions) {
            if (txn instanceof TransferTransaction trf) {
                consistent &= checkSingleTransfer(trf, walletMap, categoryMap, issues);
            }
        }
        return consistent;
    }

    private static boolean checkSingleTransfer(
            TransferTransaction trf,
            Map<UUID, Wallet> walletMap,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueResponseDTO> issues) {

        UUID srcId = trf.getSourceWallet().getId();
        UUID dstId = trf.getDestinationWallet().getId();
        String txnId = trf.getTransactionId().toString();
        boolean valid = true;

        if (srcId.equals(dstId)) {
            valid &= recordIssue(issues, "BR-FIN-03", "TRANSFER_INCONSISTENCY",
                    String.format("Giao dịch chuyển tiền [%s] có ví nguồn trùng với ví đích", txnId),
                    txnId, "Hủy bỏ giao dịch chuyển tiền trùng ví hoặc cập nhật lại ví đích");
        }
        if (!walletMap.containsKey(srcId)) {
            valid &= recordIssue(issues, "BR-FIN-03", "TRANSFER_INCONSISTENCY",
                    String.format("Giao dịch chuyển tiền [%s] trỏ tới ví nguồn [%s] không tồn tại", txnId, srcId),
                    txnId, "Kiểm tra lại tính hợp lệ của ví nguồn");
        }
        if (!walletMap.containsKey(dstId)) {
            valid &= recordIssue(issues, "BR-FIN-03", "TRANSFER_INCONSISTENCY",
                    String.format("Giao dịch chuyển tiền [%s] trỏ tới ví đích [%s] không tồn tại", txnId, dstId),
                    txnId, "Kiểm tra lại tính hợp lệ của ví đích");
        }
        if (trf.getFee().isPresent()) {
            TransactionFee fee = trf.getFee().get();
            if (!fee.getWallet().getId().equals(srcId)) {
                valid &= recordIssue(issues, "BR-FIN-02", "TRANSFER_INCONSISTENCY",
                        String.format("Phí của giao dịch chuyển tiền [%s] không ghi nhận vào ví nguồn", txnId),
                        txnId, "Gán lại mã ví của phụ phí chuyển tiền khớp với ví nguồn");
            }
            valid &= checkFeeCategory(fee, categoryMap, "TRANSFER_INCONSISTENCY", txnId, "của chuyển tiền [" + txnId + "]", issues);
        }
        return valid;
    }

    private static boolean checkFees(
            List<Transaction> transactions,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = true;
        for (Transaction txn : transactions) {
            if (txn.getFee().isEmpty()) {
                continue;
            }
            TransactionFee fee = txn.getFee().get();
            String feeId = fee.getFeeId().toString();
            if (!fee.getParentTransactionId().equals(txn.getTransactionId())) {
                consistent &= recordIssue(issues, "BR-FIN-02", "FEE_INCONSISTENCY",
                        String.format("Phụ phí [%s] có mã giao dịch cha không khớp với giao dịch [%s]", feeId, txn.getTransactionId()),
                        feeId, "Cập nhật lại mã giao dịch cha cho phụ phí");
            }
            if (txn instanceof IncomeExpenseTransaction ie && !fee.getWallet().getId().equals(ie.getWallet().getId())) {
                consistent &= recordIssue(issues, "BR-FIN-02", "FEE_INCONSISTENCY",
                        String.format("Phụ phí [%s] có ví thanh toán không trùng với ví của giao dịch thu chi [%s]", feeId, txn.getTransactionId()),
                        feeId, "Gán lại mã ví của phụ phí khớp với ví của giao dịch");
            }
            consistent &= checkFeeCategory(fee, categoryMap, "FEE_INCONSISTENCY", feeId, "", issues);
        }
        return consistent;
    }

    private static boolean checkFeeCategory(
            TransactionFee fee,
            Map<UUID, Category> categoryMap,
            String issueType,
            Object entityId,
            String contextSuffix,
            List<ReconciliationIssueResponseDTO> issues) {

        Category feeCat = categoryMap.get(fee.getCategory().getCategoryId());
        String context = (contextSuffix == null || contextSuffix.isEmpty()) ? "" : " " + contextSuffix;
        if (feeCat == null) {
            return recordIssue(issues, "BR-FIN-04", issueType,
                    String.format("Phụ phí [%s]%s trỏ tới danh mục không tồn tại", fee.getFeeId(), context),
                    entityId, "Gán danh mục chi phí hợp lệ cho phụ phí");
        }
        if (feeCat.getTransactionType() != TransactionType.EXPENSE) {
            return recordIssue(issues, "BR-FIN-04", issueType,
                    String.format("Danh mục của phụ phí [%s]%s phải thuộc loại EXPENSE", fee.getFeeId(), context),
                    entityId, "Chuyển danh mục phụ phí sang danh mục loại chi phí");
        }
        return true;
    }

    private static boolean recordIssue(
            List<ReconciliationIssueResponseDTO> issues,
            String ruleCode,
            String issueType,
            String message,
            Object entityId,
            String suggestion) {

        issues.add(new ReconciliationIssueResponseDTO(
                "ERROR", ruleCode, issueType, message, Objects.toString(entityId, ""), suggestion));
        return false;
    }
    // endregion
}
