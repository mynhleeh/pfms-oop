package com.pfms.infrastructure.memory;

import com.pfms.domain.model.category.Category;
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

@DisplayName("MemoryCategoryRepository Unit Tests")
class MemoryCategoryRepositoryTest {

    private MemoryCategoryRepository repository;
    private UUID needGroupId1;
    private UUID needGroupId2;
    private UUID catId1;
    private UUID catId2;
    private UUID catId3;
    private Category cat1;
    private Category cat2;
    private Category cat3;

    @BeforeEach
    void setUp() {
        repository = new MemoryCategoryRepository();
        needGroupId1 = UUID.randomUUID();
        needGroupId2 = UUID.randomUUID();
        catId1 = UUID.randomUUID();
        catId2 = UUID.randomUUID();
        catId3 = UUID.randomUUID();

        cat1 = new Category(catId1, "Ăn uống", needGroupId1, TransactionType.EXPENSE, "icon1", "#111");
        cat2 = new Category(catId2, "Đi lại", needGroupId1, TransactionType.EXPENSE, "icon2", "#222");
        cat3 = new Category(catId3, "Tiền lương", needGroupId2, TransactionType.INCOME, "icon3", "#333");

        repository.save(cat1);
        repository.save(cat2);
        repository.save(cat3);
    }

    @Nested
    @DisplayName("Group 1: Basic CRUD Operations")
    class CrudTests {

        @Test
        @DisplayName("Lưu và tìm kiếm danh mục theo ID")
        void testSaveAndFindById() {
            Optional<Category> found = repository.findById(catId1);
            assertTrue(found.isPresent());
            assertEquals("Ăn uống", found.get().getName());
        }

        @Test
        @DisplayName("Xóa danh mục theo ID")
        void testDeleteById() {
            assertTrue(repository.deleteById(catId1));
            assertFalse(repository.existsById(catId1));
            assertFalse(repository.deleteById(catId1));
        }

        @Test
        @DisplayName("findAll trả về toàn bộ danh mục")
        void testFindAll() {
            assertEquals(3, repository.findAll().size());
        }
    }

    @Nested
    @DisplayName("Group 2: Queries and Filters")
    class QueryFilterTests {

        @Test
        @DisplayName("Lọc theo loại thu chi findByType")
        void testFindByType() {
            List<Category> expenses = repository.findByType(TransactionType.EXPENSE);
            assertEquals(2, expenses.size());

            List<Category> incomes = repository.findByType(TransactionType.INCOME);
            assertEquals(1, incomes.size());
        }

        @Test
        @DisplayName("Lọc theo nhóm nhu cầu findByNeedGroupId")
        void testFindByNeedGroupId() {
            List<Category> group1Cats = repository.findByNeedGroupId(needGroupId1);
            assertEquals(2, group1Cats.size());

            List<Category> group2Cats = repository.findByNeedGroupId(needGroupId2);
            assertEquals(1, group2Cats.size());
        }

        @Test
        @DisplayName("Lọc các danh mục hoạt động findActiveCategories")
        void testFindActiveCategories() {
            cat2.markDeleted();
            repository.save(cat2);

            List<Category> active = repository.findActiveCategories();
            assertEquals(2, active.size());
            assertFalse(active.contains(cat2));
        }

        @Test
        @DisplayName("Đếm số lượng danh mục con hoạt động trong nhóm countActiveByNeedGroupId")
        void testCountActiveByNeedGroupId() {
            assertEquals(2, repository.countActiveByNeedGroupId(needGroupId1));

            cat2.markDeleted();
            repository.save(cat2);

            assertEquals(1, repository.countActiveByNeedGroupId(needGroupId1));
        }
    }

    @Nested
    @DisplayName("Group 3: Name Uniqueness and Scope")
    class NameUniquenessTests {

        @Test
        @DisplayName("existsByNameAndNeedGroupId kiểm tra không phân biệt hoa thường và chỉ xét danh mục ACTIVE")
        void testExistsByNameAndNeedGroupId() {
            assertTrue(repository.existsByNameAndNeedGroupId("  ăn uống  ", needGroupId1));
            assertFalse(repository.existsByNameAndNeedGroupId("Ăn uống", needGroupId2)); // Khác nhóm cha -> false

            cat1.markDeleted();
            repository.save(cat1);
            assertFalse(repository.existsByNameAndNeedGroupId("Ăn uống", needGroupId1), "Danh mục DELETED không tính vào trùng tên");
        }

        @Test
        @DisplayName("existsByNameAndNeedGroupIdAndIdNot loại trừ chính danh mục đang cập nhật")
        void testExistsByNameAndNeedGroupIdAndIdNot() {
            assertFalse(repository.existsByNameAndNeedGroupIdAndIdNot("Ăn uống", needGroupId1, catId1));
            assertTrue(repository.existsByNameAndNeedGroupIdAndIdNot("Đi lại", needGroupId1, catId1));
        }

        @Test
        @DisplayName("findByNameAndNeedGroupId tìm kiếm danh mục theo tên trong nhóm")
        void testFindByNameAndNeedGroupId() {
            Optional<Category> found = repository.findByNameAndNeedGroupId("Ăn uống", needGroupId1);
            assertTrue(found.isPresent());
            assertEquals(catId1, found.get().getCategoryId());
        }

        @Test
        @DisplayName("findByNameAndNeedGroupId ưu tiên trả về danh mục ACTIVE trước DELETED khi trùng tên")
        void testFindByNameAndNeedGroupIdPrefersActiveOverDeleted() {
            cat1.markDeleted();
            repository.save(cat1);

            UUID activeReplacementId = UUID.randomUUID();
            Category activeReplacement = new Category(
                    activeReplacementId, "Ăn uống", needGroupId1, TransactionType.EXPENSE, "new-icon", "#444");
            repository.save(activeReplacement);

            Optional<Category> found = repository.findByNameAndNeedGroupId("  ăn uống  ", needGroupId1);
            assertTrue(found.isPresent());
            assertTrue(found.get().isActive());
            assertEquals(activeReplacementId, found.get().getCategoryId());
        }
    }

    @Nested
    @DisplayName("Group 4: Null Handling and Validation")
    class ValidationTests {

        @Test
        @DisplayName("Từ chối tham số null")
        void testNullChecks() {
            assertThrows(NullPointerException.class, () -> repository.findByType(null));
            assertThrows(NullPointerException.class, () -> repository.findByNeedGroupId(null));
            assertThrows(NullPointerException.class, () -> repository.countActiveByNeedGroupId(null));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndNeedGroupId(null, needGroupId1));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndNeedGroupId("A", null));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndNeedGroupIdAndIdNot(null, needGroupId1, catId1));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndNeedGroupIdAndIdNot("A", null, catId1));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndNeedGroupIdAndIdNot("A", needGroupId1, null));
        }
    }

    @Nested
    @DisplayName("Group 5: Thread Safety and Concurrency")
    class ConcurrencyTests {

        @Test
        @DisplayName("An toàn đa luồng khi thêm đồng thời vào ConcurrentHashMap")
        void testConcurrentSaves() throws InterruptedException {
            int threadCount = 20;
            ExecutorService service = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                final int idx = i;
                service.submit(() -> {
                    try {
                        Category c = new Category(UUID.randomUUID(), "Danh mục " + idx, needGroupId1, TransactionType.EXPENSE, null, null);
                        repository.save(c);
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

