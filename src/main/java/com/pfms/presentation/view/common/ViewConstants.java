package com.pfms.presentation.view.common;

import java.awt.Color;
import java.awt.Font;

/**
 * Hằng số cấu hình chuẩn cho Tầng Trình diễn (Presentation Layer).
 * Bao gồm kích thước cửa sổ, bảng màu giao diện phẳng Luxury Black & Gold và kiểu phông chữ hệ thống.
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
    public static final Color COLOR_PRIMARY = new Color(212, 175, 55);       // Royal Gold (#D4AF37)
    public static final Color COLOR_PRIMARY_LIGHT = new Color(229, 192, 123); // Champagne Gold (#E5C07B)
    public static final Color COLOR_GOLD_LIGHT = new Color(243, 229, 171);    // Light Champagne (#F3E5AB)
    public static final Color COLOR_GOLD_BORDER = new Color(212, 175, 55, 64);
    public static final Color COLOR_SIDEBAR_BG = new Color(0, 0, 0);          // Pure Black (#000000)
    public static final Color COLOR_SIDEBAR_HOVER = new Color(17, 17, 17);    // Dark Onyx (#111111)
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(20, 18, 11);   // Gold-tinted dark (#14120B)
    public static final Color COLOR_SIDEBAR_ACCENT = new Color(229, 192, 123); // Champagne Gold (#E5C07B)
    public static final Color COLOR_TEXT_SIDEBAR = new Color(161, 161, 170);  // Zinc Muted (#A1A1AA)
    public static final Color COLOR_TEXT_ACTIVE = new Color(229, 192, 123);   // Champagne Gold (#E5C07B)
    public static final Color COLOR_CONTENT_BG = new Color(7, 7, 7);          // Deep Obsidian (#070707)
    public static final Color COLOR_CARD_BG = new Color(18, 18, 18);          // Onyx Card (#121212)
    public static final Color COLOR_STATUS_BG = new Color(0, 0, 0);           // Pure Black (#000000)
    public static final Color COLOR_BORDER = new Color(34, 34, 34);           // Metallic Dark (#222222)
    public static final Color COLOR_TEXT_MAIN = new Color(250, 250, 250);     // Pearl White (#FAFAFA)
    public static final Color COLOR_TEXT_MUTED = new Color(161, 161, 170);    // Zinc Muted (#A1A1AA)
    public static final Color COLOR_TEXT_DIM = new Color(113, 113, 122);      // Dark Hint (#71717A)
    public static final Color COLOR_INCOME = new Color(74, 222, 128);         // Pastel Mint Green (#4ADE80)
    public static final Color COLOR_EXPENSE = new Color(251, 113, 133);       // Pastel Rose Coral (#FB7185)
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
    public static final String APP_TITLE = "Personal Finance Management System";
    public static final String APP_BRAND = "PFMS";
    public static final String APP_SUBTITLE = "Quản lý Tài chính";
    public static final String CURRENCY_DEFAULT = "VNĐ";
    // endregion

    private ViewConstants() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo");
    }
}
