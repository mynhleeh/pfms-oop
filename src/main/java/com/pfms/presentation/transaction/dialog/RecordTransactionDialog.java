package com.pfms.presentation.transaction.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.dialog.BaseFormDialog;
import com.pfms.presentation.common.util.FxControlUtils;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.transaction.TransactionGUIController;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
 * Hộp thoại modal ghi nhận giao dịch thu chi mới (UC-TRN-03) sử dụng JavaFX.
 * Hiện thực giao diện Code-First theo nhận diện Luxury Black & Gold và kế thừa BaseFormDialog.
 */
public class RecordTransactionDialog extends BaseFormDialog<TransactionResponseDTO> {

    private final TransactionGUIController transactionController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;

    private final FxButton expenseTypeBtn = new FxButton("Chi tiêu");
    private final FxButton incomeTypeBtn = new FxButton("Thu nhập");
    private final ComboBox<String> typeCombo = new ComboBox<>();

    private final ComboBox<WalletResponseDTO> walletCombo = new ComboBox<>();
    private final Label availableBalanceLabel = new Label();

    private final ComboBox<CategoryHierarchyResponseDTO> groupCombo = new ComboBox<>();
    private final ComboBox<CategoryResponseDTO> categoryCombo = new ComboBox<>();

    private final TextField amountField = new TextField();
    private final Label amountInWordsHint = new Label("0 VNĐ");
    private final TextField dateField = new TextField();
    private final TextField noteField = new TextField();

    private final FxCheckBox feeCheckBox = new FxCheckBox("Giao dịch phát sinh phụ phí");
    private final VBox feeDetailsPanel = new VBox(8);
    private final TextField feeAmountField = new TextField("0");
    private final ComboBox<CategoryHierarchyResponseDTO> feeGroupCombo = new ComboBox<>();
    private final ComboBox<CategoryResponseDTO> feeCategoryCombo = new ComboBox<>();

    private final FxButton saveButton = new FxButton("Lưu giao dịch");
    private final FxButton cancelButton = new FxButton("Hủy bỏ");

    private final List<CategoryHierarchyResponseDTO> expenseGroups = new ArrayList<>();
    private final List<CategoryHierarchyResponseDTO> incomeGroups = new ArrayList<>();
    private final DecimalFormat currencyFormatter;

