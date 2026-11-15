package com.pfms.presentation.dashboard;

import com.pfms.presentation.report.ReportGUIController;
import com.pfms.presentation.transaction.TransactionGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.dashboard.component.KpiCardsPanel;
import com.pfms.presentation.dashboard.model.RecentTransactionsTableModel;
import com.pfms.presentation.dashboard.viewmodel.DashboardKpiViewModel;
import com.pfms.presentation.dashboard.viewmodel.RecentTransactionViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng Điều Khiển Tổng Quan (DashboardPanel - Passive View).
 * Trình diễn 4 thẻ KPI Card và Bảng Giao Dịch Gần Nhất theo chuẩn MVP và Luxury Black &amp; Gold.
 */
public class DashboardPanel extends JPanel implements DashboardView {

    // region Fields
    private final DashboardPresenter presenter;
    private final ReportGUIController reportController;
    private final KpiCardsPanel kpiCardsPanel = new KpiCardsPanel();
    private final RecentTransactionsTableModel tableModel = new RecentTransactionsTableModel();
    private final JTable recentTable = new JTable(this.tableModel);
    private JLabel headerTitleLabel, cycleSubtitleLabel;
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer;
    private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public DashboardPanel(WalletGUIController walletController, TransactionGUIController transactionController, ReportGUIController reportController) {
        Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        Objects.requireNonNull(transactionController, "TransactionGUIController không được để null");
        this.reportController = Objects.requireNonNull(reportController, "ReportGUIController không được để null");
        this.presenter = new DashboardPresenter(this, walletController, transactionController);
        initializeUI(); reloadDashboard();
    }
    // endregion

