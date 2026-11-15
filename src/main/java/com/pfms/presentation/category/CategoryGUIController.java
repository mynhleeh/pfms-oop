package com.pfms.presentation.category;

import com.pfms.common.util.Preconditions;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.category.CreateCategoryRequestDTO;
import com.pfms.presentation.dto.category.CreateNeedGroupRequestDTO;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import com.pfms.presentation.dto.category.RenameCategoryRequestDTO;
import com.pfms.presentation.dto.category.RenameNeedGroupRequestDTO;
import com.pfms.presentation.common.InputValidator;
import com.pfms.service.CategoryService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Bộ điều phối giao diện đồ họa cho phân hệ Quản lý Danh mục (CAT).
 * Tiếp nhận dữ liệu biểu mẫu, kiểm thực đầu vào và điều phối tương tác với CategoryService.
 */
public final class CategoryGUIController {

    // region State
    private final CategoryService categoryService;
    // endregion

    // region Constructors
    public CategoryGUIController(CategoryService categoryService) {
        this.categoryService = Objects.requireNonNull(categoryService, "CategoryService không được để null");
    }
    // endregion

    // region Operations
    // region Create Need Group
    public NeedGroupResponseDTO handleCreateNeedGroup(String rawType, String rawName, String rawIcon, String rawColor) {
        String type = InputValidator.validateTransactionType(rawType, null);
        String name = InputValidator.validateRequiredText(rawName, 50, "Tên nhóm nhu cầu");
        String icon = InputValidator.validateTextLength(rawIcon, 50, "Biểu tượng");
        String color = InputValidator.validateTextLength(rawColor, 20, "Mã màu");

        CreateNeedGroupRequestDTO request = new CreateNeedGroupRequestDTO(type, name, icon, color);
        return this.categoryService.createNeedGroup(request);
    }

    public NeedGroupResponseDTO handleCreateNeedGroup(String rawType, String rawName) {
        return handleCreateNeedGroup(rawType, rawName, "", "");
    }
    // endregion

    // region Rename Need Group
    public NeedGroupResponseDTO handleRenameNeedGroup(
            UUID needGroupId, String rawName, String rawIcon, String rawColor) {

        Preconditions.checkArgument(needGroupId != null, "Mã nhóm nhu cầu không được để null");
        String name = InputValidator.validateRequiredText(rawName, 50, "Tên nhóm nhu cầu");
        String icon = InputValidator.validateTextLength(rawIcon, 50, "Biểu tượng");
        String color = InputValidator.validateTextLength(rawColor, 20, "Mã màu");

        RenameNeedGroupRequestDTO request = new RenameNeedGroupRequestDTO(needGroupId, name, icon, color);
        return this.categoryService.renameNeedGroup(request);
    }

    public NeedGroupResponseDTO handleRenameNeedGroup(UUID needGroupId, String rawName) {
        return handleRenameNeedGroup(needGroupId, rawName, "", "");
    }
    // endregion

    // region Restore Need Group
    public NeedGroupResponseDTO handleRestoreNeedGroup(UUID needGroupId) {
        Preconditions.checkArgument(needGroupId != null, "Mã nhóm nhu cầu không được để null");
        return this.categoryService.restoreNeedGroup(needGroupId);
    }
    // endregion

    // region Delete Need Group
    public void handleDeleteNeedGroup(UUID needGroupId) {
        Preconditions.checkArgument(needGroupId != null, "Mã nhóm nhu cầu không được để null");
        this.categoryService.deleteNeedGroup(needGroupId);
    }
    // endregion

    // region Create Category
    public CategoryResponseDTO handleCreateCategory(UUID parentGroupId, String rawName) {
        Preconditions.checkArgument(parentGroupId != null, "Mã nhóm nhu cầu cha không được để null");
        String name = InputValidator.validateRequiredText(rawName, 50, "Tên danh mục");

        CreateCategoryRequestDTO request = new CreateCategoryRequestDTO(parentGroupId, name);
        return this.categoryService.createCategory(request);
    }
    // endregion

    // region Rename Category
    public CategoryResponseDTO handleRenameCategory(UUID categoryId, String rawName, UUID newParentGroupId) {
        Preconditions.checkArgument(categoryId != null, "Mã danh mục không được để null");
        String name = InputValidator.validateRequiredText(rawName, 50, "Tên danh mục");

        RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(categoryId, name, newParentGroupId);
        return this.categoryService.renameCategory(request);
    }

    public CategoryResponseDTO handleRenameCategory(UUID categoryId, String rawName) {
        return handleRenameCategory(categoryId, rawName, null);
    }
    // endregion

    // region Restore Category
    public CategoryResponseDTO handleRestoreCategory(UUID categoryId) {
        Preconditions.checkArgument(categoryId != null, "Mã danh mục không được để null");
        return this.categoryService.restoreCategory(categoryId);
    }
    // endregion

    // region Delete Category
    public void handleDeleteCategory(UUID categoryId) {
        Preconditions.checkArgument(categoryId != null, "Mã danh mục không được để null");
        this.categoryService.deleteCategory(categoryId);
    }
    // endregion

    // region Hierarchy
    public List<CategoryHierarchyResponseDTO> loadCategoryHierarchy() {
        return this.categoryService.getCategoryHierarchy();
    }
    // endregion
    // endregion
}
