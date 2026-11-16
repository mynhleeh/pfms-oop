package com.pfms.presentation.common.component;

import com.pfms.presentation.common.MainController;
import com.pfms.testutil.JavaFxTestHelper;
import javafx.scene.control.Button;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử SidebarNav - Thanh điều hướng phân hệ bên trái JavaFX theo chuẩn giao diện cũ")
class SidebarNavTest {

    private SidebarNav sidebarNav;
    private AtomicReference<String> selectedNavigation;

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        selectedNavigation = new AtomicReference<>();
        JavaFxTestHelper.runOnFxThread(() -> {
            sidebarNav = new SidebarNav(selectedNavigation::set);
        });
    }

    @Test
    @DisplayName("Khởi tạo đúng thẻ mặc định ban đầu là DASHBOARD")
    void testInitialActiveTab() {
        assertEquals(MainController.CARD_DASHBOARD, sidebarNav.getCurrentActiveTab());
    }

    @Test
    @DisplayName("Kích hoạt chuyển thẻ qua hàm setActiveTab cập nhật style class chính xác")
    void testSetActiveTab() {
        JavaFxTestHelper.runOnFxThread(() -> {
            sidebarNav.setActiveTab(MainController.CARD_WALLETS);
        });

        assertEquals(MainController.CARD_WALLETS, sidebarNav.getCurrentActiveTab());
        Button walletBtn = sidebarNav.getNavButton(MainController.CARD_WALLETS);
        assertNotNull(walletBtn);
        assertTrue(walletBtn.getStyleClass().contains("nav-button-active"));

        Button dashboardBtn = sidebarNav.getNavButton(MainController.CARD_DASHBOARD);
        assertNotNull(dashboardBtn);
        assertFalse(dashboardBtn.getStyleClass().contains("nav-button-active"));
    }

    @Test
    @DisplayName("Mô phỏng click nút điều hướng phát đúng sự kiện callback và đổi active tab")
    void testButtonClickTriggersCallback() {
        JavaFxTestHelper.runOnFxThread(() -> {
            sidebarNav.triggerCardClick(MainController.CARD_TRANSACTIONS);
        });

        assertEquals(MainController.CARD_TRANSACTIONS, selectedNavigation.get());
        assertEquals(MainController.CARD_TRANSACTIONS, sidebarNav.getCurrentActiveTab());
    }

    @Test
    @DisplayName("Ném NullPointerException khi callback bị null")
    void testNullCallbackThrowsException() {
        assertThrows(NullPointerException.class, () -> new SidebarNav(null));
    }

    @Test
    @DisplayName("Brand Header hiển thị đúng logo thương hiệu và tên PFMS")
    void testBrandHeader() {
        assertNotNull(sidebarNav.getBrandLabel());
        assertEquals("PFMS", sidebarNav.getBrandLabel().getText());
        assertNotNull(sidebarNav.getSubtitleLabel());
    }

    @Test
    @DisplayName("Tất cả 7 nút điều hướng phân hệ đều có Graphic Icon vector hợp lệ")
    void testNavButtonsAndIconsPresent() {
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
            Button btn = sidebarNav.getNavButton(cardName);
            assertNotNull(btn, "Nút điều hướng " + cardName + " không được để null");
            assertNotNull(btn.getGraphic(), "Nút điều hướng " + cardName + " phải được gán Graphic Icon");
            assertEquals(12.0, btn.getGraphicTextGap(), 0.01, "Khoảng cách icon và text phải là 12px");
        }
    }

    @Test
    @DisplayName("Các nút điều hướng có khoảng hở margin đối xứng hai bên so với khung viền sidebar")
    void testNavButtonsHaveHorizontalMargins() {
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
            Button btn = sidebarNav.getNavButton(cardName);
            javafx.geometry.Insets margin = javafx.scene.layout.VBox.getMargin(btn);
            assertNotNull(margin, "Nút điều hướng " + cardName + " phải có margin để không đè viền khung");
            assertEquals(10.0, margin.getLeft(), 0.01, "Margin trái của nút phải là 10px");
            assertEquals(10.0, margin.getRight(), 0.01, "Margin phải của nút phải là 10px");
        }
    }
}
