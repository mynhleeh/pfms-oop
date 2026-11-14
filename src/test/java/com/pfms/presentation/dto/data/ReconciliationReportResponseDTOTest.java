package com.pfms.presentation.dto.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử ReconciliationReportResponseDTO")
class ReconciliationReportResponseDTOTest {

    @Nested
    @DisplayName("Khởi tạo và Đóng gói dữ liệu hợp lệ")
    class ValidCreationTests {

        @Test
        @DisplayName("Khởi tạo thành công với đầy đủ 4 cờ đối soát, số lượng bản ghi và danh sách sự cố")
        void shouldCreateWithAllFields() {
            ReconciliationIssueResponseDTO err = new ReconciliationIssueResponseDTO(
                    "ERROR", "BR-FIN-02", "WALLET_BALANCE", "Lệch số dư", "w-1", "Kiểm kê");
            ReconciliationIssueResponseDTO warn = new ReconciliationIssueResponseDTO(
                    "WARNING", "BR-FIN-03", "TRANSFER_BALANCE", "Cảnh báo", "Kiểm tra");

            ReconciliationReportResponseDTO dto = new ReconciliationReportResponseDTO(
                    false, true, true, true,
                    5, 120, 25, 4,
                    Arrays.asList(err, warn));

            assertFalse(dto.isWalletBalanceConsistent());
            assertTrue(dto.isTransferConsistent());
            assertTrue(dto.isCategoryHierarchyConsistent());
            assertTrue(dto.isFeeConsistent());

            assertEquals(5, dto.getScannedWallets());
            assertEquals(120, dto.getScannedTransactions());
            assertEquals(25, dto.getScannedCategories());
            assertEquals(4, dto.getScannedAdjustments());
            assertEquals(154, dto.getTotalScannedRecords());

            assertEquals(2, dto.getTotalIssues());
            assertEquals(1, dto.getErrorCount());
            assertEquals(1, dto.getWarningCount());
            assertEquals(0, dto.getInfoCount());
            assertFalse(dto.isFullyConsistent());
        }

        @Test
        @DisplayName("Phương thức xuất xưởng perfect tạo báo cáo nhất quán hoàn hảo 100%")
        void shouldCreatePerfectReport() {
            ReconciliationReportResponseDTO dto = ReconciliationReportResponseDTO.perfect(3, 50, 15, 2);

            assertTrue(dto.isWalletBalanceConsistent());
            assertTrue(dto.isTransferConsistent());
            assertTrue(dto.isCategoryHierarchyConsistent());
            assertTrue(dto.isFeeConsistent());
            assertTrue(dto.isFullyConsistent());

            assertEquals(3, dto.getScannedWallets());
            assertEquals(50, dto.getScannedTransactions());
            assertEquals(15, dto.getScannedCategories());
            assertEquals(2, dto.getScannedAdjustments());
            assertEquals(70, dto.getTotalScannedRecords());

            assertEquals(0, dto.getTotalIssues());
            assertEquals(0, dto.getErrorCount());
            assertEquals(0, dto.getWarningCount());
            assertEquals(0, dto.getInfoCount());
            assertTrue(dto.getIssues().isEmpty());
        }

        @Test
        @DisplayName("Tự động chuẩn hóa số lượng âm thành 0 và null issues thành danh sách rỗng")
        void shouldNormalizeNegativeCountsAndNullIssues() {
            ReconciliationReportResponseDTO dto = new ReconciliationReportResponseDTO(
                    true, true, true, true,
                    -5, -10, -2, -1,
                    null);

            assertEquals(0, dto.getScannedWallets());
            assertEquals(0, dto.getScannedTransactions());
            assertEquals(0, dto.getScannedCategories());
            assertEquals(0, dto.getScannedAdjustments());
            assertEquals(0, dto.getTotalScannedRecords());
            assertNotNull(dto.getIssues());
            assertTrue(dto.getIssues().isEmpty());
            assertTrue(dto.isFullyConsistent());
        }

        @Test
        @DisplayName("Bảo đảm danh sách issues là bất biến không thể sửa đổi từ bên ngoài")
        void shouldEnforceImmutabilityOfIssuesList() {
            List<ReconciliationIssueResponseDTO> mutableList = new ArrayList<>();
            mutableList.add(new ReconciliationIssueResponseDTO("INFO", "BR-FIN-01", "TYPE", "msg", "rec"));

            ReconciliationReportResponseDTO dto = new ReconciliationReportResponseDTO(
                    true, true, true, true,
                    1, 1, 1, 1,
                    mutableList);

            assertEquals(1, dto.getIssues().size());
            assertThrows(UnsupportedOperationException.class, () -> dto.getIssues().add(
                    new ReconciliationIssueResponseDTO("ERROR", "BR-FIN-02", "TYPE", "msg2", "rec2")));
        }
    }

