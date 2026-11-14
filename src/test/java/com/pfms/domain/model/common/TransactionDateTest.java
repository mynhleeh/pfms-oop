package com.pfms.domain.model.common;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for TransactionDate Value Object")
class TransactionDateTest {

    @Nested
    @DisplayName("Group 1: Happy Path Scenarios")
    class HappyPathTests {

        @Test
        @DisplayName("Should initialize TransactionDate with valid past date")
        void testValidPastDateInitialization() {
            LocalDate date = LocalDate.of(2023, 5, 15);
            TransactionDate txDate = new TransactionDate(date);

            assertEquals(date, txDate.getDate());
            assertEquals("2023-05-15", txDate.getIsoString());
            assertEquals("2023-05", txDate.getYearMonth());
            assertEquals("2023-05-15", txDate.toString());
        }

        @Test
        @DisplayName("Should initialize TransactionDate with today's date")
        void testTodayDateInitialization() {
            LocalDate today = LocalDate.now();
            TransactionDate txDate = new TransactionDate(today);

            assertEquals(today, txDate.getDate());
            assertEquals(today.toString(), txDate.getIsoString());
            assertEquals(TransactionDate.now(), txDate);
        }

        @Test
        @DisplayName("Should create TransactionDate via static factory of(year, month, day)")
        void testStaticFactoryOfYMD() {
            TransactionDate txDate = TransactionDate.of(2022, 12, 31);

            assertEquals(LocalDate.of(2022, 12, 31), txDate.getDate());
            assertEquals("2022-12-31", txDate.getIsoString());
            assertEquals("2022-12", txDate.getYearMonth());
        }

        @Test
        @DisplayName("Should create TransactionDate via static factory of(LocalDate)")
        void testStaticFactoryOfLocalDate() {
            LocalDate date = LocalDate.of(2024, 1, 1);
            TransactionDate txDate = TransactionDate.of(date);

            assertEquals(date, txDate.getDate());
            assertEquals("2024-01-01", txDate.getIsoString());
        }

        @Test
        @DisplayName("Should parse valid ISO-8601 string correctly")
        void testParseValidIsoString() {
            TransactionDate txDate = TransactionDate.parse("2023-11-20");

            assertEquals(LocalDate.of(2023, 11, 20), txDate.getDate());
            assertEquals("2023-11-20", txDate.getIsoString());
            assertEquals("2023-11", txDate.getYearMonth());
        }

        @Test
        @DisplayName("Should trim leading and trailing whitespaces when parsing ISO string")
        void testParseIsoStringWithWhitespace() {
            TransactionDate txDate = TransactionDate.parse("   2023-11-20   ");

            assertEquals(LocalDate.of(2023, 11, 20), txDate.getDate());
            assertEquals("2023-11-20", txDate.getIsoString());
        }
    }

    @Nested
    @DisplayName("Group 2: Boundary & Ordering Tests")
    class BoundaryAndOrderingTests {

        @Test
        @DisplayName("Should correctly compare chronological order with compareTo")
        void testChronologicalOrdering() {
            TransactionDate d1 = TransactionDate.of(2023, 1, 1);
            TransactionDate d2 = TransactionDate.of(2023, 1, 2);
            TransactionDate d3 = TransactionDate.of(2023, 2, 1);

            assertTrue(d1.compareTo(d2) < 0);
            assertTrue(d2.compareTo(d1) > 0);
            assertEquals(0, d1.compareTo(TransactionDate.of(2023, 1, 1)));
            assertTrue(d2.compareTo(d3) < 0);
        }

        @Test
        @DisplayName("Should ensure compareTo consistency with equals per Effective Java Item 14")
        void testCompareToConsistencyWithEquals() {
            TransactionDate d1 = TransactionDate.of(2023, 5, 10);
            TransactionDate d2 = TransactionDate.of(2023, 5, 10);
            TransactionDate d3 = TransactionDate.of(2023, 5, 11);

            assertEquals(0, d1.compareTo(d2));
            assertEquals(d1, d2);
            assertTrue((d1.compareTo(d2) == 0) == d1.equals(d2));

            assertNotEquals(0, d1.compareTo(d3));
            assertNotEquals(d1, d3);
            assertTrue((d1.compareTo(d3) == 0) == d1.equals(d3));
        }

