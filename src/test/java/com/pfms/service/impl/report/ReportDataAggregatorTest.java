package com.pfms.service.impl.report;

import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReportDataAggregator Unit Tests")
class ReportDataAggregatorTest {

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<ReportDataAggregator> constructor = ReportDataAggregator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("aggregateCashFlow should initialize 12 month buckets with zero averages when empty")
    void aggregateCashFlowShouldReturnEmptyBuckets() {
        YearMonth startYm = YearMonth.of(2025, 2);
        CashFlowReportResponseDTO response = ReportDataAggregator.aggregateCashFlow(
                startYm, Collections.emptyList());

        assertNotNull(response);
        assertEquals(12, response.getMonths().size());
        assertEquals(BigDecimal.ZERO.setScale(2), response.getAvgIncome());
        assertEquals(BigDecimal.ZERO.setScale(2), response.getAvgExpense());
        assertEquals(BigDecimal.ZERO.setScale(2), response.getAvgNetCashFlow());
    }

    @Test
    @DisplayName("calculateSpendingStructure should return empty groups when total amount is zero")
    void calculateSpendingStructureShouldReturnEmptyGroupsWhenZero() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);
        LocalDate priorStartDate = LocalDate.of(2025, 12, 1);

        SpendingStructureReportResponseDTO response = ReportDataAggregator.calculateSpendingStructure(
                TransactionType.EXPENSE,
                startDate,
                endDate,
                priorStartDate,
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap());

        assertNotNull(response);
        assertEquals("EXPENSE", response.getTransactionType());
        assertEquals(BigDecimal.ZERO.setScale(2), response.getTotalAmount());
        assertEquals(BigDecimal.ZERO.setScale(2), response.getPreviousTotalAmount());
        assertTrue(response.getGroups().isEmpty());
    }
}
