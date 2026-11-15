package com.pfms.presentation.view.data.model;

import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử ReconciliationIssueTableModel - Mô hình bảng hiển thị sự cố đối soát dữ liệu")
class ReconciliationIssueTableModelTest {

    private ReconciliationIssueTableModel model;

    @BeforeEach
    void setUp() {
        model = new ReconciliationIssueTableModel();
    }

    @Test
    @DisplayName("Khởi tạo mô hình rỗng có đúng 5 cột dữ liệu")
    void testInitialEmptyModel() {
        assertEquals(0, model.getRowCount());
        assertEquals(5, model.getColumnCount());
        assertEquals("Mức độ", model.getColumnName(0));
        assertEquals("Mã quy tắc", model.getColumnName(1));
        assertEquals("Loại vấn đề", model.getColumnName(2));
        assertEquals("Chi tiết sai lệch", model.getColumnName(3));
        assertEquals("Khuyến nghị", model.getColumnName(4));
    }

    @Test
    @DisplayName("Nạp danh sách sự cố và hiển thị chính xác các trường dữ liệu")
    void testSetIssuesAndValues() {
        ReconciliationIssueResponseDTO issue1 = new ReconciliationIssueResponseDTO(
                "ERROR", "RULE-BAL-01", "Sai lệch số dư ví", "Ví MB Bank lệch 50,000 VNĐ", "Kiểm kê lại số dư");

        ReconciliationIssueResponseDTO issue2 = new ReconciliationIssueResponseDTO(
                "WARNING", "RULE-CAT-01", "Danh mục mồ côi", "Danh mục không có nhóm cha", "Gán nhóm cha");

        model.setIssues(List.of(issue1, issue2));

        assertEquals(2, model.getRowCount());
        assertEquals("ERROR", model.getValueAt(0, 0));
        assertEquals("RULE-BAL-01", model.getValueAt(0, 1));
        assertEquals("Sai lệch số dư ví", model.getValueAt(0, 2));
        assertEquals("Ví MB Bank lệch 50,000 VNĐ", model.getValueAt(0, 3));
        assertEquals("Kiểm kê lại số dư", model.getValueAt(0, 4));

        assertEquals("WARNING", model.getValueAt(1, 0));
        assertSame(issue1, model.getIssueAt(0));
        assertSame(issue2, model.getIssueAt(1));
    }

    @Test
    @DisplayName("An toàn khi truy xuất chỉ mục ngoài biên và xóa sạch khi clear")
    void testBoundsAndClear() {
        assertNull(model.getValueAt(-1, 0));
        assertNull(model.getValueAt(0, 0));
        assertNull(model.getIssueAt(-1));
        assertNull(model.getIssueAt(5));
        assertEquals("", model.getColumnName(-1));
        assertEquals("", model.getColumnName(10));

        ReconciliationIssueResponseDTO issue = new ReconciliationIssueResponseDTO(
                "INFO", "RULE-01", "Thông tin", "Tin nhắn", "Khuyến nghị");
        model.setIssues(List.of(issue));
        assertEquals(1, model.getRowCount());

        model.clear();
        assertEquals(0, model.getRowCount());
    }
}
