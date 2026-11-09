package com.pfms.domain.model.transaction;

/**
 * Kiểu liệt kê loại giao dịch và nhánh danh mục cấp 1 trong hệ thống PFMS.
 * Xác định chiều dòng tiền (Thu nhập hoặc Chi tiêu) và ràng buộc tính tương
 * thích phân loại.
 */
public enum TransactionType {

    // region Constants
    INCOME("INCOME", "Thu nhập"),
    EXPENSE("EXPENSE", "Chi tiêu");
    // endregion

    // region State
    private final String code;
    private final String displayLabel;
    // endregion

    // region Constructors
    TransactionType(String code, String displayLabel) {
        this.code = code;
        this.displayLabel = displayLabel;
    }
    // endregion

    // region Getters
    public String getCode() {
        return this.code;
    }

    public String getDisplayLabel() {
        return this.displayLabel;
    }
    // endregion

    // region Queries
    public boolean isIncome() {
        return this == INCOME;
    }

    public boolean isExpense() {
        return this == EXPENSE;
    }

    public boolean isCompatibleWith(TransactionType categoryType) {
        return categoryType != null && this == categoryType;
    }

    public TransactionType opposite() {
        return this == INCOME ? EXPENSE : INCOME;
    }
    // endregion

    // region Parsers
    public static TransactionType fromString(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại giao dịch không được để trống.");
        }
        String normalized = rawValue.trim().toUpperCase();
        for (TransactionType type : values()) {
            if (type.name().equals(normalized) || type.code.equalsIgnoreCase(normalized)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Loại giao dịch không hợp lệ: " + rawValue);
    }
    // endregion
}
