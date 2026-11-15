package com.pfms.service;

import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.category.CreateCategoryRequestDTO;
import com.pfms.presentation.dto.category.CreateNeedGroupRequestDTO;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import com.pfms.presentation.dto.category.RenameCategoryRequestDTO;
import com.pfms.presentation.dto.category.RenameNeedGroupRequestDTO;

import java.util.List;
import java.util.UUID;

/**
 * Dịch vụ quản lý nhóm nhu cầu và danh mục thu chi 3 cấp của hệ thống PFMS.
 */
public interface CategoryService {

    // region Commands
    NeedGroupResponseDTO createNeedGroup(CreateNeedGroupRequestDTO request);

    NeedGroupResponseDTO renameNeedGroup(RenameNeedGroupRequestDTO request);

    NeedGroupResponseDTO restoreNeedGroup(UUID needGroupId);

    void deleteNeedGroup(UUID needGroupId);

    CategoryResponseDTO createCategory(CreateCategoryRequestDTO request);

    CategoryResponseDTO renameCategory(RenameCategoryRequestDTO request);

    CategoryResponseDTO restoreCategory(UUID categoryId);

    void deleteCategory(UUID categoryId);
    // endregion

    // region Queries
    List<CategoryHierarchyResponseDTO> getCategoryHierarchy();
    // endregion
}