        @Test
        @DisplayName("Should verify isBefore, isAfter, isEqual on consecutive dates")
        void testRelativeComparisons() {
            TransactionDate earlier = TransactionDate.of(2023, 3, 14);
            TransactionDate later = TransactionDate.of(2023, 3, 15);

            assertTrue(earlier.isBefore(later));
            assertFalse(earlier.isAfter(later));
            assertFalse(earlier.isEqual(later));

            assertTrue(later.isAfter(earlier));
            assertFalse(later.isBefore(earlier));

            TransactionDate duplicate = TransactionDate.of(2023, 3, 14);
            assertTrue(earlier.isEqual(duplicate));
            assertFalse(earlier.isBefore(duplicate));
            assertFalse(earlier.isAfter(duplicate));
        }

        @Test
        @DisplayName("Should sort transaction dates chronologically using standard sort")
        void testSortingCollection() {
            TransactionDate d1 = TransactionDate.of(2023, 1, 10);
            TransactionDate d2 = TransactionDate.of(2023, 5, 20);
            TransactionDate d3 = TransactionDate.of(2022, 12, 31);

            List<TransactionDate> dates = new ArrayList<>(List.of(d1, d2, d3));
            Collections.sort(dates);

            assertEquals(List.of(d3, d1, d2), dates);
        }

        @Test
        @DisplayName("isEqual with null should return false")
        void testIsEqualWithNull() {
            TransactionDate date = TransactionDate.of(2023, 1, 1);
            assertFalse(date.isEqual(null));
        }
    }

    @Nested
    @DisplayName("Group 3: Negative, Zero & Null Values")
    class NullAndInvalidInputTests {

        @Test
        @DisplayName("Constructor should throw BusinessException with ERR_FIN_001 when date is null")
        void testConstructorNullDate() {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> new TransactionDate(null));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
            assertTrue(ex.getMessage().contains("null"));
        }

