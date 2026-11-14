package com.pfms.presentation.dto.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử MonthlyCashFlowResponseDTO")
class MonthlyCashFlowResponseDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 5 tham số")
        void shouldCreateWithAllFiveParameters() {
            BigDecimal income = new BigDecimal("25000000");
            BigDecimal expense = new BigDecimal("15000000");
            BigDecimal net = new BigDecimal("10000000");
            BigDecimal rate = new BigDecimal("40.00");

            MonthlyCashFlowResponseDTO dto = new MonthlyCashFlowResponseDTO(
                    "2026-10", income, expense, net, rate);

            assertEquals("2026-10", dto.getYearMonth());
            assertEquals(income, dto.getTotalIncome());
            assertEquals(expense, dto.getTotalExpense());
            assertEquals(net, dto.getNetCashFlow());
            assertEquals(rate, dto.getSavingsRate());
        }

        @Test
        @DisplayName("Khởi tạo tháng trống chỉ với yearMonth sẽ nhận các giá trị số học bằng ZERO")
        void shouldCreateEmptyMonthWithZeroValues() {
            MonthlyCashFlowResponseDTO dto = new MonthlyCashFlowResponseDTO("2026-05");

            assertEquals("2026-05", dto.getYearMonth());
            assertEquals(BigDecimal.ZERO, dto.getTotalIncome());
            assertEquals(BigDecimal.ZERO, dto.getTotalExpense());
            assertEquals(BigDecimal.ZERO, dto.getNetCashFlow());
            assertEquals(BigDecimal.ZERO, dto.getSavingsRate());
        }

        @Test
        @DisplayName("Phương thức tĩnh empty() tạo đối tượng tháng rỗng chuẩn xác")
        void shouldCreateViaStaticFactoryEmpty() {
            MonthlyCashFlowResponseDTO dto = MonthlyCashFlowResponseDTO.empty("2026-06");

            assertEquals("2026-06", dto.getYearMonth());
            assertEquals(BigDecimal.ZERO, dto.getTotalIncome());
            assertEquals(BigDecimal.ZERO, dto.getTotalExpense());
            assertEquals(BigDecimal.ZERO, dto.getNetCashFlow());
            assertEquals(BigDecimal.ZERO, dto.getSavingsRate());
        }

        @Test
        @DisplayName("Tự động tính netCashFlow nếu truyền null và gán savingsRate về ZERO nếu null")
        void shouldComputeNetCashFlowAndDefaultSavingsRateWhenNull() {
            BigDecimal income = new BigDecimal("30000000");
            BigDecimal expense = new BigDecimal("10000000");

            MonthlyCashFlowResponseDTO dto = new MonthlyCashFlowResponseDTO(
                    "2026-07", income, expense, null, null);

            assertEquals(new BigDecimal("20000000"), dto.getNetCashFlow());
            assertEquals(BigDecimal.ZERO, dto.getSavingsRate());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng cho chuỗi yearMonth")
        void shouldTrimYearMonth() {
            MonthlyCashFlowResponseDTO dto = new MonthlyCashFlowResponseDTO(
                    "   2026-08   ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

            assertEquals("2026-08", dto.getYearMonth());
        }
        @Test
        @DisplayName("Nhận diện chính xác trạng thái thặng dư, thâm hụt và cân bằng")
        void shouldIdentifyCashFlowBalanceStatus() {
            MonthlyCashFlowResponseDTO surplus = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("40.00"));
            MonthlyCashFlowResponseDTO deficit = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("50"), new BigDecimal("80"), new BigDecimal("-30"), BigDecimal.ZERO);
            MonthlyCashFlowResponseDTO balanced = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("70"), new BigDecimal("70"), BigDecimal.ZERO, BigDecimal.ZERO);

            assertTrue(surplus.isSurplus());
            assertFalse(surplus.isDeficit());
            assertFalse(surplus.isBalanced());

            assertFalse(deficit.isSurplus());
            assertTrue(deficit.isDeficit());
            assertFalse(deficit.isBalanced());

            assertFalse(balanced.isSurplus());
            assertFalse(balanced.isDeficit());
            assertTrue(balanced.isBalanced());
        }
    }

    @Nested
    @DisplayName("Kiểm tra Ràng buộc Null (Null Safety)")
    class NullSafetyTests {

        @Test
        @DisplayName("Ném NullPointerException khi yearMonth là null")
        void shouldThrowExceptionWhenYearMonthIsNull() {
            assertThrows(NullPointerException.class,
                    () -> new MonthlyCashFlowResponseDTO(null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            assertThrows(NullPointerException.class,
                    () -> new MonthlyCashFlowResponseDTO(null));
        }

        @Test
        @DisplayName("Ném NullPointerException khi totalIncome là null")
        void shouldThrowExceptionWhenTotalIncomeIsNull() {
            assertThrows(NullPointerException.class,
                    () -> new MonthlyCashFlowResponseDTO("2026-09", null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        }

        @Test
        @DisplayName("Ném NullPointerException khi totalExpense là null")
        void shouldThrowExceptionWhenTotalExpenseIsNull() {
            assertThrows(NullPointerException.class,
                    () -> new MonthlyCashFlowResponseDTO("2026-09", BigDecimal.ZERO, null, BigDecimal.ZERO, BigDecimal.ZERO));
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (Identity Contract)")
    class IdentityContractTests {

        @Test
        @DisplayName("Hai đối tượng có cùng thuộc tính phải bằng nhau và có cùng hashCode")
        void shouldBeEqualWhenSameProperties() {
            MonthlyCashFlowResponseDTO dto1 = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("40.00"));
            MonthlyCashFlowResponseDTO dto2 = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("40.00"));

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Bảo đảm tính bất biến về thang số thập phân (Scale Invariance) trong equals và hashCode")
        void shouldBeEqualRegardlessOfBigDecimalScale() {
            MonthlyCashFlowResponseDTO dto1 = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("40.0"));
            MonthlyCashFlowResponseDTO dto2 = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("100.00"), new BigDecimal("60.000"), new BigDecimal("40.0000"), new BigDecimal("40.000"));

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác thuộc tính thì không bằng nhau")
        void shouldNotBeEqualWhenDifferentProperties() {
            MonthlyCashFlowResponseDTO base = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("40.00"));
            MonthlyCashFlowResponseDTO diffMonth = new MonthlyCashFlowResponseDTO(
                    "2026-11", new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("40.00"));
            MonthlyCashFlowResponseDTO diffIncome = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("200"), new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("40.00"));

            assertNotEquals(base, diffMonth);
            assertNotEquals(base, diffIncome);
            assertNotEquals(base, null);
            assertNotEquals(base, "2026-10");
        }

        @Test
        @DisplayName("equals với chính nó trả về true")
        void shouldBeEqualToItself() {
            MonthlyCashFlowResponseDTO dto = new MonthlyCashFlowResponseDTO("2026-10");
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("toString chứa đầy đủ thông tin các trường tài chính")
        void shouldContainFieldsInToString() {
            MonthlyCashFlowResponseDTO dto = new MonthlyCashFlowResponseDTO(
                    "2026-10", new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("40.00"));
            String str = dto.toString();

            assertTrue(str.contains("2026-10"));
            assertTrue(str.contains("100"));
            assertTrue(str.contains("60"));
            assertTrue(str.contains("40"));
            assertTrue(str.contains("MonthlyCashFlowResponseDTO"));
        }
    }
}

