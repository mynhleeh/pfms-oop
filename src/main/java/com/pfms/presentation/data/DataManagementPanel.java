package com.pfms.presentation.data;

import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.data.model.ReconciliationIssueTableModel;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Quản trị &amp; An toàn Dữ liệu theo mô hình MVP
 * (Passive View).
 * Tích hợp Xuất sao lưu JSON (UC-DAT-01), Phục hồi dữ liệu (UC-DAT-02), Đối
 * soát hệ thống (UC-DAT-03).
 */
public class DataManagementPanel extends JPanel implements DataManagementView {

    // region Fields
    private final DataManagementPresenter presenter;
    private final String storagePath;
    private final JTextField destinationField = new JTextField("backups", 16);
    private final JComboBox<String> scopeBox = new JComboBox<>(new String[] { "FULL", "TRANSACTIONS" });
    private final JLabel exportResultLabel = new JLabel("Chưa thực hiện sao lưu");
    private final JTextField restoreFileField = new JTextField(16);
    private final JLabel restoreResultLabel = new JLabel("Chưa thực hiện phục hồi");
    private final ReconciliationIssueTableModel issueTableModel = new ReconciliationIssueTableModel();
    private final JTable issueTable = new JTable(this.issueTableModel);
    private final JLabel balanceStatusLabel = createStatusBadge("Số dư ví: Chưa kiểm tra", Color.GRAY);
    private final JLabel transferStatusLabel = createStatusBadge("Chuyển tiền: Chưa kiểm tra", Color.GRAY);
    private final JLabel categoryStatusLabel = createStatusBadge("Danh mục: Chưa kiểm tra", Color.GRAY);
    private final JLabel feeStatusLabel = createStatusBadge("Phụ phí: Chưa kiểm tra", Color.GRAY);
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer;
    private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public DataManagementPanel(DataManagementGUIController dataController, String storagePath) {
        Objects.requireNonNull(dataController, "DataManagementGUIController không được để null");
        this.storagePath = (storagePath != null && !storagePath.isBlank()) ? storagePath : "data/pfms_data.json";
        this.presenter = new DataManagementPresenter(this, dataController);
        initUI();
    }

    public DataManagementPanel(DataManagementGUIController dataController) {
        this(dataController, "data/pfms_data.json");
    }
    // endregion

