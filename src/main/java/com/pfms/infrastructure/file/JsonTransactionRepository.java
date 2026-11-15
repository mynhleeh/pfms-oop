package com.pfms.infrastructure.file;

import com.pfms.common.util.Preconditions;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.infrastructure.file.support.JsonDataStore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Cài đặt kho lưu trữ tệp JSON cho thực thể Giao dịch ({@link TransactionRepository}).
 * Quản lý đồng bộ danh sách giao dịch đa hình (thu chi, chuyển tiền) và phí liên kết
 * qua {@link JsonDataStore} theo chuẩn OOD 4.2.2.
 */
public class JsonTransactionRepository implements TransactionRepository {

    // region State
    private final JsonDataStore dataStore;
    // endregion

    // region Constructors
    public JsonTransactionRepository(JsonDataStore dataStore) {
        this.dataStore = Objects.requireNonNull(dataStore, "JsonDataStore không được để null");
    }
    // endregion

    // region Commands
    @Override
    public Transaction save(Transaction entity) {
        Objects.requireNonNull(entity, "Thực thể giao dịch không được để null");
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            List<Transaction> existing = current.getTransactions();
            List<Transaction> updated = new ArrayList<>(existing.size() + 1);
            boolean replaced = false;

            for (Transaction tx : existing) {
                if (tx.getTransactionId().equals(entity.getTransactionId())) {
                    updated.add(entity);
                    replaced = true;
                } else {
                    updated.add(tx);
                }
            }

            if (!replaced) {
                updated.add(entity);
            }

            BackupPayload next = new BackupPayload(
                    current.getWallets(),
                    current.getNeedGroups(),
                    current.getCategories(),
                    updated,
                    current.getAdjustments());
            dataStore.save(next);
            return entity;
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "Mã giao dịch không được để null");
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            List<Transaction> existing = current.getTransactions();
            List<Transaction> updated = new ArrayList<>(existing.size());
            boolean removed = false;

            for (Transaction tx : existing) {
                if (tx.getTransactionId().equals(id)) {
                    removed = true;
                } else {
                    updated.add(tx);
                }
            }

            if (removed) {
                BackupPayload next = new BackupPayload(
                        current.getWallets(),
                        current.getNeedGroups(),
                        current.getCategories(),
                        updated,
                        current.getAdjustments());
                dataStore.save(next);
            }
            return removed;
        }
    }

    @Override
    public void deleteAll() {
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            BackupPayload next = new BackupPayload(
                    current.getWallets(),
                    current.getNeedGroups(),
                    current.getCategories(),
                    Collections.emptyList(),
                    current.getAdjustments());
            dataStore.save(next);
        }
    }
    // endregion

    // region Queries
    @Override
    public Optional<Transaction> findById(UUID id) {
        Objects.requireNonNull(id, "Mã giao dịch không được để null");
        return dataStore.getPayload().getTransactions().stream()
                .filter(tx -> tx.getTransactionId().equals(id))
                .findFirst();
    }

    @Override
    public List<Transaction> findAll() {
        return dataStore.getPayload().getTransactions();
    }

    @Override
    public boolean existsById(UUID id) {
        Objects.requireNonNull(id, "Mã giao dịch không được để null");
        return dataStore.getPayload().getTransactions().stream()
                .anyMatch(tx -> tx.getTransactionId().equals(id));
    }

    @Override
    public List<Transaction> findByWalletId(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");
        return findByWalletId(walletId.toString());
    }

    @Override
    public List<Transaction> findByWalletId(String walletId) {
        String targetId = Preconditions.requireNonBlank(walletId, "Mã ví không được để null hoặc rỗng");
        return dataStore.getPayload().getTransactions().stream()
                .filter(tx -> isTransactionRelatedToWallet(tx, targetId))
                .sorted(Transaction.DESCENDING_ORDER)
                .toList();
    }

    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return dataStore.getPayload().getTransactions().stream()
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
        return dataStore.getPayload().getTransactions().stream()
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
        return dataStore.getPayload().getTransactions().stream()
                .filter(tx -> isTransactionRelatedToCategory(tx, categoryId))
                .sorted(Transaction.DESCENDING_ORDER)
                .toList();
    }

    @Override
    public boolean existsByCategoryId(UUID categoryId) {
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        return dataStore.getPayload().getTransactions().stream()
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
