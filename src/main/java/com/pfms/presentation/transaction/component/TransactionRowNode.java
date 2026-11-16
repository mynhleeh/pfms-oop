package com.pfms.presentation.transaction.component;

import com.pfms.presentation.transaction.viewmodel.TransactionItemViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Thẻ dòng giao dịch phẳng dàn ngang (Phương án 3 - Minimalist Guide Rows).
 * Thay thế cho TableView thô cứng, tối ưu hóa hiển thị số tiền kèm phụ phí
 * và đồng bộ phong cách Luxury Black & Gold.
 */
public class TransactionRowNode extends HBox {

    private final TransactionItemViewModel item;
    private final Label dateLabel;
    private final VBox amountBox;
    private final Label amountLabel;
    private Label feeBadge;
    private final Label walletLabel;
    private final HBox groupBox;
    private final Label categoryLabel;
    private final Label noteLabel;
    private final Button btnEdit;
    private final Button btnDelete;

    public TransactionRowNode(
            TransactionItemViewModel item,
            Consumer<TransactionItemViewModel> onEdit,
            Consumer<TransactionItemViewModel> onDelete) {
        this.item = Objects.requireNonNull(item, "item không được null");

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

        // 1. Cột Ngày (100px cố định)
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

        String sign = item.isIncome() ? "+" : "-";
        String colorStyle = item.isIncome() ? "-fx-text-fill: -color-income;" : "-fx-text-fill: -color-expense;";
        this.amountLabel = new Label(sign + item.formattedAmount() + " ₫");
        this.amountLabel.setStyle(colorStyle + " -fx-font-size: 14px; -fx-font-weight: bold;");
        this.amountBox.getChildren().add(this.amountLabel);

        if (item.hasFee() && item.feeFormatted() != null && !item.feeFormatted().isBlank()) {
            this.feeBadge = new Label("⚡ Phí: " + item.feeFormatted() + " ₫");
            this.feeBadge.setStyle("-fx-font-size: 10.5px; -fx-font-weight: 600; -fx-text-fill: #F59E0B; " +
                    "-fx-background-color: rgba(245, 158, 11, 0.12); -fx-border-color: rgba(245, 158, 11, 0.28); " +
                    "-fx-border-radius: 4px; -fx-background-radius: 4px; -fx-padding: 1px 6px;");
            this.amountBox.getChildren().add(this.feeBadge);
        }

        // 3. Cột Ví Tài Khoản (135px cố định, bỏ icon dot, bỏ tiền tố "Ví ")
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
            groupBadge.setStyle("-fx-background-color: rgba(212, 175, 55, 0.12); -fx-text-fill: -color-primary-light; " +
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

        // 6. Cột Ghi Chú (Co giãn tự do Priority.ALWAYS, minWidth 0 để cột Thao tác luôn thẳng hàng)
        String note = item.note();
        boolean hasNote = note != null && !note.isBlank();
        this.noteLabel = new Label(hasNote ? note : "—");
        this.noteLabel.setMinWidth(0);
        this.noteLabel.setMaxWidth(Double.MAX_VALUE);
        this.noteLabel.setStyle(hasNote ?
                "-fx-text-fill: -color-text-muted; -fx-font-size: 12px;" :
                "-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");
        HBox.setHgrow(this.noteLabel, Priority.ALWAYS);

        // 7. Cột Thao Tác (115px cố định, căn giữa, thẳng tắp trên mọi hàng)
        this.btnEdit = new Button("Sửa");
        this.btnEdit.getStyleClass().addAll("btn-action-sm", "btn-secondary");
        this.btnEdit.setPrefWidth(52);
        this.btnEdit.setMinWidth(52);
        this.btnEdit.setMaxWidth(52);
        this.btnEdit.setStyle("-fx-font-size: 11px; -fx-padding: 3px 0; -fx-alignment: CENTER;");
        if (onEdit != null) {
            this.btnEdit.setOnAction(e -> onEdit.accept(item));
        }

        this.btnDelete = new Button("Xóa");
        this.btnDelete.getStyleClass().addAll("btn-action-sm", "btn-danger");
        this.btnDelete.setPrefWidth(52);
        this.btnDelete.setMinWidth(52);
        this.btnDelete.setMaxWidth(52);
        this.btnDelete.setStyle("-fx-font-size: 11px; -fx-padding: 3px 0; -fx-alignment: CENTER;");
        if (onDelete != null) {
            this.btnDelete.setOnAction(e -> onDelete.accept(item));
        }

        HBox actionsBox = new HBox(6, this.btnEdit, this.btnDelete);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setPrefWidth(115);
        actionsBox.setMinWidth(115);
        actionsBox.setMaxWidth(115);

        this.getChildren().addAll(
                this.dateLabel,
                this.amountBox,
                this.walletLabel,
                this.groupBox,
                this.categoryLabel,
                this.noteLabel,
                actionsBox
        );
    }

    public TransactionItemViewModel getItem() {
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

    public Button getBtnEdit() {
        return btnEdit;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }
}
