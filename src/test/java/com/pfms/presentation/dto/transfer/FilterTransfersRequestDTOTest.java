package com.pfms.presentation.dto.transfer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử FilterTransfersRequestDTO")
class FilterTransfersRequestDTOTest {

    private final UUID sampleSourceWalletId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID sampleDestinationWalletId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final LocalDate startDate = LocalDate.of(2026, 11, 1);
    private final LocalDate endDate = LocalDate.of(2026, 11, 30);

    @Nested
    @DisplayName("Nhóm 1: Happy Path - Khởi tạo và đóng gói tiêu chí lọc hợp lệ")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 6 tiêu chí lọc và phân trang")
        void shouldCreateWithAllCriteria() {
            FilterTransfersRequestDTO dto = new FilterTransfersRequestDTO(
                    startDate,
                    endDate,
                    sampleSourceWalletId,
                    sampleDestinationWalletId,
                    2,
                    25
            );

            assertEquals(startDate, dto.getStartDate());
            assertEquals(endDate, dto.getEndDate());
            assertEquals(sampleSourceWalletId, dto.getSourceWalletId());
            assertEquals(sampleDestinationWalletId, dto.getDestinationWalletId());
            assertEquals(2, dto.getPageNumber());
            assertEquals(25, dto.getPageSize());
            assertTrue(dto.hasDateFilter());
            assertTrue(dto.hasSourceWalletFilter());
            assertTrue(dto.hasDestinationWalletFilter());
        }

        @Test
        @DisplayName("Khởi tạo không đối số (mặc định xem tất cả, trang 1, 50 bản ghi/trang)")
        void shouldCreateDefaultEmptyFilter() {
            FilterTransfersRequestDTO dto = new FilterTransfersRequestDTO();

            assertNull(dto.getStartDate());
            assertNull(dto.getEndDate());
            assertNull(dto.getSourceWalletId());
            assertNull(dto.getDestinationWalletId());
            assertEquals(1, dto.getPageNumber());
            assertEquals(50, dto.getPageSize());
            assertFalse(dto.hasDateFilter());
            assertFalse(dto.hasSourceWalletFilter());
            assertFalse(dto.hasDestinationWalletFilter());
        }

        @Test
        @DisplayName("Khởi tạo 4 tham số không phân trang (tự động gán trang 1, kích thước 50)")
        void shouldCreateWith4ParamsDefaultPagination() {
            FilterTransfersRequestDTO dto = new FilterTransfersRequestDTO(
                    startDate,
                    endDate,
                    sampleSourceWalletId,
                    null
            );

            assertEquals(startDate, dto.getStartDate());
            assertEquals(endDate, dto.getEndDate());
            assertEquals(sampleSourceWalletId, dto.getSourceWalletId());
            assertNull(dto.getDestinationWalletId());
            assertEquals(1, dto.getPageNumber());
            assertEquals(50, dto.getPageSize());
            assertTrue(dto.hasDateFilter());
            assertTrue(dto.hasSourceWalletFilter());
            assertFalse(dto.hasDestinationWalletFilter());
        }

