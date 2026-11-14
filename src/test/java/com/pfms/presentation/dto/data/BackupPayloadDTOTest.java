package com.pfms.presentation.dto.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BackupPayloadDTO Test Suite")
class BackupPayloadDTOTest {

    private Wallet createSampleWallet() {
        return new Wallet("Ví kiểm thử", "Ghi chú ví", false);
    }

    private NeedGroup createSampleNeedGroup() {
        return new NeedGroup(UUID.randomUUID(), "Ăn uống", TransactionType.EXPENSE, "food", "#FF0000");
    }

    private Category createSampleCategory(UUID needGroupId) {
        return new Category(UUID.randomUUID(), "Cơm trưa", needGroupId, TransactionType.EXPENSE, "lunch", "#00FF00");
    }

    private Transaction createSampleTransaction(Wallet wallet, Category category) {
        wallet.credit(Money.of("100000.00"));
        return new IncomeExpenseTransaction(
                UUID.randomUUID(),
                TransactionType.EXPENSE,
                wallet,
                category,
                Money.of("50000.00"),
                TransactionDate.of(LocalDate.now()),
                "Ăn trưa",
                null);
    }

    private BalanceAdjustmentRecord createSampleAdjustment(UUID walletId) {
        return new BalanceAdjustmentRecord(
                UUID.randomUUID(),
                walletId,
                Money.of("100000.00"),
                Money.of("120000.00"),
                TransactionDate.of(LocalDate.now()),
                "Kiểm kê cuối tháng");
    }

    @Nested
    @DisplayName("Constructor and Null Safety Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should initialize cleanly with non-null lists and calculate total records")
        void shouldInitializeWithValidLists() {
            Wallet wallet = createSampleWallet();
            NeedGroup group = createSampleNeedGroup();
            Category category = createSampleCategory(group.getNeedGroupId());
            Transaction txn = createSampleTransaction(wallet, category);
            BalanceAdjustmentRecord adj = createSampleAdjustment(wallet.getId());

            List<Wallet> wallets = List.of(wallet);
            List<NeedGroup> groups = List.of(group);
            List<Category> categories = List.of(category);
            List<Transaction> txns = List.of(txn);
            List<BalanceAdjustmentRecord> adjs = List.of(adj);

            BackupPayloadDTO payload = new BackupPayloadDTO(wallets, groups, categories, txns, adjs);

            assertEquals(1, payload.getWallets().size());
            assertEquals(1, payload.getNeedGroups().size());
            assertEquals(1, payload.getCategories().size());
            assertEquals(1, payload.getTransactions().size());
            assertEquals(1, payload.getAdjustments().size());
            assertEquals(5, payload.getTotalRecordCount());
            assertFalse(payload.isEmpty());
        }

        @Test
        @DisplayName("Should convert null lists to empty unmodifiable lists safely")
        void shouldDefaultNullListsToEmpty() {
            BackupPayloadDTO payload = new BackupPayloadDTO(null, null, null, null, null);

            assertNotNull(payload.getWallets());
            assertNotNull(payload.getNeedGroups());
            assertNotNull(payload.getCategories());
            assertNotNull(payload.getTransactions());
            assertNotNull(payload.getAdjustments());

            assertTrue(payload.getWallets().isEmpty());
            assertTrue(payload.getNeedGroups().isEmpty());
            assertTrue(payload.getCategories().isEmpty());
            assertTrue(payload.getTransactions().isEmpty());
            assertTrue(payload.getAdjustments().isEmpty());
            assertEquals(0, payload.getTotalRecordCount());
            assertTrue(payload.isEmpty());
        }

        @Test
        @DisplayName("Factory method empty() should create an empty payload")
        void shouldCreateEmptyPayloadViaFactory() {
            BackupPayloadDTO emptyPayload = BackupPayloadDTO.empty();

            assertNotNull(emptyPayload);
            assertTrue(emptyPayload.isEmpty());
            assertEquals(0, emptyPayload.getTotalRecordCount());
            assertTrue(emptyPayload.getWallets().isEmpty());
        }
    }

    @Nested
    @DisplayName("Immutability and Defensive Copy Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should defend against external mutations of the input lists")
        void shouldPerformDefensiveCopies() {
            List<Wallet> wallets = new ArrayList<>();
            wallets.add(createSampleWallet());

            BackupPayloadDTO payload = new BackupPayloadDTO(wallets, null, null, null, null);
            assertEquals(1, payload.getWallets().size());

            // Mutate external list
            wallets.add(new Wallet("Ví phụ"));

            // Payload should still only have 1 wallet
            assertEquals(1, payload.getWallets().size());
        }

        @Test
        @DisplayName("Returned lists should be unmodifiable")
        void shouldReturnUnmodifiableLists() {
            Wallet wallet = createSampleWallet();
            NeedGroup group = createSampleNeedGroup();
            Category category = createSampleCategory(group.getNeedGroupId());
            Transaction txn = createSampleTransaction(wallet, category);
            BalanceAdjustmentRecord adj = createSampleAdjustment(wallet.getId());

            BackupPayloadDTO payload = new BackupPayloadDTO(
                    List.of(wallet),
                    List.of(group),
                    List.of(category),
                    List.of(txn),
                    List.of(adj));

            List<Wallet> returnedWallets = payload.getWallets();
            assertThrows(UnsupportedOperationException.class, () -> returnedWallets.add(new Wallet("Ví mới")));
            assertThrows(UnsupportedOperationException.class, () -> payload.getNeedGroups().add(createSampleNeedGroup()));
            assertThrows(UnsupportedOperationException.class, () -> payload.getCategories().add(category));
            assertThrows(UnsupportedOperationException.class, () -> payload.getTransactions().remove(0));
            assertThrows(UnsupportedOperationException.class, () -> payload.getAdjustments().clear());
        }
    }

    @Nested
    @DisplayName("Equality and Identity Tests")
    class IdentityTests {

        @Test
        @DisplayName("Two payloads with identical lists should be equal and have matching hashCodes")
        void shouldAdhereToEqualsAndHashCodeContract() {
            Wallet wallet = createSampleWallet();
            BackupPayloadDTO payload1 = new BackupPayloadDTO(List.of(wallet), null, null, null, null);
            BackupPayloadDTO payload2 = new BackupPayloadDTO(List.of(wallet), null, null, null, null);

            assertEquals(payload1, payload2);
            assertEquals(payload1.hashCode(), payload2.hashCode());
            assertEquals(payload1, payload1);
            assertNotEquals(payload1, null);
            assertNotEquals(payload1, "other");
        }

        @Test
        @DisplayName("toString should contain descriptive summary of record counts")
        void toStringShouldProvideMeaningfulOutput() {
            Wallet wallet = createSampleWallet();
            BackupPayloadDTO payload = new BackupPayloadDTO(List.of(wallet), null, null, null, null);

            String text = payload.toString();
            assertTrue(text.contains("wallets=1"));
            assertTrue(text.contains("needGroups=0"));
            assertTrue(text.contains("totalRecords=1"));
        }
    }
}
