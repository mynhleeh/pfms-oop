package com.pfms.presentation.view;

import com.pfms.presentation.controller.MainController;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.view.category.CategoryPanel;
import com.pfms.presentation.view.common.ViewConstants;
import com.pfms.presentation.view.component.SidebarPanel;
import com.pfms.presentation.view.component.StatusBarPanel;
import com.pfms.presentation.view.dashboard.DashboardPanel;
import com.pfms.presentation.view.data.DataManagementPanel;
import com.pfms.presentation.view.report.ReportPanel;
import com.pfms.presentation.view.transaction.TransactionPanel;
import com.pfms.presentation.view.transfer.TransferPanel;
import com.pfms.presentation.view.wallet.WalletPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Cửa sổ chính (MainFrame) của ứng dụng đồ họa PFMS (1024x680).
 * Bố cục ba vùng BorderLayout: SidebarPanel (WEST), contentPanel (CENTER, CardLayout), StatusBarPanel (SOUTH).
 */
public class MainFrame extends JFrame {

    // region Fields
    private final MainController mainController;
    private final SidebarPanel sidebarPanel;
    private final StatusBarPanel statusBarPanel;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private String currentCardName = MainController.CARD_DASHBOARD;
    private DashboardPanel dashboardPanel;
    private WalletPanel walletPanel;
    private TransactionPanel transactionPanel;
    private TransferPanel transferPanel;
    private CategoryPanel categoryPanel;
    private ReportPanel reportPanel;
    private DataManagementPanel dataManagementPanel;
    // endregion

    // region Constructors
    public MainFrame(MainController mainController, String storagePath) {
        this.mainController = Objects.requireNonNull(mainController, "MainController không được để null");

        setTitle(ViewConstants.APP_TITLE);
        setSize(ViewConstants.APP_WIDTH, ViewConstants.APP_HEIGHT);
        setMinimumSize(new Dimension(ViewConstants.MIN_WIDTH, ViewConstants.MIN_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        this.sidebarPanel = new SidebarPanel(this::showCard);
        this.statusBarPanel = new StatusBarPanel(storagePath);

        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(this.cardLayout);
        this.contentPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        registerPanels(storagePath);

        add(this.sidebarPanel, BorderLayout.WEST);
        add(this.contentPanel, BorderLayout.CENTER);
        add(this.statusBarPanel, BorderLayout.SOUTH);

        refreshStatusMetrics();
    }
    // endregion

    // region UI
    private void registerPanels(String storagePath) {
        this.walletPanel = new WalletPanel(this.mainController.getWalletController());
        this.walletPanel.setOnWalletChangedListener(this::refreshStatusMetrics);

        this.transactionPanel = new TransactionPanel(
                this.mainController.getTransactionController(),
                this.mainController.getWalletController(),
                this.mainController.getCategoryController());
        this.transactionPanel.setOnTransactionChangedListener(this::refreshStatusMetrics);

        this.transferPanel = new TransferPanel(
                this.mainController.getTransferController(),
                this.mainController.getWalletController(),
                this.mainController.getCategoryController());
        this.transferPanel.setOnTransferChangedListener(this::refreshStatusMetrics);

        this.categoryPanel = new CategoryPanel(this.mainController.getCategoryController());
        this.reportPanel = new ReportPanel(this.mainController.getReportController());

        this.dataManagementPanel = new DataManagementPanel(
                this.mainController.getDataManagementController(), storagePath);
        this.dataManagementPanel.setOnDataRestoredListener(this::refreshAllSubsystems);

        this.dashboardPanel = new DashboardPanel(
                this.mainController.getWalletController(),
                this.mainController.getTransactionController(),
                this.mainController.getReportController());

        this.contentPanel.add(this.dashboardPanel, MainController.CARD_DASHBOARD);
        this.contentPanel.add(this.walletPanel, MainController.CARD_WALLETS);
        this.contentPanel.add(this.transactionPanel, MainController.CARD_TRANSACTIONS);
        this.contentPanel.add(this.transferPanel, MainController.CARD_TRANSFERS);
        this.contentPanel.add(this.categoryPanel, MainController.CARD_CATEGORIES);
        this.contentPanel.add(this.reportPanel, MainController.CARD_REPORTS);
        this.contentPanel.add(this.dataManagementPanel, MainController.CARD_DATA_MANAGEMENT);
    }
    // endregion

    // region Actions
    public void showCard(String cardName) {
        if (cardName == null || cardName.trim().isEmpty()) {
            return;
        }
        String targetCard = this.mainController.navigateTo(cardName);
        this.cardLayout.show(this.contentPanel, targetCard);
        this.currentCardName = targetCard;
        this.sidebarPanel.setActiveCard(targetCard);
        if (MainController.CARD_DASHBOARD.equals(targetCard) && this.dashboardPanel != null) {
            this.dashboardPanel.reloadDashboard();
        }
    }
    // endregion

    // region Data
    public void refreshStatusMetrics() {
        try {
            WalletsSummaryResponseDTO summary = this.mainController.loadDashboardWallets();
            BigDecimal total = (summary != null) ? summary.getTotalBalance() : BigDecimal.ZERO;
            String cur = (summary != null) ? summary.getCurrency() : ViewConstants.CURRENCY_DEFAULT;
            this.statusBarPanel.updateTotalBalance(total, cur);
        } catch (Exception ignored) {
            this.statusBarPanel.updateTotalBalance(BigDecimal.ZERO, ViewConstants.CURRENCY_DEFAULT);
        }
    }

    public void refreshAllSubsystems() {
        refreshStatusMetrics();
        if (this.dashboardPanel != null) {
            this.dashboardPanel.reloadDashboard();
        }
        if (this.walletPanel != null) {
            this.walletPanel.reloadWallets();
        }
        if (this.transactionPanel != null) {
            this.transactionPanel.reloadTransactions();
        }
        if (this.transferPanel != null) {
            this.transferPanel.reloadTransfers();
        }
        if (this.categoryPanel != null) {
            this.categoryPanel.reloadCategories();
        }
        if (this.reportPanel != null) {
            this.reportPanel.reloadReports();
        }
    }
    // endregion

    // region Getters
    public SidebarPanel getSidebarPanel() {
        return this.sidebarPanel;
    }

    public StatusBarPanel getStatusBarPanel() {
        return this.statusBarPanel;
    }

    public String getCurrentCardName() {
        return this.currentCardName;
    }

    public MainController getMainController() {
        return this.mainController;
    }

    public WalletPanel getWalletPanel() {
        return this.walletPanel;
    }

    public TransactionPanel getTransactionPanel() {
        return this.transactionPanel;
    }

    public TransferPanel getTransferPanel() {
        return this.transferPanel;
    }

    public CategoryPanel getCategoryPanel() {
        return this.categoryPanel;
    }

    public ReportPanel getReportPanel() {
        return this.reportPanel;
    }

    public DataManagementPanel getDataManagementPanel() {
        return this.dataManagementPanel;
    }

    public DashboardPanel getDashboardPanel() {
        return this.dashboardPanel;
    }
    // endregion
}
