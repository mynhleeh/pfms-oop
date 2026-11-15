package com.pfms.infrastructure.file.support;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.data.BackupPayload;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Bộ giải mã chuỗi JSON sao lưu hệ thống sang cấu trúc {@link BackupPayload}.
 */
public final class JsonEntityDeserializer {

    // region Constants
    private static final Gson GSON = new Gson();
    // endregion

    // region Constructors
    private JsonEntityDeserializer() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    @SuppressWarnings("unchecked")
    public static BackupPayload fromJson(String json) {
        Objects.requireNonNull(json, "Chuỗi JSON không được để null");
        if (json.trim().isEmpty()) {
            throw DataCorruptionException.syntaxError(
                    "backup.json", 1, new IllegalArgumentException("Nội dung tệp JSON rỗng"));
        }

        try {
            JsonElement element = JsonParser.parseString(json);
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("JSON gốc phải là một đối tượng");
            }
            Map<String, Object> root = GSON.fromJson(element, Map.class);

            List<Wallet> wallets = parseWallets(root.get("wallets"));
            List<NeedGroup> needGroups = parseNeedGroups(root.get("needGroups"));
            List<Category> categories = parseCategories(root.get("categories"));
            Map<UUID, Wallet> walletMap = indexById(wallets, Wallet::getId);
            Map<UUID, Category> categoryMap = indexById(categories, Category::getCategoryId);

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
    private static <T, ID> Map<ID, T> indexById(List<T> items, Function<T, ID> idExtractor) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<ID, T> map = new HashMap<>(items.size());
        for (T item : items) {
            if (item != null) {
                ID id = idExtractor.apply(item);
                if (id != null) {
                    map.put(id, item);
                }
            }
        }
        return Collections.unmodifiableMap(map);
    }

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
        List<Transaction> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof Map<?, ?> map)) {
                throw new IllegalArgumentException("Mỗi giao dịch phải là đối tượng JSON");
            }
            Map<String, Object> m = (Map<String, Object>) map;
            String recordType = requireNonBlankString(m, "recordType");
            result.add(switch (recordType) {
                case "INCOME_EXPENSE" -> parseIncomeExpense(m, walletMap, categoryMap);
                case "TRANSFER" -> parseTransfer(m, walletMap, categoryMap);
                default -> throw new IllegalArgumentException("recordType không hợp lệ: " + recordType);
            });
        }
        return result;
    }

    private static IncomeExpenseTransaction parseIncomeExpense(
            Map<String, Object> m, Map<UUID, Wallet> walletMap, Map<UUID, Category> categoryMap) {
        UUID walletId = requireUuid(m, "walletId");
        UUID categoryId = requireUuid(m, "categoryId");
        TransactionType type = TransactionType.fromString(requireNonBlankString(m, "transactionType"));

        Wallet wallet = resolveWallet(walletMap, walletId, "Ví tạm");
        Category category = resolveCategory(categoryMap, categoryId, "Danh mục tạm", type);
        TransactionFee fee = parseFeeIfPresent(m.get("fee"), walletMap, categoryMap);

        return IncomeExpenseTransaction.reconstitute(
                requireUuid(m, "transactionId"),
                type,
                wallet,
                category,
                requireMoney(m, "amount"),
                requireDate(m, "transactionDate"),
                getOptionalString(m, "note"),
                fee,
                requireDateTime(m, "createdAt"));
    }

    private static TransferTransaction parseTransfer(
            Map<String, Object> m, Map<UUID, Wallet> walletMap, Map<UUID, Category> categoryMap) {
        UUID srcId = requireUuid(m, "sourceWalletId");
        UUID dstId = requireUuid(m, "destinationWalletId");

        Wallet src = resolveWallet(walletMap, srcId, "Ví nguồn tạm");
        Wallet dst = resolveWallet(walletMap, dstId, "Ví đích tạm");
        TransactionFee fee = parseFeeIfPresent(m.get("fee"), walletMap, categoryMap);

        return TransferTransaction.reconstitute(
                requireUuid(m, "transactionId"),
                src,
                dst,
                requireMoney(m, "amount"),
                requireDate(m, "transactionDate"),
                getOptionalString(m, "note"),
                fee,
                requireDateTime(m, "createdAt"));
    }

    @SuppressWarnings("unchecked")
    private static TransactionFee parseFeeIfPresent(
            Object feeRaw, Map<UUID, Wallet> walletMap, Map<UUID, Category> categoryMap) {
        if (feeRaw == null) {
            return null;
        }
        if (!(feeRaw instanceof Map<?, ?> feeMap)) {
            throw new IllegalArgumentException("fee phải là một đối tượng JSON");
        }
        Map<String, Object> m = (Map<String, Object>) feeMap;
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
