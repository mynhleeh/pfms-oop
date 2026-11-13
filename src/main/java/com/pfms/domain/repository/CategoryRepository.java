package com.pfms.domain.repository;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.transaction.TransactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Kho lưu trữ thực thể Danh mục ({@link Category}).
 * Hỗ trợ truy vấn theo loại thu chi, nhóm nhu cầu, tên và trạng thái hoạt động.
 */
public interface CategoryRepository extends GenericRepository<Category, UUID> {

    // region Queries
    List<Category> findByType(TransactionType type);

    List<Category> findByNeedGroupId(UUID needGroupId);

    boolean existsByNameAndNeedGroupId(String name, UUID needGroupId);

    boolean existsByNameAndNeedGroupIdAndIdNot(String name, UUID needGroupId, UUID categoryId);

    Optional<Category> findByNameAndNeedGroupId(String name, UUID needGroupId);

    long countActiveByNeedGroupId(UUID needGroupId);

    List<Category> findActiveCategories();
    // endregion
}