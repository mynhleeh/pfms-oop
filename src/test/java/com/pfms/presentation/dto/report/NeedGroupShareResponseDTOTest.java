package com.pfms.presentation.dto.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử NeedGroupShareResponseDTO")
class NeedGroupShareResponseDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 6 tham số")
        void shouldCreateWithAllSixParameters() {
            UUID groupId = UUID.randomUUID();
            UUID catId = UUID.randomUUID();
            CategoryShareResponseDTO catShare = new CategoryShareResponseDTO(
                    catId, "Tiền chợ", "ACTIVE", new BigDecimal("3000000"), new BigDecimal("20.00"));

            NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                    groupId, "Nhu cầu thiết yếu", "ACTIVE", new BigDecimal("5000000"), new BigDecimal("33.33"), List.of(catShare));

            assertEquals(groupId, dto.getNeedGroupId());
            assertEquals("Nhu cầu thiết yếu", dto.getName());
            assertEquals("ACTIVE", dto.getStatus());
            assertEquals(new BigDecimal("5000000"), dto.getTotalAmount());
            assertEquals(new BigDecimal("33.33"), dto.getShare());
            assertEquals(1, dto.getCategoryCount());
            assertEquals(1, dto.getCategories().size());
            assertTrue(dto.isActive());
            assertFalse(dto.isDeleted());
        }

        @Test
        @DisplayName("Khởi tạo với 5 tham số sẽ mặc định trạng thái ACTIVE")
        void shouldDefaultStatusToActiveWithFiveParameters() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                    groupId, "Tích lũy & Đầu tư", new BigDecimal("2000000"), new BigDecimal("15.00"), List.of());

            assertEquals(groupId, dto.getNeedGroupId());
            assertEquals("Tích lũy & Đầu tư", dto.getName());
            assertEquals("ACTIVE", dto.getStatus());
            assertTrue(dto.isActive());
            assertEquals(0, dto.getCategoryCount());
        }

        @Test
        @DisplayName("Tự động sao chép phòng vệ danh sách categories và bảo đảm tính bất biến")
        void shouldEnforceDefensiveCopyingAndImmutability() {
            UUID groupId = UUID.randomUUID();
            List<CategoryShareResponseDTO> mutableList = new ArrayList<>();
            mutableList.add(new CategoryShareResponseDTO(UUID.randomUUID(), "Danh mục 1", BigDecimal.ZERO, BigDecimal.ZERO));

            NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                    groupId, "Nhóm 1", BigDecimal.ZERO, BigDecimal.ZERO, mutableList);

            mutableList.add(new CategoryShareResponseDTO(UUID.randomUUID(), "Danh mục 2", BigDecimal.ZERO, BigDecimal.ZERO));
            assertEquals(1, dto.getCategoryCount());

            List<CategoryShareResponseDTO> returnedList = dto.getCategories();
            assertThrows(UnsupportedOperationException.class,
                    () -> returnedList.add(new CategoryShareResponseDTO(UUID.randomUUID(), "Danh mục 3", BigDecimal.ZERO, BigDecimal.ZERO)));
        }

        @Test
        @DisplayName("Khi truyền danh sách categories null thì mặc định danh sách rỗng")
        void shouldHandleNullCategoriesAsEmpty() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                    groupId, "Nhóm rỗng", BigDecimal.ZERO, BigDecimal.ZERO, null);

            assertNotNull(dto.getCategories());
            assertTrue(dto.getCategories().isEmpty());
            assertEquals(0, dto.getCategoryCount());
        }

        @Test
        @DisplayName("Nhận diện chính xác trạng thái xóa mềm DELETED")
        void shouldIdentifyDeletedStatus() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                    groupId, "Nhóm cũ (đã xóa)", "DELETED", BigDecimal.ZERO, BigDecimal.ZERO, List.of());

            assertEquals("DELETED", dto.getStatus());
            assertFalse(dto.isActive());
            assertTrue(dto.isDeleted());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa chuỗi và gán giá trị mặc định khi null")
        void shouldNormalizeStringsAndDefaultNullValues() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                    groupId, "   Chi phí sinh hoạt   ", "   active   ", null, null, null);

            assertEquals("Chi phí sinh hoạt", dto.getName());
            assertEquals("ACTIVE", dto.getStatus());
            assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
            assertEquals(BigDecimal.ZERO, dto.getShare());
        }

        @Test
        @DisplayName("Khi tên nhóm là null thì mặc định chuỗi rỗng")
        void shouldDefaultNullNameToEmpty() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                    groupId, null, BigDecimal.ZERO, BigDecimal.ZERO, List.of());

            assertEquals("", dto.getName());
        }
    }

    @Nested
    @DisplayName("Kiểm tra Ràng buộc Null (Null Safety)")
    class NullSafetyTests {

        @Test
        @DisplayName("Ném NullPointerException khi needGroupId là null")
        void shouldThrowExceptionWhenNeedGroupIdIsNull() {
            assertThrows(NullPointerException.class,
                    () -> new NeedGroupShareResponseDTO(null, "Nhóm", "ACTIVE", BigDecimal.ZERO, BigDecimal.ZERO, List.of()));
            assertThrows(NullPointerException.class,
                    () -> new NeedGroupShareResponseDTO(null, "Nhóm", BigDecimal.ZERO, BigDecimal.ZERO, List.of()));
        }
    }

    @Nested
    @DisplayName("Hợp đồng So sánh và Định danh (Identity Contract)")
    class IdentityContractTests {

        @Test
        @DisplayName("Hai đối tượng có cùng thuộc tính phải bằng nhau và có cùng hashCode")
        void shouldBeEqualWhenSameProperties() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO dto1 = new NeedGroupShareResponseDTO(
                    groupId, "Nhóm", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"), List.of());
            NeedGroupShareResponseDTO dto2 = new NeedGroupShareResponseDTO(
                    groupId, "Nhóm", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"), List.of());

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Bảo đảm tính bất biến về thang số thập phân (Scale Invariance) trong equals và hashCode")
        void shouldBeEqualRegardlessOfBigDecimalScale() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO dto1 = new NeedGroupShareResponseDTO(
                    groupId, "Nhóm", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.0"), List.of());
            NeedGroupShareResponseDTO dto2 = new NeedGroupShareResponseDTO(
                    groupId, "Nhóm", "ACTIVE", new BigDecimal("100.00"), new BigDecimal("20.000"), List.of());

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng khác thuộc tính thì không bằng nhau")
        void shouldNotBeEqualWhenDifferentProperties() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();

            NeedGroupShareResponseDTO base = new NeedGroupShareResponseDTO(
                    id1, "Nhóm", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"), List.of());
            NeedGroupShareResponseDTO diffId = new NeedGroupShareResponseDTO(
                    id2, "Nhóm", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"), List.of());
            NeedGroupShareResponseDTO diffName = new NeedGroupShareResponseDTO(
                    id1, "Nhóm khác", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.00"), List.of());

            assertNotEquals(base, diffId);
            assertNotEquals(base, diffName);
            assertNotEquals(base, null);
            assertNotEquals(base, "NeedGroup");
        }

        @Test
        @DisplayName("equals với chính nó trả về true")
        void shouldBeEqualToItself() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                    groupId, "Nhóm", BigDecimal.ZERO, BigDecimal.ZERO, List.of());
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("toString chứa đầy đủ thông tin mã nhóm, tên và số lượng danh mục con")
        void shouldContainFieldsInToString() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                    groupId, "Nhu cầu thiết yếu", new BigDecimal("100"), new BigDecimal("20.00"), List.of());
            String str = dto.toString();

            assertTrue(str.contains(groupId.toString()));
            assertTrue(str.contains("Nhu cầu thiết yếu"));
            assertTrue(str.contains("categoryCount=0"));
            assertTrue(str.contains("NeedGroupShareResponseDTO"));
        }
    }

    @Nested
    @DisplayName("Trường So sánh Kỳ liền kề")
    class ComparisonFieldsTests {

        @Test
        @DisplayName("Khởi tạo với constructor 10 tham số và chuẩn hóa null an toàn")
        void shouldCreateWithComparisonFieldsAndDefaultNulls() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO full = new NeedGroupShareResponseDTO(
                    groupId, "Thiết yếu", "ACTIVE",
                    new BigDecimal("1000000"), new BigDecimal("60.00"),
                    new BigDecimal("800000"), new BigDecimal("200000"), new BigDecimal("25.00"), true,
                    List.of());

            assertEquals(new BigDecimal("800000"), full.getPreviousTotalAmount());
            assertEquals(new BigDecimal("200000"), full.getDifferenceAmount());
            assertEquals(new BigDecimal("25.00"), full.getPercentageChange());
            assertTrue(full.isNew());

            NeedGroupShareResponseDTO defaulted = new NeedGroupShareResponseDTO(
                    groupId, "Thiết yếu", "ACTIVE",
                    new BigDecimal("1000000"), new BigDecimal("60.00"),
                    null, null, null, false, List.of());
            assertEquals(BigDecimal.ZERO, defaulted.getPreviousTotalAmount());
            assertEquals(BigDecimal.ZERO, defaulted.getDifferenceAmount());
            assertEquals(BigDecimal.ZERO, defaulted.getPercentageChange());
            assertFalse(defaulted.isNew());
        }

        @Test
        @DisplayName("Bảo đảm bất biến thang số thập phân trong equals và hashCode cho trường so sánh")
        void shouldPreserveScaleInvarianceForComparisonFields() {
            UUID groupId = UUID.randomUUID();
            NeedGroupShareResponseDTO a = new NeedGroupShareResponseDTO(
                    groupId, "Thiết yếu", "ACTIVE", new BigDecimal("100"), new BigDecimal("20.0"),
                    new BigDecimal("50"), new BigDecimal("50.0"), new BigDecimal("100"), false, List.of());
            NeedGroupShareResponseDTO b = new NeedGroupShareResponseDTO(
                    groupId, "Thiết yếu", "ACTIVE", new BigDecimal("100.00"), new BigDecimal("20.00"),
                    new BigDecimal("50.00"), new BigDecimal("50.00"), new BigDecimal("100.00"), false, List.of());
            NeedGroupShareResponseDTO c = new NeedGroupShareResponseDTO(
                    groupId, "Thiết yếu", "ACTIVE", new BigDecimal("100.00"), new BigDecimal("20.00"),
                    new BigDecimal("50.00"), new BigDecimal("50.00"), new BigDecimal("100.00"), true, List.of());

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
                NeedGroupShareResponseDTO dto = new NeedGroupShareResponseDTO(
                        UUID.randomUUID(), "Thiết yếu", "active",
                        new BigDecimal("1000000"), new BigDecimal("60.00"),
                        null, null, null, false, List.of()
                );

                assertEquals("ACTIVE", dto.getStatus());
            } finally {
                Locale.setDefault(previous);
            }
        }
    }
}

