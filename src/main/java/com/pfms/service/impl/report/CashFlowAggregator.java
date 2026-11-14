package com.pfms.service.impl.report;

import com.pfms.domain.model.category.NetCashFlow;
import com.pfms.domain.model.category.SavingsRate;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bộ xử lý nội bộ gom và tính toán báo cáo dòng tiền 12 tháng liên tiếp.
 */
final class CashFlowAggregator {

    // region Constants
    private static final int CASH_FLOW_MONTH_WINDOW = 12;
    // endregion

    // region Constructors
    private CashFlowAggregator() {
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

    private static final class MonthlyBucket {
        private Money income = Money.ZERO;
        private Money expense = Money.ZERO;
    }
    // endregion
}

