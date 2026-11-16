package com.pfms.presentation.common.component;

import com.pfms.presentation.common.MainController;
import com.pfms.presentation.common.ViewConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Thanh điều hướng bên trái (Navigation Sidebar) sử dụng JavaFX.
 * Hiển thị khối logo thương hiệu PFMS dạng kim cương vàng kim và 7 nút phân hệ chức năng
 * kèm biểu tượng vector theo phong cách Luxury Black &amp; Gold của giao diện chuẩn.
 */
public class SidebarNav extends VBox {

    private final Map<String, Button> navButtons = new LinkedHashMap<>();
    private final Consumer<String> onTabSelected;
    private final Label brandLabel;
    private final Label subtitleLabel;
    private String currentActiveTab = MainController.CARD_DASHBOARD;

    public SidebarNav(Consumer<String> onTabSelected) {
        this.onTabSelected = Objects.requireNonNull(onTabSelected, "Callback onTabSelected không được để null");

        setPrefWidth(ViewConstants.SIDEBAR_WIDTH);
        setMinWidth(ViewConstants.SIDEBAR_WIDTH);
        setMaxWidth(ViewConstants.SIDEBAR_WIDTH);
        setStyle("-fx-background-color: -color-sidebar-bg; -fx-border-color: transparent -color-border transparent transparent; -fx-border-width: 0 1px 0 0;");
        setSpacing(4);
        setPadding(new Insets(16, 0, 0, 0));

        // Header Branding
        VBox headerBox = new VBox(6);
        headerBox.setPadding(new Insets(4, 16, 20, 16));

        HBox brandHBox = new HBox(10);
        brandHBox.setAlignment(Pos.CENTER_LEFT);

        Node brandIcon = NavIconFactory.createBrandIcon(28);

        this.brandLabel = new Label(ViewConstants.APP_BRAND);
        this.brandLabel.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 20px; -fx-font-weight: bold;");

        brandHBox.getChildren().addAll(brandIcon, this.brandLabel);

        this.subtitleLabel = new Label(ViewConstants.APP_SUBTITLE);
        this.subtitleLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        headerBox.getChildren().addAll(brandHBox, this.subtitleLabel);
        getChildren().add(headerBox);

        // Navigation Items
        createNavButton("Tổng quan", MainController.CARD_DASHBOARD);
        createNavButton("Ví tài chính", MainController.CARD_WALLETS);
        createNavButton("Thu & Chi", MainController.CARD_TRANSACTIONS);
        createNavButton("Chuyển tiền", MainController.CARD_TRANSFERS);
        createNavButton("Danh mục", MainController.CARD_CATEGORIES);
        createNavButton("Báo cáo", MainController.CARD_REPORTS);
        createNavButton("Quản trị dữ liệu", MainController.CARD_DATA_MANAGEMENT);

        setActiveTab(MainController.CARD_DASHBOARD);

        // Spacer đẩy footer xuống đáy
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        getChildren().add(spacer);

        // Footer đáy sidebar
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER_LEFT);
        footerBox.setPadding(new Insets(10, 16, 12, 16));
        footerBox.setStyle("-fx-border-color: -color-border transparent transparent transparent; -fx-border-width: 1px 0 0 0;");

        Label versionLabel = new Label("Phiên bản 1.0.0");
        versionLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Label techLabel = new Label("PFMS Core");
        techLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        footerBox.getChildren().addAll(versionLabel, footerSpacer, techLabel);
        getChildren().add(footerBox);
    }

    private void createNavButton(String title, String cardKey) {
        Button btn = new Button(title);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.BASELINE_LEFT);

        Node icon = NavIconFactory.createNavIcon(cardKey, 18);
        btn.setGraphic(icon);
        btn.setGraphicTextGap(12);

        btn.setOnAction(e -> {
            setActiveTab(cardKey);
            this.onTabSelected.accept(cardKey);
        });

        VBox.setMargin(btn, new Insets(1, 10, 1, 10));
        navButtons.put(cardKey, btn);
        getChildren().add(btn);
    }

    public void setActiveTab(String cardKey) {
        if (cardKey == null || !navButtons.containsKey(cardKey)) {
            return;
        }
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

    public Button getNavButton(String cardKey) {
        return navButtons.get(cardKey);
    }

    public Label getBrandLabel() {
        return brandLabel;
    }

    public Label getSubtitleLabel() {
        return subtitleLabel;
    }

    public void triggerCardClick(String cardKey) {
        Button btn = navButtons.get(cardKey);
        if (btn != null) {
            btn.fire();
        }
    }
}
