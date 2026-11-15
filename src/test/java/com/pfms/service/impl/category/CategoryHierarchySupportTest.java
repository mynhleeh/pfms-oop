package com.pfms.service.impl.category;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.infrastructure.memory.MemoryCategoryRepository;
import com.pfms.infrastructure.memory.MemoryNeedGroupRepository;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CategoryHierarchySupport")
class CategoryHierarchySupportTest {

    private MemoryNeedGroupRepository needGroupRepository;
    private MemoryCategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        needGroupRepository = new MemoryNeedGroupRepository();
        categoryRepository = new MemoryCategoryRepository();
    }

    @Test
    @DisplayName("Ném AssertionError khi cố tình khởi tạo lớp tiện ích qua reflection")
    void testUtilityClassDesign() throws Exception {
        Constructor<CategoryHierarchySupport> constructor =
                CategoryHierarchySupport.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("Ánh xạ Category sang CategoryResponseDTO giữ nguyên thời điểm createdAt thực tế")
    void shouldMapCategoryResponseDtoWithActualEntityCreatedAt() {
        UUID groupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID categoryId = UUID.fromString("00000000-0000-0000-0000-000000000010");
        LocalDateTime historicalCreatedAt = LocalDateTime.of(2025, 1, 15, 8, 30, 0);

        NeedGroup group = NeedGroup.reconstitute(
                groupId, "Ăn uống", TransactionType.EXPENSE, EntityStatus.ACTIVE,
                "utensils", "#FF5722", historicalCreatedAt.minusDays(1));
        Category category = Category.reconstitute(
                categoryId, "Cà phê sáng", groupId, TransactionType.EXPENSE,
                EntityStatus.ACTIVE, "coffee", "#795548", historicalCreatedAt);

        CategoryResponseDTO dto = CategoryHierarchySupport.toResponseDTO(category, group);

        assertEquals(categoryId, dto.getCategoryId());
        assertEquals("Cà phê sáng", dto.getName());
        assertEquals(groupId, dto.getParentGroupId());
        assertEquals("Ăn uống", dto.getParentGroupName());
        assertEquals("EXPENSE", dto.getTransactionType());
        assertEquals("ACTIVE", dto.getStatus());
        assertEquals(historicalCreatedAt, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Ánh xạ NeedGroup sang NeedGroupResponseDTO đầy đủ thuộc tính và createdAt thực tế")
    void shouldMapNeedGroupToNeedGroupResponseDTO() {
        UUID groupId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        LocalDateTime createdAt = LocalDateTime.of(2025, 3, 20, 14, 15, 0);

        NeedGroup activeGroup = NeedGroup.reconstitute(
                groupId, "Sức khỏe", TransactionType.EXPENSE, EntityStatus.ACTIVE,
                "heart", "#E91E63", createdAt);

        NeedGroupResponseDTO activeDto = CategoryHierarchySupport.toNeedGroupResponseDTO(activeGroup);

        assertEquals(groupId, activeDto.getNeedGroupId());
        assertEquals("Sức khỏe", activeDto.getName());
        assertEquals("EXPENSE", activeDto.getTransactionType());
        assertEquals("ACTIVE", activeDto.getStatus());
        assertEquals("heart", activeDto.getIcon());
        assertEquals("#E91E63", activeDto.getColor());
        assertEquals(createdAt, activeDto.getCreatedAt());
        assertTrue(activeDto.isActive());

        NeedGroup deletedGroup = NeedGroup.reconstitute(
                groupId, "Nhóm cũ", TransactionType.INCOME, EntityStatus.DELETED,
                "archive", "#757575", createdAt);
        NeedGroupResponseDTO deletedDto = CategoryHierarchySupport.toNeedGroupResponseDTO(deletedGroup);

        assertEquals("INCOME", deletedDto.getTransactionType());
        assertEquals("DELETED", deletedDto.getStatus());
        assertTrue(deletedDto.isDeleted());
    }

    @Test
    @DisplayName("Dựng cây phân cấp bao gồm cả nhóm nhu cầu và danh mục ở trạng thái ACTIVE và DELETED")
    void shouldBuildCategoryHierarchyIncludingActiveAndDeletedEntities() {
        UUID activeExpenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID deletedExpenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID activeIncomeGroupId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 1, 9, 0, 0);

        NeedGroup activeExpenseGroup = NeedGroup.reconstitute(
                activeExpenseGroupId, "Ăn uống", TransactionType.EXPENSE, EntityStatus.ACTIVE,
                "utensils", "#FF5722", createdAt);
        NeedGroup deletedExpenseGroup = NeedGroup.reconstitute(
                deletedExpenseGroupId, "Nhóm cũ", TransactionType.EXPENSE, EntityStatus.DELETED,
                "archive", "#757575", createdAt);
        NeedGroup activeIncomeGroup = NeedGroup.reconstitute(
                activeIncomeGroupId, "Lương", TransactionType.INCOME, EntityStatus.ACTIVE,
                "briefcase", "#4CAF50", createdAt);

        needGroupRepository.save(activeExpenseGroup);
        needGroupRepository.save(deletedExpenseGroup);
        needGroupRepository.save(activeIncomeGroup);

        Category activeChild = Category.reconstitute(
                UUID.fromString("00000000-0000-0000-0000-000000000101"),
                "Cơm trưa", activeExpenseGroupId, TransactionType.EXPENSE,
                EntityStatus.ACTIVE, "", "", createdAt);
        Category deletedChild = Category.reconstitute(
                UUID.fromString("00000000-0000-0000-0000-000000000102"),
                "Trà sữa", activeExpenseGroupId, TransactionType.EXPENSE,
                EntityStatus.DELETED, "", "", createdAt);
        Category orphanDeletedChild = Category.reconstitute(
                UUID.fromString("00000000-0000-0000-0000-000000000103"),
                "Đồ cũ", deletedExpenseGroupId, TransactionType.EXPENSE,
                EntityStatus.DELETED, "", "", createdAt);

        categoryRepository.save(activeChild);
        categoryRepository.save(deletedChild);
        categoryRepository.save(orphanDeletedChild);

        List<CategoryHierarchyResponseDTO> hierarchy =
                CategoryHierarchySupport.buildCategoryHierarchy(needGroupRepository, categoryRepository);

        assertEquals(3, hierarchy.size());

        CategoryHierarchyResponseDTO first = hierarchy.get(0);
        assertEquals(activeExpenseGroupId, first.getNeedGroupId());
        assertEquals("ACTIVE", first.getNeedGroupStatus());
        assertEquals(2, first.getCategories().size());
        assertEquals("Cơm trưa", first.getCategories().get(0).getName());
        assertEquals("ACTIVE", first.getCategories().get(0).getStatus());
        assertEquals("Trà sữa", first.getCategories().get(1).getName());
        assertEquals("DELETED", first.getCategories().get(1).getStatus());

        CategoryHierarchyResponseDTO second = hierarchy.get(1);
        assertEquals(deletedExpenseGroupId, second.getNeedGroupId());
        assertEquals("DELETED", second.getNeedGroupStatus());
        assertEquals(1, second.getCategories().size());
        assertEquals("Đồ cũ", second.getCategories().get(0).getName());
        assertEquals("DELETED", second.getCategories().get(0).getStatus());

        CategoryHierarchyResponseDTO third = hierarchy.get(2);
        assertEquals(activeIncomeGroupId, third.getNeedGroupId());
        assertEquals("INCOME", third.getTransactionType());
        assertEquals("ACTIVE", third.getNeedGroupStatus());
        assertTrue(third.getCategories().isEmpty());
    }

    @Test
    @DisplayName("Ưu tiên mục ACTIVE trước DELETED và sắp xếp tên đúng bảng chữ cái tiếng Việt")
    void shouldSortActiveBeforeDeletedAndUseVietnameseAlphabetOrder() {
        UUID deletedEarlyNameId = UUID.fromString("00000000-0000-0000-0000-000000000011");
        UUID activeLaterNameId = UUID.fromString("00000000-0000-0000-0000-000000000012");
        UUID activeVietnameseAId = UUID.fromString("00000000-0000-0000-0000-000000000013");
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 1, 9, 0, 0);

        needGroupRepository.save(NeedGroup.reconstitute(
                deletedEarlyNameId, "An sinh cũ", TransactionType.EXPENSE, EntityStatus.DELETED, "", "", createdAt));
        needGroupRepository.save(NeedGroup.reconstitute(
                activeLaterNameId, "Đi lại", TransactionType.EXPENSE, EntityStatus.ACTIVE, "", "", createdAt));
        needGroupRepository.save(NeedGroup.reconstitute(
                activeVietnameseAId, "Ăn uống", TransactionType.EXPENSE, EntityStatus.ACTIVE, "", "", createdAt));

        categoryRepository.save(Category.reconstitute(
                UUID.fromString("00000000-0000-0000-0000-000000000201"),
                "Áo quần cũ", activeVietnameseAId, TransactionType.EXPENSE, EntityStatus.DELETED, "", "", createdAt));
        categoryRepository.save(Category.reconstitute(
                UUID.fromString("00000000-0000-0000-0000-000000000202"),
                "Bún bò", activeVietnameseAId, TransactionType.EXPENSE, EntityStatus.ACTIVE, "", "", createdAt));

        List<CategoryHierarchyResponseDTO> hierarchy =
                CategoryHierarchySupport.buildCategoryHierarchy(needGroupRepository, categoryRepository);

        assertEquals(3, hierarchy.size());
        assertEquals(activeVietnameseAId, hierarchy.get(0).getNeedGroupId());
        assertEquals(activeLaterNameId, hierarchy.get(1).getNeedGroupId());
        assertEquals(deletedEarlyNameId, hierarchy.get(2).getNeedGroupId());

        List<CategoryResponseDTO> firstChildren = hierarchy.get(0).getCategories();
        assertEquals(2, firstChildren.size());
        assertEquals("Bún bò", firstChildren.get(0).getName());
        assertEquals("Áo quần cũ", firstChildren.get(1).getName());
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi kho lưu trữ chưa có nhóm nhu cầu nào")
    void shouldReturnEmptyHierarchyWhenNoGroupsExist() {
        List<CategoryHierarchyResponseDTO> hierarchy =
                CategoryHierarchySupport.buildCategoryHierarchy(needGroupRepository, categoryRepository);

        assertNotNull(hierarchy);
        assertTrue(hierarchy.isEmpty());
    }

    @Test
    @DisplayName("Kiểm tra tham số null cho các phương thức hỗ trợ")
    void shouldRejectNullArguments() {
        UUID groupId = UUID.randomUUID();
        NeedGroup group = new NeedGroup(groupId, "Ăn uống", TransactionType.EXPENSE, "", "");
        Category category = new Category(UUID.randomUUID(), "Cơm", groupId, TransactionType.EXPENSE, "", "");

        assertThrows(NullPointerException.class,
                () -> CategoryHierarchySupport.buildCategoryHierarchy(null, categoryRepository));
        assertThrows(NullPointerException.class,
                () -> CategoryHierarchySupport.buildCategoryHierarchy(needGroupRepository, null));
        assertThrows(NullPointerException.class,
                () -> CategoryHierarchySupport.toResponseDTO(null, group));
        assertThrows(NullPointerException.class,
                () -> CategoryHierarchySupport.toResponseDTO(category, null));
        assertThrows(NullPointerException.class,
                () -> CategoryHierarchySupport.toNeedGroupResponseDTO(null));
    }
}
