package com.pfms.service.impl.transfer;

import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.service.impl.common.RollbackSupport;

/**
 * Bộ điều phối nội bộ lưu trữ và hoàn nguyên cho giao dịch chuyển tiền nội bộ.
 * Bảo toàn tính nguyên tử số dư hai chiều giữa ví nguồn và ví đích khi xảy ra lỗi đĩa.
 */
final class TransferPersistenceCoordinator {

    // region Constructors
    private TransferPersistenceCoordinator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static void persistTransferWithRollback(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            Wallet sourceWallet,
            Wallet destinationWallet,
            TransferTransaction transfer) {

        Wallet sourceSnapshot = sourceWallet.copy();
        Wallet destinationSnapshot = destinationWallet.copy();
        transfer.applyToWallet();

        boolean sourceSaved = false;
        boolean destinationSaved = false;
        try {
            walletRepository.save(sourceWallet);
            sourceSaved = true;
            walletRepository.save(destinationWallet);
            destinationSaved = true;
            transactionRepository.save(transfer);
        } catch (StorageException storageException) {
            rollbackTransferAfterStorageFailure(
                    transactionRepository, walletRepository, sourceSaved, destinationSaved,
                    sourceSnapshot, destinationSnapshot, transfer, storageException);
            throw storageException;
        }
    }

    static void persistDeletionWithRollback(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            TransferTransaction transfer,
            Wallet sourceWallet,
            Wallet destinationWallet) {

        Wallet destinationSnapshot = destinationWallet.copy();
        transfer.revertFromWallet(sourceWallet, destinationWallet);

        boolean transactionDeleted = false;
        boolean destinationSaved = false;
        try {
            if (!transactionRepository.deleteById(transfer.getTransactionId())) {
                restoreRevertedInMemoryWallets(transfer, sourceWallet, destinationWallet);
                throw new EntityNotFoundException(
                        ErrorCode.ERR_NOT_FOUND, "Giao dịch chuyển tiền", transfer.getTransactionId().toString());
            }
            transactionDeleted = true;
            walletRepository.save(destinationWallet);
            destinationSaved = true;
            walletRepository.save(sourceWallet);
        } catch (StorageException storageException) {
            rollbackDeletionAfterStorageFailure(
                    transactionRepository, walletRepository, transactionDeleted, destinationSaved,
                    transfer, sourceWallet, destinationWallet, destinationSnapshot, storageException);
            throw storageException;
        }
    }
    // endregion

    // region Helpers
    private static void rollbackTransferAfterStorageFailure(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            boolean sourceSaved,
            boolean destinationSaved,
            Wallet sourceSnapshot,
            Wallet destinationSnapshot,
            TransferTransaction transfer,
            StorageException primaryException) {

        transfer.revertFromWallet();
        if (sourceSaved) {
            RollbackSupport.runRollbackStep(() -> walletRepository.save(sourceSnapshot), primaryException);
        }
        if (destinationSaved) {
            RollbackSupport.runRollbackStep(() -> walletRepository.save(destinationSnapshot), primaryException);
            RollbackSupport.runRollbackStep(
                    () -> transactionRepository.deleteById(transfer.getTransactionId()), primaryException);
        }
    }

    private static void rollbackDeletionAfterStorageFailure(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            boolean transactionDeleted,
            boolean destinationSaved,
            TransferTransaction transfer,
            Wallet sourceWallet,
            Wallet destinationWallet,
            Wallet destinationSnapshot,
            StorageException primaryException) {

        restoreRevertedInMemoryWallets(transfer, sourceWallet, destinationWallet);
        if (destinationSaved) {
            RollbackSupport.runRollbackStep(() -> walletRepository.save(destinationSnapshot), primaryException);
        }
        if (transactionDeleted) {
            RollbackSupport.runRollbackStep(() -> transactionRepository.save(transfer), primaryException);
        }
    }

    private static void restoreRevertedInMemoryWallets(
            TransferTransaction transfer, Wallet sourceWallet, Wallet destinationWallet) {
        sourceWallet.debit(transfer.getTotalSourceDeduction());
        destinationWallet.credit(transfer.getAmount());
    }
    // endregion
}

