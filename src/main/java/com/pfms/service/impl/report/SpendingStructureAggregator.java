package com.pfms.service.impl.report;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.category.SpendingStructureShare;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.presentation.dto.report.CategoryShareResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Bộ xử lý nội bộ tính toán tỷ trọng cơ cấu thu chi theo nhóm nhu cầu và so sánh biến động kỳ trước.
 */
final class SpendingStructureAggregator {

    // region Constants
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
    private SpendingStructureAggregator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static SpendingStructureReportResponseDTO calculateSpendingStructure(
            TransactionType targetType,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate priorStartDate,
            List<Transaction> transactions,
            Map<UUID, Category> liveCategoriesById,
            Map<UUID, NeedGroup> liveNeedGroupsById) {

        SpendingPeriodAccumulator current = new SpendingPeriodAccumulator(true);
        SpendingPeriodAccumulator prior = new SpendingPeriodAccumulator(false);
        SpendingPeriodAccumulator.accumulatePeriods(
                priorStartDate, startDate, endDate, targetType, current, prior, liveCategoriesById, transactions);

        BigDecimal currentTotal = current.getTotalAmount().getAmount();
        BigDecimal priorTotal = prior.getTotalAmount().getAmount();
        ComparisonMetrics rootComparison = computeComparison(currentTotal, priorTotal);
        List<NeedGroupShareResponseDTO> groupDtos = current.getTotalAmount().isPositive()
                ? buildGroupShareDtos(current, prior, liveNeedGroupsById)
                : Collections.emptyList();

        return new SpendingStructureReportResponseDTO(
                targetType.getCode(), startDate, endDate, currentTotal, priorTotal,
                rootComparison.differenceAmount(), rootComparison.percentageChange(),
                rootComparison.isNew(), groupDtos);
    }
    // endregion

    // region Helpers
    private static List<NeedGroupShareResponseDTO> buildGroupShareDtos(
            SpendingPeriodAccumulator current,
            SpendingPeriodAccumulator prior,
            Map<UUID, NeedGroup> liveNeedGroupsById) {

        BigDecimal currentTotal = current.getTotalAmount().getAmount();
        Map<UUID, List<Category>> categoriesByGroup = current.groupResolvedCategories();
        List<NeedGroupShareResponseDTO> groupDtos = new ArrayList<>(current.getGroupTotals().size());

        for (Map.Entry<UUID, Money> groupEntry : current.getGroupTotals().entrySet()) {
            UUID groupId = groupEntry.getKey();
            BigDecimal groupAmount = groupEntry.getValue().getAmount();
            BigDecimal priorGroupAmount = prior.getGroupTotal(groupId).getAmount();

            NeedGroup liveGroup = liveNeedGroupsById.get(groupId);
            String groupName = (liveGroup != null) ? liveGroup.getName() : UNKNOWN_GROUP_NAME;
            String groupStatus = (liveGroup != null)
                    ? liveGroup.getStatus().getCode()
                    : EntityStatus.ACTIVE.getCode();

            BigDecimal groupShare = SpendingStructureShare.of(
                    groupAmount, currentTotal, SpendingStructureShare.SCOPE_NEED_GROUP).getShare();
            ComparisonMetrics comparison = computeComparison(groupAmount, priorGroupAmount);

            groupDtos.add(new NeedGroupShareResponseDTO(
                    groupId, groupName, groupStatus, groupAmount, groupShare,
                    comparison.previousTotalAmount(), comparison.differenceAmount(),
                    comparison.percentageChange(), comparison.isNew(),
                    buildCategoryShareDtos(
                            categoriesByGroup.getOrDefault(groupId, Collections.emptyList()),
                            groupAmount, current, prior)));
        }

        groupDtos.sort(GROUP_SHARE_ORDER);
        return groupDtos;
    }

    private static List<CategoryShareResponseDTO> buildCategoryShareDtos(
            List<Category> categories,
            BigDecimal groupAmount,
            SpendingPeriodAccumulator current,
            SpendingPeriodAccumulator prior) {

        BigDecimal currentTotal = current.getTotalAmount().getAmount();
        List<CategoryShareResponseDTO> categoryDtos = new ArrayList<>(categories.size());

        for (Category category : categories) {
            UUID catId = category.getCategoryId();
            BigDecimal catAmount = current.getCategoryTotal(catId).getAmount();
            BigDecimal priorCatAmount = prior.getCategoryTotal(catId).getAmount();

            BigDecimal catShareTotal = SpendingStructureShare.of(
                    catAmount, currentTotal, SpendingStructureShare.SCOPE_CATEGORY).getShare();
            BigDecimal catShareInGroup = SpendingStructureShare.of(
                    catAmount, groupAmount, SpendingStructureShare.SCOPE_CATEGORY).getShare();
            ComparisonMetrics comparison = computeComparison(catAmount, priorCatAmount);

            categoryDtos.add(new CategoryShareResponseDTO(
                    catId, category.getName(), category.getStatus().getCode(),
                    catAmount, catShareTotal, catShareInGroup,
                    comparison.previousTotalAmount(), comparison.differenceAmount(),
                    comparison.percentageChange(), comparison.isNew()));
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

    private record ComparisonMetrics(
            BigDecimal previousTotalAmount,
            BigDecimal differenceAmount,
            BigDecimal percentageChange,
            boolean isNew) {
    }
    // endregion
}

