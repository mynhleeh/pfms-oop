package com.pfms.presentation.category.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.dialog.BaseFormDialog;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Hộp thoại modal tạo Danh mục phân loại con chi tiết mới (UC-CAT-03) sử dụng JavaFX.
 * Kế thừa BaseFormDialog theo mô hình Template Method, loại bỏ boilerplate lặp lại.
 */
public class CreateCategoryDialog extends BaseFormDialog<CategoryResponseDTO> {

    private final CategoryGUIController categoryController;
    private final List<CategoryHierarchyResponseDTO> cachedHierarchy = new ArrayList<>();

    private String currentType = "EXPENSE";
    private final Button expenseTypeBtn;
    private final Button incomeTypeBtn;
    private final ComboBox<NeedGroupComboItem> parentGroupBox;
    private final TextField nameField;

    public CreateCategoryDialog(Window owner, CategoryGUIController categoryController, String defaultType) {
        super(owner, "Thêm Danh Mục Con Mới", 500);
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");

        this.currentType = (defaultType != null && "INCOME".equalsIgnoreCase(defaultType.trim())) ? "INCOME" : "EXPENSE";

        this.expenseTypeBtn = new Button("Khoản Chi Tiêu");
        this.incomeTypeBtn = new Button("Khoản Thu Nhập");
        this.expenseTypeBtn.setMaxWidth(Double.MAX_VALUE);
        this.incomeTypeBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.expenseTypeBtn, Priority.ALWAYS);
        HBox.setHgrow(this.incomeTypeBtn, Priority.ALWAYS);

        this.expenseTypeBtn.setOnAction(e -> setTypeInputValue("EXPENSE"));
        this.incomeTypeBtn.setOnAction(e -> setTypeInputValue("INCOME"));

        this.parentGroupBox = new ComboBox<>();
        this.parentGroupBox.setMaxWidth(Double.MAX_VALUE);

        this.nameField = new TextField();
        this.nameField.setPromptText("Nhập tên danh mục con...");
        this.nameField.setStyle("-fx-font-size: 13px;");

        // Nạp dữ liệu cây phân cấp và lọc ban đầu theo type
        loadAllNeedGroups();
        filterNeedGroupsByType(this.currentType);

        var headerBox = createHeaderBox("Thêm Danh Mục Con Mới", "Gán danh mục chi tiết trực thuộc nhóm nhu cầu tài chính");

        // 1. Phân loại giao dịch (Type Switcher đồng bộ phẳng 2 nút)
        VBox typeBoxContainer = new VBox(4);
        Label typeLabel = new Label("Phân loại giao dịch (*):");
        typeLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        HBox typeSwitcher = new HBox(6);
        typeSwitcher.setStyle("-fx-background-color: #0E0E0E; -fx-padding: 4px; -fx-border-color: -color-border; " +
                "-fx-border-radius: 8px; -fx-background-radius: 8px;");
        typeSwitcher.getChildren().addAll(expenseTypeBtn, incomeTypeBtn);
        typeBoxContainer.getChildren().addAll(typeLabel, typeSwitcher);

        // 2. Nhóm nhu cầu cha (Lọc động theo type)
        VBox groupBoxContainer = new VBox(4);
        Label parentLabel = new Label("Nhóm nhu cầu cha (*):");
        parentLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        groupBoxContainer.getChildren().addAll(parentLabel, parentGroupBox);

        // 3. Tên danh mục con
        VBox nameBoxContainer = new VBox(4);
        Label nameLabel = new Label("Tên danh mục con (*):");
        nameLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");
        nameBoxContainer.getChildren().addAll(nameLabel, nameField);

        var form = createFormBox(12, headerBox, typeBoxContainer, groupBoxContainer, nameBoxContainer);
        getDialogPane().setContent(form);

        setupButtons("Lưu Danh Mục", "Hủy bỏ");
        updateTypeSwitcherUI();
    }

    public CreateCategoryDialog(Window owner, CategoryGUIController categoryController) {
        this(owner, categoryController, "EXPENSE");
    }

    public CreateCategoryDialog(CategoryGUIController categoryController) {
        this((Window) null, categoryController, "EXPENSE");
    }

    public CreateCategoryDialog(Object dummyParent, CategoryGUIController categoryController) {
        this(dummyParent instanceof Window w ? w : null, categoryController, "EXPENSE");
    }

    private void loadAllNeedGroups() {
        try {
            this.cachedHierarchy.clear();
            List<CategoryHierarchyResponseDTO> hierarchy = this.categoryController.loadCategoryHierarchy();
            if (hierarchy != null) {
                this.cachedHierarchy.addAll(hierarchy);
            }
        } catch (Exception ignored) {
        }
    }

    private void filterNeedGroupsByType(String type) {
        this.parentGroupBox.getItems().clear();
        for (CategoryHierarchyResponseDTO group : cachedHierarchy) {
            if (type.equalsIgnoreCase(group.getTransactionType())
                    && "ACTIVE".equalsIgnoreCase(group.getNeedGroupStatus())) {
                this.parentGroupBox.getItems().add(new NeedGroupComboItem(
                        group.getNeedGroupId(), group.getNeedGroupName(), group.getTransactionType()));
            }
        }
        if (!this.parentGroupBox.getItems().isEmpty()) {
            this.parentGroupBox.getSelectionModel().selectFirst();
        }
    }

    private void updateTypeSwitcherUI() {
        boolean isIncome = "INCOME".equalsIgnoreCase(this.currentType);
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
    protected CategoryResponseDTO onExecuteSave() throws Exception {
        NeedGroupComboItem selected = this.parentGroupBox.getValue();
        if (selected == null) {
            throw new IllegalArgumentException("Vui lòng chọn nhóm nhu cầu cha");
        }
        String name = getNameInputValue();
        return this.categoryController.handleCreateCategory(selected.id(), name);
    }

    public CategoryResponseDTO getCreatedCategory() {
        return this.resultData;
    }

    public String getTypeInputValue() {
        return this.currentType;
    }

    public void setTypeInputValue(String type) {
        if (type != null) {
            String normalized = "INCOME".equalsIgnoreCase(type.trim()) ? "INCOME" : "EXPENSE";
            if (!this.currentType.equalsIgnoreCase(normalized)) {
                this.currentType = normalized;
                updateTypeSwitcherUI();
                filterNeedGroupsByType(this.currentType);
            }
        }
    }

    public UUID getSelectedParentGroupId() {
        NeedGroupComboItem selected = this.parentGroupBox.getValue();
        return selected != null ? selected.id() : null;
    }

    public void setSelectedParentGroupId(UUID groupId) {
        if (groupId == null) return;
        for (CategoryHierarchyResponseDTO group : cachedHierarchy) {
            if (groupId.equals(group.getNeedGroupId())) {
                String groupType = group.getTransactionType();
                if (!this.currentType.equalsIgnoreCase(groupType)) {
                    this.currentType = groupType;
                    updateTypeSwitcherUI();
                    filterNeedGroupsByType(this.currentType);
                }
                break;
            }
        }
        for (NeedGroupComboItem item : this.parentGroupBox.getItems()) {
            if (item != null && groupId.equals(item.id())) {
                this.parentGroupBox.setValue(item);
                return;
            }
        }
    }

    public String getNameInputValue() {
        return this.nameField.getText() != null ? this.nameField.getText().trim() : "";
    }

    public void setNameInputValue(String name) {
        this.nameField.setText(name != null ? name : "");
    }

    public record NeedGroupComboItem(UUID id, String name, String type) {
        @Override
        public String toString() {
            return name;
        }
    }
}
