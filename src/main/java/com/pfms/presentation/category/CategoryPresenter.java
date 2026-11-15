package com.pfms.presentation.category;

import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Bộ điều phối giao diện phân hệ Quản lý Danh mục theo mô hình MVP.
 * Đóng gói toàn bộ logic điều phối và xử lý ngoại lệ, 100% không phụ thuộc Swing/AWT.
 */
public class CategoryPresenter {

    // region Fields
    private final CategoryView view;
    private final CategoryGUIController controller;
    // endregion

    // region Constructors
    public CategoryPresenter(CategoryView view, CategoryGUIController controller) {
        this.view = Objects.requireNonNull(view, "CategoryView không được để null");
        this.controller = Objects.requireNonNull(controller, "CategoryGUIController không được để null");
    }
    // endregion

    // region Actions
    public void onAddNeedGroupClicked() { this.view.openCreateNeedGroupDialog(); }

    public void onAddCategoryClicked() { this.view.openCreateCategoryDialog(); }

    public void createNeedGroup(String type, String name) { createNeedGroup(type, name, "", ""); }

    public void createNeedGroup(String type, String name, String icon, String color) {
        try {
            this.controller.handleCreateNeedGroup(type, name, icon, color);
            this.view.showMessage("Đã tạo nhóm nhu cầu thành công!");
            loadData();
        } catch (Exception ex) { this.view.showError(ex.getMessage()); }
    }

    public void createCategory(UUID parentGroupId, String name) {
        try {
            this.controller.handleCreateCategory(parentGroupId, name);
            this.view.showMessage("Đã tạo danh mục con thành công!");
            loadData();
        } catch (Exception ex) { this.view.showError(ex.getMessage()); }
    }

    public void renameNeedGroup(UUID groupId, String currentName) {
        if (groupId == null) { this.view.showWarning("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con trên cây để đổi tên!"); return; }
        String newName = this.view.openRenameDialog(currentName, true);
        if (newName != null && !newName.trim().isEmpty()) {
            try {
                this.controller.handleRenameNeedGroup(groupId, newName.trim());
                this.view.showMessage("Đổi tên nhóm nhu cầu thành công!");
                loadData();
            } catch (Exception ex) { this.view.showError("Đổi tên thất bại: " + ex.getMessage()); }
        }
    }

    public void renameCategory(UUID categoryId, String currentName) {
        if (categoryId == null) { this.view.showWarning("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con trên cây để đổi tên!"); return; }
        String newName = this.view.openRenameDialog(currentName, false);
        if (newName != null && !newName.trim().isEmpty()) {
            try {
                this.controller.handleRenameCategory(categoryId, newName.trim());
                this.view.showMessage("Đổi tên danh mục con thành công!");
                loadData();
            } catch (Exception ex) { this.view.showError("Đổi tên thất bại: " + ex.getMessage()); }
        }
    }

    public void deleteNeedGroup(UUID groupId, String groupName) {
        if (groupId == null) { this.view.showWarning("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con để xóa!"); return; }
        String label = (groupName != null && !groupName.isBlank()) ? "\"" + groupName + "\" " : "";
        if (this.view.confirmAction("Bạn có chắc chắn muốn xóa nhóm nhu cầu " + label + "không?")) {
            try {
                this.controller.handleDeleteNeedGroup(groupId);
                this.view.showMessage("Đã xóa nhóm nhu cầu thành công!");
                loadData();
            } catch (Exception ex) { this.view.showError("Xóa nhóm thất bại: " + ex.getMessage()); }
        }
    }

    public void deleteCategory(UUID categoryId, String categoryName) {
        if (categoryId == null) { this.view.showWarning("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con để xóa!"); return; }
        String label = (categoryName != null && !categoryName.isBlank()) ? "\"" + categoryName + "\" " : "";
        if (this.view.confirmAction("Bạn có chắc chắn muốn xóa danh mục con " + label + "không?")) {
            try {
                this.controller.handleDeleteCategory(categoryId);
                this.view.showMessage("Đã xóa danh mục con thành công!");
                loadData();
            } catch (Exception ex) { this.view.showError("Xóa danh mục thất bại: " + ex.getMessage()); }
        }
    }
    // endregion

    // region Data
    public void loadData() {
        try {
            List<CategoryHierarchyResponseDTO> hierarchy = this.controller.loadCategoryHierarchy();
            if (hierarchy == null) hierarchy = Collections.emptyList();
            int totalGroups = hierarchy.size();
            int totalCats = hierarchy.stream().mapToInt(CategoryHierarchyResponseDTO::getCategoryCount).sum();
            this.view.renderCategoryHierarchy(hierarchy, totalGroups, totalCats);
        } catch (Exception ex) {
            this.view.showError("Không thể tải danh sách danh mục: " + ex.getMessage());
        }
    }
    // endregion

    // region Getters
    public CategoryView getView() { return this.view; }
    public CategoryGUIController getController() { return this.controller; }
    // endregion
}
