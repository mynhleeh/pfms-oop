package com.pfms.presentation.report;

import com.pfms.presentation.common.ViewUtils;
import com.pfms.presentation.common.util.FxAlertUtils;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.presentation.report.dialog.ExportReportDialog;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Bảng điều khiển phân hệ Báo cáo &amp; Thống kê Tài chính (Report Management Pane) sử dụng JavaFX.
 * Hiện thực hóa giao diện ReportView theo mô hình MVP (Passive View).
 * Trực quan hóa dữ liệu bằng JavaFX BarChart (Dòng tiền 12 tháng) và PieChart (Cơ cấu Thu Chi).
 */
public class ReportPane extends BorderPane implements ReportView {

    private final ReportPresenter presenter;
    private final ReportGUIController controller;

    private final TabPane tabPane = new TabPane();

    // Tab 1: Cash Flow controls & data
    private final ComboBox<Integer> monthBox = new ComboBox<>();
    private final TextField yearField = new TextField();
    private final ComboBox<String> cfFormatBox = new ComboBox<>();
    private final Label cashFlowSummaryLabel = new Label("Thu nhập BQ: 0 VNĐ | Chi tiêu BQ: 0 VNĐ | Tiết kiệm BQ: 0 VNĐ");

    private final ObservableList<MonthlyCashFlowResponseDTO> cashFlowTableData = FXCollections.observableArrayList();
    private final TableView<MonthlyCashFlowResponseDTO> cashFlowTable = new TableView<>(cashFlowTableData);

    private final CategoryAxis cfXAxis = new CategoryAxis();
    private final NumberAxis cfYAxis = new NumberAxis();
    private final BarChart<String, Number> cashFlowChart = new BarChart<>(cfXAxis, cfYAxis);

    // Tab 2: Spending Structure controls & data
    private final ComboBox<String> typeBox = new ComboBox<>();
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final ComboBox<String> stFormatBox = new ComboBox<>();
    private final Label structureSummaryLabel = new Label("Tổng số tiền: 0 VNĐ");

    private final ObservableList<NeedGroupShareResponseDTO> structureTableData = FXCollections.observableArrayList();
    private final TableView<NeedGroupShareResponseDTO> structureTable = new TableView<>(structureTableData);
    private final PieChart structurePieChart = new PieChart();

    private Consumer<String> messageConsumer;
    private Consumer<String> warningConsumer;
    private Consumer<String> errorConsumer;

    public ReportPane(ReportGUIController reportController) {
        this.controller = Objects.requireNonNull(reportController, "ReportGUIController không được để null");
        this.presenter = new ReportPresenter(this, reportController);

        setStyle("-fx-background-color: -color-bg-base;");
        setPadding(new Insets(20));

        LocalDate now = LocalDate.now();
        for (int i = 1; i <= 12; i++) monthBox.getItems().add(i);
        monthBox.setValue(now.getMonthValue());
        yearField.setText(String.valueOf(now.getYear()));
        yearField.setPrefWidth(80);

        typeBox.getItems().addAll("EXPENSE", "INCOME");
        typeBox.setValue("EXPENSE");

        startDatePicker.setValue(now.withDayOfMonth(1));
        endDatePicker.setValue(now);

        cfFormatBox.getItems().addAll("Bảng văn bản (.txt)", "Định dạng JSON (.json)");
        cfFormatBox.setValue("Bảng văn bản (.txt)");

        stFormatBox.getItems().addAll("Bảng văn bản (.txt)", "Định dạng JSON (.json)");
        stFormatBox.setValue("Bảng văn bản (.txt)");

        // Top Section: Header
        setTop(createHeaderSection());

        // Center Section: TabPane
        setCenter(createTabPane());

        // Nạp dữ liệu ban đầu
        reloadReports();
    }

