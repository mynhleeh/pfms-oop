package com.pfms.domain.model.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Money Value Object")
class MoneyTest {

    @Test
    @DisplayName("Should create Money from string, long, BigDecimal with scale 2 and VNĐ currency")
    void testCreationAndScale() {
        Money m1 = Money.of(100000L);
        Money m2 = Money.of("100000.00");
        Money m3 = Money.of(new BigDecimal("100000"));

        assertEquals(m1, m2);
        assertEquals(m2, m3);
        assertEquals("VNĐ", m1.getCurrency());
        assertEquals("100000.00 VNĐ", m1.toString());
        assertEquals(new BigDecimal("100000.00"), m1.getAmount());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creation string is invalid or null")
    void testInvalidCreation() {
        assertThrows(IllegalArgumentException.class, () -> Money.of((String) null));
        assertThrows(IllegalArgumentException.class, () -> Money.of(""));
        assertThrows(IllegalArgumentException.class, () -> Money.of("   "));
        assertThrows(IllegalArgumentException.class, () -> Money.of("invalid_number"));
        assertThrows(IllegalArgumentException.class, () -> Money.of((BigDecimal) null));
    }

    @Test
    @DisplayName("Should perform exact addition and subtraction while preserving immutability")
    void testAdditionAndSubtraction() {
        Money initial = Money.of("150000.50");
        Money delta = Money.of("50000.25");

        Money sum = initial.add(delta);
        Money diff = initial.subtract(delta);

        assertEquals(Money.of("200000.75"), sum);
        assertEquals(Money.of("100000.25"), diff);
        assertEquals(Money.of("150000.50"), initial, "Original instance must remain immutable");

        assertThrows(IllegalArgumentException.class, () -> initial.add(null));
        assertThrows(IllegalArgumentException.class, () -> initial.subtract(null));
    }

    @Test
    @DisplayName("Should perform multiplication correctly")
    void testMultiplication() {
        Money m = Money.of("50000.00");
        assertEquals(Money.of("150000.00"), m.multiply(3L));
        assertEquals(Money.of("75000.00"), m.multiply(new BigDecimal("1.5")));
        assertThrows(IllegalArgumentException.class, () -> m.multiply((BigDecimal) null));
    }

    @Test
    @DisplayName("Should apply Half-Even banker's rounding on division")
    void testDivisionWithBankersRounding() {
        Money m = Money.of("100.00");
        Money result = m.divide(3L);
        assertEquals(Money.of("33.33"), result);

        Money m1 = Money.of("2.50").divide(2L);
        assertEquals(Money.of("1.25"), m1);

        assertThrows(ArithmeticException.class, () -> m.divide(0L));
        assertThrows(ArithmeticException.class, () -> m.divide(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> m.divide((BigDecimal) null));
    }

    @Test
    @DisplayName("Should calculate absolute value correctly")
    void testAbsoluteValue() {
        Money negative = Money.of("-50000.00");
        Money positive = Money.of("50000.00");

        assertEquals(Money.of("50000.00"), negative.abs());
        assertEquals(Money.of("50000.00"), positive.abs());
        assertEquals(Money.ZERO, Money.ZERO.abs());
    }

    @Test
    @DisplayName("Should validate requireNonNegative invariant per BR-FIN-02")
    void testRequireNonNegative() {
        Money zero = Money.ZERO;
        Money positive = Money.of("1000.00");
        Money negative = Money.of("-0.01");

        assertDoesNotThrow(zero::requireNonNegative);
        assertDoesNotThrow(positive::requireNonNegative);
        assertThrows(IllegalStateException.class, negative::requireNonNegative);
    }

    @Test
    @DisplayName("Should perform algebraic comparisons correctly")
    void testComparisons() {
        Money small = Money.of("10000.00");
        Money large = Money.of("50000.00");
        Money negative = Money.of("-500.00");

        assertTrue(large.isGreaterThan(small));
        assertTrue(large.isGreaterThanOrEqual(small));
        assertTrue(large.isGreaterThanOrEqual(Money.of("50000.00")));
        assertTrue(small.isLessThan(large));
        assertTrue(small.isLessThanOrEqual(large));

        assertTrue(large.isPositive());
        assertFalse(Money.ZERO.isPositive());
        assertTrue(negative.isNegative());
        assertTrue(Money.ZERO.isZero());

        assertThrows(NullPointerException.class, () -> small.compareTo(null));
    }

    @Test
    @DisplayName("Should maintain value equality and hash code consistency across scale differences")
    void testEqualityAndHashCode() {
        Money a = Money.of("50000.0");
        Money b = Money.of("50000.000");
        Money c = Money.of("60000.00");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(null, a);
        assertNotEquals("50000.00 VNĐ", a);
    }

    @Test
    @DisplayName("Should format display as integer VND with dot grouping separator per BR-FIN-01")
    void testFormatDisplay() {
        Money m1 = Money.of("1250000.00");
        Money m2 = Money.of("500.00");
        Money m3 = Money.ZERO;

        assertEquals("1.250.000 VNĐ", m1.formatDisplay());
        assertEquals("500 VNĐ", m2.formatDisplay());
        assertEquals("0 VNĐ", m3.formatDisplay());
    }

    @Test
    @DisplayName("Should divide by BigDecimal factor using banker's rounding")
    void testDivisionByBigDecimal() {
        Money m = Money.of("100.00");
        assertEquals(Money.of("25.00"), m.divide(new BigDecimal("4.0")));
        assertEquals(Money.of("33.33"), m.divide(new BigDecimal("3.0")));
    }

    @Test
    @DisplayName("Should format negative amounts and zero correctly")
    void testFormatDisplayWithNegativeAndZero() {
        Money negative = Money.of("-50000.00");
        assertEquals("-50.000 VNĐ", negative.formatDisplay());
        assertEquals(Money.ZERO, Money.zero());
    }
}

