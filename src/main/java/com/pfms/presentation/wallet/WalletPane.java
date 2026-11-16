package com.pfms.presentation.wallet;

import com.pfms.presentation.common.util.FxAlertUtils;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.wallet.dialog.AdjustBalanceDialog;
import com.pfms.presentation.wallet.dialog.CreateWalletDialog;
import com.pfms.presentation.wallet.dialog.UpdateWalletDialog;
import com.pfms.presentation.wallet.viewmodel.WalletItemViewModel;
import com.pfms.presentation.wallet.viewmodel.WalletsSummaryViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Bảng điều khiển phân hệ Quản lý Ví (Wallet Management Pane) sử dụng JavaFX.
 * Hiện thực hóa giao diện WalletView theo mô hình MVP (Passive View).
 */
public class WalletPane extends BorderPane implements WalletView {

    private final WalletGUIController controller;
    private final WalletPresenter presenter;
    private final ObservableList<WalletItemViewModel> tableData = FXCollections.observableArrayList();
    private final TableView<WalletItemViewModel> tableView = new TableView<>(tableData);

    private final Label kpiTotalBalance = new Label("0 ₫");
    private final Label kpiActiveCount = new Label("0 ví đang dùng");
    private final Label kpiArchivedCount = new Label("0 ví lưu trữ");
    private final Label summaryFooterLabel = new Label("Sẵn sàng");
    private final ComboBox<String> statusFilterBox = new ComboBox<>();
    private final TextField searchField = new TextField();

    private List<WalletItemViewModel> allWallets = new ArrayList<>();
    private Consumer<String> messageConsumer;
    private Consumer<String> warningConsumer;
    private Consumer<String> errorConsumer;

    public WalletPane(WalletGUIController controller) {
        this.controller = Objects.requireNonNull(controller, "WalletGUIController không được để null");
        this.presenter = new WalletPresenter(this, controller);

        setStyle("-fx-background-color: -color-bg-base;");
        setPadding(new Insets(20));

        // Top Section: Header + KPI Cards + Toolbar
        VBox topBox = new VBox(16);
        topBox.getChildren().addAll(createHeaderSection(), createKpiCardsSection(), createToolbarSection());
        setTop(topBox);

        // Center Section: TableView
        setCenter(createTableSection());

        // Bottom Section: Summary Footer
        HBox bottomBox = new HBox(summaryFooterLabel);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        summaryFooterLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");
        setBottom(bottomBox);

        // Nạp dữ liệu ban đầu
        this.presenter.loadData();
    }

    private Node createHeaderSection() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Sổ Ví Tài Chính");
        title.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAdd = new Button("+ Thêm ví mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> this.presenter.onAddWalletClicked());

        header.getChildren().addAll(title, spacer, btnAdd);
        return header;
    }

    private Node createKpiCardsSection() {
        HBox kpiBox = new HBox(16);

        VBox cardTotal = createKpiCard("TỔNG TÀI SẢN KHẢ DỤNG", kpiTotalBalance, "-color-primary");
        VBox cardActive = createKpiCard("VÍ ĐANG HOẠT ĐỘNG", kpiActiveCount, "-color-income");
        VBox cardArchived = createKpiCard("VÍ ĐANG LƯU TRỮ", kpiArchivedCount, "-color-text-muted");

        HBox.setHgrow(cardTotal, Priority.ALWAYS);
        HBox.setHgrow(cardActive, Priority.ALWAYS);
        HBox.setHgrow(cardArchived, Priority.ALWAYS);

        kpiBox.getChildren().addAll(cardTotal, cardActive, cardArchived);
        return kpiBox;
    }

    private VBox createKpiCard(String subtitle, Label valueLabel, String colorVar) {
        VBox card = new VBox(6);
        card.getStyleClass().add("pfms-card");

        Label sub = new Label(subtitle);
        sub.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        valueLabel.setStyle("-fx-text-fill: " + colorVar + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        card.getChildren().addAll(sub, valueLabel);
        return card;
    }

    private Node createToolbarSection() {
        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        searchField.setPromptText("Tìm kiếm ví theo tên hoặc ghi chú...");
        searchField.setPrefWidth(280);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyClientFilter());

        statusFilterBox.getItems().addAll("Tất cả trạng thái", "ACTIVE", "ARCHIVED", "DELETED");
        statusFilterBox.setValue("Tất cả trạng thái");
        statusFilterBox.setOnAction(e -> {
            String val = statusFilterBox.getValue();
            String filter = "Tất cả trạng thái".equals(val) ? null : val;
            this.presenter.filterWallets(filter);
        });

        toolbar.getChildren().addAll(searchField, statusFilterBox);
        return toolbar;
    }

    @SuppressWarnings("unchecked")
    private Node createTableSection() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("table-view");

        TableColumn<WalletItemViewModel, String> colName = new TableColumn<>("Tên ví");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        colName.setPrefWidth(200);

