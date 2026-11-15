package com.pfms.presentation.common.component;

import com.pfms.presentation.common.MainController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử NavIconFactory - Nhà máy tạo biểu tượng vector thuần Swing")
class NavIconFactoryTest {

    @Test
    @DisplayName("Lớp tiện ích không thể khởi tạo trực tiếp")
    void testConstructorPrivate() throws Exception {
        Constructor<NavIconFactory> constructor = NavIconFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(ex.getCause() instanceof UnsupportedOperationException);
    }

    @Test
    @DisplayName("Tạo icon cho từng phân hệ điều hướng hợp lệ")
    void testCreateNavIcons() {
        String[] cards = {
                MainController.CARD_DASHBOARD,
                MainController.CARD_WALLETS,
                MainController.CARD_TRANSACTIONS,
                MainController.CARD_TRANSFERS,
                MainController.CARD_CATEGORIES,
                MainController.CARD_REPORTS,
                MainController.CARD_DATA_MANAGEMENT
        };

        for (String card : cards) {
            Icon icon = NavIconFactory.createNavIcon(card, 18);
            assertNotNull(icon);
            assertEquals(18, icon.getIconWidth());
            assertEquals(18, icon.getIconHeight());
            assertTrue(icon instanceof NavIconFactory.VectorNavIcon);
        }
    }

    @Test
    @DisplayName("Tạo Brand Icon cho logo thương hiệu")
    void testCreateBrandIcon() {
        Icon brandIcon = NavIconFactory.createBrandIcon(24, Color.YELLOW);
        assertNotNull(brandIcon);
        assertEquals(24, brandIcon.getIconWidth());
        assertEquals(24, brandIcon.getIconHeight());
    }

    @Test
    @DisplayName("Thực hiện vẽ icon lên Graphics2D không phát sinh lỗi")
    void testPaintIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        for (NavIconFactory.IconType type : NavIconFactory.IconType.values()) {
            Icon icon = NavIconFactory.createIcon(type, 20, Color.WHITE);
            assertDoesNotThrow(() -> icon.paintIcon(null, g2, 0, 0));
        }

        g2.dispose();
    }
}
