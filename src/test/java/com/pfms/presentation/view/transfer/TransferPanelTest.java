package com.pfms.presentation.view.transfer;

import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.controller.TransferGUIController;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
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

@DisplayName("Kiểm thử TransferPanel - Bảng điều khiển phân hệ chuyển tiền nội bộ")
class TransferPanelTest {

    private TransferGUIController transferController;
    private WalletGUIController walletController;
    private CategoryGUIController categoryController;

    private TransferResponseDTO transfer1;
    private TransferListResponseDTO listResponse;
    private TransferPanel panel;

    @BeforeEach
    void setUp() {
        transferController = Mockito.mock(TransferGUIController.class);
        walletController = Mockito.mock(WalletGUIController.class);
        categoryController = Mockito.mock(CategoryGUIController.class);

        WalletResponseDTO w1 = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Tiền mặt", new BigDecimal("5000000"), "VND", true, "ACTIVE", "Tiền mặt", LocalDateTime.now());
        WalletResponseDTO w2 = new WalletResponseDTO(
                UUID.randomUUID(), "Ví MB Bank", new BigDecimal("10000000"), "VND", false, "ACTIVE", "Tài khoản", LocalDateTime.now());

        when(walletController.loadWalletsSummary()).thenReturn(
                new WalletsSummaryResponseDTO(List.of(w1, w2), new BigDecimal("15000000"), "VND", Collections.emptyMap()));
        when(categoryController.loadCategoryHierarchy()).thenReturn(Collections.emptyList());

        transfer1 = new TransferResponseDTO(
                UUID.randomUUID(), w1.getWalletId(), "Ví Tiền mặt",
                w2.getWalletId(), "Ví MB Bank", new BigDecimal("1000000"),
                LocalDate.now(), "Nạp tiền", new BigDecimal("4000000"),
                new BigDecimal("11000000"), LocalDateTime.now());

        listResponse = new TransferListResponseDTO(
                List.of(transfer1), new BigDecimal("1000000"), BigDecimal.ZERO, 1, 20, 1L, 1);

        when(transferController.loadTransfers(any(FilterTransfersRequestDTO.class))).thenReturn(listResponse);

        panel = new TransferPanel(transferController, walletController, categoryController);
    }

    @Test
    @DisplayName("Khởi tạo nạp dữ liệu danh sách chuyển tiền ban đầu và tóm tắt thành công")
    void testInitialDataLoaded() {
        assertEquals(1, panel.getTableModel().getRowCount());
        assertEquals("Ví Tiền mặt", panel.getTableModel().getValueAt(0, 2));
        assertEquals("Ví MB Bank", panel.getTableModel().getValueAt(0, 3));
        assertTrue(panel.getSummaryText().contains("Tổng tiền chuyển: 1,000,000 VNĐ"));
        assertTrue(panel.getPageInfoText().contains("Trang 1 / 1"));
        assertNotNull(panel.getTable());
    }

    @Test
    @DisplayName("Kích hoạt thay đổi bộ lọc ví nguồn gọi lại controller")
    void testFilterSourceWalletChanged() {
        panel.reloadTransfers();
        verify(transferController, atLeastOnce()).loadTransfers(any(FilterTransfersRequestDTO.class));
    }

    @Test
    @DisplayName("Kích hoạt callback onTransferChanged khi có thao tác làm mới")
    void testTransferChangedCallback() {
        AtomicBoolean triggered = new AtomicBoolean(false);
        panel.setOnTransferChangedListener(() -> triggered.set(true));

        panel.reloadTransfers();
        assertTrue(triggered.get());
    }

    @Test
    @DisplayName("Ném NullPointerException khi bất kỳ controller nào bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new TransferPanel(null, walletController, categoryController));
        assertThrows(NullPointerException.class, () -> new TransferPanel(transferController, null, categoryController));
        assertThrows(NullPointerException.class, () -> new TransferPanel(transferController, walletController, null));
    }
}
