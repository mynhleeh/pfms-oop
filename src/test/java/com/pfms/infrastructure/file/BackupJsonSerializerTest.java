package com.pfms.infrastructure.file;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.data.BackupPayload;
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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BackupJsonSerializer Unit Tests")
class BackupJsonSerializerTest {

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<BackupJsonSerializer> constructor = BackupJsonSerializer.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("toJson should reject null payload")
    void toJsonShouldRejectNullPayload() {
        assertThrows(NullPointerException.class, () -> BackupJsonSerializer.toJson(null));
    }

    @Test
    @DisplayName("toJson should serialize empty payload with empty arrays")
    void toJsonShouldSerializeEmptyPayload() {
        BackupPayload payload = BackupPayload.empty();
        String json = BackupJsonSerializer.toJson(payload);

        assertNotNull(json);
        assertTrue(json.contains("\"wallets\": []"));
        assertTrue(json.contains("\"needGroups\": []"));
        assertTrue(json.contains("\"categories\": []"));
        assertTrue(json.contains("\"transactions\": []"));
        assertTrue(json.contains("\"adjustments\": []"));
    }

    @Test
    @DisplayName("toJson should serialize full payload with formatted amounts and escaped characters")
    void toJsonShouldSerializeFullPayload() {
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

        BackupPayload payload = new BackupPayload(
                List.of(wallet, destWallet),
                List.of(group),
                List.of(category, feeCategory),
                List.of(ieTxn, transferTxn),
                List.of(adj));

        String json = BackupJsonSerializer.toJson(payload);

        assertNotNull(json);
        assertTrue(json.contains("Ví \\\"Đặc Biệt\\\" \\n & Tiền Mặt"));
        assertTrue(json.contains("Ghi chú có dấu \\\\"));
        assertTrue(json.contains("1234567.89"));
        assertTrue(json.contains("INCOME_EXPENSE"));
        assertTrue(json.contains("TRANSFER"));
        assertTrue(json.contains("55000.00"));
        assertTrue(json.contains("2200.00"));
        assertTrue(json.contains("100000.00"));
        assertTrue(json.contains("2026-11-14"));
        assertTrue(json.contains("\"fee\": {"));
        assertTrue(json.contains("\"createdAt\":"));
    }
}
