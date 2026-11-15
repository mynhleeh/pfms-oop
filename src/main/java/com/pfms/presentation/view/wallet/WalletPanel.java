package com.pfms.presentation.view.wallet;

import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.view.common.ViewConstants;
import com.pfms.presentation.view.wallet.dialog.AdjustBalanceDialog;
import com.pfms.presentation.view.wallet.dialog.CreateWalletDialog;
import com.pfms.presentation.view.wallet.dialog.UpdateWalletDialog;
import com.pfms.presentation.view.wallet.model.WalletTableModel;

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
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển trung tâm của Phân hệ Quản lý Ví tài chính (WAL).
 */
public class WalletPanel extends JPanel {

    private final WalletGUIController walletController;
    private final WalletTableModel tableModel;
    private final JTable table;
    private final JComboBox<String> statusFilterBox;
    private final JLabel summaryLabel;
    private final DecimalFormat currencyFormatter;
    private Runnable onWalletChangedListener;

    private Consumer<String> messageConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    };
    private Consumer<String> warningConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    };
    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    };
    private Predicate<String> confirmConsumer = msg -> {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                msg,
                "Xác nhận thao tác",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return confirm == JOptionPane.YES_OPTION;
    };

    public WalletPanel(WalletGUIController walletController) {
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        this.tableModel = new WalletTableModel();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);

        setLayout(new BorderLayout());
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Header & Toolbar
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(ViewConstants.COLOR_CONTENT_BG);
        topContainer.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JLabel titleLabel = new JLabel("Quản lý Ví tài chính");
        titleLabel.setFont(ViewConstants.FONT_PANEL_HEADER);
        titleLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topContainer.add(titleLabel);
        topContainer.add(Box.createVerticalStrut(12));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(ViewConstants.COLOR_CONTENT_BG);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel filterLabel = new JLabel("Trạng thái:");
        filterLabel.setFont(ViewConstants.FONT_BODY);

        this.statusFilterBox = new JComboBox<>(new String[]{"Tất cả", "ACTIVE", "ARCHIVED", "DELETED"});
        this.statusFilterBox.setFont(ViewConstants.FONT_BODY);
        this.statusFilterBox.addActionListener(e -> applyFilter());

        JButton addButton = createToolButton("Thêm ví mới", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE);
        addButton.addActionListener(e -> handleAddWallet());

        JButton editButton = createToolButton("Sửa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        editButton.addActionListener(e -> handleEditWallet());

        JButton adjustButton = createToolButton("Kiểm kê số dư", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        adjustButton.addActionListener(e -> handleAdjustBalance());

        JButton toggleStatusButton = createToolButton("Lưu trữ / Kích hoạt", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        toggleStatusButton.addActionListener(e -> handleToggleStatus());

        JButton deleteButton = createToolButton("Xóa ví", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE);
        deleteButton.addActionListener(e -> handleDeleteWallet());

        toolbar.add(filterLabel);
        toolbar.add(this.statusFilterBox);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(adjustButton);
        toolbar.add(toggleStatusButton);
        toolbar.add(deleteButton);

        topContainer.add(toolbar);
        add(topContainer, BorderLayout.NORTH);

        // Center Table
        this.table = new JTable(this.tableModel);
        this.table.setFont(ViewConstants.FONT_BODY);
        this.table.setRowHeight(28);
        this.table.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        configureTableRenderers();

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 24, 12, 24),
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Summary
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        bottomPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        this.summaryLabel = new JLabel("Đang tải dữ liệu...");
        this.summaryLabel.setFont(ViewConstants.FONT_BODY);
        this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        bottomPanel.add(this.summaryLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        reloadWallets();
    }

    private JButton createToolButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(ViewConstants.FONT_BODY);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return btn;
    }

    private void configureTableRenderers() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        this.table.getColumnModel().getColumn(0).setPreferredWidth(40);
        this.table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(1).setPreferredWidth(180);

        this.table.getColumnModel().getColumn(2).setPreferredWidth(140);
        this.table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        this.table.getColumnModel().getColumn(3).setPreferredWidth(60);
        this.table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(4).setPreferredWidth(100);
        this.table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(5).setPreferredWidth(90);
        this.table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(6).setPreferredWidth(200);
    }

    public void reloadWallets() {
        String filter = getSelectedFilter();
        WalletsSummaryResponseDTO summary;
        if (filter == null) {
            summary = this.walletController.loadWalletsSummary();
        } else {
            summary = this.walletController.loadWalletsSummary(filter);
        }

        if (summary != null) {
            this.tableModel.setWallets(summary.getWallets());
            this.summaryLabel.setText("Tổng số ví: " + summary.getWallets().size() +
                    " | Tổng số dư: " + this.currencyFormatter.format(summary.getTotalBalance()) + " " + summary.getCurrency());
        } else {
            this.tableModel.setWallets(null);
            this.summaryLabel.setText("Không có dữ liệu ví.");
        }

        if (this.onWalletChangedListener != null) {
            this.onWalletChangedListener.run();
        }
    }

    private void applyFilter() {
        reloadWallets();
    }

    private String getSelectedFilter() {
        int idx = this.statusFilterBox.getSelectedIndex();
        return (idx <= 0) ? null : (String) this.statusFilterBox.getSelectedItem();
    }

    public void setSelectedStatusFilter(String filter) {
        if (filter == null) {
            this.statusFilterBox.setSelectedIndex(0);
        } else {
            this.statusFilterBox.setSelectedItem(filter);
        }
    }

    private JFrame getParentFrame() {
        Component c = SwingUtilities.getWindowAncestor(this);
        return (c instanceof JFrame frame) ? frame : null;
    }

    public WalletResponseDTO getSelectedWallet() {
        int row = this.table.getSelectedRow();
        if (row < 0) {
            if (this.warningConsumer != null) {
                this.warningConsumer.accept("Vui lòng chọn một ví trong bảng để thao tác.");
            }
            return null;
        }
        return this.tableModel.getWalletAt(row);
    }

    public void handleAddWallet() {
        CreateWalletDialog dialog = new CreateWalletDialog(getParentFrame(), this.walletController);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            reloadWallets();
        }
    }

    public void handleEditWallet() {
        WalletResponseDTO selected = getSelectedWallet();
        if (selected == null) return;

        UpdateWalletDialog dialog = new UpdateWalletDialog(getParentFrame(), this.walletController, selected);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            reloadWallets();
        }
    }

    public void handleAdjustBalance() {
        WalletResponseDTO selected = getSelectedWallet();
        if (selected == null) return;

        AdjustBalanceDialog dialog = new AdjustBalanceDialog(getParentFrame(), this.walletController, selected);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            reloadWallets();
        }
    }

    public void handleToggleStatus() {
        WalletResponseDTO selected = getSelectedWallet();
        if (selected == null) return;

        try {
            if ("ACTIVE".equalsIgnoreCase(selected.getStatus())) {
                this.walletController.handleArchiveWallet(selected.getWalletId());
                if (this.messageConsumer != null) {
                    this.messageConsumer.accept("Đã chuyển ví sang trạng thái lưu trữ.");
                }
            } else {
                this.walletController.handleActivateWallet(selected.getWalletId());
                if (this.messageConsumer != null) {
                    this.messageConsumer.accept("Đã kích hoạt lại ví thành công.");
                }
            }
            reloadWallets();
        } catch (Exception ex) {
            if (this.errorConsumer != null) {
                this.errorConsumer.accept(ex.getMessage());
            }
        }
    }

    public void handleDeleteWallet() {
        WalletResponseDTO selected = getSelectedWallet();
        if (selected == null) return;

        boolean confirm = false;
        if (this.confirmConsumer != null) {
            confirm = this.confirmConsumer.test(
                    "Bạn có chắc chắn muốn xóa ví \"" + selected.getName() + "\" không?\nThao tác này sẽ chuyển ví sang trạng thái đã xóa.");
        }

        if (confirm) {
            try {
                this.walletController.handleDeleteWallet(selected.getWalletId());
                if (this.messageConsumer != null) {
                    this.messageConsumer.accept("Đã xóa ví thành công.");
                }
                reloadWallets();
            } catch (Exception ex) {
                if (this.errorConsumer != null) {
                    this.errorConsumer.accept(ex.getMessage());
                }
            }
        }
    }

    public void setOnWalletChangedListener(Runnable listener) {
        this.onWalletChangedListener = listener;
    }

    public WalletTableModel getTableModel() {
        return this.tableModel;
    }

    public JTable getTable() {
        return this.table;
    }

    public String getSummaryText() {
        return this.summaryLabel.getText();
    }

    public void setMessageConsumer(Consumer<String> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public void setWarningConsumer(Consumer<String> warningConsumer) {
        this.warningConsumer = warningConsumer;
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public void setConfirmConsumer(Predicate<String> confirmConsumer) {
        this.confirmConsumer = confirmConsumer;
    }
}
