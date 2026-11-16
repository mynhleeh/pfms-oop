package com.pfms.presentation.report;

import com.pfms.presentation.common.AbstractBasePane;
import com.pfms.presentation.common.ViewUtils;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.presentation.report.component.CashFlowRowNode;
import com.pfms.presentation.report.component.SpendingStructureRowNode;
import com.pfms.presentation.report.dialog.ExportReportDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Bảng điều khiển phân hệ Báo cáo &amp; Thống kê Tài chính (Report Management Pane) sử dụng JavaFX.
 * Hiện thực hóa giao diện ReportView theo mô hình MVP (Passive View), kế thừa AbstractBasePane.
 * Trực quan hóa dữ liệu bằng chuỗi thẻ số liệu phân cấp (Stream Rows) đồng bộ 100% với bản preview.
 */
public class ReportPane extends AbstractBasePane implements ReportView {

    private final ReportPresenter presenter;
    private final ReportGUIController controller;

    private final TabPane tabPane = new TabPane();

    // Cache dữ liệu báo cáo gần nhất để cập nhật hàng thẻ KPI tổng quan
    private CashFlowReportResponseDTO lastCashFlowReport;
    private SpendingStructureReportResponseDTO lastStructureReport;

    // Hàng thẻ số liệu tổng quan KPI (3 thẻ định lượng chuẩn DTO)
    private final Label kpiLabel1 = new Label("THU NHẬP BÌNH QUÂN (12T)");
    private final Label kpiValue1 = new Label("0 VNĐ");
    private final Label kpiLabel2 = new Label("CHI TIÊU BÌNH QUÂN (12T)");
    private final Label kpiValue2 = new Label("0 VNĐ");
    private final Label kpiLabel3 = new Label("TIẾT KIỆM BÌNH QUÂN (12T)");
    private final Label kpiValue3 = new Label("0 VNĐ");

    // Tab 1: Cash Flow controls & data
    private final ComboBox<Integer> monthBox = new ComboBox<>();
    private final TextField yearField = new TextField();
    private final ComboBox<String> cfFormatBox = new ComboBox<>();
    private final Label cashFlowSummaryLabel = new Label("Thu nhập BQ: 0 VNĐ | Chi tiêu BQ: 0 VNĐ | Tiết kiệm BQ: 0 VNĐ");

    private final ObservableList<MonthlyCashFlowResponseDTO> cashFlowTableData = FXCollections.observableArrayList();
    private final TableView<MonthlyCashFlowResponseDTO> cashFlowTable = new TableView<>(cashFlowTableData);

    private final VBox cashFlowStreamBox = new VBox(6);
    private final ScrollPane cashFlowScrollPane = new ScrollPane(cashFlowStreamBox);
    private HBox cashFlowStreamHeader;

    // Tab 2: Spending Structure controls & data
    private final ComboBox<String> typeBox = new ComboBox<>();
    private final Button btnTypeExpense = new Button("EXPENSE");
    private final Button btnTypeIncome = new Button("INCOME");
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final Button chipMonth = new Button("Tháng này");
    private final Button chipQuarter = new Button("Quý này");
    private final Button chipYear = new Button("Năm nay");
    private final ComboBox<String> stFormatBox = new ComboBox<>();
    private final Label structureSummaryLabel = new Label("Tổng số tiền: 0 VNĐ");

    private final ObservableList<NeedGroupShareResponseDTO> structureTableData = FXCollections.observableArrayList();
    private final TableView<NeedGroupShareResponseDTO> structureTable = new TableView<>(structureTableData);

    private final VBox structureStreamBox = new VBox(6);
    private final ScrollPane structureScrollPane = new ScrollPane(structureStreamBox);
    private HBox structureStreamHeader;

