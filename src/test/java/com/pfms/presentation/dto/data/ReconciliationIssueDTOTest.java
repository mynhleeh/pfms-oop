package com.pfms.presentation.dto.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử ReconciliationIssueDTO")
class ReconciliationIssueDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ thông tin chi tiết sự cố đối soát")
        void shouldCreateWithAllFields() {
            ReconciliationIssueDTO dto = new ReconciliationIssueDTO(
                    "ERROR",
                    "BR-FIN-02",
                    "WALLET_BALANCE",
                    "Số dư ghi nhận lệch 50.000,00 VNĐ so với lịch sử dòng tiền",
                    "wallet-001",
                    "Thực hiện kiểm kê điều chỉnh số dư thực tế");

            assertEquals("ERROR", dto.getSeverity());
            assertEquals("BR-FIN-02", dto.getRuleCode());
            assertEquals("WALLET_BALANCE", dto.getIssueType());
            assertEquals("Số dư ghi nhận lệch 50.000,00 VNĐ so với lịch sử dòng tiền", dto.getMessage());
            assertEquals("wallet-001", dto.getAffectedEntityId());
            assertEquals("Thực hiện kiểm kê điều chỉnh số dư thực tế", dto.getRecommendation());
            assertTrue(dto.isError());
            assertFalse(dto.isWarning());
            assertFalse(dto.isInfo());
        }

        @Test
        @DisplayName("Khởi tạo thành công với constructor rút gọn không có affectedEntityId")
        void shouldCreateWithConvenienceConstructor() {
            ReconciliationIssueDTO dto = new ReconciliationIssueDTO(
                    "WARNING",
                    "BR-FIN-03",
                    "TRANSFER_BALANCE",
                    "Phát hiện chênh lệch phí chuyển tiền",
                    "Kiểm tra lại phí giao dịch liên kết");

            assertEquals("WARNING", dto.getSeverity());
            assertEquals("BR-FIN-03", dto.getRuleCode());
            assertEquals("TRANSFER_BALANCE", dto.getIssueType());
            assertEquals("Phát hiện chênh lệch phí chuyển tiền", dto.getMessage());
            assertEquals("", dto.getAffectedEntityId());
            assertEquals("Kiểm tra lại phí giao dịch liên kết", dto.getRecommendation());
            assertFalse(dto.isError());
            assertTrue(dto.isWarning());
            assertFalse(dto.isInfo());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Tự động chuẩn hóa severity thành INFO khi nhận giá trị null hoặc rỗng")
        void shouldDefaultSeverityToInfo(String blankSeverity) {
            ReconciliationIssueDTO dto = new ReconciliationIssueDTO(
                    blankSeverity, "BR-FIN-01", "FORMAT", "Lỗi định dạng", "id-1", "Sửa định dạng");

            assertEquals("INFO", dto.getSeverity());
            assertTrue(dto.isInfo());
            assertFalse(dto.isError());
            assertFalse(dto.isWarning());
        }

        @Test
        @DisplayName("Tự động viết hoa severity và cắt khoảng trắng hai đầu")
        void shouldUppercaseAndTrimSeverity() {
            ReconciliationIssueDTO dto = new ReconciliationIssueDTO(
                    "  warning  ", "BR-FIN-04", "CATEGORY", "Danh mục mồ côi", "cat-1", "Gán nhóm cha");

            assertEquals("WARNING", dto.getSeverity());
            assertTrue(dto.isWarning());
        }

        @Test
        @DisplayName("Tự động thay thế null bằng chuỗi rỗng và cắt khoảng trắng hai đầu cho các trường chuỗi")
        void shouldNormalizeNullFieldsToEmptyString() {
            ReconciliationIssueDTO dto = new ReconciliationIssueDTO(
                    "INFO", null, null, null, null, null);

            assertEquals("INFO", dto.getSeverity());
            assertEquals("", dto.getRuleCode());
            assertEquals("", dto.getIssueType());
            assertEquals("", dto.getMessage());
            assertEquals("", dto.getAffectedEntityId());
            assertEquals("", dto.getRecommendation());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Hợp đồng Nhận diện (Identity Contract)")
    class IdentityTests {

        @Test
        @DisplayName("equals và hashCode hoạt động nhất quán khi các trường giống nhau")
        void shouldRespectEqualsAndHashCode() {
            ReconciliationIssueDTO dto1 = new ReconciliationIssueDTO(
                    "ERROR", "BR-FIN-02", "WALLET_BALANCE", "Lệch số dư", "w-1", "Kiểm kê");
            ReconciliationIssueDTO dto2 = new ReconciliationIssueDTO(
                    "ERROR", "BR-FIN-02", "WALLET_BALANCE", "Lệch số dư", "w-1", "Kiểm kê");
            ReconciliationIssueDTO dto3 = new ReconciliationIssueDTO(
                    "WARNING", "BR-FIN-02", "WALLET_BALANCE", "Lệch số dư", "w-1", "Kiểm kê");

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dto3);
            assertNotEquals(null, dto1);
            assertNotEquals("Different Type", dto1);
        }

        @Test
        @DisplayName("toString chứa đầy đủ các thuộc tính của DTO")
        void shouldContainFieldsInToString() {
            ReconciliationIssueDTO dto = new ReconciliationIssueDTO(
                    "ERROR", "BR-FIN-02", "WALLET_BALANCE", "Lệch số dư", "w-1", "Kiểm kê");

            String str = dto.toString();
            assertTrue(str.contains("severity='ERROR'"));
            assertTrue(str.contains("ruleCode='BR-FIN-02'"));
            assertTrue(str.contains("issueType='WALLET_BALANCE'"));
            assertTrue(str.contains("message='Lệch số dư'"));
            assertTrue(str.contains("affectedEntityId='w-1'"));
            assertTrue(str.contains("recommendation='Kiểm kê'"));
        }
    }
}

