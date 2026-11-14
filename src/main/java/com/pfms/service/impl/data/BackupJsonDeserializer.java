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
import com.pfms.service.impl.common.EntityLookupSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Bộ giải mã nội bộ chuỗi JSON sao lưu hệ thống sang cấu trúc {@link BackupPayload}.
 */
final class BackupJsonDeserializer {

    // region Constructors
    private BackupJsonDeserializer() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    @SuppressWarnings("unchecked")
    static BackupPayload fromJson(String json) {
        Objects.requireNonNull(json, "Chuỗi JSON không được để null");
        if (json.trim().isEmpty()) {
            throw DataCorruptionException.syntaxError(
                    "backup.json", 1, new IllegalArgumentException("Nội dung tệp JSON rỗng"));
        }

        try {
            Object parsed = new SimpleJsonParser(json).parse();
            if (!(parsed instanceof Map<?, ?> rawRoot)) {
                throw new IllegalArgumentException("JSON gốc phải là một đối tượng");
            }
            Map<String, Object> root = (Map<String, Object>) rawRoot;

            List<Wallet> wallets = parseWallets(root.get("wallets"));
            List<NeedGroup> needGroups = parseNeedGroups(root.get("needGroups"));
            List<Category> categories = parseCategories(root.get("categories"));
            Map<UUID, Wallet> walletMap = EntityLookupSupport.indexById(wallets, Wallet::getId);
            Map<UUID, Category> categoryMap = EntityLookupSupport.indexById(categories, Category::getCategoryId);

            return new BackupPayload(wallets, needGroups, categories,
                    parseTransactions(root.get("transactions"), walletMap, categoryMap),
                    parseAdjustments(root.get("adjustments")));
        } catch (DataCorruptionException e) {
            throw e;
        } catch (Exception e) {
            throw DataCorruptionException.syntaxError("backup.json", 1, e);
        }
    }
    // endregion

    // region Helpers
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
            Object raw, Map<UUID, Wallet> walletMap, Map<UUID, Category> categoryMap) {
        return parseList(raw, "transactions", map -> {
            UUID id = getUuid(map, "transactionId");
            Money amount = getMoney(map, "amount");
            TransactionDate date = getDate(map, "transactionDate");
            String note = getString(map, "note");
            TransactionFee fee = parseFee(map.get("fee"), walletMap, categoryMap);

            if ("TRANSFER".equalsIgnoreCase(getString(map, "recordType"))) {
                return new TransferTransaction(id,
                        resolveWallet(walletMap, getUuid(map, "sourceWalletId"), "Ví nguồn tạm"),
                        resolveWallet(walletMap, getUuid(map, "destinationWalletId"), "Ví đích tạm"),
                        amount, date, note, fee);
            }

            TransactionType type = TransactionType.valueOf(getString(map, "transactionType"));
            return new IncomeExpenseTransaction(id, type,
                    resolveWallet(walletMap, getUuid(map, "walletId"), "Ví tạm"),
                    resolveCategory(categoryMap, getUuid(map, "categoryId"), "Danh mục tạm", type),
                    amount, date, note, fee);
        });
    }

    @SuppressWarnings("unchecked")
    private static TransactionFee parseFee(Object rawFee, Map<UUID, Wallet> walletMap, Map<UUID, Category> categoryMap) {
        if (rawFee == null) {
            return null;
        }
        Map<String, Object> m = (Map<String, Object>) rawFee;
        return new TransactionFee(getUuid(m, "feeId"), getUuid(m, "parentTransactionId"), getMoney(m, "amount"),
                resolveWallet(walletMap, getUuid(m, "walletId"), "Ví phí tạm"),
                resolveCategory(categoryMap, getUuid(m, "categoryId"), "Danh mục phí tạm", TransactionType.EXPENSE));
    }

    private static List<BalanceAdjustmentRecord> parseAdjustments(Object raw) {
        return parseList(raw, "adjustments", map -> new BalanceAdjustmentRecord(
                getUuid(map, "recordId"), getUuid(map, "walletId"),
                getMoney(map, "previousBalance"), getMoney(map, "actualBalance"),
                getDate(map, "adjustedAt"), getString(map, "reason")));
    }

    private static Wallet resolveWallet(Map<UUID, Wallet> walletMap, UUID id, String fallbackName) {
        Wallet wallet = walletMap.get(id);
        return (wallet != null) ? wallet : new Wallet(id, fallbackName, "", false);
    }

    private static Category resolveCategory(
            Map<UUID, Category> categoryMap, UUID id, String fallbackName, TransactionType type) {
        Category category = categoryMap.get(id);
        return (category != null) ? category : new Category(id, fallbackName, UUID.randomUUID(), type, "", "");
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
