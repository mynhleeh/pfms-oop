package com.pfms.presentation.dto.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CashFlowReportRequestDTO")
class CashFlowReportRequestDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với tháng và năm chỉ định")
        void shouldCreateWithSpecifiedMonthAndYear() {
            CashFlowReportRequestDTO dto = new CashFlowReportRequestDTO(10, 2026);

            assertEquals(10, dto.getEndMonth());
            assertEquals(2026, dto.getEndYear());
        }

        @Test
        @DisplayName("Khởi tạo với constructor mặc định sẽ lấy tháng và năm hiện tại")
        void shouldDefaultToCurrentMonthAndYear() {
            LocalDate now = LocalDate.now();
            CashFlowReportRequestDTO dto = new CashFlowReportRequestDTO();

            assertEquals(now.getMonthValue(), dto.getEndMonth());
            assertEquals(now.getYear(), dto.getEndYear());
        }

        @Test
        @DisplayName("Phương thức tĩnh now() tạo đối tượng với tháng và năm hiện tại")
        void shouldCreateViaStaticFactoryNow() {
            LocalDate now = LocalDate.now();
            CashFlowReportRequestDTO dto = CashFlowReportRequestDTO.now();

            assertEquals(now.getMonthValue(), dto.getEndMonth());
            assertEquals(now.getYear(), dto.getEndYear());
        }
    }

    @Nested
    @DisplayName("Chuyển tiếp dữ liệu cho Service kiểm soát nghiệp vụ (Thin DTO)")
    class ValidationPassthroughTests {

        @Test
        @DisplayName("Truyền thẳng giá trị biên để Tầng Dịch vụ (ReportService) kiểm tra hợp lệ")
        void shouldPreserveValuesForServiceValidation() {
            CashFlowReportRequestDTO invalidMonthDto = assertDoesNotThrow(
                    () -> new CashFlowReportRequestDTO(13, 2026));
            CashFlowReportRequestDTO invalidYearDto = assertDoesNotThrow(
                    () -> new CashFlowReportRequestDTO(5, 1999));

            assertEquals(13, invalidMonthDto.getEndMonth());
            assertEquals(2026, invalidMonthDto.getEndYear());
            assertEquals(5, invalidYearDto.getEndMonth());
            assertEquals(1999, invalidYearDto.getEndYear());
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (Identity Contract)")
    class IdentityContractTests {

        @Test
        @DisplayName("Hai đối tượng có cùng endMonth và endYear phải bằng nhau và có cùng hashCode")
        void shouldBeEqualWhenSameMonthAndYear() {
            CashFlowReportRequestDTO dto1 = new CashFlowReportRequestDTO(11, 2026);
            CashFlowReportRequestDTO dto2 = new CashFlowReportRequestDTO(11, 2026);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác tháng hoặc khác năm thì không bằng nhau")
        void shouldNotBeEqualWhenDifferentMonthOrYear() {
            CashFlowReportRequestDTO base = new CashFlowReportRequestDTO(11, 2026);
            CashFlowReportRequestDTO differentMonth = new CashFlowReportRequestDTO(12, 2026);
            CashFlowReportRequestDTO differentYear = new CashFlowReportRequestDTO(11, 2025);

            assertNotEquals(base, differentMonth);
            assertNotEquals(base, differentYear);
            assertNotEquals(base, null);
            assertNotEquals(base, "11/2026");
        }

        @Test
        @DisplayName("equals với chính nó trả về true")
        void shouldBeEqualToItself() {
            CashFlowReportRequestDTO dto = new CashFlowReportRequestDTO(11, 2026);
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("toString chứa đầy đủ thông tin endMonth và endYear")
        void shouldContainFieldsInToString() {
            CashFlowReportRequestDTO dto = new CashFlowReportRequestDTO(11, 2026);
            String str = dto.toString();

            assertTrue(str.contains("11"));
            assertTrue(str.contains("2026"));
            assertTrue(str.contains("CashFlowReportRequestDTO"));
        }
    }
}

