package com.pfms.presentation.data;

import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử DataManagementPane - Giao diện JavaFX quản trị và an toàn dữ liệu")
class DataManagementPaneTest {

    private DataManagementGUIController dataController;
    private DataManagementPane pane;

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

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

        JavaFxTestHelper.runOnFxThread(() -> {
            pane = new DataManagementPane(dataController, "test_data.json");
            pane.setMessageConsumer(msg -> {});
            pane.setErrorConsumer(msg -> {});
            pane.setWarningConsumer(msg -> {});
            pane.setConfirmConsumer(msg -> true);
        });
    }

    @Test
    @DisplayName("Khởi tạo bảng điều khiển nạp sẵn các trường và cấu trúc mặc định")
    void testInitialPaneStructure() {
        assertNotNull(pane.getIssueTable());
        assertNotNull(pane.getIssueList());
        assertEquals(0, pane.getIssueList().size());
        assertEquals("backups", pane.getDestinationField().getText());
        assertEquals("FULL", pane.getScopeBox().getValue());
    }

    @Test
    @DisplayName("Thực thi xuất sao lưu dữ liệu gọi DataManagementGUIController và cập nhật kết quả")
    void testTriggerExportBackup() {
        JavaFxTestHelper.runOnFxThread(() -> {
            pane.triggerExportBackup("backups", "FULL");
        });

        verify(dataController).handleExportBackup("backups", "FULL");
        assertTrue(pane.getExportResultText().contains("backups/pfms_backup.json"));
        assertTrue(pane.getExportResultText().contains("Tổng số bản ghi: 18"));
    }

    @Test
    @DisplayName("Thực thi phục hồi dữ liệu gọi controller và kích hoạt callback onDataRestored")
    void testTriggerRestoreBackupCallsCallback() {
        AtomicBoolean restored = new AtomicBoolean(false);
        pane.setOnDataRestoredListener(() -> restored.set(true));

        JavaFxTestHelper.runOnFxThread(() -> {
            pane.triggerRestoreBackup("backups/valid_backup.json");
        });

        verify(dataController).handleRestoreBackup("backups/valid_backup.json");
        assertTrue(pane.getRestoreResultText().contains("Phục hồi thành công"));
        assertTrue(restored.get());
    }

    @Test
    @DisplayName("Thực thi đối soát dữ liệu gọi controller và cập nhật danh sách sự cố vào bảng")
    void testTriggerReconcile() {
        JavaFxTestHelper.runOnFxThread(() -> {
            pane.triggerReconcile();
        });

        verify(dataController).handleReconcileSystemData();
        assertEquals(1, pane.getIssueList().size());
        assertEquals("RULE-BAL-01", pane.getIssueList().get(0).getRuleCode());
        assertEquals("ERROR", pane.getIssueList().get(0).getSeverity());
    }

    @Test
    @DisplayName("Ném NullPointerException khi DataManagementGUIController bị null")
    void testNullControllerThrowsException() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertThrows(NullPointerException.class, () -> new DataManagementPane(null, "path.json"));
        });
    }
}
