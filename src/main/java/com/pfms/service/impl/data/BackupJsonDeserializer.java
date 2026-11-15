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
import com.pfms.domain.model.wallet.DeletedWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.model.wallet.WalletState;
import com.pfms.service.impl.common.EntityLookupSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Bộ giải mã nội bộ chuỗi JSON sao lưu hệ thống sang cấu trúc
 * {@link BackupPayload}.
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

            return new BackupPayload(
                    wallets, needGroups, categories,
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
            WalletState state = parseWalletState(requireNonBlankString(map, "status"));
            return Wallet.reconstitute(
                    requireUuid(map, "walletId"),
                    requireNonBlankString(map, "name"),
                    getOptionalString(map, "note"),
                    requireBoolean(map, "isDefault"),
                    requireDateTime(map, "createdAt"),
                    state,
                    requireMoney(map, "currentBalance"));
        });
    }

    private static WalletState parseWalletState(String status) {
        return switch (status.trim().toUpperCase(Locale.ROOT)) {
            case "ACTIVE" -> new ActiveWalletState();
            case "ARCHIVED" -> new ArchivedWalletState();
            case "DELETED" -> new DeletedWalletState();
            default -> throw new IllegalArgumentException("Trạng thái ví không hợp lệ: " + status);
        };
    }

    private static List<NeedGroup> parseNeedGroups(Object raw) {
        return parseList(raw, "needGroups", map -> NeedGroup.reconstitute(
                requireUuid(map, "groupId"),
                requireNonBlankString(map, "groupName"),
                TransactionType.fromString(requireNonBlankString(map, "flowType")),
                EntityStatus.fromString(requireNonBlankString(map, "groupStatus")),
                getOptionalString(map, "icon"),
                getOptionalString(map, "color"),
                requireDateTime(map, "createdAt")));
    }

    private static List<Category> parseCategories(Object raw) {
        return parseList(raw, "categories", map -> Category.reconstitute(
                requireUuid(map, "categoryId"),
                requireNonBlankString(map, "name"),
                requireUuid(map, "groupId"),
                TransactionType.fromString(requireNonBlankString(map, "flowType")),
                EntityStatus.fromString(requireNonBlankString(map, "status")),
                getOptionalString(map, "icon"),
                getOptionalString(map, "color"),
                requireDateTime(map, "createdAt")));
    }

    @SuppressWarnings("unchecked")
    private static List<Transaction> parseTransactions(
            Object raw, Map<UUID, Wallet> walletMap, Map<UUID, Category> categoryMap) {
        if (raw == null) {
            return Collections.emptyList();
        }
        if (!(raw instanceof List<?> rawList)) {
            throw new IllegalArgumentException("Trường transactions phải là mảng");
        }

        List<Transaction> transactions = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof Map<?, ?> itemMap)) {
                throw new IllegalArgumentException("Phần tử trong transactions phải là đối tượng JSON");
            }
            transactions.add(parseTransaction((Map<String, Object>) itemMap, walletMap, categoryMap));
        }
        return transactions;
    }

    private static Transaction parseTransaction(
            Map<String, Object> fields,
            Map<UUID, Wallet> walletMap,
            Map<UUID, Category> categoryMap) {
        UUID transactionId = requireUuid(fields, "transactionId");
        Money amount = requireMoney(fields, "amount");
        TransactionDate transactionDate = requireDate(fields, "transactionDate");
        String note = getOptionalString(fields, "note");
        LocalDateTime createdAt = requireDateTime(fields, "createdAt");
        TransactionFee fee = parseFee(fields.get("fee"), walletMap, categoryMap);
        String recordType = requireNonBlankString(fields, "recordType").toUpperCase(Locale.ROOT);

        return switch (recordType) {
            case "TRANSFER" -> {
                Wallet sourceWallet = resolveWallet(
                        walletMap, requireUuid(fields, "sourceWalletId"), "Ví nguồn tạm");
                Wallet destinationWallet = resolveWallet(
                        walletMap, requireUuid(fields, "destinationWalletId"), "Ví đích tạm");
                yield TransferTransaction.reconstitute(
                        transactionId, sourceWallet, destinationWallet,
                        amount, transactionDate, note, fee, createdAt);
            }
            case "INCOME_EXPENSE" -> {
                TransactionType transactionType = TransactionType.fromString(
                        requireNonBlankString(fields, "transactionType"));
                Wallet wallet = resolveWallet(walletMap, requireUuid(fields, "walletId"), "Ví tạm");
                Category category = resolveCategory(
                        categoryMap, requireUuid(fields, "categoryId"), "Danh mục tạm", transactionType);
                yield IncomeExpenseTransaction.reconstitute(
                        transactionId, transactionType, wallet, category,
                        amount, transactionDate, note, fee, createdAt);
            }
            default -> throw new IllegalArgumentException("Loại bản ghi giao dịch không hợp lệ: " + recordType);
        };
    }

    @SuppressWarnings("unchecked")
    private static TransactionFee parseFee(Object rawFee, Map<UUID, Wallet> walletMap,
            Map<UUID, Category> categoryMap) {
        if (rawFee == null) {
            return null;
        }
        if (!(rawFee instanceof Map<?, ?> rawMap)) {
            throw new IllegalArgumentException("Trường fee phải là đối tượng JSON");
        }
        Map<String, Object> m = (Map<String, Object>) rawMap;
        return TransactionFee.reconstitute(
                requireUuid(m, "feeId"),
                requireUuid(m, "parentTransactionId"),
                requireMoney(m, "amount"),
                resolveWallet(walletMap, requireUuid(m, "walletId"), "Ví phí tạm"),
                resolveCategory(
                        categoryMap, requireUuid(m, "categoryId"), "Danh mục phí tạm", TransactionType.EXPENSE));
    }

    private static List<BalanceAdjustmentRecord> parseAdjustments(Object raw) {
        return parseList(raw, "adjustments", map -> BalanceAdjustmentRecord.reconstitute(
                requireUuid(map, "recordId"),
                requireUuid(map, "walletId"),
                requireMoney(map, "previousBalance"),
                requireMoney(map, "actualBalance"),
                requireDate(map, "adjustedAt"),
                getOptionalString(map, "reason"),
                requireDateTime(map, "createdAt")));
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
            if (!(item instanceof Map<?, ?> itemMap)) {
                throw new IllegalArgumentException("Phần tử trong " + fieldName + " phải là đối tượng JSON");
            }
            result.add(mapper.apply((Map<String, Object>) itemMap));
        }
        return result;
    }

    private static UUID requireUuid(Map<String, Object> map, String key) {
        return UUID.fromString(requireNonBlankString(map, key));
    }

    private static Money requireMoney(Map<String, Object> map, String key) {
        return Money.of(requireNonBlankString(map, key));
    }

    private static TransactionDate requireDate(Map<String, Object> map, String key) {
        return TransactionDate.reconstitute(LocalDate.parse(requireNonBlankString(map, key)));
    }

    private static boolean requireBoolean(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (!(value instanceof Boolean bool)) {
            throw new IllegalArgumentException("Trường '" + key + "' phải là giá trị boolean hợp lệ");
        }
        return bool;
    }

    private static String requireNonBlankString(Map<String, Object> map, String key) {
        String value = getOptionalString(map, key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Thiếu trường bắt buộc: " + key);
        }
        return value.trim();
    }

    private static String getOptionalString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (!(value instanceof String str)) {
            throw new IllegalArgumentException("Trường '" + key + "' phải là chuỗi ký tự hợp lệ");
        }
        return str;
    }

    private static LocalDateTime requireDateTime(Map<String, Object> map, String key) {
        return LocalDateTime.parse(requireNonBlankString(map, key));
    }
    // endregion
}
