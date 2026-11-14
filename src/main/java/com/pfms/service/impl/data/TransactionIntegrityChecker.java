package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Bộ kiểm tra nội bộ tính toàn vẹn của giao dịch chuyển tiền và phụ phí đi kèm.
 */
final class TransactionIntegrityChecker {

    // region Constructors
    private TransactionIntegrityChecker() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static boolean checkTransfers(
            Map<UUID, Wallet> walletMap,
            List<Transaction> transactions,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = true;
        for (Transaction txn : transactions) {
            if (!(txn instanceof TransferTransaction trf)) {
                continue;
            }
            if (!checkSingleTransfer(trf, walletMap, categoryMap, issues)) {
                consistent = false;
            }
        }
        return consistent;
    }

    static boolean checkFees(
            List<Transaction> transactions,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = true;
        for (Transaction txn : transactions) {
            if (txn.getFee().isEmpty()) {
                continue;
            }
            TransactionFee fee = txn.getFee().get();
            String feeIdStr = fee.getFeeId().toString();
            if (!fee.getParentTransactionId().equals(txn.getTransactionId())) {
                consistent = recordIssue(issues, "BR-FIN-02", "FEE_INCONSISTENCY",
                        String.format("Phụ phí [%s] có mã giao dịch cha không khớp với giao dịch [%s]",
                                fee.getFeeId(), txn.getTransactionId()),
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
    // endregion

    // region Helpers
    private static boolean checkSingleTransfer(
            TransferTransaction trf,
            Map<UUID, Wallet> walletMap,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = true;
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
                    String.format("Giao dịch chuyển tiền [%s] trỏ tới ví nguồn [%s] không tồn tại",
                            trf.getTransactionId(), srcId),
                    txnIdStr, "Kiểm tra lại tính hợp lệ của ví nguồn");
        }
        if (!walletMap.containsKey(dstId)) {
            consistent = recordIssue(issues, "BR-FIN-03", "TRANSFER_INCONSISTENCY",
                    String.format("Giao dịch chuyển tiền [%s] trỏ tới ví đích [%s] không tồn tại",
                            trf.getTransactionId(), dstId),
                    txnIdStr, "Kiểm tra lại tính hợp lệ của ví đích");
        }
        if (trf.getFee().isPresent() && !checkTransferFee(trf, srcId, categoryMap, issues)) {
            consistent = false;
        }
        return consistent;
    }

    private static boolean checkTransferFee(
            TransferTransaction trf,
            UUID srcId,
            Map<UUID, Category> categoryMap,
            List<ReconciliationIssueResponseDTO> issues) {

        boolean consistent = true;
        String txnIdStr = trf.getTransactionId().toString();
        TransactionFee fee = trf.getFee().get();
        if (!fee.getWallet().getId().equals(srcId)) {
            consistent = recordIssue(issues, "BR-FIN-02", "TRANSFER_INCONSISTENCY",
                    String.format("Phí của giao dịch chuyển tiền [%s] không ghi nhận vào ví nguồn",
                            trf.getTransactionId()),
                    txnIdStr, "Gán lại mã ví của phụ phí chuyển tiền khớp với ví nguồn");
        }
        Category feeCat = categoryMap.get(fee.getCategory().getCategoryId());
        if (feeCat == null || feeCat.getTransactionType() != TransactionType.EXPENSE) {
            consistent = recordIssue(issues, "BR-FIN-04", "TRANSFER_INCONSISTENCY",
                    String.format("Phụ phí của chuyển tiền [%s] gắn với danh mục không hợp lệ hoặc không phải chi phí",
                            trf.getTransactionId()),
                    txnIdStr, "Gán lại danh mục chi phí hợp lệ cho phụ phí chuyển tiền");
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

