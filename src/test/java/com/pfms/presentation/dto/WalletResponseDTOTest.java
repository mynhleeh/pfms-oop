package com.pfms.presentation.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử WalletResponseDTO")
class WalletResponseDTOTest {

        private final UUID sampleId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        private final LocalDateTime sampleTime = LocalDateTime.of(2026, 11, 1, 8, 30, 0);

        @Nested
        @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
        class ValidCreationTests {

                @Test
                @DisplayName("Khởi tạo thành công với đầy đủ thông tin chi tiết ví")
                void shouldCreateWithAllFields() {
                        BigDecimal balance = new BigDecimal("15000000.00");
                        WalletResponseDTO dto = new WalletResponseDTO(
                                        sampleId, "Ví Tiền Mặt", balance, "VND", true, "ACTIVE",
                                        "Ví chi tiêu hàng ngày", sampleTime);

                        assertEquals(sampleId, dto.getId());
                        assertEquals(sampleId, dto.getWalletId());
                        assertEquals("Ví Tiền Mặt", dto.getName());
                        assertEquals(balance, dto.getBalance());
                        assertEquals("VND", dto.getCurrency());
                        assertTrue(dto.isDefaultWallet());
                        assertTrue(dto.isDefault());
                        assertEquals("ACTIVE", dto.getStatus());
                        assertEquals("Ví chi tiêu hàng ngày", dto.getNote());
                        assertEquals(sampleTime, dto.getCreatedAt());
                }

                @Test
                @DisplayName("Cung cấp getWalletId như phương thức alias song song của getId")
                void shouldProvideWalletIdAsAliasOfId() {
                        WalletResponseDTO dto = new WalletResponseDTO(
                                        sampleId, "Ví Tiền Mặt", BigDecimal.ZERO, "VND", false, "ACTIVE", "",
                                        sampleTime);

                        assertEquals(sampleId, dto.getWalletId());
                        assertEquals(dto.getId(), dto.getWalletId());
                }

                @Test
                @DisplayName("Tự động chuẩn hóa currency, status, note và createdAt khi nhận giá trị null")
                void shouldNormalizeNullFieldsWithDefaults() {
                        BigDecimal balance = new BigDecimal("500000.00");
                        WalletResponseDTO dto = new WalletResponseDTO(
                                        sampleId, "   Ví Phụ   ", balance, null, false, null, null, null);

                        assertEquals("Ví Phụ", dto.getName());
                        assertEquals("VND", dto.getCurrency());
                        assertEquals("ACTIVE", dto.getStatus());
                        assertEquals("", dto.getNote());
                        assertNotNull(dto.getCreatedAt());
                        assertFalse(dto.isDefaultWallet());
                }
        }

        @Nested
        @DisplayName("Kiểm soát Tiền điều kiện và Dữ liệu biên không hợp lệ")
        class InvalidInputTests {

                @Test
                @DisplayName("Ném NullPointerException khi mã ví id bị null")
                void shouldThrowExceptionWhenIdIsNull() {
                        NullPointerException ex = assertThrows(
                                        NullPointerException.class,
                                        () -> new WalletResponseDTO(null, "Ví", BigDecimal.ZERO, "VND", false, "ACTIVE",
                                                        "", sampleTime));
                        assertTrue(ex.getMessage().contains("Mã ví"));
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = { "   ", "\t", "\n" })
                @DisplayName("Ném IllegalArgumentException khi tên ví null hoặc chỉ chứa khoảng trắng")
                void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
                        IllegalArgumentException ex = assertThrows(
                                        IllegalArgumentException.class,
                                        () -> new WalletResponseDTO(sampleId, invalidName, BigDecimal.ZERO, "VND",
                                                        false, "ACTIVE", "", sampleTime));
                        assertTrue(ex.getMessage().contains("Tên ví"));
                }

                @Test
                @DisplayName("Ném NullPointerException khi số dư balance bị null")
                void shouldThrowExceptionWhenBalanceIsNull() {
                        NullPointerException ex = assertThrows(
                                        NullPointerException.class,
                                        () -> new WalletResponseDTO(sampleId, "Ví", null, "VND", false, "ACTIVE", "",
                                                        sampleTime));
                        assertTrue(ex.getMessage().contains("Số dư"));
                }
        }

        @Nested
        @DisplayName("Hợp đồng Bình đẳng và Bất biến (Equality & Immutability)")
        class EqualityAndContractTests {

                @Test
                @DisplayName("Hai DTO cùng giá trị thuộc tính phải bình đẳng và cùng hashCode")
                void shouldRespectEqualsAndHashCodeContract() {
                        UUID otherId = UUID.fromString("00000000-0000-0000-0000-000000000002");
                        BigDecimal balance = new BigDecimal("2000000.00");

                        WalletResponseDTO dto1 = new WalletResponseDTO(
                                        sampleId, "Ví MB", balance, "VND", false, "ACTIVE", "Note", sampleTime);
                        WalletResponseDTO dto2 = new WalletResponseDTO(
                                        sampleId, "Ví MB", balance, "VND", false, "ACTIVE", "Note", sampleTime);
                        WalletResponseDTO dtoDiffId = new WalletResponseDTO(
                                        otherId, "Ví MB", balance, "VND", false, "ACTIVE", "Note", sampleTime);
                        WalletResponseDTO dtoDiffName = new WalletResponseDTO(
                                        sampleId, "Ví VP", balance, "VND", false, "ACTIVE", "Note", sampleTime);
                        WalletResponseDTO dtoDiffBalance = new WalletResponseDTO(
                                        sampleId, "Ví MB", new BigDecimal("3000000.00"), "VND", false, "ACTIVE", "Note",
                                        sampleTime);
                        WalletResponseDTO dtoDiffDefault = new WalletResponseDTO(
                                        sampleId, "Ví MB", balance, "VND", true, "ACTIVE", "Note", sampleTime);
                        WalletResponseDTO dtoDiffStatus = new WalletResponseDTO(
                                        sampleId, "Ví MB", balance, "VND", false, "ARCHIVED", "Note", sampleTime);

                        assertEquals(dto1, dto2);
                        assertEquals(dto1.hashCode(), dto2.hashCode());
                        assertNotEquals(dto1, dtoDiffId);
                        assertNotEquals(dto1, dtoDiffName);
                        assertNotEquals(dto1, dtoDiffBalance);
                        assertNotEquals(dto1, dtoDiffDefault);
                        assertNotEquals(dto1, dtoDiffStatus);
                        assertNotEquals(null, dto1);
                        assertNotEquals(dto1, "NotAWalletResponseDTO");
                }

                @Test
                @DisplayName("Phương thức toString cung cấp thông tin chi tiết đầy đủ")
                void shouldProvideMeaningfulToString() {
                        WalletResponseDTO dto = new WalletResponseDTO(
                                        sampleId, "Ví MB", new BigDecimal("2000000.00"), "VND", true, "ACTIVE", "Note",
                                        sampleTime);
                        String text = dto.toString();

                        assertTrue(text.contains("WalletResponseDTO"));
                        assertTrue(text.contains(sampleId.toString()));
                        assertTrue(text.contains("Ví MB"));
                        assertTrue(text.contains("2000000.00"));
                        assertTrue(text.contains("VND"));
                        assertTrue(text.contains("ACTIVE"));
                }
        }
}
