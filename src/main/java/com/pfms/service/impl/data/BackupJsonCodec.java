package com.pfms.service.impl.data;

import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.ArchivedWalletState;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.model.wallet.WalletState;
import com.pfms.presentation.dto.data.BackupPayloadDTO;
import com.pfms.service.impl.common.EntityLookupSupport;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Bộ mã hóa và giải mã nội bộ định dạng sao lưu hệ thống sang chuỗi JSON chuẩn UTF-8.
 * Đảm bảo định dạng số tiền chuẩn xác 2 chữ số thập phân và phân tích cú pháp an toàn.
 */
final class BackupJsonCodec {

    // region Constructors
    private BackupJsonCodec() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static String toJson(BackupPayloadDTO payload) {
        Objects.requireNonNull(payload, "BackupPayloadDTO không được để null");

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        appendWalletsJson(sb, payload.getWallets());
        sb.append(",\n");
        appendNeedGroupsJson(sb, payload.getNeedGroups());
        sb.append(",\n");
        appendCategoriesJson(sb, payload.getCategories());
        sb.append(",\n");
        appendTransactionsJson(sb, payload.getTransactions());
        sb.append(",\n");
        appendAdjustmentsJson(sb, payload.getAdjustments());
        sb.append("\n}\n");
        return sb.toString();
    }

