package com.pfms.presentation.common.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử RoundedBorder - Đường viền bo góc khử răng cưa")
class RoundedBorderTest {

    @Test
    @DisplayName("Khởi tạo RoundedBorder và kiểm tra Insets")
    void testRoundedBorderInsets() {
        RoundedBorder border = new RoundedBorder(12, Color.GRAY);
        Insets insets = border.getBorderInsets(new JPanel());
        assertNotNull(insets);
        assertFalse(border.isBorderOpaque());
    }

    @Test
    @DisplayName("Vẽ RoundedBorder trên đồ họa Graphics2D không ném ngoại lệ")
    void testPaintBorder() {
        RoundedBorder border = new RoundedBorder(14, Color.RED);
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        assertDoesNotThrow(() -> border.paintBorder(new JPanel(), g2, 0, 0, 50, 50));
        g2.dispose();
    }
}
