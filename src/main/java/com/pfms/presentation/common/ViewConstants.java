package com.pfms.presentation.common;

/**
 * Hằng số cấu hình chuẩn cho Tầng Trình diễn (Presentation Layer) JavaFX.
 * Bao gồm kích thước cửa sổ, bảng màu giao diện phẳng Luxury Black &amp; Gold
 * và các chuỗi cấu hình hệ thống.
 */
public final class ViewConstants {

    // region Dimensions
    public static final int APP_WIDTH = 1560;
    public static final int APP_HEIGHT = 820;
    public static final int MIN_WIDTH = 1024;
    public static final int MIN_HEIGHT = 680;
    public static final int SIDEBAR_WIDTH = 220;
    public static final int STATUS_BAR_HEIGHT = 32;
    // endregion

    // region Hex Colors for JavaFX
    public static final String HEX_PRIMARY = "#D4AF37";
    public static final String HEX_PRIMARY_LIGHT = "#E5C07B";
    public static final String HEX_BG_BASE = "#070707";
    public static final String HEX_CARD_BG = "#121212";
    public static final String HEX_SIDEBAR_BG = "#000000";
    public static final String HEX_BORDER = "#222222";
    public static final String HEX_TEXT_MAIN = "#FAFAFA";
    public static final String HEX_TEXT_MUTED = "#A1A1AA";
    public static final String HEX_TEXT_DIM = "#71717A";
    public static final String HEX_INCOME = "#4ADE80";
    public static final String HEX_EXPENSE = "#FB7185";
    // endregion

    // region Application Strings
    public static final String APP_TITLE = "Personal Finance Management System";
    public static final String APP_BRAND = "PFMS";
    public static final String APP_SUBTITLE = "Quản lý Tài chính";
    public static final String CURRENCY_DEFAULT = "VNĐ";
    // endregion

    private ViewConstants() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo");
    }
}
