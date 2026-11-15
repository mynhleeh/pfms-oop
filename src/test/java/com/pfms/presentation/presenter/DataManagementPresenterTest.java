package com.pfms.presentation.presenter;

import com.pfms.presentation.contract.DataManagementView;
import com.pfms.presentation.controller.DataManagementGUIController;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử DataManagementPresenter - Bộ điều phối phân hệ Quản trị & An toàn Dữ liệu MVP")
class DataManagementPresenterTest {

    private DataManagementView view;
    private DataManagementGUIController controller;
    private DataManagementPresenter presenter;

    @BeforeEach
    void setUp() {
        this.view = Mockito.mock(DataManagementView.class);
        this.controller = Mockito.mock(DataManagementGUIController.class);
        this.presenter = new DataManagementPresenter(this.view, this.controller);
    }

    @Test
    @DisplayName("Ném NullPointerException khi tham số constructor bị null")
    void testConstructorValidation() {
        assertThrows(NullPointerException.class, () -> new DataManagementPresenter(null, this.controller));
        assertThrows(NullPointerException.class, () -> new DataManagementPresenter(this.view, null));
    }

    @Test
    @DisplayName("exportBackup thành công gọi controller và cập nhật view")
    void testExportBackupSuccess() {
        ExportBackupResponseDTO response = new ExportBackupResponseDTO(
                "backups/pfms_2026.json", 4096L, LocalDateTime.now(), "FULL", 2, 5, 20, 1);
        when(this.controller.handleExportBackup("backups", "FULL")).thenReturn(response);

        this.presenter.exportBackup("backups", "FULL");

        verify(this.controller).handleExportBackup("backups", "FULL");
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.view).displayExportResult(eq(response), textCaptor.capture());
        assertTrue(textCaptor.getValue().contains("backups/pfms_2026.json"));
        assertTrue(textCaptor.getValue().contains("Tổng số bản ghi: 28"));
        verify(this.view).showMessage("Xuất bản sao lưu dữ liệu thành công!");
    }

    @Test
    @DisplayName("exportBackup xử lý ngoại lệ và hiển thị thông báo lỗi")
    void testExportBackupException() {
        when(this.controller.handleExportBackup(anyString(), anyString())).thenThrow(new RuntimeException("Disk full"));

        this.presenter.exportBackup("backups", "FULL");

        verify(this.view).displayExportResult(isNull(), contains("Xuất sao lưu thất bại: Disk full"));
        verify(this.view).showError(contains("Xuất sao lưu thất bại: Disk full"));
    }

    @Test
    @DisplayName("restoreBackup cảnh báo khi đường dẫn tệp trống")
    void testRestoreBackupEmptyPath() {
        this.presenter.restoreBackup("");
        this.presenter.restoreBackup(null);

        verify(this.view, times(2)).showWarning("Vui lòng nhập hoặc chọn đường dẫn tệp sao lưu JSON!");
        verify(this.controller, never()).handleRestoreBackup(anyString());
    }

    @Test
    @DisplayName("restoreBackup hủy bỏ khi người dùng không xác nhận")
    void testRestoreBackupCancelledByUser() {
        when(this.view.confirmAction(anyString())).thenReturn(false);

        this.presenter.restoreBackup("backups/test.json");

        verify(this.view).confirmAction(contains("CẢNH BÁO"));
        verify(this.controller, never()).handleRestoreBackup(anyString());
    }

    @Test
    @DisplayName("restoreBackup thành công gọi callback onDataRestoredListener")
    void testRestoreBackupSuccessCallsListener() {
        when(this.view.confirmAction(anyString())).thenReturn(true);
        RestoreBackupResponseDTO response = RestoreBackupResponseDTO.success("Phục hồi thành công", 2, 5, 20, 1);
        when(this.controller.handleRestoreBackup("backups/test.json")).thenReturn(response);

        AtomicBoolean restored = new AtomicBoolean(false);
        this.presenter.setOnDataRestoredListener(() -> restored.set(true));

        this.presenter.restoreBackup("backups/test.json");

        verify(this.controller).handleRestoreBackup("backups/test.json");
        verify(this.view).displayRestoreResult(eq(response), contains("Phục hồi thành công! Tổng bản ghi: 28"));
        verify(this.view).showMessage("Phục hồi dữ liệu từ sao lưu thành công!");
        assertTrue(restored.get());
    }

    @Test
    @DisplayName("restoreBackup thất bại không gọi callback onDataRestoredListener")
    void testRestoreBackupFailure() {
        when(this.view.confirmAction(anyString())).thenReturn(true);
        RestoreBackupResponseDTO response = RestoreBackupResponseDTO.failure("Checksum không khớp");
        when(this.controller.handleRestoreBackup("backups/corrupt.json")).thenReturn(response);

        AtomicBoolean restored = new AtomicBoolean(false);
        this.presenter.setOnDataRestoredListener(() -> restored.set(true));

        this.presenter.restoreBackup("backups/corrupt.json");

        verify(this.view).displayRestoreResult(eq(response), contains("Phục hồi thất bại: Checksum không khớp"));
        verify(this.view).showError(contains("Phục hồi dữ liệu thất bại: Checksum không khớp"));
        assertFalse(restored.get());
    }

    @Test
    @DisplayName("restoreBackup xử lý ngoại lệ và hiển thị thông báo lỗi")
    void testRestoreBackupException() {
        when(this.view.confirmAction(anyString())).thenReturn(true);
        when(this.controller.handleRestoreBackup(anyString())).thenThrow(new RuntimeException("File corrupted"));

        this.presenter.restoreBackup("backups/test.json");

        verify(this.view).displayRestoreResult(isNull(), contains("Lỗi phục hồi: File corrupted"));
        verify(this.view).showError(contains("Phục hồi dữ liệu thất bại: File corrupted"));
    }

    @Test
    @DisplayName("reconcileData thành công khi hệ thống toàn vẹn 100%")
    void testReconcileDataConsistent() {
        ReconciliationReportResponseDTO report = new ReconciliationReportResponseDTO(
                true, true, true, true, 2, 10, 5, 1, List.of());
        when(this.controller.handleReconcileSystemData()).thenReturn(report);

        this.presenter.reconcileData();

        verify(this.view).displayReconciliationReport(report);
        verify(this.view).showMessage("Đối soát hoàn tất: Hệ thống đạt trạng thái toàn vẹn 100%!");
    }

    @Test
    @DisplayName("reconcileData cảnh báo khi phát hiện vấn đề sai lệch")
    void testReconcileDataWithIssues() {
        ReconciliationIssueResponseDTO issue1 = new ReconciliationIssueResponseDTO(
                "ERROR", "RULE-BAL-01", "Sai lệch số dư", "Ví bị lệch", "Kiểm kê");
        ReconciliationIssueResponseDTO issue2 = new ReconciliationIssueResponseDTO(
                "WARN", "RULE-FEE-01", "Phụ phí sai lệch", "Lệch phí", "Sửa phí");
        ReconciliationReportResponseDTO report = new ReconciliationReportResponseDTO(
                false, true, true, false, 2, 10, 5, 1, List.of(issue1, issue2));
        when(this.controller.handleReconcileSystemData()).thenReturn(report);

        this.presenter.reconcileData();

        verify(this.view).displayReconciliationReport(report);
        verify(this.view).showWarning(contains("Phát hiện 2 vấn đề sai lệch toàn vẹn trong hệ thống!"));
    }

    @Test
    @DisplayName("reconcileData xử lý ngoại lệ và hiển thị thông báo lỗi")
    void testReconcileDataException() {
        when(this.controller.handleReconcileSystemData()).thenThrow(new RuntimeException("Database locked"));

        this.presenter.reconcileData();

        verify(this.view).showError(contains("Đối soát dữ liệu thất bại: Database locked"));
    }
}
