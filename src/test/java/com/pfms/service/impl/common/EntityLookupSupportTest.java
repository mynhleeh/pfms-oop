package com.pfms.service.impl.common;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.ArchivedWalletState;
import com.pfms.domain.model.wallet.DeletedWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.domain.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử EntityLookupSupport (com.pfms.service.impl)")
class EntityLookupSupportTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private NeedGroupRepository needGroupRepository;

    private UUID walletId;
    private UUID categoryId;
    private UUID needGroupId;
    private Wallet activeWallet;
    private Category expenseCategory;
    private NeedGroup needGroup;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        needGroupId = UUID.randomUUID();
        activeWallet = new Wallet(
                walletId, "Ví Chính", "", true,
                LocalDateTime.now().minusDays(3), new ActiveWalletState(), Money.of("100000.00"));
        expenseCategory = new Category(
                categoryId, "Ăn uống", UUID.randomUUID(),
                TransactionType.EXPENSE, "food", "#FF9800");
        needGroup = new NeedGroup(
                needGroupId, "Ăn uống", TransactionType.EXPENSE, "utensils", "#FF5722");
    }

    @Nested
    @DisplayName("Thiết kế Utility Class")
    class UtilityClassDesignTests {

        @Test
        @DisplayName("Hàm khởi tạo private phải ném AssertionError khi gọi qua Reflection")
        void shouldPreventInstantiationViaReflection() throws NoSuchMethodException {
            Constructor<EntityLookupSupport> constructor = EntityLookupSupport.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
            assertInstanceOf(AssertionError.class, exception.getCause());
            assertEquals("Utility class cannot be instantiated", exception.getCause().getMessage());
        }
    }

    @Nested
    @DisplayName("Nhóm 1: Tra cứu Thực thể Tổng quát (findEntityOrThrow)")
    class GenericEntityLookupTests {

        @Test
        @DisplayName("findEntityOrThrow trả về đúng Ví khi tồn tại")
        void shouldFindExistingWallet() {
            Wallet archivedWallet = new Wallet(
                    walletId, "Ví Cũ", "", false,
                    LocalDateTime.now().minusDays(10), new ArchivedWalletState(), Money.ZERO);
            when(walletRepository.findById(walletId)).thenReturn(Optional.of(archivedWallet));

            Wallet found = EntityLookupSupport.findEntityOrThrow(
                    walletRepository, walletId, EntityLookupSupport.LABEL_WALLET);

            assertSame(archivedWallet, found);
        }

        @Test
        @DisplayName("findEntityOrThrow trả về đúng Danh mục khi tồn tại")
        void shouldFindExistingCategory() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

            Category found = EntityLookupSupport.findEntityOrThrow(
                    categoryRepository, categoryId, EntityLookupSupport.LABEL_CATEGORY);

            assertSame(expenseCategory, found);
        }

        @Test
        @DisplayName("findEntityOrThrow trả về đúng Nhóm nhu cầu khi tồn tại")
        void shouldFindExistingNeedGroup() {
            when(needGroupRepository.findById(needGroupId)).thenReturn(Optional.of(needGroup));

            NeedGroup found = EntityLookupSupport.findEntityOrThrow(
                    needGroupRepository, needGroupId, EntityLookupSupport.LABEL_NEED_GROUP);

            assertSame(needGroup, found);
        }

        @Test
        @DisplayName("findEntityOrThrow ném EntityNotFoundException ERR_NOT_FOUND khi thiếu thực thể")
        void shouldThrowNotFoundWhenEntityMissing() {
            when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> EntityLookupSupport.findEntityOrThrow(
                            walletRepository, walletId, EntityLookupSupport.LABEL_WALLET));

            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
            assertEquals("Ví", exception.getEntityType());
            assertEquals(walletId.toString(), exception.getIdentifier());

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
            EntityNotFoundException feeException = assertThrows(
                    EntityNotFoundException.class,
                    () -> EntityLookupSupport.findEntityOrThrow(
                            categoryRepository, categoryId, "Danh mục phí"));
            assertEquals("Danh mục phí", feeException.getEntityType());
            assertEquals(categoryId.toString(), feeException.getIdentifier());
        }

        @Test
        @DisplayName("findEntityOrThrow từ chối repository null, id null và nhãn rỗng")
        void shouldRejectInvalidArgumentsForGenericLookup() {
            assertThrows(NullPointerException.class,
                    () -> EntityLookupSupport.findEntityOrThrow(null, walletId, EntityLookupSupport.LABEL_WALLET));
            assertThrows(NullPointerException.class,
                    () -> EntityLookupSupport.findEntityOrThrow(walletRepository, null, EntityLookupSupport.LABEL_WALLET));
            assertThrows(IllegalArgumentException.class,
                    () -> EntityLookupSupport.findEntityOrThrow(walletRepository, walletId, "   "));
            assertThrows(IllegalArgumentException.class,
                    () -> EntityLookupSupport.findEntityOrThrow(walletRepository, walletId, null));
        }
    }

    @Nested
    @DisplayName("Nhóm 2: Tải Thực Thể Hoạt Động Tổng Quát (loadActiveEntity)")
    class LoadActiveEntityGenericTests {

        @Test
        @DisplayName("loadActiveEntity ném NullPointerException khi activePredicate là null")
        void shouldThrowNullPointerExceptionWhenPredicateIsNull() {
            assertThrows(NullPointerException.class,
                    () -> EntityLookupSupport.loadActiveEntity(
                            walletRepository, walletId, "Ví", null, ErrorCode.ERR_WAL_001, "msg"));
        }

        @Test
        @DisplayName("loadActiveEntity trả về thực thể khi điều kiện activePredicate thỏa mãn")
        void shouldReturnEntityWhenPredicateMatches() {
            when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));

            Wallet result = EntityLookupSupport.loadActiveEntity(
                    walletRepository, walletId, "Ví", Wallet::isActive, ErrorCode.ERR_WAL_001, "msg");

            assertSame(activeWallet, result);
        }

        @Test
        @DisplayName("loadActiveEntity ném BusinessException khi điều kiện activePredicate không thỏa mãn")
        void shouldThrowBusinessExceptionWhenPredicateFails() {
            when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> EntityLookupSupport.loadActiveEntity(
                            walletRepository, walletId, "Ví", w -> false, ErrorCode.ERR_WAL_001, "Lỗi trạng thái"));

            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
            assertEquals("Lỗi trạng thái", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Nhóm 3: Tải Ví Hoạt Động (loadActiveWallet)")
    class LoadActiveWalletTests {

        @Test
        @DisplayName("loadActiveWallet trả về ví khi đang ở trạng thái ACTIVE")
        void shouldLoadActiveWallet() {
            when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));

            Wallet loaded = EntityLookupSupport.loadActiveWallet(walletRepository, walletId);

            assertSame(activeWallet, loaded);
        }

        @Test
        @DisplayName("loadActiveWallet ném BusinessException ERR_WAL_001 khi ví ARCHIVED hoặc DELETED")
        void shouldRejectArchivedOrDeletedWallet() {
            Wallet archivedWallet = new Wallet(
                    walletId, "Ví Lưu Trữ", "", false,
                    LocalDateTime.now().minusDays(1), new ArchivedWalletState(), Money.ZERO);
            when(walletRepository.findById(walletId)).thenReturn(Optional.of(archivedWallet));

            BusinessException archivedException = assertThrows(
                    BusinessException.class,
                    () -> EntityLookupSupport.loadActiveWallet(walletRepository, walletId));
            assertEquals(ErrorCode.ERR_WAL_001, archivedException.getErrorCode());

            Wallet deletedWallet = new Wallet(
                    walletId, "Ví Đã Xóa", "", false,
                    LocalDateTime.now().minusDays(1), new DeletedWalletState(), Money.ZERO);
            when(walletRepository.findById(walletId)).thenReturn(Optional.of(deletedWallet));

            BusinessException deletedException = assertThrows(
                    BusinessException.class,
                    () -> EntityLookupSupport.loadActiveWallet(walletRepository, walletId));
            assertEquals(ErrorCode.ERR_WAL_001, deletedException.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Nhóm 3: Tải Danh Mục Hoạt Động (loadActiveCategory)")
    class LoadActiveCategoryTests {

        @Test
        @DisplayName("loadActiveCategory trả về danh mục khi đang ở trạng thái ACTIVE")
        void shouldLoadActiveCategory() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

            Category loaded = EntityLookupSupport.loadActiveCategory(categoryRepository, categoryId);

            assertSame(expenseCategory, loaded);
        }

        @Test
        @DisplayName("loadActiveCategory ném BusinessException ERR_CAT_001 khi danh mục không active (đã xóa)")
        void shouldRejectInactiveCategory() {
            Category deletedCategory = new Category(
                    categoryId, "Đã Xóa", UUID.randomUUID(),
                    TransactionType.EXPENSE, "food", "#FF9800");
            deletedCategory.markDeleted();
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(deletedCategory));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> EntityLookupSupport.loadActiveCategory(categoryRepository, categoryId));
            assertEquals(ErrorCode.ERR_CAT_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("loadActiveCategory ném EntityNotFoundException khi không tìm thấy danh mục")
        void shouldThrowNotFoundWhenCategoryDoesNotExist() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> EntityLookupSupport.loadActiveCategory(categoryRepository, categoryId));
            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
            assertEquals("Danh mục", exception.getEntityType());
        }
    }

    @Nested
    @DisplayName("Nhóm 4: Thẩm định Danh Mục Phí Hợp Lệ (resolveActiveExpenseFeeCategory)")
    class ResolveActiveExpenseFeeCategoryTests {

        @Test
        @DisplayName("resolveActiveExpenseFeeCategory trả về null khi feeAmount là null hoặc không dương (<= 0)")
        void shouldReturnNullWhenFeeNotPositive() {
            assertNull(EntityLookupSupport.resolveActiveExpenseFeeCategory(categoryRepository, null, categoryId));
            assertNull(EntityLookupSupport.resolveActiveExpenseFeeCategory(categoryRepository, Money.ZERO, categoryId));
            assertNull(EntityLookupSupport.resolveActiveExpenseFeeCategory(categoryRepository, Money.of("-1000"), categoryId));
        }

        @Test
        @DisplayName("resolveActiveExpenseFeeCategory ném ERR_FEE_002 khi feeAmount > 0 nhưng feeCategoryId là null")
        void shouldThrowErrFee002WhenFeeCategoryMissing() {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> EntityLookupSupport.resolveActiveExpenseFeeCategory(
                            categoryRepository, Money.of("5000"), null));
            assertEquals(ErrorCode.ERR_FEE_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("resolveActiveExpenseFeeCategory ném ERR_NOT_FOUND khi danh mục phí không tồn tại")
        void shouldThrowNotFoundWhenFeeCategoryDoesNotExist() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> EntityLookupSupport.resolveActiveExpenseFeeCategory(
                            categoryRepository, Money.of("5000"), categoryId));
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
            assertEquals("Danh mục phí", ex.getEntityType());
        }

        @Test
        @DisplayName("resolveActiveExpenseFeeCategory ném ERR_FEE_003 khi danh mục phí không active hoặc không phải EXPENSE")
        void shouldThrowErrFee003WhenFeeCategoryInvalid() {
            // Không active
            Category inactiveCategory = new Category(
                    categoryId, "Phí CK", UUID.randomUUID(),
                    TransactionType.EXPENSE, "fee", "#999999");
            inactiveCategory.markDeleted();
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(inactiveCategory));

            BusinessException exInactive = assertThrows(
                    BusinessException.class,
                    () -> EntityLookupSupport.resolveActiveExpenseFeeCategory(
                            categoryRepository, Money.of("5000"), categoryId));
            assertEquals(ErrorCode.ERR_FEE_003, exInactive.getErrorCode());

            // Type là INCOME thay vì EXPENSE
            Category incomeCategory = new Category(
                    categoryId, "Thu nhập khác", UUID.randomUUID(),
                    TransactionType.INCOME, "income", "#00FF00");
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(incomeCategory));

            BusinessException exIncome = assertThrows(
                    BusinessException.class,
                    () -> EntityLookupSupport.resolveActiveExpenseFeeCategory(
                            categoryRepository, Money.of("5000"), categoryId));
            assertEquals(ErrorCode.ERR_FEE_003, exIncome.getErrorCode());
        }

        @Test
        @DisplayName("resolveActiveExpenseFeeCategory trả về đúng danh mục phí hợp lệ")
        void shouldReturnValidFeeCategory() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

            Category resolved = EntityLookupSupport.resolveActiveExpenseFeeCategory(
                    categoryRepository, Money.of("5000"), categoryId);

            assertSame(expenseCategory, resolved);
        }
    }

    @Nested
    @DisplayName("Nhóm 5: Tra cứu Thực thể để Hiển thị với Fallback (resolveForDisplay)")
    class ResolveForDisplayTests {

        @Test
        @DisplayName("resolveForDisplay trả về fallback khi repository hoặc id là null")
        void shouldReturnFallbackWhenRepoOrIdNull() {
            assertSame(expenseCategory, EntityLookupSupport.resolveForDisplay(null, categoryId, expenseCategory));
            assertSame(expenseCategory, EntityLookupSupport.resolveForDisplay(categoryRepository, null, expenseCategory));
        }

        @Test
        @DisplayName("resolveForDisplay trả về entity từ repo khi tìm thấy")
        void shouldReturnEntityFromRepositoryWhenFound() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

            Category fallback = new Category(
                    UUID.randomUUID(), "Dự phòng", UUID.randomUUID(),
                    TransactionType.EXPENSE, "fb", "#000000");

            Category result = EntityLookupSupport.resolveForDisplay(categoryRepository, categoryId, fallback);

            assertSame(expenseCategory, result);
        }

        @Test
        @DisplayName("resolveForDisplay trả về fallback khi không tìm thấy trong repo")
        void shouldReturnFallbackWhenNotFoundInRepository() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            Category fallback = new Category(
                    UUID.randomUUID(), "Dự phòng", UUID.randomUUID(),
                    TransactionType.EXPENSE, "fb", "#000000");

            Category result = EntityLookupSupport.resolveForDisplay(categoryRepository, categoryId, fallback);

            assertSame(fallback, result);
        }
    }

    @Nested
    @DisplayName("Nhóm 6: Lập chỉ mục Thực thể theo Mã (indexById)")
    class IndexByIdTests {

        @Test
        @DisplayName("indexById trả về map rỗng khi danh sách null hoặc rỗng")
        void shouldReturnEmptyMapWhenInputNullOrEmpty() {
            assertTrue(EntityLookupSupport.indexById(null, Category::getCategoryId).isEmpty());
            assertTrue(EntityLookupSupport.indexById(Collections.emptyList(), Category::getCategoryId).isEmpty());
        }

        @Test
        @DisplayName("indexById ném NullPointerException khi hàm trích xuất mã là null")
        void shouldRejectNullIdExtractor() {
            assertThrows(NullPointerException.class,
                    () -> EntityLookupSupport.indexById(List.of(expenseCategory), null));
        }

        @Test
        @DisplayName("indexById lập chỉ mục chính xác và bỏ qua phần tử hoặc mã null")
        void shouldIndexEntitiesAndSkipNullEntries() {
            Category secondCategory = new Category(
                    UUID.randomUUID(), "Di chuyển", UUID.randomUUID(),
                    TransactionType.EXPENSE, "car", "#2196F3");

            Map<UUID, Category> indexed = EntityLookupSupport.indexById(
                    Arrays.asList(expenseCategory, null, secondCategory),
                    Category::getCategoryId);

            assertEquals(2, indexed.size());
            assertSame(expenseCategory, indexed.get(expenseCategory.getCategoryId()));
            assertSame(secondCategory, indexed.get(secondCategory.getCategoryId()));
        }
    }

    @Nested
    @DisplayName("Nhóm 7: Kiểm tra số tiền và phụ phí dùng chung (validateAmountAndFee)")
    class ValidateAmountAndFeeTests {

        @Test
        @DisplayName("validateAmountAndFee ném BusinessException với mã lỗi tương ứng khi số tiền null hoặc không dương")
        void shouldRejectNullOrNonPositiveAmount() {
            BusinessException nullEx = assertThrows(BusinessException.class,
                    () -> EntityLookupSupport.validateAmountAndFee(null, ErrorCode.ERR_FIN_001, null, null));
            assertEquals(ErrorCode.ERR_FIN_001, nullEx.getErrorCode());

            BusinessException zeroEx = assertThrows(BusinessException.class,
                    () -> EntityLookupSupport.validateAmountAndFee(BigDecimal.ZERO, ErrorCode.ERR_TRF_002, null, null));
            assertEquals(ErrorCode.ERR_TRF_002, zeroEx.getErrorCode());
        }

        @Test
        @DisplayName("validateAmountAndFee ném ERR_FEE_001 khi phí âm và ERR_FEE_002 khi phí dương thiếu danh mục")
        void shouldValidateFeeAmountAndCategory() {
            BusinessException negFeeEx = assertThrows(BusinessException.class,
                    () -> EntityLookupSupport.validateAmountAndFee(
                            BigDecimal.TEN, ErrorCode.ERR_FIN_001, new BigDecimal("-1"), null));
            assertEquals(ErrorCode.ERR_FEE_001, negFeeEx.getErrorCode());

            BusinessException missingCatEx = assertThrows(BusinessException.class,
                    () -> EntityLookupSupport.validateAmountAndFee(
                            BigDecimal.TEN, ErrorCode.ERR_TRF_002, BigDecimal.ONE, null));
            assertEquals(ErrorCode.ERR_FEE_002, missingCatEx.getErrorCode());

            EntityLookupSupport.validateAmountAndFee(
                    BigDecimal.TEN, ErrorCode.ERR_FIN_001, BigDecimal.ONE, UUID.randomUUID());
        }
    }
}

