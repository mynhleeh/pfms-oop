package com.pfms.presentation.category.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Hộp thoại modal tạo Nhóm Nhu cầu mới (UC-CAT-02) sử dụng JavaFX.
 * Tích hợp Type Switcher phẳng 2 nút đồng bộ phân loại thu/chi và tối giản trường nhập liệu.
 */
public class CreateNeedGroupDialog extends Dialog<ButtonType> {

    private final CategoryGUIController categoryController;

    private final ComboBox<String> typeBox;
    private final Button expenseTypeBtn;
    private final Button incomeTypeBtn;
    private final TextField nameField;
    private final TextField iconField;
    private final TextField colorField;

    private boolean success = false;
    private NeedGroupResponseDTO createdNeedGroup = null;

    private Consumer<String> errorConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi tạo nhóm nhu cầu");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };

    public CreateNeedGroupDialog(Window owner, CategoryGUIController categoryController, String defaultType) {
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");

        setTitle("Tạo Nhóm Nhu Cầu Mới");
        if (owner != null) {
            initOwner(owner);
        }

        String initialType = (defaultType != null && "INCOME".equalsIgnoreCase(defaultType.trim())) ? "INCOME" : "EXPENSE";

        this.typeBox = new ComboBox<>();
        this.typeBox.getItems().addAll("EXPENSE", "INCOME");
        this.typeBox.setValue(initialType);
        this.typeBox.setVisible(false);
        this.typeBox.setManaged(false);

        this.expenseTypeBtn = new Button("Khoản Chi Tiêu");
        this.incomeTypeBtn = new Button("Khoản Thu Nhập");
        this.expenseTypeBtn.setMaxWidth(Double.MAX_VALUE);
        this.incomeTypeBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.expenseTypeBtn, Priority.ALWAYS);
        HBox.setHgrow(this.incomeTypeBtn, Priority.ALWAYS);

        this.expenseTypeBtn.setOnAction(e -> setTypeInputValue("EXPENSE"));
        this.incomeTypeBtn.setOnAction(e -> setTypeInputValue("INCOME"));

        this.nameField = new TextField();
        this.nameField.setPromptText("Nhập tên nhóm nhu cầu...");
        this.nameField.setStyle("-fx-font-size: 13px;");

        this.iconField = new TextField();
        this.colorField = new TextField();

        VBox form = new VBox(12);
        form.setPadding(new Insets(18, 22, 18, 22));
        form.setStyle("-fx-background-color: #141414;");

        // Header Title
        VBox headerBox = new VBox(4);
        headerBox.setPadding(new Insets(0, 0, 6, 0));
        Label headerTitle = new Label("Tạo Nhóm Nhu Cầu Mới");
        headerTitle.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label headerSubtitle = new Label("Phân loại danh mục theo nhu cầu tài chính cá nhân");
        headerSubtitle.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 11px;");
        headerBox.getChildren().addAll(headerTitle, headerSubtitle);

        // 1. Phân loại giao dịch (Type Switcher đồng bộ RecordTransactionDialog, không icon dot)
        VBox typeBoxContainer = new VBox(4);
        Label typeLabel = new Label("Phân loại giao dịch (*):");
        typeLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        HBox typeSwitcher = new HBox(6);
        typeSwitcher.setStyle("-fx-background-color: #0E0E0E; -fx-padding: 4px; -fx-border-color: -color-border; " +
                "-fx-border-radius: 8px; -fx-background-radius: 8px;");
        typeSwitcher.getChildren().addAll(expenseTypeBtn, incomeTypeBtn);
        typeBoxContainer.getChildren().addAll(typeLabel, typeSwitcher);

        // 2. Tên nhóm nhu cầu
        VBox nameBoxContainer = new VBox(4);
        Label nameLabel = new Label("Tên nhóm nhu cầu (*):");
        nameLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        nameBoxContainer.getChildren().addAll(nameLabel, nameField);

        form.getChildren().addAll(headerBox, typeBoxContainer, nameBoxContainer);

        DialogPane pane = getDialogPane();
        pane.setPrefWidth(480);
        pane.setMinWidth(480);
        pane.setContent(form);

        ButtonType saveBtnType = new ButtonType("Lưu Nhóm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Hủy bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(saveBtnType, cancelBtnType);

        var cssResource = getClass().getResource("/css/pfms-theme.css");
        if (cssResource != null) {
            pane.getStylesheets().add(cssResource.toExternalForm());
        }

        final var okButton = pane.lookupButton(saveBtnType);
        if (okButton != null) {
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                triggerSave();
                if (!isSuccess()) {
                    event.consume();
                }
            });
        }

        updateTypeSwitcherUI();
    }

    public CreateNeedGroupDialog(Window owner, CategoryGUIController categoryController) {
        this(owner, categoryController, "EXPENSE");
    }

    public CreateNeedGroupDialog(CategoryGUIController categoryController) {
        this((Window) null, categoryController, "EXPENSE");
    }

    public CreateNeedGroupDialog(Object dummyParent, CategoryGUIController categoryController) {
        this((Window) (dummyParent instanceof Window ? (Window) dummyParent : null), categoryController, "EXPENSE");
    }

    private void updateTypeSwitcherUI() {
        boolean isIncome = "INCOME".equalsIgnoreCase(this.typeBox.getValue());
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

    public void triggerSave() {
        try {
            String type = getTypeInputValue();
            String name = getNameInputValue();
            String icon = getIconInputValue();
            String color = getColorInputValue();

            this.createdNeedGroup = this.categoryController.handleCreateNeedGroup(type, name, icon, color);
            this.success = true;
            dispose();
        } catch (Exception ex) {
            this.success = false;
            this.createdNeedGroup = null;
            if (this.errorConsumer != null) {
                this.errorConsumer.accept(ex.getMessage());
            }
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

    public boolean isSuccess() {
        return this.success;
    }

    public NeedGroupResponseDTO getCreatedNeedGroup() {
        return this.createdNeedGroup;
    }

    public String getTypeInputValue() {
        return this.typeBox.getValue();
    }

    public void setTypeInputValue(String type) {
        if (type != null) {
            String normalized = "INCOME".equalsIgnoreCase(type.trim()) ? "INCOME" : "EXPENSE";
            this.typeBox.setValue(normalized);
            updateTypeSwitcherUI();
        }
    }

    public String getNameInputValue() {
        return this.nameField.getText() != null ? this.nameField.getText().trim() : "";
    }

    public void setNameInputValue(String name) {
        this.nameField.setText(name != null ? name : "");
    }

    public String getIconInputValue() {
        return this.iconField.getText() != null ? this.iconField.getText().trim() : "";
    }

    public void setIconInputValue(String icon) {
        this.iconField.setText(icon != null ? icon : "");
    }

    public String getColorInputValue() {
        return this.colorField.getText() != null ? this.colorField.getText().trim() : "";
    }

    public void setColorInputValue(String color) {
        this.colorField.setText(color != null ? color : "");
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = Objects.requireNonNull(errorConsumer);
    }
}
