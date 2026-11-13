package com.pfms.service.impl;

import com.pfms.common.util.Preconditions;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.GenericRepository;
import com.pfms.domain.repository.WalletRepository;

import java.util.Objects;
import java.util.UUID;

/**
 * Tiện ích tra cứu và xác thực thực thể miền (Ví, Danh mục, Nhóm nhu cầu) dùng
 * chung cho
 * các lớp cài đặt dịch vụ trong gói {@code com.pfms.service.impl}.
 */
final class EntityLookupSupport {

    // region Constants
    static final String LABEL_WALLET = "Ví";
    static final String LABEL_CATEGORY = "Danh mục";
    static final String LABEL_NEED_GROUP = "Nhóm nhu cầu";
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

    static Wallet loadActiveWallet(WalletRepository walletRepository, UUID walletId) {
        Wallet wallet = findEntityOrThrow(walletRepository, walletId, LABEL_WALLET);
        if (!wallet.isActive()) {
            throw new BusinessException(
                    ErrorCode.ERR_WAL_001, "Ví thanh toán không ở trạng thái hoạt động.");
        }
        return wallet;
    }
    // endregion
}