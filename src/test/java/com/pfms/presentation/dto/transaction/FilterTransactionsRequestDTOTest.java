package com.pfms.presentation.dto.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử FilterTransactionsRequestDTO")
class FilterTransactionsRequestDTOTest {

    private final UUID wallet1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID wallet2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID cat1 = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private final LocalDate startDate = LocalDate.of(2026, 11, 1);
    private final LocalDate endDate = LocalDate.of(2026, 11, 30);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 8 tiêu chí lọc và phân trang")
        void shouldCreateWithAllCriteria() {
            Set<UUID> wallets = Set.of(wallet1, wallet2);
            Set<UUID> cats = Set.of(cat1);

            FilterTransactionsRequestDTO dto = new FilterTransactionsRequestDTO(
                    startDate,
                    endDate,
                    wallets,
                    "EXPENSE",
                    cats,
                    "siêu thị",
                    2,
                    25
            );

            assertEquals(startDate, dto.getStartDate());
            assertEquals(endDate, dto.getEndDate());
            assertEquals(wallets, dto.getWalletIds());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals(cats, dto.getCategoryIds());
            assertEquals("siêu thị", dto.getKeyword());
            assertEquals(2, dto.getPageNumber());
            assertEquals(25, dto.getPageSize());
            assertTrue(dto.hasDateFilter());
            assertTrue(dto.hasWalletFilter());
            assertTrue(dto.hasCategoryFilter());
            assertTrue(dto.hasTypeFilter());
            assertTrue(dto.hasKeywordFilter());
        }

        @Test
        @DisplayName("Khởi tạo không đối số (mặc định xem tất cả, trang 1, 50 bản ghi/trang)")
        void shouldCreateDefaultEmptyFilter() {
            FilterTransactionsRequestDTO dto = new FilterTransactionsRequestDTO();

            assertNull(dto.getStartDate());
            assertNull(dto.getEndDate());
            assertTrue(dto.getWalletIds().isEmpty());
            assertNull(dto.getTransactionType());
            assertTrue(dto.getCategoryIds().isEmpty());
            assertEquals("", dto.getKeyword());
            assertEquals(1, dto.getPageNumber());
            assertEquals(50, dto.getPageSize());
            assertFalse(dto.hasDateFilter());
            assertFalse(dto.hasWalletFilter());
            assertFalse(dto.hasCategoryFilter());
            assertFalse(dto.hasTypeFilter());
            assertFalse(dto.hasKeywordFilter());
        }

        @Test
        @DisplayName("Khởi tạo 6 tham số không phân trang (tự động gán trang 1, kích thước 50)")
        void shouldCreateWith6ParamsDefaultPagination() {
            FilterTransactionsRequestDTO dto = new FilterTransactionsRequestDTO(
                    startDate,
                    endDate,
                    Set.of(wallet1),
                    "INCOME",
                    Collections.emptySet(),
                    "lương tháng"
            );

            assertEquals(startDate, dto.getStartDate());
            assertEquals(endDate, dto.getEndDate());
            assertEquals(1, dto.getPageNumber());
            assertEquals(50, dto.getPageSize());
        }

        @Test
        @DisplayName("Khởi tạo nhanh chỉ với khoảng ngày và tập ví")
        void shouldCreateWithDateRangeAndWallets() {
            FilterTransactionsRequestDTO dto = new FilterTransactionsRequestDTO(
                    startDate,
                    endDate,
                    Set.of(wallet1)
            );

            assertEquals(startDate, dto.getStartDate());
            assertEquals(endDate, dto.getEndDate());
            assertEquals(Set.of(wallet1), dto.getWalletIds());
            assertNull(dto.getTransactionType());
            assertEquals(1, dto.getPageNumber());
            assertEquals(50, dto.getPageSize());
        }

