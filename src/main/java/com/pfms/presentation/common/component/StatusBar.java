package com.pfms.presentation.common.component;

import com.pfms.presentation.common.ViewConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * Thanh trạng thái phía dưới (Bottom Status Bar) sử dụng JavaFX.
 * Hiển thị thông báo ngữ cảnh, chỉ báo kho lưu trữ và trạng thái vận hành.
 */
public class StatusBar extends HBox {

    private final Label statusLabel;
    private final Label storageLabel;
    private final Label versionLabel;

    public StatusBar() {
        setPrefHeight(ViewConstants.STATUS_BAR_HEIGHT);
        setMinHeight(ViewConstants.STATUS_BAR_HEIGHT);
        setMaxHeight(ViewConstants.STATUS_BAR_HEIGHT);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(0, 16, 0, 16));
        setStyle("-fx-background-color: -color-sidebar-bg; -fx-border-color: -color-border transparent transparent transparent; -fx-border-width: 1px 0 0 0;");

        statusLabel = new Label("Sẵn sàng");
        statusLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 11px;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        storageLabel = new Label("Lưu trữ: JSON Cục bộ");
        storageLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Label dot = new Label(" • ");
        dot.setStyle("-fx-text-fill: -color-border;");

        versionLabel = new Label("PFMS Core v1.0.0");
        versionLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        getChildren().addAll(statusLabel, spacer, storageLabel, dot, versionLabel);
    }

    public void setStatusMessage(String message) {
        if (message != null && !message.isBlank()) {
            this.statusLabel.setText(message);
        } else {
            this.statusLabel.setText("Sẵn sàng");
        }
    }

    public void setStorageStatus(String storageStatus) {
        if (storageStatus != null) {
            this.storageLabel.setText(storageStatus);
        }
    }

    public String getStatusMessage() {
        return this.statusLabel.getText();
    }
}
