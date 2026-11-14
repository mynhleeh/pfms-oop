package com.pfms.domain.repository;

import com.pfms.common.util.Preconditions;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CategoryRepository Contract Tests")
class CategoryRepositoryTest {

    static class InMemoryCategoryRepositoryStub implements CategoryRepository {
        private final Map<UUID, Category> storage = new LinkedHashMap<>();

        @Override
        public Category save(Category entity) {
            if (entity == null) {
                throw new IllegalArgumentException("Category entity must not be null");
            }
            storage.put(entity.getCategoryId(), entity);
            return entity;
        }

        @Override
        public Optional<Category> findById(UUID categoryId) {
            if (categoryId == null) {
                throw new IllegalArgumentException("Category ID must not be null");
            }
            return Optional.ofNullable(storage.get(categoryId));
        }

        @Override
        public List<Category> findAll() {
            return Collections.unmodifiableList(new ArrayList<>(storage.values()));
        }

        @Override
        public boolean existsById(UUID categoryId) {
            if (categoryId == null) {
                throw new IllegalArgumentException("Category ID must not be null");
            }
            return storage.containsKey(categoryId);
        }

        @Override
        public boolean deleteById(UUID categoryId) {
            if (categoryId == null) {
                throw new IllegalArgumentException("Category ID must not be null");
            }
            return storage.remove(categoryId) != null;
        }

        @Override
        public List<Category> findByType(TransactionType type) {
            if (type == null) {
                throw new IllegalArgumentException("TransactionType must not be null");
            }
            return storage.values().stream()
                    .filter(category -> category.getTransactionType() == type)
                    .toList();
        }

        @Override
        public List<Category> findByNeedGroupId(UUID needGroupId) {
            if (needGroupId == null) {
                throw new IllegalArgumentException("NeedGroup ID must not be null");
            }
            return storage.values().stream()
                    .filter(category -> category.getNeedGroupId().equals(needGroupId))
                    .toList();
        }

        @Override
        public boolean existsByNameAndNeedGroupId(String name, UUID needGroupId) {
            String trimmedName = Preconditions.requireNonBlank(name, "Category name must not be null or blank");
            if (needGroupId == null) {
                throw new IllegalArgumentException("NeedGroup ID must not be null");
            }
            return storage.values().stream()
                    .anyMatch(category -> category.isActive()
                            && category.getNeedGroupId().equals(needGroupId)
                            && category.getName().equalsIgnoreCase(trimmedName));
        }

        @Override
        public boolean existsByNameAndNeedGroupIdAndIdNot(String name, UUID needGroupId, UUID categoryId) {
            String trimmedName = Preconditions.requireNonBlank(name, "Category name must not be null or blank");
            if (needGroupId == null) {
                throw new IllegalArgumentException("NeedGroup ID must not be null");
            }
            if (categoryId == null) {
                throw new IllegalArgumentException("Excluded category ID must not be null");
            }
            return storage.values().stream()
                    .anyMatch(category -> category.isActive()
                            && category.getNeedGroupId().equals(needGroupId)
                            && !category.getCategoryId().equals(categoryId)
                            && category.getName().equalsIgnoreCase(trimmedName));
        }

        @Override
        public Optional<Category> findByNameAndNeedGroupId(String name, UUID needGroupId) {
            String trimmedName = Preconditions.requireNonBlank(name, "Category name must not be null or blank");
            if (needGroupId == null) {
                throw new IllegalArgumentException("NeedGroup ID must not be null");
            }
            return storage.values().stream()
                    .filter(category -> category.getNeedGroupId().equals(needGroupId)
                            && category.getName().equalsIgnoreCase(trimmedName))
                    .sorted(Comparator.comparing(Category::isActive).reversed())
                    .findFirst();
        }

