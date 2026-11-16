package com.pfms.presentation.transaction.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.transaction.TransactionGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Hộp thoại modal chỉnh sửa thông tin giao dịch Thu / Chi hiện có (UC-TRN-04) sử dụng JavaFX.
 * Hiện thực giao diện Code-First theo nhận diện Luxury Black & Gold.
 */
public class UpdateTransactionDialog extends Dialog<ButtonType> {

    private final TransactionGUIController transactionController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;
    private final TransactionResponseDTO existingTxn;

    private final TextField amountField = new TextField();
    private final Label amountInWordsHint = new Label();
    private final ComboBox<WalletResponseDTO> walletCombo = new ComboBox<>();
    private final Label availableBalanceLabel = new Label();
    private final ComboBox<CategoryResponseDTO> categoryCombo = new ComboBox<>();
    private final TextField dateField = new TextField();
    private final TextField noteField = new TextField();

    private final FxCheckBox feeCheckBox = new FxCheckBox("Giao dịch phát sinh phụ phí");
    private final VBox feeDetailsPanel = new VBox(8);
    private final TextField feeAmountField = new TextField("0");
    private final ComboBox<CategoryHierarchyResponseDTO> feeGroupCombo = new ComboBox<>();
    private final ComboBox<CategoryResponseDTO> feeCategoryCombo = new ComboBox<>();

    private final FxButton saveButton = new FxButton("Lưu thay đổi");
    private final FxButton cancelButton = new FxButton("Hủy bỏ");

    private final List<CategoryHierarchyResponseDTO> expenseGroups = new ArrayList<>();
    private final List<CategoryResponseDTO> availableCategories = new ArrayList<>();
    private final DecimalFormat currencyFormatter;

    private boolean success = false;
    private TransactionResponseDTO updatedTransaction = null;
    private String errorMessage = null;

    private Consumer<String> errorConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi cập nhật giao dịch");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };

