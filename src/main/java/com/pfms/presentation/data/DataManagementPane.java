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
import javafx.scene.control.SplitPane;
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
        setStyle("-fx-background-color: #070707;");

        // 1. Header (North)
        VBox headerBox = new VBox(4);
        headerBox.setPadding(new Insets(0, 0, 16, 0));
        Label titleLabel = new Label("Quản trị & An toàn Dữ liệu (Backup, Restore & Integrity Audit)");
        titleLabel.setStyle("-fx-text-fill: #FAFAFA; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitleLabel = new Label("Sao lưu JSON định kỳ, phục hồi hệ thống và kiểm tra tính toàn vẹn 4 tầng số liệu tài chính");
        subtitleLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px;");
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);
        setTop(headerBox);

        // 2. Content Center (SplitPane: Left = Backup & Restore, Right = Reconciliation)
        SplitPane splitPane = new SplitPane();
        splitPane.setStyle("-fx-background-color: transparent; -fx-box-border: transparent;");

        VBox leftPane = createBackupRestoreCard();
        VBox rightPane = createReconciliationCard();

        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.42);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        setCenter(splitPane);

        // 3. Footer (South)
        HBox footerBox = new HBox(8);
        footerBox.setPadding(new Insets(12, 0, 0, 0));
        footerBox.setAlignment(Pos.CENTER_LEFT);
        Label storageInfo = new Label("Tệp cơ sở dữ liệu hệ thống: " + this.storagePath);
        storageInfo.setStyle("-fx-text-fill: #71717A; -fx-font-size: 12px;");
        footerBox.getChildren().add(storageInfo);
        setBottom(footerBox);
    }

    private VBox createBackupRestoreCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("pfms-card");
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #121212; -fx-border-color: #222222; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        // --- Block 1: Export Backup ---
        Label exportSectionTitle = new Label("Xuất Bản Sao lưu JSON (UC-DAT-01)");
        exportSectionTitle.setStyle("-fx-text-fill: #E5C07B; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label destLabel = new Label("Thư mục đích:");
        destLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px;");
        destinationField.setPromptText("backups");
        HBox.setHgrow(destinationField, Priority.ALWAYS);

        Button chooseDirBtn = new Button("Chọn...");
        chooseDirBtn.getStyleClass().add("btn-secondary");
        chooseDirBtn.setOnAction(e -> chooseDestinationFolder());

        HBox destRow = new HBox(8, destinationField, chooseDirBtn);
        destRow.setAlignment(Pos.CENTER_LEFT);

        Label scopeLabel = new Label("Phạm vi sao lưu:");
        scopeLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px;");

        scopeBox.getItems().setAll("FULL", "TRANSACTIONS");
        scopeBox.setValue("FULL");
        scopeBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(scopeBox, Priority.ALWAYS);

        exportButton.getStyleClass().add("btn-primary");
        exportButton.setOnAction(e -> triggerExportBackup(destinationField.getText(), scopeBox.getValue()));

        HBox scopeRow = new HBox(8, scopeBox, exportButton);
        scopeRow.setAlignment(Pos.CENTER_LEFT);

        exportResultLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px;");
        exportResultLabel.setWrapText(true);

        VBox exportBlock = new VBox(8, exportSectionTitle, destLabel, destRow, scopeLabel, scopeRow, exportResultLabel);
        exportBlock.setPadding(new Insets(12));
        exportBlock.setStyle("-fx-background-color: #18181B; -fx-border-color: #27272A; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        // --- Block 2: Restore Backup ---
        Label restoreSectionTitle = new Label("Phục hồi Dữ liệu từ Sao lưu (UC-DAT-02)");
        restoreSectionTitle.setStyle("-fx-text-fill: #FB7185; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label restoreFileLabel = new Label("Tệp sao lưu (.json):");
        restoreFileLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px;");
        restoreFileField.setPromptText("backups/pfms_backup_xxx.json");
        HBox.setHgrow(restoreFileField, Priority.ALWAYS);

        Button chooseFileBtn = new Button("Chọn...");
        chooseFileBtn.getStyleClass().add("btn-secondary");
        chooseFileBtn.setOnAction(e -> chooseRestoreFile());

        restoreButton.getStyleClass().add("btn-danger");
        restoreButton.setOnAction(e -> triggerRestoreBackup(restoreFileField.getText()));

        HBox restoreRow = new HBox(8, restoreFileField, chooseFileBtn, restoreButton);
        restoreRow.setAlignment(Pos.CENTER_LEFT);

        restoreResultLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px;");
        restoreResultLabel.setWrapText(true);

        VBox restoreBlock = new VBox(8, restoreSectionTitle, restoreFileLabel, restoreRow, restoreResultLabel);
        restoreBlock.setPadding(new Insets(12));
        restoreBlock.setStyle("-fx-background-color: #18181B; -fx-border-color: #27272A; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        card.getChildren().addAll(exportBlock, restoreBlock);
        return card;
    }

    private VBox createReconciliationCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("pfms-card");
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #121212; -fx-border-color: #222222; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        // Header Section
        Label reconcileTitle = new Label("Đối soát Tính Toàn vẹn Hệ thống (UC-DAT-03)");
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
        severityCol.setPrefWidth(80);

        TableColumn<ReconciliationIssueResponseDTO, String> ruleCodeCol = new TableColumn<>("Mã quy tắc");
        ruleCodeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRuleCode()));
        ruleCodeCol.setPrefWidth(110);

        TableColumn<ReconciliationIssueResponseDTO, String> typeCol = new TableColumn<>("Loại sự cố");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIssueType()));
        typeCol.setPrefWidth(130);

        TableColumn<ReconciliationIssueResponseDTO, String> messageCol = new TableColumn<>("Nội dung sai lệch");
        messageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMessage()));
        messageCol.setPrefWidth(220);

        TableColumn<ReconciliationIssueResponseDTO, String> entityCol = new TableColumn<>("Mã đối tượng");
        entityCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAffectedEntityId()));
        entityCol.setPrefWidth(110);

        TableColumn<ReconciliationIssueResponseDTO, String> recCol = new TableColumn<>("Khuyến nghị khắc phục");
        recCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRecommendation()));
        recCol.setPrefWidth(160);

        issueTable.getColumns().setAll(severityCol, ruleCodeCol, typeCol, messageCol, entityCol, recCol);
        issueTable.setPlaceholder(new Label("Không có sai lệch dữ liệu nào. Hệ thống đạt trạng thái toàn vẹn 100%."));
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
        badge.setStyle(String.format(
                "-fx-text-fill: %s; -fx-border-color: %s; -fx-border-width: 1px; -fx-border-radius: 4px; " +
                        "-fx-padding: 3px 8px; -fx-font-size: 11px; -fx-font-weight: bold;",
                colorHex, colorHex));
        return badge;
    }

    private void updateBadge(Label badge, String prefix, boolean passed) {
        String statusText = passed ? "HỢP LỆ" : "SAI LỆCH";
        String colorHex = passed ? "#4ADE80" : "#FB7185";
        badge.setText(prefix + ": " + statusText);
        badge.setStyle(String.format(
                "-fx-text-fill: %s; -fx-border-color: %s; -fx-border-width: 1px; -fx-border-radius: 4px; " +
                        "-fx-padding: 3px 8px; -fx-font-size: 11px; -fx-font-weight: bold;",
                colorHex, colorHex));
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
