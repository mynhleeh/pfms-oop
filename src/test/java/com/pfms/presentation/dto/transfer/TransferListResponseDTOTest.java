package com.pfms.presentation.dto.transfer;

import com.pfms.common.util.PaginationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử TransferListResponseDTO")
class TransferListResponseDTOTest {

    private final UUID sampleSourceWalletId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID sampleDestinationWalletId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID sampleFeeCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private final LocalDate sampleDate = LocalDate.of(2026, 11, 12);
    private final LocalDateTime sampleCreatedAt = LocalDateTime.of(2026, 11, 12, 10, 0);

    private TransferResponseDTO createSampleItem(BigDecimal amount, BigDecimal fee) {
        return new TransferResponseDTO(
                UUID.randomUUID(),
                sampleSourceWalletId,
                "Tài khoản MB Bank",
                sampleDestinationWalletId,
                "Tiền mặt sinh hoạt",
                amount,
                sampleDate,
                "Chuyển khoản mẫu",
                fee,
                fee.compareTo(BigDecimal.ZERO) > 0 ? sampleFeeCategoryId : null,
                fee.compareTo(BigDecimal.ZERO) > 0 ? "Phí ngân hàng" : "",
                new BigDecimal("10000000.00"),
                new BigDecimal("5000000.00"),
                sampleCreatedAt
        );
    }

    @Nested
    @DisplayName("Nhóm 1: Happy Path - Khởi tạo và đóng gói dữ liệu lịch sử phân trang hợp lệ")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ danh sách, 2 chỉ số tổng hợp và thông số phân trang")
        void shouldCreateWithAllFields() {
            List<TransferResponseDTO> items = List.of(
                    createSampleItem(new BigDecimal("3000000.00"), new BigDecimal("3300.00")),
                    createSampleItem(new BigDecimal("2000000.00"), BigDecimal.ZERO)
            );
            BigDecimal totalTransferred = new BigDecimal("5000000.00");
            BigDecimal totalFees = new BigDecimal("3300.00");

            TransferListResponseDTO dto = new TransferListResponseDTO(
                    items,
                    totalTransferred,
                    totalFees,
                    1,
                    50,
                    2L,
                    1
            );

            assertEquals(2, dto.getItems().size());
            assertEquals(2, dto.getItemCount());
            assertFalse(dto.isEmpty());
            assertEquals(totalTransferred, dto.getTotalTransferredAmount());
            assertEquals(totalFees, dto.getTotalFees());
            assertEquals(1, dto.getPageNumber());
            assertEquals(50, dto.getPageSize());
            assertEquals(2L, dto.getTotalRecords());
            assertEquals(1, dto.getTotalPages());
            assertFalse(dto.hasNextPage());
            assertFalse(dto.hasPreviousPage());
        }

        @Test
        @DisplayName("Khởi tạo không đối số tạo ra danh sách rỗng và các chỉ số bằng 0")
        void shouldCreateDefaultEmptyHistory() {
            TransferListResponseDTO dto = new TransferListResponseDTO();

            assertTrue(dto.isEmpty());
            assertEquals(0, dto.getItemCount());
            assertTrue(dto.getItems().isEmpty());
            assertEquals(BigDecimal.ZERO, dto.getTotalTransferredAmount());
            assertEquals(BigDecimal.ZERO, dto.getTotalFees());
            assertEquals(0L, dto.getTotalRecords());
        }

        @Test
        @DisplayName("Khởi tạo không đối số mang thông số phân trang mặc định trang 1, size 50, tổng 0 trang")
        void shouldProvideDefaultPaginationMetadata() {
            TransferListResponseDTO dto = new TransferListResponseDTO();

            assertEquals(PaginationUtils.DEFAULT_PAGE_NUMBER, dto.getPageNumber());
            assertEquals(PaginationUtils.DEFAULT_PAGE_SIZE, dto.getPageSize());
            assertEquals(0L, dto.getTotalRecords());
            assertEquals(0, dto.getTotalPages());
            assertFalse(dto.hasNextPage());
            assertFalse(dto.hasPreviousPage());
        }