    private Node createHeaderSection() {
        VBox header = new VBox(4);
        header.setPadding(new Insets(0, 0, 14, 0));

        Label title = new Label("Báo Cáo & Thống Kê Tài Chính");
        title.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label subtitle = new Label("Phân tích chuỗi dòng tiền 12 tháng & Cơ cấu thu chi đa chiều");
        subtitle.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private Node createTabPane() {
        tabPane.getStyleClass().add("tab-pane");

        Tab tabCashFlow = new Tab("Báo cáo Dòng tiền 12 tháng", createCashFlowTabContent());
        tabCashFlow.setClosable(false);

        Tab tabStructure = new Tab("Cơ cấu Thu Chi", createStructureTabContent());
        tabStructure.setClosable(false);

        tabPane.getTabs().addAll(tabCashFlow, tabStructure);
        return tabPane;
    }

    private Node createCashFlowTabContent() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(14, 0, 0, 0));
        VBox.setVgrow(box, Priority.ALWAYS);

        // Toolbar
        HBox toolbar = new HBox(8);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("pfms-card");
        toolbar.setPadding(new Insets(10, 14, 10, 14));

        Label lblMonth = new Label("Tháng kết thúc:");
        lblMonth.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Label lblYear = new Label("Năm:");
        lblYear.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Button btnView = new Button("Xem báo cáo");
        btnView.getStyleClass().add("btn-primary");
        btnView.setOnAction(e -> loadCashFlow());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblFormat = new Label("Định dạng:");
        lblFormat.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Button btnExport = new Button("Xuất báo cáo");
        btnExport.getStyleClass().add("btn-secondary");
        btnExport.setOnAction(e -> {
            boolean isJson = cfFormatBox.getSelectionModel().getSelectedIndex() == 1;
            this.presenter.onExportCashFlowClicked(isJson);
        });

        toolbar.getChildren().addAll(lblMonth, monthBox, lblYear, yearField, btnView, spacer, lblFormat, cfFormatBox, btnExport);

        // Chart section
        cfXAxis.setLabel("Tháng");
        cfYAxis.setLabel("Số tiền (VNĐ)");
        cashFlowChart.setTitle("Biểu Đồ So Sánh Dòng Tiền 12 Tháng");
        cashFlowChart.setPrefHeight(260);
        cashFlowChart.setAnimated(false);

        // Table section
        cashFlowTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cashFlowTable.getStyleClass().add("table-view");

