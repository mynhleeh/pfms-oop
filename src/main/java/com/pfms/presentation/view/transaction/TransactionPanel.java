package com.pfms.presentation.view.transaction;

import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.controller.TransactionGUIController;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.util.InputValidator;
import com.pfms.presentation.view.common.ViewConstants;
import com.pfms.presentation.view.transaction.dialog.RecordTransactionDialog;
import com.pfms.presentation.view.transaction.dialog.UpdateTransactionDialog;
import com.pfms.presentation.view.transaction.model.TransactionTableModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển trung tâm của Phân hệ Quản lý Giao dịch Thu & Chi (CARD_TRANSACTIONS).
 */
public class TransactionPanel extends JPanel {

    private final TransactionGUIController transactionController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;

    private final TransactionTableModel tableModel;
    private final JTable table;

    private final JComboBox<String> typeFilterBox;
    private final JComboBox<Object> walletFilterBox;
    private final JTextField keywordField;
    private final JTextField startDateField;
    private final JTextField endDateField;

    private final JLabel pageInfoLabel;
    private final JButton prevButton;
    private final JButton nextButton;
    private final JLabel summaryLabel;

    private final DecimalFormat currencyFormatter;
    private Runnable onTransactionChangedListener;

    private int currentPage = 1;
    private int pageSize = 20;
    private int totalPages = 1;
    private long totalRecords = 0;

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

