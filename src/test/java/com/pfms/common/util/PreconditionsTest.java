package com.pfms.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử lớp tiện ích Preconditions (com.pfms.common.util)")
class PreconditionsTest {

    @Nested
    @DisplayName("Kiểm tra thiết kế Utility Class (Effective Java Item 4)")
    class UtilityClassDesignTests {

        @Test
        @DisplayName("Hàm khởi tạo private phải ném UnsupportedOperationException hoặc AssertionError khi bị gọi qua Reflection")
        void shouldPreventInstantiationViaReflection() throws NoSuchMethodException {
            Constructor<Preconditions> constructor = Preconditions.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
            assertInstanceOf(AssertionError.class, ex.getCause());
            assertEquals("Utility class cannot be instantiated", ex.getCause().getMessage());
        }
    }

    @Nested
    @DisplayName("Kiểm thử requireNonBlank")
    class RequireNonBlankTests {

        @Test
        @DisplayName("Trả về chuỗi đã cắt khoảng trắng khi hợp lệ")
        void shouldReturnTrimmedStringWhenValid() {
            String result = Preconditions.requireNonBlank("   Ví Tiền Mặt   ", "Tên không được để trống");
            assertEquals("Ví Tiền Mặt", result);
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi chuỗi là null")
        void shouldThrowWhenStringIsNull() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> Preconditions.requireNonBlank(null, "Chuỗi không được null")
            );
            assertEquals("Chuỗi không được null", ex.getMessage());
        }

        @ParameterizedTest(name = "Chuỗi chỉ gồm khoảng trắng: \"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t", "\n", " \t \n "})
        @DisplayName("Ném IllegalArgumentException khi chuỗi rỗng hoặc chỉ chứa khoảng trắng")
        void shouldThrowWhenStringIsBlank(String blankStr) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> Preconditions.requireNonBlank(blankStr, "Chuỗi không được để trống")
            );
            assertEquals("Chuỗi không được để trống", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Kiểm thử requirePositive")
    class RequirePositiveTests {

        @Test
        @DisplayName("Trả về chính số tiền khi lớn hơn 0")
        void shouldReturnAmountWhenPositive() {
            BigDecimal amount = new BigDecimal("100000.00");
            BigDecimal result = Preconditions.requirePositive(amount, "Số tiền phải lớn hơn 0");
            assertSame(amount, result);
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền là null")
        void shouldThrowWhenAmountIsNull() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> Preconditions.requirePositive(null, "Số tiền không được null")
            );
            assertEquals("Số tiền không được null", ex.getMessage());
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền bằng 0")
        void shouldThrowWhenAmountIsZero() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> Preconditions.requirePositive(BigDecimal.ZERO, "Số tiền phải lớn hơn 0")
            );
            assertEquals("Số tiền phải lớn hơn 0", ex.getMessage());
        }

        @ParameterizedTest(name = "Số tiền âm: {0}")
        @ValueSource(strings = {"-0.01", "-1", "-500000.00"})
        @DisplayName("Ném IllegalArgumentException khi số tiền âm")
        void shouldThrowWhenAmountIsNegative(String negativeStr) {
            BigDecimal negativeAmount = new BigDecimal(negativeStr);
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> Preconditions.requirePositive(negativeAmount, "Số tiền phải lớn hơn 0")
            );
            assertEquals("Số tiền phải lớn hơn 0", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Kiểm thử requireNonNegative")
    class RequireNonNegativeTests {

        @Test
        @DisplayName("Trả về chính số tiền khi lớn hơn hoặc bằng 0")
        void shouldReturnAmountWhenNonNegative() {
            BigDecimal zero = BigDecimal.ZERO;
            assertSame(zero, Preconditions.requireNonNegative(zero, "Phí không được âm"));

            BigDecimal positive = new BigDecimal("5000.00");
            assertSame(positive, Preconditions.requireNonNegative(positive, "Phí không được âm"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền là null")
        void shouldThrowWhenAmountIsNull() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> Preconditions.requireNonNegative(null, "Phí không được null")
            );
            assertEquals("Phí không được null", ex.getMessage());
        }

        @ParameterizedTest(name = "Số tiền âm: {0}")
        @ValueSource(strings = {"-0.01", "-100", "-5000.00"})
        @DisplayName("Ném IllegalArgumentException khi số tiền âm")
        void shouldThrowWhenAmountIsNegative(String negativeStr) {
            BigDecimal negativeAmount = new BigDecimal(negativeStr);
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> Preconditions.requireNonNegative(negativeAmount, "Phí không được âm")
            );
            assertEquals("Phí không được âm", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Kiểm thử checkArgument")
    class CheckArgumentTests {

        @Test
        @DisplayName("Không ném ngoại lệ khi biểu thức là true")
        void shouldDoNothingWhenExpressionIsTrue() {
            assertDoesNotThrow(() -> Preconditions.checkArgument(true, "Không có lỗi"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi biểu thức là false")
        void shouldThrowWhenExpressionIsFalse() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> Preconditions.checkArgument(false, "Biểu thức không thỏa mãn")
            );
            assertEquals("Biểu thức không thỏa mãn", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Kiểm thử requireValidDateRange")
    class RequireValidDateRangeTests {

        @Test
        @DisplayName("Chấp nhận khoảng ngày mở một đầu, cả hai null, cùng ngày hoặc startDate trước endDate")
        void shouldAcceptValidOrOpenEndedDateRanges() {
            LocalDate leapDay = LocalDate.of(2024, 2, 29);
            assertDoesNotThrow(() -> Preconditions.requireValidDateRange(null, null));
            assertDoesNotThrow(() -> Preconditions.requireValidDateRange(leapDay, null));
            assertDoesNotThrow(() -> Preconditions.requireValidDateRange(null, leapDay));
            assertDoesNotThrow(() -> Preconditions.requireValidDateRange(leapDay, leapDay));
            assertDoesNotThrow(() -> Preconditions.requireValidDateRange(leapDay.minusDays(1), leapDay));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi startDate đứng sau endDate")
        void shouldThrowWhenStartDateAfterEndDate() {
            LocalDate startDate = LocalDate.of(2026, 11, 20);
            LocalDate endDate = LocalDate.of(2026, 11, 10);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Preconditions.requireValidDateRange(startDate, endDate)
            );
            assertTrue(exception.getMessage().contains("Ngày bắt đầu không được sau ngày kết thúc"));
        }
    }
}

