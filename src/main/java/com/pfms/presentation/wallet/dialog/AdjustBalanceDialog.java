package com.pfms.presentation.wallet.dialog;

import com.pfms.presentation.common.dialog.BaseFormDialog;
import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.wallet.WalletGUIController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Hộp thoại modal kiểm kê điều chỉnh số dư ví thực tế (UC-WAL-04) sử dụng JavaFX.
 * Kế thừa BaseFormDialog theo mô hình Template Method, loại bỏ boilerplate lặp lại.
 */
public class AdjustBalanceDialog extends BaseFormDialog<BalanceAdjustmentResponseDTO> {

    private final WalletGUIController walletController;
    private final WalletResponseDTO wallet;
    private final TextField actualBalanceField;
    private final TextField reasonField;
    private final TextField dateField;
    private Consumer<String> infoConsumer;

    public AdjustBalanceDialog(Window owner, WalletGUIController walletController, WalletResponseDTO wallet) {
        super(owner, "Kiểm Kê Điều Chỉnh Số Dư Ví", 440);
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        this.wallet = Objects.requireNonNull(wallet, "WalletResponseDTO không được để null");

        var headerBox = createHeaderBox("Kiểm Kê & Điều Chỉnh Số Dư", "Cập nhật số dư thực tế cho ví " + wallet.getName());

        // Khối hiển thị số dư sổ sách hiện tại
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

        HBox balanceCard = new HBox(8);
        balanceCard.setAlignment(Pos.CENTER_LEFT);
        balanceCard.setPadding(new Insets(10, 14, 10, 14));
        balanceCard.setStyle("-fx-background-color: rgba(212, 175, 55, 0.08); " +
                "-fx-border-color: -color-gold-border; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        Label balanceTitle = new Label("Số dư sổ sách hiện tại:");
        balanceTitle.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label currentBalanceLabel = new Label(formatter.format(wallet.getBalance()) + " " + wallet.getCurrency());
        currentBalanceLabel.setStyle("-fx-text-fill: -color-primary-light; -fx-font-weight: bold; -fx-font-size: 13px;");
        balanceCard.getChildren().addAll(balanceTitle, spacer, currentBalanceLabel);

        // Trường nhập liệu
        this.actualBalanceField = new TextField();
        this.actualBalanceField.setPromptText("Nhập số dư thực tế...");

        this.reasonField = new TextField();
        this.reasonField.setPromptText("Lý do điều chỉnh...");

        this.dateField = new TextField(LocalDate.now().toString());

        Label actualLabel = new Label("Số dư thực tế (*):");
        actualLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label reasonLabel = new Label("Lý do điều chỉnh (*):");
        reasonLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label dateLabel = new Label("Ngày điều chỉnh (YYYY-MM-DD):");
        dateLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 12px;");

        var form = createFormBox(10,
                headerBox,
                balanceCard,
                actualLabel, this.actualBalanceField,
                reasonLabel, this.reasonField,
                dateLabel, this.dateField
        );
        getDialogPane().setContent(form);

        setupButtons("Xác nhận điều chỉnh", "Hủy bỏ");
    }

    public AdjustBalanceDialog(WalletGUIController walletController, WalletResponseDTO wallet) {
        this((Window) null, walletController, wallet);
    }

    public AdjustBalanceDialog(Object dummyParent, WalletGUIController walletController, WalletResponseDTO wallet) {
        this(dummyParent instanceof Window w ? w : null, walletController, wallet);
    }

    @Override
    protected BalanceAdjustmentResponseDTO onExecuteSave() throws Exception {
        String rawBalance = this.actualBalanceField.getText();
        String reason = this.reasonField.getText();
        String date = this.dateField.getText();

        BalanceAdjustmentResponseDTO res = this.walletController.handleAdjustBalance(
                this.wallet.getWalletId(), rawBalance, reason, date);

        if (this.infoConsumer != null) {
            this.infoConsumer.accept("Kiểm kê số dư thành công!\nChênh lệch điều chỉnh: " +
                    res.getDifference() + " VNĐ");
        }
        return res;
    }

    public void triggerAdjust() {
        triggerSave();
    }

    public BalanceAdjustmentResponseDTO getAdjustmentResponse() {
        return this.resultData;
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

    public void setInfoConsumer(Consumer<String> infoConsumer) {
        this.infoConsumer = infoConsumer;
    }
}
