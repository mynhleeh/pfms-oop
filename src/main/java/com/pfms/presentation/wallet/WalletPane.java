package com.pfms.presentation.wallet;

import com.pfms.presentation.common.AbstractBasePane;
import com.pfms.presentation.common.component.KpiPill;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.wallet.component.ViewModeSwitcher;
import com.pfms.presentation.wallet.component.WalletCardNode;
import com.pfms.presentation.wallet.dialog.AdjustBalanceDialog;
import com.pfms.presentation.wallet.dialog.CreateWalletDialog;
import com.pfms.presentation.wallet.dialog.UpdateWalletDialog;
import com.pfms.presentation.wallet.viewmodel.WalletItemViewModel;
import com.pfms.presentation.wallet.viewmodel.WalletsSummaryViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Bảng điều khiển phân hệ Quản lý Ví (Wallet Management Pane) sử dụng JavaFX.
 * Hiện thực hóa giao diện WalletView theo mô hình MVP (Passive View), kế thừa AbstractBasePane.
 */
public class WalletPane extends AbstractBasePane implements WalletView {

    private final WalletGUIController controller;
    private final WalletPresenter presenter;
    private final ObservableList<WalletItemViewModel> tableData = FXCollections.observableArrayList();
    private final TableView<WalletItemViewModel> tableView = new TableView<>(tableData);

    private final FlowPane cardGridPane = new FlowPane();
    private final ScrollPane cardGridScroll = new ScrollPane(cardGridPane);

    private final KpiPill kpiTotalBalance = new KpiPill("Tổng số dư", "0 ₫", KpiPill.Variant.GOLD);
    private final KpiPill kpiActiveCount = new KpiPill("Đang hoạt động", "0 ví", KpiPill.Variant.INCOME);
    private final KpiPill kpiArchivedCount = new KpiPill("Lưu trữ", "0 ví", KpiPill.Variant.WARNING);
    private final ComboBox<String> statusFilterBox = new ComboBox<>();
    private final TextField searchField = new TextField();
    private final ViewModeSwitcher viewModeSwitcher = new ViewModeSwitcher(this::switchViewMode);

    private List<WalletItemViewModel> allWallets = new ArrayList<>();

    public WalletPane(WalletGUIController controller) {
        super();
        this.controller = Objects.requireNonNull(controller, "WalletGUIController không được để null");
        this.presenter = new WalletPresenter(this, controller);

        // Top Section: Header + Toolbar
        VBox topBox = new VBox(14);
        topBox.getChildren().addAll(createHeaderSection(), createToolbarSection());
        setTop(topBox);

        // Center Section: Dual-View Container (Card Grid + Table)
        setCenter(createCenterSection());

        // Nạp dữ liệu ban đầu
        this.presenter.loadData();
    }

    private Node createHeaderSection() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Sổ Ví Tài Chính");
        title.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitle = new Label("Quản lý các nguồn tiền mặt, tài khoản ngân hàng và thẻ tín dụng cá nhân");
        subtitle.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox kpiBox = new HBox(10);
        kpiBox.setAlignment(Pos.CENTER_RIGHT);
        kpiBox.getChildren().addAll(kpiTotalBalance, kpiActiveCount, kpiArchivedCount);

