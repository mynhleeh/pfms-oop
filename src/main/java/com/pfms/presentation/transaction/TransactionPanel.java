package com.pfms.presentation.transaction;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
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
import javax.swing.table.TableCellRenderer;
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
 * Đã tối ưu hóa giao diện Luxury Rounded, bảng dữ liệu 8 cột có phân nhóm và cho phép ghi chú xuống dòng.
 */
public class TransactionPanel extends JPanel implements TransactionView {
    private static final long serialVersionUID = 1L;

    // region Fields
    private final TransactionPresenter presenter;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;
    private final TransactionFilterToolbar filterToolbar;
    private final TransactionTableModel tableModel = new TransactionTableModel();
    private final JTable table = new JTable(this.tableModel);
    private final JLabel pageInfoLabel = new JLabel("Trang 1 / 1 (Tổng 0 giao dịch)");
    private final JLabel summaryLabel = new JLabel("Tổng thu: 0 VNĐ | Tổng chi: 0 VNĐ | Dòng tiền thuần: 0 VNĐ");
    private final JLabel statIncomeLabel = new JLabel("Tổng thu: +0 VNĐ");
    private final JLabel statExpenseLabel = new JLabel("Tổng chi: -0 VNĐ");
    private final JLabel statNetLabel = new JLabel("Dòng tiền: +0 VNĐ");
    private final JLabel statCountLabel = new JLabel("0 giao dịch");
    private final JButton prevButton = new JButton("< Trước"), nextButton = new JButton("Sau >");
    private List<TransactionItemViewModel> transactionViewModels = Collections.emptyList();
    private Runnable onTransactionChangedListener;
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer;
    private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public TransactionPanel(TransactionGUIController tc, WalletGUIController wc, CategoryGUIController cc) {
        this.walletController = Objects.requireNonNull(wc, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(cc, "CategoryGUIController không được null");
        this.presenter = new TransactionPresenter(this, Objects.requireNonNull(tc, "TransactionGUIController không được null"));
        this.filterToolbar = new TransactionFilterToolbar(this::reloadTransactions);
        initUI();
        loadWallets();
        loadCategories();
        reloadTransactions();
    }
    // endregion

    // region UI
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Header & Toolbar Panel
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(ViewConstants.COLOR_CONTENT_BG);
        north.setBorder(BorderFactory.createEmptyBorder(16, 20, 10, 20));

        // Header Row: Tiêu đề bên trái, 4 thẻ KPI Stats bên phải đối xứng (chuẩn Luxury Dashboard)
        JPanel headerRow = new JPanel(new BorderLayout(16, 0));
        headerRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Ghi nhận Thu & Chi");
        title.setFont(ViewConstants.FONT_PANEL_HEADER);
        title.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        headerRow.add(title, BorderLayout.WEST);

        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        statsRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        statsRow.add(createStatPill(this.statIncomeLabel, ViewConstants.COLOR_INCOME));
        statsRow.add(createStatPill(this.statExpenseLabel, ViewConstants.COLOR_EXPENSE));
        statsRow.add(createStatPill(this.statNetLabel, ViewConstants.COLOR_PRIMARY_LIGHT));
        statsRow.add(createStatPill(this.statCountLabel, ViewConstants.COLOR_TEXT_MUTED));
        headerRow.add(statsRow, BorderLayout.EAST);

        north.add(headerRow);
        north.add(Box.createVerticalStrut(12));

        // Kết nối các nút tác vụ tích hợp trong Thẻ Bộ Lọc (Filter Card)
        this.filterToolbar.getAddButton().addActionListener(e -> handleAddTransaction());
        this.filterToolbar.getRefreshButton().addActionListener(e -> { loadWallets(); loadCategories(); reloadTransactions(); });

        north.add(this.filterToolbar);
        add(north, BorderLayout.NORTH);

        // Cấu hình bảng dữ liệu giao dịch
        configureTable();
        JScrollPane scroll = new JScrollPane(this.table);
        scroll.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        scroll.setBackground(ViewConstants.COLOR_CARD_BG);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 8, 20),
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true)));
        add(scroll, BorderLayout.CENTER);

        // Footer phân trang gọn gàng (đã loại bỏ dòng tổng thu chi ở đáy theo yêu cầu)
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        south.setBackground(ViewConstants.COLOR_STATUS_BG);
        south.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        this.prevButton.setFont(ViewConstants.FONT_BODY);
        this.nextButton.setFont(ViewConstants.FONT_BODY);
        this.pageInfoLabel.setFont(ViewConstants.FONT_BODY);
        this.prevButton.addActionListener(e -> this.presenter.prevPage());
        this.nextButton.addActionListener(e -> this.presenter.nextPage());
        south.add(this.prevButton);
        south.add(this.pageInfoLabel);
        south.add(this.nextButton);
        add(south, BorderLayout.SOUTH);
    }

    private void configureTable() {
        com.pfms.presentation.common.ViewUtils.styleTable(this.table);
        this.table.setRowHeight(36);

        int[] w = {90, 90, 120, 120, 110, 110, 90, 180, 130};
        for (int i = 0; i < w.length; i++) {
            this.table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        DefaultTableCellRenderer center = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.CENTER, ViewConstants.COLOR_TEXT_MUTED);
        DefaultTableCellRenderer left = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.LEFT, ViewConstants.COLOR_TEXT_MAIN);
        DefaultTableCellRenderer leftMuted = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.LEFT, ViewConstants.COLOR_TEXT_MUTED);

        this.table.getColumnModel().getColumn(0).setCellRenderer(center); // Ngày

        // Cột 1 - Loại: Phân biệt rõ Thu nhập (Green) và Chi tiêu (Coral)
        this.table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, s, f, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!s) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    String val = (v != null) ? v.toString() : "";
                    if ("Thu nhập".equals(val)) {
                        setForeground(ViewConstants.COLOR_INCOME);
                    } else if ("Chi tiêu".equals(val)) {
                        setForeground(ViewConstants.COLOR_EXPENSE);
                    } else {
                        setForeground(ViewConstants.COLOR_TEXT_MAIN);
                    }
                }
                return this;
            }
        });

        this.table.getColumnModel().getColumn(2).setCellRenderer(leftMuted); // Nhóm
        this.table.getColumnModel().getColumn(3).setCellRenderer(left);      // Danh mục
        this.table.getColumnModel().getColumn(5).setCellRenderer(left);      // Ví

        // Cột 4 - Số tiền: Xanh bạc hà cho thu nhập, hồng san hô cho chi tiêu
        this.table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, s, f, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!s) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    TransactionResponseDTO d = tableModel.getTransactionAt(row);
                    if (d != null) {
                        if ("INCOME".equalsIgnoreCase(d.getTransactionType())) setForeground(ViewConstants.COLOR_INCOME);
                        else if ("EXPENSE".equalsIgnoreCase(d.getTransactionType())) setForeground(ViewConstants.COLOR_EXPENSE);
                        else setForeground(ViewConstants.COLOR_TEXT_MAIN);
                    } else {
                        setForeground(ViewConstants.COLOR_TEXT_MAIN);
                    }
                }
                return this;
            }
        });

        // Cột 6 - Phụ phí: Luôn trừ tiền và hiển thị màu đỏ COLOR_EXPENSE
        this.table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, s, f, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!s) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    String val = (v != null) ? v.toString() : "";
                    if (val.startsWith("-") && !"-".equals(val.trim())) {
                        setForeground(ViewConstants.COLOR_EXPENSE);
                    } else {
                        setForeground(ViewConstants.COLOR_TEXT_DIM);
                    }
                }
                return this;
            }
        });

        // Cột 7 - Ghi chú: Cho phép tự động xuống dòng (Line wrap), các cột khác cố định 1 dòng
        this.table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            private final JTextArea textArea = new JTextArea();
            {
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setOpaque(true);
                textArea.setFont(ViewConstants.FONT_TABLE_BODY);
            }
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                textArea.setText(v != null ? v.toString() : "");
                textArea.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                if (s) {
                    textArea.setBackground(t.getSelectionBackground());
                    textArea.setForeground(t.getSelectionForeground());
                } else {
                    textArea.setBackground(ViewConstants.COLOR_CARD_BG);
                    textArea.setForeground(ViewConstants.COLOR_TEXT_MAIN);
                }
                return textArea;
            }
        });

        // Cột 8 - Thao tác: Nút Sửa và Xóa trực tiếp trên từng dòng
        this.table.getColumnModel().getColumn(8).setCellRenderer(new TableCellRenderer() {
            private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            private final JButton editBtn = createTableActionButton("Sửa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
            private final JButton delBtn = createTableActionButton("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE);
            {
                panel.setOpaque(true);
                panel.setBackground(ViewConstants.COLOR_CARD_BG);
                panel.add(editBtn);
                panel.add(delBtn);
            }
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                panel.setBackground(s ? t.getSelectionBackground() : ViewConstants.COLOR_CARD_BG);
                return panel;
            }
        });

        this.table.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            private final JButton editBtn = createTableActionButton("Sửa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
            private final JButton delBtn = createTableActionButton("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE);
            private int currentRow = -1;
            {
                panel.setOpaque(true);
                panel.setBackground(ViewConstants.COLOR_CARD_BG);
                panel.add(editBtn);
                panel.add(delBtn);

                editBtn.addActionListener(e -> {
                    fireEditingStopped();
                    if (currentRow >= 0 && currentRow < tableModel.getRowCount()) {
                        TransactionResponseDTO dto = tableModel.getTransactionAt(currentRow);
                        if (dto != null) openUpdateDialogForDto(dto);
                    }
                });

                delBtn.addActionListener(e -> {
                    fireEditingStopped();
                    if (currentRow >= 0 && currentRow < tableModel.getRowCount()) {
                        TransactionResponseDTO dto = tableModel.getTransactionAt(currentRow);
                        if (dto != null) presenter.deleteTransaction(dto.getTransactionId());
                    }
                });
            }

            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int row, int col) {
                this.currentRow = row;
                panel.setBackground(t.getSelectionBackground());
                return panel;
            }

            @Override public Object getCellEditorValue() {
                return "";
            }
        });

        this.table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    TransactionResponseDTO dto = tableModel.getTransactionAt(table.getSelectedRow());
                    if (dto != null) openUpdateDialogForDto(dto);
                }
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
        openUpdateDialogForDto(dto);
    }
    public void openUpdateDialogForDto(TransactionResponseDTO dto) {
        if (dto == null) return;
        UpdateTransactionDialog d = new UpdateTransactionDialog(getParentFrame(), this.presenter.getController(), this.walletController, this.categoryController, dto);
        d.setVisible(true); if (d.isSuccess()) reloadTransactions();
    }
    // endregion

    // region Data
    @Override public void renderTransactions(List<TransactionItemViewModel> txns, String summaryText, String pageInfoText) {
        this.transactionViewModels = (txns != null) ? txns : Collections.emptyList();
        this.tableModel.setTransactions(!this.presenter.getCurrentTransactions().isEmpty() ? this.presenter.getCurrentTransactions() : this.transactionViewModels.stream().map(this::toTransactionDto).toList());
        this.summaryLabel.setText(summaryText);
        this.pageInfoLabel.setText(pageInfoText);
        updateKPIStats(summaryText, pageInfoText);
        this.prevButton.setEnabled(this.presenter.getCurrentPage() > 1);
        this.nextButton.setEnabled(this.presenter.getCurrentPage() < this.presenter.getTotalPages());
        if (this.onTransactionChangedListener != null) this.onTransactionChangedListener.run();
    }
    public void reloadTransactions() { this.presenter.loadTransactions(this.filterToolbar.buildFilterRequest(this.presenter.getCurrentPage(), 20)); }
    private void loadWallets() {
        try { WalletsSummaryResponseDTO s = this.walletController.loadWalletsSummary(); if (s != null) this.filterToolbar.populateWallets(s.getWallets()); } catch (Exception ignored) {}
    }
    private void loadCategories() {
        try {
            List<CategoryHierarchyResponseDTO> hierarchy = this.categoryController.loadCategoryHierarchy();
            if (hierarchy != null) {
                List<CategoryResponseDTO> flat = new ArrayList<>();
                Map<UUID, String> groupMap = new HashMap<>();
                for (CategoryHierarchyResponseDTO group : hierarchy) {
                    if (group.getCategories() != null) {
                        for (CategoryResponseDTO cat : group.getCategories()) {
                            flat.add(cat);
                            String gName = (cat.getParentGroupName() != null && !cat.getParentGroupName().isBlank())
                                    ? cat.getParentGroupName()
                                    : group.getNeedGroupName();
                            groupMap.put(cat.getCategoryId(), gName);
                        }
                    }
                }
                this.tableModel.setCategoryGroupMap(groupMap);
                this.filterToolbar.populateCategories(flat);
            }
        } catch (Exception ignored) {}
    }
    @Override public void setSelectedTypeFilter(String type) { this.filterToolbar.setSelectedTypeFilter(type); }

    private void updateKPIStats(String summaryText, String pageInfoText) {
        if (summaryText != null && summaryText.contains("|")) {
            String[] parts = summaryText.split("\\|");
            if (parts.length >= 3) {
                String inc = parts[0].trim().replace("Tổng thu: ", "");
                String exp = parts[1].trim().replace("Tổng chi: ", "");
                String net = parts[2].trim().replace("Dòng tiền thuần: ", "");
                this.statIncomeLabel.setText("Tổng thu: +" + inc);
                this.statExpenseLabel.setText("Tổng chi: -" + exp);
                if (net.startsWith("-")) {
                    this.statNetLabel.setText("Dòng tiền: " + net);
                    this.statNetLabel.setForeground(ViewConstants.COLOR_EXPENSE);
                } else {
                    this.statNetLabel.setText("Dòng tiền: +" + net);
                    this.statNetLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
                }
            }
        }
        if (pageInfoText != null && pageInfoText.contains("(Tổng ")) {
            int start = pageInfoText.indexOf("(Tổng ") + 6;
            int end = pageInfoText.indexOf(")", start);
            if (start < end) {
                this.statCountLabel.setText(pageInfoText.substring(start, end).trim());
            }
        }
    }
    // endregion

    // region Getters
    public TransactionFilterToolbar getFilterToolbar() { return this.filterToolbar; }
    public TransactionPresenter getPresenter() { return this.presenter; } public TransactionTableModel getTableModel() { return this.tableModel; }
    public JTable getTable() { return this.table; } public String getSummaryText() { return this.summaryLabel.getText(); }
    public String getPageInfoText() { return this.pageInfoLabel.getText(); } public int getPageNumber() { return this.presenter.getCurrentPage(); }
    public int getTotalPages() { return this.presenter.getTotalPages(); } public String getSelectedTypeFilter() { return this.filterToolbar.getSelectedTypeFilter(); }
    public JLabel getStatIncomeLabel() { return this.statIncomeLabel; }
    public JLabel getStatExpenseLabel() { return this.statExpenseLabel; }
    public JLabel getStatNetLabel() { return this.statNetLabel; }
    public JLabel getStatCountLabel() { return this.statCountLabel; }
    public TransactionItemViewModel getSelectedViewModel() { int r = this.table.getSelectedRow(); return (r >= 0 && r < this.transactionViewModels.size()) ? this.transactionViewModels.get(r) : null; }
    public TransactionResponseDTO getSelectedTransaction() { int r = this.table.getSelectedRow(); if (r < 0) { showWarning("Vui lòng chọn một giao dịch trong bảng để thao tác."); return null; } return this.tableModel.getTransactionAt(r); }
    public void setOnTransactionChangedListener(Runnable l) { this.onTransactionChangedListener = l; } public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = c; }
    public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = c; } public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = c; }
    public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = c; }
    // endregion

    // region Helpers
    private JPanel createStatPill(JLabel label, Color accentColor) {
        label.setFont(ViewConstants.FONT_NAV_ACTIVE);
        label.setForeground(accentColor);
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        pill.setBackground(ViewConstants.COLOR_CARD_BG);
        pill.putClientProperty("FlatLaf.style", "arc: 999");
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 90), 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        pill.add(label);
        return pill;
    }
    @Override public void showMessage(String m) { if (messageConsumer != null) messageConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    @Override public void showWarning(String m) { if (warningConsumer != null) warningConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE); }
    @Override public void showError(String m) { if (errorConsumer != null) errorConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    @Override public boolean confirmAction(String m) { return (confirmConsumer != null) ? confirmConsumer.test(m) : JOptionPane.showConfirmDialog(this, m, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION; }
    private JFrame getParentFrame() { Component c = SwingUtilities.getWindowAncestor(this); return (c instanceof JFrame f) ? f : null; }
    private TransactionResponseDTO findTransactionDto(UUID id) { if (id == null) return null; for (TransactionResponseDTO t : this.tableModel.getTransactions()) if (id.equals(t.getTransactionId())) return t; return null; }
    private JButton createTableActionButton(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(ViewConstants.FONT_STATUS);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.putClientProperty("FlatLaf.style", "arc: 6");
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return b;
    }

    private TransactionResponseDTO toTransactionDto(TransactionItemViewModel vm) {
        LocalDate d = null;
        if (vm.dateFormatted() != null && !vm.dateFormatted().isBlank() && !"-".equals(vm.dateFormatted())) {
            try { d = LocalDate.parse(vm.dateFormatted(), DateTimeFormatter.ofPattern("dd/MM/yyyy")); } catch (Exception ignored) { d = LocalDate.now(); }
        }
        BigDecimal amt = BigDecimal.ZERO;
        if (vm.formattedAmount() != null) { try { amt = new BigDecimal(vm.formattedAmount().replace(" VNĐ", "").replace("+", "").replace("-", "").replace(",", "").trim()); } catch (Exception ignored) {} }
        BigDecimal bal = BigDecimal.ZERO;
        if (vm.balanceAfterFormatted() != null) { try { bal = new BigDecimal(vm.balanceAfterFormatted().replace(" VNĐ", "").replace(",", "").trim()); } catch (Exception ignored) {} }
        return new TransactionResponseDTO(vm.id(), null, vm.walletName(), null, vm.categoryName(), amt, vm.isIncome() ? "INCOME" : "EXPENSE", d, vm.note(), bal, null);
    }
    // endregion
}
