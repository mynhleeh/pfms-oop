package com.pfms.presentation.report.component;

import com.pfms.presentation.common.ViewUtils;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

/**
 * Thẻ dòng báo cáo Cơ cấu Tỷ trọng Nhóm nhu cầu (SpendingStructureRowNode) phẳng tối giản.
 * Cột căn thẳng tắp, thuần số định lượng (loại bỏ hoàn toàn thanh tiến độ progress bar và câu mô tả).
 */
public class SpendingStructureRowNode extends HBox {

    private final NeedGroupShareResponseDTO item;
    private final Label nameLabel;
    private final Label shareLabel;
    private final Label currentAmountLabel;
    private final Label prevAmountLabel;
    private final Label diffLabel;

    public SpendingStructureRowNode(NeedGroupShareResponseDTO item) {
        this(item, "EXPENSE");
    }

    public SpendingStructureRowNode(NeedGroupShareResponseDTO item, String transactionType) {
        this.item = Objects.requireNonNull(item, "NeedGroupShareResponseDTO không được để null");
        boolean isIncome = "INCOME".equalsIgnoreCase(transactionType);

        this.setAlignment(Pos.CENTER_LEFT);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setStyle("-fx-background-color: #141414; -fx-background-radius: 6px; " +
                "-fx-border-color: #1F1F1F; -fx-border-radius: 6px; -fx-padding: 10px 14px;");

        // Hover effect
        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #1A1A1A; -fx-background-radius: 6px; " +
                "-fx-border-color: #282828; -fx-border-radius: 6px; -fx-padding: 10px 14px; -fx-cursor: hand;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: #141414; -fx-background-radius: 6px; " +
                "-fx-border-color: #1F1F1F; -fx-border-radius: 6px; -fx-padding: 10px 14px;"));

        // 1. Cột NHÓM NHU CẦU (250px)
        this.nameLabel = new Label(item.getName() != null ? item.getName() : "—");
        this.nameLabel.setPrefWidth(250);
        this.nameLabel.setMinWidth(250);
        this.nameLabel.setMaxWidth(250);
        this.nameLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: 700; -fx-font-size: 13px;");

        // 2. Cột TỶ TRỌNG (%) (140px, right-aligned)
        BigDecimal share = item.getShare() != null ? item.getShare() : BigDecimal.ZERO;
        this.shareLabel = new Label(String.format(Locale.US, "%.1f%%", share));
        this.shareLabel.setStyle("-fx-background-color: rgba(229, 192, 123, 0.12); -fx-text-fill: #E5C07B; " +
                "-fx-background-radius: 4px; -fx-border-color: rgba(229, 192, 123, 0.25); -fx-border-radius: 4px; " +
                "-fx-padding: 2px 8px; -fx-font-size: 11.5px; -fx-font-weight: 700;");

        HBox shareWrapper = new HBox(this.shareLabel);
        shareWrapper.setAlignment(Pos.CENTER_RIGHT);
        shareWrapper.setPrefWidth(140);
        shareWrapper.setMinWidth(140);
        shareWrapper.setMaxWidth(140);
        shareWrapper.setPadding(new javafx.geometry.Insets(0, 14, 0, 0));

        // 3. Cột SỐ TIỀN HIỆN TẠI (190px, right-aligned) - Màu vàng theo chuẩn thiết kế
        BigDecimal total = item.getTotalAmount() != null ? item.getTotalAmount() : BigDecimal.ZERO;
        this.currentAmountLabel = new Label(ViewUtils.formatCurrency(total));
        this.currentAmountLabel.setPrefWidth(190);
        this.currentAmountLabel.setMinWidth(190);
        this.currentAmountLabel.setMaxWidth(190);
        this.currentAmountLabel.setAlignment(Pos.CENTER_RIGHT);
        this.currentAmountLabel.setStyle("-fx-text-fill: #E5C07B; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 0 14px 0 0;");

        // 4. Cột KỲ TRƯỚC (190px, right-aligned) - Màu trắng
        BigDecimal prev = item.getPreviousTotalAmount();
        String prevText = (prev != null) ? ViewUtils.formatCurrency(prev) : "—";
        this.prevAmountLabel = new Label(prevText);
        this.prevAmountLabel.setPrefWidth(190);
        this.prevAmountLabel.setMinWidth(190);
        this.prevAmountLabel.setMaxWidth(190);
        this.prevAmountLabel.setAlignment(Pos.CENTER_RIGHT);
        this.prevAmountLabel.setStyle("-fx-text-fill: #FAFAFA; -fx-font-size: 13px; -fx-padding: 0 14px 0 0;");

        // 5. Cột CHÊNH LỆCH (+/-) (Priority.ALWAYS, right-aligned) - Dương xanh, âm đỏ
        BigDecimal diff = item.getDifferenceAmount();
        String diffText;
        String diffColor;
        if (diff == null || diff.compareTo(BigDecimal.ZERO) == 0) {
            diffText = "0 ₫";
            diffColor = "#A1A1AA";
        } else if (diff.compareTo(BigDecimal.ZERO) > 0) {
            diffText = "+" + ViewUtils.formatCurrency(diff);
            diffColor = "#4ADE80"; // Chênh lệch dương màu xanh
        } else {
            diffText = ViewUtils.formatCurrency(diff);
            diffColor = "#FB7185"; // Chênh lệch âm màu đỏ
        }

        this.diffLabel = new Label(diffText);
        this.diffLabel.setStyle("-fx-text-fill: " + diffColor + "; -fx-font-weight: 700; -fx-font-size: 13px;");

        HBox diffWrapper = new HBox(this.diffLabel);
        diffWrapper.setAlignment(Pos.CENTER_RIGHT);
        diffWrapper.setPadding(new javafx.geometry.Insets(0, 10, 0, 0));
        HBox.setHgrow(diffWrapper, Priority.ALWAYS);

        this.getChildren().addAll(
                this.nameLabel,
                shareWrapper,
                this.currentAmountLabel,
                this.prevAmountLabel,
                diffWrapper
        );
    }

    public NeedGroupShareResponseDTO getItem() {
        return item;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getShareLabel() {
        return shareLabel;
    }

    public Label getCurrentAmountLabel() {
        return currentAmountLabel;
    }

    public Label getPrevAmountLabel() {
        return prevAmountLabel;
    }

    public Label getDiffLabel() {
        return diffLabel;
    }
}
