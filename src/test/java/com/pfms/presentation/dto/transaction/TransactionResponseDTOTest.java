package com.pfms.presentation.dto.transaction;

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

@DisplayName("Kiểm thử TransactionResponseDTO")
class TransactionResponseDTOTest {

    private final UUID sampleTxnId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID sampleWalletId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private final UUID sampleFeeCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private final LocalDate sampleDate = LocalDate.of(2026, 11, 10);
    private final LocalDateTime sampleCreatedAt = LocalDateTime.of(2026, 11, 10, 14, 30);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 15 trường dữ liệu kèm phụ phí")
        void shouldCreateWithAllFieldsAndFee() {
            BigDecimal amount = new BigDecimal("350000.00");
            BigDecimal fee = new BigDecimal("5500.00");
            BigDecimal balanceAfter = new BigDecimal("4644500.00");

            TransactionResponseDTO dto = new TransactionResponseDTO(
                    sampleTxnId,
                    sampleWalletId,
                    "Ví Tiền mặt",
                    sampleCategoryId,
                    "Ăn uống",
                    amount,
                    "EXPENSE",
                    sampleDate,
                    "Ăn trưa với khách hàng",
                    true,
                    fee,
                    sampleFeeCategoryId,
                    "Phí dịch vụ",
                    balanceAfter,
                    sampleCreatedAt
            );

            assertEquals(sampleTxnId, dto.getTransactionId());
            assertEquals(sampleTxnId, dto.getId());
            assertEquals(sampleWalletId, dto.getWalletId());
            assertEquals("Ví Tiền mặt", dto.getWalletName());
            assertEquals(sampleCategoryId, dto.getCategoryId());
            assertEquals("Ăn uống", dto.getCategoryName());
            assertEquals(amount, dto.getAmount());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals(sampleDate, dto.getTransactionDate());
            assertEquals("Ăn trưa với khách hàng", dto.getNote());
            assertTrue(dto.hasFee());
            assertEquals(fee, dto.getFeeAmount());
            assertEquals(sampleFeeCategoryId, dto.getFeeCategoryId());
            assertEquals("Phí dịch vụ", dto.getFeeCategoryName());
            assertEquals(balanceAfter, dto.getWalletBalanceAfter());
            assertEquals(sampleCreatedAt, dto.getCreatedAt());
        }

