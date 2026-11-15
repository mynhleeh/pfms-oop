package com.pfms.presentation.wallet;

import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.wallet.dialog.AdjustBalanceDialog;
import com.pfms.presentation.wallet.dialog.CreateWalletDialog;
import com.pfms.presentation.wallet.dialog.UpdateWalletDialog;
import com.pfms.presentation.wallet.model.WalletTableModel;
import com.pfms.presentation.wallet.viewmodel.WalletItemViewModel;
import com.pfms.presentation.wallet.viewmodel.WalletsSummaryViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Quản lý Ví theo mô hình MVP (Passive View).
 * Áp dụng bảng màu phẳng Luxury Black &amp; Gold và thứ tự cột chuẩn.
 */
public class WalletPanel extends JPanel implements WalletView {
    // region Fields
    private final WalletPresenter presenter;
    private final WalletTableModel tableModel = new WalletTableModel();
    private final JTable table = new JTable(this.tableModel);
    private final JScrollPane scrollPane = new JScrollPane(this.table);
    private final JComboBox<String> statusFilterBox = new JComboBox<>(new String[]{"Tất cả", "ACTIVE", "ARCHIVED", "DELETED"});
    private final JLabel summaryLabel = new JLabel("Đang tải dữ liệu...");
    private final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
    private final JButton addButton = createToolBtn("Thêm ví mới", ViewConstants.COLOR_PRIMARY, Color.BLACK, ViewConstants.FONT_NAV_ACTIVE),
            editButton = createToolBtn("Sửa", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.FONT_BODY),
            adjustButton = createToolBtn("Kiểm kê số dư", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.FONT_BODY),
            toggleStatusButton = createToolBtn("Lưu trữ / Kích hoạt", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.FONT_BODY),
            deleteButton = createToolBtn("Xóa ví", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_EXPENSE, ViewConstants.FONT_BODY);
    private List<WalletItemViewModel> walletViewModels = Collections.emptyList();
    private Runnable onWalletChangedListener;
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer;
    private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public WalletPanel(WalletGUIController walletController) {
        this.presenter = new WalletPresenter(this, Objects.requireNonNull(walletController, "WalletGUIController không được để null"));
        initUI(); reloadWallets();
    }
    // endregion

