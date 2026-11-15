package com.pfms.presentation.view.dashboard;

import com.pfms.presentation.controller.ReportGUIController;
import com.pfms.presentation.controller.TransactionGUIController;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.view.common.ViewConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Container;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử DashboardPanel - Bảng điều khiển tổng quan tài chính")
class DashboardPanelTest {

    private WalletGUIController walletController;
    private TransactionGUIController transactionController;
    private ReportGUIController reportController;

    private DashboardPanel dashboardPanel;

    @BeforeEach
    void setUp() {
        walletController = Mockito.mock(WalletGUIController.class);
        transactionController = Mockito.mock(TransactionGUIController.class);
        reportController = Mockito.mock(ReportGUIController.class);

        WalletsSummaryResponseDTO emptySummary = new WalletsSummaryResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, "VNĐ", Collections.emptyMap());
        when(walletController.getWalletsSummary()).thenReturn(emptySummary);
        when(transactionController.filterTransactions(any())).thenReturn(new TransactionListResponseDTO());

        dashboardPanel = new DashboardPanel(walletController, transactionController, reportController);
    }

    @Test
    @DisplayName("Ném NullPointerException khi các controller bị null trong constructor")
    void testConstructorNullValidation() {
        assertThrows(NullPointerException.class, () -> new DashboardPanel(null, transactionController, reportController));
        assertThrows(NullPointerException.class, () -> new DashboardPanel(walletController, null, reportController));
        assertThrows(NullPointerException.class, () -> new DashboardPanel(walletController, transactionController, null));
    }

    @Test
    @DisplayName("Khởi tạo bảng điều khiển với màu nền, tiêu đề, và không có nút ghi nhanh")
    void testInitialStylingAndHeader() {
        assertEquals(ViewConstants.COLOR_CONTENT_BG, dashboardPanel.getBackground());

        JLabel headerTitle = dashboardPanel.getHeaderTitleLabel();
        assertNotNull(headerTitle);
        assertEquals("Bảng Điều Khiển Tổng Quan", headerTitle.getText());
        assertEquals(ViewConstants.COLOR_TEXT_MAIN, headerTitle.getForeground());
        assertEquals(ViewConstants.FONT_PANEL_HEADER, headerTitle.getFont());

        String currentCycle = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        JLabel subtitleLabel = dashboardPanel.getCycleSubtitleLabel();
        assertNotNull(subtitleLabel);
        assertTrue(subtitleLabel.getText().contains(currentCycle));

        // Tuyệt đối KHÔNG có nút 'Ghi giao dịch nhanh'
        List<JButton> buttons = findButtons(dashboardPanel);
        for (JButton btn : buttons) {
            String text = btn.getText();
            assertFalse(text != null && text.contains("Ghi giao dịch nhanh"),
                    "Bảng điều khiển không được chứa nút 'Ghi giao dịch nhanh'");
        }
    }

    @Test
    @DisplayName("4 Thẻ KPI Card có màu sắc, nhãn và viền đơn sắc chuẩn")
    void testKpiCardsStyling() {
        assertNotNull(dashboardPanel.getTotalBalanceValueLabel());
        assertNotNull(dashboardPanel.getIncomeValueLabel());
        assertNotNull(dashboardPanel.getExpenseValueLabel());
        assertNotNull(dashboardPanel.getSavingsRateValueLabel());

        assertEquals(ViewConstants.COLOR_INCOME, dashboardPanel.getIncomeValueLabel().getForeground());
        assertEquals(ViewConstants.COLOR_EXPENSE, dashboardPanel.getExpenseValueLabel().getForeground());
        assertEquals(ViewConstants.COLOR_PRIMARY_LIGHT, dashboardPanel.getSavingsRateValueLabel().getForeground());

        assertNotNull(dashboardPanel.getTotalBalanceSubtextLabel());
        assertNotNull(dashboardPanel.getIncomeSubtextLabel());
        assertNotNull(dashboardPanel.getExpenseSubtextLabel());
        assertNotNull(dashboardPanel.getSavingsRateSubtextLabel());

        assertTrue(dashboardPanel.getTotalBalanceValueLabel().getParent() instanceof com.pfms.presentation.view.component.RoundedCardPanel,
                "Thẻ KPI phải là RoundedCardPanel để bo góc mềm mại");
        com.pfms.presentation.view.component.RoundedCardPanel card =
                (com.pfms.presentation.view.component.RoundedCardPanel) dashboardPanel.getTotalBalanceValueLabel().getParent();
        assertEquals(10, card.getArc(), "Độ bo góc thẻ KPI phải là 10px");
        assertEquals(ViewConstants.COLOR_BORDER, card.getBorderColor(), "Màu viền thẻ KPI phải là COLOR_BORDER");
    }

    @Test
    @DisplayName("Bảng giao dịch gần nhất cấu hình đúng số cột, tiêu đề, chiều cao dòng và renderer")
    void testRecentTableStructureAndRenderer() {
        JTable table = dashboardPanel.getRecentTable();
        assertNotNull(table);
        assertEquals(5, table.getColumnCount());
        assertEquals("THỜI GIAN", table.getColumnName(0));
        assertEquals("VÍ TÀI CHÍNH", table.getColumnName(1));
        assertEquals("DANH MỤC", table.getColumnName(2));
        assertEquals("GHI CHÚ", table.getColumnName(3));
        assertEquals("SỐ TIỀN (VNĐ)", table.getColumnName(4));
        assertEquals(30, table.getRowHeight());

        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        assertNotNull(headerRenderer);
        Component headerComp = headerRenderer.getTableCellRendererComponent(
                table, "THỜI GIAN", false, false, -1, 0);
        assertEquals(new java.awt.Color(14, 14, 14), headerComp.getBackground());
        assertEquals(ViewConstants.COLOR_PRIMARY_LIGHT, headerComp.getForeground());
    }

    @Test
    @DisplayName("reloadDashboard cập nhật chính xác số liệu tổng hợp 4 thẻ KPI và bảng giao dịch")
    void testReloadDashboardWithData() {
        UUID wallet1Id = UUID.randomUUID();
        UUID wallet2Id = UUID.randomUUID();
        WalletResponseDTO w1 = new WalletResponseDTO(
                wallet1Id, "Ví Chính", new BigDecimal("5000000"), "VNĐ", true, "ACTIVE", "", LocalDateTime.now());
        WalletResponseDTO w2 = new WalletResponseDTO(
                wallet2Id, "Ví Phụ", new BigDecimal("2000000"), "VNĐ", false, "ARCHIVED", "", LocalDateTime.now());
        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                List.of(w1, w2), new BigDecimal("7000000"), "VNĐ", Map.of(wallet1Id, BigDecimal.valueOf(71.4), wallet2Id, BigDecimal.valueOf(28.6)));
        when(walletController.getWalletsSummary()).thenReturn(summary);

        UUID cat1 = UUID.randomUUID();
        UUID cat2 = UUID.randomUUID();
        TransactionResponseDTO incomeTxn = new TransactionResponseDTO(
                UUID.randomUUID(), wallet1Id, "Ví Chính", cat1, "Lương",
                new BigDecimal("20000000"), "INCOME", LocalDate.now(), "Lương tháng",
                new BigDecimal("25000000"), LocalDateTime.now());
        TransactionResponseDTO expenseTxn = new TransactionResponseDTO(
                UUID.randomUUID(), wallet1Id, "Ví Chính", cat2, "Tiền nhà",
                new BigDecimal("8000000"), "EXPENSE", LocalDate.now(), "Thuê nhà",
                new BigDecimal("17000000"), LocalDateTime.now());

        TransactionListResponseDTO txnResponse = new TransactionListResponseDTO(
                List.of(incomeTxn, expenseTxn),
                new BigDecimal("20000000"),
                new BigDecimal("8000000"),
                new BigDecimal("12000000"),
                1, 50, 2, 1);
        when(transactionController.filterTransactions(any())).thenReturn(txnResponse);

        dashboardPanel.reloadDashboard();

        // Kiểm tra Thẻ 1: Tổng số dư khả dụng
        assertTrue(dashboardPanel.getTotalBalanceValueLabel().getText().contains("7,000,000"));
        assertTrue(dashboardPanel.getTotalBalanceSubtextLabel().getText().contains("1 ví"));

        // Kiểm tra Thẻ 2: Thu nhập trong tháng
        assertTrue(dashboardPanel.getIncomeValueLabel().getText().contains("20,000,000"));
        assertTrue(dashboardPanel.getIncomeSubtextLabel().getText().contains("1 giao dịch thu"));

        // Kiểm tra Thẻ 3: Chi tiêu trong tháng
        assertTrue(dashboardPanel.getExpenseValueLabel().getText().contains("8,000,000"));
        assertTrue(dashboardPanel.getExpenseSubtextLabel().getText().contains("40.0%"));

        // Kiểm tra Thẻ 4: Tỷ lệ tiết kiệm ròng: (12M / 20M) * 100 = 60.0%
        assertTrue(dashboardPanel.getSavingsRateValueLabel().getText().contains("60.0%"));
        assertTrue(dashboardPanel.getSavingsRateSubtextLabel().getText().contains("> 30%"));

        // Kiểm tra bảng giao dịch gần nhất
        JTable table = dashboardPanel.getRecentTable();
        assertEquals(2, table.getRowCount());
        assertEquals("+20,000,000", table.getValueAt(0, 4));
        assertEquals("-8,000,000", table.getValueAt(1, 4));

        // Kiểm tra màu renderer cho cột số tiền
        TableCellRenderer amountRenderer = table.getColumnModel().getColumn(4).getCellRenderer();
        Component compIncome = amountRenderer.getTableCellRendererComponent(table, table.getValueAt(0, 4), false, false, 0, 4);
        assertEquals(ViewConstants.COLOR_INCOME, compIncome.getForeground());

        Component compExpense = amountRenderer.getTableCellRendererComponent(table, table.getValueAt(1, 4), false, false, 1, 4);
        assertEquals(ViewConstants.COLOR_EXPENSE, compExpense.getForeground());
    }

    @Test
    @DisplayName("Xử lý an toàn khi thu nhập bằng 0 tránh chia cho 0")
    void testZeroIncomeDivisionSafeguard() {
        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, "VNĐ", Collections.emptyMap());
        when(walletController.getWalletsSummary()).thenReturn(summary);

        TransactionListResponseDTO emptyTxns = new TransactionListResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 50, 0, 0);
        when(transactionController.filterTransactions(any())).thenReturn(emptyTxns);

        assertDoesNotThrow(() -> dashboardPanel.reloadDashboard());

        assertTrue(dashboardPanel.getTotalBalanceValueLabel().getText().contains("0"));
        assertTrue(dashboardPanel.getIncomeValueLabel().getText().contains("0"));
        assertTrue(dashboardPanel.getExpenseValueLabel().getText().contains("0"));
        assertTrue(dashboardPanel.getSavingsRateValueLabel().getText().contains("0.0%"));
    }

    private List<JButton> findButtons(Container container) {
        List<JButton> result = new ArrayList<>();
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton btn) {
                result.add(btn);
            } else if (comp instanceof Container childContainer) {
                result.addAll(findButtons(childContainer));
            }
        }
        return result;
    }
}
