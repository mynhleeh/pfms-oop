package com.pfms.presentation.transaction;

import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.RecordTransactionRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.transaction.UpdateTransactionRequestDTO;
import com.pfms.service.TransactionService;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử TransactionGUIController - Bộ điều phối giao diện phân hệ Quản lý Giao dịch")
class TransactionGUIControllerTest {

    private TransactionService transactionService;
    private TransactionGUIController controller;

    @BeforeEach
    void setUp() {
        transactionService = Mockito.mock(TransactionService.class);
        controller = new TransactionGUIController(transactionService);
    }

    @Nested
    @DisplayName("Kiểm thử Khởi tạo (Constructor)")
    class ConstructorTests {

        @Test
        @DisplayName("Ném NullPointerException khi TransactionService là null")
        void constructor_NullService_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () -> new TransactionGUIController(null));
        }

        @Test
        @DisplayName("Khởi tạo thành công khi truyền TransactionService hợp lệ")
        void constructor_ValidService_InstantiatesSuccessfully() {
            assertNotNull(controller);
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleRecordTransaction (UC-TXN-01, FR-TXN-01..05)")
    class RecordTransactionTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã ví là null")
        void handleRecordTransaction_NullWalletId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRecordTransaction(null, UUID.randomUUID(), "50000", "2026-11-15", "Ăn trưa"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã danh mục là null")
        void handleRecordTransaction_NullCategoryId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRecordTransaction(UUID.randomUUID(), null, "50000", "2026-11-15", "Ăn trưa"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền không phải là số hợp lệ")
        void handleRecordTransaction_InvalidAmount_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRecordTransaction(UUID.randomUUID(), UUID.randomUUID(), "abc", "2026-11-15", "Ăn trưa"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền bằng không hoặc âm")
        void handleRecordTransaction_ZeroOrNegativeAmount_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRecordTransaction(UUID.randomUUID(), UUID.randomUUID(), "0", "2026-11-15", "Ăn trưa"));
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRecordTransaction(UUID.randomUUID(), UUID.randomUUID(), "-50000", "2026-11-15", "Ăn trưa"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ngày giao dịch sai định dạng")
        void handleRecordTransaction_InvalidDateFormat_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRecordTransaction(UUID.randomUUID(), UUID.randomUUID(), "50000", "15-11-2026", "Ăn trưa"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ghi chú vượt quá 255 ký tự")
        void handleRecordTransaction_NoteExceeds255_ThrowsIllegalArgumentException() {
            String longNote = "N".repeat(256);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRecordTransaction(UUID.randomUUID(), UUID.randomUUID(), "50000", "2026-11-15", longNote));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi phụ phí âm")
        void handleRecordTransaction_NegativeFee_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRecordTransaction(
                            UUID.randomUUID(), UUID.randomUUID(), "50000", "2026-11-15", "Ăn trưa", "-1000", UUID.randomUUID()));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi có phụ phí nhưng danh mục phụ phí là null")
        void handleRecordTransaction_FeeWithoutFeeCategory_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleRecordTransaction(
                            UUID.randomUUID(), UUID.randomUUID(), "50000", "2026-11-15", "Ăn trưa", "5000", null));
        }

        @Test
        @DisplayName("Ghi nhận thành công giao dịch có phụ phí và ủy quyền xuống service")
        void handleRecordTransaction_ValidWithFee_DelegatesToService() {
            UUID walletId = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();
            UUID feeCategoryId = UUID.randomUUID();
            UUID txnId = UUID.randomUUID();

            TransactionResponseDTO expected = new TransactionResponseDTO(
                    txnId, walletId, "Ví Tiền Mặt", categoryId, "Ăn trưa",
                    new BigDecimal("50000.00"), "EXPENSE", LocalDate.of(2026, 11, 15), "Ăn trưa tại quán",
                    true, new BigDecimal("5000.00"), feeCategoryId, "Phí dịch vụ",
                    new BigDecimal("1000000.00"), LocalDateTime.now());

            when(transactionService.recordIncomeExpense(any())).thenReturn(expected);

            TransactionResponseDTO result = controller.handleRecordTransaction(
                    walletId, categoryId, " 50,000 ", " 15/11/2026 ", "  Ăn trưa tại quán  ", " 5,000 ", feeCategoryId);

            assertNotNull(result);
            assertEquals(txnId, result.getTransactionId());

            ArgumentCaptor<RecordTransactionRequestDTO> captor = ArgumentCaptor.forClass(RecordTransactionRequestDTO.class);
            verify(transactionService).recordIncomeExpense(captor.capture());
            RecordTransactionRequestDTO request = captor.getValue();
            assertEquals(walletId, request.getWalletId());
            assertEquals(categoryId, request.getCategoryId());
            assertEquals(new BigDecimal("50000"), request.getAmount());
            assertEquals(LocalDate.of(2026, 11, 15), request.getTransactionDate());
            assertEquals("Ăn trưa tại quán", request.getNote());
            assertEquals(new BigDecimal("5000"), request.getFeeAmount());
            assertEquals(feeCategoryId, request.getFeeCategoryId());
        }

        @Test
        @DisplayName("Hỗ trợ phương thức nạp chồng không phụ phí và mặc định ngày hôm nay khi để trống")
        void handleRecordTransaction_OverloadNoFeeAndBlankDate_DefaultsDateAndZeroFee() {
            UUID walletId = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();
            UUID txnId = UUID.randomUUID();

            TransactionResponseDTO expected = new TransactionResponseDTO(
                    txnId, walletId, "Ví Tiền Mặt", categoryId, "Lương",
                    new BigDecimal("20000000.00"), "INCOME", LocalDate.now(), "Lương tháng",
                    new BigDecimal("30000000.00"), LocalDateTime.now());

            when(transactionService.recordIncomeExpense(any())).thenReturn(expected);

            TransactionResponseDTO result = controller.handleRecordTransaction(
                    walletId, categoryId, "20000000", "", "Lương tháng");

            assertNotNull(result);
            ArgumentCaptor<RecordTransactionRequestDTO> captor = ArgumentCaptor.forClass(RecordTransactionRequestDTO.class);
            verify(transactionService).recordIncomeExpense(captor.capture());
            RecordTransactionRequestDTO request = captor.getValue();
            assertEquals(LocalDate.now(), request.getTransactionDate());
            assertEquals(BigDecimal.ZERO, request.getFeeAmount());
            assertNull(request.getFeeCategoryId());
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleUpdateTransaction (UC-TXN-03, FR-TXN-03..06)")
    class UpdateTransactionTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã giao dịch là null")
        void handleUpdateTransaction_NullTransactionId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleUpdateTransaction(
                            null, UUID.randomUUID(), "60000", "2026-11-15", UUID.randomUUID(), "Ghi chú mới"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã ví mới là null")
        void handleUpdateTransaction_NullNewWalletId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleUpdateTransaction(
                            UUID.randomUUID(), null, "60000", "2026-11-15", UUID.randomUUID(), "Ghi chú mới"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã danh mục mới là null")
        void handleUpdateTransaction_NullNewCategoryId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleUpdateTransaction(
                            UUID.randomUUID(), UUID.randomUUID(), "60000", "2026-11-15", null, "Ghi chú mới"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền mới không hợp lệ")
        void handleUpdateTransaction_InvalidAmount_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleUpdateTransaction(
                            UUID.randomUUID(), UUID.randomUUID(), "-100", "2026-11-15", UUID.randomUUID(), "Ghi chú mới"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ngày mới sai định dạng")
        void handleUpdateTransaction_InvalidDateFormat_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleUpdateTransaction(
                            UUID.randomUUID(), UUID.randomUUID(), "60000", "invalid-date", UUID.randomUUID(), "Ghi chú mới"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi phụ phí mới lớn hơn 0 nhưng thiếu newFeeCategoryId")
        void handleUpdateTransaction_FeeWithoutCategory_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.handleUpdateTransaction(
                            UUID.randomUUID(), UUID.randomUUID(), "60000", "2026-11-15",
                            UUID.randomUUID(), "Ghi chú mới", "2000", null));
        }

        @Test
        @DisplayName("Cập nhật thành công với phụ phí mới và ủy quyền xuống service")
        void handleUpdateTransaction_ValidWithFee_DelegatesToService() {
            UUID txnId = UUID.randomUUID();
            UUID newWalletId = UUID.randomUUID();
            UUID newCategoryId = UUID.randomUUID();
            UUID newFeeCategoryId = UUID.randomUUID();

            TransactionResponseDTO expected = new TransactionResponseDTO(
                    txnId, newWalletId, "Ví Mới", newCategoryId, "Danh mục mới",
                    new BigDecimal("60000.00"), "EXPENSE", LocalDate.of(2026, 11, 16), "Ghi chú mới",
                    true, new BigDecimal("2000.00"), newFeeCategoryId, "Phí mới",
                    new BigDecimal("1000000.00"), LocalDateTime.now());

            when(transactionService.updateTransaction(any())).thenReturn(expected);

            TransactionResponseDTO result = controller.handleUpdateTransaction(
                    txnId, newWalletId, " 60,000 ", " 2026-11-16 ", newCategoryId, " Ghi chú mới ", " 2000 ", newFeeCategoryId);

            assertNotNull(result);
            ArgumentCaptor<UpdateTransactionRequestDTO> captor = ArgumentCaptor.forClass(UpdateTransactionRequestDTO.class);
            verify(transactionService).updateTransaction(captor.capture());
            UpdateTransactionRequestDTO request = captor.getValue();
            assertEquals(txnId, request.getTransactionId());
            assertEquals(newWalletId, request.getNewWalletId());
            assertEquals(new BigDecimal("60000"), request.getNewAmount());
            assertEquals(LocalDate.of(2026, 11, 16), request.getNewDate());
            assertEquals(newCategoryId, request.getNewCategoryId());
            assertEquals("Ghi chú mới", request.getNewNote());
            assertEquals(new BigDecimal("2000"), request.getNewFeeAmount());
            assertEquals(newFeeCategoryId, request.getNewFeeCategoryId());
        }

        @Test
        @DisplayName("Hỗ trợ nạp chồng cập nhật không truyền phụ phí mới (giữ nguyên phụ phí cũ - null)")
        void handleUpdateTransaction_OverloadWithoutFee_DelegatesWithNullFee() {
            UUID txnId = UUID.randomUUID();
            UUID newWalletId = UUID.randomUUID();
            UUID newCategoryId = UUID.randomUUID();

            TransactionResponseDTO expected = new TransactionResponseDTO(
                    txnId, newWalletId, "Ví Mới", newCategoryId, "Danh mục mới",
                    new BigDecimal("60000.00"), "EXPENSE", LocalDate.of(2026, 11, 16), "Ghi chú mới",
                    new BigDecimal("1000000.00"), LocalDateTime.now());

            when(transactionService.updateTransaction(any())).thenReturn(expected);

            TransactionResponseDTO result = controller.handleUpdateTransaction(
                    txnId, newWalletId, "60000", "2026-11-16", newCategoryId, "Ghi chú mới");

            assertNotNull(result);
            ArgumentCaptor<UpdateTransactionRequestDTO> captor = ArgumentCaptor.forClass(UpdateTransactionRequestDTO.class);
            verify(transactionService).updateTransaction(captor.capture());
            UpdateTransactionRequestDTO request = captor.getValue();
            assertNull(request.getNewFeeAmount());
            assertNull(request.getNewFeeCategoryId());
        }
    }

    @Nested
    @DisplayName("Kiểm thử handleDeleteTransaction (UC-TXN-04)")
    class DeleteTransactionTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi mã giao dịch là null")
        void handleDeleteTransaction_NullId_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.handleDeleteTransaction(null));
        }

        @Test
        @DisplayName("Ủy quyền xóa giao dịch cho transactionService.deleteTransaction")
        void handleDeleteTransaction_ValidId_DelegatesToService() {
            UUID txnId = UUID.randomUUID();
            controller.handleDeleteTransaction(txnId);
            verify(transactionService).deleteTransaction(txnId);
        }
    }

    @Nested
    @DisplayName("Kiểm thử loadTransactions (UC-TXN-02, FR-TXN-02)")
    class LoadTransactionsTests {

        @Test
        @DisplayName("Tải danh sách giao dịch không tham số mặc định")
        void loadTransactions_NoArgs_DelegatesToServiceWithDefaultFilter() {
            TransactionListResponseDTO expected = new TransactionListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 20, 0L, 0);
            when(transactionService.filterTransactions(any())).thenReturn(expected);

            TransactionListResponseDTO result = controller.loadTransactions();

            assertNotNull(result);
            verify(transactionService).filterTransactions(any(FilterTransactionsRequestDTO.class));
        }

        @Test
        @DisplayName("Tải danh sách giao dịch nhận FilterTransactionsRequestDTO trực tiếp")
        void loadTransactions_WithFilterDTO_DelegatesToService() {
            FilterTransactionsRequestDTO filter = new FilterTransactionsRequestDTO();
            TransactionListResponseDTO expected = new TransactionListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 20, 0L, 0);
            when(transactionService.filterTransactions(filter)).thenReturn(expected);

            TransactionListResponseDTO result = controller.loadTransactions(filter);

            assertSame(expected, result);
            verify(transactionService).filterTransactions(filter);
        }

        @Test
        @DisplayName("Tải danh sách giao dịch với tham số thô được kiểm thực ngày tháng và chuỗi")
        void loadTransactions_WithRawParams_ValidatesAndDelegatesToService() {
            UUID walletId = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();
            TransactionListResponseDTO expected = new TransactionListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 10, 0L, 0);
            when(transactionService.filterTransactions(any())).thenReturn(expected);

            TransactionListResponseDTO result = controller.loadTransactions(
                    "01/11/2026", "30/11/2026", Set.of(walletId), "EXPENSE", Set.of(categoryId), " ăn trưa ", 1, 10);

            assertNotNull(result);
            ArgumentCaptor<FilterTransactionsRequestDTO> captor = ArgumentCaptor.forClass(FilterTransactionsRequestDTO.class);
            verify(transactionService).filterTransactions(captor.capture());
            FilterTransactionsRequestDTO request = captor.getValue();
            assertEquals(LocalDate.of(2026, 11, 1), request.getStartDate());
            assertEquals(LocalDate.of(2026, 11, 30), request.getEndDate());
            assertEquals("ăn trưa", request.getKeyword());
            assertEquals("EXPENSE", request.getTransactionType());
            assertEquals(1, request.getPageNumber());
            assertEquals(10, request.getPageSize());
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi chuỗi ngày bắt đầu hoặc kết thúc sai định dạng")
        void loadTransactions_InvalidDateFormat_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.loadTransactions(
                            "sai-dinh-dang", "30/11/2026", null, null, null, "", 1, 20));
        }
    }
}
