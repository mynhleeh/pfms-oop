package com.pfms.presentation.controller;

import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferFundsRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.service.TransferService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử TransferGUIController - Bộ điều phối giao diện phân hệ Chuyển tiền")
class TransferGUIControllerTest {

    private TransferService transferService;
    private TransferGUIController controller;

    @BeforeEach
    void setUp() {
        transferService = Mockito.mock(TransferService.class);
        controller = new TransferGUIController(transferService);
    }

    @Nested
    @DisplayName("Kiểm thử Khởi tạo (Constructor)")
    class ConstructorTests {

        @Test
        @DisplayName("Ném NullPointerException khi TransferService là null")
        void constructor_NullService_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () -> new TransferGUIController(null));
        }

        @Test
        @DisplayName("Khởi tạo thành công khi truyền TransferService hợp lệ")
        void constructor_ValidService_InstantiatesSuccessfully() {
            assertNotNull(controller);
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleTransferFunds (UC-TRF-01, FR-TRF-01..05)")
    class TransferFundsTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã ví nguồn là null")
        void handleTransferFunds_NullSourceWallet_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            null, UUID.randomUUID(), "100000", "2026-11-15", "Chuyển tiền"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã ví đích là null")
        void handleTransferFunds_NullDestinationWallet_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            UUID.randomUUID(), null, "100000", "2026-11-15", "Chuyển tiền"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ví nguồn trùng với ví đích")
        void handleTransferFunds_SameSourceAndDestination_ThrowsIllegalArgumentException() {
            UUID sameWalletId = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            sameWalletId, sameWalletId, "100000", "2026-11-15", "Chuyển tiền"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền chuyển không phải là số hợp lệ")
        void handleTransferFunds_InvalidAmount_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            UUID.randomUUID(), UUID.randomUUID(), "abc", "2026-11-15", "Chuyển tiền"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền chuyển bằng không hoặc âm")
        void handleTransferFunds_ZeroOrNegativeAmount_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            UUID.randomUUID(), UUID.randomUUID(), "0", "2026-11-15", "Chuyển tiền"));
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            UUID.randomUUID(), UUID.randomUUID(), "-50000", "2026-11-15", "Chuyển tiền"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ngày chuyển tiền sai định dạng")
        void handleTransferFunds_InvalidDateFormat_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            UUID.randomUUID(), UUID.randomUUID(), "100000", "15.11.2026", "Chuyển tiền"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ghi chú vượt quá 255 ký tự")
        void handleTransferFunds_NoteExceeds255_ThrowsIllegalArgumentException() {
            String longNote = "T".repeat(256);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            UUID.randomUUID(), UUID.randomUUID(), "100000", "2026-11-15", longNote));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi phụ phí chuyển tiền âm")
        void handleTransferFunds_NegativeFee_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            UUID.randomUUID(), UUID.randomUUID(), "100000", "2026-11-15", "Chuyển tiền",
                            "-5000", UUID.randomUUID()));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi có phụ phí nhưng danh mục phụ phí là null")
        void handleTransferFunds_FeeWithoutCategory_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleTransferFunds(
                            UUID.randomUUID(), UUID.randomUUID(), "100000", "2026-11-15", "Chuyển tiền",
                            "5000", null));
        }

        @Test
        @DisplayName("Chuyển tiền thành công có phụ phí và ủy quyền xuống transferService")
        void handleTransferFunds_ValidWithFee_DelegatesToService() {
            UUID sourceId = UUID.randomUUID();
            UUID destId = UUID.randomUUID();
            UUID feeCategoryId = UUID.randomUUID();
            UUID transferId = UUID.randomUUID();

            TransferResponseDTO expected = new TransferResponseDTO(
                    transferId, sourceId, "Ví Tiền Mặt", destId, "Ví Ngân Hàng",
                    new BigDecimal("500000.00"), LocalDate.of(2026, 11, 15), "Nạp tiền ngân hàng",
                    new BigDecimal("3300.00"), feeCategoryId, "Phí chuyển khoản",
                    new BigDecimal("1500000.00"), new BigDecimal("500000.00"), LocalDateTime.now());

            when(transferService.transferFunds(any())).thenReturn(expected);

            TransferResponseDTO result = controller.handleTransferFunds(
                    sourceId, destId, " 500,000 ", " 15/11/2026 ", " Nạp tiền ngân hàng ", " 3,300 ", feeCategoryId);

            assertNotNull(result);
            assertEquals(transferId, result.getTransferId());

            ArgumentCaptor<TransferFundsRequestDTO> captor = ArgumentCaptor.forClass(TransferFundsRequestDTO.class);
            verify(transferService).transferFunds(captor.capture());
            TransferFundsRequestDTO request = captor.getValue();
            assertEquals(sourceId, request.getSourceWalletId());
            assertEquals(destId, request.getDestinationWalletId());
            assertEquals(new BigDecimal("500000"), request.getAmount());
            assertEquals(LocalDate.of(2026, 11, 15), request.getTransferDate());
            assertEquals("Nạp tiền ngân hàng", request.getNote());
            assertEquals(new BigDecimal("3300"), request.getFeeAmount());
            assertEquals(feeCategoryId, request.getFeeCategoryId());
        }

        @Test
        @DisplayName("Hỗ trợ phương thức nạp chồng không phụ phí và mặc định ngày hôm nay khi để trống")
        void handleTransferFunds_OverloadNoFeeAndBlankDate_DefaultsDateAndZeroFee() {
            UUID sourceId = UUID.randomUUID();
            UUID destId = UUID.randomUUID();
            UUID transferId = UUID.randomUUID();

            TransferResponseDTO expected = new TransferResponseDTO(
                    transferId, sourceId, "Ví Tiền Mặt", destId, "Ví Tiết Kiệm",
                    new BigDecimal("200000.00"), LocalDate.now(), "Tiết kiệm",
                    new BigDecimal("800000.00"), new BigDecimal("200000.00"), LocalDateTime.now());

            when(transferService.transferFunds(any())).thenReturn(expected);

            TransferResponseDTO result = controller.handleTransferFunds(
                    sourceId, destId, "200000", "", "Tiết kiệm");

            assertNotNull(result);
            ArgumentCaptor<TransferFundsRequestDTO> captor = ArgumentCaptor.forClass(TransferFundsRequestDTO.class);
            verify(transferService).transferFunds(captor.capture());
            TransferFundsRequestDTO request = captor.getValue();
            assertEquals(LocalDate.now(), request.getTransferDate());
            assertEquals(BigDecimal.ZERO, request.getFeeAmount());
            assertNull(request.getFeeCategoryId());
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleDeleteTransfer (UC-TRF-03)")
    class DeleteTransferTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã chuyển tiền là null")
        void handleDeleteTransfer_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleDeleteTransfer(null));
        }

        @Test
        @DisplayName("Ủy quyền xóa chuyển tiền cho transferService.deleteTransfer")
        void handleDeleteTransfer_ValidId_DelegatesToService() {
            UUID transferId = UUID.randomUUID();
            controller.handleDeleteTransfer(transferId);
            verify(transferService).deleteTransfer(transferId);
        }
    }

    @Nested
    @DisplayName("Kiểm thử loadTransfers (UC-TRF-02, FR-TRF-02)")
    class LoadTransfersTests {

        @Test
        @DisplayName("Tải danh sách chuyển tiền không tham số mặc định")
        void loadTransfers_NoArgs_DelegatesToServiceWithDefaultFilter() {
            TransferListResponseDTO expected = new TransferListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, 1, 20, 0L, 0);
            when(transferService.filterTransfers(any())).thenReturn(expected);

            TransferListResponseDTO result = controller.loadTransfers();

            assertNotNull(result);
            verify(transferService).filterTransfers(any(FilterTransfersRequestDTO.class));
        }

        @Test
        @DisplayName("Tải danh sách chuyển tiền nhận FilterTransfersRequestDTO trực tiếp")
        void loadTransfers_WithFilterDTO_DelegatesToService() {
            FilterTransfersRequestDTO filter = new FilterTransfersRequestDTO();
            TransferListResponseDTO expected = new TransferListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, 1, 20, 0L, 0);
            when(transferService.filterTransfers(filter)).thenReturn(expected);

            TransferListResponseDTO result = controller.loadTransfers(filter);

            assertSame(expected, result);
            verify(transferService).filterTransfers(filter);
        }

        @Test
        @DisplayName("Tải danh sách chuyển tiền với tham số thô được kiểm thực ngày tháng")
        void loadTransfers_WithRawParams_ValidatesAndDelegatesToService() {
            UUID sourceId = UUID.randomUUID();
            UUID destId = UUID.randomUUID();
            TransferListResponseDTO expected = new TransferListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, 1, 10, 0L, 0);
            when(transferService.filterTransfers(any())).thenReturn(expected);

            TransferListResponseDTO result = controller.loadTransfers(
                    "01/11/2026", "30/11/2026", sourceId, destId, 1, 10);

            assertNotNull(result);
            ArgumentCaptor<FilterTransfersRequestDTO> captor = ArgumentCaptor.forClass(FilterTransfersRequestDTO.class);
            verify(transferService).filterTransfers(captor.capture());
            FilterTransfersRequestDTO request = captor.getValue();
            assertEquals(LocalDate.of(2026, 11, 1), request.getStartDate());
            assertEquals(LocalDate.of(2026, 11, 30), request.getEndDate());
            assertEquals(sourceId, request.getSourceWalletId());
            assertEquals(destId, request.getDestinationWalletId());
            assertEquals(1, request.getPageNumber());
            assertEquals(10, request.getPageSize());
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi chuỗi ngày bắt đầu hoặc kết thúc sai định dạng")
        void loadTransfers_InvalidDateFormat_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.loadTransfers(
                            "sai-dinh-dang", "30/11/2026", null, null, 1, 20));
        }
    }
}
