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

@DisplayName("Kiểm thử RecordTransactionRequestDTO")
class RecordTransactionRequestDTOTest {

        private final UUID sampleWalletId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        private final UUID sampleFeeCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        private final LocalDate sampleDate = LocalDate.of(2026, 11, 10);

        @Nested
        @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
        class ValidCreationTests {

                @Test
                @DisplayName("Khởi tạo thành công với đầy đủ 7 trường dữ liệu kèm phí phát sinh")
                void shouldCreateWithAllFieldsAndFee() {
                        BigDecimal amount = new BigDecimal("250000.00");
                        BigDecimal fee = new BigDecimal("5500.00");

                        RecordTransactionRequestDTO dto = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        amount,
                                        sampleDate,
                                        "Ăn trưa với đồng nghiệp",
                                        fee,
                                        sampleFeeCategoryId);

                        assertEquals(sampleWalletId, dto.getWalletId());
                        assertEquals(sampleCategoryId, dto.getCategoryId());
                        assertEquals(amount, dto.getAmount());
                        assertEquals(sampleDate, dto.getTransactionDate());
                        assertEquals("Ăn trưa với đồng nghiệp", dto.getNote());
                        assertEquals(fee, dto.getFeeAmount());
                        assertEquals(sampleFeeCategoryId, dto.getFeeCategoryId());
                        assertTrue(dto.hasFee());
                }

                @Test
                @DisplayName("Khởi tạo không có phí với hàm khởi tạo 5 tham số (feeAmount mặc định ZERO, feeCategoryId null)")
                void shouldCreateWithoutFeeUsing5Params() {
                        BigDecimal amount = new BigDecimal("500000.00");

                        RecordTransactionRequestDTO dto = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        amount,
                                        sampleDate,
                                        "Mua sách giáo trình");

