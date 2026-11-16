package com.pfms.presentation.common;

import com.pfms.presentation.common.util.FxAlertUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Lớp trừu tượng cơ sở cho các bảng điều khiển phân hệ (Panes) trong PFMS.
 * Quản lý vòng đời hiển thị, điều phối đa luồng JavaFX và hệ thống thông báo chuẩn.
 */
public abstract class AbstractBasePane extends BorderPane implements BaseView {

    protected final Label summaryFooterLabel = new Label("Sẵn sàng");

    protected Consumer<String> messageConsumer;
    protected Consumer<String> warningConsumer;
    protected Consumer<String> errorConsumer;
    protected Predicate<String> confirmConsumer;

    protected AbstractBasePane() {
        this(true);
    }

    protected AbstractBasePane(boolean withDefaultFooter) {
        setStyle("-fx-background-color: -color-bg-base;");
        setPadding(new Insets(20));

        if (withDefaultFooter) {
            HBox bottomBox = new HBox(this.summaryFooterLabel);
            bottomBox.setPadding(new Insets(10, 0, 0, 0));
            this.summaryFooterLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");
            setBottom(bottomBox);
        }
    }

    /**
     * Điều phối thực thi an toàn trên luồng JavaFX Application Thread.
     */
    public void runOnFx(Runnable action) {
        if (action == null) {
            return;
        }
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    @Override
    public void showMessage(String message) {
        if (this.messageConsumer != null) {
            this.messageConsumer.accept(message);
        } else {
            FxAlertUtils.showInfo("PFMS Thông báo", message);
        }
    }

    @Override
    public void showWarning(String message) {
        if (this.warningConsumer != null) {
            this.warningConsumer.accept(message);
        } else {
            FxAlertUtils.showWarning("PFMS Cảnh báo", message);
        }
    }

    @Override
    public void showError(String message) {
        if (this.errorConsumer != null) {
            this.errorConsumer.accept(message);
        } else {
            FxAlertUtils.showError("PFMS Lỗi", message);
        }
    }

    @Override
    public boolean confirmAction(String message) {
        if (this.confirmConsumer != null) {
            return this.confirmConsumer.test(message);
        }
        return FxAlertUtils.showConfirm("Xác nhận thao tác", message);
    }

    public String getSummaryText() {
        return this.summaryFooterLabel.getText();
    }

    public void setMessageConsumer(Consumer<String> consumer) {
        this.messageConsumer = consumer;
    }

    public void setWarningConsumer(Consumer<String> consumer) {
        this.warningConsumer = consumer;
    }

    public void setErrorConsumer(Consumer<String> consumer) {
        this.errorConsumer = consumer;
    }

    public void setConfirmConsumer(Predicate<String> consumer) {
        this.confirmConsumer = consumer;
    }
}
