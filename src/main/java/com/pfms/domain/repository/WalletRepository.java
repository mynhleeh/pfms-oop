package com.pfms.domain.repository;

import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.wallet.Wallet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Kho lưu trữ chuyên biệt cho thực thể Ví tiền.
 */
public interface WalletRepository extends GenericRepository<Wallet, UUID> {

    // region Queries
    Optional<Wallet> findByName(String name);

    boolean existsByName(String name);

    List<Wallet> findByStatus(EntityStatus status);

    long countByStatus(EntityStatus status);

    Optional<Wallet> findDefaultWallet();
    // endregion
}
