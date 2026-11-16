package com.pfms.presentation.transfer.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.dialog.BaseFormDialog;
import com.pfms.presentation.common.util.FxControlUtils;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.transfer.TransferGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Hộp thoại modal điều chuyển tiền giữa hai ví tài chính (UC-TRF-01) sử dụng JavaFX.
 * Hiện thực giao diện Code-First theo nhận diện Luxury Black & Gold và kế thừa BaseFormDialog.
 */
public class TransferFundsDialog extends BaseFormDialog<TransferResponseDTO> {

    private final TransferGUIController transferController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;

    private final ComboBox<WalletResponseDTO> sourceWalletCombo = new ComboBox<>();
    private final Label sourceBalanceHintLabel = new Label("Số dư: 0 VNĐ");
    private final ComboBox<WalletResponseDTO> destWalletCombo = new ComboBox<>();
    private final Label destBalanceHintLabel = new Label("Số dư: 0 VNĐ");

    private final TextField amountField = new TextField();
    private final Label amountInWordsHint = new Label("");
    private final TextField dateField = new TextField();
    private final TextField noteField = new TextField();

    private final CheckBox feeCheckBox = new CheckBox("Giao dịch phát sinh phụ phí chuyển khoản");
    private final VBox feeDetailsPanel = new VBox(8);
    private final TextField feeAmountField = new TextField("0");
    private final ComboBox<CategoryHierarchyResponseDTO> feeGroupCombo = new ComboBox<>();
    private final ComboBox<CategoryResponseDTO> feeCategoryCombo = new ComboBox<>();

    private final List<CategoryHierarchyResponseDTO> feeExpenseGroups = new ArrayList<>();
    private final DecimalFormat currencyFormatter;

