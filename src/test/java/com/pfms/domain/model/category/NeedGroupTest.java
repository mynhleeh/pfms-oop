package com.pfms.domain.model.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for NeedGroup Entity")
class NeedGroupTest {

    private UUID needGroupId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        needGroupId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
    }

    // =========================================================================
    // Nhóm 1: Happy Path (Khởi tạo và luồng chuẩn)
    // =========================================================================
    @Nested
    @DisplayName("Group 1: Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo nhóm nhu cầu hợp lệ với đầy đủ tham số")
        void testValidConstructionWithFullArguments() {
            NeedGroup group = new NeedGroup(
                    needGroupId,
                    "Nhu cầu thiết yếu",
                    TransactionType.EXPENSE,
                    "essential-icon",
                    "#FF5733"
            );

            assertEquals(needGroupId, group.getNeedGroupId());
            assertEquals("Nhu cầu thiết yếu", group.getName());
            assertEquals(TransactionType.EXPENSE, group.getTransactionType());
            assertEquals(EntityStatus.ACTIVE, group.getStatus());
            assertEquals("essential-icon", group.getIcon());
            assertEquals("#FF5733", group.getColor());
            assertNotNull(group.getCreatedAt());
            assertTrue(group.isActive());
            assertFalse(group.isDeleted());
            assertTrue(group.getCategories().isEmpty());
        }

        @Test
        @DisplayName("Tái tạo nhóm nhu cầu từ dữ liệu lưu trữ giữ nguyên trạng thái DELETED và thời điểm tạo lịch sử")
        void testReconstituteDeletedGroupWithHistoricalCreatedAt() {
            LocalDateTime historicalTime = LocalDateTime.of(2024, 3, 15, 10, 45, 0);

            NeedGroup group = NeedGroup.reconstitute(
                    needGroupId,
                    "Giải trí cũ",
                    TransactionType.EXPENSE,
                    EntityStatus.DELETED,
                    "gamepad",
                    "#9C27B0",
                    historicalTime
            );

            assertEquals(needGroupId, group.getNeedGroupId());
            assertEquals("Giải trí cũ", group.getName());
            assertEquals(TransactionType.EXPENSE, group.getTransactionType());
            assertEquals(EntityStatus.DELETED, group.getStatus());
            assertTrue(group.isDeleted());
            assertFalse(group.isActive());
            assertEquals("gamepad", group.getIcon());
            assertEquals("#9C27B0", group.getColor());
            assertEquals(historicalTime, group.getCreatedAt());
            assertTrue(group.getCategories().isEmpty());
        }

        @Test
        @DisplayName("Khởi tạo nhóm thu nhập với icon và màu null")
        void testValidIncomeGroupWithNullIconAndColor() {
            NeedGroup group = new NeedGroup(
                    needGroupId,
                    "Thu nhập chính",
                    TransactionType.INCOME,
                    null,
                    null
            );

            assertEquals("Thu nhập chính", group.getName());
            assertEquals(TransactionType.INCOME, group.getTransactionType());
            assertEquals("", group.getIcon());
            assertEquals("", group.getColor());
            assertTrue(group.isActive());
        }

        @Test
        @DisplayName("Đổi tên nhóm nhu cầu hợp lệ")
        void testRenameValid() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Chi tiêu hàng ngày", TransactionType.EXPENSE, null, null
            );

            group.rename("Chi phí sinh hoạt");
            assertEquals("Chi phí sinh hoạt", group.getName());
        }

        @Test
        @DisplayName("Cập nhật biểu tượng và màu sắc hợp lệ")
        void testUpdateAppearance() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Ăn uống", TransactionType.EXPENSE, "old-icon", "#000000"
            );

            group.updateAppearance("new-icon", "#FFFFFF");
            assertEquals("new-icon", group.getIcon());
            assertEquals("#FFFFFF", group.getColor());
        }

        @Test
        @DisplayName("Thêm và xóa danh mục con hợp lệ")
        void testAddAndRemoveCategoryValid() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Nhu cầu thiết yếu", TransactionType.EXPENSE, null, null
            );
            Category category = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );

            group.addCategory(category);
            assertEquals(1, group.getCategories().size());
            assertTrue(group.getCategories().contains(category));

            group.removeCategory(category);
            assertTrue(group.getCategories().isEmpty());
        }

        @Test
        @DisplayName("Khôi phục trạng thái nhóm từ DELETED về ACTIVE")
        void testRestoreStateTransition() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Giải trí", TransactionType.EXPENSE, null, null
            );
            group.markDeleted(2);
            assertTrue(group.isDeleted());
            assertFalse(group.isActive());

            group.restore();
            assertTrue(group.isActive());
            assertFalse(group.isDeleted());
        }
    }

    // =========================================================================
    // Nhóm 2: Boundary & Normalization (Chuẩn hóa chuỗi và biên giá trị)
    // =========================================================================
    @Nested
    @DisplayName("Group 2: Boundary and Normalization Tests")
    class BoundaryAndNormalizationTests {

        @Test
        @DisplayName("Tự động cắt khoảng trắng ở hai đầu tên nhóm")
        void testNameTrimming() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "   Đầu tư tích lũy   ", TransactionType.EXPENSE, "  icon  ", "  #123456  "
            );

            assertEquals("Đầu tư tích lũy", group.getName());
            assertEquals("icon", group.getIcon());
            assertEquals("#123456", group.getColor());
        }

        @Test
        @DisplayName("Tên nhóm đạt biên tối thiểu (1 ký tự)")
        void testMinBoundaryName() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "A", TransactionType.EXPENSE, null, null
            );
            assertEquals("A", group.getName());
        }

        @Test
        @DisplayName("Tên nhóm đạt biên tối đa (50 ký tự)")
        void testMaxBoundaryName() {
            String exact50Chars = "N".repeat(50);
            NeedGroup group = new NeedGroup(
                    needGroupId, exact50Chars, TransactionType.EXPENSE, null, null
            );
            assertEquals(exact50Chars, group.getName());
        }

        @Test
        @DisplayName("Icon đạt biên tối đa 50 ký tự và Color đạt biên tối đa 10 ký tự")
        void testMaxBoundaryIconAndColor() {
            String icon50 = "i".repeat(50);
            String color10 = "c".repeat(10);
            NeedGroup group = new NeedGroup(
                    needGroupId, "Đầu tư", TransactionType.EXPENSE, icon50, color10
            );

            assertEquals(icon50, group.getIcon());
            assertEquals(color10, group.getColor());
        }
    }

    // =========================================================================
    // Nhóm 3: Validation & Negative (Kiểm tra dữ liệu rác và tham số lỗi)
    // =========================================================================
    @Nested
    @DisplayName("Group 3: Validation and Negative Tests")
    class ValidationAndNegativeTests {

        @Test
        @DisplayName("Từ chối khởi tạo khi needGroupId là null")
        void testNullNeedGroupIdThrowsException() {
            assertThrows(NullPointerException.class, () -> new NeedGroup(
                    null, "Nhu cầu", TransactionType.EXPENSE, null, null
            ));
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi transactionType là null")
        void testNullTransactionTypeThrowsException() {
            assertThrows(NullPointerException.class, () -> new NeedGroup(
                    needGroupId, "Nhu cầu", null, null, null
            ));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Từ chối khởi tạo khi tên rỗng hoặc chỉ có khoảng trắng")
        void testBlankNameThrowsException(String invalidName) {
            BusinessException ex = assertThrows(BusinessException.class, () -> new NeedGroup(
                    needGroupId, invalidName, TransactionType.EXPENSE, null, null
            ));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi tên vượt quá 50 ký tự")
        void testNameOver50CharsThrowsException() {
            String over50Chars = "A".repeat(51);
            BusinessException ex = assertThrows(BusinessException.class, () -> new NeedGroup(
                    needGroupId, over50Chars, TransactionType.EXPENSE, null, null
            ));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối đổi tên khi tên mới vượt quá 50 ký tự")
        void testRenameOver50CharsThrowsException() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Chi tiêu", TransactionType.EXPENSE, null, null
            );
            String over50Chars = "X".repeat(51);
            BusinessException ex = assertThrows(BusinessException.class, () -> group.rename(over50Chars));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối icon vượt quá 50 ký tự")
        void testIconOver50CharsThrowsException() {
            String over50 = "x".repeat(51);
            assertThrows(IllegalArgumentException.class, () -> new NeedGroup(
                    needGroupId, "Ăn uống", TransactionType.EXPENSE, over50, null
            ));
        }

        @Test
        @DisplayName("Từ chối color vượt quá 10 ký tự")
        void testColorOver10CharsThrowsException() {
            String over10 = "c".repeat(11);
            assertThrows(IllegalArgumentException.class, () -> new NeedGroup(
                    needGroupId, "Ăn uống", TransactionType.EXPENSE, null, over10
            ));
        }

        @Test
        @DisplayName("Từ chối xóa danh mục con không tồn tại trong nhóm")
        void testRemoveNonExistentCategoryThrowsException() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Nhu cầu", TransactionType.EXPENSE, null, null
            );
            Category nonExistentCategory = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );

            BusinessException ex = assertThrows(BusinessException.class, () -> group.removeCategory(nonExistentCategory));
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }
    }

    // =========================================================================
    // Nhóm 4: Invariants & State Transitions (Bất biến tài chính và chuyển trạng thái)
    // =========================================================================
    @Nested
    @DisplayName("Group 4: Invariants and State Transitions")
    class InvariantsAndStateTransitionsTests {

        @Test
        @DisplayName("Xóa nhóm thành công khi số lượng nhóm hoạt động > 1 và kích hoạt xóa lan truyền")
        void testMarkDeletedSuccessWhenActiveGroupCountGreaterThanOne() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Giải trí", TransactionType.EXPENSE, null, null
            );
            Category cat1 = new Category(
                    UUID.randomUUID(), "Xem phim", needGroupId, TransactionType.EXPENSE, null, null
            );
            Category cat2 = new Category(
                    UUID.randomUUID(), "Du lịch", needGroupId, TransactionType.EXPENSE, null, null
            );
            group.addCategory(cat1);
            group.addCategory(cat2);

            group.markDeleted(2);

            assertTrue(group.isDeleted());
            assertFalse(group.isActive());
            // Kiểm tra xóa lan truyền tới các danh mục con
            assertTrue(cat1.isDeleted());
            assertTrue(cat2.isDeleted());
        }

        @Test
        @DisplayName("Từ chối xóa khi đây là nhóm hoạt động duy nhất còn lại của loại thu chi (activeGroupCount <= 1)")
        void testMarkDeletedThrowsWhenLastActiveGroup() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Khoản chi bắt buộc", TransactionType.EXPENSE, null, null
            );

            BusinessException ex1 = assertThrows(BusinessException.class, () -> group.markDeleted(1));
            assertEquals(ErrorCode.ERR_CAT_002, ex1.getErrorCode());

            BusinessException ex0 = assertThrows(BusinessException.class, () -> group.markDeleted(0));
            assertEquals(ErrorCode.ERR_CAT_002, ex0.getErrorCode());
            assertTrue(group.isActive());
        }

        @Test
        @DisplayName("Từ chối thêm danh mục khi loại thu chi không tương thích (ném ERR_CAT_001)")
        void testAddCategoryFailsWhenTransactionTypeMismatch() {
            NeedGroup expenseGroup = new NeedGroup(
                    needGroupId, "Chi tiêu", TransactionType.EXPENSE, null, null
            );
            Category incomeCategory = new Category(
                    categoryId, "Lương", needGroupId, TransactionType.INCOME, null, null
            );

            BusinessException ex = assertThrows(BusinessException.class, () -> expenseGroup.addCategory(incomeCategory));
            assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối thêm danh mục khi nhóm nhu cầu đang ở trạng thái DELETED (ném ERR_CAT_005)")
        void testAddCategoryFailsWhenGroupIsDeleted() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Chi tiêu cũ", TransactionType.EXPENSE, null, null
            );
            group.markDeleted(3);

            Category category = new Category(
                    categoryId, "Trà sữa", needGroupId, TransactionType.EXPENSE, null, null
            );

            BusinessException ex = assertThrows(BusinessException.class, () -> group.addCategory(category));
            assertEquals(ErrorCode.ERR_CAT_005, ex.getErrorCode());
        }
    }

    // =========================================================================
    // Nhóm 5: Identity, Encapsulation & Robustness (Đóng gói và bản sắc đối tượng)
    // =========================================================================
    @Nested
    @DisplayName("Group 5: Identity and Encapsulation Tests")
    class IdentityAndEncapsulationTests {

        @Test
        @DisplayName("Danh sách getCategories() phải bất biến và không cho phép can thiệp ngoài")
        void testGetCategoriesIsUnmodifiable() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Nhu cầu", TransactionType.EXPENSE, null, null
            );
            Category category = new Category(
                    categoryId, "Ăn sáng", needGroupId, TransactionType.EXPENSE, null, null
            );

            assertThrows(UnsupportedOperationException.class, () -> group.getCategories().add(category));
        }

        @Test
        @DisplayName("Bình đẳng Entity dựa trên needGroupId (equals và hashCode)")
        void testEntityEqualityBasedOnNeedGroupId() {
            NeedGroup group1 = new NeedGroup(
                    needGroupId, "Tên A", TransactionType.EXPENSE, "iconA", "#111111"
            );
            NeedGroup group2 = new NeedGroup(
                    needGroupId, "Tên B", TransactionType.EXPENSE, "iconB", "#222222"
            );
            NeedGroup groupDifferentId = new NeedGroup(
                    UUID.randomUUID(), "Tên A", TransactionType.EXPENSE, "iconA", "#111111"
            );

            assertEquals(group1, group2);
            assertEquals(group1.hashCode(), group2.hashCode());
            assertNotEquals(group1, groupDifferentId);
            assertNotEquals(group1, null);
            assertNotEquals(group1, "some-string");
        }

        @Test
        @DisplayName("Phương thức toString phản ánh đầy đủ thông tin định danh")
        void testToStringFormat() {
            NeedGroup group = new NeedGroup(
                    needGroupId, "Nhu cầu thiết yếu", TransactionType.EXPENSE, null, null
            );
            String str = group.toString();
            assertTrue(str.contains(needGroupId.toString()));
            assertTrue(str.contains("Nhu cầu thiết yếu"));
            assertTrue(str.contains("EXPENSE"));
        }

        @Test
        @DisplayName("Tái tạo nhóm nhu cầu từ chối tham số status/createdAt null hoặc trạng thái ARCHIVED không hợp lệ")
        void testReconstituteRejectsInvalidStatusOrNullCreatedAt() {
            LocalDateTime createdAt = LocalDateTime.of(2025, 6, 15, 9, 30, 0);

            assertThrows(NullPointerException.class, () -> NeedGroup.reconstitute(
                    needGroupId, "Nhu cầu", TransactionType.EXPENSE, null, "", "", createdAt));
            assertThrows(NullPointerException.class, () -> NeedGroup.reconstitute(
                    needGroupId, "Nhu cầu", TransactionType.EXPENSE, EntityStatus.ACTIVE, "", "", null));
            assertThrows(IllegalArgumentException.class, () -> NeedGroup.reconstitute(
                    needGroupId, "Nhu cầu", TransactionType.EXPENSE, EntityStatus.ARCHIVED, "", "", createdAt));
        }

        @Test
        @DisplayName("Tái tạo nhóm nhu cầu lịch sử vượt 50 ký tự vẫn thành công theo AS-02 (bỏ qua BR-NAM-01)")
        void testReconstituteBypassesBrNam01LegacyName() {
            LocalDateTime createdAt = LocalDateTime.of(2025, 6, 15, 9, 30, 0);
            String legacyLongName = "A".repeat(55);

            NeedGroup reconstituted = assertDoesNotThrow(() -> NeedGroup.reconstitute(
                    needGroupId,
                    "  " + legacyLongName + "  ",
                    TransactionType.EXPENSE,
                    EntityStatus.ACTIVE,
                    "",
                    "",
                    createdAt));

            assertEquals(legacyLongName, reconstituted.getName());
            assertEquals(createdAt, reconstituted.getCreatedAt());
        }
    }
}

