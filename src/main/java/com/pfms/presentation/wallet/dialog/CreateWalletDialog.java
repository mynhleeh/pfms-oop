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
 * Hộp thoại modal tạo ví tài chính mới (UC-WAL-01) sử dụng JavaFX.
 */
public class CreateWalletDialog extends Dialog<ButtonType> {

    private final WalletGUIController walletController;
    private final TextField nameField;
    private final TextArea noteField;
    private boolean success = false;
    private WalletResponseDTO createdWallet = null;
    private String errorMessage = null;
    private Consumer<String> errorConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi tạo ví");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };

    public CreateWalletDialog(Window owner, WalletGUIController walletController) {
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");

        setTitle("Thêm Ví Tài Chính Mới");
        if (owner != null) {
            initOwner(owner);
        }

        this.nameField = new TextField();
        this.nameField.setPromptText("Nhập tên ví...");

        this.noteField = new TextArea();
        this.noteField.setPromptText("Ghi chú tùy chọn...");
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

    public CreateWalletDialog(WalletGUIController walletController) {
        this((Window) null, walletController);
    }

    public CreateWalletDialog(Object dummyParent, WalletGUIController walletController) {
        this((Window) (dummyParent instanceof Window ? (Window) dummyParent : null), walletController);
    }

    public void triggerSave() {
        String name = this.nameField.getText();
        String note = this.noteField.getText();

        try {
            this.createdWallet = this.walletController.handleCreateWallet(name, note);
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

    public WalletResponseDTO getCreatedWallet() {
        return this.createdWallet;
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
