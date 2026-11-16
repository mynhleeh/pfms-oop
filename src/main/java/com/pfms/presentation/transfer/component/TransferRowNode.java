package com.pfms.presentation.transfer.component;

import com.pfms.presentation.transfer.viewmodel.TransferItemViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Thẻ dòng chuyển tiền nội bộ phẳng dàn ngang (TransferRowNode).
 * Tách biệt rõ ràng Ví Nguồn và Ví Đích, hỗ trợ hiển thị phụ phí nếu có
 * và nút thao tác xóa căn giữa thẳng tắp 100%.
 */
public class TransferRowNode extends HBox {

    private final TransferItemViewModel item;
    private final Label dateLabel;
    private final Label fromWalletLabel;
    private final Label toWalletLabel;
    private final VBox amountBox;
    private final Label amountLabel;
    private Label feeBadge;
    private final Label noteLabel;
    private final Button btnDelete;

    public TransferRowNode(TransferItemViewModel item, Consumer<TransferItemViewModel> onDelete) {
        this.item = Objects.requireNonNull(item, "item không được để null");

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

        // 1. Cột Ngày Chuyển (100px cố định)
        this.dateLabel = new Label(item.dateFormatted() != null ? item.dateFormatted() : "—");
        this.dateLabel.setPrefWidth(100);
        this.dateLabel.setMinWidth(100);
        this.dateLabel.setMaxWidth(100);
        this.dateLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 13px; -fx-font-weight: 500;");

        // 2. Cột Ví Nguồn (Chuyển đi) (140px cố định, loại bỏ tiền tố 'Ví ')
        String rawFrom = item.fromWalletName();
        String cleanFrom = (rawFrom != null) ? rawFrom.replaceFirst("^(?i)ví\\s+", "") : "—";
        this.fromWalletLabel = new Label(cleanFrom);
        this.fromWalletLabel.setPrefWidth(140);
        this.fromWalletLabel.setMinWidth(140);
        this.fromWalletLabel.setMaxWidth(140);
        this.fromWalletLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 13px; -fx-font-weight: 500;");

        // 3. Cột Ví Đích (Nhận tiền) (140px cố định, loại bỏ tiền tố 'Ví ')
        String rawTo = item.toWalletName();
        String cleanTo = (rawTo != null) ? rawTo.replaceFirst("^(?i)ví\\s+", "") : "—";
        this.toWalletLabel = new Label(cleanTo);
        this.toWalletLabel.setPrefWidth(140);
        this.toWalletLabel.setMinWidth(140);
        this.toWalletLabel.setMaxWidth(140);
        this.toWalletLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 13px; -fx-font-weight: 500;");

        // 4. Cột Số Tiền & Phụ Phí (165px cố định, căn phải)
        this.amountBox = new VBox(2);
        this.amountBox.setAlignment(Pos.CENTER_RIGHT);
        this.amountBox.setPrefWidth(165);
        this.amountBox.setMinWidth(165);
        this.amountBox.setMaxWidth(165);

        String rawAmount = item.formattedAmount();
        String displayAmount = (rawAmount != null) ? rawAmount : "0";
        if (!displayAmount.endsWith(" ₫") && !displayAmount.endsWith(" VNĐ")) {
            displayAmount = displayAmount + " ₫";
        }

        this.amountLabel = new Label(displayAmount);
        this.amountLabel.setStyle("-fx-text-fill: #FAFAFA; -fx-font-size: 14px; -fx-font-weight: bold;");
        this.amountBox.getChildren().add(this.amountLabel);

        if (item.hasFee() && item.feeFormatted() != null && !item.feeFormatted().isBlank()) {
            this.feeBadge = new Label("⚡ Phí: " + item.feeFormatted() + " ₫");
            this.feeBadge.setStyle("-fx-font-size: 10px; -fx-font-weight: 600; -fx-text-fill: #F59E0B; " +
                    "-fx-background-color: rgba(245, 158, 11, 0.12); -fx-border-color: rgba(245, 158, 11, 0.28); " +
                    "-fx-border-radius: 4px; -fx-background-radius: 4px; -fx-padding: 1px 6px;");
            this.amountBox.getChildren().add(this.feeBadge);
        }

        // 5. Cột Ghi Chú / Diễn Giải (Co giãn tự do Priority.ALWAYS)
        String note = item.note();
        boolean hasNote = note != null && !note.isBlank();
        this.noteLabel = new Label(hasNote ? note : "—");
        this.noteLabel.setMinWidth(0);
        this.noteLabel.setMaxWidth(Double.MAX_VALUE);
        this.noteLabel.setStyle(hasNote ?
                "-fx-text-fill: -color-text-muted; -fx-font-size: 12px;" :
                "-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");
        HBox.setHgrow(this.noteLabel, Priority.ALWAYS);

        // 6. Cột Thao Tác (115px cố định, căn giữa, thẳng tắp trên mọi hàng)
        this.btnDelete = new Button("Xóa");
        this.btnDelete.getStyleClass().addAll("btn-action-sm", "btn-danger");
        this.btnDelete.setPrefWidth(52);
        this.btnDelete.setMinWidth(52);
        this.btnDelete.setMaxWidth(52);
        this.btnDelete.setStyle("-fx-font-size: 11px; -fx-padding: 3px 0; -fx-alignment: CENTER;");
        if (onDelete != null) {
            this.btnDelete.setOnAction(e -> onDelete.accept(item));
        }

        HBox actionsBox = new HBox(6, this.btnDelete);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setPrefWidth(115);
        actionsBox.setMinWidth(115);
        actionsBox.setMaxWidth(115);

        this.getChildren().addAll(
                this.dateLabel,
                this.fromWalletLabel,
                this.toWalletLabel,
                this.amountBox,
                this.noteLabel,
                actionsBox
        );
    }

    public TransferItemViewModel getItem() {
        return item;
    }

    public Label getDateLabel() {
        return dateLabel;
    }

    public Label getFromWalletLabel() {
        return fromWalletLabel;
    }

    public Label getToWalletLabel() {
        return toWalletLabel;
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

    public Label getNoteLabel() {
        return noteLabel;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }
}
