package com.pfms.domain.repository;

import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NeedGroupRepository Contract Tests")
class NeedGroupRepositoryTest {

    static class InMemoryNeedGroupRepositoryStub implements NeedGroupRepository {
        private final Map<UUID, NeedGroup> storage = new LinkedHashMap<>();

        @Override
        public NeedGroup save(NeedGroup entity) {
            Objects.requireNonNull(entity, "NeedGroup entity must not be null");
            storage.put(entity.getNeedGroupId(), entity);
            return entity;
        }

        @Override
        public Optional<NeedGroup> findById(UUID id) {
            Objects.requireNonNull(id, "NeedGroup ID must not be null");
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<NeedGroup> findAll() {
            return Collections.unmodifiableList(new ArrayList<>(storage.values()));
        }

        @Override
        public boolean existsById(UUID id) {
            Objects.requireNonNull(id, "NeedGroup ID must not be null");
            return storage.containsKey(id);
        }

        @Override
        public boolean deleteById(UUID id) {
            Objects.requireNonNull(id, "NeedGroup ID must not be null");
            return storage.remove(id) != null;
        }

        @Override
        public List<NeedGroup> findByType(TransactionType type) {
            Objects.requireNonNull(type, "TransactionType must not be null");
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
            Objects.requireNonNull(type, "TransactionType must not be null");
            return storage.values().stream()
                    .filter(g -> g.getTransactionType() == type && g.isActive())
                    .toList();
        }

        @Override
        public boolean existsByNameAndType(String name, TransactionType type) {
            Objects.requireNonNull(name, "Name must not be null");
            Objects.requireNonNull(type, "TransactionType must not be null");
            String trimmed = name.trim();
            return storage.values().stream()
                    .anyMatch(g -> g.getTransactionType() == type && g.getName().equalsIgnoreCase(trimmed));
        }

        @Override
        public boolean existsByNameAndTypeAndIdNot(String name, TransactionType type, UUID needGroupId) {
            Objects.requireNonNull(name, "Name must not be null");
            Objects.requireNonNull(type, "TransactionType must not be null");
            Objects.requireNonNull(needGroupId, "needGroupId must not be null");
            String trimmed = name.trim();
            return storage.values().stream()
                    .anyMatch(g -> !g.getNeedGroupId().equals(needGroupId)
                            && g.getTransactionType() == type
                            && g.getName().equalsIgnoreCase(trimmed));
        }

        @Override
        public Optional<NeedGroup> findByNameAndType(String name, TransactionType type) {
            Objects.requireNonNull(name, "Name must not be null");
            Objects.requireNonNull(type, "TransactionType must not be null");
            String trimmed = name.trim();
            return storage.values().stream()
                    .filter(g -> g.getTransactionType() == type && g.getName().equalsIgnoreCase(trimmed))
                    .findFirst();
        }

        @Override
        public long countActiveByType(TransactionType type) {
            Objects.requireNonNull(type, "TransactionType must not be null");
            return storage.values().stream()
                    .filter(g -> g.getTransactionType() == type && g.isActive())
                    .count();
        }
    }

    private NeedGroupRepository repository;
    private NeedGroup expenseGroup1;
    private NeedGroup expenseGroup2;
    private NeedGroup incomeGroup;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNeedGroupRepositoryStub();
        expenseGroup1 = new NeedGroup(UUID.randomUUID(), "Thiết yếu", TransactionType.EXPENSE, "icon1", "#111");
        expenseGroup2 = new NeedGroup(UUID.randomUUID(), "Giải trí", TransactionType.EXPENSE, "icon2", "#222");
        incomeGroup = new NeedGroup(UUID.randomUUID(), "Thu nhập chính", TransactionType.INCOME, "icon3", "#333");

        repository.save(expenseGroup1);
        repository.save(expenseGroup2);
        repository.save(incomeGroup);
    }

    @Nested
    @DisplayName("Group 1: Basic CRUD Queries")
    class BasicCrudTests {

        @Test
        @DisplayName("Lưu và tìm kiếm nhóm theo ID thành công")
        void testSaveAndFindById() {
            Optional<NeedGroup> found = repository.findById(expenseGroup1.getNeedGroupId());
            assertTrue(found.isPresent());
            assertEquals("Thiết yếu", found.get().getName());
        }

        @Test
        @DisplayName("Xóa nhóm theo ID")
        void testDeleteById() {
            assertTrue(repository.deleteById(expenseGroup1.getNeedGroupId()));
            assertFalse(repository.existsById(expenseGroup1.getNeedGroupId()));
        }

        @Test
        @DisplayName("findAll trả về danh sách đầy đủ")
        void testFindAll() {
            assertEquals(3, repository.findAll().size());
        }
    }

    @Nested
    @DisplayName("Group 2: Type and Status Filter Queries")
    class FilterQueriesTests {

        @Test
        @DisplayName("findByType lọc đúng theo nhánh thu/chi")
        void testFindByType() {
            List<NeedGroup> expenses = repository.findByType(TransactionType.EXPENSE);
            assertEquals(2, expenses.size());

            List<NeedGroup> incomes = repository.findByType(TransactionType.INCOME);
            assertEquals(1, incomes.size());
        }

        @Test
        @DisplayName("findActiveGroups lọc các nhóm ACTIVE")
        void testFindActiveGroups() {
            expenseGroup2.markDeleted(2);
            repository.save(expenseGroup2);

            List<NeedGroup> active = repository.findActiveGroups();
            assertEquals(2, active.size());
            assertFalse(active.contains(expenseGroup2));
        }

        @Test
        @DisplayName("countActiveByType đếm chính xác số nhóm hoạt động trong loại thu chi")
        void testCountActiveByType() {
            assertEquals(2, repository.countActiveByType(TransactionType.EXPENSE));
            assertEquals(1, repository.countActiveByType(TransactionType.INCOME));

            expenseGroup2.markDeleted(2);
            repository.save(expenseGroup2);

            assertEquals(1, repository.countActiveByType(TransactionType.EXPENSE));
        }
    }

    @Nested
    @DisplayName("Group 3: Name Uniqueness Queries")
    class NameUniquenessTests {

        @Test
        @DisplayName("existsByNameAndType kiểm tra không phân biệt hoa thường và cắt khoảng trắng")
        void testExistsByNameAndType() {
            assertTrue(repository.existsByNameAndType("  thiết yếu  ", TransactionType.EXPENSE));
            assertFalse(repository.existsByNameAndType("Thiết yếu", TransactionType.INCOME));
            assertFalse(repository.existsByNameAndType("Không tồn tại", TransactionType.EXPENSE));
        }

        @Test
        @DisplayName("existsByNameAndTypeAndIdNot bỏ qua chính nhóm đang cập nhật")
        void testExistsByNameAndTypeAndIdNot() {
            // Kiểm tra trùng tên với chính mình -> false
            assertFalse(repository.existsByNameAndTypeAndIdNot("Thiết yếu", TransactionType.EXPENSE, expenseGroup1.getNeedGroupId()));
            // Kiểm tra trùng tên với nhóm khác -> true
            assertTrue(repository.existsByNameAndTypeAndIdNot("Giải trí", TransactionType.EXPENSE, expenseGroup1.getNeedGroupId()));
        }

        @Test
        @DisplayName("findByNameAndType tìm thấy nhóm theo tên và loại thu chi")
        void testFindByNameAndType() {
            Optional<NeedGroup> found = repository.findByNameAndType("Thiết yếu", TransactionType.EXPENSE);
            assertTrue(found.isPresent());
            assertEquals(expenseGroup1.getNeedGroupId(), found.get().getNeedGroupId());
        }
    }
}