        @Test
        @DisplayName("Chuẩn hóa khoảng trắng keyword và uppercase loại thu chi")
        void shouldNormalizeKeywordAndType() {
            FilterTransactionsRequestDTO dto = new FilterTransactionsRequestDTO(
                    null,
                    null,
                    null,
                    "  expense  ",
                    null,
                    "   mua sắm   ",
                    1,
                    50
            );

            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals("mua sắm", dto.getKeyword());
            assertTrue(dto.getWalletIds().isEmpty());
            assertTrue(dto.getCategoryIds().isEmpty());
        }
    }

    @Nested
    @DisplayName("Sao chép phòng thủ (Defensive Copying)")
    class DefensiveCopyingTests {

        @Test
        @DisplayName("Biến đổi Set bên ngoài không làm thay đổi trạng thái bên trong DTO")
        void shouldProtectAgainstExternalMutation() {
            Set<UUID> mutableWallets = new HashSet<>();
            mutableWallets.add(wallet1);

            FilterTransactionsRequestDTO dto = new FilterTransactionsRequestDTO(
                    null, null, mutableWallets, null, null, "", 1, 50);

            assertEquals(1, dto.getWalletIds().size());

            // Thử đột biến tập hợp bên ngoài
            mutableWallets.add(wallet2);
            assertEquals(1, dto.getWalletIds().size(), "DTO phải giữ bản sao phòng thủ độc lập");
        }

        @Test
        @DisplayName("Tập hợp trả về từ getter không thể bị chỉnh sửa trực tiếp")
        void shouldReturnUnmodifiableSets() {
            FilterTransactionsRequestDTO dto = new FilterTransactionsRequestDTO(
                    null, null, Set.of(wallet1), null, Set.of(cat1), "", 1, 50);

            assertThrows(UnsupportedOperationException.class, () -> dto.getWalletIds().add(wallet2));
            assertThrows(UnsupportedOperationException.class, () -> dto.getCategoryIds().clear());
        }
    }

    @Nested
    @DisplayName("Cho phép Dữ liệu Nghiệp vụ / Phân trang Đi qua DTO (Data Carrier thuần túy)")
    class DomainValidationPassthroughTests {

        @Test
        @DisplayName("Cho phép khoảng ngày đảo ngược (startDate > endDate) đi qua DTO để Service/Domain xử lý ERR_FIN_001")
        void shouldAllowInvertedDateRangePassthroughForServiceValidation() {
            LocalDate start = LocalDate.of(2026, 11, 20);
            LocalDate end = LocalDate.of(2026, 11, 10);

            FilterTransactionsRequestDTO dto = assertDoesNotThrow(
                    () -> new FilterTransactionsRequestDTO(start, end, null, null, null, "", 1, 50)
            );

            assertEquals(start, dto.getStartDate());
            assertEquals(end, dto.getEndDate());
            assertTrue(dto.hasDateFilter());
        }

        @Test
        @DisplayName("Cho phép mốc ngày tương lai đi qua DTO để Service/Domain xử lý ERR_FIN_001")
        void shouldAllowFutureDatesPassthroughForServiceValidation() {
            LocalDate futureDate = LocalDate.now().plusDays(1);

            FilterTransactionsRequestDTO dto = assertDoesNotThrow(
                    () -> new FilterTransactionsRequestDTO(futureDate, futureDate, null, null, null, "", 1, 50)
            );

            assertEquals(futureDate, dto.getStartDate());
            assertEquals(futureDate, dto.getEndDate());
        }

        @ParameterizedTest(name = "Chuỗi transactionType không chuẩn \"{0}\" đi qua DTO và được chuẩn hóa viết hoa")
        @ValueSource(strings = {"TRANSFER", "  transfer  ", "unknown"})
        @DisplayName("Chỉ chuẩn hóa trim().toUpperCase() cho transactionType thay vì ném lỗi tại DTO")
        void shouldNormalizeAndAllowArbitraryTransactionTypePassthrough(String rawType) {
            FilterTransactionsRequestDTO dto = assertDoesNotThrow(
                    () -> new FilterTransactionsRequestDTO(null, null, null, rawType, null, "", 1, 50)
            );

            assertEquals(rawType.trim().toUpperCase(), dto.getTransactionType());
            assertTrue(dto.hasTypeFilter());
        }

        @ParameterizedTest(name = "Chuỗi transactionType rỗng/khoảng trắng \"{0}\" chuẩn hóa thành null")
        @ValueSource(strings = {"", "   ", "\t"})
        @DisplayName("Chuyển transactionType toàn khoảng trắng thành null")
        void shouldNormalizeBlankTransactionTypeToNull(String blankType) {
            FilterTransactionsRequestDTO dto = new FilterTransactionsRequestDTO(
                    null, null, null, blankType, null, "", 1, 50);

            assertNull(dto.getTransactionType());
            assertFalse(dto.hasTypeFilter());
        }

        @ParameterizedTest(name = "Chỉ số trang không hợp lệ {0} đi qua DTO để tầng Service kiểm tra")
        @ValueSource(ints = { 0, -1, -10 })
        @DisplayName("Cho phép pageNumber < 1 đi qua DTO")
        void shouldAllowOutOfRangePageNumberPassthrough(int invalidPageNumber) {
            FilterTransactionsRequestDTO dto = assertDoesNotThrow(
                    () -> new FilterTransactionsRequestDTO(null, null, null, null, null, "", invalidPageNumber, 50)
            );

            assertEquals(invalidPageNumber, dto.getPageNumber());
        }

        @ParameterizedTest(name = "Kích thước trang ngoài biên {0} đi qua DTO để tầng Service kiểm tra")
        @ValueSource(ints = { -5, 0, 9, 101, 500 })
        @DisplayName("Cho phép pageSize ngoài khoảng 10 đến 100 đi qua DTO")
        void shouldAllowOutOfRangePageSizePassthrough(int invalidSize) {
            FilterTransactionsRequestDTO dto = assertDoesNotThrow(
                    () -> new FilterTransactionsRequestDTO(null, null, null, null, null, "", 1, invalidSize)
            );

            assertEquals(invalidSize, dto.getPageSize());
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (equals, hashCode, toString)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai đối tượng bằng nhau khi cùng toàn bộ tiêu chí")
        void shouldBeEqualWhenAllFieldsMatch() {
            FilterTransactionsRequestDTO dto1 = new FilterTransactionsRequestDTO(
                    startDate, endDate, Set.of(wallet1), "EXPENSE", Set.of(cat1), "cà phê", 1, 20);
            FilterTransactionsRequestDTO dto2 = new FilterTransactionsRequestDTO(
                    startDate, endDate, Set.of(wallet1), "EXPENSE", Set.of(cat1), "cà phê", 1, 20);

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Không bằng nhau khi khác tiêu chí lọc hoặc phân trang")
        void shouldNotEqualWhenFieldsDiffer() {
            FilterTransactionsRequestDTO base = new FilterTransactionsRequestDTO(
                    startDate, endDate, Set.of(wallet1), null, null, "", 1, 50);
            FilterTransactionsRequestDTO diffPage = new FilterTransactionsRequestDTO(
                    startDate, endDate, Set.of(wallet1), null, null, "", 2, 50);

            assertNotEquals(base, diffPage);
            assertNotEquals(base, null);
            assertNotEquals(base, "other");
        }

        @Test
        @DisplayName("toString thể hiện đầy đủ các tiêu chí lọc")
        void shouldProduceInformativeToString() {
            FilterTransactionsRequestDTO dto = new FilterTransactionsRequestDTO(
                    startDate, endDate, Set.of(wallet1), "INCOME", Set.of(cat1), "thưởng", 1, 15);

            String str = dto.toString();
            assertTrue(str.contains("FilterTransactionsRequestDTO"));
            assertTrue(str.contains("INCOME"));
            assertTrue(str.contains("thưởng"));
            assertTrue(str.contains("pageSize=15"));
        }
    }
}

