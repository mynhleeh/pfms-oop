package com.pfms.service.impl.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DuplicateEntityException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.EntityName;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.GenericRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.category.CreateCategoryRequestDTO;
import com.pfms.presentation.dto.category.CreateNeedGroupRequestDTO;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import com.pfms.presentation.dto.category.RenameCategoryRequestDTO;
import com.pfms.presentation.dto.category.RenameNeedGroupRequestDTO;
import com.pfms.service.CategoryService;
import com.pfms.service.impl.common.EntityLookupSupport;
import com.pfms.service.impl.common.RollbackSupport;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Cài đặt dịch vụ quản lý nhóm nhu cầu và danh mục thu chi 3 cấp ({@link CategoryService}).
 */
public class CategoryServiceImpl implements CategoryService {

    // region State
    private final CategoryRepository categoryRepository;
    private final NeedGroupRepository needGroupRepository;
    private final TransactionRepository transactionRepository;
    // endregion

    // region Constructors
    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            NeedGroupRepository needGroupRepository,
            TransactionRepository transactionRepository) {

        this.categoryRepository = Objects.requireNonNull(categoryRepository, "CategoryRepository không được để null");
        this.needGroupRepository = Objects.requireNonNull(needGroupRepository, "NeedGroupRepository không được để null");
        this.transactionRepository = Objects.requireNonNull(
                transactionRepository, "TransactionRepository không được để null");
    }
    // endregion

    // region Commands - NeedGroup
    @Override
    public NeedGroupResponseDTO createNeedGroup(CreateNeedGroupRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu tạo nhóm nhu cầu không được để null");

        TransactionType transactionType = parseTransactionType(request.getTransactionType());
        EntityName validName = EntityName.of(request.getName(), "Tên nhóm nhu cầu");

        if (needGroupRepository.existsByNameAndType(validName.getValue(), transactionType)) {
            throw new DuplicateEntityException(
                    ErrorCode.ERR_NAM_002, EntityLookupSupport.LABEL_NEED_GROUP, validName.getValue());
        }

        NeedGroup newGroup = new NeedGroup(
                UUID.randomUUID(), validName.getValue(), transactionType, request.getIcon(), request.getColor());

        return CategoryHierarchySupport.toNeedGroupResponseDTO(needGroupRepository.save(newGroup));
    }

    @Override
    public NeedGroupResponseDTO renameNeedGroup(RenameNeedGroupRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu đổi tên nhóm nhu cầu không được để null");
        Objects.requireNonNull(request.getNeedGroupId(), "Mã nhóm nhu cầu không được để null");

        EntityName validName = EntityName.of(request.getName(), "Tên nhóm nhu cầu");
        NeedGroup group = loadEntityByDeletedState(
                needGroupRepository, request.getNeedGroupId(),
                EntityLookupSupport.LABEL_NEED_GROUP, NeedGroup::isDeleted, false);

        if (needGroupRepository.existsByNameAndTypeAndIdNot(
                validName.getValue(), group.getTransactionType(), group.getNeedGroupId())) {
            throw new DuplicateEntityException(
                    ErrorCode.ERR_NAM_002, EntityLookupSupport.LABEL_NEED_GROUP, validName.getValue());
        }

        group.rename(validName.getValue());
        if (request.hasAppearanceUpdate()) {
            String targetIcon = (request.getIcon() != null) ? request.getIcon() : group.getIcon();
            String targetColor = (request.getColor() != null) ? request.getColor() : group.getColor();
            group.updateAppearance(targetIcon, targetColor);
        }

        return CategoryHierarchySupport.toNeedGroupResponseDTO(needGroupRepository.save(group));
    }

    @Override
    public NeedGroupResponseDTO restoreNeedGroup(UUID needGroupId) {
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        NeedGroup group = loadEntityByDeletedState(
                needGroupRepository, needGroupId,
                EntityLookupSupport.LABEL_NEED_GROUP, NeedGroup::isDeleted, true);

        if (needGroupRepository.existsByNameAndType(group.getName(), group.getTransactionType())) {
            throw new DuplicateEntityException(
                    ErrorCode.ERR_NAM_002, EntityLookupSupport.LABEL_NEED_GROUP, group.getName());
        }

        group.restore();
        return CategoryHierarchySupport.toNeedGroupResponseDTO(needGroupRepository.save(group));
    }

    @Override
    public void deleteNeedGroup(UUID needGroupId) {
        Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        NeedGroup group = loadEntityByDeletedState(
                needGroupRepository, needGroupId,
                EntityLookupSupport.LABEL_NEED_GROUP, NeedGroup::isDeleted, false);

        long activeGroupCount = needGroupRepository.countActiveByType(group.getTransactionType());
        if (activeGroupCount <= 1) {
            throw new BusinessException(
                    ErrorCode.ERR_CAT_002,
                    "Không thể xóa nhóm cuối cùng của loại thu chi này.");
        }

        List<Category> childCategories = categoryRepository.findByNeedGroupId(needGroupId);
        List<Category> childSnapshots = childCategories.stream()
                .map(c -> Category.reconstitute(
                        c.getCategoryId(), c.getName(), c.getNeedGroupId(),
                        c.getTransactionType(), c.getStatus(), c.getIcon(), c.getColor(), c.getCreatedAt()))
                .toList();
        NeedGroup groupSnapshot = NeedGroup.reconstitute(
                group.getNeedGroupId(), group.getName(), group.getTransactionType(),
                group.getStatus(), group.getIcon(), group.getColor(), group.getCreatedAt());

        boolean keepGroupSoftDeleted = false;
        try {
            for (Category child : childCategories) {
                if (transactionRepository.existsByCategoryId(child.getCategoryId())) {
                    if (!child.isDeleted()) {
                        child.markDeleted();
                        categoryRepository.save(child);
                    }
                    keepGroupSoftDeleted = true;
                } else {
                    categoryRepository.deleteById(child.getCategoryId());
                }
            }

            if (keepGroupSoftDeleted) {
                group.markDeleted((int) activeGroupCount);
                needGroupRepository.save(group);
            } else {
                needGroupRepository.deleteById(needGroupId);
            }
        } catch (StorageException storageException) {
            for (Category childSnapshot : childSnapshots) {
                RollbackSupport.runRollbackStep(() -> categoryRepository.save(childSnapshot), storageException);
            }
            RollbackSupport.runRollbackStep(() -> needGroupRepository.save(groupSnapshot), storageException);
            throw storageException;
        }
    }
    // endregion

    // region Commands - Category
    @Override
    public CategoryResponseDTO createCategory(CreateCategoryRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu tạo danh mục không được để null");

        EntityName validName = EntityName.of(request.getName(), "Tên danh mục");
        NeedGroup parentGroup = EntityLookupSupport.findEntityOrThrow(
                needGroupRepository, request.getParentGroupId(), EntityLookupSupport.LABEL_NEED_GROUP);

        if (parentGroup.isDeleted()) {
            throw new BusinessException(ErrorCode.ERR_CAT_005,
                    "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng.");
        }
        if (categoryRepository.existsByNameAndNeedGroupId(validName.getValue(), parentGroup.getNeedGroupId())) {
            throw new DuplicateEntityException(
                    ErrorCode.ERR_NAM_002, EntityLookupSupport.LABEL_CATEGORY, validName.getValue());
        }

        Category newCategory = new Category(
                UUID.randomUUID(), validName.getValue(), parentGroup.getNeedGroupId(),
                parentGroup.getTransactionType(), "", "");

        return CategoryHierarchySupport.toResponseDTO(categoryRepository.save(newCategory), parentGroup);
    }

    @Override
    public CategoryResponseDTO renameCategory(RenameCategoryRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu đổi tên danh mục không được để null");
        Objects.requireNonNull(request.getCategoryId(), "Mã danh mục không được để null");

        EntityName validName = EntityName.of(request.getName(), "Tên danh mục");
        Category category = loadEntityByDeletedState(
                categoryRepository, request.getCategoryId(),
                EntityLookupSupport.LABEL_CATEGORY, Category::isDeleted, false);

        UUID targetGroupId = request.hasNewParentGroup() ? request.getNewParentGroupId() : category.getNeedGroupId();
        NeedGroup targetGroup = EntityLookupSupport.findEntityOrThrow(
                needGroupRepository, targetGroupId, EntityLookupSupport.LABEL_NEED_GROUP);

        boolean movingGroup = request.hasNewParentGroup() && !targetGroupId.equals(category.getNeedGroupId());
        if (movingGroup) {
            validateGroupMove(category, targetGroup);
        }
        if (categoryRepository.existsByNameAndNeedGroupIdAndIdNot(
                validName.getValue(), targetGroupId, category.getCategoryId())) {
            throw new DuplicateEntityException(
                    ErrorCode.ERR_NAM_002, EntityLookupSupport.LABEL_CATEGORY, validName.getValue());
        }
        if (movingGroup) {
            category.changeNeedGroup(targetGroupId, targetGroup.getTransactionType());
        }
        category.rename(validName.getValue());

        return CategoryHierarchySupport.toResponseDTO(categoryRepository.save(category), targetGroup);
    }

    @Override
    public CategoryResponseDTO restoreCategory(UUID categoryId) {
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        Category category = loadEntityByDeletedState(
                categoryRepository, categoryId,
                EntityLookupSupport.LABEL_CATEGORY, Category::isDeleted, true);

        NeedGroup parentGroup = EntityLookupSupport.findEntityOrThrow(
                needGroupRepository, category.getNeedGroupId(), EntityLookupSupport.LABEL_NEED_GROUP);

        if (parentGroup.isDeleted()) {
            throw new BusinessException(ErrorCode.ERR_CAT_004,
                    "Nhóm chứa danh mục này đang ở trạng thái đã xóa. Hãy khôi phục nhóm trước.");
        }
        if (categoryRepository.existsByNameAndNeedGroupId(category.getName(), category.getNeedGroupId())) {
            throw new DuplicateEntityException(
                    ErrorCode.ERR_NAM_002, EntityLookupSupport.LABEL_CATEGORY, category.getName());
        }

        category.restore(parentGroup.isActive());
        return CategoryHierarchySupport.toResponseDTO(categoryRepository.save(category), parentGroup);
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        Category category = loadEntityByDeletedState(
                categoryRepository, categoryId,
                EntityLookupSupport.LABEL_CATEGORY, Category::isDeleted, false);

        if (transactionRepository.existsByCategoryId(categoryId)) {
            category.markDeleted();
            categoryRepository.save(category);
        } else {
            categoryRepository.deleteById(categoryId);
        }
    }
    // endregion

    // region Queries
    @Override
    public List<CategoryHierarchyResponseDTO> getCategoryHierarchy() {
        return CategoryHierarchySupport.buildCategoryHierarchy(needGroupRepository, categoryRepository);
    }
    // endregion

    // region Helpers
    private static <T> T loadEntityByDeletedState(
            GenericRepository<T, UUID> repository,
            UUID id,
            String label,
            Predicate<T> deletedCheck,
            boolean expectedDeleted) {

        T entity = EntityLookupSupport.findEntityOrThrow(repository, id, label);
        if (deletedCheck.test(entity) != expectedDeleted) {
            throw new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, label, id.toString());
        }
        return entity;
    }

    private static TransactionType parseTransactionType(String rawType) {
        try {
            return TransactionType.fromString(rawType);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(
                    ErrorCode.ERR_CAT_001,
                    "Loại thu chi không hợp lệ. Chỉ hỗ trợ INCOME hoặc EXPENSE.");
        }
    }

    private static void validateGroupMove(Category category, NeedGroup targetGroup) {
        if (targetGroup.isDeleted()) {
            throw new BusinessException(ErrorCode.ERR_CAT_003, "Không thể chuyển danh mục sang nhóm đã xóa.");
        }
        if (targetGroup.getTransactionType() != category.getTransactionType()) {
            throw new BusinessException(ErrorCode.ERR_CAT_001,
                    "Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm khác loại.");
        }
    }
    // endregion
}
