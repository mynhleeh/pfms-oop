package com.pfms.service.impl.report;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.category.NetCashFlow;
import com.pfms.domain.model.category.SavingsRate;
import com.pfms.domain.model.category.SpendingStructureShare;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.CategoryShareResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Bộ xử lý nội bộ tính toán và tổng hợp số liệu báo cáo tài chính.
 * Thực thi thuật toán gom dòng tiền 12 tháng, đo lường tỷ trọng chi tiêu và so sánh biến động kỳ trước.
 */
final class ReportDataAggregator {

    // region Constants
    private static final int CASH_FLOW_MONTH_WINDOW = 12;
    private static final int INTERMEDIATE_SCALE = 6;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal NEGATIVE_HUNDRED = new BigDecimal("-100.00");
    private static final String UNKNOWN_GROUP_NAME = "Nhóm không xác định";

    private static final Comparator<NeedGroupShareResponseDTO> GROUP_SHARE_ORDER = Comparator
            .comparing(NeedGroupShareResponseDTO::getTotalAmount).reversed()
            .thenComparing(NeedGroupShareResponseDTO::getName)
            .thenComparing(NeedGroupShareResponseDTO::getNeedGroupId);

    private static final Comparator<CategoryShareResponseDTO> CATEGORY_SHARE_ORDER = Comparator
            .comparing(CategoryShareResponseDTO::getTotalAmount).reversed()
            .thenComparing(CategoryShareResponseDTO::getName)
            .thenComparing(CategoryShareResponseDTO::getCategoryId);
    // endregion

    // region Constructors
    private ReportDataAggregator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static CashFlowReportResponseDTO aggregateCashFlow(YearMonth startYm, List<Transaction> transactions) {
        Map<YearMonth, MonthlyBucket> buckets = initMonthBuckets(startYm);
        if (transactions != null) {
            for (Transaction tx : transactions) {
                accumulateCashFlowTransaction(tx, buckets);
            }
        }
        return buildCashFlowResponse(buckets);
    }

    static SpendingStructureReportResponseDTO calculateSpendingStructure(
            TransactionType targetType,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate priorStartDate,
            List<Transaction> transactions,
            Map<UUID, Category> liveCategoriesById,
            Map<UUID, NeedGroup> liveNeedGroupsById) {

        PeriodTotals current = new PeriodTotals(true);
        PeriodTotals prior = new PeriodTotals(false);
        accumulateStructurePeriods(
                priorStartDate, startDate, endDate, targetType, current, prior, liveCategoriesById, transactions);

        BigDecimal currentTotal = current.totalAmount.getAmount();
        BigDecimal priorTotal = prior.totalAmount.getAmount();
        ComparisonMetrics rootComparison = computeComparison(currentTotal, priorTotal);
        List<NeedGroupShareResponseDTO> groupDtos = current.totalAmount.isPositive()
                ? buildGroupShareDtos(current, prior, liveNeedGroupsById)
                : Collections.emptyList();

        return new SpendingStructureReportResponseDTO(
                targetType.getCode(),
                startDate,
                endDate,
                currentTotal,
                priorTotal,
                rootComparison.differenceAmount(),
                rootComparison.percentageChange(),
                rootComparison.isNew(),
                groupDtos);
    }
    // endregion

    // region Helpers
    private static Map<YearMonth, MonthlyBucket> initMonthBuckets(YearMonth startYm) {
        Map<YearMonth, MonthlyBucket> buckets = new LinkedHashMap<>(CASH_FLOW_MONTH_WINDOW);
        for (int i = 0; i < CASH_FLOW_MONTH_WINDOW; i++) {
            buckets.put(startYm.plusMonths(i), new MonthlyBucket());
        }
        return buckets;
    }

    private static void accumulateCashFlowTransaction(Transaction tx, Map<YearMonth, MonthlyBucket> buckets) {
        if (tx == null || tx.getTransactionDate() == null) {
            return;
        }
        MonthlyBucket bucket = buckets.get(YearMonth.from(tx.getTransactionDate().getDate()));
        if (bucket == null) {
            return;
        }

        if (tx instanceof IncomeExpenseTransaction iet) {
            if (iet.getTransactionType() == TransactionType.INCOME) {
                bucket.income = bucket.income.add(iet.getAmount());
            } else {
                bucket.expense = bucket.expense.add(iet.getAmount());
            }
        }
        tx.getFee().ifPresent(fee -> bucket.expense = bucket.expense.add(fee.getAmount()));
    }

