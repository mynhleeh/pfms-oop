package com.pfms.presentation.dto.transfer;

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

@DisplayName("Kiểm thử TransferResponseDTO")
class TransferResponseDTOTest {

    private final UUID sampleTransferId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID sampleSourceWalletId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID sampleDestinationWalletId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private final UUID sampleFeeCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private final LocalDate sampleDate = LocalDate.of(2026, 11, 12);
    private final LocalDateTime sampleCreatedAt = LocalDateTime.of(2026, 11, 12, 15, 30);

    @Nested
    @DisplayName("Nhóm 1: Happy Path - Khởi tạo và đóng gói dữ liệu phản hồi hợp lệ")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 14 trường dữ liệu kèm phụ phí chuyển khoản")
        void shouldCreateWithAll14FieldsAndFee() {
            BigDecimal amount = new BigDecimal("5000000.00");
            BigDecimal fee = new BigDecimal("3300.00");
            BigDecimal sourceBalanceAfter = new BigDecimal("23346700.00");
            BigDecimal destinationBalanceAfter = new BigDecimal("9500000.00");

            TransferResponseDTO dto = new TransferResponseDTO(
                    sampleTransferId,
                    sampleSourceWalletId,
                    "Tài khoản MB Bank",
                    sampleDestinationWalletId,
                    "Tiền mặt sinh hoạt",
                    amount,
                    sampleDate,
                    "Rút tiền chi tiêu tuần",
                    fee,
                    sampleFeeCategoryId,
                    "Phí ngân hàng",
                    sourceBalanceAfter,
                    destinationBalanceAfter,
                    sampleCreatedAt
            );

            assertEquals(sampleTransferId, dto.getTransferId());
            assertEquals(sampleTransferId, dto.getId());
            assertEquals(sampleSourceWalletId, dto.getSourceWalletId());
            assertEquals("Tài khoản MB Bank", dto.getSourceWalletName());
            assertEquals(sampleDestinationWalletId, dto.getDestinationWalletId());
            assertEquals("Tiền mặt sinh hoạt", dto.getDestinationWalletName());
            assertEquals(amount, dto.getAmount());
            assertEquals(sampleDate, dto.getTransferDate());
            assertEquals("Rút tiền chi tiêu tuần", dto.getNote());
            assertTrue(dto.hasFee());
            assertTrue(dto.isHasFee());
            assertEquals(fee, dto.getFeeAmount());
            assertEquals(sampleFeeCategoryId, dto.getFeeCategoryId());
            assertEquals("Phí ngân hàng", dto.getFeeCategoryName());
            assertEquals(sourceBalanceAfter, dto.getSourceBalanceAfter());
            assertEquals(destinationBalanceAfter, dto.getDestinationBalanceAfter());
            assertEquals(sampleCreatedAt, dto.getCreatedAt());
        }

