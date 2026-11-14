package com.pfms.domain.model.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for SavingsRate Value Object")
class SavingsRateTest {

    // =========================================================================
    // Nhóm 1: Happy Path Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 1: Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo tỷ lệ tiết kiệm dương chuẩn (40.00%)")
        void testPositiveSavingsRate() {
            SavingsRate rate = new SavingsRate(new BigDecimal("40.00"));

            assertEquals(new BigDecimal("40.00"), rate.getRate());
            assertEquals("%", rate.getUnit());
            assertTrue(rate.isPositive());
            assertFalse(rate.isNegative());
            assertFalse(rate.isZero());
        }

        @Test
        @DisplayName("Khởi tạo tỷ lệ tiết kiệm âm (-15.00%) khi chi vượt thu")
        void testNegativeSavingsRate() {
            SavingsRate rate = new SavingsRate(new BigDecimal("-15.00"));

            assertEquals(new BigDecimal("-15.00"), rate.getRate());
            assertEquals("%", rate.getUnit());
            assertFalse(rate.isPositive());
            assertTrue(rate.isNegative());
            assertFalse(rate.isZero());
        }

        @Test
        @DisplayName("Khởi tạo tỷ lệ tiết kiệm bằng không (0.00%)")
        void testZeroSavingsRate() {
            SavingsRate rate = new SavingsRate(BigDecimal.ZERO);

            assertEquals(new BigDecimal("0.00"), rate.getRate());
            assertFalse(rate.isPositive());
            assertFalse(rate.isNegative());
            assertTrue(rate.isZero());
        }

        @Test
        @DisplayName("Tính toán tỷ lệ tiết kiệm qua static factory method of(netCashFlow, totalIncome)")
        void testFactoryMethodOfStandard() {
            BigDecimal netCashFlow = new BigDecimal("8000000.00");
            BigDecimal totalIncome = new BigDecimal("20000000.00");

            SavingsRate rate = SavingsRate.of(netCashFlow, totalIncome);
            assertEquals(new BigDecimal("40.00"), rate.getRate());
        }
    }

    // =========================================================================
    // Nhóm 2: Boundary & Financial Arithmetic Tests (Tránh ArithmeticException)
    // =========================================================================
    @Nested
    @DisplayName("Group 2: Boundary and Precision Tests")
    class BoundaryAndPrecisionTests {

        @Test
        @DisplayName("Xử lý an toàn khi totalIncome = 0 (tránh lỗi chia cho không, trả về 0.00%)")
        void testZeroTotalIncomeReturnsZeroPercent() {
            SavingsRate rate = SavingsRate.of(new BigDecimal("5000000"), BigDecimal.ZERO);
            assertEquals(new BigDecimal("0.00"), rate.getRate());
        }

        @Test
        @DisplayName("Xử lý an toàn khi totalIncome âm (trả về 0.00%)")
        void testNegativeTotalIncomeReturnsZeroPercent() {
            SavingsRate rate = SavingsRate.of(new BigDecimal("5000000"), new BigDecimal("-1000000"));
            assertEquals(new BigDecimal("0.00"), rate.getRate());
        }

        @Test
        @DisplayName("Triệt tiêu lỗi ArithmeticException khi phép chia tạo số thập phân vô hạn (1/3)")
        void testNonTerminatingDecimalDivisionDoesNotThrow() {
            // netCashFlow = 100, totalIncome = 300 -> 33.33%
            BigDecimal netCashFlow = new BigDecimal("100");
            BigDecimal totalIncome = new BigDecimal("300");

            assertDoesNotThrow(() -> {
                SavingsRate rate = SavingsRate.of(netCashFlow, totalIncome);
                assertEquals(new BigDecimal("33.33"), rate.getRate());
            });
        }

        @ParameterizedTest(name = "Banker's Rounding: {0} -> {1}")
        @CsvSource({
                "25.125, 25.12",
                "25.135, 25.14",
                "-15.125, -15.12",
                "-15.135, -15.14"
        })
        @DisplayName("Tuân thủ quy tắc làm tròn nửa chẵn HALF_EVEN")
        void testBankersRounding(String input, String expected) {
            SavingsRate rate = new SavingsRate(new BigDecimal(input));
            assertEquals(new BigDecimal(expected), rate.getRate());
        }
    }

    // =========================================================================
    // Nhóm 3: Validation & Negative Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 3: Validation and Negative Tests")
    class ValidationAndNegativeTests {

        @Test
        @DisplayName("Từ chối khởi tạo với rate null")
        void testNullRateThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class, () -> new SavingsRate(null));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối of() khi netCashFlow hoặc totalIncome là null")
        void testNullParamsInFactoryThrowsException() {
            assertThrows(BusinessException.class, () -> SavingsRate.of(null, new BigDecimal("100")));
            assertThrows(BusinessException.class, () -> SavingsRate.of(new BigDecimal("100"), null));
        }
    }

    // =========================================================================
    // Nhóm 4: Format & String Representation Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 4: Format Tests")
    class FormatTests {

        @Test
        @DisplayName("Định dạng dương chuẩn 40.00%")
        void testFormatPositive() {
            SavingsRate rate = new SavingsRate(new BigDecimal("40.00"));
            assertEquals("40.00%", rate.format());
        }

        @Test
        @DisplayName("Định dạng âm không có hai dấu trừ liên tiếp (--)")
        void testFormatNegativeDoesNotHaveDoubleMinus() {
            SavingsRate rate = new SavingsRate(new BigDecimal("-10.00"));
            assertEquals("-10.00%", rate.format());
            assertFalse(rate.format().contains("--"), "Chuỗi format không được chứa hai dấu trừ liên tiếp");
        }

        @Test
        @DisplayName("Định dạng zero 0.00%")
        void testFormatZero() {
            SavingsRate rate = new SavingsRate(BigDecimal.ZERO);
            assertEquals("0.00%", rate.format());
        }
    }

    // =========================================================================
    // Nhóm 5: Value Object Contract & Comparable Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 5: Value Object Contract Tests")
    class ValueObjectContractTests {

        @Test
        @DisplayName("Cài đặt interface Comparable<SavingsRate>")
        void testImplementsComparable() {
            SavingsRate rate1 = new SavingsRate(new BigDecimal("10.00"));
            SavingsRate rate2 = new SavingsRate(new BigDecimal("20.00"));

            assertTrue(rate1 instanceof Comparable, "SavingsRate phải implements Comparable<SavingsRate>");
            assertTrue(rate1.compareTo(rate2) < 0);
            assertTrue(rate2.compareTo(rate1) > 0);
            assertEquals(0, rate1.compareTo(new SavingsRate(new BigDecimal("10.000"))));
        }

        @Test
        @DisplayName("Bình đẳng Value Object dựa trên giá trị đại số (equals & hashCode)")
        void testEqualsAndHashCode() {
            SavingsRate rate1 = new SavingsRate(new BigDecimal("25.00"));
            SavingsRate rate2 = new SavingsRate(new BigDecimal("25.000"));
            SavingsRate rateDiff = new SavingsRate(new BigDecimal("30.00"));

            assertEquals(rate1, rate2);
            assertEquals(rate1.hashCode(), rate2.hashCode());
            assertNotEquals(rate1, rateDiff);
            assertNotEquals(rate1, null);
            assertNotEquals(rate1, "string");
        }

        @Test
        @DisplayName("toString thể hiện rõ tỷ lệ và đơn vị")
        void testToString() {
            SavingsRate rate = new SavingsRate(new BigDecimal("15.50"));
            assertTrue(rate.toString().contains("15.50"));
            assertTrue(rate.toString().contains("%"));
        }
    }
}

