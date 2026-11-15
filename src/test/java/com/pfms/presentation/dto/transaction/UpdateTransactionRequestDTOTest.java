package com.pfms.presentation.dto.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử UpdateTransactionRequestDTO")
class UpdateTransactionRequestDTOTest {

    private final UUID sampleTxnId = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private final UUID sampleWalletId = UUID.fromString("00000000-0000-0000-0000-000000000015");
    private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private final UUID sampleFeeCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000030");
    private final LocalDate sampleDate = LocalDate.of(2026, 11, 11);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 8 trường dữ liệu cập nhật gồm ví mới và phí mới")
        void shouldCreateWithAllFieldsWalletAndFee() {
            BigDecimal amount = new BigDecimal("450000.00");
            BigDecimal fee = new BigDecimal("7000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    amount,
                    sampleDate,
                    sampleCategoryId,
                    "Tiền tiếp khách đối tác",
                    fee,
                    sampleFeeCategoryId
            );

            assertEquals(sampleTxnId, dto.getTransactionId());
            assertEquals(sampleWalletId, dto.getNewWalletId());
            assertEquals(amount, dto.getNewAmount());
            assertEquals(sampleDate, dto.getNewDate());
            assertEquals(sampleCategoryId, dto.getNewCategoryId());
            assertEquals("Tiền tiếp khách đối tác", dto.getNewNote());
            assertEquals(fee, dto.getNewFeeAmount());
            assertEquals(sampleFeeCategoryId, dto.getNewFeeCategoryId());
            assertTrue(dto.hasFee());
        }

        @Test
        @DisplayName("Khởi tạo 6 tham số kèm ví mới mặc định giữ nguyên phí (newFeeAmount null)")
        void shouldCreateWith6ParamsKeepingFeeUnset() {
            BigDecimal amount = new BigDecimal("600000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    amount,
                    sampleDate,
                    sampleCategoryId,
                    "Mua phụ kiện máy tính"
            );

            assertEquals(sampleTxnId, dto.getTransactionId());
            assertEquals(sampleWalletId, dto.getNewWalletId());
            assertEquals(amount, dto.getNewAmount());
            assertEquals(sampleDate, dto.getNewDate());
            assertEquals(sampleCategoryId, dto.getNewCategoryId());
            assertEquals("Mua phụ kiện máy tính", dto.getNewNote());
            assertNull(dto.getNewFeeAmount());
            assertNull(dto.getNewFeeCategoryId());
            assertFalse(dto.hasFee());
        }

        @Test
        @DisplayName("Khởi tạo tối giản 4 tham số kèm ví mới mặc định ngày hiện tại, ghi chú rỗng và newFeeAmount null")
        void shouldCreateMinimalWith4ParamsKeepingFeeUnset() {
            BigDecimal amount = new BigDecimal("85000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    amount,
                    sampleCategoryId
            );

            assertEquals(sampleTxnId, dto.getTransactionId());
            assertEquals(sampleWalletId, dto.getNewWalletId());
            assertEquals(amount, dto.getNewAmount());
            assertEquals(LocalDate.now(), dto.getNewDate());
            assertEquals(sampleCategoryId, dto.getNewCategoryId());
            assertEquals("", dto.getNewNote());
            assertNull(dto.getNewFeeAmount());
            assertNull(dto.getNewFeeCategoryId());
            assertFalse(dto.hasFee());
        }

        @Test
        @DisplayName("Chuẩn hóa ghi chú và ngày null đồng thời giữ nguyên newFeeAmount null để bảo toàn phí hiện tại")
        void shouldPreserveNullFeeAmountAndNormalizeDateAndNote() {
            BigDecimal amount = new BigDecimal("150000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    amount,
                    null,
                    sampleCategoryId,
                    "   Ăn tối   ",
                    null,
                    sampleFeeCategoryId
            );

            assertEquals(sampleWalletId, dto.getNewWalletId());
            assertEquals(LocalDate.now(), dto.getNewDate());
            assertEquals("Ăn tối", dto.getNewNote());
            assertNull(dto.getNewFeeAmount());
            assertNull(dto.getNewFeeCategoryId());
            assertFalse(dto.hasFee());
        }