    private static CashFlowReportResponseDTO buildCashFlowResponse(Map<YearMonth, MonthlyBucket> buckets) {
        List<MonthlyCashFlowResponseDTO> monthlyDtos = new ArrayList<>(buckets.size());
        Money sumIncome = Money.ZERO;
        Money sumExpense = Money.ZERO;

        for (Map.Entry<YearMonth, MonthlyBucket> entry : buckets.entrySet()) {
            MonthlyBucket bucket = entry.getValue();
            BigDecimal income = bucket.income.getAmount();
            BigDecimal expense = bucket.expense.getAmount();
            BigDecimal netCashFlow = NetCashFlow.of(income, expense).getAmount();
            BigDecimal savingsRate = SavingsRate.of(netCashFlow, income).getRate();

            monthlyDtos.add(new MonthlyCashFlowResponseDTO(
                    entry.getKey().toString(), income, expense, netCashFlow, savingsRate));

            sumIncome = sumIncome.add(bucket.income);
            sumExpense = sumExpense.add(bucket.expense);
        }

        Money sumNetCashFlow = sumIncome.subtract(sumExpense);
        return new CashFlowReportResponseDTO(
                monthlyDtos,
                sumIncome.divide(CASH_FLOW_MONTH_WINDOW).getAmount(),
                sumExpense.divide(CASH_FLOW_MONTH_WINDOW).getAmount(),
                sumNetCashFlow.divide(CASH_FLOW_MONTH_WINDOW).getAmount());
    }

    private static void accumulateStructurePeriods(
            LocalDate priorStartDate,
            LocalDate startDate,
            LocalDate endDate,
            TransactionType targetType,
            PeriodTotals current,
            PeriodTotals prior,
            Map<UUID, Category> liveCategoriesById,
            List<Transaction> transactions) {

        if (transactions == null) {
            return;
        }

        for (Transaction tx : transactions) {
            if (!isWithinRange(tx, priorStartDate, endDate)) {
                continue;
            }
            PeriodTotals target = tx.getTransactionDate().getDate().isBefore(startDate) ? prior : current;
            if (tx instanceof IncomeExpenseTransaction iet && iet.getTransactionType() == targetType) {
                target.add(iet.getCategory(), iet.getAmount(), liveCategoriesById);
            }
            if (targetType == TransactionType.EXPENSE) {
                tx.getFee().ifPresent(fee -> target.add(fee.getCategory(), fee.getAmount(), liveCategoriesById));
            }
        }
    }

    private static boolean isWithinRange(Transaction tx, LocalDate startDate, LocalDate endDate) {
        if (tx == null || tx.getTransactionDate() == null) {
            return false;
        }
        LocalDate txDate = tx.getTransactionDate().getDate();
        return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
    }

    private static List<NeedGroupShareResponseDTO> buildGroupShareDtos(
            PeriodTotals current, PeriodTotals prior, Map<UUID, NeedGroup> liveNeedGroupsById) {

        BigDecimal currentTotal = current.totalAmount.getAmount();
        Map<UUID, List<Category>> categoriesByGroup = current.resolvedCategories.values().stream()
                .collect(Collectors.groupingBy(Category::getNeedGroupId));
        List<NeedGroupShareResponseDTO> groupDtos = new ArrayList<>(current.groupTotals.size());

        for (Map.Entry<UUID, Money> groupEntry : current.groupTotals.entrySet()) {
            UUID groupId = groupEntry.getKey();
            BigDecimal groupAmount = groupEntry.getValue().getAmount();
            BigDecimal priorGroupAmount = prior.groupTotals.getOrDefault(groupId, Money.ZERO).getAmount();

            NeedGroup liveGroup = liveNeedGroupsById.get(groupId);
            String groupName = (liveGroup != null) ? liveGroup.getName() : UNKNOWN_GROUP_NAME;
            String groupStatus = (liveGroup != null)
                    ? liveGroup.getStatus().getCode()
                    : EntityStatus.ACTIVE.getCode();

            BigDecimal groupShare = SpendingStructureShare.of(
                    groupAmount, currentTotal, SpendingStructureShare.SCOPE_NEED_GROUP).getShare();
            ComparisonMetrics groupComparison = computeComparison(groupAmount, priorGroupAmount);

            groupDtos.add(new NeedGroupShareResponseDTO(
                    groupId,
                    groupName,
                    groupStatus,
                    groupAmount,
                    groupShare,
                    groupComparison.previousTotalAmount(),
                    groupComparison.differenceAmount(),
                    groupComparison.percentageChange(),
                    groupComparison.isNew(),
                    buildCategoryShareDtos(
                            categoriesByGroup.getOrDefault(groupId, Collections.emptyList()),
                            groupAmount,
                            current,
                            prior)));
        }

        groupDtos.sort(GROUP_SHARE_ORDER);
        return groupDtos;
    }

