package com.pfms.domain.model.wallet;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Wallet Domain Entity (Unit 1.2)")
class WalletTest {

    @Nested
    @DisplayName("Group 1: Constructors and Basic Properties")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize wallet with default name, zero balance and ACTIVE status")
        void testDefaultConstructor() {
            Wallet wallet = new Wallet();

            assertNotNull(wallet.getId());
            assertEquals("Ví chính", wallet.getName());
            assertEquals("", wallet.getNote());
            assertFalse(wallet.isDefaultWallet());
            assertEquals(Money.ZERO, wallet.getCurrentBalance());
            assertEquals(EntityStatus.ACTIVE, wallet.getStatus());
            assertNotNull(wallet.getCreatedAt());
        }

        @Test
        @DisplayName("Constructor with name and note should initialize fields properly")
        void testCustomNameAndNoteConstructor() {
            Wallet wallet = new Wallet("Techcombank", "Tài khoản nhận lương");

            assertEquals("Techcombank", wallet.getName());
            assertEquals("Tài khoản nhận lương", wallet.getNote());
            assertFalse(wallet.isDefaultWallet());
            assertEquals(Money.ZERO, wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("Constructor with defaultWallet flag should record wallet status")
        void testDefaultWalletConstructor() {
            Wallet wallet = new Wallet("Tiền mặt", "Ví tiền mặt vật lý", true);

            assertEquals("Tiền mặt", wallet.getName());
            assertTrue(wallet.isDefaultWallet());
        }

        @Test
        @DisplayName("Full constructor should populate all fields and preserve state")
        void testFullConstructor() {
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            Wallet wallet = new Wallet(id, "Vietcombank", "Ví chính", false,
                    now, new ActiveWalletState(), Money.of(500_000));

            assertEquals(id, wallet.getId());
            assertEquals("Vietcombank", wallet.getName());
            assertEquals("Ví chính", wallet.getNote());
            assertFalse(wallet.isDefaultWallet());
            assertEquals(now, wallet.getCreatedAt());
            assertEquals(Money.of(500_000), wallet.getCurrentBalance());
            assertEquals(EntityStatus.ACTIVE, wallet.getStatus());
        }

        @Test
        @DisplayName("Constructor with UUID, name, note, defaultWallet should match Doc 02 specification")
        void testFourParameterConstructor() {
            UUID id = UUID.randomUUID();
            Wallet wallet = new Wallet(id, "MoMo", "Ví điện tử", false);

            assertEquals(id, wallet.getId());
            assertEquals(id, wallet.getWalletId());
            assertEquals("MoMo", wallet.getName());
            assertEquals("Ví điện tử", wallet.getNote());
            assertFalse(wallet.isDefault());
            assertEquals(Money.ZERO, wallet.getCurrentBalance());
            assertEquals(EntityStatus.ACTIVE, wallet.getStatus());
        }
    }

    @Nested
    @DisplayName("Group 2: Name Invariants [BR-NAM-01]")
    class NameInvariantTests {

        @Test
        @DisplayName("Null name should throw BusinessException with ERR_NAM_001")
        void testNullNameThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class, () -> new Wallet((String) null));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @ParameterizedTest(name = "Blank name \"{0}\" should throw BusinessException with ERR_NAM_001")
        @ValueSource(strings = { "", "   ", "\t", "\n" })
        void testBlankNameThrowsException(String blankName) {
            BusinessException ex = assertThrows(BusinessException.class, () -> new Wallet(blankName));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Name exceeding 50 characters should throw BusinessException with ERR_NAM_001")
        void testNameExceeding50CharsThrowsException() {
            String longName = "A".repeat(51);
            BusinessException ex = assertThrows(BusinessException.class, () -> new Wallet(longName));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Name should be trimmed of leading and trailing whitespaces")
        void testNameTrimmed() {
            Wallet wallet = new Wallet("   Momo e-Wallet   ");
            assertEquals("Momo e-Wallet", wallet.getName());
        }

        @Test
        @DisplayName("rename() should update name when valid")
        void testRenameValid() {
            Wallet wallet = new Wallet("Ví tạm");
            wallet.rename("   Ví chi tiêu hàng ngày   ");

            assertEquals("Ví chi tiêu hàng ngày", wallet.getName());
        }

        @Test
        @DisplayName("rename() with invalid name should throw BusinessException with ERR_NAM_001")
        void testRenameInvalidThrowsException() {
            Wallet wallet = new Wallet("Ví hợp lệ");

            assertThrows(BusinessException.class, () -> wallet.rename(""));
            assertThrows(BusinessException.class, () -> wallet.rename(null));
            assertThrows(BusinessException.class, () -> wallet.rename("X".repeat(51)));
            assertEquals("Ví hợp lệ", wallet.getName(), "Name must remain unchanged on failed rename");
        }
    }

    @Nested
    @DisplayName("Group 3: Note Invariants")
    class NoteInvariantTests {

        @Test
        @DisplayName("Null note should be normalized to empty string")
        void testNullNoteNormalized() {
            Wallet wallet = new Wallet("Ví", null);
            assertEquals("", wallet.getNote());
        }

        @Test
        @DisplayName("Note exceeding 255 characters should throw IllegalArgumentException")
        void testNoteExceeding255CharsThrowsException() {
            String longNote = "N".repeat(256);
            assertThrows(IllegalArgumentException.class, () -> new Wallet("Ví", longNote));
        }

        @Test
        @DisplayName("updateNote() should update and trim note")
        void testUpdateNote() {
            Wallet wallet = new Wallet("Ví");
            wallet.updateNote("   Ghi chú mới   ");
            assertEquals("Ghi chú mới", wallet.getNote());

            wallet.updateNote(null);
            assertEquals("", wallet.getNote());
        }

        @Test
        @DisplayName("updateInfo() should update both name and note atomically")
        void testUpdateInfo() {
            Wallet wallet = new Wallet("Ví cũ", "Ghi chú cũ");
            wallet.updateInfo("Ví mới", "   Ghi chú mới   ");
            assertEquals("Ví mới", wallet.getName());
            assertEquals("Ghi chú mới", wallet.getNote());
        }
    }

    @Nested
    @DisplayName("Group 4: Financial Operations & [BR-FIN-02]")
    class FinancialOperationTests {

        @Test
        @DisplayName("credit() should increase balance for positive amounts")
        void testCreditIncreasesBalance() {
            Wallet wallet = new Wallet("Ví");
            wallet.credit(Money.of(200_000));

            assertEquals(Money.of(200_000), wallet.getCurrentBalance());

            wallet.credit(Money.of(50_000));
            assertEquals(Money.of(250_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("debit() should decrease balance when sufficient funds exist")
        void testDebitDecreasesBalance() {
            Wallet wallet = new Wallet("Ví");
            wallet.credit(Money.of(100_000));
            wallet.debit(Money.of(40_000));

            assertEquals(Money.of(60_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("debit() exceeding current balance should throw InsufficientBalanceException and preserve balance")
        void testDebitInsufficientBalanceThrowsException() {
            Wallet wallet = new Wallet("Ví");
            wallet.credit(Money.of(50_000));

            InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class,
                    () -> wallet.debit(Money.of(70_000)));

            assertEquals(wallet.getId().toString(), ex.getWalletId());
            assertEquals(Money.of(50_000), ex.getBalance());
            assertEquals(Money.of(70_000), ex.getAmount());
            assertEquals(Money.of(50_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("credit() and debit() with zero or negative amount should throw BusinessException (ERR_FIN_001)")
        void testNonPositiveAmountsThrowException() {
            Wallet wallet = new Wallet("Ví");
            wallet.credit(Money.of(100_000));

            BusinessException ex1 = assertThrows(BusinessException.class, () -> wallet.credit(Money.ZERO));
            assertEquals(ErrorCode.ERR_FIN_001, ex1.getErrorCode());

            BusinessException ex2 = assertThrows(BusinessException.class, () -> wallet.credit(Money.of(-10_000)));
            assertEquals(ErrorCode.ERR_FIN_001, ex2.getErrorCode());

            BusinessException ex3 = assertThrows(BusinessException.class, () -> wallet.debit(Money.ZERO));
            assertEquals(ErrorCode.ERR_FIN_001, ex3.getErrorCode());

            BusinessException ex4 = assertThrows(BusinessException.class, () -> wallet.debit(Money.of(-5_000)));
            assertEquals(ErrorCode.ERR_FIN_001, ex4.getErrorCode());
        }

        @Test
        @DisplayName("credit() and debit() with null amount should throw NullPointerException")
        void testNullAmountThrowsException() {
            Wallet wallet = new Wallet("Ví");

            assertThrows(NullPointerException.class, () -> wallet.credit(null));
            assertThrows(NullPointerException.class, () -> wallet.debit(null));
        }

        @Test
        @DisplayName("hasSufficientBalance() should accurately reflect balance sufficiency")
        void testHasSufficientBalance() {
            Wallet wallet = new Wallet("Ví");
            wallet.credit(Money.of(100_000));

            assertTrue(wallet.hasSufficientBalance(Money.of(50_000)));
            assertTrue(wallet.hasSufficientBalance(Money.of(100_000)));
            assertFalse(wallet.hasSufficientBalance(Money.of(150_000)));
            assertThrows(NullPointerException.class, () -> wallet.hasSufficientBalance(null));
        }
    }

    @Nested
    @DisplayName("Group 5: Default Wallet Protection [FR-WAL-03]")
    class DefaultWalletProtectionTests {

        @Test
        @DisplayName("archive() on default wallet should throw IllegalStateException")
        void testDefaultWallet_archiveThrowsException() {
            Wallet defaultWallet = new Wallet("Tiền mặt", "Ví tiền mặt mặc định", true);

            IllegalStateException ex = assertThrows(IllegalStateException.class, defaultWallet::archive);
            assertTrue(ex.getMessage().contains("mặc định"));
            assertEquals(EntityStatus.ACTIVE, defaultWallet.getStatus());
        }

        @Test
        @DisplayName("delete() on default wallet should throw IllegalStateException")
        void testDefaultWallet_deleteThrowsException() {
            Wallet defaultWallet = new Wallet("Tiền mặt", "Ví tiền mặt mặc định", true);

            IllegalStateException ex = assertThrows(IllegalStateException.class, defaultWallet::delete);
            assertTrue(ex.getMessage().contains("mặc định"));
            assertEquals(EntityStatus.ACTIVE, defaultWallet.getStatus());
        }

        @Test
        @DisplayName("Non-default wallet should be allowed to archive, activate, and delete")
        void testNonDefaultWalletLifecycleTransitions() {
            Wallet normalWallet = new Wallet("Ví phụ", "", false);

            normalWallet.archive();
            assertEquals(EntityStatus.ARCHIVED, normalWallet.getStatus());

            normalWallet.activate();
            assertEquals(EntityStatus.ACTIVE, normalWallet.getStatus());

            normalWallet.delete();
            assertInstanceOf(DeletedWalletState.class, normalWallet.getState());
        }

        @Test
        @DisplayName("isActive() should return true for active and false for archived")
        void testIsActiveQuery() {
            Wallet wallet = new Wallet("Ví phụ", "", false);
            assertTrue(wallet.isActive());

            wallet.archive();
            assertFalse(wallet.isActive());
        }

        @Test
        @DisplayName("isArchived() should return false for active and true for archived")
        void testIsArchivedQuery() {
            Wallet wallet = new Wallet("Ví phụ", "", false);
            assertFalse(wallet.isArchived());

            wallet.archive();
            assertTrue(wallet.isArchived());

            wallet.activate();
            assertFalse(wallet.isArchived());
        }

        @Test
        @DisplayName("isArchived() and isActive() on deleted wallet should return false safely, isDeleted() true")
        void testQueriesOnDeletedWallet() {
            Wallet wallet = new Wallet("Ví phụ", "", false);
            wallet.delete();
            assertFalse(wallet.isArchived());
            assertFalse(wallet.isActive());
            assertTrue(wallet.isDeleted());
            assertEquals(EntityStatus.DELETED, wallet.getStatus());
        }
    }

    @Nested
    @DisplayName("Group 6: Entity Identity (equals, hashCode, toString)")
    class IdentityTests {

        @Test
        @DisplayName("Two wallets with identical id should be equal regardless of other fields")
        void testEqualsSameId() {
            UUID sharedId = UUID.randomUUID();
            LocalDateTime time = LocalDateTime.now();

            Wallet w1 = new Wallet(sharedId, "Ví A", "Ghi chú A", false, time, new ActiveWalletState(), Money.of(10_000));
            Wallet w2 = new Wallet(sharedId, "Ví B", "Ghi chú B", true, time, new ArchivedWalletState(), Money.of(500_000));

            assertEquals(w1, w2);
            assertEquals(w1.hashCode(), w2.hashCode());
        }

        @Test
        @DisplayName("Two wallets with different ids should not be equal")
        void testEqualsDifferentId() {
            Wallet w1 = new Wallet("Ví");
            Wallet w2 = new Wallet("Ví");

            assertNotEquals(w1, w2);
        }

        @Test
        @DisplayName("equals() contract edge cases: reflexivity, null safety, type mismatch")
        void testEqualsContractEdgeCases() {
            Wallet wallet = new Wallet("Ví");

            assertEquals(wallet, wallet);
            assertNotEquals(null, wallet);
            assertNotEquals("Some String", wallet);
        }

        @Test
        @DisplayName("toString() should include id, name, formatted balance and status")
        void testToStringFormat() {
            Wallet wallet = new Wallet("Ví ShopeePay");
            String output = wallet.toString();

            assertTrue(output.contains(wallet.getId().toString()));
            assertTrue(output.contains("Ví ShopeePay"));
            assertTrue(output.contains("0 VNĐ"));
            assertTrue(output.contains("ACTIVE"));
        }

        @Test
        @DisplayName("toString() on deleted wallet should safely show DELETED without throwing")
        void testToStringOnDeletedWallet() {
            Wallet wallet = new Wallet("Ví phụ", "", false);
            wallet.delete();

            String output = wallet.toString();
            assertTrue(output.contains("DELETED"));
            assertTrue(output.contains("Ví phụ"));
        }
    }

    @Nested
    @DisplayName("Group 8: Balance Adjustment and Design Parity [UC-WAL-04, FR-WAL-05, BR-FIN-01, BR-FIN-02]")
    class BalanceAdjustmentAndParityTests {

        @Test
        @DisplayName("adjustBalance with higher actual balance should create surplus record and update balance")
        void testAdjustBalanceSurplus() {
            Wallet wallet = new Wallet("Ví Tiết kiệm");
            wallet.credit(Money.of(100_000));

            TransactionDate today = TransactionDate.now();
            BalanceAdjustmentRecord record = wallet.adjustBalance(Money.of(150_000), today, "Kiểm đếm tiền thưởng");

            assertNotNull(record);
            assertEquals(wallet.getId(), record.getWalletId());
            assertEquals(Money.of(100_000), record.getPreviousBalance());
            assertEquals(Money.of(150_000), record.getActualBalance());
            assertEquals(Money.of(50_000), record.getDifference());
            assertTrue(record.isSurplus());
            assertEquals(today, record.getAdjustedAt());
            assertEquals("Kiểm đếm tiền thưởng", record.getReason());
            assertEquals(Money.of(150_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("adjustBalance with lower actual balance should create deficit record and update balance")
        void testAdjustBalanceDeficit() {
            Wallet wallet = new Wallet("Ví Tiền mặt");
            wallet.credit(Money.of(200_000));

            TransactionDate today = TransactionDate.now();
            BalanceAdjustmentRecord record = wallet.adjustBalance(Money.of(120_000), today, "Kiểm kê hao hụt lẻ");

            assertNotNull(record);
            assertEquals(Money.of(200_000), record.getPreviousBalance());
            assertEquals(Money.of(120_000), record.getActualBalance());
            assertEquals(Money.of(-80_000), record.getDifference());
            assertFalse(record.isSurplus());
            assertEquals(Money.of(80_000), record.getAbsoluteDifference());
            assertEquals(Money.of(120_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("adjustBalance with identical balance should throw BusinessException with ERR_FIN_001")
        void testAdjustBalanceZeroDifferenceThrows() {
            Wallet wallet = new Wallet("Ví Test");
            wallet.credit(Money.of(100_000));

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    wallet.adjustBalance(Money.of(100_000), TransactionDate.now(), "Không lệch"));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
            assertEquals(Money.of(100_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("adjustBalance with negative actual balance should throw BusinessException with ERR_FIN_001")
        void testAdjustBalanceNegativeActualThrows() {
            Wallet wallet = new Wallet("Ví Test");
            wallet.credit(Money.of(50_000));

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    wallet.adjustBalance(Money.of(-10_000), TransactionDate.now(), "Âm tiền"));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
            assertEquals(Money.of(50_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("adjustBalance on inactive wallet should throw BusinessException with ERR_WAL_001")
        void testAdjustBalanceInactiveWalletThrows() {
            Wallet wallet = new Wallet("Ví Phụ", "", false);
            wallet.archive();

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    wallet.adjustBalance(Money.of(50_000), TransactionDate.now(), "Ví lưu trữ"));
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("getWalletId() and isDefault() aliases should match getId() and isDefaultWallet()")
        void testDesignParityAliases() {
            Wallet defaultWallet = new Wallet("Tiền mặt", "Ví mặc định", true);
            assertEquals(defaultWallet.getId(), defaultWallet.getWalletId());
            assertTrue(defaultWallet.isDefault());
            assertEquals(defaultWallet.isDefaultWallet(), defaultWallet.isDefault());

            Wallet regularWallet = new Wallet("Ví ngân hàng", "Ví phụ", false);
            assertEquals(regularWallet.getId(), regularWallet.getWalletId());
            assertFalse(regularWallet.isDefault());
            assertEquals(regularWallet.isDefaultWallet(), regularWallet.isDefault());
        }
    }

    @Nested
    @DisplayName("Group 6: Snapshot Copy Operations")
    class CopyTests {

        @Test
        @DisplayName("copy should preserve all fields including balance, default flag, createdAt and state")
        void testCopyPreservesAllFields() {
            UUID walletId = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.of(2025, 1, 15, 10, 30);
            Wallet original = new Wallet(
                    walletId, "Ví Tiết Kiệm", "Ghi chú gốc", true,
                    createdAt, new ActiveWalletState(), Money.of("250000.00"));

            Wallet snapshot = original.copy();

            assertNotSame(original, snapshot);
            assertEquals(walletId, snapshot.getId());
            assertEquals("Ví Tiết Kiệm", snapshot.getName());
            assertEquals("Ghi chú gốc", snapshot.getNote());
            assertTrue(snapshot.isDefaultWallet());
            assertEquals(createdAt, snapshot.getCreatedAt());
            assertEquals(EntityStatus.ACTIVE, snapshot.getStatus());
            assertEquals(Money.of("250000.00"), snapshot.getCurrentBalance());
        }

        @Test
        @DisplayName("copy should remain independent when original or snapshot is mutated")
        void testCopyRemainsIndependentOnMutation() {
            Wallet original = new Wallet("Ví Phụ", "Ban đầu", false);
            original.credit(Money.of("500000.00"));
            Wallet snapshot = original.copy();

            original.debit(Money.of("200000.00"));
            original.updateInfo("Ví Đã Đổi Tên", "Ghi chú mới");
            original.archive();

            assertEquals(Money.of("300000.00"), original.getCurrentBalance());
            assertEquals(EntityStatus.ARCHIVED, original.getStatus());
            assertEquals(Money.of("500000.00"), snapshot.getCurrentBalance());
            assertEquals("Ví Phụ", snapshot.getName());
            assertEquals("Ban đầu", snapshot.getNote());
            assertEquals(EntityStatus.ACTIVE, snapshot.getStatus());
        }

        @Test
        @DisplayName("copy should preserve archived state independently of original reactivation")
        void testCopyPreservesArchivedState() {
            Wallet archivedWallet = new Wallet("Ví Lưu Trữ", "", false);
            archivedWallet.credit(Money.of("100000.00"));
            archivedWallet.archive();

            Wallet snapshot = archivedWallet.copy();
            archivedWallet.activate();

            assertEquals(EntityStatus.ACTIVE, archivedWallet.getStatus());
            assertEquals(EntityStatus.ARCHIVED, snapshot.getStatus());
            assertEquals(Money.of("100000.00"), snapshot.getCurrentBalance());
        }
    }
}
