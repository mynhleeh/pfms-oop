package com.pfms.presentation.common.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử SearchIcon - Biểu tượng vector tìm kiếm")
class SearchIconTest {

    @Test
    @DisplayName("Khởi tạo SearchIcon với kích thước mặc định và tùy chỉnh")
    void testSearchIconDimensions() {
        SearchIcon defaultIcon = new SearchIcon();
        assertEquals(14, defaultIcon.getIconWidth());
        assertEquals(14, defaultIcon.getIconHeight());

        SearchIcon customIcon = new SearchIcon(18, Color.YELLOW);
        assertEquals(18, customIcon.getIconWidth());
        assertEquals(18, customIcon.getIconHeight());
    }

    @Test
    @DisplayName("Vẽ SearchIcon trên đồ họa Graphics2D không ném lỗi")
    void testPaintIcon() {
        SearchIcon icon = new SearchIcon(16, Color.WHITE);
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        assertDoesNotThrow(() -> icon.paintIcon(new JLabel(), g2, 4, 4));
        g2.dispose();
    }
}
