package com.pfms.presentation.view.dashboard;

import com.pfms.presentation.controller.ReportGUIController;
import com.pfms.presentation.controller.TransactionGUIController;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.view.common.ViewConstants;
import com.pfms.presentation.view.component.RoundedCardPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Bảng Điều Khiển Tổng Quan (DashboardPanel - CARD_DASHBOARD).
 * Trình diễn 4 thẻ KPI Card (Tổng số dư, Thu nhập, Chi tiêu, Tiết kiệm ròng)
 * và Bảng Giao Dịch Gần Nhất theo chuẩn giao diện Luxury Black & Gold.
 */
public class DashboardPanel extends JPanel {

    // region State
    private final WalletGUIController walletController;
    private final TransactionGUIController transactionController;
    private final ReportGUIController reportController;

    private final DecimalFormat currencyFormatter;
    private final DateTimeFormatter dateFormatter;

    // Header Components
    private JLabel headerTitleLabel;
    private JLabel cycleSubtitleLabel;

    // KPI Cards - Value Labels
    private JLabel totalBalanceValueLabel;
    private JLabel incomeValueLabel;
    private JLabel expenseValueLabel;
    private JLabel savingsRateValueLabel;

    // KPI Cards - Subtext Labels
    private JLabel totalBalanceSubtextLabel;
    private JLabel incomeSubtextLabel;
    private JLabel expenseSubtextLabel;
    private JLabel savingsRateSubtextLabel;

    // Recent Transactions Table
    private RecentTransactionsTableModel tableModel;
    private JTable recentTable;
    // endregion

