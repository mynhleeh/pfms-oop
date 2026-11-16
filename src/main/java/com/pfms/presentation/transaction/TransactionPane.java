package com.pfms.presentation.transaction;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.util.FxAlertUtils;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.transaction.component.TransactionFilterBar;
import com.pfms.presentation.transaction.component.TransactionRowNode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Map<String, String> categoryToGroupNameMap = new ConcurrentHashMap<>();

    private final ObservableList<TransactionItemViewModel> tableData = FXCollections.observableArrayList();
    private final TableView<TransactionItemViewModel> tableView = new TableView<>(tableData);

    private final VBox streamItemsContainer = new VBox(8);
    private final ScrollPane streamScrollPane = new ScrollPane(streamItemsContainer);
    private HBox streamHeader;

    private final Label statTotalIncomeLabel = new Label("0 VNĐ");
    private final Label statTotalExpenseLabel = new Label("0 VNĐ");
    private final Label statNetCashFlowLabel = new Label("0 VNĐ");

    private final com.pfms.presentation.common.component.KpiPill kpiTotalIncome =
            new com.pfms.presentation.common.component.KpiPill("Tổng thu", "0 VNĐ", com.pfms.presentation.common.component.KpiPill.Variant.INCOME);
    private final com.pfms.presentation.common.component.KpiPill kpiTotalExpense =
            new com.pfms.presentation.common.component.KpiPill("Tổng chi", "0 VNĐ", com.pfms.presentation.common.component.KpiPill.Variant.EXPENSE);
    private final com.pfms.presentation.common.component.KpiPill kpiNetCashFlow =
            new com.pfms.presentation.common.component.KpiPill("Dòng tiền thuần", "0 VNĐ", com.pfms.presentation.common.component.KpiPill.Variant.GOLD);

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

        // Center Section: Minimalist Guide Rows Stream (Phương án 3)
        createTableSection();
        setCenter(createStreamSection());

        // Bottom Section: Pagination Bar & Summary
        setBottom(createBottomSection());

        // Nạp dữ liệu bộ lọc và giao dịch ban đầu
        reloadFilterData();
        this.presenter.loadTransactions(null);
    }

    private Node createHeaderSection() {
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Sổ Giao Dịch Thu & Chi");
        title.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitle = new Label("Ghi nhận dòng tiền thu nhập, chi phí sinh hoạt và các khoản mua sắm");
        subtitle.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox kpiBox = new HBox(8);
        kpiBox.setAlignment(Pos.CENTER_RIGHT);
        kpiBox.getChildren().addAll(kpiTotalIncome, kpiTotalExpense, kpiNetCashFlow);

        header.getChildren().addAll(titleBox, spacer, kpiBox);
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

    private Node createStreamSection() {
        VBox container = new VBox(0);
        container.setPadding(new Insets(10, 0, 0, 0));
        VBox.setVgrow(container, Priority.ALWAYS);

        this.streamHeader = new HBox(12);
        this.streamHeader.getStyleClass().add("stream-header");
        this.streamHeader.setAlignment(Pos.CENTER_LEFT);
        this.streamHeader.setStyle("-fx-padding: 8px 16px; -fx-background-color: transparent; " +
                "-fx-border-color: transparent transparent #242424 transparent; -fx-border-width: 0 0 1px 0;");

        Label hDate = new Label("NGÀY");
        hDate.setPrefWidth(100);
        hDate.setMinWidth(100);
        hDate.setMaxWidth(100);
        hDate.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label hAmount = new Label("SỐ TIỀN & PHỤ PHÍ");
        hAmount.setAlignment(Pos.CENTER_RIGHT);
        hAmount.setPrefWidth(165);
        hAmount.setMinWidth(165);
        hAmount.setMaxWidth(165);
        hAmount.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;");

        Label hWallet = new Label("VÍ TÀI KHOẢN");
        hWallet.setPrefWidth(135);
        hWallet.setMinWidth(135);
        hWallet.setMaxWidth(135);
        hWallet.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label hGroup = new Label("NHÓM NHU CẦU");
        hGroup.setPrefWidth(145);
        hGroup.setMinWidth(145);
        hGroup.setMaxWidth(145);
        hGroup.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label hCat = new Label("DANH MỤC");
        hCat.setPrefWidth(140);
        hCat.setMinWidth(140);
        hCat.setMaxWidth(140);
        hCat.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label hNote = new Label("GHI CHÚ / DIỄN GIẢI");
        hNote.setMinWidth(0);
        hNote.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hNote, Priority.ALWAYS);
        hNote.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label hActions = new Label("THAO TÁC");
        hActions.setAlignment(Pos.CENTER);
        hActions.setPrefWidth(115);
        hActions.setMinWidth(115);
        hActions.setMaxWidth(115);
        hActions.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold; -fx-alignment: CENTER;");

        this.streamHeader.getChildren().addAll(hDate, hAmount, hWallet, hGroup, hCat, hNote, hActions);

        this.streamItemsContainer.setSpacing(8);
        this.streamItemsContainer.setFillWidth(true);
        this.streamItemsContainer.setStyle("-fx-background-color: transparent; -fx-padding: 8px 0 8px 0;");

        this.streamScrollPane.setFitToWidth(true);
        this.streamScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.streamScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.streamScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(this.streamScrollPane, Priority.ALWAYS);

        container.getChildren().addAll(this.streamHeader, this.streamScrollPane);
        return container;
    }

    private Node createTableSection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10, 0, 0, 0));
        VBox.setVgrow(box, Priority.ALWAYS);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("table-view");

        TableColumn<TransactionItemViewModel, String> colDate = new TableColumn<>("Ngày Giao Dịch");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().dateFormatted()));
        colDate.setStyle("-fx-alignment: CENTER; -fx-text-fill: -color-text-main;");
        colDate.setPrefWidth(110);

        TableColumn<TransactionItemViewModel, String> colAmount = new TableColumn<>("Số Tiền");
        colAmount.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().formattedAmount()));
        colAmount.setPrefWidth(150);
        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        TransactionItemViewModel row = getTableView().getItems().get(idx);
                        String prefix = row.isIncome() ? "+" : "-";
                        String display = (item.startsWith("+") || item.startsWith("-")) ? item : prefix + item;
                        setText(display);
                        if (row.isIncome()) {
                            setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #4ADE80;");
                        } else {
                            setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #FB7185;");
                        }
                    } else {
                        setText(item);
                    }
                }
            }
        });

        TableColumn<TransactionItemViewModel, String> colWallet = new TableColumn<>("Ví Tài Khoản");
        colWallet.setCellValueFactory(data -> {
            String wName = data.getValue().walletName();
            if (wName == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(wName.replaceFirst("^(?i)ví\\s+", ""));
        });
        colWallet.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: -color-text-main;");
        colWallet.setPrefWidth(140);

        TableColumn<TransactionItemViewModel, String> colGroup = new TableColumn<>("Nhóm Nhu Cầu");
        colGroup.setCellValueFactory(data -> {
            String grp = data.getValue().groupName();
            if (grp != null && !grp.isBlank()) {
                return new SimpleStringProperty(grp);
            }
            String resolved = categoryToGroupNameMap.getOrDefault(data.getValue().categoryName(), "");
            return new SimpleStringProperty(resolved);
        });
        colGroup.setPrefWidth(145);
        colGroup.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isBlank()) {
                    setText("—");
                    setGraphic(null);
                    setStyle("-fx-text-fill: -color-text-dim; -fx-alignment: CENTER-LEFT;");
                } else {
                    setText(null);
                    Label badge = new Label(item);
                    badge.setStyle("-fx-background-color: rgba(212, 175, 55, 0.12); -fx-text-fill: -color-primary-light; " +
                            "-fx-background-radius: 4px; -fx-border-color: rgba(212, 175, 55, 0.25); -fx-border-radius: 4px; " +
                            "-fx-padding: 2px 8px; -fx-font-size: 11px; -fx-font-weight: 600;");
                    setGraphic(badge);
                    setStyle("-fx-alignment: CENTER-LEFT;");
                }
            }
        });

        TableColumn<TransactionItemViewModel, String> colCat = new TableColumn<>("Danh Mục");
        colCat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().categoryName()));
        colCat.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: -color-text-main; -fx-font-weight: 600;");
        colCat.setPrefWidth(150);

        TableColumn<TransactionItemViewModel, String> colNote = new TableColumn<>("Ghi Chú");
        colNote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().note() != null ? data.getValue().note() : ""));
        colNote.setPrefWidth(200);
        colNote.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isBlank()) {
                    setText("—");
                    setStyle("-fx-text-fill: -color-text-dim; -fx-alignment: CENTER-LEFT;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: -color-text-muted; -fx-alignment: CENTER-LEFT;");
                }
            }
        });

        TableColumn<TransactionItemViewModel, Void> colActions = new TableColumn<>("Thao Tác");
        colActions.setStyle("-fx-alignment: CENTER;");
        colActions.setPrefWidth(120);
        colActions.setMaxWidth(130);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().addAll("btn-action-sm", "btn-secondary");
                btnDelete.getStyleClass().addAll("btn-action-sm", "btn-danger");
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

        tableView.getColumns().addAll(List.of(colDate, colAmount, colWallet, colGroup, colCat, colNote, colActions));
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
                categoryToGroupNameMap.clear();
                List<CategoryResponseDTO> categories = new ArrayList<>();
                for (CategoryHierarchyResponseDTO g : hierarchy) {
                    if (g.getCategories() != null) {
                        for (CategoryResponseDTO cat : g.getCategories()) {
                            categories.add(cat);
                            if (cat.getName() != null && g.getNeedGroupName() != null) {
                                categoryToGroupNameMap.put(cat.getName(), g.getNeedGroupName());
                            }
                        }
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
            List<TransactionItemViewModel> safeList = txns != null ? txns : List.of();
            tableData.setAll(safeList);
            summaryFooterLabel.setText(summaryText != null ? summaryText : "");
            pageInfoLabel.setText(pageInfoText != null ? pageInfoText : "");
            updateKpiStats(summaryText);

            streamItemsContainer.getChildren().clear();
            if (safeList.isEmpty()) {
                VBox emptyBox = new VBox(8);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.getStyleClass().add("stream-empty-card");
                emptyBox.setStyle("-fx-background-color: #141414; -fx-background-radius: 8px; " +
                        "-fx-border-color: #1F1F1F; -fx-border-radius: 8px; -fx-padding: 36px 20px;");
                Label emptyLabel = new Label("Không có giao dịch nào phù hợp với bộ lọc hiện tại");
                emptyLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 13px; -fx-font-weight: 500;");
                emptyBox.getChildren().add(emptyLabel);
                streamItemsContainer.getChildren().add(emptyBox);
            } else {
                for (TransactionItemViewModel item : safeList) {
                    String resolvedGroup = item.groupName();
                    if (resolvedGroup == null || resolvedGroup.isBlank()) {
                        resolvedGroup = categoryToGroupNameMap.getOrDefault(item.categoryName(), "");
                    }
                    TransactionItemViewModel displayItem = new TransactionItemViewModel(
                            item.id(), item.displayOrder(), item.dateFormatted(), resolvedGroup,
                            item.categoryName(), item.walletName(), item.formattedAmount(),
                            item.isIncome(), item.note(), item.balanceAfterFormatted(),
                            item.hasFee(), item.feeFormatted()
                    );
                    TransactionRowNode row = new TransactionRowNode(
                            displayItem,
                            presenter::onEditTransactionClicked,
                            presenter::deleteTransaction
                    );
                    streamItemsContainer.getChildren().add(row);
                }
            }
        });
    }

    private void updateKpiStats(String summaryText) {
        if (summaryText != null) {
            String[] parts = summaryText.split("\\|");
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.startsWith("Tổng thu:")) {
                    String val = trimmed.replace("Tổng thu:", "").trim();
                    statTotalIncomeLabel.setText(val);
                    kpiTotalIncome.setValue(val);
                } else if (trimmed.startsWith("Tổng chi:")) {
                    String val = trimmed.replace("Tổng chi:", "").trim();
                    statTotalExpenseLabel.setText(val);
                    kpiTotalExpense.setValue(val);
                } else if (trimmed.startsWith("Dòng tiền thuần:")) {
                    String val = trimmed.replace("Dòng tiền thuần:", "").trim();
                    statNetCashFlowLabel.setText(val);
                    kpiNetCashFlow.setValue(val);
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

    public VBox getStreamItemsContainer() { return streamItemsContainer; }
    public ScrollPane getStreamScrollPane() { return streamScrollPane; }
    public HBox getStreamHeader() { return streamHeader; }

    public void setMessageConsumer(Consumer<String> consumer) { this.messageConsumer = consumer; }
    public void setWarningConsumer(Consumer<String> consumer) { this.warningConsumer = consumer; }
    public void setErrorConsumer(Consumer<String> consumer) { this.errorConsumer = consumer; }
}