    public RecordTransactionDialog(
            Window owner,
            TransactionGUIController transactionController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {

        super(owner, "Ghi Nhận Giao Dịch Thu Chi", 520);
        this.transactionController = Objects.requireNonNull(transactionController, "TransactionGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);

        typeCombo.getItems().addAll("Chi tiêu", "Thu nhập");
        typeCombo.setValue("Chi tiêu");

        buildUI();
        loadCategories();
        loadWallets();
        updateGroupsForSelectedType();
        updateAmountHint();
    }

    public RecordTransactionDialog(
            Object parent,
            TransactionGUIController transactionController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {
        this((Window) null, transactionController, walletController, categoryController);
    }

    private void buildUI() {
        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("pfms-dialog");

        VBox contentBox = new VBox(12);
        contentBox.setPadding(new Insets(18, 22, 18, 22));
        contentBox.setStyle("-fx-background-color: #141414;");

        var headerBox = createHeaderBox("Ghi Nhận Giao Dịch Thu Chi Mới", "Nhập thông tin giao dịch thu chi cá nhân và phân loại danh mục");

        // 1. Type Switcher (Chi tiêu vs Thu nhập)
        HBox typeSwitcher = new HBox(6);
        typeSwitcher.setStyle("-fx-background-color: #0E0E0E; -fx-padding: 4px; -fx-border-color: -color-border; " +
                "-fx-border-radius: 8px; -fx-background-radius: 8px;");
        expenseTypeBtn.setMaxWidth(Double.MAX_VALUE);
        incomeTypeBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(expenseTypeBtn, Priority.ALWAYS);
        HBox.setHgrow(incomeTypeBtn, Priority.ALWAYS);

        expenseTypeBtn.setOnAction(e -> {
            typeCombo.setValue("Chi tiêu");
            updateTypeSwitcherUI();
            updateGroupsForSelectedType();
        });

        incomeTypeBtn.setOnAction(e -> {
            typeCombo.setValue("Thu nhập");
            updateTypeSwitcherUI();
            updateGroupsForSelectedType();
        });

        updateTypeSwitcherUI();
        typeSwitcher.getChildren().addAll(expenseTypeBtn, incomeTypeBtn);

        // 2. Wallet selection
        VBox walletBox = new VBox(4);
        Label lblWallet = new Label("VÍ TÀI KHOẢN (*):");
        lblWallet.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        walletCombo.setMaxWidth(Double.MAX_VALUE);
        setupWalletComboRenderer();
        availableBalanceLabel.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 11px;");
        walletCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateAvailableBalance());
        walletBox.getChildren().addAll(lblWallet, walletCombo, availableBalanceLabel);

        // 3. Category Group & Detail Category (Cascading)
        VBox groupBox = new VBox(4);
        Label lblGroup = new Label("NHÓM PHÂN LOẠI / NHU CẦU (*):");
        lblGroup.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        groupCombo.setMaxWidth(Double.MAX_VALUE);
        setupGroupComboRenderer();
        groupCombo.valueProperty().addListener((obs, oldVal, newVal) -> onGroupSelected());
        groupBox.getChildren().addAll(lblGroup, groupCombo);

        VBox categoryBox = new VBox(4);
        Label lblCategory = new Label("DANH MỤC CHI TIẾT (*):");
        lblCategory.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        categoryCombo.setMaxWidth(Double.MAX_VALUE);
        setupCategoryComboRenderer();
        categoryCombo.setDisable(true);
        categoryBox.getChildren().addAll(lblCategory, categoryCombo);

        // 4. Amount and live hint
        VBox amountBox = new VBox(4);
        Label lblAmount = new Label("SỐ TIỀN GIAO DỊCH (*):");
        lblAmount.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        amountField.setPromptText("Nhập số tiền (ví dụ: 150000)");
        amountField.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        amountInWordsHint.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 11px; -fx-font-style: italic;");
        amountField.textProperty().addListener((obs, oldVal, newVal) -> updateAmountHint());
        amountBox.getChildren().addAll(lblAmount, amountField, amountInWordsHint);

        // 5. Date & Note
        VBox dateBox = new VBox(4);
        Label lblDate = new Label("NGÀY GIAO DỊCH (YYYY-MM-DD) (*):");
        lblDate.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        dateField.setText(LocalDate.now().toString());
        dateBox.getChildren().addAll(lblDate, dateField);

        VBox noteBox = new VBox(4);
        Label lblNote = new Label("GHI CHÚ / DIỄN GIẢI:");
        lblNote.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        noteField.setPromptText("Nội dung diễn giải giao dịch (tùy chọn)");
        noteBox.getChildren().addAll(lblNote, noteField);

        // 6. Fee Section (Accordion Card)
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
        setupFeeGroupComboRenderer();

        Label lblFeeCat = new Label("Danh mục phụ phí:");
        lblFeeCat.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 11px;");
        feeCategoryCombo.setMaxWidth(Double.MAX_VALUE);
        setupFeeCategoryComboRenderer();

        feeGroupCombo.valueProperty().addListener((obs, oldVal, newVal) -> onFeeGroupSelected(newVal));

        feeDetailsPanel.getChildren().addAll(lblFeeAmount, feeAmountField, lblFeeGroup, feeGroupCombo, lblFeeCat, feeCategoryCombo);
        feeDetailsPanel.setVisible(false);
        feeDetailsPanel.setManaged(false);

        feeAmountField.setDisable(true);
        feeGroupCombo.setDisable(true);
        feeCategoryCombo.setDisable(true);

        feeCheckBox.selectedProperty().addListener((obs, oldVal, isSelected) -> onFeeCheckBoxToggled(isSelected));

        feeSectionBox.getChildren().addAll(feeCheckBox, feeDetailsPanel);

        contentBox.getChildren().addAll(headerBox, typeSwitcher, walletBox, groupBox, categoryBox, amountBox, dateBox, noteBox, feeSectionBox);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #141414; -fx-background-color: transparent; -fx-border-color: transparent;");
        pane.setContent(scrollPane);

        // Buttons
        ButtonType saveBtnType = new ButtonType("Lưu giao dịch", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Hủy bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(saveBtnType, cancelBtnType);

        Button btnSave = (Button) pane.lookupButton(saveBtnType);
        if (btnSave != null) {
            btnSave.setText("Lưu giao dịch");
            btnSave.setDefaultButton(true);
            btnSave.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                event.consume();
                triggerSave();
            });
        }

        Button btnCancel = (Button) pane.lookupButton(cancelBtnType);
        if (btnCancel != null) {
            btnCancel.setText("Hủy bỏ");
        }

        saveButton.setOnAction(e -> triggerSave());
        cancelButton.setOnAction(e -> close());
    }

    private void updateTypeSwitcherUI() {
        boolean isIncome = "Thu nhập".equalsIgnoreCase(typeCombo.getValue());
        if (isIncome) {
            incomeTypeBtn.setStyle("-fx-background-color: rgba(34, 197, 94, 0.22); -fx-text-fill: #4ADE80; " +
                    "-fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: rgba(34, 197, 94, 0.5); " +
                    "-fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 8px 16px; -fx-cursor: hand;");
            expenseTypeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: -color-text-muted; " +
                    "-fx-font-weight: normal; -fx-font-size: 13px; -fx-border-color: transparent; " +
                    "-fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 8px 16px; -fx-cursor: hand;");
        } else {
            expenseTypeBtn.setStyle("-fx-background-color: rgba(225, 29, 72, 0.22); -fx-text-fill: #FB7185; " +
                    "-fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: rgba(225, 29, 72, 0.5); " +
                    "-fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 8px 16px; -fx-cursor: hand;");
            incomeTypeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: -color-text-muted; " +
                    "-fx-font-weight: normal; -fx-font-size: 13px; -fx-border-color: transparent; " +
                    "-fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 8px 16px; -fx-cursor: hand;");
        }
    }

    private void setupWalletComboRenderer() {
        FxControlUtils.configureComboBox(walletCombo, WalletResponseDTO::getName, "-- Chọn ví tài chính --");
    }

    private void setupGroupComboRenderer() {
        FxControlUtils.configureComboBox(groupCombo, CategoryHierarchyResponseDTO::getNeedGroupName, "-- Chọn nhóm danh mục --");
    }

    private void setupCategoryComboRenderer() {
        FxControlUtils.configureComboBox(categoryCombo, CategoryResponseDTO::getName, "-- Chọn danh mục --");
    }

    private void setupFeeGroupComboRenderer() {
        FxControlUtils.configureComboBox(feeGroupCombo, CategoryHierarchyResponseDTO::getNeedGroupName, "-- Chọn nhóm phụ phí --");
    }

    private void setupFeeCategoryComboRenderer() {
        FxControlUtils.configureComboBox(feeCategoryCombo, CategoryResponseDTO::getName, "-- Chọn danh mục phụ phí --");
    }

    private void updateGroupsForSelectedType() {
        String selectedType = typeCombo.getValue();
        List<CategoryHierarchyResponseDTO> targetList = "Thu nhập".equalsIgnoreCase(selectedType) ? incomeGroups : expenseGroups;
        groupCombo.getItems().setAll(targetList);
        groupCombo.setValue(null);
        categoryCombo.getItems().clear();
        categoryCombo.setDisable(true);
    }

    private void onGroupSelected() {
        CategoryHierarchyResponseDTO selectedGroup = groupCombo.getValue();
        categoryCombo.getItems().clear();
        if (selectedGroup != null && selectedGroup.getCategories() != null && !selectedGroup.getCategories().isEmpty()) {
            categoryCombo.getItems().addAll(selectedGroup.getCategories());
            categoryCombo.setDisable(false);
            categoryCombo.getSelectionModel().selectFirst();
        } else {
            categoryCombo.setDisable(true);
        }
    }

    private void onFeeGroupSelected(CategoryHierarchyResponseDTO group) {
        feeCategoryCombo.getItems().clear();
        if (group != null && group.getCategories() != null && !group.getCategories().isEmpty()) {
            feeCategoryCombo.getItems().addAll(group.getCategories());
            feeCategoryCombo.setDisable(false);
            feeCategoryCombo.getSelectionModel().selectFirst();
        } else {
            feeCategoryCombo.setDisable(true);
        }
    }

    private void onFeeCheckBoxToggled(boolean isSelected) {
        feeDetailsPanel.setVisible(isSelected);
        feeDetailsPanel.setManaged(isSelected);
        feeAmountField.setDisable(!isSelected);
        feeGroupCombo.setDisable(!isSelected);

        if (!isSelected) {
            feeAmountField.setText("0");
            feeGroupCombo.getSelectionModel().clearSelection();
            feeCategoryCombo.getItems().clear();
            feeCategoryCombo.setDisable(true);
        } else {
            if (!feeGroupCombo.getItems().isEmpty() && feeGroupCombo.getValue() == null) {
                feeCategoryCombo.setDisable(true);
            }
        }
    }

    private void updateAvailableBalance() {
        WalletResponseDTO w = walletCombo.getValue();
        if (w != null && w.getBalance() != null) {
            availableBalanceLabel.setText("Số dư khả dụng: " + currencyFormatter.format(w.getBalance()) + " VNĐ");
            availableBalanceLabel.setVisible(true);
        } else {
            availableBalanceLabel.setText("");
            availableBalanceLabel.setVisible(false);
        }
    }

    private void updateAmountHint() {
        String text = amountField.getText();
        if (text == null || text.isBlank()) {
            amountInWordsHint.setText("0 VNĐ");
            return;
        }
        try {
            BigDecimal val = new BigDecimal(text.trim());
            amountInWordsHint.setText(currencyFormatter.format(val) + " VNĐ");
        } catch (Exception e) {
            amountInWordsHint.setText("0 VNĐ");
        }
    }

    private void loadWallets() {
        try {
            WalletsSummaryResponseDTO summary = walletController.loadWalletsSummary();
            if (summary != null && summary.getWallets() != null) {
                List<WalletResponseDTO> active = summary.getWallets().stream()
                        .filter(w -> "ACTIVE".equalsIgnoreCase(w.getStatus()))
                        .toList();
                walletCombo.getItems().setAll(active);
                if (!active.isEmpty()) {
                    walletCombo.setValue(active.get(0));
                }
            }
            updateAvailableBalance();
        } catch (Exception ignored) {
        }
    }

    private void loadCategories() {
        try {
            List<CategoryHierarchyResponseDTO> hierarchy = categoryController.loadCategoryHierarchy();
            expenseGroups.clear();
            incomeGroups.clear();
            if (hierarchy != null) {
                for (CategoryHierarchyResponseDTO g : hierarchy) {
                    if ("ACTIVE".equalsIgnoreCase(g.getNeedGroupStatus())) {
                        if ("EXPENSE".equalsIgnoreCase(g.getTransactionType())) {
                            expenseGroups.add(g);
                        } else if ("INCOME".equalsIgnoreCase(g.getTransactionType())) {
                            incomeGroups.add(g);
                        }
                    }
                }
            }
            feeGroupCombo.getItems().setAll(expenseGroups);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected TransactionResponseDTO onExecuteSave() throws Exception {
        WalletResponseDTO wallet = walletCombo.getValue();
        CategoryHierarchyResponseDTO group = getSelectedGroup();
        CategoryResponseDTO category = categoryCombo.getValue();

        if (wallet == null) {
            throw new IllegalArgumentException("Vui lòng chọn ví tài chính.");
        }
        if (group == null) {
            throw new IllegalArgumentException("Vui lòng chọn nhóm danh mục.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Vui lòng chọn danh mục thu chi.");
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

        return this.transactionController.handleRecordTransaction(
                wallet.getWalletId(),
                category.getCategoryId(),
                rawAmount,
                rawDate,
                rawNote,
                rawFeeAmount,
                feeCatId);
    }

    public TransactionResponseDTO getCreatedTransaction() {
        return this.resultData;
    }

    public String getSelectedTransactionType() { return typeCombo.getValue(); }
    public void setSelectedTransactionType(String type) {
        typeCombo.setValue(type);
        updateGroupsForSelectedType();
        updateTypeSwitcherUI();
    }

    public String getAmountInputValue() { return amountField.getText() != null ? amountField.getText().trim() : ""; }
    public void setAmountInputValue(String amount) {
        amountField.setText(amount);
        updateAmountHint();
    }

    public WalletResponseDTO getSelectedWallet() { return walletCombo.getValue(); }
    public void setSelectedWallet(WalletResponseDTO wallet) {
        walletCombo.setValue(wallet);
        updateAvailableBalance();
    }

    public Label getAvailableBalanceLabel() { return availableBalanceLabel; }
    public ComboBox<WalletResponseDTO> getWalletCombo() { return walletCombo; }
    public ComboBox<CategoryHierarchyResponseDTO> getGroupCombo() { return groupCombo; }

    public CategoryHierarchyResponseDTO getSelectedGroup() { return groupCombo.getValue(); }
    public void setSelectedGroup(CategoryHierarchyResponseDTO group) {
        groupCombo.setValue(group);
        onGroupSelected();
    }

    public ComboBox<CategoryResponseDTO> getCategoryCombo() { return categoryCombo; }
    public CategoryResponseDTO getSelectedCategory() { return categoryCombo.getValue(); }

    public void setSelectedCategory(CategoryResponseDTO category) {
        if (category == null) {
            categoryCombo.setValue(null);
            return;
        }
        for (CategoryHierarchyResponseDTO g : groupCombo.getItems()) {
            if (g.getNeedGroupId().equals(category.getParentGroupId())) {
                groupCombo.setValue(g);
                onGroupSelected();
                break;
            }
        }
        categoryCombo.setValue(category);
    }

    public FxButton getCancelButton() { return cancelButton; }
    public FxButton getSaveButton() { return saveButton; }
    public FxButton getExpenseTypeBtn() { return expenseTypeBtn; }
    public FxButton getIncomeTypeBtn() { return incomeTypeBtn; }
    public FxCheckBox getFeeCheckBox() { return feeCheckBox; }

    public String getDateInputValue() { return dateField.getText() != null ? dateField.getText().trim() : ""; }
    public void setDateInputValue(String date) { dateField.setText(date); }

    public String getNoteInputValue() { return noteField.getText() != null ? noteField.getText().trim() : ""; }
    public void setNoteInputValue(String note) { noteField.setText(note); }

    public Label getAmountInWordsHint() { return amountInWordsHint; }
    public VBox getFeeDetailsPanel() { return feeDetailsPanel; }
    public TextField getFeeAmountField() { return feeAmountField; }
    public ComboBox<CategoryHierarchyResponseDTO> getFeeGroupCombo() { return feeGroupCombo; }
    public ComboBox<CategoryResponseDTO> getFeeCategoryCombo() { return feeCategoryCombo; }

    public void setFeeData(boolean hasFee, String feeAmount, CategoryResponseDTO feeCategory) {
        feeCheckBox.setSelected(hasFee);
        feeAmountField.setDisable(!hasFee);
        feeGroupCombo.setDisable(!hasFee);
        feeAmountField.setText(feeAmount != null ? feeAmount : "0");
        if (hasFee && feeCategory != null) {
            for (CategoryHierarchyResponseDTO g : feeGroupCombo.getItems()) {
                if (g.getNeedGroupId().equals(feeCategory.getParentGroupId())) {
                    feeGroupCombo.setValue(g);
                    onFeeGroupSelected(g);
                    break;
                }
            }
            feeCategoryCombo.setValue(feeCategory);
            feeCategoryCombo.setDisable(false);
        } else {
            feeCategoryCombo.setDisable(true);
        }
    }

    public static class FxButton extends Button {
        public FxButton(String text) { super(text); }
        public void doClick() { fire(); }
    }

    public static class FxCheckBox extends CheckBox {
        public FxCheckBox(String text) { super(text); }
        public void doClick() { fire(); }
    }
}
