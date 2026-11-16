package com.pfms.presentation.common.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.function.Consumer;

/**
 * Lớp trừu tượng cơ sở cho các hộp thoại nhập liệu (Modal Form Dialogs) trong PFMS.
 * Quản lý cấu trúc giao diện Luxury Black & Gold, vòng đời modal và bẫy lỗi tự động theo Template Method.
 *
 * @param <R> Kiểu đối tượng DTO kết quả trả về khi lưu thành công
 */
public abstract class BaseFormDialog<R> extends Dialog<ButtonType> {

    protected boolean success = false;
    protected R resultData = null;
    protected String errorMessage = null;
    protected Consumer<String> errorConsumer = this::defaultAlertError;

    protected BaseFormDialog(Window owner, String title, int prefWidth) {
        if (title != null) {
            setTitle(title);
        }
        if (owner != null) {
            initOwner(owner);
        }

        DialogPane pane = getDialogPane();
        pane.setPrefWidth(prefWidth);
        pane.setMinWidth(prefWidth);

        var cssResource = getClass().getResource("/css/pfms-theme.css");
        if (cssResource != null) {
            pane.getStylesheets().add(cssResource.toExternalForm());
        }
    }

    /**
     * Dựng khối tiêu đề chuẩn: Tiêu đề chính vàng gold và phụ đề xám nhạt.
     */
    protected VBox createHeaderBox(String titleText, String subtitleText) {
        VBox headerBox = new VBox(4);
        headerBox.setPadding(new Insets(0, 0, 8, 0));

        Label titleLabel = new Label(titleText != null ? titleText : "");
        titleLabel.setStyle("-fx-text-fill: -color-primary-light; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label(subtitleText != null ? subtitleText : "");
        subtitleLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 11px;");

        headerBox.getChildren().addAll(titleLabel, subtitleLabel);
        return headerBox;
    }

    /**
     * Dựng khung chứa form nhập liệu với padding và màu nền chuẩn #141414.
     */
    protected VBox createFormBox(int spacing, Node... children) {
        VBox form = new VBox(spacing);
        form.setPadding(new Insets(18, 22, 18, 22));
        form.setStyle("-fx-background-color: #141414;");
        if (children != null && children.length > 0) {
            form.getChildren().addAll(children);
        }
        return form;
    }

    /**
     * Cấu hình các nút bấm OK và Cancel tiêu chuẩn cùng event filter chặn đóng khi lỗi.
     */
    protected void setupButtons(String okText, String cancelText) {
        DialogPane pane = getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final var okButton = pane.lookupButton(ButtonType.OK);
        if (okButton instanceof Button btn) {
            btn.setText(okText != null ? okText : "Lưu");
            btn.setDefaultButton(true);
        }
        if (okButton != null) {
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                triggerSave();
                if (!isSuccess()) {
                    event.consume();
                }
            });
        }

        final var cancelButton = pane.lookupButton(ButtonType.CANCEL);
        if (cancelButton instanceof Button btn) {
            btn.setText(cancelText != null ? cancelText : "Hủy bỏ");
        }
    }

    /**
     * Phương thức mẫu (Template Method): Thực thi nghiệp vụ lưu dữ liệu.
     * Ném Exception nếu dữ liệu không hợp lệ hoặc thao tác thất bại.
     *
     * @return DTO kết quả sau khi lưu thành công
     * @throws Exception Ngoại lệ nghiệp vụ hoặc kỹ thuật nếu có lỗi
     */
    protected abstract R onExecuteSave() throws Exception;

    /**
     * Kích hoạt lưu dữ liệu: gọi Template Method, bắt lỗi và cập nhật cờ thành công.
     */
    public void triggerSave() {
        try {
            this.resultData = onExecuteSave();
            this.success = true;
            this.errorMessage = null;
            dispose();
        } catch (Exception ex) {
            this.errorMessage = ex.getMessage();
            this.success = false;
            this.resultData = null;
            if (this.errorConsumer != null) {
                this.errorConsumer.accept(ex.getMessage());
            }
        }
    }

    protected void defaultAlertError(String msg) {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi thao tác");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        } catch (Exception ignored) {
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

    public R getResultData() {
        return this.resultData;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }
}
