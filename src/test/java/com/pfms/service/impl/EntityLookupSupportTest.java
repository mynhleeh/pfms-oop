package com.pfms.service.impl;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.ArchivedWalletState;
import com.pfms.domain.model.wallet.DeletedWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử EntityLookupSupport (com.pfms.service.impl)")
class EntityLookupSupportTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private UUID walletId;
    private UUID categoryId;
    private Wallet activeWallet;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        activeWallet = new Wallet(
                walletId, "Ví Chính", "", true,
                LocalDateTime.now().minusDays(3), new ActiveWalletState(), Money.of("100000.00"));
        expenseCategory = new Category(
                categoryId, "Ăn uống", UUID.randomUUID(),
                TransactionType.EXPENSE, "food", "#FF9800");
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
    @DisplayName("Nhóm 1: Tra cứu Ví (findWalletOrThrow & loadActiveWallet)")
    class WalletLookupTests {

        @Test
        @DisplayName("findWalletOrThrow trả về ví dù ở trạng thái ARCHIVED khi tồn tại")
        void shouldFindExistingArchivedWallet() {
            Wallet archivedWallet = new Wallet(
                    walletId, "Ví Cũ", "", false,
                    LocalDateTime.now().minusDays(10), new ArchivedWalletState(), Money.ZERO);
            when(walletRepository.findById(walletId)).thenReturn(Optional.of(archivedWallet));

            Wallet found = EntityLookupSupport.findWalletOrThrow(walletRepository, walletId);

            assertSame(archivedWallet, found);
        }

        @Test
        @DisplayName("findWalletOrThrow ném EntityNotFoundException ERR_NOT_FOUND khi thiếu ví")
        void shouldThrowNotFoundWhenWalletMissing() {
            when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> EntityLookupSupport.findWalletOrThrow(walletRepository, walletId));

            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
            assertEquals("Ví", exception.getEntityType());
            assertEquals(walletId.toString(), exception.getIdentifier());
        }

        @Test
        @DisplayName("findWalletOrThrow từ chối repository hoặc walletId null")
        void shouldRejectNullArgumentsForWalletLookup() {
            assertThrows(NullPointerException.class,
                    () -> EntityLookupSupport.findWalletOrThrow(null, walletId));
            assertThrows(NullPointerException.class,
                    () -> EntityLookupSupport.findWalletOrThrow(walletRepository, null));
        }

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
    @DisplayName("Nhóm 2: Tra cứu Danh mục (findCategoryOrThrow)")
    class CategoryLookupTests {

        @Test
        @DisplayName("findCategoryOrThrow trả về đúng danh mục khi tồn tại")
        void shouldFindExistingCategory() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

            Category found = EntityLookupSupport.findCategoryOrThrow(categoryRepository, categoryId);

            assertSame(expenseCategory, found);
        }

        @Test
        @DisplayName("findCategoryOrThrow sử dụng nhãn mặc định 'Danh mục' hoặc nhãn tùy biến khi thiếu")
        void shouldThrowNotFoundWithExpectedEntityLabel() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            EntityNotFoundException defaultException = assertThrows(
                    EntityNotFoundException.class,
                    () -> EntityLookupSupport.findCategoryOrThrow(categoryRepository, categoryId));
            assertEquals(ErrorCode.ERR_NOT_FOUND, defaultException.getErrorCode());
            assertEquals("Danh mục", defaultException.getEntityType());
            assertEquals(categoryId.toString(), defaultException.getIdentifier());

            EntityNotFoundException feeException = assertThrows(
                    EntityNotFoundException.class,
                    () -> EntityLookupSupport.findCategoryOrThrow(categoryRepository, categoryId, "Danh mục phí"));
            assertEquals("Danh mục phí", feeException.getEntityType());
            assertEquals(categoryId.toString(), feeException.getIdentifier());
        }

        @Test
        @DisplayName("findCategoryOrThrow từ chối repository null, categoryId null và nhãn rỗng")
        void shouldRejectInvalidArgumentsForCategoryLookup() {
            assertThrows(NullPointerException.class,
                    () -> EntityLookupSupport.findCategoryOrThrow(null, categoryId));
            assertThrows(NullPointerException.class,
                    () -> EntityLookupSupport.findCategoryOrThrow(categoryRepository, null));
            assertThrows(IllegalArgumentException.class,
                    () -> EntityLookupSupport.findCategoryOrThrow(categoryRepository, categoryId, "   "));
            assertThrows(IllegalArgumentException.class,
                    () -> EntityLookupSupport.findCategoryOrThrow(categoryRepository, categoryId, null));
        }
    }
}

