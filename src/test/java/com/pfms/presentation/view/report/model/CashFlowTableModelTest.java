package com.pfms.presentation.view.report.model;

import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CashFlowTableModel - Mô hình bảng dữ liệu dòng tiền 12 tháng")
class CashFlowTableModelTest {

    private CashFlowTableModel model;

    @BeforeEach
    void setUp() {
        model = new CashFlowTableModel();
    }

    @Test
    @DisplayName("Khởi tạo mô hình rỗng có đúng 5 cột dữ liệu")
    void testInitialEmptyModel() {
        assertEquals(0, model.getRowCount());
        assertEquals(5, model.getColumnCount());
        assertEquals("Tháng", model.getColumnName(0));
        assertEquals("Thu nhập", model.getColumnName(1));
        assertEquals("Chi tiêu", model.getColumnName(2));
        assertEquals("Dòng tiền thuần", model.getColumnName(3));
        assertEquals("Tỷ lệ tiết kiệm", model.getColumnName(4));
    }

    @Test
    @DisplayName("Nạp danh sách dữ liệu dòng tiền và kết xuất định dạng tiền tệ và phần trăm chính xác")
    void testSetMonthsAndFormatting() {
        MonthlyCashFlowResponseDTO m1 = new MonthlyCashFlowResponseDTO(
                "2026-10",
                new BigDecimal("20000000"),
                new BigDecimal("12000000"),
                new BigDecimal("8000000"),
                new BigDecimal("40.0"));

        MonthlyCashFlowResponseDTO m2 = new MonthlyCashFlowResponseDTO(
                "2026-11",
                new BigDecimal("15000000"),
                new BigDecimal("18000000"),
                new BigDecimal("-3000000"),
                new BigDecimal("-20.0"));

        model.setMonths(List.of(m1, m2));

        assertEquals(2, model.getRowCount());
        assertEquals("2026-10", model.getValueAt(0, 0));
        assertEquals("20,000,000 VNĐ", model.getValueAt(0, 1));
        assertEquals("12,000,000 VNĐ", model.getValueAt(0, 2));
        assertEquals("8,000,000 VNĐ", model.getValueAt(0, 3));
        assertEquals("40.0%", model.getValueAt(0, 4));

        assertEquals("2026-11", model.getValueAt(1, 0));
        assertEquals("-3,000,000 VNĐ", model.getValueAt(1, 3));
        assertEquals("-20.0%", model.getValueAt(1, 4));
    }

    @Test
    @DisplayName("Trả về null hoặc rỗng khi truy xuất chỉ mục ngoài biên")
    void testIndexOutOfBoundsSafety() {
        assertNull(model.getValueAt(-1, 0));
        assertNull(model.getValueAt(0, 0));
        assertNull(model.getValueAt(0, 10));
        assertEquals("", model.getColumnName(-1));
        assertEquals("", model.getColumnName(10));
    }

    @Test
    @DisplayName("Xóa sạch dữ liệu khi gọi clear")
    void testClearData() {
        MonthlyCashFlowResponseDTO m1 = new MonthlyCashFlowResponseDTO("2026-10");
        model.setMonths(List.of(m1));
        assertEquals(1, model.getRowCount());

        model.clear();
        assertEquals(0, model.getRowCount());
    }
}