    // region UI
    private void initializeUI() {
        setLayout(new BorderLayout(0, 20)); setBackground(ViewConstants.COLOR_CONTENT_BG); setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel top = new JPanel(); top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS)); top.setBackground(ViewConstants.COLOR_CONTENT_BG);
        top.add(createHeaderPanel()); top.add(Box.createVerticalStrut(20)); top.add(this.kpiCardsPanel);
        add(top, BorderLayout.NORTH); add(createTableSection(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout()); headerPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        JPanel titles = new JPanel(); titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS)); titles.setBackground(ViewConstants.COLOR_CONTENT_BG);
        this.headerTitleLabel = new JLabel("Bảng Điều Khiển Tổng Quan"); this.headerTitleLabel.setFont(ViewConstants.FONT_PANEL_HEADER);
        this.headerTitleLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN); this.headerTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titles.add(this.headerTitleLabel); titles.add(Box.createVerticalStrut(4));
        String currentCycle = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        this.cycleSubtitleLabel = new JLabel("Chu kỳ tài chính: Tháng " + currentCycle); this.cycleSubtitleLabel.setFont(ViewConstants.FONT_SUBTITLE);
        this.cycleSubtitleLabel.setForeground(ViewConstants.COLOR_GOLD_LIGHT); this.cycleSubtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titles.add(this.cycleSubtitleLabel); headerPanel.add(titles, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createTableSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10)); section.setBackground(ViewConstants.COLOR_CONTENT_BG);
        JLabel title = new JLabel("GIAO DỊCH GẦN NHẤT"); title.setFont(new Font("Segoe UI", Font.BOLD, 13)); title.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        section.add(title, BorderLayout.NORTH); configureTable();
        JScrollPane scroll = new JScrollPane(this.recentTable); scroll.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        scroll.setBackground(ViewConstants.COLOR_CARD_BG); scroll.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1));
        section.add(scroll, BorderLayout.CENTER); return section;
    }

    private void configureTable() {
        this.recentTable.setRowHeight(30); this.recentTable.setBackground(ViewConstants.COLOR_CARD_BG);
        this.recentTable.setForeground(ViewConstants.COLOR_TEXT_MAIN); this.recentTable.setGridColor(ViewConstants.COLOR_BORDER);
        this.recentTable.setShowGrid(true); this.recentTable.setSelectionBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        this.recentTable.setSelectionForeground(ViewConstants.COLOR_TEXT_ACTIVE); this.recentTable.setFont(ViewConstants.FONT_BODY);
        this.recentTable.getTableHeader().setReorderingAllowed(false);
        this.recentTable.getTableHeader().setDefaultRenderer(createHeaderRenderer());
        DefaultTableCellRenderer textRenderer = createTextRenderer();
        for (int i = 0; i < 4; i++) this.recentTable.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        this.recentTable.getColumnModel().getColumn(4).setCellRenderer(createAmountRenderer());
        int[] widths = {100, 140, 140, 220, 130};
        for (int i = 0; i < 5; i++) this.recentTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }
    // endregion

    // region Actions
    public void onRefreshClicked() { this.presenter.onRefreshClicked(); }
    // endregion

    // region Data
    @Override public void renderKpis(DashboardKpiViewModel kpis) { this.kpiCardsPanel.updateKpis(kpis); }
    @Override public void renderRecentTransactions(List<RecentTransactionViewModel> txns) { this.tableModel.setTransactions(txns); }
    @Override public void setCycleSubtitle(String text) { if (this.cycleSubtitleLabel != null) this.cycleSubtitleLabel.setText(text); }
    public void reloadDashboard() { this.presenter.loadDashboard(); }
    // endregion

    // region Getters
    public JLabel getHeaderTitleLabel() { return this.headerTitleLabel; } public JLabel getCycleSubtitleLabel() { return this.cycleSubtitleLabel; }
    public JLabel getTotalBalanceValueLabel() { return this.kpiCardsPanel.getTotalBalanceValueLabel(); } public JLabel getTotalBalanceSubtextLabel() { return this.kpiCardsPanel.getTotalBalanceSubtextLabel(); }
    public JLabel getIncomeValueLabel() { return this.kpiCardsPanel.getIncomeValueLabel(); } public JLabel getIncomeSubtextLabel() { return this.kpiCardsPanel.getIncomeSubtextLabel(); }
    public JLabel getExpenseValueLabel() { return this.kpiCardsPanel.getExpenseValueLabel(); } public JLabel getExpenseSubtextLabel() { return this.kpiCardsPanel.getExpenseSubtextLabel(); }
    public JLabel getSavingsRateValueLabel() { return this.kpiCardsPanel.getSavingsRateValueLabel(); } public JLabel getSavingsRateSubtextLabel() { return this.kpiCardsPanel.getSavingsRateSubtextLabel(); }
    public JTable getRecentTable() { return this.recentTable; } public RecentTransactionsTableModel getTableModel() { return this.tableModel; }
    public DashboardPresenter getPresenter() { return this.presenter; } public KpiCardsPanel getKpiCardsPanel() { return this.kpiCardsPanel; }
    public ReportGUIController getReportController() { return this.reportController; }
    // endregion

    // region Helpers
    @Override public void showMessage(String m) { if (messageConsumer != null) messageConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thành công", JOptionPane.INFORMATION_MESSAGE); }
    @Override public void showWarning(String m) { if (warningConsumer != null) warningConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.WARNING_MESSAGE); }
    @Override public void showError(String m) { if (errorConsumer != null) errorConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    @Override public boolean confirmAction(String m) { return (confirmConsumer != null) ? confirmConsumer.test(m) : JOptionPane.showConfirmDialog(this, m, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; }
    public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = c; }
    public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = c; }
    public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = c; }
    public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = c; }
    private DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c); setBackground(new Color(14, 14, 14)); setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
                setFont(ViewConstants.FONT_NAV_ACTIVE); setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ViewConstants.COLOR_BORDER), BorderFactory.createEmptyBorder(6, 8, 6, 8)));
                setHorizontalAlignment(c == 4 ? SwingConstants.RIGHT : (c == 0 ? SwingConstants.CENTER : SwingConstants.LEFT)); return this;
            }
        };
    }
    private DefaultTableCellRenderer createTextRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                if (!s) { setBackground(ViewConstants.COLOR_CARD_BG); setForeground(c == 0 ? ViewConstants.COLOR_TEXT_MUTED : ViewConstants.COLOR_TEXT_MAIN); }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8)); setHorizontalAlignment(c == 0 ? SwingConstants.CENTER : SwingConstants.LEFT); return this;
            }
        };
    }
    private DefaultTableCellRenderer createAmountRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c); setHorizontalAlignment(SwingConstants.RIGHT); setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!s) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    RecentTransactionViewModel txn = tableModel.getTransactionAt(r);
                    if (txn != null && (txn.isIncome() || "INCOME".equalsIgnoreCase(txn.type()))) setForeground(ViewConstants.COLOR_INCOME);
                    else if (txn != null && "EXPENSE".equalsIgnoreCase(txn.type())) setForeground(ViewConstants.COLOR_EXPENSE);
                    else {
                        String val = (v != null) ? v.toString() : "";
                        if (val.startsWith("+")) setForeground(ViewConstants.COLOR_INCOME);
                        else if (val.startsWith("-")) setForeground(ViewConstants.COLOR_EXPENSE);
                        else setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
                    }
                }
                return this;
            }
        };
    }
    // endregion
}
