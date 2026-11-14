package com.pfms.domain.repository;

import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.transaction.TransactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Kho lưu trữ thực thể Nhóm nhu cầu ({@link NeedGroup}).
 * Hỗ trợ truy vấn theo loại thu chi, tên và trạng thái hoạt động.
 */
public interface NeedGroupRepository extends GenericRepository<NeedGroup, UUID> {

    // region Queries
    List<NeedGroup> findByType(TransactionType type);

    List<NeedGroup> findActiveGroups();

    List<NeedGroup> findActiveGroupsByType(TransactionType type);

    boolean existsByNameAndType(String name, TransactionType type);

    boolean existsByNameAndTypeAndIdNot(String name, TransactionType type, UUID needGroupId);

    Optional<NeedGroup> findByNameAndType(String name, TransactionType type);

    long countActiveByType(TransactionType type);
    // endregion
}