    // region Constructors
    public DashboardPanel(
            WalletGUIController walletController,
            TransactionGUIController transactionController,
            ReportGUIController reportController) {

        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        this.transactionController = Objects.requireNonNull(transactionController, "TransactionGUIController không được để null");
        this.reportController = Objects.requireNonNull(reportController, "ReportGUIController không được để null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        initializeUI();
        reloadDashboard();
    }
    // endregion

    // region UI Initialization
    private void initializeUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(ViewConstants.COLOR_CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        topSection.add(headerPanel);
        topSection.add(Box.createVerticalStrut(20));

        // KPI Cards Row (1x4)
        JPanel kpiCardsPanel = createKpiCardsPanel();
        topSection.add(kpiCardsPanel);

        add(topSection, BorderLayout.NORTH);

        // Center: Recent Transactions Table
        JPanel tableSection = createTableSection();
        add(tableSection, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        JPanel titlesContainer = new JPanel();
        titlesContainer.setLayout(new BoxLayout(titlesContainer, BoxLayout.Y_AXIS));
        titlesContainer.setBackground(ViewConstants.COLOR_CONTENT_BG);

        this.headerTitleLabel = new JLabel("Bảng Điều Khiển Tổng Quan");
        this.headerTitleLabel.setFont(ViewConstants.FONT_PANEL_HEADER);
        this.headerTitleLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.headerTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlesContainer.add(this.headerTitleLabel);

        titlesContainer.add(Box.createVerticalStrut(4));

        String currentCycle = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        this.cycleSubtitleLabel = new JLabel("Chu kỳ tài chính: Tháng " + currentCycle);
        this.cycleSubtitleLabel.setFont(ViewConstants.FONT_SUBTITLE);
        this.cycleSubtitleLabel.setForeground(ViewConstants.COLOR_GOLD_LIGHT);
        this.cycleSubtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlesContainer.add(this.cycleSubtitleLabel);

        headerPanel.add(titlesContainer, BorderLayout.WEST);
        // Lưu ý: Tuyệt đối KHÔNG có nút "Ghi giao dịch nhanh" theo yêu cầu người dùng
        return headerPanel;
    }

    private JPanel createKpiCardsPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        gridPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Thẻ 1: Tổng số dư khả dụng
        this.totalBalanceValueLabel = new JLabel("0 VNĐ");
        this.totalBalanceValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.totalBalanceValueLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.totalBalanceSubtextLabel = new JLabel("0 ví đang hoạt động");
        this.totalBalanceSubtextLabel.setFont(ViewConstants.FONT_STATUS);
        this.totalBalanceSubtextLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        JPanel card1 = buildKpiCard("TỔNG SỐ DƯ KHẢ DỤNG", this.totalBalanceValueLabel, this.totalBalanceSubtextLabel);

        // Thẻ 2: Thu nhập trong tháng
        this.incomeValueLabel = new JLabel("0 VNĐ");
        this.incomeValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.incomeValueLabel.setForeground(ViewConstants.COLOR_INCOME);
        this.incomeSubtextLabel = new JLabel("0 giao dịch thu");
        this.incomeSubtextLabel.setFont(ViewConstants.FONT_STATUS);
        this.incomeSubtextLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        JPanel card2 = buildKpiCard("THU NHẬP TRONG THÁNG", this.incomeValueLabel, this.incomeSubtextLabel);

        // Thẻ 3: Chi tiêu trong tháng
        this.expenseValueLabel = new JLabel("0 VNĐ");
        this.expenseValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.expenseValueLabel.setForeground(ViewConstants.COLOR_EXPENSE);
        this.expenseSubtextLabel = new JLabel("0.0% so với thu nhập");
        this.expenseSubtextLabel.setFont(ViewConstants.FONT_STATUS);
        this.expenseSubtextLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        JPanel card3 = buildKpiCard("CHI TIÊU TRONG THÁNG", this.expenseValueLabel, this.expenseSubtextLabel);

        // Thẻ 4: Tỷ lệ tiết kiệm ròng
        this.savingsRateValueLabel = new JLabel("0.0%");
        this.savingsRateValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.savingsRateValueLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.savingsRateSubtextLabel = new JLabel("Mục tiêu tích lũy > 30%");
        this.savingsRateSubtextLabel.setFont(ViewConstants.FONT_STATUS);
        this.savingsRateSubtextLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        JPanel card4 = buildKpiCard("TỶ LỆ TIẾT KIỆM RÒNG", this.savingsRateValueLabel, this.savingsRateSubtextLabel);

        gridPanel.add(card1);
        gridPanel.add(card2);
        gridPanel.add(card3);
        gridPanel.add(card4);

        return gridPanel;
    }

    private JPanel buildKpiCard(String title, JLabel valueLabel, JLabel subtextLabel) {
        RoundedCardPanel card = new RoundedCardPanel(10, ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subtextLabel);

        return card;
    }

    private JPanel createTableSection() {
        JPanel tableSection = new JPanel(new BorderLayout(0, 10));
        tableSection.setBackground(ViewConstants.COLOR_CONTENT_BG);

        JLabel sectionTitle = new JLabel("GIAO DỊCH GẦN NHẤT");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sectionTitle.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        tableSection.add(sectionTitle, BorderLayout.NORTH);

        this.tableModel = new RecentTransactionsTableModel(this.currencyFormatter, this.dateFormatter);
        this.recentTable = new JTable(this.tableModel);
        this.recentTable.setRowHeight(30);
        this.recentTable.setBackground(ViewConstants.COLOR_CARD_BG);
        this.recentTable.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.recentTable.setGridColor(ViewConstants.COLOR_BORDER);
        this.recentTable.setShowGrid(true);
        this.recentTable.setSelectionBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        this.recentTable.setSelectionForeground(ViewConstants.COLOR_TEXT_ACTIVE);
        this.recentTable.setFont(ViewConstants.FONT_BODY);

        // Header Styling
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
                if (col == 4) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (col == 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return this;
            }
        };
        this.recentTable.getTableHeader().setDefaultRenderer(headerRenderer);
        this.recentTable.getTableHeader().setReorderingAllowed(false);

        // Default cell renderer for text columns
        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(col == 0 ? ViewConstants.COLOR_TEXT_MUTED : ViewConstants.COLOR_TEXT_MAIN);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (col == 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return this;
            }
        };

        for (int i = 0; i < 4; i++) {
            this.recentTable.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }

