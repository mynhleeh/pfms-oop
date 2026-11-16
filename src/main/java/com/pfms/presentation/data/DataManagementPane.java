package com.pfms.presentation.data;

import com.pfms.presentation.common.util.FxAlertUtils;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Quản trị &amp; An toàn Dữ liệu (Data Management Pane) sử dụng JavaFX.
 * Hiện thực hóa giao diện DataManagementView theo mô hình MVP (Passive View).
 * Hỗ trợ: Xuất sao lưu JSON (UC-DAT-01), Phục hồi dữ liệu (UC-DAT-02), Đối soát hệ thống (UC-DAT-03).
 */
public class DataManagementPane extends BorderPane implements DataManagementView {

    // region Fields
    private final DataManagementPresenter presenter;
    private final String storagePath;

    // Controls: Export Backup
    private final TextField destinationField = new TextField("backups");
    private final ComboBox<String> scopeBox = new ComboBox<>();
    private final Button exportButton = new Button("Xuất Sao lưu");
    private final Label exportResultLabel = new Label("Chưa thực hiện sao lưu");

    // Controls: Restore Backup
    private final TextField restoreFileField = new TextField();
    private final Button restoreButton = new Button("Phục hồi Dữ liệu");
    private final Label restoreResultLabel = new Label("Chưa thực hiện phục hồi");

    // Controls: Reconciliation
    private final Button reconcileButton = new Button("Đối soát dữ liệu ngay");
    private final Label balanceStatusLabel = createStatusBadge("Số dư ví: Chưa kiểm tra", "#71717A");
    private final Label transferStatusLabel = createStatusBadge("Chuyển tiền: Chưa kiểm tra", "#71717A");
    private final Label categoryStatusLabel = createStatusBadge("Danh mục: Chưa kiểm tra", "#71717A");
    private final Label feeStatusLabel = createStatusBadge("Phụ phí: Chưa kiểm tra", "#71717A");

    private final ObservableList<ReconciliationIssueResponseDTO> issueList = FXCollections.observableArrayList();
    private final TableView<ReconciliationIssueResponseDTO> issueTable = new TableView<>(issueList);

    // Callbacks & Consumers
    private Consumer<String> messageConsumer;
    private Consumer<String> warningConsumer;
    private Consumer<String> errorConsumer;
    private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public DataManagementPane(DataManagementGUIController dataController, String storagePath) {
        Objects.requireNonNull(dataController, "DataManagementGUIController không được để null");
        this.storagePath = (storagePath != null && !storagePath.isBlank()) ? storagePath : "data/pfms_data.json";
        this.presenter = new DataManagementPresenter(this, dataController);

        initUI();
    }

    public DataManagementPane(DataManagementGUIController dataController) {
        this(dataController, "data/pfms_data.json");
    }
    // endregion

    // region UI Setup
    private void initUI() {
        setPadding(new Insets(20, 24, 20, 24));
        setStyle("-fx-background-color: -color-bg-base;");

        // 1. Header (North)
        VBox headerBox = new VBox(4);
        headerBox.setPadding(new Insets(0, 0, 16, 0));
        Label titleLabel = new Label("Quản trị dữ liệu");
        titleLabel.setStyle("-fx-text-fill: #E5C07B; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitleLabel = new Label("Sao lưu dữ liệu định kỳ, phục hồi hệ thống an toàn và đối soát tính toàn vẹn số liệu tài chính");
        subtitleLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 13px;");
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);
        setTop(headerBox);

        // 2. Content Center (SplitPane: Left = Backup & Restore, Right = Reconciliation)
        SplitPane splitPane = new SplitPane();
        splitPane.getStyleClass().add("split-pane");
        splitPane.setStyle("-fx-background-color: transparent; -fx-box-border: transparent; -fx-padding: 0;");

        VBox leftPane = createBackupRestoreCard();
        VBox rightPane = createReconciliationCard();

        leftPane.setMinWidth(380);
        leftPane.setPrefWidth(420);
        rightPane.setMinWidth(480);
        SplitPane.setResizableWithParent(leftPane, false);
        SplitPane.setResizableWithParent(rightPane, true);

        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.40);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        setCenter(splitPane);
        Platform.runLater(() -> splitPane.setDividerPositions(0.40));

