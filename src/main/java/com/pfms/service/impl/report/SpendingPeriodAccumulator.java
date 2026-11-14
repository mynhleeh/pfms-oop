package com.pfms.service.impl.report;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Bộ tích lũy nội bộ gom số liệu thu chi theo kỳ hiện tại và kỳ trước cho báo cáo cơ cấu.
 */
final class SpendingPeriodAccumulator {

    // region State
    private Money totalAmount = Money.ZERO;
    private final Map<UUID, Money> categoryTotals = new HashMap<>();
    private final Map<UUID, Money> groupTotals = new HashMap<>();
    private final Map<UUID, Category> resolvedCategories;
    // endregion

    // region Constructors
    SpendingPeriodAccumulator(boolean trackCategories) {
        this.resolvedCategories = trackCategories ? new HashMap<>() : null;
    }
    // endregion

    // region Operations
    static void accumulatePeriods(
            LocalDate priorStartDate,
            LocalDate startDate,
            LocalDate endDate,
            TransactionType targetType,
            SpendingPeriodAccumulator current,
            SpendingPeriodAccumulator prior,
            Map<UUID, Category> liveCategoriesById,
            List<Transaction> transactions) {

        if (transactions == null) {
            return;
        }
        for (Transaction tx : transactions) {
            if (!isWithinRange(tx, priorStartDate, endDate)) {
                continue;
            }
            SpendingPeriodAccumulator target = tx.getTransactionDate().getDate().isBefore(startDate) ? prior : current;
            if (tx instanceof IncomeExpenseTransaction iet && iet.getTransactionType() == targetType) {
                target.add(iet.getCategory(), iet.getAmount(), liveCategoriesById);
            }
            if (targetType == TransactionType.EXPENSE) {
                tx.getFee().ifPresent(fee -> target.add(fee.getCategory(), fee.getAmount(), liveCategoriesById));
            }
        }
    }

    Money getTotalAmount() {
        return totalAmount;
    }

    Map<UUID, Money> getGroupTotals() {
        return groupTotals;
    }

    Money getGroupTotal(UUID groupId) {
        return groupTotals.getOrDefault(groupId, Money.ZERO);
    }

    Money getCategoryTotal(UUID categoryId) {
        return categoryTotals.getOrDefault(categoryId, Money.ZERO);
    }

    Map<UUID, List<Category>> groupResolvedCategories() {
        return resolvedCategories.values().stream().collect(Collectors.groupingBy(Category::getNeedGroupId));
    }
    // endregion

    // region Helpers
    private static boolean isWithinRange(Transaction tx, LocalDate startDate, LocalDate endDate) {
        if (tx == null || tx.getTransactionDate() == null) {
            return false;
        }
        LocalDate txDate = tx.getTransactionDate().getDate();
        return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
    }

    private void add(Category snapshotCategory, Money amount, Map<UUID, Category> liveCategoriesById) {
        if (snapshotCategory == null || amount == null || !amount.isPositive()) {
            return;
        }
        Category liveCategory = liveCategoriesById.getOrDefault(snapshotCategory.getCategoryId(), snapshotCategory);
        UUID catId = liveCategory.getCategoryId();
        UUID groupId = liveCategory.getNeedGroupId();

        if (this.resolvedCategories != null) {
            this.resolvedCategories.put(catId, liveCategory);
        }
        this.totalAmount = this.totalAmount.add(amount);
        this.categoryTotals.merge(catId, amount, Money::add);
        this.groupTotals.merge(groupId, amount, Money::add);
    }
    // endregion
}

