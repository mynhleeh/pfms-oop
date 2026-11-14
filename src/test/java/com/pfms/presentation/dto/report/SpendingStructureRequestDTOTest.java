package com.pfms.presentation.dto.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử SpendingStructureRequestDTO")
class SpendingStructureRequestDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 3 tham số ngày bắt đầu, ngày kết thúc và loại giao dịch")
        void shouldCreateWithAllThreeParameters() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO dto = new SpendingStructureRequestDTO(start, end, "EXPENSE");

            assertEquals(start, dto.getStartDate());
            assertEquals(end, dto.getEndDate());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertTrue(dto.isExpense());
            assertFalse(dto.isIncome());
        }

        @Test
        @DisplayName("Nhận diện chính xác khi loại giao dịch là INCOME")
        void shouldIdentifyIncomeType() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO dto = new SpendingStructureRequestDTO(start, end, "INCOME");

            assertEquals("INCOME", dto.getTransactionType());
            assertTrue(dto.isIncome());
            assertFalse(dto.isExpense());
        }

        @Test
        @DisplayName("Khởi tạo với 2 tham số ngày sẽ mặc định loại giao dịch là EXPENSE")
        void shouldDefaultToExpenseWithTwoParameters() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO dto = new SpendingStructureRequestDTO(start, end);

            assertEquals(start, dto.getStartDate());
            assertEquals(end, dto.getEndDate());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertTrue(dto.isExpense());
            assertFalse(dto.isIncome());
        }

        @Test
        @DisplayName("Khởi tạo với constructor mặc định sẽ lấy từ đầu tháng hiện tại đến ngày hiện tại và loại EXPENSE")
        void shouldDefaultToCurrentMonthAndExpense() {
            LocalDate now = LocalDate.now();
            SpendingStructureRequestDTO dto = new SpendingStructureRequestDTO();

            assertEquals(now.withDayOfMonth(1), dto.getStartDate());
            assertEquals(now, dto.getEndDate());
            assertEquals("EXPENSE", dto.getTransactionType());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa chữ hoa và cắt tỉa khoảng trắng cho loại giao dịch")
        void shouldTrimAndUpperCaseTransactionType() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO dto = new SpendingStructureRequestDTO(start, end, "  income  ");

            assertEquals("INCOME", dto.getTransactionType());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Khi loại giao dịch rỗng hoặc toàn khoảng trắng thì mặc định về EXPENSE")
        void shouldDefaultToExpenseWhenBlank(String blankType) {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO dto = new SpendingStructureRequestDTO(start, end, blankType);

            assertEquals("EXPENSE", dto.getTransactionType());
        }

        @Test
        @DisplayName("Khi loại giao dịch null thì mặc định về EXPENSE")
        void shouldDefaultToExpenseWhenNull() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO dto = new SpendingStructureRequestDTO(start, end, null);

            assertEquals("EXPENSE", dto.getTransactionType());
        }
    }

    @Nested
    @DisplayName("Chuyển tiếp dữ liệu cho Service kiểm soát nghiệp vụ (Thin DTO)")
    class ValidationPassthroughTests {

        @Test
        @DisplayName("Truyền thẳng khoảng ngày đảo ngược để Service kiểm tra và ném ValidationException")
        void shouldPreserveInvalidDateRangeForServiceValidation() {
            LocalDate start = LocalDate.of(2026, 2, 1);
            LocalDate end = LocalDate.of(2026, 1, 1);
            SpendingStructureRequestDTO dto = assertDoesNotThrow(
                    () -> new SpendingStructureRequestDTO(start, end, "EXPENSE"));

            assertEquals(start, dto.getStartDate());
            assertEquals(end, dto.getEndDate());
        }

        @Test
        @DisplayName("Truyền thẳng loại giao dịch tùy biến để Service kiểm tra tính hợp lệ")
        void shouldPreserveArbitraryTypeForServiceValidation() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO dto = assertDoesNotThrow(
                    () -> new SpendingStructureRequestDTO(start, end, "TRANSFER"));

            assertEquals("TRANSFER", dto.getTransactionType());
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (Identity Contract)")
    class IdentityContractTests {

        @Test
        @DisplayName("Hai đối tượng có cùng thuộc tính phải bằng nhau và có cùng hashCode")
        void shouldBeEqualWhenSameProperties() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO dto1 = new SpendingStructureRequestDTO(start, end, "EXPENSE");
            SpendingStructureRequestDTO dto2 = new SpendingStructureRequestDTO(start, end, "EXPENSE");

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác thuộc tính thì không bằng nhau")
        void shouldNotBeEqualWhenDifferentProperties() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO base = new SpendingStructureRequestDTO(start, end, "EXPENSE");
            SpendingStructureRequestDTO diffStart = new SpendingStructureRequestDTO(LocalDate.of(2026, 1, 2), end, "EXPENSE");
            SpendingStructureRequestDTO diffEnd = new SpendingStructureRequestDTO(start, LocalDate.of(2026, 2, 1), "EXPENSE");
            SpendingStructureRequestDTO diffType = new SpendingStructureRequestDTO(start, end, "INCOME");

            assertNotEquals(base, diffStart);
            assertNotEquals(base, diffEnd);
            assertNotEquals(base, diffType);
            assertNotEquals(base, null);
            assertNotEquals(base, "EXPENSE");
        }

        @Test
        @DisplayName("equals với chính nó trả về true")
        void shouldBeEqualToItself() {
            SpendingStructureRequestDTO dto = new SpendingStructureRequestDTO();
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("toString chứa đầy đủ thông tin startDate, endDate và transactionType")
        void shouldContainFieldsInToString() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureRequestDTO dto = new SpendingStructureRequestDTO(start, end, "EXPENSE");
            String str = dto.toString();

            assertTrue(str.contains("2026-01-01"));
            assertTrue(str.contains("2026-01-31"));
            assertTrue(str.contains("EXPENSE"));
            assertTrue(str.contains("SpendingStructureRequestDTO"));
        }
    }
}