        header.getChildren().addAll(titleBox, spacer, kpiBox);
        return header;
    }

    private Node createToolbarSection() {
        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button btnAdd = new Button("+ Thêm ví mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> this.presenter.onAddWalletClicked());

        Label statusLabel = new Label("Trạng thái:");
        statusLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");

        statusFilterBox.getItems().addAll("Tất cả trạng thái", "ACTIVE", "ARCHIVED", "DELETED");
        statusFilterBox.setValue("Tất cả trạng thái");
        statusFilterBox.setOnAction(e -> {
            String val = statusFilterBox.getValue();
            String filter = "Tất cả trạng thái".equals(val) ? null : val;
            this.presenter.filterWallets(filter);
        });

        searchField.setPromptText("Tìm kiếm tên ví, ghi chú...");
        searchField.setPrefWidth(240);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyClientFilter());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnReset = new Button("Đặt lại");
        btnReset.getStyleClass().add("btn-secondary");
        btnReset.setOnAction(e -> {
            searchField.clear();
            statusFilterBox.setValue("Tất cả trạng thái");
            this.presenter.filterWallets(null);
        });

        Button btnRefresh = new Button("Làm mới");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> this.presenter.loadData());

        toolbar.getChildren().addAll(btnAdd, statusLabel, statusFilterBox, searchField, spacer, btnReset, btnRefresh);
        return toolbar;
    }

    private Node createCenterSection() {
        // Setup Card Grid ScrollPane (Chế độ xem thẻ ví dạng lưới duy nhất)
        cardGridPane.setHgap(18);
        cardGridPane.setVgap(18);
        cardGridPane.setPadding(new Insets(16, 4, 16, 4));
        cardGridPane.setStyle("-fx-background-color: transparent;");

        cardGridScroll.setFitToWidth(true);
        cardGridScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        cardGridScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        cardGridScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        cardGridScroll.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getWidth() > 0) {
                updateCardGridWidth(newVal.getWidth() - 24);
            }
        });

        return cardGridScroll;
    }

    private void updateCardGridWidth(double availableWidth) {
        if (availableWidth <= 0) return;
        double gap = 18.0;
        int cols;
        if (availableWidth < 700) {
            cols = 1;
        } else if (availableWidth < 1140) {
            cols = 2;
        } else {
            cols = 3;
        }
        double cardWidth = Math.floor((availableWidth - (cols - 1) * gap) / cols);
        for (Node node : cardGridPane.getChildren()) {
            if (node instanceof Region card) {
                card.setPrefWidth(cardWidth);
                card.setMinWidth(cardWidth);
                card.setMaxWidth(cardWidth);
            }
        }
    }

    private void switchViewMode(ViewModeSwitcher.ViewMode mode) {
        if (mode == ViewModeSwitcher.ViewMode.CARD_GRID) {
            cardGridScroll.setVisible(true);
            tableView.setVisible(false);
        } else {
            cardGridScroll.setVisible(false);
            tableView.setVisible(true);
        }
    }

    @SuppressWarnings("unchecked")
    private Node createTableSection() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("table-view");

        TableColumn<WalletItemViewModel, String> colName = new TableColumn<>("Tên ví tài chính");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        colName.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int idx = getIndex();
                    if (idx < 0 || idx >= getTableView().getItems().size()) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }
                    WalletItemViewModel vm = getTableView().getItems().get(idx);
                    VBox box = new VBox(3);
                    Label nameLbl = new Label(vm.name());
                    nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;");

                    if (vm.isDefault()) {
                        Label defBadge = new Label("[Ví mặc định]");
                        defBadge.setStyle("-fx-font-size: 10px; -fx-text-fill: -color-primary-light; -fx-font-weight: bold;");
                        box.getChildren().addAll(nameLbl, defBadge);
                    } else {
                        box.getChildren().add(nameLbl);
                    }
                    setGraphic(box);
                }
            }
        });
        colName.setPrefWidth(220);

        TableColumn<WalletItemViewModel, String> colBalance = new TableColumn<>("Số dư khả dụng");
        colBalance.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().formattedBalance()));
        colBalance.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: -color-primary-light;");
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
                    badge.getStyleClass().add("badge-status");
                    if ("ACTIVE".equalsIgnoreCase(item) || "Đang hoạt động".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-status-active");
                    } else if ("ARCHIVED".equalsIgnoreCase(item) || "Lưu trữ".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-status-archived");
                    } else {
                        badge.getStyleClass().add("badge-status-deleted");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        colStatus.setPrefWidth(130);

        TableColumn<WalletItemViewModel, String> colNote = new TableColumn<>("Ghi chú");
        colNote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().note() != null ? data.getValue().note() : ""));
        colNote.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isBlank()) {
                    setText(null);
                    Label dash = new Label("—");
                    dash.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");
                    setGraphic(dash);
                } else {
                    Label lbl = new Label(item);
                    lbl.setWrapText(true);
                    lbl.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
                    setGraphic(lbl);
                }
            }
        });
        colNote.setPrefWidth(220);

        TableColumn<WalletItemViewModel, Void> colActions = new TableColumn<>("Thao tác");
        colActions.setSortable(false);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnAdjust = new Button("Nạp/Rút");
            private final Button btnEdit = new Button("Sửa");
            private final Button btnToggle = new Button("Lưu trữ");
            private final Button btnDelete = new Button("Xóa");
            private final HBox box = new HBox(6, btnAdjust, btnEdit, btnToggle, btnDelete);

            {
                btnAdjust.getStyleClass().addAll("btn-action-sm", "btn-action-gold");
                btnEdit.getStyleClass().add("btn-action-sm");
                btnToggle.getStyleClass().add("btn-action-sm");
                btnDelete.getStyleClass().addAll("btn-action-sm", "btn-danger");
                box.setAlignment(Pos.CENTER);

                btnAdjust.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        presenter.onAdjustBalanceClicked(getTableView().getItems().get(idx));
                    }
                });
                btnEdit.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        presenter.onEditWalletClicked(getTableView().getItems().get(idx));
                    }
                });
                btnToggle.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        WalletItemViewModel it = getTableView().getItems().get(idx);
                        presenter.toggleStatus(it.id(), it.isActive());
                    }
                });
                btnDelete.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        WalletItemViewModel it = getTableView().getItems().get(idx);
                        presenter.deleteWallet(it.id(), it.name());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        WalletItemViewModel it = getTableView().getItems().get(idx);
                        btnToggle.setText(it.isActive() ? "Lưu trữ" : "Kích hoạt");
                    }
                    setGraphic(box);
                }
            }
        });
        colActions.setPrefWidth(260);

        tableView.getColumns().addAll(colName, colBalance, colStatus, colNote, colActions);
        return tableView;
    }

    private void applyClientFilter() {
        String keyword = searchField.getText();
        List<WalletItemViewModel> filtered;
        if (keyword == null || keyword.isBlank()) {
            filtered = allWallets;
        } else {
            String lower = keyword.trim().toLowerCase();
            filtered = allWallets.stream()
                    .filter(w -> (w.name() != null && w.name().toLowerCase().contains(lower))
                            || (w.note() != null && w.note().toLowerCase().contains(lower)))
                    .toList();
        }

        // Cập nhật Table View
        tableData.setAll(filtered);

        // Cập nhật Card Grid View
        cardGridPane.getChildren().clear();
        for (WalletItemViewModel item : filtered) {
            WalletCardNode card = new WalletCardNode(
                    item,
                    presenter::onAdjustBalanceClicked,
                    presenter::onEditWalletClicked,
                    it -> presenter.toggleStatus(it.id(), it.isActive()),
                    it -> presenter.deleteWallet(it.id(), it.name())
            );
            cardGridPane.getChildren().add(card);
        }

        if (cardGridScroll.getViewportBounds() != null && cardGridScroll.getViewportBounds().getWidth() > 0) {
            updateCardGridWidth(cardGridScroll.getViewportBounds().getWidth() - 24);
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
                kpiTotalBalance.setValue(summary.formattedTotalBalance());
                kpiActiveCount.setValue(summary.activeCount() + " ví");
                kpiArchivedCount.setValue(summary.archivedCount() + " ví");
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
    // endregion

    public WalletPresenter getPresenter() {
        return presenter;
    }

    public ObservableList<WalletItemViewModel> getTableData() {
        return tableData;
    }

    public TableView<WalletItemViewModel> getTableView() {
        return tableView;
    }

    public FlowPane getCardGridPane() {
        return cardGridPane;
    }

    public ViewModeSwitcher getViewModeSwitcher() {
        return viewModeSwitcher;
    }
}
