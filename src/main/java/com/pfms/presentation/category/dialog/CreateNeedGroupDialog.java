package com.pfms.presentation.category.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
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

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Hộp thoại modal tạo Nhóm Nhu cầu mới (UC-CAT-02) sử dụng JavaFX.
 */
public class CreateNeedGroupDialog extends Dialog<ButtonType> {

    private final CategoryGUIController categoryController;

    private final ComboBox<String> typeBox;
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

    public CreateNeedGroupDialog(Window owner, CategoryGUIController categoryController) {
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");

        setTitle("Thêm Nhóm Nhu cầu Mới");
        if (owner != null) {
            initOwner(owner);
        }

        this.typeBox = new ComboBox<>();
        this.typeBox.getItems().addAll("EXPENSE", "INCOME");
        this.typeBox.setValue("EXPENSE");
        this.typeBox.setMaxWidth(Double.MAX_VALUE);

        this.nameField = new TextField();
        this.nameField.setPromptText("Nhập tên nhóm nhu cầu...");

        this.iconField = new TextField();
        this.iconField.setPromptText("Mã icon (tùy chọn)...");

        this.colorField = new TextField();
        this.colorField.setPromptText("Mã màu Hex (tùy chọn, ví dụ #D4AF37)...");

        VBox form = new VBox(8);
        form.setPadding(new Insets(16, 20, 16, 20));
        form.setStyle("-fx-background-color: #121212;");

        Label typeLabel = new Label("Loại giao dịch (*):");
        typeLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold;");

        Label nameLabel = new Label("Tên nhóm nhu cầu (*):");
        nameLabel.setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold;");

        Label iconLabel = new Label("Biểu tượng (Icon):");
        iconLabel.setStyle("-fx-text-fill: -color-text-main;");

        Label colorLabel = new Label("Màu sắc (Color):");
        colorLabel.setStyle("-fx-text-fill: -color-text-main;");

        form.getChildren().addAll(
                typeLabel, typeBox,
                nameLabel, nameField,
                iconLabel, iconField,
                colorLabel, colorField
        );

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

    public CreateNeedGroupDialog(CategoryGUIController categoryController) {
        this((Window) null, categoryController);
    }

    public CreateNeedGroupDialog(Object dummyParent, CategoryGUIController categoryController) {
        this((Window) (dummyParent instanceof Window ? (Window) dummyParent : null), categoryController);
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
            this.typeBox.setValue(type);
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
