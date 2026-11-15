package com.pfms.presentation.dto.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử NeedGroupResponseDTO")
class NeedGroupResponseDTOTest {

    private final UUID sampleGroupId = UUID.fromString("00000000-0000-0000-0000-000000000030");
    private final UUID otherGroupId = UUID.fromString("00000000-0000-0000-0000-000000000031");
    private final LocalDateTime sampleTime = LocalDateTime.of(2026, 11, 14, 9, 30, 0);

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 7 tham số bao gồm createdAt")
        void shouldCreateWithAllParameters() {
            NeedGroupResponseDTO dto = new NeedGroupResponseDTO(
                    sampleGroupId,
                    "Sức khỏe",
                    "EXPENSE",
                    "ACTIVE",
                    "heart",
                    "#E91E63",
                    sampleTime
            );

            assertEquals(sampleGroupId, dto.getNeedGroupId());
            assertEquals("Sức khỏe", dto.getName());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals("ACTIVE", dto.getStatus());
            assertEquals("heart", dto.getIcon());
            assertEquals("#E91E63", dto.getColor());
            assertEquals(sampleTime, dto.getCreatedAt());
            assertTrue(dto.isActive());
            assertFalse(dto.isDeleted());
            assertThrows(NoSuchMethodException.class, () -> NeedGroupResponseDTO.class.getMethod("getId"));
        }

        @Test
        @DisplayName("Khởi tạo với constructor 6 tham số tự động gán createdAt không null")
        void shouldCreateWithConvenienceConstructorAndDefaultCreatedAt() {
            NeedGroupResponseDTO dto = new NeedGroupResponseDTO(
                    sampleGroupId,
                    "Thu nhập thụ động",
                    "INCOME",
                    "ACTIVE",
                    "trending-up",
                    "#4CAF50"
            );

            assertEquals(sampleGroupId, dto.getNeedGroupId());
            assertEquals("Thu nhập thụ động", dto.getName());
            assertEquals("INCOME", dto.getTransactionType());
            assertEquals("ACTIVE", dto.getStatus());
            assertEquals("trending-up", dto.getIcon());
            assertEquals("#4CAF50", dto.getColor());
            assertNotNull(dto.getCreatedAt());
            assertTrue(dto.isActive());
            assertFalse(dto.isDeleted());
        }

        @Test
        @DisplayName("Chuẩn hóa các trường chuỗi và nhận diện trạng thái DELETED")
        void shouldNormalizeWhitespaceAndIdentifyDeletedStatus() {
            NeedGroupResponseDTO dto = new NeedGroupResponseDTO(
                    sampleGroupId,
                    "   Giải trí   ",
                    "  expense  ",
                    "  deleted  ",
                    "   film   ",
                    "   #9C27B0   ",
                    null
            );

            assertEquals("Giải trí", dto.getName());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals("DELETED", dto.getStatus());
            assertEquals("film", dto.getIcon());
            assertEquals("#9C27B0", dto.getColor());
            assertNotNull(dto.getCreatedAt());
            assertFalse(dto.isActive());
            assertTrue(dto.isDeleted());
        }

        @Test
        @DisplayName("Mặc định name/icon/color thành rỗng, transactionType EXPENSE và status ACTIVE khi null/trống")
        void shouldDefaultStringsWhenNullOrBlank() {
            NeedGroupResponseDTO dto = new NeedGroupResponseDTO(
                    sampleGroupId,
                    null,
                    "   ",
                    "",
                    null,
                    null,
                    null
            );

            assertEquals("", dto.getName());
            assertEquals("EXPENSE", dto.getTransactionType());
            assertEquals("ACTIVE", dto.getStatus());
            assertEquals("", dto.getIcon());
            assertEquals("", dto.getColor());
            assertNotNull(dto.getCreatedAt());
            assertTrue(dto.isActive());
            assertFalse(dto.isDeleted());
        }
    }

    @Nested
    @DisplayName("Kiểm tra tiền điều kiện bắt buộc")
    class PreconditionTests {

        @Test
        @DisplayName("Ném NullPointerException khi needGroupId là null")
        void shouldThrowExceptionWhenNeedGroupIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new NeedGroupResponseDTO(null, "Tên nhóm", "EXPENSE", "ACTIVE", "", "", sampleTime)
            );

            assertTrue(ex.getMessage().contains("Mã nhóm nhu cầu"));
        }
    }

    @Nested
    @DisplayName("Kiểm tra tính đồng nhất (equals, hashCode, toString)")
    class IdentityTests {

        @Test
        @DisplayName("Hai đối tượng cùng giá trị thuộc tính phải bằng nhau và cùng hashCode (bỏ qua createdAt)")
        void shouldBeEqualWhenAttributesMatchIgnoringCreatedAt() {
            NeedGroupResponseDTO dto1 = new NeedGroupResponseDTO(
                    sampleGroupId, "Sức khỏe", "EXPENSE", "ACTIVE", "heart", "#E91E63", sampleTime);
            NeedGroupResponseDTO dto2 = new NeedGroupResponseDTO(
                    sampleGroupId, "  Sức khỏe  ", "expense", "active", " heart ", " #E91E63 ", sampleTime.plusDays(1));

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Không bằng nhau khi khác bất kỳ thuộc tính nghiệp vụ nào")
        void shouldNotEqualWhenAttributesDiffer() {
            NeedGroupResponseDTO base = new NeedGroupResponseDTO(
                    sampleGroupId, "Sức khỏe", "EXPENSE", "ACTIVE", "heart", "#E91E63", sampleTime);

            assertNotEquals(base, new NeedGroupResponseDTO(otherGroupId, "Sức khỏe", "EXPENSE", "ACTIVE", "heart", "#E91E63", sampleTime));
            assertNotEquals(base, new NeedGroupResponseDTO(sampleGroupId, "Y tế", "EXPENSE", "ACTIVE", "heart", "#E91E63", sampleTime));
            assertNotEquals(base, new NeedGroupResponseDTO(sampleGroupId, "Sức khỏe", "INCOME", "ACTIVE", "heart", "#E91E63", sampleTime));
            assertNotEquals(base, new NeedGroupResponseDTO(sampleGroupId, "Sức khỏe", "EXPENSE", "DELETED", "heart", "#E91E63", sampleTime));
            assertNotEquals(base, new NeedGroupResponseDTO(sampleGroupId, "Sức khỏe", "EXPENSE", "ACTIVE", "star", "#E91E63", sampleTime));
            assertNotEquals(base, new NeedGroupResponseDTO(sampleGroupId, "Sức khỏe", "EXPENSE", "ACTIVE", "heart", "#000000", sampleTime));
            assertNotEquals(base, null);
            assertNotEquals(base, "NotADTO");
        }

        @Test
        @DisplayName("toString chứa đầy đủ tên lớp và thông tin các thuộc tính")
        void shouldProduceInformativeToString() {
            NeedGroupResponseDTO dto = new NeedGroupResponseDTO(
                    sampleGroupId, "Sức khỏe", "EXPENSE", "ACTIVE", "heart", "#E91E63", sampleTime);

            String str = dto.toString();
            assertTrue(str.contains("NeedGroupResponseDTO"));
            assertTrue(str.contains(sampleGroupId.toString()));
            assertTrue(str.contains("Sức khỏe"));
            assertTrue(str.contains("EXPENSE"));
            assertTrue(str.contains("ACTIVE"));
            assertTrue(str.contains("heart"));
            assertTrue(str.contains("#E91E63"));
        }
    }
}

