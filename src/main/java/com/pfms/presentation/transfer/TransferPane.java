package com.pfms.presentation.transfer;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.util.FxAlertUtils;
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

    private final Label statTotalTransferredLabel = new Label("0 VNĐ");
    private final Label statTotalFeesLabel = new Label("0 VNĐ");
    private final Label statCountLabel = new Label("0 giao dịch");

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

        // Top Section: Header + KPI Summary Cards
        VBox topBox = new VBox(16);
        topBox.getChildren().addAll(createHeaderSection(), createKpiCardsSection());
        setTop(topBox);

        // Center Section: TableView
        setCenter(createTableSection());

        // Bottom Section: Pagination & Summary Footer
        setBottom(createBottomSection());

        // Nạp dữ liệu ban đầu
        this.presenter.loadTransfers(null);
    }

    private Node createHeaderSection() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Quản Lý Chuyển Tiền Giữa Các Ví");
        title.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnRefresh = new Button("Làm mới");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> this.presenter.loadTransfers(this.presenter.getCurrentFilter()));

        Button btnNewTransfer = new Button("+ Chuyển tiền mới");
        btnNewTransfer.getStyleClass().add("btn-primary");
        btnNewTransfer.setOnAction(e -> this.presenter.onNewTransferClicked());

        header.getChildren().addAll(title, spacer, btnRefresh, btnNewTransfer);
        return header;
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

    private Node createTableSection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(12, 0, 0, 0));
        VBox.setVgrow(box, Priority.ALWAYS);

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
        VBox.setVgrow(tableView, Priority.ALWAYS);
        box.getChildren().add(tableView);
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
        });
    }

    private void updateKpiStats(String summaryText, String pageInfoText) {
        if (summaryText != null) {
            String[] parts = summaryText.split("\\|");
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.startsWith("Tổng tiền chuyển:")) {
                    statTotalTransferredLabel.setText(trimmed.replace("Tổng tiền chuyển:", "").trim());
                } else if (trimmed.startsWith("Tổng phí:")) {
                    statTotalFeesLabel.setText(trimmed.replace("Tổng phí:", "").trim());
                }
            }
        }
        if (pageInfoText != null && pageInfoText.contains("Tổng")) {
            int idx = pageInfoText.indexOf("Tổng");
            int endIdx = pageInfoText.indexOf(")", idx);
            if (idx != -1 && endIdx != -1) {
                statCountLabel.setText(pageInfoText.substring(idx + 4, endIdx).trim());
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
