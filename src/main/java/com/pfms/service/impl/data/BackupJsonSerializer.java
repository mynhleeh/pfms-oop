package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;

import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Bộ tuần tự hóa nội bộ dữ liệu sao lưu hệ thống sang chuỗi JSON chuẩn UTF-8.
 */
final class BackupJsonSerializer {

    // region Constructors
    private BackupJsonSerializer() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static String toJson(BackupPayload payload) {
        Objects.requireNonNull(payload, "BackupPayload không được để null");

        StringBuilder sb = new StringBuilder("{\n");
        appendWalletsJson(sb, payload.getWallets());
        sb.append(",\n");
        appendNeedGroupsJson(sb, payload.getNeedGroups());
        sb.append(",\n");
        appendCategoriesJson(sb, payload.getCategories());
        sb.append(",\n");
        appendTransactionsJson(sb, payload.getTransactions());
        sb.append(",\n");
        appendAdjustmentsJson(sb, payload.getAdjustments());
        return sb.append("\n}\n").toString();
    }

    static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (ch < ' ') {
                        String hex = Integer.toHexString(ch);
                        sb.append("\\u0000", 0, 6 - hex.length()).append(hex);
                    } else {
                        sb.append(ch);
                    }
                }
            }
        }
        return sb.toString();
    }
    // endregion

    // region Helpers
    private static void appendWalletsJson(StringBuilder sb, List<Wallet> wallets) {
        appendObjectArray(sb, "wallets", wallets, (buf, wallet) -> {
            appendField(buf, "walletId", wallet.getId(), true);
            appendField(buf, "name", wallet.getName(), true);
            appendField(buf, "currentBalance", formatMoney(wallet.getCurrentBalance()), true);
            appendField(buf, "status", wallet.getStatus().name(), true);
            appendField(buf, "isDefault", wallet.isDefault(), true);
            appendField(buf, "note", wallet.getNote(), false);
        });
    }

    private static void appendNeedGroupsJson(StringBuilder sb, List<NeedGroup> needGroups) {
        appendObjectArray(sb, "needGroups", needGroups, (buf, group) -> {
            appendField(buf, "groupId", group.getNeedGroupId(), true);
            appendField(buf, "groupName", group.getName(), true);
            appendField(buf, "flowType", group.getTransactionType().name(), true);
            appendField(buf, "groupStatus", group.getStatus().name(), true);
            appendField(buf, "icon", group.getIcon(), true);
            appendField(buf, "color", group.getColor(), false);
        });
    }

    private static void appendCategoriesJson(StringBuilder sb, List<Category> categories) {
        appendObjectArray(sb, "categories", categories, (buf, category) -> {
            appendField(buf, "categoryId", category.getCategoryId(), true);
            appendField(buf, "name", category.getName(), true);
            appendField(buf, "groupId", category.getNeedGroupId(), true);
            appendField(buf, "flowType", category.getTransactionType().name(), true);
            appendField(buf, "status", category.getStatus().name(), true);
            appendField(buf, "icon", category.getIcon(), true);
            appendField(buf, "color", category.getColor(), false);
        });
    }

    private static void appendTransactionsJson(StringBuilder sb, List<Transaction> transactions) {
        appendObjectArray(sb, "transactions", transactions, (buf, txn) -> {
            appendField(buf, "transactionId", txn.getTransactionId(), true);
            appendField(buf, "amount", formatMoney(txn.getAmount()), true);
            appendField(buf, "transactionDate", txn.getTransactionDate().getIsoString(), true);
            appendField(buf, "note", txn.getNote(), true);
            appendTransactionSubtypeFields(buf, txn);
            appendFeeJson(buf, txn.getFee().orElse(null));
        });
    }

    private static void appendTransactionSubtypeFields(StringBuilder buf, Transaction txn) {
        if (txn instanceof IncomeExpenseTransaction ie) {
            appendField(buf, "recordType", "INCOME_EXPENSE", true);
            appendField(buf, "transactionType", ie.getTransactionType().name(), true);
            appendField(buf, "walletId", ie.getWallet().getId(), true);
            appendField(buf, "categoryId", ie.getCategory().getCategoryId(), true);
        } else if (txn instanceof TransferTransaction trf) {
            appendField(buf, "recordType", "TRANSFER", true);
            appendField(buf, "sourceWalletId", trf.getSourceWallet().getId(), true);
            appendField(buf, "destinationWalletId", trf.getDestinationWallet().getId(), true);
        }
    }

    private static void appendFeeJson(StringBuilder buf, TransactionFee fee) {
        if (fee == null) {
            buf.append("      \"fee\": null\n");
            return;
        }
        buf.append("      \"fee\": {\n");
        appendField(buf, 8, "feeId", fee.getFeeId(), true);
        appendField(buf, 8, "parentTransactionId", fee.getParentTransactionId(), true);
        appendField(buf, 8, "amount", formatMoney(fee.getAmount()), true);
        appendField(buf, 8, "walletId", fee.getWallet().getId(), true);
        appendField(buf, 8, "categoryId", fee.getCategory().getCategoryId(), false);
        buf.append("      }\n");
    }

    private static void appendAdjustmentsJson(StringBuilder sb, List<BalanceAdjustmentRecord> adjustments) {
        appendObjectArray(sb, "adjustments", adjustments, (buf, adj) -> {
            appendField(buf, "recordId", adj.getRecordId(), true);
            appendField(buf, "walletId", adj.getWalletId(), true);
            appendField(buf, "previousBalance", formatMoney(adj.getPreviousBalance()), true);
            appendField(buf, "actualBalance", formatMoney(adj.getActualBalance()), true);
            appendField(buf, "difference", formatMoney(adj.getDifference()), true);
            appendField(buf, "adjustedAt", adj.getAdjustedAt().getIsoString(), true);
            appendField(buf, "reason", adj.getReason(), false);
        });
    }

    private static <T> void appendObjectArray(
            StringBuilder sb, String fieldName, List<T> items, BiConsumer<StringBuilder, T> fieldsWriter) {
        sb.append("  \"").append(fieldName).append("\": [\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append("    {\n");
            fieldsWriter.accept(sb, items.get(i));
            sb.append(i < items.size() - 1 ? "    },\n" : "    }\n");
        }
        sb.append("  ]");
    }

    private static void appendField(StringBuilder sb, String key, Object value, boolean comma) {
        appendField(sb, 6, key, value, comma);
    }

    private static void appendField(StringBuilder sb, int indentSpaces, String key, Object value, boolean comma) {
        sb.append(" ".repeat(indentSpaces)).append("\"").append(key).append("\": ");
        if (value == null) {
            sb.append("null");
        } else if (value instanceof Boolean || value instanceof Number) {
            sb.append(value);
        } else {
            sb.append("\"").append(escapeJson(value.toString())).append("\"");
        }
        sb.append(comma ? ",\n" : "\n");
    }

    private static String formatMoney(Money money) {
        return (money == null) ? "0.00" : money.getAmount().setScale(2, RoundingMode.HALF_EVEN).toPlainString();
    }
    // endregion
}
