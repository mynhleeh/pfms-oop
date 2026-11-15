package com.pfms.presentation.view.component;

import com.pfms.presentation.controller.MainController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import java.awt.Component;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử PlaceholderPanel - Tấm nền giữ chỗ phân hệ")
class PlaceholderPanelTest {

    @Test
    @DisplayName("Khởi tạo với thông tin thẻ phân hệ hợp lệ")
    void testPlaceholderPanelInitialization() {
        PlaceholderPanel panel = new PlaceholderPanel(MainController.CARD_DASHBOARD, "Tổng quan");
        assertEquals(MainController.CARD_DASHBOARD, panel.getCardName());
        assertEquals("Tổng quan", panel.getTitle());
        assertTrue(panel.getComponentCount() > 0);

        boolean hasLabel = false;
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel label && label.getText().contains("Tổng quan")) {
                hasLabel = true;
                break;
            }
        }
        assertTrue(hasLabel, "Phải chứa nhãn hiển thị tiêu đề phân hệ");
    }

    @Test
    @DisplayName("Ném NullPointerException khi thẻ hoặc tiêu đề bị null")
    void testPlaceholderPanelNullArguments() {
        assertThrows(NullPointerException.class, () -> new PlaceholderPanel(null, "Tiêu đề"));
        assertThrows(NullPointerException.class, () -> new PlaceholderPanel(MainController.CARD_DASHBOARD, null));
    }
}
