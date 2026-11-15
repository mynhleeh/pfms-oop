package com.pfms.presentation.report.model;

import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử SpendingStructureTableModel - Mô hình bảng phân tích cơ cấu thu chi")
class SpendingStructureTableModelTest {

    private SpendingStructureTableModel model;

    @BeforeEach
    void setUp() {
        model = new SpendingStructureTableModel();
    }

    @Test
    @DisplayName("Khởi tạo mô hình rỗng có đúng 6 cột dữ liệu")
    void testInitialEmptyModel() {
        assertEquals(0, model.getRowCount());
        assertEquals(6, model.getColumnCount());
        assertEquals("Nhóm Nhu cầu", model.getColumnName(0));
        assertEquals("Số tiền", model.getColumnName(1));
        assertEquals("Tỷ trọng", model.getColumnName(2));
        assertEquals("Kỳ trước", model.getColumnName(3));
        assertEquals("Chênh lệch", model.getColumnName(4));
        assertEquals("% Thay đổi", model.getColumnName(5));
    }

    @Test
    @DisplayName("Nạp danh sách nhóm nhu cầu và kết xuất chính xác các giá trị so sánh kỳ")
    void testSetGroupsAndFormatting() {
        NeedGroupShareResponseDTO g1 = new NeedGroupShareResponseDTO(
                UUID.randomUUID(),
                "Chi tiêu thiết yếu",
                "ACTIVE",
                new BigDecimal("6000000"),
                new BigDecimal("60.0"),
                new BigDecimal("5000000"),
                new BigDecimal("1000000"),
                new BigDecimal("20.0"),
                false,
                Collections.emptyList());

        NeedGroupShareResponseDTO g2 = new NeedGroupShareResponseDTO(
                UUID.randomUUID(),
                "Hưởng thụ cá nhân",
                "ACTIVE",
                new BigDecimal("4000000"),
                new BigDecimal("40.0"),
                BigDecimal.ZERO,
                new BigDecimal("4000000"),
                BigDecimal.ZERO,
                true,
                Collections.emptyList());

        model.setGroups(List.of(g1, g2));

        assertEquals(2, model.getRowCount());
        assertEquals("Chi tiêu thiết yếu", model.getValueAt(0, 0));
        assertEquals("6,000,000 VNĐ", model.getValueAt(0, 1));
        assertEquals("60.0%", model.getValueAt(0, 2));
        assertEquals("5,000,000 VNĐ", model.getValueAt(0, 3));
        assertEquals("1,000,000 VNĐ", model.getValueAt(0, 4));
        assertEquals("20.0%", model.getValueAt(0, 5));

        assertEquals("Hưởng thụ cá nhân", model.getValueAt(1, 0));
        assertEquals("Mới", model.getValueAt(1, 5));
    }

    @Test
    @DisplayName("An toàn khi truy xuất chỉ mục ngoài biên và xóa sạch khi clear")
    void testBoundsAndClear() {
        assertNull(model.getValueAt(-1, 0));
        assertNull(model.getValueAt(0, 0));
        assertEquals("", model.getColumnName(-1));
        assertEquals("", model.getColumnName(10));

        NeedGroupShareResponseDTO g1 = new NeedGroupShareResponseDTO(
                UUID.randomUUID(), "Đầu tư", BigDecimal.ZERO, BigDecimal.ZERO, Collections.emptyList());
        model.setGroups(List.of(g1));
        assertEquals(1, model.getRowCount());

        model.clear();
        assertEquals(0, model.getRowCount());
    }
}