        @Test
        @DisplayName("Khởi tạo giao dịch không có phụ phí qua hàm khởi tạo tiện dụng 11 tham số")
        void shouldCreateWithoutFeeUsingConvenientConstructor() {
            BigDecimal amount = new BigDecimal("10000000.00");
            BigDecimal balanceAfter = new BigDecimal("15000000.00");

            TransactionResponseDTO dto = new TransactionResponseDTO(
                    sampleTxnId,
                    sampleWalletId,
                    "Tài khoản Lương",
                    sampleCategoryId,
                    "Tiền lương",
                    amount,
                    "INCOME",
                    sampleDate,
                    "Nhận lương tháng 11",
                    balanceAfter,
                    sampleCreatedAt
            );

            assertEquals(sampleTxnId, dto.getTransactionId());
            assertEquals(amount, dto.getAmount());
            assertEquals("INCOME", dto.getTransactionType());
            assertFalse(dto.hasFee());
            assertEquals(BigDecimal.ZERO, dto.getFeeAmount());
            assertNull(dto.getFeeCategoryId());
            assertEquals("", dto.getFeeCategoryName());
            assertEquals(balanceAfter, dto.getWalletBalanceAfter());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa khoảng trắng và gán giá trị mặc định cho ngày / thời điểm tạo / ghi chú null")
        void shouldNormalizeNullsAndWhitespace() {
            BigDecimal amount = new BigDecimal("50000.00");
            BigDecimal balanceAfter = new BigDecimal("950000.00");

            TransactionResponseDTO dto = new TransactionResponseDTO(
                    sampleTxnId,
                    sampleWalletId,
                    "   Ví Tiền mặt   ",
                    sampleCategoryId,
                    "   Cà phê   ",
                    amount,
                    "expense",
                    null,
                    null,
                    balanceAfter,
                    null
            );

            assertEquals("Ví Tiền mặt", dto.getWalletName());
            assertEquals("Cà phê", dto.getCategoryName());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals(LocalDate.now(), dto.getTransactionDate());
            assertEquals("", dto.getNote());
            assertNotNull(dto.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
    class InvalidInputTests {

        @Test
        @DisplayName("Ném NullPointerException khi transactionId bị null")
        void shouldThrowWhenTransactionIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransactionResponseDTO(
                            null, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                            new BigDecimal("100000"), "INCOME", sampleDate, "",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Mã giao dịch"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi walletId bị null")
        void shouldThrowWhenWalletIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, null, "Ví A", sampleCategoryId, "Danh mục A",
                            new BigDecimal("100000"), "INCOME", sampleDate, "",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Mã ví"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi walletName bị rỗng hoặc khoảng trắng")
        void shouldThrowWhenWalletNameIsBlank() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, sampleWalletId, "   ", sampleCategoryId, "Danh mục A",
                            new BigDecimal("100000"), "INCOME", sampleDate, "",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Tên ví"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi categoryId bị null")
        void shouldThrowWhenCategoryIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, sampleWalletId, "Ví A", null, "Danh mục A",
                            new BigDecimal("100000"), "INCOME", sampleDate, "",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Mã danh mục"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi categoryName bị rỗng hoặc khoảng trắng")
        void shouldThrowWhenCategoryNameIsBlank() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "",
                            new BigDecimal("100000"), "INCOME", sampleDate, "",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Tên danh mục"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi số tiền amount bị null")
        void shouldThrowWhenAmountIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                            null, "INCOME", sampleDate, "",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Số tiền"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi số tiền amount nhỏ hơn hoặc bằng 0")
        void shouldThrowWhenAmountIsNonPositive() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                            BigDecimal.ZERO, "INCOME", sampleDate, "",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Số tiền giao dịch phải lớn hơn 0"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi loại thu chi không phải INCOME hoặc EXPENSE")
        void shouldThrowWhenTransactionTypeIsInvalid() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                            new BigDecimal("100000"), "TRANSFER", sampleDate, "",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Loại giao dịch không hợp lệ"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi số dư sau giao dịch walletBalanceAfter bị null")
        void shouldThrowWhenWalletBalanceAfterIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                            new BigDecimal("100000"), "INCOME", sampleDate, "",
                            null, sampleCreatedAt
                    )
            );
            assertTrue(ex.getMessage().contains("Số dư ví sau giao dịch"));
        }

        @Test
        @DisplayName("Ném NullPointerException hoặc IllegalArgumentException khi phí > 0 mà thiếu mã hoặc tên danh mục phí")
        void shouldThrowWhenFeeIsPositiveButFeeCategoryMissing() {
            assertThrows(
                    NullPointerException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                            new BigDecimal("100000"), "EXPENSE", sampleDate, "",
                            true, new BigDecimal("5000"), null, "Phí",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionResponseDTO(
                            sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                            new BigDecimal("100000"), "EXPENSE", sampleDate, "",
                            true, new BigDecimal("5000"), sampleFeeCategoryId, "   ",
                            new BigDecimal("500000"), sampleCreatedAt
                    )
            );
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (equals, hashCode, toString)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai đối tượng bằng nhau khi cùng thuộc tính dù khác scale số học")
        void shouldBeEqualRegardlessOfScale() {
            TransactionResponseDTO dto1 = new TransactionResponseDTO(
                    sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                    new BigDecimal("100000.0"), "EXPENSE", sampleDate, "Ăn trưa",
                    true, new BigDecimal("5000.0"), sampleFeeCategoryId, "Phí",
                    new BigDecimal("400000.0"), sampleCreatedAt
            );

            TransactionResponseDTO dto2 = new TransactionResponseDTO(
                    sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                    new BigDecimal("100000.00"), "EXPENSE", sampleDate, "Ăn trưa",
                    true, new BigDecimal("5000.00"), sampleFeeCategoryId, "Phí",
                    new BigDecimal("400000.00"), sampleCreatedAt
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Không bằng nhau khi khác mã giao dịch hoặc số tiền")
        void shouldNotEqualWhenFieldsDiffer() {
            TransactionResponseDTO base = new TransactionResponseDTO(
                    sampleTxnId, sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                    new BigDecimal("100000"), "EXPENSE", sampleDate, "",
                    new BigDecimal("400000"), sampleCreatedAt
            );

            TransactionResponseDTO diffId = new TransactionResponseDTO(
                    UUID.randomUUID(), sampleWalletId, "Ví A", sampleCategoryId, "Danh mục A",
                    new BigDecimal("100000"), "EXPENSE", sampleDate, "",
                    new BigDecimal("400000"), sampleCreatedAt
            );

            assertNotEquals(base, diffId);
            assertNotEquals(base, null);
            assertNotEquals(base, "string");
        }

        @Test
        @DisplayName("toString thể hiện đầy đủ các trường thông tin cơ bản")
        void shouldProduceInformativeToString() {
            TransactionResponseDTO dto = new TransactionResponseDTO(
                    sampleTxnId, sampleWalletId, "Ví Tiền mặt", sampleCategoryId, "Ăn uống",
                    new BigDecimal("120000"), "EXPENSE", sampleDate, "Ăn tối",
                    new BigDecimal("880000"), sampleCreatedAt
            );

            String str = dto.toString();
            assertTrue(str.contains("TransactionResponseDTO"));
            assertTrue(str.contains("Ví Tiền mặt"));
            assertTrue(str.contains("Ăn uống"));
            assertTrue(str.contains("120000"));
            assertTrue(str.contains("880000"));
        }
    }
}