        TableColumn<MonthlyCashFlowResponseDTO, String> colMonth = new TableColumn<>("Tháng");
        colMonth.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getYearMonth()));
        colMonth.setStyle("-fx-alignment: CENTER;");

        TableColumn<MonthlyCashFlowResponseDTO, String> colIncome = new TableColumn<>("Thu Nhập (VNĐ)");
        colIncome.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getTotalIncome())));
        colIncome.setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #4ADE80; -fx-font-weight: bold;");

        TableColumn<MonthlyCashFlowResponseDTO, String> colExpense = new TableColumn<>("Chi Tiêu (VNĐ)");
        colExpense.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getTotalExpense())));
        colExpense.setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #FB7185; -fx-font-weight: bold;");

        TableColumn<MonthlyCashFlowResponseDTO, String> colNet = new TableColumn<>("Dòng Tiền Ròng (VNĐ)");
        colNet.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getNetCashFlow())));
        colNet.setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: -color-primary-light; -fx-font-weight: bold;");

        TableColumn<MonthlyCashFlowResponseDTO, String> colRate = new TableColumn<>("Tỷ Lệ Tiết Kiệm");
        colRate.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f%%", data.getValue().getSavingsRate())));
        colRate.setStyle("-fx-alignment: CENTER;");

        cashFlowTable.getColumns().addAll(List.of(colMonth, colIncome, colExpense, colNet, colRate));
        VBox.setVgrow(cashFlowTable, Priority.ALWAYS);

        // Bottom summary
        HBox bottomBox = new HBox(cashFlowSummaryLabel);
        bottomBox.setPadding(new Insets(8, 0, 0, 0));
        cashFlowSummaryLabel.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 12px; -fx-font-weight: bold;");

        box.getChildren().addAll(toolbar, cashFlowChart, cashFlowTable, bottomBox);
        return box;
    }

    private Node createStructureTabContent() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(14, 0, 0, 0));
        VBox.setVgrow(box, Priority.ALWAYS);

        // Toolbar
        HBox toolbar = new HBox(8);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("pfms-card");
        toolbar.setPadding(new Insets(10, 14, 10, 14));

        Label lblType = new Label("Phân loại:");
        lblType.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Label lblStart = new Label("Từ ngày:");
        lblStart.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Label lblEnd = new Label("Đến ngày:");
        lblEnd.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Button btnView = new Button("Xem báo cáo");
        btnView.getStyleClass().add("btn-primary");
        btnView.setOnAction(e -> loadStructure());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblFormat = new Label("Định dạng:");
        lblFormat.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Button btnExport = new Button("Xuất báo cáo");
        btnExport.getStyleClass().add("btn-secondary");
        btnExport.setOnAction(e -> {
            boolean isJson = stFormatBox.getSelectionModel().getSelectedIndex() == 1;
            this.presenter.onExportSpendingStructureClicked(isJson);
        });

        toolbar.getChildren().addAll(lblType, typeBox, lblStart, startDatePicker, lblEnd, endDatePicker, btnView, spacer, lblFormat, stFormatBox, btnExport);

        // Center split: PieChart (Left) + TableView (Right)
        HBox centerBox = new HBox(14);
        VBox.setVgrow(centerBox, Priority.ALWAYS);

        structurePieChart.setTitle("Cơ Cấu Tỷ Trọng Nhóm Nhu Cầu");
        structurePieChart.setPrefWidth(380);
        structurePieChart.setAnimated(false);

        structureTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        structureTable.getStyleClass().add("table-view");
        HBox.setHgrow(structureTable, Priority.ALWAYS);

        TableColumn<NeedGroupShareResponseDTO, String> colName = new TableColumn<>("Nhóm Nhu Cầu");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<NeedGroupShareResponseDTO, String> colAmount = new TableColumn<>("Số Tiền (VNĐ)");
        colAmount.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getTotalAmount())));
        colAmount.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold;");

        TableColumn<NeedGroupShareResponseDTO, String> colShare = new TableColumn<>("Tỷ Trọng (%)");
        colShare.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f%%", data.getValue().getShare())));
        colShare.setStyle("-fx-alignment: CENTER; -fx-text-fill: -color-primary-light; -fx-font-weight: bold;");

        TableColumn<NeedGroupShareResponseDTO, String> colPrev = new TableColumn<>("Kỳ Trước");
        colPrev.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPreviousTotalAmount() != null ? ViewUtils.formatCurrency(data.getValue().getPreviousTotalAmount()) : "-"));
        colPrev.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<NeedGroupShareResponseDTO, String> colDiff = new TableColumn<>("Chênh Lệch");
        colDiff.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDifferenceAmount() != null ? ViewUtils.formatCurrency(data.getValue().getDifferenceAmount()) : "-"));
        colDiff.setStyle("-fx-alignment: CENTER-RIGHT;");

        structureTable.getColumns().addAll(List.of(colName, colAmount, colShare, colPrev, colDiff));

        centerBox.getChildren().addAll(structurePieChart, structureTable);

        // Bottom summary
        HBox bottomBox = new HBox(structureSummaryLabel);
        bottomBox.setPadding(new Insets(8, 0, 0, 0));
        structureSummaryLabel.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 12px; -fx-font-weight: bold;");

        box.getChildren().addAll(toolbar, centerBox, bottomBox);
        return box;
    }

    public void reloadReports() {
        loadCashFlow();
        loadStructure();
    }

    private void loadCashFlow() {
        try {
            int month = monthBox.getValue() != null ? monthBox.getValue() : LocalDate.now().getMonthValue();
            int year = Integer.parseInt(yearField.getText().trim());
            this.presenter.loadCashFlowReport(month, year);
        } catch (Exception ex) {
            showError("Vui lòng nhập năm hợp lệ (ví dụ: 2026)");
        }
    }

    private void loadStructure() {
        String type = typeBox.getValue() != null ? typeBox.getValue() : "EXPENSE";
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        this.presenter.loadSpendingStructureReport(start, end, type);
    }

    // region ReportView Implementation
    @Override
    public void renderCashFlowReport(CashFlowReportResponseDTO report, String summaryText) {
        runOnFx(() -> {
            cashFlowSummaryLabel.setText(summaryText != null ? summaryText : "");
            cashFlowTableData.clear();
            cashFlowChart.getData().clear();

            if (report != null && report.getMonths() != null) {
                cashFlowTableData.addAll(report.getMonths());

                XYChart.Series<String, Number> seriesIncome = new XYChart.Series<>();
                seriesIncome.setName("Thu nhập");

                XYChart.Series<String, Number> seriesExpense = new XYChart.Series<>();
                seriesExpense.setName("Chi tiêu");

                XYChart.Series<String, Number> seriesNet = new XYChart.Series<>();
                seriesNet.setName("Dòng tiền ròng");

                for (MonthlyCashFlowResponseDTO item : report.getMonths()) {
                    seriesIncome.getData().add(new XYChart.Data<>(item.getYearMonth(), item.getTotalIncome()));
                    seriesExpense.getData().add(new XYChart.Data<>(item.getYearMonth(), item.getTotalExpense()));
                    seriesNet.getData().add(new XYChart.Data<>(item.getYearMonth(), item.getNetCashFlow()));
                }

                cashFlowChart.getData().addAll(List.of(seriesIncome, seriesExpense, seriesNet));
            }
        });
    }

    @Override
    public void renderSpendingStructureReport(SpendingStructureReportResponseDTO report, String summaryText) {
        runOnFx(() -> {
            structureSummaryLabel.setText(summaryText != null ? summaryText : "");
            structureTableData.clear();
            structurePieChart.getData().clear();

            if (report != null && report.getGroups() != null) {
                structureTableData.addAll(report.getGroups());

                for (NeedGroupShareResponseDTO item : report.getGroups()) {
                    double val = item.getTotalAmount() != null ? item.getTotalAmount().doubleValue() : 0.0;
                    String label = item.getName() + " (" + String.format("%.1f%%", item.getShare()) + ")";
                    structurePieChart.getData().add(new PieChart.Data(label, val));
                }
            }
        });
    }

    @Override
    public void openExportDialog(String title, String content) {
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            ExportReportDialog dialog = new ExportReportDialog(owner, title, content);
            dialog.showAndWait();
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

    // Getters
    public ReportPresenter getPresenter() { return presenter; }
    public TabPane getTabPane() { return tabPane; }
    public ObservableList<MonthlyCashFlowResponseDTO> getCashFlowTableData() { return cashFlowTableData; }
    public ObservableList<NeedGroupShareResponseDTO> getStructureTableData() { return structureTableData; }
    public BarChart<String, Number> getCashFlowChart() { return cashFlowChart; }
    public PieChart getStructurePieChart() { return structurePieChart; }
    public String getCashFlowSummaryText() { return cashFlowSummaryLabel.getText(); }
    public String getStructureSummaryText() { return structureSummaryLabel.getText(); }

    public void setMessageConsumer(Consumer<String> consumer) { this.messageConsumer = consumer; }
    public void setWarningConsumer(Consumer<String> consumer) { this.warningConsumer = consumer; }
    public void setErrorConsumer(Consumer<String> consumer) { this.errorConsumer = consumer; }
}
