package com.pfms.infrastructure.memory;

import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.common.util.Preconditions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Kho lưu trữ Giao dịch trong bộ nhớ RAM phục vụ môi trường kiểm thử.
 */
public class MemoryTransactionRepository
        extends MemoryGenericRepository<Transaction, UUID>
        implements TransactionRepository {

    // region Constructors
    public MemoryTransactionRepository() {
        super(Transaction::getTransactionId);
    }
    // endregion

    // region Queries
    @Override
    public List<Transaction> findByWalletId(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");
        return findByWalletId(walletId.toString());
    }

    @Override
    public List<Transaction> findByWalletId(String walletId) {
        String targetId = Preconditions.requireNonBlank(walletId, "Mã ví không được để null hoặc rỗng");
        return storage.values().stream()
                .filter(tx -> isTransactionRelatedToWallet(tx, targetId))
                .sorted(Transaction.DESCENDING_ORDER)
                .toList();
    }

    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return storage.values().stream()
                .filter(tx -> {
                    LocalDate d = tx.getTransactionDate().getDate();
                    return !d.isBefore(startDate) && !d.isAfter(endDate);
                })
                .sorted(Transaction.DESCENDING_ORDER)
                .toList();
    }

    @Override
    public List<Transaction> findByDateRangeAndType(LocalDate startDate, LocalDate endDate, TransactionType type) {
        validateDateRange(startDate, endDate);
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return storage.values().stream()
                .filter(tx -> {
                    if (!(tx instanceof IncomeExpenseTransaction iet)) {
                        return false;
                    }
                    LocalDate d = iet.getTransactionDate().getDate();
                    return iet.getTransactionType() == type && !d.isBefore(startDate) && !d.isAfter(endDate);
                })
                .sorted(Transaction.DESCENDING_ORDER)
                .toList();
    }

    @Override
    public List<Transaction> findByCategoryId(UUID categoryId) {
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        return storage.values().stream()
                .filter(tx -> isTransactionRelatedToCategory(tx, categoryId))
                .sorted(Transaction.DESCENDING_ORDER)
                .toList();
    }

    @Override
    public boolean existsByCategoryId(UUID categoryId) {
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        return storage.values().stream()
                .anyMatch(tx -> isTransactionRelatedToCategory(tx, categoryId));
    }

    @Override
    public BigDecimal sumAmountByFilter(LocalDate startDate, LocalDate endDate, TransactionType type) {
        List<Transaction> matches = findByDateRangeAndType(startDate, endDate, type);
        return matches.stream()
                .map(tx -> tx.getAmount().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
    // endregion

    // region Helpers
    private boolean isTransactionRelatedToWallet(Transaction tx, String targetId) {
        if (tx instanceof IncomeExpenseTransaction iet) {
            if (iet.getWallet().getId().toString().equalsIgnoreCase(targetId)) {
                return true;
            }
        } else if (tx instanceof TransferTransaction tt) {
            if (tt.getSourceWallet().getId().toString().equalsIgnoreCase(targetId)
                    || tt.getDestinationWallet().getId().toString().equalsIgnoreCase(targetId)) {
                return true;
            }
        }
        return tx.getFee()
                .map(fee -> fee.getWallet().getId().toString().equalsIgnoreCase(targetId))
                .orElse(false);
    }

    private boolean isTransactionRelatedToCategory(Transaction tx, UUID categoryId) {
        if (tx instanceof IncomeExpenseTransaction iet) {
            if (iet.getCategory().getCategoryId().equals(categoryId)) {
                return true;
            }
        }
        return tx.getFee()
                .map(fee -> fee.getCategory().getCategoryId().equals(categoryId))
                .orElse(false);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        Preconditions.checkArgument(startDate != null && endDate != null, "Ngày biên khoảng thời gian không được để null");
        Preconditions.requireValidDateRange(startDate, endDate);
    }
    // endregion
}

