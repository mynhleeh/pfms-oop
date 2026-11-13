package com.pfms.presentation.dto.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử WalletsSummaryDTO")
class WalletsSummaryDTOTest {

    private final UUID id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID id2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final LocalDateTime now = LocalDateTime.of(2026, 11, 1, 9, 0);

    private WalletResponseDTO createWallet(UUID id, String name, String amount) {
        return new WalletResponseDTO(
                id, name, new BigDecimal(amount), "VND", false, "ACTIVE", "", now);
    }

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với danh sách ví, tổng số dư và tỷ trọng cơ cấu")
        void shouldCreateWithAllFields() {
            List<WalletResponseDTO> walletList = List.of(
                    createWallet(id1, "Ví Tiền Mặt", "4000000.00"),
                    createWallet(id2, "Ví MB Bank", "6000000.00"));
            BigDecimal total = new BigDecimal("10000000.00");
            Map<UUID, BigDecimal> shares = Map.of(
                    id1, new BigDecimal("40.00"),
                    id2, new BigDecimal("60.00"));

            WalletsSummaryDTO dto = new WalletsSummaryDTO(walletList, total, "VND", shares);

            assertEquals(2, dto.getWalletCount());
            assertEquals(2, dto.getWallets().size());
            assertEquals(total, dto.getTotalBalance());
            assertEquals("VND", dto.getCurrency());
            assertEquals(new BigDecimal("40.00"), dto.getWalletShares().get(id1));
            assertEquals(new BigDecimal("60.00"), dto.getWalletShares().get(id2));
            assertEquals(new BigDecimal("40.00"), dto.getWalletShare(id1));
            assertEquals(new BigDecimal("60.00"), dto.getWalletShare(id2));
        }

        @Test
        @DisplayName("Cung cấp phương thức getWalletShare trả về tỷ trọng ví hoặc BigDecimal.ZERO an toàn")
        void shouldReturnWalletShareSafely() {
            UUID unknownId = UUID.randomUUID();
            Map<UUID, BigDecimal> shares = Map.of(id1, new BigDecimal("45.50"));
            WalletsSummaryDTO dto = new WalletsSummaryDTO(
                    Collections.emptyList(), new BigDecimal("1000000.00"), "VND", shares);

            assertEquals(new BigDecimal("45.50"), dto.getWalletShare(id1));
            assertEquals(BigDecimal.ZERO, dto.getWalletShare(unknownId));
            assertEquals(BigDecimal.ZERO, dto.getWalletShare(null));
        }

