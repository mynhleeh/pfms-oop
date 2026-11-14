package com.pfms.service.impl.transfer;

import com.pfms.common.util.PaginationUtils;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.Note;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFactory;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferFundsRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.service.TransferService;
import com.pfms.service.impl.common.EntityLookupSupport;

import java.util.Objects;
import java.util.UUID;

/**
 * Cài đặt dịch vụ chuyển tiền nội bộ ({@link TransferService}).
 * Đảm bảo biến động số dư nguyên tử hai chiều, bảo toàn tổng tài sản và rollback an toàn khi lỗi đĩa.
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
    public TransferResponseDTO transferFunds(TransferFundsRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu chuyển tiền không được để null");
        validateTransferRequest(request);

        Money transferAmount = Money.of(request.getAmount());
        Money feeAmount = (request.getFeeAmount() != null) ? Money.of(request.getFeeAmount()) : Money.ZERO;
        TransactionDate transferDate = TransactionDate.ofOrToday(request.getTransferDate());
        String normalizedNote = Note.of(request.getNote(), "Ghi chú giao dịch").getValue();

        Wallet sourceWallet = EntityLookupSupport.loadActiveWallet(walletRepository, request.getSourceWalletId());
        Wallet destinationWallet = EntityLookupSupport.loadActiveWallet(
                walletRepository, request.getDestinationWalletId());
        Category feeCategory = EntityLookupSupport.resolveActiveExpenseFeeCategory(
                categoryRepository, feeAmount, request.getFeeCategoryId());

        TransferTransaction transfer = TransactionFactory.createTransfer(
                sourceWallet, destinationWallet, transferAmount, transferDate, normalizedNote, feeAmount, feeCategory);

        TransferPersistenceCoordinator.persistTransferWithRollback(
                transactionRepository, walletRepository, sourceWallet, destinationWallet, transfer);

        return TransferQuerySupport.toResponseDTO(transfer, sourceWallet, destinationWallet);
    }

    @Override
    public void deleteTransfer(UUID transferId) {
        Objects.requireNonNull(transferId, "Mã giao dịch chuyển tiền không được để null");
        TransferTransaction transfer = loadTransferOrThrow(transferId);
        Wallet liveSourceWallet = EntityLookupSupport.findEntityOrThrow(
                walletRepository, transfer.getSourceWallet().getId(), EntityLookupSupport.LABEL_WALLET);
        Wallet liveDestinationWallet = EntityLookupSupport.findEntityOrThrow(
                walletRepository, transfer.getDestinationWallet().getId(), EntityLookupSupport.LABEL_WALLET);

        TransferPersistenceCoordinator.persistDeletionWithRollback(
                transactionRepository, walletRepository, transfer, liveSourceWallet, liveDestinationWallet);
    }
    // endregion

    // region Queries
    @Override
    public TransferListResponseDTO filterTransfers(FilterTransfersRequestDTO request) {
        Objects.requireNonNull(request, "Tiêu chí lọc chuyển tiền không được để null");
        TransactionDate.validateRange(request.getStartDate(), request.getEndDate());
        PaginationUtils.validate(request.getPageNumber(), request.getPageSize());

        return TransferQuerySupport.filterTransfers(transactionRepository, walletRepository, request);
    }
    // endregion

    // region Helpers
    private void validateTransferRequest(TransferFundsRequestDTO request) {
        Objects.requireNonNull(request.getSourceWalletId(), "Mã ví nguồn không được để null");
        Objects.requireNonNull(request.getDestinationWalletId(), "Mã ví đích không được để null");
        if (request.getSourceWalletId().equals(request.getDestinationWalletId())) {
            throw new BusinessException(ErrorCode.ERR_TRF_001);
        }
        EntityLookupSupport.validateAmountAndFee(
                request.getAmount(), ErrorCode.ERR_TRF_002, request.getFeeAmount(), request.getFeeCategoryId());
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
    // endregion
}
