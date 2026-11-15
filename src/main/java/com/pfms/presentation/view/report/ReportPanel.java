package com.pfms.presentation.view.report;

import com.pfms.presentation.controller.ReportGUIController;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.presentation.util.InputValidator;
import com.pfms.presentation.view.common.ViewConstants;
import com.pfms.presentation.view.report.dialog.ExportReportDialog;
import com.pfms.presentation.view.report.model.CashFlowTableModel;
import com.pfms.presentation.view.report.model.SpendingStructureTableModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Bảng điều khiển trung tâm phân hệ Báo cáo & Thống kê Tài chính (CARD_REPORTS).
 * Tích hợp 2 tab: Báo cáo Dòng tiền 12 tháng (UC-REP-01) và Cơ cấu Thu Chi (UC-REP-02),
 * hỗ trợ xuất báo cáo văn bản bảng theo GoF Strategy Pattern (UC-REP-03).
 */
public class ReportPanel extends JPanel {

    private final ReportGUIController reportController;

    private final JTabbedPane tabbedPane;

    // Tab 1: Dòng tiền
    private final CashFlowTableModel cashFlowTableModel;
    private final JTable cashFlowTable;
    private final JComboBox<Integer> monthBox;
    private final JTextField yearField;
    private final JLabel cashFlowSummaryLabel;

    // Tab 2: Cơ cấu
    private final SpendingStructureTableModel spendingStructureTableModel;
    private final JTable spendingStructureTable;
    private final JComboBox<String> typeBox;
    private final JTextField startDateField;
    private final JTextField endDateField;
    private final JLabel structureSummaryLabel;

    private final DecimalFormat currencyFormatter;
    private final DateTimeFormatter dateFormatter;

    private CashFlowReportResponseDTO currentCashFlowReport;
    private SpendingStructureReportResponseDTO currentStructureReport;