        @Test
        @DisplayName("Tự động gán danh sách rỗng, map rỗng và currency mặc định khi nhận null")
        void shouldHandleNullCollectionsAndCurrency() {
            WalletsSummaryDTO dto = new WalletsSummaryDTO(null, BigDecimal.ZERO, null, null);

            assertEquals(0, dto.getWalletCount());
            assertTrue(dto.getWallets().isEmpty());
            assertTrue(dto.getWalletShares().isEmpty());
            assertEquals("VND", dto.getCurrency());
            assertEquals(BigDecimal.ZERO, dto.getTotalBalance());
        }
    }

    @Nested
    @DisplayName("Bảo vệ Tính Bất biến (Defensive Copying)")
    class DefensiveCopyingTests {

        @Test
        @DisplayName("Không bị ảnh hưởng khi danh sách và map đầu vào bị sửa đổi từ bên ngoài")
        void shouldBeImmuneToExternalListModification() {
            List<WalletResponseDTO> mutableList = new ArrayList<>();
            mutableList.add(createWallet(id1, "Ví Tiền Mặt", "1000000.00"));
            Map<UUID, BigDecimal> mutableMap = new HashMap<>();
            mutableMap.put(id1, new BigDecimal("100.00"));

            WalletsSummaryDTO dto = new WalletsSummaryDTO(
                    mutableList, new BigDecimal("1000000.00"), "VND", mutableMap);

            // Sửa đổi bên ngoài
            mutableList.add(createWallet(id2, "Ví MB Bank", "2000000.00"));
            mutableMap.put(id2, new BigDecimal("50.00"));

            assertEquals(1, dto.getWalletCount());
            assertEquals(1, dto.getWalletShares().size());
        }

        @Test
        @DisplayName("Ném UnsupportedOperationException khi cố tình sửa danh sách ví từ DTO")
        void shouldThrowExceptionWhenModifyingWalletsList() {
            WalletsSummaryDTO dto = new WalletsSummaryDTO(
                    List.of(createWallet(id1, "Ví Tiền Mặt", "1000000.00")),
                    new BigDecimal("1000000.00"),
                    "VND",
                    Map.of(id1, new BigDecimal("100.00")));

            assertThrows(
                    UnsupportedOperationException.class,
                    () -> dto.getWallets().add(createWallet(id2, "Ví Thêm", "500000.00")));
            assertThrows(
                    UnsupportedOperationException.class,
                    () -> dto.getWalletShares().put(id2, new BigDecimal("20.00")));
        }
    }

    @Nested
    @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
    class InvalidInputTests {

        @Test
        @DisplayName("Ném NullPointerException khi tổng số dư totalBalance bị null")
        void shouldThrowExceptionWhenTotalBalanceIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new WalletsSummaryDTO(Collections.emptyList(), null, "VND", Collections.emptyMap()));
            assertTrue(ex.getMessage().contains("Tổng số dư"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "-0.01", "-100000" })
        @DisplayName("Chấp nhận tổng số dư totalBalance âm để giữ DTO là pure data carrier")
        void shouldAllowNegativeTotalBalanceAsPureDataCarrier(String negativeAmount) {
            BigDecimal negative = new BigDecimal(negativeAmount);
            WalletsSummaryDTO dto = new WalletsSummaryDTO(
                    Collections.emptyList(), negative, "VND", Collections.emptyMap());
            assertEquals(0, negative.compareTo(dto.getTotalBalance()));
        }
    }

    @Nested
    @DisplayName("Hợp đồng Bình đẳng và Bất biến (Equality & Immutability)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai DTO cùng thuộc tính phải bình đẳng và cùng hashCode")
        void shouldRespectEqualsAndHashCodeContract() {
            List<WalletResponseDTO> list1 = List.of(createWallet(id1, "Ví 1", "1000.00"));
            List<WalletResponseDTO> list2 = List.of(createWallet(id1, "Ví 1", "1000.00"));
            BigDecimal total = new BigDecimal("1000.00");
            Map<UUID, BigDecimal> shares = Map.of(id1, new BigDecimal("100.00"));

            WalletsSummaryDTO dto1 = new WalletsSummaryDTO(list1, total, "VND", shares);
            WalletsSummaryDTO dto2 = new WalletsSummaryDTO(list2, total, "VND", shares);
            WalletsSummaryDTO dtoDiffTotal = new WalletsSummaryDTO(list1, new BigDecimal("2000.00"), "VND", shares);
            WalletsSummaryDTO dtoDiffCurrency = new WalletsSummaryDTO(list1, total, "USD", shares);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dtoDiffTotal);
            assertNotEquals(dto1, dtoDiffCurrency);
            assertNotEquals(null, dto1);
            assertNotEquals(dto1, "NotAWalletsSummaryDTO");
        }

        @Test
        @DisplayName("Phương thức toString cung cấp thông tin tổng quan đầy đủ")
        void shouldProvideMeaningfulToString() {
            WalletsSummaryDTO dto = new WalletsSummaryDTO(
                    Collections.emptyList(), new BigDecimal("5000000.00"), "VND", Collections.emptyMap());
            String text = dto.toString();

            assertTrue(text.contains("WalletsSummaryDTO"));
            assertTrue(text.contains("5000000.00"));
            assertTrue(text.contains("VND"));
        }
    }
}
