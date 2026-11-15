package com.pfms.infrastructure.file;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.infrastructure.file.support.JsonDataStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Cài đặt kho lưu trữ tệp JSON cho thực thể Danh mục ({@link CategoryRepository}).
 * Quản lý đồng bộ danh sách danh mục phân loại chi tiết qua {@link JsonDataStore} theo chuẩn OOD 4.2.3.
 */
public class JsonCategoryRepository implements CategoryRepository {

    // region State
    private final JsonDataStore dataStore;
    // endregion

    // region Constructors
    public JsonCategoryRepository(JsonDataStore dataStore) {
        this.dataStore = Objects.requireNonNull(dataStore, "JsonDataStore không được để null");
    }
    // endregion

    // region Commands
    @Override
    public Category save(Category entity) {
        Objects.requireNonNull(entity, "Thực thể danh mục không được để null");
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            List<Category> existing = current.getCategories();
            List<Category> updated = new ArrayList<>(existing.size() + 1);
            boolean replaced = false;

            for (Category c : existing) {
                if (c.getCategoryId().equals(entity.getCategoryId())) {
                    updated.add(entity);
                    replaced = true;
                } else {
                    updated.add(c);
                }
            }

            if (!replaced) {
                updated.add(entity);
            }

            BackupPayload next = new BackupPayload(
                    current.getWallets(),
                    current.getNeedGroups(),
                    updated,
                    current.getTransactions(),
                    current.getAdjustments());
            dataStore.save(next);
            return entity;
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "Mã danh mục không được để null");
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            List<Category> existing = current.getCategories();
            List<Category> updated = new ArrayList<>(existing.size());
            boolean removed = false;

            for (Category c : existing) {
                if (c.getCategoryId().equals(id)) {
                    removed = true;
                } else {
                    updated.add(c);
                }
            }

            if (removed) {
                BackupPayload next = new BackupPayload(
                        current.getWallets(),
                        current.getNeedGroups(),
                        updated,
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
                    current.getNeedGroups(),
                    Collections.emptyList(),
                    current.getTransactions(),
                    current.getAdjustments());
            dataStore.save(next);
        }
    }
    // endregion

    // region Queries
    @Override
    public Optional<Category> findById(UUID id) {
        Objects.requireNonNull(id, "Mã danh mục không được để null");
        return dataStore.getPayload().getCategories().stream()
                .filter(c -> c.getCategoryId().equals(id))
                .findFirst();
    }

    @Override
    public List<Category> findAll() {
        return dataStore.getPayload().getCategories();
    }

    @Override
    public boolean existsById(UUID id) {
        Objects.requireNonNull(id, "Mã danh mục không được để null");
        return dataStore.getPayload().getCategories().stream()
                .anyMatch(c -> c.getCategoryId().equals(id));
    }

    @Override
    public List<Category> findByType(TransactionType type) {
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return dataStore.getPayload().getCategories().stream()
                .filter(c -> c.getTransactionType() == type)
                .toList();
    }

    @Override
    public List<Category> findByNeedGroupId(UUID needGroupId) {
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        return dataStore.getPayload().getCategories().stream()
                .filter(c -> c.getNeedGroupId().equals(needGroupId))
                .toList();
    }

    @Override
    public boolean existsByNameAndNeedGroupId(String name, UUID needGroupId) {
        String trimmed = requireNameAndNeedGroupId(name, needGroupId);
        return dataStore.getPayload().getCategories().stream()
                .anyMatch(c -> c.getNeedGroupId().equals(needGroupId)
                        && c.isActive()
                        && c.getName().equalsIgnoreCase(trimmed));
    }

    @Override
    public boolean existsByNameAndNeedGroupIdAndIdNot(String name, UUID needGroupId, UUID categoryId) {
        String trimmed = requireNameAndNeedGroupId(name, needGroupId);
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        return dataStore.getPayload().getCategories().stream()
                .anyMatch(c -> !c.getCategoryId().equals(categoryId)
                        && c.getNeedGroupId().equals(needGroupId)
                        && c.isActive()
                        && c.getName().equalsIgnoreCase(trimmed));
    }

    @Override
    public Optional<Category> findByNameAndNeedGroupId(String name, UUID needGroupId) {
        String trimmed = requireNameAndNeedGroupId(name, needGroupId);
        return dataStore.getPayload().getCategories().stream()
                .filter(c -> c.getNeedGroupId().equals(needGroupId)
                        && c.getName().equalsIgnoreCase(trimmed))
                .sorted(Comparator.comparing(Category::isActive).reversed())
                .findFirst();
    }

    @Override
    public long countActiveByNeedGroupId(UUID needGroupId) {
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        return dataStore.getPayload().getCategories().stream()
                .filter(c -> c.getNeedGroupId().equals(needGroupId) && c.isActive())
                .count();
    }

    @Override
    public List<Category> findActiveCategories() {
        return dataStore.getPayload().getCategories().stream()
                .filter(Category::isActive)
                .toList();
    }
    // endregion

    // region Helpers
    private static String requireNameAndNeedGroupId(String name, UUID needGroupId) {
        Objects.requireNonNull(name, "Tên danh mục không được để null");
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        return name.trim();
    }
    // endregion
}
