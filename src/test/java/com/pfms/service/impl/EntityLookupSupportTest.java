package com.pfms.service.impl;

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
    @DisplayName("Nhóm 2: Tải Ví Hoạt Động (loadActiveWallet)")
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
}

