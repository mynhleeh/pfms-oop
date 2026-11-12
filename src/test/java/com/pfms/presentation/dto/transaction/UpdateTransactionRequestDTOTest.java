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
    private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private final UUID sampleFeeCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000030");
    private final LocalDate sampleDate = LocalDate.of(2026, 11, 11);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 7 trường dữ liệu cập nhật kèm phí mới")
        void shouldCreateWithAllFieldsAndFee() {
            BigDecimal amount = new BigDecimal("450000.00");
            BigDecimal fee = new BigDecimal("7000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    amount,
                    sampleDate,
                    sampleCategoryId,
                    "Tiền tiếp khách đối tác",
                    fee,
                    sampleFeeCategoryId
            );

            assertEquals(sampleTxnId, dto.getTransactionId());
            assertEquals(amount, dto.getNewAmount());
            assertEquals(sampleDate, dto.getNewDate());
            assertEquals(sampleCategoryId, dto.getNewCategoryId());
            assertEquals("Tiền tiếp khách đối tác", dto.getNewNote());
            assertEquals(fee, dto.getNewFeeAmount());
            assertEquals(sampleFeeCategoryId, dto.getNewFeeCategoryId());
            assertTrue(dto.hasFee());
        }

        @Test
        @DisplayName("Khởi tạo không có phí với hàm khởi tạo 5 tham số (newFeeAmount mặc định ZERO, newFeeCategoryId null)")
        void shouldCreateWithoutFeeUsing5Params() {
            BigDecimal amount = new BigDecimal("600000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    amount,
                    sampleDate,
                    sampleCategoryId,
                    "Mua phụ kiện máy tính"
            );

            assertEquals(sampleTxnId, dto.getTransactionId());
            assertEquals(amount, dto.getNewAmount());
            assertEquals(sampleDate, dto.getNewDate());
            assertEquals(sampleCategoryId, dto.getNewCategoryId());
            assertEquals("Mua phụ kiện máy tính", dto.getNewNote());
            assertEquals(BigDecimal.ZERO, dto.getNewFeeAmount());
            assertNull(dto.getNewFeeCategoryId());
            assertFalse(dto.hasFee());
        }

        @Test
        @DisplayName("Khởi tạo tối giản 3 tham số (tự gán ngày hiện tại, ghi chú rỗng và không phí)")
        void shouldCreateMinimalWith3Params() {
            BigDecimal amount = new BigDecimal("85000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    amount,
                    sampleCategoryId
            );

            assertEquals(sampleTxnId, dto.getTransactionId());
            assertEquals(amount, dto.getNewAmount());
            assertEquals(LocalDate.now(), dto.getNewDate());
            assertEquals(sampleCategoryId, dto.getNewCategoryId());
            assertEquals("", dto.getNewNote());
            assertEquals(BigDecimal.ZERO, dto.getNewFeeAmount());
            assertNull(dto.getNewFeeCategoryId());
            assertFalse(dto.hasFee());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa khoảng trắng của ghi chú và chuyển null thành chuỗi rỗng / ngày hiện tại / phí ZERO")
        void shouldNormalizeNullsAndWhitespace() {
            BigDecimal amount = new BigDecimal("150000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    amount,
                    null,
                    sampleCategoryId,
                    "   Ăn tối   ",
                    null,
                    null
            );

            assertEquals(LocalDate.now(), dto.getNewDate());
            assertEquals("Ăn tối", dto.getNewNote());
            assertEquals(BigDecimal.ZERO, dto.getNewFeeAmount());
            assertNull(dto.getNewFeeCategoryId());
            assertFalse(dto.hasFee());
        }

        @Test
        @DisplayName("Chấp nhận mức phí bằng 0 kèm danh mục phí null")
        void shouldAcceptZeroFeeWithNullCategory() {
            BigDecimal amount = new BigDecimal("200000.00");

            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    amount,
                    sampleDate,
                    sampleCategoryId,
                    "Tiền điện thoại",
                    BigDecimal.ZERO,
                    null
            );

            assertEquals(BigDecimal.ZERO, dto.getNewFeeAmount());
            assertNull(dto.getNewFeeCategoryId());
            assertFalse(dto.hasFee());
        }
    }

    @Nested
    @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
    class InvalidInputTests {

        @Test
        @DisplayName("Ném NullPointerException khi mã giao dịch transactionId bị null")
        void shouldThrowWhenTransactionIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(null, new BigDecimal("100000"), sampleCategoryId)
            );
            assertTrue(ex.getMessage().contains("Mã giao dịch"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi số tiền newAmount bị null")
        void shouldThrowWhenNewAmountIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(sampleTxnId, null, sampleCategoryId)
            );
            assertTrue(ex.getMessage().contains("Số tiền"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền newAmount bằng 0")
        void shouldThrowWhenNewAmountIsZero() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new UpdateTransactionRequestDTO(sampleTxnId, BigDecimal.ZERO, sampleCategoryId)
            );
            assertTrue(ex.getMessage().contains("Số tiền giao dịch mới phải lớn hơn 0"));
        }

        @ParameterizedTest(name = "Số tiền mới âm {0} phải bị từ chối")
        @ValueSource(strings = {"-0.01", "-50000", "-1000000.00"})
        @DisplayName("Ném IllegalArgumentException khi số tiền newAmount âm")
        void shouldThrowWhenNewAmountIsNegative(String invalidAmount) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new UpdateTransactionRequestDTO(sampleTxnId, new BigDecimal(invalidAmount), sampleCategoryId)
            );
            assertTrue(ex.getMessage().contains("Số tiền giao dịch mới phải lớn hơn 0"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi mã danh mục newCategoryId bị null")
        void shouldThrowWhenNewCategoryIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(sampleTxnId, new BigDecimal("100000"), null)
            );
            assertTrue(ex.getMessage().contains("Mã danh mục mới"));
        }

        @ParameterizedTest(name = "Mức phí mới âm {0} phải bị từ chối")
        @ValueSource(strings = {"-0.01", "-500", "-10000.00"})
        @DisplayName("Ném IllegalArgumentException khi mức phí newFeeAmount âm")
        void shouldThrowWhenNewFeeAmountIsNegative(String invalidFee) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId,
                            new BigDecimal("100000"),
                            sampleDate,
                            sampleCategoryId,
                            "",
                            new BigDecimal(invalidFee),
                            sampleFeeCategoryId
                    )
            );
            assertTrue(ex.getMessage().contains("Mức phí giao dịch mới không được âm"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi mức phí mới dương nhưng mã danh mục phí bị null")
        void shouldThrowWhenFeeIsPositiveButFeeCategoryIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new UpdateTransactionRequestDTO(
                            sampleTxnId,
                            new BigDecimal("100000"),
                            sampleDate,
                            sampleCategoryId,
                            "",
                            new BigDecimal("5000.00"),
                            null
                    )
            );
            assertTrue(ex.getMessage().contains("danh mục phí mới không được để trống"));
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (equals, hashCode, toString)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai đối tượng bằng nhau khi cùng thuộc tính dù khác scale số học")
        void shouldBeEqualRegardlessOfScale() {
            UpdateTransactionRequestDTO dto1 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    new BigDecimal("300000.0"),
                    sampleDate,
                    sampleCategoryId,
                    "Ăn trưa",
                    new BigDecimal("3000.0"),
                    sampleFeeCategoryId
            );

            UpdateTransactionRequestDTO dto2 = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    new BigDecimal("300000.00"),
                    sampleDate,
                    sampleCategoryId,
                    "Ăn trưa",
                    new BigDecimal("3000.00"),
                    sampleFeeCategoryId
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Không bằng nhau nếu khác bất kỳ trường dữ liệu nào")
        void shouldNotEqualWhenFieldsDiffer() {
            UpdateTransactionRequestDTO base = new UpdateTransactionRequestDTO(
                    sampleTxnId,
                    new BigDecimal("200000"),
                    sampleDate,
                    sampleCategoryId,
                    "Tiền nước",
                    BigDecimal.ZERO,
                    null
            );

            UpdateTransactionRequestDTO diffTxn = new UpdateTransactionRequestDTO(
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
                    new BigDecimal("350000"),
                    sampleDate,
                    sampleCategoryId,
                    "Tiền nước",
                    BigDecimal.ZERO,
                    null
            );

            assertNotEquals(base, diffTxn);
            assertNotEquals(base, diffAmount);
            assertNotEquals(base, null);
            assertNotEquals(base, "some string");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và các thông tin cơ bản")
        void shouldProduceMeaningfulToString() {
            UpdateTransactionRequestDTO dto = new UpdateTransactionRequestDTO(
                    sampleTxnId,
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
            assertTrue(str.contains("180000"));
            assertTrue(str.contains(sampleCategoryId.toString()));
            assertTrue(str.contains("Ghi chú sửa"));
            assertTrue(str.contains("2500"));
        }
    }
}

