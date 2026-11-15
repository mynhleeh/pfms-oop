package com.pfms.presentation.view.component;

import com.pfms.presentation.view.common.ViewConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử StatusBarPanel - Thanh trạng thái hệ thống")
class StatusBarPanelTest {

    private StatusBarPanel statusBar;

    @BeforeEach
    void setUp() {
        statusBar = new StatusBarPanel("data/pfms_data.json");
    }

    @Test
    @DisplayName("Khởi tạo hiển thị đúng đường dẫn tệp dữ liệu mặc định kèm tiền tố ●")
    void testInitialDataFilePath() {
        assertTrue(statusBar.getDataFilePathText().contains("data/pfms_data.json"));
        assertTrue(statusBar.getDataFilePathText().startsWith("● Tệp dữ liệu: "));
    }

    @Test
    @DisplayName("Cập nhật đường dẫn tệp dữ liệu hiển thị chính xác kèm tiền tố ●")
    void testUpdateDataFilePath() {
        statusBar.updateDataFilePath("custom/path/storage.json");
        assertTrue(statusBar.getDataFilePathText().contains("custom/path/storage.json"));
        assertTrue(statusBar.getDataFilePathText().startsWith("● Tệp dữ liệu: "));

        statusBar.updateDataFilePath(null);
        assertEquals("● Tệp dữ liệu: Chưa kết nối tệp", statusBar.getDataFilePathText());

        statusBar.updateDataFilePath("   ");
        assertEquals("● Tệp dữ liệu: Chưa kết nối tệp", statusBar.getDataFilePathText());
    }

    @Test
    @DisplayName("Cập nhật tổng số dư định dạng chuẩn tiền tệ Việt Nam (tương thích ngược)")
    void testUpdateTotalBalance() {
        statusBar.updateTotalBalance(new BigDecimal("82850000"), "VNĐ");
        assertTrue(statusBar.getTotalBalanceText().contains("82,850,000 VNĐ"));
    }

    @Test
    @DisplayName("Xử lý an toàn khi tổng số dư null hoặc bằng 0")
    void testUpdateTotalBalanceNullOrZero() {
        statusBar.updateTotalBalance(null, "VNĐ");
        assertTrue(statusBar.getTotalBalanceText().contains("0 VNĐ"));

        statusBar.updateTotalBalance(BigDecimal.ZERO, "VNĐ");
        assertTrue(statusBar.getTotalBalanceText().contains("0 VNĐ"));
    }

    @Test
    @DisplayName("Thanh trạng thái không thêm totalBalanceLabel vào layout hiển thị")
    void testTotalBalanceNotAddedToPanel() {
        // Chỉ chứa 1 thành phần duy nhất được add vào panel là dataPathLabel
        assertEquals(1, statusBar.getComponentCount());
        assertEquals(statusBar.getDataPathLabel(), statusBar.getComponent(0));

        // totalBalanceLabel tồn tại phục vụ tương thích ngược nhưng không có parent component
        assertNotNull(statusBar.getTotalBalanceLabel());
        assertNull(statusBar.getTotalBalanceLabel().getParent());
    }

    @Test
    @DisplayName("Nền và viền thanh trạng thái khớp chuẩn ViewConstants")
    void testStatusBarBackgroundAndBorder() {
        assertEquals(ViewConstants.COLOR_STATUS_BG, statusBar.getBackground());
        assertNotNull(statusBar.getBorder());
    }
}
