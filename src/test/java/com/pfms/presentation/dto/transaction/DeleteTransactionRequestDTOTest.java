package com.pfms.presentation.dto.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DeleteTransactionRequestDTO Specification Tests")
class DeleteTransactionRequestDTOTest {

    @Nested
    @DisplayName("1. Khởi tạo hợp lệ (Valid Creation)")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với transactionId hợp lệ")
        void shouldCreateSuccessfullyWithValidTransactionId() {
            UUID id = UUID.randomUUID();
            DeleteTransactionRequestDTO dto = new DeleteTransactionRequestDTO(id);

            assertEquals(id, dto.getTransactionId());
            assertEquals(id, dto.getId());
        }
    }

    @Nested
    @DisplayName("2. Kiểm tra dữ liệu không hợp lệ (Invalid Input)")
    class InvalidInputTests {

        @Test
        @DisplayName("Từ chối khi transactionId là null")
        void shouldThrowExceptionWhenTransactionIdIsNull() {
            NullPointerException ex = assertThrows(NullPointerException.class, () ->
                    new DeleteTransactionRequestDTO(null));
            assertTrue(ex.getMessage().contains("Mã giao dịch không được để null"));
        }
    }

    @Nested
    @DisplayName("3. Bình đẳng và Hợp đồng (Equality and Contract)")
    class EqualityAndContractTests {

        @Test
        @DisplayName("Hai DTO cùng transactionId phải bằng nhau và cùng hashCode")
        void shouldBeEqualWhenSameTransactionId() {
            UUID id = UUID.randomUUID();
            DeleteTransactionRequestDTO dto1 = new DeleteTransactionRequestDTO(id);
            DeleteTransactionRequestDTO dto2 = new DeleteTransactionRequestDTO(id);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai DTO khác transactionId không được bằng nhau")
        void shouldNotBeEqualWhenDifferentTransactionId() {
            DeleteTransactionRequestDTO dto1 = new DeleteTransactionRequestDTO(UUID.randomUUID());
            DeleteTransactionRequestDTO dto2 = new DeleteTransactionRequestDTO(UUID.randomUUID());

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("So sánh với chính nó, null và kiểu khác")
        void shouldHandleEdgeCasesInEquals() {
            DeleteTransactionRequestDTO dto = new DeleteTransactionRequestDTO(UUID.randomUUID());

            assertEquals(dto, dto);
            assertNotEquals(null, dto);
            assertNotEquals("otherType", dto);
        }

        @Test
        @DisplayName("toString phải chứa mã giao dịch")
        void shouldContainTransactionIdInToString() {
            UUID id = UUID.randomUUID();
            DeleteTransactionRequestDTO dto = new DeleteTransactionRequestDTO(id);

            assertTrue(dto.toString().contains(id.toString()));
            assertTrue(dto.toString().contains("DeleteTransactionRequestDTO"));
        }
    }
}