        @Test
        @DisplayName("Chấp nhận mức phí bằng 0 kèm danh mục phí null để biểu diễn ý định xóa phí hiện có")
        void shouldAcceptZeroFeeWithNullCategory() {
            BigDecimal amount = new BigDecimal("200000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    amount,
                    sampleDate,
                    sampleCategoryId,
                    "Tiền điện thoại",
                    BigDecimal.ZERO,
                    sampleFeeCategoryId
            );

            assertEquals(BigDecimal.ZERO, dto.getNewFeeAmount());
            assertNull(dto.getNewFeeCategoryId());
            assertFalse(dto.hasFee());
        }
    }

    @Nested
    @DisplayName("Kiểm soát Cấu trúc (Null-Check) và Cho phép Dữ liệu Nghiệp vụ Đi qua Tới Domain")
    class PreconditionAndPassthroughTests {

        @Test
        @DisplayName("Ném NullPointerException khi mã giao dịch transactionId bị null")
        void shouldThrowWhenTransactionIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(
                            null, sampleWalletId, new BigDecimal("100000"), sampleCategoryId)
            );
            assertTrue(ex.getMessage().contains("Mã giao dịch"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi mã ví mới newWalletId bị null ở các constructor mới")
        void shouldThrowWhenNewWalletIdIsNull() {
            NullPointerException ex4 = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId, null, new BigDecimal("100000"), sampleCategoryId)
            );
            NullPointerException ex6 = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId, null, new BigDecimal("100000"), sampleDate, sampleCategoryId, "note")
            );
            NullPointerException ex8 = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId, null, new BigDecimal("100000"), sampleDate, sampleCategoryId,
                            "note", null, null)
            );
            assertTrue(ex4.getMessage().contains("Mã ví mới"));
            assertTrue(ex6.getMessage().contains("Mã ví mới"));
            assertTrue(ex8.getMessage().contains("Mã ví mới"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi số tiền newAmount bị null")
        void shouldThrowWhenNewAmountIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(sampleTxnId, sampleWalletId, null, sampleCategoryId)
            );
            assertTrue(ex.getMessage().contains("Số tiền"));
        }

        @Test
        @DisplayName("Cho phép số tiền newAmount bằng 0 đi qua DTO để Domain/Service xử lý ERR_FIN_001")
        void shouldAllowZeroNewAmountPassthroughForDomainValidation() {
            UpdateTransactionRequestDTO dto = assertDoesNotThrow(
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId, sampleWalletId, BigDecimal.ZERO, sampleCategoryId)
            );
            assertEquals(0, BigDecimal.ZERO.compareTo(dto.getNewAmount()));
        }

        @ParameterizedTest(name = "Số tiền mới âm {0} được giữ nguyên để Domain/Service ném ERR_FIN_001")
        @ValueSource(strings = {"-0.01", "-50000", "-1000000.00"})
        @DisplayName("Cho phép số tiền newAmount âm đi qua DTO để Domain/Service xử lý ERR_FIN_001")
        void shouldAllowNegativeNewAmountPassthroughForDomainValidation(String negativeAmount) {
            BigDecimal expected = new BigDecimal(negativeAmount);
            UpdateTransactionRequestDTO dto = assertDoesNotThrow(
                    () -> new UpdateTransactionRequestDTO(sampleTxnId, sampleWalletId, expected, sampleCategoryId)
            );
            assertEquals(0, expected.compareTo(dto.getNewAmount()));
        }

        @Test
        @DisplayName("Ném NullPointerException khi mã danh mục newCategoryId bị null")
        void shouldThrowWhenNewCategoryIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId, sampleWalletId, new BigDecimal("100000"), null)
            );
            assertTrue(ex.getMessage().contains("Mã danh mục mới"));
        }

        @ParameterizedTest(name = "Mức phí mới âm {0} đi qua DTO và tự đưa newFeeCategoryId về null")
        @ValueSource(strings = {"-0.01", "-500", "-10000.00"})
        @DisplayName("Cho phép mức phí newFeeAmount âm đi qua DTO để Domain/Service xử lý ERR_FEE_001")
        void shouldAllowNegativeNewFeeAmountPassthroughForDomainValidation(String negativeFee) {
            BigDecimal expectedFee = new BigDecimal(negativeFee);
            UpdateTransactionRequestDTO dto = assertDoesNotThrow(
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId,
                            sampleWalletId,
                            new BigDecimal("100000"),
                            sampleDate,
                            sampleCategoryId,
                            "",
                            expectedFee,
                            sampleFeeCategoryId
                    )
            );
            assertEquals(0, expectedFee.compareTo(dto.getNewFeeAmount()));
            assertNull(dto.getNewFeeCategoryId());
            assertFalse(dto.hasFee());
        }

        @Test
        @DisplayName("Cho phép mức phí mới dương nhưng newFeeCategoryId null đi qua DTO để Domain xử lý ERR_FEE_002")
        void shouldAllowPositiveFeeWithNullFeeCategoryIdPassthrough() {
            UpdateTransactionRequestDTO dto = assertDoesNotThrow(
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId,
                            sampleWalletId,
                            new BigDecimal("100000"),
                            sampleDate,
                            sampleCategoryId,
                            "",
                            new BigDecimal("5000.00"),
                            null
                    )
            );
            assertEquals(0, new BigDecimal("5000.00").compareTo(dto.getNewFeeAmount()));
            assertNull(dto.getNewFeeCategoryId());
            assertTrue(dto.hasFee());
        }

        @Test
        @DisplayName("Cho phép ngày giao dịch tương lai và ghi chú vượt 255 ký tự đi qua DTO")
        void shouldAllowFutureNewDateAndOversizedNewNotePassthrough() {
            LocalDate futureDate = LocalDate.now().plusDays(1);
            String oversizedNote = "b".repeat(256);

            UpdateTransactionRequestDTO dto = assertDoesNotThrow(
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId,
                            sampleWalletId,
                            new BigDecimal("100000"),
                            futureDate,
                            sampleCategoryId,
                            oversizedNote
                    )
            );

            assertEquals(futureDate, dto.getNewDate());
            assertEquals(oversizedNote, dto.getNewNote());
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (equals, hashCode, toString)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai đối tượng bằng nhau khi cùng thuộc tính dù khác scale số học (kể cả số âm)")
        void shouldBeEqualRegardlessOfScale() {
            UpdateTransactionRequestDTO dto1 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("300000.0"),
                    sampleDate,
                    sampleCategoryId,
                    "Ăn trưa",
                    new BigDecimal("3000.0"),
                    sampleFeeCategoryId
            );

            UpdateTransactionRequestDTO dto2 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("300000.00"),
                    sampleDate,
                    sampleCategoryId,
                    "Ăn trưa",
                    new BigDecimal("3000.00"),
                    sampleFeeCategoryId
            );

            UpdateTransactionRequestDTO dtoNeg1 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("-100"),
                    sampleDate,
                    sampleCategoryId,
                    "Test âm",
                    new BigDecimal("-50"),
                    null
            );

            UpdateTransactionRequestDTO dtoNeg2 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("-100.00"),
                    sampleDate,
                    sampleCategoryId,
                    "Test âm",
                    new BigDecimal("-50.00"),
                    null
            );

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertEquals(dtoNeg1, dtoNeg2);
            assertEquals(dtoNeg1.hashCode(), dtoNeg2.hashCode());
        }

        @Test
        @DisplayName("An toàn equals, hashCode khi newFeeAmount null và phân biệt rõ null (giữ phí) với ZERO (xóa phí)")
        void shouldDistinguishNullFeeFromZeroFeeInIdentityContract() {
            UpdateTransactionRequestDTO dtoNullFee1 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("100000"),
                    sampleDate,
                    sampleCategoryId,
                    "Giữ phí cũ",
                    null,
                    null
            );
            UpdateTransactionRequestDTO dtoNullFee2 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("100000.00"),
                    sampleDate,
                    sampleCategoryId,
                    "Giữ phí cũ",
                    null,
                    null
            );
            UpdateTransactionRequestDTO dtoZeroFee = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("100000"),
                    sampleDate,
                    sampleCategoryId,
                    "Giữ phí cũ",
                    BigDecimal.ZERO,
                    null
            );
            UpdateTransactionRequestDTO dtoZeroScaled = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("100000"),
                    sampleDate,
                    sampleCategoryId,
                    "Giữ phí cũ",
                    new BigDecimal("0.00"),
                    null
            );

            assertEquals(dtoNullFee1, dtoNullFee2);
            assertEquals(dtoNullFee1.hashCode(), dtoNullFee2.hashCode());
            assertEquals(dtoZeroFee, dtoZeroScaled);
            assertEquals(dtoZeroFee.hashCode(), dtoZeroScaled.hashCode());
            assertNotEquals(dtoNullFee1, dtoZeroFee);
            assertNotEquals(dtoZeroFee, dtoNullFee1);
        }

        @Test
        @DisplayName("An toàn equals, hashCode và toString khi phí mới dương nhưng newFeeCategoryId bị null")
        void shouldHandleNullNewFeeCategoryIdSafelyInIdentityContract() {
            UpdateTransactionRequestDTO dto1 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("100000"),
                    sampleDate,
                    sampleCategoryId,
                    "Phí thiếu danh mục",
                    new BigDecimal("5000"),
                    null
            );
            UpdateTransactionRequestDTO dto2 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("100000"),
                    sampleDate,
                    sampleCategoryId,
                    "Phí thiếu danh mục",
                    new BigDecimal("5000.00"),
                    null
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertTrue(dto1.toString().contains("newFeeCategoryId=null"));
        }

        @Test
        @DisplayName("Không bằng nhau nếu khác bất kỳ trường dữ liệu nào")
        void shouldNotEqualWhenFieldsDiffer() {
            UpdateTransactionRequestDTO base = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("200000"),
                    sampleDate,
                    sampleCategoryId,
                    "Tiền nước",
                    BigDecimal.ZERO,
                    null
            );

            UpdateTransactionRequestDTO diffTxn = new UpdateTransactionRequestDTO(
                    UUID.randomUUID(),
                    sampleWalletId,
                    new BigDecimal("200000"),
                    sampleDate,
                    sampleCategoryId,
                    "Tiền nước",
                    BigDecimal.ZERO,
                    null
            );

            UpdateTransactionRequestDTO diffWallet = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    UUID.randomUUID(),
                    new BigDecimal("200000"),
                    sampleDate,
                    sampleCategoryId,
                    "Tiền nước",
                    BigDecimal.ZERO,
                    null
            );

            UpdateTransactionRequestDTO diffAmount = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("350000"),
                    sampleDate,
                    sampleCategoryId,
                    "Tiền nước",
                    BigDecimal.ZERO,
                    null
            );

            assertNotEquals(base, diffTxn);
            assertNotEquals(base, diffWallet);
            assertNotEquals(base, diffAmount);
            assertNotEquals(base, null);
            assertNotEquals(base, "some string");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và các thông tin cơ bản")
        void shouldProduceMeaningfulToString() {
            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    sampleWalletId,
                    new BigDecimal("180000"),
                    sampleDate,
                    sampleCategoryId,
                    "Ghi chú sửa",
                    new BigDecimal("2500"),
                    sampleFeeCategoryId
            );

            String str = dto.toString();
            assertTrue(str.contains("UpdateTransactionRequestDTO"));
            assertTrue(str.contains(sampleTxnId.toString()));
            assertTrue(str.contains(sampleWalletId.toString()));
            assertTrue(str.contains("180000"));
            assertTrue(str.contains(sampleCategoryId.toString()));
            assertTrue(str.contains("Ghi chú sửa"));
            assertTrue(str.contains("2500"));
        }
    }
}

