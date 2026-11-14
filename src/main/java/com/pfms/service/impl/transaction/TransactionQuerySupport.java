package com.pfms.service.impl.transaction;

import com.pfms.service.impl.common.EntityLookupSupport;

import com.pfms.common.util.PaginationUtils;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Bộ hỗ trợ nội bộ lọc và truy vấn giao dịch thu chi.
 * Thực thi lọc đa tiêu chí theo ngày, ví, danh mục, từ khóa; phân trang và định dạng DTO phản hồi.
 */
final class TransactionQuerySupport {

    // region Constructors
    private TransactionQuerySupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static TransactionListResponseDTO filterTransactions(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            CategoryRepository categoryRepository,
            FilterTransactionsRequestDTO request) {

        List<IncomeExpenseTransaction> matchedTransactions = queryMatchingTransactions(transactionRepository, request);

        Money totalIncome = Money.ZERO;
        Money totalExpense = Money.ZERO;

        for (IncomeExpenseTransaction transaction : matchedTransactions) {
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
            if (transaction.getFee().isPresent()) {
                totalExpense = totalExpense.add(transaction.getFee().get().getAmount());
            }
        }

        Money netCashFlow = totalIncome.subtract(totalExpense);
        List<TransactionResponseDTO> pageItems = toPageItems(
                matchedTransactions, request, walletRepository, categoryRepository);

        int totalRecords = matchedTransactions.size();
        int totalPages = PaginationUtils.calculateTotalPages(totalRecords, request.getPageSize());

        return new TransactionListResponseDTO(
                pageItems,
                totalIncome.getAmount(),
                totalExpense.getAmount(),
                netCashFlow.getAmount(),
                request.getPageNumber(),
                request.getPageSize(),
                totalRecords,
                totalPages);
    }

    static TransactionResponseDTO toResponseDTO(
            IncomeExpenseTransaction transaction,
            Wallet wallet,
            Category category,
            CategoryRepository categoryRepository) {

        TransactionFee fee = transaction.getFee().orElse(null);
        boolean hasFee = (fee != null);
        BigDecimal feeAmount = hasFee ? fee.getAmount().getAmount() : BigDecimal.ZERO;
        UUID feeCategoryId = hasFee ? fee.getCategory().getCategoryId() : null;
        String feeCategoryName = hasFee
                ? EntityLookupSupport.resolveForDisplay(categoryRepository, feeCategoryId, fee.getCategory()).getName()
                : "";

        return new TransactionResponseDTO(
                transaction.getTransactionId(),
                wallet.getId(),
                wallet.getName(),
                category.getCategoryId(),
                category.getName(),
                transaction.getAmount().getAmount(),
                transaction.getTransactionType().name(),
                transaction.getTransactionDate().getDate(),
                transaction.getNote(),
                hasFee,
                feeAmount,
                feeCategoryId,
                feeCategoryName,
                wallet.getCurrentBalance().getAmount(),
                transaction.getCreatedAt()
        );
    }
    // endregion

    // region Helpers
    private static List<IncomeExpenseTransaction> queryMatchingTransactions(
            TransactionRepository transactionRepository, FilterTransactionsRequestDTO request) {

        List<Transaction> candidates = (request.getStartDate() != null && request.getEndDate() != null)
                ? transactionRepository.findByDateRange(request.getStartDate(), request.getEndDate())
                : transactionRepository.findAll();

        return candidates.stream()
                .filter(IncomeExpenseTransaction.class::isInstance)
                .map(IncomeExpenseTransaction.class::cast)
                .filter(transaction -> matchesFilterCriteria(transaction, request))
                .sorted(Transaction.DESCENDING_ORDER)
                .toList();
    }

    private static boolean matchesFilterCriteria(
            IncomeExpenseTransaction transaction, FilterTransactionsRequestDTO request) {

        LocalDate txnDate = transaction.getTransactionDate().getDate();
        if (request.getStartDate() != null && txnDate.isBefore(request.getStartDate())) {
            return false;
        }
        if (request.getEndDate() != null && txnDate.isAfter(request.getEndDate())) {
            return false;
        }
        if (!request.getWalletIds().isEmpty()
                && !request.getWalletIds().contains(transaction.getWallet().getId())) {
            return false;
        }
        if (request.getTransactionType() != null && !request.getTransactionType().isEmpty()
                && !transaction.getTransactionType().name().equalsIgnoreCase(request.getTransactionType())) {
            return false;
        }
        if (!request.getCategoryIds().isEmpty()
                && !request.getCategoryIds().contains(transaction.getCategory().getCategoryId())) {
            return false;
        }
        return request.getKeyword() == null || request.getKeyword().isEmpty()
                || (transaction.getNote() != null
                && transaction.getNote().toLowerCase().contains(request.getKeyword().toLowerCase()));
    }

    private static List<TransactionResponseDTO> toPageItems(
            List<IncomeExpenseTransaction> matchedTransactions,
            FilterTransactionsRequestDTO request,
            WalletRepository walletRepository,
            CategoryRepository categoryRepository) {

        return PaginationUtils.slice(
                matchedTransactions, request.getPageNumber(), request.getPageSize())
                .stream()
                .map(txn -> toResponseDTO(
                        txn,
                        EntityLookupSupport.resolveForDisplay(walletRepository, txn.getWallet().getId(), txn.getWallet()),
                        EntityLookupSupport.resolveForDisplay(categoryRepository, txn.getCategory().getCategoryId(), txn.getCategory()),
                        categoryRepository))
                .toList();
    }
    // endregion
}

