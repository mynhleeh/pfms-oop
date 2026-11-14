package com.pfms.service.impl.category;

import com.pfms.service.impl.common.EntityLookupSupport;

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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
        this.transactionRepository = Objects.requireNonNull(transactionRepository, "TransactionRepository không được để null");
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
                UUID.randomUUID(),
                validName.getValue(),
                parentGroup.getNeedGroupId(),
                parentGroup.getTransactionType(),
                "",
                ""
        );

        Category savedCategory = categoryRepository.save(newCategory);
        return toResponseDTO(savedCategory, parentGroup);
    }

    @Override
    public CategoryResponseDTO renameCategory(RenameCategoryRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu đổi tên danh mục không được để null");
        Objects.requireNonNull(request.getCategoryId(), "Mã danh mục không được để null");

        EntityName validName = EntityName.of(request.getName(), "Tên danh mục");
        Category category = EntityLookupSupport.findEntityOrThrow(
                categoryRepository, request.getCategoryId(), EntityLookupSupport.LABEL_CATEGORY);

        if (category.isDeleted()) {
            throw new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, "Danh mục", request.getCategoryId().toString());
        }

        UUID targetGroupId = request.hasNewParentGroup() ? request.getNewParentGroupId() : category.getNeedGroupId();
        NeedGroup targetGroup = EntityLookupSupport.findEntityOrThrow(
                needGroupRepository, targetGroupId, EntityLookupSupport.LABEL_NEED_GROUP);

        if (request.hasNewParentGroup() && !request.getNewParentGroupId().equals(category.getNeedGroupId())) {
            if (targetGroup.isDeleted()) {
                throw new BusinessException(ErrorCode.ERR_CAT_003,
                        "Không thể chuyển danh mục sang nhóm đã xóa.");
            }
            if (targetGroup.getTransactionType() != category.getTransactionType()) {
                throw new BusinessException(ErrorCode.ERR_CAT_001,
                        "Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm khác loại.");
            }
        }

        if (categoryRepository.existsByNameAndNeedGroupIdAndIdNot(validName.getValue(), targetGroupId, category.getCategoryId())) {
            throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Danh mục", validName.getValue());
        }

        if (request.hasNewParentGroup() && !request.getNewParentGroupId().equals(category.getNeedGroupId())) {
            category.changeNeedGroup(targetGroupId, targetGroup.getTransactionType());
        }
        category.rename(validName.getValue());

        Category savedCategory = categoryRepository.save(category);
        return toResponseDTO(savedCategory, targetGroup);
    }

    @Override
    public CategoryResponseDTO restoreCategory(UUID categoryId) {
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");

        Category category = EntityLookupSupport.findEntityOrThrow(
                categoryRepository, categoryId, EntityLookupSupport.LABEL_CATEGORY);
        if (!category.isDeleted()) {
            throw new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, "Danh mục", categoryId.toString());
        }

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
        Category savedCategory = categoryRepository.save(category);

        return toResponseDTO(savedCategory, parentGroup);
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        Objects.requireNonNull(categoryId, "Mã danh mục không được để null");

        Category category = EntityLookupSupport.findEntityOrThrow(
                categoryRepository, categoryId, EntityLookupSupport.LABEL_CATEGORY);
        if (category.isDeleted()) {
            throw new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, "Danh mục", categoryId.toString());
        }

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
        List<NeedGroup> activeGroups = needGroupRepository.findActiveGroups();
        if (activeGroups.isEmpty()) {
            return Collections.emptyList();
        }

        List<Category> activeCategories = categoryRepository.findActiveCategories();
        Map<UUID, List<Category>> categoriesByGroupId = activeCategories.stream()
                .collect(Collectors.groupingBy(Category::getNeedGroupId));

        return activeGroups.stream()
                .sorted(Comparator.comparing((NeedGroup g) -> g.getTransactionType().name())
                        .thenComparing(NeedGroup::getName, String.CASE_INSENSITIVE_ORDER))
                .map(group -> {
                    List<Category> children = categoriesByGroupId.getOrDefault(group.getNeedGroupId(), Collections.emptyList());
                    List<CategoryResponseDTO> childDTOs = children.stream()
                            .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                            .map(cat -> toResponseDTO(cat, group))
                            .toList();

                    return new CategoryHierarchyResponseDTO(
                            group.getTransactionType().name(),
                            group.getNeedGroupId(),
                            group.getName(),
                            group.getStatus().name(),
                            childDTOs
                    );
                })
                .toList();
    }
    // endregion

    // region Helpers
    private CategoryResponseDTO toResponseDTO(Category category, NeedGroup parentGroup) {
        return new CategoryResponseDTO(
                category.getCategoryId(),
                category.getName(),
                parentGroup.getNeedGroupId(),
                parentGroup.getName(),
                category.getTransactionType().name(),
                category.getStatus().name(),
                LocalDateTime.now()
        );
    }
    // endregion
}

