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
            assertEquals(sampleSourceWalletId, dto.getSourceWalletId());
            assertEquals("Tài khoản MB Bank", dto.getSourceWalletName());
            assertEquals(sampleDestinationWalletId, dto.getDestinationWalletId());
            assertEquals("Tiền mặt sinh hoạt", dto.getDestinationWalletName());
            assertEquals(amount, dto.getAmount());
            assertEquals(sampleDate, dto.getTransferDate());
            assertEquals("Rút tiền chi tiêu tuần", dto.getNote());
            assertTrue(dto.hasFee());
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
    @DisplayName("Nhóm 3: Negative & Zero Values - Chấp nhận giá trị biên như pure data carrier")
    class NegativeAndZeroValueTests {

        @ParameterizedTest(name = "Chấp nhận số tiền chuyển không dương ({0}) làm pure data carrier")
        @ValueSource(strings = {"0", "0.00", "-0.01", "-500000.00"})
        @DisplayName("Chấp nhận số tiền chuyển nhỏ hơn hoặc bằng 0 để giữ DTO là pure data carrier")
        void shouldAllowNonPositiveAmountAsPureDataCarrier(String rawAmount) {
            BigDecimal amount = new BigDecimal(rawAmount);
            TransferResponseDTO dto = new TransferResponseDTO(
                    sampleTransferId,
                    sampleSourceWalletId,
                    "Ví A",
                    sampleDestinationWalletId,
                    "Ví B",
                    amount,
                    sampleDate,
                    "",
                    new BigDecimal("500000"),
                    new BigDecimal("500000"),
                    sampleCreatedAt
            );
            assertEquals(0, amount.compareTo(dto.getAmount()));
        }

        @ParameterizedTest(name = "Chuẩn hóa mức phí âm ({0}) về 0 để giữ DTO là pure data carrier")
        @ValueSource(strings = {"-0.01", "-1000.00"})
        @DisplayName("Chuẩn hóa mức phí âm về 0 khi giữ DTO là pure data carrier")
        void shouldNormalizeNegativeFeeToZeroAsPureDataCarrier(String rawFee) {
            TransferResponseDTO dto = new TransferResponseDTO(
                    sampleTransferId,
                    sampleSourceWalletId,
                    "Ví A",
                    sampleDestinationWalletId,
                    "Ví B",
                    new BigDecimal("100000"),
                    sampleDate,
                    "",
                    new BigDecimal(rawFee),
                    sampleFeeCategoryId,
                    "Phí",
                    new BigDecimal("500000"),
                    new BigDecimal("500000"),
                    sampleCreatedAt
            );
            assertFalse(dto.hasFee());
            assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
            assertNull(dto.getFeeCategoryId());
            assertEquals("", dto.getFeeCategoryName());
        }

        @Test
        @DisplayName("Chấp nhận số dư sau giao dịch của ví nguồn hoặc ví đích âm để giữ DTO là pure data carrier")
        void shouldAllowNegativeBalancesAfterAsPureDataCarrier() {
            TransferResponseDTO negSource = new TransferResponseDTO(
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
            );

            TransferResponseDTO negDest = new TransferResponseDTO(
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
            );

            assertEquals(0, new BigDecimal("-1.00").compareTo(negSource.getSourceBalanceAfter()));
            assertEquals(0, new BigDecimal("-1.00").compareTo(negDest.getDestinationBalanceAfter()));
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
        @DisplayName("Chấp nhận ví nguồn trùng ví đích và tên ví rỗng hoặc null để giữ DTO là pure data carrier")
        void shouldAllowSameWalletsAndNullOrBlankNamesAsPureDataCarrier() {
            TransferResponseDTO sameWalletDto = new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "   ", sampleSourceWalletId, null,
                    new BigDecimal("100000"), sampleDate, "", BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt);

            assertEquals(sampleSourceWalletId, sameWalletDto.getSourceWalletId());
            assertEquals(sampleSourceWalletId, sameWalletDto.getDestinationWalletId());
            assertEquals("", sameWalletDto.getSourceWalletName());
            assertNull(sameWalletDto.getDestinationWalletName());
        }

        @Test
        @DisplayName("Chấp nhận mức phí dương thiếu mã hoặc tên danh mục phí để giữ DTO là pure data carrier")
        void shouldAllowPositiveFeeWithoutFeeCategoryAsPureDataCarrier() {
            TransferResponseDTO dto = new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, "Ví A", sampleDestinationWalletId, "Ví B",
                    new BigDecimal("100000"), sampleDate, "",
                    new BigDecimal("3300"), null, "   ",
                    BigDecimal.ZERO, BigDecimal.ZERO, sampleCreatedAt);

            assertTrue(dto.hasFee());
            assertEquals(new BigDecimal("3300"), dto.getFeeAmount());
            assertNull(dto.getFeeCategoryId());
            assertEquals("", dto.getFeeCategoryName());
        }

        @Test
        @DisplayName("Hợp đồng equals, hashCode và toString an toàn khi tên ví nguồn/đích và feeCategoryId bằng null")
        void shouldHandleNullableFieldsSafelyInIdentityMethods() {
            TransferResponseDTO dto1 = new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, null, sampleDestinationWalletId, null,
                    new BigDecimal("250000.0"), sampleDate, null,
                    new BigDecimal("3300.0"), null, null,
                    new BigDecimal("750000.0"), new BigDecimal("1250000.0"), sampleCreatedAt
            );
            TransferResponseDTO dto2 = new TransferResponseDTO(
                    sampleTransferId, sampleSourceWalletId, null, sampleDestinationWalletId, null,
                    new BigDecimal("250000.00"), sampleDate, null,
                    new BigDecimal("3300.00"), null, null,
                    new BigDecimal("750000.00"), new BigDecimal("1250000.00"), sampleCreatedAt
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertTrue(dto1.toString().contains("sourceWalletName='null'"));
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

        @Test
        @DisplayName("Không còn cung cấp các getter bí danh getId() và isHasFee()")
        void shouldNotExposeLegacyAliasGetters() {
            assertThrows(NoSuchMethodException.class, () -> TransferResponseDTO.class.getMethod("getId"));
            assertThrows(NoSuchMethodException.class, () -> TransferResponseDTO.class.getMethod("isHasFee"));
        }
    }
}

