package com.pfms.domain.model.wallet;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BalanceAdjustmentRecord Domain Model Tests")
class BalanceAdjustmentRecordTest {

    private UUID recordId;
    private UUID walletId;
    private TransactionDate validDate;

    @BeforeEach
    void setUp() {
        recordId = UUID.randomUUID();
        walletId = UUID.randomUUID();
        validDate = TransactionDate.now();
    }

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo thành công bản ghi điều chỉnh số dư thặng dư (actual > previous)")
        void testConstructor_SurplusAdjustment_Success() {
            Money previous = Money.of(100_000);
            Money actual = Money.of(150_000);
            String reason = "Phát hiện tiền lẻ kiểm kê thừa";

            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId, walletId, previous, actual, validDate, reason);

            assertEquals(recordId, record.getRecordId());
            assertEquals(walletId, record.getWalletId());
            assertEquals(previous, record.getPreviousBalance());
            assertEquals(actual, record.getActualBalance());
            assertEquals(validDate, record.getAdjustedAt());
            assertEquals(reason, record.getReason());
            assertNotNull(record.getCreatedAt());

            // difference = 150_000 - 100_000 = +50_000
            assertEquals(Money.of(50_000), record.getDifference());
            assertEquals(Money.of(50_000), record.getAbsoluteDifference());
            assertTrue(record.isSurplus());
        }

        @Test
        @DisplayName("Khởi tạo với constructor 7 tham số giữ nguyên thời điểm tạo lịch sử")
        void testConstructor_PreservesHistoricalCreatedAt() {
            Money previous = Money.of(100_000);
            Money actual = Money.of(150_000);
            LocalDateTime historicalTime = LocalDateTime.of(2024, 5, 20, 14, 15, 0);

            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId, walletId, previous, actual, validDate, "Khôi phục kiểm kê", historicalTime);

            assertEquals(recordId, record.getRecordId());
            assertEquals(walletId, record.getWalletId());
            assertEquals(previous, record.getPreviousBalance());
            assertEquals(actual, record.getActualBalance());
            assertEquals(Money.of(50_000), record.getDifference());
            assertEquals(validDate, record.getAdjustedAt());
            assertEquals("Khôi phục kiểm kê", record.getReason());
            assertEquals(historicalTime, record.getCreatedAt());
        }

        @Test
        @DisplayName("Khởi tạo thành công bản ghi điều chỉnh số dư thâm hụt (actual < previous)")
        void testConstructor_DeficitAdjustment_Success() {
            Money previous = Money.of(200_000);
            Money actual = Money.of(120_000);
            String reason = "Chênh lệch tiền hao hụt";

            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId, walletId, previous, actual, validDate, reason);

            assertEquals(Money.of(-80_000), record.getDifference());
            assertEquals(Money.of(80_000), record.getAbsoluteDifference());
            assertFalse(record.isSurplus());
        }

        @Test
        @DisplayName("Khởi tạo với lý do null được chuẩn hóa thành chuỗi rỗng")
        void testConstructor_NullReason_NormalizedToEmpty() {
            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(120_000), validDate, null);

            assertEquals("", record.getReason());
        }

        @Test
        @DisplayName("Khởi tạo với lý do có khoảng trắng hai đầu được cắt tỉa")
        void testConstructor_ReasonTrimmed() {
            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(120_000), validDate, "   Kiểm kê định kỳ   ");

            assertEquals("Kiểm kê định kỳ", record.getReason());
        }

        @Test
        @DisplayName("Khởi tạo thành công khi actualBalance bằng 0 (rút sạch số dư)")
        void testConstructor_ActualBalanceZero_Success() {
            Money previous = Money.of(100_000);
            Money actual = Money.ZERO;

            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId, walletId, previous, actual, validDate, "Rút sạch số dư ví về 0");

            assertEquals(Money.ZERO, record.getActualBalance());
            assertEquals(Money.of(-100_000), record.getDifference());
            assertEquals(Money.of(100_000), record.getAbsoluteDifference());
            assertFalse(record.isSurplus());
        }

        @Test
        @DisplayName("Khởi tạo thành công với lý do có độ dài đúng 255 ký tự")
        void testConstructor_ReasonExactly255Chars_Success() {
            String maxReason = "a".repeat(255);
            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(120_000), validDate, maxReason);

            assertEquals(maxReason, record.getReason());
        }
    }

    @Nested
    @DisplayName("Null and Invalid Input Tests")
    class NullAndInvalidInputTests {

        @Test
        @DisplayName("Khởi tạo với recordId null ném NullPointerException")
        void testConstructor_NullRecordId_ThrowsException() {
            assertThrows(NullPointerException.class, () -> new BalanceAdjustmentRecord(
                    null, walletId, Money.of(100_000), Money.of(150_000), validDate, "Reason"));
        }

        @Test
        @DisplayName("Khởi tạo với walletId null ném NullPointerException")
        void testConstructor_NullWalletId_ThrowsException() {
            assertThrows(NullPointerException.class, () -> new BalanceAdjustmentRecord(
                    recordId, (UUID) null, Money.of(100_000), Money.of(150_000), validDate, "Reason"));
        }

        @Test
        @DisplayName("Khởi tạo với walletId rỗng hoặc toàn khoảng trắng ném IllegalArgumentException")
        void testConstructor_BlankWalletId_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> new BalanceAdjustmentRecord(
                    recordId, "   ", Money.of(100_000), Money.of(150_000), validDate, "Reason"));
        }

        @Test
        @DisplayName("Khởi tạo với previousBalance null ném NullPointerException")
        void testConstructor_NullPreviousBalance_ThrowsException() {
            assertThrows(NullPointerException.class, () -> new BalanceAdjustmentRecord(
                    recordId, walletId, null, Money.of(150_000), validDate, "Reason"));
        }

        @Test
        @DisplayName("Khởi tạo với previousBalance âm ném BusinessException ERR_WAL_001")
        void testConstructor_NegativePreviousBalance_ThrowsBusinessException() {
            BusinessException ex = assertThrows(BusinessException.class, () -> new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(-50_000), Money.of(100_000), validDate, "Reason"));
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Khởi tạo với actualBalance null ném NullPointerException")
        void testConstructor_NullActualBalance_ThrowsException() {
            assertThrows(NullPointerException.class, () -> new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), null, validDate, "Reason"));
        }

        @Test
        @DisplayName("Khởi tạo với actualBalance âm ném BusinessException ERR_WAL_001")
        void testConstructor_NegativeActualBalance_ThrowsBusinessException() {
            BusinessException ex = assertThrows(BusinessException.class, () -> new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(-50_000), validDate, "Reason"));
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Khởi tạo với actualBalance trùng previousBalance (difference = 0) ném BusinessException ERR_FIN_001")
        void testConstructor_ZeroDifference_ThrowsBusinessException() {
            BusinessException ex = assertThrows(BusinessException.class, () -> new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(100_000), validDate, "Reason"));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Khởi tạo với adjustedAt null ném NullPointerException")
        void testConstructor_NullAdjustedAt_ThrowsException() {
            assertThrows(NullPointerException.class, () -> new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(150_000), null, "Reason"));
        }

        @Test
        @DisplayName("Khởi tạo với lý do dài quá 255 ký tự ném IllegalArgumentException")
        void testConstructor_ReasonExceeds255Chars_ThrowsException() {
            String longReason = "a".repeat(256);
            assertThrows(IllegalArgumentException.class, () -> new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(150_000), validDate, longReason));
        }

        @Test
        @DisplayName("Khởi tạo 7 tham số với createdAt null ném NullPointerException")
        void testConstructor_NullCreatedAt_ThrowsException() {
            assertThrows(NullPointerException.class, () -> new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(150_000), validDate, "Reason", null));
        }
    }

    @Nested
    @DisplayName("Robustness and Identity Tests")
    class RobustnessAndIdentityTests {

        @Test
        @DisplayName("Hai bản ghi cùng recordId thì bằng nhau (equals và hashCode)")
        void testEqualsAndHashCode() {
            BalanceAdjustmentRecord record1 = new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(150_000), validDate, "Reason 1");
            BalanceAdjustmentRecord record2 = new BalanceAdjustmentRecord(
                    recordId, UUID.randomUUID().toString(), Money.of(50_000), Money.of(80_000), validDate, "Reason 2");

            assertEquals(record1, record2);
            assertEquals(record1.hashCode(), record2.hashCode());
        }

        @Test
        @DisplayName("Hai bản ghi khác recordId thì không bằng nhau")
        void testEquals_DifferentRecordId_NotEqual() {
            BalanceAdjustmentRecord record1 = new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(150_000), validDate, "Reason");
            BalanceAdjustmentRecord record2 = new BalanceAdjustmentRecord(
                    UUID.randomUUID(), walletId, Money.of(100_000), Money.of(150_000), validDate, "Reason");

            assertNotEquals(record1, record2);
        }

        @Test
        @DisplayName("Kiểm tra equals phản xạ và an toàn với null hoặc kiểu dữ liệu khác")
        void testEquals_ReflexiveAndNullSafe() {
            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(150_000), validDate, "Reason");

            assertEquals(record, record);
            assertNotEquals(null, record);
            assertNotEquals("Some String", record);
        }

        @Test
        @DisplayName("Định dạng toString chứa các thông tin nhận diện cốt lõi")
        void testToString() {
            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId, walletId, Money.of(100_000), Money.of(150_000), validDate, "Test reason");
            String str = record.toString();

            assertTrue(str.contains(recordId.toString()));
            assertTrue(str.contains(walletId.toString()));
            assertTrue(str.contains("150.000"));
            assertTrue(str.contains("50.000"));
        }

        @Test
        @DisplayName("reconstitute tái tạo thành công bản ghi kiểm kê lịch sử từ dữ liệu lưu trữ")
        void testReconstitutePreservesPersistedRecord() {
            TransactionDate futureHistoricalDate = TransactionDate.reconstitute(java.time.LocalDate.now().plusDays(1));
            LocalDateTime historicalCreatedAt = LocalDateTime.of(2025, 3, 10, 8, 45);

            BalanceAdjustmentRecord reconstituted = assertDoesNotThrow(() -> BalanceAdjustmentRecord.reconstitute(
                    recordId, walletId, Money.of("100000.00"), Money.of("80000.00"),
                    futureHistoricalDate, "  Đối soát lưu trữ  ", historicalCreatedAt));

            assertEquals(recordId, reconstituted.getRecordId());
            assertEquals(walletId, reconstituted.getWalletId());
            assertEquals(Money.of("100000.00"), reconstituted.getPreviousBalance());
            assertEquals(Money.of("80000.00"), reconstituted.getActualBalance());
            assertEquals(Money.of("-20000.00"), reconstituted.getDifference());
            assertEquals(futureHistoricalDate, reconstituted.getAdjustedAt());
            assertEquals("Đối soát lưu trữ", reconstituted.getReason());
            assertEquals(historicalCreatedAt, reconstituted.getCreatedAt());
        }
    }
}

