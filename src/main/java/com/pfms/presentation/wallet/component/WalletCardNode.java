package com.pfms.presentation.wallet.component;

import com.pfms.presentation.wallet.viewmodel.WalletItemViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Thẻ hiển thị ví tài chính trực quan (Luxury Onyx Wallet Card).
 * Đồng bộ với giao diện thẻ ví trong preview/preview-wallet.html.
 */
public class WalletCardNode extends VBox {

    private final WalletItemViewModel item;
    private final Consumer<WalletItemViewModel> onAdjust;
    private final Consumer<WalletItemViewModel> onEdit;
    private final Consumer<WalletItemViewModel> onToggleStatus;
    private final Consumer<WalletItemViewModel> onDelete;

    private Button btnAdjust;
    private Button btnEdit;
    private Button btnToggleStatus;
    private Button btnDelete;

    public WalletCardNode(
            WalletItemViewModel item,
            Consumer<WalletItemViewModel> onAdjust,
            Consumer<WalletItemViewModel> onEdit,
            Consumer<WalletItemViewModel> onToggleStatus,
            Consumer<WalletItemViewModel> onDelete) {

        this.item = Objects.requireNonNull(item, "WalletItemViewModel không được để null");
        this.onAdjust = Objects.requireNonNull(onAdjust, "onAdjust không được để null");
        this.onEdit = Objects.requireNonNull(onEdit, "onEdit không được để null");
        this.onToggleStatus = Objects.requireNonNull(onToggleStatus, "onToggleStatus không được để null");
        this.onDelete = Objects.requireNonNull(onDelete, "onDelete không được để null");

        getStyleClass().add("wallet-card");
        if (!item.isActive()) {
            if (item.statusText() != null && item.statusText().contains("Lưu trữ")) {
                getStyleClass().add("status-archived");
            } else {
                getStyleClass().add("status-deleted");
            }
        }
        setSpacing(12);
        setMinWidth(280);
        setMaxWidth(Double.MAX_VALUE);

        // Header: Avatar Icon + Tên ví + Cụm Badges
        HBox headerBox = createHeaderBox();

        // Ghi chú đa dòng
        Label noteLabel = new Label(item.note() != null && !item.note().isBlank() ? item.note() : "Không có ghi chú");
        noteLabel.setWrapText(true);
        noteLabel.getStyleClass().add("wallet-card-note");
        noteLabel.setStyle("-fx-min-height: 32px;");

        // Khối số dư
        VBox balanceBox = new VBox(2);
        Label balanceTitle = new Label("SỐ DƯ KHẢ DỤNG");
        balanceTitle.getStyleClass().add("wallet-card-balance-label");

        Label balanceValue = new Label(item.formattedBalance());
        balanceValue.getStyleClass().add("wallet-card-balance-value");
        balanceBox.getChildren().addAll(balanceTitle, balanceValue);

        // Spacer đẩy nút bấm xuống đáy
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Hàng nút hành động
        HBox actionsBox = createActionsBox();

        getChildren().addAll(headerBox, noteLabel, balanceBox, spacer, actionsBox);
    }

    private HBox createHeaderBox() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Avatar icon hiển thị chữ cái viết tắt
        Label avatarLabel = new Label(extractInitials(item.name()));
        avatarLabel.setStyle("-fx-background-color: rgba(212, 175, 55, 0.12); " +
                "-fx-text-fill: -color-primary-light; -fx-font-weight: 800; -fx-font-size: 13px; " +
                "-fx-alignment: center; -fx-min-width: 38px; -fx-min-height: 38px; " +
                "-fx-background-radius: 8px; -fx-border-color: -color-gold-border; -fx-border-radius: 8px;");

        VBox titleBox = new VBox(2);
        Label nameLabel = new Label(item.name());
        nameLabel.getStyleClass().add("wallet-card-name");
        titleBox.getChildren().add(nameLabel);

        if (item.isDefault()) {
            Label defaultBadge = new Label("VÍ MẶC ĐỊNH");
            defaultBadge.getStyleClass().add("badge-default");
            titleBox.getChildren().add(defaultBadge);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox badgesBox = new HBox(6);
        badgesBox.setAlignment(Pos.CENTER_RIGHT);

        Label statusBadge = new Label();
        statusBadge.getStyleClass().add("badge-status");
        if (item.isActive()) {
            statusBadge.setText("HOẠT ĐỘNG");
            statusBadge.getStyleClass().add("badge-status-active");
        } else if (item.statusText() != null && item.statusText().contains("Lưu trữ")) {
            statusBadge.setText("LƯU TRỮ");
            statusBadge.getStyleClass().add("badge-status-archived");
        } else {
            statusBadge.setText("ĐÃ ĐÓNG");
            statusBadge.getStyleClass().add("badge-status-deleted");
        }
        badgesBox.getChildren().add(statusBadge);

        header.getChildren().addAll(avatarLabel, titleBox, spacer, badgesBox);
        return header;
    }

    private String extractInitials(String name) {
        if (name == null || name.isBlank()) return "VÍ";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].length() <= 3 ? parts[0].toUpperCase() : parts[0].substring(0, 2).toUpperCase();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(3, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
            }
        }
        return sb.toString();
    }

    private HBox createActionsBox() {
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_RIGHT);

        this.btnAdjust = new Button("Điều chỉnh số dư");
        this.btnAdjust.getStyleClass().addAll("btn-action-sm", "btn-action-gold");
        this.btnAdjust.setOnAction(e -> onAdjust.accept(item));

        this.btnEdit = new Button("Sửa");
        this.btnEdit.getStyleClass().add("btn-action-sm");
        this.btnEdit.setOnAction(e -> onEdit.accept(item));

        this.btnToggleStatus = new Button(item.isActive() ? "Lưu trữ" : "Kích hoạt");
        this.btnToggleStatus.getStyleClass().add("btn-action-sm");
        this.btnToggleStatus.setOnAction(e -> onToggleStatus.accept(item));

        this.btnDelete = new Button("Xóa");
        this.btnDelete.getStyleClass().addAll("btn-action-sm", "btn-danger");
        this.btnDelete.setOnAction(e -> onDelete.accept(item));

        actions.getChildren().addAll(this.btnAdjust, this.btnEdit, this.btnToggleStatus, this.btnDelete);
        return actions;
    }

    public WalletItemViewModel getItem() {
        return item;
    }

    // region Testing Hooks
    public void triggerAdjustAction() {
        if (btnAdjust != null) btnAdjust.fire();
    }

    public void triggerEditAction() {
        if (btnEdit != null) btnEdit.fire();
    }

    public void triggerToggleStatusAction() {
        if (btnToggleStatus != null) btnToggleStatus.fire();
    }

    public void triggerDeleteAction() {
        if (btnDelete != null) btnDelete.fire();
    }
    // endregion
}
