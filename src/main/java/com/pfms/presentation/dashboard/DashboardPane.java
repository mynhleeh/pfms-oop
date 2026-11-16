package com.pfms.presentation.dashboard;

import com.pfms.presentation.common.util.FxAlertUtils;
import com.pfms.presentation.dashboard.component.DashboardTransactionRowNode;
import com.pfms.presentation.dashboard.viewmodel.DashboardKpiViewModel;
import com.pfms.presentation.dashboard.viewmodel.RecentTransactionViewModel;
import com.pfms.presentation.report.ReportGUIController;
import com.pfms.presentation.transaction.TransactionGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng Điều Khiển Tổng Quan (DashboardPane - Passive View) sử dụng JavaFX.
 * Hiển thị 4 thẻ KPI tài chính chủ chốt và Bảng Giao dịch gần nhất theo chủ đề Luxury Black &amp; Gold.
 * 100% tuân thủ mô hình MVP, tách biệt giao diện thụ động khỏi logic nghiệp vụ.
 */
public class DashboardPane extends BorderPane implements DashboardView {

    // region Fields
    private final DashboardPresenter presenter;
    private final ReportGUIController reportController;

    private final Label headerTitleLabel = new Label("Bảng Điều Khiển Tổng Quan");
    private final Label cycleSubtitleLabel = new Label();

    // 4 KPI Cards Labels
    private final Label totalBalanceValueLabel = new Label("0 VNĐ");
    private final Label totalBalanceSubtextLabel = new Label("0 ví đang hoạt động");

    private final Label incomeValueLabel = new Label("0 VNĐ");
    private final Label incomeSubtextLabel = new Label("0 giao dịch thu");

    private final Label expenseValueLabel = new Label("0 VNĐ");
    private final Label expenseSubtextLabel = new Label("0.0% so với thu nhập");

    private final Label savingsRateValueLabel = new Label("0.0%");
    private final Label savingsRateSubtextLabel = new Label("Mục tiêu tích lũy > 30%");

    // Recent Transactions Table
    private final ObservableList<RecentTransactionViewModel> recentTxnsList = FXCollections.observableArrayList();
    private final TableView<RecentTransactionViewModel> recentTable = new TableView<>(recentTxnsList);

    // Recent Transactions Stream Cards Layout
    private final VBox streamItemsContainer = new VBox(8);
    private final ScrollPane streamScrollPane = new ScrollPane(streamItemsContainer);
    private HBox streamHeader;

    // Callbacks & Consumers for Testability
    private Consumer<String> messageConsumer;
    private Consumer<String> warningConsumer;
    private Consumer<String> errorConsumer;
    private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public DashboardPane(
            WalletGUIController walletController,
            TransactionGUIController transactionController,
            ReportGUIController reportController) {

        Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        Objects.requireNonNull(transactionController, "TransactionGUIController không được để null");
        this.reportController = Objects.requireNonNull(reportController, "ReportGUIController không được để null");

        this.presenter = new DashboardPresenter(this, walletController, transactionController);

        initializeUI();
        reloadDashboard();
    }

    public DashboardPane(
            WalletGUIController walletController,
            TransactionGUIController transactionController) {

        Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        Objects.requireNonNull(transactionController, "TransactionGUIController không được để null");
        this.reportController = null;

        this.presenter = new DashboardPresenter(this, walletController, transactionController);

        initializeUI();
        reloadDashboard();
    }
    // endregion

    // region UI Setup
    private void initializeUI() {
        setPadding(new Insets(20, 24, 20, 24));
        setStyle("-fx-background-color: #070707;");

        // Top Section: Header & KPI Cards
        VBox topSection = new VBox(20);
        topSection.getChildren().addAll(createHeaderPanel(), createKpiCardsPanel());
        setTop(topSection);

        // Center Section: Recent Transactions Table
        setCenter(createTableSection());
    }

    private VBox createHeaderPanel() {
        VBox headerBox = new VBox(4);
        headerTitleLabel.setStyle("-fx-text-fill: #FAFAFA; -fx-font-size: 20px; -fx-font-weight: bold;");

        String currentCycle = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        cycleSubtitleLabel.setText("Chu kỳ tài chính: Tháng " + currentCycle);
        cycleSubtitleLabel.setStyle("-fx-text-fill: #E5C07B; -fx-font-size: 13px;");

        headerBox.getChildren().addAll(headerTitleLabel, cycleSubtitleLabel);
        return headerBox;
    }

    private GridPane createKpiCardsPanel() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        VBox cardBalance = createKpiCard("TỔNG SỐ DƯ KHẢ DỤNG", totalBalanceValueLabel, totalBalanceSubtextLabel, "#FAFAFA");
        VBox cardIncome = createKpiCard("THU NHẬP THÁNG NÀY", incomeValueLabel, incomeSubtextLabel, "#4ADE80");
        VBox cardExpense = createKpiCard("CHI TIÊU THÁNG NÀY", expenseValueLabel, expenseSubtextLabel, "#FB7185");
        VBox cardSavings = createKpiCard("TỶ LỆ TIẾT KIỆM RÒNG", savingsRateValueLabel, savingsRateSubtextLabel, "#E5C07B");

