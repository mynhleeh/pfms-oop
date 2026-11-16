package com.pfms.presentation.transaction;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.util.FxAlertUtils;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.transaction.component.TransactionFilterBar;
import com.pfms.presentation.transaction.dialog.RecordTransactionDialog;
import com.pfms.presentation.transaction.dialog.UpdateTransactionDialog;
import com.pfms.presentation.transaction.viewmodel.TransactionItemViewModel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Bảng điều khiển phân hệ Quản lý Giao dịch Thu &amp; Chi (Transaction Management Pane) sử dụng JavaFX.
 * Hiện thực hóa giao diện TransactionView theo mô hình MVP (Passive View).
 */
public class TransactionPane extends BorderPane implements TransactionView {

    private final TransactionGUIController transactionController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;
    private final TransactionPresenter presenter;

    private final TransactionFilterBar filterBar;

    private final ObservableList<TransactionItemViewModel> tableData = FXCollections.observableArrayList();
    private final TableView<TransactionItemViewModel> tableView = new TableView<>(tableData);

    private final Label statTotalIncomeLabel = new Label("0 VNĐ");
    private final Label statTotalExpenseLabel = new Label("0 VNĐ");
    private final Label statNetCashFlowLabel = new Label("0 VNĐ");

    private final Label pageInfoLabel = new Label("Trang 1 / 1 (Tổng 0 giao dịch)");
    private final Label summaryFooterLabel = new Label("");

    private Consumer<String> messageConsumer;
    private Consumer<String> warningConsumer;
    private Consumer<String> errorConsumer;

    public TransactionPane(
            TransactionGUIController transactionController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {

        this.transactionController = Objects.requireNonNull(transactionController, "TransactionGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");
        this.presenter = new TransactionPresenter(this, transactionController);

        this.filterBar = new TransactionFilterBar();
        this.filterBar.setOnFilterAction(() -> this.presenter.loadTransactions(
                this.filterBar.buildFilterRequest(this.presenter.getCurrentPage(), this.presenter.getPageSize())));
        this.filterBar.setOnResetAction(() -> this.presenter.loadTransactions(null));
        this.filterBar.setOnAddAction(this.presenter::onAddTransactionClicked);

        setStyle("-fx-background-color: -color-bg-base;");
        setPadding(new Insets(20));

        // Top Section: Header + KPI Summary Cards + FilterBar
        VBox topBox = new VBox(14);
        topBox.getChildren().addAll(createHeaderSection(), createKpiCardsSection(), this.filterBar);
        setTop(topBox);

        // Center Section: TableView
        setCenter(createTableSection());

        // Bottom Section: Pagination Bar & Summary
        setBottom(createBottomSection());

        // Nạp dữ liệu bộ lọc và giao dịch ban đầu
        reloadFilterData();
        this.presenter.loadTransactions(null);
    }

    private Node createHeaderSection() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Sổ Giao Dịch Thu & Chi");
        title.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAddIncome = new Button("+ Ghi thu nhập");
        btnAddIncome.setStyle("-fx-background-color: #143520; -fx-text-fill: #4ADE80; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16;");
        btnAddIncome.setOnAction(e -> {
            setSelectedTypeFilter("INCOME");
            this.presenter.onAddTransactionClicked();
        });

        Button btnAddExpense = new Button("- Ghi chi tiêu");
        btnAddExpense.setStyle("-fx-background-color: #381A20; -fx-text-fill: #FB7185; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16;");
        btnAddExpense.setOnAction(e -> {
            setSelectedTypeFilter("EXPENSE");
            this.presenter.onAddTransactionClicked();
        });

        header.getChildren().addAll(title, spacer, btnAddIncome, btnAddExpense);
        return header;
    }

