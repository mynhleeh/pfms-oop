package com.pfms.presentation.transfer;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.util.FxAlertUtils;
import com.pfms.presentation.transfer.component.TransferRowNode;
import com.pfms.presentation.transfer.dialog.TransferFundsDialog;
import com.pfms.presentation.transfer.viewmodel.TransferItemViewModel;
import com.pfms.presentation.wallet.WalletGUIController;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Bảng điều khiển phân hệ Chuyển tiền Nội bộ (Transfer Management Pane) sử dụng JavaFX.
 * Hiện thực hóa giao diện TransferView theo mô hình MVP (Passive View).
 */
public class TransferPane extends BorderPane implements TransferView {

    private final TransferGUIController transferController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;
    private final TransferPresenter presenter;

    private final ObservableList<TransferItemViewModel> tableData = FXCollections.observableArrayList();
    private final TableView<TransferItemViewModel> tableView = new TableView<>(tableData);

    // Stream Cards Layout
    private final VBox streamItemsContainer = new VBox(8);
    private final ScrollPane streamScrollPane = new ScrollPane(streamItemsContainer);
    private HBox streamHeader;

    private final Label statTotalTransferredLabel = new Label("0 VNĐ");
    private final Label statTotalFeesLabel = new Label("0 VNĐ");
    private final Label statCountLabel = new Label("0 giao dịch");

    private final com.pfms.presentation.common.component.KpiPill kpiTransferred =
            new com.pfms.presentation.common.component.KpiPill("Tổng tiền chuyển", "0 VNĐ", com.pfms.presentation.common.component.KpiPill.Variant.GOLD);
    private final com.pfms.presentation.common.component.KpiPill kpiFees =
            new com.pfms.presentation.common.component.KpiPill("Tổng phí", "0 VNĐ", com.pfms.presentation.common.component.KpiPill.Variant.EXPENSE);
    private final com.pfms.presentation.common.component.KpiPill kpiCount =
            new com.pfms.presentation.common.component.KpiPill("Số lượt chuyển", "0 giao dịch", com.pfms.presentation.common.component.KpiPill.Variant.NEUTRAL);

    private final Label pageInfoLabel = new Label("Trang 1 / 1 (Tổng 0 giao dịch)");
    private final Label summaryFooterLabel = new Label("Tổng tiền chuyển: 0 VNĐ | Tổng phí: 0 VNĐ");

    private Consumer<String> messageConsumer;
    private Consumer<String> warningConsumer;
    private Consumer<String> errorConsumer;

    public TransferPane(
            TransferGUIController transferController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {

        this.transferController = Objects.requireNonNull(transferController, "TransferGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");
        this.presenter = new TransferPresenter(this, transferController);

        setStyle("-fx-background-color: -color-bg-base;");
        setPadding(new Insets(20));

        // Top Section: Header + KPI Summary Cards + Toolbar
        VBox topBox = new VBox(14);
        topBox.getChildren().addAll(createHeaderSection(), createKpiCardsSection(), createToolbarSection());
        setTop(topBox);

        // Center Section: TableView
        setCenter(createTableSection());

        // Bottom Section: Pagination & Summary Footer
        setBottom(createBottomSection());

        // Nạp dữ liệu ban đầu
        this.presenter.loadTransfers(null);
    }

    private Node createHeaderSection() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Quản Lý Chuyển Tiền Giữa Các Ví");
        title.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitle = new Label("Theo dõi luồng điều chuyển số dư qua lại an toàn giữa các tài khoản và ví");
        subtitle.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox kpiBox = new HBox(8);
        kpiBox.setAlignment(Pos.CENTER_RIGHT);
        kpiBox.getChildren().addAll(kpiTransferred, kpiFees, kpiCount);

        header.getChildren().addAll(titleBox, spacer, kpiBox);
        return header;
    }

    private Node createToolbarSection() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button btnNewTransfer = new Button("+ Chuyển tiền mới");
        btnNewTransfer.getStyleClass().add("btn-primary");
        btnNewTransfer.setOnAction(e -> this.presenter.onNewTransferClicked());

        Button btnRefresh = new Button("Làm mới");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> this.presenter.loadTransfers(this.presenter.getCurrentFilter()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label hintLabel = new Label("Tự động ghi nhận phụ phí và bảo toàn số dư hai ví");
        hintLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11.5px;");

        toolbar.getChildren().addAll(btnNewTransfer, btnRefresh, spacer, hintLabel);
        return toolbar;
    }

