package com.pfms.presentation.wallet.dialog;

import com.pfms.presentation.common.dialog.BaseFormDialog;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.wallet.WalletGUIController;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.util.Objects;

/**
 * Hộp thoại modal sửa đổi tên và ghi chú ví tài chính (UC-WAL-03) sử dụng JavaFX.
 * Kế thừa BaseFormDialog theo mô hình Template Method, loại bỏ boilerplate lặp lại.
 */
public class UpdateWalletDialog extends BaseFormDialog<WalletResponseDTO> {

    private final WalletGUIController walletController;
    private final WalletResponseDTO initialWallet;
    private final TextField nameField;
    private final TextArea noteField;

    public UpdateWalletDialog(Window owner, WalletGUIController walletController, WalletResponseDTO initialWallet) {
        super(owner, "Chỉnh Sửa Thông Tin Ví", 440);
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        this.initialWallet = Objects.requireNonNull(initialWallet, "WalletResponseDTO không được để null");

        var headerBox = createHeaderBox("Chỉnh Sửa Thông Tin Ví", "Cập nhật tên và ghi chú cho ví tài chính #" + this.initialWallet.getName());

        this.nameField = new TextField(this.initialWallet.getName());
        this.nameField.setPromptText("Ví dụ: Ví Tiền Mặt, Thẻ VPBank...");

        this.noteField = new TextArea(this.initialWallet.getNote() != null ? this.initialWallet.getNote() : "");
        this.noteField.setPromptText("Ghi chú mục đích sử dụng (tùy chọn)...");
        this.noteField.setPrefRowCount(3);
        this.noteField.setStyle("-fx-control-inner-background: #141414;");

        Label nameLabel = new Label("Tên ví (*):");
        nameLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label noteLabel = new Label("Ghi chú:");
        noteLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");

        var form = createFormBox(10, headerBox, nameLabel, this.nameField, noteLabel, this.noteField);
        getDialogPane().setContent(form);

        setupButtons("Lưu thay đổi", "Hủy bỏ");
    }

    public UpdateWalletDialog(WalletGUIController walletController, WalletResponseDTO initialWallet) {
        this((Window) null, walletController, initialWallet);
    }

    public UpdateWalletDialog(Object dummyParent, WalletGUIController walletController, WalletResponseDTO initialWallet) {
        this(dummyParent instanceof Window w ? w : null, walletController, initialWallet);
    }

    @Override
    protected WalletResponseDTO onExecuteSave() throws Exception {
        String name = this.nameField.getText();
        String note = this.noteField.getText();
        return this.walletController.handleUpdateWallet(this.initialWallet.getWalletId(), name, note);
    }

    public WalletResponseDTO getUpdatedWallet() {
        return this.resultData;
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
