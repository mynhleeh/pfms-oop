package com.pfms.presentation.common.component;

import com.pfms.presentation.common.ViewConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Dimension;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử KpiCapsulePanel - Thẻ thống kê KPI bo góc chuẩn Design Tokens")
class KpiCapsulePanelTest {

    @Test
    @DisplayName("Khởi tạo KpiCapsulePanel với tiêu đề và giá trị ban đầu hợp lệ")
    void testInitialState() {
        KpiCapsulePanel kpi = new KpiCapsulePanel("TỔNG SỐ DƯ", "182,850,000 VNĐ", ViewConstants.COLOR_PRIMARY);

        assertEquals("TỔNG SỐ DƯ", kpi.getTitle());
        assertEquals("182,850,000 VNĐ", kpi.getValue());
        assertEquals(ViewConstants.COLOR_PRIMARY, kpi.getValueColor());
        assertEquals(ViewConstants.COLOR_TEXT_MUTED, kpi.getTitleColor());
        assertFalse(kpi.isOpaque(), "Panel bo góc phải có isOpaque = false để khử răng cưa");
    }

    @Test
    @DisplayName("Cập nhật giá trị và màu sắc giá trị phản ánh chính xác lên giao diện")
    void testUpdateValueAndColor() {
        KpiCapsulePanel kpi = new KpiCapsulePanel("SỐ VÍ HOẠT ĐỘNG", "0 ví");
        assertEquals("0 ví", kpi.getValue());

        kpi.setValue("5 ví", ViewConstants.COLOR_INCOME);
        assertEquals("5 ví", kpi.getValue());
        assertEquals(ViewConstants.COLOR_INCOME, kpi.getValueColor());

        kpi.setTitle("TỔNG SỐ VÍ");
        assertEquals("TỔNG SỐ VÍ", kpi.getTitle());
    }

    @Test
    @DisplayName("Kích thước tối thiểu và khoảng đệm padding ngăn chặn hiện tượng co rúm và cắt chữ")
    void testDimensionsAndPadding() {
        KpiCapsulePanel kpi = new KpiCapsulePanel("TỔNG SỐ DƯ", "182,850,000 VNĐ");
        Dimension pref = kpi.getPreferredSize();

        assertTrue(pref.width >= 160, "Chiều rộng tối thiểu của KPI card phải >= 160px để hiển thị số dư dài");
        assertTrue(pref.height >= 56, "Chiều cao tối thiểu của KPI card phải >= 56px để chứa 2 tầng nhãn");
    }
}