    public TransferFundsDialog(
            Window owner,
            TransferGUIController transferController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {

        super(owner, "Chuyển Tiền Nội Bộ", 520);
        this.transferController = Objects.requireNonNull(transferController, "TransferGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);

        buildUI();
        loadFeeCategories();
        loadWallets();
    }

    public TransferFundsDialog(
            Object parent,
            TransferGUIController transferController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {
        this((Window) null, transferController, walletController, categoryController);
    }

    private void buildUI() {
        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("pfms-dialog");

        VBox contentBox = new VBox(12);
        contentBox.setPadding(new Insets(18, 22, 18, 22));
        contentBox.setStyle("-fx-background-color: #141414;");

        var headerBox = createHeaderBox("Điều Chuyển Tiền Giữa Các Ví", "Thực hiện chuyển quỹ nội bộ giữa hai ví tài chính và ghi nhận phụ phí");

        // 1. Wallets selection section
        VBox sourceBox = new VBox(4);
        Label lblSource = new Label("Ví chuyển đi (Ví nguồn) (*):");
        lblSource.getStyleClass().add("form-label");
        sourceWalletCombo.setMaxWidth(Double.MAX_VALUE);
        setupWalletComboRenderer(sourceWalletCombo);
        sourceBalanceHintLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");
        sourceBox.getChildren().addAll(lblSource, sourceWalletCombo, sourceBalanceHintLabel);

        VBox destBox = new VBox(4);
        Label lblDest = new Label("Ví nhận tiền (Ví đích) (*):");
        lblDest.getStyleClass().add("form-label");
        destWalletCombo.setMaxWidth(Double.MAX_VALUE);
        setupWalletComboRenderer(destWalletCombo);
        destBalanceHintLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");
        destBox.getChildren().addAll(lblDest, destWalletCombo, destBalanceHintLabel);

        sourceWalletCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateWalletBalanceHints());
        destWalletCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateWalletBalanceHints());

        // 2. Amount and Date section
        VBox amountBox = new VBox(4);
        Label lblAmount = new Label("Số tiền chuyển (*):");
        lblAmount.getStyleClass().add("form-label");
        amountField.setPromptText("Nhập số tiền (ví dụ: 500000)");
        amountInWordsHint.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 11px; -fx-font-style: italic;");
        amountField.textProperty().addListener((obs, oldVal, newVal) -> updateAmountHint(newVal));
        amountBox.getChildren().addAll(lblAmount, amountField, amountInWordsHint);

        VBox dateBox = new VBox(4);
        Label lblDate = new Label("Ngày thực hiện (yyyy-MM-dd) (*):");
        lblDate.getStyleClass().add("form-label");
        dateField.setText(LocalDate.now().toString());
        dateBox.getChildren().addAll(lblDate, dateField);

        // 3. Note
        VBox noteBox = new VBox(4);
        Label lblNote = new Label("Ghi chú giao dịch:");
        lblNote.getStyleClass().add("form-label");
        noteField.setPromptText("Nội dung chuyển khoản (tùy chọn)");
        noteBox.getChildren().addAll(lblNote, noteField);

        // 4. Fee Section (Accordion Card đồng bộ)
        VBox feeSectionBox = new VBox(8);
        feeSectionBox.setStyle("-fx-background-color: #0E0E0E; -fx-border-color: -color-border; " +
                "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 12px 14px;");

        feeCheckBox.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label lblFeeAmount = new Label("Số tiền phụ phí:");
        lblFeeAmount.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 11px;");
        feeAmountField.setMaxWidth(Double.MAX_VALUE);

        Label lblFeeGroup = new Label("Nhóm phụ phí:");
        lblFeeGroup.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 11px;");
        feeGroupCombo.setMaxWidth(Double.MAX_VALUE);
        FxControlUtils.configureComboBox(feeGroupCombo, CategoryHierarchyResponseDTO::getNeedGroupName, "-- Chọn nhóm chi phí --");

        Label lblFeeCat = new Label("Danh mục phụ phí:");
        lblFeeCat.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 11px;");
        feeCategoryCombo.setMaxWidth(Double.MAX_VALUE);
        FxControlUtils.configureComboBox(feeCategoryCombo, CategoryResponseDTO::getName, "-- Chọn danh mục chi tiết --");

        feeGroupCombo.valueProperty().addListener((obs, oldVal, newVal) -> onFeeGroupSelected(newVal));

        feeDetailsPanel.getChildren().addAll(lblFeeAmount, feeAmountField, lblFeeGroup, feeGroupCombo, lblFeeCat, feeCategoryCombo);
        feeDetailsPanel.setVisible(false);
        feeDetailsPanel.setManaged(false);

        feeAmountField.setDisable(true);
        feeGroupCombo.setDisable(true);
        feeCategoryCombo.setDisable(true);

        feeCheckBox.selectedProperty().addListener((obs, oldVal, isSelected) -> onFeeCheckBoxToggled(isSelected));

        feeSectionBox.getChildren().addAll(feeCheckBox, feeDetailsPanel);

        contentBox.getChildren().addAll(headerBox, sourceBox, destBox, amountBox, dateBox, noteBox, feeSectionBox);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #141414; -fx-background-color: transparent; -fx-border-color: transparent;");

        pane.setContent(scrollPane);

        // Buttons
        ButtonType transferButtonType = new ButtonType("Xác nhận chuyển tiền", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(transferButtonType, cancelButtonType);

        Button btnTransfer = (Button) pane.lookupButton(transferButtonType);
        if (btnTransfer != null) {
            btnTransfer.getStyleClass().add("btn-primary");
            btnTransfer.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                event.consume();
                triggerTransfer();
            });
        }

        Button btnCancel = (Button) pane.lookupButton(cancelButtonType);
        if (btnCancel != null) {
            btnCancel.getStyleClass().add("btn-secondary");
        }
    }

    private void setupWalletComboRenderer(ComboBox<WalletResponseDTO> combo) {
        FxControlUtils.configureComboBox(
                combo,
                w -> w.getName() + " (" + formatCurrency(w.getBalance()) + ")",
                "-- Chọn ví tài chính --");
    }

