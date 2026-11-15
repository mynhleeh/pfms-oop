package com.pfms.presentation.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Tiện ích kiểm thực dữ liệu đầu vào từ biểu mẫu giao diện người dùng.
 * Xác thực tính hợp lệ của chuỗi văn bản, số tiền và ngày tháng trước khi chuyển sang Tầng Dịch vụ.
 */
public final class InputValidator {

    // region Constants
    private static final BigDecimal MAX_MONEY_AMOUNT = new BigDecimal("100000000000"); // 100 tỷ VNĐ

    private static final DateTimeFormatter FORMATTER_DD_MM_YYYY = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter FORMATTER_YYYY_MM_DD = DateTimeFormatter
            .ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    // endregion

    // region Constructors
    private InputValidator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    // Kiểm tra chuỗi bắt buộc không được rỗng sau khi cắt tỉa khoảng trắng.
    public static String validateNonEmpty(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống");
        }
        return text.trim();
    }

    // Kiểm tra số tiền hợp lệ và phải lớn hơn 0 (tối đa 100 tỷ VNĐ).
    public static BigDecimal validatePositiveAmount(String text, String fieldName) {
        String clean = validateNonEmpty(text, fieldName).replace(",", "").trim();
        BigDecimal amount;
        try {
            amount = new BigDecimal(clean);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " phải là số hợp lệ", ex);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " phải lớn hơn 0");
        }
        if (amount.compareTo(MAX_MONEY_AMOUNT) > 0) {
            throw new IllegalArgumentException(fieldName + " không được vượt quá 100 tỷ VNĐ");
        }
        return amount;
    }

    // Kiểm tra số tiền hợp lệ và không được âm (tối đa 100 tỷ VNĐ).
    public static BigDecimal validateNonNegativeAmount(String text, String fieldName) {
        String clean = validateNonEmpty(text, fieldName).replace(",", "").trim();
        BigDecimal amount;
        try {
            amount = new BigDecimal(clean);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " phải là số hợp lệ", ex);
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " không được âm");
        }
        if (amount.compareTo(MAX_MONEY_AMOUNT) > 0) {
            throw new IllegalArgumentException(fieldName + " không được vượt quá 100 tỷ VNĐ");
        }
        return amount;
    }

    // Kiểm tra và chuyển đổi chuỗi ngày tháng hợp lệ theo định dạng dd/MM/yyyy hoặc yyyy-MM-dd.
    public static LocalDate validateDate(String text, String fieldName) {
        String clean = validateNonEmpty(text, fieldName);

        try {
            if (clean.contains("/")) {
                return LocalDate.parse(clean, FORMATTER_DD_MM_YYYY);
            } else if (clean.contains("-")) {
                return LocalDate.parse(clean, FORMATTER_YYYY_MM_DD);
            }
        } catch (DateTimeParseException ignored) {
            // Sẽ ném ngoại lệ rõ ràng bên dưới
        }

        throw new IllegalArgumentException(
                fieldName + " phải có định dạng dd/MM/yyyy hoặc yyyy-MM-dd hợp lệ trên lịch"
        );
    }

    // Kiểm tra độ dài chuỗi ký tự sau khi cắt tỉa không vượt quá giới hạn cho phép.
    public static String validateTextLength(String text, int maxLength, String fieldName) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " không được vượt quá " + maxLength + " ký tự");
        }
        return trimmed;
    }

    // Kiểm tra chuỗi bắt buộc không rỗng đồng thời giới hạn độ dài tối đa cho phép.
    public static String validateRequiredText(String text, int maxLength, String fieldName) {
        String clean = validateNonEmpty(text, fieldName);
        return validateTextLength(clean, maxLength, fieldName);
    }

    // Chuyển đổi ngày tháng tùy chọn (trả về null nếu chuỗi rỗng).
    public static LocalDate parseOptionalDate(String text, String fieldName) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return validateDate(text, fieldName);
    }

    // Chuyển đổi ngày tháng đầu vào hoặc lấy ngày hiện tại nếu chuỗi rỗng.
    public static LocalDate parseDateOrToday(String text, String fieldName) {
        if (text == null || text.isBlank()) {
            return LocalDate.now();
        }
        return validateDate(text, fieldName);
    }

    // Kiểm tra và trả về phụ phí hợp lệ kèm ràng buộc phải có danh mục phí khi phụ phí dương.
    public static BigDecimal validateOptionalFee(String rawFeeAmount, java.util.UUID feeCategoryId) {
        BigDecimal feeAmount = BigDecimal.ZERO;
        if (rawFeeAmount != null && !rawFeeAmount.isBlank()) {
            feeAmount = validateNonNegativeAmount(rawFeeAmount, "Phụ phí");
        }
        if (feeAmount.compareTo(BigDecimal.ZERO) > 0 && feeCategoryId == null) {
            throw new IllegalArgumentException("Danh mục phụ phí không được để trống khi có phụ phí");
        }
        return feeAmount;
    }

    // Kiểm tra và chuẩn hóa loại giao dịch phải là INCOME hoặc EXPENSE.
    public static String validateTransactionType(String rawType, String defaultType) {
        String type = (rawType == null || rawType.isBlank())
                ? defaultType
                : rawType.trim().toUpperCase(java.util.Locale.ROOT);
        if (!"INCOME".equals(type) && !"EXPENSE".equals(type)) {
            throw new IllegalArgumentException("Loại giao dịch phải là INCOME hoặc EXPENSE");
        }
        return type;
    }
    // endregion
}
