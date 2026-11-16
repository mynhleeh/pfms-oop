package com.pfms.presentation.wallet.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Nút gạt chuyển đổi chế độ xem hiển thị ví: Dạng lưới (Card Grid) vs Dạng bảng (Table).
 */
public class ViewModeSwitcher extends HBox {

    public enum ViewMode {
        CARD_GRID,
        TABLE
    }

    private final Button btnCardGrid = new Button("⊞ Thẻ ví");
    private final Button btnTable = new Button("☰ Danh sách");
    private final Consumer<ViewMode> onModeChanged;
    private ViewMode currentMode = ViewMode.CARD_GRID;

    public ViewModeSwitcher(Consumer<ViewMode> onModeChanged) {
        this.onModeChanged = Objects.requireNonNull(onModeChanged, "Callback onModeChanged không được để null");

        getStyleClass().add("view-mode-switcher");
        setAlignment(Pos.CENTER_LEFT);

        btnCardGrid.getStyleClass().addAll("view-mode-btn", "view-mode-btn-active");
        btnTable.getStyleClass().add("view-mode-btn");

        btnCardGrid.setOnAction(e -> setMode(ViewMode.CARD_GRID));
        btnTable.setOnAction(e -> setMode(ViewMode.TABLE));

        getChildren().addAll(btnCardGrid, btnTable);
    }

    public ViewMode getCurrentMode() {
        return currentMode;
    }

    public void setMode(ViewMode mode) {
        Objects.requireNonNull(mode, "ViewMode không được để null");
        if (this.currentMode == mode && getChildren().contains(btnCardGrid)) {
            return;
        }
        this.currentMode = mode;
        if (mode == ViewMode.CARD_GRID) {
            btnCardGrid.getStyleClass().removeAll("view-mode-btn-active");
            btnCardGrid.getStyleClass().add("view-mode-btn-active");
            btnTable.getStyleClass().removeAll("view-mode-btn-active");
        } else {
            btnTable.getStyleClass().removeAll("view-mode-btn-active");
            btnTable.getStyleClass().add("view-mode-btn-active");
            btnCardGrid.getStyleClass().removeAll("view-mode-btn-active");
        }
        onModeChanged.accept(mode);
    }
}