    // region UI
    private void initUI() {
        setLayout(new BorderLayout()); setBackground(ViewConstants.COLOR_CONTENT_BG);
        JPanel top = new JPanel(); top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(ViewConstants.COLOR_CONTENT_BG); top.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        JLabel title = new JLabel("Quản lý Ví tài chính"); title.setFont(ViewConstants.FONT_PANEL_HEADER); title.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        top.add(title); top.add(Box.createVerticalStrut(12));
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); bar.setBackground(ViewConstants.COLOR_CONTENT_BG);
        JLabel lbl = new JLabel("Trạng thái:"); lbl.setFont(ViewConstants.FONT_BODY); lbl.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        this.statusFilterBox.setFont(ViewConstants.FONT_BODY); this.statusFilterBox.setBackground(ViewConstants.COLOR_CARD_BG);
        this.statusFilterBox.setForeground(ViewConstants.COLOR_TEXT_MAIN); this.statusFilterBox.addActionListener(e -> applyFilter());
        this.addButton.addActionListener(e -> handleAddWallet()); this.editButton.addActionListener(e -> handleEditWallet());
        this.adjustButton.addActionListener(e -> handleAdjustBalance()); this.toggleStatusButton.addActionListener(e -> handleToggleStatus()); this.deleteButton.addActionListener(e -> handleDeleteWallet());
        for (Component c : new Component[]{lbl, this.statusFilterBox, Box.createHorizontalStrut(8), this.addButton, this.editButton, this.adjustButton, this.toggleStatusButton, this.deleteButton}) bar.add(c);
        top.add(bar); add(top, BorderLayout.NORTH); configureTable();
        JPanel center = new JPanel(new BorderLayout()); center.setBackground(ViewConstants.COLOR_CONTENT_BG); center.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 24));
        this.scrollPane.getViewport().setBackground(ViewConstants.COLOR_CARD_BG); this.scrollPane.setBackground(ViewConstants.COLOR_CARD_BG);
        this.scrollPane.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)); center.add(this.scrollPane, BorderLayout.CENTER); add(center, BorderLayout.CENTER);
        this.bottomPanel.setBackground(ViewConstants.COLOR_STATUS_BG); this.bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));
        this.summaryLabel.setFont(ViewConstants.FONT_BODY); this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED); this.bottomPanel.add(this.summaryLabel); add(this.bottomPanel, BorderLayout.SOUTH);
    }
    private void configureTable() {
        this.table.setFont(ViewConstants.FONT_BODY); this.table.setRowHeight(30); this.table.setBackground(ViewConstants.COLOR_CARD_BG);
        this.table.setForeground(ViewConstants.COLOR_TEXT_MAIN); this.table.setGridColor(ViewConstants.COLOR_BORDER); this.table.setShowGrid(true);
        this.table.setSelectionBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE); this.table.setSelectionForeground(ViewConstants.COLOR_TEXT_ACTIVE);
        this.table.getTableHeader().setBackground(new Color(14, 14, 14)); this.table.getTableHeader().setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.table.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE); this.table.getTableHeader().setReorderingAllowed(false);
        this.table.getTableHeader().setDefaultRenderer(createHeaderRenderer());
        for (int i = 0; i < 7; i++) this.table.getColumnModel().getColumn(i).setPreferredWidth(new int[]{40, 180, 140, 60, 100, 90, 200}[i]);
        DefaultTableCellRenderer center = createCellRenderer(SwingConstants.CENTER, ViewConstants.COLOR_TEXT_MAIN);
        this.table.getColumnModel().getColumn(0).setCellRenderer(createCellRenderer(SwingConstants.CENTER, ViewConstants.COLOR_TEXT_MUTED));
        this.table.getColumnModel().getColumn(1).setCellRenderer(createCellRenderer(SwingConstants.LEFT, ViewConstants.COLOR_TEXT_MAIN));
        this.table.getColumnModel().getColumn(2).setCellRenderer(createCellRenderer(SwingConstants.RIGHT, ViewConstants.COLOR_PRIMARY_LIGHT));
        for (int i : new int[]{3, 4, 5}) this.table.getColumnModel().getColumn(i).setCellRenderer(center);
        this.table.getColumnModel().getColumn(6).setCellRenderer(createCellRenderer(SwingConstants.LEFT, ViewConstants.COLOR_TEXT_MUTED));
    }
    // endregion

    // region Actions
    public void handleAddWallet() { this.presenter.onAddWalletClicked(); }
    public void handleEditWallet() { this.presenter.onEditWalletClicked(getSelectedItem()); }
    public void handleAdjustBalance() { this.presenter.onAdjustBalanceClicked(getSelectedItem()); }
    public void handleToggleStatus() {
        WalletItemViewModel it = getSelectedItem();
        if (it != null) this.presenter.toggleStatus(it.id(), it.isActive()); else showWarning("Vui lòng chọn một ví trong bảng để thao tác.");
    }
    public void handleDeleteWallet() {
        WalletItemViewModel it = getSelectedItem();
        if (it != null) this.presenter.deleteWallet(it.id(), it.name()); else showWarning("Vui lòng chọn một ví trong bảng để thao tác.");
    }
    @Override public void openCreateWalletDialog() { CreateWalletDialog d = new CreateWalletDialog(getParentFrame(), getWalletController()); d.setVisible(true); if (d.isSuccess()) reloadWallets(); }
    @Override public void openUpdateWalletDialog(WalletItemViewModel w) { if (w != null && findWalletDto(w.id()) != null) { UpdateWalletDialog d = new UpdateWalletDialog(getParentFrame(), getWalletController(), findWalletDto(w.id())); d.setVisible(true); if (d.isSuccess()) reloadWallets(); } }
    @Override public void openAdjustBalanceDialog(WalletItemViewModel w) { if (w != null && findWalletDto(w.id()) != null) { AdjustBalanceDialog d = new AdjustBalanceDialog(getParentFrame(), getWalletController(), findWalletDto(w.id())); d.setVisible(true); if (d.isSuccess()) reloadWallets(); } }
    // endregion

    // region Data
    @Override public void renderWallets(List<WalletItemViewModel> wallets) {
        this.walletViewModels = (wallets == null) ? Collections.emptyList() : wallets;
        this.tableModel.setWallets(this.walletViewModels.stream().map(vm -> new WalletResponseDTO(vm.id(), vm.name(), vm.rawBalance(), "VND", false, vm.isActive() ? "ACTIVE" : "ARCHIVED", vm.note(), null)).toList());
        if (this.onWalletChangedListener != null) this.onWalletChangedListener.run();
    }
    @Override public void renderSummary(WalletsSummaryViewModel s) { this.summaryLabel.setText(s == null ? "Không có dữ liệu ví." : "Tổng số ví: " + (s.activeCount() + s.archivedCount()) + " | Tổng số dư: " + s.formattedTotalBalance()); }
    public void reloadWallets() { this.presenter.loadData(); }
    private void applyFilter() { int i = this.statusFilterBox.getSelectedIndex(); this.presenter.filterWallets(i <= 0 ? null : (String) this.statusFilterBox.getSelectedItem()); }
    public void setSelectedStatusFilter(String f) { if (f == null) this.statusFilterBox.setSelectedIndex(0); else this.statusFilterBox.setSelectedItem(f); }
    // endregion

    // region Getters
    public WalletPresenter getPresenter() { return this.presenter; } public WalletGUIController getWalletController() { return this.presenter.getController(); }
    public WalletTableModel getTableModel() { return this.tableModel; } public JTable getTable() { return this.table; }
    public JScrollPane getScrollPane() { return this.scrollPane; } public JLabel getSummaryLabel() { return this.summaryLabel; }
    public JButton getAddButton() { return this.addButton; } public JButton getEditButton() { return this.editButton; }
    public JButton getAdjustButton() { return this.adjustButton; } public JButton getToggleStatusButton() { return this.toggleStatusButton; }
    public JButton getDeleteButton() { return this.deleteButton; } public JComboBox<String> getStatusFilterBox() { return this.statusFilterBox; }
    public JPanel getBottomPanel() { return this.bottomPanel; } public String getSummaryText() { return this.summaryLabel.getText(); }
    public WalletItemViewModel getSelectedItem() { int r = this.table.getSelectedRow(); return (r >= 0 && r < this.walletViewModels.size()) ? this.walletViewModels.get(r) : null; }
    public WalletResponseDTO getSelectedWallet() { int r = this.table.getSelectedRow(); if (r < 0) { showWarning("Vui lòng chọn một ví trong bảng để thao tác."); return null; } return this.tableModel.getWalletAt(r); }
    public void setOnWalletChangedListener(Runnable l) { this.onWalletChangedListener = l; }
    public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = c; } public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = c; }
    public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = c; } public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = c; }
    // endregion

    // region Helpers
    @Override public void showMessage(String m) { if (messageConsumer != null) messageConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thành công", JOptionPane.INFORMATION_MESSAGE); }
    @Override public void showWarning(String m) { if (warningConsumer != null) warningConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.WARNING_MESSAGE); }
    @Override public void showError(String m) { if (errorConsumer != null) errorConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    @Override public boolean confirmAction(String m) { return (confirmConsumer != null) ? confirmConsumer.test(m) : JOptionPane.showConfirmDialog(this, m, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION; }
    private JFrame getParentFrame() { Component c = SwingUtilities.getWindowAncestor(this); return (c instanceof JFrame f) ? f : null; }
    private WalletResponseDTO findWalletDto(UUID id) { for (WalletResponseDTO w : this.tableModel.getWallets()) if (w.getWalletId().equals(id)) return w; return null; }
    private JButton createToolBtn(String text, Color bg, Color fg, Font font) {
        JButton b = new JButton(text); b.setFont(font); b.setBackground(bg); b.setForeground(fg); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER), BorderFactory.createEmptyBorder(6, 12, 6, 12))); return b;
    }
    private DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c); setBackground(new Color(14, 14, 14)); setForeground(ViewConstants.COLOR_PRIMARY_LIGHT); setFont(ViewConstants.FONT_NAV_ACTIVE);
                setHorizontalAlignment(c == 2 ? SwingConstants.RIGHT : (c == 0 || c >= 3 ? SwingConstants.CENTER : SwingConstants.LEFT)); return this;
            }
        };
    }
    private DefaultTableCellRenderer createCellRenderer(int a, Color fg) {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c); setHorizontalAlignment(a); if (!s) { setBackground(ViewConstants.COLOR_CARD_BG); setForeground(fg); } return this;
            }
        };
    }
    // endregion
}