    public ReportPane(ReportGUIController reportController) {
        super(false);
        setPadding(Insets.EMPTY);
        this.controller = Objects.requireNonNull(reportController, "ReportGUIController không được để null");
        this.presenter = new ReportPresenter(this, reportController);

        setStyle("-fx-background-color: -color-bg-base;");

        LocalDate now = LocalDate.now();
        for (int i = 1; i <= 12; i++) monthBox.getItems().add(i);
        monthBox.setValue(now.getMonthValue());
        yearField.setText(String.valueOf(now.getYear()));

        typeBox.getItems().addAll("EXPENSE", "INCOME");
        typeBox.setValue("EXPENSE");

        startDatePicker.setValue(now.withDayOfMonth(1));
        endDatePicker.setValue(now);

        cfFormatBox.getItems().addAll("Bảng văn bản (.txt)", "Định dạng JSON (.json)");
        cfFormatBox.setValue("Bảng văn bản (.txt)");

        stFormatBox.getItems().addAll("Bảng văn bản (.txt)", "Định dạng JSON (.json)");
        stFormatBox.setValue("Bảng văn bản (.txt)");

        // Top Section: Header & KPI Overview Cards
        setTop(createTopSection());

        // Center Section: TabPane
        setCenter(createTabPane());

        // Nạp dữ liệu ban đầu
        reloadReports();
    }

    private Node createTopSection() {
        VBox topContainer = new VBox(0);

        // Thanh tiêu đề phía trên cùng (.pane-header)
        HBox topBar = new HBox(12);
        topBar.getStyleClass().add("pane-header");

        VBox titleBox = new VBox(3);
        Label title = new Label("Báo Cáo & Thống Kê Tài Chính");
        title.getStyleClass().add("pane-title");

        Label subtitle = new Label("Phân tích chuỗi dòng tiền 12 tháng & Cơ cấu thu chi đa chiều");
        subtitle.getStyleClass().add("pane-subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Cặp nút xuất đồng bộ tại header
        HBox exportActions = new HBox(8);
        exportActions.setAlignment(Pos.CENTER_RIGHT);

        Button btnExportTxtTop = new Button("Xuất văn bản (.txt)");
        btnExportTxtTop.getStyleClass().add("btn-export-action");
        btnExportTxtTop.setOnAction(e -> exportCurrentActiveTab(false));

        Button btnExportJsonTop = new Button("Xuất dữ liệu (.json)");
        btnExportJsonTop.getStyleClass().add("btn-export-action");
        btnExportJsonTop.setOnAction(e -> exportCurrentActiveTab(true));

        exportActions.getChildren().addAll(btnExportTxtTop, btnExportJsonTop);
        topBar.getChildren().addAll(titleBox, spacer, exportActions);

        // Hàng thẻ số liệu định lượng KPI (.kpi-row)
        HBox kpiRow = createKpiSection();

        topContainer.getChildren().addAll(topBar, kpiRow);
        return topContainer;
    }

    private HBox createKpiSection() {
        HBox kpiRow = new HBox(14);
        kpiRow.getStyleClass().add("kpi-row");

        kpiRow.getChildren().addAll(
                createKpiCard(kpiLabel1, kpiValue1, "kpi-value-income"),
                createKpiCard(kpiLabel2, kpiValue2, "kpi-value-expense"),
                createKpiCard(kpiLabel3, kpiValue3, "kpi-value-gold")
        );
        return kpiRow;
    }

    private Node createKpiCard(Label titleLabel, Label valueLabel, String initialColorClass) {
        VBox card = new VBox(4);
        card.getStyleClass().add("kpi-card");
        titleLabel.getStyleClass().add("kpi-label");
        valueLabel.getStyleClass().addAll("kpi-value", initialColorClass);

        card.getChildren().addAll(titleLabel, valueLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private void exportCurrentActiveTab(boolean isJson) {
        if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
            this.presenter.onExportCashFlowClicked(isJson);
        } else {
            this.presenter.onExportSpendingStructureClicked(isJson);
        }
    }

    private Node createTabPane() {
        tabPane.getStyleClass().add("tab-pane");

        Tab tabCashFlow = new Tab("Báo Cáo Dòng Tiền 12 Tháng", createCashFlowTabContent());
        tabCashFlow.setClosable(false);

        Tab tabStructure = new Tab("Cơ Cấu Thu Chi", createStructureTabContent());
        tabStructure.setClosable(false);

        tabPane.getTabs().addAll(tabCashFlow, tabStructure);
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> updateKpiCards());

        return tabPane;
    }

    private Node createCashFlowTabContent() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(14, 24, 20, 24));
        VBox.setVgrow(box, Priority.ALWAYS);

