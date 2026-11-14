package com.pfms.presentation.dto.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CategoryShareResponseDTO")
class CategoryShareResponseDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 5 tham số")
        void shouldCreateWithAllFiveParameters() {
            UUID id = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("4500000");
            BigDecimal share = new BigDecimal("30.00");

            CategoryShareResponseDTO dto = new CategoryShareResponseDTO(
                    id, "Ăn uống hàng ngày", "ACTIVE", amount, share);

            assertEquals(id, dto.getCategoryId());
            assertEquals("Ăn uống hàng ngày", dto.getName());
            assertEquals("ACTIVE", dto.getStatus());
            assertEquals(amount, dto.getTotalAmount());
            assertEquals(share, dto.getShare());
            assertTrue(dto.isActive());
            assertFalse(dto.isDeleted());
        }

        @Test
        @DisplayName("Khởi tạo với 4 tham số sẽ mặc định trạng thái ACTIVE")
        void shouldDefaultStatusToActiveWithFourParameters() {
            UUID id = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("1500000");
            BigDecimal share = new BigDecimal("10.00");

            CategoryShareResponseDTO dto = new CategoryShareResponseDTO(
                    id, "Tiền điện nước", amount, share);

            assertEquals(id, dto.getCategoryId());
            assertEquals("Tiền điện nước", dto.getName());
            assertEquals("ACTIVE", dto.getStatus());
            assertTrue(dto.isActive());
        }

        @Test
        @DisplayName("Nhận diện chính xác trạng thái xóa mềm DELETED")
        void shouldIdentifyDeletedStatus() {
            UUID id = UUID.randomUUID();
            CategoryShareResponseDTO dto = new CategoryShareResponseDTO(
                    id, "Mua sắm cũ (đã xóa)", "DELETED", new BigDecimal("500000"), new BigDecimal("3.33"));

            assertEquals("DELETED", dto.getStatus());
            assertFalse(dto.isActive());
            assertTrue(dto.isDeleted());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa chuỗi và gán giá trị mặc định an toàn khi null")
        void shouldNormalizeStringsAndDefaultNullValues() {
            UUID id = UUID.randomUUID();
            CategoryShareResponseDTO dto = new CategoryShareResponseDTO(
                    id, "   Cà phê   ", "   active   ", null, null);

            assertEquals("Cà phê", dto.getName());
            assertEquals("ACTIVE", dto.getStatus());
            assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
            assertEquals(BigDecimal.ZERO, dto.getShare());
        }

        @Test
        @DisplayName("Khi tên là null thì mặc định chuỗi rỗng")
        void shouldDefaultNullNameToEmpty() {
            UUID id = UUID.randomUUID();
            CategoryShareResponseDTO dto = new CategoryShareResponseDTO(
                    id, null, "ACTIVE", BigDecimal.ZERO, BigDecimal.ZERO);

            assertEquals("", dto.getName());
        }
    }

    @Nested
    @DisplayName("Kiểm tra Ràng buộc Null (Null Safety)")
    class NullSafetyTests {

        @Test
        @DisplayName("Ném NullPointerException khi categoryId là null")
        void shouldThrowExceptionWhenCategoryIdIsNull() {
            assertThrows(NullPointerException.class,
                    () -> new CategoryShareResponseDTO(null, "Ăn uống", "ACTIVE", BigDecimal.ZERO, BigDecimal.ZERO));
            assertThrows(NullPointerException.class,
                    () -> new CategoryShareResponseDTO(null, "Ăn uống", BigDecimal.ZERO, BigDecimal.ZERO));
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (Identity Contract)")
    class IdentityContractTests {

        @Test
        @DisplayName("Hai đối tượng có cùng thuộc tính phải bằng nhau và có cùng hashCode")
        void shouldBeEqualWhenSameProperties() {
            UUID id = UUID.randomUUID();
            CategoryShareResponseDTO dto1 = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"));
            CategoryShareResponseDTO dto2 = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"));

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Bảo đảm tính bất biến về thang số thập phân (Scale Invariance) trong equals và hashCode")
        void shouldBeEqualRegardlessOfBigDecimalScale() {
            UUID id = UUID.randomUUID();
            CategoryShareResponseDTO dto1 = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.0"));
            CategoryShareResponseDTO dto2 = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("100.00"), new BigDecimal("20.000"));

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác thuộc tính thì không bằng nhau")
        void shouldNotBeEqualWhenDifferentProperties() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();

            CategoryShareResponseDTO base = new CategoryShareResponseDTO(
                    id1, "Ăn uống", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"));
            CategoryShareResponseDTO diffId = new CategoryShareResponseDTO(
                    id2, "Ăn uống", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"));
            CategoryShareResponseDTO diffName = new CategoryShareResponseDTO(
                    id1, "Mua sắm", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"));

            assertNotEquals(base, diffId);
            assertNotEquals(base, diffName);
            assertNotEquals(base, null);
            assertNotEquals(base, "Share");
        }

        @Test
        @DisplayName("equals với chính nó trả về true")
        void shouldBeEqualToItself() {
            UUID id = UUID.randomUUID();
            CategoryShareResponseDTO dto = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", BigDecimal.ZERO, BigDecimal.ZERO);
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("toString chứa đầy đủ thông tin mã danh mục, tên và tỷ trọng")
        void shouldContainFieldsInToString() {
            UUID id = UUID.randomUUID();
            CategoryShareResponseDTO dto = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"));
            String str = dto.toString();

            assertTrue(str.contains(id.toString()));
            assertTrue(str.contains("Ăn uống"));
            assertTrue(str.contains("ACTIVE"));
            assertTrue(str.contains("20.00"));
            assertTrue(str.contains("CategoryShareResponseDTO"));
        }
    }
}

