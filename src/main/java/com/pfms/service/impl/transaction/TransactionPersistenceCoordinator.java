package com.pfms.service.impl.transaction;

import com.pfms.service.impl.common.RollbackSupport;

import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;

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
            Wallet wallet,
            Wallet walletSnapshot,
            IncomeExpenseTransaction transaction,
            Category oldCategory,
            Money oldAmount,
            TransactionDate oldDate,
            String oldNote,
            TransactionFee oldFee) {

        executeTwoPhasePersistence(
                walletRepository,
                () -> transactionRepository.save(transaction),
                () -> transaction.updateDetails(wallet, oldCategory, oldAmount, oldDate, oldNote, oldFee),
                () -> transactionRepository.save(transaction),
                wallet,
                walletSnapshot);
    }

    static void persistDeletionWithRollback(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            IncomeExpenseTransaction transaction,
            Wallet wallet,
            Wallet walletSnapshot) {

        executeTwoPhasePersistence(
                walletRepository,
                () -> transactionRepository.deleteById(transaction.getTransactionId()),
                transaction::applyToWallet,
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
    // endregion
}

