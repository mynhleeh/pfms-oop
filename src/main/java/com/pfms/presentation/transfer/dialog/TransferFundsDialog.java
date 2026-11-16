package com.pfms.presentation.transfer.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.transfer.TransferGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Hộp thoại modal điều chuyển tiền giữa hai ví tài chính (UC-TRF-01) sử dụng JavaFX.
 * Hiện thực giao diện Code-First theo nhận diện Luxury Black & Gold.
 */
public class TransferFundsDialog extends Dialog<ButtonType> {

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

    private boolean success = false;
    private TransferResponseDTO createdTransfer = null;
    private String errorMessage = null;

    private Consumer<String> errorConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi chuyển tiền");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };

    public TransferFundsDialog(
            Window owner,
            TransferGUIController transferController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {

        this.transferController = Objects.requireNonNull(transferController, "TransferGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);

        setTitle("Chuyển Tiền Nội Bộ");
        initOwner(owner);

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

        var cssResource = getClass().getResource("/css/pfms-theme.css");
        if (cssResource != null) {
            pane.getStylesheets().add(cssResource.toExternalForm());
        }

        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(16, 20, 16, 20));
        contentBox.setPrefWidth(480);

        // Header Title
        Label headerTitle = new Label("Điều Chuyển Tiền Giữa Các Ví");
        headerTitle.setStyle("-fx-text-fill: -color-primary; -fx-font-size: 16px; -fx-font-weight: bold;");

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

        // 4. Fee Section (Accordion Card)
        VBox feeSectionBox = new VBox(8);
        feeSectionBox.getStyleClass().add("pfms-card");
        feeSectionBox.setStyle("-fx-border-color: -color-border; -fx-border-radius: 6px; -fx-padding: 10px;");

        feeCheckBox.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold;");

        Label lblFeeAmount = new Label("Số tiền phí:");
        lblFeeAmount.getStyleClass().add("form-label");
        feeAmountField.setMaxWidth(Double.MAX_VALUE);

        Label lblFeeGroup = new Label("Nhóm chi phí:");
        lblFeeGroup.getStyleClass().add("form-label");
        feeGroupCombo.setMaxWidth(Double.MAX_VALUE);
        setupFeeGroupComboRenderer();

        Label lblFeeCat = new Label("Danh mục phụ phí:");
        lblFeeCat.getStyleClass().add("form-label");
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

        contentBox.getChildren().addAll(headerTitle, sourceBox, destBox, amountBox, dateBox, noteBox, feeSectionBox);
        pane.setContent(contentBox);

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
        StringConverter<WalletResponseDTO> converter = new StringConverter<>() {
            @Override
            public String toString(WalletResponseDTO w) {
                if (w == null) return "-- Chọn ví tài chính --";
                return w.getName() + " (" + formatCurrency(w.getBalance()) + ")";
            }

            @Override
            public WalletResponseDTO fromString(String string) {
                return null;
            }
        };
        combo.setConverter(converter);
        combo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(WalletResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + formatCurrency(item.getBalance()) + ")");
                }
            }
        });
    }

    private void setupFeeGroupComboRenderer() {
        feeGroupCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategoryHierarchyResponseDTO g) {
                return g == null ? "-- Chọn nhóm chi phí --" : g.getNeedGroupName();
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
                return c == null ? "-- Chọn danh mục chi tiết --" : c.getName();
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
                // Keep not selected until user picks
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

    private void updateAmountHint(String text) {
        if (text == null || text.isBlank()) {
            amountInWordsHint.setText("");
            return;
        }
        try {
            BigDecimal val = new BigDecimal(text.trim());
            amountInWordsHint.setText(currencyFormatter.format(val) + " VNĐ");
        } catch (Exception e) {
            amountInWordsHint.setText("");
        }
    }

    private void updateWalletBalanceHints() {
        WalletResponseDTO src = sourceWalletCombo.getValue();
        if (src != null && src.getBalance() != null) {
            sourceBalanceHintLabel.setText("Số dư khả dụng: " + formatCurrency(src.getBalance()));
        } else {
            sourceBalanceHintLabel.setText("Số dư: 0 VNĐ");
        }

        WalletResponseDTO dst = destWalletCombo.getValue();
        if (dst != null && dst.getBalance() != null) {
            destBalanceHintLabel.setText("Số dư hiện tại: " + formatCurrency(dst.getBalance()));
        } else {
            destBalanceHintLabel.setText("Số dư: 0 VNĐ");
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

    public void triggerTransfer() {
        WalletResponseDTO sourceWallet = sourceWalletCombo.getValue();
        WalletResponseDTO destWallet = destWalletCombo.getValue();

        if (sourceWallet == null || destWallet == null) {
            handleError("Vui lòng chọn ví nguồn và ví đích.");
            return;
        }

        if (sourceWallet.getWalletId().equals(destWallet.getWalletId())) {
            handleError("Ví nguồn và ví đích không được trùng nhau.");
            return;
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
                handleError("Vui lòng chọn nhóm và danh mục phụ phí.");
                return;
            }
        }

        try {
            this.createdTransfer = this.transferController.handleTransferFunds(
                    sourceWallet.getWalletId(),
                    destWallet.getWalletId(),
                    rawAmount,
                    rawDate,
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
    public boolean isSuccess() {
        return this.success;
    }

    public TransferResponseDTO getCreatedTransfer() {
        return this.createdTransfer;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
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
    // endregion
}
