package com.pfms.presentation.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử ViewConstants - Hằng số giao diện người dùng JavaFX")
class ViewConstantsTest {

    @Test
    @DisplayName("Lớp tiện ích ViewConstants không được phép khởi tạo")
    void testConstructorIsPrivate() throws NoSuchMethodException {
        Constructor<ViewConstants> constructor = ViewConstants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(ex.getCause() instanceof UnsupportedOperationException);
    }

    @Test
    @DisplayName("Các token màu HEX Luxury Black & Gold phải khớp giá trị chuẩn xác")
    void testColorHexTokens() {
        assertEquals("#D4AF37", ViewConstants.HEX_PRIMARY);
        assertEquals("#E5C07B", ViewConstants.HEX_PRIMARY_LIGHT);
        assertEquals("#070707", ViewConstants.HEX_BG_BASE);
        assertEquals("#121212", ViewConstants.HEX_CARD_BG);
        assertEquals("#000000", ViewConstants.HEX_SIDEBAR_BG);
        assertEquals("#222222", ViewConstants.HEX_BORDER);
        assertEquals("#FAFAFA", ViewConstants.HEX_TEXT_MAIN);
        assertEquals("#A1A1AA", ViewConstants.HEX_TEXT_MUTED);
        assertEquals("#71717A", ViewConstants.HEX_TEXT_DIM);
        assertEquals("#4ADE80", ViewConstants.HEX_INCOME);
        assertEquals("#FB7185", ViewConstants.HEX_EXPENSE);
    }

    @Test
    @DisplayName("Kích thước tiêu chuẩn của cửa sổ ứng dụng")
    void testDimensions() {
        assertEquals(1560, ViewConstants.APP_WIDTH);
        assertEquals(820, ViewConstants.APP_HEIGHT);
        assertEquals(1024, ViewConstants.MIN_WIDTH);
        assertEquals(680, ViewConstants.MIN_HEIGHT);
        assertEquals(220, ViewConstants.SIDEBAR_WIDTH);
        assertEquals(32, ViewConstants.STATUS_BAR_HEIGHT);
    }

    @Test
    @DisplayName("Tiêu đề ứng dụng phải là Personal Finance Management System")
    void testAppTitle() {
        assertEquals("Personal Finance Management System", ViewConstants.APP_TITLE);
        assertEquals("PFMS", ViewConstants.APP_BRAND);
        assertEquals("VNĐ", ViewConstants.CURRENCY_DEFAULT);
    }
}
