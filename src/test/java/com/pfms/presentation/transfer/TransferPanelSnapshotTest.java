package com.pfms.presentation.transfer;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.SwingSnapshotHelper;
import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.wallet.WalletGUIController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử thị giác TransferPanelSnapshotTest - Xuất ảnh chụp phân hệ Chuyển tiền chuẩn preview")
class TransferPanelSnapshotTest {

    @Test
    @DisplayName("Xuất snapshot màn hình chuyển tiền sang target/snapshots/transfer-panel-redesign.png thành công")
    void testExportTransferSnapshot() throws IOException {
        TransferGUIController transferController = Mockito.mock(TransferGUIController.class);
        WalletGUIController walletController = Mockito.mock(WalletGUIController.class);
        CategoryGUIController categoryController = Mockito.mock(CategoryGUIController.class);

        // Mock Wallets
        UUID wCashId = UUID.randomUUID();
        WalletResponseDTO wCash = new WalletResponseDTO(wCashId, "Tiền mặt", new BigDecimal("15000000"), "VND", true, "ACTIVE", "Tiền mặt bỏ túi", LocalDateTime.now());
        UUID wVcbId = UUID.randomUUID();
        WalletResponseDTO wVcb = new WalletResponseDTO(wVcbId, "Ngân hàng VCB", new BigDecimal("45000000"), "VND", false, "ACTIVE", "Tài khoản chính", LocalDateTime.now());
        UUID wMomoId = UUID.randomUUID();
        WalletResponseDTO wMomo = new WalletResponseDTO(wMomoId, "Ví MoMo", new BigDecimal("3500000"), "VND", false, "ACTIVE", "Thanh toán online", LocalDateTime.now());
        UUID wSavingId = UUID.randomUUID();
        WalletResponseDTO wSaving = new WalletResponseDTO(wSavingId, "Sổ tiết kiệm Techcombank", new BigDecimal("22000000"), "VND", false, "ACTIVE", "Tiết kiệm", LocalDateTime.now());

        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                List.of(wCash, wVcb, wMomo, wSaving), new BigDecimal("85500000"), "VNĐ", Collections.emptyMap());
        when(walletController.loadWalletsSummary()).thenReturn(summary);
        when(categoryController.loadCategoryHierarchy()).thenReturn(Collections.emptyList());

        // Mock Transfers
        TransferResponseDTO trf1 = new TransferResponseDTO(
                UUID.randomUUID(), wCashId, "Tiền mặt", wVcbId, "Ngân hàng VCB",
                new BigDecimal("10000000"), LocalDate.of(2026, 11, 15),
                "Nộp tiền mặt vào tài khoản ngân hàng", new BigDecimal("5000000"),
                new BigDecimal("55000000"), LocalDateTime.now());

        TransferResponseDTO trf2 = new TransferResponseDTO(
                UUID.randomUUID(), wVcbId, "Ngân hàng VCB", wMomoId, "Ví MoMo",
                new BigDecimal("2000000"), LocalDate.of(2026, 11, 14),
                "Nạp ví điện tử thanh toán online", new BigDecimal("53000000"),
                new BigDecimal("5500000"), LocalDateTime.now());

        TransferResponseDTO trf3 = new TransferResponseDTO(
                UUID.randomUUID(), wVcbId, "Ngân hàng VCB", wSavingId, "Sổ tiết kiệm Techcombank",
                new BigDecimal("15000000"), LocalDate.of(2026, 11, 11),
                "Trích lương gửi tiết kiệm định kỳ", new BigDecimal("11000"),
                null, "Phí chuyển liên ngân hàng", new BigDecimal("37989000"),
                new BigDecimal("37000000"), LocalDateTime.now());

        TransferResponseDTO trf4 = new TransferResponseDTO(
                UUID.randomUUID(), wMomoId, "Ví MoMo", wCashId, "Tiền mặt",
                new BigDecimal("500000"), LocalDate.of(2026, 11, 8),
                "Rút tiền mặt chi tiêu cuối tuần", new BigDecimal("5000000"),
                new BigDecimal("5500000"), LocalDateTime.now());

        TransferResponseDTO trf5 = new TransferResponseDTO(
                UUID.randomUUID(), wVcbId, "Ngân hàng VCB", wCashId, "Tiền mặt",
                new BigDecimal("5000000"), LocalDate.of(2026, 11, 5),
                "Rút tiền mặt dự phòng chi tiêu", new BigDecimal("11000"),
                null, "Phí rút tiền tại ATM", new BigDecimal("32989000"),
                new BigDecimal("10500000"), LocalDateTime.now());

        List<TransferResponseDTO> transfers = List.of(trf1, trf2, trf3, trf4, trf5);
        TransferListResponseDTO listRes = new TransferListResponseDTO(
                transfers, new BigDecimal("32500000"), new BigDecimal("22000"), 1, 20, 5L, 1);

        when(transferController.loadTransfers(any(FilterTransfersRequestDTO.class))).thenReturn(listRes);

        TransferPanel panel = new TransferPanel(transferController, walletController, categoryController);

        Path snapshotPath = SwingSnapshotHelper.capture(panel, 1340, 780, "transfer-panel-redesign.png");
        assertNotNull(snapshotPath);
        assertTrue(Files.exists(snapshotPath));
    }
}
