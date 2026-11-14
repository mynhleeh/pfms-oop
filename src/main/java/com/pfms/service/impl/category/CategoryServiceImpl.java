package com.pfms.service.impl.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DuplicateEntityException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.EntityName;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.category.CreateCategoryRequestDTO;
import com.pfms.presentation.dto.category.RenameCategoryRequestDTO;
import com.pfms.service.CategoryService;
import com.pfms.service.impl.common.EntityLookupSupport;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Cài đặt dịch vụ quản lý danh mục thu chi ({@link CategoryService}).
 * Đảm bảo tính duy nhất của tên theo nhóm cha và thực thi xóa mềm bảo toàn lịch sử giao dịch.
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

    // region Commands
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
            throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Danh mục", validName.getValue());
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
        Category category = loadCategoryAndCheckDeleted(request.getCategoryId(), false);

        UUID targetGroupId = request.hasNewParentGroup() ? request.getNewParentGroupId() : category.getNeedGroupId();
        NeedGroup targetGroup = EntityLookupSupport.findEntityOrThrow(
                needGroupRepository, targetGroupId, EntityLookupSupport.LABEL_NEED_GROUP);

        boolean movingGroup = request.hasNewParentGroup() && !targetGroupId.equals(category.getNeedGroupId());
        if (movingGroup) {
            validateGroupMove(category, targetGroup);
        }
        if (categoryRepository.existsByNameAndNeedGroupIdAndIdNot(
                validName.getValue(), targetGroupId, category.getCategoryId())) {
            throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Danh mục", validName.getValue());
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
        Category category = loadCategoryAndCheckDeleted(categoryId, true);

        NeedGroup parentGroup = EntityLookupSupport.findEntityOrThrow(
                needGroupRepository, category.getNeedGroupId(), EntityLookupSupport.LABEL_NEED_GROUP);

        if (parentGroup.isDeleted()) {
            throw new BusinessException(ErrorCode.ERR_CAT_004,
                    "Nhóm chứa danh mục này đang ở trạng thái đã xóa. Hãy khôi phục nhóm trước.");
        }
        if (categoryRepository.existsByNameAndNeedGroupId(category.getName(), category.getNeedGroupId())) {
            throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Danh mục", category.getName());
        }

        category.restore(parentGroup.isActive());
        return CategoryHierarchySupport.toResponseDTO(categoryRepository.save(category), parentGroup);
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        Category category = loadCategoryAndCheckDeleted(categoryId, false);

        long activeCount = categoryRepository.countActiveByNeedGroupId(category.getNeedGroupId());
        if (activeCount <= 1) {
            throw new BusinessException(ErrorCode.ERR_CAT_002,
                    "Không thể xóa danh mục hoạt động cuối cùng của nhóm nhu cầu này.");
        }

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
    private Category loadCategoryAndCheckDeleted(UUID categoryId, boolean expectedDeleted) {
        Category category = EntityLookupSupport.findEntityOrThrow(
                categoryRepository, categoryId, EntityLookupSupport.LABEL_CATEGORY);
        if (category.isDeleted() != expectedDeleted) {
            throw new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, "Danh mục", categoryId.toString());
        }
        return category;
    }

    private void validateGroupMove(Category category, NeedGroup targetGroup) {
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
