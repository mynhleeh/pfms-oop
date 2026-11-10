package com.pfms.infrastructure.memory;

import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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
        if (walletId == null) {
            throw new IllegalArgumentException("Mã ví không được để null");
        }
        return findByWalletId(walletId.toString());
    }

    @Override
    public List<Transaction> findByWalletId(String walletId) {
        if (walletId == null || walletId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã ví không được để null hoặc rỗng");
        }
        String targetId = walletId.trim();
        return storage.values().stream()
                .filter(tx -> isTransactionRelatedToWallet(tx, targetId))
                .sorted(getDescendingOrderComparator())
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
                .sorted(getDescendingOrderComparator())
                .toList();
    }

    @Override
    public List<Transaction> findByDateRangeAndType(LocalDate startDate, LocalDate endDate, TransactionType type) {
        validateDateRange(startDate, endDate);
        if (type == null) {
            throw new IllegalArgumentException("Loại thu chi không được để null");
        }
        return storage.values().stream()
                .filter(tx -> {
                    if (!(tx instanceof IncomeExpenseTransaction iet)) {
                        return false;
                    }
                    LocalDate d = iet.getTransactionDate().getDate();
                    return iet.getTransactionType() == type && !d.isBefore(startDate) && !d.isAfter(endDate);
                })
                .sorted(getDescendingOrderComparator())
                .toList();
    }

    @Override
    public List<Transaction> findByCategoryId(UUID categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Mã danh mục không được để null");
        }
        return storage.values().stream()
                .filter(tx -> isTransactionRelatedToCategory(tx, categoryId))
                .sorted(getDescendingOrderComparator())
                .toList();
    }

    @Override
    public boolean existsByCategoryId(UUID categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Mã danh mục không được để null");
        }
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
            if (iet.getWallet().getId().equalsIgnoreCase(targetId)) {
                return true;
            }
        } else if (tx instanceof TransferTransaction tt) {
            if (tt.getSourceWallet().getId().equalsIgnoreCase(targetId)
                    || tt.getDestinationWallet().getId().equalsIgnoreCase(targetId)) {
                return true;
            }
        }
        return tx.getFee()
                .map(fee -> fee.getWallet().getId().equalsIgnoreCase(targetId))
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
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày biên khoảng thời gian không được để null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
    }

    private Comparator<Transaction> getDescendingOrderComparator() {
        return Comparator.comparing((Transaction tx) -> tx.getTransactionDate().getDate()).reversed()
                .thenComparing(Comparator.comparing(Transaction::getCreatedAt).reversed());
    }
    // endregion
}