    private Node createKpiCardsSection() {
        HBox kpiBox = new HBox(16);

        VBox cardTransferred = createKpiCard("TỔNG TIỀN ĐÃ CHUYỂN", statTotalTransferredLabel, "-color-income");
        VBox cardFees = createKpiCard("TỔNG PHÍ GIAO DỊCH", statTotalFeesLabel, "-color-expense");
        VBox cardCount = createKpiCard("TỔNG SỐ GIAO DỊCH", statCountLabel, "-color-primary");

        HBox.setHgrow(cardTransferred, Priority.ALWAYS);
        HBox.setHgrow(cardFees, Priority.ALWAYS);
        HBox.setHgrow(cardCount, Priority.ALWAYS);

        kpiBox.getChildren().addAll(cardTransferred, cardFees, cardCount);
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

    private Label createHeaderLabel(String text, double width, Pos alignment) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setMinWidth(width);
        label.setMaxWidth(width);
        label.setAlignment(alignment);
        String alignStyle = (alignment == Pos.CENTER_RIGHT) ? " -fx-alignment: CENTER-RIGHT;" :
                ((alignment == Pos.CENTER) ? " -fx-alignment: CENTER;" : "");
        label.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;" + alignStyle);
        return label;
    }

    private void setupLegacyTable() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("table-view");

