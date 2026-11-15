package com.pfms.presentation.transaction;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.transaction.component.TransactionFilterToolbar;
import com.pfms.presentation.transaction.dialog.RecordTransactionDialog;
import com.pfms.presentation.transaction.dialog.UpdateTransactionDialog;
import com.pfms.presentation.transaction.model.TransactionTableModel;
import com.pfms.presentation.transaction.viewmodel.TransactionItemViewModel;

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
 * Bảng điều khiển phân hệ Quản lý Giao dịch Thu &amp; Chi theo kiến trúc MVP Passive View.
 */
public class TransactionPanel extends JPanel implements TransactionView {
    // region Fields
    private final TransactionPresenter presenter; private final WalletGUIController walletController; private final CategoryGUIController categoryController;
    private final TransactionFilterToolbar filterToolbar; private final TransactionTableModel tableModel = new TransactionTableModel();
    private final JTable table = new JTable(this.tableModel); private final JLabel pageInfoLabel = new JLabel("Trang 1 / 1 (Tổng 0 giao dịch)");
    private final JLabel summaryLabel = new JLabel("Tổng thu: 0 VNĐ | Tổng chi: 0 VNĐ | Dòng tiền thuần: 0 VNĐ");
    private final JButton prevButton = new JButton("< Trước"), nextButton = new JButton("Sau >");
    private List<TransactionItemViewModel> transactionViewModels = Collections.emptyList(); private Runnable onTransactionChangedListener;
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer; private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public TransactionPanel(TransactionGUIController tc, WalletGUIController wc, CategoryGUIController cc) {
        this.walletController = Objects.requireNonNull(wc, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(cc, "CategoryGUIController không được null");
        this.presenter = new TransactionPresenter(this, Objects.requireNonNull(tc, "TransactionGUIController không được null"));
        this.filterToolbar = new TransactionFilterToolbar(this::reloadTransactions);
        initUI(); loadWallets(); reloadTransactions();
    }
    // endregion

    // region UI
    private void initUI() {
        setLayout(new BorderLayout()); setBackground(ViewConstants.COLOR_CONTENT_BG);
        JPanel north = new JPanel(); north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(ViewConstants.COLOR_CONTENT_BG); north.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        JLabel title = new JLabel("Ghi nhận Thu & Chi"); title.setFont(ViewConstants.FONT_PANEL_HEADER); title.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        north.add(title); north.add(Box.createVerticalStrut(10)); north.add(this.filterToolbar); north.add(Box.createVerticalStrut(8));
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); bar.setBackground(ViewConstants.COLOR_CONTENT_BG);
        JButton addBtn = createBtn("Thêm giao dịch", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE), editBtn = createBtn("Sửa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN),
                delBtn = createBtn("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE), refBtn = createBtn("Làm mới", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        addBtn.addActionListener(e -> handleAddTransaction()); editBtn.addActionListener(e -> handleEditTransaction());
        delBtn.addActionListener(e -> handleDeleteTransaction()); refBtn.addActionListener(e -> { loadWallets(); reloadTransactions(); });
        for (Component b : new Component[]{addBtn, editBtn, delBtn, refBtn}) bar.add(b);
        north.add(bar); add(north, BorderLayout.NORTH); configureTable();
        JScrollPane scroll = new JScrollPane(this.table); scroll.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 20, 8, 20), BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        add(scroll, BorderLayout.CENTER);
        JPanel south = new JPanel(); south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS)); south.setBackground(ViewConstants.COLOR_STATUS_BG);
        south.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));
        JPanel pgn = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6)); pgn.setBackground(ViewConstants.COLOR_STATUS_BG);
        this.prevButton.setFont(ViewConstants.FONT_BODY); this.nextButton.setFont(ViewConstants.FONT_BODY); this.pageInfoLabel.setFont(ViewConstants.FONT_BODY);
        this.prevButton.addActionListener(e -> this.presenter.prevPage()); this.nextButton.addActionListener(e -> this.presenter.nextPage());
        for (Component c : new Component[]{this.prevButton, this.pageInfoLabel, this.nextButton}) pgn.add(c);
        JPanel sum = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6)); sum.setBackground(ViewConstants.COLOR_STATUS_BG);
        sum.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));
        this.summaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE); this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN); sum.add(this.summaryLabel);
        south.add(pgn); south.add(sum); add(south, BorderLayout.SOUTH);
    }
    private void configureTable() {
        this.table.setFont(ViewConstants.FONT_BODY); this.table.setRowHeight(28); this.table.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        DefaultTableCellRenderer c = new DefaultTableCellRenderer(), r = new DefaultTableCellRenderer();
        c.setHorizontalAlignment(SwingConstants.CENTER); r.setHorizontalAlignment(SwingConstants.RIGHT);
        int[] w = {40, 90, 90, 140, 120, 120, 90, 180};
        for (int i = 0; i < w.length; i++) this.table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        for (int i : new int[]{0, 1, 2}) this.table.getColumnModel().getColumn(i).setCellRenderer(c);
        this.table.getColumnModel().getColumn(6).setCellRenderer(r);
        this.table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, s, f, row, col); setHorizontalAlignment(SwingConstants.RIGHT);
                TransactionResponseDTO d = tableModel.getTransactionAt(row);
                if (d != null && !s) {
                    if ("INCOME".equalsIgnoreCase(d.getTransactionType())) setForeground(ViewConstants.COLOR_INCOME);
                    else if ("EXPENSE".equalsIgnoreCase(d.getTransactionType())) setForeground(ViewConstants.COLOR_EXPENSE);
                    else setForeground(ViewConstants.COLOR_TEXT_MAIN);
                }
                return this;
            }
        });
    }
    // endregion

    // region Actions
    public void handleAddTransaction() { this.presenter.onAddTransactionClicked(); }
    public void handleEditTransaction() { this.presenter.onEditTransactionClicked(getSelectedViewModel()); }
    public void handleDeleteTransaction() { this.presenter.deleteTransaction(getSelectedViewModel()); }
    @Override public void openRecordDialog() {
        RecordTransactionDialog d = new RecordTransactionDialog(getParentFrame(), this.presenter.getController(), this.walletController, this.categoryController);
        d.setVisible(true); if (d.isSuccess()) reloadTransactions();
    }
    @Override public void openUpdateDialog(TransactionItemViewModel txn) {
        if (txn == null) return;
        TransactionResponseDTO dto = findTransactionDto(txn.id()) != null ? findTransactionDto(txn.id()) : getSelectedTransaction();
        if (dto == null) return;
        UpdateTransactionDialog d = new UpdateTransactionDialog(getParentFrame(), this.presenter.getController(), this.walletController, this.categoryController, dto);
        d.setVisible(true); if (d.isSuccess()) reloadTransactions();
    }
    // endregion

    // region Data
    @Override public void renderTransactions(List<TransactionItemViewModel> txns, String summaryText, String pageInfoText) {
        this.transactionViewModels = (txns != null) ? txns : Collections.emptyList();
        this.tableModel.setTransactions(!this.presenter.getCurrentTransactions().isEmpty() ? this.presenter.getCurrentTransactions() : this.transactionViewModels.stream().map(this::toTransactionDto).toList());
        this.summaryLabel.setText(summaryText); this.pageInfoLabel.setText(pageInfoText);
        this.prevButton.setEnabled(this.presenter.getCurrentPage() > 1);
        this.nextButton.setEnabled(this.presenter.getCurrentPage() < this.presenter.getTotalPages());
        if (this.onTransactionChangedListener != null) this.onTransactionChangedListener.run();
    }
    public void reloadTransactions() { this.presenter.loadTransactions(this.filterToolbar.buildFilterRequest(this.presenter.getCurrentPage(), 20)); }
    private void loadWallets() {
        try { WalletsSummaryResponseDTO s = this.walletController.loadWalletsSummary(); if (s != null) this.filterToolbar.populateWallets(s.getWallets()); } catch (Exception ignored) {}
    }
    @Override public void setSelectedTypeFilter(String type) { this.filterToolbar.setSelectedTypeFilter(type); }
    // endregion

    // region Getters
    public TransactionPresenter getPresenter() { return this.presenter; } public TransactionTableModel getTableModel() { return this.tableModel; }
    public JTable getTable() { return this.table; } public String getSummaryText() { return this.summaryLabel.getText(); }
    public String getPageInfoText() { return this.pageInfoLabel.getText(); } public int getPageNumber() { return this.presenter.getCurrentPage(); }
    public int getTotalPages() { return this.presenter.getTotalPages(); } public String getSelectedTypeFilter() { return this.filterToolbar.getSelectedTypeFilter(); }
    public TransactionItemViewModel getSelectedViewModel() { int r = this.table.getSelectedRow(); return (r >= 0 && r < this.transactionViewModels.size()) ? this.transactionViewModels.get(r) : null; }
    public TransactionResponseDTO getSelectedTransaction() { int r = this.table.getSelectedRow(); if (r < 0) { showWarning("Vui lòng chọn một giao dịch trong bảng để thao tác."); return null; } return this.tableModel.getTransactionAt(r); }
    public void setOnTransactionChangedListener(Runnable l) { this.onTransactionChangedListener = l; } public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = c; }
    public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = c; } public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = c; }
    public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = c; }
    // endregion

    // region Helpers
    @Override public void showMessage(String m) { if (messageConsumer != null) messageConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    @Override public void showWarning(String m) { if (warningConsumer != null) warningConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE); }
    @Override public void showError(String m) { if (errorConsumer != null) errorConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    @Override public boolean confirmAction(String m) { return (confirmConsumer != null) ? confirmConsumer.test(m) : JOptionPane.showConfirmDialog(this, m, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION; }
    private JFrame getParentFrame() { Component c = SwingUtilities.getWindowAncestor(this); return (c instanceof JFrame f) ? f : null; }
    private TransactionResponseDTO findTransactionDto(UUID id) { if (id == null) return null; for (TransactionResponseDTO t : this.tableModel.getTransactions()) if (id.equals(t.getTransactionId())) return t; return null; }
    private JButton createBtn(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt); b.setFont(ViewConstants.FONT_BODY); b.setBackground(bg); b.setForeground(fg); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER), BorderFactory.createEmptyBorder(6, 12, 6, 12))); return b;
    }
    private TransactionResponseDTO toTransactionDto(TransactionItemViewModel vm) {
        LocalDate d = null;
        if (vm.dateFormatted() != null && !vm.dateFormatted().isBlank() && !"-".equals(vm.dateFormatted())) {
            try { d = LocalDate.parse(vm.dateFormatted(), DateTimeFormatter.ofPattern("dd/MM/yyyy")); } catch (Exception ignored) { d = LocalDate.now(); }
        }
        BigDecimal amt = BigDecimal.ZERO;
        if (vm.formattedAmount() != null) { try { amt = new BigDecimal(vm.formattedAmount().replace(" VNĐ", "").replace(",", "").trim()); } catch (Exception ignored) {} }
        BigDecimal bal = BigDecimal.ZERO;
        if (vm.balanceAfterFormatted() != null) { try { bal = new BigDecimal(vm.balanceAfterFormatted().replace(" VNĐ", "").replace(",", "").trim()); } catch (Exception ignored) {} }
        return new TransactionResponseDTO(vm.id(), null, vm.walletName(), null, vm.categoryName(), amt, vm.isIncome() ? "INCOME" : "EXPENSE", d, vm.note(), bal, null);
    }
    // endregion
}