        @Override
        public long countActiveByNeedGroupId(UUID needGroupId) {
            if (needGroupId == null) {
                throw new IllegalArgumentException("NeedGroup ID must not be null");
            }
            return storage.values().stream()
                    .filter(category -> category.isActive() && category.getNeedGroupId().equals(needGroupId))
                    .count();
        }

        @Override
        public List<Category> findActiveCategories() {
            return storage.values().stream()
                    .filter(Category::isActive)
                    .toList();
        }
    }

    private InMemoryCategoryRepositoryStub repository;
    private UUID essentialGroupId;
    private UUID wantsGroupId;
    private UUID incomeGroupId;

    private Category foodCategory;
    private Category feeCategory;
    private Category archivedFoodCategory;
    private Category entertainmentCategory;
    private Category salaryCategory;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCategoryRepositoryStub();
        essentialGroupId = UUID.randomUUID();
        wantsGroupId = UUID.randomUUID();
        incomeGroupId = UUID.randomUUID();

        foodCategory = new Category(
                UUID.randomUUID(), "Ăn uống", essentialGroupId, TransactionType.EXPENSE, "food", "#FF5733");
        feeCategory = new Category(
                UUID.randomUUID(), "Phí chuyển khoản", essentialGroupId, TransactionType.EXPENSE, "fee", "#C70039");
        archivedFoodCategory = new Category(
                UUID.randomUUID(), "Ăn vặt cũ", essentialGroupId, TransactionType.EXPENSE, "snack", "#900C3F");
        archivedFoodCategory.markDeleted();

