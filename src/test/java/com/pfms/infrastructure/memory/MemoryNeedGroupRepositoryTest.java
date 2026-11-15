package com.pfms.infrastructure.memory;

import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemoryNeedGroupRepository Unit Tests")
class MemoryNeedGroupRepositoryTest {

    private MemoryNeedGroupRepository repository;
    private UUID groupId1;
    private UUID groupId2;
    private UUID incomeGroupId;
    private NeedGroup expenseGroup1;
    private NeedGroup expenseGroup2;
    private NeedGroup incomeGroup;

    @BeforeEach
    void setUp() {
        repository = new MemoryNeedGroupRepository();
        groupId1 = UUID.randomUUID();
        groupId2 = UUID.randomUUID();
        incomeGroupId = UUID.randomUUID();

        expenseGroup1 = new NeedGroup(groupId1, "Thiết yếu", TransactionType.EXPENSE, "icon1", "#111");
        expenseGroup2 = new NeedGroup(groupId2, "Hưởng thụ", TransactionType.EXPENSE, "icon2", "#222");
        incomeGroup = new NeedGroup(incomeGroupId, "Thu nhập chính", TransactionType.INCOME, "icon3", "#333");

        repository.save(expenseGroup1);
        repository.save(expenseGroup2);
        repository.save(incomeGroup);
    }

    @Nested
    @DisplayName("Group 1: Basic CRUD Operations")
    class CrudTests {

        @Test
        @DisplayName("Lưu và tìm kiếm theo ID")
        void testSaveAndFindById() {
            Optional<NeedGroup> found = repository.findById(groupId1);
            assertTrue(found.isPresent());
            assertEquals("Thiết yếu", found.get().getName());
        }

        @Test
        @DisplayName("Xóa nhóm theo ID")
        void testDeleteById() {
            assertTrue(repository.deleteById(groupId1));
            assertFalse(repository.existsById(groupId1));
            assertFalse(repository.deleteById(groupId1)); // Xóa lần 2 trả về false
        }

        @Test
        @DisplayName("findAll trả về toàn bộ các phần tử")
        void testFindAll() {
            assertEquals(3, repository.findAll().size());
        }
    }

    @Nested
    @DisplayName("Group 2: Queries and Filters")
    class QueryFilterTests {

        @Test
        @DisplayName("Lọc theo nhánh thu chi findByType")
        void testFindByType() {
            List<NeedGroup> expenses = repository.findByType(TransactionType.EXPENSE);
            assertEquals(2, expenses.size());

            List<NeedGroup> incomes = repository.findByType(TransactionType.INCOME);
            assertEquals(1, incomes.size());
        }

        @Test
        @DisplayName("Lọc các nhóm hoạt động findActiveGroups")
        void testFindActiveGroups() {
            expenseGroup2.markDeleted(2);
            repository.save(expenseGroup2);

            List<NeedGroup> active = repository.findActiveGroups();
            assertEquals(2, active.size());
            assertFalse(active.contains(expenseGroup2));
        }

        @Test
        @DisplayName("Lọc các nhóm hoạt động theo loại thu chi findActiveGroupsByType")
        void testFindActiveGroupsByType() {
            expenseGroup2.markDeleted(2);
            repository.save(expenseGroup2);

            List<NeedGroup> activeExpenses = repository.findActiveGroupsByType(TransactionType.EXPENSE);
            assertEquals(1, activeExpenses.size());
            assertEquals("Thiết yếu", activeExpenses.get(0).getName());
        }

        @Test
        @DisplayName("Đếm số lượng nhóm hoạt động countActiveByType")
        void testCountActiveByType() {
            assertEquals(2, repository.countActiveByType(TransactionType.EXPENSE));
            assertEquals(1, repository.countActiveByType(TransactionType.INCOME));

            expenseGroup2.markDeleted(2);
            repository.save(expenseGroup2);

            assertEquals(1, repository.countActiveByType(TransactionType.EXPENSE));
        }
    }

    @Nested
    @DisplayName("Group 3: Name Uniqueness and Trimming")
    class NameUniquenessTests {

        @Test
        @DisplayName("Kiểm tra tồn tại tên không phân biệt hoa thường và cắt khoảng trắng")
        void testExistsByNameAndType() {
            assertTrue(repository.existsByNameAndType("  thiết yếu  ", TransactionType.EXPENSE));
            assertFalse(repository.existsByNameAndType("Thiết yếu", TransactionType.INCOME));
            assertFalse(repository.existsByNameAndType("Chưa có", TransactionType.EXPENSE));
        }

