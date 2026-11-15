package com.pfms.infrastructure.file;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.infrastructure.file.support.JsonDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử kho lưu trữ tệp JsonCategoryRepository")
class JsonCategoryRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonDataStore dataStore;
    private JsonCategoryRepository repository;

    @BeforeEach
    void setUp() {
        Path storageFile = tempDir.resolve("test_pfms_categories.json");
        this.dataStore = new JsonDataStore(storageFile);
        this.repository = new JsonCategoryRepository(dataStore);
    }

    @Nested
    @DisplayName("Khởi tạo và kiểm tra tham số")
    class ConstructorTests {

        @Test
        @DisplayName("Từ chối khởi tạo khi JsonDataStore là null")
        void shouldThrowExceptionWhenDataStoreIsNull() {
            assertThrows(NullPointerException.class, () -> new JsonCategoryRepository(null));
        }
    }

    @Nested
    @DisplayName("Thao tác CRUD cơ bản")
    class CrudOperationsTests {

        @Test
        @DisplayName("Lưu mới và nạp lại danh mục qua findById")
        void shouldSaveAndFindById() {
            UUID catId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Category category = new Category(
                    catId,
                    "Cà Phê",
                    groupId,
                    TransactionType.EXPENSE,
                    "coffee",
                    "#6F4E37");

            Category saved = repository.save(category);
            assertNotNull(saved);
            assertEquals(catId, saved.getCategoryId());

            Optional<Category> found = repository.findById(catId);
            assertTrue(found.isPresent());
            assertEquals("Cà Phê", found.get().getName());
            assertEquals(groupId, found.get().getNeedGroupId());

            // Kiểm tra tính bền vững độc lập từ đĩa
            JsonDataStore freshStore = new JsonDataStore(dataStore.getStoragePath());
            JsonCategoryRepository freshRepo = new JsonCategoryRepository(freshStore);
            Optional<Category> diskFound = freshRepo.findById(catId);
            assertTrue(diskFound.isPresent());
            assertEquals("Cà Phê", diskFound.get().getName());
        }

        @Test
        @DisplayName("Cập nhật danh mục đã tồn tại (Upsert)")
        void shouldUpdateExistingCategory() {
            UUID catId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Category initial = new Category(
                    catId,
                    "Tên Cũ",
                    groupId,
                    TransactionType.EXPENSE,
                    "icon-old",
                    "#000000");
            repository.save(initial);

            Category updated = new Category(
                    catId,
                    "Tên Mới",
                    groupId,
                    TransactionType.EXPENSE,
                    "icon-new",
                    "#FFFFFF");
            repository.save(updated);

            List<Category> all = repository.findAll();
            assertEquals(1, all.size());
            assertEquals("Tên Mới", all.get(0).getName());
            assertEquals("icon-new", all.get(0).getIcon());
        }

        @Test
        @DisplayName("Xóa danh mục theo ID và kiểm tra existsById")
        void shouldDeleteByIdAndVerifyExistence() {
            UUID catId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Category category = new Category(
                    catId,
                    "Mục Xóa",
                    groupId,
                    TransactionType.INCOME,
                    "trash",
                    "#123456");

            repository.save(category);
            assertTrue(repository.existsById(catId));

            boolean deleted = repository.deleteById(catId);
            assertTrue(deleted);
            assertFalse(repository.existsById(catId));
            assertTrue(repository.findById(catId).isEmpty());

            assertFalse(repository.deleteById(catId));
        }

        @Test
        @DisplayName("Xóa toàn bộ danh mục qua deleteAll")
        void shouldDeleteAllCategories() {
            UUID gId = UUID.randomUUID();
            repository.save(new Category(UUID.randomUUID(), "C1", gId, TransactionType.EXPENSE, "i1", "#111"));
            repository.save(new Category(UUID.randomUUID(), "C2", gId, TransactionType.INCOME, "i2", "#222"));
            assertEquals(2, repository.findAll().size());

            repository.deleteAll();
            assertTrue(repository.findAll().isEmpty());
        }

        @Test
        @DisplayName("Từ chối tham số null trong các thao tác CRUD")
        void shouldThrowExceptionOnNullArguments() {
            assertThrows(NullPointerException.class, () -> repository.save(null));
            assertThrows(NullPointerException.class, () -> repository.findById(null));
            assertThrows(NullPointerException.class, () -> repository.existsById(null));
            assertThrows(NullPointerException.class, () -> repository.deleteById(null));
        }
    }

    @Nested
    @DisplayName("Truy vấn nghiệp vụ chuyên biệt")
    class BusinessQueriesTests {

        private UUID groupId1;
        private UUID groupId2;
        private UUID groupId3;
        private UUID catId1;
        private UUID catId2;
        private UUID deletedCatId;
        private UUID incomeCatId;

        @BeforeEach
        void seedData() {
            this.groupId1 = UUID.randomUUID();
            this.groupId2 = UUID.randomUUID();
            this.groupId3 = UUID.randomUUID();

            this.catId1 = UUID.randomUUID();
            Category c1 = new Category(
                    catId1,
                    "Ăn Sáng",
                    groupId1,
                    TransactionType.EXPENSE,
                    "utensils",
                    "#FF0000");
            repository.save(c1);

            this.catId2 = UUID.randomUUID();
            Category c2 = new Category(
                    catId2,
                    "Ăn Trưa",
                    groupId1,
                    TransactionType.EXPENSE,
                    "burger",
                    "#00FF00");
            repository.save(c2);

            this.deletedCatId = UUID.randomUUID();
            Category c3 = Category.reconstitute(
                    deletedCatId,
                    "Ăn Vặt Cũ",
                    groupId1,
                    TransactionType.EXPENSE,
                    EntityStatus.DELETED,
                    "cookie",
                    "#888888",
                    LocalDateTime.now());
            repository.save(c3);

            Category c4 = new Category(
                    UUID.randomUUID(),
                    "Xem Phim",
                    groupId2,
                    TransactionType.EXPENSE,
                    "film",
                    "#FFFF00");
            repository.save(c4);

            this.incomeCatId = UUID.randomUUID();
            Category c5 = new Category(
                    incomeCatId,
                    "Lương Cố Định",
                    groupId3,
                    TransactionType.INCOME,
                    "money-bill",
                    "#0000FF");
            repository.save(c5);
        }

        @Test
        @DisplayName("Lọc danh mục theo nhánh loại thu chi")
        void shouldFindByType() {
            List<Category> expenseList = repository.findByType(TransactionType.EXPENSE);
            assertEquals(4, expenseList.size());

            List<Category> incomeList = repository.findByType(TransactionType.INCOME);
            assertEquals(1, incomeList.size());
            assertEquals(incomeCatId, incomeList.get(0).getCategoryId());

            assertThrows(NullPointerException.class, () -> repository.findByType(null));
        }

        @Test
        @DisplayName("Lọc danh mục theo mã nhóm nhu cầu cha")
        void shouldFindByNeedGroupId() {
            List<Category> group1List = repository.findByNeedGroupId(groupId1);
            assertEquals(3, group1List.size());

            List<Category> group2List = repository.findByNeedGroupId(groupId2);
            assertEquals(1, group2List.size());

            assertThrows(NullPointerException.class, () -> repository.findByNeedGroupId(null));
        }

        @Test
        @DisplayName("Kiểm tra tồn tại danh mục đang hoạt động trong nhóm cha theo tên")
        void shouldCheckExistsByNameAndNeedGroupId() {
            assertTrue(repository.existsByNameAndNeedGroupId("  ăn sáng  ", groupId1));
            assertTrue(repository.existsByNameAndNeedGroupId("ĂN SÁNG", groupId1));

            // Danh mục đã xóa không tính là tồn tại đang hoạt động
            assertFalse(repository.existsByNameAndNeedGroupId("Ăn Vặt Cũ", groupId1));
            // Danh mục ở nhóm khác
            assertFalse(repository.existsByNameAndNeedGroupId("Ăn Sáng", groupId2));

            assertThrows(NullPointerException.class, () -> repository.existsByNameAndNeedGroupId(null, groupId1));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndNeedGroupId("Tên", null));
        }

        @Test
        @DisplayName("Kiểm tra trùng tên danh mục trong nhóm cha loại trừ chính ID đang cập nhật")
        void shouldCheckExistsByNameAndNeedGroupIdAndIdNot() {
            assertTrue(repository.existsByNameAndNeedGroupIdAndIdNot("Ăn Sáng", groupId1, catId2));

            // Trùng tên nhưng loại trừ chính nó
            assertFalse(repository.existsByNameAndNeedGroupIdAndIdNot("Ăn Sáng", groupId1, catId1));

            assertThrows(NullPointerException.class,
                    () -> repository.existsByNameAndNeedGroupIdAndIdNot(null, groupId1, catId1));
            assertThrows(NullPointerException.class,
                    () -> repository.existsByNameAndNeedGroupIdAndIdNot("Tên", null, catId1));
            assertThrows(NullPointerException.class,
                    () -> repository.existsByNameAndNeedGroupIdAndIdNot("Tên", groupId1, null));
        }

        @Test
        @DisplayName("Tìm kiếm danh mục theo tên trong nhóm cha, ưu tiên danh mục hoạt động")
        void shouldFindByNameAndNeedGroupId() {
            Optional<Category> found = repository.findByNameAndNeedGroupId("ăn sáng", groupId1);
            assertTrue(found.isPresent());
            assertEquals(catId1, found.get().getCategoryId());

            // Danh mục đã xóa vẫn tìm thấy được phục vụ luồng khôi phục
            Optional<Category> deletedFound = repository.findByNameAndNeedGroupId("ăn vặt cũ", groupId1);
            assertTrue(deletedFound.isPresent());
            assertEquals(deletedCatId, deletedFound.get().getCategoryId());

            assertTrue(repository.findByNameAndNeedGroupId("Không Tồn Tại", groupId1).isEmpty());
        }

        @Test
        @DisplayName("Đếm số lượng danh mục đang hoạt động trong nhóm nhu cầu")
        void shouldCountActiveByNeedGroupId() {
            assertEquals(2, repository.countActiveByNeedGroupId(groupId1));
            assertEquals(1, repository.countActiveByNeedGroupId(groupId2));
            assertEquals(0, repository.countActiveByNeedGroupId(UUID.randomUUID()));

            assertThrows(NullPointerException.class, () -> repository.countActiveByNeedGroupId(null));
        }

        @Test
        @DisplayName("Lọc toàn bộ danh mục đang hoạt động của hệ thống")
        void shouldFindActiveCategories() {
            List<Category> activeCategories = repository.findActiveCategories();
            assertEquals(4, activeCategories.size());
            assertFalse(activeCategories.stream().anyMatch(c -> c.getStatus() == EntityStatus.DELETED));
        }
    }
}
