package com.pfms.presentation.category.component;

import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử thành phần hiển thị thẻ nhóm nhu cầu (CategoryGroupCardNode)")
class CategoryGroupCardNodeTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Khởi tạo thẻ nhóm nhu cầu hiển thị đầy đủ tiêu đề, huy hiệu loại và danh sách danh mục con")
    void testGroupCardWithSubCategories() {
        JavaFxTestHelper.runOnFxThread(() -> {
            UUID groupId = UUID.randomUUID();
            CategoryResponseDTO cat1 = new CategoryResponseDTO(
                    UUID.randomUUID(), "Ăn Uống", groupId, "Nhu Cầu Thiết Yếu",
                    "EXPENSE", "ACTIVE"
            );
            CategoryResponseDTO cat2 = new CategoryResponseDTO(
                    UUID.randomUUID(), "Tiền Thuê Nhà", groupId, "Nhu Cầu Thiết Yếu",
                    "EXPENSE", "ACTIVE"
            );

            CategoryHierarchyResponseDTO group = new CategoryHierarchyResponseDTO(
                    "EXPENSE", groupId, "Nhu Cầu Thiết Yếu", "ACTIVE", List.of(cat1, cat2)
            );

            CategoryGroupCardNode card = new CategoryGroupCardNode(group, null, null, null, null, null);

            assertEquals("Nhu Cầu Thiết Yếu", card.getTitleLabel().getText());
            assertEquals("CHI TIÊU", card.getTypeBadge().getText());
            assertEquals("2 danh mục con", card.getCountBadge().getText());
            assertEquals(2, card.getSubListContainer().getChildren().size());
            assertNotNull(card.getBtnAddSub());
            assertNotNull(card.getBtnRename());
            assertNotNull(card.getBtnDelete());
        });
    }

    @Test
    @DisplayName("Thẻ nhóm rỗng hiển thị thông báo chưa có danh mục con")
    void testEmptyGroupCard() {
        JavaFxTestHelper.runOnFxThread(() -> {
            CategoryHierarchyResponseDTO emptyGroup = new CategoryHierarchyResponseDTO(
                    "INCOME", UUID.randomUUID(), "Thu Nhập Thụ Động", "ACTIVE", List.of()
            );

            CategoryGroupCardNode card = new CategoryGroupCardNode(emptyGroup, null, null, null, null, null);

            assertEquals("THU NHẬP", card.getTypeBadge().getText());
            assertEquals("0 danh mục con", card.getCountBadge().getText());
            assertEquals(1, card.getSubListContainer().getChildren().size());
        });
    }

    @Test
    @DisplayName("Các nút thao tác Đổi tên, Xóa, Thêm con kích hoạt callback chính xác")
    void testActionButtonsTriggerCallbacks() {
        JavaFxTestHelper.runOnFxThread(() -> {
            AtomicBoolean addSubCalled = new AtomicBoolean(false);
            AtomicBoolean renameCalled = new AtomicBoolean(false);
            AtomicBoolean deleteCalled = new AtomicBoolean(false);

            CategoryHierarchyResponseDTO group = new CategoryHierarchyResponseDTO(
                    "EXPENSE", UUID.randomUUID(), "Nhóm Test", "ACTIVE", List.of()
            );

            CategoryGroupCardNode card = new CategoryGroupCardNode(
                    group,
                    g -> addSubCalled.set(true),
                    g -> renameCalled.set(true),
                    g -> deleteCalled.set(true),
                    null,
                    null
            );

            card.getBtnAddSub().fire();
            assertTrue(addSubCalled.get());

            card.getBtnRename().fire();
            assertTrue(renameCalled.get());

            card.getBtnDelete().fire();
            assertTrue(deleteCalled.get());
        });
    }
}
