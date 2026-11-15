package com.pfms.infrastructure.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;

import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Bộ tuần tự hóa nội bộ dữ liệu sao lưu hệ thống sang chuỗi JSON chuẩn UTF-8 qua Gson.
 */
final class BackupJsonSerializer {

    // region Constants
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
    // endregion

    // region Constructors
    private BackupJsonSerializer() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static String toJson(BackupPayload payload) {
        Objects.requireNonNull(payload, "BackupPayload không được để null");

        JsonObject root = new JsonObject();
        root.add("wallets", mapToJsonArray(payload.getWallets(), BackupJsonSerializer::serializeWallet));
        root.add("needGroups", mapToJsonArray(payload.getNeedGroups(), BackupJsonSerializer::serializeNeedGroup));
        root.add("categories", mapToJsonArray(payload.getCategories(), BackupJsonSerializer::serializeCategory));
        root.add("transactions", mapToJsonArray(payload.getTransactions(), BackupJsonSerializer::serializeTransaction));
        root.add("adjustments", mapToJsonArray(payload.getAdjustments(), BackupJsonSerializer::serializeAdjustment));
        return GSON.toJson(root) + "\n";
    }
    // endregion

    // region Helpers
    private static <T> JsonArray mapToJsonArray(List<T> items, Function<T, JsonObject> mapper) {
        JsonArray array = new JsonArray();
        for (T item : items) {
            array.add(mapper.apply(item));
        }
        return array;
    }

    private static JsonObject serializeWallet(Wallet wallet) {
        JsonObject obj = new JsonObject();
        obj.addProperty("walletId", wallet.getId().toString());
        obj.addProperty("name", wallet.getName());
        obj.addProperty("currentBalance", formatMoney(wallet.getCurrentBalance()));
        obj.addProperty("status", wallet.getStatus().name());
        obj.addProperty("isDefault", wallet.isDefault());
        obj.addProperty("note", wallet.getNote());
        obj.addProperty("createdAt", wallet.getCreatedAt().toString());
        return obj;
    }

    private static JsonObject serializeNeedGroup(NeedGroup group) {
        JsonObject obj = new JsonObject();
        obj.addProperty("groupId", group.getNeedGroupId().toString());
        obj.addProperty("groupName", group.getName());
        obj.addProperty("flowType", group.getTransactionType().name());
        obj.addProperty("groupStatus", group.getStatus().name());
        obj.addProperty("icon", group.getIcon());
        obj.addProperty("color", group.getColor());
        obj.addProperty("createdAt", group.getCreatedAt().toString());
        return obj;
    }

    private static JsonObject serializeCategory(Category category) {
        JsonObject obj = new JsonObject();
        obj.addProperty("categoryId", category.getCategoryId().toString());
        obj.addProperty("name", category.getName());
        obj.addProperty("groupId", category.getNeedGroupId().toString());
        obj.addProperty("flowType", category.getTransactionType().name());
        obj.addProperty("status", category.getStatus().name());
        obj.addProperty("icon", category.getIcon());
        obj.addProperty("color", category.getColor());
        obj.addProperty("createdAt", category.getCreatedAt().toString());
        return obj;
    }

    private static JsonObject serializeTransaction(Transaction txn) {
        JsonObject obj = new JsonObject();
        obj.addProperty("transactionId", txn.getTransactionId().toString());
        obj.addProperty("amount", formatMoney(txn.getAmount()));
        obj.addProperty("transactionDate", txn.getTransactionDate().getIsoString());
        obj.addProperty("note", txn.getNote());
        obj.addProperty("createdAt", txn.getCreatedAt().toString());

        if (txn instanceof IncomeExpenseTransaction ie) {
            obj.addProperty("recordType", "INCOME_EXPENSE");
            obj.addProperty("transactionType", ie.getTransactionType().name());
            obj.addProperty("walletId", ie.getWallet().getId().toString());
            obj.addProperty("categoryId", ie.getCategory().getCategoryId().toString());
        } else if (txn instanceof TransferTransaction trf) {
            obj.addProperty("recordType", "TRANSFER");
            obj.addProperty("sourceWalletId", trf.getSourceWallet().getId().toString());
            obj.addProperty("destinationWalletId", trf.getDestinationWallet().getId().toString());
        }

        if (txn.getFee().isPresent()) {
            TransactionFee fee = txn.getFee().get();
            JsonObject feeObj = new JsonObject();
            feeObj.addProperty("feeId", fee.getFeeId().toString());
            feeObj.addProperty("parentTransactionId", fee.getParentTransactionId().toString());
            feeObj.addProperty("amount", formatMoney(fee.getAmount()));
            feeObj.addProperty("walletId", fee.getWallet().getId().toString());
            feeObj.addProperty("categoryId", fee.getCategory().getCategoryId().toString());
            obj.add("fee", feeObj);
        } else {
            obj.add("fee", null);
        }
        return obj;
    }

    private static JsonObject serializeAdjustment(BalanceAdjustmentRecord adj) {
        JsonObject obj = new JsonObject();
        obj.addProperty("recordId", adj.getRecordId().toString());
        obj.addProperty("walletId", adj.getWalletId().toString());
        obj.addProperty("previousBalance", formatMoney(adj.getPreviousBalance()));
        obj.addProperty("actualBalance", formatMoney(adj.getActualBalance()));
        obj.addProperty("difference", formatMoney(adj.getDifference()));
        obj.addProperty("adjustedAt", adj.getAdjustedAt().getIsoString());
        obj.addProperty("reason", adj.getReason());
        obj.addProperty("createdAt", adj.getCreatedAt().toString());
        return obj;
    }

    private static String formatMoney(Money money) {
        return (money == null) ? "0.00" : money.getAmount().setScale(2, RoundingMode.HALF_EVEN).toPlainString();
    }
    // endregion
}