        @Test
        @DisplayName("Nhận diện đúng bộ lọc ngày khi chỉ có startDate hoặc chỉ có endDate")
        void shouldDetectDateFilterWhenOnlyOneBoundProvided() {
            FilterTransfersRequestDTO onlyStart = new FilterTransfersRequestDTO(
                    startDate, null, null, null
            );
            FilterTransfersRequestDTO onlyEnd = new FilterTransfersRequestDTO(
                    null, endDate, null, sampleDestinationWalletId
            );

            assertTrue(onlyStart.hasDateFilter());
            assertTrue(onlyEnd.hasDateFilter());
            assertTrue(onlyEnd.hasDestinationWalletFilter());
        }
    }

    @Nested
    @DisplayName("Nhóm 2: Boundary & Precision - Giá trị biên phân trang và khoảng ngày")
    class BoundaryAndPrecisionTests {

        @ParameterizedTest(name = "Chấp nhận pageSize tại biên hợp lệ: {0}")
        @ValueSource(ints = {10, 50, 100})
        @DisplayName("Chấp nhận kích thước trang tại cận dưới (10) và cận trên (100)")
        void shouldAcceptBoundaryPageSizes(int validPageSize) {
            FilterTransfersRequestDTO dto = new FilterTransfersRequestDTO(
                    null, null, null, null, 1, validPageSize
            );

            assertEquals(1, dto.getPageNumber());
            assertEquals(validPageSize, dto.getPageSize());
        }

        @Test
        @DisplayName("Chấp nhận khoảng thời gian biên trong cùng 1 ngày (startDate trùng endDate)")
        void shouldAcceptSameStartAndEndDate() {
            LocalDate singleDay = LocalDate.of(2026, 11, 12);

            FilterTransfersRequestDTO dto = new FilterTransfersRequestDTO(
                    singleDay, singleDay, sampleSourceWalletId, sampleDestinationWalletId
            );

            assertEquals(singleDay, dto.getStartDate());
            assertEquals(singleDay, dto.getEndDate());
            assertTrue(dto.hasDateFilter());
        }
    }

    @Nested
    @DisplayName("Nhóm 3: Passthrough Phân trang Ngoài Biên tới Service")
    class NegativeAndZeroValueTests {

        @ParameterizedTest(name = "Chỉ số trang ngoài biên {0} đi qua DTO để Service xử lý")
        @ValueSource(ints = {-100, -1, 0})
        @DisplayName("Cho phép pageNumber < 1 đi qua DTO")
        void shouldAllowOutOfRangePageNumberPassthrough(int invalidPageNumber) {
            FilterTransfersRequestDTO dto = assertDoesNotThrow(
                    () -> new FilterTransfersRequestDTO(
                            startDate, endDate, null, null, invalidPageNumber, 50
                    )
            );
            assertEquals(invalidPageNumber, dto.getPageNumber());
        }

        @ParameterizedTest(name = "Kích thước trang ngoài biên {0} đi qua DTO để Service xử lý")
        @ValueSource(ints = {-10, 0, 9, 101, 500})
        @DisplayName("Cho phép pageSize ngoài đoạn [10, 100] đi qua DTO")
        void shouldAllowOutOfRangePageSizePassthrough(int invalidPageSize) {
            FilterTransfersRequestDTO dto = assertDoesNotThrow(
                    () -> new FilterTransfersRequestDTO(
                            startDate, endDate, null, null, 1, invalidPageSize
                    )
            );
            assertEquals(invalidPageSize, dto.getPageSize());
        }
    }

    @Nested
    @DisplayName("Nhóm 4: Temporal Invariants - Bất biến thời gian và Passthrough Khoảng Ngày")
    class TemporalInvariantTests {

        @Test
        @DisplayName("Cho phép khoảng ngày đảo ngược (startDate > endDate) đi qua DTO để Service/Domain ném ERR_FIN_001")
        void shouldAllowInvertedDateRangePassthroughForServiceValidation() {
            LocalDate invalidStart = LocalDate.of(2026, 11, 25);
            LocalDate invalidEnd = LocalDate.of(2026, 11, 10);

            FilterTransfersRequestDTO dto = assertDoesNotThrow(
                    () -> new FilterTransfersRequestDTO(
                            invalidStart, invalidEnd, sampleSourceWalletId, sampleDestinationWalletId, 1, 50
                    )
            );
            assertEquals(invalidStart, dto.getStartDate());
            assertEquals(invalidEnd, dto.getEndDate());
            assertTrue(dto.hasDateFilter());
        }

        @Test
        @DisplayName("Cho phép mốc ngày tương lai đi qua DTO để Service/Domain xử lý ERR_FIN_001")
        void shouldAllowFutureDatesPassthroughForServiceValidation() {
            LocalDate futureDate = LocalDate.now().plusDays(1);

            FilterTransfersRequestDTO dto = assertDoesNotThrow(
                    () -> new FilterTransfersRequestDTO(futureDate, futureDate, null, null)
            );
            assertEquals(futureDate, dto.getStartDate());
            assertEquals(futureDate, dto.getEndDate());
        }

        @Test
        @DisplayName("Chấp nhận khoảng ngày năm nhuận (29/02) và biên cuối tháng")
        void shouldAcceptLeapYearDateRange() {
            LocalDate leapStart = LocalDate.of(2024, 2, 1);
            LocalDate leapEnd = LocalDate.of(2024, 2, 29);

            FilterTransfersRequestDTO dto = new FilterTransfersRequestDTO(
                    leapStart, leapEnd, null, null
            );

            assertEquals(leapStart, dto.getStartDate());
            assertEquals(leapEnd, dto.getEndDate());
        }
    }

    @Nested
    @DisplayName("Nhóm 5: State & Robustness - Hợp đồng equals, hashCode và toString")
    class StateAndRobustnessTests {

        @Test
        @DisplayName("Hai đối tượng bằng nhau và cùng hashCode khi khớp toàn bộ tiêu chí")
        void shouldBeEqualWhenAllFieldsMatch() {
            FilterTransfersRequestDTO dto1 = new FilterTransfersRequestDTO(
                    startDate, endDate, sampleSourceWalletId, sampleDestinationWalletId, 1, 20
            );
            FilterTransfersRequestDTO dto2 = new FilterTransfersRequestDTO(
                    startDate, endDate, sampleSourceWalletId, sampleDestinationWalletId, 1, 20
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Không bằng nhau khi khác bất kỳ tiêu chí lọc hoặc thông số phân trang nào")
        void shouldNotEqualWhenFieldsDiffer() {
            FilterTransfersRequestDTO base = new FilterTransfersRequestDTO(
                    startDate, endDate, sampleSourceWalletId, sampleDestinationWalletId, 1, 50
            );
            FilterTransfersRequestDTO diffPage = new FilterTransfersRequestDTO(
                    startDate, endDate, sampleSourceWalletId, sampleDestinationWalletId, 2, 50
            );
            FilterTransfersRequestDTO diffSource = new FilterTransfersRequestDTO(
                    startDate, endDate, UUID.randomUUID(), sampleDestinationWalletId, 1, 50
            );

            assertEquals(base, base);
            assertNotEquals(base, diffPage);
            assertNotEquals(base, diffSource);
            assertNotEquals(base, null);
            assertNotEquals(base, "other");
        }

        @Test
        @DisplayName("toString thể hiện đầy đủ tên lớp và các tiêu chí lọc")
        void shouldProduceInformativeToString() {
            FilterTransfersRequestDTO dto = new FilterTransfersRequestDTO(
                    startDate, endDate, sampleSourceWalletId, sampleDestinationWalletId, 3, 25
            );

            String str = dto.toString();
            assertTrue(str.contains("FilterTransfersRequestDTO"));
            assertTrue(str.contains(sampleSourceWalletId.toString()));
            assertTrue(str.contains(sampleDestinationWalletId.toString()));
            assertTrue(str.contains("pageNumber=3"));
            assertTrue(str.contains("pageSize=25"));
        }
    }
}

