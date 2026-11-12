package com.pfms.presentation.dto.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử BalanceAdjustmentResponseDTO")
class BalanceAdjustmentResponseDTOTest {

    private final UUID recordId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID walletId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final LocalDate adjustedDate = LocalDate.of(2026, 11, 1);
    private final LocalDateTime createdTime = LocalDateTime.of(2026, 11, 1, 9, 30, 0);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với chênh lệch dương (thừa tiền / surplus)")
        void shouldCreateWithSurplusDifference() {
            BigDecimal prev = new BigDecimal("4000000.00");
            BigDecimal actual = new BigDecimal("4500000.00");
            BigDecimal diff = new BigDecimal("500000.00");

            BalanceAdjustmentResponseDTO dto = new BalanceAdjustmentResponseDTO(
                    recordId, walletId, prev, actual, diff, "VND", "Tìm thấy tiền mặt thừa", adjustedDate, createdTime);

            assertEquals(recordId, dto.getRecordId());
            assertEquals(walletId, dto.getWalletId());
            assertEquals(prev, dto.getPreviousBalance());
            assertEquals(actual, dto.getActualBalance());
            assertEquals(diff, dto.getDifference());
            assertEquals("VND", dto.getCurrency());
            assertEquals("Tìm thấy tiền mặt thừa", dto.getReason());
            assertEquals(adjustedDate, dto.getAdjustedAt());
            assertEquals(createdTime, dto.getCreatedAt());
            assertTrue(dto.isSurplus());
            assertFalse(dto.isDeficit());
            assertFalse(dto.isBalanced());
            assertEquals(diff, dto.getAbsoluteDifference());
        }

        @Test
        @DisplayName("Khởi tạo thành công với chênh lệch âm (hụt tiền / deficit)")
        void shouldCreateWithDeficitDifference() {
            BigDecimal prev = new BigDecimal("5000000.00");
            BigDecimal actual = new BigDecimal("4800000.00");
            BigDecimal diff = new BigDecimal("-200000.00");

            BalanceAdjustmentResponseDTO dto = new BalanceAdjustmentResponseDTO(
                    recordId, walletId, prev, actual, diff, "VND", "Quên ghi chép cà phê", adjustedDate, createdTime);

            assertEquals(diff, dto.getDifference());
            assertFalse(dto.isSurplus());
            assertTrue(dto.isDeficit());
            assertFalse(dto.isBalanced());
            assertEquals(new BigDecimal("200000.00"), dto.getAbsoluteDifference());
        }

        @Test
        @DisplayName("Cung cấp getAbsoluteDifference và isBalanced khi số dư thực tế khớp hoàn toàn số dư trước")
        void shouldHandleBalancedAdjustment() {
            BigDecimal prev = new BigDecimal("2000000.00");
            BalanceAdjustmentResponseDTO dto = new BalanceAdjustmentResponseDTO(
                    recordId, walletId, prev, prev, BigDecimal.ZERO, "VND", "Khớp sổ cái", adjustedDate, createdTime);

            assertTrue(dto.isBalanced());
            assertFalse(dto.isSurplus());
            assertFalse(dto.isDeficit());
            assertEquals(BigDecimal.ZERO, dto.getAbsoluteDifference());
        }

        @Test
        @DisplayName("Tự động tính chênh lệch khi difference truyền vào là null")
        void shouldAutoCalculateDifferenceWhenNull() {
            BigDecimal prev = new BigDecimal("1000000.00");
            BigDecimal actual = new BigDecimal("1250000.00");

            BalanceAdjustmentResponseDTO dto = new BalanceAdjustmentResponseDTO(
                    recordId, walletId, prev, actual, null, "VND", "Kiểm kê", adjustedDate, createdTime);

            assertEquals(new BigDecimal("250000.00"), dto.getDifference());
            assertTrue(dto.isSurplus());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa currency, reason, adjustedAt và createdAt khi nhận null")
        void shouldNormalizeNullFieldsWithDefaults() {
            BigDecimal prev = new BigDecimal("1000000.00");
            BigDecimal actual = new BigDecimal("1000000.00");

            BalanceAdjustmentResponseDTO dto = new BalanceAdjustmentResponseDTO(
                    recordId, walletId, prev, actual, BigDecimal.ZERO, null, null, null, null);

            assertEquals("VND", dto.getCurrency());
            assertEquals("", dto.getReason());
            assertEquals(LocalDate.now(), dto.getAdjustedAt());
            assertNotNull(dto.getCreatedAt());
            assertFalse(dto.isSurplus());
            assertFalse(dto.isDeficit());
            assertTrue(dto.isBalanced());
            assertEquals(BigDecimal.ZERO, dto.getAbsoluteDifference());
        }
    }