        grid.add(cardBalance, 0, 0);
        grid.add(cardIncome, 1, 0);
        grid.add(cardExpense, 2, 0);
        grid.add(cardSavings, 3, 0);

        // Ensure 4 columns share equal width
        for (int i = 0; i < 4; i++) {
            javafx.scene.layout.ColumnConstraints col = new javafx.scene.layout.ColumnConstraints();
            col.setPercentWidth(25);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }

        return grid;
    }

    private VBox createKpiCard(String title, Label valueLabel, Label subtextLabel, String valueColorHex) {
        VBox card = new VBox(8);
        card.getStyleClass().add("pfms-card");
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #121212; -fx-border-color: #222222; -fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 11px; -fx-font-weight: bold;");

        valueLabel.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 20px; -fx-font-weight: bold;", valueColorHex));
        subtextLabel.setStyle("-fx-text-fill: #71717A; -fx-font-size: 11px;");

        card.getChildren().addAll(titleLabel, valueLabel, subtextLabel);
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

    @SuppressWarnings("unchecked")
    private void setupLegacyTable() {
        TableColumn<RecentTransactionViewModel, String> dateCol = new TableColumn<>("THỜI GIAN");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().dateFormatted()));
        dateCol.setPrefWidth(120);

        TableColumn<RecentTransactionViewModel, String> walletCol = new TableColumn<>("VÍ TÀI CHÍNH");
        walletCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().walletName()));
        walletCol.setPrefWidth(140);

        TableColumn<RecentTransactionViewModel, String> catCol = new TableColumn<>("DANH MỤC");
        catCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().categoryName()));
        catCol.setPrefWidth(140);

        TableColumn<RecentTransactionViewModel, String> noteCol = new TableColumn<>("GHI CHÚ");
        noteCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().note()));
        noteCol.setPrefWidth(240);

        TableColumn<RecentTransactionViewModel, String> amountCol = new TableColumn<>("SỐ TIỀN (VNĐ)");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().amountFormatted()));
        amountCol.setPrefWidth(140);

        recentTable.getColumns().setAll(dateCol, walletCol, catCol, noteCol, amountCol);
        recentTable.setPlaceholder(new Label("Chưa có giao dịch phát sinh trong chu kỳ này."));
    }

    private VBox createTableSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20, 0, 0, 0));
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox sectionHeader = new HBox(12);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        Label tableTitle = new Label("GIAO DỊCH GẦN NHẤT");
        tableTitle.setStyle("-fx-text-fill: #FAFAFA; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label subtitle = new Label("Tự động cập nhật từ sổ giao dịch");
        subtitle.setStyle("-fx-text-fill: #71717A; -fx-font-size: 11px;");
        sectionHeader.getChildren().addAll(tableTitle, subtitle);

        // Minimalist Guides Stream Header (7 Columns matching DashboardTransactionRowNode)
        this.streamHeader = new HBox(12);
        this.streamHeader.getStyleClass().add("stream-header");
        this.streamHeader.setAlignment(Pos.CENTER_LEFT);
        this.streamHeader.setStyle("-fx-padding: 8px 16px; -fx-background-color: transparent; " +
                "-fx-border-color: transparent transparent #242424 transparent; -fx-border-width: 0 0 1px 0;");

        Label hDate = createHeaderLabel("THỜI GIAN", 100, Pos.CENTER_LEFT);
        Label hAmount = createHeaderLabel("SỐ TIỀN & PHỤ PHÍ", 165, Pos.CENTER_RIGHT);
        Label hWallet = createHeaderLabel("TÀI KHOẢN VÍ", 135, Pos.CENTER_LEFT);
        Label hGroup = createHeaderLabel("NHÓM NHU CẦU", 145, Pos.CENTER_LEFT);
        Label hCat = createHeaderLabel("DANH MỤC", 140, Pos.CENTER_LEFT);

        Label hNote = new Label("GHI CHÚ / NỘI DUNG");
        hNote.setMinWidth(0);
        hNote.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hNote, Priority.ALWAYS);
        hNote.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label hType = createHeaderLabel("PHÂN LOẠI", 100, Pos.CENTER);

        this.streamHeader.getChildren().addAll(hDate, hAmount, hWallet, hGroup, hCat, hNote, hType);

        this.streamItemsContainer.setFillWidth(true);
        this.streamItemsContainer.setStyle("-fx-background-color: transparent; -fx-padding: 8px 0 8px 0;");

        this.streamScrollPane.setFitToWidth(true);
        this.streamScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(this.streamScrollPane, Priority.ALWAYS);

        // Keep legacy TableView columns configured for test compatibility
        setupLegacyTable();

        section.getChildren().addAll(sectionHeader, this.streamHeader, this.streamScrollPane);
        return section;
    }
    // endregion

    // region Actions
    public void onRefreshClicked() {
        this.presenter.onRefreshClicked();
    }

    public void reloadDashboard() {
        this.presenter.loadDashboard();
    }
    // endregion

    // region Data & Presentation Callbacks
    @Override
    public void renderKpis(DashboardKpiViewModel kpis) {
        if (kpis == null) {
            return;
        }
        runOnFx(() -> {
            totalBalanceValueLabel.setText(kpis.totalBalanceFormatted());
            totalBalanceSubtextLabel.setText(kpis.totalBalanceSubtext());

            incomeValueLabel.setText(kpis.incomeFormatted());
            incomeSubtextLabel.setText(kpis.incomeSubtext());

            expenseValueLabel.setText(kpis.expenseFormatted());
            expenseSubtextLabel.setText(kpis.expenseSubtext());

            savingsRateValueLabel.setText(kpis.savingsRateFormatted());
            savingsRateSubtextLabel.setText(kpis.savingsRateSubtext());
        });
    }

    @Override
    public void renderRecentTransactions(List<RecentTransactionViewModel> txns) {
        runOnFx(() -> {
            if (txns != null) {
                recentTxnsList.setAll(txns);
            } else {
                recentTxnsList.clear();
            }

            streamItemsContainer.getChildren().clear();
            if (txns == null || txns.isEmpty()) {
                VBox emptyCard = new VBox(8);
                emptyCard.setAlignment(Pos.CENTER);
                emptyCard.setStyle("-fx-background-color: #121212; -fx-background-radius: 8px; " +
                        "-fx-border-color: #1E1E1E; -fx-border-radius: 8px; -fx-padding: 36px 20px;");
                Label emptyText = new Label("Chưa có giao dịch phát sinh trong chu kỳ này.");
                emptyText.setStyle("-fx-text-fill: #71717A; -fx-font-size: 13px;");
                emptyCard.getChildren().add(emptyText);
                streamItemsContainer.getChildren().add(emptyCard);
            } else {
                for (RecentTransactionViewModel item : txns) {
                    streamItemsContainer.getChildren().add(new DashboardTransactionRowNode(item));
                }
            }
        });
    }

    @Override
    public void setCycleSubtitle(String text) {
        runOnFx(() -> {
            if (cycleSubtitleLabel != null) {
                cycleSubtitleLabel.setText(text);
            }
        });
    }
    // endregion

    // region Dialog & Message Helpers
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
        if (confirmConsumer != null) {
            return confirmConsumer.test(message);
        }
        return FxAlertUtils.showConfirm("Xác nhận thao tác", message);
    }

    private void runOnFx(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
    // endregion

    // region Getters & Test Hooks
    public Label getHeaderTitleLabel() {
        return headerTitleLabel;
    }

    public Label getCycleSubtitleLabel() {
        return cycleSubtitleLabel;
    }

    public Label getTotalBalanceValueLabel() {
        return totalBalanceValueLabel;
    }

    public Label getTotalBalanceSubtextLabel() {
        return totalBalanceSubtextLabel;
    }

    public Label getIncomeValueLabel() {
        return incomeValueLabel;
    }

    public Label getIncomeSubtextLabel() {
        return incomeSubtextLabel;
    }

    public Label getExpenseValueLabel() {
        return expenseValueLabel;
    }

    public Label getExpenseSubtextLabel() {
        return expenseSubtextLabel;
    }

    public Label getSavingsRateValueLabel() {
        return savingsRateValueLabel;
    }

    public Label getSavingsRateSubtextLabel() {
        return savingsRateSubtextLabel;
    }

    public TableView<RecentTransactionViewModel> getRecentTable() {
        return recentTable;
    }

    public ObservableList<RecentTransactionViewModel> getRecentTxnsList() {
        return recentTxnsList;
    }

    public DashboardPresenter getPresenter() {
        return presenter;
    }

    public ReportGUIController getReportController() {
        return reportController;
    }

    public HBox getStreamHeader() {
        return streamHeader;
    }

    public VBox getStreamItemsContainer() {
        return streamItemsContainer;
    }

    public ScrollPane getStreamScrollPane() {
        return streamScrollPane;
    }

    public void setMessageConsumer(Consumer<String> consumer) {
        this.messageConsumer = consumer;
    }

    public void setWarningConsumer(Consumer<String> consumer) {
        this.warningConsumer = consumer;
    }

    public void setErrorConsumer(Consumer<String> consumer) {
        this.errorConsumer = consumer;
    }

    public void setConfirmConsumer(Predicate<String> consumer) {
        this.confirmConsumer = consumer;
    }
    // endregion
}
