package com.pfms.presentation.wallet.dialog;

import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.wallet.WalletGUIController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Hộp thoại modal kiểm kê điều chỉnh số dư ví thực tế (UC-WAL-04) sử dụng JavaFX.
 */
public class AdjustBalanceDialog extends Dialog<ButtonType> {

    private final WalletGUIController walletController;
    private final WalletResponseDTO wallet;
    private final TextField actualBalanceField;
    private final TextField reasonField;
    private final TextField dateField;
    private boolean success = false;
    private BalanceAdjustmentResponseDTO adjustmentResponse = null;
    private String errorMessage = null;
    private Consumer<String> errorConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi kiểm kê số dư");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };
    private Consumer<String> infoConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Kết quả kiểm kê");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };

    public AdjustBalanceDialog(Window owner, WalletGUIController walletController, WalletResponseDTO wallet) {
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        this.wallet = Objects.requireNonNull(wallet, "WalletResponseDTO không được để null");

        setTitle("Kiểm Kê Điều Chỉnh Số Dư Ví");
        if (owner != null) {
            initOwner(owner);
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

        Label currentBalanceLabel = new Label("Số dư hiện tại: " + formatter.format(wallet.getBalance()) + " " + wallet.getCurrency());
        currentBalanceLabel.setStyle("-fx-text-fill: -color-primary-light; -fx-font-weight: bold;");

        this.actualBalanceField = new TextField();
        this.actualBalanceField.setPromptText("Nhập số dư thực tế...");

        this.reasonField = new TextField();
        this.reasonField.setPromptText("Lý do điều chỉnh...");

        this.dateField = new TextField(LocalDate.now().toString());

        VBox form = new VBox(8);
        form.setPadding(new Insets(16, 20, 16, 20));
        form.setStyle("-fx-background-color: #121212;");

        Label actualLabel = new Label("Số dư thực tế (*):");
        actualLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold;");

        Label reasonLabel = new Label("Lý do điều chỉnh (*):");
        reasonLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold;");

        Label dateLabel = new Label("Ngày điều chỉnh (YYYY-MM-DD):");
        dateLabel.setStyle("-fx-text-fill: -color-text-main;");

        form.getChildren().addAll(
                currentBalanceLabel,
                actualLabel, this.actualBalanceField,
                reasonLabel, this.reasonField,
                dateLabel, this.dateField
        );

        DialogPane pane = getDialogPane();
        pane.setContent(form);
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        var cssResource = getClass().getResource("/css/pfms-theme.css");
        if (cssResource != null) {
            pane.getStylesheets().add(cssResource.toExternalForm());
        }

        final var okButton = pane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                triggerAdjust();
                if (!isSuccess()) {
                    event.consume();
                }
            });
        }
    }

    public AdjustBalanceDialog(WalletGUIController walletController, WalletResponseDTO wallet) {
        this((Window) null, walletController, wallet);
    }

    public AdjustBalanceDialog(Object dummyParent, WalletGUIController walletController, WalletResponseDTO wallet) {
        this((Window) (dummyParent instanceof Window ? (Window) dummyParent : null), walletController, wallet);
    }

    public void triggerAdjust() {
        String rawBalance = this.actualBalanceField.getText();
        String reason = this.reasonField.getText();
        String date = this.dateField.getText();

        try {
            this.adjustmentResponse = this.walletController.handleAdjustBalance(
                    this.wallet.getWalletId(), rawBalance, reason, date);
            this.success = true;
            this.errorMessage = null;
            if (this.infoConsumer != null) {
                this.infoConsumer.accept("Kiểm kê số dư thành công!\nChênh lệch điều chỉnh: " +
                        this.adjustmentResponse.getDifference() + " VNĐ");
            }
            dispose();
        } catch (Exception ex) {
            this.errorMessage = ex.getMessage();
            this.success = false;
            if (this.errorConsumer != null) {
                this.errorConsumer.accept(ex.getMessage());
            }
        }
    }

    public void dispose() {
        close();
    }

    public boolean isSuccess() {
        return this.success;
    }

    public BalanceAdjustmentResponseDTO getAdjustmentResponse() {
        return this.adjustmentResponse;
    }

    public void setActualBalanceInputValue(String balance) {
        this.actualBalanceField.setText(balance);
    }

    public void setReasonInputValue(String reason) {
        this.reasonField.setText(reason);
    }

    public String getDateInputValue() {
        return this.dateField.getText();
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public void setInfoConsumer(Consumer<String> infoConsumer) {
        this.infoConsumer = infoConsumer;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
