package com.pfms.presentation.dto.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CashFlowReportResponseDTO")
class CashFlowReportResponseDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với danh sách các tháng và các chỉ số bình quân năm")
        void shouldCreateWithMonthsAndAverages() {
            List<MonthlyCashFlowResponseDTO> months = List.of(
                    new MonthlyCashFlowResponseDTO("2026-09", new BigDecimal("20000000"), new BigDecimal("12000000"), new BigDecimal("8000000"), new BigDecimal("40.00")),
                    new MonthlyCashFlowResponseDTO("2026-10", new BigDecimal("30000000"), new BigDecimal("18000000"), new BigDecimal("12000000"), new BigDecimal("40.00"))
            );
            BigDecimal avgIncome = new BigDecimal("25000000");
            BigDecimal avgExpense = new BigDecimal("15000000");
            BigDecimal avgNet = new BigDecimal("10000000");

            CashFlowReportResponseDTO dto = new CashFlowReportResponseDTO(months, avgIncome, avgExpense, avgNet);

            assertEquals(2, dto.getMonthCount());
            assertEquals(2, dto.getMonths().size());
            assertEquals(avgIncome, dto.getAvgIncome());
            assertEquals(avgExpense, dto.getAvgExpense());
            assertEquals(avgNet, dto.getAvgNetCashFlow());
        }

        @Test
        @DisplayName("Tự động sao chép phòng vệ danh sách tháng, danh sách trả về là bất biến")
        void shouldEnforceDefensiveCopyingAndImmutability() {
            List<MonthlyCashFlowResponseDTO> mutableList = new ArrayList<>();
            mutableList.add(new MonthlyCashFlowResponseDTO("2026-01"));

            CashFlowReportResponseDTO dto = new CashFlowReportResponseDTO(
                    mutableList, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

            mutableList.add(new MonthlyCashFlowResponseDTO("2026-02"));
            assertEquals(1, dto.getMonthCount());

            List<MonthlyCashFlowResponseDTO> returnedList = dto.getMonths();
            assertThrows(UnsupportedOperationException.class,
                    () -> returnedList.add(new MonthlyCashFlowResponseDTO("2026-03")));
        }

        @Test
        @DisplayName("Khi truyền danh sách null thì mặc định khởi tạo danh sách rỗng")
        void shouldHandleNullListAsEmpty() {
            CashFlowReportResponseDTO dto = new CashFlowReportResponseDTO(
                    null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

            assertNotNull(dto.getMonths());
            assertTrue(dto.getMonths().isEmpty());
            assertEquals(0, dto.getMonthCount());
        }

        @Test
        @DisplayName("Tự động tính avgNetCashFlow nếu truyền null")
        void shouldComputeAvgNetCashFlowWhenNull() {
            BigDecimal avgIncome = new BigDecimal("20000000");
            BigDecimal avgExpense = new BigDecimal("12000000");

            CashFlowReportResponseDTO dto = new CashFlowReportResponseDTO(
                    List.of(), avgIncome, avgExpense, null);

            assertEquals(new BigDecimal("8000000"), dto.getAvgNetCashFlow());
        }

        @Test
        @DisplayName("Phương thức tĩnh empty() tạo báo cáo rỗng với các chỉ số bình quân bằng ZERO")
        void shouldCreateEmptyReportViaStaticFactory() {
            CashFlowReportResponseDTO dto = CashFlowReportResponseDTO.empty();

            assertEquals(0, dto.getMonthCount());
            assertTrue(dto.getMonths().isEmpty());
            assertEquals(BigDecimal.ZERO, dto.getAvgIncome());
            assertEquals(BigDecimal.ZERO, dto.getAvgExpense());
            assertEquals(BigDecimal.ZERO, dto.getAvgNetCashFlow());
        }
    }

    @Nested
    @DisplayName("Kiểm tra Ràng buộc Null (Null Safety)")
    class NullSafetyTests {

        @Test
        @DisplayName("Ném NullPointerException khi avgIncome là null")
        void shouldThrowExceptionWhenAvgIncomeIsNull() {
            assertThrows(NullPointerException.class,
                    () -> new CashFlowReportResponseDTO(List.of(), null, BigDecimal.ZERO, BigDecimal.ZERO));
        }

        @Test
        @DisplayName("Ném NullPointerException khi avgExpense là null")
        void shouldThrowExceptionWhenAvgExpenseIsNull() {
            assertThrows(NullPointerException.class,
                    () -> new CashFlowReportResponseDTO(List.of(), BigDecimal.ZERO, null, BigDecimal.ZERO));
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (Identity Contract)")
    class IdentityContractTests {

        @Test
        @DisplayName("Hai đối tượng có cùng thuộc tính phải bằng nhau và có cùng hashCode")
        void shouldBeEqualWhenSameProperties() {
            MonthlyCashFlowResponseDTO m1 = new MonthlyCashFlowResponseDTO("2026-10");
            CashFlowReportResponseDTO dto1 = new CashFlowReportResponseDTO(
                    List.of(m1), new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"));
            CashFlowReportResponseDTO dto2 = new CashFlowReportResponseDTO(
                    List.of(m1), new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"));

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Bảo đảm tính bất biến về thang số thập phân (Scale Invariance) trong equals và hashCode")
        void shouldBeEqualRegardlessOfBigDecimalScale() {
            MonthlyCashFlowResponseDTO m1 = new MonthlyCashFlowResponseDTO("2026-10");
            CashFlowReportResponseDTO dto1 = new CashFlowReportResponseDTO(
                    List.of(m1), new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"));
            CashFlowReportResponseDTO dto2 = new CashFlowReportResponseDTO(
                    List.of(m1), new BigDecimal("100.00"), new BigDecimal("60.000"), new BigDecimal("40.0000"));

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác thuộc tính thì không bằng nhau")
        void shouldNotBeEqualWhenDifferentProperties() {
            MonthlyCashFlowResponseDTO m1 = new MonthlyCashFlowResponseDTO("2026-10");
            MonthlyCashFlowResponseDTO m2 = new MonthlyCashFlowResponseDTO("2026-11");

            CashFlowReportResponseDTO base = new CashFlowReportResponseDTO(
                    List.of(m1), new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"));
            CashFlowReportResponseDTO diffMonths = new CashFlowReportResponseDTO(
                    List.of(m2), new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"));
            CashFlowReportResponseDTO diffAvg = new CashFlowReportResponseDTO(
                    List.of(m1), new BigDecimal("200"), new BigDecimal("60"), new BigDecimal("40"));

            assertNotEquals(base, diffMonths);
            assertNotEquals(base, diffAvg);
            assertNotEquals(base, null);
            assertNotEquals(base, "Report");
        }

        @Test
        @DisplayName("equals với chính nó trả về true")
        void shouldBeEqualToItself() {
            CashFlowReportResponseDTO dto = CashFlowReportResponseDTO.empty();
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("toString chứa đầy đủ thông tin danh sách tháng và các chỉ số bình quân")
        void shouldContainFieldsInToString() {
            CashFlowReportResponseDTO dto = new CashFlowReportResponseDTO(
                    List.of(), new BigDecimal("100"), new BigDecimal("60"), new BigDecimal("40"));
            String str = dto.toString();

            assertTrue(str.contains("100"));
            assertTrue(str.contains("60"));
            assertTrue(str.contains("40"));
            assertTrue(str.contains("CashFlowReportResponseDTO"));
        }
    }
}

