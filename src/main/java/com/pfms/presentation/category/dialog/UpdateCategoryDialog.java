package com.pfms.presentation.category.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.dialog.BaseFormDialog;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Hộp thoại modal chỉnh sửa thông tin danh mục con: đổi tên và chuyển nhóm nhu cầu cha (UC-CAT-02) sử dụng JavaFX.
 * Kế thừa BaseFormDialog theo mô hình Template Method, loại bỏ boilerplate lặp lại.
 */
public class UpdateCategoryDialog extends BaseFormDialog<CategoryResponseDTO> {

    private final CategoryGUIController categoryController;
    private final UUID categoryId;
    private final UUID initialParentGroupId;
    private final String transactionType;

    private final ComboBox<NeedGroupComboItem> parentGroupBox;
    private final TextField nameField;

    public UpdateCategoryDialog(
            Window owner,
            CategoryGUIController categoryController,
            UUID categoryId,
            String currentName,
            UUID currentParentGroupId,
            String transactionType) {

        super(owner, "Chỉnh Sửa Danh Mục Con", 500);
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");
        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.initialParentGroupId = Objects.requireNonNull(currentParentGroupId, "Mã nhóm cha không được để null");
        this.transactionType = transactionType != null ? transactionType : "EXPENSE";

        this.parentGroupBox = new ComboBox<>();
        this.parentGroupBox.setMaxWidth(Double.MAX_VALUE);
        this.nameField = new TextField(currentName != null ? currentName : "");
        this.nameField.setStyle("-fx-font-size: 13px;");

        loadCompatibleNeedGroups();

        var headerBox = createHeaderBox("Chỉnh Sửa Danh Mục Con", "Cập nhật tên hoặc chuyển đổi nhóm nhu cầu cha");

        // 1. Phân loại giao dịch (Badge hiển thị)
        VBox typeBoxContainer = new VBox(4);
        Label typeHeader = new Label("Phân loại giao dịch:");
        typeHeader.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        boolean isIncome = "INCOME".equalsIgnoreCase(this.transactionType);
        Label typeBadge = new Label(isIncome ? "INCOME" : "EXPENSE");
        typeBadge.setStyle("-fx-background-color: " + (isIncome ? "rgba(74, 222, 128, 0.15)" : "rgba(251, 113, 133, 0.15)") + "; " +
                "-fx-text-fill: " + (isIncome ? "#4ADE80" : "#FB7185") + "; -fx-font-weight: 800; -fx-padding: 4px 12px; -fx-background-radius: 4px; -fx-font-size: 11px;");
        typeBoxContainer.getChildren().addAll(typeHeader, typeBadge);

        // 2. Nhóm nhu cầu cha
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
    }

    public UpdateCategoryDialog(
            CategoryGUIController categoryController,
            UUID categoryId,
            String currentName,
            UUID currentParentGroupId,
            String transactionType) {
        this((Window) null, categoryController, categoryId, currentName, currentParentGroupId, transactionType);
    }

    public UpdateCategoryDialog(
            Object dummyParent,
            CategoryGUIController categoryController,
            UUID categoryId,
            String currentName,
            UUID currentParentGroupId,
            String transactionType) {
        this(dummyParent instanceof Window w ? w : null,
                categoryController, categoryId, currentName, currentParentGroupId, transactionType);
    }

    private void loadCompatibleNeedGroups() {
        try {
            List<CategoryHierarchyResponseDTO> hierarchy = this.categoryController.loadCategoryHierarchy();
            NeedGroupComboItem matchItem = null;

            for (CategoryHierarchyResponseDTO group : hierarchy) {
                if (this.transactionType.equalsIgnoreCase(group.getTransactionType())
                        && "ACTIVE".equalsIgnoreCase(group.getNeedGroupStatus())) {

                    NeedGroupComboItem item = new NeedGroupComboItem(
                            group.getNeedGroupId(), group.getNeedGroupName(), group.getTransactionType());
                    this.parentGroupBox.getItems().add(item);

                    if (this.initialParentGroupId.equals(group.getNeedGroupId())) {
                        matchItem = item;
                    }
                }
            }

            if (matchItem != null) {
                this.parentGroupBox.setValue(matchItem);
            } else if (!this.parentGroupBox.getItems().isEmpty()) {
                this.parentGroupBox.getSelectionModel().selectFirst();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    protected CategoryResponseDTO onExecuteSave() throws Exception {
        NeedGroupComboItem selected = this.parentGroupBox.getValue();
        if (selected == null) {
            throw new IllegalArgumentException("Vui lòng chọn nhóm nhu cầu cha");
        }
        String name = getNameInputValue();
        return this.categoryController.handleRenameCategory(this.categoryId, name, selected.id());
    }

    public CategoryResponseDTO getUpdatedCategory() {
        return this.resultData;
    }

    public UUID getSelectedParentGroupId() {
        NeedGroupComboItem selected = this.parentGroupBox.getValue();
        return selected != null ? selected.id() : null;
    }

    public void setSelectedParentGroupId(UUID groupId) {
        if (groupId == null) return;
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
