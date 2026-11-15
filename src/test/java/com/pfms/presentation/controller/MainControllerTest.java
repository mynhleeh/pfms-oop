package com.pfms.presentation.controller;

import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.service.CategoryService;
import com.pfms.service.DataManagementService;
import com.pfms.service.ReportService;
import com.pfms.service.TransactionService;
import com.pfms.service.TransferService;
import com.pfms.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử MainController - Bộ điều phối tổng thể giao diện người dùng")
class MainControllerTest {

    private WalletService walletService;
    private TransactionService transactionService;
    private TransferService transferService;
    private CategoryService categoryService;
    private ReportService reportService;
    private DataManagementService dataManagementService;

    private MainController mainController;

    @BeforeEach
    void setUp() {
        walletService = Mockito.mock(WalletService.class);
        transactionService = Mockito.mock(TransactionService.class);
        transferService = Mockito.mock(TransferService.class);
        categoryService = Mockito.mock(CategoryService.class);
        reportService = Mockito.mock(ReportService.class);
        dataManagementService = Mockito.mock(DataManagementService.class);

        mainController = new MainController(
                walletService, transactionService, transferService,
                categoryService, reportService, dataManagementService);
    }

    @Nested
    @DisplayName("Kiểm thử Khởi tạo (Constructor)")
    class ConstructorTests {

