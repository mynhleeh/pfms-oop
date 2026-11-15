package com.pfms.presentation.view.wallet.model;

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
    @DisplayName("Khởi tạo bảng rỗng ban đầu có 0 dòng và 7 cột")
    void testInitialEmptyTable() {
        assertEquals(0, model.getRowCount());
        assertEquals(7, model.getColumnCount());
        assertEquals("#", model.getColumnName(0));
        assertEquals("Tên ví", model.getColumnName(1));
        assertEquals("Số dư khả dụng", model.getColumnName(2));
        assertEquals("Đơn vị", model.getColumnName(3));
        assertEquals("Trạng thái", model.getColumnName(4));
        assertEquals("Ví mặc định", model.getColumnName(5));
        assertEquals("Ghi chú", model.getColumnName(6));
    }

    @Test
    @DisplayName("Nạp danh sách ví và đọc giá trị các ô dữ liệu chính xác")
    void testSetWalletsAndGetValueAt() {
        model.setWallets(List.of(sampleWallet));

        assertEquals(1, model.getRowCount());
        assertEquals(1, model.getValueAt(0, 0)); // STT
        assertEquals("Ví Tiền mặt", model.getValueAt(0, 1));
        assertEquals("5,000,000 VNĐ", model.getValueAt(0, 2));
        assertEquals("VND", model.getValueAt(0, 3));
        assertEquals("Hoạt động", model.getValueAt(0, 4));
        assertEquals("Mặc định", model.getValueAt(0, 5));
        assertEquals("Tiền chi tiêu hằng ngày", model.getValueAt(0, 6));

        assertSame(sampleWallet, model.getWalletAt(0));
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
