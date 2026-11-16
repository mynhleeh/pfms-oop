package com.pfms.presentation.dashboard.component;

import com.pfms.presentation.dashboard.viewmodel.RecentTransactionViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Thẻ dòng giao dịch gần nhất tại Dashboard theo phong cách thẻ dòng phẳng dàn ngang.
 * Tối ưu hóa hiển thị 7 cột chuẩn: Thời gian, Số tiền & Phụ phí, Ví, Nhóm nhu cầu,
 * Danh mục, Ghi chú, Phân loại loại hình giao dịch.
 */
public class DashboardTransactionRowNode extends HBox {

    private final RecentTransactionViewModel item;
    private final Label dateLabel;
    private final VBox amountBox;
    private final Label amountLabel;
    private Label feeBadge;
    private final Label walletLabel;
    private final HBox groupBox;
    private final Label categoryLabel;
    private final Label noteLabel;
    private final HBox typeBadgeBox;

    public DashboardTransactionRowNode(RecentTransactionViewModel item) {
        this.item = Objects.requireNonNull(item, "RecentTransactionViewModel không được để null");

        this.getStyleClass().add("stream-row-card");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(12);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setStyle("-fx-background-color: #141414; -fx-background-radius: 8px; " +
                "-fx-border-color: #1F1F1F; -fx-border-radius: 8px; -fx-padding: 10px 16px;");

        // Hover effect qua mouse listeners
        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #1A1A1A; -fx-background-radius: 8px; " +
                "-fx-border-color: #2A2A2A; -fx-border-radius: 8px; -fx-padding: 10px 16px; -fx-cursor: hand;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: #141414; -fx-background-radius: 8px; " +
                "-fx-border-color: #1F1F1F; -fx-border-radius: 8px; -fx-padding: 10px 16px;"));

        // 1. Cột Thời Gian (100px cố định)
        this.dateLabel = new Label(item.dateFormatted() != null ? item.dateFormatted() : "—");
        this.dateLabel.setPrefWidth(100);
        this.dateLabel.setMinWidth(100);
        this.dateLabel.setMaxWidth(100);
        this.dateLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 13px; -fx-font-weight: 500;");

        // 2. Cột Số Tiền & Phụ Phí (165px cố định, căn phải)
        this.amountBox = new VBox(2);
        this.amountBox.setAlignment(Pos.CENTER_RIGHT);
        this.amountBox.setPrefWidth(165);
        this.amountBox.setMinWidth(165);
        this.amountBox.setMaxWidth(165);

        String rawAmount = item.amountFormatted();
        String displayAmount = (rawAmount != null) ? rawAmount : "0";
        if (!displayAmount.endsWith(" ₫") && !displayAmount.endsWith(" VNĐ")) {
            displayAmount = displayAmount + " ₫";
        }

        String colorStyle = item.isIncome() ? "-fx-text-fill: #4ADE80;" : "-fx-text-fill: #FB7185;";
        this.amountLabel = new Label(displayAmount);
        this.amountLabel.setStyle(colorStyle + " -fx-font-size: 14px; -fx-font-weight: bold;");
        this.amountBox.getChildren().add(this.amountLabel);

        if (item.hasFee() && item.feeFormatted() != null && !item.feeFormatted().isBlank()) {
            this.feeBadge = new Label("⚡ Phí: " + item.feeFormatted() + " ₫");
            this.feeBadge.setStyle("-fx-font-size: 10px; -fx-font-weight: 600; -fx-text-fill: #F59E0B; " +
                    "-fx-background-color: rgba(245, 158, 11, 0.12); -fx-border-color: rgba(245, 158, 11, 0.28); " +
                    "-fx-border-radius: 4px; -fx-background-radius: 4px; -fx-padding: 1px 6px;");
            this.amountBox.getChildren().add(this.feeBadge);
        }

        // 3. Cột Ví Tài Khoản (135px cố định, loại bỏ tiền tố 'Ví ')
        String rawWallet = item.walletName();
        String cleanWallet = (rawWallet != null) ? rawWallet.replaceFirst("^(?i)ví\\s+", "") : "—";
        this.walletLabel = new Label(cleanWallet);
        this.walletLabel.setPrefWidth(135);
        this.walletLabel.setMinWidth(135);
        this.walletLabel.setMaxWidth(135);
        this.walletLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 13px; -fx-font-weight: 500;");

        // 4. Cột Nhóm Nhu Cầu (145px cố định)
        this.groupBox = new HBox();
        this.groupBox.setAlignment(Pos.CENTER_LEFT);
        this.groupBox.setPrefWidth(145);
        this.groupBox.setMinWidth(145);
        this.groupBox.setMaxWidth(145);
        String groupName = item.groupName();
        if (groupName != null && !groupName.isBlank()) {
            Label groupBadge = new Label(groupName);
            groupBadge.setStyle("-fx-background-color: rgba(212, 175, 55, 0.12); -fx-text-fill: #E5C07B; " +
                    "-fx-background-radius: 4px; -fx-border-color: rgba(212, 175, 55, 0.25); -fx-border-radius: 4px; " +
                    "-fx-padding: 2px 8px; -fx-font-size: 11px; -fx-font-weight: 600;");
            this.groupBox.getChildren().add(groupBadge);
        } else {
            Label emptyGroup = new Label("—");
            emptyGroup.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");
            this.groupBox.getChildren().add(emptyGroup);
        }

        // 5. Cột Danh Mục (140px cố định)
        this.categoryLabel = new Label(item.categoryName() != null ? item.categoryName() : "—");
        this.categoryLabel.setPrefWidth(140);
        this.categoryLabel.setMinWidth(140);
        this.categoryLabel.setMaxWidth(140);
        this.categoryLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px; -fx-font-weight: 600;");

        // 6. Cột Ghi Chú (Co giãn tự do Priority.ALWAYS)
        String note = item.note();
        boolean hasNote = note != null && !note.isBlank();
        this.noteLabel = new Label(hasNote ? note : "—");
        this.noteLabel.setMinWidth(0);
        this.noteLabel.setMaxWidth(Double.MAX_VALUE);
        this.noteLabel.setStyle(hasNote ?
                "-fx-text-fill: -color-text-muted; -fx-font-size: 12px;" :
                "-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");
        HBox.setHgrow(this.noteLabel, Priority.ALWAYS);

        // 7. Cột Phân Loại (100px cố định, căn giữa)
        this.typeBadgeBox = new HBox();
        this.typeBadgeBox.setAlignment(Pos.CENTER);
        this.typeBadgeBox.setPrefWidth(100);
        this.typeBadgeBox.setMinWidth(100);
        this.typeBadgeBox.setMaxWidth(100);

        Label typeBadge = new Label(item.isIncome() ? "Thu Nhập" : "Chi Tiêu");
        if (item.isIncome()) {
            typeBadge.setStyle("-fx-background-color: rgba(74, 222, 128, 0.12); -fx-text-fill: #4ADE80; " +
                    "-fx-background-radius: 4px; -fx-border-color: rgba(74, 222, 128, 0.25); -fx-border-radius: 4px; " +
                    "-fx-padding: 2px 8px; -fx-font-size: 11px; -fx-font-weight: 600;");
        } else {
            typeBadge.setStyle("-fx-background-color: rgba(251, 113, 133, 0.12); -fx-text-fill: #FB7185; " +
                    "-fx-background-radius: 4px; -fx-border-color: rgba(251, 113, 133, 0.25); -fx-border-radius: 4px; " +
                    "-fx-padding: 2px 8px; -fx-font-size: 11px; -fx-font-weight: 600;");
        }
        this.typeBadgeBox.getChildren().add(typeBadge);

        this.getChildren().addAll(
                this.dateLabel,
                this.amountBox,
                this.walletLabel,
                this.groupBox,
                this.categoryLabel,
                this.noteLabel,
                this.typeBadgeBox
        );
    }

    public RecentTransactionViewModel getItem() {
        return item;
    }

    public Label getDateLabel() {
        return dateLabel;
    }

    public VBox getAmountBox() {
        return amountBox;
    }

    public Label getAmountLabel() {
        return amountLabel;
    }

    public Label getFeeBadge() {
        return feeBadge;
    }

    public Label getWalletLabel() {
        return walletLabel;
    }

    public HBox getGroupBox() {
        return groupBox;
    }

    public Label getCategoryLabel() {
        return categoryLabel;
    }

    public Label getNoteLabel() {
        return noteLabel;
    }

    public HBox getTypeBadgeBox() {
        return typeBadgeBox;
    }
}
