package com.pfms.presentation.transfer;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.dto.transfer.*;
import com.pfms.presentation.dto.wallet.*;
import com.pfms.presentation.common.InputValidator;
import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.transfer.dialog.TransferFundsDialog;
import com.pfms.presentation.transfer.model.TransferTableModel;
import com.pfms.presentation.transfer.viewmodel.TransferItemViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Chuyển tiền Nội bộ theo mô hình MVP (Passive View).
 */
public class TransferPanel extends JPanel implements TransferView {
    // region Fields
    private final TransferPresenter presenter; private final WalletGUIController walletController; private final CategoryGUIController categoryController;
    private final TransferTableModel tableModel = new TransferTableModel(); private final JTable table = new JTable(this.tableModel);
    private final JComboBox<Object> sourceWalletBox = new JComboBox<>(), destWalletBox = new JComboBox<>();
    private final JTextField startDateField = new JTextField(8), endDateField = new JTextField(8);
    private final JLabel pageInfoLabel = new JLabel("Trang 1 / 1 (Tổng 0 giao dịch)"), summaryLabel = new JLabel("Tổng tiền chuyển: 0 VNĐ | Tổng phí: 0 VNĐ");
    private final JButton prevButton = new JButton("< Trước"), nextButton = new JButton("Sau >");
    private List<TransferItemViewModel> transferViewModels = Collections.emptyList(); private Runnable onTransferChangedListener;
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer; private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public TransferPanel(TransferGUIController tc, WalletGUIController wc, CategoryGUIController cc) {
        this.walletController = Objects.requireNonNull(wc, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(cc, "CategoryGUIController không được null");
        this.presenter = new TransferPresenter(this, Objects.requireNonNull(tc, "TransferGUIController không được null"));
        initUI(); loadWallets(); reloadTransfers();
    }
    // endregion

    // region UI
    private void initUI() {
        setLayout(new BorderLayout(10, 10)); setBackground(ViewConstants.COLOR_CONTENT_BG);
        JPanel north = new JPanel(); north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(ViewConstants.COLOR_CONTENT_BG); north.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        JLabel title = new JLabel("Chuyển tiền Nội bộ"); title.setFont(ViewConstants.FONT_PANEL_HEADER); title.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        north.add(title); north.add(Box.createVerticalStrut(10));
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); filterRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        JButton btnFilter = createBtn("Lọc", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN), btnReset = createBtn("Đặt lại", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        btnFilter.addActionListener(e -> applyFilter()); btnReset.addActionListener(e -> resetFilter());
        for (Component c : new Component[]{new JLabel("Ví nguồn:"), this.sourceWalletBox, new JLabel("Ví đích:"), this.destWalletBox, new JLabel("Từ ngày:"), this.startDateField, new JLabel("Đến ngày:"), this.endDateField, btnFilter, btnReset}) filterRow.add(c);
        north.add(filterRow); north.add(Box.createVerticalStrut(5));
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); actionRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        JButton btnNew = createBtn("+ Chuyển tiền mới", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE), btnDel = createBtn("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE), btnRefresh = createBtn("Làm mới", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        btnNew.addActionListener(e -> openTransferFundsDialog()); btnDel.addActionListener(e -> handleDeleteTransfer()); btnRefresh.addActionListener(e -> { loadWallets(); reloadTransfers(); });
        for (Component c : new Component[]{btnNew, btnDel, btnRefresh}) actionRow.add(c);
        north.add(actionRow); add(north, BorderLayout.NORTH); setupTable();
        JScrollPane scroll = new JScrollPane(this.table); scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 24, 12, 24), BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        add(scroll, BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout()); south.setBackground(ViewConstants.COLOR_STATUS_BG);
        south.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER), BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        this.summaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE); south.add(this.summaryLabel, BorderLayout.WEST);
        JPanel pgn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); pgn.setBackground(ViewConstants.COLOR_STATUS_BG);
        this.prevButton.setEnabled(false); this.prevButton.addActionListener(e -> this.presenter.prevPage());
        this.nextButton.setEnabled(false); this.nextButton.addActionListener(e -> this.presenter.nextPage());
        for (Component c : new Component[]{this.prevButton, this.pageInfoLabel, this.nextButton}) pgn.add(c);
        south.add(pgn, BorderLayout.EAST); add(south, BorderLayout.SOUTH);
    }
    private void setupTable() {
        this.table.setFont(ViewConstants.FONT_BODY); this.table.setRowHeight(28);
        this.table.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE); this.table.setGridColor(ViewConstants.COLOR_BORDER);
        DefaultTableCellRenderer c = new DefaultTableCellRenderer(), r = new DefaultTableCellRenderer();
        c.setHorizontalAlignment(SwingConstants.CENTER); r.setHorizontalAlignment(SwingConstants.RIGHT);
        if (this.table.getColumnModel().getColumnCount() >= 7) {
            int[] w = {50, 100, 160, 160, 130, 110, 200};
            for (int i = 0; i < w.length; i++) this.table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
            this.table.getColumnModel().getColumn(0).setCellRenderer(c); this.table.getColumnModel().getColumn(1).setCellRenderer(c);
            this.table.getColumnModel().getColumn(4).setCellRenderer(r); this.table.getColumnModel().getColumn(5).setCellRenderer(r);
        }
    }
    // endregion

    // region Actions
    public void applyFilter() { this.presenter.loadTransfers(buildFilter(1)); }
    public void resetFilter() {
        this.sourceWalletBox.setSelectedIndex(0); this.destWalletBox.setSelectedIndex(0);
        this.startDateField.setText(""); this.endDateField.setText("");
        this.presenter.loadTransfers(new FilterTransfersRequestDTO());
    }
    public void handleDeleteTransfer() {
        int row = this.table.getSelectedRow();
        if (row < 0) { showWarning("Vui lòng chọn giao dịch chuyển tiền cần xóa!"); return; }
        if (row < this.transferViewModels.size()) this.presenter.deleteTransfer(this.transferViewModels.get(row));
    }
    @Override public void openTransferFundsDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        TransferFundsDialog dialog = new TransferFundsDialog(parent, this.presenter.getController(), this.walletController, this.categoryController);
        dialog.setVisible(true);
        if (dialog.isSuccess()) { showMessage("Chuyển tiền thành công!"); reloadTransfers(); }
    }
    // endregion

    // region Data
    @Override public void renderTransfers(List<TransferItemViewModel> transfers, String summaryText, String pageInfoText) {
        this.transferViewModels = (transfers != null) ? transfers : Collections.emptyList();
        this.tableModel.setTransfers(this.transferViewModels.stream().map(this::toTransferDto).toList());
        this.summaryLabel.setText(summaryText); this.pageInfoLabel.setText(pageInfoText);
        this.prevButton.setEnabled(this.presenter.getCurrentPage() > 1);
        this.nextButton.setEnabled(this.presenter.getCurrentPage() < this.presenter.getTotalPages());
        if (this.onTransferChangedListener != null) this.onTransferChangedListener.run();
    }
    public void reloadTransfers() { this.presenter.loadTransfers(buildFilter(this.presenter.getCurrentPage())); }
    private void loadWallets() {
        try {
            WalletsSummaryResponseDTO s = this.walletController.loadWalletsSummary();
            this.sourceWalletBox.removeAllItems(); this.destWalletBox.removeAllItems();
            this.sourceWalletBox.addItem("Tất cả ví nguồn"); this.destWalletBox.addItem("Tất cả ví đích");
            if (s != null && s.getWallets() != null) for (WalletResponseDTO w : s.getWallets()) {
                this.sourceWalletBox.addItem(new WalletComboItem(w.getWalletId(), w.getName()));
                this.destWalletBox.addItem(new WalletComboItem(w.getWalletId(), w.getName()));
            }
        } catch (Exception ignored) {}
    }
    private FilterTransfersRequestDTO buildFilter(int page) {
        UUID src = (this.sourceWalletBox.getSelectedItem() instanceof WalletComboItem it) ? it.id() : null;
        UUID dst = (this.destWalletBox.getSelectedItem() instanceof WalletComboItem it) ? it.id() : null;
        return new FilterTransfersRequestDTO(InputValidator.parseOptionalDate(this.startDateField.getText(), "Ngày bắt đầu"),
                InputValidator.parseOptionalDate(this.endDateField.getText(), "Ngày kết thúc"), src, dst, page, 20);
    }
    // endregion

    // region Getters
    public TransferPresenter getPresenter() { return this.presenter; } public TransferTableModel getTableModel() { return this.tableModel; }
    public JTable getTable() { return this.table; } public String getSummaryText() { return this.summaryLabel.getText(); }
    public String getPageInfoText() { return this.pageInfoLabel.getText(); } public void setOnTransferChangedListener(Runnable l) { this.onTransferChangedListener = l; }
    public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = c; } public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = c; }
    public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = c; } public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = c; }
    // endregion

    // region Helpers
    @Override public void showMessage(String m) { if (messageConsumer != null) messageConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    @Override public void showWarning(String m) { if (warningConsumer != null) warningConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE); }
    @Override public void showError(String m) { if (errorConsumer != null) errorConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    @Override public boolean confirmAction(String m) { return (confirmConsumer != null) ? confirmConsumer.test(m) : JOptionPane.showConfirmDialog(this, m, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION; }
    private JButton createBtn(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt); b.setFont(ViewConstants.FONT_BODY); b.setBackground(bg); b.setForeground(fg); b.setFocusPainted(false); return b;
    }
    private TransferResponseDTO toTransferDto(TransferItemViewModel vm) {
        LocalDate d = null;
        if (vm.dateFormatted() != null && !vm.dateFormatted().isBlank() && !"-".equals(vm.dateFormatted())) {
            try { d = LocalDate.parse(vm.dateFormatted(), DateTimeFormatter.ofPattern("dd/MM/yyyy")); } catch (Exception ignored) { d = LocalDate.now(); }
        }
        return new TransferResponseDTO(vm.id(), UUID.randomUUID(), vm.fromWalletName(), UUID.randomUUID(), vm.toWalletName(),
                vm.rawAmount() != null ? vm.rawAmount() : BigDecimal.ZERO, d, vm.note(),
                vm.fromBalanceAfter() != null ? vm.fromBalanceAfter() : BigDecimal.ZERO,
                vm.toBalanceAfter() != null ? vm.toBalanceAfter() : BigDecimal.ZERO, null);
    }
    private record WalletComboItem(UUID id, String name) { @Override public String toString() { return name; } }
    // endregion
}
