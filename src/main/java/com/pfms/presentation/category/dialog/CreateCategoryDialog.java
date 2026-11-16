package com.pfms.presentation.category.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Hộp thoại modal tạo Danh mục phân loại con chi tiết mới (UC-CAT-03) sử dụng JavaFX.
 * Tích hợp Type Switcher phẳng 2 nút và dropdown nhóm cha lọc động theo phân loại (Cascading).
 */
public class CreateCategoryDialog extends Dialog<ButtonType> {

    private final CategoryGUIController categoryController;
    private final List<CategoryHierarchyResponseDTO> cachedHierarchy = new ArrayList<>();

    private String currentType = "EXPENSE";
    private final Button expenseTypeBtn;
    private final Button incomeTypeBtn;
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

    public CreateCategoryDialog(Window owner, CategoryGUIController categoryController, String defaultType) {
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");

        setTitle("Thêm Danh Mục Con Mới");
        if (owner != null) {
            initOwner(owner);
        }

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

        VBox form = new VBox(12);
        form.setPadding(new Insets(18, 22, 18, 22));
        form.setStyle("-fx-background-color: #141414;");

        // Header Title
        VBox headerBox = new VBox(4);
        headerBox.setPadding(new Insets(0, 0, 6, 0));
        Label headerTitle = new Label("Thêm Danh Mục Con Mới");
        headerTitle.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label headerSubtitle = new Label("Gán danh mục chi tiết trực thuộc nhóm nhu cầu tài chính");
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

        form.getChildren().addAll(headerBox, typeBoxContainer, groupBoxContainer, nameBoxContainer);

        DialogPane pane = getDialogPane();
        pane.setPrefWidth(500);
        pane.setMinWidth(500);
        pane.setContent(form);

        ButtonType saveBtnType = new ButtonType("Lưu Danh Mục", ButtonBar.ButtonData.OK_DONE);
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

    public CreateCategoryDialog(Window owner, CategoryGUIController categoryController) {
        this(owner, categoryController, "EXPENSE");
    }

    public CreateCategoryDialog(CategoryGUIController categoryController) {
        this((Window) null, categoryController, "EXPENSE");
    }

    public CreateCategoryDialog(Object dummyParent, CategoryGUIController categoryController) {
        this((Window) (dummyParent instanceof Window ? (Window) dummyParent : null), categoryController, "EXPENSE");
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
