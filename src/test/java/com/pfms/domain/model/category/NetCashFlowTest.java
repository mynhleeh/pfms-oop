package com.pfms.domain.model.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for NetCashFlow Value Object")
class NetCashFlowTest {

    // =========================================================================
    // Nhóm 1: Happy Path Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 1: Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo dòng tiền dương (thặng dư tài chính)")
        void testPositiveNetCashFlow() {
            NetCashFlow flow = new NetCashFlow(new BigDecimal("15000000.00"));

            assertEquals(new BigDecimal("15000000.00"), flow.getAmount());
            assertEquals("VNĐ", flow.getCurrency());
            assertTrue(flow.isPositive());
            assertFalse(flow.isNegative());
            assertFalse(flow.isZero());
        }

        @Test
        @DisplayName("Khởi tạo dòng tiền âm (thâm hụt tài chính)")
        void testNegativeNetCashFlow() {
            NetCashFlow flow = new NetCashFlow(new BigDecimal("-5000000.00"));

            assertEquals(new BigDecimal("-5000000.00"), flow.getAmount());
            assertEquals("VNĐ", flow.getCurrency());
            assertFalse(flow.isPositive());
            assertTrue(flow.isNegative());
            assertFalse(flow.isZero());
        }

        @Test
        @DisplayName("Khởi tạo dòng tiền cân bằng bằng không")
        void testZeroNetCashFlow() {
            NetCashFlow flow = new NetCashFlow(BigDecimal.ZERO);

            assertEquals(new BigDecimal("0.00"), flow.getAmount());
            assertFalse(flow.isPositive());
            assertFalse(flow.isNegative());
            assertTrue(flow.isZero());
        }

