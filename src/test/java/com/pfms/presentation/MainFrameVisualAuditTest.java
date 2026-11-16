package com.pfms.presentation;

import com.pfms.presentation.common.MainController;
import com.pfms.presentation.common.MainFrame;
import com.pfms.presentation.common.SwingSnapshotHelper;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.service.*;
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

@DisplayName("Kiểm toán thị giác toàn diện 4 phân hệ chính - MainFrameVisualAuditTest")
class MainFrameVisualAuditTest {

    @Test
    @DisplayName("Xuất snapshot toàn bộ 4 màn hình của MainFrame để đối soát thị giác")
    void testAuditAllFourSubsystems() throws IOException {
        WalletService walletService = Mockito.mock(WalletService.class);
        TransactionService transactionService = Mockito.mock(TransactionService.class);
        TransferService transferService = Mockito.mock(TransferService.class);
        CategoryService categoryService = Mockito.mock(CategoryService.class);
        ReportService reportService = Mockito.mock(ReportService.class);
        DataManagementService dataManagementService = Mockito.mock(DataManagementService.class);

        // 1. Mock Wallets
        UUID w1Id = UUID.randomUUID();
        WalletResponseDTO w1 = new WalletResponseDTO(w1Id, "MB Bank Priority (Lương)", new BigDecimal("45500000"), "VND", true, "ACTIVE", "Tài khoản thanh toán nhận lương và chi tiêu sinh hoạt chính.", LocalDateTime.now());
        UUID w2Id = UUID.randomUUID();
        WalletResponseDTO w2 = new WalletResponseDTO(w2Id, "Tiền mặt hàng ngày (Cash)", new BigDecimal("3500000"), "VND", false, "ACTIVE", "Tiền mặt phục vụ ăn sáng, cafe, xăng xe và chi tiêu nhỏ lẻ.", LocalDateTime.now());
        UUID w3Id = UUID.randomUUID();
        WalletResponseDTO w3 = new WalletResponseDTO(w3Id, "Quỹ khẩn cấp (Emergency Fund)", new BigDecimal("33850000"), "VND", false, "ACTIVE", "Dự phòng rủi ro 3-6 tháng sinh hoạt, gửi tiết kiệm online.", LocalDateTime.now());
        UUID w4Id = UUID.randomUUID();
        WalletResponseDTO w4 = new WalletResponseDTO(w4Id, "Sổ tiết kiệm MB Bank (6 tháng)", new BigDecimal("100000000"), "VND", false, "ARCHIVED", "Sổ tiết kiệm hưởng lãi kỳ hạn 6 tháng. Tạm ngưng phát sinh giao dịch thường.", LocalDateTime.now());

        List<WalletResponseDTO> walletList = List.of(w1, w2, w3, w4);
        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                walletList, new BigDecimal("182850000"), "VNĐ", Collections.emptyMap());
        when(walletService.getWalletsSummary()).thenReturn(summary);
        when(walletService.getWalletsSummary(any(String.class))).thenReturn(summary);

        // 2. Mock Categories
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

        when(categoryService.getCategoryHierarchy()).thenReturn(List.of(g1, g2, g3));

        // 3. Mock Transactions
        TransactionResponseDTO t1 = new TransactionResponseDTO(
                UUID.randomUUID(), w1Id, w1.getName(), c1.getCategoryId(), c1.getName(),
                new BigDecimal("450000"), "EXPENSE", LocalDate.of(2026, 11, 15),
                "Ăn tối buffet cuối tuần cùng đồng nghiệp", new BigDecimal("45050000"), LocalDateTime.now());

        TransactionResponseDTO t2 = new TransactionResponseDTO(
                UUID.randomUUID(), w1Id, w1.getName(), c7.getCategoryId(), c7.getName(),
                new BigDecimal("35000000"), "INCOME", LocalDate.of(2026, 11, 14),
                "Nhận lương tháng 11/2026", new BigDecimal("45500000"), LocalDateTime.now());

        TransactionResponseDTO t3 = new TransactionResponseDTO(
                UUID.randomUUID(), w1Id, w1.getName(), c2.getCategoryId(), c2.getName(),
                new BigDecimal("8500000"), "EXPENSE", LocalDate.of(2026, 11, 12),
                "Thanh toán tiền thuê căn hộ Vinhomes", new BigDecimal("10500000"), LocalDateTime.now());

        TransactionResponseDTO t4 = new TransactionResponseDTO(
                UUID.randomUUID(), w1Id, w1.getName(), c5.getCategoryId(), c5.getName(),
                new BigDecimal("1200000"), "EXPENSE", LocalDate.of(2026, 11, 10),
                "Mua sách OOD và Clean Architecture", new BigDecimal("19000000"), LocalDateTime.now());

        TransactionResponseDTO t5 = new TransactionResponseDTO(
                UUID.randomUUID(), w1Id, w1.getName(), c8.getCategoryId(), c8.getName(),
                new BigDecimal("10000000"), "INCOME", LocalDate.of(2026, 11, 8),
                "Thưởng hoàn thành milestone Q3", new BigDecimal("20200000"), LocalDateTime.now());

        TransactionResponseDTO t6 = new TransactionResponseDTO(
                UUID.randomUUID(), w2Id, w2.getName(), c4.getCategoryId(), c4.getName(),
                new BigDecimal("350000"), "EXPENSE", LocalDate.of(2026, 11, 6),
                "Đổ xăng xe máy và bảo dưỡng định kỳ", new BigDecimal("3500000"), LocalDateTime.now());

        TransactionResponseDTO t7 = new TransactionResponseDTO(
                UUID.randomUUID(), w2Id, w2.getName(), c6.getCategoryId(), c6.getName(),
                new BigDecimal("180000"), "EXPENSE", LocalDate.of(2026, 11, 5),
                "Cafe sáng cuối tuần", new BigDecimal("3850000"), LocalDateTime.now());

