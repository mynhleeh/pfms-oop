package com.pfms.service.impl;

import com.pfms.common.util.PaginationUtils;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.Note;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFactory;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.transfer.TransferFilterCriteriaDTO;
import com.pfms.presentation.dto.transfer.TransferHistoryResponseDTO;
import com.pfms.presentation.dto.transfer.TransferRequestDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.service.TransferService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Lớp cài đặt dịch vụ điều phối nghiệp vụ chuyển tiền nội bộ (TransferService).
 * Đảm bảo tính nguyên tử hai chiều, bảo toàn tổng tài sản và xử lý phụ phí đi
 * kèm.
 */
public class TransferServiceImpl implements TransferService {

    // region State
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    // endregion

    // region Constructors
    public TransferServiceImpl(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            CategoryRepository categoryRepository) {

        this.transactionRepository = Objects.requireNonNull(
                transactionRepository, "TransactionRepository không được để null");
        this.walletRepository = Objects.requireNonNull(
                walletRepository, "WalletRepository không được để null");
        this.categoryRepository = Objects.requireNonNull(
                categoryRepository, "CategoryRepository không được để null");
    }
    // endregion

    // region Commands
    @Override
    public TransferResponseDTO transferFunds(TransferRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu chuyển tiền không được để null");
        validateTransferRequest(request);

        Money transferAmount = Money.of(request.getAmount());
        Money feeAmount = (request.getFeeAmount() != null) ? Money.of(request.getFeeAmount()) : Money.ZERO;
        TransactionDate transferDate = TransactionDate.ofOrToday(request.getTransferDate());
        String normalizedNote = Note.of(request.getNote(), "Ghi chú giao dịch").getValue();

        Wallet sourceWallet = EntityLookupSupport.loadActiveWallet(walletRepository, request.getSourceWalletId());
        Wallet destinationWallet = EntityLookupSupport.loadActiveWallet(
                walletRepository, request.getDestinationWalletId());
        Category feeCategory = feeAmount.isPositive()
                ? EntityLookupSupport.findEntityOrThrow(
                        categoryRepository, request.getFeeCategoryId(), "Danh mục phí")
                : null;

        TransferTransaction transfer = TransactionFactory.createTransfer(
                sourceWallet, destinationWallet, transferAmount, transferDate, normalizedNote, feeAmount, feeCategory);

        persistTransferWithRollback(sourceWallet, destinationWallet, transfer);

        return toResponseDTO(transfer, sourceWallet, destinationWallet);
    }

    @Override
    public void deleteTransfer(UUID transferId) {
        Objects.requireNonNull(transferId, "Mã giao dịch chuyển tiền không được để null");
        TransferTransaction transfer = loadTransferOrThrow(transferId);
        Wallet liveSourceWallet = EntityLookupSupport.findEntityOrThrow(
                walletRepository, transfer.getSourceWallet().getId(), EntityLookupSupport.LABEL_WALLET);
        Wallet liveDestinationWallet = EntityLookupSupport.findEntityOrThrow(
                walletRepository, transfer.getDestinationWallet().getId(), EntityLookupSupport.LABEL_WALLET);

        Wallet destinationSnapshot = liveDestinationWallet.copy();
        transfer.revertFromWallet(liveSourceWallet, liveDestinationWallet);

        persistDeletionWithRollback(transfer, liveSourceWallet, liveDestinationWallet, destinationSnapshot);
    }
    // endregion

    // region Queries
    @Override
    public TransferHistoryResponseDTO filterTransfers(TransferFilterCriteriaDTO request) {
        Objects.requireNonNull(request, "Tiêu chí lọc chuyển tiền không được để null");
        TransactionDate.validateRange(request.getStartDate(), request.getEndDate());
        PaginationUtils.validate(request.getPageNumber(), request.getPageSize());

        List<TransferTransaction> matchedTransfers = queryMatchingTransfers(request);
        Money totalTransferred = matchedTransfers.stream()
                .map(TransferTransaction::getAmount)
                .reduce(Money.ZERO, Money::add);
        Money totalFees = matchedTransfers.stream()
                .map(transfer -> transfer.getFee().map(TransactionFee::getAmount).orElse(Money.ZERO))
                .reduce(Money.ZERO, Money::add);

        List<TransferResponseDTO> pageItems = toPageItems(matchedTransfers, request);

        return new TransferHistoryResponseDTO(
                pageItems,
                totalTransferred.getAmount(),
                totalFees.getAmount(),
                matchedTransfers.size());
    }
    // endregion

