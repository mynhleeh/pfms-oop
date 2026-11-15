package com.pfms.presentation.view.transaction.model;

import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử TransactionTableModel - Mô hình bảng danh sách giao dịch")
class TransactionTableModelTest {

    private TransactionTableModel model;
    private TransactionResponseDTO txnIncome;
    private TransactionResponseDTO txnExpenseWithFee;

    @BeforeEach
    void setUp() {
        model = new TransactionTableModel();

        txnIncome = new TransactionResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ví MB Bank",
                UUID.randomUUID(),
                "Lương",
                new BigDecimal("5000000"),
                "INCOME",
                LocalDate.of(2026, 11, 15),
                "Lương tháng 11",
                new BigDecimal("15000000"),
                LocalDateTime.now());

        txnExpenseWithFee = new TransactionResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ví Tiền mặt",
                UUID.randomUUID(),
                "Ăn uống",
                new BigDecimal("200000"),
                "EXPENSE",
                LocalDate.of(2026, 11, 16),
                "Ăn tối",
                true,
                new BigDecimal("5000"),
                UUID.randomUUID(),
                "Phí dịch vụ",
                new BigDecimal("4795000"),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Khởi tạo bảng rỗng ban đầu có 0 dòng và 8 cột")
    void testInitialEmptyTable() {
        assertEquals(0, model.getRowCount());
        assertEquals(8, model.getColumnCount());
        assertEquals("STT", model.getColumnName(0));
        assertEquals("Ngày", model.getColumnName(1));
        assertEquals("Loại", model.getColumnName(2));
        assertEquals("Danh mục", model.getColumnName(3));
        assertEquals("Số tiền", model.getColumnName(4));
        assertEquals("Ví", model.getColumnName(5));
        assertEquals("Phụ phí", model.getColumnName(6));
        assertEquals("Ghi chú", model.getColumnName(7));
    }

    @Test
    @DisplayName("Nạp danh sách giao dịch hiển thị chính xác các cột và định dạng")
    void testSetTransactionsPopulatesData() {
        model.setTransactions(List.of(txnIncome, txnExpenseWithFee));

        assertEquals(2, model.getRowCount());

        // Row 0 - Thu nhập
        assertEquals(1, model.getValueAt(0, 0));
        assertEquals("15/11/2026", model.getValueAt(0, 1));
        assertEquals("Thu nhập", model.getValueAt(0, 2));
        assertEquals("Lương", model.getValueAt(0, 3));
        assertEquals("5,000,000 VNĐ", model.getValueAt(0, 4));
        assertEquals("Ví MB Bank", model.getValueAt(0, 5));
        assertEquals("-", model.getValueAt(0, 6));
        assertEquals("Lương tháng 11", model.getValueAt(0, 7));

        // Row 1 - Chi tiêu có phí
        assertEquals(2, model.getValueAt(1, 0));
        assertEquals("16/11/2026", model.getValueAt(1, 1));
        assertEquals("Chi tiêu", model.getValueAt(1, 2));
        assertEquals("Ăn uống", model.getValueAt(1, 3));
        assertEquals("200,000 VNĐ", model.getValueAt(1, 4));
        assertEquals("Ví Tiền mặt", model.getValueAt(1, 5));
        assertEquals("5,000 VNĐ", model.getValueAt(1, 6));
        assertEquals("Ăn tối", model.getValueAt(1, 7));

        assertSame(txnIncome, model.getTransactionAt(0));
        assertSame(txnExpenseWithFee, model.getTransactionAt(1));
    }

    @Test
    @DisplayName("Truy xuất chỉ mục âm hoặc vượt biên trả về null an toàn")
    void testOutOfBoundsIndexSafe() {
        model.setTransactions(List.of(txnIncome));

        assertNull(model.getValueAt(-1, 0));
        assertNull(model.getValueAt(1, 0));
        assertNull(model.getValueAt(0, -1));
        assertNull(model.getValueAt(0, 8));
        assertNull(model.getTransactionAt(-1));
        assertNull(model.getTransactionAt(5));
    }

    @Test
    @DisplayName("Nạp null danh sách giao dịch không gây lỗi và xóa sạch dữ liệu")
    void testSetNullTransactionsSafe() {
        model.setTransactions(List.of(txnIncome));
        assertEquals(1, model.getRowCount());

        model.setTransactions(null);
        assertEquals(0, model.getRowCount());
        assertTrue(model.getTransactions().isEmpty());
    }
}
