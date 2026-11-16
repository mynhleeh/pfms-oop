package com.pfms.presentation.common.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tiện ích hiển thị các hộp thoại thông báo và xác nhận chuẩn JavaFX.
 * Tự động đồng bộ hóa với luồng JavaFX Application Thread và áp dụng pfms-theme.css.
 */
public final class FxAlertUtils {

    private FxAlertUtils() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo");
    }

    public static void showInfo(String title, String message) {
        runOnFxThread(() -> createAlert(Alert.AlertType.INFORMATION, title, message).showAndWait());
    }

    public static void showWarning(String title, String message) {
        runOnFxThread(() -> createAlert(Alert.AlertType.WARNING, title, message).showAndWait());
    }

    public static void showError(String title, String message) {
        runOnFxThread(() -> createAlert(Alert.AlertType.ERROR, title, message).showAndWait());
    }

    public static boolean showConfirm(String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            AtomicBoolean result = new AtomicBoolean(false);
            try {
                Platform.runLater(() -> result.set(executeConfirm(title, message)));
            } catch (IllegalStateException ex) {
                return false;
            }
            return result.get();
        }
        return executeConfirm(title, message);
    }

    private static boolean executeConfirm(String title, String message) {
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, message);
        Optional<ButtonType> choice = alert.showAndWait();
        return choice.isPresent() && choice.get() == ButtonType.OK;
    }

    public static Alert createAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title != null ? title : "PFMS Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogPane pane = alert.getDialogPane();
        var resource = FxAlertUtils.class.getResource("/css/pfms-theme.css");
        if (resource != null) {
            pane.getStylesheets().add(resource.toExternalForm());
        }
        return alert;
    }

    private static void runOnFxThread(Runnable action) {
        try {
            if (Platform.isFxApplicationThread()) {
                action.run();
            } else {
                Platform.runLater(action);
            }
        } catch (IllegalStateException ex) {
            // Không khởi tạo JavaFX toolkit trong chế độ headless test
        }
    }
}
