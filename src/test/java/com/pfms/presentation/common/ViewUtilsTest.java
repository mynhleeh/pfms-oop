package com.pfms.presentation.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Kiểm thử ViewUtils - Tiện ích định dạng tiền tệ và thời gian")
class ViewUtilsTest {

    @Test
    @DisplayName("formatCurrency định dạng đúng số tiền dương, âm và null")
    void testFormatCurrency() {
        assertEquals("0 VNĐ", ViewUtils.formatCurrency(null));
        assertEquals("0 VNĐ", ViewUtils.formatCurrency(BigDecimal.ZERO));
        assertEquals("1,500,000 VNĐ", ViewUtils.formatCurrency(new BigDecimal("1500000")));
        assertEquals("-250,000 VNĐ", ViewUtils.formatCurrency(new BigDecimal("-250000")));
    }

    @Test
    @DisplayName("formatAmount định dạng chuỗi số với dấu phẩy ngăn cách hàng nghìn")
    void testFormatAmount() {
        assertEquals("0", ViewUtils.formatAmount(null));
        assertEquals("0", ViewUtils.formatAmount(BigDecimal.ZERO));
        assertEquals("1,500,000", ViewUtils.formatAmount(new BigDecimal("1500000")));
    }

    @Test
    @DisplayName("formatDate định dạng LocalDate thành mẫu dd/MM/yyyy")
    void testFormatDate() {
        assertEquals("", ViewUtils.formatDate(null));
        assertEquals("15/11/2026", ViewUtils.formatDate(LocalDate.of(2026, 11, 15)));
    }
}
