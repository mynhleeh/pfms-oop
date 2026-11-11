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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Category Entity")
class CategoryTest {

    private UUID categoryId;
    private UUID needGroupId;
    private UUID newNeedGroupId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        needGroupId = UUID.randomUUID();
        newNeedGroupId = UUID.randomUUID();
    }

    // =========================================================================
    // Nhóm 1: Happy Path (Khởi tạo và luồng chuẩn)
    // =========================================================================
    @Nested
    @DisplayName("Group 1: Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo danh mục hợp lệ với đầy đủ tham số")
        void testValidConstructionWithFullArguments() {
            Category category = new Category(
                    categoryId,
                    "Ăn uống",
                    needGroupId,
                    TransactionType.EXPENSE,
                    "food-icon",
                    "#FF5733"
            );

            assertEquals(categoryId, category.getCategoryId());
            assertEquals("Ăn uống", category.getName());
            assertEquals(needGroupId, category.getNeedGroupId());
            assertEquals(TransactionType.EXPENSE, category.getTransactionType());
            assertEquals(EntityStatus.ACTIVE, category.getStatus());
            assertEquals("food-icon", category.getIcon());
            assertEquals("#FF5733", category.getColor());
            assertTrue(category.isActive());
            assertFalse(category.isDeleted());
        }

        @Test
        @DisplayName("Khởi tạo danh mục thu nhập hợp lệ")
        void testValidIncomeCategoryConstruction() {
            Category category = new Category(
                    categoryId,
                    "Tiền lương",
                    needGroupId,
                    TransactionType.INCOME,
                    null,
                    null
            );

            assertEquals("Tiền lương", category.getName());
            assertEquals(TransactionType.INCOME, category.getTransactionType());
            assertEquals("", category.getIcon());
            assertEquals("", category.getColor());
            assertTrue(category.isActive());
        }

        @Test
        @DisplayName("Đổi tên danh mục hợp lệ")
        void testRenameValid() {
            Category category = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );

            category.rename("Nhà hàng & Tiệc");
            assertEquals("Nhà hàng & Tiệc", category.getName());
        }

        @Test
        @DisplayName("Cập nhật biểu tượng và màu sắc hợp lệ")
        void testUpdateAppearance() {
            Category category = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, "old-icon", "#000000"
            );

            category.updateAppearance("new-icon", "#FFFFFF");
            assertEquals("new-icon", category.getIcon());
            assertEquals("#FFFFFF", category.getColor());
        }
    }

    // =========================================================================
    // Nhóm 2: Boundary & Normalization (Biên và chuẩn hóa)
    // =========================================================================
    @Nested
    @DisplayName("Group 2: Boundary and Normalization Tests")
    class BoundaryAndNormalizationTests {

        @Test
        @DisplayName("Chuẩn hóa cắt tỉa khoảng trắng ở hai đầu tên khi khởi tạo và đổi tên")
        void testNameTrimming() {
            Category category = new Category(
                    categoryId, "   Mua sắm online \t  ", needGroupId, TransactionType.EXPENSE, null, null
            );
            assertEquals("Mua sắm online", category.getName());

            category.rename("   Siêu thị cuối tuần \n ");
            assertEquals("Siêu thị cuối tuần", category.getName());
        }

        @Test
        @DisplayName("Tên hợp lệ tại cận biên dưới: chính xác 1 ký tự")
        void testNameBoundaryMin1Char() {
            Category category = new Category(
                    categoryId, "A", needGroupId, TransactionType.EXPENSE, null, null
            );
            assertEquals("A", category.getName());

            category.rename("B");
            assertEquals("B", category.getName());
        }

        @Test
        @DisplayName("Tên hợp lệ tại cận biên trên: chính xác 50 ký tự")
        void testNameBoundaryMax50Chars() {
            String exact50Chars = "A".repeat(50);
            Category category = new Category(
                    categoryId, exact50Chars, needGroupId, TransactionType.EXPENSE, null, null
            );
            assertEquals(exact50Chars, category.getName());

            String another50Chars = "B".repeat(50);
            category.rename(another50Chars);
            assertEquals(another50Chars, category.getName());
        }

        @Test
        @DisplayName("Chuẩn hóa icon và color khi truyền khoảng trắng hoặc null")
        void testIconAndColorNormalization() {
            Category category = new Category(
                    categoryId, "Chi tiêu", needGroupId, TransactionType.EXPENSE, "  icon-test  ", "  #123456  "
            );
            assertEquals("icon-test", category.getIcon());
            assertEquals("#123456", category.getColor());

            category.updateAppearance(null, null);
            assertEquals("", category.getIcon());
            assertEquals("", category.getColor());
        }
    }

    // =========================================================================
    // Nhóm 3: Null & Invalid Input (Kiểm tra dữ liệu rỗng và không hợp lệ)
    // =========================================================================
    @Nested
    @DisplayName("Group 3: Null and Invalid Input Tests")
    class NullAndInvalidInputTests {

        @Test
        @DisplayName("Từ chối categoryId null")
        void testNullCategoryIdThrowsException() {
            assertThrows(NullPointerException.class, () -> new Category(
                    null, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            ));
        }

        @Test
        @DisplayName("Từ chối needGroupId null")
        void testNullNeedGroupIdThrowsException() {
            assertThrows(NullPointerException.class, () -> new Category(
                    categoryId, "Ăn uống", null, TransactionType.EXPENSE, null, null
            ));
        }

        @Test
        @DisplayName("Từ chối transactionType null")
        void testNullTransactionTypeThrowsException() {
            assertThrows(NullPointerException.class, () -> new Category(
                    categoryId, "Ăn uống", needGroupId, null, null, null
            ));
        }

        @ParameterizedTest(name = "Tên khởi tạo không hợp lệ: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\t", "\n" })
        @DisplayName("Từ chối khởi tạo danh mục với tên null hoặc rỗng")
        void testInvalidNameOnConstruction(String invalidName) {
            BusinessException ex = assertThrows(BusinessException.class, () -> new Category(
                    categoryId, invalidName, needGroupId, TransactionType.EXPENSE, null, null
            ));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo với tên vượt quá 50 ký tự (51 ký tự)")
        void testNameExceeds50CharsOnConstruction() {
            String name51Chars = "A".repeat(51);
            BusinessException ex = assertThrows(BusinessException.class, () -> new Category(
                    categoryId, name51Chars, needGroupId, TransactionType.EXPENSE, null, null
            ));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @ParameterizedTest(name = "Tên đổi mới không hợp lệ: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\t", "\n" })
        @DisplayName("Từ chối đổi tên danh mục sang tên null hoặc rỗng")
        void testInvalidNameOnRename(String invalidName) {
            Category category = new Category(
                    categoryId, "Hợp lệ", needGroupId, TransactionType.EXPENSE, null, null
            );
            BusinessException ex = assertThrows(BusinessException.class, () -> category.rename(invalidName));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối đổi tên vượt quá 50 ký tự (51 ký tự)")
        void testNameExceeds50CharsOnRename() {
            Category category = new Category(
                    categoryId, "Hợp lệ", needGroupId, TransactionType.EXPENSE, null, null
            );
            String name51Chars = "A".repeat(51);
            BusinessException ex = assertThrows(BusinessException.class, () -> category.rename(name51Chars));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối icon vượt quá 50 ký tự")
        void testIconExceeds50Chars() {
            String longIcon = "I".repeat(51);
            assertThrows(IllegalArgumentException.class, () -> new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, longIcon, null
            ));
        }

        @Test
        @DisplayName("Từ chối color vượt quá 10 ký tự")
        void testColorExceeds10Chars() {
            String longColor = "#12345678901";
            assertThrows(IllegalArgumentException.class, () -> new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, longColor
            ));
        }
    }

    // =========================================================================
    // Nhóm 4: State Transitions & Domain Invariants (Chuyển trạng thái & Bất biến)
    // =========================================================================
    @Nested
    @DisplayName("Group 4: State Transitions and Invariants Tests")
    class StateTransitionsAndInvariantsTests {

        @Test
        @DisplayName("Xóa mềm danh mục chuyển trạng thái sang DELETED")
        void testMarkDeleted() {
            Category category = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );
            assertTrue(category.isActive());

            category.markDeleted();
            assertFalse(category.isActive());
            assertTrue(category.isDeleted());
            assertEquals(EntityStatus.DELETED, category.getStatus());
        }

        @Test
        @DisplayName("Khôi phục danh mục thành công khi nhóm cha đang hoạt động")
        void testRestoreSuccessWhenParentGroupActive() {
            Category category = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );
            category.markDeleted();
            assertTrue(category.isDeleted());

            category.restore(true);
            assertTrue(category.isActive());
            assertFalse(category.isDeleted());
            assertEquals(EntityStatus.ACTIVE, category.getStatus());
        }

        @Test
        @DisplayName("Từ chối khôi phục danh mục khi nhóm cha đã bị xóa (ERR-CAT-004)")
        void testRestoreFailsWhenParentGroupInactive() {
            Category category = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );
            category.markDeleted();

            BusinessException ex = assertThrows(BusinessException.class, () -> category.restore(false));
            assertEquals(ErrorCode.ERR_CAT_004, ex.getErrorCode());
            assertTrue(category.isDeleted(), "Trạng thái phải giữ nguyên là DELETED khi bị từ chối khôi phục");
        }

        @Test
        @DisplayName("Chuyển nhóm nhu cầu cha thành công khi nhóm đích cùng loại thu chi")
        void testChangeNeedGroupSuccess() {
            Category category = new Category(
                    categoryId, "Cà phê", needGroupId, TransactionType.EXPENSE, null, null
            );

            category.changeNeedGroup(newNeedGroupId, TransactionType.EXPENSE);
            assertEquals(newNeedGroupId, category.getNeedGroupId());
        }

        @Test
        @DisplayName("Từ chối chuyển nhóm cha sang nhóm khác loại thu chi (ERR-CAT-001)")
        void testChangeNeedGroupDifferentTransactionTypeThrows() {
            Category category = new Category(
                    categoryId, "Cà phê", needGroupId, TransactionType.EXPENSE, null, null
            );

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> category.changeNeedGroup(newNeedGroupId, TransactionType.INCOME));
            assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
            assertEquals(needGroupId, category.getNeedGroupId(), "Mã nhóm cha không đổi khi chuyển thất bại");
        }

        @Test
        @DisplayName("Từ chối chuyển nhóm cha khi tham số null")
        void testChangeNeedGroupWithNullParameters() {
            Category category = new Category(
                    categoryId, "Cà phê", needGroupId, TransactionType.EXPENSE, null, null
            );

            assertThrows(NullPointerException.class, () -> category.changeNeedGroup(null, TransactionType.EXPENSE));
            assertThrows(NullPointerException.class, () -> category.changeNeedGroup(newNeedGroupId, null));
        }
    }

    // =========================================================================
    // Nhóm 5: Robustness & Entity Identity (Độ bền bỉ và định danh thực thể)
    // =========================================================================
    @Nested
    @DisplayName("Group 5: Robustness and Identity Tests")
    class RobustnessAndIdentityTests {

        @Test
        @DisplayName("Hai Category cùng categoryId được xem là bằng nhau theo quy tắc định danh thực thể")
        void testEntityEqualityById() {
            Category cat1 = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, "icon1", "#000"
            );
            Category cat2 = new Category(
                    categoryId, "Ăn tiệm", UUID.randomUUID(), TransactionType.EXPENSE, "icon2", "#FFF"
            );

            assertEquals(cat1, cat2);
            assertEquals(cat1.hashCode(), cat2.hashCode());
        }

        @Test
        @DisplayName("Hai Category khác categoryId không bằng nhau")
        void testEntityInequalityDifferentId() {
            Category cat1 = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );
            Category cat2 = new Category(
                    UUID.randomUUID(), "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );

            assertNotEquals(cat1, cat2);
        }

        @Test
        @DisplayName("So sánh bằng nhau với null hoặc đối tượng kiểu khác trả về false")
        void testEqualityWithNullAndOtherTypes() {
            Category category = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );

            assertNotEquals(null, category);
            assertNotEquals("Ăn uống", category);
            assertEquals(category, category);
        }

        @Test
        @DisplayName("Phương thức toString cung cấp thông tin định danh và trạng thái hữu ích")
        void testToStringFormat() {
            Category category = new Category(
                    categoryId, "Ăn uống", needGroupId, TransactionType.EXPENSE, null, null
            );

            String str = category.toString();
            assertNotNull(str);
            assertTrue(str.contains(categoryId.toString()));
            assertTrue(str.contains("Ăn uống"));
            assertTrue(str.contains("EXPENSE"));
            assertTrue(str.contains("ACTIVE"));
        }
    }
}

