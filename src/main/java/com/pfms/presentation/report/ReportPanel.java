package com.pfms.presentation.report;

import com.pfms.presentation.common.InputValidator;
import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.presentation.report.dialog.ExportReportDialog;
import com.pfms.presentation.report.model.CashFlowTableModel;
import com.pfms.presentation.report.model.SpendingStructureTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Báo cáo &amp; Thống kê Tài chính theo chuẩn MVP (Passive View).
 * Tích hợp Báo cáo Dòng tiền 12 tháng (UC-REP-01) và Cơ cấu Thu Chi (UC-REP-02).
 */
public class ReportPanel extends JPanel implements ReportView {

    // region Fields
    private final ReportPresenter presenter;
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final CashFlowTableModel cashFlowTableModel = new CashFlowTableModel();
    private final JTable cashFlowTable = new JTable(this.cashFlowTableModel);
    private final JComboBox<Integer> monthBox;
    private final JTextField yearField;
    private final JLabel cashFlowSummaryLabel = new JLabel("Thu nhập BQ: 0 VNĐ | Chi tiêu BQ: 0 VNĐ | Tiết kiệm BQ: 0 VNĐ");
    private final SpendingStructureTableModel spendingStructureTableModel = new SpendingStructureTableModel();
    private final JTable spendingStructureTable = new JTable(this.spendingStructureTableModel);
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
    private final JTextField startDateField, endDateField;
    private final JLabel structureSummaryLabel = new JLabel("Tổng số tiền: 0 VNĐ");
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer;
    private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public ReportPanel(ReportGUIController reportController) {
        Objects.requireNonNull(reportController, "ReportGUIController không được để null");
        this.presenter = new ReportPresenter(this, reportController);
        LocalDate now = LocalDate.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.monthBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        this.monthBox.setSelectedItem(now.getMonthValue());
        this.yearField = new JTextField(String.valueOf(now.getYear()), 6);
        this.startDateField = new JTextField(now.withDayOfMonth(1).format(df), 8);
        this.endDateField = new JTextField(now.format(df), 8);
        initUI(); reloadReports();
    }
    // endregion

