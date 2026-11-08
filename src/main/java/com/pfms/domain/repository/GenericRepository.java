package com.pfms.domain.repository;

import java.util.List;
import java.util.Optional;

/**
 * Giao diện lưu trữ cơ sở định hình các thao tác dữ liệu chuẩn cho thực thể
 * nghiệp vụ.
 */
public interface GenericRepository<E, ID> {

    // region Commands
    E save(E entity);

    boolean deleteById(ID id);
    // endregion

    // region Queries
    Optional<E> findById(ID id);

    List<E> findAll();

    boolean existsById(ID id);
    // endregion
}