        @Test
        @DisplayName("Khởi tạo giao dịch chuyển tiền không phí qua hàm khởi tạo rút gọn 11 tham số")
        void shouldCreateWithoutFeeUsing11ParamConstructor() {
            BigDecimal amount = new BigDecimal("2000000.00");
            BigDecimal sourceBalanceAfter = new BigDecimal("8000000.00");
            BigDecimal destinationBalanceAfter = new BigDecimal("52000000.00");

            TransferResponseDTO dto = new TransferResponseDTO(
                    sampleTransferId,
                    sampleSourceWalletId,
                    "Tài khoản MB Bank",
                    sampleDestinationWalletId,
                    "Quỹ dự phòng sự cố",
                    amount,
                    sampleDate,
                    "Trích quỹ dự phòng",
                    sourceBalanceAfter,
                    destinationBalanceAfter,
                    sampleCreatedAt
            );

            assertEquals(sampleTransferId, dto.getTransferId());
            assertEquals(amount, dto.getAmount());
            assertFalse(dto.hasFee());
            assertFalse(dto.isHasFee());
            assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
            assertNull(dto.getFeeCategoryId());
            assertEquals("", dto.getFeeCategoryName());
            assertEquals(sourceBalanceAfter, dto.getSourceBalanceAfter());
            assertEquals(destinationBalanceAfter, dto.getDestinationBalanceAfter());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa khoảng trắng và gán mặc định cho ngày, ghi chú, phí và thời điểm tạo null")
        void shouldNormalizeNullsAndWhitespace() {
            TransferResponseDTO dto = new TransferResponseDTO(
                    sampleTransferId,
                    sampleSourceWalletId,
                    "   Tài khoản MB Bank   ",
                    sampleDestinationWalletId,
                    "   Tiền mặt sinh hoạt   ",
                    new BigDecimal("100000.00"),
                    null,
                    "   Nạp tiền lẻ   ",
                    null,
                    sampleFeeCategoryId,
                    "   Phí chuyển khoản   ",
                    new BigDecimal("900000.00"),
                    new BigDecimal("100000.00"),
                    null
            );

            assertEquals("Tài khoản MB Bank", dto.getSourceWalletName());
            assertEquals("Tiền mặt sinh hoạt", dto.getDestinationWalletName());
            assertEquals(LocalDate.now(), dto.getTransferDate());
            assertEquals("Nạp tiền lẻ", dto.getNote());
            assertFalse(dto.hasFee());
            assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
            assertNull(dto.getFeeCategoryId());
            assertEquals("", dto.getFeeCategoryName());
            assertNotNull(dto.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Nhóm 2: Boundary & Precision - Biên số học và bất biến scale")
    class BoundaryAndPrecisionTests {

        @Test
        @DisplayName("Chấp nhận số dư ví nguồn sau chuyển bằng 0.00 (chuyển toàn bộ số dư)")
        void shouldAcceptZeroSourceBalanceAfter() {
            TransferResponseDTO dto = new TransferResponseDTO(
                    sampleTransferId,
                    sampleSourceWalletId,
                    "Ví A",
                    sampleDestinationWalletId,
                    "Ví B",
                    new BigDecimal("1000000000000.00"),
                    sampleDate,
                    "Tất toán ví",
                    BigDecimal.ZERO,
                    new BigDecimal("1000000000000.00"),
                    sampleCreatedAt
            );

            assertEquals(0, BigDecimal.ZERO.compareTo(dto.getSourceBalanceAfter()));
        }

        @Test
        @DisplayName("Hai đối tượng bằng nhau và cùng hashCode khi cùng giá trị số học dù khác scale")
        void shouldBeEqualRegardlessOfBigDecimalScale() {
            TransferResponseDTO dto1 = new TransferResponseDTO(
                    sampleTransferId,
                    sampleSourceWalletId,
                    "Ví A",
                    sampleDestinationWalletId,
                    "Ví B",
                    new BigDecimal("500000.0"),
                    sampleDate,
                    "Chuyển tiền",
                    new BigDecimal("3300.0"),
                    sampleFeeCategoryId,
                    "Phí ngân hàng",
                    new BigDecimal("100000.0"),
                    new BigDecimal("900000.0"),
                    sampleCreatedAt
            );

            TransferResponseDTO dto2 = new TransferResponseDTO(
                    sampleTransferId,
                    sampleSourceWalletId,
                    "Ví A",
                    sampleDestinationWalletId,
                    "Ví B",
                    new BigDecimal("500000.00"),
                    sampleDate,
                    "Chuyển tiền",
                    new BigDecimal("3300.00"),
                    sampleFeeCategoryId,
                    "Phí ngân hàng",
                    new BigDecimal("100000.00"),
                    new BigDecimal("900000.00"),
                    sampleCreatedAt
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("Nhóm 3: Negative & Zero Values - Kiểm soát số âm và số không")
    class NegativeAndZeroValueTests {

        @ParameterizedTest(name = "Số tiền chuyển không dương ({0}) phải bị từ chối")
        @ValueSource(strings = {"0", "0.00", "-0.01", "-500000.00"})
        @DisplayName("Ném IllegalArgumentException khi số tiền chuyển nhỏ hơn hoặc bằng 0")
        void shouldThrowWhenAmountIsNonPositive(String invalidAmount) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransferResponseDTO(
                            sampleTransferId,
                            sampleSourceWalletId,
                            "Ví A",
                            sampleDestinationWalletId,
                            "Ví B",
                            new BigDecimal(invalidAmount),
                            sampleDate,
                            "",
                            new BigDecimal("500000"),
                            new BigDecimal("500000"),
                            sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Số tiền chuyển phải lớn hơn 0"));
        }

        @ParameterizedTest(name = "Mức phí âm ({0}) phải bị từ chối")
        @ValueSource(strings = {"-0.01", "-1000.00"})
        @DisplayName("Ném IllegalArgumentException khi mức phí chuyển tiền âm")
        void shouldThrowWhenFeeAmountIsNegative(String invalidFee) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransferResponseDTO(
                            sampleTransferId,
                            sampleSourceWalletId,
                            "Ví A",
                            sampleDestinationWalletId,
                            "Ví B",
                            new BigDecimal("100000"),
                            sampleDate,
                            "",
                            new BigDecimal(invalidFee),
                            sampleFeeCategoryId,
                            "Phí",
                            new BigDecimal("500000"),
                            new BigDecimal("500000"),
                            sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Mức phí chuyển tiền không được âm"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số dư sau giao dịch của ví nguồn hoặc ví đích bị âm")
        void shouldThrowWhenBalancesAfterAreNegative() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransferResponseDTO(
                            sampleTransferId,
                            sampleSourceWalletId,
                            "Ví A",
                            sampleDestinationWalletId,
                            "Ví B",
                            new BigDecimal("100000"),
                            sampleDate,
                            "",
                            new BigDecimal("-1.00"),
                            new BigDecimal("500000"),
                            sampleCreatedAt
                    )
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransferResponseDTO(
                            sampleTransferId,
                            sampleSourceWalletId,
                            "Ví A",
                            sampleDestinationWalletId,
                            "Ví B",
                            new BigDecimal("100000"),
                            sampleDate,
                            "",
                            new BigDecimal("500000"),
                            new BigDecimal("-1.00"),
                            sampleCreatedAt
                    )
            );
        }
    }

    @Nested
    @DisplayName("Nhóm 4: Temporal Invariants - Bất biến thời gian và lịch pháp")
    class TemporalInvariantTests {

        @Test
        @DisplayName("Bảo toàn chính xác mốc thời gian năm nhuận 29/02")
        void shouldPreserveLeapYearDates() {
            LocalDate leapDate = LocalDate.of(2024, 2, 29);
            LocalDateTime leapCreatedAt = LocalDateTime.of(2024, 2, 29, 23, 59, 59);

            TransferResponseDTO dto = new TransferResponseDTO(
                    sampleTransferId,
                    sampleSourceWalletId,
                    "Ví A",
                    sampleDestinationWalletId,
                    "Ví B",
                    new BigDecimal("100000"),
                    leapDate,
                    "Năm nhuận",
                    new BigDecimal("400000"),
                    new BigDecimal("600000"),
                    leapCreatedAt
            );

            assertEquals(leapDate, dto.getTransferDate());
            assertEquals(leapCreatedAt, dto.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Nhóm 5: State & Robustness - Kiểm soát null, trùng ví, danh mục phí và hợp đồng định danh")
    class StateAndRobustnessTests {

        @Test
        @DisplayName("Ném NullPointerException khi các trường định danh hoặc số dư bắt buộc bị null")
        void shouldThrowWhenRequiredFieldsAreNull() {
            assertThrows(NullPointerException.class, () -> new TransferResponseDTO(
                    null, sampleSourceWalletId, "Ví A", sampleDestinationWalletId, "Ví B",
                    new BigDecimal("100000"), sampleDate, "", BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt));

            assertThrows(NullPointerException.class, () -> new TransferResponseDTO(
                    sampleTransferId, null, "Ví A", sampleDestinationWalletId, "Ví B",
                    new BigDecimal("100000"), sampleDate, "", BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt));

            assertThrows(NullPointerException.class, () -> new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví A", null, "Ví B",
                    new BigDecimal("100000"), sampleDate, "", BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt));

            assertThrows(NullPointerException.class, () -> new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví A", sampleDestinationWalletId, "Ví B",
                    null, sampleDate, "", BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt));

            assertThrows(NullPointerException.class, () -> new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví A", sampleDestinationWalletId, "Ví B",
                    new BigDecimal("100000"), sampleDate, "", null, BigDecimal.ZERO, sampleCreatedAt));

            assertThrows(NullPointerException.class, () -> new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví A", sampleDestinationWalletId, "Ví B",
                    new BigDecimal("100000"), sampleDate, "", BigDecimal.ZERO, null, sampleCreatedAt));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ví nguồn trùng ví đích hoặc tên ví rỗng")
        void shouldThrowWhenWalletsAreSameOrNamesBlank() {
            assertThrows(IllegalArgumentException.class, () -> new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví A", sampleSourceWalletId, "Ví A",
                    new BigDecimal("100000"), sampleDate, "", BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt));

            assertThrows(IllegalArgumentException.class, () -> new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "   ", sampleDestinationWalletId, "Ví B",
                    new BigDecimal("100000"), sampleDate, "", BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt));

            assertThrows(IllegalArgumentException.class, () -> new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví A", sampleDestinationWalletId, "   ",
                    new BigDecimal("100000"), sampleDate, "", BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi mức phí dương nhưng thiếu mã hoặc tên danh mục phí")
        void shouldThrowWhenFeeIsPositiveButFeeCategoryMissing() {
            assertThrows(NullPointerException.class, () -> new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví A", sampleDestinationWalletId, "Ví B",
                    new BigDecimal("100000"), sampleDate, "",
                    new BigDecimal("3300"), null, "Phí ngân hàng",
                    BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt));

            assertThrows(IllegalArgumentException.class, () -> new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví A", sampleDestinationWalletId, "Ví B",
                    new BigDecimal("100000"), sampleDate, "",
                    new BigDecimal("3300"), sampleFeeCategoryId, "   ",
                    BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt));
        }

        @Test
        @DisplayName("Thỏa mãn hợp đồng equals, hashCode và toString")
        void shouldSatisfyEqualsHashCodeAndToString() {
            TransferResponseDTO base = new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví Nguồn",
                    sampleDestinationWalletId, "Ví Đích",
                    new BigDecimal("250000"), sampleDate, "Ghi chú",
                    new BigDecimal("750000"), new BigDecimal("1250000"), sampleCreatedAt
            );

            TransferResponseDTO diffId = new TransferResponseDTO(
                    UUID.randomUUID(), sampleSourceWalletId, "Ví Nguồn",
                    sampleDestinationWalletId, "Ví Đích",
                    new BigDecimal("250000"), sampleDate, "Ghi chú",
                    new BigDecimal("750000"), new BigDecimal("1250000"), sampleCreatedAt
            );

            assertEquals(base, base);
            assertNotEquals(base, diffId);
            assertNotEquals(base, null);
            assertNotEquals(base, "string");

            String str = base.toString();
            assertTrue(str.contains("TransferResponseDTO"));
            assertTrue(str.contains("Ví Nguồn"));
            assertTrue(str.contains("Ví Đích"));
            assertTrue(str.contains("250000"));
        }
    }
}

