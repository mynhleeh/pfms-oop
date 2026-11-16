package com.pfms.presentation.transfer;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.transfer.viewmodel.TransferItemViewModel;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử TransferPane - Bảng điều khiển phân hệ chuyển tiền nội bộ JavaFX")
class TransferPaneTest {

    private TransferGUIController transferController;
    private WalletGUIController walletController;
    private CategoryGUIController categoryController;

    private TransferResponseDTO transfer1;
    private TransferResponseDTO transfer2;
    private TransferListResponseDTO listResponse;
    private TransferPane pane;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

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

        transfer2 = new TransferResponseDTO(
                UUID.randomUUID(), w2.getWalletId(), "Ví MB Bank",
                w1.getWalletId(), "Ví Tiền mặt", new BigDecimal("500000"),
                LocalDate.now(), "Rút tiền mặt", new BigDecimal("10500000"),
                new BigDecimal("4500000"), LocalDateTime.now());

        listResponse = new TransferListResponseDTO(
                List.of(transfer1, transfer2), new BigDecimal("1500000"), new BigDecimal("5000"), 1, 20, 2L, 1);

        when(transferController.loadTransfers(any())).thenReturn(listResponse);

        JavaFxTestHelper.runOnFxThread(() -> {
            pane = new TransferPane(transferController, walletController, categoryController);
        });
    }

    @Test
    @DisplayName("Khởi tạo TransferPane nạp chính xác dữ liệu giao dịch vào TableView và KPI cards")
    void testInitialDataLoaded() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(pane.getTableView());
            assertEquals(2, pane.getTableData().size());
            assertEquals("Ví Tiền mặt", pane.getTableData().get(0).fromWalletName());
            assertEquals("Ví MB Bank", pane.getTableData().get(0).toWalletName());
            assertEquals("Ví MB Bank", pane.getTableData().get(1).fromWalletName());
            assertEquals("Ví Tiền mặt", pane.getTableData().get(1).toWalletName());

            assertTrue(pane.getSummaryText().contains("Tổng tiền chuyển: 1,500,000 VNĐ"));
            assertTrue(pane.getSummaryText().contains("Tổng phí: 5,000 VNĐ"));
            assertTrue(pane.getPageInfoText().contains("Trang 1 / 1 (Tổng 2 giao dịch)"));

            assertTrue(pane.getStatTotalTransferredLabel().getText().contains("1,500,000 VNĐ"));
            assertTrue(pane.getStatTotalFeesLabel().getText().contains("5,000 VNĐ"));
            assertTrue(pane.getStatCountLabel().getText().contains("2 giao dịch"));
        });
    }

    @Test
    @DisplayName("renderTransfers cập nhật chính xác danh sách chuyển khoản mới và các nhãn tóm tắt")
    void testRenderTransfers() {
        TransferItemViewModel customItem = new TransferItemViewModel(
                UUID.randomUUID(), 1, "16/11/2026", "Ví A", "Ví B",
                "2,000,000 VNĐ", new BigDecimal("2000000"), "Chuyển gấp",
                BigDecimal.ZERO, BigDecimal.ZERO);

        JavaFxTestHelper.runOnFxThread(() -> {
            pane.renderTransfers(
                    List.of(customItem),
                    "Tổng tiền chuyển: 2,000,000 VNĐ | Tổng phí: 0 VNĐ",
                    "Trang 1 / 1 (Tổng 1 giao dịch)");

            assertEquals(1, pane.getTableData().size());
            assertEquals("Ví A", pane.getTableData().get(0).fromWalletName());
            assertEquals("Ví B", pane.getTableData().get(0).toWalletName());
            assertTrue(pane.getStatTotalTransferredLabel().getText().contains("2,000,000 VNĐ"));
            assertTrue(pane.getStatTotalFeesLabel().getText().contains("0 VNĐ"));
            assertTrue(pane.getStatCountLabel().getText().contains("1 giao dịch"));
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

        pane.showMessage("Chuyển tiền thành công");
        assertEquals("Chuyển tiền thành công", msgRef.get());

        pane.showWarning("Số dư thấp");
        assertEquals("Số dư thấp", warnRef.get());

        pane.showError("Lỗi kết nối");
        assertEquals("Lỗi kết nối", errRef.get());
    }

    @Test
    @DisplayName("Presenter và Controller liên kết hợp lệ trong TransferPane")
    void testPresenterAttached() {
        assertNotNull(pane.getPresenter());
        assertNotNull(pane.getPresenter().getController());
    }
}
