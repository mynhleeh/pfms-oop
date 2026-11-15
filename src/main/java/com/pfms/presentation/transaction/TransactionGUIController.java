package com.pfms.presentation.transaction;

import com.pfms.common.util.Preconditions;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.RecordTransactionRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.transaction.UpdateTransactionRequestDTO;
import com.pfms.presentation.common.InputValidator;
import com.pfms.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Bộ điều phối giao diện đồ họa cho phân hệ Quản lý Giao dịch (TXN).
 * Tiếp nhận dữ liệu biểu mẫu thu chi, kiểm thực số tiền, ngày tháng, phụ phí và điều phối TransactionService.
 */
public final class TransactionGUIController {

    // region State
    private final TransactionService transactionService;
    // endregion

    // region Constructors
    public TransactionGUIController(TransactionService transactionService) {
        this.transactionService = Objects.requireNonNull(transactionService, "TransactionService không được để null");
    }
    // endregion

    // region Operations
    // region Record
    public TransactionResponseDTO handleRecordTransaction(
            UUID walletId,
            UUID categoryId,
            String rawAmount,
            String rawDate,
            String rawNote,
            String rawFeeAmount,
            UUID feeCategoryId) {

        Preconditions.checkArgument(walletId != null, "Mã ví không được để null");
        Preconditions.checkArgument(categoryId != null, "Mã danh mục không được để null");

        BigDecimal amount = InputValidator.validatePositiveAmount(rawAmount, "Số tiền giao dịch");
        LocalDate date = InputValidator.parseDateOrToday(rawDate, "Ngày giao dịch");
        String note = InputValidator.validateTextLength(rawNote, 255, "Ghi chú");
        BigDecimal feeAmount = InputValidator.validateOptionalFee(rawFeeAmount, feeCategoryId);

        RecordTransactionRequestDTO request = new RecordTransactionRequestDTO(
                walletId, categoryId, amount, date, note, feeAmount, feeCategoryId);
        return this.transactionService.recordIncomeExpense(request);
    }

    public TransactionResponseDTO handleRecordTransaction(
            UUID walletId, UUID categoryId, String rawAmount, String rawDate, String rawNote) {

        return handleRecordTransaction(walletId, categoryId, rawAmount, rawDate, rawNote, "0", null);
    }
    // endregion

    // region Update
    public TransactionResponseDTO handleUpdateTransaction(
            UUID transactionId,
            UUID newWalletId,
            String rawNewAmount,
            String rawNewDate,
            UUID newCategoryId,
            String rawNewNote,
            String rawNewFeeAmount,
            UUID newFeeCategoryId) {

        Preconditions.checkArgument(transactionId != null, "Mã giao dịch không được để null");
        Preconditions.checkArgument(newWalletId != null, "Mã ví không được để null");
        Preconditions.checkArgument(newCategoryId != null, "Mã danh mục không được để null");

        BigDecimal newAmount = InputValidator.validatePositiveAmount(rawNewAmount, "Số tiền giao dịch");
        LocalDate newDate = InputValidator.parseDateOrToday(rawNewDate, "Ngày giao dịch");
        String newNote = InputValidator.validateTextLength(rawNewNote, 255, "Ghi chú");

        BigDecimal newFeeAmount = null;
        if (rawNewFeeAmount != null && !rawNewFeeAmount.isBlank()) {
            newFeeAmount = InputValidator.validateOptionalFee(rawNewFeeAmount, newFeeCategoryId);
        }

        UpdateTransactionRequestDTO request = new UpdateTransactionRequestDTO(
                transactionId, newWalletId, newAmount, newDate, newCategoryId, newNote, newFeeAmount, newFeeCategoryId);
        return this.transactionService.updateTransaction(request);
    }

    public TransactionResponseDTO handleUpdateTransaction(
            UUID transactionId,
            UUID newWalletId,
            String rawNewAmount,
            String rawNewDate,
            UUID newCategoryId,
            String rawNewNote) {

        return handleUpdateTransaction(
                transactionId, newWalletId, rawNewAmount, rawNewDate, newCategoryId, rawNewNote, null, null);
    }
    // endregion

    // region Delete
    public void handleDeleteTransaction(UUID transactionId) {
        Preconditions.checkArgument(transactionId != null, "Mã giao dịch không được để null");
        this.transactionService.deleteTransaction(transactionId);
    }
    // endregion

    // region Filter
    public TransactionListResponseDTO loadTransactions(FilterTransactionsRequestDTO filter) {
        return this.transactionService.filterTransactions(
                filter == null ? new FilterTransactionsRequestDTO() : filter);
    }

    public TransactionListResponseDTO filterTransactions(FilterTransactionsRequestDTO filter) {
        return loadTransactions(filter);
    }

    public TransactionListResponseDTO loadTransactions(
            String rawStartDate,
            String rawEndDate,
            Set<UUID> walletIds,
            String transactionType,
            Set<UUID> categoryIds,
            String rawKeyword,
            int pageNumber,
            int pageSize) {

        LocalDate startDate = InputValidator.parseOptionalDate(rawStartDate, "Ngày bắt đầu");
        LocalDate endDate = InputValidator.parseOptionalDate(rawEndDate, "Ngày kết thúc");
        Preconditions.requireValidDateRange(startDate, endDate);
        String keyword = InputValidator.validateTextLength(rawKeyword, 100, "Từ khóa tìm kiếm");

        FilterTransactionsRequestDTO filter = new FilterTransactionsRequestDTO(
                startDate, endDate, walletIds, transactionType, categoryIds, keyword, pageNumber, pageSize);
        return this.transactionService.filterTransactions(filter);
    }

    public TransactionListResponseDTO loadTransactions() {
        return this.transactionService.filterTransactions(new FilterTransactionsRequestDTO());
    }
    // endregion
    // endregion
}
