package com.pfms.domain.model.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for SpendingStructureShare Value Object")
class SpendingStructureShareTest {

    // =========================================================================
    // Nhóm 1: Happy Path Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 1: Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo tỷ trọng hợp lệ cho NeedGroup")
        void testValidNeedGroupShare() {
            SpendingStructureShare share = new SpendingStructureShare(new BigDecimal("45.50"), "NeedGroup");

            assertEquals(new BigDecimal("45.50"), share.getShare());
            assertEquals("%", share.getUnit());
            assertEquals("NeedGroup", share.getScope());
            assertFalse(share.isZero());
        }

        @Test
        @DisplayName("Khởi tạo tỷ trọng hợp lệ cho Category")
        void testValidCategoryShare() {
            SpendingStructureShare share = new SpendingStructureShare(new BigDecimal("12.25"), "Category");

            assertEquals(new BigDecimal("12.25"), share.getShare());
            assertEquals("%", share.getUnit());
            assertEquals("Category", share.getScope());
            assertFalse(share.isZero());
        }

        @Test
        @DisplayName("Khởi tạo tỷ trọng bằng không (0.00%)")
        void testZeroShare() {
            SpendingStructureShare share = new SpendingStructureShare(BigDecimal.ZERO, "NeedGroup");

            assertEquals(new BigDecimal("0.00"), share.getShare());
            assertTrue(share.isZero());
        }

