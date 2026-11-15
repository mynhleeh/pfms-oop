package com.pfms.infrastructure.file;

import com.pfms.domain.model.category.NeedGroup;
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

@DisplayName("Kiểm thử kho lưu trữ tệp JsonNeedGroupRepository")
class JsonNeedGroupRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonDataStore dataStore;
    private JsonNeedGroupRepository repository;

    @BeforeEach
    void setUp() {
        Path storageFile = tempDir.resolve("test_pfms_need_groups.json");
        this.dataStore = new JsonDataStore(storageFile);
        this.repository = new JsonNeedGroupRepository(dataStore);
    }

    @Nested
    @DisplayName("Khởi tạo và kiểm tra tham số")
    class ConstructorTests {

        @Test
        @DisplayName("Từ chối khởi tạo khi JsonDataStore là null")
        void shouldThrowExceptionWhenDataStoreIsNull() {
            assertThrows(NullPointerException.class, () -> new JsonNeedGroupRepository(null));
        }
    }

    @Nested
    @DisplayName("Thao tác CRUD cơ bản")
    class CrudOperationsTests {

        @Test
        @DisplayName("Lưu mới và nạp lại nhóm nhu cầu qua findById")
        void shouldSaveAndFindById() {
            UUID groupId = UUID.randomUUID();
            NeedGroup group = new NeedGroup(
                    groupId,
                    "Nhu cầu thiết yếu",
                    TransactionType.EXPENSE,
                    "shopping-cart",
                    "#FF0000");

            NeedGroup saved = repository.save(group);
            assertNotNull(saved);
            assertEquals(groupId, saved.getNeedGroupId());

            Optional<NeedGroup> found = repository.findById(groupId);
            assertTrue(found.isPresent());
            assertEquals("Nhu cầu thiết yếu", found.get().getName());
            assertEquals(TransactionType.EXPENSE, found.get().getTransactionType());

            // Kiểm tra tính bền vững độc lập từ đĩa
            JsonDataStore freshStore = new JsonDataStore(dataStore.getStoragePath());
            JsonNeedGroupRepository freshRepo = new JsonNeedGroupRepository(freshStore);
            Optional<NeedGroup> diskFound = freshRepo.findById(groupId);
            assertTrue(diskFound.isPresent());
            assertEquals("Nhu cầu thiết yếu", diskFound.get().getName());
        }

        @Test
        @DisplayName("Cập nhật nhóm nhu cầu đã tồn tại (Upsert)")
        void shouldUpdateExistingGroup() {
            UUID groupId = UUID.randomUUID();
            NeedGroup initial = new NeedGroup(
                    groupId,
                    "Tên Cũ",
                    TransactionType.EXPENSE,
                    "icon-old",
                    "#000000");
            repository.save(initial);

            NeedGroup updated = new NeedGroup(
                    groupId,
                    "Tên Mới",
                    TransactionType.EXPENSE,
                    "icon-new",
                    "#FFFFFF");
            repository.save(updated);

            List<NeedGroup> all = repository.findAll();
            assertEquals(1, all.size());
            assertEquals("Tên Mới", all.get(0).getName());
            assertEquals("icon-new", all.get(0).getIcon());
        }

        @Test
        @DisplayName("Xóa nhóm nhu cầu theo ID và kiểm tra existsById")
        void shouldDeleteByIdAndVerifyExistence() {
            UUID groupId = UUID.randomUUID();
            NeedGroup group = new NeedGroup(
                    groupId,
                    "Nhóm Xóa",
                    TransactionType.INCOME,
                    "icon",
                    "#123456");

            repository.save(group);
            assertTrue(repository.existsById(groupId));

            boolean deleted = repository.deleteById(groupId);
            assertTrue(deleted);
            assertFalse(repository.existsById(groupId));
            assertTrue(repository.findById(groupId).isEmpty());

            assertFalse(repository.deleteById(groupId));
        }

        @Test
        @DisplayName("Xóa toàn bộ nhóm nhu cầu qua deleteAll")
        void shouldDeleteAllGroups() {
            repository.save(new NeedGroup(UUID.randomUUID(), "G1", TransactionType.EXPENSE, "i1", "#111"));
            repository.save(new NeedGroup(UUID.randomUUID(), "G2", TransactionType.INCOME, "i2", "#222"));
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

        private UUID activeExpenseId1;
        private UUID activeExpenseId2;
        private UUID deletedExpenseId;
        private UUID activeIncomeId;

        @BeforeEach
        void seedData() {
            this.activeExpenseId1 = UUID.randomUUID();
            NeedGroup g1 = new NeedGroup(
                    activeExpenseId1,
                    "Ăn Uống Sinh Hoạt",
                    TransactionType.EXPENSE,
                    "utensils",
                    "#FF0000");
            repository.save(g1);

            this.activeExpenseId2 = UUID.randomUUID();
            NeedGroup g2 = new NeedGroup(
                    activeExpenseId2,
                    "Mua Sắm Giải Trí",
                    TransactionType.EXPENSE,
                    "film",
                    "#00FF00");
            repository.save(g2);

            this.deletedExpenseId = UUID.randomUUID();
            NeedGroup g3 = NeedGroup.reconstitute(
                    deletedExpenseId,
                    "Chi Phí Cũ",
                    TransactionType.EXPENSE,
                    EntityStatus.DELETED,
                    "archive",
                    "#888888",
                    LocalDateTime.now());
            repository.save(g3);

            this.activeIncomeId = UUID.randomUUID();
            NeedGroup g4 = new NeedGroup(
                    activeIncomeId,
                    "Lương Thưởng",
                    TransactionType.INCOME,
                    "money-bill",
                    "#0000FF");
            repository.save(g4);
        }

        @Test
        @DisplayName("Lọc danh sách nhóm nhu cầu theo nhánh loại thu chi")
        void shouldFindByType() {
            List<NeedGroup> expenseGroups = repository.findByType(TransactionType.EXPENSE);
            assertEquals(3, expenseGroups.size());

            List<NeedGroup> incomeGroups = repository.findByType(TransactionType.INCOME);
            assertEquals(1, incomeGroups.size());
            assertEquals(activeIncomeId, incomeGroups.get(0).getNeedGroupId());

            assertThrows(NullPointerException.class, () -> repository.findByType(null));
        }

        @Test
        @DisplayName("Lọc các nhóm nhu cầu đang hoạt động")
        void shouldFindActiveGroups() {
            List<NeedGroup> activeGroups = repository.findActiveGroups();
            assertEquals(3, activeGroups.size());
        }

        @Test
        @DisplayName("Lọc nhóm nhu cầu đang hoạt động theo nhánh thu chi")
        void shouldFindActiveGroupsByType() {
            List<NeedGroup> activeExpense = repository.findActiveGroupsByType(TransactionType.EXPENSE);
            assertEquals(2, activeExpense.size());

            List<NeedGroup> activeIncome = repository.findActiveGroupsByType(TransactionType.INCOME);
            assertEquals(1, activeIncome.size());

            assertThrows(NullPointerException.class, () -> repository.findActiveGroupsByType(null));
        }

        @Test
        @DisplayName("Kiểm tra tồn tại nhóm đang hoạt động theo tên và loại")
        void shouldCheckExistsByNameAndType() {
            assertTrue(repository.existsByNameAndType("  ăn uống sinh hoạt  ", TransactionType.EXPENSE));
            assertTrue(repository.existsByNameAndType("ĂN UỐNG SINH HOẠT", TransactionType.EXPENSE));

            // Nhóm đã xóa không tính là tồn tại đang hoạt động
            assertFalse(repository.existsByNameAndType("Chi Phí Cũ", TransactionType.EXPENSE));
            // Nhầm nhánh loại thu chi
            assertFalse(repository.existsByNameAndType("Ăn Uống Sinh Hoạt", TransactionType.INCOME));

            assertThrows(NullPointerException.class, () -> repository.existsByNameAndType(null, TransactionType.EXPENSE));
            assertThrows(NullPointerException.class, () -> repository.existsByNameAndType("Tên", null));
        }

        @Test
        @DisplayName("Kiểm tra tồn tại nhóm theo tên và loại loại trừ chính ID đang cập nhật")
        void shouldCheckExistsByNameAndTypeAndIdNot() {
            assertTrue(repository.existsByNameAndTypeAndIdNot(
                    "Ăn Uống Sinh Hoạt", TransactionType.EXPENSE, activeExpenseId2));

            // Trùng tên nhưng loại trừ chính nó
            assertFalse(repository.existsByNameAndTypeAndIdNot(
                    "Ăn Uống Sinh Hoạt", TransactionType.EXPENSE, activeExpenseId1));

            assertThrows(NullPointerException.class,
                    () -> repository.existsByNameAndTypeAndIdNot(null, TransactionType.EXPENSE, activeExpenseId1));
            assertThrows(NullPointerException.class,
                    () -> repository.existsByNameAndTypeAndIdNot("Tên", null, activeExpenseId1));
            assertThrows(NullPointerException.class,
                    () -> repository.existsByNameAndTypeAndIdNot("Tên", TransactionType.EXPENSE, null));
        }

        @Test
        @DisplayName("Tìm kiếm nhóm nhu cầu theo tên và loại, ưu tiên nhóm hoạt động")
        void shouldFindByNameAndType() {
            Optional<NeedGroup> found = repository.findByNameAndType("lương thưởng", TransactionType.INCOME);
            assertTrue(found.isPresent());
            assertEquals(activeIncomeId, found.get().getNeedGroupId());

            // Nhóm đã xóa vẫn tìm thấy được phục vụ luồng khôi phục
            Optional<NeedGroup> deletedFound = repository.findByNameAndType("chi phí cũ", TransactionType.EXPENSE);
            assertTrue(deletedFound.isPresent());
            assertEquals(deletedExpenseId, deletedFound.get().getNeedGroupId());

            assertTrue(repository.findByNameAndType("Không Tồn Tại", TransactionType.EXPENSE).isEmpty());
        }

        @Test
        @DisplayName("Đếm số lượng nhóm đang hoạt động theo loại thu chi")
        void shouldCountActiveByType() {
            assertEquals(2, repository.countActiveByType(TransactionType.EXPENSE));
            assertEquals(1, repository.countActiveByType(TransactionType.INCOME));

            assertThrows(NullPointerException.class, () -> repository.countActiveByType(null));
        }
    }
}
