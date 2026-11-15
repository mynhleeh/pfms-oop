package com.pfms.presentation.view;

import com.pfms.presentation.controller.MainController;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.service.CategoryService;
import com.pfms.service.DataManagementService;
import com.pfms.service.ReportService;
import com.pfms.service.TransactionService;
import com.pfms.service.TransferService;
import com.pfms.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử MainFrame - Cửa sổ chính của ứng dụng PFMS")
class MainFrameTest {

    private WalletService walletService;
    private TransactionService transactionService;
    private TransferService transferService;
    private CategoryService categoryService;
    private ReportService reportService;
    private DataManagementService dataManagementService;
    private MainController mainController;

    private MainFrame mainFrame;

    @BeforeEach
    void setUp() {
        walletService = Mockito.mock(WalletService.class);
        transactionService = Mockito.mock(TransactionService.class);
        transferService = Mockito.mock(TransferService.class);
        categoryService = Mockito.mock(CategoryService.class);
        reportService = Mockito.mock(ReportService.class);
        dataManagementService = Mockito.mock(DataManagementService.class);

        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                Collections.emptyList(), new BigDecimal("5000000"), "VNĐ", Collections.emptyMap());
        when(walletService.getWalletsSummary()).thenReturn(summary);
        when(categoryService.getCategoryHierarchy()).thenReturn(Collections.emptyList());
        when(transactionService.filterTransactions(any())).thenReturn(new TransactionListResponseDTO());
        when(transferService.filterTransfers(any())).thenReturn(new TransferListResponseDTO());
        when(reportService.generateCashFlowReport(any())).thenReturn(CashFlowReportResponseDTO.empty());
        when(reportService.generateSpendingStructureReport(any()))
                .thenReturn(new SpendingStructureReportResponseDTO("EXPENSE", null, null, BigDecimal.ZERO, Collections.emptyList()));

        mainController = new MainController(
                walletService, transactionService, transferService,
                categoryService, reportService, dataManagementService);

        mainFrame = new MainFrame(mainController, "test_data.json");
    }

    @Test
    @DisplayName("Khởi tạo cấu trúc ba vùng BorderLayout chính xác")
    void testFrameStructure() {
        assertNotNull(mainFrame.getSidebarPanel());
        assertNotNull(mainFrame.getStatusBarPanel());
        assertNotNull(mainFrame.getWalletPanel());
        assertNotNull(mainFrame.getTransactionPanel());
        assertNotNull(mainFrame.getTransferPanel());
        assertNotNull(mainFrame.getCategoryPanel());
        assertNotNull(mainFrame.getReportPanel());
        assertNotNull(mainFrame.getDataManagementPanel());
        assertEquals(MainController.CARD_DASHBOARD, mainFrame.getCurrentCardName());
        assertTrue(mainFrame.getStatusBarPanel().getTotalBalanceText().contains("5,000,000 VNĐ"));
    }

    @Test
    @DisplayName("Chuyển thẻ qua showCard đồng bộ trạng thái CardLayout và Controller")
    void testShowCard() {
        mainFrame.showCard(MainController.CARD_WALLETS);
        assertEquals(MainController.CARD_WALLETS, mainFrame.getCurrentCardName());
        assertEquals(MainController.CARD_WALLETS, mainController.getCurrentCard());
        assertEquals(MainController.CARD_WALLETS, mainFrame.getSidebarPanel().getActiveCard());

        mainFrame.showCard(MainController.CARD_TRANSACTIONS);
        assertEquals(MainController.CARD_TRANSACTIONS, mainFrame.getCurrentCardName());
        assertEquals(MainController.CARD_TRANSACTIONS, mainController.getCurrentCard());
        assertEquals(MainController.CARD_TRANSACTIONS, mainFrame.getSidebarPanel().getActiveCard());

        mainFrame.showCard(MainController.CARD_TRANSFERS);
        assertEquals(MainController.CARD_TRANSFERS, mainFrame.getCurrentCardName());
        assertEquals(MainController.CARD_TRANSFERS, mainController.getCurrentCard());
        assertEquals(MainController.CARD_TRANSFERS, mainFrame.getSidebarPanel().getActiveCard());

        mainFrame.showCard(MainController.CARD_CATEGORIES);
        assertEquals(MainController.CARD_CATEGORIES, mainFrame.getCurrentCardName());
        assertEquals(MainController.CARD_CATEGORIES, mainController.getCurrentCard());
        assertEquals(MainController.CARD_CATEGORIES, mainFrame.getSidebarPanel().getActiveCard());

        mainFrame.showCard(MainController.CARD_REPORTS);
        assertEquals(MainController.CARD_REPORTS, mainFrame.getCurrentCardName());
        assertEquals(MainController.CARD_REPORTS, mainController.getCurrentCard());
        assertEquals(MainController.CARD_REPORTS, mainFrame.getSidebarPanel().getActiveCard());

        mainFrame.showCard(MainController.CARD_DATA_MANAGEMENT);
        assertEquals(MainController.CARD_DATA_MANAGEMENT, mainFrame.getCurrentCardName());
        assertEquals(MainController.CARD_DATA_MANAGEMENT, mainController.getCurrentCard());
        assertEquals(MainController.CARD_DATA_MANAGEMENT, mainFrame.getSidebarPanel().getActiveCard());
    }

    @Test
    @DisplayName("Làm mới toàn bộ phân hệ khi có sự kiện phục hồi dữ liệu")
    void testRefreshAllSubsystems() {
        assertDoesNotThrow(() -> mainFrame.refreshAllSubsystems());
    }

    @Test
    @DisplayName("Ném NullPointerException khi MainController bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new MainFrame(null, "path.json"));
    }
}
