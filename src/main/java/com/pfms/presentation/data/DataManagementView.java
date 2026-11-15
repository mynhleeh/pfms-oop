package com.pfms.presentation.data;

import com.pfms.presentation.common.BaseView;

import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;

/**
 * Hợp đồng hiển thị cho phân hệ Quản trị &amp; An toàn Dữ liệu theo kiến trúc MVP Passive View.
 * 100% không phụ thuộc các thư viện giao diện đồ họa Swing/AWT.
 */
public interface DataManagementView extends BaseView {

    void displayExportResult(ExportBackupResponseDTO result, String summaryText);

    void displayRestoreResult(RestoreBackupResponseDTO result, String summaryText);

    void displayReconciliationReport(ReconciliationReportResponseDTO report);
}
