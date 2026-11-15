package com.pfms.presentation.view.data;

import com.pfms.presentation.controller.DataManagementGUIController;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;
import com.pfms.presentation.view.common.ViewConstants;
import com.pfms.presentation.view.data.model.ReconciliationIssueTableModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Quản trị & An toàn Dữ liệu (CARD_DATA_MANAGEMENT).
 * Bao gồm: Xuất sao lưu JSON (UC-DAT-01), Phục hồi dữ liệu (UC-DAT-02),
 * và Đối soát tính toàn vẹn hệ thống (UC-DAT-03).
 */
public class DataManagementPanel extends JPanel {

    private final DataManagementGUIController dataController;
    private final String storagePath;

    // Backup components
    private final JTextField destinationField;
    private final JComboBox<String> scopeBox;
    private final JLabel exportResultLabel;

    // Restore components
    private final JTextField restoreFileField;
    private final JLabel restoreResultLabel;

    // Reconciliation components
    private final ReconciliationIssueTableModel issueTableModel;
    private final JTable issueTable;
    private final JLabel balanceStatusLabel;
    private final JLabel transferStatusLabel;
    private final JLabel categoryStatusLabel;
    private final JLabel feeStatusLabel;

    private Runnable onDataRestoredListener;

    private Consumer<String> messageConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    };
    private Consumer<String> warningConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    };
    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    };
    private Predicate<String> confirmConsumer = msg -> {
        int confirm = JOptionPane.showConfirmDialog(
                this, msg, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return confirm == JOptionPane.YES_OPTION;
    };

    public DataManagementPanel(DataManagementGUIController dataController, String storagePath) {
        this.dataController = Objects.requireNonNull(dataController, "DataManagementGUIController không được để null");
        this.storagePath = (storagePath != null && !storagePath.isBlank()) ? storagePath : "data/pfms_data.json";

        setLayout(new BorderLayout(10, 10));
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Khởi tạo thành phần sao lưu
        this.destinationField = new JTextField("backups", 18);
        this.destinationField.setFont(ViewConstants.FONT_BODY);

        this.scopeBox = new JComboBox<>(new String[]{"FULL", "TRANSACTIONS"});
        this.scopeBox.setFont(ViewConstants.FONT_BODY);

        this.exportResultLabel = new JLabel("Chưa thực hiện sao lưu");
        this.exportResultLabel.setFont(ViewConstants.FONT_BODY);
        this.exportResultLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        // Khởi tạo thành phần phục hồi
        this.restoreFileField = new JTextField(18);
        this.restoreFileField.setFont(ViewConstants.FONT_BODY);

        this.restoreResultLabel = new JLabel("Chưa thực hiện phục hồi");
        this.restoreResultLabel.setFont(ViewConstants.FONT_BODY);
        this.restoreResultLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        // Khởi tạo thành phần đối soát
        this.issueTableModel = new ReconciliationIssueTableModel();
        this.issueTable = new JTable(this.issueTableModel);
        setupIssueTable();

        this.balanceStatusLabel = createStatusBadge("Số dư ví: Chưa kiểm tra", Color.GRAY);
        this.transferStatusLabel = createStatusBadge("Chuyển tiền: Chưa kiểm tra", Color.GRAY);
        this.categoryStatusLabel = createStatusBadge("Danh mục: Chưa kiểm tra", Color.GRAY);
        this.feeStatusLabel = createStatusBadge("Phụ phí: Chưa kiểm tra", Color.GRAY);

        initViews();
    }

    public DataManagementPanel(DataManagementGUIController dataController) {
        this(dataController, "data/pfms_data.json");
    }

    private void initViews() {
        // North Container: Header
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));

        JLabel titleLabel = new JLabel("Quản trị & An toàn Dữ liệu (Backup, Restore & Integrity Audit)");
        titleLabel.setFont(ViewConstants.FONT_PANEL_HEADER);
        titleLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        northPanel.add(titleLabel);
        add(northPanel, BorderLayout.NORTH);

        // Center Container: SplitPane (Left: Backup & Restore, Right: Reconciliation)
        JPanel leftPanel = createBackupRestorePanel();
        JPanel rightPanel = createReconciliationPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(460);
        splitPane.setResizeWeight(0.45);
        splitPane.setBorder(null);
        splitPane.setBackground(ViewConstants.COLOR_CONTENT_BG);
        add(splitPane, BorderLayout.CENTER);

        // South: Storage info
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        southPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        southPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));

        JLabel storageInfo = new JLabel("Tệp cơ sở dữ liệu hệ thống: " + this.storagePath);
        storageInfo.setFont(ViewConstants.FONT_STATUS);
        storageInfo.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        southPanel.add(storageInfo);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel createBackupRestorePanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(ViewConstants.COLOR_CONTENT_BG);
        container.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 12));

        // Box 1: Backup
        JPanel backupBox = new JPanel();
        backupBox.setLayout(new BoxLayout(backupBox, BoxLayout.Y_AXIS));
        backupBox.setBackground(Color.WHITE);
        backupBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                        "Xuất Bản Sao lưu JSON (UC-DAT-01)",
                        0, 0, ViewConstants.FONT_NAV_ACTIVE, ViewConstants.COLOR_TEXT_MAIN),
                BorderFactory.createEmptyBorder(12, 16, 16, 16)));

        JPanel destRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        destRow.setBackground(Color.WHITE);
        destRow.add(new JLabel("Thư mục đích:"));
        destRow.add(this.destinationField);
        JButton browseDestBtn = createSmallButton("Chọn...");
        browseDestBtn.addActionListener(e -> chooseDestinationFolder());
        destRow.add(browseDestBtn);

        JPanel scopeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        scopeRow.setBackground(Color.WHITE);
        scopeRow.add(new JLabel("Phạm vi sao lưu:"));
        scopeRow.add(this.scopeBox);

        JButton exportBtn = createPrimaryButton("Xuất Sao lưu");
        exportBtn.addActionListener(e -> triggerExportBackup(this.destinationField.getText(), (String) this.scopeBox.getSelectedItem()));
        scopeRow.add(exportBtn);

        JPanel resultRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        resultRow.setBackground(Color.WHITE);
        resultRow.add(this.exportResultLabel);

        backupBox.add(destRow);
        backupBox.add(scopeRow);
        backupBox.add(Box.createVerticalStrut(6));
        backupBox.add(resultRow);

        // Box 2: Restore
        JPanel restoreBox = new JPanel();
        restoreBox.setLayout(new BoxLayout(restoreBox, BoxLayout.Y_AXIS));
        restoreBox.setBackground(Color.WHITE);
        restoreBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                        "Phục hồi Dữ liệu từ Sao lưu (UC-DAT-02)",
                        0, 0, ViewConstants.FONT_NAV_ACTIVE, ViewConstants.COLOR_TEXT_MAIN),
                BorderFactory.createEmptyBorder(12, 16, 16, 16)));

        JPanel fileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fileRow.setBackground(Color.WHITE);
        fileRow.add(new JLabel("Tệp sao lưu:"));
        fileRow.add(this.restoreFileField);
        JButton browseFileBtn = createSmallButton("Chọn...");
        browseFileBtn.addActionListener(e -> chooseRestoreFile());
        fileRow.add(browseFileBtn);

        JButton restoreBtn = new JButton("Phục hồi Dữ liệu");
        restoreBtn.setFont(ViewConstants.FONT_BODY);
        restoreBtn.setBackground(new Color(220, 53, 69));
        restoreBtn.setForeground(Color.WHITE);
        restoreBtn.setFocusPainted(false);
        restoreBtn.addActionListener(e -> triggerRestoreBackup(this.restoreFileField.getText()));
        fileRow.add(restoreBtn);

        JPanel restoreResultRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        restoreResultRow.setBackground(Color.WHITE);
        restoreResultRow.add(this.restoreResultLabel);

        restoreBox.add(fileRow);
        restoreBox.add(Box.createVerticalStrut(6));
        restoreBox.add(restoreResultRow);

        container.add(backupBox);
        container.add(Box.createVerticalStrut(15));
        container.add(restoreBox);
        container.add(Box.createVerticalGlue());

        return container;
    }

    private JPanel createReconciliationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 12, 12, 24),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                                "Đối soát Tính Toàn vẹn Hệ thống (UC-DAT-03)",
                                0, 0, ViewConstants.FONT_NAV_ACTIVE, ViewConstants.COLOR_TEXT_MAIN),
                        BorderFactory.createEmptyBorder(12, 16, 16, 16))));

        // Top controls of Reconcile
        JPanel topRow = new JPanel(new BorderLayout(10, 8));
        topRow.setBackground(Color.WHITE);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        btnRow.setBackground(Color.WHITE);
        JButton reconcileBtn = createPrimaryButton("Đối soát dữ liệu ngay");
        reconcileBtn.addActionListener(e -> triggerReconcile());
        btnRow.add(reconcileBtn);
        topRow.add(btnRow, BorderLayout.NORTH);

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        badgeRow.setBackground(Color.WHITE);
        badgeRow.add(this.balanceStatusLabel);
        badgeRow.add(this.transferStatusLabel);
        badgeRow.add(this.categoryStatusLabel);
        badgeRow.add(this.feeStatusLabel);
        topRow.add(badgeRow, BorderLayout.SOUTH);

        panel.add(topRow, BorderLayout.NORTH);

        // Center Table of issues
        JScrollPane scrollPane = new JScrollPane(this.issueTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupIssueTable() {
        this.issueTable.setFont(ViewConstants.FONT_BODY);
        this.issueTable.setRowHeight(28);
        this.issueTable.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.issueTable.getTableHeader().setBackground(new Color(245, 245, 245));
        this.issueTable.setGridColor(ViewConstants.COLOR_BORDER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        if (this.issueTable.getColumnModel().getColumnCount() >= 5) {
            this.issueTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            this.issueTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

            this.issueTable.getColumnModel().getColumn(1).setPreferredWidth(110);
            this.issueTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

            this.issueTable.getColumnModel().getColumn(2).setPreferredWidth(140);
            this.issueTable.getColumnModel().getColumn(3).setPreferredWidth(220);
            this.issueTable.getColumnModel().getColumn(4).setPreferredWidth(160);
        }
    }

    private JLabel createStatusBadge(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ViewConstants.FONT_STATUS);
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        return lbl;
    }

    private void updateBadge(JLabel label, String prefix, boolean passed) {
        if (passed) {
            label.setText(prefix + ": HỢP LỆ");
            label.setForeground(ViewConstants.COLOR_INCOME);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ViewConstants.COLOR_INCOME, 1),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        } else {
            label.setText(prefix + ": SAI LỆCH");
            label.setForeground(ViewConstants.COLOR_EXPENSE);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ViewConstants.COLOR_EXPENSE, 1),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        }
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(ViewConstants.FONT_BODY);
        btn.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(ViewConstants.FONT_BODY);
        btn.setFocusPainted(false);
        return btn;
    }

    private void chooseDestinationFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (selected != null) {
                this.destinationField.setText(selected.getAbsolutePath());
            }
        }
    }

    private void chooseRestoreFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (selected != null) {
                this.restoreFileField.setText(selected.getAbsolutePath());
            }
        }
    }

    public void triggerExportBackup(String destinationPath, String scope) {
        try {
            ExportBackupResponseDTO response = this.dataController.handleExportBackup(destinationPath, scope);
            if (response != null) {
                String msg = String.format("Đã sao lưu tệp %s (%d bytes). Tổng số bản ghi: %d (Ví: %d, Danh mục: %d, Giao dịch: %d, Kiểm kê: %d)",
                        response.getFilePath(), response.getFileSizeBytes(), response.getTotalRecordCount(),
                        response.getWalletCount(), response.getCategoryCount(),
                        response.getTransactionCount(), response.getAdjustmentCount());
                this.exportResultLabel.setText(msg);
                this.exportResultLabel.setForeground(ViewConstants.COLOR_INCOME);
                this.messageConsumer.accept("Xuất bản sao lưu dữ liệu thành công!");
            }
        } catch (Exception ex) {
            this.exportResultLabel.setText("Xuất sao lưu thất bại: " + ex.getMessage());
            this.exportResultLabel.setForeground(ViewConstants.COLOR_EXPENSE);
            this.errorConsumer.accept("Xuất sao lưu thất bại: " + ex.getMessage());
        }
    }

    public void triggerRestoreBackup(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            this.warningConsumer.accept("Vui lòng nhập hoặc chọn đường dẫn tệp sao lưu JSON!");
            return;
        }

        boolean confirm = this.confirmConsumer.test(
                "CẢNH BÁO: Thao tác phục hồi sẽ ghi đè toàn bộ dữ liệu hiện hành của hệ thống bằng bản sao lưu này.\nBạn có chắc chắn muốn tiếp tục không?");

        if (!confirm) {
            return;
        }

        try {
            RestoreBackupResponseDTO response = this.dataController.handleRestoreBackup(filePath);
            if (response != null && response.isSuccess()) {
                String msg = String.format("Phục hồi thành công! Tổng bản ghi: %d (Ví: %d, Danh mục: %d, Giao dịch: %d)",
                        response.getTotalRestoredRecords(), response.getRestoredWallets(),
                        response.getRestoredCategories(), response.getRestoredTransactions());
                this.restoreResultLabel.setText(msg);
                this.restoreResultLabel.setForeground(ViewConstants.COLOR_INCOME);
                this.messageConsumer.accept("Phục hồi dữ liệu từ sao lưu thành công!");

                if (this.onDataRestoredListener != null) {
                    this.onDataRestoredListener.run();
                }
            } else {
                String err = (response != null) ? response.getMessage() : "Không nhận được phản hồi";
                this.restoreResultLabel.setText("Phục hồi thất bại: " + err);
                this.restoreResultLabel.setForeground(ViewConstants.COLOR_EXPENSE);
                this.errorConsumer.accept("Phục hồi dữ liệu thất bại: " + err);
            }
        } catch (Exception ex) {
            this.restoreResultLabel.setText("Lỗi phục hồi: " + ex.getMessage());
            this.restoreResultLabel.setForeground(ViewConstants.COLOR_EXPENSE);
            this.errorConsumer.accept("Phục hồi dữ liệu thất bại: " + ex.getMessage());
        }
    }

    public void triggerReconcile() {
        try {
            ReconciliationReportResponseDTO report = this.dataController.handleReconcileSystemData();
            if (report != null) {
                updateBadge(this.balanceStatusLabel, "Số dư ví", report.isWalletBalanceConsistent());
                updateBadge(this.transferStatusLabel, "Chuyển tiền", report.isTransferConsistent());
                updateBadge(this.categoryStatusLabel, "Danh mục", report.isCategoryHierarchyConsistent());
                updateBadge(this.feeStatusLabel, "Phụ phí", report.isFeeConsistent());

                this.issueTableModel.setIssues(report.getIssues());

                if (report.isFullyConsistent()) {
                    this.messageConsumer.accept("Đối soát hoàn tất: Hệ thống đạt trạng thái toàn vẹn 100%!");
                } else {
                    this.warningConsumer.accept(String.format("Phát hiện %d vấn đề sai lệch toàn vẹn trong hệ thống!", report.getIssues().size()));
                }
            }
        } catch (Exception ex) {
            this.errorConsumer.accept("Đối soát dữ liệu thất bại: " + ex.getMessage());
        }
    }

    // Getters and Listeners
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

    public void setOnDataRestoredListener(Runnable listener) {
        this.onDataRestoredListener = listener;
    }

    public void setMessageConsumer(Consumer<String> consumer) {
        this.messageConsumer = Objects.requireNonNull(consumer);
    }

    public void setWarningConsumer(Consumer<String> consumer) {
        this.warningConsumer = Objects.requireNonNull(consumer);
    }

    public void setErrorConsumer(Consumer<String> consumer) {
        this.errorConsumer = Objects.requireNonNull(consumer);
    }

    public void setConfirmConsumer(Predicate<String> consumer) {
        this.confirmConsumer = Objects.requireNonNull(consumer);
    }
}
