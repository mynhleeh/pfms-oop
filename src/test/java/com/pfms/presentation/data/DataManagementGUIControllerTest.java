package com.pfms.presentation.data;

import com.pfms.presentation.dto.data.ExportBackupRequestDTO;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupRequestDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;
import com.pfms.service.DataManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử DataManagementGUIController - Bộ điều phối giao diện phân hệ Quản trị Dữ liệu")
class DataManagementGUIControllerTest {

    private DataManagementService dataManagementService;
    private DataManagementGUIController controller;

    @BeforeEach
    void setUp() {
        dataManagementService = Mockito.mock(DataManagementService.class);
        controller = new DataManagementGUIController(dataManagementService);
    }

    @Nested
    @DisplayName("Kiểm thử Khởi tạo (Constructor)")
    class ConstructorTests {

        @Test
        @DisplayName("Ném NullPointerException khi DataManagementService là null")
        void constructor_NullService_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () -> new DataManagementGUIController(null));
        }

        @Test
        @DisplayName("Khởi tạo thành công khi truyền DataManagementService hợp lệ")
        void constructor_ValidService_InstantiatesSuccessfully() {
            assertNotNull(controller);
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleExportBackup (UC-DAT-01, FR-DAT-01)")
    class ExportBackupTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi đường dẫn đích để trống")
        void handleExportBackup_BlankDestinationPath_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleExportBackup("   ", "FULL"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi phạm vi sao lưu không phải FULL hoặc TRANSACTIONS")
        void handleExportBackup_InvalidScope_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleExportBackup("C:/backups", "INVALID_SCOPE"));
        }

        @Test
        @DisplayName("Xuất sao lưu phạm vi FULL thành công và ủy quyền xuống service")
        void handleExportBackup_ValidFullScope_DelegatesToService() {
            ExportBackupResponseDTO expected = new ExportBackupResponseDTO(
                    "C:/backups/backup.json", 1024L, LocalDateTime.now(), "FULL", 3, 10, 50, 2);
            when(dataManagementService.exportBackupData(any())).thenReturn(expected);

            ExportBackupResponseDTO result = controller.handleExportBackup(" C:/backups ", " full ");

            assertSame(expected, result);
            ArgumentCaptor<ExportBackupRequestDTO> captor = ArgumentCaptor.forClass(ExportBackupRequestDTO.class);
            verify(dataManagementService).exportBackupData(captor.capture());
            ExportBackupRequestDTO request = captor.getValue();
            assertEquals("C:/backups", request.getDestinationPath());
            assertEquals("FULL", request.getScope());
        }

        @Test
        @DisplayName("Xuất sao lưu phạm vi TRANSACTIONS thành công và ủy quyền xuống service")
        void handleExportBackup_ValidTransactionsScope_DelegatesToService() {
            ExportBackupResponseDTO expected = new ExportBackupResponseDTO(
                    "C:/backups/txns.json", 512L, LocalDateTime.now(), "TRANSACTIONS", 0, 0, 50, 0);
            when(dataManagementService.exportBackupData(any())).thenReturn(expected);

            ExportBackupResponseDTO result = controller.handleExportBackup("C:/backups", "TRANSACTIONS");

            assertSame(expected, result);
            ArgumentCaptor<ExportBackupRequestDTO> captor = ArgumentCaptor.forClass(ExportBackupRequestDTO.class);
            verify(dataManagementService).exportBackupData(captor.capture());
            ExportBackupRequestDTO request = captor.getValue();
            assertEquals("TRANSACTIONS", request.getScope());
        }

        @Test
        @DisplayName("Hỗ trợ phương thức nạp chồng mặc định phạm vi FULL khi không truyền scope")
        void handleExportBackup_OverloadNoScope_DefaultsToFullScope() {
            ExportBackupResponseDTO expected = new ExportBackupResponseDTO(
                    "C:/backups/backup.json", 1024L, LocalDateTime.now(), "FULL", 3, 10, 50, 2);
            when(dataManagementService.exportBackupData(any())).thenReturn(expected);

            ExportBackupResponseDTO result = controller.handleExportBackup("C:/backups");

            assertSame(expected, result);
            ArgumentCaptor<ExportBackupRequestDTO> captor = ArgumentCaptor.forClass(ExportBackupRequestDTO.class);
            verify(dataManagementService).exportBackupData(captor.capture());
            ExportBackupRequestDTO request = captor.getValue();
            assertEquals("FULL", request.getScope());
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleRestoreBackup (UC-DAT-02, FR-DAT-02)")
    class RestoreBackupTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi đường dẫn tệp sao lưu để trống")
        void handleRestoreBackup_BlankFilePath_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleRestoreBackup("   "));
        }

        @Test
        @DisplayName("Phục hồi dữ liệu thành công và ủy quyền xuống service")
        void handleRestoreBackup_ValidFilePath_DelegatesToService() {
            RestoreBackupResponseDTO expected = RestoreBackupResponseDTO.success(
                    "Phục hồi thành công", 3, 10, 50, 2);
            when(dataManagementService.restoreBackupData(any())).thenReturn(expected);

            RestoreBackupResponseDTO result = controller.handleRestoreBackup(" C:/backups/backup.json ");

            assertSame(expected, result);
            ArgumentCaptor<RestoreBackupRequestDTO> captor = ArgumentCaptor.forClass(RestoreBackupRequestDTO.class);
            verify(dataManagementService).restoreBackupData(captor.capture());
            RestoreBackupRequestDTO request = captor.getValue();
            assertEquals("C:/backups/backup.json", request.getBackupFilePath());
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleReconcileSystemData (UC-DAT-03, FR-DAT-03)")
    class ReconciliationTests {

        @Test
        @DisplayName("Kích hoạt đối soát toàn vẹn thành công và ủy quyền xuống service")
        void handleReconcileSystemData_DelegatesToService() {
            ReconciliationReportResponseDTO expected = ReconciliationReportResponseDTO.perfect(3, 50, 10, 2);
            when(dataManagementService.reconcileSystemData()).thenReturn(expected);

            ReconciliationReportResponseDTO result = controller.handleReconcileSystemData();

            assertSame(expected, result);
            verify(dataManagementService).reconcileSystemData();
        }
    }
}