    private void onFeeGroupSelected(CategoryHierarchyResponseDTO selectedGroup) {
        feeCategoryCombo.getItems().clear();
        if (selectedGroup != null && selectedGroup.getCategories() != null) {
            List<CategoryResponseDTO> activeCats = selectedGroup.getCategories().stream()
                    .filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus()))
                    .toList();
            feeCategoryCombo.getItems().setAll(activeCats);
            feeCategoryCombo.setDisable(activeCats.isEmpty());
            if (!activeCats.isEmpty()) {
                feeCategoryCombo.setValue(activeCats.get(0));
            }
        } else {
            feeCategoryCombo.setDisable(true);
        }
    }

    private void onFeeCheckBoxToggled(boolean isSelected) {
        feeDetailsPanel.setVisible(isSelected);
        feeDetailsPanel.setManaged(isSelected);
        feeAmountField.setDisable(!isSelected);
        feeGroupCombo.setDisable(!isSelected);
        feeCategoryCombo.setDisable(true);

        if (!isSelected) {
            feeAmountField.setText("0");
            feeGroupCombo.getSelectionModel().clearSelection();
            feeCategoryCombo.getItems().clear();
        }
    }

    private void updateWalletBalanceHints() {
        WalletResponseDTO src = sourceWalletCombo.getValue();
        if (src != null && src.getBalance() != null) {
            sourceBalanceHintLabel.setText("Số dư khả dụng: " + formatCurrency(src.getBalance()));
        } else {
            sourceBalanceHintLabel.setText("Số dư: 0 VNĐ");
        }

        WalletResponseDTO dest = destWalletCombo.getValue();
        if (dest != null && dest.getBalance() != null) {
            destBalanceHintLabel.setText("Số dư hiện tại: " + formatCurrency(dest.getBalance()));
        } else {
            destBalanceHintLabel.setText("Số dư: 0 VNĐ");
        }
    }

    private void updateAmountHint(String text) {
        if (text == null || text.isBlank()) {
            amountInWordsHint.setText("");
            return;
        }
        try {
            BigDecimal val = new BigDecimal(text.trim());
            amountInWordsHint.setText(currencyFormatter.format(val) + " VNĐ");
        } catch (Exception ex) {
            amountInWordsHint.setText("");
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VNĐ";
        return currencyFormatter.format(amount) + " VNĐ";
    }

    private void loadWallets() {
        try {
            WalletsSummaryResponseDTO summary = walletController.loadWalletsSummary();
            if (summary != null && summary.getWallets() != null) {
                List<WalletResponseDTO> activeWallets = summary.getWallets().stream()
                        .filter(w -> "ACTIVE".equalsIgnoreCase(w.getStatus()))
                        .toList();

                sourceWalletCombo.getItems().setAll(activeWallets);
                destWalletCombo.getItems().setAll(activeWallets);

                if (!activeWallets.isEmpty()) {
                    sourceWalletCombo.setValue(activeWallets.get(0));
                    if (activeWallets.size() > 1) {
                        destWalletCombo.setValue(activeWallets.get(1));
                    } else {
                        destWalletCombo.setValue(activeWallets.get(0));
                    }
                }
            }
            updateWalletBalanceHints();
        } catch (Exception ignored) {
        }
    }

    private void loadFeeCategories() {
        try {
            List<CategoryHierarchyResponseDTO> hierarchy = categoryController.loadCategoryHierarchy();
            feeExpenseGroups.clear();
            if (hierarchy != null) {
                for (CategoryHierarchyResponseDTO group : hierarchy) {
                    if ("EXPENSE".equalsIgnoreCase(group.getTransactionType())
                            && "ACTIVE".equalsIgnoreCase(group.getNeedGroupStatus())) {
                        feeExpenseGroups.add(group);
                    }
                }
            }
            feeGroupCombo.getItems().setAll(feeExpenseGroups);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected TransferResponseDTO onExecuteSave() throws Exception {
        WalletResponseDTO sourceWallet = sourceWalletCombo.getValue();
        WalletResponseDTO destWallet = destWalletCombo.getValue();

        if (sourceWallet == null || destWallet == null) {
            throw new IllegalArgumentException("Vui lòng chọn ví nguồn và ví đích.");
        }

        if (sourceWallet.getWalletId().equals(destWallet.getWalletId())) {
            throw new IllegalArgumentException("Ví nguồn và ví đích không được trùng nhau.");
        }

        String rawAmount = getAmountInputValue();
        String rawDate = getDateInputValue();
        String rawNote = getNoteInputValue();

        String rawFeeAmount = "0";
        UUID feeCatId = null;
        if (feeCheckBox.isSelected()) {
            rawFeeAmount = feeAmountField.getText();
            CategoryResponseDTO feeCat = feeCategoryCombo.getValue();
            if (feeCat != null) {
                feeCatId = feeCat.getCategoryId();
            } else {
                throw new IllegalArgumentException("Vui lòng chọn nhóm và danh mục phụ phí.");
            }
        }

        return this.transferController.handleTransferFunds(
                sourceWallet.getWalletId(),
                destWallet.getWalletId(),
                rawAmount,
                rawDate,
                rawNote,
                rawFeeAmount,
                feeCatId);
    }

    public void triggerTransfer() {
        triggerSave();
    }

    public TransferResponseDTO getCreatedTransfer() {
        return this.resultData;
    }

    public WalletResponseDTO getSourceWallet() {
        return this.sourceWalletCombo.getValue();
    }

    public void setSourceWallet(WalletResponseDTO wallet) {
        this.sourceWalletCombo.setValue(wallet);
        updateWalletBalanceHints();
    }

    public WalletResponseDTO getDestinationWallet() {
        return this.destWalletCombo.getValue();
    }

    public void setDestinationWallet(WalletResponseDTO wallet) {
        this.destWalletCombo.setValue(wallet);
        updateWalletBalanceHints();
    }

    public String getAmountInputValue() {
        return this.amountField.getText() != null ? this.amountField.getText().trim() : "";
    }

    public void setAmountInputValue(String amount) {
        this.amountField.setText(amount);
    }

    public String getDateInputValue() {
        return this.dateField.getText() != null ? this.dateField.getText().trim() : "";
    }

    public void setDateInputValue(String date) {
        this.dateField.setText(date);
    }

    public String getNoteInputValue() {
        return this.noteField.getText() != null ? this.noteField.getText().trim() : "";
    }

    public void setNoteInputValue(String note) {
        this.noteField.setText(note);
    }

    public ComboBox<CategoryHierarchyResponseDTO> getFeeGroupCombo() {
        return this.feeGroupCombo;
    }

    public ComboBox<CategoryResponseDTO> getFeeCategoryCombo() {
        return this.feeCategoryCombo;
    }

    public CheckBox getFeeCheckBox() {
        return this.feeCheckBox;
    }

    public TextField getFeeAmountField() {
        return this.feeAmountField;
    }

    public VBox getFeeDetailsPanel() {
        return this.feeDetailsPanel;
    }

    public Label getSourceBalanceHintLabel() {
        return this.sourceBalanceHintLabel;
    }

    public Label getDestBalanceHintLabel() {
        return this.destBalanceHintLabel;
    }

    public Label getAmountInWordsHint() {
        return this.amountInWordsHint;
    }

    public void setFeeData(boolean hasFee, String feeAmount, CategoryResponseDTO feeCategory) {
        this.feeCheckBox.setSelected(hasFee);
        this.feeAmountField.setDisable(!hasFee);
        this.feeGroupCombo.setDisable(!hasFee);
        this.feeAmountField.setText(feeAmount != null ? feeAmount : "0");
        if (hasFee && feeCategory != null) {
            for (CategoryHierarchyResponseDTO g : this.feeGroupCombo.getItems()) {
                if (g != null && g.getNeedGroupId().equals(feeCategory.getParentGroupId())) {
                    this.feeGroupCombo.setValue(g);
                    break;
                }
            }
            this.feeCategoryCombo.setValue(feeCategory);
            this.feeCategoryCombo.setDisable(false);
        } else {
            this.feeCategoryCombo.setDisable(true);
        }
    }
}
