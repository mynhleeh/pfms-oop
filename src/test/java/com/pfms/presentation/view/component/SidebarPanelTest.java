package com.pfms.presentation.view.component;

import com.pfms.presentation.controller.MainController;
import com.pfms.presentation.view.common.ViewConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử SidebarPanel - Thanh điều hướng phân hệ bên trái")
class SidebarPanelTest {

    private SidebarPanel sidebarPanel;
    private AtomicReference<String> selectedNavigation;

    @BeforeEach
    void setUp() {
        selectedNavigation = new AtomicReference<>();
        sidebarPanel = new SidebarPanel(selectedNavigation::set);
    }

    @Test
    @DisplayName("Khởi tạo đúng thẻ mặc định ban đầu là DASHBOARD")
    void testInitialActiveCard() {
        assertEquals(MainController.CARD_DASHBOARD, sidebarPanel.getActiveCard());
    }

    @Test
    @DisplayName("Kích hoạt chuyển thẻ qua hàm setActiveCard cập nhật thành công")
    void testSetActiveCard() {
        sidebarPanel.setActiveCard(MainController.CARD_WALLETS);
        assertEquals(MainController.CARD_WALLETS, sidebarPanel.getActiveCard());
    }

    @Test
    @DisplayName("Mô phỏng click nút điều hướng phát đúng sự kiện callback")
    void testButtonClickTriggersCallback() {
        sidebarPanel.triggerCardClick(MainController.CARD_TRANSACTIONS);
        assertEquals(MainController.CARD_TRANSACTIONS, selectedNavigation.get());
        assertEquals(MainController.CARD_TRANSACTIONS, sidebarPanel.getActiveCard());
    }

    @Test
    @DisplayName("Ném NullPointerException khi callback bị null")
    void testNullCallbackThrowsException() {
        assertThrows(NullPointerException.class, () -> new SidebarPanel(null));
    }

    @Test
    @DisplayName("Khối Header Brand áp dụng đúng màu và font Luxury")
    void testHeaderStyling() {
        assertEquals(ViewConstants.COLOR_PRIMARY_LIGHT, sidebarPanel.getBrandLabel().getForeground());
        assertEquals(ViewConstants.FONT_TITLE, sidebarPanel.getBrandLabel().getFont());
        assertEquals(ViewConstants.COLOR_TEXT_MUTED, sidebarPanel.getSubtitleLabel().getForeground());
        assertEquals(ViewConstants.FONT_SUBTITLE, sidebarPanel.getSubtitleLabel().getFont());
    }

    @Test
    @DisplayName("SidebarPanel có nền đen và viền bên phải hợp lệ")
    void testSidebarBorderAndBackground() {
        assertEquals(ViewConstants.COLOR_SIDEBAR_BG, sidebarPanel.getBackground());
        assertNotNull(sidebarPanel.getBorder());
    }

    @Test
    @DisplayName("Nút Active và Inactive áp dụng màu sắc và font chuẩn xác")
    void testNavButtonsStyling() {
        JButton activeBtn = sidebarPanel.getNavButton(MainController.CARD_DASHBOARD);
        assertNotNull(activeBtn);
        assertEquals(ViewConstants.COLOR_SIDEBAR_ACTIVE, activeBtn.getBackground());
        assertEquals(ViewConstants.COLOR_TEXT_ACTIVE, activeBtn.getForeground());
        assertEquals(ViewConstants.FONT_NAV_ACTIVE, activeBtn.getFont());

        JButton inactiveBtn = sidebarPanel.getNavButton(MainController.CARD_WALLETS);
        assertNotNull(inactiveBtn);
        assertEquals(ViewConstants.COLOR_SIDEBAR_BG, inactiveBtn.getBackground());
        assertEquals(ViewConstants.COLOR_TEXT_SIDEBAR, inactiveBtn.getForeground());
        assertEquals(ViewConstants.FONT_NAV, inactiveBtn.getFont());
    }

    @Test
    @DisplayName("Hiệu ứng MouseListener hover cho nút điều hướng hoạt động chính xác")
    void testNavButtonHoverEffect() {
        JButton inactiveBtn = sidebarPanel.getNavButton(MainController.CARD_REPORTS);
        assertNotNull(inactiveBtn);

        // Giả lập hover vào nút inactive
        MouseEvent enterEvent = new MouseEvent(inactiveBtn, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, 10, 10, 0, false);
        for (var listener : inactiveBtn.getMouseListeners()) {
            listener.mouseEntered(enterEvent);
        }
        assertEquals(ViewConstants.COLOR_SIDEBAR_HOVER, inactiveBtn.getBackground());
        assertEquals(ViewConstants.COLOR_GOLD_LIGHT, inactiveBtn.getForeground());

        // Giả lập rời chuột khỏi nút inactive
        MouseEvent exitEvent = new MouseEvent(inactiveBtn, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 10, 10, 0, false);
        for (var listener : inactiveBtn.getMouseListeners()) {
            listener.mouseExited(exitEvent);
        }
        assertEquals(ViewConstants.COLOR_SIDEBAR_BG, inactiveBtn.getBackground());
        assertEquals(ViewConstants.COLOR_TEXT_SIDEBAR, inactiveBtn.getForeground());

        // Đối với nút active: hover vào không làm đổi màu active
        JButton activeBtn = sidebarPanel.getNavButton(MainController.CARD_DASHBOARD);
        for (var listener : activeBtn.getMouseListeners()) {
            listener.mouseEntered(enterEvent);
        }
        assertEquals(ViewConstants.COLOR_SIDEBAR_ACTIVE, activeBtn.getBackground());
        assertEquals(ViewConstants.COLOR_TEXT_ACTIVE, activeBtn.getForeground());
    }

    @Test
    @DisplayName("Brand Logo và tất cả 7 nút điều hướng đều được gán Icon vector hợp lệ")
    void testNavIconsAndBrandIconPresent() {
        assertNotNull(sidebarPanel.getBrandLabel().getIcon(), "Brand label phải có icon thương hiệu PFMS");

        String[] cardNames = {
                MainController.CARD_DASHBOARD,
                MainController.CARD_WALLETS,
                MainController.CARD_TRANSACTIONS,
                MainController.CARD_TRANSFERS,
                MainController.CARD_CATEGORIES,
                MainController.CARD_REPORTS,
                MainController.CARD_DATA_MANAGEMENT
        };

        for (String cardName : cardNames) {
            JButton btn = sidebarPanel.getNavButton(cardName);
            assertNotNull(btn, "Nút điều hướng " + cardName + " không được để null");
            assertNotNull(btn.getIcon(), "Nút điều hướng " + cardName + " phải được gán icon vector");
            assertEquals(12, btn.getIconTextGap(), "Khoảng cách icon và text phải là 12px");
        }
    }
}
