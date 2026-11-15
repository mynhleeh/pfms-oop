package com.pfms.service.impl.transaction;

import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.service.impl.common.RollbackSupport;

import java.util.Objects;

/**
 * Bộ điều phối nội bộ lưu trữ hai pha (Two-Phase Persistence) cho giao dịch thu chi.
 * Đảm bảo tính nhất quán dữ liệu giữa bảng giao dịch và số dư ví, tự động hoàn nguyên khi lưu trữ thất bại.
 */
final class TransactionPersistenceCoordinator {

    // region Constructors
    private TransactionPersistenceCoordinator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static void persistTransactionWithRollback(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            Wallet wallet,
            IncomeExpenseTransaction transaction) {

        Wallet walletSnapshot = wallet.copy();
        transaction.applyToWallet();
        executeTwoPhasePersistence(
                walletRepository,
                () -> transactionRepository.save(transaction),
                transaction::revertFromWallet,
                () -> transactionRepository.deleteById(transaction.getTransactionId()),
                wallet,
                walletSnapshot);
    }

    static void persistUpdateWithRollback(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            Wallet currentWallet,
            Wallet newWallet,
            IncomeExpenseTransaction transaction,
            Category newCategory,
            Money newAmount,
            TransactionDate newDate,
            String newNote,
            TransactionFee newFee) {

        Objects.requireNonNull(currentWallet, "Ví hiện tại không được để null");
        Objects.requireNonNull(newWallet, "Ví mới không được để null");

        if (Objects.equals(currentWallet.getId(), newWallet.getId())) {
            Wallet walletSnapshot = currentWallet.copy();
            IncomeExpenseTransaction transactionSnapshot = snapshotTransaction(transaction, currentWallet);

            transaction.updateDetails(currentWallet, currentWallet, newCategory, newAmount, newDate, newNote, newFee);

            executeTwoPhasePersistence(
                    walletRepository,
                    () -> transactionRepository.save(transaction),
                    () -> rollbackInMemoryUpdate(transaction, transactionSnapshot, currentWallet),
                    () -> transactionRepository.save(transactionSnapshot),
                    currentWallet,
                    walletSnapshot);
            return;
        }

        Wallet currentWalletSnapshot = currentWallet.copy();
        Wallet newWalletSnapshot = newWallet.copy();
        IncomeExpenseTransaction transactionSnapshot = snapshotTransaction(transaction, currentWallet);

        transaction.updateDetails(currentWallet, newWallet, newCategory, newAmount, newDate, newNote, newFee);

        boolean transactionSaved = false;
        boolean currentWalletSaved = false;
        try {
            transactionRepository.save(transaction);
            transactionSaved = true;
            walletRepository.save(currentWallet);
            currentWalletSaved = true;
            walletRepository.save(newWallet);
        } catch (StorageException storageException) {
            rollbackInMemoryUpdate(transaction, transactionSnapshot, newWallet);
            if (currentWalletSaved) {
                RollbackSupport.runRollbackStep(() -> walletRepository.save(currentWalletSnapshot), storageException);
                RollbackSupport.runRollbackStep(() -> walletRepository.save(newWalletSnapshot), storageException);
            }
            if (transactionSaved) {
                RollbackSupport.runRollbackStep(() -> transactionRepository.save(transactionSnapshot), storageException);
            }
            throw storageException;
        }
    }

    static void persistDeletionWithRollback(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            IncomeExpenseTransaction transaction,
            Wallet wallet) {

        Wallet walletSnapshot = wallet.copy();
        transaction.revertFromWallet(wallet);

        executeTwoPhasePersistence(
                walletRepository,
                () -> transactionRepository.deleteById(transaction.getTransactionId()),
                () -> restoreRevertedInMemoryWallet(transaction, wallet),
                () -> transactionRepository.save(transaction),
                wallet,
                walletSnapshot);
    }
    // endregion

    // region Helpers
    private static void executeTwoPhasePersistence(
            WalletRepository walletRepository,
            Runnable primaryStorageAction,
            Runnable inMemoryRollback,
            Runnable primaryRollbackStep,
            Wallet wallet,
            Wallet walletSnapshot) {

        boolean primaryCompleted = false;
        try {
            primaryStorageAction.run();
            primaryCompleted = true;
            walletRepository.save(wallet);
        } catch (StorageException storageException) {
            if (inMemoryRollback != null) {
                try {
                    inMemoryRollback.run();
                } catch (RuntimeException ignore) {
                }
            }
            if (primaryCompleted && primaryRollbackStep != null) {
                RollbackSupport.runRollbackStep(primaryRollbackStep, storageException);
            }
            RollbackSupport.runRollbackStep(() -> walletRepository.save(walletSnapshot), storageException);
            throw storageException;
        }
    }

    private static IncomeExpenseTransaction snapshotTransaction(
            IncomeExpenseTransaction transaction, Wallet currentWallet) {
        return IncomeExpenseTransaction.reconstitute(
                transaction.getTransactionId(),
                transaction.getTransactionType(),
                currentWallet != null ? currentWallet : transaction.getWallet(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getNote(),
                transaction.getFee().orElse(null),
                transaction.getCreatedAt());
    }

    private static void rollbackInMemoryUpdate(
            IncomeExpenseTransaction transaction,
            IncomeExpenseTransaction transactionSnapshot,
            Wallet newWallet) {
        try {
            transaction.revertFromWallet(newWallet);
            transaction.restoreFromSnapshot(transactionSnapshot);
            transaction.applyToWallet();
        } catch (RuntimeException ignore) {
        }
    }

    private static void restoreRevertedInMemoryWallet(IncomeExpenseTransaction transaction, Wallet wallet) {
        if (wallet == null) {
            return;
        }
        Money feeAmount = transaction.getFee().map(TransactionFee::getAmount).orElse(Money.ZERO);
        if (transaction.getTransactionType() == TransactionType.INCOME) {
            wallet.credit(transaction.getAmount());
            if (feeAmount.isPositive()) {
                wallet.debit(feeAmount);
            }
        } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
            wallet.debit(transaction.getAmount());
            if (feeAmount.isPositive()) {
                wallet.debit(feeAmount);
            }
        }
        if (transaction.getWallet() != null && transaction.getWallet() != wallet) {
            restoreRevertedInMemoryWallet(transaction, transaction.getWallet());
        }
    }
    // endregion
}
