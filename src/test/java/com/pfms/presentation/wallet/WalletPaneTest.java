package com.pfms.presentation.wallet;

import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.wallet.viewmodel.WalletItemViewModel;
import com.pfms.presentation.wallet.viewmodel.WalletsSummaryViewModel;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử WalletPane - Bảng điều khiển phân hệ Quản lý Ví JavaFX")
class WalletPaneTest {

    private WalletGUIController walletController;
    private WalletResponseDTO wallet1;
    private WalletResponseDTO wallet2;
    private WalletPane pane;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        walletController = Mockito.mock(WalletGUIController.class);

        wallet1 = new WalletResponseDTO(
                UUID.randomUUID(), "Ví MB Bank", new BigDecimal("15000000"), "VND", true, "ACTIVE", "Tài khoản lương", LocalDateTime.now());
        wallet2 = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Tiền mặt", new BigDecimal("2000000"), "VND", false, "ACTIVE", "Chi tiêu hàng ngày", LocalDateTime.now());

        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                List.of(wallet1, wallet2), new BigDecimal("17000000"), "VND", Collections.emptyMap());

        when(walletController.loadWalletsSummary()).thenReturn(summary);
        when(walletController.loadWalletsSummary(any())).thenReturn(summary);

        JavaFxTestHelper.runOnFxThread(() -> {
            pane = new WalletPane(walletController);
        });
    }

    @Test
    @DisplayName("Khởi tạo WalletPane nạp thành công 2 ví vào bảng dữ liệu JavaFX")
    void testInitialDataLoaded() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertEquals(2, pane.getTableData().size());
            assertEquals("Ví MB Bank", pane.getTableData().get(0).name());
            assertEquals("Ví Tiền mặt", pane.getTableData().get(1).name());
            assertTrue(pane.getSummaryText().contains("Tổng số ví: 2"));
            assertNotNull(pane.getTableView());
            assertNotNull(pane.getPresenter());
        });
    }

    @Test
    @DisplayName("renderWallets cập nhật danh sách hiển thị trên bảng")
    void testRenderWallets() {
        WalletItemViewModel item = new WalletItemViewModel(
                UUID.randomUUID(), 1, "Ví Mới", BigDecimal.TEN, "10 ₫", "ACTIVE", "Ghi chú", true);

        JavaFxTestHelper.runOnFxThread(() -> {
            pane.renderWallets(List.of(item));
            assertEquals(1, pane.getTableData().size());
            assertEquals("Ví Mới", pane.getTableData().get(0).name());
        });
    }

    @Test
    @DisplayName("renderSummary cập nhật nhãn tóm tắt và KPI")
    void testRenderSummary() {
        WalletsSummaryViewModel summaryVM = new WalletsSummaryViewModel(
                new BigDecimal("50000000"), "50,000,000 ₫", 3, 1);

        JavaFxTestHelper.runOnFxThread(() -> {
            pane.renderSummary(summaryVM);
            assertTrue(pane.getSummaryText().contains("50,000,000 ₫"));
            assertTrue(pane.getSummaryText().contains("Tổng số ví: 4"));
        });
    }

    @Test
    @DisplayName("Các hàm showMessage, showWarning, showError chuyển tiếp chính xác đến consumer")
    void testNotificationConsumers() {
        AtomicReference<String> msgRef = new AtomicReference<>();
        AtomicReference<String> warnRef = new AtomicReference<>();
        AtomicReference<String> errRef = new AtomicReference<>();

        pane.setMessageConsumer(msgRef::set);
        pane.setWarningConsumer(warnRef::set);
        pane.setErrorConsumer(errRef::set);

        pane.showMessage("Tin nhắn");
        pane.showWarning("Cảnh báo");
        pane.showError("Báo lỗi");

        assertEquals("Tin nhắn", msgRef.get());
        assertEquals("Cảnh báo", warnRef.get());
        assertEquals("Báo lỗi", errRef.get());
    }

    @Test
    @DisplayName("Chuyển đổi chế độ xem ViewModeSwitcher và kết xuất Card Grid View thành công")
    void testDualViewSwitchingAndCardGridRendering() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(pane.getCardGridPane());
            assertEquals(2, pane.getCardGridPane().getChildren().size());

            pane.getViewModeSwitcher().setMode(com.pfms.presentation.wallet.component.ViewModeSwitcher.ViewMode.TABLE);
            assertEquals(com.pfms.presentation.wallet.component.ViewModeSwitcher.ViewMode.TABLE,
                    pane.getViewModeSwitcher().getCurrentMode());

            pane.getViewModeSwitcher().setMode(com.pfms.presentation.wallet.component.ViewModeSwitcher.ViewMode.CARD_GRID);
            assertEquals(com.pfms.presentation.wallet.component.ViewModeSwitcher.ViewMode.CARD_GRID,
                    pane.getViewModeSwitcher().getCurrentMode());
        });
    }
}
