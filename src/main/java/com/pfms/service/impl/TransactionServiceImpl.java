package com.pfms.service.impl;

import com.pfms.common.util.PaginationUtils;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.exception.StorageException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Lớp cài đặt dịch vụ điều phối nghiệp vụ ghi nhận và quản lý giao dịch thu chi (TransactionService).
 * Quản lý vòng đời giao dịch thu chi, cập nhật số dư ví tức thời và thực hiện quy trình hoàn nguyên an toàn khi sửa hoặc xóa.
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
                category.getTransactionType(),
                wallet,
                category,
                amount,
                transactionDate,
                normalizedNote,
                feeAmount,
                feeCategory);

        persistTransactionWithRollback(wallet, transaction);

        return toResponseDTO(transaction, wallet, category);
    }

    @Override
    public TransactionResponseDTO updateTransaction(UpdateTransactionRequestDTO request) {
        validateUpdateRequest(request);

        IncomeExpenseTransaction transaction = loadIncomeExpenseOrThrow(request.getTransactionId());
        Wallet wallet = EntityLookupSupport.loadActiveWallet(walletRepository, transaction.getWallet().getId());
        Category newCategory = EntityLookupSupport.loadActiveCategory(categoryRepository, request.getNewCategoryId());

        if (newCategory.getTransactionType() != transaction.getTransactionType()) {
            throw new BusinessException(
                    ErrorCode.ERR_CAT_001, "Loại danh mục mới không tương thích với loại giao dịch.");
        }

        Money newAmount = Money.of(request.getNewAmount());
        Money newFeeAmount = (request.getNewFeeAmount() != null)
                ? Money.of(request.getNewFeeAmount())
                : Money.ZERO;
        TransactionDate newDate = TransactionDate.ofOrToday(request.getNewDate());
        String newNote = Note.of(request.getNewNote(), "Ghi chú giao dịch").getValue();
        Category newFeeCategory = EntityLookupSupport.resolveActiveExpenseFeeCategory(
                categoryRepository, newFeeAmount, request.getNewFeeCategoryId());

        TransactionFee newFee = newFeeAmount.isPositive()
                ? TransactionFactory.createFee(
                        transaction.getTransactionId(), newFeeAmount, wallet, newFeeCategory)
                : null;

        Wallet walletSnapshot = wallet.copy();
        Money oldAmount = transaction.getAmount();
        TransactionDate oldDate = transaction.getTransactionDate();
        String oldNote = transaction.getNote();
        Category oldCategory = transaction.getCategory();
        TransactionFee oldFee = transaction.getFee().orElse(null);

        transaction.updateDetails(wallet, newCategory, newAmount, newDate, newNote, newFee);

        persistUpdateWithRollback(
                wallet, walletSnapshot, transaction, oldCategory, oldAmount, oldDate, oldNote, oldFee);

        return toResponseDTO(transaction, wallet, newCategory);
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

        Wallet walletSnapshot = wallet.copy();
        revertTransactionBalance(transaction, wallet);

        persistDeletionWithRollback(transaction, wallet, walletSnapshot);
    }

    private void revertTransactionBalance(IncomeExpenseTransaction transaction, Wallet wallet) {
        transaction.revertFromWallet();
        if (transaction.getWallet() != wallet) {
            Money fee = transaction.getFee().map(TransactionFee::getAmount).orElse(Money.ZERO);
            if (fee.isPositive()) {
                wallet.credit(fee);
            }
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                wallet.debit(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                wallet.credit(transaction.getAmount());
            }
        }
    }
    // endregion

    // region Queries
    @Override
    public TransactionListResponseDTO filterTransactions(FilterTransactionsRequestDTO request) {
        Objects.requireNonNull(request, "Tiêu chí lọc giao dịch không được để null");
        TransactionDate.validateRange(request.getStartDate(), request.getEndDate());
        PaginationUtils.validate(request.getPageNumber(), request.getPageSize());

        List<IncomeExpenseTransaction> matchedTransactions = queryMatchingTransactions(request);

        Money totalIncome = Money.ZERO;
        Money totalExpense = Money.ZERO;

        for (IncomeExpenseTransaction transaction : matchedTransactions) {
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
            if (transaction.getFee().isPresent()) {
                totalExpense = totalExpense.add(transaction.getFee().get().getAmount());
            }
        }

        Money netCashFlow = totalIncome.subtract(totalExpense);
        List<TransactionResponseDTO> pageItems = toPageItems(matchedTransactions, request);

        int totalRecords = matchedTransactions.size();
        int totalPages = (totalRecords == 0)
                ? 0
                : (int) Math.ceil((double) totalRecords / request.getPageSize());

        return new TransactionListResponseDTO(
                pageItems,
                totalIncome.getAmount(),
                totalExpense.getAmount(),
                netCashFlow.getAmount(),
                request.getPageNumber(),
                request.getPageSize(),
                totalRecords,
                totalPages);
    }
    // endregion

    // region Helpers
    private void validateRecordRequest(RecordTransactionRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu ghi nhận giao dịch không được để null");
        Objects.requireNonNull(request.getWalletId(), "Mã ví không được để null");
        Objects.requireNonNull(request.getCategoryId(), "Mã danh mục không được để null");
        validateAmountAndFee(request.getAmount(), request.getFeeAmount(), request.getFeeCategoryId());
    }

    private void validateUpdateRequest(UpdateTransactionRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu cập nhật giao dịch không được để null");
        Objects.requireNonNull(request.getTransactionId(), "Mã giao dịch không được để null");
        Objects.requireNonNull(request.getNewCategoryId(), "Mã danh mục mới không được để null");
        validateAmountAndFee(request.getNewAmount(), request.getNewFeeAmount(), request.getNewFeeCategoryId());
    }

    private void validateAmountAndFee(BigDecimal amount, BigDecimal feeAmount, UUID feeCategoryId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.ERR_FIN_001);
        }
        if (feeAmount != null && feeAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.ERR_FEE_001);
        }
        if (feeAmount != null && feeAmount.compareTo(BigDecimal.ZERO) > 0 && feeCategoryId == null) {
            throw new BusinessException(ErrorCode.ERR_FEE_002);
        }
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

    private void executeTwoPhasePersistence(
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

    private void persistTransactionWithRollback(Wallet wallet, IncomeExpenseTransaction transaction) {
        Wallet walletSnapshot = wallet.copy();
        transaction.applyToWallet();
        executeTwoPhasePersistence(
                () -> transactionRepository.save(transaction),
                transaction::revertFromWallet,
                () -> transactionRepository.deleteById(transaction.getTransactionId()),
                wallet,
                walletSnapshot);
    }

    private void persistUpdateWithRollback(
            Wallet wallet,
            Wallet walletSnapshot,
            IncomeExpenseTransaction transaction,
            Category oldCategory,
            Money oldAmount,
            TransactionDate oldDate,
            String oldNote,
            TransactionFee oldFee) {

        executeTwoPhasePersistence(
                () -> transactionRepository.save(transaction),
                () -> transaction.updateDetails(wallet, oldCategory, oldAmount, oldDate, oldNote, oldFee),
                () -> transactionRepository.save(transaction),
                wallet,
                walletSnapshot);
    }

    private void persistDeletionWithRollback(
            IncomeExpenseTransaction transaction, Wallet wallet, Wallet walletSnapshot) {

        executeTwoPhasePersistence(
                () -> transactionRepository.deleteById(transaction.getTransactionId()),
                transaction::applyToWallet,
                () -> transactionRepository.save(transaction),
                wallet,
                walletSnapshot);
    }

    private List<IncomeExpenseTransaction> queryMatchingTransactions(FilterTransactionsRequestDTO request) {
        List<Transaction> candidates = (request.getStartDate() != null && request.getEndDate() != null)
                ? transactionRepository.findByDateRange(request.getStartDate(), request.getEndDate())
                : transactionRepository.findAll();

        return candidates.stream()
                .filter(IncomeExpenseTransaction.class::isInstance)
                .map(IncomeExpenseTransaction.class::cast)
                .filter(transaction -> matchesFilterCriteria(transaction, request))
                .sorted(Transaction.DESCENDING_ORDER)
                .toList();
    }

    private boolean matchesFilterCriteria(
            IncomeExpenseTransaction transaction, FilterTransactionsRequestDTO request) {

        LocalDate txnDate = transaction.getTransactionDate().getDate();
        if (request.getStartDate() != null && txnDate.isBefore(request.getStartDate())) {
            return false;
        }
        if (request.getEndDate() != null && txnDate.isAfter(request.getEndDate())) {
            return false;
        }
        if (!request.getWalletIds().isEmpty()
                && !request.getWalletIds().contains(transaction.getWallet().getId())) {
            return false;
        }
        if (request.getTransactionType() != null && !request.getTransactionType().isEmpty()
                && !transaction.getTransactionType().name().equalsIgnoreCase(request.getTransactionType())) {
            return false;
        }
        if (!request.getCategoryIds().isEmpty()
                && !request.getCategoryIds().contains(transaction.getCategory().getCategoryId())) {
            return false;
        }
        return request.getKeyword() == null || request.getKeyword().isEmpty()
                || (transaction.getNote() != null
                && transaction.getNote().toLowerCase().contains(request.getKeyword().toLowerCase()));
    }

    private List<TransactionResponseDTO> toPageItems(
            List<IncomeExpenseTransaction> matchedTransactions, FilterTransactionsRequestDTO request) {

        return PaginationUtils.slice(
                matchedTransactions, request.getPageNumber(), request.getPageSize())
                .stream()
                .map(txn -> toResponseDTO(
                        txn,
                        EntityLookupSupport.resolveForDisplay(walletRepository, txn.getWallet().getId(), txn.getWallet()),
                        EntityLookupSupport.resolveForDisplay(categoryRepository, txn.getCategory().getCategoryId(), txn.getCategory())))
                .toList();
    }

    private TransactionResponseDTO toResponseDTO(
            IncomeExpenseTransaction transaction, Wallet wallet, Category category) {

        TransactionFee fee = transaction.getFee().orElse(null);
        boolean hasFee = (fee != null);
        BigDecimal feeAmount = hasFee ? fee.getAmount().getAmount() : BigDecimal.ZERO;
        UUID feeCategoryId = hasFee ? fee.getCategory().getCategoryId() : null;
        String feeCategoryName = hasFee
                ? EntityLookupSupport.resolveForDisplay(categoryRepository, feeCategoryId, fee.getCategory()).getName()
                : "";

        return new TransactionResponseDTO(
                transaction.getTransactionId(),
                wallet.getId(),
                wallet.getName(),
                category.getCategoryId(),
                category.getName(),
                transaction.getAmount().getAmount(),
                transaction.getTransactionType().name(),
                transaction.getTransactionDate().getDate(),
                transaction.getNote(),
                hasFee,
                feeAmount,
                feeCategoryId,
                feeCategoryName,
                wallet.getCurrentBalance().getAmount(),
                transaction.getCreatedAt()
        );
    }
    // endregion
}
