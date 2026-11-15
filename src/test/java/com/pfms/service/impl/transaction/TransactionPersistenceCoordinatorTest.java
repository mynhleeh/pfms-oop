package com.pfms.service.impl.transaction;

import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("TransactionPersistenceCoordinator Unit Tests")
class TransactionPersistenceCoordinatorTest {

    private TransactionRepository transactionRepository;
    private WalletRepository walletRepository;
    private Wallet currentWallet;
    private Wallet newWallet;
    private Category deletedExpenseCategory;
    private Category activeExpenseCategory;
    private TransactionDate validDate;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        walletRepository = mock(WalletRepository.class);

        currentWallet = new Wallet("Ví hiện tại", "Ví chính", false);
        currentWallet.credit(Money.of(1_000_000));

        newWallet = new Wallet("Ví mới", "Ví chuyển đến", false);
        newWallet.credit(Money.of(500_000));

        deletedExpenseCategory = new Category(
                UUID.randomUUID(), "Danh mục đã xóa", UUID.randomUUID(),
                TransactionType.EXPENSE, "icon_old", "#000000");
        deletedExpenseCategory.markDeleted();

        activeExpenseCategory = new Category(
                UUID.randomUUID(), "Danh mục mới", UUID.randomUUID(),
                TransactionType.EXPENSE, "icon_new", "#FFFFFF");

        validDate = TransactionDate.of(2026, 3, 15);
    }

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<TransactionPersistenceCoordinator> constructor =
                TransactionPersistenceCoordinator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Nested
    @DisplayName("Single Wallet Update Tests")
    class SingleWalletUpdateTests {

        @Test
        @DisplayName("Lưu cập nhật thành công khi cùng ví")
        void testPersistUpdate_SameWallet_Success() {
            IncomeExpenseTransaction transaction = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, currentWallet, activeExpenseCategory,
                    Money.of(100_000), validDate, "Ghi chú cũ", null);
            transaction.applyToWallet(); // Số dư ví còn 900_000

            TransactionPersistenceCoordinator.persistUpdateWithRollback(
                    transactionRepository, walletRepository, currentWallet, currentWallet,
                    transaction, activeExpenseCategory, Money.of(150_000), validDate, "Ghi chú mới", null);

            verify(transactionRepository).save(transaction);
            verify(walletRepository).save(currentWallet);
            assertEquals(Money.of(850_000), currentWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("Rollback thành công và phục hồi transactionSnapshot gốc khi danh mục cũ đã bị xóa mềm")
        void testPersistUpdate_SameWallet_RollbackWithDeletedOldCategory_Success() {
            IncomeExpenseTransaction transaction = IncomeExpenseTransaction.reconstitute(
                    UUID.randomUUID(), TransactionType.EXPENSE, currentWallet, deletedExpenseCategory,
                    Money.of(100_000), validDate, "Giao dịch cũ danh mục xóa", null,
                    LocalDateTime.now());
            currentWallet.debit(Money.of(100_000)); // Số dư ví còn 900_000

            doThrow(new StorageException("Lỗi ghi ví", new IOException("I/O error")))
                    .when(walletRepository).save(any(Wallet.class));

            assertThrows(StorageException.class, () ->
                    TransactionPersistenceCoordinator.persistUpdateWithRollback(
                            transactionRepository, walletRepository, currentWallet, currentWallet,
                            transaction, activeExpenseCategory, Money.of(200_000), validDate, "Đổi sang active", null));

            ArgumentCaptor<IncomeExpenseTransaction> txCaptor = ArgumentCaptor.forClass(IncomeExpenseTransaction.class);
            verify(transactionRepository, times(2)).save(txCaptor.capture());

            IncomeExpenseTransaction rolledBackTx = txCaptor.getAllValues().get(1);
            assertEquals(deletedExpenseCategory.getCategoryId(), rolledBackTx.getCategory().getCategoryId());
            assertEquals(Money.of(100_000), rolledBackTx.getAmount());
            assertEquals(Money.of(900_000), currentWallet.getCurrentBalance());
        }
    }

    @Nested
    @DisplayName("Cross Wallet Update Tests")
    class CrossWalletUpdateTests {

        @Test
        @DisplayName("Rollback thành công và phục hồi cả 2 ví và transactionSnapshot khi lưu ví mới thất bại")
        void testPersistUpdate_CrossWallet_RollbackWithDeletedOldCategory_Success() {
            IncomeExpenseTransaction transaction = IncomeExpenseTransaction.reconstitute(
                    UUID.randomUUID(), TransactionType.EXPENSE, currentWallet, deletedExpenseCategory,
                    Money.of(100_000), validDate, "Giao dịch cũ trên ví hiện tại", null,
                    LocalDateTime.now());
            currentWallet.debit(Money.of(100_000)); // Ví hiện tại còn 900_000

            doThrow(new StorageException("Lỗi ghi ví mới", new IOException("I/O error")))
                    .when(walletRepository).save(newWallet);

            assertThrows(StorageException.class, () ->
                    TransactionPersistenceCoordinator.persistUpdateWithRollback(
                            transactionRepository, walletRepository, currentWallet, newWallet,
                            transaction, activeExpenseCategory, Money.of(200_000), validDate, "Chuyển sang ví mới", null));

            assertEquals(Money.of(900_000), currentWallet.getCurrentBalance());
            assertEquals(Money.of(500_000), newWallet.getCurrentBalance());
            assertEquals(currentWallet.getId(), transaction.getWallet().getId());
            assertEquals(deletedExpenseCategory.getCategoryId(), transaction.getCategory().getCategoryId());
            assertEquals(Money.of(100_000), transaction.getAmount());

            ArgumentCaptor<IncomeExpenseTransaction> txCaptor = ArgumentCaptor.forClass(IncomeExpenseTransaction.class);
            verify(transactionRepository, times(2)).save(txCaptor.capture());
            IncomeExpenseTransaction rolledBackTx = txCaptor.getAllValues().get(1);
            assertEquals(currentWallet.getId(), rolledBackTx.getWallet().getId());
            assertEquals(deletedExpenseCategory.getCategoryId(), rolledBackTx.getCategory().getCategoryId());

            ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
            verify(walletRepository, times(4)).save(walletCaptor.capture());
            assertEquals(currentWallet.getId(), walletCaptor.getAllValues().get(2).getId());
            assertEquals(newWallet.getId(), walletCaptor.getAllValues().get(3).getId());
        }
    }

    @Nested
    @DisplayName("Deletion With Rollback Tests")
    class DeletionTests {

        @Test
        @DisplayName("Rollback phục hồi số dư ví và tái lưu giao dịch khi lưu ví thất bại")
        void testPersistDeletion_RollbackOnWalletStorageFailure() {
            IncomeExpenseTransaction transaction = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, currentWallet, activeExpenseCategory,
                    Money.of(100_000), validDate, "Chi tiêu trước khi xóa", null);
            transaction.applyToWallet(); // Số dư còn 900_000

            doThrow(new StorageException("Lỗi ghi ví khi xóa", new IOException("I/O error")))
                    .when(walletRepository).save(any(Wallet.class));

            assertThrows(StorageException.class, () ->
                    TransactionPersistenceCoordinator.persistDeletionWithRollback(
                            transactionRepository, walletRepository, transaction, currentWallet));

            assertEquals(Money.of(900_000), currentWallet.getCurrentBalance());
            verify(transactionRepository).deleteById(transaction.getTransactionId());
            verify(transactionRepository).save(transaction);
        }
    }
}