                        assertEquals(sampleWalletId, dto.getWalletId());
                        assertEquals(sampleCategoryId, dto.getCategoryId());
                        assertEquals(amount, dto.getAmount());
                        assertEquals(sampleDate, dto.getTransactionDate());
                        assertEquals("Mua sách giáo trình", dto.getNote());
                        assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
                        assertNull(dto.getFeeCategoryId());
                        assertFalse(dto.hasFee());
                }

                @Test
                @DisplayName("Khởi tạo tối giản 3 tham số (tự gán ngày hiện tại, ghi chú rỗng và không phí)")
                void shouldCreateMinimalWith3Params() {
                        BigDecimal amount = new BigDecimal("120000.00");

                        RecordTransactionRequestDTO dto = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        amount);

                        assertEquals(sampleWalletId, dto.getWalletId());
                        assertEquals(sampleCategoryId, dto.getCategoryId());
                        assertEquals(amount, dto.getAmount());
                        assertEquals(LocalDate.now(), dto.getTransactionDate());
                        assertEquals("", dto.getNote());
                        assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
                        assertNull(dto.getFeeCategoryId());
                        assertFalse(dto.hasFee());
                }

                @Test
                @DisplayName("Tự động chuẩn hóa khoảng trắng của ghi chú và chuyển null thành chuỗi rỗng / ngày hiện tại / phí ZERO")
                void shouldNormalizeNullsAndWhitespace() {
                        BigDecimal amount = new BigDecimal("100000.00");

                        RecordTransactionRequestDTO dto = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        amount,
                                        null,
                                        "   Cà phê sáng   ",
                                        null,
                                        null);

                        assertEquals(LocalDate.now(), dto.getTransactionDate());
                        assertEquals("Cà phê sáng", dto.getNote());
                        assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
                        assertNull(dto.getFeeCategoryId());
                        assertFalse(dto.hasFee());
                }

                @Test
                @DisplayName("Chấp nhận mức phí bằng 0 kèm danh mục phí null")
                void shouldAcceptZeroFeeWithNullCategory() {
                        BigDecimal amount = new BigDecimal("350000.00");

                        RecordTransactionRequestDTO dto = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        amount,
                                        sampleDate,
                                        "Đổ xăng",
                                        BigDecimal.ZERO,
                                        null);

                        assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
                        assertNull(dto.getFeeCategoryId());
                        assertFalse(dto.hasFee());
                }
        }

        @Nested
        @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
        class InvalidInputTests {

                @Test
                @DisplayName("Ném NullPointerException khi mã ví walletId bị null")
                void shouldThrowWhenWalletIdIsNull() {
                        NullPointerException ex = assertThrows(
                                        NullPointerException.class,
                                        () -> new RecordTransactionRequestDTO(null, sampleCategoryId,
                                                        new BigDecimal("100000")));
                        assertTrue(ex.getMessage().contains("Mã ví"));
                }

                @Test
                @DisplayName("Ném NullPointerException khi mã danh mục categoryId bị null")
                void shouldThrowWhenCategoryIdIsNull() {
                        NullPointerException ex = assertThrows(
                                        NullPointerException.class,
                                        () -> new RecordTransactionRequestDTO(sampleWalletId, null,
                                                        new BigDecimal("100000")));
                        assertTrue(ex.getMessage().contains("Mã danh mục"));
                }

                @Test
                @DisplayName("Ném NullPointerException khi số tiền amount bị null")
                void shouldThrowWhenAmountIsNull() {
                        NullPointerException ex = assertThrows(
                                        NullPointerException.class,
                                        () -> new RecordTransactionRequestDTO(sampleWalletId, sampleCategoryId, null));
                        assertTrue(ex.getMessage().contains("Số tiền"));
                }

                @Test
                @DisplayName("Ném IllegalArgumentException khi số tiền amount bằng 0")
                void shouldThrowWhenAmountIsZero() {
                        IllegalArgumentException ex = assertThrows(
                                        IllegalArgumentException.class,
                                        () -> new RecordTransactionRequestDTO(sampleWalletId, sampleCategoryId,
                                                        BigDecimal.ZERO));
                        assertTrue(ex.getMessage().contains("Số tiền giao dịch phải lớn hơn 0"));
                }

                @ParameterizedTest(name = "Số tiền âm {0} phải bị từ chối")
                @ValueSource(strings = { "-0.01", "-1000", "-500000.00" })
                @DisplayName("Ném IllegalArgumentException khi số tiền amount âm")
                void shouldThrowWhenAmountIsNegative(String invalidAmount) {
                        IllegalArgumentException ex = assertThrows(
                                        IllegalArgumentException.class,
                                        () -> new RecordTransactionRequestDTO(sampleWalletId, sampleCategoryId,
                                                        new BigDecimal(invalidAmount)));
                        assertTrue(ex.getMessage().contains("Số tiền giao dịch phải lớn hơn 0"));
                }

                @ParameterizedTest(name = "Mức phí âm {0} phải bị từ chối")
                @ValueSource(strings = { "-0.01", "-100", "-5000.00" })
                @DisplayName("Ném IllegalArgumentException khi mức phí feeAmount âm")
                void shouldThrowWhenFeeAmountIsNegative(String invalidFee) {
                        IllegalArgumentException ex = assertThrows(
                                        IllegalArgumentException.class,
                                        () -> new RecordTransactionRequestDTO(
                                                        sampleWalletId,
                                                        sampleCategoryId,
                                                        new BigDecimal("100000"),
                                                        sampleDate,
                                                        "",
                                                        new BigDecimal(invalidFee),
                                                        sampleFeeCategoryId));
                        assertTrue(ex.getMessage().contains("Mức phí giao dịch không được âm"));
                }

                @Test
                @DisplayName("Ném IllegalArgumentException khi mức phí dương nhưng mã danh mục phí bị null")
                void shouldThrowWhenFeeIsPositiveButFeeCategoryIdIsNull() {
                        IllegalArgumentException ex = assertThrows(
                                        IllegalArgumentException.class,
                                        () -> new RecordTransactionRequestDTO(
                                                        sampleWalletId,
                                                        sampleCategoryId,
                                                        new BigDecimal("100000"),
                                                        sampleDate,
                                                        "",
                                                        new BigDecimal("5000.00"),
                                                        null));
                        assertTrue(ex.getMessage().contains("danh mục phí không được để trống"));
                }
        }

        @Nested
        @DisplayName("Hợp đồng So sánh và Định danh (equals, hashCode, toString)")
        class EqualityAndContractTests {

                @Test
                @DisplayName("Hai đối tượng bằng nhau khi cùng thuộc tính dù khác scale số học")
                void shouldBeEqualRegardlessOfScale() {
                        RecordTransactionRequestDTO dto1 = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        new BigDecimal("200000.0"),
                                        sampleDate,
                                        "Ăn trưa",
                                        new BigDecimal("5000.0"),
                                        sampleFeeCategoryId);

                        RecordTransactionRequestDTO dto2 = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        new BigDecimal("200000.00"),
                                        sampleDate,
                                        "Ăn trưa",
                                        new BigDecimal("5000.00"),
                                        sampleFeeCategoryId);

                        assertEquals(dto1, dto2);
                        assertEquals(dto1.hashCode(), dto2.hashCode());
                }

                @Test
                @DisplayName("Không bằng nhau nếu khác bất kỳ trường dữ liệu nào")
                void shouldNotEqualWhenFieldsDiffer() {
                        RecordTransactionRequestDTO base = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        new BigDecimal("200000"),
                                        sampleDate,
                                        "Tiền điện",
                                        BigDecimal.ZERO,
                                        null);

                        RecordTransactionRequestDTO diffWallet = new RecordTransactionRequestDTO(
                                        UUID.randomUUID(),
                                        sampleCategoryId,
                                        new BigDecimal("200000"),
                                        sampleDate,
                                        "Tiền điện",
                                        BigDecimal.ZERO,
                                        null);

                        RecordTransactionRequestDTO diffAmount = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        new BigDecimal("300000"),
                                        sampleDate,
                                        "Tiền điện",
                                        BigDecimal.ZERO,
                                        null);

                        assertNotEquals(base, diffWallet);
                        assertNotEquals(base, diffAmount);
                        assertNotEquals(base, null);
                        assertNotEquals(base, "some string");
                }

                @Test
                @DisplayName("toString chứa đầy đủ tên lớp và các thông tin cơ bản")
                void shouldProduceMeaningfulToString() {
                        RecordTransactionRequestDTO dto = new RecordTransactionRequestDTO(
                                        sampleWalletId,
                                        sampleCategoryId,
                                        new BigDecimal("150000"),
                                        sampleDate,
                                        "Ghi chú test",
                                        new BigDecimal("2000"),
                                        sampleFeeCategoryId);

                        String str = dto.toString();
                        assertTrue(str.contains("RecordTransactionRequestDTO"));
                        assertTrue(str.contains(sampleWalletId.toString()));
                        assertTrue(str.contains(sampleCategoryId.toString()));
                        assertTrue(str.contains("150000"));
                        assertTrue(str.contains("Ghi chú test"));
                        assertTrue(str.contains("2000"));
                }
        }
}
