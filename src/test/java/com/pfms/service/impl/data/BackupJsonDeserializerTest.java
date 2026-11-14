package com.pfms.service.impl.data;

import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BackupJsonDeserializer Unit Tests")
class BackupJsonDeserializerTest {

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<BackupJsonDeserializer> constructor = BackupJsonDeserializer.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("fromJson should reject null or blank json")
    void fromJsonShouldRejectNullOrBlank() {
        assertThrows(NullPointerException.class, () -> BackupJsonDeserializer.fromJson(null));
        assertThrows(DataCorruptionException.class, () -> BackupJsonDeserializer.fromJson(""));
        assertThrows(DataCorruptionException.class, () -> BackupJsonDeserializer.fromJson("   \n\t"));
    }

    @Test
    @DisplayName("fromJson should reject malformed JSON syntax")
    void fromJsonShouldRejectMalformedJson() {
        assertThrows(DataCorruptionException.class, () -> BackupJsonDeserializer.fromJson("{ not a valid json"));
        assertThrows(DataCorruptionException.class, () -> BackupJsonDeserializer.fromJson("{\"wallets\": ["));
    }

    @Test
    @DisplayName("fromJson should parse empty payload")
    void fromJsonShouldParseEmptyPayload() {
        String json = "{\"wallets\":[],\"needGroups\":[],\"categories\":[],\"transactions\":[],\"adjustments\":[]}";
        BackupPayload payload = BackupJsonDeserializer.fromJson(json);

        assertNotNull(payload);
        assertTrue(payload.isEmpty());
        assertEquals(0, payload.getTotalRecordCount());
    }

    @Test
    @DisplayName("fromJson should round-trip full payload completely")
    void fromJsonShouldRoundTripFullPayload() {
        Wallet wallet = new Wallet(UUID.randomUUID(), "Ví \"Đặc Biệt\" \n & Tiền Mặt", "Ghi chú có dấu \\", true);
        wallet.credit(Money.of("1234567.89"));

        NeedGroup group = new NeedGroup(UUID.randomUUID(), "Ăn Uống", TransactionType.EXPENSE, "food", "#FF0000");
        Category category = new Category(
                UUID.randomUUID(), "Cơm Trưa", group.getNeedGroupId(), TransactionType.EXPENSE, "lunch", "#FF0000");
        Category feeCategory = new Category(
                UUID.randomUUID(), "Phí Dịch Vụ", group.getNeedGroupId(), TransactionType.EXPENSE, "fee", "#999999");

        IncomeExpenseTransaction ieTxn = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.EXPENSE, wallet, category,
                Money.of("55000.00"), TransactionDate.of(LocalDate.of(2026, 11, 14)), "Ăn trưa", null);

        UUID transferId = UUID.randomUUID();
        Wallet destWallet = new Wallet(UUID.randomUUID(), "Ví Tiết Kiệm", "", false);
        TransactionFee fee = new TransactionFee(UUID.randomUUID(), transferId, Money.of("2200.00"), wallet, feeCategory);
        TransferTransaction transferTxn = new TransferTransaction(
                transferId, wallet, destWallet, Money.of("100000.00"),
                TransactionDate.of(LocalDate.of(2026, 11, 14)), "Chuyển khoản", fee);

        BalanceAdjustmentRecord adj = wallet.adjustBalance(
                Money.of("1200000.00"), TransactionDate.of(LocalDate.of(2026, 11, 14)), "Kiểm kê định kỳ");

        BackupPayload original = new BackupPayload(
                List.of(wallet, destWallet),
                List.of(group),
                List.of(category, feeCategory),
                List.of(ieTxn, transferTxn),
                List.of(adj));

        String json = BackupJsonSerializer.toJson(original);
        BackupPayload restored = BackupJsonDeserializer.fromJson(json);

        assertNotNull(restored);
        assertEquals(2, restored.getWallets().size());
        assertEquals(1, restored.getNeedGroups().size());
        assertEquals(2, restored.getCategories().size());
        assertEquals(2, restored.getTransactions().size());
        assertEquals(1, restored.getAdjustments().size());

        Wallet restoredW1 = restored.getWallets().stream()
                .filter(w -> w.getId().equals(wallet.getId()))
                .findFirst().orElseThrow();
        assertEquals(wallet.getName(), restoredW1.getName());
        assertEquals(wallet.getCurrentBalance(), restoredW1.getCurrentBalance());
        assertEquals(wallet.getStatus(), restoredW1.getStatus());
        assertEquals(wallet.isDefault(), restoredW1.isDefault());
        assertEquals(wallet.getNote(), restoredW1.getNote());

        NeedGroup restoredG = restored.getNeedGroups().get(0);
        assertEquals(group.getNeedGroupId(), restoredG.getNeedGroupId());
        assertEquals(group.getName(), restoredG.getName());
        assertEquals(group.getTransactionType(), restoredG.getTransactionType());
        assertEquals(group.getStatus(), restoredG.getStatus());
        assertEquals(group.getIcon(), restoredG.getIcon());
        assertEquals(group.getColor(), restoredG.getColor());

        Category restoredC = restored.getCategories().stream()
                .filter(c -> c.getCategoryId().equals(category.getCategoryId()))
                .findFirst().orElseThrow();
        assertEquals(category.getName(), restoredC.getName());
        assertEquals(category.getNeedGroupId(), restoredC.getNeedGroupId());
        assertEquals(category.getTransactionType(), restoredC.getTransactionType());

        IncomeExpenseTransaction restoredIE = (IncomeExpenseTransaction) restored.getTransactions().stream()
                .filter(t -> t.getTransactionId().equals(ieTxn.getTransactionId()))
                .findFirst().orElseThrow();
        assertEquals(ieTxn.getAmount(), restoredIE.getAmount());
        assertEquals(ieTxn.getTransactionDate(), restoredIE.getTransactionDate());
        assertEquals(ieTxn.getNote(), restoredIE.getNote());
        assertEquals(wallet.getId(), restoredIE.getWallet().getId());
        assertEquals(category.getCategoryId(), restoredIE.getCategory().getCategoryId());

        TransferTransaction restoredTrf = (TransferTransaction) restored.getTransactions().stream()
                .filter(t -> t.getTransactionId().equals(transferId))
                .findFirst().orElseThrow();
        assertEquals(transferTxn.getAmount(), restoredTrf.getAmount());
        assertEquals(wallet.getId(), restoredTrf.getSourceWallet().getId());
        assertEquals(destWallet.getId(), restoredTrf.getDestinationWallet().getId());
        assertTrue(restoredTrf.getFee().isPresent());
        assertEquals(fee.getAmount(), restoredTrf.getFee().get().getAmount());
        assertEquals(feeCategory.getCategoryId(), restoredTrf.getFee().get().getCategory().getCategoryId());

        BalanceAdjustmentRecord restoredAdj = restored.getAdjustments().get(0);
        assertEquals(adj.getRecordId(), restoredAdj.getRecordId());
        assertEquals(adj.getWalletId(), restoredAdj.getWalletId());
        assertEquals(adj.getPreviousBalance(), restoredAdj.getPreviousBalance());
        assertEquals(adj.getActualBalance(), restoredAdj.getActualBalance());
        assertEquals(adj.getDifference(), restoredAdj.getDifference());
        assertEquals(adj.getAdjustedAt(), restoredAdj.getAdjustedAt());
        assertEquals(adj.getReason(), restoredAdj.getReason());
    }
}

