package com.pfms.presentation.dto.transaction;

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

@DisplayName("Kiểm thử TransactionListResponseDTO")
class TransactionListResponseDTOTest {

    private final UUID sampleTxnId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID sampleWalletId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID sampleCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private final LocalDate sampleDate = LocalDate.of(2026, 11, 10);
    private final LocalDateTime sampleCreatedAt = LocalDateTime.of(2026, 11, 10, 9, 0);

    private TransactionResponseDTO createSampleItem(BigDecimal amount, String type) {
        return new TransactionResponseDTO(
                UUID.randomUUID(),
                sampleWalletId,
                "Ví Tiền mặt",
                sampleCategoryId,
                "Danh mục mẫu",
                amount,
                type,
                sampleDate,
                "Giao dịch mẫu",
                new BigDecimal("5000000.00"),
                sampleCreatedAt
        );
    }

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ danh sách và chỉ số dòng tiền")
        void shouldCreateWithAllFields() {
            List<TransactionResponseDTO> list = List.of(
                    createSampleItem(new BigDecimal("10000000.00"), "INCOME"),
                    createSampleItem(new BigDecimal("2500000.00"), "EXPENSE")
            );

            BigDecimal totalIncome = new BigDecimal("10000000.00");
            BigDecimal totalExpense = new BigDecimal("2500000.00");
            BigDecimal netFlow = new BigDecimal("7500000.00");

            TransactionListResponseDTO dto = new TransactionListResponseDTO(
                    list, totalIncome, totalExpense, netFlow, 1, 20, 45, 3
            );

            assertEquals(2, dto.getItems().size());
            assertEquals(2, dto.getItemCount());
            assertFalse(dto.isEmpty());
            assertEquals(totalIncome, dto.getTotalIncome());
            assertEquals(totalExpense, dto.getTotalExpense());
            assertEquals(netFlow, dto.getNetCashFlow());
            assertEquals(1, dto.getPageNumber());
            assertEquals(20, dto.getPageSize());
            assertEquals(45, dto.getTotalRecords());
            assertEquals(3, dto.getTotalPages());
            assertTrue(dto.hasNextPage());
            assertFalse(dto.hasPreviousPage());
        }

        @Test
        @DisplayName("Khởi tạo không đối số tạo ra danh sách rỗng, các chỉ số bằng 0, trang 1/50")
        void shouldCreateDefaultEmpty() {
            TransactionListResponseDTO dto = new TransactionListResponseDTO();

            assertTrue(dto.isEmpty());
            assertEquals(0, dto.getItemCount());
            assertEquals(BigDecimal.ZERO, dto.getTotalIncome());
            assertEquals(BigDecimal.ZERO, dto.getTotalExpense());
            assertEquals(BigDecimal.ZERO, dto.getNetCashFlow());
            assertEquals(1, dto.getPageNumber());
            assertEquals(50, dto.getPageSize());
            assertEquals(0, dto.getTotalRecords());
            assertEquals(0, dto.getTotalPages());
            assertFalse(dto.hasNextPage());
            assertFalse(dto.hasPreviousPage());
        }

        @Test
        @DisplayName("Chấp nhận dòng tiền thuần âm khi tổng chi lớn hơn tổng thu")
        void shouldAcceptNegativeNetCashFlow() {
            BigDecimal totalIncome = new BigDecimal("1000000.00");
            BigDecimal totalExpense = new BigDecimal("3500000.00");
            BigDecimal negativeNet = new BigDecimal("-2500000.00");

            TransactionListResponseDTO dto = new TransactionListResponseDTO(
                    Collections.emptyList(), totalIncome, totalExpense, negativeNet, 2, 20, 25, 2
            );

            assertEquals(negativeNet, dto.getNetCashFlow());
            assertFalse(dto.hasNextPage());
            assertTrue(dto.hasPreviousPage());
        }

