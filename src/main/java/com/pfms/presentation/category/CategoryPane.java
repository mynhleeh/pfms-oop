package com.pfms.presentation.category;

import com.pfms.presentation.category.component.CategoryGroupCardNode;
import com.pfms.presentation.category.component.CategorySubRowNode;
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
import javafx.scene.control.ScrollPane;
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
    private final com.pfms.presentation.common.component.KpiPill kpiTotalGroups =
            new com.pfms.presentation.common.component.KpiPill("Nhóm nhu cầu", "0 nhóm", com.pfms.presentation.common.component.KpiPill.Variant.GOLD);
    private final com.pfms.presentation.common.component.KpiPill kpiTotalCategories =
            new com.pfms.presentation.common.component.KpiPill("Danh mục con", "0 danh mục", com.pfms.presentation.common.component.KpiPill.Variant.INCOME);
    private final Label summaryFooterLabel = new Label("Sẵn sàng");

    // Two-Zone Stream Cards Layout (Horizontal Split: Income Left, Expense Right)
    private final VBox incomeColumn = new VBox(10);
    private final VBox expenseColumn = new VBox(10);
    private final VBox incomeCardsBox = new VBox(12);
    private final VBox expenseCardsBox = new VBox(12);
    private final Label incomeCountLabel = new Label("0 Nhóm • 0 Danh mục");
    private final Label expenseCountLabel = new Label("0 Nhóm • 0 Danh mục");
    private final HBox twoZonesContainer = new HBox(20);
    private final ScrollPane streamScrollPane = new ScrollPane(twoZonesContainer);

    private final Button btnFilterAll = new Button("Song Song 2 Cột (Mặc định)");
    private final Button btnFilterIncome = new Button("Chỉ Xem Thu Nhập (Income Trái)");
    private final Button btnFilterExpense = new Button("Chỉ Xem Chi Tiêu (Expense Phải)");

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

        // Center Section: Two-Zone Stream Cards (Income Left, Expense Right)
        setCenter(createCenterSection());

        // Bottom Section: Summary Footer
        HBox bottomBox = new HBox(summaryFooterLabel);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        summaryFooterLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");
        setBottom(bottomBox);

        // Nạp dữ liệu ban đầu
        this.presenter.loadData();
    }

    private Node createHeaderSection() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Cấu Trúc Danh Mục & Nhóm Nhu Cầu");
        title.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitle = new Label("Hệ thống phân cấp chuẩn tài chính: Phân loại ➔ Nhóm nhu cầu ➔ Danh mục con");
        subtitle.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox kpiBox = new HBox(8);
        kpiBox.setAlignment(Pos.CENTER_RIGHT);
        kpiBox.getChildren().addAll(kpiTotalGroups, kpiTotalCategories);

        header.getChildren().addAll(titleBox, spacer, kpiBox);
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
    private void setupLegacyTreeTable() {
        treeTableView.setShowRoot(false);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTableView.getStyleClass().add("table-view");

        TreeTableColumn<CategoryHierarchyItem, String> colName = new TreeTableColumn<>("Tên Phân Cấp / Danh Mục");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().name()));
        colName.setPrefWidth(300);

        TreeTableColumn<CategoryHierarchyItem, String> colType = new TreeTableColumn<>("Loại Giao Dịch");
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().type()));
        colType.setPrefWidth(140);

        TreeTableColumn<CategoryHierarchyItem, String> colStatus = new TreeTableColumn<>("Trạng Thái");
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().status()));
        colStatus.setStyle("-fx-alignment: CENTER;");
        colStatus.setPrefWidth(120);

        TreeTableColumn<CategoryHierarchyItem, Void> colActions = new TreeTableColumn<>("Thao Tác");
        colActions.setPrefWidth(220);

        treeTableView.getColumns().setAll(colName, colType, colStatus, colActions);
    }

    private Node createCenterSection() {
        VBox centerBox = new VBox(12);
        centerBox.setPadding(new Insets(12, 0, 0, 0));
        VBox.setVgrow(centerBox, Priority.ALWAYS);

        // Income Column (Left)
        VBox incomeHeader = new VBox(4);
        HBox incomeTitleRow = new HBox(10);
        incomeTitleRow.setAlignment(Pos.CENTER_LEFT);
        Label incomeZoneTag = new Label("INCOME");
        incomeZoneTag.setStyle("-fx-background-color: rgba(74, 222, 128, 0.15); -fx-text-fill: #4ADE80; " +
                "-fx-background-radius: 4px; -fx-padding: 3px 8px; -fx-font-size: 11px; -fx-font-weight: 800;");
        incomeCountLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");

        Region incomeSpacer = new Region();
        HBox.setHgrow(incomeSpacer, Priority.ALWAYS);

        Button btnAddIncomeGroup = new Button("+ Thêm nhóm");
        btnAddIncomeGroup.getStyleClass().addAll("btn-action-sm");
        btnAddIncomeGroup.setStyle("-fx-padding: 4px 10px; -fx-font-size: 11.5px; -fx-border-color: rgba(74, 222, 128, 0.4); " +
                "-fx-text-fill: #4ADE80; -fx-font-weight: 700; -fx-background-color: rgba(74, 222, 128, 0.08);");
        btnAddIncomeGroup.setOnAction(e -> this.presenter.onAddNeedGroupClicked("INCOME"));

        Button btnAddIncomeCategory = new Button("+ Thêm danh mục");
        btnAddIncomeCategory.getStyleClass().addAll("btn-primary");
        btnAddIncomeCategory.setStyle("-fx-padding: 4px 12px; -fx-font-size: 11.5px; -fx-font-weight: 700;");
        btnAddIncomeCategory.setOnAction(e -> this.presenter.onAddCategoryClicked("INCOME"));

        HBox incomeActions = new HBox(8, btnAddIncomeGroup, btnAddIncomeCategory);
        incomeActions.setAlignment(Pos.CENTER_RIGHT);

        incomeTitleRow.getChildren().addAll(incomeZoneTag, incomeCountLabel, incomeSpacer, incomeActions);
        incomeHeader.getChildren().addAll(incomeTitleRow);
        incomeHeader.setStyle("-fx-border-color: transparent transparent #222222 transparent; -fx-border-width: 0 0 1px 0; -fx-padding: 0 0 8px 0;");

        incomeCardsBox.setFillWidth(true);
        incomeColumn.getChildren().addAll(incomeHeader, incomeCardsBox);
        HBox.setHgrow(incomeColumn, Priority.ALWAYS);

        // Expense Column (Right)
        VBox expenseHeader = new VBox(4);
        HBox expenseTitleRow = new HBox(10);
        expenseTitleRow.setAlignment(Pos.CENTER_LEFT);
        Label expenseZoneTag = new Label("EXPENSE");
        expenseZoneTag.setStyle("-fx-background-color: rgba(251, 113, 133, 0.15); -fx-text-fill: #FB7185; " +
                "-fx-background-radius: 4px; -fx-padding: 3px 8px; -fx-font-size: 11px; -fx-font-weight: 800;");
        expenseCountLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");

        Region expenseSpacer = new Region();
        HBox.setHgrow(expenseSpacer, Priority.ALWAYS);

        Button btnAddExpenseGroup = new Button("+ Thêm nhóm");
        btnAddExpenseGroup.getStyleClass().addAll("btn-action-sm");
        btnAddExpenseGroup.setStyle("-fx-padding: 4px 10px; -fx-font-size: 11.5px; -fx-border-color: rgba(251, 113, 133, 0.4); " +
                "-fx-text-fill: #FB7185; -fx-font-weight: 700; -fx-background-color: rgba(251, 113, 133, 0.08);");
        btnAddExpenseGroup.setOnAction(e -> this.presenter.onAddNeedGroupClicked("EXPENSE"));

        Button btnAddExpenseCategory = new Button("+ Thêm danh mục");
        btnAddExpenseCategory.getStyleClass().addAll("btn-primary");
        btnAddExpenseCategory.setStyle("-fx-padding: 4px 12px; -fx-font-size: 11.5px; -fx-font-weight: 700;");
        btnAddExpenseCategory.setOnAction(e -> this.presenter.onAddCategoryClicked("EXPENSE"));

        HBox expenseActions = new HBox(8, btnAddExpenseGroup, btnAddExpenseCategory);
        expenseActions.setAlignment(Pos.CENTER_RIGHT);

        expenseTitleRow.getChildren().addAll(expenseZoneTag, expenseCountLabel, expenseSpacer, expenseActions);
        expenseHeader.getChildren().addAll(expenseTitleRow);
        expenseHeader.setStyle("-fx-border-color: transparent transparent #222222 transparent; -fx-border-width: 0 0 1px 0; -fx-padding: 0 0 8px 0;");

        expenseCardsBox.setFillWidth(true);
        expenseColumn.getChildren().addAll(expenseHeader, expenseCardsBox);
        HBox.setHgrow(expenseColumn, Priority.ALWAYS);

        // Two-Zone Container (Horizontal Split - Always 2 columns)
        twoZonesContainer.setAlignment(Pos.TOP_LEFT);
        twoZonesContainer.setFillHeight(true);
        twoZonesContainer.getChildren().addAll(incomeColumn, expenseColumn);

        streamScrollPane.setFitToWidth(true);
        streamScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(streamScrollPane, Priority.ALWAYS);

        // Keep legacy treeTableView setup for tests
        setupLegacyTreeTable();

        centerBox.getChildren().addAll(streamScrollPane);
        return centerBox;
    }

    public void setFilterMode(String mode) {
        String activeStyle = "-fx-background-color: rgba(229, 192, 123, 0.2); -fx-text-fill: #FAFAFA; -fx-font-weight: bold; " +
                "-fx-border-color: #E5C07B; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 4px 12px;";
        String normalStyle = "-fx-background-color: #161616; -fx-text-fill: -color-text-muted; " +
                "-fx-border-color: #242424; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 4px 12px;";

        btnFilterAll.setStyle("ALL".equalsIgnoreCase(mode) ? activeStyle : normalStyle);
        btnFilterIncome.setStyle("INCOME".equalsIgnoreCase(mode) ? activeStyle : normalStyle);
        btnFilterExpense.setStyle("EXPENSE".equalsIgnoreCase(mode) ? activeStyle : normalStyle);

        if ("ALL".equalsIgnoreCase(mode)) {
            incomeColumn.setVisible(true);
            incomeColumn.setManaged(true);
            expenseColumn.setVisible(true);
            expenseColumn.setManaged(true);
        } else if ("INCOME".equalsIgnoreCase(mode)) {
            incomeColumn.setVisible(true);
            incomeColumn.setManaged(true);
            expenseColumn.setVisible(false);
            expenseColumn.setManaged(false);
        } else if ("EXPENSE".equalsIgnoreCase(mode)) {
            incomeColumn.setVisible(false);
            incomeColumn.setManaged(false);
            expenseColumn.setVisible(true);
            expenseColumn.setManaged(true);
        }
    }

    // region CategoryView Implementation
    @Override
    public void renderCategoryHierarchy(List<CategoryHierarchyResponseDTO> hierarchy, int totalGroups, int totalCategories) {
        runOnFx(() -> {
            // 1. Đồng bộ treeRoot cho test hiện hữu
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
            kpiTotalGroups.setValue(totalGroups + " nhóm");
            kpiTotalCategories.setValue(totalCategories + " danh mục");
            summaryFooterLabel.setText("Tổng số: " + totalGroups + " nhóm nhu cầu | " + totalCategories + " danh mục chi tiết");

            // 2. Render thẻ khối vào 2 cột Income (Trái) & Expense (Phải)
            incomeCardsBox.getChildren().clear();
            expenseCardsBox.getChildren().clear();

            int incomeGroupCount = 0;
            int incomeCatCount = 0;
            int expenseGroupCount = 0;
            int expenseCatCount = 0;

            if (hierarchy != null) {
                for (CategoryHierarchyResponseDTO group : hierarchy) {
                    boolean isIncome = "INCOME".equalsIgnoreCase(group.getTransactionType());
                    int catCount = (group.getCategories() != null) ? group.getCategories().size() : 0;

                    CategoryGroupCardNode cardNode = new CategoryGroupCardNode(
                            group,
                            g -> presenter.onAddCategoryClicked(),
                            g -> presenter.renameNeedGroup(g.getNeedGroupId(), g.getNeedGroupName()),
                            g -> presenter.deleteNeedGroup(g.getNeedGroupId(), g.getNeedGroupName()),
                            cat -> presenter.onEditCategoryClicked(cat.getCategoryId(), cat.getName(), cat.getParentGroupId(), cat.getTransactionType()),
                            cat -> presenter.deleteCategory(cat.getCategoryId(), cat.getName())
                    );

                    if (isIncome) {
                        incomeGroupCount++;
                        incomeCatCount += catCount;
                        incomeCardsBox.getChildren().add(cardNode);
                    } else {
                        expenseGroupCount++;
                        expenseCatCount += catCount;
                        expenseCardsBox.getChildren().add(cardNode);
                    }
                }
            }

            incomeCountLabel.setText(incomeGroupCount + " Nhóm • " + incomeCatCount + " Danh mục");
            expenseCountLabel.setText(expenseGroupCount + " Nhóm • " + expenseCatCount + " Danh mục");

            if (incomeCardsBox.getChildren().isEmpty()) {
                Label emptyInc = new Label("Chưa có nhóm thu nhập nào");
                emptyInc.setStyle("-fx-text-fill: -color-text-dim; -fx-padding: 12px; -fx-font-style: italic;");
                incomeCardsBox.getChildren().add(emptyInc);
            }
            if (expenseCardsBox.getChildren().isEmpty()) {
                Label emptyExp = new Label("Chưa có nhóm chi tiêu nào");
                emptyExp.setStyle("-fx-text-fill: -color-text-dim; -fx-padding: 12px; -fx-font-style: italic;");
                expenseCardsBox.getChildren().add(emptyExp);
            }
        });
    }

    @Override
    public void openCreateNeedGroupDialog() {
        openCreateNeedGroupDialog("EXPENSE");
    }

    @Override
    public void openCreateNeedGroupDialog(String defaultType) {
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            CreateNeedGroupDialog dialog = new CreateNeedGroupDialog(owner, this.controller, defaultType);
            dialog.showAndWait();
            if (dialog.isSuccess()) {
                this.presenter.loadData();
            }
        });
    }

    @Override
    public void openCreateCategoryDialog() {
        openCreateCategoryDialog("EXPENSE");
    }

    @Override
    public void openCreateCategoryDialog(String defaultType) {
        runOnFx(() -> {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            CreateCategoryDialog dialog = new CreateCategoryDialog(owner, this.controller, defaultType);
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
    public HBox getTwoZonesContainer() { return twoZonesContainer; }
    public VBox getIncomeColumn() { return incomeColumn; }
    public VBox getExpenseColumn() { return expenseColumn; }
    public VBox getIncomeCardsBox() { return incomeCardsBox; }
    public VBox getExpenseCardsBox() { return expenseCardsBox; }
    public Button getBtnFilterAll() { return btnFilterAll; }
    public Button getBtnFilterIncome() { return btnFilterIncome; }
    public Button getBtnFilterExpense() { return btnFilterExpense; }
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
