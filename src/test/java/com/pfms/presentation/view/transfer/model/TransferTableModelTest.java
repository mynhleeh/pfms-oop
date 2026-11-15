package com.pfms.presentation.view.transfer.model;

import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử TransferTableModel - Mô hình bảng danh sách chuyển tiền nội bộ")
class TransferTableModelTest {

    private TransferTableModel model;
    private TransferResponseDTO transfer1;
    private TransferResponseDTO transferWithFee;

    @BeforeEach
    void setUp() {
        model = new TransferTableModel();

        transfer1 = new TransferResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ví Tiền mặt",
                UUID.randomUUID(),
                "Ví MB Bank",
                new BigDecimal("1000000"),
                LocalDate.of(2026, 11, 15),
                "Nạp tiền ngân hàng",
                new BigDecimal("4000000"),
                new BigDecimal("16000000"),
                LocalDateTime.now());

        transferWithFee = new TransferResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ví MB Bank",
                UUID.randomUUID(),
                "Ví Tiết kiệm",
                new BigDecimal("500000"),
                LocalDate.of(2026, 11, 16),
                "Gửi tiết kiệm",
                new BigDecimal("2200"),
                UUID.randomUUID(),
                "Phí chuyển khoản",
                new BigDecimal("15497800"),
                new BigDecimal("20500000"),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Khởi tạo bảng rỗng ban đầu có 0 dòng và 7 cột")
    void testInitialEmptyTable() {
        assertEquals(0, model.getRowCount());
        assertEquals(7, model.getColumnCount());
        assertEquals("STT", model.getColumnName(0));
        assertEquals("Ngày", model.getColumnName(1));
        assertEquals("Ví nguồn", model.getColumnName(2));
        assertEquals("Ví đích", model.getColumnName(3));
        assertEquals("Số tiền", model.getColumnName(4));
        assertEquals("Phụ phí", model.getColumnName(5));
        assertEquals("Ghi chú", model.getColumnName(6));
    }

    @Test
    @DisplayName("Nạp danh sách chuyển tiền hiển thị chính xác các cột và định dạng")
    void testSetTransfersPopulatesData() {
        model.setTransfers(List.of(transfer1, transferWithFee));

        assertEquals(2, model.getRowCount());

        // Row 0 - Không phí
        assertEquals(1, model.getValueAt(0, 0));
        assertEquals("15/11/2026", model.getValueAt(0, 1));
        assertEquals("Ví Tiền mặt", model.getValueAt(0, 2));
        assertEquals("Ví MB Bank", model.getValueAt(0, 3));
        assertEquals("1,000,000 VNĐ", model.getValueAt(0, 4));
        assertEquals("-", model.getValueAt(0, 5));
        assertEquals("Nạp tiền ngân hàng", model.getValueAt(0, 6));

        // Row 1 - Có phí
        assertEquals(2, model.getValueAt(1, 0));
        assertEquals("16/11/2026", model.getValueAt(1, 1));
        assertEquals("Ví MB Bank", model.getValueAt(1, 2));
        assertEquals("Ví Tiết kiệm", model.getValueAt(1, 3));
        assertEquals("500,000 VNĐ", model.getValueAt(1, 4));
        assertEquals("2,200 VNĐ", model.getValueAt(1, 5));
        assertEquals("Gửi tiết kiệm", model.getValueAt(1, 6));

        assertSame(transfer1, model.getTransferAt(0));
        assertSame(transferWithFee, model.getTransferAt(1));
    }

    @Test
    @DisplayName("Truy xuất chỉ mục âm hoặc vượt biên trả về null an toàn")
    void testOutOfBoundsIndexSafe() {
        model.setTransfers(List.of(transfer1));

        assertNull(model.getValueAt(-1, 0));
        assertNull(model.getValueAt(1, 0));
        assertNull(model.getValueAt(0, -1));
        assertNull(model.getValueAt(0, 7));
        assertNull(model.getTransferAt(-1));
        assertNull(model.getTransferAt(5));
    }

    @Test
    @DisplayName("Nạp null danh sách chuyển tiền không gây lỗi và xóa sạch dữ liệu")
    void testSetNullTransfersSafe() {
        model.setTransfers(List.of(transfer1));
        assertEquals(1, model.getRowCount());

        model.setTransfers(null);
        assertEquals(0, model.getRowCount());
        assertTrue(model.getTransfers().isEmpty());
    }
}
