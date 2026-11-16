package com.pfms.presentation.common.util;

import com.pfms.presentation.common.ViewConstants;
import com.pfms.testutil.JavaFxTestHelper;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử FxAlertUtils và ViewConstants JavaFX")
class FxAlertUtilsTest {

    @Test
    @DisplayName("Constructor của FxAlertUtils phải ném UnsupportedOperationException khi khởi tạo qua reflection")
    void testFxAlertUtilsConstructorPrivate() throws NoSuchMethodException {
        Constructor<FxAlertUtils> constructor = FxAlertUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(ex.getCause() instanceof UnsupportedOperationException);
    }

    @Test
    @DisplayName("Các mã màu Hex trong ViewConstants phải đúng chuẩn định dạng #RRGGBB")
    void testViewConstantsHexColors() {
        String hexRegex = "^#[0-9A-Fa-f]{6}$";
        assertTrue(ViewConstants.HEX_PRIMARY.matches(hexRegex));
        assertTrue(ViewConstants.HEX_PRIMARY_LIGHT.matches(hexRegex));
        assertTrue(ViewConstants.HEX_BG_BASE.matches(hexRegex));
        assertTrue(ViewConstants.HEX_CARD_BG.matches(hexRegex));
        assertTrue(ViewConstants.HEX_SIDEBAR_BG.matches(hexRegex));
        assertTrue(ViewConstants.HEX_BORDER.matches(hexRegex));
        assertTrue(ViewConstants.HEX_TEXT_MAIN.matches(hexRegex));
        assertTrue(ViewConstants.HEX_TEXT_MUTED.matches(hexRegex));
        assertTrue(ViewConstants.HEX_INCOME.matches(hexRegex));
        assertTrue(ViewConstants.HEX_EXPENSE.matches(hexRegex));
    }

    @Test
    @DisplayName("showConfirm trả về false an toàn khi chạy trên môi trường headless không có JavaFX Toolkit")
    void testShowConfirmHeadlessSafety() {
        boolean result = FxAlertUtils.showConfirm("Test", "Headless confirm");
        assertFalse(result);
    }

    @Test
    @DisplayName("createAlert tạo hộp thoại Alert đúng loại, tiêu đề và áp dụng stylesheet")
    void testCreateAlertProperties() {
        JavaFxTestHelper.runOnFxThread(() -> {
            Alert alert = FxAlertUtils.createAlert(Alert.AlertType.WARNING, "Cảnh báo kiểm thử", "Nội dung kiểm thử");
            assertNotNull(alert, "Đối tượng Alert không được null");
            assertEquals(Alert.AlertType.WARNING, alert.getAlertType());
            assertEquals("Cảnh báo kiểm thử", alert.getTitle());
            assertEquals("Nội dung kiểm thử", alert.getContentText());
            assertNull(alert.getHeaderText(), "HeaderText phải là null theo chuẩn thiết kế");
            assertNotNull(alert.getDialogPane(), "DialogPane phải được khởi tạo");
            assertFalse(alert.getDialogPane().getStylesheets().isEmpty(), "Phải áp dụng pfms-theme.css");
        });
    }
}
