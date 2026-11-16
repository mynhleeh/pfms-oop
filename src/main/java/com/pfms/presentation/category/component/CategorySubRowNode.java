package com.pfms.presentation.category.component;

import com.pfms.presentation.dto.category.CategoryResponseDTO;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Thẻ dòng danh mục con cấp 2 (CategorySubRowNode) dàn ngang phẳng.
 * Phù hợp với bố cục 2 cột song song (Income trái, Expense phải).
 */
public class CategorySubRowNode extends HBox {

    private final CategoryResponseDTO category;
    private final Label indentLabel;
    private final Label nameLabel;
    private final Label noteLabel;
    private final Label statusBadge;
    private final Button btnEdit;
    private final Button btnDelete;

    public CategorySubRowNode(
            CategoryResponseDTO category,
            Consumer<CategoryResponseDTO> onEdit,
            Consumer<CategoryResponseDTO> onDelete) {

        this.category = Objects.requireNonNull(category, "category không được null");

        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(8);
        this.setMaxWidth(Double.MAX_VALUE);
        this.getStyleClass().add("subcat-row");

        // 1. Icon phân cấp (16px)
        this.indentLabel = new Label("└");
        this.indentLabel.setPrefWidth(16);
        this.indentLabel.setMinWidth(16);
        this.indentLabel.setMaxWidth(16);
        this.indentLabel.setAlignment(Pos.CENTER);
        this.indentLabel.setStyle("-fx-text-fill: #E5C07B; -fx-font-size: 12px; -fx-font-weight: bold;");

        // 2. Tên Danh Mục (90px - 160px)
        this.nameLabel = new Label(category.getName() != null ? category.getName() : "—");
        this.nameLabel.setPrefWidth(130);
        this.nameLabel.setMinWidth(90);
        this.nameLabel.setMaxWidth(160);
        this.nameLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        this.nameLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px; -fx-font-weight: 600;");

        // 3. Nhóm cha / Diễn Giải (Co giãn tự do Priority.ALWAYS)
        String parentName = category.getParentGroupName();
        boolean hasParent = parentName != null && !parentName.isBlank();
        this.noteLabel = new Label(hasParent ? parentName : "—");
        this.noteLabel.setMinWidth(0);
        this.noteLabel.setMaxWidth(Double.MAX_VALUE);
        this.noteLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        this.noteLabel.setStyle(hasParent ?
                "-fx-text-fill: -color-text-muted; -fx-font-size: 11.5px;" :
                "-fx-text-fill: -color-text-dim; -fx-font-size: 11.5px;");
        HBox.setHgrow(this.noteLabel, Priority.ALWAYS);

        // 4. Trạng Thái Badge (60px)
        String status = category.getStatus() != null ? category.getStatus().toUpperCase() : "ACTIVE";
        this.statusBadge = new Label(status);
        this.statusBadge.setPrefWidth(60);
        this.statusBadge.setMinWidth(60);
        this.statusBadge.setMaxWidth(60);
        this.statusBadge.setAlignment(Pos.CENTER);
        this.statusBadge.setStyle("-fx-background-color: rgba(229, 192, 123, 0.12); -fx-text-fill: #E5C07B; " +
                "-fx-background-radius: 4px; -fx-border-color: rgba(229, 192, 123, 0.25); -fx-border-radius: 4px; " +
                "-fx-padding: 2px 4px; -fx-font-size: 10px; -fx-font-weight: 600; -fx-alignment: CENTER;");

        // 5. Thao Tác (102px: 2 nút 48px + 6px gap)
        this.btnEdit = new Button("Sửa");
        this.btnEdit.getStyleClass().add("btn-action-sm");
        this.btnEdit.setPrefWidth(48);
        this.btnEdit.setMinWidth(48);
        this.btnEdit.setMaxWidth(48);
        this.btnEdit.setStyle("-fx-font-size: 11px; -fx-padding: 3px 0; -fx-alignment: CENTER;");
        if (onEdit != null) {
            this.btnEdit.setOnAction(e -> onEdit.accept(category));
        }

        this.btnDelete = new Button("Xóa");
        this.btnDelete.getStyleClass().addAll("btn-action-sm", "btn-danger");
        this.btnDelete.setPrefWidth(48);
        this.btnDelete.setMinWidth(48);
        this.btnDelete.setMaxWidth(48);
        this.btnDelete.setStyle("-fx-font-size: 11px; -fx-padding: 3px 0; -fx-alignment: CENTER;");
        if (onDelete != null) {
            this.btnDelete.setOnAction(e -> onDelete.accept(category));
        }

        HBox actionsBox = new HBox(6, this.btnEdit, this.btnDelete);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        actionsBox.setPrefWidth(102);
        actionsBox.setMinWidth(102);
        actionsBox.setMaxWidth(102);

        this.getChildren().addAll(
                this.indentLabel,
                this.nameLabel,
                this.noteLabel,
                this.statusBadge,
                actionsBox
        );
    }

    public CategoryResponseDTO getCategory() {
        return category;
    }

    public Label getIndentLabel() {
        return indentLabel;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getNoteLabel() {
        return noteLabel;
    }

    public Label getStatusBadge() {
        return statusBadge;
    }

    public Button getBtnEdit() {
        return btnEdit;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }
}
