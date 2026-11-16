package com.pfms.presentation.wallet.model;

import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử WalletTableModel - Mô hình bảng danh sách ví")
class WalletTableModelTest {

    private WalletTableModel model;
    private WalletResponseDTO sampleWallet;

    @BeforeEach
    void setUp() {
        model = new WalletTableModel();
        sampleWallet = new WalletResponseDTO(
                UUID.randomUUID(),
                "Ví Tiền mặt",
                new BigDecimal("5000000"),
                "VND",
                true,
                "ACTIVE",
                "Tiền chi tiêu hằng ngày",
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Khởi tạo bảng rỗng ban đầu có 0 dòng và 5 cột chuẩn preview")
    void testInitialEmptyTable() {
        assertEquals(0, model.getRowCount());
        assertEquals(5, model.getColumnCount());
        assertEquals("Tên ví tài chính", model.getColumnName(0));
        assertEquals("Số dư khả dụng", model.getColumnName(1));
        assertEquals("Trạng thái", model.getColumnName(2));
        assertEquals("Ghi chú", model.getColumnName(3));
        assertEquals("Thao tác", model.getColumnName(4));
    }

    @Test
    @DisplayName("Nạp danh sách ví và đọc giá trị các ô dữ liệu chính xác")
    void testSetWalletsAndGetValueAt() {
        model.setWallets(List.of(sampleWallet));

        assertEquals(1, model.getRowCount());
        assertEquals("Ví Tiền mặt", model.getValueAt(0, 0));
        assertEquals("5,000,000 VNĐ", model.getValueAt(0, 1));
        assertEquals("Hoạt động", model.getValueAt(0, 2));
        assertEquals("Tiền chi tiêu hằng ngày", model.getValueAt(0, 3));
        assertSame(sampleWallet, model.getValueAt(0, 4));

        assertSame(sampleWallet, model.getWalletAt(0));
    }

    @Test
    @DisplayName("Cột Thao tác (chỉ mục 4) cho phép chỉnh sửa tương tác để nhận sự kiện chuột")
    void testCellEditableOnlyForActionsColumn() {
        assertFalse(model.isCellEditable(0, 0));
        assertFalse(model.isCellEditable(0, 1));
        assertFalse(model.isCellEditable(0, 2));
        assertFalse(model.isCellEditable(0, 3));
        assertTrue(model.isCellEditable(0, 4));
    }

    @Test
    @DisplayName("Trích xuất ví tại chỉ mục không hợp lệ trả về null")
    void testGetWalletAtInvalidIndex() {
        assertNull(model.getWalletAt(-1));
        assertNull(model.getWalletAt(10));
    }

    @Test
    @DisplayName("Nạp danh sách null sẽ làm rỗng bảng dữ liệu an toàn")
    void testSetNullWalletsEmptiesTable() {
        model.setWallets(List.of(sampleWallet));
        assertEquals(1, model.getRowCount());

        model.setWallets(null);
        assertEquals(0, model.getRowCount());
    }
}
