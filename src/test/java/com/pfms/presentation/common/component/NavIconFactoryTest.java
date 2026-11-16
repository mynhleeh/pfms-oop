package com.pfms.presentation.common.component;

import com.pfms.presentation.common.MainController;
import com.pfms.testutil.JavaFxTestHelper;
import javafx.scene.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử NavIconFactory - Khởi tạo biểu tượng vector JavaFX")
class NavIconFactoryTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Khởi tạo biểu tượng thương hiệu BrandIcon thành công")
    void testCreateBrandIcon() {
        JavaFxTestHelper.runOnFxThread(() -> {
            Node brandIcon = NavIconFactory.createBrandIcon(28);
            assertNotNull(brandIcon);
        });
    }

    @Test
    @DisplayName("Khởi tạo tất cả 7 biểu tượng phân hệ điều hướng thành công")
    void testCreateAllNavIcons() {
        String[] cardNames = {
                MainController.CARD_DASHBOARD,
                MainController.CARD_WALLETS,
                MainController.CARD_TRANSACTIONS,
                MainController.CARD_TRANSFERS,
                MainController.CARD_CATEGORIES,
                MainController.CARD_REPORTS,
                MainController.CARD_DATA_MANAGEMENT
        };

        JavaFxTestHelper.runOnFxThread(() -> {
            for (String cardName : cardNames) {
                Node icon = NavIconFactory.createNavIcon(cardName, 18);
                assertNotNull(icon, "Icon cho card " + cardName + " không được null");
            }
        });
    }

    @Test
    @DisplayName("Ném NullPointerException khi tên card null")
    void testNullCardThrows() {
        assertThrows(NullPointerException.class, () -> NavIconFactory.createNavIcon(null, 18));
    }
}