        @Test
        @DisplayName("existsByNameAndType bỏ qua nhóm đã xóa mềm (DELETED)")
        void testExistsByNameAndTypeIgnoresSoftDeletedGroup() {
            expenseGroup1.markDeleted(2);
            repository.save(expenseGroup1);

            assertFalse(repository.existsByNameAndType("  thiết yếu  ", TransactionType.EXPENSE));
        }

        @Test
        @DisplayName("existsByNameAndTypeAndIdNot bỏ qua chính ID của nhóm và nhóm đã xóa mềm")
        void testExistsByNameAndTypeAndIdNot() {
            assertFalse(repository.existsByNameAndTypeAndIdNot("Thiết yếu", TransactionType.EXPENSE, groupId1));
            assertTrue(repository.existsByNameAndTypeAndIdNot("Hưởng thụ", TransactionType.EXPENSE, groupId1));

            expenseGroup2.markDeleted(2);
            repository.save(expenseGroup2);
            assertFalse(repository.existsByNameAndTypeAndIdNot("  Hưởng thụ  ", TransactionType.EXPENSE, groupId1));
        }

        @Test
        @DisplayName("findByNameAndType tìm thấy nhóm theo tên và loại")
        void testFindByNameAndType() {
            Optional<NeedGroup> found = repository.findByNameAndType("Thiết yếu", TransactionType.EXPENSE);
            assertTrue(found.isPresent());
            assertEquals(groupId1, found.get().getNeedGroupId());
        }

        @Test
        @DisplayName("findByNameAndType ưu tiên nhóm ACTIVE khi có nhóm DELETED trùng tên")
        void testFindByNameAndTypePrefersActiveOverDeleted() {
            expenseGroup1.markDeleted(2);
            repository.save(expenseGroup1);

            NeedGroup deletedOnly = repository.findByNameAndType("  thiết yếu  ", TransactionType.EXPENSE).orElseThrow();
            assertTrue(deletedOnly.isDeleted());

            for (int i = 0; i < 5; i++) {
                String name = "Nhóm trùng tên " + i;
                NeedGroup deletedDup = new NeedGroup(UUID.randomUUID(), name, TransactionType.EXPENSE, "icon", "#111");
                deletedDup.markDeleted(2);
                NeedGroup activeDup = new NeedGroup(UUID.randomUUID(), name, TransactionType.EXPENSE, "icon", "#222");
                repository.save(deletedDup);
                repository.save(activeDup);

                NeedGroup resolved = repository.findByNameAndType("  " + name.toLowerCase() + " ", TransactionType.EXPENSE)
                        .orElseThrow();
                assertEquals(activeDup.getNeedGroupId(), resolved.getNeedGroupId());
                assertTrue(resolved.isActive());
            }
        }
    }

    @Nested
    @DisplayName("Group 4: Null Handling and Validation")
    class ValidationTests {

        @Test
        @DisplayName("Từ chối tham số null")
        void testNullChecks() {
            assertThrows(NullPointerException.class, () -> repository.findByType(null));
            assertThrows(NullPointerException.class, () -> repository.findActiveGroupsByType(null));
            assertThrows(NullPointerException.class, () -> repository.countActiveByType(null));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndType(null, TransactionType.EXPENSE));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndType("A", null));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndTypeAndIdNot(null, TransactionType.EXPENSE, groupId1));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndTypeAndIdNot("A", null, groupId1));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndTypeAndIdNot("A", TransactionType.EXPENSE, null));
            assertThrows(NullPointerException.class, () -> repository.findByNameAndType(null, TransactionType.EXPENSE));
            assertThrows(NullPointerException.class, () -> repository.findByNameAndType("A", null));
        }
    }

    @Nested
    @DisplayName("Group 5: Thread Safety and Concurrency")
    class ConcurrencyTests {

        @Test
        @DisplayName("An toàn đa luồng khi ghi đồng thời vào ConcurrentHashMap")
        void testConcurrentSaves() throws InterruptedException {
            int threadCount = 20;
            ExecutorService service = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                final int idx = i;
                service.submit(() -> {
                    try {
                        NeedGroup g = new NeedGroup(UUID.randomUUID(), "Nhóm " + idx, TransactionType.EXPENSE, null, null);
                        repository.save(g);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertTrue(latch.await(3, TimeUnit.SECONDS));
            service.shutdown();
            assertEquals(3 + threadCount, repository.findAll().size());
        }
    }
}

