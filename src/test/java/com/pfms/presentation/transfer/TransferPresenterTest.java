package com.pfms.presentation.transfer;

import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.transfer.viewmodel.TransferItemViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử TransferPresenter - Bộ điều phối phân hệ chuyển tiền nội bộ (MVP)")
class TransferPresenterTest {

    private TransferView view;
    private TransferGUIController controller;
    private TransferPresenter presenter;

    private TransferResponseDTO transfer1;
    private TransferListResponseDTO listResponse;

    @BeforeEach
    void setUp() {
        view = Mockito.mock(TransferView.class);
        controller = Mockito.mock(TransferGUIController.class);

        transfer1 = new TransferResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ví Tiền mặt",
                UUID.randomUUID(),
                "Ví MB Bank",
                new BigDecimal("1000000"),
                LocalDate.of(2026, 11, 15),
                "Nạp tiền ngân hàng",
                new BigDecimal("4000000"),
                new BigDecimal("11000000"),
                LocalDateTime.now()
        );

        listResponse = new TransferListResponseDTO(
                List.of(transfer1),
                new BigDecimal("1000000"),
                BigDecimal.ZERO,
                1,
                20,
                1L,
                1
        );

        presenter = new TransferPresenter(view, controller);
    }

    @Test
    @DisplayName("loadTransfers() nạp dữ liệu thành công, chuyển đổi sang ViewModel và render View")
    void testLoadTransfersSuccess() {
        when(controller.loadTransfers(any(FilterTransfersRequestDTO.class))).thenReturn(listResponse);

        presenter.loadTransfers(new FilterTransfersRequestDTO());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransferItemViewModel>> itemsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> summaryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pageInfoCaptor = ArgumentCaptor.forClass(String.class);

        verify(view).renderTransfers(itemsCaptor.capture(), summaryCaptor.capture(), pageInfoCaptor.capture());

        List<TransferItemViewModel> items = itemsCaptor.getValue();
        assertEquals(1, items.size());
        TransferItemViewModel vm = items.get(0);
        assertEquals(transfer1.getTransferId(), vm.id());
        assertEquals(1, vm.displayOrder());
        assertEquals("15/11/2026", vm.dateFormatted());
        assertEquals("Ví Tiền mặt", vm.fromWalletName());
        assertEquals("Ví MB Bank", vm.toWalletName());
        assertEquals("1,000,000 VNĐ", vm.formattedAmount());
        assertEquals(new BigDecimal("1000000"), vm.rawAmount());
        assertEquals("Nạp tiền ngân hàng", vm.note());
        assertEquals(new BigDecimal("4000000"), vm.fromBalanceAfter());
        assertEquals(new BigDecimal("11000000"), vm.toBalanceAfter());

        assertTrue(summaryCaptor.getValue().contains("Tổng tiền chuyển: 1,000,000 VNĐ"));
        assertTrue(summaryCaptor.getValue().contains("Tổng phí: 0 VNĐ"));
        assertTrue(pageInfoCaptor.getValue().contains("Trang 1 / 1 (Tổng 1 giao dịch)"));
    }

    @Test
    @DisplayName("loadTransfers() xử lý trường hợp response là null")
    void testLoadTransfersNullResponse() {
        when(controller.loadTransfers(any())).thenReturn(null);

        presenter.loadTransfers(null);

        verify(view).renderTransfers(
                argThat(List::isEmpty),
                eq("Tổng tiền chuyển: 0 VNĐ | Tổng phí: 0 VNĐ"),
                eq("Trang 1 / 1 (Tổng 0 giao dịch)")
        );
        assertEquals(1, presenter.getCurrentPage());
        assertEquals(1, presenter.getTotalPages());
        assertEquals(0, presenter.getTotalRecords());
    }

    @Test
    @DisplayName("loadTransfers() bắt ngoại lệ và gọi view.showError")
    void testLoadTransfersExceptionShowsError() {
        when(controller.loadTransfers(any())).thenThrow(new RuntimeException("Lỗi kết nối cơ sở dữ liệu"));

        presenter.loadTransfers(null);

        verify(view).showError(argThat(msg -> msg.contains("Lỗi kết nối cơ sở dữ liệu")));
    }

    @Test
    @DisplayName("deleteTransfer() thành công khi người dùng xác nhận YES")
    void testDeleteTransferSuccessConfirmed() {
        when(view.confirmAction(anyString())).thenReturn(true);
        when(controller.loadTransfers(any())).thenReturn(listResponse);

        presenter.deleteTransfer(transfer1.getTransferId(), "Ví Tiền mặt", "Ví MB Bank");

        verify(controller).handleDeleteTransfer(transfer1.getTransferId());
        verify(view).showMessage(argThat(msg -> msg.contains("thành công")));
        verify(controller).loadTransfers(any());
    }

    @Test
    @DisplayName("deleteTransfer() không thực thi khi người dùng chọn NO")
    void testDeleteTransferCancelled() {
        when(view.confirmAction(anyString())).thenReturn(false);

        presenter.deleteTransfer(transfer1.getTransferId(), "Ví Tiền mặt", "Ví MB Bank");

        verify(controller, never()).handleDeleteTransfer(any());
        verify(view, never()).showMessage(anyString());
    }

    @Test
    @DisplayName("deleteTransfer() cảnh báo khi mã chuyển tiền bị null")
    void testDeleteTransferNullId() {
        presenter.deleteTransfer((UUID) null, "Ví Tiền mặt", "Ví MB Bank");

        verify(view).showWarning(argThat(msg -> msg.contains("Vui lòng chọn")));
        verify(controller, never()).handleDeleteTransfer(any());
    }

    @Test
    @DisplayName("deleteTransfer() hỗ trợ tham số TransferItemViewModel")
    void testDeleteTransferViaItemViewModel() {
        when(view.confirmAction(anyString())).thenReturn(true);
        when(controller.loadTransfers(any())).thenReturn(listResponse);

        TransferItemViewModel vm = new TransferItemViewModel(
                transfer1.getTransferId(), 1, "15/11/2026", "Ví Tiền mặt", "Ví MB Bank",
                "1,000,000 VNĐ", new BigDecimal("1000000"), "Ghi chú",
                new BigDecimal("4000000"), new BigDecimal("11000000")
        );

        presenter.deleteTransfer(vm);

        verify(controller).handleDeleteTransfer(transfer1.getTransferId());
        verify(view).showMessage(argThat(msg -> msg.contains("thành công")));
    }

    @Test
    @DisplayName("deleteTransfer() cảnh báo khi ViewModel truyền vào là null")
    void testDeleteTransferNullViewModel() {
        presenter.deleteTransfer((TransferItemViewModel) null);

        verify(view).showWarning(argThat(msg -> msg.contains("Vui lòng chọn")));
        verify(controller, never()).handleDeleteTransfer(any());
    }

    @Test
    @DisplayName("deleteTransfer() hiển thị lỗi khi xóa thất bại do ngoại lệ")
    void testDeleteTransferExceptionShowsError() {
        when(view.confirmAction(anyString())).thenReturn(true);
        doThrow(new RuntimeException("Không tìm thấy giao dịch")).when(controller).handleDeleteTransfer(any());

        presenter.deleteTransfer(transfer1.getTransferId(), "Ví Tiền mặt", "Ví MB Bank");

        verify(view).showError(argThat(msg -> msg.contains("Không tìm thấy giao dịch")));
    }

    @Test
    @DisplayName("Điều hướng phân trang nextPage() và prevPage() hoạt động chính xác")
    void testPaginationNextAndPrev() {
        TransferListResponseDTO page1Response = new TransferListResponseDTO(
                List.of(transfer1), new BigDecimal("1000000"), BigDecimal.ZERO, 1, 20, 50L, 3);
        TransferListResponseDTO page2Response = new TransferListResponseDTO(
                List.of(transfer1), new BigDecimal("1000000"), BigDecimal.ZERO, 2, 20, 50L, 3);
        TransferListResponseDTO page3Response = new TransferListResponseDTO(
                List.of(transfer1), new BigDecimal("1000000"), BigDecimal.ZERO, 3, 20, 50L, 3);

        when(controller.loadTransfers(any())).thenReturn(page1Response);
        presenter.loadTransfers(new FilterTransfersRequestDTO());

        assertEquals(1, presenter.getCurrentPage());
        assertEquals(3, presenter.getTotalPages());

        when(controller.loadTransfers(any())).thenReturn(page2Response);
        presenter.nextPage();
        assertEquals(2, presenter.getCurrentPage());

        when(controller.loadTransfers(any())).thenReturn(page1Response);
        presenter.prevPage();
        assertEquals(1, presenter.getCurrentPage());
    }

    @Test
    @DisplayName("Điều hướng phân trang không vượt quá biên 1 và totalPages")
    void testPaginationBoundaries() {
        TransferListResponseDTO singlePageResponse = new TransferListResponseDTO(
                List.of(transfer1), new BigDecimal("1000000"), BigDecimal.ZERO, 1, 20, 1L, 1);
        when(controller.loadTransfers(any())).thenReturn(singlePageResponse);
        presenter.loadTransfers(new FilterTransfersRequestDTO());

        clearInvocations(controller);

        presenter.prevPage();
        verify(controller, never()).loadTransfers(any());

        presenter.nextPage();
        verify(controller, never()).loadTransfers(any());
    }

    @Test
    @DisplayName("onNewTransferClicked() chuyển tiếp mở dialog tạo lệnh chuyển tiền")
    void testOnNewTransferClicked() {
        presenter.onNewTransferClicked();
        verify(view).openTransferFundsDialog();
    }

    @Test
    @DisplayName("Khởi tạo Presenter ném NullPointerException khi tham số null")
    void testConstructorValidation() {
        assertThrows(NullPointerException.class, () -> new TransferPresenter(null, controller));
        assertThrows(NullPointerException.class, () -> new TransferPresenter(view, null));
    }
}