        // Toolbar
        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("filter-card");

        Label lblMonth = new Label("THÁNG KẾT THÚC:");
        lblMonth.getStyleClass().add("filter-label");
        lblMonth.setMinWidth(Region.USE_PREF_SIZE);

        monthBox.setPrefWidth(100);
        monthBox.setMinWidth(80);

        Label lblYear = new Label("NĂM:");
        lblYear.getStyleClass().add("filter-label");
        lblYear.setMinWidth(Region.USE_PREF_SIZE);

        yearField.setStyle("-fx-pref-width: 80px; -fx-min-width: 70px; -fx-background-color: #0E0E0E; -fx-border-color: -color-border; " +
                "-fx-text-fill: -color-text-main; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 6px 10px;");

        Button btnView = new Button("Xem báo cáo");
        btnView.getStyleClass().add("btn-primary");
        btnView.setMinWidth(Region.USE_PREF_SIZE);
        btnView.setOnAction(e -> loadCashFlow());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblFormat = new Label("ĐỊNH DẠNG:");
        lblFormat.getStyleClass().add("filter-label");
        lblFormat.setMinWidth(Region.USE_PREF_SIZE);

        cfFormatBox.setPrefWidth(170);
        cfFormatBox.setMinWidth(150);

        Button btnExport = new Button("Xuất báo cáo");
        btnExport.getStyleClass().add("btn-export-action");
        btnExport.setMinWidth(Region.USE_PREF_SIZE);
        btnExport.setOnAction(e -> {
            boolean isJson = cfFormatBox.getSelectionModel().getSelectedIndex() == 1;
            this.presenter.onExportCashFlowClicked(isJson);
        });

        toolbar.getChildren().addAll(lblMonth, monthBox, lblYear, yearField, btnView, spacer, lblFormat, cfFormatBox, btnExport);

        // Setup legacy table for backward compatibility
        setupCashFlowLegacyTable();

        // Modern Stream section (Minimalist Guide Rows)
        this.cashFlowStreamHeader = createCashFlowStreamHeader();

        this.cashFlowStreamBox.setPadding(new Insets(6, 10, 6, 10));
        this.cashFlowStreamBox.setFillWidth(true);

        this.cashFlowScrollPane.setFitToWidth(true);
        this.cashFlowScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(this.cashFlowScrollPane, Priority.ALWAYS);

        VBox streamContainer = new VBox(0, this.cashFlowStreamHeader, this.cashFlowScrollPane);
        streamContainer.getStyleClass().addAll("report-stream-container", "stream-container");
        VBox.setVgrow(streamContainer, Priority.ALWAYS);

