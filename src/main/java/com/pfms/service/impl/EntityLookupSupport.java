package com.pfms.service.impl;

import com.pfms.common.util.Preconditions;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.WalletRepository;

import java.util.Objects;
import java.util.UUID;

/**
 * Tiện ích tra cứu và xác thực thực thể miền (Ví, Danh mục) dùng chung cho
 * các lớp cài đặt dịch vụ trong gói {@code com.pfms.service.impl}.
 */
final class EntityLookupSupport {

    // region Constructors
    private EntityLookupSupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static Wallet findWalletOrThrow(WalletRepository walletRepository, UUID walletId) {
        Objects.requireNonNull(walletRepository, "WalletRepository không được để null");
        Objects.requireNonNull(walletId, "Mã ví không được để null");
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorCode.ERR_NOT_FOUND, "Ví", walletId.toString()));
    }

    static Wallet loadActiveWallet(WalletRepository walletRepository, UUID walletId) {
        Wallet wallet = findWalletOrThrow(walletRepository, walletId);
        if (!wallet.isActive()) {
            throw new BusinessException(
                    ErrorCode.ERR_WAL_001, "Ví thanh toán không ở trạng thái hoạt động.");
        }
        return wallet;
    }

    static Category findCategoryOrThrow(CategoryRepository categoryRepository, UUID categoryId) {
        return findCategoryOrThrow(categoryRepository, categoryId, "Danh mục");
    }

    static Category findCategoryOrThrow(
            CategoryRepository categoryRepository,
            UUID categoryId,
            String entityLabel) {

        Objects.requireNonNull(categoryRepository, "CategoryRepository không được để null");
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        String resolvedLabel = Preconditions.requireNonBlank(
                entityLabel, "Nhãn thực thể danh mục không được để trống");
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorCode.ERR_NOT_FOUND, resolvedLabel, categoryId.toString()));
    }
    // endregion
}

