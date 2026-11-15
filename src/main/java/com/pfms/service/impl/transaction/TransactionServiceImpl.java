package com.pfms.service.impl.transaction;

import com.pfms.common.util.PaginationUtils;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.Note;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFactory;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.RecordTransactionRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.transaction.UpdateTransactionRequestDTO;
import com.pfms.service.TransactionService;
import com.pfms.service.impl.common.EntityLookupSupport;

import java.util.Objects;
import java.util.UUID;

/**
 * Cài đặt dịch vụ giao dịch thu chi ({@link TransactionService}).
 * Quản lý vòng đời giao dịch, cập nhật số dư ví và phối hợp các bộ phận hỗ trợ lưu trữ 2 pha và truy vấn.
 */
public class TransactionServiceImpl implements TransactionService {

    // region State
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    // endregion

    // region Constructors
    public TransactionServiceImpl(
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
    public TransactionResponseDTO recordIncomeExpense(RecordTransactionRequestDTO request) {
        validateRecordRequest(request);

        Money amount = Money.of(request.getAmount());
        Money feeAmount = (request.getFeeAmount() != null) ? Money.of(request.getFeeAmount()) : Money.ZERO;
        TransactionDate transactionDate = TransactionDate.ofOrToday(request.getTransactionDate());
        String normalizedNote = Note.of(request.getNote(), "Ghi chú giao dịch").getValue();

        Wallet wallet = EntityLookupSupport.loadActiveWallet(walletRepository, request.getWalletId());
        Category category = EntityLookupSupport.loadActiveCategory(categoryRepository, request.getCategoryId());
        Category feeCategory = EntityLookupSupport.resolveActiveExpenseFeeCategory(
                categoryRepository, feeAmount, request.getFeeCategoryId());

        IncomeExpenseTransaction transaction = TransactionFactory.createIncomeExpense(
                category.getTransactionType(), wallet, category, amount,
                transactionDate, normalizedNote, feeAmount, feeCategory);

        TransactionPersistenceCoordinator.persistTransactionWithRollback(
                transactionRepository, walletRepository, wallet, transaction);

        return TransactionQuerySupport.toResponseDTO(transaction, wallet, category, categoryRepository);
    }

    @Override
    public TransactionResponseDTO updateTransaction(UpdateTransactionRequestDTO request) {
        validateUpdateRequest(request);

        IncomeExpenseTransaction transaction = loadIncomeExpenseOrThrow(request.getTransactionId());
        Wallet currentWallet = EntityLookupSupport.loadActiveWallet(walletRepository, transaction.getWallet().getId());

        UUID targetWalletId = request.getNewWalletId() != null ? request.getNewWalletId() : currentWallet.getId();
        Wallet targetWallet = Objects.equals(targetWalletId, currentWallet.getId())
                ? currentWallet
                : EntityLookupSupport.loadActiveWallet(walletRepository, targetWalletId);

        UUID existingCategoryId = transaction.getCategory().getCategoryId();
        Category newCategory = Objects.equals(existingCategoryId, request.getNewCategoryId())
                ? EntityLookupSupport.findEntityOrThrow(categoryRepository, request.getNewCategoryId(), EntityLookupSupport.LABEL_CATEGORY)
                : EntityLookupSupport.loadActiveCategory(categoryRepository, request.getNewCategoryId());

        if (newCategory.getTransactionType() != transaction.getTransactionType()) {
            throw new BusinessException(
                    ErrorCode.ERR_CAT_001, "Loại danh mục mới không tương thích với loại giao dịch.");
        }

        Money newAmount = Money.of(request.getNewAmount());
        TransactionDate newDate = TransactionDate.ofOrToday(request.getNewDate());
        String newNote = Note.of(request.getNewNote(), "Ghi chú giao dịch").getValue();

        TransactionFee newFee = resolveNewFee(request, transaction, targetWallet);

        TransactionPersistenceCoordinator.persistUpdateWithRollback(
                transactionRepository, walletRepository, currentWallet, targetWallet, transaction,
                newCategory, newAmount, newDate, newNote, newFee);

        return TransactionQuerySupport.toResponseDTO(transaction, targetWallet, newCategory, categoryRepository);
    }

    @Override
    public void deleteTransaction(UUID transactionId) {
        Objects.requireNonNull(transactionId, "Mã giao dịch không được để null");
        IncomeExpenseTransaction transaction = loadIncomeExpenseOrThrow(transactionId);
        Wallet wallet = EntityLookupSupport.loadActiveWallet(
                walletRepository, transaction.getWallet().getId());

        if (transaction.getTransactionType() == TransactionType.INCOME) {
            Money feeAmount = transaction.getFee().map(TransactionFee::getAmount).orElse(Money.ZERO);
            Money available = wallet.getCurrentBalance().add(feeAmount);
            if (!available.isGreaterThanOrEqual(transaction.getAmount())) {
                throw new InsufficientBalanceException(
                        wallet.getId(), wallet.getCurrentBalance(), transaction.getAmount());
            }
        }

        TransactionPersistenceCoordinator.persistDeletionWithRollback(
                transactionRepository, walletRepository, transaction, wallet);
    }
    // endregion

    // region Queries
    @Override
    public TransactionListResponseDTO filterTransactions(FilterTransactionsRequestDTO request) {
        Objects.requireNonNull(request, "Tiêu chí lọc giao dịch không được để null");
        TransactionDate.validateRange(request.getStartDate(), request.getEndDate());
        PaginationUtils.validate(request.getPageNumber(), request.getPageSize());

        return TransactionQuerySupport.filterTransactions(
                transactionRepository, walletRepository, categoryRepository, request);
    }
    // endregion

    // region Helpers
    private void validateRecordRequest(RecordTransactionRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu ghi nhận giao dịch không được để null");
        Objects.requireNonNull(request.getWalletId(), "Mã ví không được để null");
        Objects.requireNonNull(request.getCategoryId(), "Mã danh mục không được để null");
        EntityLookupSupport.validateAmountAndFee(
                request.getAmount(), ErrorCode.ERR_FIN_001, request.getFeeAmount(), request.getFeeCategoryId());
    }

    private void validateUpdateRequest(UpdateTransactionRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu cập nhật giao dịch không được để null");
        Objects.requireNonNull(request.getTransactionId(), "Mã giao dịch không được để null");
        Objects.requireNonNull(request.getNewWalletId(), "Mã ví mới không được để null");
        Objects.requireNonNull(request.getNewCategoryId(), "Mã danh mục mới không được để null");
        EntityLookupSupport.validateAmountAndFee(
                request.getNewAmount(), ErrorCode.ERR_FIN_001, request.getNewFeeAmount(), request.getNewFeeCategoryId());
    }

    private TransactionFee resolveNewFee(
            UpdateTransactionRequestDTO request,
            IncomeExpenseTransaction transaction,
            Wallet targetWallet) {

        if (request.getNewFeeAmount() == null) {
            return transaction.getFee()
                    .map(existingFee -> TransactionFactory.createFee(
                            transaction.getTransactionId(),
                            existingFee.getAmount(),
                            targetWallet,
                            existingFee.getCategory()))
                    .orElse(null);
        }

        Money newFeeAmount = Money.of(request.getNewFeeAmount());
        if (!newFeeAmount.isPositive()) {
            return null;
        }

        Category feeCategory;
        if (transaction.getFee().isPresent()
                && Objects.equals(request.getNewFeeCategoryId(), transaction.getFee().get().getCategory().getCategoryId())) {
            feeCategory = EntityLookupSupport.findEntityOrThrow(
                    categoryRepository, request.getNewFeeCategoryId(), EntityLookupSupport.LABEL_FEE_CATEGORY);
            if (feeCategory.getTransactionType() != TransactionType.EXPENSE) {
                throw new BusinessException(ErrorCode.ERR_FEE_003);
            }
        } else {
            feeCategory = EntityLookupSupport.resolveActiveExpenseFeeCategory(
                    categoryRepository, newFeeAmount, request.getNewFeeCategoryId());
        }

        return TransactionFactory.createFee(
                transaction.getTransactionId(), newFeeAmount, targetWallet, feeCategory);
    }

    private IncomeExpenseTransaction loadIncomeExpenseOrThrow(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorCode.ERR_NOT_FOUND, "Giao dịch thu chi", transactionId.toString()));

        if (!(transaction instanceof IncomeExpenseTransaction incomeExpenseTxn)) {
            throw new BusinessException(
                    ErrorCode.ERR_NOT_FOUND, "Bản ghi giao dịch không phải thu chi hợp lệ.");
        }
        return incomeExpenseTxn;
    }
    // endregion
}
