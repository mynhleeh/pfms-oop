package com.pfms.presentation.view.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử ViewConstants - Hằng số giao diện người dùng")
class ViewConstantsTest {

    @Test
    @DisplayName("Lớp tiện ích có hàm khởi tạo private và không thể khởi tạo qua reflection")
    void testUtilityClassDesign() throws Exception {
        Constructor<ViewConstants> constructor = ViewConstants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(ex.getCause() instanceof UnsupportedOperationException);
    }

    @Test
    @DisplayName("Các hằng số kích thước chuẩn có giá trị hợp lệ")
    void testDimensions() {
        assertEquals(1024, ViewConstants.APP_WIDTH);
        assertEquals(680, ViewConstants.APP_HEIGHT);
        assertEquals(900, ViewConstants.MIN_WIDTH);
        assertEquals(600, ViewConstants.MIN_HEIGHT);
        assertEquals(220, ViewConstants.SIDEBAR_WIDTH);
        assertEquals(32, ViewConstants.STATUS_BAR_HEIGHT);
    }

    @Test
    @DisplayName("Các hằng số màu sắc và phông chữ không được để null")
    void testColorsAndFonts() {
        assertNotNull(ViewConstants.COLOR_PRIMARY);
        assertNotNull(ViewConstants.COLOR_PRIMARY_LIGHT);
        assertNotNull(ViewConstants.COLOR_SIDEBAR_BG);
        assertNotNull(ViewConstants.COLOR_SIDEBAR_HOVER);
        assertNotNull(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        assertNotNull(ViewConstants.COLOR_SIDEBAR_ACCENT);
        assertNotNull(ViewConstants.COLOR_TEXT_SIDEBAR);
        assertNotNull(ViewConstants.COLOR_TEXT_ACTIVE);
        assertNotNull(ViewConstants.COLOR_CONTENT_BG);
        assertNotNull(ViewConstants.COLOR_STATUS_BG);
        assertNotNull(ViewConstants.COLOR_BORDER);
        assertNotNull(ViewConstants.COLOR_TEXT_MAIN);
        assertNotNull(ViewConstants.COLOR_TEXT_MUTED);
        assertNotNull(ViewConstants.COLOR_INCOME);
        assertNotNull(ViewConstants.COLOR_EXPENSE);

        assertNotNull(ViewConstants.FONT_TITLE);
        assertNotNull(ViewConstants.FONT_SUBTITLE);
        assertNotNull(ViewConstants.FONT_NAV);
        assertNotNull(ViewConstants.FONT_NAV_ACTIVE);
        assertNotNull(ViewConstants.FONT_BODY);
        assertNotNull(ViewConstants.FONT_STATUS);
        assertNotNull(ViewConstants.FONT_PANEL_HEADER);

        assertEquals("Hệ thống Quản lý Tài chính Cá nhân - PFMS", ViewConstants.APP_TITLE);
        assertEquals("PFMS", ViewConstants.APP_BRAND);
        assertEquals("Quản lý Tài chính", ViewConstants.APP_SUBTITLE);
        assertEquals("VNĐ", ViewConstants.CURRENCY_DEFAULT);
    }
}
