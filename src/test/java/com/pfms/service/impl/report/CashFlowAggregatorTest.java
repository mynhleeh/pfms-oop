package com.pfms.service.impl.report;

import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CashFlowAggregator Unit Tests")
class CashFlowAggregatorTest {

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<CashFlowAggregator> constructor =
                CashFlowAggregator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("aggregateCashFlow should initialize 12 month buckets with zero averages when empty")
    void aggregateCashFlowShouldReturnEmptyBuckets() {
        YearMonth startYm = YearMonth.of(2025, 2);
        CashFlowReportResponseDTO response = CashFlowAggregator.aggregateCashFlow(
                startYm, Collections.emptyList());

        assertNotNull(response);
        assertEquals(12, response.getMonths().size());
        assertEquals(BigDecimal.ZERO.setScale(2), response.getAvgIncome());
        assertEquals(BigDecimal.ZERO.setScale(2), response.getAvgExpense());
        assertEquals(BigDecimal.ZERO.setScale(2), response.getAvgNetCashFlow());
    }
}
