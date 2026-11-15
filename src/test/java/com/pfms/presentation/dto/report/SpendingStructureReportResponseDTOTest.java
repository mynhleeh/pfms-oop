package com.pfms.presentation.dto.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử SpendingStructureReportResponseDTO")
class SpendingStructureReportResponseDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 5 tham số")
        void shouldCreateWithAllFiveParameters() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            BigDecimal totalAmount = new BigDecimal("10000000");

            NeedGroupShareResponseDTO group = new NeedGroupShareResponseDTO(
                    UUID.randomUUID(), "Nhu cầu thiết yếu", "ACTIVE", totalAmount, new BigDecimal("100.00"), List.of());

            SpendingStructureReportResponseDTO dto = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, totalAmount, List.of(group));

            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals(start, dto.getStartDate());
            assertEquals(end, dto.getEndDate());
            assertEquals(totalAmount, dto.getTotalAmount());
            assertEquals(1, dto.getGroupCount());
            assertEquals(1, dto.getGroups().size());
            assertTrue(dto.isExpense());
            assertFalse(dto.isIncome());
        }

        @Test
        @DisplayName("Nhận diện chính xác khi báo cáo cơ cấu thuộc loại INCOME")
        void shouldIdentifyIncomeReportType() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            SpendingStructureReportResponseDTO dto = new SpendingStructureReportResponseDTO(
                    "INCOME", start, end, BigDecimal.ZERO, List.of());

            assertEquals("INCOME", dto.getTransactionType());
            assertTrue(dto.isIncome());
            assertFalse(dto.isExpense());
        }

        @Test
        @DisplayName("Tự động sao chép phòng vệ danh sách groups và bảo đảm tính bất biến")
        void shouldEnforceDefensiveCopyingAndImmutability() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);
            List<NeedGroupShareResponseDTO> mutableList = new ArrayList<>();
            mutableList.add(new NeedGroupShareResponseDTO(UUID.randomUUID(), "Nhóm 1", BigDecimal.ZERO, BigDecimal.ZERO, List.of()));

            SpendingStructureReportResponseDTO dto = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, BigDecimal.ZERO, mutableList);

            mutableList.add(new NeedGroupShareResponseDTO(UUID.randomUUID(), "Nhóm 2", BigDecimal.ZERO, BigDecimal.ZERO, List.of()));
            assertEquals(1, dto.getGroupCount());

            List<NeedGroupShareResponseDTO> returnedList = dto.getGroups();
            assertThrows(UnsupportedOperationException.class,
                    () -> returnedList.add(new NeedGroupShareResponseDTO(UUID.randomUUID(), "Nhóm 3", BigDecimal.ZERO, BigDecimal.ZERO, List.of())));
        }

        @Test
        @DisplayName("Tự động chuẩn hóa chữ hoa loại giao dịch và gán giá trị mặc định khi null")
        void shouldNormalizeStringsAndDefaultNullValues() {
            LocalDate now = LocalDate.now();
            SpendingStructureReportResponseDTO dto = new SpendingStructureReportResponseDTO(
                    "   income   ", null, null, null, null);

            assertEquals("INCOME", dto.getTransactionType());
            assertEquals(now.withDayOfMonth(1), dto.getStartDate());
            assertEquals(now, dto.getEndDate());
            assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
            assertNotNull(dto.getGroups());
            assertTrue(dto.getGroups().isEmpty());
            assertEquals(0, dto.getGroupCount());
        }

        @Test
        @DisplayName("Khi loại giao dịch null hoặc rỗng thì mặc định là EXPENSE")
        void shouldDefaultTransactionTypeToExpense() {
            SpendingStructureReportResponseDTO dtoNull = new SpendingStructureReportResponseDTO(
                    null, null, null, BigDecimal.ZERO, List.of());
            SpendingStructureReportResponseDTO dtoBlank = new SpendingStructureReportResponseDTO(
                    "   \t  ", null, null, BigDecimal.ZERO, List.of());

            assertEquals("EXPENSE", dtoNull.getTransactionType());
            assertEquals("EXPENSE", dtoBlank.getTransactionType());
        }

        @Test
        @DisplayName("Phương thức tĩnh empty() tạo báo cáo cơ cấu rỗng an toàn")
        void shouldCreateEmptyReportViaStaticFactory() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);

            SpendingStructureReportResponseDTO dto = SpendingStructureReportResponseDTO.empty(
                    "EXPENSE", start, end);

            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals(start, dto.getStartDate());
            assertEquals(end, dto.getEndDate());
            assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
            assertEquals(0, dto.getGroupCount());
            assertTrue(dto.getGroups().isEmpty());
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

            SpendingStructureReportResponseDTO dto1 = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("100"), List.of());
            SpendingStructureReportResponseDTO dto2 = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("100"), List.of());

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Bảo đảm tính bất biến về thang số thập phân (Scale Invariance) trong equals và hashCode")
        void shouldBeEqualRegardlessOfBigDecimalScale() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);

            SpendingStructureReportResponseDTO dto1 = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("100"), List.of());
            SpendingStructureReportResponseDTO dto2 = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("100.00"), List.of());

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác thuộc tính thì không bằng nhau")
        void shouldNotBeEqualWhenDifferentProperties() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);

            SpendingStructureReportResponseDTO base = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("100"), List.of());
            SpendingStructureReportResponseDTO diffType = new SpendingStructureReportResponseDTO(
                    "INCOME", start, end, new BigDecimal("100"), List.of());
            SpendingStructureReportResponseDTO diffAmount = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("200"), List.of());

            assertNotEquals(base, diffType);
            assertNotEquals(base, diffAmount);
            assertNotEquals(base, null);
            assertNotEquals(base, "Report");
        }

        @Test
        @DisplayName("equals với chính nó trả về true")
        void shouldBeEqualToItself() {
            SpendingStructureReportResponseDTO dto = SpendingStructureReportResponseDTO.empty(
                    "EXPENSE", LocalDate.now(), LocalDate.now());
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("toString chứa đầy đủ thông tin loại giao dịch, khoảng ngày và tổng tiền")
        void shouldContainFieldsInToString() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);

            SpendingStructureReportResponseDTO dto = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("100"), List.of());
            String str = dto.toString();

            assertTrue(str.contains("EXPENSE"));
            assertTrue(str.contains("2026-01-01"));
            assertTrue(str.contains("2026-01-31"));
            assertTrue(str.contains("100"));
            assertTrue(str.contains("SpendingStructureReportResponseDTO"));
        }
    }

    @Nested
    @DisplayName("Trường So sánh Tổng Kỳ liền kề")
    class ComparisonFieldsTests {

        @Test
        @DisplayName("Khởi tạo với constructor 9 tham số và chuẩn hóa null an toàn")
        void shouldCreateWithComparisonFieldsAndDefaultNulls() {
            LocalDate start = LocalDate.of(2026, 2, 1);
            LocalDate end = LocalDate.of(2026, 2, 28);

            SpendingStructureReportResponseDTO full = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end,
                    new BigDecimal("1500000"), new BigDecimal("1000000"),
                    new BigDecimal("500000"), new BigDecimal("50.00"), true, List.of());

            assertEquals(new BigDecimal("1000000"), full.getPreviousTotalAmount());
            assertEquals(new BigDecimal("500000"), full.getDifferenceAmount());
            assertEquals(new BigDecimal("50.00"), full.getPercentageChange());
            assertTrue(full.isNew());

            SpendingStructureReportResponseDTO defaulted = new SpendingStructureReportResponseDTO(
                    "INCOME", start, end,
                    new BigDecimal("1500000"), null, null, null, false, List.of());
            assertEquals(BigDecimal.ZERO, defaulted.getPreviousTotalAmount());
            assertEquals(BigDecimal.ZERO, defaulted.getDifferenceAmount());
            assertEquals(BigDecimal.ZERO, defaulted.getPercentageChange());
            assertFalse(defaulted.isNew());
        }

        @Test
        @DisplayName("Bảo đảm bất biến thang số thập phân trong equals và hashCode cho trường so sánh")
        void shouldPreserveScaleInvarianceForComparisonFields() {
            LocalDate start = LocalDate.of(2026, 2, 1);
            LocalDate end = LocalDate.of(2026, 2, 28);

            SpendingStructureReportResponseDTO a = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("100"),
                    new BigDecimal("80"), new BigDecimal("20.0"), new BigDecimal("25"), false, List.of());
            SpendingStructureReportResponseDTO b = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("100.00"),
                    new BigDecimal("80.00"), new BigDecimal("20.00"), new BigDecimal("25.00"), false, List.of());
            SpendingStructureReportResponseDTO c = new SpendingStructureReportResponseDTO(
                    "EXPENSE", start, end, new BigDecimal("100.00"),
                    new BigDecimal("80.00"), new BigDecimal("20.00"), new BigDecimal("25.00"), true, List.of());

            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, c);
        }

        @Test
        @DisplayName("Chuẩn hóa transactionType không phụ thuộc Locale mặc định (tr-TR)")
        void shouldNormalizeTransactionTypeWithLocaleRoot() {
            Locale previous = Locale.getDefault();
            try {
                Locale.setDefault(Locale.forLanguageTag("tr-TR"));
                SpendingStructureReportResponseDTO dto = new SpendingStructureReportResponseDTO(
                        "income",
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 1, 31),
                        new BigDecimal("1500000"),
                        null, null, null, false, List.of()
                );

                assertEquals("INCOME", dto.getTransactionType());
            } finally {
                Locale.setDefault(previous);
            }
        }
    }
}