    @Nested
    @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
    class InvalidInputTests {

        @Test
        @DisplayName("Ném NullPointerException khi recordId bị null")
        void shouldThrowExceptionWhenRecordIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new BalanceAdjustmentResponseDTO(
                            null, walletId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "VND", "", adjustedDate,
                            createdTime));
            assertTrue(ex.getMessage().contains("Mã bản ghi"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi walletId bị null")
        void shouldThrowExceptionWhenWalletIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new BalanceAdjustmentResponseDTO(
                            recordId, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "VND", "", adjustedDate,
                            createdTime));
            assertTrue(ex.getMessage().contains("Mã ví"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi previousBalance bị null")
        void shouldThrowExceptionWhenPreviousBalanceIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new BalanceAdjustmentResponseDTO(
                            recordId, walletId, null, BigDecimal.ZERO, BigDecimal.ZERO, "VND", "", adjustedDate,
                            createdTime));
            assertTrue(ex.getMessage().contains("Số dư trước"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi actualBalance bị null")
        void shouldThrowExceptionWhenActualBalanceIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new BalanceAdjustmentResponseDTO(
                            recordId, walletId, BigDecimal.ZERO, null, BigDecimal.ZERO, "VND", "", adjustedDate,
                            createdTime));
            assertTrue(ex.getMessage().contains("Số dư thực tế"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "-0.01", "-100000" })
        @DisplayName("Ném IllegalArgumentException khi previousBalance hoặc actualBalance mang giá trị âm")
        void shouldThrowExceptionWhenBalancesAreNegative(String negativeAmount) {
            BigDecimal neg = new BigDecimal(negativeAmount);

            assertThrows(
                    IllegalArgumentException.class,
                    () -> new BalanceAdjustmentResponseDTO(
                            recordId, walletId, neg, BigDecimal.ZERO, BigDecimal.ZERO, "VND", "", adjustedDate,
                            createdTime));
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new BalanceAdjustmentResponseDTO(
                            recordId, walletId, BigDecimal.ZERO, neg, BigDecimal.ZERO, "VND", "", adjustedDate,
                            createdTime));
        }
    }

    @Nested
    @DisplayName("Hợp đồng Bình đẳng và Bất biến (Equality & Immutability)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai DTO cùng thuộc tính phải bình đẳng và cùng hashCode")
        void shouldRespectEqualsAndHashCodeContract() {
            UUID otherId = UUID.fromString("00000000-0000-0000-0000-000000000099");
            BigDecimal prev = new BigDecimal("1000.00");
            BigDecimal actual = new BigDecimal("1200.00");
            BigDecimal diff = new BigDecimal("200.00");

            BalanceAdjustmentResponseDTO dto1 = new BalanceAdjustmentResponseDTO(
                    recordId, walletId, prev, actual, diff, "VND", "Lý do", adjustedDate, createdTime);
            BalanceAdjustmentResponseDTO dto2 = new BalanceAdjustmentResponseDTO(
                    recordId, walletId, prev, actual, diff, "VND", "Lý do", adjustedDate, createdTime);
            BalanceAdjustmentResponseDTO dtoDiffRecordId = new BalanceAdjustmentResponseDTO(
                    otherId, walletId, prev, actual, diff, "VND", "Lý do", adjustedDate, createdTime);
            BalanceAdjustmentResponseDTO dtoDiffDiff = new BalanceAdjustmentResponseDTO(
                    recordId, walletId, prev, actual, new BigDecimal("300.00"), "VND", "Lý do", adjustedDate,
                    createdTime);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dtoDiffRecordId);
            assertNotEquals(dto1, dtoDiffDiff);
            assertNotEquals(null, dto1);
            assertNotEquals(dto1, "DifferentType");
        }

        @Test
        @DisplayName("Phương thức toString cung cấp thông tin kiểm kê đầy đủ")
        void shouldProvideMeaningfulToString() {
            BalanceAdjustmentResponseDTO dto = new BalanceAdjustmentResponseDTO(
                    recordId, walletId, new BigDecimal("1000.00"), new BigDecimal("1500.00"),
                    new BigDecimal("500.00"), "VND", "Kiểm kê", adjustedDate, createdTime);
            String text = dto.toString();

            assertTrue(text.contains("BalanceAdjustmentResponseDTO"));
            assertTrue(text.contains(recordId.toString()));
            assertTrue(text.contains(walletId.toString()));
            assertTrue(text.contains("1000.00"));
            assertTrue(text.contains("1500.00"));
            assertTrue(text.contains("500.00"));
        }
    }
}
