package com.pfms.presentation.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ViewUtils Unit Tests")
class ViewUtilsTest {

    @Test
    @DisplayName("formatCurrency formats positive and negative amounts correctly")
    void testFormatCurrency() {
        assertEquals("0 VNĐ", ViewUtils.formatCurrency(null));
        assertEquals("0 VNĐ", ViewUtils.formatCurrency(BigDecimal.ZERO));
        assertEquals("1,500,000 VNĐ", ViewUtils.formatCurrency(new BigDecimal("1500000")));
        assertEquals("-250,000 VNĐ", ViewUtils.formatCurrency(new BigDecimal("-250000")));
    }

    @Test
    @DisplayName("formatDate formats LocalDate into dd/MM/yyyy pattern")
    void testFormatDate() {
        assertEquals("", ViewUtils.formatDate(null));
        assertEquals("15/11/2026", ViewUtils.formatDate(LocalDate.of(2026, 11, 15)));
    }

    @Test
    @DisplayName("createRenderers return proper Swing alignments")
    void testCreateRenderers() {
        DefaultTableCellRenderer right = ViewUtils.createRightAlignRenderer();
        assertEquals(SwingConstants.RIGHT, right.getHorizontalAlignment());

        DefaultTableCellRenderer center = ViewUtils.createCenterAlignRenderer();
        assertEquals(SwingConstants.CENTER, center.getHorizontalAlignment());
    }

    @Test
    @DisplayName("styleTable sets standard row height and styling")
    void testStyleTable() {
        JTable table = new JTable(new DefaultTableModel(2, 2));
        ViewUtils.styleTable(table);
        assertEquals(32, table.getRowHeight());
        assertNotNull(table.getTableHeader());
    }
}