        TableColumn<TransferItemViewModel, Integer> colOrder = new TableColumn<>("STT");
        colOrder.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().displayOrder()));
        colOrder.setStyle("-fx-alignment: CENTER;");
        colOrder.setPrefWidth(50);
        colOrder.setMaxWidth(60);

        TableColumn<TransferItemViewModel, String> colDate = new TableColumn<>("Ngày Chuyển");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().dateFormatted()));
        colDate.setStyle("-fx-alignment: CENTER;");
        colDate.setPrefWidth(120);

        TableColumn<TransferItemViewModel, String> colFrom = new TableColumn<>("Ví Nguồn (Chuyển đi)");
        colFrom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().fromWalletName()));
        colFrom.setPrefWidth(180);

        TableColumn<TransferItemViewModel, String> colTo = new TableColumn<>("Ví Đích (Nhận tiền)");
        colTo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().toWalletName()));
        colTo.setPrefWidth(180);

        TableColumn<TransferItemViewModel, String> colAmount = new TableColumn<>("Số Tiền");
        colAmount.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().formattedAmount()));
        colAmount.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: -color-primary-light;");
        colAmount.setPrefWidth(140);

        TableColumn<TransferItemViewModel, String> colNote = new TableColumn<>("Ghi Chú");
        colNote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().note() != null ? data.getValue().note() : ""));
        colNote.setPrefWidth(220);

        TableColumn<TransferItemViewModel, Void> colActions = new TableColumn<>("Thao Tác");
        colActions.setStyle("-fx-alignment: CENTER;");
        colActions.setPrefWidth(100);
        colActions.setMaxWidth(120);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Xóa");
            {
                btnDelete.getStyleClass().add("btn-danger");
                btnDelete.setOnAction(e -> {
                    TransferItemViewModel item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        presenter.deleteTransfer(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                }
            }
        });

        tableView.getColumns().addAll(List.of(colOrder, colDate, colFrom, colTo, colAmount, colNote, colActions));
    }

    private Node createTableSection() {
        VBox box = new VBox(0);
        box.setPadding(new Insets(12, 0, 0, 0));
        VBox.setVgrow(box, Priority.ALWAYS);

        // Minimalist Guides Stream Header (6 Columns matching TransferRowNode)
        this.streamHeader = new HBox(12);
        this.streamHeader.getStyleClass().add("stream-header");
        this.streamHeader.setAlignment(Pos.CENTER_LEFT);
        this.streamHeader.setStyle("-fx-padding: 8px 16px; -fx-background-color: transparent; " +
                "-fx-border-color: transparent transparent #242424 transparent; -fx-border-width: 0 0 1px 0;");

        Label hDate = createHeaderLabel("NGÀY CHUYỂN", 100, Pos.CENTER_LEFT);
        Label hFrom = createHeaderLabel("VÍ NGUỒN (CHUYỂN)", 140, Pos.CENTER_LEFT);
        Label hTo = createHeaderLabel("VÍ ĐÍCH (NHẬN)", 140, Pos.CENTER_LEFT);
        Label hAmount = createHeaderLabel("SỐ TIỀN & PHỤ PHÍ", 165, Pos.CENTER_RIGHT);

        Label hNote = new Label("GHI CHÚ / DIỄN GIẢI");
        hNote.setMinWidth(0);
        hNote.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hNote, Priority.ALWAYS);
        hNote.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label hActions = createHeaderLabel("THAO TÁC", 115, Pos.CENTER);

        this.streamHeader.getChildren().addAll(hDate, hFrom, hTo, hAmount, hNote, hActions);

        this.streamItemsContainer.setFillWidth(true);
        this.streamItemsContainer.setStyle("-fx-background-color: transparent; -fx-padding: 8px 0 8px 0;");

        this.streamScrollPane.setFitToWidth(true);
        this.streamScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(this.streamScrollPane, Priority.ALWAYS);

        // Setup legacy TableView for tests
        setupLegacyTable();

        box.getChildren().addAll(this.streamHeader, this.streamScrollPane);
        return box;
    }

    private Node createBottomSection() {
        VBox bottomBox = new VBox(6);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));

        HBox paginationBar = new HBox(12);
        paginationBar.setAlignment(Pos.CENTER_LEFT);

        Button btnPrev = new Button("← Trang trước");
        btnPrev.getStyleClass().add("btn-secondary");
        btnPrev.setOnAction(e -> this.presenter.prevPage());

        pageInfoLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");

        Button btnNext = new Button("Trang sau →");
        btnNext.getStyleClass().add("btn-secondary");
        btnNext.setOnAction(e -> this.presenter.nextPage());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        summaryFooterLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        paginationBar.getChildren().addAll(btnPrev, pageInfoLabel, btnNext, spacer, summaryFooterLabel);
        bottomBox.getChildren().add(paginationBar);
        return bottomBox;
    }

    // region TransferView Implementation
    @Override
    public void renderTransfers(List<TransferItemViewModel> transfers, String summaryText, String pageInfoText) {
        runOnFx(() -> {
            tableData.setAll(transfers != null ? transfers : List.of());
            summaryFooterLabel.setText(summaryText != null ? summaryText : "");
            pageInfoLabel.setText(pageInfoText != null ? pageInfoText : "");
            updateKpiStats(summaryText, pageInfoText);

            streamItemsContainer.getChildren().clear();
            if (transfers == null || transfers.isEmpty()) {
                VBox emptyCard = new VBox(8);
                emptyCard.setAlignment(Pos.CENTER);
                emptyCard.setStyle("-fx-background-color: #121212; -fx-background-radius: 8px; " +
                        "-fx-border-color: #1E1E1E; -fx-border-radius: 8px; -fx-padding: 36px 20px;");
                Label emptyText = new Label("Không có giao dịch chuyển tiền nào trong chu kỳ này.");
                emptyText.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 13px;");
                emptyCard.getChildren().add(emptyText);
                streamItemsContainer.getChildren().add(emptyCard);
            } else {
                for (TransferItemViewModel item : transfers) {
                    streamItemsContainer.getChildren().add(new TransferRowNode(item, presenter::deleteTransfer));
                }
            }
        });
    }

    private void updateKpiStats(String summaryText, String pageInfoText) {
        if (summaryText != null) {
            String[] parts = summaryText.split("\\|");
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.startsWith("Tổng tiền chuyển:")) {
                    String val = trimmed.replace("Tổng tiền chuyển:", "").trim();
                    statTotalTransferredLabel.setText(val);
                    kpiTransferred.setValue(val);
                } else if (trimmed.startsWith("Tổng phí:")) {
                    String val = trimmed.replace("Tổng phí:", "").trim();
                    statTotalFeesLabel.setText(val);
                    kpiFees.setValue(val);
                }
            }
        }
        if (pageInfoText != null && pageInfoText.contains("Tổng")) {
            int idx = pageInfoText.indexOf("Tổng");
            int endIdx = pageInfoText.indexOf(")", idx);
            if (idx != -1 && endIdx != -1) {
                String countVal = pageInfoText.substring(idx + 4, endIdx).trim();
                statCountLabel.setText(countVal);
                kpiCount.setValue(countVal);
            }
        }
    }

    @Override
    public void openTransferFundsDialog() {
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            TransferFundsDialog dialog = new TransferFundsDialog(
                    owner, this.transferController, this.walletController, this.categoryController);
            dialog.showAndWait();
            if (dialog.isSuccess()) {
                showMessage("Chuyển tiền thành công!");
                this.presenter.loadTransfers(this.presenter.getCurrentFilter());
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

    // region Getters & Helpers
    public TransferPresenter getPresenter() { return this.presenter; }
    public TableView<TransferItemViewModel> getTableView() { return this.tableView; }
    public ObservableList<TransferItemViewModel> getTableData() { return this.tableData; }
    public HBox getStreamHeader() { return this.streamHeader; }
    public VBox getStreamItemsContainer() { return this.streamItemsContainer; }
    public ScrollPane getStreamScrollPane() { return this.streamScrollPane; }
    public String getSummaryText() { return this.summaryFooterLabel.getText(); }
    public String getPageInfoText() { return this.pageInfoLabel.getText(); }
    public Label getStatTotalTransferredLabel() { return this.statTotalTransferredLabel; }
    public Label getStatTotalFeesLabel() { return this.statTotalFeesLabel; }
    public Label getStatCountLabel() { return this.statCountLabel; }

    public void setMessageConsumer(Consumer<String> consumer) { this.messageConsumer = consumer; }
    public void setWarningConsumer(Consumer<String> consumer) { this.warningConsumer = consumer; }
    public void setErrorConsumer(Consumer<String> consumer) { this.errorConsumer = consumer; }
    // endregion
}