    static BackupPayloadDTO fromJson(String json) {
        Objects.requireNonNull(json, "Chuỗi JSON không được để null");
        if (json.trim().isEmpty()) {
            throw DataCorruptionException.syntaxError("backup.json", 1, new IllegalArgumentException("Nội dung tệp JSON rỗng"));
        }

        Map<String, Object> root;
        try {
            Object parsed = new SimpleJsonParser(json).parse();
            if (!(parsed instanceof Map<?, ?> m)) {
                throw DataCorruptionException.syntaxError("backup.json", 1, new IllegalArgumentException("JSON gốc phải là một đối tượng"));
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> typedRoot = (Map<String, Object>) m;
            root = typedRoot;
        } catch (Exception e) {
            throw DataCorruptionException.syntaxError("backup.json", 1, e);
        }

        try {
            List<Wallet> wallets = parseWallets(root.get("wallets"));
            List<NeedGroup> needGroups = parseNeedGroups(root.get("needGroups"));
            List<Category> categories = parseCategories(root.get("categories"));

            Map<UUID, Wallet> walletMap = EntityLookupSupport.indexById(wallets, Wallet::getId);
            Map<UUID, Category> categoryMap = EntityLookupSupport.indexById(categories, Category::getCategoryId);

            List<Transaction> transactions = parseTransactions(root.get("transactions"), walletMap, categoryMap);
            List<BalanceAdjustmentRecord> adjustments = parseAdjustments(root.get("adjustments"));

            return new BackupPayloadDTO(wallets, needGroups, categories, transactions, adjustments);
        } catch (DataCorruptionException e) {
            throw e;
        } catch (Exception e) {
            throw DataCorruptionException.syntaxError("backup.json", 1, e);
        }
    }
    // endregion

    // region Helpers
    private static void appendWalletsJson(StringBuilder sb, List<Wallet> wallets) {
        appendArray(sb, "wallets", wallets, (buf, w) -> {
            buf.append("    {\n");
            appendField(buf, "walletId", w.getId(), true);
            appendField(buf, "name", w.getName(), true);
            appendField(buf, "currentBalance", formatMoney(w.getCurrentBalance()), true);
            appendField(buf, "status", w.getStatus().name(), true);
            appendField(buf, "isDefault", w.isDefault(), true);
            appendField(buf, "note", w.getNote(), false);
            buf.append("    }");
        });
    }

    private static void appendNeedGroupsJson(StringBuilder sb, List<NeedGroup> needGroups) {
        appendArray(sb, "needGroups", needGroups, (buf, g) -> {
            buf.append("    {\n");
            appendField(buf, "groupId", g.getNeedGroupId(), true);
            appendField(buf, "groupName", g.getName(), true);
            appendField(buf, "flowType", g.getTransactionType().name(), true);
            appendField(buf, "groupStatus", g.getStatus().name(), true);
            appendField(buf, "icon", g.getIcon(), true);
            appendField(buf, "color", g.getColor(), false);
            buf.append("    }");
        });
    }

    private static void appendCategoriesJson(StringBuilder sb, List<Category> categories) {
        appendArray(sb, "categories", categories, (buf, c) -> {
            buf.append("    {\n");
            appendField(buf, "categoryId", c.getCategoryId(), true);
            appendField(buf, "name", c.getName(), true);
            appendField(buf, "groupId", c.getNeedGroupId(), true);
            appendField(buf, "flowType", c.getTransactionType().name(), true);
            appendField(buf, "status", c.getStatus().name(), true);
            appendField(buf, "icon", c.getIcon(), true);
            appendField(buf, "color", c.getColor(), false);
            buf.append("    }");
        });
    }

    private static void appendTransactionsJson(StringBuilder sb, List<Transaction> transactions) {
        appendArray(sb, "transactions", transactions, (buf, txn) -> {
            buf.append("    {\n");
            appendField(buf, "transactionId", txn.getTransactionId(), true);
            appendField(buf, "amount", formatMoney(txn.getAmount()), true);
            appendField(buf, "transactionDate", txn.getTransactionDate().getIsoString(), true);
            appendField(buf, "note", txn.getNote(), true);

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

            if (txn.getFee().isPresent()) {
                TransactionFee fee = txn.getFee().get();
                buf.append("      \"fee\": {\n");
                appendField(buf, 8, "feeId", fee.getFeeId(), true);
                appendField(buf, 8, "parentTransactionId", fee.getParentTransactionId(), true);
                appendField(buf, 8, "amount", formatMoney(fee.getAmount()), true);
                appendField(buf, 8, "walletId", fee.getWallet().getId(), true);
                appendField(buf, 8, "categoryId", fee.getCategory().getCategoryId(), false);
                buf.append("      }\n");
            } else {
                buf.append("      \"fee\": null\n");
            }
            buf.append("    }");
        });
    }

    private static void appendAdjustmentsJson(StringBuilder sb, List<BalanceAdjustmentRecord> adjustments) {
        appendArray(sb, "adjustments", adjustments, (buf, adj) -> {
            buf.append("    {\n");
            appendField(buf, "recordId", adj.getRecordId(), true);
            appendField(buf, "walletId", adj.getWalletId(), true);
            appendField(buf, "previousBalance", formatMoney(adj.getPreviousBalance()), true);
            appendField(buf, "actualBalance", formatMoney(adj.getActualBalance()), true);
            appendField(buf, "difference", formatMoney(adj.getDifference()), true);
            appendField(buf, "adjustedAt", adj.getAdjustedAt().getIsoString(), true);
            appendField(buf, "reason", adj.getReason(), false);
            buf.append("    }");
        });
    }

    private static <T> void appendArray(StringBuilder sb, String fieldName, List<T> items, BiConsumer<StringBuilder, T> itemWriter) {
        sb.append("  \"").append(fieldName).append("\": [\n");
        for (int i = 0; i < items.size(); i++) {
            itemWriter.accept(sb, items.get(i));
            if (i < items.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ]");
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
        if (comma) {
            sb.append(",");
        }
        sb.append("\n");
    }

    private static void appendField(StringBuilder sb, String key, Object value, boolean comma) {
        appendField(sb, 6, key, value, comma);
    }

    private static String formatMoney(Money money) {
        return (money == null) ? "0.00" : money.getAmount().setScale(2, RoundingMode.HALF_EVEN).toPlainString();
    }

    static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        String hex = Integer.toHexString(c);
                        sb.append("\\u0000", 0, 6 - hex.length()).append(hex);
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    private static List<Wallet> parseWallets(Object raw) {
        return parseList(raw, "wallets", map -> {
            WalletState state = "ARCHIVED".equalsIgnoreCase(getString(map, "status"))
                    ? new ArchivedWalletState() : new ActiveWalletState();
            return new Wallet(getUuid(map, "walletId"), getString(map, "name"),
                    getString(map, "note"), Boolean.TRUE.equals(map.get("isDefault")),
                    LocalDateTime.now(), state, getMoney(map, "currentBalance"));
        });
    }

    private static List<NeedGroup> parseNeedGroups(Object raw) {
        return parseList(raw, "needGroups", map -> {
            NeedGroup group = new NeedGroup(getUuid(map, "groupId"), getString(map, "groupName"),
                    TransactionType.valueOf(getString(map, "flowType")),
                    getString(map, "icon"), getString(map, "color"));
            if (EntityStatus.DELETED.name().equalsIgnoreCase(getString(map, "groupStatus"))) {
                group.markDeleted(2);
            }
            return group;
        });
    }

    private static List<Category> parseCategories(Object raw) {
        return parseList(raw, "categories", map -> {
            Category cat = new Category(getUuid(map, "categoryId"), getString(map, "name"),
                    getUuid(map, "groupId"), TransactionType.valueOf(getString(map, "flowType")),
                    getString(map, "icon"), getString(map, "color"));
            if (EntityStatus.DELETED.name().equalsIgnoreCase(getString(map, "status"))) {
                cat.markDeleted();
            }
            return cat;
        });
    }

    private static List<Transaction> parseTransactions(
            Object raw,
            Map<UUID, Wallet> walletMap,
            Map<UUID, Category> categoryMap) {

        return parseList(raw, "transactions", map -> {
            UUID id = getUuid(map, "transactionId");
            Money amount = getMoney(map, "amount");
            TransactionDate date = getDate(map, "transactionDate");
            String note = getString(map, "note");
            TransactionFee fee = parseFee(map.get("fee"), walletMap, categoryMap);

            if ("TRANSFER".equalsIgnoreCase(getString(map, "recordType"))) {
                UUID srcId = getUuid(map, "sourceWalletId");
                UUID dstId = getUuid(map, "destinationWalletId");
                return new TransferTransaction(id,
                        walletMap.getOrDefault(srcId, new Wallet(srcId, "Ví nguồn tạm", "", false)),
                        walletMap.getOrDefault(dstId, new Wallet(dstId, "Ví đích tạm", "", false)),
                        amount, date, note, fee);
            }

            UUID wId = getUuid(map, "walletId");
            UUID cId = getUuid(map, "categoryId");
            TransactionType type = TransactionType.valueOf(getString(map, "transactionType"));
            return new IncomeExpenseTransaction(id, type,
                    walletMap.getOrDefault(wId, new Wallet(wId, "Ví tạm", "", false)),
                    categoryMap.getOrDefault(cId, new Category(cId, "Danh mục tạm", UUID.randomUUID(), type, "", "")),
                    amount, date, note, fee);
        });
    }

    @SuppressWarnings("unchecked")
    private static TransactionFee parseFee(
            Object rawFee,
            Map<UUID, Wallet> walletMap,
            Map<UUID, Category> categoryMap) {

        if (rawFee == null) {
            return null;
        }
        Map<String, Object> m = (Map<String, Object>) rawFee;
        UUID feeWalletId = getUuid(m, "walletId");
        UUID feeCatId = getUuid(m, "categoryId");

        Wallet feeWallet = walletMap.getOrDefault(feeWalletId, new Wallet(feeWalletId, "Ví phí tạm", "", false));
        Category feeCat = categoryMap.getOrDefault(feeCatId,
                new Category(feeCatId, "Danh mục phí tạm", UUID.randomUUID(), TransactionType.EXPENSE, "", ""));

        return new TransactionFee(getUuid(m, "feeId"), getUuid(m, "parentTransactionId"),
                getMoney(m, "amount"), feeWallet, feeCat);
    }

    private static List<BalanceAdjustmentRecord> parseAdjustments(Object raw) {
        return parseList(raw, "adjustments", map -> new BalanceAdjustmentRecord(
                getUuid(map, "recordId"), getUuid(map, "walletId"),
                getMoney(map, "previousBalance"), getMoney(map, "actualBalance"),
                getDate(map, "adjustedAt"), getString(map, "reason")));
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> parseList(Object raw, String fieldName, Function<Map<String, Object>, T> mapper) {
        if (raw == null) {
            return Collections.emptyList();
        }
        if (!(raw instanceof List<?> list)) {
            throw new IllegalArgumentException("Trường " + fieldName + " phải là mảng");
        }
        List<T> result = new ArrayList<>();
        for (Object item : list) {
            result.add(mapper.apply((Map<String, Object>) item));
        }
        return result;
    }

    private static UUID getUuid(Map<String, Object> map, String key) {
        String val = getString(map, key);
        return (val == null) ? null : UUID.fromString(val);
    }

    private static Money getMoney(Map<String, Object> map, String key) {
        String val = getString(map, key);
        return (val == null) ? Money.ZERO : Money.of(val);
    }

    private static TransactionDate getDate(Map<String, Object> map, String key) {
        String val = getString(map, key);
        return (val == null) ? null : TransactionDate.of(LocalDate.parse(val));
    }

    private static String getString(Map<String, Object> map, String key) {
        return (String) map.get(key);
    }
    // endregion
}
