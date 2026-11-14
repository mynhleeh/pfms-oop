package com.pfms.service.impl;

import com.pfms.common.util.Preconditions;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.GenericRepository;
import com.pfms.domain.repository.WalletRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Tiện ích tra cứu và xác thực thực thể miền dùng chung cho các lớp cài đặt
 * dịch vụ
 */
final class EntityLookupSupport {

    // region Constants
    static final String LABEL_WALLET = "Ví";
    static final String LABEL_CATEGORY = "Danh mục";
    static final String LABEL_NEED_GROUP = "Nhóm nhu cầu";
    static final String LABEL_FEE_CATEGORY = "Danh mục phí";
    // endregion

    // region Constructors
    private EntityLookupSupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static <T, ID> T findEntityOrThrow(
            GenericRepository<T, ID> repository,
            ID id,
            String entityLabel) {

        Objects.requireNonNull(repository, "Kho lưu trữ không được để null");
        Objects.requireNonNull(id, "Mã định danh không được để null");
        String resolvedLabel = Preconditions.requireNonBlank(
                entityLabel, "Nhãn thực thể không được để trống");
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorCode.ERR_NOT_FOUND, resolvedLabel, id.toString()));
    }

    static <T, ID> T loadActiveEntity(
            GenericRepository<T, ID> repository,
            ID id,
            String entityLabel,
            Predicate<T> activePredicate,
            ErrorCode inactiveErrorCode,
            String inactiveMessage) {

        Objects.requireNonNull(activePredicate, "Điều kiện kiểm tra trạng thái không được để null");
        T entity = findEntityOrThrow(repository, id, entityLabel);
        if (!activePredicate.test(entity)) {
            throw new BusinessException(inactiveErrorCode, inactiveMessage);
        }
        return entity;
    }

    static Wallet loadActiveWallet(WalletRepository walletRepository, UUID walletId) {
        return loadActiveEntity(
                walletRepository,
                walletId,
                LABEL_WALLET,
                Wallet::isActive,
                ErrorCode.ERR_WAL_001,
                "Ví thanh toán không ở trạng thái hoạt động.");
    }

    static Category loadActiveCategory(CategoryRepository categoryRepository, UUID categoryId) {
        return loadActiveEntity(
                categoryRepository,
                categoryId,
                LABEL_CATEGORY,
                Category::isActive,
                ErrorCode.ERR_CAT_001,
                "Danh mục không ở trạng thái hoạt động.");
    }

    static Category resolveActiveExpenseFeeCategory(
            CategoryRepository categoryRepository, Money feeAmount, UUID feeCategoryId) {

        if (feeAmount == null || !feeAmount.isPositive()) {
            return null;
        }
        if (feeCategoryId == null) {
            throw new BusinessException(ErrorCode.ERR_FEE_002);
        }
        Category feeCategory = findEntityOrThrow(categoryRepository, feeCategoryId, LABEL_FEE_CATEGORY);
        if (!feeCategory.isActive() || feeCategory.getTransactionType() != TransactionType.EXPENSE) {
            throw new BusinessException(ErrorCode.ERR_FEE_003);
        }
        return feeCategory;
    }

    static <T, ID> T resolveForDisplay(GenericRepository<T, ID> repository, ID id, T fallback) {
        if (repository == null || id == null) {
            return fallback;
        }
        return repository.findById(id).orElse(fallback);
    }

    static <T, ID> Map<ID, T> indexById(Collection<T> entities, Function<T, ID> idExtractor) {
        Objects.requireNonNull(idExtractor, "Hàm trích xuất mã định danh không được để null");
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<ID, T> indexed = new HashMap<>(entities.size());
        for (T entity : entities) {
            if (entity == null) {
                continue;
            }
            ID id = idExtractor.apply(entity);
            if (id != null) {
                indexed.put(id, entity);
            }
        }
        return Collections.unmodifiableMap(indexed);
    }
    // endregion
}