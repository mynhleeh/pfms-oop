package com.pfms.presentation.view.component;

import com.pfms.presentation.controller.MainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