        // Amount Column Renderer (col 4)
        this.recentTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    TransactionResponseDTO txn = tableModel.getTransactionAt(row);
                    if (txn != null) {
                        String type = txn.getTransactionType();
                        if ("INCOME".equalsIgnoreCase(type)) {
                            setForeground(ViewConstants.COLOR_INCOME);
                        } else if ("EXPENSE".equalsIgnoreCase(type)) {
                            setForeground(ViewConstants.COLOR_EXPENSE);
                        } else {
                            setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
                        }
                    } else {
                        String valStr = (value != null) ? value.toString() : "";
                        if (valStr.startsWith("+")) {
                            setForeground(ViewConstants.COLOR_INCOME);
                        } else if (valStr.startsWith("-")) {
                            setForeground(ViewConstants.COLOR_EXPENSE);
                        } else {
                            setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
                        }
                    }
                }
                return this;
            }
        });

        // Set column widths
        this.recentTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        this.recentTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        this.recentTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        this.recentTable.getColumnModel().getColumn(3).setPreferredWidth(220);
        this.recentTable.getColumnModel().getColumn(4).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(this.recentTable);
        scrollPane.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        scrollPane.setBackground(ViewConstants.COLOR_CARD_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1));

        tableSection.add(scrollPane, BorderLayout.CENTER);
        return tableSection;
    }
    // endregion

    // region Data Reload
    public void reloadDashboard() {
        // Cập nhật Thẻ 1: Tổng số dư khả dụng
        try {
            WalletsSummaryResponseDTO summary = this.walletController.getWalletsSummary();
            BigDecimal totalBalance = (summary != null) ? summary.getTotalBalance() : BigDecimal.ZERO;
            String currency = (summary != null && summary.getCurrency() != null && !summary.getCurrency().isBlank())
                    ? summary.getCurrency() : ViewConstants.CURRENCY_DEFAULT;

            this.totalBalanceValueLabel.setText(this.currencyFormatter.format(totalBalance) + " " + currency);

            long activeWallets = 0;
            if (summary != null && summary.getWallets() != null) {
                activeWallets = summary.getWallets().stream()
                        .filter(w -> "ACTIVE".equalsIgnoreCase(w.getStatus()))
                        .count();
            }
            this.totalBalanceSubtextLabel.setText(activeWallets + " ví đang hoạt động");
        } catch (Exception ignored) {
            this.totalBalanceValueLabel.setText("0 " + ViewConstants.CURRENCY_DEFAULT);
            this.totalBalanceSubtextLabel.setText("0 ví đang hoạt động");
        }

        // Cập nhật Thẻ 2, 3, 4 & Bảng giao dịch gần nhất
        try {
            YearMonth currentMonth = YearMonth.now();
            FilterTransactionsRequestDTO monthFilter = new FilterTransactionsRequestDTO(
                    currentMonth.atDay(1), currentMonth.atEndOfMonth(), null, null, null, "", 1, 100);

            TransactionListResponseDTO txnResponse = this.transactionController.filterTransactions(monthFilter);
            BigDecimal income = (txnResponse != null && txnResponse.getTotalIncome() != null)
                    ? txnResponse.getTotalIncome() : BigDecimal.ZERO;
            BigDecimal expense = (txnResponse != null && txnResponse.getTotalExpense() != null)
                    ? txnResponse.getTotalExpense() : BigDecimal.ZERO;
            BigDecimal netCashFlow = (txnResponse != null && txnResponse.getNetCashFlow() != null)
                    ? txnResponse.getNetCashFlow() : income.subtract(expense);

            // Thẻ 2: Thu nhập
            this.incomeValueLabel.setText(this.currencyFormatter.format(income) + " " + ViewConstants.CURRENCY_DEFAULT);
            long incomeCount = 0;
            if (txnResponse != null && txnResponse.getItems() != null) {
                incomeCount = txnResponse.getItems().stream()
                        .filter(t -> "INCOME".equalsIgnoreCase(t.getTransactionType()))
                        .count();
            }
            this.incomeSubtextLabel.setText(incomeCount + " giao dịch thu");

            // Thẻ 3: Chi tiêu
            this.expenseValueLabel.setText(this.currencyFormatter.format(expense) + " " + ViewConstants.CURRENCY_DEFAULT);
            if (income.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal expRatio = expense.multiply(BigDecimal.valueOf(100))
                        .divide(income, 1, RoundingMode.HALF_UP);
                this.expenseSubtextLabel.setText(expRatio.toPlainString() + "% so với thu nhập");
            } else {
                this.expenseSubtextLabel.setText("0.0% so với thu nhập");
            }

            // Thẻ 4: Tỷ lệ tiết kiệm ròng
            if (income.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal savingsRate = netCashFlow.multiply(BigDecimal.valueOf(100))
                        .divide(income, 1, RoundingMode.HALF_UP);
                this.savingsRateValueLabel.setText(savingsRate.toPlainString() + "%");
            } else {
                this.savingsRateValueLabel.setText("0.0%");
            }
            this.savingsRateSubtextLabel.setText("Mục tiêu tích lũy > 30%");

            // Đổ 10 giao dịch gần nhất vào bảng
            List<TransactionResponseDTO> recentList = Collections.emptyList();
            if (txnResponse != null && txnResponse.getItems() != null) {
                recentList = txnResponse.getItems().stream().limit(10).toList();
            }
            this.tableModel.setTransactions(recentList);

        } catch (Exception ignored) {
            this.incomeValueLabel.setText("0 " + ViewConstants.CURRENCY_DEFAULT);
            this.incomeSubtextLabel.setText("0 giao dịch thu");
            this.expenseValueLabel.setText("0 " + ViewConstants.CURRENCY_DEFAULT);
            this.expenseSubtextLabel.setText("0.0% so với thu nhập");
            this.savingsRateValueLabel.setText("0.0%");
            this.savingsRateSubtextLabel.setText("Mục tiêu tích lũy > 30%");
            this.tableModel.clear();
        }
    }
    // endregion

    // region Getters for Testing
    public JLabel getHeaderTitleLabel() {
        return this.headerTitleLabel;
    }

    public JLabel getCycleSubtitleLabel() {
        return this.cycleSubtitleLabel;
    }

    public JLabel getTotalBalanceValueLabel() {
        return this.totalBalanceValueLabel;
    }

    public JLabel getTotalBalanceSubtextLabel() {
        return this.totalBalanceSubtextLabel;
    }

    public JLabel getIncomeValueLabel() {
        return this.incomeValueLabel;
    }

    public JLabel getIncomeSubtextLabel() {
        return this.incomeSubtextLabel;
    }

    public JLabel getExpenseValueLabel() {
        return this.expenseValueLabel;
    }

    public JLabel getExpenseSubtextLabel() {
        return this.expenseSubtextLabel;
    }

    public JLabel getSavingsRateValueLabel() {
        return this.savingsRateValueLabel;
    }

    public JLabel getSavingsRateSubtextLabel() {
        return this.savingsRateSubtextLabel;
    }

    public JTable getRecentTable() {
        return this.recentTable;
    }
    // endregion

    // region Inner Table Model
    private static class RecentTransactionsTableModel extends AbstractTableModel {

        private static final String[] COLUMN_NAMES = {
                "THỜI GIAN", "VÍ TÀI CHÍNH", "DANH MỤC", "GHI CHÚ", "SỐ TIỀN (VNĐ)"
        };

        private final List<TransactionResponseDTO> transactions = new ArrayList<>();
        private final DecimalFormat currencyFormatter;
        private final DateTimeFormatter dateFormatter;

        public RecentTransactionsTableModel(DecimalFormat currencyFormatter, DateTimeFormatter dateFormatter) {
            this.currencyFormatter = currencyFormatter;
            this.dateFormatter = dateFormatter;
        }

        public void setTransactions(List<TransactionResponseDTO> list) {
            this.transactions.clear();
            if (list != null) {
                this.transactions.addAll(list);
            }
            fireTableDataChanged();
        }

        public void clear() {
            this.transactions.clear();
            fireTableDataChanged();
        }

        public TransactionResponseDTO getTransactionAt(int rowIndex) {
            if (rowIndex < 0 || rowIndex >= this.transactions.size()) {
                return null;
            }
            return this.transactions.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return this.transactions.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public String getColumnName(int column) {
            if (column >= 0 && column < COLUMN_NAMES.length) {
                return COLUMN_NAMES[column];
            }
            return "";
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= this.transactions.size()) {
                return null;
            }
            TransactionResponseDTO txn = this.transactions.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> (txn.getTransactionDate() != null) ? this.dateFormatter.format(txn.getTransactionDate()) : "-";
                case 1 -> (txn.getWalletName() != null) ? txn.getWalletName() : "-";
                case 2 -> (txn.getCategoryName() != null) ? txn.getCategoryName() : "-";
                case 3 -> (txn.getNote() != null) ? txn.getNote() : "";
                case 4 -> {
                    String formatted = this.currencyFormatter.format(txn.getAmount());
                    String type = txn.getTransactionType();
                    if ("INCOME".equalsIgnoreCase(type)) {
                        yield "+" + formatted;
                    } else if ("EXPENSE".equalsIgnoreCase(type)) {
                        yield "-" + formatted;
                    } else {
                        yield formatted;
                    }
                }
                default -> null;
            };
        }
    }
    // endregion
}
