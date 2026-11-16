package com.pfms;

import com.pfms.domain.repository.BackupRepository;
import com.pfms.domain.repository.BalanceAdjustmentRepository;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.infrastructure.file.FileBackupRepository;
import com.pfms.infrastructure.file.JsonBalanceAdjustmentRepository;
import com.pfms.infrastructure.file.JsonCategoryRepository;
import com.pfms.infrastructure.file.JsonNeedGroupRepository;
import com.pfms.infrastructure.file.JsonTransactionRepository;
import com.pfms.infrastructure.file.JsonWalletRepository;
import com.pfms.infrastructure.file.support.JsonDataStore;
import com.pfms.presentation.common.MainController;
import com.pfms.presentation.common.MainStage;
import com.pfms.service.CategoryService;
import com.pfms.service.DataManagementService;
import com.pfms.service.ReportService;
import com.pfms.service.TransactionService;
import com.pfms.service.TransferService;
import com.pfms.service.WalletService;
import com.pfms.service.impl.category.CategoryServiceImpl;
import com.pfms.service.impl.data.DataManagementServiceImpl;
import com.pfms.service.impl.report.ReportServiceImpl;
import com.pfms.service.impl.transaction.TransactionServiceImpl;
import com.pfms.service.impl.transfer.TransferServiceImpl;
import com.pfms.service.impl.wallet.WalletServiceImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Điểm khởi chạy ứng dụng máy tính để bàn PFMS (Personal Finance Management System).
 * Hỗ trợ khởi chạy trên nền tảng JavaFX 17 (OpenJFX).
 */
public class App extends Application {

    static {
        // Tắt cảnh báo runtime của OpenJFX khi chạy trên Classpath (unnamed module)
        java.util.logging.Logger.getLogger("javafx").setLevel(java.util.logging.Level.SEVERE);
        java.util.logging.Logger.getLogger("com.sun.javafx").setLevel(java.util.logging.Level.SEVERE);
    }

    // region Fields
    public static final String DEFAULT_DATA_PATH = "data/pfms_data.json";
    // endregion

    // region JavaFX Application Bootstrap
    @Override
    public void start(Stage primaryStage) {
        Path storagePath = Paths.get(DEFAULT_DATA_PATH);
        MainStage stage = createFxApplication(storagePath, primaryStage);
        stage.show();
    }

    public static MainStage createFxApplication(Path storagePath, Stage primaryStage) {
        Objects.requireNonNull(storagePath, "Đường dẫn tệp lưu trữ không được để null");
        Objects.requireNonNull(primaryStage, "Stage không được để null");

        MainController mainController = buildMainController(storagePath);
        MainStage mainStage = new MainStage(primaryStage, mainController);

        // Đăng ký các phân hệ JavaFX hoàn thiện
        com.pfms.presentation.dashboard.DashboardPane dashboardPane = new com.pfms.presentation.dashboard.DashboardPane(
                mainController.getWalletController(),
                mainController.getTransactionController(),
                mainController.getReportController());
        mainStage.registerView(MainController.CARD_DASHBOARD, dashboardPane);

        com.pfms.presentation.wallet.WalletPane walletPane = new com.pfms.presentation.wallet.WalletPane(mainController.getWalletController());
        mainStage.registerView(MainController.CARD_WALLETS, walletPane);

        com.pfms.presentation.category.CategoryPane categoryPane = new com.pfms.presentation.category.CategoryPane(mainController.getCategoryController());
        mainStage.registerView(MainController.CARD_CATEGORIES, categoryPane);

        com.pfms.presentation.transfer.TransferPane transferPane = new com.pfms.presentation.transfer.TransferPane(
                mainController.getTransferController(),
                mainController.getWalletController(),
                mainController.getCategoryController());
        mainStage.registerView(MainController.CARD_TRANSFERS, transferPane);

        com.pfms.presentation.transaction.TransactionPane transactionPane = new com.pfms.presentation.transaction.TransactionPane(
                mainController.getTransactionController(),
                mainController.getWalletController(),
                mainController.getCategoryController());
        mainStage.registerView(MainController.CARD_TRANSACTIONS, transactionPane);

        com.pfms.presentation.report.ReportPane reportPane = new com.pfms.presentation.report.ReportPane(
                mainController.getReportController());
        mainStage.registerView(MainController.CARD_REPORTS, reportPane);

        com.pfms.presentation.data.DataManagementPane dataPane = new com.pfms.presentation.data.DataManagementPane(
                mainController.getDataManagementController(), storagePath.toString());
        mainStage.registerView(MainController.CARD_DATA_MANAGEMENT, dataPane);

        return mainStage;
    }
    // endregion

    // region Shared Controller Builder
    public static MainController buildMainController(Path storagePath) {
        Objects.requireNonNull(storagePath, "Đường dẫn tệp lưu trữ không được để null");

        JsonDataStore dataStore = new JsonDataStore(storagePath);

        WalletRepository walletRepo = new JsonWalletRepository(dataStore);
        NeedGroupRepository needGroupRepo = new JsonNeedGroupRepository(dataStore);
        CategoryRepository categoryRepo = new JsonCategoryRepository(dataStore);
        TransactionRepository transactionRepo = new JsonTransactionRepository(dataStore);
        BalanceAdjustmentRepository balanceAdjustmentRepo = new JsonBalanceAdjustmentRepository(dataStore);
        BackupRepository backupRepo = new FileBackupRepository();

        WalletService walletService = new WalletServiceImpl(walletRepo, balanceAdjustmentRepo, transactionRepo);
        CategoryService categoryService = new CategoryServiceImpl(categoryRepo, needGroupRepo, transactionRepo);
        TransactionService transactionService = new TransactionServiceImpl(transactionRepo, walletRepo, categoryRepo);
        TransferService transferService = new TransferServiceImpl(transactionRepo, walletRepo, categoryRepo);
        ReportService reportService = new ReportServiceImpl(transactionRepo, categoryRepo, needGroupRepo);
        DataManagementService dataManagementService = new DataManagementServiceImpl(
                walletRepo, transactionRepo, categoryRepo, needGroupRepo, balanceAdjustmentRepo, backupRepo);

        MainController mainController = new MainController(
                walletService, transactionService, transferService,
                categoryService, reportService, dataManagementService);

        mainController.initializeSystem();
        return mainController;
    }
    // endregion

    public static void main(String[] args) {
        launch(args);
    }
}
