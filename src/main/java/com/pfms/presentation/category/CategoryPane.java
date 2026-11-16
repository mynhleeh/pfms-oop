package com.pfms.presentation.category;

import com.pfms.presentation.category.dialog.CreateCategoryDialog;
import com.pfms.presentation.category.dialog.CreateNeedGroupDialog;
import com.pfms.presentation.category.dialog.UpdateCategoryDialog;
import com.pfms.presentation.common.util.FxAlertUtils;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Bảng điều khiển phân hệ Quản lý Danh mục (Category Management Pane) sử dụng JavaFX.
 * Hiện thực hóa giao diện CategoryView theo mô hình MVP (Passive View).
 */
public class CategoryPane extends BorderPane implements CategoryView {

    private final CategoryGUIController controller;
    private final CategoryPresenter presenter;

    private final TreeItem<CategoryHierarchyItem> treeRoot = new TreeItem<>(
            new CategoryHierarchyItem(null, "Gốc", "", "", true, null));
    private final TreeTableView<CategoryHierarchyItem> treeTableView = new TreeTableView<>(treeRoot);

    private final Label kpiTotalGroupsLabel = new Label("0 nhóm");
    private final Label kpiTotalCategoriesLabel = new Label("0 danh mục");
    private final Label summaryFooterLabel = new Label("Sẵn sàng");

    private Consumer<String> messageConsumer;
    private Consumer<String> warningConsumer;
    private Consumer<String> errorConsumer;

    public CategoryPane(CategoryGUIController controller) {
        this.controller = Objects.requireNonNull(controller, "CategoryGUIController không được để null");
        this.presenter = new CategoryPresenter(this, controller);

        setStyle("-fx-background-color: -color-bg-base;");
        setPadding(new Insets(20));

        // Top Section: Header + KPI Cards
        VBox topBox = new VBox(16);
        topBox.getChildren().addAll(createHeaderSection(), createKpiCardsSection());
        setTop(topBox);

        // Center Section: TreeTableView
        setCenter(createTreeTableSection());

        // Bottom Section: Summary Footer
        HBox bottomBox = new HBox(summaryFooterLabel);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        summaryFooterLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");
        setBottom(bottomBox);

        // Nạp dữ liệu ban đầu
        this.presenter.loadData();
    }

    private Node createHeaderSection() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Cấu Trúc Danh Mục & Nhóm Nhu Cầu");
        title.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAddGroup = new Button("+ Thêm nhóm nhu cầu");
        btnAddGroup.getStyleClass().add("btn-secondary");
        btnAddGroup.setOnAction(e -> this.presenter.onAddNeedGroupClicked());

        Button btnAddCategory = new Button("+ Thêm danh mục con");
        btnAddCategory.getStyleClass().add("btn-primary");
        btnAddCategory.setOnAction(e -> this.presenter.onAddCategoryClicked());

