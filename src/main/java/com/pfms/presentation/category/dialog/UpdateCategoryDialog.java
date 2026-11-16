package com.pfms.presentation.category.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Hộp thoại modal chỉnh sửa thông tin danh mục con: đổi tên và chuyển nhóm nhu cầu cha (UC-CAT-02) sử dụng JavaFX.
 */
public class UpdateCategoryDialog extends Dialog<ButtonType> {

    private final CategoryGUIController categoryController;
    private final UUID categoryId;
    private final UUID initialParentGroupId;
    private final String transactionType;

    private final ComboBox<NeedGroupComboItem> parentGroupBox;
    private final TextField nameField;

    private boolean success = false;
    private CategoryResponseDTO updatedCategory = null;

    private Consumer<String> errorConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi cập nhật danh mục");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };

    public UpdateCategoryDialog(
            Window owner,
            CategoryGUIController categoryController,
            UUID categoryId,
            String currentName,
            UUID currentParentGroupId,
            String transactionType) {

        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");
        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.initialParentGroupId = Objects.requireNonNull(currentParentGroupId, "Mã nhóm cha không được để null");
        this.transactionType = transactionType != null ? transactionType : "EXPENSE";

        setTitle("Chỉnh Sửa Danh Mục Con");
        if (owner != null) {
            initOwner(owner);
        }

        this.parentGroupBox = new ComboBox<>();
        this.parentGroupBox.setMaxWidth(Double.MAX_VALUE);
        this.nameField = new TextField(currentName != null ? currentName : "");
        this.nameField.setStyle("-fx-font-size: 13px;");

        loadCompatibleNeedGroups();

        VBox form = new VBox(12);
        form.setPadding(new Insets(18, 22, 18, 22));
        form.setStyle("-fx-background-color: #141414;");

        // Header Title
        VBox headerBox = new VBox(4);
        headerBox.setPadding(new Insets(0, 0, 6, 0));
        Label headerTitle = new Label("Chỉnh Sửa Danh Mục Con");
        headerTitle.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label headerSubtitle = new Label("Cập nhật tên hoặc chuyển đổi nhóm nhu cầu cha");
        headerSubtitle.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 11px;");
        headerBox.getChildren().addAll(headerTitle, headerSubtitle);

        // 1. Phân loại giao dịch (Badge sạch sẽ)
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

        form.getChildren().addAll(headerBox, typeBoxContainer, groupBoxContainer, nameBoxContainer);

        DialogPane pane = getDialogPane();
        pane.setPrefWidth(500);
        pane.setMinWidth(500);
        pane.setContent(form);

        javafx.scene.control.ButtonBar.ButtonData okData = javafx.scene.control.ButtonBar.ButtonData.OK_DONE;
        javafx.scene.control.ButtonBar.ButtonData cancelData = javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
        ButtonType saveBtnType = new ButtonType("Lưu Danh Mục", okData);
        ButtonType cancelBtnType = new ButtonType("Hủy bỏ", cancelData);
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
        this((Window) (dummyParent instanceof Window ? (Window) dummyParent : null),
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

    public void triggerSave() {
        try {
            NeedGroupComboItem selected = this.parentGroupBox.getValue();
            if (selected == null) {
                throw new IllegalArgumentException("Vui lòng chọn nhóm nhu cầu cha");
            }

            String name = getNameInputValue();
            this.updatedCategory = this.categoryController.handleRenameCategory(this.categoryId, name, selected.id());
            this.success = true;
            dispose();
        } catch (Exception ex) {
            this.success = false;
            this.updatedCategory = null;
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

    public CategoryResponseDTO getUpdatedCategory() {
        return this.updatedCategory;
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

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = Objects.requireNonNull(errorConsumer);
    }

    public record NeedGroupComboItem(UUID id, String name, String type) {
        @Override
        public String toString() {
            return name;
        }
    }
}
