package com.pfms.infrastructure.memory;

import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.model.wallet.WalletStatus;
import com.pfms.domain.repository.WalletRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Kho lưu trữ Ví tiền trong bộ nhớ RAM phục vụ môi trường kiểm thử.
 */
public class MemoryWalletRepository
        extends MemoryGenericRepository<Wallet, UUID>
        implements WalletRepository {

    // region Constructors
    public MemoryWalletRepository() {
        super(Wallet::getId);
    }
    // endregion

    // region Queries
    @Override
    public Optional<Wallet> findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Tên ví không được để null");
        }
        String trimmed = name.trim();
        return storage.values().stream()
                .filter(w -> w.getName().equalsIgnoreCase(trimmed))
                .findFirst();
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Tên ví không được để null");
        }
        String trimmed = name.trim();
        return storage.values().stream()
                .anyMatch(w -> w.getName().equalsIgnoreCase(trimmed));
    }

    @Override
    public List<Wallet> findByStatus(WalletStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái ví không được để null");
        }
        return storage.values().stream()
                .filter(w -> w.getStatus() == status)
                .toList();
    }

    @Override
    public long countByStatus(WalletStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái ví không được để null");
        }
        return storage.values().stream()
                .filter(w -> w.getStatus() == status)
                .count();
    }

    @Override
    public Optional<Wallet> findDefaultWallet() {
        return storage.values().stream()
                .filter(Wallet::isDefaultWallet)
                .findFirst();
    }
    // endregion
}
