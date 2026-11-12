package com.pfms.presentation.dto.transfer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử TransferRequestDTO")
class TransferRequestDTOTest {

    private final UUID sampleSourceWalletId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID sampleDestinationWalletId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID sampleFeeCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private final LocalDate sampleDate = LocalDate.of(2026, 11, 12);

    @Nested
    @DisplayName("Nhóm 1: Happy Path - Khởi tạo và đóng gói dữ liệu hợp lệ")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 7 trường dữ liệu kèm phí chuyển tiền")
        void shouldCreateWithAllFieldsAndFee() {
            BigDecimal amount = new BigDecimal("5000000.00");
            BigDecimal fee = new BigDecimal("3300.00");

            TransferRequestDTO dto = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    amount,
                    sampleDate,
                    "Chuyển tiền sang quỹ tiết kiệm",
                    fee,
                    sampleFeeCategoryId
            );

            assertEquals(sampleSourceWalletId, dto.getSourceWalletId());
            assertEquals(sampleDestinationWalletId, dto.getDestinationWalletId());
            assertEquals(amount, dto.getAmount());
            assertEquals(sampleDate, dto.getTransferDate());
            assertEquals("Chuyển tiền sang quỹ tiết kiệm", dto.getNote());
            assertEquals(fee, dto.getFeeAmount());
            assertEquals(sampleFeeCategoryId, dto.getFeeCategoryId());
            assertTrue(dto.hasFee());
        }

        @Test
        @DisplayName("Khởi tạo không có phí với hàm khởi tạo 5 tham số (feeAmount mặc định ZERO, feeCategoryId null)")
        void shouldCreateWithoutFeeUsing5Params() {
            BigDecimal amount = new BigDecimal("1500000.00");

            TransferRequestDTO dto = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    amount,
                    sampleDate,
                    "Rút tiền mặt ATM"
            );

            assertEquals(sampleSourceWalletId, dto.getSourceWalletId());
            assertEquals(sampleDestinationWalletId, dto.getDestinationWalletId());
            assertEquals(amount, dto.getAmount());
            assertEquals(sampleDate, dto.getTransferDate());
            assertEquals("Rút tiền mặt ATM", dto.getNote());
            assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
            assertNull(dto.getFeeCategoryId());
            assertFalse(dto.hasFee());
        }

        @Test
        @DisplayName("Khởi tạo tối giản 3 tham số (tự gán ngày hiện tại, ghi chú rỗng và không phí)")
        void shouldCreateMinimalWith3Params() {
            BigDecimal amount = new BigDecimal("200000.00");

            TransferRequestDTO dto = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    amount
            );

            assertEquals(sampleSourceWalletId, dto.getSourceWalletId());
            assertEquals(sampleDestinationWalletId, dto.getDestinationWalletId());
            assertEquals(amount, dto.getAmount());
            assertEquals(LocalDate.now(), dto.getTransferDate());
            assertEquals("", dto.getNote());
            assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
            assertNull(dto.getFeeCategoryId());
            assertFalse(dto.hasFee());
        }

        @Test
        @DisplayName("Chuẩn hóa mức phí bằng 0 kèm danh mục phí khác null về feeCategoryId = null")
        void shouldNormalizeFeeCategoryIdToNullWhenFeeIsZero() {
            BigDecimal amount = new BigDecimal("500000.00");

            TransferRequestDTO dto = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    amount,
                    sampleDate,
                    "Miễn phí chuyển khoản",
                    BigDecimal.ZERO,
                    sampleFeeCategoryId
            );

            assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
            assertNull(dto.getFeeCategoryId());
            assertFalse(dto.hasFee());
        }
    }

    @Nested
    @DisplayName("Nhóm 2: Boundary & Precision - Biên số học và độ chính xác thập phân")
    class BoundaryAndPrecisionTests {

        @ParameterizedTest(name = "Chấp nhận số tiền biên hợp lệ: {0}")
        @ValueSource(strings = {"0.01", "1", "100000000000.00", "1000000000000.00"})
        @DisplayName("Chấp nhận giá trị biên nhỏ nhất (0.01, 1) và giá trị cực lớn (10^12)")
        void shouldAcceptBoundaryPositiveAmounts(String validBoundaryAmount) {
            BigDecimal amount = new BigDecimal(validBoundaryAmount);

            TransferRequestDTO dto = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    amount
            );

            assertEquals(0, amount.compareTo(dto.getAmount()));
        }

        @Test
        @DisplayName("Hai đối tượng bằng nhau và cùng hashCode khi cùng giá trị số học dù khác scale")
        void shouldBeEqualRegardlessOfBigDecimalScale() {
            TransferRequestDTO dto1 = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    new BigDecimal("200000.0"),
                    sampleDate,
                    "Chuyển tiền",
                    new BigDecimal("5000.0"),
                    sampleFeeCategoryId
            );

            TransferRequestDTO dto2 = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    new BigDecimal("200000.00"),
                    sampleDate,
                    "Chuyển tiền",
                    new BigDecimal("5000.00"),
                    sampleFeeCategoryId
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("Nhóm 3: Negative & Zero Values - Kiểm soát số không và số âm")
    class NegativeAndZeroValueTests {

        @ParameterizedTest(name = "Số tiền bằng 0 ({0}) phải bị từ chối")
        @ValueSource(strings = {"0", "0.0", "0.00"})
        @DisplayName("Ném IllegalArgumentException khi số tiền chuyển bằng 0")
        void shouldThrowWhenAmountIsZero(String zeroAmount) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransferRequestDTO(
                            sampleSourceWalletId,
                            sampleDestinationWalletId,
                            new BigDecimal(zeroAmount)
                    )
            );
            assertTrue(ex.getMessage().contains("Số tiền chuyển phải lớn hơn 0"));
        }

        @ParameterizedTest(name = "Số tiền âm ({0}) phải bị từ chối")
        @ValueSource(strings = {"-0.01", "-1", "-500000.00"})
        @DisplayName("Ném IllegalArgumentException khi số tiền chuyển âm")
        void shouldThrowWhenAmountIsNegative(String negativeAmount) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransferRequestDTO(
                            sampleSourceWalletId,
                            sampleDestinationWalletId,
                            new BigDecimal(negativeAmount)
                    )
            );
            assertTrue(ex.getMessage().contains("Số tiền chuyển phải lớn hơn 0"));
        }

        @ParameterizedTest(name = "Mức phí âm ({0}) phải bị từ chối")
        @ValueSource(strings = {"-0.01", "-100", "-3300.00"})
        @DisplayName("Ném IllegalArgumentException khi mức phí chuyển tiền âm")
        void shouldThrowWhenFeeAmountIsNegative(String negativeFee) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransferRequestDTO(
                            sampleSourceWalletId,
                            sampleDestinationWalletId,
                            new BigDecimal("100000.00"),
                            sampleDate,
                            "Chuyển khoản",
                            new BigDecimal(negativeFee),
                            sampleFeeCategoryId
                    )
            );
            assertTrue(ex.getMessage().contains("Mức phí chuyển tiền không được âm"));
        }
    }

    @Nested
    @DisplayName("Nhóm 4: Temporal Invariants - Bất biến thời gian và lịch pháp")
    class TemporalInvariantTests {

        @Test
        @DisplayName("Chấp nhận ngày năm nhuận 29/02 và ngày cuối tháng")
        void shouldAcceptLeapYearAndMonthEndDates() {
            LocalDate leapDay = LocalDate.of(2024, 2, 29);
            LocalDate monthEnd = LocalDate.of(2026, 1, 31);

            TransferRequestDTO leapDto = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    new BigDecimal("100000.00"),
                    leapDay,
                    "Năm nhuận"
            );
            TransferRequestDTO monthEndDto = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    new BigDecimal("100000.00"),
                    monthEnd,
                    "Cuối tháng"
            );

            assertEquals(leapDay, leapDto.getTransferDate());
            assertEquals(monthEnd, monthEndDto.getTransferDate());
        }

        @Test
        @DisplayName("Tự động gán ngày hiện tại khi transferDate truyền vào là null")
        void shouldDefaultTransferDateToTodayWhenNull() {
            TransferRequestDTO dto = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    new BigDecimal("100000.00"),
                    null,
                    "   Nạp ví điện tử   ",
                    null,
                    null
            );

            assertEquals(LocalDate.now(), dto.getTransferDate());
            assertEquals("Nạp ví điện tử", dto.getNote());
            assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
            assertNull(dto.getFeeCategoryId());
        }
    }

    @Nested
    @DisplayName("Nhóm 5: State & Robustness - Kiểm soát tham số null, trùng ví và hợp đồng định danh")
    class StateAndRobustnessTests {

        @Test
        @DisplayName("Ném NullPointerException khi mã ví nguồn sourceWalletId bị null")
        void shouldThrowWhenSourceWalletIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransferRequestDTO(null, sampleDestinationWalletId, new BigDecimal("100000"))
            );
            assertTrue(ex.getMessage().contains("Mã ví nguồn"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi mã ví đích destinationWalletId bị null")
        void shouldThrowWhenDestinationWalletIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransferRequestDTO(sampleSourceWalletId, null, new BigDecimal("100000"))
            );
            assertTrue(ex.getMessage().contains("Mã ví đích"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi ví nguồn trùng với ví đích (UC-TRF-01 8a)")
        void shouldThrowWhenSourceAndDestinationWalletsAreSame() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransferRequestDTO(sampleSourceWalletId, sampleSourceWalletId, new BigDecimal("100000"))
            );
            assertTrue(ex.getMessage().contains("Ví nguồn và ví đích phải khác nhau"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi số tiền chuyển amount bị null")
        void shouldThrowWhenAmountIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransferRequestDTO(sampleSourceWalletId, sampleDestinationWalletId, null)
            );
            assertTrue(ex.getMessage().contains("Số tiền chuyển"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi mức phí dương nhưng mã danh mục phí bị null (UC-TRF-01 8d)")
        void shouldThrowWhenFeeIsPositiveButFeeCategoryIdIsNull() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransferRequestDTO(
                            sampleSourceWalletId,
                            sampleDestinationWalletId,
                            new BigDecimal("100000"),
                            sampleDate,
                            "Chuyển khoản có phí",
                            new BigDecimal("2200.00"),
                            null
                    )
            );
            assertTrue(ex.getMessage().contains("danh mục phí không được để trống"));
        }

        @Test
        @DisplayName("Không bằng nhau nếu khác bất kỳ trường dữ liệu nào và toString chứa đầy đủ thông tin")
        void shouldSatisfyEqualsHashCodeAndToStringContracts() {
            TransferRequestDTO base = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    new BigDecimal("200000"),
                    sampleDate,
                    "Ghi chú test",
                    new BigDecimal("1100"),
                    sampleFeeCategoryId
            );

            TransferRequestDTO diffSource = new TransferRequestDTO(
                    UUID.randomUUID(),
                    sampleDestinationWalletId,
                    new BigDecimal("200000"),
                    sampleDate,
                    "Ghi chú test",
                    new BigDecimal("1100"),
                    sampleFeeCategoryId
            );

            TransferRequestDTO diffAmount = new TransferRequestDTO(
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    new BigDecimal("300000"),
                    sampleDate,
                    "Ghi chú test",
                    new BigDecimal("1100"),
                    sampleFeeCategoryId
            );

            assertEquals(base, base);
            assertNotEquals(base, diffSource);
            assertNotEquals(base, diffAmount);
            assertNotEquals(base, null);
            assertNotEquals(base, "invalid type");

            String str = base.toString();
            assertTrue(str.contains("TransferRequestDTO"));
            assertTrue(str.contains(sampleSourceWalletId.toString()));
            assertTrue(str.contains(sampleDestinationWalletId.toString()));
            assertTrue(str.contains("200000"));
            assertTrue(str.contains("Ghi chú test"));
            assertTrue(str.contains("1100"));
        }
    }
}