        @Test
        @DisplayName("Chuẩn hóa danh sách null thành danh sách rỗng bất biến")
        void shouldNormalizeNullList() {
            TransactionListResponseDTO dto = new TransactionListResponseDTO(
                    null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 50, 0, 0
            );

            assertNotNull(dto.getItems());
            assertTrue(dto.getItems().isEmpty());
        }
    }

    @Nested
    @DisplayName("Sao chép phòng thủ (Defensive Copying)")
    class DefensiveCopyingTests {

        @Test
        @DisplayName("Biến đổi List bên ngoài không ảnh hưởng đến trạng thái trong DTO")
        void shouldProtectAgainstExternalMutation() {
            List<TransactionResponseDTO> mutableList = new ArrayList<>();
            mutableList.add(createSampleItem(new BigDecimal("100000"), "EXPENSE"));

            TransactionListResponseDTO dto = new TransactionListResponseDTO(
                    mutableList, BigDecimal.ZERO, new BigDecimal("100000"),
                    new BigDecimal("-100000"), 1, 50, 1, 1
            );

            assertEquals(1, dto.getItems().size());

            // Thử thêm phần tử bên ngoài
            mutableList.add(createSampleItem(new BigDecimal("200000"), "EXPENSE"));
            assertEquals(1, dto.getItems().size(), "DTO phải giữ bản sao phòng thủ độc lập");
        }

        @Test
        @DisplayName("Danh sách trả về từ getItems() không thể sửa đổi")
        void shouldReturnUnmodifiableList() {
            TransactionListResponseDTO dto = new TransactionListResponseDTO();

            assertThrows(UnsupportedOperationException.class,
                    () -> dto.getItems().add(createSampleItem(new BigDecimal("100000"), "INCOME")));
        }
    }

    @Nested
    @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
    class InvalidInputTests {

        @Test
        @DisplayName("Ném NullPointerException khi totalIncome bị null")
        void shouldThrowWhenTotalIncomeIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransactionListResponseDTO(
                            null, null, BigDecimal.ZERO, BigDecimal.ZERO, 1, 50, 0, 0)
            );
            assertTrue(ex.getMessage().contains("Tổng thu nhập"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi totalIncome âm")
        void shouldThrowWhenTotalIncomeIsNegative() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionListResponseDTO(
                            null, new BigDecimal("-100"), BigDecimal.ZERO, BigDecimal.ZERO, 1, 50, 0, 0)
            );
            assertTrue(ex.getMessage().contains("Tổng thu nhập không được âm"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi totalExpense bị null")
        void shouldThrowWhenTotalExpenseIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransactionListResponseDTO(
                            null, BigDecimal.ZERO, null, BigDecimal.ZERO, 1, 50, 0, 0)
            );
            assertTrue(ex.getMessage().contains("Tổng chi tiêu"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi totalExpense âm")
        void shouldThrowWhenTotalExpenseIsNegative() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionListResponseDTO(
                            null, BigDecimal.ZERO, new BigDecimal("-50"), BigDecimal.ZERO, 1, 50, 0, 0)
            );
            assertTrue(ex.getMessage().contains("Tổng chi tiêu không được âm"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi netCashFlow bị null")
        void shouldThrowWhenNetCashFlowIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TransactionListResponseDTO(
                            null, BigDecimal.ZERO, BigDecimal.ZERO, null, 1, 50, 0, 0)
            );
            assertTrue(ex.getMessage().contains("Dòng tiền thuần"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi pageNumber < 1")
        void shouldThrowWhenPageNumberIsLessThanOne() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionListResponseDTO(
                            null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 50, 0, 0)
            );
            assertTrue(ex.getMessage().contains("Chỉ số trang"));
        }

        @ParameterizedTest(name = "Kích thước trang không hợp lệ: {0}")
        @ValueSource(ints = { -1, 0, 9, 101, 200 })
        @DisplayName("Ném IllegalArgumentException khi pageSize ngoài khoảng 10 đến 100")
        void shouldThrowWhenPageSizeIsOutOfRange(int invalidSize) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionListResponseDTO(
                            null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, invalidSize, 0, 0)
            );
            assertTrue(ex.getMessage().contains("Kích thước trang"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi totalRecords âm")
        void shouldThrowWhenTotalRecordsIsNegative() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionListResponseDTO(
                            null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 50, -1, 0)
            );
            assertTrue(ex.getMessage().contains("Tổng số bản ghi"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi totalPages âm")
        void shouldThrowWhenTotalPagesIsNegative() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new TransactionListResponseDTO(
                            null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 50, 0, -1)
            );
            assertTrue(ex.getMessage().contains("Tổng số trang"));
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (equals, hashCode, toString)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai đối tượng bằng nhau khi cùng thuộc tính dù khác scale số học")
        void shouldBeEqualRegardlessOfScale() {
            TransactionListResponseDTO dto1 = new TransactionListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("1000.0"),
                    new BigDecimal("500.0"),
                    new BigDecimal("500.0"),
                    1, 20, 10, 1
            );

            TransactionListResponseDTO dto2 = new TransactionListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("1000.00"),
                    new BigDecimal("500.00"),
                    new BigDecimal("500.00"),
                    1, 20, 10, 1
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Không bằng nhau khi khác số trang hoặc dòng tiền")
        void shouldNotEqualWhenFieldsDiffer() {
            TransactionListResponseDTO base = new TransactionListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1, 50, 0, 0
            );

            TransactionListResponseDTO diffPage = new TransactionListResponseDTO(
                    Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 2, 50, 0, 0
            );

            assertNotEquals(base, diffPage);
            assertNotEquals(base, null);
            assertNotEquals(base, "text");
        }

        @Test
        @DisplayName("toString thể hiện đầy đủ các thông tin thống kê")
        void shouldProduceInformativeToString() {
            TransactionListResponseDTO dto = new TransactionListResponseDTO(
                    Collections.emptyList(),
                    new BigDecimal("5000000"),
                    new BigDecimal("2000000"),
                    new BigDecimal("3000000"),
                    1, 50, 10, 1
            );

            String str = dto.toString();
            assertTrue(str.contains("TransactionListResponseDTO"));
            assertTrue(str.contains("totalIncome=5000000"));
            assertTrue(str.contains("totalExpense=2000000"));
            assertTrue(str.contains("netCashFlow=3000000"));
            assertTrue(str.contains("totalRecords=10"));
        }
    }
}

