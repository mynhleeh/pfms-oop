package com.pfms.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GenericRepository Tests")
class GenericRepositoryTest {

    static class DummyEntity {
        private final UUID id;
        private final String name;

        DummyEntity(UUID id, String name) {
            this.id = Objects.requireNonNull(id, "ID must not be null");
            this.name = Objects.requireNonNull(name, "Name must not be null");
        }

        UUID getId() {
            return this.id;
        }

        String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DummyEntity that = (DummyEntity) o;
            return Objects.equals(id, that.id) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    static class InMemoryDummyRepository implements GenericRepository<DummyEntity, UUID> {
        private final Map<UUID, DummyEntity> storage = new LinkedHashMap<>();

        @Override
        public DummyEntity save(DummyEntity entity) {
            if (entity == null) {
                throw new IllegalArgumentException("Entity must not be null");
            }
            storage.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<DummyEntity> findById(UUID id) {
            if (id == null) {
                throw new IllegalArgumentException("ID must not be null");
            }
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<DummyEntity> findAll() {
            return Collections.unmodifiableList(new ArrayList<>(storage.values()));
        }

        @Override
        public boolean existsById(UUID id) {
            if (id == null) {
                throw new IllegalArgumentException("ID must not be null");
            }
            return storage.containsKey(id);
        }

        @Override
        public boolean deleteById(UUID id) {
            if (id == null) {
                throw new IllegalArgumentException("ID must not be null");
            }
            return storage.remove(id) != null;
        }
    }

    private InMemoryDummyRepository repository;
    private DummyEntity entity1;
    private DummyEntity entity2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDummyRepository();
        entity1 = new DummyEntity(UUID.randomUUID(), "Ví Tiền Mặt");
        entity2 = new DummyEntity(UUID.randomUUID(), "Tài Khoản Ngân Hàng");
    }

    @Nested
    @DisplayName("Tests for save(E entity)")
    class SaveTests {

        @Test
        @DisplayName("Should successfully save new entity and return it")
        void shouldSaveNewEntity() {
            DummyEntity saved = repository.save(entity1);

            assertNotNull(saved);
            assertEquals(entity1, saved);
            assertEquals(1, repository.findAll().size());
        }

        @Test
        @DisplayName("Should update existing entity when same ID is saved")
        void shouldUpdateExistingEntity() {
            repository.save(entity1);

            DummyEntity updated = new DummyEntity(entity1.getId(), "Ví Cập Nhật");
            DummyEntity result = repository.save(updated);

            assertEquals(updated, result);
            assertEquals(1, repository.findAll().size());
            assertEquals("Ví Cập Nhật", repository.findById(entity1.getId()).get().getName());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when entity is null")
        void shouldThrowExceptionWhenEntityIsNull() {
            assertThrows(IllegalArgumentException.class, () -> repository.save(null));
        }
    }

    @Nested
    @DisplayName("Tests for findById(ID id)")
    class FindByIdTests {

        @Test
        @DisplayName("Should return Optional containing entity when ID exists")
        void shouldReturnEntityWhenIdExists() {
            repository.save(entity1);

            Optional<DummyEntity> found = repository.findById(entity1.getId());

            assertTrue(found.isPresent());
            assertEquals(entity1, found.get());
        }

        @Test
        @DisplayName("Should return Optional.empty() when ID does not exist")
        void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
            Optional<DummyEntity> found = repository.findById(UUID.randomUUID());

            assertNotNull(found);
            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when id is null")
        void shouldThrowExceptionWhenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
        }
    }

    @Nested
    @DisplayName("Tests for findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Should return empty list when repository has no data")
        void shouldReturnEmptyListWhenEmpty() {
            List<DummyEntity> all = repository.findAll();

            assertNotNull(all);
            assertTrue(all.isEmpty());
        }

        @Test
        @DisplayName("Should return all saved entities preserving storage count")
        void shouldReturnAllSavedEntities() {
            repository.save(entity1);
            repository.save(entity2);

            List<DummyEntity> all = repository.findAll();

            assertEquals(2, all.size());
            assertTrue(all.contains(entity1));
            assertTrue(all.contains(entity2));
        }

        @Test
        @DisplayName("Should return unmodifiable list throwing exception on mutation attempts")
        void shouldReturnUnmodifiableList() {
            repository.save(entity1);
            List<DummyEntity> all = repository.findAll();

            assertThrows(UnsupportedOperationException.class, all::clear);
            assertThrows(UnsupportedOperationException.class, () -> all.add(entity2));
        }
    }

    @Nested
    @DisplayName("Tests for existsById(ID id)")
    class ExistsByIdTests {

        @Test
        @DisplayName("Should return true when entity with ID exists")
        void shouldReturnTrueWhenEntityExists() {
            repository.save(entity1);

            assertTrue(repository.existsById(entity1.getId()));
        }

        @Test
        @DisplayName("Should return false when entity with ID does not exist")
        void shouldReturnFalseWhenEntityDoesNotExist() {
            assertFalse(repository.existsById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> repository.existsById(null));
        }
    }

    @Nested
    @DisplayName("Tests for deleteById(ID id)")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete entity and return true when ID exists")
        void shouldDeleteEntityWhenIdExists() {
            repository.save(entity1);

            boolean deleted = repository.deleteById(entity1.getId());

            assertTrue(deleted);
            assertFalse(repository.existsById(entity1.getId()));
            assertTrue(repository.findById(entity1.getId()).isEmpty());
        }

        @Test
        @DisplayName("Should return false when ID to delete does not exist")
        void shouldReturnFalseWhenIdDoesNotExist() {
            boolean deleted = repository.deleteById(UUID.randomUUID());

            assertFalse(deleted);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> repository.deleteById(null));
        }
    }
}