    private static List<CategoryShareResponseDTO> buildCategoryShareDtos(
            List<Category> categories, BigDecimal groupAmount, PeriodTotals current, PeriodTotals prior) {

        BigDecimal currentTotal = current.totalAmount.getAmount();
        List<CategoryShareResponseDTO> categoryDtos = new ArrayList<>(categories.size());

        for (Category category : categories) {
            UUID catId = category.getCategoryId();
            BigDecimal catAmount = current.categoryTotals.get(catId).getAmount();
            BigDecimal priorCatAmount = prior.categoryTotals.getOrDefault(catId, Money.ZERO).getAmount();

            BigDecimal catShareTotal = SpendingStructureShare.of(
                    catAmount, currentTotal, SpendingStructureShare.SCOPE_CATEGORY).getShare();
            BigDecimal catShareInGroup = SpendingStructureShare.of(
                    catAmount, groupAmount, SpendingStructureShare.SCOPE_CATEGORY).getShare();
            ComparisonMetrics catComparison = computeComparison(catAmount, priorCatAmount);

            categoryDtos.add(new CategoryShareResponseDTO(
                    catId,
                    category.getName(),
                    category.getStatus().getCode(),
                    catAmount,
                    catShareTotal,
                    catShareInGroup,
                    catComparison.previousTotalAmount(),
                    catComparison.differenceAmount(),
                    catComparison.percentageChange(),
                    catComparison.isNew()));
        }

        categoryDtos.sort(CATEGORY_SHARE_ORDER);
        return categoryDtos;
    }

    private static ComparisonMetrics computeComparison(BigDecimal current, BigDecimal prior) {
        BigDecimal diff = current.subtract(prior);
        if (prior.signum() > 0) {
            BigDecimal pct = (current.signum() == 0)
                    ? NEGATIVE_HUNDRED
                    : diff.divide(prior, INTERMEDIATE_SCALE, Money.DEFAULT_ROUNDING)
                            .multiply(HUNDRED)
                            .setScale(Money.COMPUTATION_SCALE, Money.DEFAULT_ROUNDING);
            return new ComparisonMetrics(prior, diff, pct, false);
        }
        return new ComparisonMetrics(prior, diff, Money.ZERO.getAmount(), current.signum() > 0);
    }

    private static final class MonthlyBucket {
        private Money income = Money.ZERO;
        private Money expense = Money.ZERO;
    }

    private static final class PeriodTotals {
        private Money totalAmount = Money.ZERO;
        private final Map<UUID, Money> categoryTotals = new HashMap<>();
        private final Map<UUID, Money> groupTotals = new HashMap<>();
        private final Map<UUID, Category> resolvedCategories;

        private PeriodTotals(boolean trackCategories) {
            this.resolvedCategories = trackCategories ? new HashMap<>() : null;
        }

        private void add(Category snapshotCategory, Money amount, Map<UUID, Category> liveCategoriesById) {
            if (snapshotCategory == null || amount == null || !amount.isPositive()) {
                return;
            }
            Category liveCategory = liveCategoriesById.getOrDefault(
                    snapshotCategory.getCategoryId(), snapshotCategory);
            UUID catId = liveCategory.getCategoryId();
            UUID groupId = liveCategory.getNeedGroupId();

            if (this.resolvedCategories != null) {
                this.resolvedCategories.put(catId, liveCategory);
            }
            this.totalAmount = this.totalAmount.add(amount);
            this.categoryTotals.merge(catId, amount, Money::add);
            this.groupTotals.merge(groupId, amount, Money::add);
        }
    }

    private record ComparisonMetrics(
            BigDecimal previousTotalAmount,
            BigDecimal differenceAmount,
            BigDecimal percentageChange,
            boolean isNew) {
    }
    // endregion
}

