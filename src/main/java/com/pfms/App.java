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
import com.pfms.presentation.controller.MainController;
import com.pfms.presentation.view.MainFrame;
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

import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Điểm khởi chạy ứng dụng máy tính để bàn PFMS (Personal Finance Management
 * System).
 * Thiết lập chuỗi tiêm phụ thuộc toàn hệ thống và hiển thị cửa sổ chính trên
 * luồng Swing EDT.
 */
public final class App {

    public static final String DEFAULT_DATA_PATH = "data/pfms_data.json";

    private App() {
        // Chặn khởi tạo trực tiếp
    }

    public static MainFrame createApplication(Path storagePath) {
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

        setupLookAndFeel();
        return new MainFrame(mainController, storagePath.toString());
    }

    public static void setupLookAndFeel() {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
            javax.swing.UIManager.put("Component.arc", 8);
            javax.swing.UIManager.put("Button.arc", 8);
            javax.swing.UIManager.put("TextComponent.arc", 8);
            javax.swing.UIManager.put("ScrollBar.thumbArc", 6);
            javax.swing.UIManager.put("ScrollBar.width", 10);
            javax.swing.UIManager.put("Table.rowHeight", 30);
            javax.swing.UIManager.put("Table.showHorizontalLines", true);
            javax.swing.UIManager.put("Table.showVerticalLines", false);
            javax.swing.UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 1));
            javax.swing.UIManager.put("TitlePane.useWindowDecorations", true);
        } catch (Exception ignored) {
            // An toàn trong môi trường test/headless
        }
    }

    public static void main(String[] args) {
        setupLookAndFeel();
        Path storagePath = Paths.get(DEFAULT_DATA_PATH);
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = createApplication(storagePath);
            frame.setVisible(true);
        });
    }
}