        @Test
        @DisplayName("Xác định đúng hasNextPage và hasPreviousPage theo pageNumber và totalPages")
        void shouldEvaluateNextAndPreviousPageFlagsCorrectly() {
            TransferListResponseDTO firstPage = new TransferListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, 1, 10, 25L, 3);
            TransferListResponseDTO middlePage = new TransferListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, 2, 10, 25L, 3);
            TransferListResponseDTO lastPage = new TransferListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, 3, 10, 25L, 3);

            assertTrue(firstPage.hasNextPage());
            assertFalse(firstPage.hasPreviousPage());
            assertTrue(middlePage.hasNextPage());
            assertTrue(middlePage.hasPreviousPage());
            assertFalse(lastPage.hasNextPage());
            assertTrue(lastPage.hasPreviousPage());
        }

        @Test
        @DisplayName("Chuẩn hóa danh sách items null thành danh sách rỗng bất biến")
        void shouldNormalizeNullListToEmptyList() {
            TransferListResponseDTO dto = new TransferListResponseDTO(
                    null,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    1,
                    50,
                    0L,
                    0
            );

            assertNotNull(dto.getItems());
            assertTrue(dto.getItems().isEmpty());
            assertTrue(dto.isEmpty());
            assertEquals(0, dto.getItemCount());
        }
    }

    @Nested
    @DisplayName("Nhóm 2: Boundary & Precision - Biên số học và bất biến scale")
    class BoundaryAndPrecisionTests {

        @Test
        @DisplayName("Chấp nhận tổng tiền chuyển cực lớn (10^12) và phí bằng 0.00")
        void shouldAcceptLargeTransferredAmountAndZeroFees() {
            BigDecimal largeAmount = new BigDecimal("1000000000000.00");

            TransferListResponseDTO dto = new TransferListResponseDTO(
                    Collections.emptyList(),
                    largeAmount,
                    BigDecimal.ZERO,
                    1,
                    50,
                    1000L,
                    20
            );

            assertEquals(0, largeAmount.compareTo(dto.getTotalTransferredAmount()));
            assertEquals(0, BigDecimal.ZERO.compareTo(dto.getTotalFees()));
            assertEquals(1000L, dto.getTotalRecords());
            assertEquals(20, dto.getTotalPages());
        }

        @Test
        @DisplayName("Hai đối tượng bằng nhau và cùng hashCode khi cùng giá trị số học dù khác scale")
        void shouldBeEqualRegardlessOfBigDecimalScale() {
            TransferListResponseDTO dto1 = new TransferListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("15000000.0"),
                    new BigDecimal("6600.0"),
                    1,
                    50,
                    5L,
                    1
            );

            TransferListResponseDTO dto2 = new TransferListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("15000000.00"),
                    new BigDecimal("6600.00"),
                    1,
                    50,
                    5L,
                    1
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("Nhóm 3: Negative & Zero Values - Chấp nhận các giá trị biên làm pure data carrier")
    class NegativeAndZeroValueTests {

        @ParameterizedTest(name = "Chấp nhận tổng tiền chuyển âm ({0}) làm pure data carrier")
        @ValueSource(strings = {"-0.01", "-1", "-5000000.00"})
        @DisplayName("Chấp nhận totalTransferredAmount âm để giữ DTO là pure data carrier")
        void shouldAllowNegativeTotalTransferredAmountAsPureDataCarrier(String negativeAmount) {
            BigDecimal amount = new BigDecimal(negativeAmount);
            TransferListResponseDTO dto = new TransferListResponseDTO(
                    Collections.emptyList(),
                    amount,
                    BigDecimal.ZERO,
                    1,
                    50,
                    0L,
                    0
            );
            assertEquals(0, amount.compareTo(dto.getTotalTransferredAmount()));
        }

        @ParameterizedTest(name = "Chấp nhận tổng phụ phí âm ({0}) làm pure data carrier")
        @ValueSource(strings = {"-0.01", "-100", "-3300.00"})
        @DisplayName("Chấp nhận totalFees âm để giữ DTO là pure data carrier")
        void shouldAllowNegativeTotalFeesAsPureDataCarrier(String negativeFee) {
            BigDecimal fee = new BigDecimal(negativeFee);
            TransferListResponseDTO dto = new TransferListResponseDTO(
                    Collections.emptyList(),
                    BigDecimal.ZERO,
                    fee,
                    1,
                    50,
                    0L,
                    0
            );
            assertEquals(0, fee.compareTo(dto.getTotalFees()));
        }

        @ParameterizedTest(name = "Chấp nhận tổng số bản ghi âm ({0}) làm pure data carrier")
        @ValueSource(longs = {-100L, -1L})
        @DisplayName("Chấp nhận totalRecords âm để giữ DTO là pure data carrier")
        void shouldAllowNegativeTotalRecordsAsPureDataCarrier(long negativeRecords) {
            TransferListResponseDTO dto = new TransferListResponseDTO(
                    Collections.emptyList(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    1,
                    50,
                    negativeRecords,
                    0
            );
            assertEquals(negativeRecords, dto.getTotalRecords());
        }
    }

    @Nested
    @DisplayName("Nhóm 4 & 5: Defensive Copying, State & Robustness - Sao chép phòng thủ và hợp đồng định danh")
    class DefensiveCopyingAndRobustnessTests {

        @Test
        @DisplayName("Biến đổi List bên ngoài không ảnh hưởng đến danh sách bên trong DTO")
        void shouldProtectAgainstExternalListMutation() {
            List<TransferResponseDTO> mutableList = new ArrayList<>();
            mutableList.add(createSampleItem(new BigDecimal("1000000.00"), BigDecimal.ZERO));

            TransferListResponseDTO dto = new TransferListResponseDTO(
                    mutableList,
                    new BigDecimal("1000000.00"),
                    BigDecimal.ZERO,
                    1,
                    50,
                    1L,
                    1
            );

            assertEquals(1, dto.getItems().size());

            mutableList.add(createSampleItem(new BigDecimal("2000000.00"), BigDecimal.ZERO));
            assertEquals(1, dto.getItems().size(), "DTO phải giữ bản sao phòng thủ độc lập");
        }

        @Test
        @DisplayName("Danh sách trả về từ getItems() không thể sửa đổi trực tiếp")
        void shouldReturnUnmodifiableList() {
            TransferListResponseDTO dto = new TransferListResponseDTO();

            assertThrows(
                    UnsupportedOperationException.class,
                    () -> dto.getItems().add(createSampleItem(new BigDecimal("500000.00"), BigDecimal.ZERO))
            );
        }

        @Test
        @DisplayName("Ném NullPointerException khi totalTransferredAmount hoặc totalFees bị null")
        void shouldThrowWhenTotalsAreNull() {
            NullPointerException ex1 = assertThrows(
                    NullPointerException.class,
                    () -> new TransferListResponseDTO(Collections.emptyList(), null, BigDecimal.ZERO, 1, 50, 0L, 0)
            );
            assertTrue(ex1.getMessage().contains("Tổng số tiền chuyển"));

            NullPointerException ex2 = assertThrows(
                    NullPointerException.class,
                    () -> new TransferListResponseDTO(Collections.emptyList(), BigDecimal.ZERO, null, 1, 50, 0L, 0)
            );
            assertTrue(ex2.getMessage().contains("Tổng chi phí phát sinh"));
        }

        @Test
        @DisplayName("Thỏa mãn hợp đồng equals, hashCode và toString")
        void shouldSatisfyEqualsHashCodeAndToString() {
            TransferListResponseDTO base = new TransferListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("5000000"),
                    new BigDecimal("3300"),
                    1,
                    50,
                    2L,
                    1
            );

            TransferListResponseDTO diffRecords = new TransferListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("5000000"),
                    new BigDecimal("3300"),
                    1,
                    50,
                    3L,
                    1
            );

            TransferListResponseDTO diffPageNumber = new TransferListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("5000000"),
                    new BigDecimal("3300"),
                    2,
                    50,
                    2L,
                    1
            );

            TransferListResponseDTO diffPageSize = new TransferListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("5000000"),
                    new BigDecimal("3300"),
                    1,
                    25,
                    2L,
                    1
            );

            TransferListResponseDTO diffTotalPages = new TransferListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("5000000"),
                    new BigDecimal("3300"),
                    1,
                    50,
                    2L,
                    2
            );

            assertEquals(base, base);
            assertNotEquals(base, diffRecords);
            assertNotEquals(base, diffPageNumber);
            assertNotEquals(base, diffPageSize);
            assertNotEquals(base, diffTotalPages);
            assertNotEquals(base, null);
            assertNotEquals(base, "text");

            String str = base.toString();
            assertTrue(str.contains("TransferListResponseDTO"));
            assertTrue(str.contains("totalTransferredAmount=5000000"));
            assertTrue(str.contains("totalFees=3300"));
            assertTrue(str.contains("pageNumber=1"));
            assertTrue(str.contains("pageSize=50"));
            assertTrue(str.contains("totalRecords=2"));
            assertTrue(str.contains("totalPages=1"));
        }
    }
}

