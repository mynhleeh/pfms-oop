package com.pfms.service.impl.transfer;

import com.pfms.common.util.PaginationUtils;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.service.impl.common.EntityLookupSupport;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Bộ hỗ trợ nội bộ lọc, truy vấn và ánh xạ DTO cho giao dịch chuyển tiền nội bộ.
 */
final class TransferQuerySupport {

    // region Constructors
    private TransferQuerySupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static TransferListResponseDTO filterTransfers(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            FilterTransfersRequestDTO request) {

        List<TransferTransaction> matchedTransfers = queryMatchingTransfers(transactionRepository, request);
        Money totalTransferred = matchedTransfers.stream()
                .map(TransferTransaction::getAmount)
                .reduce(Money.ZERO, Money::add);
        Money totalFees = matchedTransfers.stream()
                .map(transfer -> transfer.getFee().map(TransactionFee::getAmount).orElse(Money.ZERO))
                .reduce(Money.ZERO, Money::add);

        List<TransferResponseDTO> pageItems = toPageItems(matchedTransfers, request, walletRepository);
        int totalRecords = matchedTransfers.size();
        int totalPages = PaginationUtils.calculateTotalPages(totalRecords, request.getPageSize());

        return new TransferListResponseDTO(
                pageItems,
                totalTransferred.getAmount(),
                totalFees.getAmount(),
                request.getPageNumber(),
                request.getPageSize(),
                totalRecords,
                totalPages);
    }

    static TransferResponseDTO toResponseDTO(
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
    // endregion

    // region Helpers
    private static List<TransferResponseDTO> toPageItems(
            List<TransferTransaction> matchedTransfers,
            FilterTransfersRequestDTO request,
            WalletRepository walletRepository) {

        return PaginationUtils.slice(
                matchedTransfers, request.getPageNumber(), request.getPageSize())
                .stream()
                .map(transfer -> toResponseDTO(
                        transfer,
                        EntityLookupSupport.resolveForDisplay(
                                walletRepository, transfer.getSourceWallet().getId(), transfer.getSourceWallet()),
                        EntityLookupSupport.resolveForDisplay(
                                walletRepository, transfer.getDestinationWallet().getId(), transfer.getDestinationWallet())))
                .toList();
    }

    private static List<TransferTransaction> queryMatchingTransfers(
            TransactionRepository transactionRepository, FilterTransfersRequestDTO request) {

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

    private static boolean matchesFilterCriteria(TransferTransaction transfer, FilterTransfersRequestDTO request) {
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
    // endregion
}

