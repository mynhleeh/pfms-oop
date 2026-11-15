package com.pfms.presentation.controller;

import com.pfms.presentation.dto.data.ExportBackupRequestDTO;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupRequestDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;
import com.pfms.presentation.util.InputValidator;
import com.pfms.service.DataManagementService;

import java.util.Locale;
import java.util.Objects;

/**
 * Bộ điều phối giao diện đồ họa cho phân hệ Quản trị & An toàn dữ liệu (DAT).
 * Tiếp nhận yêu cầu xuất sao lưu JSON, phục hồi dữ liệu và kích hoạt đối soát tính toàn vẹn hệ thống.
 */
public final class DataManagementGUIController {

    // region State
    private final DataManagementService dataManagementService;
    // endregion

    // region Constructors
    public DataManagementGUIController(DataManagementService dataManagementService) {
        this.dataManagementService = Objects.requireNonNull(
                dataManagementService, "DataManagementService không được để null");
    }
    // endregion

    // region Operations
    // region Export Backup
    public ExportBackupResponseDTO handleExportBackup(String rawDestinationPath, String rawScope) {
        String destinationPath = InputValidator.validateNonEmpty(rawDestinationPath, "Đường dẫn thư mục đích");

        String scope = (rawScope == null || rawScope.isBlank())
                ? ExportBackupRequestDTO.DEFAULT_SCOPE
                : rawScope.trim().toUpperCase(Locale.ROOT);

        if (!ExportBackupRequestDTO.DEFAULT_SCOPE.equals(scope)
                && !ExportBackupRequestDTO.TRANSACTIONS_SCOPE.equals(scope)) {
            throw new IllegalArgumentException("Phạm vi sao lưu phải là FULL hoặc TRANSACTIONS");
        }

        ExportBackupRequestDTO request = new ExportBackupRequestDTO(destinationPath, scope);
        return this.dataManagementService.exportBackupData(request);
    }

    public ExportBackupResponseDTO handleExportBackup(String rawDestinationPath) {
        return handleExportBackup(rawDestinationPath, ExportBackupRequestDTO.DEFAULT_SCOPE);
    }
    // endregion

    // region Restore Backup
    public RestoreBackupResponseDTO handleRestoreBackup(String rawBackupFilePath) {
        String filePath = InputValidator.validateNonEmpty(rawBackupFilePath, "Đường dẫn tệp sao lưu");

        RestoreBackupRequestDTO request = new RestoreBackupRequestDTO(filePath);
        return this.dataManagementService.restoreBackupData(request);
    }
    // endregion

    // region Reconcile
    public ReconciliationReportResponseDTO handleReconcileSystemData() {
        return this.dataManagementService.reconcileSystemData();
    }
    // endregion
    // endregion
}
