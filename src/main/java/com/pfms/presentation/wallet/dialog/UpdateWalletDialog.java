package com.pfms.presentation.wallet.dialog;

import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.wallet.WalletGUIController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Hộp thoại modal sửa đổi tên và ghi chú ví tài chính (UC-WAL-03) sử dụng JavaFX.
 */
public class UpdateWalletDialog extends Dialog<ButtonType> {

    private final WalletGUIController walletController;
    private final WalletResponseDTO initialWallet;
    private final TextField nameField;
    private final TextArea noteField;
    private boolean success = false;
    private WalletResponseDTO updatedWallet = null;
    private String errorMessage = null;
    private Consumer<String> errorConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi cập nhật ví");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };

    public UpdateWalletDialog(Window owner, WalletGUIController walletController, WalletResponseDTO initialWallet) {
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        this.initialWallet = Objects.requireNonNull(initialWallet, "WalletResponseDTO không được để null");

        setTitle("Chỉnh Sửa Thông Tin Ví");
        if (owner != null) {
            initOwner(owner);
        }

        this.nameField = new TextField(this.initialWallet.getName());
        this.noteField = new TextArea(this.initialWallet.getNote() != null ? this.initialWallet.getNote() : "");
        this.noteField.setPrefRowCount(3);

        VBox form = new VBox(8);
        form.setPadding(new Insets(16, 20, 16, 20));
        form.setStyle("-fx-background-color: #121212;");

        Label nameLabel = new Label("Tên ví (*):");
        nameLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold;");

        Label noteLabel = new Label("Ghi chú:");
        noteLabel.setStyle("-fx-text-fill: -color-text-main;");

        form.getChildren().addAll(nameLabel, nameField, noteLabel, noteField);

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
                triggerSave();
                if (!isSuccess()) {
                    event.consume();
                }
            });
        }
    }

    public UpdateWalletDialog(WalletGUIController walletController, WalletResponseDTO initialWallet) {
        this((Window) null, walletController, initialWallet);
    }

    public UpdateWalletDialog(Object dummyParent, WalletGUIController walletController, WalletResponseDTO initialWallet) {
        this((Window) (dummyParent instanceof Window ? (Window) dummyParent : null), walletController, initialWallet);
    }

    public void triggerSave() {
        String name = this.nameField.getText();
        String note = this.noteField.getText();

        try {
            this.updatedWallet = this.walletController.handleUpdateWallet(
                    this.initialWallet.getWalletId(), name, note);
            this.success = true;
            this.errorMessage = null;
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

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public WalletResponseDTO getUpdatedWallet() {
        return this.updatedWallet;
    }

    public String getNameInputValue() {
        return this.nameField.getText();
    }

    public void setNameInputValue(String name) {
        this.nameField.setText(name);
    }

    public String getNoteInputValue() {
        return this.noteField.getText();
    }

    public void setNoteInputValue(String note) {
        this.noteField.setText(note);
    }
}
