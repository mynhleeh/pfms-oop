package com.pfms.presentation.common.component;

import com.pfms.presentation.common.MainController;
import com.pfms.presentation.common.ViewConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Thanh điều hướng bên trái (Navigation Sidebar) sử dụng JavaFX.
 * Hiển thị nhận diện thương hiệu PFMS và 7 nút phân hệ chức năng.
 */
public class SidebarNav extends VBox {

    private final Map<String, Button> navButtons = new HashMap<>();
    private final Consumer<String> onTabSelected;
    private String currentActiveTab = MainController.CARD_DASHBOARD;

    public SidebarNav(Consumer<String> onTabSelected) {
        this.onTabSelected = Objects.requireNonNull(onTabSelected, "Callback onTabSelected không được để null");

        setPrefWidth(ViewConstants.SIDEBAR_WIDTH);
        setMinWidth(ViewConstants.SIDEBAR_WIDTH);
        setMaxWidth(ViewConstants.SIDEBAR_WIDTH);
        setStyle("-fx-background-color: -color-sidebar-bg; -fx-border-color: transparent -color-border transparent transparent; -fx-border-width: 0 1px 0 0;");
        setSpacing(4);
        setPadding(new Insets(16, 0, 16, 0));

        // Header Branding
        VBox headerBox = new VBox(2);
        headerBox.setPadding(new Insets(0, 16, 20, 16));

        Label brandLabel = new Label(ViewConstants.APP_BRAND);
        brandLabel.setStyle("-fx-text-fill: -color-primary; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label(ViewConstants.APP_SUBTITLE);
        subtitleLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        headerBox.getChildren().addAll(brandLabel, subtitleLabel);
        getChildren().add(headerBox);

        // Navigation Items
        createNavButton("Tổng quan", MainController.CARD_DASHBOARD);
        createNavButton("Sổ ví", MainController.CARD_WALLETS);
        createNavButton("Giao dịch", MainController.CARD_TRANSACTIONS);
        createNavButton("Chuyển quỹ", MainController.CARD_TRANSFERS);
        createNavButton("Danh mục", MainController.CARD_CATEGORIES);
        createNavButton("Báo cáo", MainController.CARD_REPORTS);
        createNavButton("Dữ liệu", MainController.CARD_DATA_MANAGEMENT);

        setActiveTab(MainController.CARD_DASHBOARD);
    }

    private void createNavButton(String title, String cardKey) {
        Button btn = new Button(title);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.BASELINE_LEFT);
        btn.setOnAction(e -> {
            setActiveTab(cardKey);
            this.onTabSelected.accept(cardKey);
        });

        navButtons.put(cardKey, btn);
        getChildren().add(btn);
    }

    public void setActiveTab(String cardKey) {
        this.currentActiveTab = cardKey;
        for (Map.Entry<String, Button> entry : navButtons.entrySet()) {
            Button btn = entry.getValue();
            if (entry.getKey().equals(cardKey)) {
                if (!btn.getStyleClass().contains("nav-button-active")) {
                    btn.getStyleClass().add("nav-button-active");
                }
            } else {
                btn.getStyleClass().remove("nav-button-active");
            }
        }
    }

    public String getCurrentActiveTab() {
        return currentActiveTab;
    }
}
