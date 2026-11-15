package com.pfms.presentation.dashboard.component;

import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.common.component.RoundedCardPanel;
import com.pfms.presentation.dashboard.viewmodel.DashboardKpiViewModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Thành phần hiển thị 4 thẻ KPI của Dashboard theo lưới 1x4.
 * Đóng gói toàn bộ cấu trúc giao diện và nhãn thông tin của 4 thẻ chỉ số chính.
 */
public class KpiCardsPanel extends JPanel {

    // region Fields
    private final JLabel totalBalanceValueLabel;
    private final JLabel totalBalanceSubtextLabel;
    private final JLabel incomeValueLabel;
    private final JLabel incomeSubtextLabel;
    private final JLabel expenseValueLabel;
    private final JLabel expenseSubtextLabel;
    private final JLabel savingsRateValueLabel;
    private final JLabel savingsRateSubtextLabel;
    // endregion

    // region Constructors
    public KpiCardsPanel() {
        super(new GridLayout(1, 4, 16, 0));
        setBackground(ViewConstants.COLOR_CONTENT_BG);

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

        add(card1);
        add(card2);
        add(card3);
        add(card4);
    }
    // endregion

    // region UI
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
    // endregion

    // region Actions
    public void updateKpis(DashboardKpiViewModel kpis) {
        if (kpis == null) {
            return;
        }
        this.totalBalanceValueLabel.setText(kpis.totalBalanceFormatted());
        this.totalBalanceSubtextLabel.setText(kpis.totalBalanceSubtext());
        this.incomeValueLabel.setText(kpis.incomeFormatted());
        this.incomeSubtextLabel.setText(kpis.incomeSubtext());
        this.expenseValueLabel.setText(kpis.expenseFormatted());
        this.expenseSubtextLabel.setText(kpis.expenseSubtext());
        this.savingsRateValueLabel.setText(kpis.savingsRateFormatted());
        this.savingsRateSubtextLabel.setText(kpis.savingsRateSubtext());
    }
    // endregion

    // region Getters
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
    // endregion
}