    public UpdateTransactionDialog(
            Window owner,
            TransactionGUIController transactionController,
            WalletGUIController walletController,
            CategoryGUIController categoryController,
            TransactionResponseDTO existingTxn) {

        this.transactionController = Objects.requireNonNull(transactionController, "TransactionGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");
        this.existingTxn = Objects.requireNonNull(existingTxn, "TransactionResponseDTO không được null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);

        setTitle("Chỉnh Sửa Thông Tin Giao Dịch");
        initOwner(owner);

        buildUI();
        loadCategories();
        loadWallets();
        populateExistingValues();
    }

    public UpdateTransactionDialog(
            Object parent,
            TransactionGUIController transactionController,
            WalletGUIController walletController,
            CategoryGUIController categoryController,
            TransactionResponseDTO existingTxn) {
        this((Window) null, transactionController, walletController, categoryController, existingTxn);
    }

    private void buildUI() {
        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("pfms-dialog");
        pane.setPrefWidth(520);
        pane.setMinWidth(520);

        var cssResource = getClass().getResource("/css/pfms-theme.css");
        if (cssResource != null) {
            pane.getStylesheets().add(cssResource.toExternalForm());
        }

        VBox contentBox = new VBox(12);
        contentBox.setPadding(new Insets(18, 22, 18, 22));
        contentBox.setStyle("-fx-background-color: #141414;");

        // Header Title & Type Badge
        VBox headerBox = new VBox(6);
        headerBox.setPadding(new Insets(0, 0, 6, 0));
        Label headerTitle = new Label("Chỉnh Sửa Thông Tin Giao Dịch");
        headerTitle.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 16px; -fx-font-weight: bold;");

        boolean isIncome = "INCOME".equalsIgnoreCase(existingTxn.getTransactionType());
        Label typeBadge = new Label(isIncome ? "Khoản Thu Nhập" : "Khoản Chi Tiêu");
        typeBadge.setPadding(new Insets(4, 12, 4, 12));
        if (isIncome) {
            typeBadge.setStyle("-fx-background-color: rgba(34, 197, 94, 0.22); -fx-text-fill: #4ADE80; -fx-background-radius: 4px; -fx-font-weight: bold; -fx-font-size: 12px;");
        } else {
            typeBadge.setStyle("-fx-background-color: rgba(225, 29, 72, 0.22); -fx-text-fill: #FB7185; -fx-background-radius: 4px; -fx-font-weight: bold; -fx-font-size: 12px;");
        }
        headerBox.getChildren().addAll(headerTitle, typeBadge);

        // 1. Amount input and hint
        VBox amountBox = new VBox(4);
        Label lblAmount = new Label("SỐ TIỀN GIAO DỊCH (*):");
        lblAmount.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        amountField.setPromptText("Nhập số tiền");
        amountField.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        amountInWordsHint.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 11px; -fx-font-style: italic;");
        amountField.textProperty().addListener((obs, oldVal, newVal) -> updateAmountHint());
        amountBox.getChildren().addAll(lblAmount, amountField, amountInWordsHint);

        // 2. Wallet selection
        VBox walletBox = new VBox(4);
        Label lblWallet = new Label("VÍ TÀI KHOẢN (*):");
        lblWallet.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        walletCombo.setMaxWidth(Double.MAX_VALUE);
        setupWalletComboRenderer();
        availableBalanceLabel.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 11px;");
        walletCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateAvailableBalance());
        walletBox.getChildren().addAll(lblWallet, walletCombo, availableBalanceLabel);

        // 3. Category selection
        VBox categoryBox = new VBox(4);
        Label lblCategory = new Label("DANH MỤC THU CHI (*):");
        lblCategory.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        categoryCombo.setMaxWidth(Double.MAX_VALUE);
        setupCategoryComboRenderer();
        categoryBox.getChildren().addAll(lblCategory, categoryCombo);

        // 4. Date & Note
        VBox dateBox = new VBox(4);
        Label lblDate = new Label("NGÀY GIAO DỊCH (YYYY-MM-DD) (*):");
        lblDate.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        dateBox.getChildren().addAll(lblDate, dateField);

        VBox noteBox = new VBox(4);
        Label lblNote = new Label("GHI CHÚ / DIỄN GIẢI:");
        lblNote.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        noteField.setPromptText("Nội dung diễn giải giao dịch (tùy chọn)");
        noteBox.getChildren().addAll(lblNote, noteField);

        // 5. Fee Section (Accordion Card)
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

        contentBox.getChildren().addAll(headerBox, amountBox, walletBox, categoryBox, dateBox, noteBox, feeSectionBox);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #141414; -fx-background-color: transparent; -fx-border-color: transparent;");
        pane.setContent(scrollPane);

        // Buttons
        ButtonType saveBtnType = new ButtonType("Lưu thay đổi", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Hủy bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(saveBtnType, cancelBtnType);

        Button btnSave = (Button) pane.lookupButton(saveBtnType);
        if (btnSave != null) {
            btnSave.setText("Lưu thay đổi");
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

    private void setupWalletComboRenderer() {
        walletCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(WalletResponseDTO w) {
                return w != null ? w.getName() : "-- Chọn ví tài chính --";
            }

            @Override
            public WalletResponseDTO fromString(String s) {
                return null;
            }
        });
        walletCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(WalletResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
    }

    private void setupCategoryComboRenderer() {
        categoryCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategoryResponseDTO c) {
                return c != null ? c.getName() + " [" + c.getParentGroupName() + "]" : "-- Chọn danh mục --";
            }

            @Override
            public CategoryResponseDTO fromString(String s) {
                return null;
            }
        });
        categoryCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CategoryResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " [" + item.getParentGroupName() + "]");
                }
            }
        });
    }

    private void setupFeeGroupComboRenderer() {
        feeGroupCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategoryHierarchyResponseDTO g) {
                return g != null ? g.getNeedGroupName() : "-- Chọn nhóm phụ phí --";
            }

            @Override
            public CategoryHierarchyResponseDTO fromString(String s) {
                return null;
            }
        });
        feeGroupCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CategoryHierarchyResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNeedGroupName());
            }
        });
    }

    private void setupFeeCategoryComboRenderer() {
        feeCategoryCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategoryResponseDTO c) {
                return c != null ? c.getName() : "-- Chọn danh mục phụ phí --";
            }

            @Override
            public CategoryResponseDTO fromString(String s) {
                return null;
            }
        });
        feeCategoryCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CategoryResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
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
                for (WalletResponseDTO w : active) {
                    if (w.getWalletId().equals(existingTxn.getWalletId())) {
                        walletCombo.setValue(w);
                        break;
                    }
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
            availableCategories.clear();
            String txnType = existingTxn.getTransactionType();
            if (hierarchy != null) {
                for (CategoryHierarchyResponseDTO g : hierarchy) {
                    if ("ACTIVE".equalsIgnoreCase(g.getNeedGroupStatus())) {
                        if ("EXPENSE".equalsIgnoreCase(g.getTransactionType())) {
                            expenseGroups.add(g);
                        }
                        if (txnType.equalsIgnoreCase(g.getTransactionType()) && g.getCategories() != null) {
                            availableCategories.addAll(g.getCategories());
                        }
                    }
                }
            }
            categoryCombo.getItems().setAll(availableCategories);
            feeGroupCombo.getItems().setAll(expenseGroups);

            for (CategoryResponseDTO c : availableCategories) {
                if (c.getCategoryId().equals(existingTxn.getCategoryId())) {
                    categoryCombo.setValue(c);
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void populateExistingValues() {
        if (existingTxn.getAmount() != null) {
            amountField.setText(existingTxn.getAmount().toPlainString());
        }
        if (existingTxn.getTransactionDate() != null) {
            dateField.setText(existingTxn.getTransactionDate().toString());
        }
        if (existingTxn.getNote() != null) {
            noteField.setText(existingTxn.getNote());
        }
        updateAmountHint();
    }

    public void triggerSave() {
        WalletResponseDTO wallet = walletCombo.getValue();
        CategoryResponseDTO category = categoryCombo.getValue();

        if (wallet == null) {
            handleError("Vui lòng chọn ví tài chính.");
            return;
        }
        if (category == null) {
            handleError("Vui lòng chọn danh mục thu chi.");
            return;
        }

        String rawAmount = getAmountInputValue();
        String rawDate = getDateInputValue();
        String rawNote = getNoteInputValue();

        String rawFeeAmount = null;
        UUID feeCatId = null;
        if (feeCheckBox.isSelected()) {
            rawFeeAmount = feeAmountField.getText();
            CategoryResponseDTO feeCat = feeCategoryCombo.getValue();
            if (feeCat != null) {
                feeCatId = feeCat.getCategoryId();
            } else {
                handleError("Vui lòng chọn nhóm và danh mục phụ phí.");
                return;
            }
        }

        try {
            this.updatedTransaction = this.transactionController.handleUpdateTransaction(
                    this.existingTxn.getTransactionId(),
                    wallet.getWalletId(),
                    rawAmount,
                    rawDate,
                    category.getCategoryId(),
                    rawNote,
                    rawFeeAmount,
                    feeCatId);
            this.success = true;
            this.errorMessage = null;
            dispose();
        } catch (Exception ex) {
            handleError(ex.getMessage());
        }
    }

    private void handleError(String message) {
        this.errorMessage = message;
        this.success = false;
        if (this.errorConsumer != null) {
            this.errorConsumer.accept(message);
        }
    }

    public void dispose() {
        close();
    }

    public void setVisible(boolean visible) {
        if (visible) {
            showAndWait();
        } else {
            close();
        }
    }

    // region Getters & Setters
    public boolean isSuccess() { return success; }
    public TransactionResponseDTO getUpdatedTransaction() { return updatedTransaction; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorConsumer(Consumer<String> errorConsumer) { this.errorConsumer = errorConsumer; }

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
    public ComboBox<CategoryResponseDTO> getCategoryCombo() { return categoryCombo; }
    public CategoryResponseDTO getSelectedCategory() { return categoryCombo.getValue(); }
    public void setSelectedCategory(CategoryResponseDTO category) { categoryCombo.setValue(category); }

    public String getDateInputValue() { return dateField.getText() != null ? dateField.getText().trim() : ""; }
    public void setDateInputValue(String date) { dateField.setText(date); }

    public String getNoteInputValue() { return noteField.getText() != null ? noteField.getText().trim() : ""; }
    public void setNoteInputValue(String note) { noteField.setText(note); }

    public FxButton getCancelButton() { return cancelButton; }
    public FxButton getSaveButton() { return saveButton; }
    public FxCheckBox getFeeCheckBox() { return feeCheckBox; }
    public TextField getFeeAmountField() { return feeAmountField; }
    public ComboBox<CategoryHierarchyResponseDTO> getFeeGroupCombo() { return feeGroupCombo; }
    public ComboBox<CategoryResponseDTO> getFeeCategoryCombo() { return feeCategoryCombo; }
    public VBox getFeeDetailsPanel() { return feeDetailsPanel; }

    public static class FxButton extends Button {
        public FxButton(String text) { super(text); }
        public void doClick() { fire(); }
    }

    public static class FxCheckBox extends CheckBox {
        public FxCheckBox(String text) { super(text); }
        public void doClick() {
            fire();
        }
    }
}
