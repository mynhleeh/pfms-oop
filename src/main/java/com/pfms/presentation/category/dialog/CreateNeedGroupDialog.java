package com.pfms.presentation.category.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.dialog.BaseFormDialog;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Objects;

/**
 * Hộp thoại modal tạo Nhóm Nhu cầu mới (UC-CAT-02) sử dụng JavaFX.
 * Kế thừa BaseFormDialog theo mô hình Template Method, loại bỏ boilerplate lặp lại.
 */
public class CreateNeedGroupDialog extends BaseFormDialog<NeedGroupResponseDTO> {

    private final CategoryGUIController categoryController;

    private final ComboBox<String> typeBox;
    private final Button expenseTypeBtn;
    private final Button incomeTypeBtn;
    private final TextField nameField;
    private final TextField iconField;
    private final TextField colorField;

    public CreateNeedGroupDialog(Window owner, CategoryGUIController categoryController, String defaultType) {
        super(owner, "Tạo Nhóm Nhu Cầu Mới", 480);
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");

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

        var headerBox = createHeaderBox("Tạo Nhóm Nhu Cầu Mới", "Phân loại danh mục theo nhu cầu tài chính cá nhân");

        // 1. Phân loại giao dịch (Type Switcher đồng bộ phẳng 2 nút)
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

        var form = createFormBox(12, headerBox, typeBoxContainer, nameBoxContainer);
        getDialogPane().setContent(form);

        setupButtons("Lưu Nhóm", "Hủy bỏ");
        updateTypeSwitcherUI();
    }

    public CreateNeedGroupDialog(Window owner, CategoryGUIController categoryController) {
        this(owner, categoryController, "EXPENSE");
    }

    public CreateNeedGroupDialog(CategoryGUIController categoryController) {
        this((Window) null, categoryController, "EXPENSE");
    }

    public CreateNeedGroupDialog(Object dummyParent, CategoryGUIController categoryController) {
        this(dummyParent instanceof Window w ? w : null, categoryController, "EXPENSE");
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

    @Override
    protected NeedGroupResponseDTO onExecuteSave() throws Exception {
        String type = getTypeInputValue();
        String name = getNameInputValue();
        String icon = getIconInputValue();
        String color = getColorInputValue();
        return this.categoryController.handleCreateNeedGroup(type, name, icon, color);
    }

    public NeedGroupResponseDTO getCreatedNeedGroup() {
        return this.resultData;
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
}
