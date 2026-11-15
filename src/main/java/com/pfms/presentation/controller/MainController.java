package com.pfms.presentation.controller;

import com.pfms.common.util.Preconditions;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.service.CategoryService;
import com.pfms.service.DataManagementService;
import com.pfms.service.ReportService;
import com.pfms.service.TransactionService;
import com.pfms.service.TransferService;
import com.pfms.service.WalletService;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Bộ điều phối trung tâm của Tầng Trình diễn (Presentation Layer).
 * Quản lý điều hướng giữa 7 màn hình phân hệ, khởi tạo hệ thống và cung cấp dữ
 * liệu màn hình Tổng quan.
 */
public final class MainController {

    // region Constants
    public static final String CARD_DASHBOARD = "DASHBOARD";
    public static final String CARD_WALLETS = "WALLETS";
    public static final String CARD_TRANSACTIONS = "TRANSACTIONS";
    public static final String CARD_TRANSFERS = "TRANSFERS";
    public static final String CARD_CATEGORIES = "CATEGORIES";
    public static final String CARD_REPORTS = "REPORTS";
    public static final String CARD_DATA_MANAGEMENT = "DATA_MANAGEMENT";

    private static final Set<String> VALID_CARDS = Set.of(
            CARD_DASHBOARD, CARD_WALLETS, CARD_TRANSACTIONS,
            CARD_TRANSFERS, CARD_CATEGORIES, CARD_REPORTS, CARD_DATA_MANAGEMENT);
    // endregion

    // region State
    private final WalletService walletService;
    private final TransactionService transactionService;

    private final WalletGUIController walletController;
    private final TransactionGUIController transactionController;
    private final TransferGUIController transferController;
    private final CategoryGUIController categoryController;
    private final ReportGUIController reportController;
    private final DataManagementGUIController dataManagementController;

    private String currentCard = CARD_DASHBOARD;
    // endregion

    // region Constructors
    public MainController(
            WalletService walletService,
            TransactionService transactionService,
            TransferService transferService,
            CategoryService categoryService,
            ReportService reportService,
            DataManagementService dataManagementService) {

        this(
                new WalletGUIController(
                        Objects.requireNonNull(walletService, "WalletService không được để null")),
                new TransactionGUIController(
                        Objects.requireNonNull(transactionService, "TransactionService không được để null")),
                new TransferGUIController(
                        Objects.requireNonNull(transferService, "TransferService không được để null")),
                new CategoryGUIController(
                        Objects.requireNonNull(categoryService, "CategoryService không được để null")),
                new ReportGUIController(
                        Objects.requireNonNull(reportService, "ReportService không được để null")),
                new DataManagementGUIController(
                        Objects.requireNonNull(dataManagementService, "DataManagementService không được để null")),
                walletService,
                transactionService);
    }

    public MainController(
            WalletGUIController walletController,
            TransactionGUIController transactionController,
            TransferGUIController transferController,
            CategoryGUIController categoryController,
            ReportGUIController reportController,
            DataManagementGUIController dataManagementController,
            WalletService walletService,
            TransactionService transactionService) {

        this.walletController = Objects.requireNonNull(
                walletController, "WalletGUIController không được để null");
        this.transactionController = Objects.requireNonNull(transactionController,
                "TransactionGUIController không được để null");
        this.transferController = Objects.requireNonNull(transferController,
                "TransferGUIController không được để null");
        this.categoryController = Objects.requireNonNull(categoryController,
                "CategoryGUIController không được để null");
        this.reportController = Objects.requireNonNull(
                reportController, "ReportGUIController không được để null");
        this.dataManagementController = Objects.requireNonNull(dataManagementController,
                "DataManagementGUIController không được để null");
        this.walletService = Objects.requireNonNull(
                walletService, "WalletService không được để null");
        this.transactionService = Objects.requireNonNull(
                transactionService, "TransactionService không được để null");
    }
    // endregion

    // region Operations
    public void initializeSystem() {
        this.walletService.initializeDefaultWallet();
    }

    public String navigateTo(String cardName) {
        Preconditions.checkArgument(
                cardName != null && !cardName.trim().isEmpty(),
                "Tên thẻ màn hình không được để trống");

        String normalized = cardName.trim().toUpperCase(Locale.ROOT);
        Preconditions.checkArgument(
                VALID_CARDS.contains(normalized),
                "Thẻ màn hình không hợp lệ: " + cardName);

        this.currentCard = normalized;
        return this.currentCard;
    }

    public String getCurrentCard() {
        return this.currentCard;
    }

    public WalletsSummaryResponseDTO loadDashboardWallets() {
        return this.walletService.getWalletsSummary();
    }

    public TransactionListResponseDTO loadRecentTransactions(int limit) {
        int pageSize = (limit <= 0) ? 5 : limit;
        FilterTransactionsRequestDTO filter = new FilterTransactionsRequestDTO(
                null, null, null, null, null, "", 1, pageSize);
        return this.transactionService.filterTransactions(filter);
    }
    // endregion

    // region Getters
    public WalletGUIController getWalletController() {
        return this.walletController;
    }

    public TransactionGUIController getTransactionController() {
        return this.transactionController;
    }

    public TransferGUIController getTransferController() {
        return this.transferController;
    }

    public CategoryGUIController getCategoryController() {
        return this.categoryController;
    }

    public ReportGUIController getReportController() {
        return this.reportController;
    }

    public DataManagementGUIController getDataManagementController() {
        return this.dataManagementController;
    }
    // endregion
}