        // 3. Footer (South)
        HBox footerBox = new HBox(8);
        footerBox.setPadding(new Insets(12, 0, 0, 0));
        footerBox.setAlignment(Pos.CENTER_LEFT);
        Label storageInfo = new Label("Tệp cơ sở dữ liệu hệ thống: " + this.storagePath);
        storageInfo.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 12px; -fx-font-family: 'Consolas', monospace;");
        footerBox.getChildren().add(storageInfo);
        setBottom(footerBox);
    }

    private VBox createBackupRestoreCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("pfms-card");
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: -color-card-bg; -fx-border-color: -color-border; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        // --- Block 1: Export Backup ---
        Label exportSectionTitle = new Label("Xuất bản sao lưu dữ liệu");
        exportSectionTitle.setStyle("-fx-text-fill: #E5C07B; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label destLabel = new Label("Thư mục đích:");
        destLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        destinationField.setPromptText("backups");
        destinationField.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");
        HBox.setHgrow(destinationField, Priority.ALWAYS);

        Button chooseDirBtn = new Button("Chọn...");
        chooseDirBtn.getStyleClass().add("btn-secondary");
        chooseDirBtn.setOnAction(e -> chooseDestinationFolder());

        HBox destRow = new HBox(8, destinationField, chooseDirBtn);
        destRow.setAlignment(Pos.CENTER_LEFT);

        Label scopeLabel = new Label("Phạm vi sao lưu:");
        scopeLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");

        scopeBox.getItems().setAll("FULL", "TRANSACTIONS");
        scopeBox.setValue("FULL");
        scopeBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(scopeBox, Priority.ALWAYS);
        setupScopeBoxLocalization();

        exportButton.getStyleClass().add("btn-primary");
        exportButton.setOnAction(e -> triggerExportBackup(destinationField.getText(), scopeBox.getValue()));

        HBox scopeRow = new HBox(8, scopeBox, exportButton);
        scopeRow.setAlignment(Pos.CENTER_LEFT);

        exportResultLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        exportResultLabel.setWrapText(true);

        VBox exportBlock = new VBox(8, exportSectionTitle, destLabel, destRow, scopeLabel, scopeRow, exportResultLabel);
        exportBlock.getStyleClass().add("data-card-block");
        exportBlock.setPadding(new Insets(12));
        exportBlock.setStyle("-fx-background-color: #18181B; -fx-border-color: #27272A; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        // --- Block 2: Restore Backup ---
        Label restoreSectionTitle = new Label("Phục hồi dữ liệu từ sao lưu");
        restoreSectionTitle.setStyle("-fx-text-fill: #E5C07B; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label restoreFileLabel = new Label("Tệp sao lưu:");
        restoreFileLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        restoreFileField.setPromptText("backups/pfms_backup_xxx.json");
        restoreFileField.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");
        HBox.setHgrow(restoreFileField, Priority.ALWAYS);

        Button chooseFileBtn = new Button("Chọn...");
        chooseFileBtn.getStyleClass().add("btn-secondary");
        chooseFileBtn.setOnAction(e -> chooseRestoreFile());

        restoreButton.getStyleClass().add("btn-primary");
        restoreButton.setOnAction(e -> triggerRestoreBackup(restoreFileField.getText()));

        HBox restoreRow = new HBox(8, restoreFileField, chooseFileBtn, restoreButton);
        restoreRow.setAlignment(Pos.CENTER_LEFT);

        restoreResultLabel.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        restoreResultLabel.setWrapText(true);

        VBox restoreBlock = new VBox(8, restoreSectionTitle, restoreFileLabel, restoreRow, restoreResultLabel);
        restoreBlock.getStyleClass().add("data-card-block");
        restoreBlock.setPadding(new Insets(12));
        restoreBlock.setStyle("-fx-background-color: #18181B; -fx-border-color: #27272A; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        card.getChildren().addAll(exportBlock, restoreBlock);
        return card;
    }

    private void setupScopeBoxLocalization() {
        scopeBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(String object) {
                if ("TRANSACTIONS".equals(object)) {
                    return "Chỉ dữ liệu giao dịch";
                }
                return "Toàn bộ cơ sở dữ liệu";
            }

            @Override
            public String fromString(String string) {
                if ("Chỉ dữ liệu giao dịch".equals(string)) {
                    return "TRANSACTIONS";
                }
                return "FULL";
            }
        });
        scopeBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if ("TRANSACTIONS".equals(item)) {
                    setText("Chỉ dữ liệu giao dịch");
                } else {
                    setText("Toàn bộ cơ sở dữ liệu");
                }
            }
        });
        scopeBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if ("TRANSACTIONS".equals(item)) {
                    setText("Chỉ dữ liệu giao dịch");
                } else {
                    setText("Toàn bộ cơ sở dữ liệu");
                }
            }
        });
    }

    private VBox createReconciliationCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("pfms-card");
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: -color-card-bg; -fx-border-color: -color-border; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        // Header Section
        Label reconcileTitle = new Label("Đối soát tính toàn vẹn hệ thống");
        reconcileTitle.setStyle("-fx-text-fill: #E5C07B; -fx-font-size: 14px; -fx-font-weight: bold;");

        reconcileButton.getStyleClass().add("btn-primary");
        reconcileButton.setOnAction(e -> triggerReconcile());

        HBox topRow = new HBox(12, reconcileTitle, reconcileButton);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Status Badges
        HBox badgesBox = new HBox(8, balanceStatusLabel, transferStatusLabel, categoryStatusLabel, feeStatusLabel);
        badgesBox.setAlignment(Pos.CENTER_LEFT);

        // Table setup
        setupIssueTable();
        VBox.setVgrow(issueTable, Priority.ALWAYS);

        card.getChildren().addAll(topRow, badgesBox, issueTable);
        return card;
    }

    @SuppressWarnings("unchecked")
    private void setupIssueTable() {
        TableColumn<ReconciliationIssueResponseDTO, String> severityCol = new TableColumn<>("Mức độ");
        severityCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeverity()));
        severityCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("severity-tag");
                    if ("ERROR".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("severity-tag-error");
                        badge.setStyle("-fx-text-fill: #FB7185; -fx-font-weight: bold; -fx-font-size: 11px;");
                    } else if ("WARNING".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("severity-tag-warning");
                        badge.setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold; -fx-font-size: 11px;");
                    } else {
                        badge.getStyleClass().add("severity-tag-info");
                        badge.setStyle("-fx-text-fill: #38BDF8; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        severityCol.setPrefWidth(85);

        TableColumn<ReconciliationIssueResponseDTO, String> ruleCodeCol = new TableColumn<>("Mã quy tắc");
        ruleCodeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRuleCode()));
        ruleCodeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    getStyleClass().add("cell-code");
                    setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 11px; -fx-text-fill: #E5C07B;");
                }
            }
        });
        ruleCodeCol.setPrefWidth(110);

        TableColumn<ReconciliationIssueResponseDTO, String> typeCol = new TableColumn<>("Loại sự cố");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIssueType()));
        typeCol.setPrefWidth(130);

        TableColumn<ReconciliationIssueResponseDTO, String> messageCol = new TableColumn<>("Nội dung sai lệch");
        messageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMessage()));
        messageCol.setCellFactory(col -> new TableCell<>() {
            private final Label label = new Label();
            {
                label.setWrapText(true);
                label.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 12px;");
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(label);
                }
            }
        });
        messageCol.setPrefWidth(220);

        TableColumn<ReconciliationIssueResponseDTO, String> entityCol = new TableColumn<>("Mã đối tượng");
        entityCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAffectedEntityId()));
        entityCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    getStyleClass().add("cell-entity");
                    setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 11px; -fx-text-fill: -color-text-muted;");
                }
            }
        });
        entityCol.setPrefWidth(110);

        TableColumn<ReconciliationIssueResponseDTO, String> recCol = new TableColumn<>("Khuyến nghị khắc phục");
        recCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRecommendation()));
        recCol.setCellFactory(col -> new TableCell<>() {
            private final Label label = new Label();
            {
                label.setWrapText(true);
                label.setStyle("-fx-text-fill: -color-text-main; -fx-font-size: 12px;");
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(label);
                }
            }
        });
        recCol.setPrefWidth(160);

        issueTable.getColumns().setAll(severityCol, ruleCodeCol, typeCol, messageCol, entityCol, recCol);
        issueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label placeholder = new Label("Không phát hiện vi phạm tính toàn vẹn số liệu. Hệ thống ở trạng thái nhất quán 100%.");
        placeholder.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 13px; -fx-padding: 40px;");
        issueTable.setPlaceholder(placeholder);
    }
    // endregion

    // region Actions
    public void triggerExportBackup(String destinationPath, String scope) {
        this.presenter.exportBackup(destinationPath, scope);
    }

    public void triggerRestoreBackup(String filePath) {
        this.presenter.restoreBackup(filePath);
    }

    public void triggerReconcile() {
        this.presenter.reconcileData();
    }

    private void chooseDestinationFolder() {
        Window window = getScene() != null ? getScene().getWindow() : null;
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Chọn thư mục lưu bản sao lưu");
        File initialDir = new File(destinationField.getText().trim());
        if (initialDir.exists() && initialDir.isDirectory()) {
            chooser.setInitialDirectory(initialDir);
        }
        File selected = chooser.showDialog(window);
        if (selected != null) {
            destinationField.setText(selected.getAbsolutePath());
        }
    }

    private void chooseRestoreFile() {
        Window window = getScene() != null ? getScene().getWindow() : null;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn tệp sao lưu dữ liệu");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tệp JSON (*.json)", "*.json"));
        File initialFile = new File(restoreFileField.getText().trim());
        if (initialFile.getParentFile() != null && initialFile.getParentFile().exists()) {
            chooser.setInitialDirectory(initialFile.getParentFile());
        }
        File selected = chooser.showOpenDialog(window);
        if (selected != null) {
            restoreFileField.setText(selected.getAbsolutePath());
        }
    }
    // endregion

    // region Data & Presentation Callbacks
    @Override
    public void displayExportResult(ExportBackupResponseDTO result, String summaryText) {
        runOnFx(() -> {
            exportResultLabel.setText(summaryText);
            if (result != null) {
                exportResultLabel.setStyle("-fx-text-fill: #4ADE80; -fx-font-size: 12px;");
            } else {
                exportResultLabel.setStyle("-fx-text-fill: #FB7185; -fx-font-size: 12px;");
            }
        });
    }

    @Override
    public void displayRestoreResult(RestoreBackupResponseDTO result, String summaryText) {
        runOnFx(() -> {
            restoreResultLabel.setText(summaryText);
            if (result != null && result.isSuccess()) {
                restoreResultLabel.setStyle("-fx-text-fill: #4ADE80; -fx-font-size: 12px;");
            } else {
                restoreResultLabel.setStyle("-fx-text-fill: #FB7185; -fx-font-size: 12px;");
            }
        });
    }

    @Override
    public void displayReconciliationReport(ReconciliationReportResponseDTO report) {
        if (report == null) {
            return;
        }
        runOnFx(() -> {
            updateBadge(balanceStatusLabel, "Số dư ví", report.isWalletBalanceConsistent());
            updateBadge(transferStatusLabel, "Chuyển tiền", report.isTransferConsistent());
            updateBadge(categoryStatusLabel, "Danh mục", report.isCategoryHierarchyConsistent());
            updateBadge(feeStatusLabel, "Phụ phí", report.isFeeConsistent());

            List<ReconciliationIssueResponseDTO> issues = report.getIssues();
            if (issues != null) {
                issueList.setAll(issues);
            } else {
                issueList.clear();
            }
        });
    }
    // endregion

    // region Dialog & Message Helpers
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
        if (confirmConsumer != null) {
            return confirmConsumer.test(message);
        }
        return FxAlertUtils.showConfirm("Xác nhận thao tác", message);
    }
    // endregion

    // region Badges & Styling Helpers
    private Label createStatusBadge(String text, String colorHex) {
        Label badge = new Label(text);
        badge.getStyleClass().addAll("status-badge", "status-badge-untested");
        badge.setStyle(
                "-fx-text-fill: #71717A; -fx-background-color: rgba(113, 113, 122, 0.15); " +
                "-fx-border-color: #71717A; -fx-border-width: 1px; -fx-border-radius: 12px; " +
                "-fx-background-radius: 12px; -fx-padding: 4px 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
        return badge;
    }

    private void updateBadge(Label badge, String prefix, boolean passed) {
        String statusText = passed ? "Hợp lệ" : "Sai lệch";
        String colorHex = passed ? "#4ADE80" : "#FB7185";
        String bgRgba = passed ? "rgba(74, 222, 128, 0.15)" : "rgba(251, 113, 133, 0.15)";
        badge.setText(prefix + ": " + statusText);
        badge.getStyleClass().removeAll("status-badge-untested", "status-badge-valid", "status-badge-invalid");
        badge.getStyleClass().addAll("status-badge", passed ? "status-badge-valid" : "status-badge-invalid");
        badge.setStyle(String.format(
                "-fx-text-fill: %s; -fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1px; " +
                "-fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 4px 10px; -fx-font-size: 11px; -fx-font-weight: bold;",
                colorHex, bgRgba, colorHex));
    }

    private void runOnFx(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
    // endregion

    // region Getters & Test Hooks
    public TableView<ReconciliationIssueResponseDTO> getIssueTable() {
        return this.issueTable;
    }

    public ObservableList<ReconciliationIssueResponseDTO> getIssueList() {
        return this.issueList;
    }

    public String getExportResultText() {
        return this.exportResultLabel.getText();
    }

    public String getRestoreResultText() {
        return this.restoreResultLabel.getText();
    }

    public DataManagementPresenter getPresenter() {
        return this.presenter;
    }

    public void setOnDataRestoredListener(Runnable listener) {
        this.presenter.setOnDataRestoredListener(listener);
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

    public TextField getDestinationField() {
        return destinationField;
    }

    public ComboBox<String> getScopeBox() {
        return scopeBox;
    }

    public Button getExportButton() {
        return exportButton;
    }

    public TextField getRestoreFileField() {
        return restoreFileField;
    }

    public Button getRestoreButton() {
        return restoreButton;
    }

    public Button getReconcileButton() {
        return reconcileButton;
    }
    // endregion
}
