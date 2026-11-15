package com.pfms.infrastructure.file;

import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.infrastructure.file.support.JsonDataStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Cài đặt kho lưu trữ tệp JSON cho thực thể Nhóm nhu cầu ({@link NeedGroupRepository}).
 * Quản lý đồng bộ danh sách nhóm nhu cầu bền vững qua {@link JsonDataStore} theo chuẩn OOD 4.2.3.
 */
public class JsonNeedGroupRepository implements NeedGroupRepository {

    // region State
    private final JsonDataStore dataStore;
    // endregion

    // region Constructors
    public JsonNeedGroupRepository(JsonDataStore dataStore) {
        this.dataStore = Objects.requireNonNull(dataStore, "JsonDataStore không được để null");
    }
    // endregion

    // region Commands
    @Override
    public NeedGroup save(NeedGroup entity) {
        Objects.requireNonNull(entity, "Thực thể nhóm nhu cầu không được để null");
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            List<NeedGroup> existing = current.getNeedGroups();
            List<NeedGroup> updated = new ArrayList<>(existing.size() + 1);
            boolean replaced = false;

            for (NeedGroup g : existing) {
                if (g.getNeedGroupId().equals(entity.getNeedGroupId())) {
                    updated.add(entity);
                    replaced = true;
                } else {
                    updated.add(g);
                }
            }

            if (!replaced) {
                updated.add(entity);
            }

            BackupPayload next = new BackupPayload(
                    current.getWallets(),
                    updated,
                    current.getCategories(),
                    current.getTransactions(),
                    current.getAdjustments());
            dataStore.save(next);
            return entity;
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "Mã nhóm nhu cầu không được để null");
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            List<NeedGroup> existing = current.getNeedGroups();
            List<NeedGroup> updated = new ArrayList<>(existing.size());
            boolean removed = false;

            for (NeedGroup g : existing) {
                if (g.getNeedGroupId().equals(id)) {
                    removed = true;
                } else {
                    updated.add(g);
                }
            }

            if (removed) {
                BackupPayload next = new BackupPayload(
                        current.getWallets(),
                        updated,
                        current.getCategories(),
                        current.getTransactions(),
                        current.getAdjustments());
                dataStore.save(next);
            }
            return removed;
        }
    }

    @Override
    public void deleteAll() {
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            BackupPayload next = new BackupPayload(
                    current.getWallets(),
                    Collections.emptyList(),
                    current.getCategories(),
                    current.getTransactions(),
                    current.getAdjustments());
            dataStore.save(next);
        }
    }
    // endregion

    // region Queries
    @Override
    public Optional<NeedGroup> findById(UUID id) {
        Objects.requireNonNull(id, "Mã nhóm nhu cầu không được để null");
        return dataStore.getPayload().getNeedGroups().stream()
                .filter(g -> g.getNeedGroupId().equals(id))
                .findFirst();
    }

    @Override
    public List<NeedGroup> findAll() {
        return dataStore.getPayload().getNeedGroups();
    }

    @Override
    public boolean existsById(UUID id) {
        Objects.requireNonNull(id, "Mã nhóm nhu cầu không được để null");
        return dataStore.getPayload().getNeedGroups().stream()
                .anyMatch(g -> g.getNeedGroupId().equals(id));
    }

    @Override
    public List<NeedGroup> findByType(TransactionType type) {
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return dataStore.getPayload().getNeedGroups().stream()
                .filter(g -> g.getTransactionType() == type)
                .toList();
    }

    @Override
    public List<NeedGroup> findActiveGroups() {
        return dataStore.getPayload().getNeedGroups().stream()
                .filter(NeedGroup::isActive)
                .toList();
    }

    @Override
    public List<NeedGroup> findActiveGroupsByType(TransactionType type) {
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return dataStore.getPayload().getNeedGroups().stream()
                .filter(g -> g.getTransactionType() == type && g.isActive())
                .toList();
    }

    @Override
    public boolean existsByNameAndType(String name, TransactionType type) {
        String trimmed = requireNameAndType(name, type);
        return dataStore.getPayload().getNeedGroups().stream()
                .anyMatch(g -> g.isActive() && matchesNameAndType(g, trimmed, type));
    }

    @Override
    public boolean existsByNameAndTypeAndIdNot(String name, TransactionType type, UUID needGroupId) {
        String trimmed = requireNameAndType(name, type);
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        return dataStore.getPayload().getNeedGroups().stream()
                .anyMatch(g -> g.isActive()
                        && !g.getNeedGroupId().equals(needGroupId)
                        && matchesNameAndType(g, trimmed, type));
    }

    @Override
    public Optional<NeedGroup> findByNameAndType(String name, TransactionType type) {
        String trimmed = requireNameAndType(name, type);
        return dataStore.getPayload().getNeedGroups().stream()
                .filter(g -> matchesNameAndType(g, trimmed, type))
                .sorted(Comparator.comparing(NeedGroup::isActive).reversed())
                .findFirst();
    }

    @Override
    public long countActiveByType(TransactionType type) {
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return dataStore.getPayload().getNeedGroups().stream()
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