    // region UI
    private void initUI() {
        setLayout(new BorderLayout(10, 10)); setBackground(ViewConstants.COLOR_CONTENT_BG);
        JPanel north = new JPanel(); north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(ViewConstants.COLOR_CONTENT_BG); north.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));
        JLabel title = new JLabel("Báo cáo & Thống kê Tài chính"); title.setFont(ViewConstants.FONT_PANEL_HEADER);
        title.setForeground(ViewConstants.COLOR_TEXT_MAIN); north.add(title); add(north, BorderLayout.NORTH);

        setupTable(this.cashFlowTable, new int[]{100, 160, 160, 160, 140}, new int[]{SwingConstants.CENTER, SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT});
        setupTable(this.spendingStructureTable, new int[]{180, 140, 100, 140, 140, 110}, new int[]{SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.CENTER});

        JButton cfView = createBtn("Xem báo cáo", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE, e -> loadCashFlow());
        JButton cfExp = createBtn("Xuất báo cáo", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN, e -> this.presenter.onExportCashFlowClicked());
        JPanel cfTb = createTb(new JLabel("Tháng kết thúc:"), this.monthBox, new JLabel("Năm:"), this.yearField, cfView, cfExp);
        this.tabbedPane.addTab("Báo cáo Dòng tiền 12 tháng", createTab(cfTb, this.cashFlowTable, this.cashFlowSummaryLabel));

        JButton stView = createBtn("Xem báo cáo", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE, e -> loadStructure());
        JButton stExp = createBtn("Xuất báo cáo", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN, e -> this.presenter.onExportSpendingStructureClicked());
        JPanel stTb = createTb(new JLabel("Loại:"), this.typeBox, new JLabel("Từ ngày:"), this.startDateField, new JLabel("Đến ngày:"), this.endDateField, stView, stExp);
        this.tabbedPane.addTab("Phân tích Cơ cấu Thu Chi", createTab(stTb, this.spendingStructureTable, this.structureSummaryLabel));
        add(this.tabbedPane, BorderLayout.CENTER);
    }

    private void setupTable(JTable t, int[] w, int[] a) {
        com.pfms.presentation.common.ViewUtils.styleTable(t);
        for (int i = 0; i < w.length && i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
            Color fg = (a[i] == SwingConstants.RIGHT) ? ViewConstants.COLOR_PRIMARY_LIGHT : ViewConstants.COLOR_TEXT_MAIN;
            t.getColumnModel().getColumn(i).setCellRenderer(com.pfms.presentation.common.ViewUtils.createCellRenderer(a[i], fg));
        }
    }

    private JButton createBtn(String txt, Color bg, Color fg, java.awt.event.ActionListener l) {
        JButton b = new JButton(txt); b.setFont(ViewConstants.FONT_BODY); b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.addActionListener(l); return b;
    }

    private JPanel createTb(Component... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); p.setBackground(ViewConstants.COLOR_CONTENT_BG);
        for (Component c : comps) p.add(c); return p;
    }

    private JPanel createTab(JPanel tb, JTable tbl, JLabel sum) {
        JPanel p = new JPanel(new BorderLayout(10, 10)); p.setBackground(ViewConstants.COLOR_CONTENT_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); p.add(tb, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(tbl);
        sp.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        sp.setBackground(ViewConstants.COLOR_CARD_BG);
        sp.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)); p.add(sp, BorderLayout.CENTER);
        JPanel s = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8)); s.setBackground(ViewConstants.COLOR_STATUS_BG);
        s.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER)); s.add(sum);
        p.add(s, BorderLayout.SOUTH); return p;
    }
    // endregion

    // region Actions
    private void loadCashFlow() {
        this.presenter.loadCashFlowReport((Integer) this.monthBox.getSelectedItem(), parseYear(this.yearField.getText()));
    }

    private void loadStructure() {
        LocalDate s = InputValidator.parseOptionalDate(this.startDateField.getText(), "Ngày bắt đầu");
        LocalDate e = InputValidator.parseOptionalDate(this.endDateField.getText(), "Ngày kết thúc");
        this.presenter.loadSpendingStructureReport(s, e, (String) this.typeBox.getSelectedItem());
    }
    // endregion

    // region Data
    @Override public void renderCashFlowReport(CashFlowReportResponseDTO r, String sum) {
        if (r != null) this.cashFlowTableModel.setMonths(r.getMonths()); else this.cashFlowTableModel.clear();
        this.cashFlowSummaryLabel.setText(sum);
    }

    @Override public void renderSpendingStructureReport(SpendingStructureReportResponseDTO r, String sum) {
        if (r != null) this.spendingStructureTableModel.setGroups(r.getGroups()); else this.spendingStructureTableModel.clear();
        this.structureSummaryLabel.setText(sum);
    }

    public void reloadReports() { loadCashFlow(); loadStructure(); }
    // endregion

    // region Getters
    public JTabbedPane getTabbedPane() { return this.tabbedPane; }
    public CashFlowTableModel getCashFlowTableModel() { return this.cashFlowTableModel; }
    public SpendingStructureTableModel getSpendingStructureTableModel() { return this.spendingStructureTableModel; }
    public String getCashFlowSummaryText() { return this.cashFlowSummaryLabel.getText(); }
    public String getStructureSummaryText() { return this.structureSummaryLabel.getText(); }
    public ReportPresenter getPresenter() { return this.presenter; }
    public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = c; }
    public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = c; }
    public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = c; }
    public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = c; }
    // endregion

    // region Helpers
    @Override public void showMessage(String m) { if (messageConsumer != null) messageConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    @Override public void showWarning(String m) { if (warningConsumer != null) warningConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE); }
    @Override public void showError(String m) { if (errorConsumer != null) errorConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    @Override public boolean confirmAction(String m) { return confirmConsumer != null ? confirmConsumer.test(m) : JOptionPane.showConfirmDialog(this, m, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; }
    @Override public void openExportDialog(String title, String content) {
        JFrame p = (JFrame) SwingUtilities.getWindowAncestor(this);
        new ExportReportDialog(p, title, content).setVisible(true);
    }
    private int parseYear(String txt) { try { return Integer.parseInt(txt.trim()); } catch (Exception e) { return LocalDate.now().getYear(); } }
    // endregion
}
