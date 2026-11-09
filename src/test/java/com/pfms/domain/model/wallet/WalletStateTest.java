package com.pfms.domain.model.wallet;

import com.pfms.domain.model.common.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Wallet State Pattern (Unit 1.1)")
class WalletStateTest {

    @Nested
    @DisplayName("Group 1: ActiveWalletState Behaviors")
    class ActiveWalletStateTests {

        @Test
        @DisplayName("credit() should increase wallet balance when wallet is active")
        void testActiveState_creditCallsWalletApplyCredit() {
            Wallet wallet = new Wallet(Money.of(100_000));
            ActiveWalletState state = new ActiveWalletState();

            state.credit(wallet, Money.of(50_000));

            assertEquals(Money.of(150_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("debit() should decrease wallet balance when wallet is active")
        void testActiveState_debitCallsWalletApplyDebit() {
            Wallet wallet = new Wallet(Money.of(100_000));
            ActiveWalletState state = new ActiveWalletState();

            state.debit(wallet, Money.of(40_000));

            assertEquals(Money.of(60_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("archive() should transition wallet to ArchivedWalletState")
        void testActiveState_archiveTransitionsToArchived() {
            Wallet wallet = new Wallet();
            ActiveWalletState state = new ActiveWalletState();

            state.archive(wallet);

            assertInstanceOf(ArchivedWalletState.class, wallet.getState());
            assertEquals(WalletStatus.ARCHIVED, wallet.getStatus());
        }

        @Test
        @DisplayName("delete() should transition wallet to DeletedWalletState")
        void testActiveState_deleteTransitionsToDeleted() {
            Wallet wallet = new Wallet();
            ActiveWalletState state = new ActiveWalletState();

            state.delete(wallet);

            assertInstanceOf(DeletedWalletState.class, wallet.getState());
        }

        @Test
        @DisplayName("activate() should throw IllegalStateException when wallet is already active")
        void testActiveState_activateThrowsIllegalStateException() {
            Wallet wallet = new Wallet();
            ActiveWalletState state = new ActiveWalletState();

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> state.activate(wallet));
            assertTrue(ex.getMessage().contains("hoạt động"));
        }
    }

    @Nested
    @DisplayName("Group 2: ArchivedWalletState Behaviors")
    class ArchivedWalletStateTests {

        @Test
        @DisplayName("credit() should throw IllegalStateException and preserve balance")
        void testArchivedState_creditThrowsIllegalStateException() {
            Wallet wallet = new Wallet(new ArchivedWalletState(), Money.of(100_000));
            ArchivedWalletState state = new ArchivedWalletState();

            assertThrows(IllegalStateException.class, () -> state.credit(wallet, Money.of(10_000)));
            assertEquals(Money.of(100_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("debit() should throw IllegalStateException and preserve balance")
        void testArchivedState_debitThrowsIllegalStateException() {
            Wallet wallet = new Wallet(new ArchivedWalletState(), Money.of(100_000));
            ArchivedWalletState state = new ArchivedWalletState();

            assertThrows(IllegalStateException.class, () -> state.debit(wallet, Money.of(10_000)));
            assertEquals(Money.of(100_000), wallet.getCurrentBalance());
        }

        @Test
        @DisplayName("archive() should throw IllegalStateException when already archived")
        void testArchivedState_archiveThrowsIllegalStateException() {
            Wallet wallet = new Wallet(new ArchivedWalletState(), Money.ZERO);
            ArchivedWalletState state = new ArchivedWalletState();

            assertThrows(IllegalStateException.class, () -> state.archive(wallet));
        }

        @Test
        @DisplayName("activate() should transition wallet back to ActiveWalletState")
        void testArchivedState_activateTransitionsToActive() {
            Wallet wallet = new Wallet(new ArchivedWalletState(), Money.ZERO);
            ArchivedWalletState state = new ArchivedWalletState();

            state.activate(wallet);

            assertInstanceOf(ActiveWalletState.class, wallet.getState());
            assertEquals(WalletStatus.ACTIVE, wallet.getStatus());
        }

        @Test
        @DisplayName("delete() should throw IllegalStateException when wallet is archived")
        void testArchivedState_deleteThrowsIllegalStateException() {
            Wallet wallet = new Wallet(new ArchivedWalletState(), Money.ZERO);
            ArchivedWalletState state = new ArchivedWalletState();

            assertThrows(IllegalStateException.class, () -> state.delete(wallet));
        }
    }

    @Nested
    @DisplayName("Group 3: DeletedWalletState Behaviors")
    class DeletedWalletStateTests {

        @Test
        @DisplayName("all mutating operations on DeletedWalletState should throw IllegalStateException")
        void testDeletedState_allOperationsThrowIllegalStateException() {
            Wallet wallet = new Wallet(new DeletedWalletState(), Money.ZERO);
            DeletedWalletState state = new DeletedWalletState();

            assertThrows(IllegalStateException.class, () -> state.credit(wallet, Money.of(1_000)));
            assertThrows(IllegalStateException.class, () -> state.debit(wallet, Money.of(1_000)));
            assertThrows(IllegalStateException.class, () -> state.archive(wallet));
            assertThrows(IllegalStateException.class, () -> state.activate(wallet));
            assertThrows(IllegalStateException.class, () -> state.delete(wallet));
        }

        @Test
        @DisplayName("getStatus() on DeletedWalletState should throw IllegalStateException")
        void testDeletedState_getStatusThrowsIllegalStateException() {
            DeletedWalletState state = new DeletedWalletState();

            assertThrows(IllegalStateException.class, state::getStatus);
        }
    }

    @Nested
    @DisplayName("Group 4: Edge Cases and Parameter Validation")
    class EdgeCasesAndValidationTests {

        @Test
        @DisplayName("State methods should throw NullPointerException when wallet parameter is null")
        void testStateMethods_nullWalletThrowsNullPointerException() {
            ActiveWalletState active = new ActiveWalletState();
            ArchivedWalletState archived = new ArchivedWalletState();
            DeletedWalletState deleted = new DeletedWalletState();

            assertThrows(NullPointerException.class, () -> active.credit(null, Money.of(100)));
            assertThrows(NullPointerException.class, () -> active.debit(null, Money.of(100)));
            assertThrows(NullPointerException.class, () -> active.archive(null));
            assertThrows(NullPointerException.class, () -> active.activate(null));
            assertThrows(NullPointerException.class, () -> active.delete(null));

            assertThrows(NullPointerException.class, () -> archived.credit(null, Money.of(100)));
            assertThrows(NullPointerException.class, () -> archived.debit(null, Money.of(100)));
            assertThrows(NullPointerException.class, () -> archived.archive(null));
            assertThrows(NullPointerException.class, () -> archived.activate(null));
            assertThrows(NullPointerException.class, () -> archived.delete(null));

            assertThrows(NullPointerException.class, () -> deleted.credit(null, Money.of(100)));
            assertThrows(NullPointerException.class, () -> deleted.debit(null, Money.of(100)));
            assertThrows(NullPointerException.class, () -> deleted.archive(null));
            assertThrows(NullPointerException.class, () -> deleted.activate(null));
            assertThrows(NullPointerException.class, () -> deleted.delete(null));
        }

        @Test
        @DisplayName("credit() and debit() on ActiveWalletState should throw NullPointerException when amount is null")
        void testStateMethods_nullAmountThrowsNullPointerException() {
            Wallet wallet = new Wallet();
            ActiveWalletState active = new ActiveWalletState();

            assertThrows(NullPointerException.class, () -> active.credit(wallet, null));
            assertThrows(NullPointerException.class, () -> active.debit(wallet, null));
        }
    }
}