        header.getChildren().addAll(title, spacer, btnAddGroup, btnAddCategory);
        return header;
    }

    private Node createKpiCardsSection() {
        HBox kpiBox = new HBox(16);

        VBox cardGroups = createKpiCard("NHÓM NHU CẦU ĐANG HOẠT ĐỘNG", kpiTotalGroupsLabel, "-color-primary");
        VBox cardCategories = createKpiCard("DANH MỤC CON CHI TIẾT", kpiTotalCategoriesLabel, "-color-income");

        HBox.setHgrow(cardGroups, Priority.ALWAYS);
        HBox.setHgrow(cardCategories, Priority.ALWAYS);

        kpiBox.getChildren().addAll(cardGroups, cardCategories);
        return kpiBox;
    }

    private VBox createKpiCard(String subtitle, Label valueLabel, String colorVar) {
        VBox card = new VBox(6);
        card.getStyleClass().add("pfms-card");

        Label sub = new Label(subtitle);
        sub.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px; -fx-font-weight: bold;");

        valueLabel.setStyle("-fx-text-fill: " + colorVar + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        card.getChildren().addAll(sub, valueLabel);
        return card;
    }

    @SuppressWarnings("unchecked")
    private Node createTreeTableSection() {
        treeTableView.setShowRoot(false);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTableView.getStyleClass().add("table-view");

        TreeTableColumn<CategoryHierarchyItem, String> colName = new TreeTableColumn<>("Tên Phân Cấp / Danh Mục");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().name()));
        colName.setCellFactory(col -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    TreeTableRow<CategoryHierarchyItem> row = getTableRow();
                    boolean isGroup = row != null && row.getTreeItem() != null 
                            && row.getTreeItem().getValue() != null 
                            && row.getTreeItem().getValue().isGroup();
                    setText(item);
                    if (isGroup) {
                        setStyle("-fx-text-fill: -color-primary-light; -fx-font-weight: bold; -fx-font-size: 13px;");
                    } else {
                        setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: normal; -fx-padding: 0 0 0 10;");
                    }
                }
            }
        });
        colName.setPrefWidth(300);

        TreeTableColumn<CategoryHierarchyItem, String> colType = new TreeTableColumn<>("Loại Giao Dịch");
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().type()));
        colType.setCellFactory(col -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.setPadding(new Insets(2, 8, 2, 8));
                    if ("INCOME".equalsIgnoreCase(item)) {
                        badge.setStyle("-fx-background-color: #143520; -fx-text-fill: #4ADE80; -fx-background-radius: 4px;");
                    } else {
                        badge.setStyle("-fx-background-color: #381A20; -fx-text-fill: #FB7185; -fx-background-radius: 4px;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        colType.setPrefWidth(140);

        TreeTableColumn<CategoryHierarchyItem, String> colStatus = new TreeTableColumn<>("Trạng Thái");
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().status()));
        colStatus.setStyle("-fx-alignment: CENTER;");
        colStatus.setPrefWidth(120);

        TreeTableColumn<CategoryHierarchyItem, Void> colActions = new TreeTableColumn<>("Thao Tác");
        colActions.setCellFactory(col -> new TreeTableCell<>() {
            private final Button btnAction1 = new Button();
            private final Button btnAction2 = new Button("Xóa");
            private final HBox box = new HBox(6, btnAction1, btnAction2);

            {
                btnAction1.getStyleClass().add("btn-secondary");
                btnAction2.getStyleClass().add("btn-danger");
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                TreeTableRow<CategoryHierarchyItem> treeRow = getTableRow();
                TreeItem<CategoryHierarchyItem> treeItem = treeRow != null ? treeRow.getTreeItem() : null;
                if (empty || treeItem == null || treeItem.getValue() == null) {
                    setGraphic(null);
                } else {
                    CategoryHierarchyItem nodeData = treeItem.getValue();
                    if (nodeData.isGroup()) {
                        btnAction1.setText("Đổi tên");
                        btnAction1.setOnAction(e -> presenter.renameNeedGroup(nodeData.id(), nodeData.name()));
                        btnAction2.setOnAction(e -> presenter.deleteNeedGroup(nodeData.id(), nodeData.name()));
                    } else {
                        btnAction1.setText("Sửa");
                        btnAction1.setOnAction(e -> presenter.onEditCategoryClicked(
                                nodeData.id(), nodeData.name(), nodeData.parentId(), nodeData.type()));
                        btnAction2.setOnAction(e -> presenter.deleteCategory(nodeData.id(), nodeData.name()));
                    }
                    setGraphic(box);
                }
            }
        });
        colActions.setPrefWidth(220);

        treeTableView.getColumns().addAll(colName, colType, colStatus, colActions);
        return treeTableView;
    }

    // region CategoryView Implementation
    @Override
    public void renderCategoryHierarchy(List<CategoryHierarchyResponseDTO> hierarchy, int totalGroups, int totalCategories) {
        runOnFx(() -> {
            treeRoot.getChildren().clear();
            if (hierarchy != null) {
                for (CategoryHierarchyResponseDTO group : hierarchy) {
                    CategoryHierarchyItem groupItem = new CategoryHierarchyItem(
                            group.getNeedGroupId(), group.getNeedGroupName(), group.getTransactionType(),
                            group.getNeedGroupStatus(), true, null);

                    TreeItem<CategoryHierarchyItem> groupNode = new TreeItem<>(groupItem);
                    groupNode.setExpanded(true);

                    if (group.getCategories() != null) {
                        for (CategoryResponseDTO cat : group.getCategories()) {
                            CategoryHierarchyItem catItem = new CategoryHierarchyItem(
                                    cat.getCategoryId(), cat.getName(), cat.getTransactionType(),
                                    cat.getStatus(), false, group.getNeedGroupId());

                            groupNode.getChildren().add(new TreeItem<>(catItem));
                        }
                    }
                    treeRoot.getChildren().add(groupNode);
                }
            }
            kpiTotalGroupsLabel.setText(totalGroups + " nhóm nhu cầu");
            kpiTotalCategoriesLabel.setText(totalCategories + " danh mục con");
            summaryFooterLabel.setText("Tổng số: " + totalGroups + " nhóm nhu cầu | " + totalCategories + " danh mục chi tiết");
        });
    }

    @Override
    public void openCreateNeedGroupDialog() {
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            CreateNeedGroupDialog dialog = new CreateNeedGroupDialog(owner, this.controller);
            dialog.showAndWait();
            if (dialog.isSuccess()) {
                this.presenter.loadData();
            }
        });
    }

    @Override
    public void openCreateCategoryDialog() {
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            CreateCategoryDialog dialog = new CreateCategoryDialog(owner, this.controller);
            dialog.showAndWait();
            if (dialog.isSuccess()) {
                this.presenter.loadData();
            }
        });
    }

    @Override
    public void openUpdateCategoryDialog(UUID categoryId, String currentName, UUID currentParentGroupId, String transactionType) {
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            UpdateCategoryDialog dialog = new UpdateCategoryDialog(
                    owner, this.controller, categoryId, currentName, currentParentGroupId, transactionType);
            dialog.showAndWait();
            if (dialog.isSuccess()) {
                this.presenter.loadData();
            }
        });
    }

    @Override
    public String openRenameDialog(String currentName, boolean isGroup) {
        TextInputDialog dialog = new TextInputDialog(currentName != null ? currentName : "");
        dialog.setTitle(isGroup ? "Đổi tên nhóm nhu cầu" : "Đổi tên danh mục");
        dialog.setHeaderText(null);
        dialog.setContentText("Tên mới:");
        var cssResource = getClass().getResource("/css/pfms-theme.css");
        if (cssResource != null) {
            dialog.getDialogPane().getStylesheets().add(cssResource.toExternalForm());
        }
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    @Override
    public void showMessage(String message) {
        if (messageConsumer != null) {
            messageConsumer.accept(message);
        } else {
            FxAlertUtils.showInfo("PFMS Thông báo", message);
        }
    }

    @Override
    public void showWarning(String message) {
        if (warningConsumer != null) {
            warningConsumer.accept(message);
        } else {
            FxAlertUtils.showWarning("PFMS Cảnh báo", message);
        }
    }

    @Override
    public void showError(String message) {
        if (errorConsumer != null) {
            errorConsumer.accept(message);
        } else {
            FxAlertUtils.showError("PFMS Lỗi", message);
        }
    }

    @Override
    public boolean confirmAction(String message) {
        return FxAlertUtils.showConfirm("Xác nhận thao tác", message);
    }
    // endregion

    private void runOnFx(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    public CategoryPresenter getPresenter() { return presenter; }
    public TreeTableView<CategoryHierarchyItem> getTreeTableView() { return treeTableView; }
    public String getSummaryText() { return summaryFooterLabel.getText(); }

    public void setMessageConsumer(Consumer<String> consumer) { this.messageConsumer = consumer; }
    public void setWarningConsumer(Consumer<String> consumer) { this.warningConsumer = consumer; }
    public void setErrorConsumer(Consumer<String> consumer) { this.errorConsumer = consumer; }

    public record CategoryHierarchyItem(
            UUID id,
            String name,
            String type,
            String status,
            boolean isGroup,
            UUID parentId
    ) {}
}
