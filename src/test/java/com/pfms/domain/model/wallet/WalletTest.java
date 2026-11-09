package com.pfms.domain.model.wallet;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.common.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

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
            assertEquals(WalletStatus.ACTIVE, wallet.getStatus());
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
            String id = "WAL-12345";
            LocalDateTime now = LocalDateTime.now();
            Wallet wallet = new Wallet(id, "Vietcombank", "Ví chính", false,
                    now, new ActiveWalletState(), Money.of(500_000));

            assertEquals(id, wallet.getId());
            assertEquals("Vietcombank", wallet.getName());
            assertEquals("Ví chính", wallet.getNote());
            assertFalse(wallet.isDefaultWallet());
            assertEquals(now, wallet.getCreatedAt());
            assertEquals(Money.of(500_000), wallet.getCurrentBalance());
            assertEquals(WalletStatus.ACTIVE, wallet.getStatus());
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

            assertEquals(wallet.getId(), ex.getWalletId());
            assertEquals(Money.of(50_000), ex.getBalance());
            assertEquals(Money.of(70_000), ex.getAmount());
            assertEquals(Money.of(50_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("credit() and debit() with zero or negative amount should throw IllegalArgumentException")
        void testNonPositiveAmountsThrowException() {
            Wallet wallet = new Wallet("Ví");
            wallet.credit(Money.of(100_000));

            assertThrows(IllegalArgumentException.class, () -> wallet.credit(Money.ZERO));
            assertThrows(IllegalArgumentException.class, () -> wallet.credit(Money.of(-10_000)));

            assertThrows(IllegalArgumentException.class, () -> wallet.debit(Money.ZERO));
            assertThrows(IllegalArgumentException.class, () -> wallet.debit(Money.of(-5_000)));
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
            assertThrows(IllegalArgumentException.class, () -> wallet.hasSufficientBalance(null));
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
            assertEquals(WalletStatus.ACTIVE, defaultWallet.getStatus());
        }

        @Test
        @DisplayName("delete() on default wallet should throw IllegalStateException")
        void testDefaultWallet_deleteThrowsException() {
            Wallet defaultWallet = new Wallet("Tiền mặt", "Ví tiền mặt mặc định", true);

            IllegalStateException ex = assertThrows(IllegalStateException.class, defaultWallet::delete);
            assertTrue(ex.getMessage().contains("mặc định"));
            assertEquals(WalletStatus.ACTIVE, defaultWallet.getStatus());
        }

        @Test
        @DisplayName("Non-default wallet should be allowed to archive, activate, and delete")
        void testNonDefaultWalletLifecycleTransitions() {
            Wallet normalWallet = new Wallet("Ví phụ", "", false);

            normalWallet.archive();
            assertEquals(WalletStatus.ARCHIVED, normalWallet.getStatus());

            normalWallet.activate();
            assertEquals(WalletStatus.ACTIVE, normalWallet.getStatus());

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
    }

    @Nested
    @DisplayName("Group 6: Entity Identity (equals, hashCode, toString)")
    class IdentityTests {

        @Test
        @DisplayName("Two wallets with identical id should be equal regardless of other fields")
        void testEqualsSameId() {
            String sharedId = "WAL-SHARED-001";
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

            assertTrue(output.contains(wallet.getId()));
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
}