    public TransactionPanel(
            TransactionGUIController transactionController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {

        this.transactionController = Objects.requireNonNull(transactionController, "TransactionGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");

        this.tableModel = new TransactionTableModel();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);

        setLayout(new BorderLayout());
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Header & Toolbars
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(ViewConstants.COLOR_CONTENT_BG);
        topContainer.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

        JLabel titleLabel = new JLabel("Ghi nhận Thu & Chi");
        titleLabel.setFont(ViewConstants.FONT_PANEL_HEADER);
        titleLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topContainer.add(titleLabel);
        topContainer.add(Box.createVerticalStrut(10));

        // Filter Bar (Row 1)
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setBackground(ViewConstants.COLOR_CONTENT_BG);
        filterBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel typeLabel = new JLabel("Loại:");
        typeLabel.setFont(ViewConstants.FONT_BODY);
        this.typeFilterBox = new JComboBox<>(new String[]{"Tất cả", "INCOME", "EXPENSE"});
        this.typeFilterBox.setFont(ViewConstants.FONT_BODY);
        this.typeFilterBox.addActionListener(e -> {
            this.currentPage = 1;
            reloadTransactions();
        });

        JLabel walletLabel = new JLabel("Ví:");
        walletLabel.setFont(ViewConstants.FONT_BODY);
        this.walletFilterBox = new JComboBox<>();
        this.walletFilterBox.setFont(ViewConstants.FONT_BODY);
        populateWalletFilter();
        this.walletFilterBox.addActionListener(e -> {
            this.currentPage = 1;
            reloadTransactions();
        });

        JLabel fromDateLabel = new JLabel("Từ:");
        fromDateLabel.setFont(ViewConstants.FONT_BODY);
        this.startDateField = new JTextField(8);
        this.startDateField.setFont(ViewConstants.FONT_BODY);

        JLabel toDateLabel = new JLabel("Đến:");
        toDateLabel.setFont(ViewConstants.FONT_BODY);
        this.endDateField = new JTextField(8);
        this.endDateField.setFont(ViewConstants.FONT_BODY);

        JLabel searchLabel = new JLabel("Tìm:");
        searchLabel.setFont(ViewConstants.FONT_BODY);
        this.keywordField = new JTextField(10);
        this.keywordField.setFont(ViewConstants.FONT_BODY);

        JButton filterButton = createToolButton("Lọc", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        filterButton.addActionListener(e -> {
            this.currentPage = 1;
            reloadTransactions();
        });

        JButton resetButton = createToolButton("Đặt lại", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MUTED);
        resetButton.addActionListener(e -> resetFilters());

        filterBar.add(typeLabel);
        filterBar.add(this.typeFilterBox);
        filterBar.add(walletLabel);
        filterBar.add(this.walletFilterBox);
        filterBar.add(fromDateLabel);
        filterBar.add(this.startDateField);
        filterBar.add(toDateLabel);
        filterBar.add(this.endDateField);
        filterBar.add(searchLabel);
        filterBar.add(this.keywordField);
        filterBar.add(filterButton);
        filterBar.add(resetButton);

        topContainer.add(filterBar);
        topContainer.add(Box.createVerticalStrut(8));

        // Action Toolbar (Row 2)
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionBar.setBackground(ViewConstants.COLOR_CONTENT_BG);
        actionBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addButton = createToolButton("Thêm giao dịch", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE);
        addButton.addActionListener(e -> handleAddTransaction());

        JButton editButton = createToolButton("Sửa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        editButton.addActionListener(e -> handleEditTransaction());

        JButton deleteButton = createToolButton("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE);
        deleteButton.addActionListener(e -> handleDeleteTransaction());

        JButton refreshButton = createToolButton("Làm mới", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        refreshButton.addActionListener(e -> reloadTransactions());

        actionBar.add(addButton);
        actionBar.add(editButton);
        actionBar.add(deleteButton);
        actionBar.add(refreshButton);

        topContainer.add(actionBar);
        add(topContainer, BorderLayout.NORTH);

        // Center Table
        this.table = new JTable(this.tableModel);
        this.table.setFont(ViewConstants.FONT_BODY);
        this.table.setRowHeight(28);
        this.table.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        configureTableRenderers();

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 8, 20),
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Controls (Pagination & Summary)
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setBackground(ViewConstants.COLOR_STATUS_BG);
        bottomContainer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        // Pagination Bar
        JPanel paginationBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        paginationBar.setBackground(ViewConstants.COLOR_STATUS_BG);

        this.prevButton = new JButton("< Trước");
        this.prevButton.setFont(ViewConstants.FONT_BODY);
        this.prevButton.addActionListener(e -> {
            if (this.currentPage > 1) {
                this.currentPage--;
                reloadTransactions();
            }
        });

        this.pageInfoLabel = new JLabel("Trang 1 / 1 (Tổng 0 giao dịch)");
        this.pageInfoLabel.setFont(ViewConstants.FONT_BODY);

        this.nextButton = new JButton("Sau >");
        this.nextButton.setFont(ViewConstants.FONT_BODY);
        this.nextButton.addActionListener(e -> {
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
                reloadTransactions();
            }
        });

        paginationBar.add(this.prevButton);
        paginationBar.add(this.pageInfoLabel);
        paginationBar.add(this.nextButton);

        // Summary Bar
        JPanel summaryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        summaryBar.setBackground(ViewConstants.COLOR_STATUS_BG);
        summaryBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        this.summaryLabel = new JLabel("Tổng thu: 0 VNĐ | Tổng chi: 0 VNĐ | Dòng tiền thuần: 0 VNĐ");
        this.summaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        summaryBar.add(this.summaryLabel);

        bottomContainer.add(paginationBar);
        bottomContainer.add(summaryBar);

        add(bottomContainer, BorderLayout.SOUTH);

        reloadTransactions();
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

    private void populateWalletFilter() {
        this.walletFilterBox.removeAllItems();
        this.walletFilterBox.addItem("Tất cả ví");

        WalletsSummaryResponseDTO summary = this.walletController.loadWalletsSummary();
        if (summary != null && summary.getWallets() != null) {
            for (WalletResponseDTO w : summary.getWallets()) {
                this.walletFilterBox.addItem(w);
            }
        }

        this.walletFilterBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WalletResponseDTO w) {
                    setText(w.getName());
                } else if (value != null) {
                    setText(value.toString());
                }
                return this;
            }
        });
    }

    private void configureTableRenderers() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        this.table.getColumnModel().getColumn(0).setPreferredWidth(40);
        this.table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(1).setPreferredWidth(90);
        this.table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(2).setPreferredWidth(90);
        this.table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        this.table.getColumnModel().getColumn(3).setPreferredWidth(140);

        this.table.getColumnModel().getColumn(4).setPreferredWidth(120);
        this.table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                TransactionResponseDTO txn = tableModel.getTransactionAt(row);
                if (txn != null && !isSelected) {
                    if ("INCOME".equalsIgnoreCase(txn.getTransactionType())) {
                        setForeground(ViewConstants.COLOR_INCOME);
                    } else if ("EXPENSE".equalsIgnoreCase(txn.getTransactionType())) {
                        setForeground(ViewConstants.COLOR_EXPENSE);
                    } else {
                        setForeground(ViewConstants.COLOR_TEXT_MAIN);
                    }
                }
                return this;
            }
        });

        this.table.getColumnModel().getColumn(5).setPreferredWidth(120);

        this.table.getColumnModel().getColumn(6).setPreferredWidth(90);
        this.table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);

        this.table.getColumnModel().getColumn(7).setPreferredWidth(180);
    }

    public void reloadTransactions() {
        try {
            FilterTransactionsRequestDTO filter = buildFilterRequest();
            TransactionListResponseDTO response = this.transactionController.loadTransactions(filter);

            if (response != null) {
                this.tableModel.setTransactions(response.getItems());
                this.currentPage = response.getPageNumber();
                this.pageSize = response.getPageSize();
                this.totalPages = Math.max(1, response.getTotalPages());
                this.totalRecords = response.getTotalRecords();

                updatePaginationControls();
                updateSummary(response.getTotalIncome(), response.getTotalExpense(), response.getNetCashFlow());
            } else {
                this.tableModel.clear();
                updatePaginationControls();
                updateSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (Exception ex) {
            this.tableModel.clear();
            if (this.errorConsumer != null) {
                this.errorConsumer.accept("Lỗi tải danh sách giao dịch: " + ex.getMessage());
            }
        }

        if (this.onTransactionChangedListener != null) {
            this.onTransactionChangedListener.run();
        }
    }

    private FilterTransactionsRequestDTO buildFilterRequest() {
        String typeFilter = (String) this.typeFilterBox.getSelectedItem();
        String type = ("Tất cả".equalsIgnoreCase(typeFilter) || typeFilter == null) ? null : typeFilter;

        Set<UUID> walletIds = null;
        Object selectedWallet = this.walletFilterBox.getSelectedItem();
        if (selectedWallet instanceof WalletResponseDTO w) {
            walletIds = new HashSet<>(Collections.singleton(w.getWalletId()));
        }

        LocalDate start = InputValidator.parseOptionalDate(this.startDateField.getText(), "Ngày bắt đầu");
        LocalDate end = InputValidator.parseOptionalDate(this.endDateField.getText(), "Ngày kết thúc");
        String kw = this.keywordField.getText();

        return new FilterTransactionsRequestDTO(start, end, walletIds, type, null, kw, this.currentPage, this.pageSize);
    }

    private void updatePaginationControls() {
        this.prevButton.setEnabled(this.currentPage > 1);
        this.nextButton.setEnabled(this.currentPage < this.totalPages);
        this.pageInfoLabel.setText("Trang " + this.currentPage + " / " + this.totalPages + " (Tổng " + this.totalRecords + " giao dịch)");
    }

    private void updateSummary(BigDecimal income, BigDecimal expense, BigDecimal net) {
        BigDecimal safeIncome = income != null ? income : BigDecimal.ZERO;
        BigDecimal safeExpense = expense != null ? expense : BigDecimal.ZERO;
        BigDecimal safeNet = net != null ? net : BigDecimal.ZERO;

        this.summaryLabel.setText("Tổng thu: " + this.currencyFormatter.format(safeIncome) + " VNĐ" +
                " | Tổng chi: " + this.currencyFormatter.format(safeExpense) + " VNĐ" +
                " | Dòng tiền thuần: " + this.currencyFormatter.format(safeNet) + " VNĐ");
    }

    private void resetFilters() {
        this.typeFilterBox.setSelectedIndex(0);
        this.walletFilterBox.setSelectedIndex(0);
        this.startDateField.setText("");
        this.endDateField.setText("");
        this.keywordField.setText("");
        this.currentPage = 1;
        reloadTransactions();
    }

    private JFrame getParentFrame() {
        Component c = SwingUtilities.getWindowAncestor(this);
        return (c instanceof JFrame frame) ? frame : null;
    }

    public TransactionResponseDTO getSelectedTransaction() {
        int row = this.table.getSelectedRow();
        if (row < 0) {
            if (this.warningConsumer != null) {
                this.warningConsumer.accept("Vui lòng chọn một giao dịch trong bảng để thao tác.");
            }
            return null;
        }
        return this.tableModel.getTransactionAt(row);
    }

    public void handleAddTransaction() {
        RecordTransactionDialog dialog = new RecordTransactionDialog(
                getParentFrame(), this.transactionController, this.walletController, this.categoryController);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            reloadTransactions();
        }
    }

    public void handleEditTransaction() {
        TransactionResponseDTO selected = getSelectedTransaction();
        if (selected == null) return;

        UpdateTransactionDialog dialog = new UpdateTransactionDialog(
                getParentFrame(), this.transactionController, this.walletController, this.categoryController, selected);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            reloadTransactions();
        }
    }

    public void handleDeleteTransaction() {
        TransactionResponseDTO selected = getSelectedTransaction();
        if (selected == null) return;

        boolean confirm = false;
        if (this.confirmConsumer != null) {
            confirm = this.confirmConsumer.test(
                    "Bạn có chắc chắn muốn xóa giao dịch này không?\nThao tác này sẽ hoàn nguyên số dư ví liên quan.");
        }

        if (confirm) {
            try {
                this.transactionController.handleDeleteTransaction(selected.getTransactionId());
                if (this.messageConsumer != null) {
                    this.messageConsumer.accept("Đã xóa giao dịch thành công.");
                }
                reloadTransactions();
            } catch (Exception ex) {
                if (this.errorConsumer != null) {
                    this.errorConsumer.accept(ex.getMessage());
                }
            }
        }
    }

    public void setOnTransactionChangedListener(Runnable listener) {
        this.onTransactionChangedListener = listener;
    }

    public TransactionTableModel getTableModel() {
        return this.tableModel;
    }

    public JTable getTable() {
        return this.table;
    }

    public String getSummaryText() {
        return this.summaryLabel.getText();
    }

    public String getPageInfoText() {
        return this.pageInfoLabel.getText();
    }

    public int getPageNumber() {
        return this.currentPage;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public String getSelectedTypeFilter() {
        return (String) this.typeFilterBox.getSelectedItem();
    }

    public void setSelectedTypeFilter(String type) {
        if (type == null) {
            this.typeFilterBox.setSelectedIndex(0);
        } else {
            this.typeFilterBox.setSelectedItem(type);
        }
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