    private Consumer<String> messageConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    };
    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    };

    public ReportPanel(ReportGUIController reportController) {
        this.reportController = Objects.requireNonNull(reportController, "ReportGUIController không được để null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        setLayout(new BorderLayout(10, 10));
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Khởi tạo các bảng
        this.cashFlowTableModel = new CashFlowTableModel();
        this.cashFlowTable = new JTable(this.cashFlowTableModel);
        setupCashFlowTable();

        this.spendingStructureTableModel = new SpendingStructureTableModel();
        this.spendingStructureTable = new JTable(this.spendingStructureTableModel);
        setupStructureTable();

        // Khởi tạo các trường lọc Tab 1
        LocalDate now = LocalDate.now();
        Integer[] months = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        this.monthBox = new JComboBox<>(months);
        this.monthBox.setSelectedItem(now.getMonthValue());
        this.monthBox.setFont(ViewConstants.FONT_BODY);

        this.yearField = new JTextField(String.valueOf(now.getYear()), 6);
        this.yearField.setFont(ViewConstants.FONT_BODY);

        this.cashFlowSummaryLabel = new JLabel("Thu nhập BQ: 0 VNĐ | Chi tiêu BQ: 0 VNĐ | Tiết kiệm BQ: 0 VNĐ");
        this.cashFlowSummaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.cashFlowSummaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);

        // Khởi tạo các trường lọc Tab 2
        this.typeBox = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
        this.typeBox.setFont(ViewConstants.FONT_BODY);

        this.startDateField = new JTextField(now.withDayOfMonth(1).format(dateFormatter), 8);
        this.startDateField.setFont(ViewConstants.FONT_BODY);

        this.endDateField = new JTextField(now.format(dateFormatter), 8);
        this.endDateField.setFont(ViewConstants.FONT_BODY);

        this.structureSummaryLabel = new JLabel("Tổng số tiền: 0 VNĐ");
        this.structureSummaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.structureSummaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);

        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setFont(ViewConstants.FONT_BODY);

        initViews();
        reloadReports();
    }

    private void initViews() {
        // Vùng trên: Header
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));

        JLabel titleLabel = new JLabel("Báo cáo & Thống kê Tài chính");
        titleLabel.setFont(ViewConstants.FONT_PANEL_HEADER);
        titleLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        northPanel.add(titleLabel);
        add(northPanel, BorderLayout.NORTH);

        // Tab 1: Dòng tiền 12 tháng
        JPanel cashFlowPanel = createCashFlowTab();
        this.tabbedPane.addTab("Báo cáo Dòng tiền 12 tháng", cashFlowPanel);

        // Tab 2: Cơ cấu Thu Chi
        JPanel structurePanel = createSpendingStructureTab();
        this.tabbedPane.addTab("Phân tích Cơ cấu Thu Chi", structurePanel);

        add(this.tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCashFlowTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(ViewConstants.COLOR_CONTENT_BG);

        toolbar.add(new JLabel("Tháng kết thúc:"));
        toolbar.add(this.monthBox);

        toolbar.add(new JLabel("Năm:"));
        toolbar.add(this.yearField);

        JButton viewButton = createToolButton("Xem báo cáo", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE);
        viewButton.addActionListener(e -> loadCashFlowReport());
        toolbar.add(viewButton);

        JButton exportButton = createToolButton("Xuất báo cáo", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        exportButton.addActionListener(e -> exportCashFlow());
        toolbar.add(exportButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Center Table
        JScrollPane scrollPane = new JScrollPane(this.cashFlowTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        // South Summary
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        southPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        southPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));
        southPanel.add(this.cashFlowSummaryLabel);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSpendingStructureTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(ViewConstants.COLOR_CONTENT_BG);

        toolbar.add(new JLabel("Loại:"));
        toolbar.add(this.typeBox);

        toolbar.add(new JLabel("Từ ngày:"));
        toolbar.add(this.startDateField);

        toolbar.add(new JLabel("Đến ngày:"));
        toolbar.add(this.endDateField);

        JButton viewButton = createToolButton("Xem báo cáo", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE);
        viewButton.addActionListener(e -> loadSpendingStructureReport());
        toolbar.add(viewButton);

        JButton exportButton = createToolButton("Xuất báo cáo", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        exportButton.addActionListener(e -> exportSpendingStructure());
        toolbar.add(exportButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Center Table
        JScrollPane scrollPane = new JScrollPane(this.spendingStructureTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        // South Summary
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        southPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        southPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));
        southPanel.add(this.structureSummaryLabel);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createToolButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(ViewConstants.FONT_BODY);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        return btn;
    }

    private void setupCashFlowTable() {
        this.cashFlowTable.setFont(ViewConstants.FONT_BODY);
        this.cashFlowTable.setRowHeight(28);
        this.cashFlowTable.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.cashFlowTable.getTableHeader().setBackground(new Color(245, 245, 245));
        this.cashFlowTable.setGridColor(ViewConstants.COLOR_BORDER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        if (this.cashFlowTable.getColumnModel().getColumnCount() >= 5) {
            this.cashFlowTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            this.cashFlowTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

            this.cashFlowTable.getColumnModel().getColumn(1).setPreferredWidth(160);
            this.cashFlowTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

            this.cashFlowTable.getColumnModel().getColumn(2).setPreferredWidth(160);
            this.cashFlowTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

            this.cashFlowTable.getColumnModel().getColumn(3).setPreferredWidth(160);
            this.cashFlowTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

            this.cashFlowTable.getColumnModel().getColumn(4).setPreferredWidth(140);
            this.cashFlowTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        }
    }

    private void setupStructureTable() {
        this.spendingStructureTable.setFont(ViewConstants.FONT_BODY);
        this.spendingStructureTable.setRowHeight(28);
        this.spendingStructureTable.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.spendingStructureTable.getTableHeader().setBackground(new Color(245, 245, 245));
        this.spendingStructureTable.setGridColor(ViewConstants.COLOR_BORDER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        if (this.spendingStructureTable.getColumnModel().getColumnCount() >= 6) {
            this.spendingStructureTable.getColumnModel().getColumn(0).setPreferredWidth(180);

            this.spendingStructureTable.getColumnModel().getColumn(1).setPreferredWidth(140);
            this.spendingStructureTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

            this.spendingStructureTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            this.spendingStructureTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

            this.spendingStructureTable.getColumnModel().getColumn(3).setPreferredWidth(140);
            this.spendingStructureTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

            this.spendingStructureTable.getColumnModel().getColumn(4).setPreferredWidth(140);
            this.spendingStructureTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

            this.spendingStructureTable.getColumnModel().getColumn(5).setPreferredWidth(110);
            this.spendingStructureTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        }
    }

    public void reloadReports() {
        loadCashFlowReport();
        loadSpendingStructureReport();
    }

    private void loadCashFlowReport() {
        try {
            int endMonth = (Integer) this.monthBox.getSelectedItem();
            int endYear = Integer.parseInt(this.yearField.getText().trim());

            this.currentCashFlowReport = this.reportController.generateCashFlowReport(endMonth, endYear);
            if (this.currentCashFlowReport != null) {
                this.cashFlowTableModel.setMonths(this.currentCashFlowReport.getMonths());

                String avgIncome = formatCurrency(this.currentCashFlowReport.getAvgIncome());
                String avgExpense = formatCurrency(this.currentCashFlowReport.getAvgExpense());
                String avgNet = formatCurrency(this.currentCashFlowReport.getAvgNetCashFlow());

                this.cashFlowSummaryLabel.setText(String.format(
                        "Thu nhập BQ: %s VNĐ | Chi tiêu BQ: %s VNĐ | Tiết kiệm BQ: %s VNĐ",
                        avgIncome, avgExpense, avgNet));
            } else {
                this.cashFlowTableModel.clear();
                this.cashFlowSummaryLabel.setText("Thu nhập BQ: 0 VNĐ | Chi tiêu BQ: 0 VNĐ | Tiết kiệm BQ: 0 VNĐ");
            }
        } catch (Exception ex) {
            this.errorConsumer.accept("Không thể tạo báo cáo dòng tiền: " + ex.getMessage());
        }
    }

    private void loadSpendingStructureReport() {
        try {
            String type = (String) this.typeBox.getSelectedItem();
            LocalDate start = InputValidator.parseOptionalDate(this.startDateField.getText(), "Ngày bắt đầu");
            LocalDate end = InputValidator.parseOptionalDate(this.endDateField.getText(), "Ngày kết thúc");

            if (start == null) start = LocalDate.now().withDayOfMonth(1);
            if (end == null) end = LocalDate.now();

            this.currentStructureReport = this.reportController.generateSpendingStructureReport(start, end, type);
            if (this.currentStructureReport != null) {
                this.spendingStructureTableModel.setGroups(this.currentStructureReport.getGroups());

                String total = formatCurrency(this.currentStructureReport.getTotalAmount());
                String prev = formatCurrency(this.currentStructureReport.getPreviousTotalAmount());
                String diff = formatCurrency(this.currentStructureReport.getDifferenceAmount());
                String pct = formatPercentage(this.currentStructureReport.getPercentageChange());

                this.structureSummaryLabel.setText(String.format(
                        "Tổng số tiền: %s VNĐ | Kỳ trước: %s VNĐ | Chênh lệch: %s VNĐ (%s%%)",
                        total, prev, diff, pct));
            } else {
                this.spendingStructureTableModel.clear();
                this.structureSummaryLabel.setText("Tổng số tiền: 0 VNĐ");
            }
        } catch (Exception ex) {
            this.errorConsumer.accept("Không thể tạo báo cáo cơ cấu: " + ex.getMessage());
        }
    }

    private void exportCashFlow() {
        if (this.currentCashFlowReport == null) {
            this.errorConsumer.accept("Vui lòng nạp báo cáo dòng tiền trước khi xuất!");
            return;
        }
        try {
            String content = this.reportController.exportCashFlow(this.currentCashFlowReport);
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            ExportReportDialog dialog = new ExportReportDialog(parent, "Xuất Báo cáo Dòng tiền", content);
            dialog.setVisible(true);
        } catch (Exception ex) {
            this.errorConsumer.accept("Xuất báo cáo thất bại: " + ex.getMessage());
        }
    }

    private void exportSpendingStructure() {
        if (this.currentStructureReport == null) {
            this.errorConsumer.accept("Vui lòng nạp báo cáo cơ cấu trước khi xuất!");
            return;
        }
        try {
            String content = this.reportController.exportSpendingStructure(this.currentStructureReport);
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            ExportReportDialog dialog = new ExportReportDialog(parent, "Xuất Báo cáo Cơ cấu Thu Chi", content);
            dialog.setVisible(true);
        } catch (Exception ex) {
            this.errorConsumer.accept("Xuất báo cáo thất bại: " + ex.getMessage());
        }
    }

    private String formatCurrency(BigDecimal amount) {
        BigDecimal safe = (amount != null) ? amount : BigDecimal.ZERO;
        return this.currencyFormatter.format(safe);
    }

    private String formatPercentage(BigDecimal percentage) {
        BigDecimal safe = (percentage != null) ? percentage : BigDecimal.ZERO;
        return String.format("%.1f", safe);
    }

    // Getters
    public JTabbedPane getTabbedPane() {
        return this.tabbedPane;
    }

    public CashFlowTableModel getCashFlowTableModel() {
        return this.cashFlowTableModel;
    }

    public SpendingStructureTableModel getSpendingStructureTableModel() {
        return this.spendingStructureTableModel;
    }

    public String getCashFlowSummaryText() {
        return this.cashFlowSummaryLabel.getText();
    }

    public String getStructureSummaryText() {
        return this.structureSummaryLabel.getText();
    }

    public void setMessageConsumer(Consumer<String> consumer) {
        this.messageConsumer = Objects.requireNonNull(consumer);
    }

    public void setErrorConsumer(Consumer<String> consumer) {
        this.errorConsumer = Objects.requireNonNull(consumer);
    }
}
