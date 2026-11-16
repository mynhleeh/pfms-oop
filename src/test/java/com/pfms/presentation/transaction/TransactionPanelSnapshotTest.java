package com.pfms.presentation.transaction;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.common.SwingSnapshotHelper;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
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

@DisplayName("Kiểm thử thị giác TransactionPanelSnapshotTest - Xuất ảnh chụp phân hệ Giao dịch chuẩn preview")
class TransactionPanelSnapshotTest {

    @Test
    @DisplayName("Xuất snapshot màn hình giao dịch sang target/snapshots/transaction-panel-redesign.png thành công")
    void testExportTransactionSnapshot() throws IOException {
        TransactionGUIController transactionController = Mockito.mock(TransactionGUIController.class);
        WalletGUIController walletController = Mockito.mock(WalletGUIController.class);
        CategoryGUIController categoryController = Mockito.mock(CategoryGUIController.class);

        // Wallets
        UUID w1Id = UUID.randomUUID();
        WalletResponseDTO w1 = new WalletResponseDTO(w1Id, "Ví Chính (VND)", new BigDecimal("125000000"), "VND", true, "ACTIVE", "Chi tiêu hàng ngày", LocalDateTime.now());
        UUID w2Id = UUID.randomUUID();
        WalletResponseDTO w2 = new WalletResponseDTO(w2Id, "Ví MB Bank", new BigDecimal("55000000"), "VND", false, "ACTIVE", "Lương & Thưởng", LocalDateTime.now());
        UUID w3Id = UUID.randomUUID();
        WalletResponseDTO w3 = new WalletResponseDTO(w3Id, "Ví Tiền mặt", new BigDecimal("2850000"), "VND", false, "ACTIVE", "Tiền mặt bỏ túi", LocalDateTime.now());

        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                List.of(w1, w2, w3), new BigDecimal("182850000"), "VNĐ", Collections.emptyMap());
        when(walletController.loadWalletsSummary()).thenReturn(summary);