        @Test
        @DisplayName("Factory of(LocalDate) should throw BusinessException when date is null")
        void testFactoryNullLocalDate() {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> TransactionDate.of((LocalDate) null));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "invalid-date", "2023/05/15", "15-05-2023"})
        @DisplayName("parse should throw BusinessException for invalid string inputs")
        void testParseInvalidStrings(String input) {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> TransactionDate.parse(input));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("parse should throw BusinessException when input is null")
        void testParseNullString() {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> TransactionDate.parse(null));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Group 4: Temporal Invariants & Calendar Rules")
    class TemporalInvariantsTests {

        @Test
        @DisplayName("Should reject future dates with BusinessException (ERR_FIN_001)")
        void testRejectFutureDate() {
            LocalDate future = LocalDate.now().plusDays(1);
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> new TransactionDate(future));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
            assertTrue(ex.getMessage().contains("ngày hiện tại"));
        }

        @Test
        @DisplayName("Should reject future date via parse")
        void testRejectFutureDateInParse() {
            LocalDate future = LocalDate.now().plusDays(5);
            assertThrows(BusinessException.class, () -> TransactionDate.parse(future.toString()));
        }

        @Test
        @DisplayName("Should accept February 29 in leap year (e.g., 2024)")
        void testLeapYearValid() {
            assertDoesNotThrow(() -> TransactionDate.of(2024, 2, 29));
            TransactionDate leapDate = TransactionDate.of(2024, 2, 29);
            assertEquals("2024-02-29", leapDate.getIsoString());
        }

        @Test
        @DisplayName("Should reject February 29 in non-leap year (e.g., 2023)")
        void testNonLeapYearInvalid() {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> TransactionDate.of(2023, 2, 29));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
            assertTrue(ex.getMessage().contains("lịch Gregory"));
        }

        @ParameterizedTest
        @ValueSource(ints = {31, 32, 0, -1})
        @DisplayName("Should reject non-existent days in April (30-day month)")
        void testInvalidDaysInApril(int day) {
            assertThrows(BusinessException.class, () -> TransactionDate.of(2023, 4, day));
        }

        @Test
        @DisplayName("Should reject February 30 and 31")
        void testFebruary30And31() {
            assertThrows(BusinessException.class, () -> TransactionDate.of(2024, 2, 30));
            assertThrows(BusinessException.class, () -> TransactionDate.of(2024, 2, 31));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 13, -5})
        @DisplayName("Should reject invalid month numbers")
        void testInvalidMonth(int month) {
            assertThrows(BusinessException.class, () -> TransactionDate.of(2023, month, 15));
        }
    }

    @Nested
    @DisplayName("Group 5: State & Value Object Robustness")
    class StateAndRobustnessTests {

        @Test
        @DisplayName("Should satisfy equals and hashCode contract")
        void testEqualsAndHashCode() {
            TransactionDate d1 = TransactionDate.of(2023, 10, 10);
            TransactionDate d2 = TransactionDate.of(2023, 10, 10);
            TransactionDate d3 = TransactionDate.of(2023, 10, 11);

            // Reflexive
            assertEquals(d1, d1);
            assertEquals(d1.hashCode(), d1.hashCode());

            // Symmetric
            assertEquals(d1, d2);
            assertEquals(d2, d1);
            assertEquals(d1.hashCode(), d2.hashCode());

            // Non-equal
            assertNotEquals(d1, d3);
            assertNotEquals(d1, null);
            assertNotEquals(d1, "2023-10-10");
        }

        @Test
        @DisplayName("Should work correctly in hash-based collections")
        void testHashSetBehavior() {
            Set<TransactionDate> dateSet = new HashSet<>();
            dateSet.add(TransactionDate.of(2023, 6, 1));
            dateSet.add(TransactionDate.of(2023, 6, 1));
            dateSet.add(TransactionDate.of(2023, 6, 2));

            assertEquals(2, dateSet.size());
            assertTrue(dateSet.contains(TransactionDate.of(2023, 6, 1)));
            assertTrue(dateSet.contains(TransactionDate.of(2023, 6, 2)));
        }

        @Test
        @DisplayName("Comparison methods should throw NullPointerException when argument is null")
        void testNullComparisonExceptions() {
            TransactionDate date = TransactionDate.of(2023, 1, 1);

            assertThrows(NullPointerException.class, () -> date.compareTo(null));
            assertThrows(NullPointerException.class, () -> date.isBefore(null));
            assertThrows(NullPointerException.class, () -> date.isAfter(null));
        }
    }

    @Nested
    @DisplayName("Group 4: ofOrToday and validateRange Factory/Guard Methods")
    class OfOrTodayAndValidateRangeTests {

        @Test
        @DisplayName("ofOrToday(null) should return today's TransactionDate")
        void testOfOrTodayWithNullReturnsToday() {
            TransactionDate resolved = TransactionDate.ofOrToday(null);

            assertEquals(TransactionDate.now(), resolved);
            assertEquals(LocalDate.now(), resolved.getDate());
        }

        @Test
        @DisplayName("ofOrToday(validPastDate) should wrap that exact date and reject future dates")
        void testOfOrTodayWithExplicitAndFutureDates() {
            LocalDate pastDate = LocalDate.of(2024, 2, 29);
            assertEquals(TransactionDate.of(pastDate), TransactionDate.ofOrToday(pastDate));

            LocalDate tomorrow = LocalDate.now().plusDays(1);
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> TransactionDate.ofOrToday(tomorrow));
            assertEquals(ErrorCode.ERR_FIN_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("validateRange should accept null bounds, open-ended bounds, same day and leap day")
        void testValidateRangeAcceptsValidBounds() {
            LocalDate today = LocalDate.now();
            LocalDate leapDay = LocalDate.of(2024, 2, 29);

            assertDoesNotThrow(() -> TransactionDate.validateRange(null, null));
            assertDoesNotThrow(() -> TransactionDate.validateRange(leapDay, null));
            assertDoesNotThrow(() -> TransactionDate.validateRange(null, today));
            assertDoesNotThrow(() -> TransactionDate.validateRange(leapDay, leapDay));
            assertDoesNotThrow(() -> TransactionDate.validateRange(leapDay, today));
        }

        @Test
        @DisplayName("validateRange should reject future startDate or future endDate with ERR_FIN_001")
        void testValidateRangeRejectsFutureBounds() {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDate tomorrow = LocalDate.now().plusDays(1);

            BusinessException futureStartException = assertThrows(
                    BusinessException.class,
                    () -> TransactionDate.validateRange(tomorrow, null));
            assertEquals(ErrorCode.ERR_FIN_001, futureStartException.getErrorCode());

            BusinessException futureEndException = assertThrows(
                    BusinessException.class,
                    () -> TransactionDate.validateRange(yesterday, tomorrow));
            assertEquals(ErrorCode.ERR_FIN_001, futureEndException.getErrorCode());
        }

        @Test
        @DisplayName("validateRange should throw BusinessException ERR_FIN_001 when startDate is after endDate")
        void testValidateRangeRejectsInvertedBounds() {
            LocalDate startDate = LocalDate.now().minusDays(1);
            LocalDate endDate = LocalDate.now().minusDays(5);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> TransactionDate.validateRange(startDate, endDate));
            assertEquals(ErrorCode.ERR_FIN_001, exception.getErrorCode());
        }
    }
}

