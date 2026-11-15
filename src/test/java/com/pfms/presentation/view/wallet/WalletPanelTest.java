package com.pfms.presentation.view.wallet;

import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử WalletPanel - Bảng điều khiển phân hệ ví")
class WalletPanelTest {

    private WalletGUIController walletController;
    private WalletResponseDTO wallet1;
    private WalletPanel panel;

    @BeforeEach
    void setUp() {
        walletController = Mockito.mock(WalletGUIController.class);
        wallet1 = new WalletResponseDTO(
                UUID.randomUUID(), "Ví MB Bank", new BigDecimal("15000000"), "VND", true, "ACTIVE", "Tài khoản chính", LocalDateTime.now());

        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                List.of(wallet1), new BigDecimal("15000000"), "VND", Collections.emptyMap());
        when(walletController.loadWalletsSummary()).thenReturn(summary);
        when(walletController.loadWalletsSummary(any())).thenReturn(summary);

        panel = new WalletPanel(walletController);
    }

    @Test
    @DisplayName("Khởi tạo nạp danh sách ví ban đầu vào bảng thành công")
    void testInitialDataLoaded() {
        assertEquals(1, panel.getTableModel().getRowCount());
        assertEquals("Ví MB Bank", panel.getTableModel().getValueAt(0, 1));
        assertTrue(panel.getSummaryText().contains("Tổng số ví: 1"));
        assertNotNull(panel.getTable());
    }

    @Test
    @DisplayName("Kích hoạt thay đổi bộ lọc trạng thái gọi lại controller")
    void testFilterStatusChanged() {
        panel.setSelectedStatusFilter("ARCHIVED");
        verify(walletController).loadWalletsSummary("ARCHIVED");
    }

    @Test
    @DisplayName("Kích hoạt callback onWalletChanged khi có thao tác làm mới")
    void testWalletChangedCallback() {
        AtomicBoolean triggered = new AtomicBoolean(false);
        panel.setOnWalletChangedListener(() -> triggered.set(true));

        panel.reloadWallets();
        assertTrue(triggered.get());
    }

    @Test
    @DisplayName("Ném NullPointerException khi WalletGUIController bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new WalletPanel(null));
    }
}
