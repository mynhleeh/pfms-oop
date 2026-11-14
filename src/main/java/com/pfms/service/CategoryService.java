package com.pfms.service;

import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.category.CreateCategoryRequestDTO;
import com.pfms.presentation.dto.category.RenameCategoryRequestDTO;

import java.util.List;
import java.util.UUID;

/**
 * Dịch vụ quản lý danh mục thu chi của hệ thống PFMS.
 * Điều phối các nghiệp vụ: tạo mới, đổi tên, xóa mềm và khôi phục danh mục 3 cấp.
 */
public interface CategoryService {

    // region Commands
    CategoryResponseDTO createCategory(CreateCategoryRequestDTO request);

    CategoryResponseDTO renameCategory(RenameCategoryRequestDTO request);

    CategoryResponseDTO restoreCategory(UUID categoryId);

    void deleteCategory(UUID categoryId);
    // endregion

    // region Queries
    List<CategoryHierarchyResponseDTO> getCategoryHierarchy();
    // endregion
}

