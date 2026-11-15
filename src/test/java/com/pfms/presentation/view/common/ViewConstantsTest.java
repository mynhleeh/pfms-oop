package com.pfms.presentation.view.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử ViewConstants - Hằng số giao diện người dùng")
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
    @DisplayName("Các token màu Luxury Black & Gold phải khớp giá trị RGB chuẩn xác")
    void testColorTokens() {
        assertEquals(new Color(7, 7, 7), ViewConstants.COLOR_CONTENT_BG);
        assertEquals(new Color(18, 18, 18), ViewConstants.COLOR_CARD_BG);
        assertEquals(new Color(0, 0, 0), ViewConstants.COLOR_SIDEBAR_BG);
        assertEquals(new Color(0, 0, 0), ViewConstants.COLOR_STATUS_BG);
        assertEquals(new Color(34, 34, 34), ViewConstants.COLOR_BORDER);
        assertEquals(new Color(212, 175, 55), ViewConstants.COLOR_PRIMARY);
        assertEquals(new Color(229, 192, 123), ViewConstants.COLOR_PRIMARY_LIGHT);
        assertEquals(new Color(243, 229, 171), ViewConstants.COLOR_GOLD_LIGHT);
        assertEquals(new Color(212, 175, 55, 64), ViewConstants.COLOR_GOLD_BORDER);
        assertEquals(new Color(113, 113, 122), ViewConstants.COLOR_TEXT_DIM);
        assertEquals(new Color(74, 222, 128), ViewConstants.COLOR_INCOME);
        assertEquals(new Color(251, 113, 133), ViewConstants.COLOR_EXPENSE);
        assertEquals(new Color(250, 250, 250), ViewConstants.COLOR_TEXT_MAIN);
        assertEquals(new Color(161, 161, 170), ViewConstants.COLOR_TEXT_MUTED);
    }

    @Test
    @DisplayName("Tiêu đề ứng dụng phải là Personal Finance Management System")
    void testAppTitle() {
        assertEquals("Personal Finance Management System", ViewConstants.APP_TITLE);
    }
}
