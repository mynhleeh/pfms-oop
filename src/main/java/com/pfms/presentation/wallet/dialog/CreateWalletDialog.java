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
 * Hộp thoại modal tạo ví tài chính mới (UC-WAL-01) sử dụng JavaFX.
 * Kế thừa BaseFormDialog theo mô hình Template Method, loại bỏ boilerplate lặp lại.
 */
public class CreateWalletDialog extends BaseFormDialog<WalletResponseDTO> {

    private final WalletGUIController walletController;
    private final TextField nameField;
    private final TextArea noteField;

    public CreateWalletDialog(Window owner, WalletGUIController walletController) {
        super(owner, "Thêm Ví Tài Chính Mới", 440);
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");

        var headerBox = createHeaderBox("Thêm Ví Tài Chính Mới", "Nhập thông tin ví để theo dõi tài sản và dòng tiền cá nhân");

        this.nameField = new TextField();
        this.nameField.setPromptText("Ví dụ: Ví Tiền Mặt, Thẻ VPBank...");

        this.noteField = new TextArea();
        this.noteField.setPromptText("Ghi chú mục đích sử dụng (tùy chọn)...");
        this.noteField.setPrefRowCount(3);
        this.noteField.setStyle("-fx-control-inner-background: #141414;");

        Label nameLabel = new Label("Tên ví (*):");
        nameLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label noteLabel = new Label("Ghi chú:");
        noteLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");

        var form = createFormBox(10, headerBox, nameLabel, this.nameField, noteLabel, this.noteField);
        getDialogPane().setContent(form);

        setupButtons("Lưu ví mới", "Hủy bỏ");
    }

    public CreateWalletDialog(WalletGUIController walletController) {
        this((Window) null, walletController);
    }

    public CreateWalletDialog(Object dummyParent, WalletGUIController walletController) {
        this(dummyParent instanceof Window w ? w : null, walletController);
    }

    @Override
    protected WalletResponseDTO onExecuteSave() throws Exception {
        String name = this.nameField.getText();
        String note = this.noteField.getText();
        return this.walletController.handleCreateWallet(name, note);
    }

    public WalletResponseDTO getCreatedWallet() {
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