        @Test
        @DisplayName("Ném NullPointerException khi bất kỳ dịch vụ nào bị null")
        void constructor_NullServices_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () -> new MainController(
                    null, transactionService, transferService, categoryService, reportService, dataManagementService));
            assertThrows(NullPointerException.class, () -> new MainController(
                    walletService, null, transferService, categoryService, reportService, dataManagementService));
            assertThrows(NullPointerException.class, () -> new MainController(
                    walletService, transactionService, null, categoryService, reportService, dataManagementService));
            assertThrows(NullPointerException.class, () -> new MainController(
                    walletService, transactionService, transferService, null, reportService, dataManagementService));
            assertThrows(NullPointerException.class, () -> new MainController(
                    walletService, transactionService, transferService, categoryService, null, dataManagementService));
            assertThrows(NullPointerException.class, () -> new MainController(
                    walletService, transactionService, transferService, categoryService, reportService, null));
        }

        @Test
        @DisplayName("Khởi tạo thành công từ 6 dịch vụ và tạo sẵn 6 sub-controllers")
        void constructor_ValidServices_InstantiatesSubControllersAndDefaultsToDashboard() {
            assertNotNull(mainController.getWalletController());
            assertNotNull(mainController.getTransactionController());
            assertNotNull(mainController.getTransferController());
            assertNotNull(mainController.getCategoryController());
            assertNotNull(mainController.getReportController());
            assertNotNull(mainController.getDataManagementController());
            assertEquals(MainController.CARD_DASHBOARD, mainController.getCurrentCard());
        }

        @Test
        @DisplayName("Khởi tạo thành công qua constructor nhận trực tiếp các sub-controllers")
        void constructor_DirectSubControllers_InstantiatesSuccessfully() {
            WalletGUIController walletCtrl = Mockito.mock(WalletGUIController.class);
            TransactionGUIController txnCtrl = Mockito.mock(TransactionGUIController.class);
            TransferGUIController trfCtrl = Mockito.mock(TransferGUIController.class);
            CategoryGUIController catCtrl = Mockito.mock(CategoryGUIController.class);
            ReportGUIController repCtrl = Mockito.mock(ReportGUIController.class);
            DataManagementGUIController datCtrl = Mockito.mock(DataManagementGUIController.class);

            MainController directController = new MainController(
                    walletCtrl, txnCtrl, trfCtrl, catCtrl, repCtrl, datCtrl, walletService, transactionService);

            assertSame(walletCtrl, directController.getWalletController());
            assertSame(txnCtrl, directController.getTransactionController());
            assertSame(trfCtrl, directController.getTransferController());
            assertSame(catCtrl, directController.getCategoryController());
            assertSame(repCtrl, directController.getReportController());
            assertSame(datCtrl, directController.getDataManagementController());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Khởi tạo hệ thống (initializeSystem)")
    class InitializeSystemTests {

        @Test
        @DisplayName("Ủy quyền khởi tạo ví mặc định cho walletService.initializeDefaultWallet")
        void initializeSystem_DelegatesToWalletService() {
            mainController.initializeSystem();
            verify(walletService).initializeDefaultWallet();
        }
    }

    @Nested
    @DisplayName("Kiểm thử Điều hướng màn hình (navigateTo)")
    class NavigationTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên thẻ null hoặc rỗng")
        void navigateTo_BlankCard_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> mainController.navigateTo(null));
            assertThrows(IllegalArgumentException.class, () -> mainController.navigateTo("   "));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tên thẻ không thuộc 7 thẻ hợp lệ")
        void navigateTo_InvalidCard_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> mainController.navigateTo("UNKNOWN_PANEL"));
        }

        @Test
        @DisplayName("Điều hướng thành công sang các thẻ hợp lệ không phân biệt hoa thường")
        void navigateTo_ValidCards_UpdatesCurrentCard() {
            assertEquals(MainController.CARD_WALLETS, mainController.navigateTo("wallets"));
            assertEquals(MainController.CARD_WALLETS, mainController.getCurrentCard());

            assertEquals(MainController.CARD_TRANSACTIONS, mainController.navigateTo("TRANSACTIONS"));
            assertEquals(MainController.CARD_TRANSFERS, mainController.navigateTo("transfers"));
            assertEquals(MainController.CARD_CATEGORIES, mainController.navigateTo("categories"));
            assertEquals(MainController.CARD_REPORTS, mainController.navigateTo("REPORTS"));
            assertEquals(MainController.CARD_DATA_MANAGEMENT, mainController.navigateTo("data_management"));
            assertEquals(MainController.CARD_DASHBOARD, mainController.navigateTo("dashboard"));
        }
    }

    @Nested
    @DisplayName("Kiểm thử Dữ liệu Tổng quan (Dashboard)")
    class DashboardTests {

        @Test
        @DisplayName("Tải danh sách ví tổng quan cho Dashboard ủy quyền cho walletService")
        void loadDashboardWallets_DelegatesToWalletService() {
            WalletsSummaryResponseDTO expected = new WalletsSummaryResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, "VND", Collections.emptyMap());
            when(walletService.getWalletsSummary()).thenReturn(expected);

            WalletsSummaryResponseDTO result = mainController.loadDashboardWallets();

            assertSame(expected, result);
            verify(walletService).getWalletsSummary();
        }

        @Test
        @DisplayName("Tải danh sách giao dịch gần đây cho Dashboard ủy quyền cho transactionService")
        void loadRecentTransactions_DelegatesToTransactionService() {
            TransactionListResponseDTO expected = new TransactionListResponseDTO();
            when(transactionService.filterTransactions(any())).thenReturn(expected);

            TransactionListResponseDTO result = mainController.loadRecentTransactions(5);

            assertSame(expected, result);
            ArgumentCaptor<FilterTransactionsRequestDTO> captor = ArgumentCaptor.forClass(FilterTransactionsRequestDTO.class);
            verify(transactionService).filterTransactions(captor.capture());
            FilterTransactionsRequestDTO request = captor.getValue();
            assertEquals(1, request.getPageNumber());
            assertEquals(5, request.getPageSize());
        }

        @Test
        @DisplayName("Mặc định giới hạn 5 bản ghi khi limit <= 0")
        void loadRecentTransactions_LimitZeroOrNegative_DefaultsToFive() {
            TransactionListResponseDTO expected = new TransactionListResponseDTO();
            when(transactionService.filterTransactions(any())).thenReturn(expected);

            mainController.loadRecentTransactions(0);

            ArgumentCaptor<FilterTransactionsRequestDTO> captor = ArgumentCaptor.forClass(FilterTransactionsRequestDTO.class);
            verify(transactionService).filterTransactions(captor.capture());
            assertEquals(5, captor.getValue().getPageSize());
        }
    }
}
