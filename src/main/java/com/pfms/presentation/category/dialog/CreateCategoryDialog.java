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
 * Hộp thoại modal tạo Danh mục phân loại con chi tiết mới (UC-CAT-03) sử dụng JavaFX.
 */
public class CreateCategoryDialog extends Dialog<ButtonType> {

    private final CategoryGUIController categoryController;
    private final ComboBox<NeedGroupComboItem> parentGroupBox;
    private final TextField nameField;

    private boolean success = false;
    private CategoryResponseDTO createdCategory = null;

    private Consumer<String> errorConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi tạo danh mục");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };

    public CreateCategoryDialog(Window owner, CategoryGUIController categoryController) {
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");

        setTitle("Thêm Danh mục Con Mới");
        if (owner != null) {
            initOwner(owner);
        }

        this.parentGroupBox = new ComboBox<>();
        this.parentGroupBox.setMaxWidth(Double.MAX_VALUE);
        this.nameField = new TextField();
        this.nameField.setPromptText("Nhập tên danh mục con...");

        loadNeedGroups();

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

    public CreateCategoryDialog(CategoryGUIController categoryController) {
        this((Window) null, categoryController);
    }

    public CreateCategoryDialog(Object dummyParent, CategoryGUIController categoryController) {
        this((Window) (dummyParent instanceof Window ? (Window) dummyParent : null), categoryController);
    }

    private void loadNeedGroups() {
        try {
            List<CategoryHierarchyResponseDTO> hierarchy = this.categoryController.loadCategoryHierarchy();
            for (CategoryHierarchyResponseDTO group : hierarchy) {
                if ("ACTIVE".equalsIgnoreCase(group.getNeedGroupStatus())) {
                    this.parentGroupBox.getItems().add(new NeedGroupComboItem(
                            group.getNeedGroupId(), group.getNeedGroupName(), group.getTransactionType()));
                }
            }
            if (!this.parentGroupBox.getItems().isEmpty()) {
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
            this.createdCategory = this.categoryController.handleCreateCategory(selected.id(), name);
            this.success = true;
            dispose();
        } catch (Exception ex) {
            this.success = false;
            this.createdCategory = null;
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

    public CategoryResponseDTO getCreatedCategory() {
        return this.createdCategory;
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
