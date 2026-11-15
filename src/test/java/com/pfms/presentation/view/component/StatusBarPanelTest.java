package com.pfms.presentation.view.component;

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
    @DisplayName("Khởi tạo hiển thị đúng đường dẫn tệp dữ liệu mặc định")
    void testInitialDataFilePath() {
        assertTrue(statusBar.getDataFilePathText().contains("data/pfms_data.json"));
    }

    @Test
    @DisplayName("Cập nhật đường dẫn tệp dữ liệu hiển thị chính xác")
    void testUpdateDataFilePath() {
        statusBar.updateDataFilePath("custom/path/storage.json");
        assertTrue(statusBar.getDataFilePathText().contains("custom/path/storage.json"));
    }

    @Test
    @DisplayName("Cập nhật tổng số dư định dạng chuẩn tiền tệ Việt Nam")
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
}
