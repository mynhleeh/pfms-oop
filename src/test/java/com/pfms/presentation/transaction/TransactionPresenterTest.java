package com.pfms.presentation.transaction;

import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.transaction.viewmodel.TransactionItemViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử TransactionPresenter - Bộ điều phối phân hệ giao dịch thu chi (MVP)")
class TransactionPresenterTest {

    private TransactionView view;
    private TransactionGUIController controller;
    private TransactionPresenter presenter;

    private TransactionResponseDTO txnIncome;
    private TransactionResponseDTO txnExpense;
    private TransactionListResponseDTO listResponse;

    @BeforeEach
    void setUp() {
        view = Mockito.mock(TransactionView.class);
        controller = Mockito.mock(TransactionGUIController.class);

        txnIncome = new TransactionResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ví MB Bank",
                UUID.randomUUID(),
                "Tiền lương",
                new BigDecimal("15000000"),
                "INCOME",
                LocalDate.of(2026, 11, 15),
                "Lương tháng 11",
                new BigDecimal("25000000"),
                LocalDateTime.now()
        );

        txnExpense = new TransactionResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ví Tiền mặt",
                UUID.randomUUID(),
                "Ăn uống",
                new BigDecimal("200000"),
                "EXPENSE",
                LocalDate.of(2026, 11, 16),
                "Ăn trưa",
                new BigDecimal("4800000"),
                LocalDateTime.now()
        );

        listResponse = new TransactionListResponseDTO(
                List.of(txnIncome, txnExpense),
                new BigDecimal("15000000"),
                new BigDecimal("200000"),
                new BigDecimal("14800000"),
                1,
                20,
                2L,
                1
        );

        presenter = new TransactionPresenter(view, controller);
    }

    @Test
    @DisplayName("loadTransactions() nạp dữ liệu thành công, chuyển đổi sang ViewModel và render View")
    void testLoadTransactionsSuccess() {
        when(controller.loadTransactions(any(FilterTransactionsRequestDTO.class))).thenReturn(listResponse);

        presenter.loadTransactions(new FilterTransactionsRequestDTO());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransactionItemViewModel>> itemsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> summaryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pageInfoCaptor = ArgumentCaptor.forClass(String.class);

        verify(view).renderTransactions(itemsCaptor.capture(), summaryCaptor.capture(), pageInfoCaptor.capture());

        List<TransactionItemViewModel> items = itemsCaptor.getValue();
        assertEquals(2, items.size());

        TransactionItemViewModel vm1 = items.get(0);
        assertEquals(txnIncome.getTransactionId(), vm1.id());
        assertEquals(1, vm1.displayOrder());
        assertEquals("15/11/2026", vm1.dateFormatted());
        assertEquals("Tiền lương", vm1.categoryName());
        assertEquals("Ví MB Bank", vm1.walletName());
        assertEquals("15,000,000 VNĐ", vm1.formattedAmount());
        assertTrue(vm1.isIncome());
        assertEquals("Lương tháng 11", vm1.note());
        assertEquals("25,000,000 VNĐ", vm1.balanceAfterFormatted());

        TransactionItemViewModel vm2 = items.get(1);
        assertEquals(txnExpense.getTransactionId(), vm2.id());
        assertEquals(2, vm2.displayOrder());
        assertEquals("16/11/2026", vm2.dateFormatted());
        assertEquals("Ăn uống", vm2.categoryName());
        assertEquals("Ví Tiền mặt", vm2.walletName());
        assertEquals("200,000 VNĐ", vm2.formattedAmount());
        assertFalse(vm2.isIncome());
        assertEquals("Ăn trưa", vm2.note());
        assertEquals("4,800,000 VNĐ", vm2.balanceAfterFormatted());

        assertTrue(summaryCaptor.getValue().contains("Tổng thu: 15,000,000 VNĐ"));
        assertTrue(summaryCaptor.getValue().contains("Tổng chi: 200,000 VNĐ"));
        assertTrue(summaryCaptor.getValue().contains("Dòng tiền thuần: 14,800,000 VNĐ"));
        assertTrue(pageInfoCaptor.getValue().contains("Trang 1 / 1 (Tổng 2 giao dịch)"));
    }

    @Test
    @DisplayName("loadTransactions() xử lý trường hợp response là null")
    void testLoadTransactionsNullResponse() {
        when(controller.loadTransactions(any())).thenReturn(null);

        presenter.loadTransactions(null);

        verify(view).renderTransactions(
                argThat(List::isEmpty),
                eq("Tổng thu: 0 VNĐ | Tổng chi: 0 VNĐ | Dòng tiền thuần: 0 VNĐ"),
                eq("Trang 1 / 1 (Tổng 0 giao dịch)")
        );
        assertEquals(1, presenter.getCurrentPage());
        assertEquals(1, presenter.getTotalPages());
        assertEquals(0, presenter.getTotalRecords());
    }

    @Test
    @DisplayName("loadTransactions() bắt ngoại lệ và gọi view.showError")
    void testLoadTransactionsExceptionShowsError() {
        when(controller.loadTransactions(any())).thenThrow(new RuntimeException("Lỗi kết nối cơ sở dữ liệu"));

        presenter.loadTransactions(null);

        verify(view).showError(argThat(msg -> msg.contains("Lỗi kết nối cơ sở dữ liệu")));
    }

    @Test
    @DisplayName("deleteTransaction() thành công khi người dùng xác nhận YES")
    void testDeleteTransactionSuccessConfirmed() {
        when(view.confirmAction(anyString())).thenReturn(true);
        when(controller.loadTransactions(any())).thenReturn(listResponse);

        presenter.deleteTransaction(txnIncome.getTransactionId());

        verify(controller).handleDeleteTransaction(txnIncome.getTransactionId());
        verify(view).showMessage(argThat(msg -> msg.contains("thành công")));
        verify(controller).loadTransactions(any());
    }

    @Test
    @DisplayName("deleteTransaction() không thực thi khi người dùng chọn NO")
    void testDeleteTransactionCancelled() {
        when(view.confirmAction(anyString())).thenReturn(false);

        presenter.deleteTransaction(txnIncome.getTransactionId());

        verify(controller, never()).handleDeleteTransaction(any());
        verify(view, never()).showMessage(anyString());
    }

    @Test
    @DisplayName("deleteTransaction() cảnh báo khi mã giao dịch bị null")
    void testDeleteTransactionNullId() {
        presenter.deleteTransaction((UUID) null);

        verify(view).showWarning(argThat(msg -> msg.contains("Vui lòng chọn")));
        verify(controller, never()).handleDeleteTransaction(any());
    }

    @Test
    @DisplayName("deleteTransaction() hỗ trợ tham số TransactionItemViewModel")
    void testDeleteTransactionViaItemViewModel() {
        when(view.confirmAction(anyString())).thenReturn(true);
        when(controller.loadTransactions(any())).thenReturn(listResponse);

        TransactionItemViewModel vm = new TransactionItemViewModel(
                txnIncome.getTransactionId(), 1, "15/11/2026", "Tiền lương", "Ví MB Bank",
                "15,000,000 VNĐ", true, "Lương tháng 11", "25,000,000 VNĐ"
        );

        presenter.deleteTransaction(vm);

        verify(controller).handleDeleteTransaction(txnIncome.getTransactionId());
        verify(view).showMessage(argThat(msg -> msg.contains("thành công")));
    }

    @Test
    @DisplayName("deleteTransaction() cảnh báo khi ViewModel truyền vào là null")
    void testDeleteTransactionNullViewModel() {
        presenter.deleteTransaction((TransactionItemViewModel) null);

        verify(view).showWarning(argThat(msg -> msg.contains("Vui lòng chọn")));
        verify(controller, never()).handleDeleteTransaction(any());
    }

    @Test
    @DisplayName("deleteTransaction() hiển thị lỗi khi xóa thất bại do ngoại lệ")
    void testDeleteTransactionExceptionShowsError() {
        when(view.confirmAction(anyString())).thenReturn(true);
        doThrow(new RuntimeException("Không tìm thấy giao dịch")).when(controller).handleDeleteTransaction(any());

        presenter.deleteTransaction(txnIncome.getTransactionId());

        verify(view).showError(argThat(msg -> msg.contains("Không tìm thấy giao dịch")));
    }

    @Test
    @DisplayName("Điều hướng phân trang nextPage() và prevPage() hoạt động chính xác")
    void testPaginationNextAndPrev() {
        TransactionListResponseDTO page1Response = new TransactionListResponseDTO(
                List.of(txnIncome), new BigDecimal("15000000"), BigDecimal.ZERO, new BigDecimal("15000000"), 1, 20, 50L, 3);
        TransactionListResponseDTO page2Response = new TransactionListResponseDTO(
                List.of(txnExpense), BigDecimal.ZERO, new BigDecimal("200000"), new BigDecimal("-200000"), 2, 20, 50L, 3);

        when(controller.loadTransactions(any())).thenReturn(page1Response);
        presenter.loadTransactions(new FilterTransactionsRequestDTO());

        assertEquals(1, presenter.getCurrentPage());
        assertEquals(3, presenter.getTotalPages());

        when(controller.loadTransactions(any())).thenReturn(page2Response);
        presenter.nextPage();
        assertEquals(2, presenter.getCurrentPage());

        when(controller.loadTransactions(any())).thenReturn(page1Response);
        presenter.prevPage();
        assertEquals(1, presenter.getCurrentPage());
    }

    @Test
    @DisplayName("Điều hướng phân trang không vượt quá biên 1 và totalPages")
    void testPaginationBoundaries() {
        TransactionListResponseDTO singlePageResponse = new TransactionListResponseDTO(
                List.of(txnIncome), new BigDecimal("15000000"), BigDecimal.ZERO, new BigDecimal("15000000"), 1, 20, 1L, 1);
        when(controller.loadTransactions(any())).thenReturn(singlePageResponse);
        presenter.loadTransactions(new FilterTransactionsRequestDTO());

        clearInvocations(controller);

        presenter.prevPage();
        verify(controller, never()).loadTransactions(any());

        presenter.nextPage();
        verify(controller, never()).loadTransactions(any());
    }

    @Test
    @DisplayName("onAddTransactionClicked() chuyển tiếp mở dialog ghi nhận giao dịch")
    void testOnAddTransactionClicked() {
        presenter.onAddTransactionClicked();
        verify(view).openRecordDialog();
    }

    @Test
    @DisplayName("onEditTransactionClicked() chuyển tiếp mở dialog chỉnh sửa khi có ViewModel hợp lệ")
    void testOnEditTransactionClicked() {
        TransactionItemViewModel vm = new TransactionItemViewModel(
                txnIncome.getTransactionId(), 1, "15/11/2026", "Tiền lương", "Ví MB Bank",
                "15,000,000 VNĐ", true, "Lương tháng 11", "25,000,000 VNĐ"
        );
        presenter.onEditTransactionClicked(vm);
        verify(view).openUpdateDialog(vm);
    }

    @Test
    @DisplayName("onEditTransactionClicked() cảnh báo khi item được chọn là null")
    void testOnEditTransactionClickedNullItem() {
        presenter.onEditTransactionClicked(null);
        verify(view).showWarning(argThat(msg -> msg.contains("Vui lòng chọn")));
        verify(view, never()).openUpdateDialog(any());
    }

    @Test
    @DisplayName("Khởi tạo Presenter ném NullPointerException khi tham số null")
    void testConstructorValidation() {
        assertThrows(NullPointerException.class, () -> new TransactionPresenter(null, controller));
        assertThrows(NullPointerException.class, () -> new TransactionPresenter(view, null));
    }
}