        @Test
        @DisplayName("Tính toán tỷ trọng qua static factory method of(itemAmount, totalAmount, scope)")
        void testFactoryMethodOf() {
            BigDecimal itemAmount = new BigDecimal("4000000.00");
            BigDecimal totalAmount = new BigDecimal("10000000.00");

            SpendingStructureShare share = SpendingStructureShare.of(itemAmount, totalAmount, "Category");
            assertEquals(new BigDecimal("40.00"), share.getShare());
            assertEquals("Category", share.getScope());
        }
    }

    // =========================================================================
    // Nhóm 2: Boundary & Financial Arithmetic Tests (Banker's Rounding & Div Zero)
    // =========================================================================
    @Nested
    @DisplayName("Group 2: Boundary and Precision Tests")
    class BoundaryAndPrecisionTests {

        @Test
        @DisplayName("Tỷ trọng biên 0.00% và 100.00% hợp lệ")
        void testBoundaryShareValues() {
            SpendingStructureShare min = new SpendingStructureShare(BigDecimal.ZERO, "NeedGroup");
            assertEquals(new BigDecimal("0.00"), min.getShare());

            SpendingStructureShare max = new SpendingStructureShare(new BigDecimal("100.00"), "Category");
            assertEquals(new BigDecimal("100.00"), max.getShare());
        }

        @Test
        @DisplayName("Xử lý an toàn khi totalAmount = 0 (trả về 0.00%, tránh chia cho không)")
        void testZeroTotalAmountReturnsZeroPercent() {
            SpendingStructureShare share = SpendingStructureShare.of(new BigDecimal("500000"), BigDecimal.ZERO, "NeedGroup");
            assertEquals(new BigDecimal("0.00"), share.getShare());
        }

        @Test
        @DisplayName("Triệt tiêu lỗi ArithmeticException khi phép chia tạo số thập phân vô hạn (1/3)")
        void testNonTerminatingDecimalDivisionDoesNotThrow() {
            BigDecimal itemAmount = new BigDecimal("100");
            BigDecimal totalAmount = new BigDecimal("300");

            assertDoesNotThrow(() -> {
                SpendingStructureShare share = SpendingStructureShare.of(itemAmount, totalAmount, "NeedGroup");
                assertEquals(new BigDecimal("33.33"), share.getShare());
            });
        }

        @ParameterizedTest(name = "Banker's Rounding: {0} -> {1}")
        @CsvSource({
                "40.125, 40.12",
                "40.135, 40.14",
                "50.145, 50.14",
                "50.155, 50.16"
        })
        @DisplayName("Tuân thủ quy tắc làm tròn nửa chẵn HALF_EVEN")
        void testBankersRounding(String input, String expected) {
            SpendingStructureShare share = new SpendingStructureShare(new BigDecimal(input), "NeedGroup");
            assertEquals(new BigDecimal(expected), share.getShare());
        }
    }

    // =========================================================================
    // Nhóm 3: Validation & Negative Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 3: Validation and Negative Tests")
    class ValidationAndNegativeTests {

        @Test
        @DisplayName("Từ chối share null")
        void testNullShareThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> new SpendingStructureShare(null, "NeedGroup"));
            assertEquals(ErrorCode.ERR_FIN_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối share âm (< 0.00)")
        void testNegativeShareThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> new SpendingStructureShare(new BigDecimal("-0.01"), "NeedGroup"));
            assertEquals(ErrorCode.ERR_FIN_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối share vượt quá 100.00%")
        void testShareOver100ThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> new SpendingStructureShare(new BigDecimal("100.01"), "NeedGroup"));
            assertEquals(ErrorCode.ERR_FIN_002, ex.getErrorCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {"InvalidScope", "WALLET", "Transaction", "   ", ""})
        @DisplayName("Từ chối phạm vi phân tích (scope) không hợp lệ")
        void testInvalidScopeThrowsException(String invalidScope) {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> new SpendingStructureShare(new BigDecimal("50.00"), invalidScope));
            assertEquals(ErrorCode.ERR_FIN_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối scope null")
        void testNullScopeThrowsException() {
            assertThrows(BusinessException.class,
                    () -> new SpendingStructureShare(new BigDecimal("50.00"), null));
        }
    }

    // =========================================================================
    // Nhóm 4: Format & String Representation Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 4: Format Tests")
    class FormatTests {

        @Test
        @DisplayName("Định dạng chuẩn 40.00% không có tiền tố dư thừa")
        void testFormatStandard() {
            SpendingStructureShare share = new SpendingStructureShare(new BigDecimal("40.00"), "NeedGroup");
            assertEquals("40.00%", share.format());
        }

        @Test
        @DisplayName("Định dạng zero chuẩn 0.00%")
        void testFormatZero() {
            SpendingStructureShare share = new SpendingStructureShare(BigDecimal.ZERO, "Category");
            assertEquals("0.00%", share.format());
        }
    }

    // =========================================================================
    // Nhóm 5: Value Object Contract & Descending Comparable Tests
    // =========================================================================
    @Nested
    @DisplayName("Group 5: Value Object Contract & Descending Sort Tests")
    class ValueObjectContractTests {

        @Test
        @DisplayName("Cài đặt interface Comparable sắp xếp GIẢM DẦN từ cao xuống thấp")
        void testImplementsComparableDescending() {
            SpendingStructureShare highShare = new SpendingStructureShare(new BigDecimal("60.00"), "NeedGroup");
            SpendingStructureShare lowShare = new SpendingStructureShare(new BigDecimal("20.00"), "NeedGroup");

            assertTrue(highShare instanceof Comparable, "Phải implements Comparable<SpendingStructureShare>");
            // Sắp xếp giảm dần: highShare phải đứng trước lowShare -> highShare.compareTo(lowShare) < 0
            assertTrue(highShare.compareTo(lowShare) < 0, "Tỷ trọng cao hơn phải đứng trước (trả về < 0)");
            assertTrue(lowShare.compareTo(highShare) > 0, "Tỷ trọng thấp hơn phải đứng sau (trả về > 0)");
            assertEquals(0, highShare.compareTo(new SpendingStructureShare(new BigDecimal("60.000"), "Category")));
        }

        @Test
        @DisplayName("Bình đẳng Value Object dựa trên share và scope (equals & hashCode)")
        void testEqualsAndHashCode() {
            SpendingStructureShare share1 = new SpendingStructureShare(new BigDecimal("35.00"), "NeedGroup");
            SpendingStructureShare share2 = new SpendingStructureShare(new BigDecimal("35.000"), "needgroup");
            SpendingStructureShare diffScope = new SpendingStructureShare(new BigDecimal("35.00"), "Category");
            SpendingStructureShare diffShare = new SpendingStructureShare(new BigDecimal("40.00"), "NeedGroup");

            assertEquals(share1, share2);
            assertEquals(share1.hashCode(), share2.hashCode());
            assertNotEquals(share1, diffScope);
            assertNotEquals(share1, diffShare);
            assertNotEquals(share1, null);
        }

        @Test
        @DisplayName("toString thể hiện rõ share và scope")
        void testToString() {
            SpendingStructureShare share = new SpendingStructureShare(new BigDecimal("25.00"), "NeedGroup");
            assertTrue(share.toString().contains("25.00"));
            assertTrue(share.toString().contains("NeedGroup"));
        }
    }
}

