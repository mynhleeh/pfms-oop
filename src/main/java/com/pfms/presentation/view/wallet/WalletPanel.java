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
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển trung tâm của Phân hệ Quản lý Ví tài chính (WAL).
 * Áp dụng bảng màu phẳng Luxury Black & Gold và thứ tự cột chuẩn.
 */
public class WalletPanel extends JPanel {

    private final WalletGUIController walletController;
    private final WalletTableModel tableModel;
    private final JTable table;
    private final JScrollPane scrollPane;
    private final JComboBox<String> statusFilterBox;
    private final JLabel summaryLabel;
    private final JPanel bottomPanel;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton adjustButton;
    private final JButton toggleStatusButton;
    private final JButton deleteButton;
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
        filterLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        this.statusFilterBox = new JComboBox<>(new String[]{"Tất cả", "ACTIVE", "ARCHIVED", "DELETED"});
        this.statusFilterBox.setFont(ViewConstants.FONT_BODY);
        this.statusFilterBox.setBackground(ViewConstants.COLOR_CARD_BG);
        this.statusFilterBox.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.statusFilterBox.addActionListener(e -> applyFilter());

        this.addButton = createToolButton("Thêm ví mới", ViewConstants.COLOR_PRIMARY, Color.BLACK, ViewConstants.FONT_NAV_ACTIVE);
        this.addButton.addActionListener(e -> handleAddWallet());

        this.editButton = createToolButton("Sửa", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.FONT_BODY);
        this.editButton.addActionListener(e -> handleEditWallet());

        this.adjustButton = createToolButton("Kiểm kê số dư", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.FONT_BODY);
        this.adjustButton.addActionListener(e -> handleAdjustBalance());

        this.toggleStatusButton = createToolButton("Lưu trữ / Kích hoạt", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.FONT_BODY);
        this.toggleStatusButton.addActionListener(e -> handleToggleStatus());

        this.deleteButton = createToolButton("Xóa ví", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_EXPENSE, ViewConstants.FONT_BODY);
        this.deleteButton.addActionListener(e -> handleDeleteWallet());

        toolbar.add(filterLabel);
        toolbar.add(this.statusFilterBox);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(this.addButton);
        toolbar.add(this.editButton);
        toolbar.add(this.adjustButton);
        toolbar.add(this.toggleStatusButton);
        toolbar.add(this.deleteButton);

        topContainer.add(toolbar);
        add(topContainer, BorderLayout.NORTH);

        // Center Table
        this.table = new JTable(this.tableModel);
        this.table.setFont(ViewConstants.FONT_BODY);
        this.table.setRowHeight(30);
        this.table.setBackground(ViewConstants.COLOR_CARD_BG);
        this.table.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.table.setGridColor(ViewConstants.COLOR_BORDER);
        this.table.setShowGrid(true);
        this.table.setSelectionBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        this.table.setSelectionForeground(ViewConstants.COLOR_TEXT_ACTIVE);

        // Header Styling
        this.table.getTableHeader().setBackground(new Color(14, 14, 14));
        this.table.getTableHeader().setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.table.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setBackground(new Color(14, 14, 14));
                setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
                setFont(ViewConstants.FONT_NAV_ACTIVE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, ViewConstants.COLOR_BORDER),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)));
                if (col == 2) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (col == 0 || col == 3 || col == 4 || col == 5) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return this;
            }
        };
        this.table.getTableHeader().setDefaultRenderer(headerRenderer);
        configureTableRenderers();

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(ViewConstants.COLOR_CONTENT_BG);
        centerContainer.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 24));

        this.scrollPane = new JScrollPane(this.table);
        this.scrollPane.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        this.scrollPane.setBackground(ViewConstants.COLOR_CARD_BG);
        this.scrollPane.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER));
        centerContainer.add(this.scrollPane, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);

        // Bottom Summary
        this.bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        this.bottomPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        this.bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        this.summaryLabel = new JLabel("Đang tải dữ liệu...");
        this.summaryLabel.setFont(ViewConstants.FONT_BODY);
        this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        this.bottomPanel.add(this.summaryLabel);

        add(this.bottomPanel, BorderLayout.SOUTH);

        reloadWallets();
    }

    private JButton createToolButton(String text, Color bg, Color fg, Font font) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return btn;
    }

    private void configureTableRenderers() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(ViewConstants.COLOR_TEXT_MAIN);
                }
                return this;
            }
        };

        DefaultTableCellRenderer numRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(ViewConstants.COLOR_TEXT_MUTED);
                }
                return this;
            }
        };

        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(ViewConstants.COLOR_TEXT_MAIN);
                }
                return this;
            }
        };

        DefaultTableCellRenderer balanceRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
                }
                return this;
            }
        };

        DefaultTableCellRenderer noteRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(ViewConstants.COLOR_TEXT_MUTED);
                }
                return this;
            }
        };

        this.table.getColumnModel().getColumn(0).setPreferredWidth(40);
        this.table.getColumnModel().getColumn(0).setCellRenderer(numRenderer);

        this.table.getColumnModel().getColumn(1).setPreferredWidth(180);
        this.table.getColumnModel().getColumn(1).setCellRenderer(textRenderer);

        this.table.getColumnModel().getColumn(2).setPreferredWidth(140);
        this.table.getColumnModel().getColumn(2).setCellRenderer(balanceRenderer);

        this.table.getColumnModel().getColumn(3).setPreferredWidth(60);
        this.table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(4).setPreferredWidth(100);
        this.table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(5).setPreferredWidth(90);
        this.table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(6).setPreferredWidth(200);
        this.table.getColumnModel().getColumn(6).setCellRenderer(noteRenderer);
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

    public JScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public JButton getAddButton() {
        return this.addButton;
    }

    public JButton getEditButton() {
        return this.editButton;
    }

    public JButton getAdjustButton() {
        return this.adjustButton;
    }

    public JButton getToggleStatusButton() {
        return this.toggleStatusButton;
    }

    public JButton getDeleteButton() {
        return this.deleteButton;
    }

    public JComboBox<String> getStatusFilterBox() {
        return this.statusFilterBox;
    }

    public JLabel getSummaryLabel() {
        return this.summaryLabel;
    }

    public JPanel getBottomPanel() {
        return this.bottomPanel;
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
