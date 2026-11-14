package com.pfms.domain.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Giao diện lưu trữ cơ sở định hình các thao tác dữ liệu chuẩn cho thực thể
 * nghiệp vụ.
 */
public interface GenericRepository<E, ID> {

    // region Commands
    E save(E entity);

    default List<E> saveAll(Iterable<E> entities) {
        Objects.requireNonNull(entities, "Tập hợp thực thể không được để null");
        List<E> savedList = new ArrayList<>();
        for (E entity : entities) {
            savedList.add(save(entity));
        }
        return Collections.unmodifiableList(savedList);
    }

    boolean deleteById(ID id);

    default void deleteAll() {
        throw new UnsupportedOperationException("Kho lưu trữ chưa hỗ trợ thao tác deleteAll");
    }
    // endregion

    // region Queries
    Optional<E> findById(ID id);

    List<E> findAll();

    boolean existsById(ID id);
    // endregion
}
