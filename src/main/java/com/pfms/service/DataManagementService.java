package com.pfms.service;

import com.pfms.presentation.dto.data.BackupResponseDTO;
import com.pfms.presentation.dto.data.ExportBackupRequestDTO;
import com.pfms.presentation.dto.data.ReconciliationReportDTO;
import com.pfms.presentation.dto.data.RestoreBackupRequestDTO;
import com.pfms.presentation.dto.data.RestoreResultDTO;

/**
 * Dịch vụ quản trị và an toàn dữ liệu hệ thống PFMS.
 * Điều phối các nghiệp vụ: xuất sao lưu JSON, phục hồi dữ liệu và đối soát tính toàn vẹn tài chính.
 */
public interface DataManagementService {

    // region Commands
    BackupResponseDTO exportBackupData(ExportBackupRequestDTO requestDto);

    RestoreResultDTO restoreBackupData(RestoreBackupRequestDTO requestDto);
    // endregion

    // region Queries
    ReconciliationReportDTO reconcileSystemData();
    // endregion
}

