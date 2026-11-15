package com.pfms.presentation.view.transaction;

import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.controller.TransactionGUIController;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử TransactionPanel - Bảng điều khiển phân hệ giao dịch")
class TransactionPanelTest {

    private TransactionGUIController transactionController;
    private WalletGUIController walletController;
    private CategoryGUIController categoryController;

    private TransactionResponseDTO txn1;
    private TransactionListResponseDTO listResponse;
    private TransactionPanel panel;

    @BeforeEach
    void setUp() {
        transactionController = Mockito.mock(TransactionGUIController.class);
        walletController = Mockito.mock(WalletGUIController.class);
        categoryController = Mockito.mock(CategoryGUIController.class);

        WalletResponseDTO wallet = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Tiền mặt", new BigDecimal("5000000"), "VND", true, "ACTIVE", "Tiêu vặt", LocalDateTime.now());
        when(walletController.loadWalletsSummary()).thenReturn(
                new WalletsSummaryResponseDTO(List.of(wallet), new BigDecimal("5000000"), "VND", Collections.emptyMap()));
        when(categoryController.loadCategoryHierarchy()).thenReturn(Collections.emptyList());

        txn1 = new TransactionResponseDTO(
                UUID.randomUUID(), wallet.getWalletId(), "Ví Tiền mặt",
                UUID.randomUUID(), "Ăn uống", new BigDecimal("100000"),
                "EXPENSE", LocalDate.now(), "Ăn sáng", new BigDecimal("4900000"), LocalDateTime.now());

        listResponse = new TransactionListResponseDTO(
                List.of(txn1), BigDecimal.ZERO, new BigDecimal("100000"), new BigDecimal("-100000"),
                1, 20, 1L, 1);

        when(transactionController.loadTransactions(any(FilterTransactionsRequestDTO.class))).thenReturn(listResponse);

        panel = new TransactionPanel(transactionController, walletController, categoryController);
    }

    @Test
    @DisplayName("Khởi tạo nạp dữ liệu danh sách giao dịch ban đầu và tóm tắt dòng tiền thành công")
    void testInitialDataLoaded() {
        assertEquals(1, panel.getTableModel().getRowCount());
        assertEquals("Ăn uống", panel.getTableModel().getValueAt(0, 3));
        assertTrue(panel.getSummaryText().contains("Tổng chi: 100,000 VNĐ"));
        assertTrue(panel.getPageInfoText().contains("Trang 1 / 1"));
        assertNotNull(panel.getTable());
    }

    @Test
    @DisplayName("Kích hoạt thay đổi bộ lọc loại giao dịch gọi lại controller")
    void testFilterTypeChanged() {
        panel.setSelectedTypeFilter("EXPENSE");
        verify(transactionController, atLeastOnce()).loadTransactions(any(FilterTransactionsRequestDTO.class));
    }

    @Test
    @DisplayName("Kích hoạt callback onTransactionChanged khi có thao tác làm mới")
    void testTransactionChangedCallback() {
        AtomicBoolean triggered = new AtomicBoolean(false);
        panel.setOnTransactionChangedListener(() -> triggered.set(true));

        panel.reloadTransactions();
        assertTrue(triggered.get());
    }

    @Test
    @DisplayName("Ném NullPointerException khi bất kỳ controller nào bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new TransactionPanel(null, walletController, categoryController));
        assertThrows(NullPointerException.class, () -> new TransactionPanel(transactionController, null, categoryController));
        assertThrows(NullPointerException.class, () -> new TransactionPanel(transactionController, walletController, null));
    }
}
