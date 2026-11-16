package com.pfms.presentation.wallet;

import com.pfms.presentation.common.SwingSnapshotHelper;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử trực quan WalletPanelSnapshotTest - Xuất ảnh chụp thực tế màn hình ví")
class WalletPanelSnapshotTest {

    @Test
    @DisplayName("Xuất snapshot màn hình ví sang target/snapshots/wallet-panel-preview.png thành công")
    void testExportWalletPanelSnapshot() throws IOException {
        WalletGUIController controller = Mockito.mock(WalletGUIController.class);

        WalletResponseDTO w1 = new WalletResponseDTO(
                UUID.randomUUID(), "MB Bank Priority (Lương)", new BigDecimal("45500000"), "VND", true, "ACTIVE",
                "Tài khoản thanh toán nhận lương và chi tiêu sinh hoạt chính.", LocalDateTime.now());

        WalletResponseDTO w2 = new WalletResponseDTO(
                UUID.randomUUID(), "Tiền mặt hàng ngày (Cash)", new BigDecimal("3500000"), "VND", false, "ACTIVE",
                "Tiền mặt phục vụ ăn sáng, cafe, xăng xe và chi tiêu nhỏ lẻ.", LocalDateTime.now());

        WalletResponseDTO w3 = new WalletResponseDTO(
                UUID.randomUUID(), "Quỹ khẩn cấp (Emergency Fund)", new BigDecimal("33850000"), "VND", false, "ACTIVE",
                "Dự phòng rủi ro 3-6 tháng sinh hoạt, gửi tiết kiệm online.", LocalDateTime.now());

        WalletResponseDTO w4 = new WalletResponseDTO(
                UUID.randomUUID(), "Sổ tiết kiệm MB Bank (6 tháng)", new BigDecimal("100000000"), "VND", false, "ARCHIVED",
                "Sổ tiết kiệm hưởng lãi kỳ hạn 6 tháng. Tạm ngưng phát sinh giao dịch thường.", LocalDateTime.now());

        List<WalletResponseDTO> list = List.of(w1, w2, w3, w4);
        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                list, new BigDecimal("182850000"), "VND", java.util.Collections.emptyMap());

        when(controller.loadWalletsSummary()).thenReturn(summary);
        when(controller.loadWalletsSummary(Mockito.any())).thenReturn(summary);

        WalletPanel panel = new WalletPanel(controller);

        // Xuất ảnh chụp chế độ mặc định (Thẻ ví / Card Grid)
        Path cardPath = SwingSnapshotHelper.capture(panel, 1340, 780, "wallet-panel-cards.png");
        assertNotNull(cardPath);
        assertTrue(Files.exists(cardPath));

        // Chuyển sang chế độ xem Bảng và xuất ảnh chụp
        panel.getBtnViewTable().doClick();
        Path tablePath = SwingSnapshotHelper.capture(panel, 1340, 780, "wallet-panel-table.png");
        assertNotNull(tablePath);
        assertTrue(Files.exists(tablePath));
    }
}
