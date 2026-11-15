package com.pfms.presentation.data;

import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;

import java.util.Objects;

/**
 * Bộ điều phối giao diện phân hệ Quản trị &amp; An toàn Dữ liệu theo mô hình MVP.
 * 100% không phụ thuộc các thư viện đồ họa Swing/AWT.
 */
public class DataManagementPresenter {

    // region Fields
    private final DataManagementView view;
    private final DataManagementGUIController controller;
    private Runnable onDataRestoredListener;
    // endregion

    // region Constructors
    public DataManagementPresenter(DataManagementView view, DataManagementGUIController controller) {
        this.view = Objects.requireNonNull(view, "DataManagementView không được để null");
        this.controller = Objects.requireNonNull(controller, "DataManagementGUIController không được để null");
    }
    // endregion

    // region Actions
    public void exportBackup(String destinationPath, String scope) {
        try {
            ExportBackupResponseDTO response = this.controller.handleExportBackup(destinationPath, scope);
            if (response != null) {
                String msg = String.format("Đã sao lưu tệp %s (%d bytes). Tổng số bản ghi: %d (Ví: %d, Danh mục: %d, Giao dịch: %d, Kiểm kê: %d)",
                        response.getFilePath(), response.getFileSizeBytes(), response.getTotalRecordCount(),
                        response.getWalletCount(), response.getCategoryCount(),
                        response.getTransactionCount(), response.getAdjustmentCount());
                this.view.displayExportResult(response, msg);
                this.view.showMessage("Xuất bản sao lưu dữ liệu thành công!");
            }
        } catch (Exception ex) {
            this.view.displayExportResult(null, "Xuất sao lưu thất bại: " + ex.getMessage());
            this.view.showError("Xuất sao lưu thất bại: " + ex.getMessage());
        }
    }

    public void restoreBackup(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            this.view.showWarning("Vui lòng nhập hoặc chọn đường dẫn tệp sao lưu JSON!");
            return;
        }
        boolean confirm = this.view.confirmAction(
                "CẢNH BÁO: Thao tác phục hồi sẽ ghi đè toàn bộ dữ liệu hiện hành của hệ thống bằng bản sao lưu này.\nBạn có chắc chắn muốn tiếp tục không?");
        if (!confirm) {
            return;
        }
        try {
            RestoreBackupResponseDTO response = this.controller.handleRestoreBackup(filePath);
            if (response != null && response.isSuccess()) {
                String msg = String.format("Phục hồi thành công! Tổng bản ghi: %d (Ví: %d, Danh mục: %d, Giao dịch: %d)",
                        response.getTotalRestoredRecords(), response.getRestoredWallets(),
                        response.getRestoredCategories(), response.getRestoredTransactions());
                this.view.displayRestoreResult(response, msg);
                this.view.showMessage("Phục hồi dữ liệu từ sao lưu thành công!");
                if (this.onDataRestoredListener != null) {
                    this.onDataRestoredListener.run();
                }
            } else {
                String err = (response != null) ? response.getMessage() : "Không nhận được phản hồi";
                this.view.displayRestoreResult(response, "Phục hồi thất bại: " + err);
                this.view.showError("Phục hồi dữ liệu thất bại: " + err);
            }
        } catch (Exception ex) {
            this.view.displayRestoreResult(null, "Lỗi phục hồi: " + ex.getMessage());
            this.view.showError("Phục hồi dữ liệu thất bại: " + ex.getMessage());
        }
    }

    public void reconcileData() {
        try {
            ReconciliationReportResponseDTO report = this.controller.handleReconcileSystemData();
            if (report != null) {
                this.view.displayReconciliationReport(report);
                if (report.isFullyConsistent()) {
                    this.view.showMessage("Đối soát hoàn tất: Hệ thống đạt trạng thái toàn vẹn 100%!");
                } else {
                    int count = report.getIssues() != null ? report.getIssues().size() : 0;
                    this.view.showWarning(String.format("Phát hiện %d vấn đề sai lệch toàn vẹn trong hệ thống!", count));
                }
            }
        } catch (Exception ex) {
            this.view.showError("Đối soát dữ liệu thất bại: " + ex.getMessage());
        }
    }
    // endregion

    // region Data
    // endregion

    // region Getters
    public DataManagementView getView() { return this.view; }
    public DataManagementGUIController getController() { return this.controller; }
    public Runnable getOnDataRestoredListener() { return this.onDataRestoredListener; }
    public void setOnDataRestoredListener(Runnable listener) { this.onDataRestoredListener = listener; }
    // endregion

    // region Helpers
    // endregion
}
