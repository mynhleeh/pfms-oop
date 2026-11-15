package com.pfms.presentation.view.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử RoundedCardPanel - Tấm nền Card bo góc khử răng cưa")
class RoundedCardPanelTest {

    @Test
    @DisplayName("Khởi tạo RoundedCardPanel với cấu hình mặc định và tùy biến")
    void testInitialization() {
        RoundedCardPanel defaultPanel = new RoundedCardPanel();
        assertNotNull(defaultPanel);
        assertEquals(10, defaultPanel.getArc());
        assertFalse(defaultPanel.isOpaque());

        RoundedCardPanel customPanel = new RoundedCardPanel(16, Color.RED, Color.BLUE);
        assertEquals(16, customPanel.getArc());
        assertEquals(Color.RED, customPanel.getBackground());
        assertEquals(Color.BLUE, customPanel.getBorderColor());

        customPanel.setArc(12);
        assertEquals(12, customPanel.getArc());

        customPanel.setBorderColor(Color.GREEN);
        assertEquals(Color.GREEN, customPanel.getBorderColor());

        RoundedCardPanel layoutPanel = new RoundedCardPanel(new BorderLayout(), 8, Color.BLACK, Color.GRAY);
        assertTrue(layoutPanel.getLayout() instanceof BorderLayout);
    }

    @Test
    @DisplayName("Vẽ paintComponent lên Graphics2D khử răng cưa không ném ngoại lệ")
    void testPaintComponent() {
        RoundedCardPanel panel = new RoundedCardPanel(12, Color.DARK_GRAY, Color.WHITE);
        panel.setSize(200, 100);

        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        assertDoesNotThrow(() -> panel.paint(g2));

        g2.dispose();
    }
}
