package com.pfms.domain.repository;

import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Kho lưu trữ chuyên biệt cho thực thể Giao dịch (Transaction). Cung cấp các
 * thao tác truy vấn theo ví, khoảng thời gian, danh mục và tổng hợp số tiền thu
 * chi.
 */
public interface TransactionRepository extends GenericRepository<Transaction, UUID> {

    // region Queries

    List<Transaction> findByWalletId(UUID walletId);

    List<Transaction> findByWalletId(String walletId);

    List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate);

    List<Transaction> findByDateRangeAndType(LocalDate startDate, LocalDate endDate, TransactionType type);

    List<Transaction> findByCategoryId(UUID categoryId);

    boolean existsByCategoryId(UUID categoryId);

    BigDecimal sumAmountByFilter(LocalDate startDate, LocalDate endDate, TransactionType type);
    // endregion
}
