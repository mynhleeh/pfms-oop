package com.pfms.presentation.dto.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Locale;
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

    @Nested
    @DisplayName("Trường mở rộng shareInGroup và So sánh Kỳ liền kề")
    class ExtendedComparisonFieldsTests {

        @Test
        @DisplayName("Khởi tạo với constructor 6 và 10 tham số giữ nguyên đầy đủ tỷ trọng và biến động")
        void shouldCreateWithExtendedConstructors() {
            UUID id = UUID.randomUUID();
            CategoryShareResponseDTO sixArgs = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("500000"),
                    new BigDecimal("25.00"), new BigDecimal("50.00"));

            assertEquals(new BigDecimal("50.00"), sixArgs.getShareInGroup());
            assertEquals(0, BigDecimal.ZERO.compareTo(sixArgs.getPreviousTotalAmount()));
            assertEquals(0, BigDecimal.ZERO.compareTo(sixArgs.getDifferenceAmount()));
            assertEquals(0, BigDecimal.ZERO.compareTo(sixArgs.getPercentageChange()));
            assertFalse(sixArgs.isNew());

            CategoryShareResponseDTO tenArgs = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE",
                    new BigDecimal("500000.00"), new BigDecimal("25.00"), new BigDecimal("50.00"),
                    new BigDecimal("400000.00"), new BigDecimal("100000.00"), new BigDecimal("25.00"), true);

            assertEquals(new BigDecimal("50.00"), tenArgs.getShareInGroup());
            assertEquals(new BigDecimal("400000.00"), tenArgs.getPreviousTotalAmount());
            assertEquals(new BigDecimal("100000.00"), tenArgs.getDifferenceAmount());
            assertEquals(new BigDecimal("25.00"), tenArgs.getPercentageChange());
            assertTrue(tenArgs.isNew());
        }

        @Test
        @DisplayName("Chuẩn hóa null về ZERO và bảo đảm bất biến thang số thập phân trong equals/hashCode")
        void shouldDefaultNullExtendedFieldsAndPreserveScaleInvariance() {
            UUID id = UUID.randomUUID();
            CategoryShareResponseDTO defaulted = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"),
                    null, null, null, null, false);
            assertEquals(BigDecimal.ZERO, defaulted.getShareInGroup());
            assertEquals(BigDecimal.ZERO, defaulted.getPreviousTotalAmount());
            assertEquals(BigDecimal.ZERO, defaulted.getDifferenceAmount());
            assertEquals(BigDecimal.ZERO, defaulted.getPercentageChange());

            CategoryShareResponseDTO a = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.0"), new BigDecimal("50.0"),
                    new BigDecimal("80"), new BigDecimal("20"), new BigDecimal("25.0"), false);
            CategoryShareResponseDTO b = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("100.00"), new BigDecimal("20.00"), new BigDecimal("50.00"),
                    new BigDecimal("80.00"), new BigDecimal("20.00"), new BigDecimal("25.00"), false);
            CategoryShareResponseDTO c = new CategoryShareResponseDTO(
                    id, "Ăn uống", "ACTIVE", new BigDecimal("100.00"), new BigDecimal("20.00"), new BigDecimal("50.00"),
                    new BigDecimal("80.00"), new BigDecimal("20.00"), new BigDecimal("25.00"), true);

            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, c);
        }

        @Test
        @DisplayName("Chuẩn hóa status không phụ thuộc Locale mặc định (tr-TR)")
        void shouldNormalizeStatusWithLocaleRoot() {
            Locale previous = Locale.getDefault();
            try {
                Locale.setDefault(Locale.forLanguageTag("tr-TR"));
                CategoryShareResponseDTO dto = new CategoryShareResponseDTO(
                        UUID.randomUUID(), "Ăn uống", "active",
                        new BigDecimal("100"), new BigDecimal("20.00"),
                        null, null, null, null, false
                );

                assertEquals("ACTIVE", dto.getStatus());
            } finally {
                Locale.setDefault(previous);
            }
        }
    }
}

