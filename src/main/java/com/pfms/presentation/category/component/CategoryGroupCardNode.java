package com.pfms.presentation.category.component;

import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Thẻ khối Nhóm nhu cầu cấp 1 (CategoryGroupCardNode).
 * Đóng gói header nhóm với các nút thao tác và danh sách thẻ dòng danh mục con cấp 2.
 */
public class CategoryGroupCardNode extends VBox {

    private final CategoryHierarchyResponseDTO group;
    private final Label titleLabel;
    private final Label typeBadge;
    private final Label countBadge;
    private final Button btnAddSub;
    private final Button btnRename;
    private final Button btnDelete;
    private final VBox subListContainer;

    public CategoryGroupCardNode(
            CategoryHierarchyResponseDTO group,
            Consumer<CategoryHierarchyResponseDTO> onAddSubCategory,
            Consumer<CategoryHierarchyResponseDTO> onRenameGroup,
            Consumer<CategoryHierarchyResponseDTO> onDeleteGroup,
            Consumer<CategoryResponseDTO> onEditSubCategory,
            Consumer<CategoryResponseDTO> onDeleteSubCategory) {

        this.group = Objects.requireNonNull(group, "group không được để null");

        this.getStyleClass().add("cat-group-card");
        this.setStyle("-fx-background-color: #141414; -fx-background-radius: 8px; " +
                "-fx-border-color: #242424; -fx-border-width: 1px; -fx-border-radius: 8px;");

        // 1. Header Box
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(8, 12, 8, 12));
        header.setStyle("-fx-background-color: #181818; -fx-background-radius: 7px 7px 0 0; " +
                "-fx-border-color: transparent transparent #222222 transparent; -fx-border-width: 0 0 1px 0;");

        Label iconLabel = new Label("📁");
        iconLabel.setStyle("-fx-font-size: 14px;");

        this.titleLabel = new Label(group.getNeedGroupName() != null ? group.getNeedGroupName() : "—");
        this.titleLabel.setStyle("-fx-text-fill: #FAFAFA; -fx-font-size: 13.5px; -fx-font-weight: bold;");
        this.titleLabel.setMinWidth(0);
        this.titleLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        HBox.setHgrow(this.titleLabel, Priority.SOMETIMES);

        boolean isIncome = "INCOME".equalsIgnoreCase(group.getTransactionType());
        this.typeBadge = new Label(isIncome ? "THU NHẬP" : "CHI TIÊU");
        this.typeBadge.setMinWidth(Region.USE_PREF_SIZE);
        if (isIncome) {
            this.typeBadge.setStyle("-fx-background-color: rgba(74, 222, 128, 0.12); -fx-text-fill: #4ADE80; " +
                    "-fx-background-radius: 4px; -fx-border-color: rgba(74, 222, 128, 0.25); -fx-border-radius: 4px; " +
                    "-fx-padding: 1px 6px; -fx-font-size: 10px; -fx-font-weight: 700;");
        } else {
            this.typeBadge.setStyle("-fx-background-color: rgba(251, 113, 133, 0.12); -fx-text-fill: #FB7185; " +
                    "-fx-background-radius: 4px; -fx-border-color: rgba(251, 113, 133, 0.25); -fx-border-radius: 4px; " +
                    "-fx-padding: 1px 6px; -fx-font-size: 10px; -fx-font-weight: 700;");
        }

        int count = group.getCategories() != null ? group.getCategories().size() : 0;
        this.countBadge = new Label(count + " danh mục con");
        this.countBadge.setMinWidth(Region.USE_PREF_SIZE);
        this.countBadge.setStyle("-fx-background-color: rgba(229, 192, 123, 0.12); -fx-text-fill: #E5C07B; " +
                "-fx-background-radius: 4px; -fx-border-color: rgba(229, 192, 123, 0.25); -fx-border-radius: 4px; " +
                "-fx-padding: 1px 6px; -fx-font-size: 10px; -fx-font-weight: 600;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.btnAddSub = new Button("+ Con");
        this.btnAddSub.getStyleClass().addAll("btn-action-sm", "btn-secondary");
        this.btnAddSub.setStyle("-fx-font-size: 10.5px; -fx-padding: 2px 7px;");
        if (onAddSubCategory != null) {
            this.btnAddSub.setOnAction(e -> onAddSubCategory.accept(group));
        }

        this.btnRename = new Button("Đổi tên");
        this.btnRename.getStyleClass().addAll("btn-action-sm", "btn-secondary");
        this.btnRename.setStyle("-fx-font-size: 10.5px; -fx-padding: 2px 7px;");
        if (onRenameGroup != null) {
            this.btnRename.setOnAction(e -> onRenameGroup.accept(group));
        }

        this.btnDelete = new Button("Xóa");
        this.btnDelete.getStyleClass().addAll("btn-action-sm", "btn-danger");
        this.btnDelete.setStyle("-fx-font-size: 10.5px; -fx-padding: 2px 7px;");
        if (onDeleteGroup != null) {
            this.btnDelete.setOnAction(e -> onDeleteGroup.accept(group));
        }

        HBox actionsBox = new HBox(5, this.btnAddSub, this.btnRename, this.btnDelete);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        actionsBox.setMinWidth(Region.USE_PREF_SIZE);

        header.getChildren().addAll(iconLabel, this.titleLabel, this.typeBadge, this.countBadge, spacer, actionsBox);

        // 2. Sub-list Container
        this.subListContainer = new VBox(6);
        this.subListContainer.setPadding(new Insets(8, 12, 10, 12));

        if (group.getCategories() != null && !group.getCategories().isEmpty()) {
            for (CategoryResponseDTO cat : group.getCategories()) {
                this.subListContainer.getChildren().add(new CategorySubRowNode(cat, onEditSubCategory, onDeleteSubCategory));
            }
        } else {
            Label emptyText = new Label("Chưa có danh mục con nào trong nhóm này");
            emptyText.setStyle("-fx-text-fill: #71717A; -fx-font-size: 11px; -fx-font-style: italic; -fx-padding: 4px 6px;");
            this.subListContainer.getChildren().add(emptyText);
        }

        this.getChildren().addAll(header, this.subListContainer);
    }

    public CategoryHierarchyResponseDTO getGroup() {
        return group;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Label getTypeBadge() {
        return typeBadge;
    }

    public Label getCountBadge() {
        return countBadge;
    }

    public Button getBtnAddSub() {
        return btnAddSub;
    }

    public Button getBtnRename() {
        return btnRename;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

    public VBox getSubListContainer() {
        return subListContainer;
    }
}
