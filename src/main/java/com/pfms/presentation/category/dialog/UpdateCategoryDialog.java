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

        loadCompatibleNeedGroups();

        VBox form = new VBox(8);
        form.setPadding(new Insets(16, 20, 16, 20));
        form.setStyle("-fx-background-color: #121212;");

        Label parentLabel = new Label("Nhóm nhu cầu cha (*):");
        parentLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold;");

        Label nameLabel = new Label("Tên danh mục con (*):");
        nameLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold;");

        form.getChildren().addAll(parentLabel, parentGroupBox, nameLabel, nameField);

        DialogPane pane = getDialogPane();
        pane.setContent(form);
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        var cssResource = getClass().getResource("/css/pfms-theme.css");
        if (cssResource != null) {
            pane.getStylesheets().add(cssResource.toExternalForm());
        }

        final var okButton = pane.lookupButton(ButtonType.OK);
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
            return name + " [" + type + "]";
        }
    }
}
