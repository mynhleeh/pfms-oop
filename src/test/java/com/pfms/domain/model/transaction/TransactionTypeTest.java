package com.pfms.domain.model.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

@DisplayName("Unit Tests for TransactionType Enum")
class TransactionTypeTest {

    @Test
    @DisplayName("Should contain exactly 2 constants: INCOME and EXPENSE")
    void testEnumConstantsCount() {
        TransactionType[] values = TransactionType.values();
        assertEquals(2, values.length, "TransactionType phải có chính xác 2 hằng số theo thiết kế OOD");
        assertSame(TransactionType.INCOME, TransactionType.valueOf("INCOME"));
        assertSame(TransactionType.EXPENSE, TransactionType.valueOf("EXPENSE"));
    }

    @Test
    @DisplayName("Should return valid codes and Vietnamese display labels")
    void testStateAndGetters() {
        assertEquals("INCOME", TransactionType.INCOME.getCode());
        assertEquals("Thu nhập", TransactionType.INCOME.getDisplayLabel());

        assertEquals("EXPENSE", TransactionType.EXPENSE.getCode());
        assertEquals("Chi tiêu", TransactionType.EXPENSE.getDisplayLabel());
    }

    @Test
    @DisplayName("Should correctly identify income and expense via query methods")
    void testQueriesIsIncomeAndIsExpense() {
        assertTrue(TransactionType.INCOME.isIncome());
        assertFalse(TransactionType.INCOME.isExpense());

        assertTrue(TransactionType.EXPENSE.isExpense());
        assertFalse(TransactionType.EXPENSE.isIncome());
    }

    @Test
    @DisplayName("Should return the opposite transaction type and satisfy involution")
    void testOpposite() {
        assertSame(TransactionType.EXPENSE, TransactionType.INCOME.opposite());
        assertSame(TransactionType.INCOME, TransactionType.EXPENSE.opposite());

        assertSame(TransactionType.INCOME, TransactionType.INCOME.opposite().opposite(),
                "opposite() 2 lần phải trả về chính nó");
        assertSame(TransactionType.EXPENSE, TransactionType.EXPENSE.opposite().opposite(),
                "opposite() 2 lần phải trả về chính nó");
    }

    @Test
    @DisplayName("Should correctly evaluate compatibility with category types and handle null safely")
    void testIsCompatibleWith() {
        // Cùng loại -> compatible
        assertTrue(TransactionType.INCOME.isCompatibleWith(TransactionType.INCOME));
        assertTrue(TransactionType.EXPENSE.isCompatibleWith(TransactionType.EXPENSE));

        // Khác loại -> incompatible
        assertFalse(TransactionType.INCOME.isCompatibleWith(TransactionType.EXPENSE));
        assertFalse(TransactionType.EXPENSE.isCompatibleWith(TransactionType.INCOME));

        // Null safety -> return false without throwing NPE
        assertFalse(TransactionType.INCOME.isCompatibleWith(null));
        assertFalse(TransactionType.EXPENSE.isCompatibleWith(null));
    }

    @ParameterizedTest(name = "fromString(\"{0}\") should resolve to INCOME")
    @ValueSource(strings = { "INCOME", "income", "Income", "  INCOME  ", "  inCOME \t" })
    @DisplayName("Should parse valid strings to INCOME case-insensitively with trimming")
    void testFromStringValidIncome(String input) {
        assertSame(TransactionType.INCOME, TransactionType.fromString(input));
    }

    @ParameterizedTest(name = "fromString(\"{0}\") should resolve to EXPENSE")
    @ValueSource(strings = { "EXPENSE", "expense", "Expense", "  EXPENSE  ", "  exPENSE \n" })
    @DisplayName("Should parse valid strings to EXPENSE case-insensitively with trimming")
    void testFromStringValidExpense(String input) {
        assertSame(TransactionType.EXPENSE, TransactionType.fromString(input));
    }

    @ParameterizedTest(name = "fromString(\"{0}\") should throw IllegalArgumentException")
    @ValueSource(strings = { "", "   ", "\t\n", "TRANSFER", "LOAN", "123", "IN_COME" })
    @DisplayName("Should throw IllegalArgumentException for blank or invalid strings")
    void testFromStringInvalid(String input) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> TransactionType.fromString(input));
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when fromString argument is null")
    void testFromStringNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> TransactionType.fromString(null));
        assertTrue(ex.getMessage().contains("không được để trống"));
    }

    @Test
    @DisplayName("Should parse case-insensitively and reject non-ASCII dotted-I independently of default Locale")
    void testFromStringLocaleIndependent() {
        Locale previousLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            assertSame(TransactionType.INCOME, TransactionType.fromString("income"));
            assertSame(TransactionType.EXPENSE, TransactionType.fromString("expense"));
            assertThrows(IllegalArgumentException.class,
                    () -> TransactionType.fromString("\u0130NCOME"));
        } finally {
            Locale.setDefault(previousLocale);
        }
    }
}

