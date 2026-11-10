package com.pfms.domain.repository;

import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.model.wallet.WalletStatus;

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

    List<Wallet> findByStatus(WalletStatus status);

    long countByStatus(WalletStatus status);

    Optional<Wallet> findDefaultWallet();
    // endregion
}