        // Categories
        UUID g1Id = UUID.randomUUID();
        CategoryResponseDTO c1 = new CategoryResponseDTO(UUID.randomUUID(), "Ăn uống hàng ngày", g1Id, "Nhu cầu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c2 = new CategoryResponseDTO(UUID.randomUUID(), "Tiền thuê nhà & bảo trì", g1Id, "Nhu cầu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c3 = new CategoryResponseDTO(UUID.randomUUID(), "Hóa đơn điện, nước, net", g1Id, "Nhu cầu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c4 = new CategoryResponseDTO(UUID.randomUUID(), "Xăng xe & di chuyển", g1Id, "Nhu cầu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryHierarchyResponseDTO g1 = new CategoryHierarchyResponseDTO("EXPENSE", g1Id, "Nhu cầu thiết yếu", "ACTIVE", List.of(c1, c2, c3, c4));

        UUID g2Id = UUID.randomUUID();
        CategoryResponseDTO c5 = new CategoryResponseDTO(UUID.randomUUID(), "Khóa học & Sách vở", g2Id, "Giải trí & Phát triển", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c6 = new CategoryResponseDTO(UUID.randomUUID(), "Cafe & Giao lưu bạn bè", g2Id, "Giải trí & Phát triển", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryHierarchyResponseDTO g2 = new CategoryHierarchyResponseDTO("EXPENSE", g2Id, "Giải trí & Phát triển", "ACTIVE", List.of(c5, c6));

        UUID g3Id = UUID.randomUUID();
        CategoryResponseDTO c7 = new CategoryResponseDTO(UUID.randomUUID(), "Lương cố định hàng tháng", g3Id, "Thu nhập chủ động", "INCOME", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c8 = new CategoryResponseDTO(UUID.randomUUID(), "Thưởng hiệu quả & Dự án", g3Id, "Thu nhập chủ động", "INCOME", "ACTIVE", LocalDateTime.now());
        CategoryHierarchyResponseDTO g3 = new CategoryHierarchyResponseDTO("INCOME", g3Id, "Thu nhập chủ động", "ACTIVE", List.of(c7, c8));

        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(g1, g2, g3));

        // Sample Transactions
        TransactionResponseDTO t1 = new TransactionResponseDTO(
                UUID.randomUUID(), w1Id, w1.getName(), c1.getCategoryId(), c1.getName(),
                new BigDecimal("450000"), "EXPENSE", LocalDate.of(2026, 11, 15),
                "Ăn tối buffet cuối tuần cùng đồng nghiệp", new BigDecimal("124550000"), LocalDateTime.now());

        TransactionResponseDTO t2 = new TransactionResponseDTO(
                UUID.randomUUID(), w2Id, w2.getName(), c7.getCategoryId(), c7.getName(),
                new BigDecimal("35000000"), "INCOME", LocalDate.of(2026, 11, 14),
                "Nhận lương tháng 11/2026", new BigDecimal("55000000"), LocalDateTime.now());

        TransactionResponseDTO t3 = new TransactionResponseDTO(
                UUID.randomUUID(), w2Id, w2.getName(), c2.getCategoryId(), c2.getName(),
                new BigDecimal("8500000"), "EXPENSE", LocalDate.of(2026, 11, 12),
                "Thanh toán tiền thuê căn hộ Vinhomes", new BigDecimal("20000000"), LocalDateTime.now());

        TransactionResponseDTO t4 = new TransactionResponseDTO(
                UUID.randomUUID(), w1Id, w1.getName(), c5.getCategoryId(), c5.getName(),
                new BigDecimal("1200000"), "EXPENSE", LocalDate.of(2026, 11, 10),
                "Mua sách OOD và Clean Architecture", new BigDecimal("125750000"), LocalDateTime.now());

        TransactionResponseDTO t5 = new TransactionResponseDTO(
                UUID.randomUUID(), w2Id, w2.getName(), c8.getCategoryId(), c8.getName(),
                new BigDecimal("10000000"), "INCOME", LocalDate.of(2026, 11, 8),
                "Thưởng hoàn thành milestone Q3", new BigDecimal("28500000"), LocalDateTime.now());

        TransactionResponseDTO t6 = new TransactionResponseDTO(
                UUID.randomUUID(), w3Id, w3.getName(), c4.getCategoryId(), c4.getName(),
                new BigDecimal("350000"), "EXPENSE", LocalDate.of(2026, 11, 6),
                "Đổ xăng xe máy và bảo dưỡng định kỳ", new BigDecimal("2850000"), LocalDateTime.now());

        TransactionResponseDTO t7 = new TransactionResponseDTO(
                UUID.randomUUID(), w3Id, w3.getName(), c6.getCategoryId(), c6.getName(),
                new BigDecimal("180000"), "EXPENSE", LocalDate.of(2026, 11, 5),
                "Cafe sáng cuối tuần", new BigDecimal("3200000"), LocalDateTime.now());

        TransactionResponseDTO t8 = new TransactionResponseDTO(
                UUID.randomUUID(), w1Id, w1.getName(), c3.getCategoryId(), c3.getName(),
                new BigDecimal("1520000"), "EXPENSE", LocalDate.of(2026, 11, 3),
                "Tiền điện và internet tháng 10", true, new BigDecimal("5000"),
                null, "Phí chuyển khoản Napas", new BigDecimal("126950000"), LocalDateTime.now());

        List<TransactionResponseDTO> transactions = List.of(t1, t2, t3, t4, t5, t6, t7, t8);
        TransactionListResponseDTO listRes = new TransactionListResponseDTO(
                transactions,
                new BigDecimal("45000000"),
                new BigDecimal("12200000"),
                new BigDecimal("32800000"),
                1, 20, 8L, 1);

        when(transactionController.loadTransactions(any(FilterTransactionsRequestDTO.class))).thenReturn(listRes);

        TransactionPanel panel = new TransactionPanel(transactionController, walletController, categoryController);

        Path snapshotPath = SwingSnapshotHelper.capture(panel, 1340, 780, "transaction-panel-redesign.png");
        assertNotNull(snapshotPath);
        assertTrue(Files.exists(snapshotPath));
    }
}
