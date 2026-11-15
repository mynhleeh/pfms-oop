package com.pfms.presentation.view.data;

import com.pfms.presentation.controller.DataManagementGUIController;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử DataManagementPanel - Bảng điều khiển quản trị và an toàn dữ liệu")
class DataManagementPanelTest {

    private DataManagementGUIController dataController;
    private DataManagementPanel panel;

    @BeforeEach
    void setUp() {
        dataController = Mockito.mock(DataManagementGUIController.class);

        ExportBackupResponseDTO exportResponse = new ExportBackupResponseDTO(
                "backups/pfms_backup.json", 2048L, LocalDateTime.now(), "FULL", 2, 5, 10, 1);
        when(dataController.handleExportBackup(anyString(), anyString())).thenReturn(exportResponse);

        RestoreBackupResponseDTO restoreResponse = RestoreBackupResponseDTO.success(
                "Phục hồi thành công", 2, 5, 10, 1);
        when(dataController.handleRestoreBackup(anyString())).thenReturn(restoreResponse);

        ReconciliationIssueResponseDTO issue1 = new ReconciliationIssueResponseDTO(
                "ERROR", "RULE-BAL-01", "Sai lệch số dư", "Ví bị lệch", "Kiểm kê");
        ReconciliationReportResponseDTO reconcileReport = new ReconciliationReportResponseDTO(
                false, true, true, true, 2, 10, 5, 1, List.of(issue1));
        when(dataController.handleReconcileSystemData()).thenReturn(reconcileReport);

        panel = new DataManagementPanel(dataController, "test_data.json");
        panel.setMessageConsumer(msg -> {});
        panel.setErrorConsumer(msg -> {});
        panel.setWarningConsumer(msg -> {});
        panel.setConfirmConsumer(msg -> true);
    }

    @Test
    @DisplayName("Khởi tạo bảng điều khiển nạp sẵn các trường và cấu trúc mặc định")
    void testInitialPanelStructure() {
        assertNotNull(panel.getIssueTable());
        assertNotNull(panel.getIssueTableModel());
        assertEquals(0, panel.getIssueTableModel().getRowCount());
    }

    @Test
    @DisplayName("Thực thi xuất sao lưu dữ liệu gọi DataManagementGUIController và cập nhật kết quả")
    void testTriggerExportBackup() {
        panel.triggerExportBackup("backups", "FULL");

        verify(dataController).handleExportBackup("backups", "FULL");
        assertTrue(panel.getExportResultText().contains("backups/pfms_backup.json"));
        assertTrue(panel.getExportResultText().contains("Tổng số bản ghi: 18"));
    }

    @Test
    @DisplayName("Thực thi phục hồi dữ liệu gọi controller và kích hoạt callback onDataRestored")
    void testTriggerRestoreBackupCallsCallback() {
        AtomicBoolean restored = new AtomicBoolean(false);
        panel.setOnDataRestoredListener(() -> restored.set(true));

        panel.triggerRestoreBackup("backups/valid_backup.json");

        verify(dataController).handleRestoreBackup("backups/valid_backup.json");
        assertTrue(panel.getRestoreResultText().contains("Phục hồi thành công"));
        assertTrue(restored.get());
    }

    @Test
    @DisplayName("Thực thi đối soát dữ liệu gọi controller và cập nhật danh sách sự cố vào bảng")
    void testTriggerReconcile() {
        panel.triggerReconcile();

        verify(dataController).handleReconcileSystemData();
        assertEquals(1, panel.getIssueTableModel().getRowCount());
        assertEquals("RULE-BAL-01", panel.getIssueTableModel().getValueAt(0, 1));
    }

    @Test
    @DisplayName("Ném NullPointerException khi DataManagementGUIController bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new DataManagementPanel(null, "path.json"));
    }
}
