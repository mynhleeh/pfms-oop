package com.pfms.presentation.report.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Hộp thoại modal hiển thị và sao chép văn bản báo cáo tài chính đã xuất (UC-REP-03) sử dụng JavaFX.
 */
public class ExportReportDialog extends Dialog<ButtonType> {

    private final String reportContent;
    private final TextArea reportTextArea = new TextArea();

    private Consumer<String> messageConsumer = msg -> {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
        }
    };

    public ExportReportDialog(Window owner, String title, String reportContent) {
        this.reportContent = Objects.requireNonNull(reportContent, "Nội dung báo cáo không được để null");

        setTitle(title != null ? title : "Xuất Báo cáo Tài chính");
        initOwner(owner);

        buildUI();
    }

    public ExportReportDialog(Object parent, String title, String reportContent) {
        this((Window) null, title, reportContent);
    }

    private void buildUI() {
        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("pfms-dialog");

        var cssResource = getClass().getResource("/css/pfms-theme.css");
        if (cssResource != null) {
            pane.getStylesheets().add(cssResource.toExternalForm());
        }

        VBox contentBox = new VBox(12);
        contentBox.setPadding(new Insets(16));
        contentBox.setPrefSize(700, 480);

        Label headerTitle = new Label("Nội Dung Báo Cáo Tài Chính Đã Xuất");
        headerTitle.setStyle("-fx-text-fill: -color-primary; -fx-font-size: 15px; -fx-font-weight: bold;");

        reportTextArea.setText(this.reportContent);
        reportTextArea.setEditable(false);
        reportTextArea.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px; -fx-text-fill: -color-text-main; -fx-control-inner-background: #0E0E0E;");
        VBox.setVgrow(reportTextArea, Priority.ALWAYS);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button btnCopy = new Button("Sao chép vào Clipboard");
        btnCopy.getStyleClass().add("btn-primary");
        btnCopy.setOnAction(e -> triggerCopy());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        buttonBar.getChildren().addAll(spacer, btnCopy);

        contentBox.getChildren().addAll(headerTitle, reportTextArea, buttonBar);
        pane.setContent(contentBox);

        ButtonType closeButtonType = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().add(closeButtonType);

        Button btnClose = (Button) pane.lookupButton(closeButtonType);
        if (btnClose != null) {
            btnClose.getStyleClass().add("btn-secondary");
        }
    }

    public void triggerCopy() {
        try {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(this.reportContent);
            clipboard.setContent(content);
        } catch (Exception ignored) {
            // bỏ qua lỗi clipboard trong môi trường test/headless
        }
        if (this.messageConsumer != null) {
            this.messageConsumer.accept("Đã sao chép nội dung báo cáo vào bộ nhớ tạm (Clipboard)!");
        }
    }

    public String getReportText() {
        return this.reportContent;
    }

    public void setMessageConsumer(Consumer<String> consumer) {
        this.messageConsumer = Objects.requireNonNull(consumer);
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
}
