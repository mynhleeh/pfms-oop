package com.pfms.presentation.wallet;

import com.pfms.presentation.dto.wallet.AdjustBalanceRequestDTO;
import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.wallet.CreateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.UpdateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử WalletGUIController - Bộ điều phối giao diện phân hệ Quản lý Ví")
class WalletGUIControllerTest {

    private WalletService walletService;
    private WalletGUIController controller;

    @BeforeEach
    void setUp() {
        walletService = Mockito.mock(WalletService.class);
        controller = new WalletGUIController(walletService);
    }

    @Nested
    @DisplayName("Kiểm thử Khởi tạo")
    class ConstructorTests {

        @Test
        @DisplayName("Ném NullPointerException khi WalletService là null")
        void constructor_NullService_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () -> new WalletGUIController(null));
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleCreateWallet (UC-WAL-01)")
    class CreateWalletTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên ví để trống")
        void handleCreateWallet_BlankName_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleCreateWallet("   ", "Ghi chú"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên ví vượt quá 50 ký tự")
        void handleCreateWallet_NameExceeds50_ThrowsIllegalArgumentException() {
            String longName = "A".repeat(51);
            assertThrows(IllegalArgumentException.class, () -> controller.handleCreateWallet(longName, "Ghi chú"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ghi chú vượt quá 255 ký tự")
        void handleCreateWallet_NoteExceeds255_ThrowsIllegalArgumentException() {
            String longNote = "B".repeat(256);
            assertThrows(IllegalArgumentException.class, () -> controller.handleCreateWallet("Ví Tiền Mặt", longNote));
        }

        @Test
        @DisplayName("Cắt tỉa chuỗi và ủy quyền gọi walletService.createWallet khi dữ liệu hợp lệ")
        void handleCreateWallet_ValidInputs_TrimsAndDelegatesToService() {
            UUID walletId = UUID.randomUUID();
            WalletResponseDTO expected = new WalletResponseDTO(
                    walletId, "Ví Tiền Mặt", BigDecimal.ZERO, "VND", false, "ACTIVE", "Ghi chú hợp lệ", LocalDateTime.now());
            when(walletService.createWallet(any(CreateWalletRequestDTO.class))).thenReturn(expected);

            WalletResponseDTO actual = controller.handleCreateWallet("  Ví Tiền Mặt  ", "  Ghi chú hợp lệ  ");

            assertSame(expected, actual);
            ArgumentCaptor<CreateWalletRequestDTO> captor = ArgumentCaptor.forClass(CreateWalletRequestDTO.class);
            verify(walletService).createWallet(captor.capture());
            assertEquals("Ví Tiền Mặt", captor.getValue().getName());
            assertEquals("Ghi chú hợp lệ", captor.getValue().getNote());
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleUpdateWallet (UC-WAL-03)")
    class UpdateWalletTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi walletId là null")
        void handleUpdateWallet_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleUpdateWallet(null, "Tên Mới", "Ghi chú"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên mới để trống")
        void handleUpdateWallet_BlankName_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleUpdateWallet(UUID.randomUUID(), "  ", ""));
        }

        @Test
        @DisplayName("Đóng gói UpdateWalletRequestDTO và ủy quyền gọi walletService.updateWallet khi hợp lệ")
        void handleUpdateWallet_ValidInputs_DelegatesToService() {
            UUID walletId = UUID.randomUUID();
            WalletResponseDTO expected = new WalletResponseDTO(
                    walletId, "Ví Đã Cập Nhật", BigDecimal.TEN, "VND", false, "ACTIVE", "Ghi chú mới", LocalDateTime.now());
            when(walletService.updateWallet(any(UpdateWalletRequestDTO.class))).thenReturn(expected);

            WalletResponseDTO actual = controller.handleUpdateWallet(walletId, " Ví Đã Cập Nhật ", " Ghi chú mới ");

            assertSame(expected, actual);
            ArgumentCaptor<UpdateWalletRequestDTO> captor = ArgumentCaptor.forClass(UpdateWalletRequestDTO.class);
            verify(walletService).updateWallet(captor.capture());
            assertEquals(walletId, captor.getValue().getWalletId());
            assertEquals("Ví Đã Cập Nhật", captor.getValue().getName());
            assertEquals("Ghi chú mới", captor.getValue().getNote());
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleAdjustBalance (UC-WAL-04)")
    class AdjustBalanceTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi walletId là null")
        void handleAdjustBalance_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleAdjustBalance(null, "50000", "Kiểm kê", "2026-11-15"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số dư kiểm kê âm hoặc không phải số")
        void handleAdjustBalance_InvalidAmount_ThrowsIllegalArgumentException() {
            UUID walletId = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleAdjustBalance(walletId, "-500", "Lý do", "2026-11-15"));
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleAdjustBalance(walletId, "abc", "Lý do", "2026-11-15"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi lý do kiểm kê để trống")
        void handleAdjustBalance_BlankReason_ThrowsIllegalArgumentException() {
            UUID walletId = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleAdjustBalance(walletId, "100000", "   ", "2026-11-15"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ngày kiểm kê sai định dạng hoặc không có trên lịch")
        void handleAdjustBalance_InvalidDate_ThrowsIllegalArgumentException() {
            UUID walletId = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleAdjustBalance(walletId, "100000", "Kiểm kê", "31/02/2026"));
        }

        @Test
        @DisplayName("Chuyển đổi số dư, lý do, ngày tháng và gọi walletService.adjustBalance khi dữ liệu hợp lệ")
        void handleAdjustBalance_ValidInputs_DelegatesToService() {
            UUID walletId = UUID.randomUUID();
            BalanceAdjustmentResponseDTO expected = new BalanceAdjustmentResponseDTO(
                    UUID.randomUUID(), walletId, BigDecimal.ZERO, new BigDecimal("15000000"),
                    new BigDecimal("15000000"), "VND", "Kiểm kê cuối tháng", LocalDate.of(2026, 11, 15), LocalDateTime.now());
            when(walletService.adjustBalance(any(AdjustBalanceRequestDTO.class))).thenReturn(expected);

            BalanceAdjustmentResponseDTO actual = controller.handleAdjustBalance(
                    walletId, "15,000,000", "  Kiểm kê cuối tháng  ", "15/11/2026");

            assertSame(expected, actual);
            ArgumentCaptor<AdjustBalanceRequestDTO> captor = ArgumentCaptor.forClass(AdjustBalanceRequestDTO.class);
            verify(walletService).adjustBalance(captor.capture());
            assertEquals(walletId, captor.getValue().getWalletId());
            assertEquals(0, captor.getValue().getActualBalance().compareTo(new BigDecimal("15000000")));
            assertEquals("Kiểm kê cuối tháng", captor.getValue().getReason());
            assertEquals(LocalDate.of(2026, 11, 15), captor.getValue().getAdjustedAt());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Vòng đời Ví: Lưu trữ, Kích hoạt, Xóa")
    class LifecycleTests {

        @Test
        @DisplayName("handleArchiveWallet kiểm tra null và ủy quyền cho service")
        void handleArchiveWallet_Tests() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleArchiveWallet(null));

            UUID id = UUID.randomUUID();
            WalletResponseDTO expected = new WalletResponseDTO(
                    id, "Ví", BigDecimal.ZERO, "VND", false, "ARCHIVED", "", LocalDateTime.now());
            when(walletService.archiveWallet(id)).thenReturn(expected);

            WalletResponseDTO actual = controller.handleArchiveWallet(id);
            assertSame(expected, actual);
            verify(walletService).archiveWallet(id);
        }

        @Test
        @DisplayName("handleActivateWallet kiểm tra null và ủy quyền cho service")
        void handleActivateWallet_Tests() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleActivateWallet(null));

            UUID id = UUID.randomUUID();
            WalletResponseDTO expected = new WalletResponseDTO(
                    id, "Ví", BigDecimal.ZERO, "VND", false, "ACTIVE", "", LocalDateTime.now());
            when(walletService.activateWallet(id)).thenReturn(expected);

            WalletResponseDTO actual = controller.handleActivateWallet(id);
            assertSame(expected, actual);
            verify(walletService).activateWallet(id);
        }

        @Test
        @DisplayName("handleDeleteWallet kiểm tra null và ủy quyền cho service")
        void handleDeleteWallet_Tests() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleDeleteWallet(null));

            UUID id = UUID.randomUUID();
            controller.handleDeleteWallet(id);
            verify(walletService).deleteWallet(id);
        }
    }

    @Nested
    @DisplayName("Kiểm thử loadWalletsSummary (UC-WAL-02)")
    class LoadWalletsSummaryTests {

        @Test
        @DisplayName("Ủy quyền nạp dữ liệu tổng quan ví từ service với chuỗi bộ lọc trạng thái")
        void loadWalletsSummary_WithStatusFilter_DelegatesToService() {
            WalletsSummaryResponseDTO expected = new WalletsSummaryResponseDTO(
                    List.of(), BigDecimal.ZERO, "VND", Collections.emptyMap());
            when(walletService.getWalletsSummary("ACTIVE")).thenReturn(expected);

            WalletsSummaryResponseDTO actual = controller.loadWalletsSummary("ACTIVE");
            assertSame(expected, actual);
            verify(walletService).getWalletsSummary("ACTIVE");
        }

        @Test
        @DisplayName("Ủy quyền nạp dữ liệu tổng quan mặc định không tham số")
        void loadWalletsSummary_NoArgs_DelegatesToService() {
            WalletsSummaryResponseDTO expected = new WalletsSummaryResponseDTO(
                    List.of(), BigDecimal.ZERO, "VND", Collections.emptyMap());
            when(walletService.getWalletsSummary()).thenReturn(expected);

            WalletsSummaryResponseDTO actual = controller.loadWalletsSummary();
            assertSame(expected, actual);
            verify(walletService).getWalletsSummary();
        }
    }
}
