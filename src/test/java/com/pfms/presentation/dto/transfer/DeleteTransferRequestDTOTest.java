package com.pfms.presentation.dto.transfer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử DeleteTransferRequestDTO")
class DeleteTransferRequestDTOTest {

    @Nested
    @DisplayName("Nhóm 1: Happy Path - Khởi tạo thành công với mã giao dịch chuyển tiền hợp lệ")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo thành công và truy xuất đúng transferId qua getTransferId() và getId()")
        void shouldCreateSuccessfullyWithValidTransferId() {
            UUID transferId = UUID.randomUUID();
            DeleteTransferRequestDTO dto = new DeleteTransferRequestDTO(transferId);

            assertEquals(transferId, dto.getTransferId());
            assertEquals(transferId, dto.getId());
        }
    }

    @Nested
    @DisplayName("Nhóm 2: Boundary & Precision - Kiểm thử các giá trị biên của định danh UUID")
    class BoundaryTests {

        @ParameterizedTest(name = "Chấp nhận UUID biên: {0}")
        @ValueSource(strings = {
                "00000000-0000-0000-0000-000000000000",
                "00000000-0000-0000-0000-000000000001",
                "ffffffff-ffff-ffff-ffff-ffffffffffff"
        })
        @DisplayName("Chấp nhận mã định danh UUID tại cận dưới (nil UUID) và cận trên (max UUID)")
        void shouldAcceptBoundaryUuids(String boundaryUuidStr) {
            UUID boundaryId = UUID.fromString(boundaryUuidStr);
            DeleteTransferRequestDTO dto = new DeleteTransferRequestDTO(boundaryId);

            assertEquals(boundaryId, dto.getTransferId());
            assertEquals(boundaryId, dto.getId());
        }
    }

    @Nested
    @DisplayName("Nhóm 3 & 5: Negative, Null & Robustness - Kiểm soát tham số null và hợp đồng định danh")
    class NullAndRobustnessTests {

        @Test
        @DisplayName("Ném NullPointerException khi transferId là null")
        void shouldThrowExceptionWhenTransferIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new DeleteTransferRequestDTO(null)
            );
            assertTrue(ex.getMessage().contains("Mã giao dịch chuyển tiền không được để null"));
        }

        @Test
        @DisplayName("Hai DTO cùng transferId phải bằng nhau và có cùng hashCode")
        void shouldBeEqualWhenSameTransferId() {
            UUID id = UUID.randomUUID();
            DeleteTransferRequestDTO dto1 = new DeleteTransferRequestDTO(id);
            DeleteTransferRequestDTO dto2 = new DeleteTransferRequestDTO(id);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai DTO khác transferId không bằng nhau và xử lý an toàn với null/kiểu khác")
        void shouldNotBeEqualWhenDifferentTransferIdOrOtherTypes() {
            DeleteTransferRequestDTO dto1 = new DeleteTransferRequestDTO(UUID.randomUUID());
            DeleteTransferRequestDTO dto2 = new DeleteTransferRequestDTO(UUID.randomUUID());

            assertEquals(dto1, dto1);
            assertNotEquals(dto1, dto2);
            assertNotEquals(null, dto1);
            assertNotEquals("otherType", dto1);
        }

        @Test
        @DisplayName("toString phải chứa tên lớp và mã giao dịch chuyển tiền")
        void shouldContainTransferIdInToString() {
            UUID id = UUID.randomUUID();
            DeleteTransferRequestDTO dto = new DeleteTransferRequestDTO(id);

            String str = dto.toString();
            assertTrue(str.contains("DeleteTransferRequestDTO"));
            assertTrue(str.contains(id.toString()));
        }
    }
}
