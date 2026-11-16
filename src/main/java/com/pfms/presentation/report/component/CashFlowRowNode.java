package com.pfms.presentation.report.component;

import com.pfms.presentation.common.ViewUtils;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Thẻ dòng báo cáo Dòng tiền 12 tháng (CashFlowRowNode) phẳng tối giản.
 * Cột căn thẳng tắp, thuần số định lượng (không thanh tiến độ, không câu nhận xét định tính).
 */
public class CashFlowRowNode extends HBox {

    private final MonthlyCashFlowResponseDTO item;
    private final Label monthLabel;
    private final Label incomeLabel;
    private final Label expenseLabel;
    private final Label netLabel;
    private final Label rateLabel;

    public CashFlowRowNode(MonthlyCashFlowResponseDTO item) {
        this.item = Objects.requireNonNull(item, "MonthlyCashFlowResponseDTO không được để null");

        this.setAlignment(Pos.CENTER_LEFT);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setStyle("-fx-background-color: #141414; -fx-background-radius: 6px; " +
                "-fx-border-color: #1F1F1F; -fx-border-radius: 6px; -fx-padding: 10px 14px;");

        // Hover effect
        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #1A1A1A; -fx-background-radius: 6px; " +
                "-fx-border-color: #282828; -fx-border-radius: 6px; -fx-padding: 10px 14px; -fx-cursor: hand;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: #141414; -fx-background-radius: 6px; " +
                "-fx-border-color: #1F1F1F; -fx-border-radius: 6px; -fx-padding: 10px 14px;"));

        // 1. Cột THÁNG (100px)
        this.monthLabel = new Label(item.getYearMonth() != null ? item.getYearMonth() : "—");
        this.monthLabel.setPrefWidth(100);
        this.monthLabel.setMinWidth(100);
        this.monthLabel.setMaxWidth(100);
        this.monthLabel.setStyle("-fx-text-fill: #E5C07B; -fx-font-weight: 700; -fx-font-size: 13px;");

        // 2. Cột THU NHẬP (180px, right-aligned)
        BigDecimal inc = item.getTotalIncome() != null ? item.getTotalIncome() : BigDecimal.ZERO;
        String incText = (inc.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + ViewUtils.formatCurrency(inc);
        this.incomeLabel = new Label(incText);
        this.incomeLabel.setPrefWidth(180);
        this.incomeLabel.setMinWidth(180);
        this.incomeLabel.setMaxWidth(180);
        this.incomeLabel.setAlignment(Pos.CENTER_RIGHT);
        this.incomeLabel.setStyle("-fx-text-fill: #4ADE80; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 0 14px 0 0;");

        // 3. Cột CHI TIÊU (180px, right-aligned)
        BigDecimal exp = item.getTotalExpense() != null ? item.getTotalExpense() : BigDecimal.ZERO;
        String expText = (exp.compareTo(BigDecimal.ZERO) > 0 ? "-" : "") + ViewUtils.formatCurrency(exp);
        this.expenseLabel = new Label(expText);
        this.expenseLabel.setPrefWidth(180);
        this.expenseLabel.setMinWidth(180);
        this.expenseLabel.setMaxWidth(180);
        this.expenseLabel.setAlignment(Pos.CENTER_RIGHT);
        this.expenseLabel.setStyle("-fx-text-fill: #FB7185; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 0 14px 0 0;");

        // 4. Cột DÒNG TIỀN RÒNG (190px, right-aligned)
        BigDecimal net = item.getNetCashFlow() != null ? item.getNetCashFlow() : BigDecimal.ZERO;
        String netPrefix = net.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        this.netLabel = new Label(netPrefix + ViewUtils.formatCurrency(net));
        this.netLabel.setPrefWidth(190);
        this.netLabel.setMinWidth(190);
        this.netLabel.setMaxWidth(190);
        this.netLabel.setAlignment(Pos.CENTER_RIGHT);
        String netColor = net.compareTo(BigDecimal.ZERO) >= 0 ? "#E5C07B" : "#FB7185";
        this.netLabel.setStyle("-fx-text-fill: " + netColor + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 0 14px 0 0;");

        // 5. Cột TỶ LỆ TIẾT KIỆM (Priority.ALWAYS, right-aligned)
        BigDecimal rate = item.getSavingsRate() != null ? item.getSavingsRate() : BigDecimal.ZERO;
        this.rateLabel = new Label(String.format("%.1f%%", rate));
        this.rateLabel.setStyle("-fx-background-color: rgba(229, 192, 123, 0.12); -fx-text-fill: #E5C07B; " +
                "-fx-background-radius: 4px; -fx-border-color: rgba(229, 192, 123, 0.25); -fx-border-radius: 4px; " +
                "-fx-padding: 2px 10px; -fx-font-size: 12px; -fx-font-weight: 700;");

        HBox rateWrapper = new HBox(this.rateLabel);
        rateWrapper.setAlignment(Pos.CENTER_RIGHT);
        rateWrapper.setPadding(new javafx.geometry.Insets(0, 8, 0, 0));
        HBox.setHgrow(rateWrapper, Priority.ALWAYS);

        this.getChildren().addAll(
                this.monthLabel,
                this.incomeLabel,
                this.expenseLabel,
                this.netLabel,
                rateWrapper
        );
    }

    public MonthlyCashFlowResponseDTO getItem() {
        return item;
    }

    public Label getMonthLabel() {
        return monthLabel;
    }

    public Label getIncomeLabel() {
        return incomeLabel;
    }

    public Label getExpenseLabel() {
        return expenseLabel;
    }

    public Label getNetLabel() {
        return netLabel;
    }

    public Label getRateLabel() {
        return rateLabel;
    }
}
