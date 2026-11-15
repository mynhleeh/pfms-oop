package com.pfms.presentation.category;

import com.pfms.presentation.common.BaseView;

import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;

import java.util.List;

/**
 * Hợp đồng hiển thị cho phân hệ Quản lý Danh mục trong kiến trúc MVP.
 * 100% không phụ thuộc các thư viện giao diện đồ họa Swing/AWT.
 */
public interface CategoryView extends BaseView {

    void renderCategoryHierarchy(List<CategoryHierarchyResponseDTO> hierarchy, int totalGroups, int totalCategories);

    void openCreateNeedGroupDialog();

    void openCreateCategoryDialog();

    String openRenameDialog(String currentName, boolean isGroup);
}