    private Node createKpiCardsSection() {
        HBox kpiBox = new HBox(16);

        VBox cardIncome = createKpiCard("TỔNG THU NHẬP", statTotalIncomeLabel, "-color-income");
        VBox cardExpense = createKpiCard("TỔNG CHI TIÊU", statTotalExpenseLabel, "-color-expense");
        VBox cardNet = createKpiCard("DÒNG TIỀN THUẦN", statNetCashFlowLabel, "-color-primary");

        HBox.setHgrow(cardIncome, Priority.ALWAYS);
        HBox.setHgrow(cardExpense, Priority.ALWAYS);
        HBox.setHgrow(cardNet, Priority.ALWAYS);

        kpiBox.getChildren().addAll(cardIncome, cardExpense, cardNet);
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
        box.setPadding(new Insets(10, 0, 0, 0));
        VBox.setVgrow(box, Priority.ALWAYS);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("table-view");

        TableColumn<TransactionItemViewModel, Integer> colOrder = new TableColumn<>("STT");
        colOrder.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().displayOrder()));
        colOrder.setStyle("-fx-alignment: CENTER;");
        colOrder.setPrefWidth(45);
        colOrder.setMaxWidth(55);

        TableColumn<TransactionItemViewModel, String> colDate = new TableColumn<>("Ngày Giao Dịch");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().dateFormatted()));
        colDate.setStyle("-fx-alignment: CENTER;");
        colDate.setPrefWidth(110);

        TableColumn<TransactionItemViewModel, Boolean> colType = new TableColumn<>("Phân Loại");
        colType.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().isIncome()));
        colType.setStyle("-fx-alignment: CENTER;");
        colType.setPrefWidth(100);
        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean isIncome, boolean empty) {
                super.updateItem(isIncome, empty);
                if (empty || isIncome == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(isIncome ? "Thu nhập" : "Chi tiêu");
                    badge.setPadding(new Insets(2, 8, 2, 8));
                    if (isIncome) {
                        badge.setStyle("-fx-background-color: #143520; -fx-text-fill: #4ADE80; -fx-background-radius: 4px;");
                    } else {
                        badge.setStyle("-fx-background-color: #381A20; -fx-text-fill: #FB7185; -fx-background-radius: 4px;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<TransactionItemViewModel, String> colAmount = new TableColumn<>("Số Tiền");
        colAmount.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().formattedAmount()));
        colAmount.setPrefWidth(140);
        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    TransactionItemViewModel row = getTableView().getItems().get(getIndex());
                    if (row != null && row.isIncome()) {
                        setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #4ADE80;");
                    } else {
                        setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #FB7185;");
                    }
                }
            }
        });

        TableColumn<TransactionItemViewModel, String> colWallet = new TableColumn<>("Ví Tài Khoản");
        colWallet.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().walletName()));
        colWallet.setPrefWidth(150);

        TableColumn<TransactionItemViewModel, String> colCat = new TableColumn<>("Danh Mục");
        colCat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().categoryName()));
        colCat.setPrefWidth(150);

        TableColumn<TransactionItemViewModel, String> colNote = new TableColumn<>("Ghi Chú");
        colNote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().note() != null ? data.getValue().note() : ""));
        colNote.setPrefWidth(180);

        TableColumn<TransactionItemViewModel, Void> colActions = new TableColumn<>("Thao Tác");
        colActions.setStyle("-fx-alignment: CENTER;");
        colActions.setPrefWidth(140);
        colActions.setMaxWidth(160);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("btn-secondary");
                btnDelete.getStyleClass().add("btn-danger");
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    TransactionItemViewModel rowItem = getTableView().getItems().get(getIndex());
                    btnEdit.setOnAction(e -> presenter.onEditTransactionClicked(rowItem));
                    btnDelete.setOnAction(e -> presenter.deleteTransaction(rowItem));
                    setGraphic(box);
                }
            }
        });

        tableView.getColumns().addAll(List.of(colOrder, colDate, colType, colAmount, colWallet, colCat, colNote, colActions));
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

    private void reloadFilterData() {
        try {
            WalletsSummaryResponseDTO summary = this.walletController.loadWalletsSummary();
            if (summary != null && summary.getWallets() != null) {
                this.filterBar.populateWallets(summary.getWallets());
            }

            List<CategoryHierarchyResponseDTO> hierarchy = this.categoryController.loadCategoryHierarchy();
            if (hierarchy != null) {
                List<CategoryResponseDTO> categories = new ArrayList<>();
                for (CategoryHierarchyResponseDTO g : hierarchy) {
                    if (g.getCategories() != null) {
                        categories.addAll(g.getCategories());
                    }
                }
                this.filterBar.populateCategories(categories);
            }
        } catch (Exception ignored) {
        }
    }

    // region TransactionView Implementation
    @Override
    public void renderTransactions(List<TransactionItemViewModel> txns, String summaryText, String pageInfoText) {
        runOnFx(() -> {
            tableData.setAll(txns != null ? txns : List.of());
            summaryFooterLabel.setText(summaryText != null ? summaryText : "");
            pageInfoLabel.setText(pageInfoText != null ? pageInfoText : "");
            updateKpiStats(summaryText);
        });
    }

    private void updateKpiStats(String summaryText) {
        if (summaryText != null) {
            String[] parts = summaryText.split("\\|");
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.startsWith("Tổng thu:")) {
                    statTotalIncomeLabel.setText(trimmed.replace("Tổng thu:", "").trim());
                } else if (trimmed.startsWith("Tổng chi:")) {
                    statTotalExpenseLabel.setText(trimmed.replace("Tổng chi:", "").trim());
                } else if (trimmed.startsWith("Dòng tiền thuần:")) {
                    statNetCashFlowLabel.setText(trimmed.replace("Dòng tiền thuần:", "").trim());
                }
            }
        }
    }

    @Override
    public void openRecordDialog() {
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            RecordTransactionDialog dialog = new RecordTransactionDialog(
                    owner, this.transactionController, this.walletController, this.categoryController);

            String selectedFilterType = this.filterBar.getSelectedTypeFilter();
            if ("INCOME".equalsIgnoreCase(selectedFilterType)) {
                dialog.setSelectedTransactionType("Thu nhập");
            } else if ("EXPENSE".equalsIgnoreCase(selectedFilterType)) {
                dialog.setSelectedTransactionType("Chi tiêu");
            }

            dialog.showAndWait();
            if (dialog.isSuccess()) {
                showMessage("Ghi nhận giao dịch thành công!");
                reloadFilterData();
                this.presenter.loadTransactions(this.filterBar.buildFilterRequest(this.presenter.getCurrentPage(), this.presenter.getPageSize()));
            }
        });
    }

    @Override
    public void openUpdateDialog(TransactionItemViewModel txn) {
        if (txn == null) return;
        runOnFx(() -> {
            TransactionResponseDTO matchDto = null;
            for (TransactionResponseDTO dto : this.presenter.getCurrentTransactions()) {
                if (dto.getTransactionId().equals(txn.id())) {
                    matchDto = dto;
                    break;
                }
            }
            if (matchDto == null) {
                showError("Không tìm thấy thông tin giao dịch cần sửa!");
                return;
            }

            Window owner = getScene() != null ? getScene().getWindow() : null;
            UpdateTransactionDialog dialog = new UpdateTransactionDialog(
                    owner, this.transactionController, this.walletController, this.categoryController, matchDto);

            dialog.showAndWait();
            if (dialog.isSuccess()) {
                showMessage("Cập nhật giao dịch thành công!");
                reloadFilterData();
                this.presenter.loadTransactions(this.filterBar.buildFilterRequest(this.presenter.getCurrentPage(), this.presenter.getPageSize()));
            }
        });
    }

    @Override
    public void setSelectedTypeFilter(String type) {
        this.filterBar.setSelectedTypeFilter(type);
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

    // Getters
    public TransactionPresenter getPresenter() { return presenter; }
    public TableView<TransactionItemViewModel> getTableView() { return tableView; }
    public ObservableList<TransactionItemViewModel> getTableData() { return tableData; }
    public TransactionFilterBar getFilterBar() { return filterBar; }
    public String getSummaryText() { return summaryFooterLabel.getText(); }
    public String getPageInfoText() { return pageInfoLabel.getText(); }
    public Label getStatTotalIncomeLabel() { return statTotalIncomeLabel; }
    public Label getStatTotalExpenseLabel() { return statTotalExpenseLabel; }
    public Label getStatNetCashFlowLabel() { return statNetCashFlowLabel; }

    public void setMessageConsumer(Consumer<String> consumer) { this.messageConsumer = consumer; }
    public void setWarningConsumer(Consumer<String> consumer) { this.warningConsumer = consumer; }
    public void setErrorConsumer(Consumer<String> consumer) { this.errorConsumer = consumer; }
}
