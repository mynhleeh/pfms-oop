package com.pfms.infrastructure.memory;

import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.repository.NeedGroupRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Kho lưu trữ Nhóm nhu cầu trong bộ nhớ RAM phục vụ môi trường kiểm thử.
 */
public class MemoryNeedGroupRepository
        extends MemoryGenericRepository<NeedGroup, UUID>
        implements NeedGroupRepository {

    // region Constructors
    public MemoryNeedGroupRepository() {
        super(NeedGroup::getNeedGroupId);
    }
    // endregion

    // region Queries
    @Override
    public List<NeedGroup> findByType(TransactionType type) {
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return storage.values().stream()
                .filter(g -> g.getTransactionType() == type)
                .toList();
    }

    @Override
    public List<NeedGroup> findActiveGroups() {
        return storage.values().stream()
                .filter(NeedGroup::isActive)
                .toList();
    }

    @Override
    public List<NeedGroup> findActiveGroupsByType(TransactionType type) {
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return storage.values().stream()
                .filter(g -> g.getTransactionType() == type && g.isActive())
                .toList();
    }

    @Override
    public boolean existsByNameAndType(String name, TransactionType type) {
        String trimmed = requireNameAndType(name, type);
        return storage.values().stream()
                .anyMatch(g -> g.isActive() && matchesNameAndType(g, trimmed, type));
    }

    @Override
    public boolean existsByNameAndTypeAndIdNot(String name, TransactionType type, UUID needGroupId) {
        String trimmed = requireNameAndType(name, type);
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        return storage.values().stream()
                .anyMatch(g -> g.isActive()
                        && !g.getNeedGroupId().equals(needGroupId)
                        && matchesNameAndType(g, trimmed, type));
    }

    @Override
    public Optional<NeedGroup> findByNameAndType(String name, TransactionType type) {
        String trimmed = requireNameAndType(name, type);
        return storage.values().stream()
                .filter(g -> matchesNameAndType(g, trimmed, type))
                .sorted(Comparator.comparing(NeedGroup::isActive).reversed())
                .findFirst();
    }

    @Override
    public long countActiveByType(TransactionType type) {
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return storage.values().stream()
                .filter(g -> g.getTransactionType() == type && g.isActive())
                .count();
    }
    // endregion

    // region Helpers
    private static String requireNameAndType(String name, TransactionType type) {
        Objects.requireNonNull(name, "Tên nhóm không được để null");
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return name.trim();
    }

    private static boolean matchesNameAndType(NeedGroup group, String trimmedName, TransactionType type) {
        return group.getTransactionType() == type && group.getName().equalsIgnoreCase(trimmedName);
    }
    // endregion
}

