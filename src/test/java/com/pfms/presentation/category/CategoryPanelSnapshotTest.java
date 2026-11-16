package com.pfms.presentation.category;

import com.pfms.presentation.common.SwingSnapshotHelper;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử thị giác CategoryPanelSnapshotTest - Xuất ảnh chụp phân hệ Danh mục chuẩn preview")
class CategoryPanelSnapshotTest {

    @Test
    @DisplayName("Xuất snapshot màn hình danh mục sang target/snapshots/category-panel-redesign.png thành công")
    void testExportCategorySnapshot() throws IOException {
        CategoryGUIController controller = Mockito.mock(CategoryGUIController.class);

        // Group 1: Thiết yếu
        UUID g1Id = UUID.randomUUID();
        CategoryResponseDTO c1 = new CategoryResponseDTO(UUID.randomUUID(), "Ăn uống hàng ngày", g1Id, "Nhu cầu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c2 = new CategoryResponseDTO(UUID.randomUUID(), "Tiền thuê nhà & bảo trì", g1Id, "Nhu cầu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c3 = new CategoryResponseDTO(UUID.randomUUID(), "Hóa đơn điện, nước, net", g1Id, "Nhu cầu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c4 = new CategoryResponseDTO(UUID.randomUUID(), "Xăng xe & di chuyển", g1Id, "Nhu cầu thiết yếu", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryHierarchyResponseDTO g1 = new CategoryHierarchyResponseDTO("EXPENSE", g1Id, "Nhu cầu thiết yếu", "ACTIVE", List.of(c1, c2, c3, c4));

        // Group 2: Phát triển & Giải trí
        UUID g2Id = UUID.randomUUID();
        CategoryResponseDTO c5 = new CategoryResponseDTO(UUID.randomUUID(), "Khóa học & Sách vở", g2Id, "Giải trí & Phát triển", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c6 = new CategoryResponseDTO(UUID.randomUUID(), "Cafe & Giao lưu bạn bè", g2Id, "Giải trí & Phát triển", "EXPENSE", "ACTIVE", LocalDateTime.now());
        CategoryHierarchyResponseDTO g2 = new CategoryHierarchyResponseDTO("EXPENSE", g2Id, "Giải trí & Phát triển", "ACTIVE", List.of(c5, c6));

        // Group 3: Thu nhập chủ động
        UUID g3Id = UUID.randomUUID();
        CategoryResponseDTO c7 = new CategoryResponseDTO(UUID.randomUUID(), "Lương cố định hàng tháng", g3Id, "Thu nhập chủ động", "INCOME", "ACTIVE", LocalDateTime.now());
        CategoryResponseDTO c8 = new CategoryResponseDTO(UUID.randomUUID(), "Thưởng hiệu quả & Dự án", g3Id, "Thu nhập chủ động", "INCOME", "ACTIVE", LocalDateTime.now());
        CategoryHierarchyResponseDTO g3 = new CategoryHierarchyResponseDTO("INCOME", g3Id, "Thu nhập chủ động", "ACTIVE", List.of(c7, c8));

        when(controller.loadCategoryHierarchy()).thenReturn(List.of(g1, g2, g3));

        CategoryPanel panel = new CategoryPanel(controller);

        Path snapshotPath = SwingSnapshotHelper.capture(panel, 1340, 780, "category-panel-redesign.png");
        assertNotNull(snapshotPath);
        assertTrue(Files.exists(snapshotPath));
    }
}