        TransactionResponseDTO t8 = new TransactionResponseDTO(
                UUID.randomUUID(), w1Id, w1.getName(), c3.getCategoryId(), c3.getName(),
                new BigDecimal("1520000"), "EXPENSE", LocalDate.of(2026, 11, 3),
                "Tiền điện và internet tháng 10", true, new BigDecimal("5000"),
                null, "Phí chuyển khoản Napas", new BigDecimal("10200000"), LocalDateTime.now());

        List<TransactionResponseDTO> transactions = List.of(t1, t2, t3, t4, t5, t6, t7, t8);
        TransactionListResponseDTO transList = new TransactionListResponseDTO(
                transactions,
                new BigDecimal("45000000"),
                new BigDecimal("12200000"),
                new BigDecimal("32800000"),
                1, 20, 8L, 1);
        when(transactionService.filterTransactions(any())).thenReturn(transList);

        // 4. Mock Transfers
        TransferResponseDTO trf1 = new TransferResponseDTO(
                UUID.randomUUID(), w2Id, "Tiền mặt hàng ngày (Cash)", w1Id, "MB Bank Priority (Lương)",
                new BigDecimal("10000000"), LocalDate.of(2026, 11, 15),
                "Nộp tiền mặt vào tài khoản ngân hàng", new BigDecimal("5000000"),
                new BigDecimal("55000000"), LocalDateTime.now());

        TransferResponseDTO trf2 = new TransferResponseDTO(
                UUID.randomUUID(), w1Id, "MB Bank Priority (Lương)", w3Id, "Quỹ khẩn cấp (Emergency Fund)",
                new BigDecimal("2000000"), LocalDate.of(2026, 11, 14),
                "Trích quỹ dự phòng rủi ro", new BigDecimal("53000000"),
                new BigDecimal("35850000"), LocalDateTime.now());

        TransferResponseDTO trf3 = new TransferResponseDTO(
                UUID.randomUUID(), w1Id, "MB Bank Priority (Lương)", w4Id, "Sổ tiết kiệm MB Bank (6 tháng)",
                new BigDecimal("15000000"), LocalDate.of(2026, 11, 11),
                "Trích lương gửi tiết kiệm định kỳ", new BigDecimal("11000"),
                null, "Phí chuyển liên ngân hàng", new BigDecimal("37989000"),
                new BigDecimal("100000000"), LocalDateTime.now());

        TransferResponseDTO trf4 = new TransferResponseDTO(
                UUID.randomUUID(), w3Id, "Quỹ khẩn cấp (Emergency Fund)", w2Id, "Tiền mặt hàng ngày (Cash)",
                new BigDecimal("500000"), LocalDate.of(2026, 11, 8),
                "Rút tiền mặt chi tiêu cuối tuần", new BigDecimal("33350000"),
                new BigDecimal("4000000"), LocalDateTime.now());

        TransferResponseDTO trf5 = new TransferResponseDTO(
                UUID.randomUUID(), w1Id, "MB Bank Priority (Lương)", w2Id, "Tiền mặt hàng ngày (Cash)",
                new BigDecimal("5000000"), LocalDate.of(2026, 11, 5),
                "Rút tiền mặt dự phòng chi tiêu", new BigDecimal("11000"),
                null, "Phí rút tiền tại ATM", new BigDecimal("32989000"),
                new BigDecimal("9000000"), LocalDateTime.now());

        List<TransferResponseDTO> transfers = List.of(trf1, trf2, trf3, trf4, trf5);
        TransferListResponseDTO transferList = new TransferListResponseDTO(
                transfers, new BigDecimal("32500000"), new BigDecimal("22000"), 1, 20, 5L, 1);
        when(transferService.filterTransfers(any())).thenReturn(transferList);

        when(reportService.generateCashFlowReport(any())).thenReturn(CashFlowReportResponseDTO.empty());
        when(reportService.generateSpendingStructureReport(any()))
                .thenReturn(new SpendingStructureReportResponseDTO("EXPENSE", null, null, BigDecimal.ZERO, Collections.emptyList()));

        MainController controller = new MainController(
                walletService, transactionService, transferService,
                categoryService, reportService, dataManagementService);

        MainFrame frame = new MainFrame(controller, "data/pfms_data.json");

        // 1. Snapshot Màn hình Ví (Wallet)
        frame.showCard(MainController.CARD_WALLETS);
        Path pWallet = SwingSnapshotHelper.capture(frame.getRootPane(), 1560, 820, "audit-wallet-frame.png");
        assertNotNull(pWallet);
        assertTrue(Files.exists(pWallet));

        // 2. Snapshot Màn hình Danh mục (Category)
        frame.showCard(MainController.CARD_CATEGORIES);
        Path pCategory = SwingSnapshotHelper.capture(frame.getRootPane(), 1560, 820, "audit-category-frame.png");
        assertNotNull(pCategory);
        assertTrue(Files.exists(pCategory));

        // 3. Snapshot Màn hình Giao dịch (Transaction)
        frame.showCard(MainController.CARD_TRANSACTIONS);
        Path pTransaction = SwingSnapshotHelper.capture(frame.getRootPane(), 1560, 820, "audit-transaction-frame.png");
        assertNotNull(pTransaction);
        assertTrue(Files.exists(pTransaction));

        // 4. Snapshot Màn hình Chuyển tiền (Transfer)
        frame.showCard(MainController.CARD_TRANSFERS);
        Path pTransfer = SwingSnapshotHelper.capture(frame.getRootPane(), 1560, 820, "audit-transfer-frame.png");
        assertNotNull(pTransfer);
        assertTrue(Files.exists(pTransfer));
    }
}