    // region UI
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ViewConstants.COLOR_CONTENT_BG);
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(ViewConstants.COLOR_CONTENT_BG);
        north.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));
        JLabel title = new JLabel("Quản trị & An toàn Dữ liệu (Backup, Restore & Integrity Audit)");
        title.setFont(ViewConstants.FONT_PANEL_HEADER);
        title.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        north.add(title);
        add(north, BorderLayout.NORTH);

        setupIssueTable();
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createBackupRestorePanel(),
                createReconciliationPanel());
        split.setDividerLocation(460);
        split.setResizeWeight(0.45);
        split.setBorder(null);
        split.setBackground(ViewConstants.COLOR_CONTENT_BG);
        add(split, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        south.setBackground(ViewConstants.COLOR_STATUS_BG);
        south.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        JLabel storageInfo = new JLabel("Tệp cơ sở dữ liệu hệ thống: " + this.storagePath);
        storageInfo.setFont(ViewConstants.FONT_STATUS);
        storageInfo.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        south.add(storageInfo);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel createBackupRestorePanel() {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(ViewConstants.COLOR_CONTENT_BG);
        c.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 12));
        this.exportResultLabel.setFont(ViewConstants.FONT_BODY);
        this.exportResultLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        this.restoreResultLabel.setFont(ViewConstants.FONT_BODY);
        this.restoreResultLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        JButton expBtn = createBtn("Xuất Sao lưu", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE,
                e -> triggerExportBackup(this.destinationField.getText(), (String) this.scopeBox.getSelectedItem()));
        c.add(box("Xuất Bản Sao lưu JSON (UC-DAT-01)",
                row("Thư mục đích:", this.destinationField, createBtn("Chọn...", e -> chooseFolder())),
                row("Phạm vi sao lưu:", this.scopeBox, expBtn), row("", this.exportResultLabel)));
        c.add(Box.createVerticalStrut(15));
        JButton rstBtn = createBtn("Phục hồi Dữ liệu", new Color(220, 53, 69), Color.WHITE,
                e -> triggerRestoreBackup(this.restoreFileField.getText()));
        c.add(box("Phục hồi Dữ liệu từ Sao lưu (UC-DAT-02)",
                row("Tệp sao lưu:", this.restoreFileField, createBtn("Chọn...", e -> chooseFile()), rstBtn),
                row("", this.restoreResultLabel)));
        c.add(Box.createVerticalGlue());
        return c;
    }

    private JPanel createReconciliationPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(ViewConstants.COLOR_CONTENT_BG);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 12, 12, 24),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                                "Đối soát Tính Toàn vẹn Hệ thống (UC-DAT-03)", 0, 0, ViewConstants.FONT_NAV_ACTIVE,
                                ViewConstants.COLOR_TEXT_MAIN),
                        BorderFactory.createEmptyBorder(12, 16, 16, 16))));
        JPanel top = new JPanel(new BorderLayout(10, 8));
        top.setBackground(ViewConstants.COLOR_CONTENT_BG);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        btnRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        btnRow.add(createBtn("Đối soát dữ liệu ngay", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE,
                e -> triggerReconcile()));
        top.add(btnRow, BorderLayout.NORTH);
        top.add(row("", this.balanceStatusLabel, this.transferStatusLabel, this.categoryStatusLabel,
                this.feeStatusLabel), BorderLayout.SOUTH);
        p.add(top, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(this.issueTable);
        sp.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        sp.setBackground(ViewConstants.COLOR_CARD_BG);
        sp.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void setupIssueTable() {
        com.pfms.presentation.common.ViewUtils.styleTable(this.issueTable);
        DefaultTableCellRenderer cr = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.CENTER, ViewConstants.COLOR_TEXT_MUTED);
        DefaultTableCellRenderer lr = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.LEFT, ViewConstants.COLOR_TEXT_MAIN);
        int[] w = { 80, 110, 140, 220, 160 };
        for (int i = 0; i < w.length && i < this.issueTable.getColumnModel().getColumnCount(); i++) {
            this.issueTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
            if (i < 2) {
                this.issueTable.getColumnModel().getColumn(i).setCellRenderer(cr);
            } else {
                this.issueTable.getColumnModel().getColumn(i).setCellRenderer(lr);
            }
        }
    }
    // endregion

    // region Actions
    public void triggerExportBackup(String d, String s) {
        this.presenter.exportBackup(d, s);
    }

    public void triggerRestoreBackup(String f) {
        this.presenter.restoreBackup(f);
    }

    public void triggerReconcile() {
        this.presenter.reconcileData();
    }

    private void chooseFolder() {
        JFileChooser c = new JFileChooser();
        c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (c.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && c.getSelectedFile() != null)
            this.destinationField.setText(c.getSelectedFile().getAbsolutePath());
    }

    private void chooseFile() {
        JFileChooser c = new JFileChooser();
        c.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (c.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && c.getSelectedFile() != null)
            this.restoreFileField.setText(c.getSelectedFile().getAbsolutePath());
    }
    // endregion

    // region Data
    @Override
    public void displayExportResult(ExportBackupResponseDTO res, String text) {
        this.exportResultLabel.setText(text);
        this.exportResultLabel.setForeground(res != null ? ViewConstants.COLOR_INCOME : ViewConstants.COLOR_EXPENSE);
    }

    @Override
    public void displayRestoreResult(RestoreBackupResponseDTO res, String text) {
        this.restoreResultLabel.setText(text);
        this.restoreResultLabel.setForeground(
                res != null && res.isSuccess() ? ViewConstants.COLOR_INCOME : ViewConstants.COLOR_EXPENSE);
    }

    @Override
    public void displayReconciliationReport(ReconciliationReportResponseDTO rep) {
        if (rep == null)
            return;
        updateBadge(this.balanceStatusLabel, "Số dư ví", rep.isWalletBalanceConsistent());
        updateBadge(this.transferStatusLabel, "Chuyển tiền", rep.isTransferConsistent());
        updateBadge(this.categoryStatusLabel, "Danh mục", rep.isCategoryHierarchyConsistent());
        updateBadge(this.feeStatusLabel, "Phụ phí", rep.isFeeConsistent());
        this.issueTableModel.setIssues(rep.getIssues());
    }
    // endregion

    // region Getters
    public ReconciliationIssueTableModel getIssueTableModel() {
        return this.issueTableModel;
    }

    public JTable getIssueTable() {
        return this.issueTable;
    }

    public String getExportResultText() {
        return this.exportResultLabel.getText();
    }

    public String getRestoreResultText() {
        return this.restoreResultLabel.getText();
    }

    public DataManagementPresenter getPresenter() {
        return this.presenter;
    }

    public void setOnDataRestoredListener(Runnable l) {
        this.presenter.setOnDataRestoredListener(l);
    }

    public void setMessageConsumer(Consumer<String> c) {
        this.messageConsumer = c;
    }

    public void setWarningConsumer(Consumer<String> c) {
        this.warningConsumer = c;
    }

    public void setErrorConsumer(Consumer<String> c) {
        this.errorConsumer = c;
    }

    public void setConfirmConsumer(Predicate<String> c) {
        this.confirmConsumer = c;
    }
    // endregion

    // region Helpers
    @Override
    public void showMessage(String m) {
        if (messageConsumer != null)
            messageConsumer.accept(m);
        else
            JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showWarning(String m) {
        if (warningConsumer != null)
            warningConsumer.accept(m);
        else
            JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void showError(String m) {
        if (errorConsumer != null)
            errorConsumer.accept(m);
        else
            JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public boolean confirmAction(String m) {
        return confirmConsumer != null ? confirmConsumer.test(m)
                : JOptionPane.showConfirmDialog(this, m, "Xác nhận", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private JLabel createStatusBadge(String text, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(ViewConstants.FONT_STATUS);
        l.setForeground(c);
        l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(c, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        return l;
    }

    private void updateBadge(JLabel l, String prefix, boolean passed) {
        l.setText(prefix + (passed ? ": HỢP LỆ" : ": SAI LỆCH"));
        Color c = passed ? ViewConstants.COLOR_INCOME : ViewConstants.COLOR_EXPENSE;
        l.setForeground(c);
        l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(c, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
    }

    private JButton createBtn(String t, java.awt.event.ActionListener l) {
        return createBtn(t, null, null, l);
    }

    private JButton createBtn(String t, Color bg, Color fg, java.awt.event.ActionListener l) {
        JButton b = new JButton(t);
        b.setFont(ViewConstants.FONT_BODY);
        if (bg != null)
            b.setBackground(bg);
        if (fg != null)
            b.setForeground(fg);
        b.setFocusPainted(false);
        b.addActionListener(l);
        return b;
    }

    private JPanel row(String l, Component... c) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        r.setBackground(ViewConstants.COLOR_CARD_BG);
        if (!l.isEmpty()) {
            JLabel lbl = new JLabel(l);
            lbl.setFont(ViewConstants.FONT_BODY);
            lbl.setForeground(ViewConstants.COLOR_TEXT_MUTED);
            r.add(lbl);
        }
        for (Component comp : c)
            r.add(comp);
        return r;
    }

    private JPanel box(String t, Component... c) {
        JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
        b.setBackground(ViewConstants.COLOR_CARD_BG);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER), t, 0, 0,
                        ViewConstants.FONT_NAV_ACTIVE, ViewConstants.COLOR_PRIMARY_LIGHT),
                BorderFactory.createEmptyBorder(12, 16, 16, 16)));
        for (Component comp : c)
            b.add(comp);
        return b;
    }
    // endregion
}
