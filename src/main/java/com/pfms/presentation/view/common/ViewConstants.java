package com.pfms.presentation.view.common;

import java.awt.Color;
import java.awt.Font;

/**
 * Hằng số cấu hình chuẩn cho Tầng Trình diễn (Presentation Layer).
 * Bao gồm kích thước cửa sổ, bảng màu giao diện phẳng và kiểu phông chữ hệ thống.
 */
public final class ViewConstants {

    // region Dimensions
    public static final int APP_WIDTH = 1024;
    public static final int APP_HEIGHT = 680;
    public static final int MIN_WIDTH = 900;
    public static final int MIN_HEIGHT = 600;
    public static final int SIDEBAR_WIDTH = 220;
    public static final int STATUS_BAR_HEIGHT = 32;
    // endregion

    // region Colors
    public static final Color COLOR_PRIMARY = new Color(30, 58, 138);       // Deep Blue
    public static final Color COLOR_PRIMARY_LIGHT = new Color(37, 99, 235); // Accent Blue
    public static final Color COLOR_SIDEBAR_BG = new Color(15, 23, 42);     // Slate 900
    public static final Color COLOR_SIDEBAR_HOVER = new Color(30, 41, 59);  // Slate 800
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(30, 41, 59); // Slate 800
    public static final Color COLOR_SIDEBAR_ACCENT = new Color(56, 189, 248); // Sky 400
    public static final Color COLOR_TEXT_SIDEBAR = new Color(148, 163, 184); // Slate 400
    public static final Color COLOR_TEXT_ACTIVE = new Color(56, 189, 248);  // Sky 400
    public static final Color COLOR_CONTENT_BG = new Color(248, 250, 252);  // Slate 50
    public static final Color COLOR_STATUS_BG = Color.WHITE;
    public static final Color COLOR_BORDER = new Color(226, 232, 240);      // Slate 200
    public static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);      // Slate 900
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);  // Slate 500
    public static final Color COLOR_INCOME = new Color(22, 163, 74);        // Green 600
    public static final Color COLOR_EXPENSE = new Color(220, 38, 38);       // Red 600
    // endregion

    // region Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_NAV = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_NAV_ACTIVE = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_STATUS = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_PANEL_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    // endregion

    // region Application Strings
    public static final String APP_TITLE = "Hệ thống Quản lý Tài chính Cá nhân - PFMS";
    public static final String APP_BRAND = "PFMS";
    public static final String APP_SUBTITLE = "Quản lý Tài chính";
    public static final String CURRENCY_DEFAULT = "VNĐ";
    // endregion

    private ViewConstants() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo");
    }
}
