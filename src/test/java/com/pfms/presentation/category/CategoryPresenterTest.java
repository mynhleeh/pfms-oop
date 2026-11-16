package com.pfms.presentation.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử CategoryPresenter - Bộ điều phối giao diện phân hệ Danh mục")
class CategoryPresenterTest {

    private CategoryView view;
    private CategoryGUIController controller;
    private CategoryPresenter presenter;

    private UUID group1Id;
    private UUID group2Id;
    private UUID cat1Id;
    private UUID cat2Id;
    private List<CategoryHierarchyResponseDTO> mockHierarchy;

    @BeforeEach
    void setUp() {
        view = Mockito.mock(CategoryView.class);
        controller = Mockito.mock(CategoryGUIController.class);

        group1Id = UUID.randomUUID();
        cat1Id = UUID.randomUUID();
        cat2Id = UUID.randomUUID();
        CategoryResponseDTO cat1 = new CategoryResponseDTO(
                cat1Id, "Ăn uống", group1Id, "Thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO cat2 = new CategoryResponseDTO(
                cat2Id, "Đi lại", group1Id, "Thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());

        CategoryHierarchyResponseDTO group1 = new CategoryHierarchyResponseDTO(
                "EXPENSE", group1Id, "Thiết yếu", "ACTIVE", List.of(cat1, cat2));

        group2Id = UUID.randomUUID();
        CategoryHierarchyResponseDTO group2 = new CategoryHierarchyResponseDTO(
                "INCOME", group2Id, "Thu nhập chính", "ACTIVE", List.of());

        mockHierarchy = List.of(group1, group2);
        when(controller.loadCategoryHierarchy()).thenReturn(mockHierarchy);

        presenter = new CategoryPresenter(view, controller);
    }

    @Test
    @DisplayName("Khởi tạo Presenter ném NullPointerException khi tham số null")
    void testConstructorNullSafety() {
        assertThrows(NullPointerException.class, () -> new CategoryPresenter(null, controller));
        assertThrows(NullPointerException.class, () -> new CategoryPresenter(view, null));
    }

    @Test
    @DisplayName("loadData() lấy phân cấp danh mục và chuyển tiếp cho view hiển thị")
    void testLoadDataSuccess() {
        presenter.loadData();

        verify(controller).loadCategoryHierarchy();
        verify(view).renderCategoryHierarchy(mockHierarchy, 2, 2);
    }

    @Test
    @DisplayName("loadData() khi controller ném lỗi thì gọi view.showError()")
    void testLoadDataException() {
        when(controller.loadCategoryHierarchy()).thenThrow(new RuntimeException("Lỗi kết nối CSDL"));

        presenter.loadData();

        verify(view).showError(contains("Lỗi kết nối CSDL"));
    }

    @Test
    @DisplayName("Điều hướng mở dialog Thêm Nhóm nhu cầu và Thêm Danh mục")
    void testDialogNavigation() {
        presenter.onAddNeedGroupClicked();
        verify(view).openCreateNeedGroupDialog();

        presenter.onAddCategoryClicked();
        verify(view).openCreateCategoryDialog();

        presenter.onAddNeedGroupClicked("INCOME");
        verify(view).openCreateNeedGroupDialog("INCOME");

        presenter.onAddCategoryClicked("INCOME");
        verify(view).openCreateCategoryDialog("INCOME");
    }

    @Test
    @DisplayName("createNeedGroup() thành công hiển thị thông báo và tải lại dữ liệu")
    void testCreateNeedGroupSuccess() {
        presenter.createNeedGroup("EXPENSE", "Tiết kiệm", "piggy", "#00FF00");

        verify(controller).handleCreateNeedGroup("EXPENSE", "Tiết kiệm", "piggy", "#00FF00");
        verify(view).showMessage("Đã tạo nhóm nhu cầu thành công!");
        verify(controller, atLeastOnce()).loadCategoryHierarchy();
    }

    @Test
    @DisplayName("createNeedGroup() khi ném ngoại lệ thì bắt lỗi và gọi view.showError()")
    void testCreateNeedGroupException() {
        doThrow(new BusinessException(ErrorCode.ERR_CAT_001, "Tên nhóm nhu cầu đã tồn tại"))
                .when(controller).handleCreateNeedGroup(anyString(), anyString(), anyString(), anyString());

        presenter.createNeedGroup("EXPENSE", "Trùng lặp", "", "");

        verify(view).showError("Tên nhóm nhu cầu đã tồn tại");
    }

    @Test
    @DisplayName("createCategory() thành công gọi controller và tải lại dữ liệu")
    void testCreateCategorySuccess() {
        presenter.createCategory(group1Id, "Xăng xe");

        verify(controller).handleCreateCategory(group1Id, "Xăng xe");
        verify(view).showMessage("Đã tạo danh mục con thành công!");
        verify(controller, atLeastOnce()).loadCategoryHierarchy();
    }

    @Test
    @DisplayName("createCategory() ném ngoại lệ thì presenter gọi view.showError()")
    void testCreateCategoryException() {
        doThrow(new BusinessException(ErrorCode.ERR_CAT_002, "Tên danh mục con đã tồn tại"))
                .when(controller).handleCreateCategory(any(), anyString());

        presenter.createCategory(group1Id, "Trùng");

        verify(view).showError("Tên danh mục con đã tồn tại");
    }

    @Test
    @DisplayName("renameNeedGroup() người dùng nhập tên mới thì gọi controller và cập nhật")
    void testRenameNeedGroupSuccess() {
        when(view.openRenameDialog("Thiết yếu", true)).thenReturn("Chi tiêu thiết yếu");

        presenter.renameNeedGroup(group1Id, "Thiết yếu");

        verify(view).openRenameDialog("Thiết yếu", true);
        verify(controller).handleRenameNeedGroup(group1Id, "Chi tiêu thiết yếu");
        verify(view).showMessage("Đổi tên nhóm nhu cầu thành công!");
        verify(controller, atLeastOnce()).loadCategoryHierarchy();
    }

    @Test
    @DisplayName("renameNeedGroup() người dùng hủy hoặc nhập chuỗi rỗng thì bỏ qua")
    void testRenameNeedGroupCancelledOrBlank() {
        when(view.openRenameDialog("Thiết yếu", true)).thenReturn(null);
        presenter.renameNeedGroup(group1Id, "Thiết yếu");

        when(view.openRenameDialog("Thiết yếu", true)).thenReturn("   ");
        presenter.renameNeedGroup(group1Id, "Thiết yếu");

        verify(controller, never()).handleRenameNeedGroup(any(), anyString());
    }

    @Test
    @DisplayName("renameNeedGroup() ném lỗi khi cập nhật thì gọi view.showError()")
    void testRenameNeedGroupException() {
        when(view.openRenameDialog("Thiết yếu", true)).thenReturn("Tên mới");
        doThrow(new RuntimeException("Lỗi trùng tên"))
                .when(controller).handleRenameNeedGroup(group1Id, "Tên mới");

        presenter.renameNeedGroup(group1Id, "Thiết yếu");

        verify(view).showError("Đổi tên thất bại: Lỗi trùng tên");
    }

    @Test
    @DisplayName("renameCategory() thành công gọi controller và cập nhật")
    void testRenameCategorySuccess() {
        when(view.openRenameDialog("Ăn uống", false)).thenReturn("Ăn tiệm & Trà sữa");

        presenter.renameCategory(cat1Id, "Ăn uống");

        verify(view).openRenameDialog("Ăn uống", false);
        verify(controller).handleRenameCategory(cat1Id, "Ăn tiệm & Trà sữa");
        verify(view).showMessage("Đổi tên danh mục con thành công!");
        verify(controller, atLeastOnce()).loadCategoryHierarchy();
    }

    @Test
    @DisplayName("renameCategory() người dùng hủy nhập liệu thì không gọi controller")
    void testRenameCategoryCancelled() {
        when(view.openRenameDialog("Ăn uống", false)).thenReturn(null);

        presenter.renameCategory(cat1Id, "Ăn uống");

        verify(controller, never()).handleRenameCategory(any(), anyString());
    }

    @Test
    @DisplayName("renameCategory() ném ngoại lệ thì presenter gọi view.showError()")
    void testRenameCategoryException() {
        when(view.openRenameDialog("Ăn uống", false)).thenReturn("Tên mới");
        doThrow(new RuntimeException("Không tìm thấy danh mục"))
                .when(controller).handleRenameCategory(cat1Id, "Tên mới");

        presenter.renameCategory(cat1Id, "Ăn uống");

        verify(view).showError("Đổi tên thất bại: Không tìm thấy danh mục");
    }

    @Test
    @DisplayName("deleteNeedGroup() khi xác nhận YES gọi controller và cập nhật")
    void testDeleteNeedGroupConfirmed() {
        when(view.confirmAction(anyString())).thenReturn(true);

        presenter.deleteNeedGroup(group1Id, "Thiết yếu");

        verify(view).confirmAction(contains("Thiết yếu"));
        verify(controller).handleDeleteNeedGroup(group1Id);
        verify(view).showMessage("Đã xóa nhóm nhu cầu thành công!");
        verify(controller, atLeastOnce()).loadCategoryHierarchy();
    }

    @Test
    @DisplayName("deleteNeedGroup() khi người dùng hủy NO không gọi controller")
    void testDeleteNeedGroupCancelled() {
        when(view.confirmAction(anyString())).thenReturn(false);

        presenter.deleteNeedGroup(group1Id, "Thiết yếu");

        verify(controller, never()).handleDeleteNeedGroup(any());
        verify(view, never()).showMessage(anyString());
    }

    @Test
    @DisplayName("deleteNeedGroup() ném ngoại lệ thì presenter gọi view.showError()")
    void testDeleteNeedGroupException() {
        when(view.confirmAction(anyString())).thenReturn(true);
        doThrow(new RuntimeException("Còn danh mục con")).when(controller).handleDeleteNeedGroup(group1Id);

        presenter.deleteNeedGroup(group1Id, "Thiết yếu");

        verify(view).showError("Xóa nhóm thất bại: Còn danh mục con");
    }

    @Test
    @DisplayName("deleteCategory() khi xác nhận YES gọi controller và cập nhật")
    void testDeleteCategoryConfirmed() {
        when(view.confirmAction(anyString())).thenReturn(true);

        presenter.deleteCategory(cat1Id, "Ăn uống");

        verify(view).confirmAction(contains("Ăn uống"));
        verify(controller).handleDeleteCategory(cat1Id);
        verify(view).showMessage("Đã xóa danh mục con thành công!");
        verify(controller, atLeastOnce()).loadCategoryHierarchy();
    }

    @Test
    @DisplayName("deleteCategory() khi người dùng hủy NO không gọi controller")
    void testDeleteCategoryCancelled() {
        when(view.confirmAction(anyString())).thenReturn(false);

        presenter.deleteCategory(cat1Id, "Ăn uống");

        verify(controller, never()).handleDeleteCategory(any());
    }

    @Test
    @DisplayName("deleteCategory() ném ngoại lệ thì presenter gọi view.showError()")
    void testDeleteCategoryException() {
        when(view.confirmAction(anyString())).thenReturn(true);
        doThrow(new RuntimeException("Đã phát sinh giao dịch")).when(controller).handleDeleteCategory(cat1Id);

        presenter.deleteCategory(cat1Id, "Ăn uống");

        verify(view).showError("Xóa danh mục thất bại: Đã phát sinh giao dịch");
    }

    @Test
    @DisplayName("Truyền ID null vào các thao tác hiển thị cảnh báo không gọi controller")
    void testNullIdsHandledWithWarning() {
        presenter.renameNeedGroup(null, "Tên");
        presenter.renameCategory(null, "Tên");
        presenter.deleteNeedGroup(null, "Tên");
        presenter.deleteCategory(null, "Tên");

        verify(view, times(2)).showWarning("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con trên cây để đổi tên!");
        verify(view, times(2)).showWarning("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con để xóa!");
        verifyNoInteractions(controller);
    }
}