        box.getChildren().addAll(toolbar, streamContainer);
        return box;
    }

    private HBox createCashFlowStreamHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("report-stream-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label hMonth = new Label("THÁNG");
        hMonth.setPrefWidth(100); hMonth.setMinWidth(100); hMonth.setMaxWidth(100);
        hMonth.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label hIncome = new Label("THU NHẬP");
        hIncome.setPrefWidth(180); hIncome.setMinWidth(180); hIncome.setMaxWidth(180);
        hIncome.setAlignment(Pos.CENTER_RIGHT);
        hIncome.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 14px 0 0;");

        Label hExpense = new Label("CHI TIÊU");
        hExpense.setPrefWidth(180); hExpense.setMinWidth(180); hExpense.setMaxWidth(180);
        hExpense.setAlignment(Pos.CENTER_RIGHT);
        hExpense.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 14px 0 0;");

        Label hNet = new Label("DÒNG TIỀN RÒNG");
        hNet.setPrefWidth(190); hNet.setMinWidth(190); hNet.setMaxWidth(190);
        hNet.setAlignment(Pos.CENTER_RIGHT);
        hNet.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 14px 0 0;");

        Label hRate = new Label("TỶ LỆ TIẾT KIỆM");
        hRate.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        HBox rateWrapper = new HBox(hRate);
        rateWrapper.setAlignment(Pos.CENTER_RIGHT);
        rateWrapper.setPadding(new Insets(0, 10, 0, 0));
        HBox.setHgrow(rateWrapper, Priority.ALWAYS);

        header.getChildren().addAll(hMonth, hIncome, hExpense, hNet, rateWrapper);
        return header;
    }

    private void setupCashFlowLegacyTable() {
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

        TableColumn<MonthlyCashFlowResponseDTO, String> colRate = new TableColumn<>("Tỷ LỆ Tiết Kiệm");
        colRate.setCellValueFactory(data -> new SimpleStringProperty(String.format(Locale.US, "%.1f%%", data.getValue().getSavingsRate())));
        colRate.setStyle("-fx-alignment: CENTER;");

        cashFlowTable.getColumns().addAll(List.of(colMonth, colIncome, colExpense, colNet, colRate));
    }

    private Node createStructureTabContent() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(14, 24, 20, 24));
        VBox.setVgrow(box, Priority.ALWAYS);

        // Filter Card: Thiết kế 2 tầng cân đối, chống tràn và không chồng chéo nút
        VBox filterCard = new VBox(10);
        filterCard.getStyleClass().add("filter-card");

        // --- HÀNG 1: Phân loại, Thẻ chọn thời gian nhanh & Xuất báo cáo ---
        HBox row1 = new HBox(12);
        row1.setAlignment(Pos.CENTER_LEFT);

        Label lblType = new Label("PHÂN LOẠI:");
        lblType.getStyleClass().add("filter-label");
        lblType.setMinWidth(Region.USE_PREF_SIZE);

        HBox typeSwitcher = setupTypeSwitcher();

        Region divider = new Region();
        divider.setStyle("-fx-background-color: -color-border; -fx-pref-width: 1px; -fx-min-width: 1px; -fx-max-width: 1px; -fx-pref-height: 18px;");

        Label lblPresets = new Label("KHOẢNG THỜI GIAN:");
        lblPresets.getStyleClass().add("filter-label");
        lblPresets.setMinWidth(Region.USE_PREF_SIZE);

        setupDatePresetChips();
        HBox chipsBox = new HBox(6, chipMonth, chipQuarter, chipYear);
        chipsBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblFormat = new Label("ĐỊNH DẠNG:");
        lblFormat.getStyleClass().add("filter-label");
        lblFormat.setMinWidth(Region.USE_PREF_SIZE);

        stFormatBox.setPrefWidth(170);
        stFormatBox.setMinWidth(150);

        Button btnExport = new Button("Xuất báo cáo");
        btnExport.getStyleClass().add("btn-export-action");
        btnExport.setMinWidth(Region.USE_PREF_SIZE);
        btnExport.setOnAction(e -> {
            boolean isJson = stFormatBox.getSelectionModel().getSelectedIndex() == 1;
            this.presenter.onExportSpendingStructureClicked(isJson);
        });

        row1.getChildren().addAll(
                lblType, typeSwitcher,
                divider,
                lblPresets, chipsBox,
                spacer,
                lblFormat, stFormatBox, btnExport
        );

        // --- HÀNG 2: Chọn khoảng ngày chi tiết & Nút thực thi chính ---
        HBox row2 = new HBox(12);
        row2.setAlignment(Pos.CENTER_LEFT);

        Label lblStart = new Label("TỪ NGÀY:");
        lblStart.getStyleClass().add("filter-label");
        lblStart.setMinWidth(Region.USE_PREF_SIZE);

        startDatePicker.setPrefWidth(135);
        startDatePicker.setMinWidth(125);

        Label lblEnd = new Label("ĐẾN NGÀY:");
        lblEnd.getStyleClass().add("filter-label");
        lblEnd.setMinWidth(Region.USE_PREF_SIZE);

        endDatePicker.setPrefWidth(135);
        endDatePicker.setMinWidth(125);

        Button btnView = new Button("Xem báo cáo");
        btnView.getStyleClass().add("btn-primary");
        btnView.setMinWidth(Region.USE_PREF_SIZE);
        btnView.setOnAction(e -> loadStructure());

        row2.getChildren().addAll(lblStart, startDatePicker, lblEnd, endDatePicker, btnView);

        filterCard.getChildren().addAll(row1, row2);

        // Setup legacy table for backward compatibility
        setupStructureLegacyTable();

        // Modern Stream section (Minimalist Guide Rows)
        this.structureStreamHeader = createStructureStreamHeader();

        this.structureStreamBox.setPadding(new Insets(6, 10, 6, 10));
        this.structureStreamBox.setFillWidth(true);

        this.structureScrollPane.setFitToWidth(true);
        this.structureScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(this.structureScrollPane, Priority.ALWAYS);

        VBox streamContainer = new VBox(0, this.structureStreamHeader, this.structureScrollPane);
        streamContainer.getStyleClass().addAll("report-stream-container", "stream-container");
        VBox.setVgrow(streamContainer, Priority.ALWAYS);

        box.getChildren().addAll(filterCard, streamContainer);
        return box;
    }

    private HBox setupTypeSwitcher() {
        HBox typeSwitcher = new HBox(2);
        typeSwitcher.getStyleClass().add("type-switcher");

        btnTypeExpense.getStyleClass().setAll("type-switch-btn", "type-switch-expense", "type-switch-active");
        btnTypeExpense.setMinWidth(Region.USE_PREF_SIZE);

        btnTypeIncome.getStyleClass().setAll("type-switch-btn", "type-switch-income");
        btnTypeIncome.setMinWidth(Region.USE_PREF_SIZE);

        btnTypeExpense.setOnAction(e -> {
            if (!btnTypeExpense.getStyleClass().contains("type-switch-active")) {
                btnTypeExpense.getStyleClass().add("type-switch-active");
            }
            btnTypeIncome.getStyleClass().remove("type-switch-active");
            typeBox.setValue("EXPENSE");
            loadStructure();
        });

        btnTypeIncome.setOnAction(e -> {
            if (!btnTypeIncome.getStyleClass().contains("type-switch-active")) {
                btnTypeIncome.getStyleClass().add("type-switch-active");
            }
            btnTypeExpense.getStyleClass().remove("type-switch-active");
            typeBox.setValue("INCOME");
            loadStructure();
        });

        typeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("INCOME".equalsIgnoreCase(newVal)) {
                if (!btnTypeIncome.getStyleClass().contains("type-switch-active")) {
                    btnTypeIncome.getStyleClass().add("type-switch-active");
                }
                btnTypeExpense.getStyleClass().remove("type-switch-active");
            } else {
                if (!btnTypeExpense.getStyleClass().contains("type-switch-active")) {
                    btnTypeExpense.getStyleClass().add("type-switch-active");
                }
                btnTypeIncome.getStyleClass().remove("type-switch-active");
            }
        });

        typeSwitcher.getChildren().addAll(btnTypeExpense, btnTypeIncome);
        return typeSwitcher;
    }

    private void setupDatePresetChips() {
        chipMonth.getStyleClass().setAll("quick-chip", "quick-chip-active");
        chipMonth.setMinWidth(Region.USE_PREF_SIZE);

        chipQuarter.getStyleClass().setAll("quick-chip");
        chipQuarter.setMinWidth(Region.USE_PREF_SIZE);

        chipYear.getStyleClass().setAll("quick-chip");
        chipYear.setMinWidth(Region.USE_PREF_SIZE);

        chipMonth.setOnAction(e -> {
            setActiveChip(chipMonth);
            LocalDate now = LocalDate.now();
            startDatePicker.setValue(now.withDayOfMonth(1));
            endDatePicker.setValue(now);
            loadStructure();
        });

        chipQuarter.setOnAction(e -> {
            setActiveChip(chipQuarter);
            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int quarterStartMonth = ((currentMonth - 1) / 3) * 3 + 1;
            startDatePicker.setValue(LocalDate.of(now.getYear(), quarterStartMonth, 1));
            endDatePicker.setValue(now);
            loadStructure();
        });

        chipYear.setOnAction(e -> {
            setActiveChip(chipYear);
            LocalDate now = LocalDate.now();
            startDatePicker.setValue(LocalDate.of(now.getYear(), 1, 1));
            endDatePicker.setValue(now);
            loadStructure();
        });
    }

    private void setActiveChip(Button target) {
        chipMonth.getStyleClass().remove("quick-chip-active");
        chipQuarter.getStyleClass().remove("quick-chip-active");
        chipYear.getStyleClass().remove("quick-chip-active");

        if (target != null && !target.getStyleClass().contains("quick-chip-active")) {
            target.getStyleClass().add("quick-chip-active");
        }
    }

    private HBox createStructureStreamHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("report-stream-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label hName = new Label("NHÓM NHU CẦU");
        hName.setPrefWidth(250); hName.setMinWidth(250); hName.setMaxWidth(250);
        hName.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label hShare = new Label("TỶ TRỌNG (%)");
        hShare.setPrefWidth(140); hShare.setMinWidth(140); hShare.setMaxWidth(140);
        hShare.setAlignment(Pos.CENTER_RIGHT);
        hShare.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 14px 0 0;");

        Label hAmount = new Label("SỐ TIỀN HIỆN TẠI");
        hAmount.setPrefWidth(190); hAmount.setMinWidth(190); hAmount.setMaxWidth(190);
        hAmount.setAlignment(Pos.CENTER_RIGHT);
        hAmount.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 14px 0 0;");

        Label hPrev = new Label("KỲ TRƯỚC");
        hPrev.setPrefWidth(190); hPrev.setMinWidth(190); hPrev.setMaxWidth(190);
        hPrev.setAlignment(Pos.CENTER_RIGHT);
        hPrev.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 14px 0 0;");

        Label hDiff = new Label("CHÊNH LỆCH (+/-)");
        hDiff.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        HBox diffWrapper = new HBox(hDiff);
        diffWrapper.setAlignment(Pos.CENTER_RIGHT);
        diffWrapper.setPadding(new Insets(0, 10, 0, 0));
        HBox.setHgrow(diffWrapper, Priority.ALWAYS);

        header.getChildren().addAll(hName, hShare, hAmount, hPrev, diffWrapper);
        return header;
    }

    private void setupStructureLegacyTable() {
        structureTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        structureTable.getStyleClass().add("table-view");

        TableColumn<NeedGroupShareResponseDTO, String> colName = new TableColumn<>("Nhóm Nhu Cầu");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<NeedGroupShareResponseDTO, String> colAmount = new TableColumn<>("Số Tiền (VNĐ)");
        colAmount.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getTotalAmount())));
        colAmount.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold;");

        TableColumn<NeedGroupShareResponseDTO, String> colShare = new TableColumn<>("Tỷ Trọng (%)");
        colShare.setCellValueFactory(data -> new SimpleStringProperty(String.format(Locale.US, "%.1f%%", data.getValue().getShare())));
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

    private void updateKpiCards() {
        int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
        if (selectedIndex == 0) {
            // Tab 1: Dòng tiền 12 tháng
            kpiLabel1.setText("THU NHẬP BÌNH QUÂN (12T)");
            kpiLabel2.setText("CHI TIÊU BÌNH QUÂN (12T)");
            kpiLabel3.setText("TIẾT KIỆM BÌNH QUÂN (12T)");

            kpiValue1.getStyleClass().setAll("kpi-value", "kpi-value-income");
            kpiValue2.getStyleClass().setAll("kpi-value", "kpi-value-expense");
            kpiValue3.getStyleClass().setAll("kpi-value", "kpi-value-gold");

            if (lastCashFlowReport != null) {
                kpiValue1.setText(ViewUtils.formatCurrency(lastCashFlowReport.getAvgIncome()));
                kpiValue2.setText(ViewUtils.formatCurrency(lastCashFlowReport.getAvgExpense()));
                kpiValue3.setText(ViewUtils.formatCurrency(lastCashFlowReport.getAvgNetCashFlow()));
            } else {
                kpiValue1.setText("0 VNĐ");
                kpiValue2.setText("0 VNĐ");
                kpiValue3.setText("0 VNĐ");
            }
        } else {
            // Tab 2: Cơ cấu Thu Chi (Cân đối thu chi)
            kpiLabel1.setText("TỔNG SỐ TIỀN KỲ NÀY");
            kpiLabel2.setText("KỲ SO SÁNH LIỀN TRƯỚC");
            kpiLabel3.setText("CHÊNH LỆCH SO VỚI KỲ TRƯỚC");

            // Màu sắc theo quy chuẩn: Tổng số tiền kỳ này màu vàng, kỳ trước màu trắng, chênh lệch dương xanh, âm đỏ
            kpiValue1.getStyleClass().setAll("kpi-value", "kpi-value-gold");
            kpiValue2.getStyleClass().setAll("kpi-value", "kpi-value-white");

            if (lastStructureReport != null) {
                BigDecimal diff = lastStructureReport.getDifferenceAmount();
                if (diff != null && diff.compareTo(BigDecimal.ZERO) > 0) {
                    kpiValue3.getStyleClass().setAll("kpi-value", "kpi-value-income");
                } else if (diff != null && diff.compareTo(BigDecimal.ZERO) < 0) {
                    kpiValue3.getStyleClass().setAll("kpi-value", "kpi-value-expense");
                } else {
                    kpiValue3.getStyleClass().setAll("kpi-value", "kpi-value-white");
                }

                kpiValue1.setText(ViewUtils.formatCurrency(lastStructureReport.getTotalAmount()));
                kpiValue2.setText(lastStructureReport.getPreviousTotalAmount() != null
                        ? ViewUtils.formatCurrency(lastStructureReport.getPreviousTotalAmount())
                        : "-");

                BigDecimal pct = lastStructureReport.getPercentageChange();
                String pctStr = (pct != null) ? String.format(Locale.US, "%.1f%%", pct.doubleValue()) : "0.0%";
                String sign = (diff != null && diff.compareTo(BigDecimal.ZERO) > 0) ? "+" : "";
                kpiValue3.setText(sign + ViewUtils.formatCurrency(diff) + " (" + (diff != null && diff.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + pctStr + ")");
            } else {
                kpiValue3.getStyleClass().setAll("kpi-value", "kpi-value-white");
                kpiValue1.setText("0 VNĐ");
                kpiValue2.setText("0 VNĐ");
                kpiValue3.setText("0 VNĐ (0.0%)");
            }
        }
    }

    // region ReportView Implementation
    @Override
    public void renderCashFlowReport(CashFlowReportResponseDTO report, String summaryText) {
        runOnFx(() -> {
            this.lastCashFlowReport = report;
            cashFlowSummaryLabel.setText(summaryText != null ? summaryText : "");
            cashFlowTableData.clear();
            cashFlowStreamBox.getChildren().clear();

            if (report != null && report.getMonths() != null && !report.getMonths().isEmpty()) {
                cashFlowTableData.addAll(report.getMonths());

                for (MonthlyCashFlowResponseDTO item : report.getMonths()) {
                    cashFlowStreamBox.getChildren().add(new CashFlowRowNode(item));
                }
            } else {
                Label emptyLabel = new Label("Chưa có dữ liệu dòng tiền trong khoảng thời gian đã chọn");
                emptyLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-style: italic; -fx-padding: 24px;");
                HBox emptyBox = new HBox(emptyLabel);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.getStyleClass().add("stream-empty-card");
                cashFlowStreamBox.getChildren().add(emptyBox);
            }
            updateKpiCards();
        });
    }

    @Override
    public void renderSpendingStructureReport(SpendingStructureReportResponseDTO report, String summaryText) {
        runOnFx(() -> {
            this.lastStructureReport = report;
            structureSummaryLabel.setText(summaryText != null ? summaryText : "");
            structureTableData.clear();
            structureStreamBox.getChildren().clear();

            if (report != null && report.getGroups() != null && !report.getGroups().isEmpty()) {
                structureTableData.addAll(report.getGroups());

                String currentType = report.getTransactionType() != null ? report.getTransactionType() : "EXPENSE";
                for (NeedGroupShareResponseDTO item : report.getGroups()) {
                    structureStreamBox.getChildren().add(new SpendingStructureRowNode(item, currentType));
                }
            } else {
                Label emptyLabel = new Label("Chưa có dữ liệu cơ cấu chi tiêu trong khoảng thời gian đã chọn");
                emptyLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-style: italic; -fx-padding: 24px;");
                HBox emptyBox = new HBox(emptyLabel);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.getStyleClass().add("stream-empty-card");
                structureStreamBox.getChildren().add(emptyBox);
            }
            updateKpiCards();
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
    // endregion

    // Getters
    public ReportPresenter getPresenter() { return presenter; }
    public TabPane getTabPane() { return tabPane; }
    public ObservableList<MonthlyCashFlowResponseDTO> getCashFlowTableData() { return cashFlowTableData; }
    public ObservableList<NeedGroupShareResponseDTO> getStructureTableData() { return structureTableData; }
    public TableView<MonthlyCashFlowResponseDTO> getCashFlowTable() { return cashFlowTable; }
    public TableView<NeedGroupShareResponseDTO> getStructureTable() { return structureTable; }
    public VBox getCashFlowStreamBox() { return cashFlowStreamBox; }
    public VBox getStructureStreamBox() { return structureStreamBox; }
    public HBox getCashFlowStreamHeader() { return cashFlowStreamHeader; }
    public HBox getStructureStreamHeader() { return structureStreamHeader; }
    public ScrollPane getCashFlowScrollPane() { return cashFlowScrollPane; }
    public ScrollPane getStructureScrollPane() { return structureScrollPane; }
    public String getCashFlowSummaryText() { return cashFlowSummaryLabel.getText(); }
    public String getStructureSummaryText() { return structureSummaryLabel.getText(); }

    public Label getKpiLabel1() { return kpiLabel1; }
    public Label getKpiValue1() { return kpiValue1; }
    public Label getKpiLabel2() { return kpiLabel2; }
    public Label getKpiValue2() { return kpiValue2; }
    public Label getKpiLabel3() { return kpiLabel3; }
    public Label getKpiValue3() { return kpiValue3; }
    public Button getBtnTypeExpense() { return btnTypeExpense; }
    public Button getBtnTypeIncome() { return btnTypeIncome; }
    public Button getChipMonth() { return chipMonth; }
    public Button getChipQuarter() { return chipQuarter; }
    public Button getChipYear() { return chipYear; }
    public DatePicker getStartDatePicker() { return startDatePicker; }
    public DatePicker getEndDatePicker() { return endDatePicker; }
    public ComboBox<String> getTypeBox() { return typeBox; }
    public ComboBox<Integer> getMonthBox() { return monthBox; }
    public TextField getYearField() { return yearField; }
    public ComboBox<String> getCfFormatBox() { return cfFormatBox; }
    public ComboBox<String> getStFormatBox() { return stFormatBox; }
}
