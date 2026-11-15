package com.pfms.presentation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử InputValidator - Tiện ích kiểm thực biểu mẫu người dùng")
class InputValidatorTest {

    @Nested
    @DisplayName("Kiểm thử Thiết kế Lớp Tiện ích")
    class UtilityClassDesignTests {

        @Test
        @DisplayName("Hàm khởi tạo private ném AssertionError khi cố tình khởi tạo qua phản chiếu")
        void privateConstructor_ThrowsAssertionError() throws NoSuchMethodException {
            Constructor<InputValidator> constructor = InputValidator.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            InvocationTargetException ex = assertThrows(
                    InvocationTargetException.class,
                    constructor::newInstance
            );
            assertInstanceOf(AssertionError.class, ex.getCause());
        }
    }

    @Nested
    @DisplayName("Kiểm thử validateNonEmpty")
    class ValidateNonEmptyTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi chuỗi nhập là null")
        void validateNonEmpty_Null_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateNonEmpty(null, "Tên ví")
            );
            assertTrue(ex.getMessage().contains("Tên ví không được để trống"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi chuỗi chỉ toàn khoảng trắng")
        void validateNonEmpty_Blank_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateNonEmpty("   ", "Tên ví")
            );
            assertTrue(ex.getMessage().contains("Tên ví không được để trống"));
        }

        @Test
        @DisplayName("Trả về chuỗi đã cắt tỉa khi dữ liệu hợp lệ")
        void validateNonEmpty_Valid_ReturnsTrimmed() {
            String result = InputValidator.validateNonEmpty("  Ví tiền mặt  ", "Tên ví");
            assertEquals("Ví tiền mặt", result);
        }
    }

    @Nested
    @DisplayName("Kiểm thử validatePositiveAmount")
    class ValidatePositiveAmountTests {

        @Test
        @DisplayName("Ném ngoại lệ khi chuỗi tiền là null hoặc rỗng")
        void validatePositiveAmount_Blank_ThrowsIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validatePositiveAmount(null, "Số tiền")
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validatePositiveAmount("   ", "Số tiền")
            );
        }

        @Test
        @DisplayName("Ném ngoại lệ khi chuỗi không phải định dạng số học")
        void validatePositiveAmount_NonNumeric_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validatePositiveAmount("abc123", "Số tiền")
            );
            assertTrue(ex.getMessage().contains("Số tiền phải là số hợp lệ"));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi số tiền bằng 0 hoặc âm")
        void validatePositiveAmount_ZeroOrNegative_ThrowsIllegalArgumentException() {
            IllegalArgumentException exZero = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validatePositiveAmount("0", "Số tiền")
            );
            assertTrue(exZero.getMessage().contains("Số tiền phải lớn hơn 0"));

            IllegalArgumentException exNeg = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validatePositiveAmount("-50000", "Số tiền")
            );
            assertTrue(exNeg.getMessage().contains("Số tiền phải lớn hơn 0"));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi số tiền vượt ngưỡng 100 tỷ VNĐ")
        void validatePositiveAmount_Exceeds100Billion_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validatePositiveAmount("100000000000.01", "Số tiền")
            );
            assertTrue(ex.getMessage().contains("Số tiền không được vượt quá 100 tỷ VNĐ"));
        }

        @Test
        @DisplayName("Chuyển đổi thành công chuỗi số thường và chuỗi có dấu phẩy phân tách nghìn")
        void validatePositiveAmount_Valid_ReturnsBigDecimal() {
            BigDecimal res1 = InputValidator.validatePositiveAmount("50000", "Số tiền");
            assertEquals(0, res1.compareTo(new BigDecimal("50000")));

            BigDecimal res2 = InputValidator.validatePositiveAmount("15,000,000.50", "Số tiền");
            assertEquals(0, res2.compareTo(new BigDecimal("15000000.50")));
        }
    }

    @Nested
    @DisplayName("Kiểm thử validateNonNegativeAmount")
    class ValidateNonNegativeAmountTests {

        @Test
        @DisplayName("Chấp nhận số 0 làm giá trị số dư hợp lệ")
        void validateNonNegativeAmount_Zero_ReturnsZero() {
            BigDecimal res = InputValidator.validateNonNegativeAmount("0", "Số dư khởi điểm");
            assertEquals(0, res.compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi số tiền là số âm")
        void validateNonNegativeAmount_Negative_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateNonNegativeAmount("-1", "Số dư khởi điểm")
            );
            assertTrue(ex.getMessage().contains("Số dư khởi điểm không được âm"));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi số tiền vượt ngưỡng 100 tỷ VNĐ")
        void validateNonNegativeAmount_Exceeds100Billion_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateNonNegativeAmount("100000000001", "Số dư")
            );
            assertTrue(ex.getMessage().contains("Số dư không được vượt quá 100 tỷ VNĐ"));
        }
    }

    @Nested
    @DisplayName("Kiểm thử validateDate")
    class ValidateDateTests {

        @Test
        @DisplayName("Ném ngoại lệ khi chuỗi ngày tháng để trống")
        void validateDate_Blank_ThrowsIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateDate(null, "Ngày giao dịch")
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateDate("   ", "Ngày giao dịch")
            );
        }

        @Test
        @DisplayName("Ném ngoại lệ khi ngày tháng không đúng cú pháp")
        void validateDate_InvalidSyntax_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateDate("15-11-2026", "Ngày giao dịch")
            );
            assertTrue(ex.getMessage().contains("định dạng dd/MM/yyyy hoặc yyyy-MM-dd"));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi ngày không tồn tại trên lịch thực tế")
        void validateDate_InvalidCalendar_ThrowsIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateDate("31/02/2026", "Ngày giao dịch")
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateDate("29/02/2025", "Ngày giao dịch")
            );
        }

        @Test
        @DisplayName("Chuyển đổi thành công định dạng dd/MM/yyyy và yyyy-MM-dd")
        void validateDate_Valid_ReturnsLocalDate() {
            LocalDate d1 = InputValidator.validateDate("15/11/2026", "Ngày giao dịch");
            assertEquals(LocalDate.of(2026, 11, 15), d1);

            LocalDate d2 = InputValidator.validateDate("2026-11-15", "Ngày giao dịch");
            assertEquals(LocalDate.of(2026, 11, 15), d2);
        }
    }

    @Nested
    @DisplayName("Kiểm thử validateTextLength")
    class ValidateTextLengthTests {

        @Test
        @DisplayName("Trả về chuỗi rỗng khi text truyền vào là null")
        void validateTextLength_Null_ReturnsEmptyString() {
            String res = InputValidator.validateTextLength(null, 50, "Ghi chú");
            assertEquals("", res);
        }

        @Test
        @DisplayName("Ném ngoại lệ khi chuỗi sau khi cắt tỉa vượt quá độ dài tối đa")
        void validateTextLength_ExceedsLimit_ThrowsIllegalArgumentException() {
            String longText = "a".repeat(51);
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> InputValidator.validateTextLength(longText, 50, "Tên ví")
            );
            assertTrue(ex.getMessage().contains("Tên ví không được vượt quá 50 ký tự"));
        }

        @Test
        @DisplayName("Trả về chuỗi đã cắt tỉa khi độ dài hợp lệ")
        void validateTextLength_Valid_ReturnsTrimmed() {
            String res = InputValidator.validateTextLength("   Ví sinh hoạt   ", 50, "Tên ví");
            assertEquals("Ví sinh hoạt", res);
        }
    }

    @Nested
    @DisplayName("Kiểm thử validateRequiredText")
    class ValidateRequiredTextTests {

        @Test
        @DisplayName("Ném ngoại lệ khi chuỗi để trống")
        void validateRequiredText_Blank_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> InputValidator.validateRequiredText("   ", 50, "Tên ví"));
            assertThrows(IllegalArgumentException.class,
                    () -> InputValidator.validateRequiredText(null, 50, "Tên ví"));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi chuỗi vượt quá độ dài tối đa")
        void validateRequiredText_ExceedsLength_ThrowsIllegalArgumentException() {
            String longText = "A".repeat(51);
            assertThrows(IllegalArgumentException.class,
                    () -> InputValidator.validateRequiredText(longText, 50, "Tên ví"));
        }

        @Test
        @DisplayName("Trả về chuỗi đã cắt tỉa khi hợp lệ")
        void validateRequiredText_Valid_ReturnsTrimmed() {
            String result = InputValidator.validateRequiredText("  Ví chính  ", 50, "Tên ví");
            assertEquals("Ví chính", result);
        }
    }

    @Nested
    @DisplayName("Kiểm thử parseOptionalDate")
    class ParseOptionalDateTests {

        @Test
        @DisplayName("Trả về null khi chuỗi ngày là null hoặc rỗng")
        void parseOptionalDate_Blank_ReturnsNull() {
            assertNull(InputValidator.parseOptionalDate(null, "Ngày bắt đầu"));
            assertNull(InputValidator.parseOptionalDate("   ", "Ngày bắt đầu"));
        }

        @Test
        @DisplayName("Chuyển đổi thành công khi chuỗi ngày hợp lệ")
        void parseOptionalDate_Valid_ReturnsLocalDate() {
            assertEquals(LocalDate.of(2026, 11, 15),
                    InputValidator.parseOptionalDate("15/11/2026", "Ngày bắt đầu"));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi chuỗi ngày sai định dạng")
        void parseOptionalDate_Invalid_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> InputValidator.parseOptionalDate("sai-ngay", "Ngày bắt đầu"));
        }
    }

    @Nested
    @DisplayName("Kiểm thử parseDateOrToday")
    class ParseDateOrTodayTests {

        @Test
        @DisplayName("Trả về ngày hôm nay khi chuỗi ngày là null hoặc rỗng")
        void parseDateOrToday_Blank_ReturnsToday() {
            assertEquals(LocalDate.now(), InputValidator.parseDateOrToday(null, "Ngày giao dịch"));
            assertEquals(LocalDate.now(), InputValidator.parseDateOrToday("   ", "Ngày giao dịch"));
        }

        @Test
        @DisplayName("Chuyển đổi thành công khi chuỗi ngày hợp lệ")
        void parseDateOrToday_Valid_ReturnsParsedDate() {
            assertEquals(LocalDate.of(2026, 11, 15),
                    InputValidator.parseDateOrToday("15/11/2026", "Ngày giao dịch"));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi chuỗi ngày sai định dạng")
        void parseDateOrToday_Invalid_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> InputValidator.parseDateOrToday("31/02/2026", "Ngày giao dịch"));
        }
    }

    @Nested
    @DisplayName("Kiểm thử validateOptionalFee")
    class ValidateOptionalFeeTests {

        @Test
        @DisplayName("Trả về ZERO khi chuỗi phụ phí là null hoặc rỗng")
        void validateOptionalFee_Blank_ReturnsZero() {
            assertEquals(BigDecimal.ZERO, InputValidator.validateOptionalFee(null, null));
            assertEquals(BigDecimal.ZERO, InputValidator.validateOptionalFee("   ", null));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi phụ phí âm")
        void validateOptionalFee_Negative_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> InputValidator.validateOptionalFee("-1000", java.util.UUID.randomUUID()));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi phụ phí lớn hơn 0 nhưng thiếu danh mục phụ phí")
        void validateOptionalFee_PositiveFeeMissingCategory_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> InputValidator.validateOptionalFee("5000", null));
            assertTrue(ex.getMessage().contains("Danh mục phụ phí không được để trống khi có phụ phí"));
        }

        @Test
        @DisplayName("Trả về số tiền phụ phí khi có danh mục hợp lệ")
        void validateOptionalFee_ValidFeeWithCategory_ReturnsAmount() {
            BigDecimal fee = InputValidator.validateOptionalFee(" 5,000 ", java.util.UUID.randomUUID());
            assertEquals(new BigDecimal("5000"), fee);
        }

        @Test
        @DisplayName("Trả về ZERO khi phụ phí bằng 0 và danh mục là null")
        void validateOptionalFee_ZeroFeeNullCategory_ReturnsZero() {
            BigDecimal fee = InputValidator.validateOptionalFee("0", null);
            assertEquals(BigDecimal.ZERO, fee);
        }
    }

    @Nested
    @DisplayName("Kiểm thử validateTransactionType")
    class ValidateTransactionTypeTests {

        @Test
        @DisplayName("Trả về defaultType khi rawType là null hoặc rỗng")
        void validateTransactionType_Blank_ReturnsDefault() {
            assertEquals("EXPENSE", InputValidator.validateTransactionType(null, "EXPENSE"));
            assertEquals("INCOME", InputValidator.validateTransactionType("   ", "INCOME"));
        }

        @Test
        @DisplayName("Chuẩn hóa thành chữ hoa khi truyền 'income' hoặc 'expense'")
        void validateTransactionType_Valid_ReturnsUpperCase() {
            assertEquals("INCOME", InputValidator.validateTransactionType("  income  ", "EXPENSE"));
            assertEquals("EXPENSE", InputValidator.validateTransactionType("  expense  ", "INCOME"));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi loại giao dịch không hợp lệ")
        void validateTransactionType_Invalid_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> InputValidator.validateTransactionType("TRANSFER", "EXPENSE"));
            assertTrue(ex.getMessage().contains("Loại giao dịch phải là INCOME hoặc EXPENSE"));
        }
    }
}