        @Test
        @DisplayName("Khởi tạo qua static factory method of(income, expense)")
        void testFactoryMethodOf() {
            BigDecimal income = new BigDecimal("20000000.00");
            BigDecimal expense = new BigDecimal("12000000.00");

            NetCashFlow flow = NetCashFlow.of(income, expense);
            assertEquals(new BigDecimal("8000000.00"), flow.getAmount());
            assertTrue(flow.isPositive());

            NetCashFlow deficit = NetCashFlow.of(new BigDecimal("10000000.00"), new BigDecimal("15000000.00"));
            assertEquals(new BigDecimal("-5000000.00"), deficit.getAmount());
            assertTrue(deficit.isNegative());
        }
    }

    // =========================================================================
    // Nhóm 2: Boundary & Precision Tests (Banker's Rounding HALF_EVEN)
    // =========================================================================
    @Nested
    @DisplayName("Group 2: Boundary and Precision Tests")
    class BoundaryAndPrecisionTests {

        @ParameterizedTest(name = "Làm tròn Banker's Rounding: {0} -> {1}")
        @CsvSource({
                "100.125, 100.12",  // 2 là số chẵn -> làm tròn xuống 100.12
                "100.135, 100.14",  // 3 là số lẻ -> làm tròn lên 100.14
                "100.145, 100.14",  // 4 là số chẵn -> làm tròn xuống 100.14
                "100.155, 100.16",  // 5 là số lẻ -> làm tròn lên 100.16
                "-100.125, -100.12",
                "-100.135, -100.14"
        })
        @DisplayName("Tuân thủ quy tắc làm tròn nửa chẵn HALF_EVEN (Banker's Rounding)")
        void testBankersRoundingHalfEven(String input, String expected) {
            NetCashFlow flow = new NetCashFlow(new BigDecimal(input));
            assertEquals(new BigDecimal(expected), flow.getAmount());
        }

        @Test
        @DisplayName("Quy mô số thập phân luôn cố định là 2")
        void testFixedScaleOfTwo() {
            NetCashFlow flow = new NetCashFlow(new BigDecimal("100"));
            assertEquals(2, flow.getAmount().scale());
            assertEquals(new BigDecimal("100.00"), flow.getAmount());
        }
    }

    // =========================================================================
    // Nhóm 3: Validation & Negative Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 3: Validation and Negative Tests")
    class ValidationAndNegativeTests {

        @Test
        @DisplayName("Từ chối khởi tạo với amount null")
        void testNullAmountThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class, () -> new NetCashFlow(null));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối of() khi totalIncome hoặc totalExpense là null")
        void testNullParamsInFactoryThrowsException() {
            BusinessException ex1 = assertThrows(BusinessException.class,
                    () -> NetCashFlow.of(null, new BigDecimal("100")));
            assertEquals(ErrorCode.ERR_FIN_001, ex1.getErrorCode());

            BusinessException ex2 = assertThrows(BusinessException.class,
                    () -> NetCashFlow.of(new BigDecimal("100"), null));
            assertEquals(ErrorCode.ERR_FIN_001, ex2.getErrorCode());
        }
    }

    // =========================================================================
    // Nhóm 4: Format & String Representation Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 4: Format Tests")
    class FormatTests {

        @Test
        @DisplayName("Định dạng dương có dấu + phía trước")
        void testFormatPositive() {
            NetCashFlow flow = new NetCashFlow(new BigDecimal("12000000.00"));
            assertEquals("+12000000.00 VNĐ", flow.format());
        }

        @Test
        @DisplayName("Định dạng âm có dấu - duy nhất, không bị lỗi hai dấu trừ (--)")
        void testFormatNegativeDoesNotHaveDoubleMinus() {
            NetCashFlow flow = new NetCashFlow(new BigDecimal("-5000000.00"));
            assertEquals("-5000000.00 VNĐ", flow.format());
            assertFalse(flow.format().contains("--"), "Chuỗi format không được chứa hai dấu trừ liên tiếp");
        }

        @Test
        @DisplayName("Định dạng zero có dấu +0.00 VNĐ")
        void testFormatZero() {
            NetCashFlow flow = new NetCashFlow(BigDecimal.ZERO);
            assertEquals("+0.00 VNĐ", flow.format());
        }
    }

    // =========================================================================
    // Nhóm 5: Value Object Contract & Comparable Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 5: Value Object Contract Tests")
    class ValueObjectContractTests {

        @Test
        @DisplayName("Cài đặt interface Comparable<NetCashFlow>")
        void testImplementsComparable() {
            NetCashFlow flow1 = new NetCashFlow(new BigDecimal("1000.00"));
            NetCashFlow flow2 = new NetCashFlow(new BigDecimal("2000.00"));

            assertTrue(flow1 instanceof Comparable, "NetCashFlow phải implements Comparable<NetCashFlow>");
            assertTrue(flow1.compareTo(flow2) < 0);
            assertTrue(flow2.compareTo(flow1) > 0);
            assertEquals(0, flow1.compareTo(new NetCashFlow(new BigDecimal("1000.00"))));
        }

        @Test
        @DisplayName("Bình đẳng Value Object dựa trên giá trị đại số (equals & hashCode)")
        void testEqualsAndHashCode() {
            NetCashFlow flow1 = new NetCashFlow(new BigDecimal("500.00"));
            NetCashFlow flow2 = new NetCashFlow(new BigDecimal("500.000"));
            NetCashFlow flowDiff = new NetCashFlow(new BigDecimal("600.00"));

            assertEquals(flow1, flow2);
            assertEquals(flow1.hashCode(), flow2.hashCode());
            assertNotEquals(flow1, flowDiff);
            assertNotEquals(flow1, null);
            assertNotEquals(flow1, "other-type");
        }

        @Test
        @DisplayName("Phương thức toString mô tả đúng giá trị")
        void testToString() {
            NetCashFlow flow = new NetCashFlow(new BigDecimal("100.00"));
            assertTrue(flow.toString().contains("100.00"));
            assertTrue(flow.toString().contains("VNĐ"));
        }
    }
}

