package com.pfms.infrastructure.memory;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.repository.CategoryRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Kho lưu trữ Danh mục trong bộ nhớ RAM phục vụ môi trường kiểm thử.
 */
public class MemoryCategoryRepository
        extends MemoryGenericRepository<Category, UUID>
        implements CategoryRepository {

    // region Constructors
    public MemoryCategoryRepository() {
        super(Category::getCategoryId);
    }
    // endregion

    // region Queries
    @Override
    public List<Category> findByType(TransactionType type) {
        Objects.requireNonNull(type, "Loại thu chi không được để null");
        return storage.values().stream()
                .filter(c -> c.getTransactionType() == type)
                .toList();
    }

    @Override
    public List<Category> findByNeedGroupId(UUID needGroupId) {
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        return storage.values().stream()
                .filter(c -> c.getNeedGroupId().equals(needGroupId))
                .toList();
    }

    @Override
    public boolean existsByNameAndNeedGroupId(String name, UUID needGroupId) {
        Objects.requireNonNull(name, "Tên danh mục không được để null");
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        String trimmed = name.trim();
        return storage.values().stream()
                .anyMatch(c -> c.getNeedGroupId().equals(needGroupId)
                        && c.isActive()
                        && c.getName().equalsIgnoreCase(trimmed));
    }

    @Override
    public boolean existsByNameAndNeedGroupIdAndIdNot(String name, UUID needGroupId, UUID categoryId) {
        Objects.requireNonNull(name, "Tên danh mục không được để null");
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        String trimmed = name.trim();
        return storage.values().stream()
                .anyMatch(c -> !c.getCategoryId().equals(categoryId)
                        && c.getNeedGroupId().equals(needGroupId)
                        && c.isActive()
                        && c.getName().equalsIgnoreCase(trimmed));
    }

    @Override
    public Optional<Category> findByNameAndNeedGroupId(String name, UUID needGroupId) {
        Objects.requireNonNull(name, "Tên danh mục không được để null");
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        String trimmed = name.trim();
        return storage.values().stream()
                .filter(c -> c.getNeedGroupId().equals(needGroupId)
                        && c.getName().equalsIgnoreCase(trimmed))
                .sorted(Comparator.comparing(Category::isActive).reversed())
                .findFirst();
    }

    @Override
    public long countActiveByNeedGroupId(UUID needGroupId) {
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        return storage.values().stream()
                .filter(c -> c.getNeedGroupId().equals(needGroupId) && c.isActive())
                .count();
    }

    @Override
    public List<Category> findActiveCategories() {
        return storage.values().stream()
                .filter(Category::isActive)
                .toList();
    }
    // endregion
}