    @Nested
    @DisplayName("Kiểm thử Logic Trạng thái Toàn vẹn (isFullyConsistent)")
    class ConsistencyLogicTests {

        @Test
        @DisplayName("isFullyConsistent trả về false khi có 1 cờ sai mặc dù không có issue ERROR")
        void shouldReturnFalseWhenAnyFlagIsFalse() {
            ReconciliationReportResponseDTO dto1 = new ReconciliationReportResponseDTO(
                    false, true, true, true, 1, 1, 1, 1, Collections.emptyList());
            ReconciliationReportResponseDTO dto2 = new ReconciliationReportResponseDTO(
                    true, false, true, true, 1, 1, 1, 1, Collections.emptyList());
            ReconciliationReportResponseDTO dto3 = new ReconciliationReportResponseDTO(
                    true, true, false, true, 1, 1, 1, 1, Collections.emptyList());
            ReconciliationReportResponseDTO dto4 = new ReconciliationReportResponseDTO(
                    true, true, true, false, 1, 1, 1, 1, Collections.emptyList());

            assertFalse(dto1.isFullyConsistent());
            assertFalse(dto2.isFullyConsistent());
            assertFalse(dto3.isFullyConsistent());
            assertFalse(dto4.isFullyConsistent());
        }

        @Test
        @DisplayName("isFullyConsistent trả về false khi cả 4 cờ true nhưng danh sách có issue mức ERROR")
        void shouldReturnFalseWhenAllFlagsTrueButHasErrorIssue() {
            ReconciliationIssueResponseDTO err = new ReconciliationIssueResponseDTO(
                    "ERROR", "BR-FIN-02", "WALLET", "Lỗi", "rec");
            ReconciliationReportResponseDTO dto = new ReconciliationReportResponseDTO(
                    true, true, true, true, 1, 1, 1, 1, Collections.singletonList(err));

            assertFalse(dto.isFullyConsistent());
        }

        @Test
        @DisplayName("isFullyConsistent trả về true khi cả 4 cờ true và chỉ có issue mức WARNING hoặc INFO")
        void shouldReturnTrueWhenAllFlagsTrueAndOnlyWarningOrInfoIssues() {
            ReconciliationIssueResponseDTO warn = new ReconciliationIssueResponseDTO(
                    "WARNING", "BR-FIN-03", "TRANSFER", "Cảnh báo", "rec");
            ReconciliationIssueResponseDTO info = new ReconciliationIssueResponseDTO(
                    "INFO", "BR-FIN-01", "GENERAL", "Thông tin", "rec");
            ReconciliationReportResponseDTO dto = new ReconciliationReportResponseDTO(
                    true, true, true, true, 1, 1, 1, 1, Arrays.asList(warn, info));

            assertTrue(dto.isFullyConsistent());
            assertEquals(1, dto.getWarningCount());
            assertEquals(1, dto.getInfoCount());
            assertEquals(0, dto.getErrorCount());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Hợp đồng Nhận diện (Identity Contract)")
    class IdentityTests {

        @Test
        @DisplayName("equals và hashCode hoạt động nhất quán")
        void shouldRespectEqualsAndHashCode() {
            ReconciliationReportResponseDTO dto1 = ReconciliationReportResponseDTO.perfect(2, 10, 5, 1);
            ReconciliationReportResponseDTO dto2 = ReconciliationReportResponseDTO.perfect(2, 10, 5, 1);
            ReconciliationReportResponseDTO dto3 = ReconciliationReportResponseDTO.perfect(3, 10, 5, 1);

            assertEquals(dto1, dto1);
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dto3);
            assertNotEquals(null, dto1);
            assertNotEquals("Different Type", dto1);
        }

        @Test
        @DisplayName("toString thể hiện thông tin các cờ và tổng số sự cố")
        void shouldContainFieldsInToString() {
            ReconciliationReportResponseDTO dto = ReconciliationReportResponseDTO.perfect(2, 10, 5, 1);
            String str = dto.toString();

            assertTrue(str.contains("ReconciliationReportResponseDTO"));
            assertTrue(str.contains("walletBalanceConsistent=true"));
            assertTrue(str.contains("transferConsistent=true"));
            assertTrue(str.contains("categoryHierarchyConsistent=true"));
            assertTrue(str.contains("feeConsistent=true"));
            assertTrue(str.contains("scannedWallets=2"));
            assertTrue(str.contains("fullyConsistent=true"));
        }
    }
}