        entertainmentCategory = new Category(
                UUID.randomUUID(), "Giải trí", wantsGroupId, TransactionType.EXPENSE, "game", "#33FF57");
        salaryCategory = new Category(
                UUID.randomUUID(), "Lương chính", incomeGroupId, TransactionType.INCOME, "salary", "#28B463");
    }

    @Nested
    @DisplayName("GenericRepository Inherited CRUD Tests")
    class GenericCrudTests {

        @Test
        @DisplayName("Should save category, return saved entity, and retrieve active or deleted by ID")
        void shouldSaveAndFindBothActiveAndDeletedCategoriesById() {
            Category savedFood = repository.save(foodCategory);
            Category savedArchived = repository.save(archivedFoodCategory);

            assertEquals(foodCategory, savedFood);
            assertEquals("Ăn uống", savedFood.getName());
            assertTrue(repository.existsById(foodCategory.getCategoryId()));
            assertFalse(repository.existsById(UUID.randomUUID()));
            assertEquals(Optional.of(foodCategory), repository.findById(foodCategory.getCategoryId()));
            assertEquals(Optional.of(savedArchived), repository.findById(archivedFoodCategory.getCategoryId()));
            assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        }

        @Test
        @DisplayName("Should overwrite existing entry when saving distinct instance with matching ID")
        void shouldOverwriteExistingCategoryWhenSavingDistinctInstanceWithSameId() {
            repository.save(foodCategory);
            Category updatedFood = new Category(
                    foodCategory.getCategoryId(), "Ăn uống hàng ngày", essentialGroupId,
                    TransactionType.EXPENSE, "meal", "#123456");

            Category returnedEntity = repository.save(updatedFood);

            assertEquals(1, repository.findAll().size());
            assertEquals("Ăn uống hàng ngày", returnedEntity.getName());
            assertEquals("Ăn uống hàng ngày",
                    repository.findById(foodCategory.getCategoryId()).orElseThrow().getName());
        }

        @Test
        @DisplayName("Should delete existing category by ID and return false on absent ID")
        void shouldDeleteCategoryByIdAndReturnFalseWhenAbsent() {
            repository.save(foodCategory);

            assertTrue(repository.deleteById(foodCategory.getCategoryId()));
            assertFalse(repository.existsById(foodCategory.getCategoryId()));
            assertTrue(repository.findById(foodCategory.getCategoryId()).isEmpty());
            assertFalse(repository.deleteById(foodCategory.getCategoryId()));
        }

        @Test
        @DisplayName("Should return both ACTIVE and DELETED categories in unmodifiable list from findAll")
        void shouldReturnActiveAndDeletedCategoriesAsUnmodifiableListFromFindAll() {
            repository.save(foodCategory);
            repository.save(archivedFoodCategory);

            List<Category> allCategories = repository.findAll();

            assertEquals(2, allCategories.size());
            assertTrue(allCategories.containsAll(List.of(foodCategory, archivedFoodCategory)));
            assertThrows(UnsupportedOperationException.class, allCategories::clear);
        }

        @Test
        @DisplayName("Should return non-null unmodifiable empty list when repository has no categories")
        void shouldReturnEmptyUnmodifiableListWhenRepositoryIsEmpty() {
            List<Category> allCategories = repository.findAll();

            assertTrue(allCategories.isEmpty());
            assertThrows(UnsupportedOperationException.class, () -> allCategories.add(foodCategory));
        }

        @Test
        @DisplayName("Should reject null arguments across all inherited GenericRepository operations")
        void shouldRejectNullArgumentsOnInheritedCrudOperations() {
            assertThrows(IllegalArgumentException.class, () -> repository.save(null));
            assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
            assertThrows(IllegalArgumentException.class, () -> repository.existsById(null));
            assertThrows(IllegalArgumentException.class, () -> repository.deleteById(null));
        }
    }

    @Nested
    @DisplayName("Hierarchy Query Tests (findByType & findByNeedGroupId)")
    class HierarchyQueryTests {

        @Test
        @DisplayName("Should filter categories by TransactionType and return unmodifiable lists")
        void shouldFilterCategoriesByTransactionType() {
            repository.save(foodCategory);
            repository.save(feeCategory);
            repository.save(archivedFoodCategory);
            repository.save(salaryCategory);

            List<Category> expenseCategories = repository.findByType(TransactionType.EXPENSE);
            List<Category> incomeCategories = repository.findByType(TransactionType.INCOME);

            assertEquals(3, expenseCategories.size());
            assertTrue(expenseCategories.containsAll(List.of(foodCategory, feeCategory, archivedFoodCategory)));
            assertEquals(1, incomeCategories.size());
            assertEquals(salaryCategory, incomeCategories.get(0));
            assertThrows(UnsupportedOperationException.class, () -> expenseCategories.remove(0));
        }

        @Test
        @DisplayName("Should return empty unmodifiable list when findByType has no matching categories")
        void shouldReturnEmptyUnmodifiableListWhenFindByTypeHasNoMatch() {
            repository.save(foodCategory);

            List<Category> incomeCategories = repository.findByType(TransactionType.INCOME);

            assertTrue(incomeCategories.isEmpty());
            assertThrows(UnsupportedOperationException.class, () -> incomeCategories.add(salaryCategory));
        }

        @Test
        @DisplayName("Should filter categories by NeedGroupId and return unmodifiable empty list when absent")
        void shouldFilterCategoriesByNeedGroupId() {
            repository.save(foodCategory);
            repository.save(feeCategory);
            repository.save(archivedFoodCategory);
            repository.save(entertainmentCategory);

            List<Category> essentialCategories = repository.findByNeedGroupId(essentialGroupId);
            List<Category> emptyGroupCategories = repository.findByNeedGroupId(UUID.randomUUID());

            assertEquals(3, essentialCategories.size());
            assertTrue(essentialCategories.containsAll(List.of(foodCategory, feeCategory, archivedFoodCategory)));
            assertTrue(emptyGroupCategories.isEmpty());
            assertThrows(UnsupportedOperationException.class, () -> essentialCategories.remove(0));
            assertThrows(UnsupportedOperationException.class, () -> emptyGroupCategories.add(foodCategory));
        }

        @Test
        @DisplayName("Should reject null parameters on findByType and findByNeedGroupId")
        void shouldRejectNullParametersOnHierarchyQueries() {
            assertThrows(IllegalArgumentException.class, () -> repository.findByType(null));
            assertThrows(IllegalArgumentException.class, () -> repository.findByNeedGroupId(null));
        }
    }

    @Nested
    @DisplayName("Name Uniqueness & Soft-Delete Awareness Tests")
    class NameUniquenessTests {

        @Test
        @DisplayName("Should detect active duplicate name case-insensitively with whitespace trimming")
        void shouldDetectDuplicateActiveNameCaseInsensitivelyWithinSameGroup() {
            repository.save(foodCategory);
            repository.save(archivedFoodCategory);
            repository.save(entertainmentCategory);

            assertTrue(repository.existsByNameAndNeedGroupId("  ĂN UỐNG  ", essentialGroupId));
            assertFalse(repository.existsByNameAndNeedGroupId("Không tồn tại", essentialGroupId));
            assertFalse(repository.existsByNameAndNeedGroupId("Ăn uống", wantsGroupId));
            assertFalse(repository.existsByNameAndNeedGroupId("Ăn vặt cũ", essentialGroupId));
        }

        @Test
        @DisplayName("Should exclude target category ID and ignore soft-deleted or cross-group entries on rename")
        void shouldExcludeSelfIdWhenCheckingDuplicateOnRename() {
            repository.save(foodCategory);
            repository.save(feeCategory);
            repository.save(archivedFoodCategory);
            repository.save(entertainmentCategory);

            assertFalse(repository.existsByNameAndNeedGroupIdAndIdNot(
                    "  ĂN UỐNG  ", essentialGroupId, foodCategory.getCategoryId()));
            assertTrue(repository.existsByNameAndNeedGroupIdAndIdNot(
                    "  phí chuyển khoản  ", essentialGroupId, foodCategory.getCategoryId()));
            assertFalse(repository.existsByNameAndNeedGroupIdAndIdNot(
                    "Ăn vặt cũ", essentialGroupId, foodCategory.getCategoryId()));
            assertFalse(repository.existsByNameAndNeedGroupIdAndIdNot(
                    "Giải trí", essentialGroupId, foodCategory.getCategoryId()));
        }

        @Test
        @DisplayName("Should find both active and soft-deleted categories and isolate by NeedGroup")
        void shouldFindActiveAndSoftDeletedCategoriesByNameAndNeedGroupId() {
            repository.save(foodCategory);
            repository.save(archivedFoodCategory);

            Optional<Category> foundActive = repository.findByNameAndNeedGroupId("  ăn uống  ", essentialGroupId);
            Optional<Category> foundDeleted = repository.findByNameAndNeedGroupId("  ăn vặt cũ ", essentialGroupId);

            assertEquals(Optional.of(foodCategory), foundActive);
            assertTrue(foundDeleted.isPresent());
            assertTrue(foundDeleted.get().isDeleted());
            assertTrue(repository.findByNameAndNeedGroupId("Ăn uống", wantsGroupId).isEmpty());
            assertTrue(repository.findByNameAndNeedGroupId("Không tồn tại", essentialGroupId).isEmpty());
        }

        @Test
        @DisplayName("Should prefer active category over soft-deleted duplicate in findByNameAndNeedGroupId")
        void shouldPreferActiveCategoryWhenDeletedDuplicateExistsWithSameName() {
            Category recreatedSnack = new Category(
                    UUID.randomUUID(), "Ăn vặt cũ", essentialGroupId, TransactionType.EXPENSE, "snack", "#112233");
            repository.save(archivedFoodCategory);
            repository.save(recreatedSnack);

            Optional<Category> resolved = repository.findByNameAndNeedGroupId("Ăn vặt cũ", essentialGroupId);

            assertEquals(Optional.of(recreatedSnack), resolved);
            assertTrue(resolved.orElseThrow().isActive());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Should reject null, empty, or blank category names on all name queries")
        void shouldRejectNullOrBlankNameOnNameQueries(String invalidName) {
            UUID selfCategoryId = foodCategory.getCategoryId();

            assertThrows(IllegalArgumentException.class,
                    () -> repository.existsByNameAndNeedGroupId(invalidName, essentialGroupId));
            assertThrows(IllegalArgumentException.class,
                    () -> repository.existsByNameAndNeedGroupIdAndIdNot(invalidName, essentialGroupId, selfCategoryId));
            assertThrows(IllegalArgumentException.class,
                    () -> repository.findByNameAndNeedGroupId(invalidName, essentialGroupId));
        }

        @Test
        @DisplayName("Should reject null NeedGroup ID or excluded Category ID on name queries")
        void shouldRejectNullIdsOnNameQueries() {
            UUID selfCategoryId = foodCategory.getCategoryId();

            assertThrows(IllegalArgumentException.class,
                    () -> repository.existsByNameAndNeedGroupId("Ăn uống", null));
            assertThrows(IllegalArgumentException.class,
                    () -> repository.existsByNameAndNeedGroupIdAndIdNot("Ăn uống", null, selfCategoryId));
            assertThrows(IllegalArgumentException.class,
                    () -> repository.existsByNameAndNeedGroupIdAndIdNot("Ăn uống", essentialGroupId, null));
            assertThrows(IllegalArgumentException.class,
                    () -> repository.findByNameAndNeedGroupId("Ăn uống", null));
        }
    }

    @Nested
    @DisplayName("Active Category Filtering & Counting Tests")
    class ActiveFilterAndCountTests {

        @Test
        @DisplayName("Should count only ACTIVE categories per NeedGroup and isolate between groups")
        void shouldCountOnlyActiveCategoriesInNeedGroup() {
            repository.save(foodCategory);
            repository.save(feeCategory);
            repository.save(archivedFoodCategory);
            repository.save(entertainmentCategory);

            assertEquals(2, repository.countActiveByNeedGroupId(essentialGroupId));
            assertEquals(1, repository.countActiveByNeedGroupId(wantsGroupId));
            assertEquals(0, repository.countActiveByNeedGroupId(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should reject null NeedGroup ID on countActiveByNeedGroupId")
        void shouldRejectNullNeedGroupIdOnCountActive() {
            assertThrows(IllegalArgumentException.class, () -> repository.countActiveByNeedGroupId(null));
        }

        @Test
        @DisplayName("Should return only ACTIVE categories as an unmodifiable list")
        void shouldReturnOnlyActiveCategoriesAcrossHierarchy() {
            repository.save(foodCategory);
            repository.save(feeCategory);
            repository.save(archivedFoodCategory);
            repository.save(salaryCategory);

            List<Category> activeCategories = repository.findActiveCategories();

            assertEquals(3, activeCategories.size());
            assertTrue(activeCategories.containsAll(List.of(foodCategory, feeCategory, salaryCategory)));
            assertFalse(activeCategories.contains(archivedFoodCategory));
            assertThrows(UnsupportedOperationException.class, () -> activeCategories.remove(0));
        }

        @Test
        @DisplayName("Should return empty unmodifiable list from findActiveCategories when repository is empty")
        void shouldReturnEmptyUnmodifiableListWhenRepositoryIsEmpty() {
            List<Category> activeCategories = repository.findActiveCategories();

            assertTrue(activeCategories.isEmpty());
            assertThrows(UnsupportedOperationException.class, () -> activeCategories.add(foodCategory));
        }

        @Test
        @DisplayName("Should return empty unmodifiable list from findActiveCategories when all categories are deleted")
        void shouldReturnEmptyUnmodifiableListWhenAllCategoriesAreDeleted() {
            repository.save(archivedFoodCategory);

            List<Category> activeCategories = repository.findActiveCategories();

            assertTrue(activeCategories.isEmpty());
            assertThrows(UnsupportedOperationException.class, () -> activeCategories.add(foodCategory));
        }
    }
}