    // region Helpers
    private List<TransferResponseDTO> toPageItems(
            List<TransferTransaction> matchedTransfers, TransferFilterCriteriaDTO request) {
        return PaginationUtils.slice(
                matchedTransfers, request.getPageNumber(), request.getPageSize())
                .stream()
                .map(transfer -> toResponseDTO(
                        transfer,
                        resolveWalletForDisplay(transfer.getSourceWallet()),
                        resolveWalletForDisplay(transfer.getDestinationWallet())))
                .toList();
    }
    private void validateTransferRequest(TransferRequestDTO request) {
        Objects.requireNonNull(request.getSourceWalletId(), "Mã ví nguồn không được để null");
        Objects.requireNonNull(request.getDestinationWalletId(), "Mã ví đích không được để null");
        if (request.getSourceWalletId().equals(request.getDestinationWalletId())) {
            throw new BusinessException(ErrorCode.ERR_TRF_001);
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.ERR_TRF_002);
        }
        if (request.getFeeAmount() != null && request.getFeeAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.ERR_FEE_001);
        }
        if (request.getFeeAmount() != null
                && request.getFeeAmount().compareTo(BigDecimal.ZERO) > 0
                && request.getFeeCategoryId() == null) {
            throw new BusinessException(ErrorCode.ERR_FEE_002);
        }
    }

    private void persistTransferWithRollback(
            Wallet sourceWallet, Wallet destinationWallet, TransferTransaction transfer) {

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
                    sourceSaved, destinationSaved, sourceSnapshot, destinationSnapshot, transfer, storageException);
            throw storageException;
        }
    }

    private void rollbackTransferAfterStorageFailure(
            boolean sourceSaved, boolean destinationSaved,
            Wallet sourceSnapshot, Wallet destinationSnapshot,
            TransferTransaction transfer, StorageException primaryException) {

        transfer.revertFromWallet();
        if (sourceSaved) {
            runRollbackStep(() -> walletRepository.save(sourceSnapshot), primaryException);
        }
        if (destinationSaved) {
            runRollbackStep(() -> walletRepository.save(destinationSnapshot), primaryException);
            runRollbackStep(() -> transactionRepository.deleteById(transfer.getTransactionId()), primaryException);
        }
    }

    private List<TransferTransaction> queryMatchingTransfers(TransferFilterCriteriaDTO request) {
        List<Transaction> candidates = (request.getStartDate() != null && request.getEndDate() != null)
                ? transactionRepository.findByDateRange(request.getStartDate(), request.getEndDate())
                : transactionRepository.findAll();

        return candidates.stream()
                .filter(TransferTransaction.class::isInstance)
                .map(TransferTransaction.class::cast)
                .filter(transfer -> matchesFilterCriteria(transfer, request))
                .sorted(Transaction.DESCENDING_ORDER)
                .toList();
    }

    private boolean matchesFilterCriteria(TransferTransaction transfer, TransferFilterCriteriaDTO request) {
        LocalDate transferDate = transfer.getTransactionDate().getDate();
        if (request.getStartDate() != null && transferDate.isBefore(request.getStartDate())) {
            return false;
        }
        if (request.getEndDate() != null && transferDate.isAfter(request.getEndDate())) {
            return false;
        }
        if (request.getSourceWalletId() != null
                && !transfer.getSourceWallet().getId().equals(request.getSourceWalletId())) {
            return false;
        }
        return request.getDestinationWalletId() == null
                || transfer.getDestinationWallet().getId().equals(request.getDestinationWalletId());
    }

    private TransferResponseDTO toResponseDTO(
            TransferTransaction transfer, Wallet sourceWallet, Wallet destinationWallet) {

        TransactionFee fee = transfer.getFee().orElse(null);
        Money feeAmount = (fee != null) ? fee.getAmount() : Money.ZERO;
        Category feeCategory = (fee != null) ? fee.getCategory() : null;
        UUID feeCategoryId = (feeCategory != null) ? feeCategory.getCategoryId() : null;
        String feeCategoryName = (feeCategory != null) ? feeCategory.getName() : "";

        return new TransferResponseDTO(
                transfer.getTransactionId(), sourceWallet.getId(), sourceWallet.getName(),
                destinationWallet.getId(), destinationWallet.getName(),
                transfer.getAmount().getAmount(), transfer.getTransactionDate().getDate(),
                transfer.getNote(), feeAmount.getAmount(), feeCategoryId,
                feeCategoryName, sourceWallet.getCurrentBalance().getAmount(),
                destinationWallet.getCurrentBalance().getAmount(), transfer.getCreatedAt());
    }

    private Wallet resolveWalletForDisplay(Wallet embeddedWallet) {
        return walletRepository.findById(embeddedWallet.getId()).orElse(embeddedWallet);
    }

    private TransferTransaction loadTransferOrThrow(UUID transferId) {
        Transaction transaction = transactionRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorCode.ERR_NOT_FOUND, "Giao dịch chuyển tiền", transferId.toString()));
        if (!(transaction instanceof TransferTransaction transfer)) {
            throw new EntityNotFoundException(
                    ErrorCode.ERR_NOT_FOUND, "Giao dịch chuyển tiền", transferId.toString());
        }
        return transfer;
    }

    private void persistDeletionWithRollback(
            TransferTransaction transfer, Wallet sourceWallet,
            Wallet destinationWallet, Wallet destinationSnapshot) {

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
                    transactionDeleted, destinationSaved,
                    transfer, sourceWallet, destinationWallet, destinationSnapshot, storageException);
            throw storageException;
        }
    }

    private void rollbackDeletionAfterStorageFailure(
            boolean transactionDeleted, boolean destinationSaved,
            TransferTransaction transfer, Wallet sourceWallet,
            Wallet destinationWallet, Wallet destinationSnapshot,
            StorageException primaryException) {

        restoreRevertedInMemoryWallets(transfer, sourceWallet, destinationWallet);
        if (destinationSaved) {
            runRollbackStep(() -> walletRepository.save(destinationSnapshot), primaryException);
        }
        if (transactionDeleted) {
            runRollbackStep(() -> transactionRepository.save(transfer), primaryException);
        }
    }

    private void runRollbackStep(Runnable compensatingStep, StorageException primaryException) {
        try {
            compensatingStep.run();
        } catch (RuntimeException rollbackException) {
            primaryException.addSuppressed(rollbackException);
        }
    }

    private void restoreRevertedInMemoryWallets(
            TransferTransaction transfer, Wallet sourceWallet, Wallet destinationWallet) {
        sourceWallet.debit(transfer.getTotalSourceDeduction());
        destinationWallet.credit(transfer.getAmount());
    }
    // endregion
}
