package com.pfms.service;

import com.pfms.presentation.dto.data.ExportBackupRequestDTO;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupRequestDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;

/**
 * Dịch vụ quản trị và an toàn dữ liệu hệ thống PFMS.
 * Điều phối các nghiệp vụ: xuất sao lưu JSON, phục hồi dữ liệu và đối soát tính toàn vẹn tài chính.
 */
public interface DataManagementService {

    // region Commands
    ExportBackupResponseDTO exportBackupData(ExportBackupRequestDTO request);

    RestoreBackupResponseDTO restoreBackupData(RestoreBackupRequestDTO request);
    // endregion

    // region Queries
    ReconciliationReportResponseDTO reconcileSystemData();
    // endregion
}

