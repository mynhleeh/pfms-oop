package com.pfms.infrastructure.memory;

import com.pfms.domain.repository.GenericRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Kho lưu trữ dữ liệu trong bộ nhớ RAM phục vụ môi trường kiểm thử tự động.
 * Quản lý các thao tác CRUD cơ sở trên cấu trúc bảng băm an toàn luồng
 * ConcurrentHashMap.
 */
public abstract class MemoryGenericRepository<E, ID> implements GenericRepository<E, ID> {

    // region State
    protected final ConcurrentHashMap<ID, E> storage = new ConcurrentHashMap<>();
    protected final Function<E, ID> idExtractor;
    // endregion

    // region Constructors
    protected MemoryGenericRepository(Function<E, ID> idExtractor) {
        this.idExtractor = Objects.requireNonNull(idExtractor, "idExtractor must not be null");
    }
    // endregion

    // region Commands
    @Override
    public E save(E entity) {
        Objects.requireNonNull(entity, "Entity must not be null");
        ID id = Objects.requireNonNull(getId(entity), "Entity ID must not be null");
        storage.put(id, entity);
        return entity;
    }

    @Override
    public boolean deleteById(ID id) {
        Objects.requireNonNull(id, "ID must not be null");
        return storage.remove(id) != null;
    }
    // endregion

    // region Queries
    @Override
    public Optional<E> findById(ID id) {
        Objects.requireNonNull(id, "ID must not be null");
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<E> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public boolean existsById(ID id) {
        Objects.requireNonNull(id, "ID must not be null");
        return storage.containsKey(id);
    }
    // endregion

    // region Testing Support
    public void clear() {
        storage.clear();
    }
    // endregion

    // region Helpers
    protected ID getId(E entity) {
        return this.idExtractor.apply(entity);
    }
    // endregion
}