        TableColumn<WalletItemViewModel, String> colBalance = new TableColumn<>("Số dư khả dụng");
        colBalance.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().formattedBalance()));
        colBalance.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold;");
        colBalance.setPrefWidth(160);

        TableColumn<WalletItemViewModel, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().statusText()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.setPadding(new Insets(2, 8, 2, 8));
                    if ("ACTIVE".equalsIgnoreCase(item) || "Đang hoạt động".equalsIgnoreCase(item)) {
                        badge.setStyle("-fx-background-color: #143520; -fx-text-fill: #4ADE80; -fx-background-radius: 4px;");
                    } else {
                        badge.setStyle("-fx-background-color: #27272A; -fx-text-fill: #A1A1AA; -fx-background-radius: 4px;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        colStatus.setPrefWidth(120);

        TableColumn<WalletItemViewModel, String> colNote = new TableColumn<>("Ghi chú");
        colNote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().note() != null ? data.getValue().note() : ""));
        colNote.setPrefWidth(220);

        TableColumn<WalletItemViewModel, Void> colActions = new TableColumn<>("Thao tác");
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnAdjust = new Button("Kiểm kê");
            private final Button btnToggle = new Button("Trạng thái");
            private final Button btnDelete = new Button("Xóa");
            private final HBox box = new HBox(6, btnEdit, btnAdjust, btnToggle, btnDelete);

            {
                btnEdit.getStyleClass().add("btn-secondary");
                btnAdjust.getStyleClass().add("btn-secondary");
                btnToggle.getStyleClass().add("btn-secondary");
                btnDelete.getStyleClass().add("btn-danger");
                box.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> {
                    WalletItemViewModel item = getTableView().getItems().get(getIndex());
                    presenter.onEditWalletClicked(item);
                });
                btnAdjust.setOnAction(e -> {
                    WalletItemViewModel item = getTableView().getItems().get(getIndex());
                    presenter.onAdjustBalanceClicked(item);
                });
                btnToggle.setOnAction(e -> {
                    WalletItemViewModel item = getTableView().getItems().get(getIndex());
                    presenter.toggleStatus(item.id(), item.isActive());
                });
                btnDelete.setOnAction(e -> {
                    WalletItemViewModel item = getTableView().getItems().get(getIndex());
                    presenter.deleteWallet(item.id(), item.name());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        colActions.setPrefWidth(260);

        tableView.getColumns().addAll(colName, colBalance, colStatus, colNote, colActions);
        return tableView;
    }

    private void applyClientFilter() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.isBlank()) {
            tableData.setAll(allWallets);
        } else {
            String lower = keyword.trim().toLowerCase();
            List<WalletItemViewModel> filtered = allWallets.stream()
                    .filter(w -> (w.name() != null && w.name().toLowerCase().contains(lower))
                            || (w.note() != null && w.note().toLowerCase().contains(lower)))
                    .toList();
            tableData.setAll(filtered);
        }
    }

    // region WalletView Implementation
    @Override
    public void renderWallets(List<WalletItemViewModel> wallets) {
        runOnFx(() -> {
            this.allWallets = wallets != null ? new ArrayList<>(wallets) : new ArrayList<>();
            applyClientFilter();
        });
    }

    @Override
    public void renderSummary(WalletsSummaryViewModel summary) {
        runOnFx(() -> {
            if (summary != null) {
                kpiTotalBalance.setText(summary.formattedTotalBalance());
                kpiActiveCount.setText(summary.activeCount() + " ví đang dùng");
                kpiArchivedCount.setText(summary.archivedCount() + " ví lưu trữ");
                summaryFooterLabel.setText("Tổng số ví: " + (summary.activeCount() + summary.archivedCount()) +
                        " | Tổng số dư khả dụng: " + summary.formattedTotalBalance());
            }
        });
    }

    @Override
    public void openCreateWalletDialog() {
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            CreateWalletDialog dialog = new CreateWalletDialog(owner, this.controller);
            dialog.showAndWait();
            if (dialog.isSuccess()) {
                this.presenter.loadData();
            }
        });
    }

    @Override
    public void openUpdateWalletDialog(WalletItemViewModel wallet) {
        if (wallet == null) return;
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            WalletResponseDTO dto = new WalletResponseDTO(
                    wallet.id(), wallet.name(), wallet.rawBalance(), "VND", wallet.isDefault(),
                    wallet.statusText(), wallet.note(), LocalDateTime.now());
            UpdateWalletDialog dialog = new UpdateWalletDialog(owner, this.controller, dto);
            dialog.showAndWait();
            if (dialog.isSuccess()) {
                this.presenter.loadData();
            }
        });
    }

    @Override
    public void openAdjustBalanceDialog(WalletItemViewModel wallet) {
        if (wallet == null) return;
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            WalletResponseDTO dto = new WalletResponseDTO(
                    wallet.id(), wallet.name(), wallet.rawBalance(), "VND", wallet.isDefault(),
                    wallet.statusText(), wallet.note(), LocalDateTime.now());
            AdjustBalanceDialog dialog = new AdjustBalanceDialog(owner, this.controller, dto);
            dialog.showAndWait();
            if (dialog.isSuccess()) {
                this.presenter.loadData();
            }
        });
    }

    @Override
    public void showMessage(String message) {
        if (messageConsumer != null) {
            messageConsumer.accept(message);
        } else {
            FxAlertUtils.showInfo("PFMS Thông báo", message);
        }
    }

    @Override
    public void showWarning(String message) {
        if (warningConsumer != null) {
            warningConsumer.accept(message);
        } else {
            FxAlertUtils.showWarning("PFMS Cảnh báo", message);
        }
    }

    @Override
    public void showError(String message) {
        if (errorConsumer != null) {
            errorConsumer.accept(message);
        } else {
            FxAlertUtils.showError("PFMS Lỗi", message);
        }
    }

    @Override
    public boolean confirmAction(String message) {
        return FxAlertUtils.showConfirm("Xác nhận thao tác", message);
    }
    // endregion

    private void runOnFx(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    public WalletPresenter getPresenter() { return presenter; }
    public ObservableList<WalletItemViewModel> getTableData() { return tableData; }
    public TableView<WalletItemViewModel> getTableView() { return tableView; }
    public String getSummaryText() { return summaryFooterLabel.getText(); }

    public void setMessageConsumer(Consumer<String> consumer) { this.messageConsumer = consumer; }
    public void setWarningConsumer(Consumer<String> consumer) { this.warningConsumer = consumer; }
    public void setErrorConsumer(Consumer<String> consumer) { this.errorConsumer = consumer; }
}
