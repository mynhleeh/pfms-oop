package com.pfms.presentation.dto.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử AdjustBalanceRequestDTO")
class AdjustBalanceRequestDTOTest {

    private final UUID sampleId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final LocalDate sampleDate = LocalDate.of(2026, 11, 1);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ các trường dữ liệu")
        void shouldCreateWithAllFields() {
            BigDecimal amount = new BigDecimal("5000000.00");
            AdjustBalanceRequestDTO dto = new AdjustBalanceRequestDTO(
                    sampleId, amount, "Kiểm kê định kỳ tháng 11", sampleDate);

            assertEquals(sampleId, dto.getWalletId());
            assertEquals(amount, dto.getActualBalance());
            assertEquals("Kiểm kê định kỳ tháng 11", dto.getReason());
            assertEquals(sampleDate, dto.getAdjustedAt());
        }

        @Test
        @DisplayName("Khởi tạo nhanh chỉ với mã ví và số dư, tự động gán ngày hiện tại và lý do rỗng")
        void shouldCreateWithOnlyIdAndBalance() {
            BigDecimal amount = new BigDecimal("1000000.00");
            AdjustBalanceRequestDTO dto = new AdjustBalanceRequestDTO(sampleId, amount);

            assertEquals(sampleId, dto.getWalletId());
            assertEquals(amount, dto.getActualBalance());
            assertEquals("", dto.getReason());
            assertEquals(LocalDate.now(), dto.getAdjustedAt());
        }

        @Test
        @DisplayName("Khởi tạo với mã ví, số dư và lý do, tự động gán ngày hiện tại")
        void shouldCreateWithIdBalanceAndReason() {
            BigDecimal amount = new BigDecimal("2500000.00");
            AdjustBalanceRequestDTO dto = new AdjustBalanceRequestDTO(sampleId, amount,
                    "Tìm thấy tiền mặt quên trong túi");

            assertEquals(sampleId, dto.getWalletId());
            assertEquals(amount, dto.getActualBalance());
            assertEquals("Tìm thấy tiền mặt quên trong túi", dto.getReason());
            assertEquals(LocalDate.now(), dto.getAdjustedAt());
        }

        @Test
        @DisplayName("Chấp nhận số dư thực tế bằng 0 (biên không âm hợp lệ)")
        void shouldAcceptZeroBalance() {
            AdjustBalanceRequestDTO dto = new AdjustBalanceRequestDTO(sampleId, BigDecimal.ZERO);

            assertEquals(BigDecimal.ZERO, dto.getActualBalance());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng lý do và chuẩn hóa null thành chuỗi rỗng / ngày hiện tại")
        void shouldNormalizeNullAndWhitespace() {
            BigDecimal amount = new BigDecimal("300000.00");
            AdjustBalanceRequestDTO dto = new AdjustBalanceRequestDTO(sampleId, amount, "   Sai lệch nhỏ   ", null);

            assertEquals("Sai lệch nhỏ", dto.getReason());
            assertEquals(LocalDate.now(), dto.getAdjustedAt());

            AdjustBalanceRequestDTO dtoNullReason = new AdjustBalanceRequestDTO(sampleId, amount, null, sampleDate);
            assertEquals("", dtoNullReason.getReason());
        }
    }

    @Nested
    @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
    class InvalidInputTests {

        @Test
        @DisplayName("Ném NullPointerException khi mã ví walletId bị null")
        void shouldThrowExceptionWhenWalletIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new AdjustBalanceRequestDTO(null, new BigDecimal("100000")));
            assertTrue(ex.getMessage().contains("Mã ví"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi số dư thực tế actualBalance bị null")
        void shouldThrowExceptionWhenActualBalanceIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new AdjustBalanceRequestDTO(sampleId, null));
            assertTrue(ex.getMessage().contains("Số dư thực tế"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "-0.01", "-1", "-500000" })
        @DisplayName("Ném IllegalArgumentException khi số dư thực tế mang giá trị âm")
        void shouldThrowExceptionWhenActualBalanceIsNegative(String negativeAmount) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new AdjustBalanceRequestDTO(sampleId, new BigDecimal(negativeAmount)));
            assertTrue(ex.getMessage().contains("không được âm"));
        }
    }

    @Nested
    @DisplayName("Hợp đồng Bình đẳng và Bất biến (Equality & Immutability)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai DTO cùng thuộc tính phải bình đẳng và cùng hashCode")
        void shouldRespectEqualsAndHashCodeContract() {
            UUID otherId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            BigDecimal amount = new BigDecimal("500000.00");
            AdjustBalanceRequestDTO dto1 = new AdjustBalanceRequestDTO(sampleId, amount, "Lý do", sampleDate);
            AdjustBalanceRequestDTO dto2 = new AdjustBalanceRequestDTO(sampleId, amount, "Lý do", sampleDate);
            AdjustBalanceRequestDTO dtoDiffId = new AdjustBalanceRequestDTO(otherId, amount, "Lý do", sampleDate);
            AdjustBalanceRequestDTO dtoDiffAmount = new AdjustBalanceRequestDTO(sampleId, new BigDecimal("600000.00"),
                    "Lý do", sampleDate);
            AdjustBalanceRequestDTO dtoDiffReason = new AdjustBalanceRequestDTO(sampleId, amount, "Khác", sampleDate);
            AdjustBalanceRequestDTO dtoDiffDate = new AdjustBalanceRequestDTO(sampleId, amount, "Lý do",
                    sampleDate.plusDays(1));

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dtoDiffId);
            assertNotEquals(dto1, dtoDiffAmount);
            assertNotEquals(dto1, dtoDiffReason);
            assertNotEquals(dto1, dtoDiffDate);
            assertNotEquals(null, dto1);
            assertNotEquals(dto1, "NotAnAdjustBalanceRequestDTO");
        }

        @Test
        @DisplayName("Phương thức toString cung cấp thông tin định danh và thuộc tính")
        void shouldProvideMeaningfulToString() {
            AdjustBalanceRequestDTO dto = new AdjustBalanceRequestDTO(
                    sampleId, new BigDecimal("500000.00"), "Kiểm kê", sampleDate);
            String text = dto.toString();

            assertTrue(text.contains("AdjustBalanceRequestDTO"));
            assertTrue(text.contains(sampleId.toString()));
            assertTrue(text.contains("500000.00"));
            assertTrue(text.contains("Kiểm kê"));
            assertTrue(text.contains(sampleDate.toString()));
        }
    }
}
