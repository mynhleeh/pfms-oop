package com.pfms.presentation.wallet;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.common.ViewConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử WalletPanel - Bảng điều khiển phân hệ ví")
class WalletPanelTest {

    private WalletGUIController walletController;
    private WalletResponseDTO wallet1;
    private WalletPanel panel;

    @BeforeEach
    void setUp() {
        walletController = Mockito.mock(WalletGUIController.class);
        wallet1 = new WalletResponseDTO(
                UUID.randomUUID(), "Ví MB Bank", new BigDecimal("15000000"), "VND", true, "ACTIVE", "Tài khoản chính", LocalDateTime.now());

        WalletsSummaryResponseDTO summary = new WalletsSummaryResponseDTO(
                List.of(wallet1), new BigDecimal("15000000"), "VND", Collections.emptyMap());
        when(walletController.loadWalletsSummary()).thenReturn(summary);
        when(walletController.loadWalletsSummary(any())).thenReturn(summary);

        panel = new WalletPanel(walletController);
    }

    @Test
    @DisplayName("Khởi tạo nạp danh sách ví ban đầu vào bảng thành công")
    void testInitialDataLoaded() {
        assertEquals(1, panel.getTableModel().getRowCount());
        assertEquals("Ví MB Bank", panel.getTableModel().getValueAt(0, 1));
        assertTrue(panel.getSummaryText().contains("Tổng số ví: 1"));
        assertNotNull(panel.getTable());
    }

    @Test
    @DisplayName("Kích hoạt thay đổi bộ lọc trạng thái gọi lại controller")
    void testFilterStatusChanged() {
        panel.setSelectedStatusFilter("ARCHIVED");
        verify(walletController).loadWalletsSummary("ARCHIVED");
    }

    @Test
    @DisplayName("Kích hoạt callback onWalletChanged khi có thao tác làm mới")
    void testWalletChangedCallback() {
        AtomicBoolean triggered = new AtomicBoolean(false);
        panel.setOnWalletChangedListener(() -> triggered.set(true));

        panel.reloadWallets();
        assertTrue(triggered.get());
    }

    @Test
    @DisplayName("Ném NullPointerException khi WalletGUIController bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new WalletPanel(null));
    }

    @Test
    @DisplayName("Giao diện tổng thể tuân thủ bảng màu Luxury Black & Gold")
    void testOverallStylingTheme() {
        assertEquals(ViewConstants.COLOR_CONTENT_BG, panel.getBackground());
        assertEquals(30, panel.getTable().getRowHeight());
        assertEquals(ViewConstants.COLOR_STATUS_BG, panel.getBottomPanel().getBackground());
        assertEquals(ViewConstants.COLOR_TEXT_MUTED, panel.getSummaryLabel().getForeground());
    }

    @Test
    @DisplayName("Nút Thêm ví mới mang màu Royal Gold chữ đen và font FONT_NAV_ACTIVE")
    void testAddButtonStyling() {
        assertEquals(ViewConstants.COLOR_PRIMARY, panel.getAddButton().getBackground());
        assertEquals(Color.BLACK, panel.getAddButton().getForeground());
        assertEquals(ViewConstants.FONT_NAV_ACTIVE, panel.getAddButton().getFont());
    }

    @Test
    @DisplayName("Các nút công cụ (Sửa, Kiểm kê, Lưu trữ) mang màu tối viền kim loại")
    void testToolButtonsStyling() {
        assertEquals(ViewConstants.COLOR_CARD_BG, panel.getEditButton().getBackground());
        assertEquals(ViewConstants.COLOR_TEXT_MAIN, panel.getEditButton().getForeground());

        assertEquals(ViewConstants.COLOR_CARD_BG, panel.getAdjustButton().getBackground());
        assertEquals(ViewConstants.COLOR_TEXT_MAIN, panel.getAdjustButton().getForeground());

        assertEquals(ViewConstants.COLOR_CARD_BG, panel.getToggleStatusButton().getBackground());
        assertEquals(ViewConstants.COLOR_TEXT_MAIN, panel.getToggleStatusButton().getForeground());
    }

    @Test
    @DisplayName("Nút Xóa ví mang màu cảnh báo Pastel Rose Coral")
    void testDeleteButtonStyling() {
        assertEquals(ViewConstants.COLOR_CARD_BG, panel.getDeleteButton().getBackground());
        assertEquals(ViewConstants.COLOR_EXPENSE, panel.getDeleteButton().getForeground());
    }

    @Test
    @DisplayName("Bộ lọc trạng thái statusFilterBox đồng bộ bảng màu nền tối")
    void testStatusFilterBoxStyling() {
        assertEquals(ViewConstants.FONT_BODY, panel.getStatusFilterBox().getFont());
        assertEquals(ViewConstants.COLOR_CARD_BG, panel.getStatusFilterBox().getBackground());
        assertEquals(ViewConstants.COLOR_TEXT_MAIN, panel.getStatusFilterBox().getForeground());
    }

    @Test
    @DisplayName("Tiêu đề bảng (Header) có màu nền tối và chữ Champagne Gold")
    void testTableHeaderStyling() {
        assertEquals(new Color(14, 14, 14), panel.getTable().getTableHeader().getBackground());
        assertEquals(ViewConstants.COLOR_PRIMARY_LIGHT, panel.getTable().getTableHeader().getForeground());
        assertEquals(ViewConstants.FONT_NAV_ACTIVE, panel.getTable().getTableHeader().getFont());

        TableCellRenderer headerRenderer = panel.getTable().getTableHeader().getDefaultRenderer();
        assertNotNull(headerRenderer);
        Component headerComp = headerRenderer.getTableCellRendererComponent(
                panel.getTable(), "Số dư khả dụng", false, false, -1, 2);
        assertEquals(new Color(14, 14, 14), headerComp.getBackground());
        assertEquals(ViewConstants.COLOR_PRIMARY_LIGHT, headerComp.getForeground());
        assertEquals(ViewConstants.FONT_NAV_ACTIVE, headerComp.getFont());
    }

    @Test
    @DisplayName("Cột 2 (Số dư khả dụng) hiển thị màu Champagne Gold và căn phải")
    void testBalanceColumnRenderer() {
        TableCellRenderer renderer = panel.getTable().getColumnModel().getColumn(2).getCellRenderer();
        assertNotNull(renderer);

        Component comp = renderer.getTableCellRendererComponent(
                panel.getTable(), "15,000,000 VNĐ", false, false, 0, 2);
        assertTrue(comp instanceof JLabel);
        JLabel label = (JLabel) comp;
        assertEquals(ViewConstants.COLOR_PRIMARY_LIGHT, label.getForeground());
        assertEquals(SwingConstants.RIGHT, label.getHorizontalAlignment());
    }

    @Test
    @DisplayName("Cột 6 (Ghi chú) hiển thị màu Zinc Muted")
    void testNoteColumnRenderer() {
        TableCellRenderer renderer = panel.getTable().getColumnModel().getColumn(6).getCellRenderer();
        assertNotNull(renderer);

        Component comp = renderer.getTableCellRendererComponent(
                panel.getTable(), "Tài khoản chính", false, false, 0, 6);
        assertTrue(comp instanceof JLabel);
        JLabel label = (JLabel) comp;
        assertEquals(ViewConstants.COLOR_TEXT_MUTED, label.getForeground());
    }
}
