package com.pfms.presentation.common;

import com.pfms.presentation.common.component.SidebarNav;
import com.pfms.presentation.common.component.StatusBar;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Cửa sổ ứng dụng chính (Main Stage Shell) sử dụng JavaFX.
 * Bố cục BorderPane: Left = SidebarNav, Center = StackPane Content Area, Bottom = StatusBar.
 */
public class MainStage {

    private final Stage stage;
    private final MainController mainController;
    private final SidebarNav sidebarNav;
    private final StatusBar statusBar;
    private final StackPane contentArea;
    private final Map<String, Node> cardViews = new HashMap<>();

    public MainStage(Stage stage, MainController mainController) {
        this.stage = Objects.requireNonNull(stage, "Stage không được để null");
        this.mainController = Objects.requireNonNull(mainController, "MainController không được để null");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: -color-bg-base;");

        this.sidebarNav = new SidebarNav(this::navigateTo);
        this.statusBar = new StatusBar();
        this.contentArea = new StackPane();
        this.contentArea.setStyle("-fx-background-color: -color-bg-base;");

        root.setLeft(this.sidebarNav);
        root.setCenter(this.contentArea);
        root.setBottom(this.statusBar);

        // Khởi tạo các placeholder cards cho các phân hệ
        initializeDefaultPlaceholders();

        Scene scene = new Scene(root, ViewConstants.APP_WIDTH, ViewConstants.APP_HEIGHT);
        var cssResource = getClass().getResource("/css/pfms-theme.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        }

        stage.setScene(scene);
        stage.setTitle(ViewConstants.APP_TITLE);
        stage.setMinWidth(ViewConstants.MIN_WIDTH);
        stage.setMinHeight(ViewConstants.MIN_HEIGHT);

        // Hiển thị thẻ mặc định Dashboard
        showCard(MainController.CARD_DASHBOARD);
    }

    public void registerView(String cardName, Node viewNode) {
        Objects.requireNonNull(cardName, "Tên card không được để null");
        Objects.requireNonNull(viewNode, "View node không được để null");

        Node old = cardViews.put(cardName, viewNode);
        if (old != null) {
            contentArea.getChildren().remove(old);
        }
        viewNode.setVisible(false);
        contentArea.getChildren().add(viewNode);

        if (cardName.equals(sidebarNav.getCurrentActiveTab())) {
            showCard(cardName);
        }
    }

    public void showCard(String cardName) {
        Node target = cardViews.get(cardName);
        if (target != null) {
            for (Node child : contentArea.getChildren()) {
                child.setVisible(child == target);
            }
            target.toFront();
        }
    }

    private void navigateTo(String cardName) {
        this.mainController.navigateTo(cardName);
        showCard(cardName);
    }

    private void initializeDefaultPlaceholders() {
        registerPlaceholder(MainController.CARD_DASHBOARD, "Tổng quan (Dashboard)");
        registerPlaceholder(MainController.CARD_WALLETS, "Sổ ví (Wallets)");
        registerPlaceholder(MainController.CARD_TRANSACTIONS, "Giao dịch (Transactions)");
        registerPlaceholder(MainController.CARD_TRANSFERS, "Chuyển quỹ (Transfers)");
        registerPlaceholder(MainController.CARD_CATEGORIES, "Danh mục (Categories)");
        registerPlaceholder(MainController.CARD_REPORTS, "Báo cáo (Reports)");
        registerPlaceholder(MainController.CARD_DATA_MANAGEMENT, "Dữ liệu (Data Management)");
    }

    private void registerPlaceholder(String cardName, String title) {
        VBox placeholder = new VBox(12);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setStyle("-fx-background-color: -color-bg-base;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label descLabel = new Label("Giao diện JavaFX đang sẵn sàng nạp...");
        descLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 13px;");

        placeholder.getChildren().addAll(titleLabel, descLabel);
        registerView(cardName, placeholder);
    }

    public Stage getStage() {
        return stage;
    }

    public MainController getMainController() {
        return mainController;
    }

    public SidebarNav getSidebarNav() {
        return sidebarNav;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public void show() {
        this.stage.show();
    }
}
